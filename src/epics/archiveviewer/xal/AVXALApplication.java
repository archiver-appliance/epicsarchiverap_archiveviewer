package epics.archiveviewer.xal;

import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.base.util.CommandLineArgsParser;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.controller.NewConnectionPanelController;
import epics.archiveviewer.xal.controller.PreferencesPanelController;
import epics.archiveviewer.xal.controller.ServerInfoPanelContoller;
import epics.archiveviewer.xal.controller.export.ExportController;
import epics.archiveviewer.xal.controller.print.PrintController;

import gov.sns.application.Application;
import gov.sns.application.ApplicationAdaptor;
import gov.sns.application.Commander;
import gov.sns.application.XalDocument;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Main is the ApplicationAdaptor for the Template application.
 * 
 * @author somebody Mods: 08-Mar-2010, Bob Hall Added code to process the new
 *         menudef.properties print_action "specified-default-print". This added
 *         code creates a new instance of the new PrintController class, which
 *         was created to be able to specify a default printer. Previously the
 *         menudef.properties print_action was "print-document", which was
 *         handled in XAL and controlled by PrintManager.java (now no longer
 *         used by the Archive Viewer).
 * 
 */
public class AVXALApplication extends ApplicationAdaptor {
	// --------- Document management -------------------------------------------
	private ExportController exportController;
	private PrintController printController;
	private AVController avController;

