/*
 * Created on Feb 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base;

import java.awt.event.ActionListener;

import javax.swing.Timer;

import epics.archiveviewer.ClientPlugin;
import epics.archiveviewer.MessageListener;
import epics.archiveviewer.base.model.ArchiveDirectoriesRepository;
import epics.archiveviewer.base.model.AxesIntervalsManager;
import epics.archiveviewer.base.model.ExportModel;
import epics.archiveviewer.base.model.ExportersRepository;
import epics.archiveviewer.base.model.MatchingAVEsRepository;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.model.PlotPluginsRepository;
import epics.archiveviewer.base.model.ValuesContainersCache;
import epics.archiveviewer.base.model.listeners.ProgressListener;
import gov.sns.tools.messaging.MessageCenter;

/**
 * @author serge
 * @version 14-Sep-2009 Bob Hall.  Added includeSparcified.
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVBase implements MessageListener
{
	private final MessageCenter messageCenter;
	private final MessageListener mlProxy;
	
	private final Timer progressTimer;
	
	private final PlotModel plotModel;
	//clear the axesIntervalsManager if you want to plot data from plot model
	private final AxesIntervalsManager axesIntervalsManager;
	private final ArchiveDirectoriesRepository adsRepository;
	private final ExportersRepository exportersRepository;
    private final MatchingAVEsRepository matchingAVEsRepository;
    private final PlotPluginsRepository plotPluginsRepository;
    private final ValuesContainersCache vcsCache;
    private final ClientPlugin client;
	private ExportModel exportModel;
	
	private String homeDirectory;
	/*
	 * added exportDirectory to have correct home directory when bringing up
	 * Filechooser in export Panel
	 * last modified: John Lee
	 */
	private String exportDirectory;
	private String snapshotDirectory;

	private String sparsificationOperator;
	
	public AVBase() throws Exception
	{
		String clientClassName = AVBaseConstants.AV_CLIENT_CLASS_NAME;
		
		this.client = (ClientPlugin) Class.forName(clientClassName).newInstance();
		
		this.messageCenter = MessageCenter.newCenter();
		this.mlProxy = (MessageListener) messageCenter.registerSource(this, MessageListener.class);
		
		this.progressTimer = new Timer(100, null);
		this.progressTimer.setRepeats(true);
		
	    this.plotModel = new PlotModel();
		this.axesIntervalsManager = new AxesIntervalsManager();
	    this.adsRepository = new ArchiveDirectoriesRepository();
	    this.exportersRepository = new ExportersRepository();
	    this.matchingAVEsRepository = new MatchingAVEsRepository();
	    this.plotPluginsRepository = new PlotPluginsRepository();
	    this.vcsCache = new ValuesContainersCache();
	}
	
	public ArchiveDirectoriesRepository getArchiveDirectoriesRepository()
	{
	    return this.adsRepository;
	}
	
	public MatchingAVEsRepository getMatchingAVEsRepository()
	{
	    return this.matchingAVEsRepository;
	}
	
	public ExportersRepository getExportersRepository()
	{
	    return this.exportersRepository;
	}
	
	
	public PlotPluginsRepository getPlotPluginsRepository()
	{
	    return this.plotPluginsRepository;
	}
	
	public ValuesContainersCache getVCsCache()
	{
	    return this.vcsCache;
	}
	
	public void setExportModel(ExportModel em)
	{
		this.exportModel = em;
	}
	
	public ClientPlugin getClient()
	{
		return this.client;
	}
	
	public PlotModel getPlotModel() {
		return this.plotModel;
	}
	
	public ExportModel getExportModel()
	{
		return this.exportModel;
	}

	//the axes ranges are going to be taken from here!
	public AxesIntervalsManager getAxesIntervalsManager()
	{
		return this.axesIntervalsManager;
	}
	
	public void displayError(String s, Exception e) {
		if(this.mlProxy != null)
			this.mlProxy.displayError(s, e);
	}

	public void displayWarning(String s, Exception e) {
		if(mlProxy != null)
			this.mlProxy.displayWarning(s, e);
	}

	public void displayInformation(String s) {
		if(mlProxy != null)
			this.mlProxy.displayInformation(s);
	}
	
	public void addMessageListener(MessageListener ml)
	{
		this.messageCenter.registerTarget(ml, this, MessageListener.class);
	}
	
	public synchronized void startProgressTimer()
	{
		this.progressTimer.start();
	}
	
	public synchronized void removeProgressListener(ProgressListener pl)
	{
		this.progressTimer.removeActionListener(pl);
		ActionListener[] actionListeners = this.progressTimer.getActionListeners();
		if(actionListeners == null || actionListeners.length == 0)
			this.progressTimer.stop();
	}
	
	public synchronized void interruptAllProgressListeners()
	{
		ActionListener[] als = this.progressTimer.getActionListeners();
		
		ProgressListener pl = null;
		
		for(int i=0; i<als.length; i++)
		{
			if(als[i] instanceof ProgressListener)
			{
				pl = (ProgressListener) als[i];
				pl.interrupt();
			}
		}
	}
	
	public void addProgressListener(ProgressListener pl)
	{
		this.progressTimer.addActionListener(pl);
	}

	public String getHomeDirectory()
	{
		return this.homeDirectory;
	}

	public void setHomeDirectory(String homeDir)
	{
		this.homeDirectory = homeDir;
	}

	public String getExportDirectory (){
		return this.exportDirectory;
	}

	public void setExportDirectory (String exportDir){
		this.exportDirectory =  exportDir;
	}

	public String getSnapshotDirectory()
	{
		return this.snapshotDirectory;
	}

	public void setSnapshotDirectory(String snapshotDir)
	{
		this.snapshotDirectory = snapshotDir;
	}

	/**
	 * @return the sparsificationOperator
	 */
	public String getSparsificationOperator() {
		return sparsificationOperator;
	}

	/**
	 * @param sparsificationOperator the sparsificationOperator to set
	 */
	public void setSparsificationOperator(String sparsificationOperator) {
		this.sparsificationOperator = sparsificationOperator;
	}
}
