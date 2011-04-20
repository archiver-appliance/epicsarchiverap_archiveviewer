/*
 * Created on Feb 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.persistence;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ArchiveDirectory;
import epics.archiveviewer.DrawType;
import epics.archiveviewer.LegendInfo;
import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.TimeAxisLocation;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.fundamental.FormulaGraph;
import epics.archiveviewer.base.fundamental.FormulaParameter;
import epics.archiveviewer.base.fundamental.Graph;
import epics.archiveviewer.base.fundamental.PVGraph;
import epics.archiveviewer.base.fundamental.RangeAxis;
import epics.archiveviewer.base.fundamental.TimeAxis;
import epics.archiveviewer.base.model.ArchiveDirectoriesRepository;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.model.listeners.PlotModelListener;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.base.util.ColorUtilities;

/**
 * @author serge, katja
 *
 *	CURRENT DTD (keep updated + report all changes in AVDocumentHelper class)
 *	-------------------------------------------------------------------------
 *
 * <!DOCTYPE AVConfiguration[
 * <!ELEMENT AVConfiguration 
 * 	(connection_parameter, (pv|formula)*, (time_axis)+, (range_axis)+, legend_configuration?, plot_title
 * >
 * <!ELEMENT connection_parameter (#PCDATA)> //if empty => null
 * <!ELEMENT pv(time_axis_name, range_axis_name, color, draw_type, draw_width, visibility)>
 * <!ELEMENT formula
 * 	(term, (argument_ave)+, method_name, nr_values,
 * 	time_axis_name, range_axis_name, color, draw_type, draw_width, visibility)
 * >
 * <!ELEMENT time_axis (start, end, location)>
 * <!ELEMENT range_axis (min, max, type, location)>
 * <!ELEMENT legend_configuration EMPTY>
 * <!ELEMENT plot_title (#PCDATA)> //if empty => ""
 * <!ELEMENT time_axis_name (#PCDATA)>
 * <!ELEMENT range_axis_name (#PCDATA)> //if empty => means "plot normalized"
 * <!ELEMENT color (#PCDATA)> //integer
 * <!ELEMENT draw_type (#PCDATA)> //string by default
 * <!ELEMENT draw_width (#PCDATA)> //float
 * <!ELEMENT visibility (#PCDATA)> //boolean
 * <!ELEMENT argument_ave EMPTY>
 * <!ELEMENT term (#PCDATA)>
 * <!ELEMENT method_name (#PCDATA)>
 * <!ELEMENT nr_values (#PCDATA)> //integer
 * <!ELEMENT start (#PCDATA)>
 * <!ELEMENT end (#PCDATA)>
 * <!ELEMENT location (#PCDATA>
 * <!ELEMENT min (#PCDATA)> //if empty => null
 * <!ELEMENT max (#PCDATA)> //if empty => null
 * <!ELEMENT type (#PCDATA)>
 * // ATTRIBUTES /////////////////////////////// 
 * <!ATTLIST pv name CDATA #REQUIRED>
 * <!ATTLIST pv directory_name CDATA #REQUIRED>
 * <!ATTLIST formula name CDATA #REQUIRED>
 * <!ATTLIST formula directory_name CDATA #REQUIRED>
 * <!ATTLIST time_axis name ID #REQUIRED>
 * <!ATTLIST range_axis name ID #REQUIRED>
 * <!ATTLIST argument_ave name CDATA #REQUIRED>
 * <!ATTLIST argument_ave variable CDATA #REQUIRED>
 * <!ATTLIST legend_configuration show_ave_name CDATA #REQUIRED> //boolean
 * <!ATTLIST legend_configuration show_directory_name CDATA #REQUIRED> //boolean
 * <!ATTLIST legend_configuration show_range CDATA #REQUIRED> //boolean
 * <!ATTLIST legend_configuration show_units CDATA #REQUIRED> //boolean
 * ]>
*/
public class XMLPlotModel {
	public static final String BASIC_DOCUMENT_AS_STRING =
		"<?xml version=\"1.0\" encoding=\"UTF-16\" ?>" +
		"<AVConfiguration>" +
			"<connection_parameter/>"+
			"<legend_configuration show_ave_name=\"true\" show_directory_name=\"true\" show_range=\"true\" show_units=\"true\">"+
			"</legend_configuration>"+
			"<plot_title/>"+
		"</AVConfiguration>";

	//the document
	private final Document doc;
	
