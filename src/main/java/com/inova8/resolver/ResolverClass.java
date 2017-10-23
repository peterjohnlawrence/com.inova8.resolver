package com.inova8.resolver;

public class ResolverClass
{
	private int intLookAhead;
	private FormulaTokenClass pLookAhead;
	private FormulaTokenClass pEOFLookAhead = new FormulaTokenClass("", FormulaTokenClass.TokenType.Noop, FormulaTokenClass.TokenSubtype.Nothing);
	private ExcelFormulaClass pExcelFormula;
	private DependentsClass pDependents = new DependentsClass();
	private NamesClass pNames = new NamesClass();
	private CellsClass pCells;
	private ConstraintsClass pConstraints = new ConstraintsClass();
	private SolutionClass pSolution;

	public ResolverClass()
	{
		pCells = new CellsClass(pNames);
	}
	public final double getMeasurementCriticalValue()
	{
		return pSolution.getMeasurementCriticalValue();
	}
	public final double getConstraintCriticalValue()
	{
		return pSolution.getConstraintCriticalValue();
	}
	public final double getGlobalCriticalValue()
	{
		return pSolution.getGlobalCriticalValue();
	}
	public final double getReconciledCost()
	{
		SolutionClass.ConvergenceClass pConvergence = pSolution.getConvergence();
		return pConvergence.getReconciledCost();
	}
	public final int getRedundancyDegree()
	{
		return pSolution.getRedundancyDegree();
	}
	public final SolutionClass getSolution()
	{
		return pSolution;
	}
	public final CellsClass getCells()
	{
		return pCells;
	}
	public final ConstraintsClass getConstraints()
	{
		return pConstraints;
	}
	public final DependentsClass getDependents()
	{
		return pDependents;
	}
	public final ResultsClass getResults()
	{
		return pSolution.getResults();
	}
	public final SolutionClass.ConvergenceClass getConvergence()
	{
		return pSolution.getConvergence();
	}

	private boolean advance()
	{
		if (pExcelFormula.getCount() > (intLookAhead + 1))
		{
			intLookAhead++;
			pLookAhead = pExcelFormula.getItem(intLookAhead);
		}
		else
		{
			intLookAhead++;
			pLookAhead = pEOFLookAhead;
		}
		return true;
	}
	private boolean start()
	{
		intLookAhead = 0;
		pLookAhead = pExcelFormula.getItem(intLookAhead);
		return true;
	}
	public final boolean Resolve(int MaximumTime, int Iterations, double Precision, double Convergence)
	{

		pSolution = new SolutionClass();
		return pSolution.Resolve(this, MaximumTime, Iterations, Precision, Convergence);
	}
	private boolean addNonNegativityConstraints(CellClass variable)
	{
		NodeClass constraint;
		constraint = new NodeClass();
		pConstraints.Add(constraint);
		constraint.setOperatorType(Constants.OperatorType.GrtEqual);
		constraint.setAddress(variable.getName() + Constants.OperatorString(Constants.OperatorType.GrtEqual) + "0");

		constraint.setLeft(new NodeClass());
		constraint.getLeft().setOperand(variable);
		constraint.getLeft().setOperatorType(Constants.OperatorType.ptrVariable);

		constraint.setRight(new NodeClass());
		constraint.getRight().setOperandString("0.0");
		constraint.getRight().setOperatorType(Constants.OperatorType.Real);

		return true;
	}
	public final boolean addName(String Name, String Address)
	{
		pNames.Add(Address, Name);
		return true;
	}

