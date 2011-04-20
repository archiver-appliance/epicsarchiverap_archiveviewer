/*
 * Created on Mar 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.model;

import java.util.HashMap;

import epics.archiveviewer.Exporter;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.export.SpreadSheetExporter;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportersRepository {
	//static stuff
	private final HashMap exportersRepository;
	private final HashMap exportersExtensions;
	
	public ExportersRepository() throws Exception
	{
		this.exportersRepository = new HashMap();
		this.exportersExtensions = new HashMap();
		
		Exporter ex;
		
		//now for foreign exporters
		String[] exporterClassNames = AVBaseConstants.AVAILABLE_FOREIGN_EXPORTER_CLASS_NAMES;
		if(exporterClassNames != null)
		{
			for(int i=0; i<exporterClassNames.length; i++)
			{
				ex = (Exporter) Class.forName(exporterClassNames[i]).newInstance();
				this.exportersRepository.put(ex.getId(), ex);
				this.exportersExtensions.put(ex.getId(), ex.getExt());
			}
		}

	}
	
	public String[] getRegisteredIds()
	{
		return (String[]) this.exportersRepository.keySet().toArray(new String[this.exportersRepository.size()]);
	}
	
	public Exporter getExporter(String keyLabel)
	{
		return (Exporter) this.exportersRepository.get(keyLabel);
	}

}
