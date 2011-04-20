package epics.archiveviewer.base.util.formula;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class contains methods to parse and calculate formulas. The current
 * handling requires a new instance of this class for every <CODE>FormulaGraph
 * </CODE>
 * 
 * @see epics.archiveviewer.base.data.FormulaGraph#isEqualInContent(Object)
 * @author Sergei chevtsov
 */
public class FormulaCalculator
{
	/** the parsed formula */
	private Node formula;

	/** maps PV names to variable indices in the parameter arrays */
	private HashMap varIndicesndDataObjects;

	/** the parameter array */
	private double[] params;

	/**
	 * Creates a new instance of <CODE>FormulCalculator</CODE>. Needs all
	 * these arguments because we wanted to keep the maximum amount of <CODE>
	 * Formulagraph</CODE> methods "protected"
	 * 
	 * @param fg
	 *            the formula graph for which this <CODE>ForumaCalculator
	 *            </CODE> is created
	 * @param term
	 *            the formula/term which is to parse
	 * @param varsAndPVNames
	 *            a <CODE>HashMap</CODE> of PV names and variables
	 * @throws IllegalArgumentException
	 *             if the formulaString can not be parsed correctly
	 */
	public FormulaCalculator(String term, Map varsAndObjectsTheyStandFor)
			throws IllegalArgumentException
	{
		this.varIndicesndDataObjects = new HashMap();

		Iterator varIt = varsAndObjectsTheyStandFor.keySet().iterator();
		int i = 0;

		//will be stored inside the formula Node
		HashMap varsAndTheirIndices = new HashMap();
		Object currentVar = null;

		while (varIt.hasNext())
		{
			currentVar = varIt.next();
			varsAndTheirIndices.put(currentVar, new Integer(i));
			this.varIndicesndDataObjects.put(new Integer(i), varsAndObjectsTheyStandFor.get(currentVar));
			i++;
		}

		try
		{
			this.formula = Parser.parse(new Class[]{Math.class, AVMath.class}, term, varsAndTheirIndices);

			if (this.formula == null)
			{
				throw new NullPointerException();
			}
		}
		catch (Throwable t)
		{
			throw new IllegalArgumentException("The term " + term
					+ " appears to be wrong; " + t.toString());
		}

		params = new double[varsAndObjectsTheyStandFor.size()];
	}

	/**
	 * Returns the result of the formula for which this <CODE>FormulaCalculator
	 * </CODE> was created, using the PV values specified in the <CODE>HashMap
	 * </CODE>. May return NaN!
	 * 
	 * @see #pvIndices
	 * @param pvsAndTheirVals
	 *            maps pv names to the current usable values
	 * @return the result of the formula for which this <CODE>FormulaCalculator
	 *         </CODE> was created, using the PV values specified in the <CODE>
	 *         HashMap</CODE>
	 */
	public Double calculate(Map objectsAndTheirVals)
	{
		try
		{
			Iterator varIndexIterator = this.varIndicesndDataObjects.keySet().iterator();
			Integer index = null;
			Object dataObject = null;

			while (varIndexIterator.hasNext())
			{
				index = (Integer) varIndexIterator.next();
				dataObject = this.varIndicesndDataObjects.get(index);
				params[index.intValue()] = ((Number) objectsAndTheirVals.get(dataObject))
						.doubleValue();
			}

			return new Double(formula.eval(params));
		}
		catch (Exception e)
		{
			return null;
		}
	}
}