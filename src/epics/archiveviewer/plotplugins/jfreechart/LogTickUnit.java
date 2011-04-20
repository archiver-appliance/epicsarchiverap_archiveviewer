/*
 * Created on Aug 24, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.data.Range;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LogTickUnit extends NumberTickUnit {

	private final NumberFormat NF;
	
	public LogTickUnit(double axisRange)
	{
		super(axisRange/5);
		//format string is going to be (0)(.)?(#)*E0
		StringBuffer formatSB = new StringBuffer();
		formatSB.append('0');
		if(axisRange < 10)
		{
			formatSB.append('.');
			while(axisRange < 10)
			{
				axisRange *= 10;
				formatSB.append('#');
			}
		}
				
		NF = new DecimalFormat(formatSB.toString() + "E0");		
	}
	
	/* (non-Javadoc)
	 * @see org.jfree.chart.axis.TickUnit#valueToString(double)
	 */
	public String valueToString(double arg0) {
	    double transformedArg = Math.exp(arg0);
		return this.NF.format(transformedArg);
	}
		
	/* (non-Javadoc)
	 * @see org.jfree.chart.axis.TickUnit#getSize()
	 */
	public double getSize() {
		// TODO Auto-generated method stub
		return super.getSize();
	}
}
