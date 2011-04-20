/*
 * Created on Feb 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.fundamental;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.util.AVBaseUtilities;
import gov.sns.xal.model.scenario.NewAndImprovedScenarioGenerator;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Formula
{	
	private final AVEntry avEntry;
	private String term;
	private FormulaParameter[] parameters;
	
	private final transient HashMap aveNamesAndArguments;
	
	public Formula(AVEntry ave, String _term, FormulaParameter[] params, boolean createMetaData)
	{
		this.avEntry = ave;
		this.aveNamesAndArguments = new HashMap();
		
		setFormulaParametersAndTerm(params, _term, createMetaData);
	}
	
	public AVEntry getAVEntry() {
		return this.avEntry;
	}
	
	public String getTerm() {
		return this.term;
	}
	
	public FormulaParameter[] getFormulaParameters() {
		return this.parameters;
	}
	
	public String getAVENameForArgument(String argName)
	{
		return ((FormulaParameter) this.aveNamesAndArguments.get(argName)).getAVEName();
	}
	
	public void setFormulaParametersAndTerm(FormulaParameter[] params, String _term, boolean updateMetaData)
	{
		this.term = _term;
		this.parameters = params;
		
		this.aveNamesAndArguments.clear();

		int i=0;
		for(i=0; i<this.parameters.length; i++)
		{
			this.aveNamesAndArguments.put(this.parameters[i].getArg(), this.parameters[i]);
		}
		
		if(updateMetaData)
		{
			Map metaData = new LinkedHashMap();
			for(i=0; i<this.parameters.length; i++)
			{
				metaData.put(this.parameters[i].getArg(), this.parameters[i].getAVEName());
			}
		
			metaData.put("term", this.term);
			metaData.put("type", "double");
			this.avEntry.setMetaData(metaData);
		}
	}
	
	public ValuesContainer calculate(ValuesContainer[] vcs) throws Exception
	{
		return new CalculatedValuesContainer(getAVEntry(), this, vcs);	
	}
	
	//may return NULL
	//if this formula contains formula arguments, 
	//1. creates a new HashMap of pv args and AVE names
	//2. replaces pv arguments in corresponding terms with unique strings (unique in regard to
	//current term)
	//3. replaces all occurencies of formula arguments with corresponding "(" + term +")" string
	//adds new arguments to hashmap
	public Formula tryToEliminateFormulaArguments(final Map namesToFormulasWithPVArgumentsOnlyMap) 
	{
		ArrayList formulaArgs = new ArrayList();
		String currentArg = null;
		//will become EVENTUALLY what it's named; at the start contains formula args
		//used in this term, too
		HashMap newPVArgsAndPVNamesMap = new HashMap();			
		for(int i=0; i<this.parameters.length; i++)
		{
			currentArg = this.parameters[i].getArg();
			if(namesToFormulasWithPVArgumentsOnlyMap.containsKey(getAVENameForArgument(currentArg)))
			{
				formulaArgs.add(currentArg);					
			}
			else if(this.parameters[i].isAVEFormula())
				//this argument is a formula, but the formula has not been resolved
				//i.e. it's not in the supplied namesToFormulasWithPVArgumentsOnlyMap
				return null;
			newPVArgsAndPVNamesMap.put(currentArg, this.parameters[i].getAVEName());
		}
		
		if(formulaArgs.isEmpty())
		{
			//don't even clone
			return this;
		}			
		int i,j;
		String newTerm = new String(this.term);
		String currentFormulaName = null;
		Formula currentFormula = null;
		String currentTerm = null;
		FormulaParameter[] formulaPVArgsAndPVNameEntries = null;
		//go through all formula args;
		//try to add the arguments of current formula (and their corresponding pv names) to pvArgsAndPVNamesMap
		// 	=> 	if argument is already in the map
		//		replace it with a unique string;
		//		replace in the formula term this argument with the unique string
		//replace in newTerm the formula arg with "(" + term +")"
		final String baseStr = "i";
		int uniqueStrCounter = 0;
		
		for(i=0; i<formulaArgs.size(); i++)
		{
			currentArg = formulaArgs.get(i).toString();
			currentFormulaName = getAVENameForArgument(currentArg);
			currentFormula = (Formula) namesToFormulasWithPVArgumentsOnlyMap.get(currentFormulaName);
			currentTerm = new String(currentFormula.getTerm());
			
			formulaPVArgsAndPVNameEntries = currentFormula.getFormulaParameters();
			String pvArg = null;
			String correspondingPVName = null;
			for(j=0; j<formulaPVArgsAndPVNameEntries.length; j++)
			{
				pvArg = formulaPVArgsAndPVNameEntries[j].getArg();
				correspondingPVName = currentFormula.getAVENameForArgument(pvArg);
				String s = pvArg;
				boolean replaceArgument = false;
				while(newPVArgsAndPVNamesMap.containsKey(s))
				{
					//replace with the unique string
					replaceArgument = true;
					s = baseStr + uniqueStrCounter;
					uniqueStrCounter++;
				}
				if(replaceArgument == true)
				{
					currentTerm = AVBaseUtilities.replaceArgumentInTerm(currentTerm, pvArg, s);
				}
				newPVArgsAndPVNamesMap.put(s, correspondingPVName);
			}
			newTerm = AVBaseUtilities.replaceArgumentInTerm(newTerm, currentArg, "(" + currentTerm + ")");
			newPVArgsAndPVNamesMap.remove(currentArg);
		}
		
		FormulaParameter[] argAndAVENameMapEntries = new FormulaParameter[newPVArgsAndPVNamesMap.size()];
		Iterator argIt = newPVArgsAndPVNamesMap.keySet().iterator();
		String currentPVName = null;
		i=0;
		while(argIt.hasNext())
		{
			currentArg = argIt.next().toString();
			currentPVName = newPVArgsAndPVNamesMap.get(currentArg).toString();
			argAndAVENameMapEntries[i] = new FormulaParameter(currentArg, currentPVName, false);
			i++;
		}
		
		return new Formula(
				this.avEntry,
				newTerm, 
				argAndAVENameMapEntries,
				false
		);
	}
}
