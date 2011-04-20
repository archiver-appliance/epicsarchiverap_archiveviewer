package epics.archiveviewer.xal.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 * This class represents a <CODE>JFrame</CODE> that delivers GUI messages to
 * the user. Three types of messages are supported: info messages, warnings, and
 * errors. The destinction between warining and errors is merely cosmetic.
 * Caution: This class is a Singleton! Do not dispose it until the program is
 * ready to exit!!!
 */
public class MessagesDialog extends JDialog
{
	/**
	 * a constant for the message type "info"; such messages are considered
	 * entirely 'main' and possess no 'details'
	 */
	public static final int INFO_MESSAGE = 0;

	/** a constant for the message type "warning" */
	public static final int WARNING_MESSAGE = 1;

	/** a constant for the message type "Error" */
	public static final int ERROR_MESSAGE = 2;

	/**
	 * a constant for the initial number of rows in the text area where the main
	 * message is to be displayed
	 */
	private static final int INITIAL_ROWS_FOR_MAIN_MESSAGE = 2;

	/**
	 * a constant for the initial number of rows in the text area where the
	 * detailed message is to be displayed
	 */
	private static final int INITIAL_ROWS_FOR_DETAILED_MESSAGE = 8;

	/**
	 * a constant for the initial number of columns in the text areas' a
	 * "column" is the width of the letter "m" in the current <CODE>Font
	 * </CODE>
	 */
	private static final int INITIAL_COLUMNS = 30;

	/**
	 * this counter keeps track of the number of <CODE>SingleMessagePanel
	 * </CODE> s displayed by this <CODE>UserMessagesFrame</CODE>
	 */
	private static int count;
	
	private static Color BG_COLOR = (new JButton()).getBackground();

	//SINGLETON

	/** the only instance of the <CODE>USerMessagesFrame</CODE> */
	private static MessagesDialog mfInstance;
	
	/**
	 * Returns the <CODE>String</CODE> representation of the specified message
	 * type
	 * 
	 * @see #WARNING_MESSAGE_TYPE
	 * @param messageType
	 *            the type of message (see the class constants)
	 * @return the <CODE>String</CODE> representation of the specified message
	 *         type
	 */
	private static String getMessageTypeAsString(int messageType)
	{
		switch (messageType)
		{
		case WARNING_MESSAGE:
			return "Warning";

		case ERROR_MESSAGE:
			return "Error";

		default:
			return "";
		}
	}

	/**
	 * Returns the only instance of the <CODE>UserMessagesFrame</CODE> (=>
	 * Singleton pattern). Thus, DO NOT DISPOSE until the prgram is ready to
	 * exit!
	 * 
	 * @param title
	 *            the title of the <CODE>UserMessagesFrame</CODE>
	 * @return the only instance of the <CODE>UserMessagesFrame</CODE> (=>
	 *         Singleton pattern)
	 */
	public static MessagesDialog getInstance(Frame owner)
	{
		if (mfInstance == null)
		{
			mfInstance = new MessagesDialog(owner);
		}

		return mfInstance;
	}
	
	/** the tabbed pane that stores the <CODE>SingleMessagePanel</CODE> s */
	private JTabbedPane tabbedPane;

