package com.inova8.resolver;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/*

Copyright (c) 2007 E. W. Bachtal, Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all copies or substantial 
  portions of the Software.

The software is provided "as is", without warranty of any kind, express or implied, including but not 
limited to the warranties of merchantability, fitness for a particular purpose and noninfringement. In 
no event shall the authors or copyright holders be liable for any claim, damages or other liability, 
whether in an action of contract, tort or otherwise, arising from, out of or in connection with the 
software or the use or other dealings in the software. 

http://ewbi.blogs.com/develops/2007/03/excel_formula_p.html
http://ewbi.blogs.com/develops/2004/12/excel_formula_p.html

v1.0  Original
v1.1  Added support for in-formula scientific notation.

*/



//C# TO JAVA CONVERTER TODO TASK: The interface type was changed to the closest equivalent Java type, but the methods implemented will need adjustment:
public class ExcelFormulaClass implements java.util.List<FormulaTokenClass>
{

	private String formula;
	private java.util.ArrayList<FormulaTokenClass> tokens;

	public ExcelFormulaClass(String formula)
	{
		if (formula == null)
		{
			throw new IllegalArgumentException("formula");
		}
		this.formula = formula.trim();
		tokens = new java.util.ArrayList<FormulaTokenClass>();
		ParseToTokens();
	}

	public final String getFormula()
	{
		return formula;
	}

	public final FormulaTokenClass getItem(int index)
	{
		return tokens.get(index);
	}
	public final void setItem(int index, FormulaTokenClass value)
	{
		throw new UnsupportedOperationException();
	}

	public final int IndexOf(FormulaTokenClass item)
	{
		return tokens.indexOf(item);
	}

	public final void Insert(int index, FormulaTokenClass item)
	{
		throw new UnsupportedOperationException();
	}

	public final void RemoveAt(int index)
	{
		throw new UnsupportedOperationException();
	}

	public final void Add(FormulaTokenClass item)
	{
		throw new UnsupportedOperationException();
	}

	public final void Clear()
	{
		throw new UnsupportedOperationException();
	}

	public final boolean Contains(FormulaTokenClass item)
	{
		return tokens.contains(item);
	}

	public final boolean getIsReadOnly()
	{
		return true;
	}

	public final boolean Remove(FormulaTokenClass item)
	{
		throw new UnsupportedOperationException();
	}

	public final void CopyTo(FormulaTokenClass[] array, int arrayIndex)
	{
		//tokens.CopyTo(array, arrayIndex);
	}

	public final int getCount()
	{
		return tokens.size();
	}

	public final java.util.Iterator<FormulaTokenClass> GetEnumerator()
	{
		return tokens.iterator();
	}

	public final int ArrayIndexOf(String[] a, String v)
	{
		int indexOf = -1;
		for (String arrayElement : a)
		{
			indexOf++;
			if (v.equals(arrayElement))
			{
				return indexOf;
			}
		}
		return -1;
	}

	private void ParseToTokens()
	{

		// No attempt is made to verify formulas; assumes formulas are derived from Excel, where 
		// they can only exist if valid; stack overflows/underflows sunk as nulls without exceptions.

		if ((formula.length() < 2) || (formula.charAt(0) != '='))
		{
			return;
		}

		ExcelFormulaTokens tokens1 = new ExcelFormulaTokens();
		ExcelFormulaStack stack = new ExcelFormulaStack();

		final char QUOTE_DOUBLE = '"';
		final char QUOTE_SINGLE = '\'';
		final char BRACKET_CLOSE = ']';
		final char BRACKET_OPEN = '[';
		final char BRACE_OPEN = '{';
		final char BRACE_CLOSE = '}';
		final char PAREN_OPEN = '(';
		final char PAREN_CLOSE = ')';
		final char SEMICOLON = ';';
		final char WHITESPACE = ' ';
		final char COMMA = ',';
		final char ERROR_START = '#';

		final String OPERATORS_SN = "+-";
		final String OPERATORS_INFIX = "+-*/^&=><";
		final String OPERATORS_POSTFIX = "%";

		String[] ERRORS = new String[] {"#NULL!", "#DIV/0!", "#VALUE!", "#REF!", "#NAME?", "#NUM!", "#N/A"};

		String[] COMPARATORS_MULTI = new String[] {">=", "<=", "<>"};

		boolean inString = false;
		boolean inPath = false;
		boolean inRange = false;
		boolean inError = false;

		int index = 1;
		String value = "";

		while (index < formula.length())
		{

			// state-dependent character evaluation (order is important)

			// double-quoted strings
			// embeds are doubled
			// end marks token

			if (inString)
			{
				if (formula.charAt(index) == QUOTE_DOUBLE)
				{
					if (((index + 2) <= formula.length()) && (formula.charAt(index + 1) == QUOTE_DOUBLE))
					{
						value += QUOTE_DOUBLE;
						index++;
					}
					else
					{
						inString = false;
						tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Operand, FormulaTokenClass.TokenSubtype.Text));
						value = "";
					}
				}
				else
				{
					value += formula.charAt(index);
				}
				index++;
				continue;
			}