	private Element getPVElement(String pvName, String directoryName) throws Exception
	{
		NodeList nl = this.doc.getElementsByTagName("pv");
		int nlLength = nl.getLength();
		
		Element el = null;
		for(int i=0; i<nlLength; i++)
		{
			el = (Element) nl.item(i);
			if(	el.getAttribute("name").equals(pvName) &&
				el.getAttribute("directory_name").trim().equals(directoryName.trim())	
			)
			{	
				return el;
			}
		}
		
		throw new NullPointerException("No such PV");
	}
	
	private Element getFormulaElement(String formulaName, String directoryName) throws Exception
	{
		NodeList nl = this.doc.getElementsByTagName("formula");
		int nlLength = nl.getLength();
		
		Element el = null;
		for(int i=0; i<nlLength; i++)
		{
			el = (Element) nl.item(i);
			if(	el.getAttribute("name").equals(formulaName) &&
				el.getAttribute("directory_name").trim().equals(directoryName.trim())	
			)
			{	
				return el;
			}
		}
		
		throw new NullPointerException("No such formula");
	}
	
	private Element getTimeAxisElement(String timeAxisName) throws Exception
	{
		NodeList nl = this.doc.getElementsByTagName("time_axis");
		int nlLength = nl.getLength();
		
		Element el = null;
		for(int i=0; i<nlLength; i++)
		{
			el = (Element) nl.item(i);
			if(el.getAttribute("name").equals(timeAxisName))
			{	
				return el;
			}
		}
		
		throw new NullPointerException("No such time axis");
	}
	
	private Element getRangeAxisElement(String rangeAxisName) throws Exception
	{
		NodeList nl = this.doc.getElementsByTagName("range_axis");
		int nlLength = nl.getLength();
		
		Element el = null;
		for(int i=0; i<nlLength; i++)
		{
			el = (Element) nl.item(i);
			if(el.getAttribute("name").equals(rangeAxisName))
			{	
				return el;
			}
		}
		
		throw new NullPointerException("No such time axis");
	}
	
	private String getPVParameter(String pvName, String directoryName, String parameterName) throws Exception
	{
		Node n = 	getPVElement(pvName, directoryName).
					getElementsByTagName(parameterName).
					item(0).
					getFirstChild();
		if(n == null)
			return null;
		else
			return n.getNodeValue();
	}
	
	private String getFormulaParameterExceptArgumentPVs(
			String pvName, 
			String directoryName, 
			String parameterName) throws Exception
	{
		if(parameterName.equalsIgnoreCase("argument_pv"))
			throw new IllegalArgumentException("Don't use this method to get argument pvs");
		Node n = 	getFormulaElement(pvName, directoryName).
					getElementsByTagName(parameterName).
					item(0).
					getFirstChild();
		if(n == null)
			return null;
		else
			return n.getNodeValue();
	}
	
	//if the parameter is absent, returns NULL
	private String getTimeAxisParameter(String timeAxisName, String parameterName) throws Exception
	{
		Node n = 	getTimeAxisElement(timeAxisName).
					getElementsByTagName(parameterName).
					item(0).
					getFirstChild();
		if(n == null)
			return null;
		else
			return n.getNodeValue();
	}
	
	//if the parameter is absent (is an empty element), returns NULL
	private String getRangeAxisParameter(String rangeAxisName, String parameterName) throws Exception
	{
		Node n = 	getRangeAxisElement(rangeAxisName).
					getElementsByTagName(parameterName).
					item(0).
					getFirstChild();
		if(n == null)
			return null;
		else
			return n.getNodeValue();
	}
	
	private Element createPVElement(
			String pvName, 
			String directoryName, 
			String timeAxisName,
			String rangeAxisName,
			String colorRGBValue,
			String drawType,
			String drawWidth,
			String visibility)
	{
		Element tANameElement = this.doc.createElement("time_axis_name");
		tANameElement.appendChild(this.doc.createTextNode(timeAxisName));
		
		Element rANameElement = this.doc.createElement("range_axis_name");
		rANameElement.appendChild(this.doc.createTextNode(rangeAxisName));
		
		Element colorElement = this.doc.createElement("color");		
		colorElement.appendChild(this.doc.createTextNode(colorRGBValue));
		
		Element drawTypeElement = this.doc.createElement("draw_type");		
		drawTypeElement.appendChild(this.doc.createTextNode(drawType));
		
		Element drawWidthElement = this.doc.createElement("draw_width");		
		drawWidthElement.appendChild(this.doc.createTextNode(drawWidth));
		
		Element visibilityElement = this.doc.createElement("visibility");		
		visibilityElement.appendChild(this.doc.createTextNode(visibility));
		
		//create new text element
		
		Element newPVElement = this.doc.createElement("pv");
		
		newPVElement.setAttribute("name", pvName);
		newPVElement.setAttribute("directory_name", directoryName);
		newPVElement.appendChild(tANameElement);
		newPVElement.appendChild(rANameElement);
		newPVElement.appendChild(colorElement);
		newPVElement.appendChild(drawTypeElement);
		newPVElement.appendChild(drawWidthElement);
		newPVElement.appendChild(visibilityElement);
		
		return newPVElement;
	}
	
