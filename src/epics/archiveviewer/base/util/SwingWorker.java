package epics.archiveviewer.base.util;

import javax.swing.SwingUtilities;

/**
 * This is the 3rd version of SwingWorker (also known as SwingWorker 3), an
 * abstract class that you subclass to perform GUI-related work in a dedicated
 * thread. For instructions on using this class, see:
 * 
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 * 
 * Note that the API changed slightly in the 3rd version: You must now invoke
 * start() on the SwingWorker after creating it.
 */
public abstract class SwingWorker
{
	/** the value to return by the {@link #get() get-}method */
	private Object value;

	/** the instance of <CODE>ThreadVar</CODE> */
	private ThreadVar threadVar;
	
	private boolean interrupted;
	
	/**
	 * Creates thread that will call the <code>construct</code> and
	 * <code>finished</code> methods
	 */
	public SwingWorker()
	{
		final Runnable doFinished = new Runnable()
		{
			public void run()
			{
				finished();
			}
		};

		Runnable doConstruct = new Runnable()
		{
			public void run()
			{
				try
				{
					setValue(construct());
				}
				finally
				{
					threadVar.clear();
				}

				SwingUtilities.invokeLater(doFinished);
			}
		};

		Thread t = new Thread(doConstruct);
		threadVar = new ThreadVar(t);
	}

	/**
	 * Returns the value produced by the worker thread, or null if it hasn't
	 * been constructed yet.
	 * 
	 * @return the value produced by the worker thread, or null if it hasn't
	 *         been constructed yet.
	 */
	protected synchronized Object getValue()
	{
		if(this.interrupted)
			return null;
		return value;
	}

	/**
	 * Sets the value produced by the worker thread
	 * 
	 * @see #value
	 * @param x
	 *            the result of the worker thread
	 */
	private synchronized void setValue(Object x)
	{
		value = x;
	}

	/**
	 * The actual algorithm to run in this <CODE>SwingWorker</CODE> thread.
	 * May return NULL
	 * 
	 * @return whatever the actual algorithm returns, or NULL
	 */
	public abstract Object construct();

	/**
	 * Code to call on the event dispatching thread after the
	 * <code>construct</code> method has returned. Useful e.g. for notifying
	 * the GUI that the actual algorithm finished
	 */
	public void finished()
	{
	}

	/**
	 * A new method that interrupts the worker thread. Call this method to force
	 * the worker to stop what it's doing.
	 */
	public void interrupt()
	{
		Thread t = threadVar.get();

		if (t != null)
		{
			t.interrupt();
		}

		this.interrupted = true;
		threadVar.clear();
	}

	/**
	 * Returns the value created by the <code>construct</code> method. Returns
	 * null if either the constructing thread or the current thread was
	 * interrupted before a value was produced.
	 * 
	 * @return the value created by the <code>construct</code> method
	 */
	public Object get()
	{
		while (true)
		{
			Thread t = threadVar.get();

			if (t == null)
			{
				return getValue();
			}

			try
			{
				t.join();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt(); 
				return null;
			}
		}
	}

	/**
	 * Starts the worker thread.
	 */
	public void start()
	{
		this.interrupted = false;
		Thread t = threadVar.get();

		if (t != null)
		{
			t.start();
		}
	}

	/**
	 * Class to maintain reference to current worker thread under separate
	 * synchronization control.
	 */
	private static class ThreadVar
	{
		/** the actual thread */
		private Thread thread;

		/**
		 * Creates a new <CODE>ThreadVar</CODE>
		 * 
		 * @param t
		 *            the actual thread
		 */
		ThreadVar(Thread t)
		{
			thread = t;
		}

		/**
		 * Returns the actual thread
		 * 
		 * @return the actualthread
		 */
		synchronized Thread get()
		{
			return thread;
		}

		/** Set {@link #thread thread}to NULL */
		synchronized void clear()
		{
			thread = null;
		}
	}
}