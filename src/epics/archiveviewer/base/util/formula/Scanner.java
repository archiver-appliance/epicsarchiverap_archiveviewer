/*
 * Originally developed by Stefan Matthias Aust (sma@3plus4.de)
 */
package epics.archiveviewer.base.util.formula;

import java.io.*;

/**
 * A scanner breaks the input stream into handy tokens which are then processed
 * by the parser. This scanner knows about mathematic operations like +, -, *, /
 * and ^ as well as named functions, like sin, syntax like parenthesises,
 * variables and floating point numbers.
 * 
 * @see Token
 * @see Parser
 * 
 * @author Stefan Matthias Aust <sma@3plus4.de>
 */
class Scanner
{
	private static final int NONE = -99;

	private Reader r;

	private int ahead = NONE;

	/**
	 * Constructs a new scanner on the given source. Please note, in case you
	 * want to reuse the <code>Reader</code> after scanning some data, this
	 * scanner will read one character ahead which is then lost.
	 */
	public Scanner(Reader r)
	{
		this.r = r;
	}

	/**
	 * Returns the next <code>Token</code> from the input source.
	 * 
	 * @see Token
	 */
	public Token nextToken() throws IOException
	{
		int ch;

		// Get the next non-whitespace character
		do
		{
			ch = getch();

			if (ch < 0)
			{
				return Token.EOF;
			}
		}
		while (Character.isWhitespace((char) ch));

		// Test for the usual operators
		if (ch == '+')
		{
			return Token.PLUS;
		}

		if (ch == '-')
		{
			return Token.MINUS;
		}

		if (ch == '*')
		{
			return Token.TIMES;
		}

		if (ch == '/')
		{
			return Token.DIVIDE;
		}

		if (ch == '^')
		{
			return Token.POWER;
		}

		if (ch == '(')
		{
			return Token.LPAREN;
		}

		if (ch == ')')
		{
			return Token.RPAREN;
		}
		
		if(ch == '|')
		{
			ch = getch();
			if(ch == '|')
				return Token.OR;
			else
				throw new IOException("unknown character " + (char) ch);
		}
		
		if(ch == '&')
		{
			ch = getch();
			if(ch == '&')
				return Token.AND;
			else
				throw new IOException("unknown character " + (char) ch);
		}
		
		if(ch == '=')
		{
			ch = getch();
			if(ch == '=')
				return Token.EQ;
			else
				throw new IOException("unknown character " + (char) ch);
		}
		
		if(ch == '>')
			return Token.GREATER;
		
		if(ch == '<')
			return Token.LESSER;
		
		if(ch == '!')
			return Token.NOT;

		if (Character.isLetter((char) ch))
		{
			return readStringToken(ch);
		}

		if (Character.isDigit((char) ch))
		{
			return readNumber(ch);
		}

		throw new IOException("unknown character " + (char) ch);
	}

	//reads either a function or a variable
	private Token readStringToken(int ch) throws IOException
	{
		StringBuffer sb = new StringBuffer(16);

		while (Character.isLetterOrDigit((char) ch) || ((char) ch) == ',')
		{
			sb.append((char) ch);
			ch = getch();
		}

		unget(ch);

		return new Token(sb.toString().toLowerCase(), false);
	}

	private Token readNumber(int ch) throws IOException
	{
		StringBuffer sb = new StringBuffer(16);

		while (Character.isDigit((char) ch))
		{
			sb.append((char) ch);
			ch = getch();
		}

		if (ch == '.')
		{
			sb.append('.');
			ch = getch();

			while (Character.isDigit((char) ch))
			{
				sb.append((char) ch);
				ch = getch();
			}
		}

		if ((ch == 'e') || (ch == 'E'))
		{
			sb.append('e');
			ch = getch();

			if ((ch == '+') || (ch == '-'))
			{
				sb.append((char) ch);
				ch = getch();
			}

			while (Character.isDigit((char) ch))
			{
				sb.append((char) ch);
				ch = getch();
			}
		}

		unget(ch);

		try
		{
			return new Token(new Double(sb.toString()));
		}
		catch (NumberFormatException e)
		{
			throw new IOException("number format exception");
		}
	}

	private int getch() throws IOException
	{
		if (ahead != NONE)
		{
			int ch = ahead;
			ahead = NONE;

			return ch;
		}

		return r.read();
	}

	private void unget(int ch)
	{
		if (ahead != NONE)
		{
			throw new Error("unget called twice");
		}

		ahead = ch;
	}
}