	private Element createFormulaElement(
			String formulaName, 
			String directoryName, 
			String term,
			FormulaParameter[] argAndAVENameEntries,
			String methodName,
			String nrValues,
			String timeAxisName,
			String rangeAxisName,
			String colorRGBValue,
			String drawType,
			String drawWidth,
			String visibility)
	{
		Element termElement = this.doc.createElement("term");
		termElement.appendChild(this.doc.createTextNode(term));
		
		Element[] argumentAVEElements = new Element[argAndAVENameEntries.length];
		int i=0;
		for(i=0; i<argAndAVENameEntries.length; i++)
		{
			argumentAVEElements[i] = this.doc.createElement("argument_ave");
			argumentAVEElements[i].setAttribute("variable", argAndAVENameEntries[i].getArg());
			argumentAVEElements[i].setAttribute("name", argAndAVENameEntries[i].getAVEName());
		}

		Element methodNameElement = this.doc.createElement("method_name");
		methodNameElement.appendChild(this.doc.createTextNode(methodName));
		
		Element nrValuesElement = this.doc.createElement("nr_values");
		nrValuesElement.appendChild(this.doc.createTextNode(nrValues));
		
		Element tANameElement = this.doc.createElement("time_axis_name");
		tANameElement.appendChild(this.doc.createTextNode(timeAxisName));
		
		Element rANameElement = this.doc.createElement("range_axis_name");
		rANameElement.appendChild(this.doc.createTextNode(rangeAxisName));
		
		Element colorElement = this.doc.createElement("color");		
		colorElement.appendChild(this.doc.createTextNode(colorRGBValue));
		
		Element drawTypeElement = this.doc.createElement("draw_type");		
		drawTypeElement.appendChild(this.doc.createTextNode(drawType));
		
		Element drawWidthElement = this.doc.createElement("draw_width");		
		drawWidthElement.appendChild(this.doc.createTextNode(drawWidth));
		
		Element visibilityElement = this.doc.createElement("visibility");		
		visibilityElement.appendChild(this.doc.createTextNode(visibility));
		
		Element newFormulaElement = this.doc.createElement("formula");
		
		newFormulaElement.setAttribute("name", formulaName);
		newFormulaElement.setAttribute("directory_name", directoryName);
		newFormulaElement.appendChild(termElement);
		for(i=0; i<argumentAVEElements.length; i++)
		{
			newFormulaElement.appendChild(argumentAVEElements[i]);
		}
		newFormulaElement.appendChild(methodNameElement);
		newFormulaElement.appendChild(nrValuesElement);
		newFormulaElement.appendChild(tANameElement);
		newFormulaElement.appendChild(rANameElement);
		newFormulaElement.appendChild(colorElement);
		newFormulaElement.appendChild(drawTypeElement);
		newFormulaElement.appendChild(drawWidthElement);
		newFormulaElement.appendChild(visibilityElement);
		
		return newFormulaElement;
	}
	
	
	private Element createTimeAxisElement(
									String timeAxisName, 
									String startTime, 
									String endTime,
									String location)
	{
		Element startElement = this.doc.createElement("start");
		startElement.appendChild(this.doc.createTextNode(startTime));
		
		Element endElement = this.doc.createElement("end");
		endElement.appendChild(this.doc.createTextNode(endTime));
		
		Element locationElement = this.doc.createElement("location");		
		locationElement.appendChild(this.doc.createTextNode(location));
		
		//create new text element
		
		Element newTimeAxisElement = this.doc.createElement("time_axis");
		
		newTimeAxisElement.setAttribute("name", timeAxisName);
		newTimeAxisElement.appendChild(startElement);
		newTimeAxisElement.appendChild(endElement);
		newTimeAxisElement.appendChild(locationElement);
		
		return newTimeAxisElement;
	}
	