	protected void customizeCommands(Commander commander) {
		AbstractAction newConnectionAction = new AbstractAction(
				"establish-new-connection") {
			public void actionPerformed(ActionEvent e) {
				try {
					new NewConnectionPanelController(getAVController());
				} catch (Exception ex) {
					getAVController().getAVBase().displayError(
							"Can't display new connection dialog", ex);
				}
			}
		};
		commander.registerAction(newConnectionAction);

		AbstractAction loadPreferencesAction = new AbstractAction(
				"load-preferences") {
			public void actionPerformed(ActionEvent e) {
				try {
					new PreferencesPanelController(getAVController());
				} catch (Exception ex) {
					getAVController().getAVBase().displayError(
							"Can't display preferences dialog", ex);
				}
			}
		};
		commander.registerAction(loadPreferencesAction);

		AbstractAction reconnectAction = new AbstractAction("reconnect") {
			public void actionPerformed(ActionEvent e) {
				try {
					getAVController().reconnect();
				} catch (Exception ex) {
					getAVController().getAVBase().displayError(
							"Can't reconnect", ex);
				}

			}
		};
		commander.registerAction(reconnectAction);

		AbstractAction printAction = new AbstractAction(
				"specified-default-print") {
			public void actionPerformed(ActionEvent e) {
				// lazy initialization
				if (printController == null)
					printController = new PrintController(getAVController()
							.getMainWindow());
				printController.print();
			}
		};
		commander.registerAction(printAction);

		AbstractAction exportAction = new AbstractAction("export") {
			public void actionPerformed(ActionEvent e) {
				// lazy initialization
				if (exportController == null)
					exportController = new ExportController(getAVController());
				exportController.showExportDialog();
			}
		};
		commander.registerAction(exportAction);
		AbstractAction clearAllAction = new AbstractAction("clear-all") {
			public void actionPerformed(ActionEvent e) {
				getAVController().clearAll();
			}
		};
		commander.registerAction(clearAllAction);

		AbstractAction fullScreenAction = new AbstractAction("maximize_window") {
			public void actionPerformed(ActionEvent e) {
				getAVController().resizeToFullScreen();
			}
		};
		commander.registerAction(fullScreenAction);

		AbstractAction assignSelectedArchiveAction = new AbstractAction(
				"assign-selected-archive") {
			public void actionPerformed(ActionEvent e) {
				getAVController().assignSelectedArchiveToSelectedAVEs();
			}
		};
		commander.registerAction(assignSelectedArchiveAction);

		AbstractAction alignRangeAxesAction = new AbstractAction(
				"align-range-axes") {
			public void actionPerformed(ActionEvent e) {
				try {
					AVBaseUtilities.alignRangeAxesRanges(getAVController()
							.getAVBase());
				} catch (Exception ex) {
					getAVController().getAVBase().displayError(
							"Can't align range axes", ex);
				}
			}
		};
		commander.registerAction(alignRangeAxesAction);

		AbstractAction sortByAVENameAction = new AbstractAction(
				"sort-by-ave-name") {
			public void actionPerformed(ActionEvent e) {
				try {
					PlotModel plotModel = getAVController().getAVBase()
							.getPlotModel();
					plotModel.sortAVEs(PlotModel.SORT_BY_AVE_NAME);
					plotModel.fireAVEsUpdated();
				} catch (Exception ex) {
					getAVController().getAVBase().displayError(
							"Can't sort AV entries", ex);
				}
			}
		};
		commander.registerAction(sortByAVENameAction);

		AbstractAction sortByADNameAction = new AbstractAction(
				"sort-by-archive-name") {
			public void actionPerformed(ActionEvent e) {
				try {
					PlotModel plotModel = getAVController().getAVBase()
							.getPlotModel();
					plotModel.sortAVEs(PlotModel.SORT_BY_AD_NAME);
					plotModel.fireAVEsUpdated();
				} catch (Exception ex) {
					getAVController().getAVBase().displayError(
							"Can't sort AV entries", ex);
				}
			}
		};
		commander.registerAction(sortByADNameAction);

		AbstractAction assignSameColorToAVEsWithSameNameAction = new AbstractAction(
				"assign-same-color") {
			public void actionPerformed(ActionEvent e) {
				try {
					PlotModel plotModel = getAVController().getAVBase()
							.getPlotModel();
					plotModel.assignSameColorToAVEsWithSameName();
					plotModel.fireAVEsUpdated();
				} catch (Exception ex) {
					getAVController().getAVBase().displayError(
							"Can't assign color to AV entries", ex);
				}
			}
		};
		commander.registerAction(assignSameColorToAVEsWithSameNameAction);

		AbstractAction clearCacheAction = new AbstractAction("clear-cache") {
			public void actionPerformed(ActionEvent e) {
				getAVController().getAVBase().getVCsCache().clear();
			}
		};
		commander.registerAction(clearCacheAction);

		AbstractAction helpAction = new AbstractAction("show-help-contents") {
			public void actionPerformed(ActionEvent e) {
				String helpCommand = "firefox https://confluence.slac.stanford.edu/display/LCLSHELP/Archive+Viewer";
				try {
					Runtime.getRuntime().exec(helpCommand);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		};
		commander.registerAction(helpAction);

		AbstractAction serverInfoAction = new AbstractAction("show-server-info") {
			public void actionPerformed(ActionEvent e) {
				new ServerInfoPanelContoller(getAVController());
			}
		};
		commander.registerAction(serverInfoAction);
	}

	public AVXALApplication(CommandLineArgsParser claParser) {
		Application.launch(this);
		getAVController().initSpecificControllers();

		String[] params = null;
		if (claParser.containsOption("u")) {
			try {
				params = claParser.getParameters("u");
				this.avController.connect(params[0], false);
			} catch (Exception e) {
				getAVController().getAVBase().displayError("Couldn't connect",
						e);
			}
		}

		if (claParser.containsOption("f")) {
			params = claParser.getParameters("f");
			try {
				this.avController.loadConfiguration(params[0], false);
				if (claParser.containsOption("plot"))
					this.avController.getMainAVPanel().getAxesSettingsPanel()
							.getPlotButton().doClick();
			} catch (Exception e) {
				getAVController().getAVBase().displayError(
						"Couldn't load plot configuration file", e);
			}
		}
		if (claParser.containsOption("a")) {
			params = claParser.getParameters("a");
			this.avController.selectArchiveDirectory(params[0].trim());
		}
		/*
		 * modified option, d, to set exportDirectroy to be the same as option d
		 * if e is not specified Last modified: John Lee
		 */
		if (claParser.containsOption("d")) {
			params = claParser.getParameters("d");
			this.avController.getAVBase().setHomeDirectory(params[0]);

			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					Application.getApp().getOpenFileChooser()
							.setCurrentDirectory(
									new File(avController.getAVBase()
											.getHomeDirectory()));
					Application.getApp().getSaveFileChooser()
							.setCurrentDirectory(
									new File(avController.getAVBase()
											.getHomeDirectory()));
				}

			});

			if (!claParser.containsOption("e"))
				this.avController.getAVBase().setExportDirectory(params[0]);

		}
		/*
		 * added option, e, to set exportDirectroy to the path given in the
		 * command line Last modified: John Lee
		 */
		if (claParser.containsOption("e")) {
			params = claParser.getParameters("e");
			this.avController.getAVBase().setExportDirectory(params[0]);
		}
		if (claParser.containsOption("snapshot_dir")) {
			params = claParser.getParameters("snapshot_dir");
			this.avController.getAVBase().setSnapshotDirectory(params[0]);
		}
		if (claParser.containsOption("window_size")) {
			params = claParser.getParameters("window_size");
			try {
				int width = Integer.parseInt(params[0]);
				int height = -1;
				if (params.length > 1)
					height = Integer.parseInt(params[1]);
				else
					height = (int) (width * 3 / 4);

				this.avController.getMainWindow().setSize(width, height);
			} catch (Exception e) {
				getAVController().getAVBase().displayError(
						"Couldn't resize the window", e);
			}
		}
	}

	/**
	 * Returns the text file suffixes of files this application can open.
	 * 
	 * @return Suffixes of readable files
	 */
	public String[] readableDocumentTypes() {
		return new String[] { WILDCARD_FILE_EXTENSION };
	}

	/**
	 * Returns the text file suffixes of files this application can write.
	 * 
	 * @return Suffixes of writable files
	 */
	public String[] writableDocumentTypes() {
		return new String[] { "xml", "avc" };
	}

	/**
	 * Implement this method to return an instance of my custom document.
	 * 
	 * @return An instance of my custom document.
	 */
	public XalDocument newEmptyDocument() {
		try {
			this.avController = new AVController();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.avController;
	}

	/**
	 * Implement this method to return an instance of my custom document
	 * corresponding to the specified URL.
	 * 
	 * @param url
	 *            The URL of the file to open.
	 * @return An instance of my custom document.
	 */
	public XalDocument newDocument(URL url) {
		try {
			this.avController.loadConfiguration(url.getFile(), true);
			this.avController.setSource(url);
			return this.avController;
		} catch (Exception e) {
			return null;
		}
	}

	// --------- Global application management ---------------------------------

	/**
	 * Specifies the name of my application.
	 * 
	 * @return Name of my application.
	 */
	public String applicationName() {
		return "ArchiveViewer";
	}

	// --------- Application events --------------------------------------------

	/**
	 * Capture the application launched event and print it. This is an optional
	 * hook that can be used to do something useful at the end of the
	 * application launch.
	 */
	public void applicationFinishedLaunching() {
		System.out.println("Application has finished launching!");
	}

	public AVController getAVController() {
		return this.avController;
	}

	/** The main method of the application. */
	public static void launch(CommandLineArgsParser claParser) throws Exception {
		try {
			if (claParser.containsOption("help")) {
				CommandLineArgsParser.printHelp();
			} else if (claParser.containsOption("version")) {
				CommandLineArgsParser.printVersion();
			} else
				new AVXALApplication(claParser);
		} catch (Exception exception) {
			System.err.println(exception.getMessage());
			exception.printStackTrace();
			Application.displayApplicationError("Launch Exception",
					"Launch Exception", exception);
			throw exception;
		}
	}
}
