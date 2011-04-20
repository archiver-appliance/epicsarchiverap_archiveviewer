/*
 * Created on Dec 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.util.formula;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVMath{

	public static double min(double[] args)
	{
		if(args == null || args.length == 0)
			return Double.NaN;
		double result = args[0];
		//need to check all arguments for NaN and infinity
		for(int i=0; i<args.length; i++)
		{
			if(Double.isNaN(args[i]) || Double.isInfinite(args[i]))
				return Double.NaN;
			if(result > args[i])
				result = args[i];
		}
		return result;
	}
	
	public static double max(double[] args)
	{
		if(args == null || args.length == 0)
			return Double.NaN;
		double result = args[0];
		//need to check all arguments for NaN and infinity
		for(int i=0; i<args.length; i++)
		{
			if(Double.isNaN(args[i]) || Double.isInfinite(args[i]))
				return Double.NaN;
			if(result < args[i])
				result = args[i];
		}
		return result;
	}
	
	public static double amean(double[] args)
	{
		if(args == null || args.length == 0)
			return Double.NaN;
		double sum = 0;
		//need to check all arguments for NaN and infinity
		for(int i=0; i<args.length; i++)
		{
			if(Double.isNaN(args[i]) || Double.isInfinite(args[i]))
				return Double.NaN;
			sum += args[i];
		}
		return sum/args.length;
	}
}