	private Element createRangeAxisElement(
			String rangeAxisName, 
			String min, 
			String max,
			String type,
			String location)
	{
		Element minElement = this.doc.createElement("min");
		if(min != null)
			minElement.appendChild(this.doc.createTextNode(min));
		
		Element maxElement = this.doc.createElement("max");
		if(max != null)
			maxElement.appendChild(this.doc.createTextNode(max));
		
		Element typeElement = this.doc.createElement("type");		
		typeElement.appendChild(this.doc.createTextNode(type));
		
		Element locationElement = this.doc.createElement("location");		
		locationElement.appendChild(this.doc.createTextNode(location));
		
		//create new text element
		
		Element newRangeAxisElement = this.doc.createElement("range_axis");
		
		newRangeAxisElement.setAttribute("name", rangeAxisName);
		newRangeAxisElement.appendChild(minElement);
		newRangeAxisElement.appendChild(maxElement);
		newRangeAxisElement.appendChild(typeElement);
		newRangeAxisElement.appendChild(locationElement);
		
		return newRangeAxisElement;
	}
	
	private Color getColor(String colorAsString) throws Exception
	{
		int radix = 10;
		if(colorAsString.startsWith("#"))
		{
			radix = 16;
			colorAsString = colorAsString.substring(1);
		}
		return new Color(Integer.parseInt(colorAsString, radix));
	}
	
	private PVGraph getPVGraph(AVEntry ave) throws Exception
	{
		String directoryName = ave.getArchiveDirectory().getName();
		String tAName = null;
		String rAName = null;
		Color color = null;
		DrawType drawType = null;
		float drawWidth = -1f;
		boolean visibility = false;
		String tmp = null;
	
		tAName = getPVParameter(ave.getName(), directoryName, "time_axis_name");
		rAName = getPVParameter(ave.getName(), directoryName, "range_axis_name");
		if(rAName != null && rAName.trim().equals(""))
			rAName = null;
		
		color = getColor(getPVParameter(ave.getName(), directoryName, "color"));
		
		tmp = getPVParameter(ave.getName(), directoryName, "draw_type");
		drawType = DrawType.getDrawType(tmp);
		
		tmp = getPVParameter(ave.getName(), directoryName, "draw_width");
		drawWidth = Float.parseFloat(tmp);
		
		tmp = getPVParameter(ave.getName(), directoryName, "visibility");
		visibility = Boolean.valueOf(tmp).booleanValue();
		
		return new PVGraph(
				ave,
				tAName,
				rAName,
				color,
				drawType,
				drawWidth,
				visibility
			);
	}
	
	private boolean isFormula(String aveName)
	{
		NodeList formulaNodes = this.doc.getElementsByTagName("formula");
		int nlLength = formulaNodes.getLength();
		Element formulaEl = null;
		for(int i=0; i<nlLength; i++)
		{
			formulaEl = (Element) formulaNodes.item(i);
			if(formulaEl.getAttribute("name").equals(aveName))
				return true;
		}
		return false;
	}
	
	
	private FormulaGraph getFormulaGraph(AVEntry ave) throws Exception
	{
		String directoryName = ave.getArchiveDirectory().getName();
		String tAName = null;
		String rAName = null;
		Color color = null;
		DrawType drawType = null;
		float drawWidth = -1f;
		boolean visibility = false;
		String term = null;
		ArrayList arguments = new ArrayList();
		String methodName = null;
		int nrValues = -1;
		
		String tmp = null;
		
		term = getFormulaParameterExceptArgumentPVs(ave.getName(), directoryName, "term");
		
		Element el = getFormulaElement(ave.getName(), directoryName);
		
		NodeList argumentAVENodes = el.getElementsByTagName("argument_ave");
		int nlLength = argumentAVENodes.getLength();
		String argAVEName = null;
		for(int i=0; i<nlLength; i++)
		{
			el = (Element)argumentAVENodes.item(i);
			argAVEName = el.getAttribute("name");
					
			arguments.add(new FormulaParameter(el.getAttribute("variable"), 
					argAVEName, 
					isFormula(argAVEName)));
		}
		
		methodName = getFormulaParameterExceptArgumentPVs(ave.getName(), directoryName, "method_name");
		
		tmp =  getFormulaParameterExceptArgumentPVs(ave.getName(), directoryName, "nr_values");
		nrValues = Integer.parseInt(tmp);
		
		tAName = getFormulaParameterExceptArgumentPVs(ave.getName(), directoryName, "time_axis_name");
		rAName = getFormulaParameterExceptArgumentPVs(ave.getName(), directoryName, "range_axis_name");
		if(rAName != null && rAName.trim().equals(""))
			rAName = null;
		
		
		color = getColor(getFormulaParameterExceptArgumentPVs(ave.getName(), directoryName, "color"));
		
		tmp = getFormulaParameterExceptArgumentPVs(ave.getName(), directoryName, "draw_type");
		drawType = DrawType.getDrawType(tmp);
		
		tmp = getFormulaParameterExceptArgumentPVs(ave.getName(), directoryName, "draw_width");
		drawWidth = Float.parseFloat(tmp);
		
		tmp = getFormulaParameterExceptArgumentPVs(ave.getName(), directoryName, "visibility");
		visibility = Boolean.valueOf(tmp).booleanValue();
		
		return new FormulaGraph(
				ave,
				term,
				(FormulaParameter[]) arguments.toArray(new FormulaParameter[arguments.size()]),
				methodName,
				nrValues, 
				tAName,
				rAName,
				color,
				drawType,
				drawWidth,
				visibility
			);
	}
	
