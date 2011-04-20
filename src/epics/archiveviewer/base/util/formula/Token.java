/*
 * Originally developed by Stefan Matthias Aust (sma@3plus4.de)
 */
package epics.archiveviewer.base.util.formula;

import epics.archiveviewer.base.util.formula.Node.Variable;

import java.lang.reflect.Method;

import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * A Token represents one piece of the input stream.
 * 
 * @see Scanner
 * 
 * @author Stefan Matthias Aust <sma@3plus4.de>
 */
class Token
{
	public static final Token EOF = new Token();

	public static final Token PLUS = new Token("+", true);

	public static final Token MINUS = new Token("-", true);

	public static final Token TIMES = new Token("*", true);

	public static final Token DIVIDE = new Token("/", true);

	public static final Token POWER = new Token("^", true);

	public static final Token LPAREN = new Token("(", true);

	public static final Token RPAREN = new Token(")", true);
	
	public static final Token OR = new Token("||", true);
	
	public static final Token AND = new Token("&&", true);
	
	public static final Token EQ = new Token("==", true);
	
	public static final Token NOT = new Token("!", true);
	
	public static final Token GREATER = new Token(">", true);

	public static final Token LESSER = new Token("<", true);

	private final boolean isNumber;

	private final boolean isOperator;
	
	private final Object value;
	
	private boolean isVariable(Object o)
	{
		Iterator varIterator = Variable.varsAndTheirIndices.keySet().iterator();

		while (varIterator.hasNext())
		{
			if (varIterator.next().toString().equals(o))
			{
				return true;
			}
		}

		return false;
	}

	public Token()
	{
		this.isNumber = false;
		this.isOperator = false;
		this.value = null;
	}

	public Token(String name, boolean _isOperator)
	{
		this.isNumber = false;
		this.isOperator = _isOperator;
		this.value = name;
	}

	public Token(Double number)
	{
		this.isNumber = true;
		this.value = number;
		this.isOperator = false;
	}

	public Object getValue()
	{
		return this.value;
	}

	public boolean isVariable()
	{
		if (this.isNumber || this.isOperator)
		{
			return false;
		}

		return isVariable(this.value);
	}
	
	public boolean isOperator()
	{
		return this.isOperator;
	}
	
	public boolean isNumber()
	{
		return this.isNumber;
	}
	
	//e.g. x0,x1 ,x2, x3
	public boolean isVariablesArray()
	{
		if (this.isNumber || this.isOperator)
		{
			return false;
		}

		StringTokenizer st = new StringTokenizer(this.value.toString(), ",");
		while(st.hasMoreTokens())
		{
			if(isVariable(st.nextToken().trim()) == false)
				return false;
		}

		return true;
	}
}