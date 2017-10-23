package com.inova8.resolver;

public class Constants
{

	public static int NextGeneratedLabel = 0;

	protected static double ETA = 0.0001;
	protected static double SMALLVALUE = 1e-20;
	protected static double LARGEVALUE = 100000000.0;
	protected static double ACCURACY = 0.001;

	public static final String OpenParen = "(";
	public static final String CloseParen = ")";
	public static final String OpenBracket = "[";
	public static final String CloseBracket = "]";
	public static final String OpenBrace = "{";
	public static final String CloseBrace = "}";
	public static final String Comma = ",";
	public static final String Equal = "=";
	public static final String Grt = ">";
	public static final String GrtEqual = ">=";
	public static final String Less = "<";
	public static final String LessEqual = "<=";

	 public static String EqualityString(OperatorType Equality)
	 {
		switch (Equality)
		{
			case Equal:
				return Equal;
			case Grt:
				return Grt;
			case GrtEqual:
				return GrtEqual;
			case Less:
				return Less;
			case LessEqual:
				return LessEqual;
			default:
				return "=";
		}
	 }

	 public static OperatorType StringEquality(String Equality)
	 {
		 if (Equality.equals(Constants.Equal))
		 {
			 return Constants.OperatorType.Equal;
		 }
		 else if (Equality.equals(Constants.Grt))
		 {
			 return Constants.OperatorType.Grt;
		 }
		 else if (Equality.equals(Constants.GrtEqual))
		 {
			 return Constants.OperatorType.GrtEqual;
		 }
		 else if (Equality.equals(Constants.Less))
		 {
			 return Constants.OperatorType.Less;
		 }
		 else if (Equality.equals(Constants.LessEqual))
		 {
			 return Constants.OperatorType.LessEqual;
		 }
		 else
		 {
			 return Constants.OperatorType.Equal;
		 }
	 }
	public enum OperatorType
	{
		NullType(0),
		StringType(1),
		Real(2),
		Equal(3),
		Grt(4),
		GrtEqual(5),
		Less(6),
		LessEqual(7),
		Minus(8),
		Plus(9),
		Multiply(10),
		Divide(11),
		Expon(12),
		UnaryMinus(13),
		UnaryPlus(14),
		Nested(15),
		Identifier(16),
		ptrVariable(17),
		ListHead(18),
		ListSeparator(19),
		EmptyList(20),
		ptrExpression(21),
		Function(22),
		Range(23);

		private int intValue;
		private static java.util.HashMap<Integer, OperatorType> mappings;
		private static java.util.HashMap<Integer, OperatorType> getMappings()
		{
			if (mappings == null)
			{
				synchronized (OperatorType.class)
				{
					if (mappings == null)
					{
						mappings = new java.util.HashMap<Integer, OperatorType>();
					}
				}
			}
			return mappings;
		}

		private OperatorType(int value)
		{
			intValue = value;
			OperatorType.getMappings().put(value, this);
		}

		public int getValue()
		{
			return intValue;
		}