	private void addPV(
			String pvName,
			String directoryName,
			String timeAxisName,
			String rangeAxisName,
			String colorRGBValue,
			String drawType,
			String drawWidth,
			String visibility) throws Exception
	{
		if(pvName == null || pvName.trim().equals(""))
			throw new Exception("No empty names allowed!");
		try
		{
			if(getFormulaElement(pvName, directoryName) != null)
				throw new Exception();
		}
		catch(Exception e)
		{
			//do nothing
		}
		Element newPVElement = createPVElement(
				pvName, directoryName, timeAxisName, rangeAxisName, colorRGBValue,
				drawType, drawWidth, visibility
			);
		
		Element currentPVElementWithSameNameAndDir = null;
		try
		{
			currentPVElementWithSameNameAndDir = getPVElement(pvName,directoryName);
		}
		catch(Exception e)
		{	
			//do nothing
		}
		
		Node nodeToInsertNewPVElementBefore = null;
		Element docElement = this.doc.getDocumentElement();
		if(currentPVElementWithSameNameAndDir != null)
		{
			//remove current time axis element
			nodeToInsertNewPVElementBefore = currentPVElementWithSameNameAndDir.getNextSibling();
			docElement.removeChild(currentPVElementWithSameNameAndDir);
		}
		else
		{
			NodeList nl = this.doc.getElementsByTagName("pv");
			int nlLength = nl.getLength();
			if(nlLength > 0)
				nodeToInsertNewPVElementBefore = nl.item(nlLength - 1).getNextSibling();
			else
				nodeToInsertNewPVElementBefore = this.doc.getElementsByTagName("time_axis").item(0); 
		}
		//insert newTimeAxisElement into our document		
		docElement.insertBefore(newPVElement, nodeToInsertNewPVElementBefore);
	}
	
	private void addFormula(
			String formulaName,
			String directoryName,
			String term,
			FormulaParameter[] argAndAVENameEntries,
			String methodName,
			String nrValues,
			String timeAxisName,
			String rangeAxisName,
			String colorRGBValue,
			String drawType,
			String drawWidth,
			String visibility) throws Exception
	{
		if(formulaName == null || formulaName.trim().equals(""))
			throw new Exception("No empty names allowed!");
		Element el = null;
		try
		{
			el = getPVElement(formulaName, directoryName);
		}
		catch(Exception e)
		{
			if(el != null)
				throw new Exception("A PV with the name " + formulaName + " already exists");
		}
		Element newFormulaElement = createFormulaElement(
				formulaName, directoryName, term, argAndAVENameEntries, methodName, nrValues,
				timeAxisName, rangeAxisName, colorRGBValue,	drawType, drawWidth, visibility
			);
		
		Element currentFormulaElementWithSameNameAndDir = null;
		try
		{
			currentFormulaElementWithSameNameAndDir = getFormulaElement(formulaName,directoryName);
		}
		catch(Exception e)
		{	
			//do nothing
		}
		
		Node nodeToInsertNewFormulaElementBefore = null;
		Element docElement = this.doc.getDocumentElement();
		if(currentFormulaElementWithSameNameAndDir != null)
		{
			//remove current time axis element
			nodeToInsertNewFormulaElementBefore = currentFormulaElementWithSameNameAndDir.getNextSibling();
			docElement.removeChild(currentFormulaElementWithSameNameAndDir);
		}
		else
		{
			NodeList nl = this.doc.getElementsByTagName("pv");
			int nlLength = nl.getLength();
			if(nlLength > 0)
				nodeToInsertNewFormulaElementBefore = nl.item(nlLength - 1).getNextSibling();
			else
				nodeToInsertNewFormulaElementBefore = this.doc.getElementsByTagName("time_axis").item(0); 
		}
		//insert newTimeAxisElement into our document		
		docElement.insertBefore(newFormulaElement, nodeToInsertNewFormulaElementBefore);
	}
	
