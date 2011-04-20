package epics.archiveviewer.clients.channelarchiver;

import epics.archiveviewer.RetrievalMethod;

/**
 * <code>RetrievalMethodImpl</code> ... DOCUMENT ME!
 * 
 * @author <a href="mailto:igor.kriznar@cosylab.com">Igor Kriznar </a>
 * @version $Id: RetrievalMethodImpl.java,v 1.1.1.1 2007/01/29 22:46:39 rdh Exp $
 * 
 * @since Jul 20, 2004.
 */
public class RetrievalMethodImpl extends RetrievalMethod
{

	/**
	 * DOCUMENT ME!
	 * 
	 * @param key
	 * @param name
	 * @param description
	 * @param timestampAligned
	 * @param resolutionReduced
	 */
	public RetrievalMethodImpl(Object key, String name, String description,
			boolean timestampAligned, boolean resolutionReduced)
	{
		super(key, name, description, timestampAligned, resolutionReduced);
	}

}