		public static OperatorType forValue(int value)
		{
			return getMappings().get(value);
		}
	}
	public enum FunctionType
	{
		NULL, // Used for initialization
		ABS, //     Returns the absolute value of a number
		ACOS, //    Returns the arccosine of a number
		ACOSH, //    Returns the inverse hyperbolic cosine of a number
		ASIN, //    Returns the arcsine of a number
		ASINH, //    Returns the inverse hyperbolic sine of a number
		ATAN, //    Returns the arctangent of a number
		ATAN2, //    Returns the arctangent from x- and y-coordinates
		ATANH, //    Returns the inverse hyperbolic tangent of a number
		CEILING, //    Rounds a number to the nearest integer or to the nearest multiple of significance
		COMBIN, //    Returns the number of combinations for a given number of objects
		COS, //    Returns the cosine of a number
		COSH, //    Returns the hyperbolic cosine of a number
		DEGREES, //    Converts radians to degrees
		EVEN, //    Rounds a number up to the nearest even integer
		EXP, //    Returns e raised to the power of a given number
		FACT, //    Returns the factorial of a number
		FACTDOUBLE, //    Returns the double factorial of a number
		FLOOR, //    Rounds a number down, toward zero
		GCD, //    Returns the greatest common divisor
		INT, //    Rounds a number down to the nearest integer
		LCM, //    Returns the least common multiple
		LN, //    Returns the natural logarithm of a number
		LOG, //    Returns the logarithm of a number to a specified base
		LOG10, //    Returns the base-10 logarithm of a number
		MDETERM, //    Returns the matrix determinant of an array
		MINVERSE, //    Returns the matrix inverse of an array
		MMULT, //    Returns the matrix product of two arrays
		MOD, //    Returns the remainder from division
		MROUND, //    Returns a number rounded to the desired multiple
		MULTINOMIAL, //    Returns the multinomial of a set of numbers
		ODD, //    Rounds a number up to the nearest odd integer
		PI, //    Returns the value of pi
		POWER, //    Returns the result of a number raised to a power
		PRODUCT, //    Multiplies its arguments
		QUOTIENT, //    Returns the integer portion of a division
		RADIANS, //    Converts degrees to radians
		RAND, //    Returns a random number between 0 and 1
		RANDBETWEEN, //    Returns a random number between the numbers you specify
		ROMAN, //    Converts an arabic numeral to roman, as text
		ROUND, //    Rounds a number to a specified number of digits
		ROUNDDOWN, //    Rounds a number down, toward zero
		ROUNDUP, //    Rounds a number up, away from zero
		SERIESSUM, //    Returns the sum of a power series based on the formula
		SIGN, //    Returns the sign of a number
		SIN, //    Returns the sine of the given angle
		SINH, //    Returns the hyperbolic sine of a number
		SQRT, //    Returns a positive square root
		SQRTPI, //    Returns the square root of (number * pi)
		SUBTOTAL, //    Returns a subtotal in a list or database
		SUM, //    Adds its arguments
		SUMIF, //    Adds the cells specified by a given criteria
		SUMIFS, //    Adds the cells in a range that meet multiple criteria
		SUMPRODUCT, //    Returns the sum of the products of corresponding array components
		SUMSQ, //    Returns the sum of the squares of the arguments
		SUMX2MY2, //    Returns the sum of the difference of squares of corresponding values in two arrays
		SUMX2PY2, //    Returns the sum of the sum of squares of corresponding values in two arrays
		SUMXMY2, //    Returns the sum of squares of differences of corresponding values in two arrays
		TAN, //    Returns the tangent of a number
		TANH, //    Returns the hyperbolic tangent of a number
		TRUNC, //    Truncates a number to an integer
		USERDEF; //    Userdefined function

		public int getValue()
		{
			return this.ordinal();
		}

		public static FunctionType forValue(int value)
		{
			return values()[value];
		}
	}
	public static String FunctionString(FunctionType Function)
	{
		switch (Function)
		{
			case NULL:
				return "NULL";
			case ABS:
				return "ABS";
			case ACOS:
				return "ACOS";
			case ACOSH:
				return "ACOSH";
			case ASIN:
				return "ASIN";
			case ASINH:
				return "ASINH";
			case ATAN:
				return "ATAN";
			case ATAN2:
				return "ATAN2";
			case ATANH:
				return "ATANH";
			case CEILING:
				return "CEILING";
			case COMBIN:
				return "COMBIN";
			case COS:
				return "COS";
			case COSH:
				return "COSH";
			case DEGREES:
				return "DEGREES";
			case EVEN:
				return "EVEN";
			case EXP:
				return "EXP";
			case FACT:
				return "FACT";
			case FACTDOUBLE:
				return "FACTDOUBLE";
			case FLOOR:
				return "FLOOR";
			case GCD:
				return "GCD";
			case INT:
				return "INT";
			case LCM:
				return "LCM";
			case LN:
				return "LN";
			case LOG:
				return "LOG";
			case LOG10:
				return "LOG10";
			case MDETERM:
				return "MDETERM";
			case MINVERSE:
				return "MINVERSE";
			case MMULT:
				return "MMULT";
			case MOD:
				return "MOD";
			case MROUND:
				return "MROUND";
			case MULTINOMIAL:
				return "MULTINOMIAL";
			case ODD:
				return "ODD";
			case PI:
				return "PI";
			case POWER:
				return "POWER";
			case PRODUCT:
				return "PRODUCT";
			case QUOTIENT:
				return "QUOTIENT";
			case RADIANS:
				return "RADIANS";
			case RAND:
				return "RAND";
			case RANDBETWEEN:
				return "RANDBETWEEN";
			case ROMAN:
				return "ROMAN";
			case ROUND:
				return "ROUND";
			case ROUNDDOWN:
				return "ROUNDDOWN";
			case ROUNDUP:
				return "ROUNDUP";
			case SERIESSUM:
				return "SERIESSUM";
			case SIGN:
				return "SIGN";
			case SIN:
				return "SIN";
			case SINH:
				return "SINH";
			case SQRT:
				return "SQRT";
			case SQRTPI:
				return "SQRTPI";
			case SUBTOTAL:
				return "SUBTOTAL";
			case SUM:
				return "SUM";
			case SUMIF:
				return "SUMIF";
			case SUMIFS:
				return "SUMIFS";
			case SUMPRODUCT:
				return "SUMPRODUCT";
			case SUMSQ:
				return "SUMSQ";
			case SUMX2MY2:
				return "SUMX2MY2";
			case SUMX2PY2:
				return "SUMX2PY2";
			case SUMXMY2:
				return "SUMXMY2";
			case TAN:
				return "TAN";
			case TANH:
				return "TANH";
			case TRUNC:
				return "TRUNC";
			case USERDEF:
				return "USERDEF";
			default:
				return "<unknownfunction>";
		}
	}
	public static FunctionType StringFunction(String FunctionName)
	{
		for (FunctionType fn : FunctionType.values())
		{
			if (FunctionString(fn).equals(FunctionName))
			{
				return fn;
			}
		}
		return FunctionType.NULL;
	}
	public enum LinearityType
	{
		Constant(0),
		Linear(1),
		Nonlinear(2),
		Unknown(3);

