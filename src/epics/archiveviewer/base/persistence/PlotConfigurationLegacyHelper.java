/*
 * Created on Jan 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.persistence;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.base.AVBaseConstants;

/**
 * @author serge
  */
public class PlotConfigurationLegacyHelper {
	
	private static final String NS_URI = "";
	
	private static void renameElements(DocumentImpl document, String oldName, String newName)
	{
		NodeList nodes = document.getElementsByTagName(oldName);
		int nlLength = nodes.getLength();
		for(int i=0; i<nlLength; i++)
		{
			document.renameNode(nodes.item(i), NS_URI, newName);
		}
	}
	
	private static void renameAttributes(	DocumentImpl document, 
											NodeList elements,
										 	String oldName, 
											String newName)
	{
		Element el = null;
		Attr attributeNode = null;
		int elLength = elements.getLength();
		for(int i=0; i<elLength; i++)
		{
			el = (Element) elements.item(i);
			attributeNode = el.getAttributeNode(oldName);
			if(attributeNode != null)
				document.renameNode(attributeNode, NS_URI, newName);	
		}
	}
	
	private static Element getLastChildElementWithTagName(Node refNode, String elementTagName)
	{
		Element match = null;
		NodeList nodes = refNode.getChildNodes();
		int nlLength = nodes.getLength();
		for(int i=0; i<nlLength; i++)
		{
			try
			{
				Element el = (Element) nodes.item(i);
				if(el.getTagName().equals(elementTagName))
					match = el;					
			}
			catch(Exception e)
			{
				//do nothing
			}
		}
		return match;
	}
	
	private static String[] getTextValuesOfElements(DocumentImpl document, String elementName)
	{
		NodeList nodes = document.getElementsByTagName(elementName);
		String[] result = new String[nodes.getLength()];
		
		Element el = null;
		
		int nlLength = nodes.getLength();
		for(int i=0; i<nlLength; i++)
		{
			el = (Element) nodes.item(i);
			result[i] = el.getFirstChild().getNodeValue();
		}
		
		return result;
	}
	
	private static void replaceValues(DocumentImpl document)
	{
		NodeList nl = document.getElementsByTagName("draw_type");
		int nlLength = nl.getLength();
		Text textNode = null;
		for(int i=0; i<nlLength; i++)
		{
			textNode = (Text) nl.item(i).getFirstChild();
			try
			{
				Integer.parseInt(textNode.getData());
				textNode.setData(AVBaseConstants.DEFAULT_DRAW_TYPE.toString());
			}
			catch(Exception e)
			{
				//do nothing
			}
		}
		
		nl = document.getElementsByTagName("type");
		nlLength = nl.getLength();
		for(int i=0; i<nlLength; i++)
		{
			textNode = (Text) nl.item(i).getFirstChild();
			try
			{
				Integer.parseInt(textNode.getData());
				textNode.setData(AVBaseConstants.DEFAULT_RANGE_AXIS_TYPE.toString());
			}
			catch(Exception e)
			{
				//do nothing
			}
		}
		
		nl = document.getElementsByTagName("time_axis_name");
		nlLength = nl.getLength();
		for(int i=0; i<nlLength; i++)
		{
			textNode = (Text) nl.item(i).getFirstChild();
			if(textNode.getData().equals("Main time axis"))
				textNode.setData("Main Time Axis");
		}
		
		nl = document.getElementsByTagName("time_axis");
		nlLength = nl.getLength();
		Element el = null;
		for(int i=0; i<nlLength; i++)
		{
			el = (Element) nl.item(i);
			if(el.getAttribute("name").equals("Main time axis"))
				el.setAttribute("name", "Main Time Axis");
		}
	}
	