	//if the directory is no more available, it is changed INSIDE xml to the default
	private AVEntry createAVEntry(
	        final AVBase avBase, 
	        final ArchiveDirectory defaultDir, 
	        final Element aveElement,
	        String aveName, 
	        String dirName)
	{
	    ArchiveDirectory ad = 
	        avBase.getArchiveDirectoriesRepository().getArchiveDirectory(
				dirName.trim()
				);
	    AVEntry ave = null;
        try
        {
		    if(ad != null)
			{
		        ave = new AVEntry(aveName, ad);
			}   
			else
			{
	
		        aveElement.setAttribute("directory_name", defaultDir.getName());
		        ave = new AVEntry(
		            aveName,
		            defaultDir);
			    avBase.displayWarning(
			            "Archive Directory " + dirName + " could not be found. " +
			    		"Default directory " + defaultDir.getName() + " was assigned to AV entry " +
			    		aveName, 
			    		new Exception("Archive directory " + dirName + " not found"));	    
			}
        }
        catch(Exception e)
        {
            avBase.displayError("Unknown error creating AV entry", e);
        }
		return ave;
	}
	
	public XMLPlotModel(Document _doc)
	{
		this.doc = _doc;
	}
	
	public String getConnectionParameter()
	{
		Node n = this.doc.getElementsByTagName("connection_parameter").item(0).getFirstChild();
		if(n == null)
			return null;
		else
			return n.getNodeValue();
	}
	
	public void setConnectionParameter(String parameter)
	{
		if(parameter == null)
			parameter = "";
		Element connectionParameterElement = (Element) this.doc.getElementsByTagName("connection_parameter").item(0);
		Text textNode = (Text) connectionParameterElement.getFirstChild();
		if(textNode == null)
			connectionParameterElement.appendChild(this.doc.createTextNode(parameter));
		else
			textNode.setData(parameter);
	}
	
	
	
	public void addPV(PVGraph pvg) throws Exception
	{
		addPV(
				pvg.getAVEntry().getName(),
				pvg.getAVEntry().getArchiveDirectory().getName(),
				pvg.getTimeAxisLabel(),
				pvg.getRangeAxisLabel(), 
				Integer.toString(pvg.getColor().getRGB()),
				pvg.getDrawType().toString(),
				Float.toString(pvg.getDrawWidth()),
				Boolean.toString(pvg.isVisible())								
			);
		
	}
	
	public void addFormula(FormulaGraph fg) throws Exception
	{
		addFormula(
				fg.getAVEntry().getName(),
				fg.getAVEntry().getArchiveDirectory().getName(),
				fg.getTerm(),
				fg.getFormulaParameters(),
				fg.getRetrievalMethodName(),
				Integer.toString(fg.getRequestedNumberOfValues()),
				fg.getTimeAxisLabel(),
				fg.getRangeAxisLabel(), 
				Integer.toString(fg.getColor().getRGB()),
				fg.getDrawType().toString(),
				Float.toString(fg.getDrawWidth()),
				Boolean.toString(fg.isVisible())								
			);
		
	}
	
	public AVEntry[] getAVEntries(final AVBase avBase)
	{
	    ArchiveDirectoriesRepository adsRepository = avBase.getArchiveDirectoriesRepository();
		ArrayList result = new ArrayList();
		
		
		//default directory if none could be assigned
		final ArchiveDirectory defaultDir = 
		    adsRepository.getArchiveDirectory
		    (
		            adsRepository.getSortedArchiveDirectoryNames()[0]
		    );
		
		NodeList pvGraphNodes = this.doc.getElementsByTagName("pv");
		int nlLength = pvGraphNodes.getLength();
		Element el = null;
		int i=0;
		String aveName = null;
		String dirName = null;
		AVEntry ave = null;
		
		
		for(i=0; i<nlLength; i++)
		{
			el = (Element) pvGraphNodes.item(i);
			aveName = el.getAttribute("name");
			dirName = el.getAttribute("directory_name");
			result.add(
					createAVEntry(avBase, defaultDir, el, aveName, dirName)
					);
			
		}
		
		NodeList formulaGraphNodes = this.doc.getElementsByTagName("formula");
		nlLength = formulaGraphNodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			el = (Element) formulaGraphNodes.item(i);
			aveName = el.getAttribute("name");
			dirName = el.getAttribute("directory_name");
			result.add(
					createAVEntry(avBase, defaultDir, el, aveName, dirName)
					);
			
		}
		