	public final boolean addVariable(String label, String initial, String measurement, String tolerance, boolean NonNegative)
	{
		CellClass variable;
		Double init = null;
		Double meas = null;
		Double tol = null;
		if (initial.trim().length() != 0)
		{
			init = Double.parseDouble(initial.trim());
		}
		if (measurement.trim().length() != 0)
		{
			meas = Double.parseDouble(measurement.trim());
		}
		if (tolerance.trim().length() != 0)
		{
			tol = Double.parseDouble(tolerance.trim());
		}

		variable = getCells().AddMeasurement(pNames.Key(label), init, meas, tol);
		if (NonNegative && (variable != null))
		{
			addNonNegativityConstraints(variable);
		}

		return true;
	}
	public final boolean addDependent(String label, String Dependent)
	{
		String address = pNames.Key(label.trim());

		pExcelFormula = new ExcelFormulaClass(Dependent);
		start();

		NodeClass expr = this.expression();
		pDependents.Add(expr);
		pCells.AddAddress(address, expr);
		expr.setAddress(pNames.Label(address));

		return true;
	}
	public final boolean addConstraint(String lblReference, String txtReference, String txtEquality, String lblConstraintReference, String txtConstraintReference)
	{

		NodeClass constraint;
		NodeClass expr;

		String lblReferenceTrimmed = pNames.Key(lblReference.trim());
		String txtEqualityTrimmed = txtEquality.trim();
		String lblConstraintReferenceTrimmed = pNames.Key(lblConstraintReference.trim());

		constraint = new NodeClass();
		pConstraints.Add(constraint);

		constraint.setOperatorType(Constants.StringEquality(txtEqualityTrimmed));

		constraint.setAddress(pNames.Label(lblReferenceTrimmed) + txtEqualityTrimmed + pNames.Label(lblConstraintReferenceTrimmed));

		pExcelFormula = new ExcelFormulaClass(txtReference);
		start();
		expr = expression();
		expr.setAddress(lblReferenceTrimmed);
		pDependents.Add(expr);

		constraint.setLeft(new NodeClass());
		constraint.getLeft().setOperand(pCells.AddAddress(lblReferenceTrimmed, expr));
		constraint.getLeft().setOperatorType(Constants.OperatorType.ptrExpression);

		pExcelFormula = new ExcelFormulaClass(txtConstraintReference);
		start();
		expr = expression();
		expr.setAddress(lblConstraintReferenceTrimmed);
		pDependents.Add(expr);

		constraint.setRight(new NodeClass());
		constraint.getRight().setOperand(pCells.AddAddress(lblConstraintReferenceTrimmed, expr));
		constraint.getRight().setOperatorType(Constants.OperatorType.ptrExpression);

		return true;
	}
	private NodeClass expression()
	{
		//expression
		//  : sumExpr // boolExpr
		//  ;
		NodeClass expression;
		expression = sumExpr();
		return expression;
	}
	private NodeClass sumExpr()
	{
		//sumExpr
		//    : productExpr ((SUB | ADD)^ productExpr)*
		//    ;

		NodeClass sumExpr;
		sumExpr = productExpr();
		while (pLookAhead.getCategory() == FormulaTokenClass.TokenCategory.Sum)
		{
			NodeClass leftNode;
			NodeClass rightNode;

			leftNode = sumExpr;
			sumExpr = new NodeClass();
			sumExpr.pToken = pLookAhead;
			if (pLookAhead.getValue().equals(Constants.OperatorString(Constants.OperatorType.Plus)))
			{
				sumExpr.setOperatorType(Constants.OperatorType.Plus);
			}
			else if (pLookAhead.getValue().equals(Constants.OperatorString(Constants.OperatorType.Minus)))
			{
				sumExpr.setOperatorType(Constants.OperatorType.Minus);
			}
			advance();
			rightNode = productExpr();
			sumExpr.setRight(rightNode);
			sumExpr.setLeft(leftNode);
		}
		return sumExpr;
	}
	private NodeClass productExpr()
	{
		//productExpr
		//     : expExpr ((DIV | MULT)^ expExpr)*
		//     ;

		NodeClass productExpr;
		productExpr = expExpr();
		while (pLookAhead.getCategory() == FormulaTokenClass.TokenCategory.Product)
		{
			NodeClass leftNode;
			NodeClass rightNode;

			leftNode = productExpr;
			productExpr = new NodeClass();
			productExpr.pToken = pLookAhead;
			if (pLookAhead.getValue().equals(Constants.OperatorString(Constants.OperatorType.Multiply)))
			{
				productExpr.setOperatorType(Constants.OperatorType.Multiply);
			}
			else if (pLookAhead.getValue().equals(Constants.OperatorString(Constants.OperatorType.Divide)))
			{
				productExpr.setOperatorType(Constants.OperatorType.Divide);
			}
			advance();
			rightNode = expExpr();
			productExpr.setRight(rightNode);
			productExpr.setLeft(leftNode);
		}
		return productExpr;
	}
	private NodeClass expExpr()
	{
		//expExpr
		//    : unaryOperation (EXP^ unaryOperation)*
		//    ;

		NodeClass expExpr;
		expExpr = unaryOperation();
		while (pLookAhead.getCategory() == FormulaTokenClass.TokenCategory.Expon)
		{
			NodeClass leftNode;
			NodeClass rightNode;

			leftNode = expExpr;
			expExpr = new NodeClass();
			expExpr.pToken = pLookAhead;
			expExpr.setOperatorType(Constants.OperatorType.Expon);
			advance();
			rightNode = unaryOperation();
			expExpr.setRight(rightNode);
			expExpr.setLeft(leftNode);
		}
		return expExpr;
	}
	private NodeClass unaryOperation()
	{
		// unaryOperation
		//     : NOT^ operand
		//     | ADD o=operand -> ^(POS $o)
		//     | SUB o=operand -> ^(NEG $o)
		//     | operand
		//     ;
		NodeClass unaryOperation;
		if (pLookAhead.getType() == FormulaTokenClass.TokenType.OperatorPrefix)
		{
			unaryOperation = new NodeClass();
			unaryOperation.pToken = pLookAhead;
			if (pLookAhead.getValue().equals(Constants.OperatorString(Constants.OperatorType.UnaryPlus)))
			{
				unaryOperation.setOperatorType(Constants.OperatorType.UnaryPlus);
			}
			else if (pLookAhead.getValue().equals(Constants.OperatorString(Constants.OperatorType.UnaryMinus)))
			{
				unaryOperation.setOperatorType(Constants.OperatorType.UnaryMinus);
			}
			advance();
			unaryOperation.setLeft(operand());

		}
		else
		{
			unaryOperation = operand();
		}
		return unaryOperation;
	}
	private NodeClass operand()
	{
		//operand
		//    : literal 
		//    | functionExpr -> ^(CALL functionExpr)
		//    | percent
		//    | VARIABLE
		//    | LPAREN expression RPAREN -> ^(expression)
		//    ;

		NodeClass operand;

		if (pLookAhead.getType() == FormulaTokenClass.TokenType.Operand && ((pLookAhead.getSubtype() == FormulaTokenClass.TokenSubtype.Number)))
		{
			operand = literal();
		}
		else if (pLookAhead.getType() == FormulaTokenClass.TokenType.Function)
		{
			operand = functionExpr();
		}
		else if (pLookAhead.getType() == FormulaTokenClass.TokenType.OperatorPostfix)
		{
			operand = percent();
		}
		else if (pLookAhead.getType() == FormulaTokenClass.TokenType.Operand && ((pLookAhead.getSubtype() == FormulaTokenClass.TokenSubtype.Range)))
		{

			//detect multivariable ranges
			java.util.ArrayList<String> ranges = ExpandRange(pNames.Key(pLookAhead.getValue())); //pLookAhead.Value);

			if (ranges.size() == 1)
			{
				operand = new NodeClass();
				operand.pToken = pLookAhead;
				operand.setOperatorType(Constants.OperatorType.ptrVariable);

				operand.setOperand(pCells.AddAddress(ranges.get(0)));
			}
			else
			{
				operand = new NodeClass();
				operand.pToken = pLookAhead;
				operand.setOperatorType(Constants.OperatorType.Range);

				NodeClass addVariable = new NodeClass();
				addVariable.pToken = null;
				addVariable.setOperatorType(Constants.OperatorType.ptrVariable);
				addVariable.setOperand(pCells.AddAddress(ranges.get(0)));
				operand.setLeft(addVariable);

				NodeClass listNode = operand;
				for (int index = 1; index < ranges.size(); index++)
				{
					String range = ranges.get(index);
					NodeClass list = new NodeClass();
					list.pToken = null;
					list.setOperatorType(Constants.OperatorType.ListSeparator);

					listNode.setRight(list);

					addVariable = new NodeClass();
					addVariable.pToken = null;
					addVariable.setOperatorType(Constants.OperatorType.ptrVariable);
					addVariable.setOperand(pCells.AddAddress(range));
					list.setLeft(addVariable);

					listNode = list;
				}
				listNode.setRight(null);
			}

			advance();
		}
		else if (pLookAhead.getType() == FormulaTokenClass.TokenType.Subexpression)
		{
			advance();
			operand = expression();
			advance();
		}
		else
		{
			operand = null;
		}
		return operand;
	}
	private NodeClass functionExpr()
	{
		//functionExpr
		//    : FUNCNAME LPAREN! (expression (COMMA! expression)*)? RPAREN!
		//    ;

		NodeClass functionExpr;
		NodeClass functionArg;
		Constants.FunctionType function = Constants.StringFunction(pLookAhead.getValue());
		;

		functionExpr = new NodeClass();
		functionExpr.pToken = pLookAhead;
		functionExpr.setOperatorType(Constants.OperatorType.Function); // Check for string or boolean
		functionExpr.setOperandString(pLookAhead.getValue());
		functionExpr.setFunction(function);
		advance();
		if (pLookAhead.getSubtype() != FormulaTokenClass.TokenSubtype.Stop) //at least it has arguments
		{
			functionExpr.setLeft(expression());
			while (pLookAhead.getSubtype() != FormulaTokenClass.TokenSubtype.Stop)
			{
				//advance();
				functionArg = new NodeClass();
				functionArg.pToken = pLookAhead;
				functionArg.setOperatorType(Constants.OperatorType.ListSeparator); // Check for string or boolean
				functionArg.setOperandString(pLookAhead.getValue());
				functionExpr.setRight(functionArg);
				advance();
				functionArg.setLeft(expression());
				functionArg.setRight(null); //assume it is the end of the list
			}
			advance();
		}
		return functionExpr;
	}
	private NodeClass literal()
	{
		//literal
		//    : NUMBER 
		//    | STRING 
		//    | TRUE
		//    | FALSE
		//    ;

		NodeClass literal;
		literal = new NodeClass();
		literal.pToken = pLookAhead;
		literal.setOperatorType(Constants.OperatorType.Real); // Check for string or boolean
		literal.setOperandString(pLookAhead.getValue());
		advance();
		return literal;
	}