	private static void replaceParents(DocumentImpl document)
	{
		int i = 0;
		int j = 0;
		NodeList nodes = null;
		Node n = null;
		Node nextSibling = null;
		Node newParent = null;
		
		
		nodes = document.getElementsByTagName("draw");
		int nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			n = nodes.item(i);
			newParent = n.getParentNode();
			nextSibling = n.getNextSibling();
			newParent.insertBefore(getLastChildElementWithTagName(n, "draw_type"), nextSibling);
		}
	}

	private static void handlePossiblyMissingElements(DocumentImpl document)
	{
		int i = 0;
		NodeList nodes = null;
		Element el = null;
		Node n = null;
		String[] s = null;
		int nlLength = -1;
		
		if(document.getElementsByTagName("connection_parameter").getLength() == 0)
		{
			el = document.createElement("connection_parameter");
			//insert as the first child of "document"
			document.getDocumentElement().insertBefore(el, document.getDocumentElement().getFirstChild());
		}
		
		if(document.getElementsByTagName("legend_configuration").getLength() == 0)
		{
			el = document.createElement("legend_configuration");
			//insert as the last child of "document"
			document.getDocumentElement().insertBefore(el, null);
		}
		
		if(document.getElementsByTagName("nr_values").getLength() == 0)
		{
			nodes = document.getElementsByTagName("formula");
			nlLength = nodes.getLength();
			for(i=0; i<nlLength; i++)
			{
				n = nodes.item(i);
				el = document.createElement("nr_values");
				el.appendChild(document.createTextNode(Integer.toString(AVBaseConstants.DEFAULT_NR_VALUES)));
				//insert as a child of <formula_pv> after <method_name>
				n.insertBefore(el, getLastChildElementWithTagName(n, "method_name").getNextSibling());
			}
		}
		
		if(document.getElementsByTagName("plot_title").getLength() == 0)
		{
			el = document.createElement("plot_title");
			//insert as the last child of "document"
			document.getDocumentElement().insertBefore(el, null);
		}
		
		if(document.getElementsByTagName("draw_width").getLength() == 0)
		{
			nodes = document.getElementsByTagName("pv");
			nlLength = nodes.getLength();
			for(i=0; i<nlLength; i++)
			{
				el = document.createElement("draw_width");
				el.appendChild(document.createTextNode(Float.toString(AVBaseConstants.DEFAULT_DRAW_WIDTH)));
				//insert as last child of <pv>
				nodes.item(i).insertBefore(el, null);
			}
			nodes = document.getElementsByTagName("formula_pv");
			nlLength = nodes.getLength();
			for(i=0; i<nlLength; i++)
			{
				el = document.createElement("draw_width");
				el.appendChild(document.createTextNode(Float.toString(AVBaseConstants.DEFAULT_DRAW_WIDTH)));
				//insert as last child of <formula_pv>
				nodes.item(i).insertBefore(el, null);
			}
		}
		
		if(document.getElementsByTagName("visibility").getLength() == 0)
		{
			nodes = document.getElementsByTagName("pv");
			nlLength = nodes.getLength();
			for(i=0; i<nlLength; i++)
			{
				el = document.createElement("visibility");
				el.appendChild(document.createTextNode("true"));
				//insert as last child of <pv>
				nodes.item(i).insertBefore(el, null);
			}
			nodes = document.getElementsByTagName("formula_pv");
			nlLength = nodes.getLength();
			for(i=0; i<nlLength; i++)
			{
				el = document.createElement("visibility");
				el.appendChild(document.createTextNode(Integer.toString(AVBaseConstants.DEFAULT_NR_VALUES)));
				//insert as last child of <formula_pv>
				nodes.item(i).insertBefore(el, null);
			}
		}
		
		//main vs. sec time axes legacy
		if(document.getElementsByTagName("time_axis").getLength() == 0)
		{
			el = document.createElement("time_axis");
			
			Element timeAxisNameEl = document.createElement("time_axis_name");
			timeAxisNameEl.appendChild(document.createTextNode(AVBaseConstants.DEFAULT_TIME_AXIS_NAME));
			
			Element timeAxisStartEl = document.createElement("start");
			s = getTextValuesOfElements(document, "plot_start");
			if(s.length > 0)
				timeAxisStartEl.appendChild(document.createTextNode(s[0]));
			
			Element timeAxisEndEl = document.createElement("end");
			s = getTextValuesOfElements(document, "plot_end");
			if(s.length > 0)
				timeAxisEndEl.appendChild(document.createTextNode(s[0]));
			
			Element timeAxisLocationEl = document.createElement("location");
			timeAxisLocationEl.appendChild(document.createTextNode(
					AVBaseConstants.DEFAULT_TIME_AXIS_LOCATION.toString()));
			
			el.appendChild(timeAxisNameEl);
			el.appendChild(timeAxisStartEl);
			el.appendChild(timeAxisEndEl);
			el.appendChild(timeAxisLocationEl);
			
			//insert as a child of "document" before the first <range_axis> element
			document.getDocumentElement().insertBefore(el, document.getElementsByTagName("range_axis").item(0));
		}
		
		if(document.getElementsByTagName("location").getLength() == 0)
		{
			nodes = document.getElementsByTagName("time_axis");
			nlLength = nodes.getLength();
			for(i=0; i<nlLength; i++)
			{
				el = document.createElement("location");
				el.appendChild(document.createTextNode(
						AVBaseConstants.DEFAULT_TIME_AXIS_LOCATION.toString()));
				//insert as last child of <time_axis>
				nodes.item(i).insertBefore(el, null);
			}
			nodes = document.getElementsByTagName("range_axis");
			nlLength = nodes.getLength();
			for(i=0; i<nlLength; i++)
			{
				el = document.createElement("location");
				el.appendChild(document.createTextNode(
						AVBaseConstants.DEFAULT_RANGE_AXIS_LOCATION.toString()));
				//insert as last child of <y_axis>
				nodes.item(i).insertBefore(el, null);
			}
		}
	}
	
	private static void handlePossiblyMissingAttributes(DocumentImpl document)
	{
		int i = 0;
		NodeList nodes = null;
		Element el = null;
		Node n = null;
		String s = null;
		Attr attr = null;
		int nlLength = -1;
		
		nodes = document.getElementsByTagName("formula");
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			el = (Element) nodes.item(i);
			attr = el.getAttributeNode("directory_name");
			if(attr == null)
			{
				n = getLastChildElementWithTagName(el, "formula_archive");
				if(n != null)
					s = n.getFirstChild().getNodeValue();
				else
					s = getTextValuesOfElements(document, "archive")[0];
				el.setAttribute("directory_name", s);
			}
		}
		
		el = (Element) document.getElementsByTagName("legend_configuration").item(0);
		
		attr = el.getAttributeNode("show_ave_name");
		if(attr == null)
			el.setAttribute("show_ave_name", "true");
		
		attr = el.getAttributeNode("show_directory_name");
		if(attr == null)
			el.setAttribute("show_directory_name", "false");
		
		attr = el.getAttributeNode("show_range");
		if(attr == null)
			el.setAttribute("show_range", "true");
		
		attr = el.getAttributeNode("show_units");
		if(attr == null)
			el.setAttribute("show_units", "true");
		
		nodes = document.getElementsByTagName("pv");
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			el = (Element) nodes.item(i);
			attr = el.getAttributeNode("directory_name");
			if(attr == null)
			{
				n = getLastChildElementWithTagName(el, "pv_archive");
				if(n != null)
					s = n.getFirstChild().getNodeValue();
				else
					s = getTextValuesOfElements(document, "archive")[0];
				el.setAttribute("directory_name", s);
			}
		}
		
		nodes = document.getElementsByTagName("time_axis");
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			el = (Element) nodes.item(i);
			attr = el.getAttributeNode("name");
			if(attr == null)
			{
				n = getLastChildElementWithTagName(el, "time_axis_name");
				s = n.getFirstChild().getNodeValue();
				el.setAttribute("name", s);
			}
		}
		
		nodes = document.getElementsByTagName("range_axis");
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			el = (Element) nodes.item(i);
			attr = el.getAttributeNode("name");
			if(attr == null)
			{
				n = getLastChildElementWithTagName(el, "range_axis_name");
				s = n.getFirstChild().getNodeValue();
				el.setAttribute("name", s);
			}
		}
	}
	
	private static void removeUnnecessaryElements(DocumentImpl document)
	{
		int i = 0;
		NodeList nodes = null;
		Node n = null;
		Element el = null;
		int nlLength = -1;

		nodes = document.getElementsByTagName("archive");
		if(nodes.getLength() > 0)
			document.getDocumentElement().removeChild(nodes.item(0));
	
		nodes = document.getElementsByTagName("formula");
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			n = nodes.item(i);
			el = getLastChildElementWithTagName(n, "formula_archive");
			if(el != null)
				n.removeChild(el);
			
			el = getLastChildElementWithTagName(n, "draw");
			if(el != null)
				n.removeChild(el);
		}
		
		nodes = document.getElementsByTagName("plot_start");
		if(nodes.getLength() > 0)
			document.getDocumentElement().removeChild(nodes.item(0));

		nodes = document.getElementsByTagName("plot_method");
		if(nodes.getLength() > 0)
			document.getDocumentElement().removeChild(nodes.item(0));
		
		nodes = document.getElementsByTagName("plot_end");
		if(nodes.getLength() > 0)
			document.getDocumentElement().removeChild(nodes.item(0));
		
		nodes = document.getElementsByTagName("pv");
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			n = nodes.item(i);
			
			el = getLastChildElementWithTagName(n, "draw");
			if(el != null)
				n.removeChild(el);
			
			el = getLastChildElementWithTagName(n, "end");
			if(el != null)
				n.removeChild(el);
			
			el = getLastChildElementWithTagName(n, "start");
			if(el != null)
				n.removeChild(el);
			
			el = getLastChildElementWithTagName(n, "pv_archive");
			if(el != null)
				n.removeChild(el);
		}
		
		nodes = document.getElementsByTagName("time_axis");
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			n = nodes.item(i);
			el = getLastChildElementWithTagName(n, "time_axis_name");
			if(el != null)
				n.removeChild(el);
		}
		
		nodes = document.getElementsByTagName("range_axis");
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			n = nodes.item(i);
			el = getLastChildElementWithTagName(n, "range_axis_name");
			if(el != null)
				n.removeChild(el);
		}
	}
	
	private static void removeUnnecessaryAttributes(DocumentImpl document)
	{
		int i = 0;
		NodeList nodes = null;
		Element el = null;
		int nlLength = -1;
		
		nodes = document.getElementsByTagName("color");
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			el = (Element) nodes.item(i);
			el.removeAttribute("type");
		}
		
		nodes = document.getElementsByTagName("draw_type");
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			el = (Element) nodes.item(i);
			el.removeAttribute("type");
		}
		
		nodes = document.getElementsByTagName("draw_width");
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			el = (Element) nodes.item(i);
			el.removeAttribute("type");
		}
		
		el = (Element) document.getElementsByTagName("legend_configuration").item(0);
		el.removeAttribute("archive");
		el.removeAttribute("graph");
		el.removeAttribute("range");
		el.removeAttribute("units");
	}
	
	/**
	 * This method transforms an older configuration document into the 
	 * document currently recognized by ArchiveViewer. Track of the changes is shown below.
	 * 
	 * Old Element Tags		New Element Tags
	 * ---------------------------------
	 * axis_type			type
	 * configuration		AVConfiguration
	 * formula_pv			argument_ave
	 * formula_term			term
	 * legend				legend_configuration
	 * method				method_name
	 * url					connection_parameter
	 * x_axis				time_axis
	 * x_axis_name			time_axis_name
	 * y_axis				range_axis
	 * y_axis_name			range_axis_name
	 * 
	 * Old Attribute Names	New Attribute Names	(lc := legend_configuration)
	 * -----------------------------------
	 * formula.archive_name	formula.directory_name
	 * lc.graph				lc.show_ae_name
	 * lc.archive			lc.show_archive_name
	 * lc.range				lc.show_range
	 * lc.units				lc.show_units
	 * pv.archive_name		pv.directory_name
	 * 
	 * ------------------
	 * after renaming =>
	 * ------------------
	 * 
	 * Old values						New Values
	 * ---------------------------------------------
	 * pv/formula.draw_type (0,1,2)		DEFAULT_DRAW_TYPE.toString()
	 * range_axis.type					DEFAULT_RANGE_AXIS_TYPE.toString()
	 * time_axis_name Main time axis	Main Time Axis
	 * time_axis.name Main time axis	Main Time Axis
	 * 
	 * Old Parent					New Parent
	 * ---------------------------------------
	 * formula->draw->draw_type		formula->draw->draw_type	
	 * pv->draw->draw_type 			pv->draw_type
	 * 
	 * Possibly Missing Elements		Default Values
	 * ----------------------------------------------
	 * connection_parameter				empty
	 * draw_width						DrawStyle.DEFAULT_DRAW_WIDTH;
	 * legend_configuration				empty
	 * nr_values						AVConstants.DEFAULT_NR_VALUES
	 * plot_title						empty
	 * range_axis->location				RangeAxis.NOT_VISIBLE
	 * time_axis						time_axis->time_axis_name = AVConstants.MAIN_TIME_AXIS_NAME
	 * 									time_axis->start = plot_start
	 * 									time_axis->end = plot_end 
	 * 									time_axis->location = TimeAxis.LOCATION_BOTTOM
	 * time_axis->location				TimeAxis.NOT_VISIBLE
	 * visibility						true
	 * 
	 * Possibly Missing Attributes		Default Values
	 * ----------------------------------------------
	 * formula.directory_name 			formula_archive, archive
	 * lc.show_ae_name					false
	 * lc.show_archive_name				false
	 * lc.show_range					true
	 * lc.show_units					true
	 * pv.directory_name				pv_archive, archive
	 * range_axis.name					range_axis->range_axis_name	
	 * time_axis.name					time_axis->time_axis_name
	 * 
	 * Unnecessary Elements
	 * -------------------
	 * AVConstants.AV_DOCUMENT_NAME->archive
	 * formula->draw
	 * formula->formula_archive
	 * AVConstants.AV_DOCUMENT_NAME->plot_end
	 * AVConstants.AV_DOCUMENT_NAME->plot_method
	 * AVConstants.AV_DOCUMENT_NAME->plot_start
	 * pv->draw
	 * pv->end
	 * pv->pv_archive
	 * pv->start
	 * range_axis->range_axis_name
	 * time_axis->time_axis_name
	 * 
	 * Unnecessary Attributes
	 * ---------------------
	 * color.type
	 * draw_type.type
	 * draw_width.type
	 * lc.archive
	 * lc.graph
	 * lc.range
	 * lc.units
	 * 
	 * Other changes
	 * -------------
	 * 1. 
	 * 		a. Remove all y axes elements whose y_axis.name values start with existing pv/formula names
	 * 		b. If pv->y_axis_name or formula->y_axis_name do not match any remaining y axes names, make the elements
	 * empty (=> meaning, those pvs/formulas are to be plotted normalized)
	 * 
	 * 2.
	 * 		If x_axis->visibility elements exist, replace all x_axis->visibility->true elements
	 *		with x_axis->location->bottom and x_axis->visibility->false elements with
	 *		x_axis->location
	 */
	public static Document translateToCurrentDTD(DocumentImpl document)
	{
		NodeList nodes = null; 
		Element el = null;
		String s = null;
		int i=0;
		int j=0;
		int nlLength = -1;
		
		renameElements(document, "axis_type", "type");
		renameElements(document, "configuration", "AVConfiguration");
		renameElements(document, "formula_pv", "argument_ave");
		renameElements(document, "formula_term", "term");
		renameElements(document, "legend", "legend_configuration");
		renameElements(document, "method", "method_name");
		renameElements(document, "url", "connection_parameter");
		renameElements(document, "x_axis", "time_axis");
		renameElements(document, "x_axis_name", "time_axis_name");
		renameElements(document, "y_axis", "range_axis");
		renameElements(document, "y_axis_name", "range_axis_name");
		
		nodes = document.getElementsByTagName("legend_configuration");
		
		//if the element is there
		if(nodes != null && nodes.getLength() > 0)
		{
			renameAttributes(document, nodes, "graph", "show_ave_name");
			renameAttributes(document, nodes, "archive", "show_directory_name");
			renameAttributes(document, nodes, "range", "show_range");
			renameAttributes(document, nodes, "units", "show_units");
		}
		renameAttributes(document, document.getElementsByTagName("pv"), "archive_name", "directory_name");
		renameAttributes(document, document.getElementsByTagName("formula"), "archive_name", "directory_name");
		
		replaceValues(document);
		
		replaceParents(document);
			
		handlePossiblyMissingElements(document);
		
		handlePossiblyMissingAttributes(document);
		
		removeUnnecessaryElements(document);
		
		removeUnnecessaryAttributes(document);
		
		//remove all y axes elements whose range_axis.name values start with existing pv/formula names
		
		ArrayList pvAndFormulaNames = new ArrayList();
		
		nodes = document.getElementsByTagName("pv");
		NodeList pvNodes = nodes;
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			el = (Element) nodes.item(i);
			pvAndFormulaNames.add(el.getAttribute("name"));
		}
		
		nodes = document.getElementsByTagName("formula");
		NodeList formulaNodes = nodes;
		nlLength = nodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			el = (Element) nodes.item(i);
			pvAndFormulaNames.add(el.getAttribute("name"));
		}
		
		nodes = document.getElementsByTagName("range_axis");
		
		HashSet nonOwnYAxisNames = new HashSet();
		
		//because we are removing nodes from the list, too, we
		//need to recalculate its length every time anew
		for(i=0; i<nodes.getLength(); i++)
		{
			el = (Element) nodes.item(i);
			s = el.getAttribute("name");
			//add first => possibly remove later 
			nonOwnYAxisNames.add(s);
			for(j=0; j<pvAndFormulaNames.size(); j++)
			{
				if(s.startsWith(pvAndFormulaNames.get(j).toString()))
				{
					document.getDocumentElement().removeChild(el);
					nonOwnYAxisNames.remove(s);
					//because the node is removed from the nodeList, too
					i--;
					break;
				}
			}
		}
		
		//if pv->range_axis_name or formula->range_axis_name do not match any remaining y axes names, make the elements
		//empty (=> meaning, those pvs/formulas are to be plotted normalized)
		Node rangeAxisNameTextNode = null;
		
		nlLength = pvNodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			el = getLastChildElementWithTagName(pvNodes.item(i), "range_axis_name");
			rangeAxisNameTextNode = el.getFirstChild();
			if(	rangeAxisNameTextNode != null &&
				nonOwnYAxisNames.contains(rangeAxisNameTextNode.getNodeValue()) == false)
			{
				el.removeChild(rangeAxisNameTextNode);
			}
		}

		nlLength = formulaNodes.getLength();
		for(i=0; i<nlLength; i++)
		{
			el = getLastChildElementWithTagName(formulaNodes.item(i), "range_axis_name");
			rangeAxisNameTextNode = el.getFirstChild();
			if(	rangeAxisNameTextNode != null &&
				nonOwnYAxisNames.contains(rangeAxisNameTextNode.getNodeValue()) == false)
			{
				el.removeChild(rangeAxisNameTextNode);
			}
		}
		
		//if time_axis->visibility element existed, replace all time_axis->visibility->true elements
		//with time_axis->location->bottom and time_axis->visibility->false elements with
		//time_axis->location
		nodes = document.getElementsByTagName("visibility");
		nlLength = nodes.getLength();
		Text txtNode = null;
		for(i=0; i<nlLength; i++)
		{
			el = (Element) nodes.item(i);
			if(el.getParentNode().getNodeName().equals("time_axis"))
			{
				txtNode = (Text)el.getFirstChild();
				s = txtNode.getNodeValue();
				if(s.equals("true"))
					txtNode.setData(AVBaseConstants.DEFAULT_TIME_AXIS_LOCATION.toString());
				else
					txtNode.setData("");
				document.renameNode(el, NS_URI, "location");
			}				
		}
		return document;		
		
	}
}