		private int intValue;
		private static java.util.HashMap<Integer, LinearityType> mappings;
		private static java.util.HashMap<Integer, LinearityType> getMappings()
		{
			if (mappings == null)
			{
				synchronized (LinearityType.class)
				{
					if (mappings == null)
					{
						mappings = new java.util.HashMap<Integer, LinearityType>();
					}
				}
			}
			return mappings;
		}

		private LinearityType(int value)
		{
			intValue = value;
			LinearityType.getMappings().put(value, this);
		}

		public int getValue()
		{
			return intValue;
		}

		public static LinearityType forValue(int value)
		{
			return getMappings().get(value);
		}
	}
	public enum SolvabilityType
	{
		Observable(0),
		Unobservable(1),
		Redundant(2),
		Determined(3),
		Fixed(4);

		private int intValue;
		private static java.util.HashMap<Integer, SolvabilityType> mappings;
		private static java.util.HashMap<Integer, SolvabilityType> getMappings()
		{
			if (mappings == null)
			{
				synchronized (SolvabilityType.class)
				{
					if (mappings == null)
					{
						mappings = new java.util.HashMap<Integer, SolvabilityType>();
					}
				}
			}
			return mappings;
		}

		private SolvabilityType(int value)
		{
			intValue = value;
			SolvabilityType.getMappings().put(value, this);
		}

		public int getValue()
		{
			return intValue;
		}

		public static SolvabilityType forValue(int value)
		{
			return getMappings().get(value);
		}
	}

	public static String SolvableString(SolvabilityType Solvability)
	{
		//System.String SolvableString = "";
		switch (Solvability)
		{
			case Observable:
				return "Observable";
			case Unobservable:
				return "Unobservable";
			case Redundant:
				return "Redundant";
			case Determined:
				return "Determined";
			case Fixed:
				return "Fixed";
			default:
				return "";
		}
	}
	public static String OperatorString(OperatorType Operator)
	{
		switch (Operator)
		{
			case NullType:
				return "<EOF>";
			case StringType:
				return "<string>";
			case Real:
				return "<real>";
			case Equal:
				return "=";
			case Grt:
				return ">";
			case GrtEqual:
				return ">=";
			case Less:
				return "<";
			case LessEqual:
				return "<=";
			case Minus:
				return "-";
			case Plus:
				return "+";
			case Multiply:
				return "*";
			case Divide:
				return "/";
			case Expon:
				return "^";
			case UnaryMinus:
				return "-";
			case UnaryPlus:
				return "+";
			case Nested:
				return "<nested>";
			case Identifier:
				return "<id>";
			case ptrVariable:
				return "<variable>";
			case ListHead:
				return "<list>";
			case ListSeparator:
				return ",";
			case EmptyList:
				return "<empty>";
			case ptrExpression:
				return "<expression>";
			case Function:
				return "<function>";
			case Range:
				return "<range>";
			default:
				return "NULL";
		}
	}
	public static void main()
	{
		NextGeneratedLabel = 1;
	}

}