			// single-quoted strings (links)
			// embeds are double
			// end does not mark a token

			if (inPath)
			{
				if (formula.charAt(index) == QUOTE_SINGLE)
				{
					if (((index + 2) <= formula.length()) && (formula.charAt(index + 1) == QUOTE_SINGLE))
					{
						value += QUOTE_SINGLE;
						index++;
					}
					else
					{
						inPath = false;
					}
				}
				else
				{
					value += formula.charAt(index);
				}
				index++;
				continue;
			}

			// bracked strings (R1C1 range index or linked workbook name)
			// no embeds (changed to "()" by Excel)
			// end does not mark a token

			if (inRange)
			{
				if (formula.charAt(index) == BRACKET_CLOSE)
				{
					inRange = false;
				}
				value += formula.charAt(index);
				index++;
				continue;
			}

			// error values
			// end marks a token, determined from absolute list of values

			if (inError)
			{
				value += formula.charAt(index);
				index++;
				//if (Array.IndexOf(ERRORS, value) != -1)
				if (ArrayIndexOf(ERRORS, value) != -1)
				{
					inError = false;
					tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Operand, FormulaTokenClass.TokenSubtype.Error));
					value = "";
				}
				continue;
			}

			// scientific notation check

			if ((OPERATORS_SN).indexOf(formula.charAt(index)) != -1)
			{
				if (value.length() > 1)
				{
					if (java.util.regex.Pattern.compile("^[1-9]{1}(\\.[0-9]+)?E{1}$").matcher(value).find())
					{
						value += formula.charAt(index);
						index++;
						continue;
					}
				}
			}

			// independent character evaluation (order not important)

			// establish state-dependent character evaluations

			if (formula.charAt(index) == QUOTE_DOUBLE)
			{
				if (value.length() > 0)
				{ // unexpected
					tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Unknown));
					value = "";
				}
				inString = true;
				index++;
				continue;
			}

			if (formula.charAt(index) == QUOTE_SINGLE)
			{
				if (value.length() > 0)
				{ // unexpected
					tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Unknown));
					value = "";
				}
				inPath = true;
				index++;
				continue;
			}

			if (formula.charAt(index) == BRACKET_OPEN)
			{
				inRange = true;
				value += BRACKET_OPEN;
				index++;
				continue;
			}

			if (formula.charAt(index) == ERROR_START)
			{
				if (value.length() > 0)
				{ // unexpected
					tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Unknown));
					value = "";
				}
				inError = true;
				value += ERROR_START;
				index++;
				continue;
			}

			// mark start and end of arrays and array rows

			if (formula.charAt(index) == BRACE_OPEN)
			{
				if (value.length() > 0)
				{ // unexpected
					tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Unknown));
					value = "";
				}
				stack.Push(tokens1.Add(new FormulaTokenClass("ARRAY", FormulaTokenClass.TokenType.Function, FormulaTokenClass.TokenSubtype.Start)));
				stack.Push(tokens1.Add(new FormulaTokenClass("ARRAYROW", FormulaTokenClass.TokenType.Function, FormulaTokenClass.TokenSubtype.Start)));
				index++;
				continue;
			}

			if (formula.charAt(index) == SEMICOLON)
			{
				if (value.length() > 0)
				{
					tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Operand));
					value = "";
				}
				tokens1.Add(stack.Pop());
				tokens1.Add(new FormulaTokenClass(",", FormulaTokenClass.TokenType.Argument));
				stack.Push(tokens1.Add(new FormulaTokenClass("ARRAYROW", FormulaTokenClass.TokenType.Function, FormulaTokenClass.TokenSubtype.Start)));
				index++;
				continue;
			}

			if (formula.charAt(index) == BRACE_CLOSE)
			{
				if (value.length() > 0)
				{
					tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Operand));
					value = "";
				}
				tokens1.Add(stack.Pop());
				tokens1.Add(stack.Pop());
				index++;
				continue;
			}

			// trim white-space

			if (formula.charAt(index) == WHITESPACE)
			{
				if (value.length() > 0)
				{
					tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Operand));
					value = "";
				}
				tokens1.Add(new FormulaTokenClass("", FormulaTokenClass.TokenType.Whitespace));
				index++;
				while ((formula.charAt(index) == WHITESPACE) && (index < formula.length()))
				{
					index++;
				}
				continue;
			}

			// multi-character comparators

			if ((index + 2) <= formula.length())
			{
				//if (Array.IndexOf(COMPARATORS_MULTI, formula.Substring(index, 2)) != -1)
				if (ArrayIndexOf(COMPARATORS_MULTI, formula.substring(index, index + 2)) != -1)
				{
					if (value.length() > 0)
					{
						tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Operand));
						value = "";
					}
					tokens1.Add(new FormulaTokenClass(formula.substring(index, index + 2), FormulaTokenClass.TokenType.OperatorInfix, FormulaTokenClass.TokenSubtype.Logical));
					index += 2;
					continue;
				}
			}

			// standard infix operators

			if ((OPERATORS_INFIX).indexOf(formula.charAt(index)) != -1)
			{
				if (value.length() > 0)
				{
					tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Operand));
					value = "";
				}
				tokens1.Add(new FormulaTokenClass(Character.toString(formula.charAt(index)), FormulaTokenClass.TokenType.OperatorInfix));
				index++;
				continue;
			}

			// standard postfix operators (only one)

			if ((OPERATORS_POSTFIX).indexOf(formula.charAt(index)) != -1)
			{
				if (value.length() > 0)
				{
					tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Operand));
					value = "";
				}
				tokens1.Add(new FormulaTokenClass(Character.toString(formula.charAt(index)), FormulaTokenClass.TokenType.OperatorPostfix));
				index++;
				continue;
			}

			// start subexpression or function

			if (formula.charAt(index) == PAREN_OPEN)
			{
				if (value.length() > 0)
				{
					stack.Push(tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Function, FormulaTokenClass.TokenSubtype.Start)));
					value = "";
				}
				else
				{
					stack.Push(tokens1.Add(new FormulaTokenClass("", FormulaTokenClass.TokenType.Subexpression, FormulaTokenClass.TokenSubtype.Start)));
				}
				index++;
				continue;
			}

			// function, subexpression, or array parameters, or operand unions

			if (formula.charAt(index) == COMMA)
			{
				if (value.length() > 0)
				{
					tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Operand));
					value = "";
				}
				if (stack.getCurrent().getType() != FormulaTokenClass.TokenType.Function)
				{
					tokens1.Add(new FormulaTokenClass(",", FormulaTokenClass.TokenType.OperatorInfix, FormulaTokenClass.TokenSubtype.Union));
				}
				else
				{
					tokens1.Add(new FormulaTokenClass(",", FormulaTokenClass.TokenType.Argument));
				}
				index++;
				continue;
			}

			// stop subexpression

			if (formula.charAt(index) == PAREN_CLOSE)
			{
				if (value.length() > 0)
				{
					tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Operand));
					value = "";
				}
				tokens1.Add(stack.Pop());
				index++;
				continue;
			}

			// token accumulation

			value += formula.charAt(index);
			index++;

		}

		// dump remaining accumulation

		if (value.length() > 0)
		{
			tokens1.Add(new FormulaTokenClass(value, FormulaTokenClass.TokenType.Operand));
		}

		// move tokenList to new set, excluding unnecessary white-space tokens and converting necessary ones to intersections

		ExcelFormulaTokens tokens2 = new ExcelFormulaTokens(tokens1.getCount());

		while (tokens1.MoveNext())
		{

			FormulaTokenClass token = tokens1.getCurrent();

			if (token == null)
			{
				continue;
			}

			if (token.getType() != FormulaTokenClass.TokenType.Whitespace)
			{
				tokens2.Add(token);
				continue;
			}

			if ((tokens1.getBOF()) || (tokens1.getEOF()))
			{
				continue;
			}

			FormulaTokenClass previous = tokens1.getPrevious();

			if (previous == null)
			{
				continue;
			}

			if (!(((previous.getType() == FormulaTokenClass.TokenType.Function) && (previous.getSubtype() == FormulaTokenClass.TokenSubtype.Stop)) || ((previous.getType() == FormulaTokenClass.TokenType.Subexpression) && (previous.getSubtype() == FormulaTokenClass.TokenSubtype.Stop)) || (previous.getType() == FormulaTokenClass.TokenType.Operand)))
			{
				continue;
			}

			FormulaTokenClass next = tokens1.getNext();

			if (next == null)
			{
				continue;
			}

			if (!(((next.getType() == FormulaTokenClass.TokenType.Function) && (next.getSubtype() == FormulaTokenClass.TokenSubtype.Start)) || ((next.getType() == FormulaTokenClass.TokenType.Subexpression) && (next.getSubtype() == FormulaTokenClass.TokenSubtype.Start)) || (next.getType() == FormulaTokenClass.TokenType.Operand)))
			{
				continue;
			}

			tokens2.Add(new FormulaTokenClass("", FormulaTokenClass.TokenType.OperatorInfix, FormulaTokenClass.TokenSubtype.Intersection));

		}

		// move tokens to final list, switching infix "-" operators to prefix when appropriate, switching infix "+" operators 
		// to noop when appropriate, identifying operand and infix-operator subtypes, and pulling "@" from function names

		tokens = new java.util.ArrayList<FormulaTokenClass>(tokens2.getCount());

		while (tokens2.MoveNext())
		{

			FormulaTokenClass token = tokens2.getCurrent();

			if (token == null)
			{
				continue;
			}

			FormulaTokenClass previous = tokens2.getPrevious();
			FormulaTokenClass next = tokens2.getNext();

			if ((token.getType() == FormulaTokenClass.TokenType.OperatorInfix) && (token.getValue().equals("-")))
			{
				if (tokens2.getBOF())
				{
					token.setType(FormulaTokenClass.TokenType.OperatorPrefix);
				}
				else if (((previous.getType() == FormulaTokenClass.TokenType.Function) && (previous.getSubtype() == FormulaTokenClass.TokenSubtype.Stop)) || ((previous.getType() == FormulaTokenClass.TokenType.Subexpression) && (previous.getSubtype() == FormulaTokenClass.TokenSubtype.Stop)) || (previous.getType() == FormulaTokenClass.TokenType.OperatorPostfix) || (previous.getType() == FormulaTokenClass.TokenType.Operand))
				{
					token.setSubtype(FormulaTokenClass.TokenSubtype.Math);
				}
				else
				{
					token.setType(FormulaTokenClass.TokenType.OperatorPrefix);
				}

				tokens.add(token);
				continue;
			}

			if ((token.getType() == FormulaTokenClass.TokenType.OperatorInfix) && (token.getValue().equals("+")))
			{
				if (tokens2.getBOF())
				{
					continue;
				}
				else if (((previous.getType() == FormulaTokenClass.TokenType.Function) && (previous.getSubtype() == FormulaTokenClass.TokenSubtype.Stop)) || ((previous.getType() == FormulaTokenClass.TokenType.Subexpression) && (previous.getSubtype() == FormulaTokenClass.TokenSubtype.Stop)) || (previous.getType() == FormulaTokenClass.TokenType.OperatorPostfix) || (previous.getType() == FormulaTokenClass.TokenType.Operand))
				{
					token.setSubtype(FormulaTokenClass.TokenSubtype.Math);
				}
				else
				{
					continue;
				}

				tokens.add(token);
				continue;
			}

			if ((token.getType() == FormulaTokenClass.TokenType.OperatorInfix) && (token.getSubtype() == FormulaTokenClass.TokenSubtype.Nothing))
			{
				if (("<>=").indexOf(token.getValue().substring(0, 1)) != -1)
				{
					token.setSubtype(FormulaTokenClass.TokenSubtype.Logical);
				}
				else if (token.getValue().equals("&"))
				{
					token.setSubtype(FormulaTokenClass.TokenSubtype.Concatenation);
				}
				else
				{
					token.setSubtype(FormulaTokenClass.TokenSubtype.Math);
				}

				tokens.add(token);
				continue;
			}

			if ((token.getType() == FormulaTokenClass.TokenType.Operand) && (token.getSubtype() == FormulaTokenClass.TokenSubtype.Nothing))
			{
				double d = 0;
                
				boolean isNumber; try  {  d = Double.parseDouble(token.getValue());  isNumber = true;    } catch ( Exception e) {  d = 0;  isNumber = false;  }  
                
				if (!isNumber)
				{
					if ((token.getValue().equals("TRUE")) || (token.getValue().equals("FALSE")))
					{
						token.setSubtype(FormulaTokenClass.TokenSubtype.Logical);
					}
					else
					{
						token.setSubtype(FormulaTokenClass.TokenSubtype.Range);
					}
				}
				else
				{
					token.setSubtype(FormulaTokenClass.TokenSubtype.Number);
				}

				tokens.add(token);
				continue;
			}

			if (token.getType() == FormulaTokenClass.TokenType.Function)
			{
				if (token.getValue().length() > 0)
				{
					if (token.getValue().substring(0, 1).equals("@"))
					{
						token.setValue(token.getValue().substring(1));
					}
				}
			}

			tokens.add(token);

		}

	}

	public static class ExcelFormulaTokens
	{

		private int index = -1;
		private java.util.ArrayList<FormulaTokenClass> tokens;

		public ExcelFormulaTokens()
		{
			this(4);
		}

		public ExcelFormulaTokens(int capacity)
		{
			tokens = new java.util.ArrayList<FormulaTokenClass>(capacity);
		}

		public final int getCount()
		{
			return tokens.size();
		}

		public final boolean getBOF()
		{
			return (index <= 0);
		}

		public final boolean getEOF()
		{
			return (index >= (tokens.size() - 1));
		}

		public final FormulaTokenClass getCurrent()
		{
			if (index == -1)
			{
				return null;
			}
			return tokens.get(index);
		}

		public final FormulaTokenClass getNext()
		{
			if (getEOF())
			{
				return null;
			}
			return tokens.get(index + 1);
		}

		public final FormulaTokenClass getPrevious()
		{
			if (index < 1)
			{
				return null;
			}
			return tokens.get(index - 1);
		}

		public final FormulaTokenClass Add(FormulaTokenClass token)
		{
			tokens.add(token);
			return token;
		}

		public final boolean MoveNext()
		{
			if (getEOF())
			{
				return false;
			}
			index++;
			return true;
		}

		public final void Reset()
		{
			index = -1;
		}

	}

	public static class ExcelFormulaStack
	{

		private java.util.Stack<FormulaTokenClass> stack = new java.util.Stack<FormulaTokenClass>();

		public ExcelFormulaStack()
		{
		}

		public final void Push(FormulaTokenClass token)
		{
			stack.push(token);
		}

		public final FormulaTokenClass Pop()
		{
			if (stack.empty())
			{
				return null;
			}
			return new FormulaTokenClass("", stack.pop().getType(), FormulaTokenClass.TokenSubtype.Stop);
		}

		public final FormulaTokenClass getCurrent()
		{
			return (stack.size() > 0) ? stack.peek() : null;
		}

	}

	@Override
	public boolean add(FormulaTokenClass arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void add(int arg0, FormulaTokenClass arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addAll(Collection<? extends FormulaTokenClass> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends FormulaTokenClass> arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FormulaTokenClass get(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<FormulaTokenClass> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lastIndexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<FormulaTokenClass> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<FormulaTokenClass> listIterator(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FormulaTokenClass remove(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FormulaTokenClass set(int arg0, FormulaTokenClass arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<FormulaTokenClass> subList(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

}