	private NodeClass percent()
	{
		//percent
		//    : NUMBER PERCENT^
		//    ;

		NodeClass percent;
		percent = new NodeClass();
		percent.pToken = pLookAhead;
		percent.setOperatorType(Constants.OperatorType.Real); // Check for string or boolean
		percent.setOperandString(pLookAhead.getValue());
		advance();
		return percent;
	}
	private static java.util.ArrayList<String> ExpandRange(String value)
	{
		java.util.ArrayList<String> pRange = new java.util.ArrayList<String>();
		String[] pLimits = new String[2];
		String[] pCells = new String[2];
		int pStartRow;
		int pStartCol;
		int pEndRow;
		int pEndCol;


		if (value.contains(":"))
		{
			pLimits = value.split("[:]", -1);
			pCells = pLimits[0].split("[$]", -1);
			pStartRow = Short.parseShort(pCells[2]);
			pStartCol = ConvertToNumber(pCells[1]);
			pCells = pLimits[1].split("[$]", -1);
			pEndRow = Short.parseShort(pCells[2]);
			pEndCol = ConvertToNumber(pCells[1]);

			for (int pRow = pStartRow; pRow <= pEndRow; pRow++)
			{
				for (int pCol = pStartCol; pCol <= pEndCol; pCol++)
				{
					pRange.add("$" + ConvertToLetter(pCol) + "$" + String.valueOf(pRow));
				}
			}
		}
		else
		{
			pRange.add(value);
		}
		return pRange;
	}
	private static String ConvertToLetter(int iCol)
	{
		String pConvertToLetter = "";
		int iAlpha = (int)Math.floor(iCol / 27.0);
		int iRemainder = iCol - (iAlpha * 26);
		if (iAlpha > 0)
		{
			pConvertToLetter += (char)(iAlpha + 64);
		}
		if (iRemainder > 0)
		{
			pConvertToLetter = pConvertToLetter + (char)(iRemainder + 64);
		}
		return pConvertToLetter;
	}
	private static int ConvertToNumber(String iCol)
	{
		String sChars = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int pConvertToNumber;
		pConvertToNumber = 1;

		for (int i = 0; i < iCol.length(); i++)
		{
			pConvertToNumber *= sChars.indexOf(iCol.charAt(i));
		}
		return pConvertToNumber;
	}
}