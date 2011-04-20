/*
 * Originally developed by Stefan Matthias Aust (sma@3plus4.de)
 */
package epics.archiveviewer.base.util.formula;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class is the abstract superclass of all nodes of a parsed expression. It
 * is also used as container to collect all that tiny classes for +, - etc. A
 * node can be evaluated using <code>eval()</code>.
 * 
 * @see Parser
 * 
 * @author Stefan Matthias Aust <sma@3plus4.de>
 */
public abstract class Node
{
	public abstract double eval(double[] params);

	public static class Number extends Node
	{
		protected double value;

		public Number(double v)
		{
			this.value = v;
		}

		public double eval(double[] params)
		{
			return this.value;
		}

		public String toString()
		{
			return String.valueOf(value);
		}
	}

	public static class Variable extends Node
	{
		//the same order as the the evaluation of params shall go
		protected static HashMap varsAndTheirIndices;

		String var;

		public Variable(String str)
		{
			var = str;
		}

		public double eval(double[] params)
		{
			//get the index
			try
			{
				return params[((Integer) varsAndTheirIndices.get(var)).intValue()];
			}
			catch (Exception e)
			{
				return Double.NaN;
			}
		}

		public String toString()
		{
			return var;
		}
	}
	
	public static class VariablesArray extends Node
	{
		private final String varsArray;
		
		private final Variable[] variables;

		public VariablesArray(String str)
		{
			this.varsArray = str;
			StringTokenizer st = new StringTokenizer(this.varsArray, ",");
			Vector v = new Vector();			
			while(st.hasMoreTokens())
			{
				v.add(new Variable(st.nextToken()));
			}			
			this.variables = (Variable[]) v.toArray(new Variable[v.size()]);
		}

		//just for the sake of implementing it
		public double eval(double[] params)
		{
			return Double.NaN;
		}
		
		public Double[] getNumericArray(double[] params)
		{
			Double[] numArray = new Double[this.variables.length];
			for(int i=0; i<this.variables.length; i++)
			{
				numArray[i] = new Double(this.variables[i].eval(params));
			}
			return numArray;
		}

		public String toString()
		{
			return varsArray;
		}
	}
	
	public static abstract class Operation extends Node
	{
		protected Node a;

		protected Node b;

		public Operation(Node a, Node b)
		{
			this.a = a;
			this.b = b;
		}

		public String toString()
		{
			if(b != null)
				return "(" + a.toString() + op() + b.toString() + ")";
			
			return "(" + op() + a.toString() + ")";
		}

		protected abstract String op();
	}

	public static class Or extends Operation
	{
		public Or(Node a, Node b)
		{
			super(a, b);
		}

		public double eval(double[] params)
		{
			boolean result = (a.eval(params) == 1) || (b.eval(params) == 1);
			if(result)
				return 1;
			return 0;
		}

		protected String op()
		{
			return "||";
		}
	}
	
	public static class And extends Operation
	{
		public And(Node a, Node b)
		{
			super(a, b);
		}

		public double eval(double[] params)
		{
			boolean result = (a.eval(params) == 1) && (b.eval(params) == 1);
			if(result)
				return 1;
			return 0;
		}

		protected String op()
		{
			return "&&";
		}
	}
	
	public static class Eq extends Operation
	{
		public Eq(Node a, Node b)
		{
			super(a, b);
		}

		public double eval(double[] params)
		{
			if(a.eval(params) == b.eval(params))
				return 1;
			return 0;
		}

		protected String op()
		{
			return "==";
		}
	}
	
	public static class Greater extends Operation
	{
		public Greater(Node a, Node b)
		{
			super(a, b);
		}

		public double eval(double[] params)
		{
			if(a.eval(params) > b.eval(params))
				return 1;
			return 0;
		}

		protected String op()
		{
			return ">";
		}
	}
	
	public static class Lesser extends Operation
	{
		public Lesser(Node a, Node b)
		{
			super(a, b);
		}

