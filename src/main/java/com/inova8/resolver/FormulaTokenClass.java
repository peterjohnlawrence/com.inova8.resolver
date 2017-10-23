package com.inova8.resolver;

public class FormulaTokenClass
{

	private String value;
	private TokenType type = TokenType.values()[0];
	private TokenSubtype subtype = TokenSubtype.values()[0];

	public FormulaTokenClass(String value, TokenType type)
	{
		this(value, type, TokenSubtype.Nothing);
	}

	public FormulaTokenClass(String value, TokenType type, TokenSubtype subtype)
	{
		this.value = value;
		this.type = type;
		this.subtype = subtype;
	}

	public final String getValue()
	{
		return value;
	}
	public final void setValue(String value)
	{
		this.value = value;
	}

	public final TokenType getType()
	{
		return type;
	}
	public final void setType(TokenType value)
	{
		type = value;
	}

	public final TokenSubtype getSubtype()
	{
		return subtype;
	}
	public final void setSubtype(TokenSubtype value)
	{
		subtype = value;
	}
	public final TokenCategory getCategory()
	{
		if (getType() == TokenType.OperatorInfix)
		{
			if (getSubtype() == TokenSubtype.Math)
			{
				if (value.equals("*") || value.equals("/"))
				{
					return TokenCategory.Product;
				}
				else if (value.equals("+") || value.equals("-"))
				{
					return TokenCategory.Sum;
				}
				else if (value.equals("^"))
				{
					return TokenCategory.Expon;
				}
				else
				{
					return TokenCategory.Nothing;
				}
			}
			else
			{
				return TokenCategory.Nothing;
			}
		}
		else if (getType() == TokenType.OperatorPrefix)
		{
			if (value.equals("+") || value.equals("-") || value.equals("!"))
			{
				return TokenCategory.Sum;
			}
			else
			{
				return TokenCategory.Nothing;
			}
		}
		else
		{
			return TokenCategory.Nothing;
		}
	}

	public enum TokenType
	{
		Noop(0),
		Operand(1), // x y z 27 abc
		Function(2), // fnc
		Subexpression(3), // ()
		Argument(4), // ,
		OperatorPrefix(5), // + -
		OperatorInfix(6), // + - * / ^ & = > <
		OperatorPostfix(7), // %
		Whitespace(8),
		Unknown(9);

		private int intValue;
		private static java.util.HashMap<Integer, TokenType> mappings;
		private static java.util.HashMap<Integer, TokenType> getMappings()
		{
			if (mappings == null)
			{
				synchronized (TokenType.class)
				{
					if (mappings == null)
					{
						mappings = new java.util.HashMap<Integer, TokenType>();
					}
				}
			}
			return mappings;
		}

		private TokenType(int value)
		{
			intValue = value;
			TokenType.getMappings().put(value, this);
		}

		public int getValue()
		{
			return intValue;
		}

		public static TokenType forValue(int value)
		{
			return getMappings().get(value);
		}
	}

	public enum TokenSubtype
	{
		Nothing,
		Start, // (
		Stop, // )
		Text, // abc
		Number, // 27
		Logical, // true false
		Error,
		Range, // x y z
		Math, // + - * / ^
		Concatenation, // &
		Intersection,
		Union;

		public int getValue()
		{
			return this.ordinal();
		}

		public static TokenSubtype forValue(int value)
		{
			return values()[value];
		}
	}
	public enum TokenCategory
	{
		Nothing,
		Product,
		Sum,
		Expon;

		public int getValue()
		{
			return this.ordinal();
		}

		public static TokenCategory forValue(int value)
		{
			return values()[value];
		}
	}
}