		return (AVEntry[]) result.toArray(new AVEntry[result.size()]);	
	}
	
	public Graph getGraph(AVEntry ave) throws Exception
	{
		boolean isPV = true;
		Element el = null;
		try
		{
			el = getPVElement(ave.getName(), ave.getArchiveDirectory().getName());
			//it's a pv
		}
		catch(Exception e)
		{
			//no such pv, try with a formula
			el = getFormulaElement(ave.getName(), ave.getArchiveDirectory().getName());
			isPV = false;
		}
		
		if(isPV)
		{
			return getPVGraph(ave);
		}
		else
		{
			return getFormulaGraph(ave);
		}
	}
	
	public void addTimeAxis(
			String timeAxisName, 
			String startTime, 
			String endTime,
			String location) throws Exception
	{
		if(timeAxisName == null || timeAxisName.trim().equals(""))
			throw new Exception("No empty names allowed!");
		
		Element newTimeAxisElement = createTimeAxisElement(timeAxisName, startTime, endTime, location);
		
		Element currentTAElementWithSameName = null;
		try
		{
			currentTAElementWithSameName = getTimeAxisElement(timeAxisName);
		}
		catch(Exception e)
		{	
			//do nothing
		}
		
		Node nodeToInsertNewTAElementBefore = null;
		Element docElement = this.doc.getDocumentElement();
		if(currentTAElementWithSameName != null)
		{
			//remove current time axis element
			nodeToInsertNewTAElementBefore = currentTAElementWithSameName.getNextSibling();
			docElement.removeChild(currentTAElementWithSameName);
		}
		else
		{
			NodeList nl = this.doc.getElementsByTagName("time_axis");
			int nlLength = nl.getLength();
			if(nlLength > 0)
				nodeToInsertNewTAElementBefore = nl.item(nlLength - 1).getNextSibling();
			else
				nodeToInsertNewTAElementBefore = this.doc.getElementsByTagName("legend_configuration").item(0);
		}
		//insert newTimeAxisElement into our document		
		docElement.insertBefore(newTimeAxisElement, nodeToInsertNewTAElementBefore);
	}
	
	public String[] getTimeAxesNames()
	{
		NodeList nl = this.doc.getElementsByTagName("time_axis");
		int nlLength = nl.getLength();
		
		String[] result = new String[nlLength];
		Element el = null;
		for(int i=0; i<nlLength; i++)
		{
			el = (Element) nl.item(i);
			result[i] = el.getAttribute("name");
		}
		
		return result;
	}
	
	public TimeAxis getTimeAxis(String timeAxisName) throws Exception
	{
		String startTime = getTimeAxisParameter(timeAxisName, "start");
		String endTime = getTimeAxisParameter(timeAxisName, "end");
		TimeAxisLocation tALocation = null;
	
		String tmp = getTimeAxisParameter(timeAxisName, "location");
		if(tmp == null)
			tALocation = TimeAxisLocation.NOT_VISIBLE;
		else
			tALocation = TimeAxisLocation.getAxisLocation(tmp);
		
		return new TimeAxis(
				timeAxisName,
				startTime,
				endTime,
				tALocation
				);
	}
	
	public void addRangeAxis(
			String rangeAxisName, 
			String min, 
			String max,
			String type,
			String location) throws Exception
	{
		if(rangeAxisName == null || rangeAxisName.trim().equals(""))
			throw new Exception("No empty names allowed!");
		
		Element newRangeAxisElement = createRangeAxisElement(rangeAxisName, min, max, type, location);
		
		Element currentRAElementWithSameName = null;
		try
		{
			currentRAElementWithSameName = getRangeAxisElement(rangeAxisName);
		}
		catch(Exception e)
		{	
			//do nothing
		}
		
		Node nodeToInsertNewRAElementBefore = null;
		Element docElement = this.doc.getDocumentElement();
		if(currentRAElementWithSameName != null)
		{
			//remove current time axis element
			nodeToInsertNewRAElementBefore = currentRAElementWithSameName.getNextSibling();
			docElement.removeChild(currentRAElementWithSameName);
		}
		else
		{
			NodeList nl = this.doc.getElementsByTagName("range_axis");
			int nlLength = nl.getLength();
			if(nlLength > 0)
				nodeToInsertNewRAElementBefore = nl.item(nlLength - 1).getNextSibling();
			else
				nodeToInsertNewRAElementBefore = this.doc.getElementsByTagName("legend_configuration").item(0);
		}
		//insert newTimeAxisElement into our document		
		docElement.insertBefore(newRangeAxisElement, nodeToInsertNewRAElementBefore);
	}
	
	public String[] getRangeAxesNames()
	{
		NodeList nl = this.doc.getElementsByTagName("range_axis");
		int nlLength = nl.getLength();
		
		String[] result = new String[nlLength];
		Element el = null;
		for(int i=0; i<nlLength; i++)
		{
			el = (Element) nl.item(i);
			result[i] = el.getAttribute("name");
		}
		
		return result;
	}
	
	public RangeAxis getRangeAxis(String rangeAxisName) throws Exception
	{
		Double min = null;
		Double max = null;
		RangeAxisType rAType = null;
		RangeAxisLocation rALocation = null;
		
		String tmp = getRangeAxisParameter(rangeAxisName, "min");
		if(	tmp != null && 
			tmp.trim().equals("") == false
		)
			min = Double.valueOf(tmp);
		
		tmp = getRangeAxisParameter(rangeAxisName, "max");
		if(	tmp != null && 
			tmp.trim().equals("") == false
		)
			max = Double.valueOf(tmp);
		
		tmp = getRangeAxisParameter(rangeAxisName, "type");
		if(tmp == null)
			rAType = RangeAxisType.NORMAL;
		else
			rAType = RangeAxisType.getRangeAxisType(tmp);
		
		tmp = getRangeAxisParameter(rangeAxisName, "location");
		if(tmp == null)
			rALocation = RangeAxisLocation.NOT_VISIBLE;
		else
			rALocation = RangeAxisLocation.getAxisLocation(tmp);
		
		return new RangeAxis(
				rangeAxisName,
				min,
				max, 
				rAType,
				rALocation
				);
	}

	public String getPlotTitle()
	{
		Node n =  this.doc.getElementsByTagName("plot_title").item(0).getFirstChild();
		if(	n == null)
			return "";
		else
			return n.getNodeValue();
	}
	
	public void setPlotTitle(String plotTitle)
	{
		if(plotTitle == null)
			plotTitle = "";
		Node plotTitleNode = this.doc.getElementsByTagName("plot_title").item(0);
		Text textNode =  (Text) plotTitleNode.getFirstChild();
		if(textNode == null)
		{
			plotTitleNode.appendChild(this.doc.createTextNode(plotTitle));	
		}
		else
			textNode.setData(plotTitle);		
	}
	
	public LegendInfo getLegendInfo() throws Exception
	{
		LegendInfo li = new LegendInfo();
		try
		{
			Element legendConfigElement = (Element) this.doc.getElementsByTagName("legend_configuration").item(0);
			li.setShowAVEName(Boolean.valueOf(legendConfigElement.getAttribute("show_ave_name")).booleanValue());
			li.setShowArchiveName(Boolean.valueOf(legendConfigElement.getAttribute("show_directory_name")).booleanValue());
			li.setShowRange(Boolean.valueOf(legendConfigElement.getAttribute("show_range")).booleanValue());
			li.setShowUnits(Boolean.valueOf(legendConfigElement.getAttribute("show_units")).booleanValue());
		}
		catch(Exception e)
		{
			//do nothing
		}
		return li;
	}
	
	public void setLegendInfo(LegendInfo legendInfo)
	{
		Element legendConfigElement = null;
		NodeList nl = this.doc.getElementsByTagName("legend_configuration");
		if(nl.getLength() == 0)
		{
			legendConfigElement = this.doc.createElement("legend_configuration");
			this.doc.getDocumentElement().insertBefore(
					legendConfigElement, 
					this.doc.getElementsByTagName("plot_title").item(0));
		}
		else
			legendConfigElement = (Element) nl.item(0);
		legendConfigElement.setAttribute("show_ave_name", Boolean.toString(legendInfo.getShowAVEName()));
		legendConfigElement.setAttribute("show_directory_name", Boolean.toString(legendInfo.getShowArchiveName()));
		legendConfigElement.setAttribute("show_range", Boolean.toString(legendInfo.getShowRange()));
		legendConfigElement.setAttribute("show_units", Boolean.toString(legendInfo.getShowUnits()));
	}	
}