		public double eval(double[] params)
		{
			if(a.eval(params) < b.eval(params))
				return 1;
			return 0;
		}

		protected String op()
		{
			return "<";
		}
	}
	
	public static class Not extends Operation
	{
		public Not(Node a)
		{
			super(a, null);
		}

		public double eval(double[] params)
		{
			if(a.eval(params) == 0)
				return 1;
			return 0;
		}

		protected String op()
		{
			return "!";
		}
	}
	
	public static class Plus extends Operation
	{
		public Plus(Node a, Node b)
		{
			super(a, b);
		}

		public double eval(double[] params)
		{
			return a.eval(params) + b.eval(params);
		}

		protected String op()
		{
			return "+";
		}
	}

	public static class Minus extends Operation
	{
		public Minus(Node a, Node b)
		{
			super(a, b);
		}

		public double eval(double[] params)
		{
			return a.eval(params) - b.eval(params);
		}

		protected String op()
		{
			return "-";
		}
	}

	public static class Times extends Operation
	{
		public Times(Node a, Node b)
		{
			super(a, b);
		}

		public double eval(double[] params)
		{
			return a.eval(params) * b.eval(params);
		}

		protected String op()
		{
			return "*";
		}
	}

	public static class Divide extends Operation
	{
		public Divide(Node a, Node b)
		{
			super(a, b);
		}

		public double eval(double[] params)
		{
			return a.eval(params) / b.eval(params);
		}

		protected String op()
		{
			return "/";
		}
	}

	public static class Power extends Operation
	{
		public Power(Node a, Node b)
		{
			super(a, b);
		}

		public double eval(double[] params)
		{
			return Math.pow(a.eval(params), b.eval(params));
		}

		protected String op()
		{
			return "^";
		}
	}

	public static class Function extends Node
	{
		private Method func;

		private Node a;

		public Function(String name, Node a) throws NoSuchMethodException
		{
			boolean methodFound = false;
			for(int i = 0; i<Parser.FUNCTIONS_CLASSES.length; i++)
			{
				try
				{
					this.func = Parser.FUNCTIONS_CLASSES[i].getMethod(name, new Class[]
					{
						Double.TYPE
					});
				}
				catch(NoSuchMethodException e1)
				{
					try
					{
						this.func = Parser.FUNCTIONS_CLASSES[i].getMethod(name, new Class[]
						{
							double[].class
						});						
					}
					catch(NoSuchMethodException e2)
					{
						try
						{
							this.func = Parser.FUNCTIONS_CLASSES[i].getMethod(name, new Class[]
							{
								Double.TYPE,
								Double.TYPE
							});	
						}
						catch(NoSuchMethodException e3)
						{
							continue;
						}
					}
				}
				methodFound = true;
				break;
			}
			if(methodFound == false)
			{
				throw new NoSuchMethodException("The function " + name + " is currently not supported");
			}
			this.a = a;
		}

		public double eval(double[] params)
		{
			Object[] args = null;
			if(a instanceof VariablesArray)
			{
				//first try to pass as an array of single arguments
				args = ((VariablesArray)a).getNumericArray(params);
				try
				{
					return ((Double) this.func.invoke(null, args)).doubleValue();
				}
				catch(Exception e)
				{
					//now as a single argument that is an array
					double[] arguments = new double[args.length];
					for(int i=0; i<arguments.length; i++)
					{
						arguments[i] = ((Double)args[i]).doubleValue();
					}
					args = new Object[]{arguments};
				}
			}
			else
			{
				args = new Object[]{new Double(a.eval(params))};
			}
			try
			{
				return ((Double) this.func.invoke(null, args)).doubleValue();		
			}
			catch (InvocationTargetException e)
			{
				throw (RuntimeException) e.getTargetException();
			}
			catch (IllegalAccessException e)
			{
				throw new IllegalArgumentException("illegal access to " + this.func.toString());
			}
		}

		public String toString()
		{
			return func.getName() + "(" + a.toString() + ")";
		}
	}
}