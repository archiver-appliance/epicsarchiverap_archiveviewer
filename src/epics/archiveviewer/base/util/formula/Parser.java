/*
 * Originally developed by Stefan Matthias Aust (sma@3plus4.de)
 */
package epics.archiveviewer.base.util.formula;

import epics.archiveviewer.base.util.formula.Node.*;

import java.io.IOException;
import java.io.StringReader;

import java.util.HashMap;

/**
 * This parser understands sentences of the following grammar: <blockquote>
 * expression = term {("+" | "-") term}. <br>
 * term = factor {("*" | "/") factor}. <br>
 * factor = unary {"^" number}. <br>
 * unary = ["-"] primary. <br>
 * primary = variable | constant | function | number | "(" expression ")". <br>
 * variable = "x". <br>
 * constant = "pi" | "e". <br>
 * function = name unary. <br>
 * number = digits ["." digits] ["e" ["+" | "-"] digits]. <br>
 * digits = "0".."9" {"0".."9"}. <br>
 * </blockquote> It will generate an AST consisting of <code>Node</code>
 * objects which you can evaluate using <code>Node.eval()</code>, providing a
 * value for the expression's variable.
 * 
 * @author Stefan Matthias Aust <sma@3plus4.de>
 */
public class Parser
{
	public static Class[] FUNCTIONS_CLASSES;
	
	private Scanner scanner;

	protected Token token;

	public Parser(Class[] functionsClasses, String s)
	{
		Parser.FUNCTIONS_CLASSES = functionsClasses;
		scanner = new Scanner(new StringReader(s));
		advance();
	}

	/**
	 * Constructs a tree of <code>Node</code> s for the given string. The
	 * method will throw an <code>Error</code> if the string cannot parsed
	 * correctly. It will return <code>null</code> if the string is empty.
	 */
	public static Node parse(Class[] functionsClasses, String s, HashMap varsAndTheirIndices)
	{
		Node.Variable.varsAndTheirIndices = varsAndTheirIndices;

		return new Parser(functionsClasses, s).parseExpression();
	}
	
	//the functions below are separate because of arithmetic order rules
	protected Node parseExpression()
	{
		Node n = null;
		
		if(token != Token.NOT)
			n = parseNonBooleanExpression();

		while (
				(token == Token.OR) || (token == Token.AND) || (token == Token.EQ) ||
				(token == Token.GREATER) || (token == Token.LESSER) || (token == Token.NOT)
		)
		{
			if(token == Token.OR)
			{
				advance();
				n = new Or(n, parseNonBooleanExpression());
			}
			else if (token == Token.AND)
			{
				
				advance();
				n = new And(n, parseNonBooleanExpression());
			}
			else if(token == Token.EQ)
			{
				advance();
				n = new Eq(n, parseNonBooleanExpression());
			}
			else if(token == Token.GREATER)
			{
				advance();
				n = new Greater(n, parseNonBooleanExpression());
			}
			else if(token == Token.LESSER)
			{
				advance();
				n = new Lesser(n, parseNonBooleanExpression());
			}
			else if(token == Token.NOT)
			{
				advance();
				n = new Not(parseNonBooleanExpression());
			}
		}

		return n;
	}

	protected Node parseNonBooleanExpression()
	{
		Node n = parseTerm();

		while ((token == Token.PLUS) || (token == Token.MINUS))
		{
			if (token == Token.PLUS)
			{
				advance();
				n = new Plus(n, parseTerm());
			}
			else
			{
				advance();
				n = new Minus(n, parseTerm());
			}
		}

		return n;
	}

	protected Node parseTerm()
	{
		Node n = parseFactor();

		while ((token == Token.TIMES) || (token == Token.DIVIDE))
		{
			if (token == Token.TIMES)
			{
				advance();
				n = new Times(n, parseFactor());
			}
			else
			{
				advance();
				n = new Divide(n, parseFactor());
			}
		}

		return n;
	}

	protected Node parseFactor()
	{
		Node n = parseUnary();

		while (token == Token.POWER)
		{
			advance();

			//n ^ (any term, not just a number)
			n = new Power(n, parsePrimary());
		}

		return n;
	}

	protected Node parseUnary()
	{
		boolean negative = token == Token.MINUS;

		if (negative)
		{
			advance();
		}

		Node n = parsePrimary();

		if (negative)
		{
			n = new Times(n, new Node.Number(-1));
		}

		return n;
	}

	protected Node parsePrimary()
	{
		if (token == Token.LPAREN)
		{
			advance();

			Node n = parseExpression();

			if (token != Token.RPAREN)
			{
				throw new Error("missing )");
			}

			advance();

			return n;
		}

		if (token.isVariable())
		{
			Node n = new Variable(token.getValue().toString());
			advance();
			return n;
		}
		
		if(token.isVariablesArray())
		{
			Node n = new VariablesArray(token.getValue().toString());
			advance();
			return n;
		}

		if (token.isNumber())
		{
			return parseNumber();
		}

		//if nothing else, try with a function
		return parseFunction();
	}

	protected Node parseFunction()
	{
		String name = token.getValue().toString();
		advance();

		if (name.equals("pi"))
		{
			return new Node.Number(Math.PI);
		}
		else if (name.equals("e"))
		{
			return new Node.Number(Math.E);
		}

		try
		{
			return new Function(name, parseUnary());
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException("unknown function " + name + "; " + e.toString());
		}
	}

	protected Node parseNumber()
	{
		double number = ((Double)token.getValue()).doubleValue();
		advance();

		return new Node.Number(number);
	}

	protected void advance()
	{
		try
		{
			token = scanner.nextToken();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Parsing the formula term failed");
		}
	}
}