	/**
	 * Creates a new instance of the <CODE>UserMessagesFrame</CODE>
	 * 
	 * @param title
	 *            the title of the frame
	 */
	private MessagesDialog(Frame owner)
	{
		super(owner, "Attention...", false);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				clearAndHide();
			}
		});
		tabbedPane = new JTabbedPane();
		count = 0;
	}

	/** Clears the tabbed pane and hides this frame */
	private void clearAndHide()
	{
		tabbedPane.removeAll();
		count = 0;
		hide();
	}

	/**
	 * Adds a <CODE>SingleMessagePanel</CODE> for the specified arguments to
	 * the tabbed pane
	 * 
	 * @param type
	 *            the type of the message
	 * @param mainMessage
	 *            the main message
	 * @param e
	 *            the exception whose <CODE>String</CODE> representation acts
	 *            as the detailed message
	 */
	public void addMessage(int type, String mainMessage, Exception e)
	{
		try
		{
			SingleMessagePanel smp = new SingleMessagePanel(this, mainMessage, e);
			
			if (count == 0)
			{
				getContentPane().setLayout(new BorderLayout());
				getContentPane().add(tabbedPane, BorderLayout.CENTER);
			}

			smp.setName(getMessageTypeAsString(type) + "(" + count + ")");
			tabbedPane.add(smp);
			tabbedPane.setSelectedIndex(count);
			count++;
			this.setLocationRelativeTo(getOwner());
			this.pack();
			this.show();
		}
		catch (Exception ex)
		{
			//do nothing, if we couldn't create a
			// <CODE>SingleMessagePanel</CODE> for the specified
			//params
		}
	}

	/**
	 * This class is a panel that displays a single message. The user can choose
	 * to display/hide the detailed message at any time
	 */
	private class SingleMessagePanel extends JPanel
	{
		/**
		 * a constant for the width of a line (in Java units); depends on the
		 * used <CODE>Font</CODE>
		 */
		private final int INITIAL_LINE_LENGTH;

		/**
		 * the instance of <CODE>UserMessagesFrame</CODE> to which this <CODE>
		 * SingleMessagePanel</CODE> is added
		 */
		private final MessagesDialog parentFrame;

		/** the panel that displays the main message */
		private JPanel mainPanel;

		/** the panel that displays the detailed message */
		private JPanel detailsPanel;

		/** the text are where the main message is displayed */
		private JTextArea mainTextArea;

		/** the text are where the detailed message is displayed */
		private JTextArea detailedTextArea;

		/** the OK button */
		private JButton okButton;

		/**
		 * the button for either display or hide the detailed message; disabled
		 * for "info" messages
		 */
		private JButton detailsButton;

		/**
		 * Creates a new instance of <CODE>SingleMessagePanel</CODE> for the
		 * spcified arguments.
		 * 
		 * @param parentFrame
		 *            the frame this <CODE>SingleMessagePanel</CODE> is to be
		 *            added to; NOT NULL!!!
		 * @param mainMessage
		 *            the main message to be displayed by this <CODE>
		 *            SingleMessagePanel</CODE>
		 * @param e
		 *            the exception that will be displayed in this <CODE>
		 *            SingleMessagePanel</CODE> as the detailed message; if
		 *            NULL, the main message is considered to be an "info
		 *            message"
		 * @throws NullPointerException
		 *             of either the parent frame was not specified; or both,
		 *             main message and the exception are NULL (so there is
		 *             nothing to display)
		 */
		public SingleMessagePanel(final MessagesDialog parentFrame,
				String mainMessage, Exception e) throws NullPointerException
		{
			if ((parentFrame == null) || ((mainMessage == null) && (e == null)))
			{
				throw new NullPointerException();
			}

			this.parentFrame = parentFrame;
			buildComponents();

			INITIAL_LINE_LENGTH = INITIAL_COLUMNS
					* mainTextArea.getFontMetrics(mainTextArea.getFont())
							.stringWidth("m");

			setMainMessage(mainMessage);
			
			setLayout(new BorderLayout());

			if (e == null)
			{
				detailsButton.setEnabled(false);
			}
			else
			{
				setDetailedMessage(e.toString());
			}

			showMainMessageOnly();
		}

		/**
		 * Builds the GUI components; see {@link #mainPanel private attributes}
		 */
		private void buildComponents()
		{
			this.mainTextArea = new JTextArea(INITIAL_ROWS_FOR_MAIN_MESSAGE,
					INITIAL_COLUMNS);

			//set font to "bold"
			mainTextArea.setFont(mainTextArea.getFont().deriveFont(Font.BOLD));
			mainTextArea.setBackground(MessagesDialog.BG_COLOR);
			mainTextArea.setEditable(false);
			mainTextArea.setLineWrap(true);
			//wrap at empty spaces only
			mainTextArea.setWrapStyleWord(true);

			detailedTextArea = new JTextArea(INITIAL_ROWS_FOR_DETAILED_MESSAGE,
					INITIAL_COLUMNS);
			detailedTextArea.setBackground(parentFrame.getBackground());
			detailedTextArea.setEditable(false);
			detailedTextArea.setLineWrap(true);

			//wrap anywhere
			detailedTextArea.setWrapStyleWord(false);

			detailsButton = new JButton();
			detailsButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (detailsPanel.isShowing() == true)
					{
						showMainMessageOnly();
					}
					else
					{
						showDetailedMessage();
					}

					parentFrame.pack();
				}
			});

			okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					parentFrame.clearAndHide();
				}
			});

			//the next two panels are needed as parts of mainPanel
			JPanel buttonsPanel1 = new JPanel(new BorderLayout());
			buttonsPanel1.add(okButton, BorderLayout.WEST);
			buttonsPanel1.add(detailsButton, BorderLayout.EAST);

			JPanel buttonsPanel2 = new JPanel(new BorderLayout());
			buttonsPanel2.add(buttonsPanel1, BorderLayout.EAST);

			this.mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(mainTextArea, BorderLayout.NORTH);
			mainPanel.add(buttonsPanel2, BorderLayout.SOUTH);
			
			JScrollPane detailsScrollPane = new JScrollPane(detailedTextArea);
			detailsScrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			this.detailsPanel = new JPanel(new BorderLayout());
			detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
		}

		/**
		 * Sets the specified message to be displayed in the <CODE>mainTextArea
		 * </CODE> Calculates the number of needed rows to displays this message
		 * on screen
		 * 
		 * @param message
		 *            the message to be displayed in the main text area
		 */
		private void setMainMessage(String message)
		{
			int rows = 1 + mainTextArea.getFontMetrics(mainTextArea.getFont())
					.stringWidth(message)
					/ INITIAL_LINE_LENGTH;
			mainTextArea.setRows(rows);
			mainTextArea.setText(message);
		}

		/**
		 * Sets the specified message to be displayed in the <CODE>
		 * detailedTextArea</CODE>
		 * 
		 * @param message
		 *            the message to be displayed in the detailed text area
		 */
		private void setDetailedMessage(String message)
		{
			detailedTextArea.setText(message);
		}

		/**
		 * Constructs this <CODE>MessagePanel</CODE> in such way that only the
		 * main message is shown
		 */
		private void showMainMessageOnly()
		{
			int original_Width = parentFrame.getSize().width;
			removeAll();
			detailsButton.setText("Show details");
			add(mainPanel);
			setSize(original_Width, this.getPreferredSize().height);
		}

		/**
		 * Constructs this <CODE>MessagePanel</CODE> in such way that both,
		 * the main message and the detailed message are shown
		 */
		private void showDetailedMessage()
		{
			int original_Width = parentFrame.getSize().width;
			removeAll();
			add(mainPanel, BorderLayout.NORTH);
			add(detailsPanel, BorderLayout.CENTER);
			detailsButton.setText("Hide details");
			setSize(original_Width, mainPanel.getPreferredSize().height);
		}
	}
}
