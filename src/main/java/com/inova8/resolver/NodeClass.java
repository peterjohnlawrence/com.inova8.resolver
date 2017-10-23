package com.inova8.resolver;

public class NodeClass
{
	public FormulaTokenClass pToken;
	private String pAddress = "";
	private NodeClass pLeft = null;
	private NodeClass pRight = null;
	private CellClass pOperand = null;
	private String strOperand = "";
	private Constants.OperatorType pOperatorType = Constants.OperatorType.NullType;
	private Constants.LinearityType pLinearity = Constants.LinearityType.Unknown;
	private Constants.FunctionType pFunction = Constants.FunctionType.NULL;
	//protected Constants.FunctionType lastFunctionContext = Constants.FunctionType.NULL;
	private int pLinearOperatorCount;
	private int pNonlinearOperatorCount;
	private int pVariableCount;
	private UserFunctions userFunctions = new UserFunctions();
	public final Constants.LinearityType getLinearity()
	{
		Constants.LinearityType getLinearity = Constants.LinearityType.forValue(0);
			//LinearType
		if (pLinearity == Constants.LinearityType.Unknown)
		{
			pLinearity = DeduceLinearity();
		}
		getLinearity = pLinearity;
		return getLinearity;
	}
	public final int getLinearOperatorCount()
	{
		return pLinearOperatorCount;
	}
	public final int getNonlinearOperatorCount()
	{
		return pNonlinearOperatorCount;
	}
	public final int getVariableCount()
	{
		return pVariableCount;
	}
	public final String getAddress()
	{
		return pAddress;
	}
	public final void setAddress(String value)
	{
		pAddress = value;
	}

	public final NodeClass getLeft()
	{
		return pLeft;
	}
	public final void setLeft(NodeClass value)
	{
		pLeft = value;
	}

	public final NodeClass getRight()
	{
		return pRight;
	}
	public final void setRight(NodeClass value)
	{
		pRight = value;
	}

	public final CellClass getOperand()
	{
		return pOperand;
	}
	public final void setOperand(CellClass value)
	{
		pOperand = value;
	}

	public final String getOperandString()
	{
		return String.valueOf(pOperand);
	}
	public final void setOperandString(String value)
	{
		strOperand = value;
	}

	public final Constants.OperatorType getOperatorType()
	{
		return pOperatorType;
	}
	public final void setOperatorType(Constants.OperatorType value)
	{
		pOperatorType = value;
	}
	public final Constants.FunctionType getFunction()
	{
		return pFunction;
	}
	public final void setFunction(Constants.FunctionType value)
	{
		pFunction = value;
	}
	public NodeClass()
	{
		pAddress = String.valueOf(-Constants.NextGeneratedLabel);
		Constants.NextGeneratedLabel = Constants.NextGeneratedLabel + 1;
		pRight = null;
		pLeft = null;
		pOperand = null;
		pOperatorType = Constants.OperatorType.NullType; // 0;
		pLinearity = Constants.LinearityType.Unknown;
	}
	public double Evaluate(Constants.FunctionType functionContext)
	{
		double Evaluate = 0.0;
		double dLeftResidual = 0.0;
		double dRightResidual = 0.0;
		CellClass tVariable = null;
		Constants.FunctionType lastFunctionContext = Constants.FunctionType.NULL;

		//virtual void pushFunctionContext(){
		//}
		//virtual void popFunctionContext(){
		//}

		switch (pOperatorType)
		{
			case Real:
				Evaluate = Double.parseDouble(strOperand);
				return Evaluate;
			case ptrVariable:
				tVariable = pOperand;
				Evaluate = tVariable.getReconciledVariable().getValue();
				return Evaluate;
			case ptrExpression:
				tVariable = pOperand;
				Evaluate = tVariable.getNode().Evaluate(functionContext);
				return Evaluate;
			case UnaryMinus:
				Evaluate = -pLeft.Evaluate(functionContext);
				return Evaluate;
			case UnaryPlus:
				Evaluate = pLeft.Evaluate(functionContext);
				return Evaluate;
			case Plus:
				Evaluate = pLeft.Evaluate(functionContext) + pRight.Evaluate(functionContext);
				return Evaluate;
			case Minus:
				Evaluate = pLeft.Evaluate(functionContext) - pRight.Evaluate(functionContext);
				return Evaluate;
			case Multiply:
				Evaluate = pLeft.Evaluate(functionContext) * pRight.Evaluate(functionContext);
				return Evaluate;
			case Divide:
				dLeftResidual = pLeft.Evaluate(functionContext);
				dRightResidual = pRight.Evaluate(functionContext);
				if (Math.abs(dRightResidual) > Constants.ETA)
				{
					Evaluate = dLeftResidual / dRightResidual;
					return Evaluate;
				}
				else
				{
					Evaluate = Constants.LARGEVALUE * (int)Math.signum((int)Math.signum(dLeftResidual) * (int)Math.signum(dRightResidual));
					return Evaluate;
				}
			case Expon:
				Evaluate = Math.pow(pLeft.Evaluate(functionContext), pRight.Evaluate(functionContext));
				return Evaluate;
			case Equal:
				Evaluate = pLeft.Evaluate(functionContext) - pRight.Evaluate(functionContext);
				return Evaluate;
			case Grt:
				Evaluate = pLeft.Evaluate(functionContext) - pRight.Evaluate(functionContext);
				return Evaluate;
			case GrtEqual:
				Evaluate = pLeft.Evaluate(functionContext) - pRight.Evaluate(functionContext);
				return Evaluate;
			case Less:
				Evaluate = -pLeft.Evaluate(functionContext) + pRight.Evaluate(functionContext);
				return Evaluate;
			case LessEqual:
				Evaluate = -pLeft.Evaluate(functionContext) + pRight.Evaluate(functionContext);
				return Evaluate;
			case Range:
			case ListSeparator:
				switch (functionContext)
				{
					case SUM:
						Evaluate = userFunctions.SUM_Evaluate(this, functionContext);
						return Evaluate;
					case PRODUCT:
						Evaluate = userFunctions.PRODUCT_Evaluate(this, functionContext);
						return Evaluate;
					case USERDEF:
						Evaluate = userFunctions.USERDEF_Evaluate(this, functionContext);
						return Evaluate;
					default:
						Evaluate = -12121212;
						return Evaluate;
				}
			case Function:
				lastFunctionContext = functionContext;
				functionContext = this.getFunction();
				switch (functionContext)
				{
					case SUM:
						Evaluate = userFunctions.SUM_Evaluate(this, functionContext);
						functionContext = lastFunctionContext;
						return Evaluate;
					case PRODUCT:
						Evaluate = userFunctions.PRODUCT_Evaluate(this, functionContext);
						functionContext = lastFunctionContext;
						return Evaluate;
					case USERDEF:
						Evaluate = userFunctions.USERDEF_Evaluate(this, functionContext);
						functionContext = lastFunctionContext;
						return Evaluate;
					default:
						Evaluate = -12121212;
						functionContext = lastFunctionContext;
						return Evaluate;
				}

			default:
				return -1212121212;
		}
	}
	public double EvaluateResidual(Constants.FunctionType functionContext)
	{
		double EvaluateResidual = 0.0;
		double dLeftResidual = 0.0;
		double dRightResidual = 0.0;
		CellClass tVariable = null;
		Constants.FunctionType lastFunctionContext = Constants.FunctionType.NULL;

		switch (pOperatorType)
		{
			case Real:
				EvaluateResidual = Double.parseDouble(strOperand);
				return EvaluateResidual;
			case ptrVariable:
				tVariable = pOperand;
				if (tVariable.gethasMeasurement())
				{
					//EvaluateResidual = tVariable.Expression.Left.EvaluateResidual 'removed by PJL to ensure that residual errors reflect edits
					EvaluateResidual = tVariable.getReconciledVariable().getInitialValue();
				}
				else
				{
					//if (tVariable.ReconciledVariable.MeasurementTolerance > (SolutionClass.REC_LARGEVALUE / 10))
					if (!tVariable.getReconciledVariable().getCell().gethasMeasurement())
					{
						EvaluateResidual = tVariable.getReconciledVariable().getValue();
					}
					else
					{
						EvaluateResidual = tVariable.getReconciledVariable().getInitialValue();
					}
				}
				return EvaluateResidual;
			case ptrExpression:
				return pOperand.getNode().EvaluateResidual(functionContext);
			case UnaryMinus:
				EvaluateResidual = -pLeft.EvaluateResidual(functionContext);
				return EvaluateResidual;
			case UnaryPlus:
				EvaluateResidual = pLeft.EvaluateResidual(functionContext);
				return EvaluateResidual;
			case Plus:
				EvaluateResidual = pLeft.EvaluateResidual(functionContext) + pRight.EvaluateResidual(functionContext);
				return EvaluateResidual;
			case Minus:
				EvaluateResidual = pLeft.EvaluateResidual(functionContext) - pRight.EvaluateResidual(functionContext);
				return EvaluateResidual;
			case Multiply:
				EvaluateResidual = pLeft.EvaluateResidual(functionContext) * pRight.EvaluateResidual(functionContext);
				return EvaluateResidual;
			case Divide:
				dLeftResidual = pLeft.EvaluateResidual(functionContext);
				dRightResidual = pRight.EvaluateResidual(functionContext);
				if (Math.abs(dRightResidual) > Constants.ETA)
				{
					EvaluateResidual = dLeftResidual / dRightResidual;
					return EvaluateResidual;
				}
				else
				{
					EvaluateResidual = Constants.LARGEVALUE * (int)Math.signum((int)Math.signum(dLeftResidual) * (int)Math.signum(dRightResidual));
					return EvaluateResidual;
				}
			case Expon:
				EvaluateResidual = Math.pow(pLeft.EvaluateResidual(functionContext), pRight.EvaluateResidual(functionContext));
				return EvaluateResidual;
			case Equal:
				EvaluateResidual = pLeft.EvaluateResidual(functionContext) - pRight.EvaluateResidual(functionContext);
				return EvaluateResidual;
			case Grt:
				EvaluateResidual = pLeft.EvaluateResidual(functionContext) - pRight.EvaluateResidual(functionContext);
				return EvaluateResidual;
			case GrtEqual:
				EvaluateResidual = pLeft.EvaluateResidual(functionContext) - pRight.EvaluateResidual(functionContext);
				return EvaluateResidual;
			case Less:
				EvaluateResidual = -pLeft.EvaluateResidual(functionContext) + pRight.EvaluateResidual(functionContext);
				return EvaluateResidual;
			case LessEqual:
				EvaluateResidual = -pLeft.EvaluateResidual(functionContext) + pRight.EvaluateResidual(functionContext);
				return EvaluateResidual;
			case Range:
			case ListSeparator:
				switch (functionContext)
				{
					case SUM:
						EvaluateResidual = userFunctions.SUM_EvaluateResidual(this, functionContext);
						return EvaluateResidual;
					case PRODUCT:
						EvaluateResidual = userFunctions.PRODUCT_EvaluateResidual(this, functionContext);
						return EvaluateResidual;
					case USERDEF:
						EvaluateResidual = userFunctions.USERDEF_EvaluateResidual(this, functionContext);
						return EvaluateResidual;
					default:
						EvaluateResidual = -12121212;
						return EvaluateResidual;
				}
			case Function:
				lastFunctionContext = functionContext;
				functionContext = this.getFunction();
				switch (functionContext)
				{
					case SUM:
						EvaluateResidual = userFunctions.SUM_EvaluateResidual(this, functionContext);
						functionContext = lastFunctionContext;
						return EvaluateResidual;
					case PRODUCT:
						EvaluateResidual = userFunctions.PRODUCT_EvaluateResidual(this, functionContext);
						functionContext = lastFunctionContext;
						return EvaluateResidual;
					case USERDEF:
						EvaluateResidual = userFunctions.USERDEF_EvaluateResidual(this, functionContext);
					   functionContext = lastFunctionContext;
					   return EvaluateResidual;
					default:
						EvaluateResidual = -12121212;
						functionContext = lastFunctionContext;
						return EvaluateResidual;
				}
			default:
				EvaluateResidual = -12121212;
				return EvaluateResidual;
		}
	}
	public double Derivative(CellClass wrt, Constants.FunctionType functionContext)
	{
		double Derivative = 0.0;
		double dLeftDerivative = 0.0;
		double dRightDerivative = 0.0;
		double dLeftEvaluate = 0.0;
		double dRightEvaluate = 0.0;
		Constants.FunctionType lastFunctionContext = Constants.FunctionType.NULL;

		switch (pOperatorType)
		{
			case Real:
				Derivative = 0.0;
				return Derivative;
			case ptrVariable:
				if (pOperand == wrt)
				{
					Derivative = 1.0;
				}
				else
				{
					Derivative = 0.0;
				}
				return Derivative;
			case ptrExpression:
				return pOperand.getNode().Derivative(wrt, functionContext);
			case UnaryMinus:
				Derivative = -pLeft.Derivative(wrt, functionContext);
				return Derivative;
			case UnaryPlus:
				Derivative = pLeft.Derivative(wrt, functionContext);
				return Derivative;
			case Plus:
				Derivative = pLeft.Derivative(wrt, functionContext) + pRight.Derivative(wrt, functionContext);
				return Derivative;
			case Minus:
				Derivative = pLeft.Derivative(wrt, functionContext) - pRight.Derivative(wrt, functionContext);
				return Derivative;
			case Multiply:
				dLeftDerivative = pLeft.Derivative(wrt, functionContext);
				dRightDerivative = pRight.Derivative(wrt, functionContext);

				Derivative = (pLeft.Evaluate(Constants.FunctionType.NULL) * dRightDerivative) + (dLeftDerivative * pRight.Evaluate(Constants.FunctionType.NULL));
				return Derivative;
			case Divide:
				dRightEvaluate = pRight.Evaluate(Constants.FunctionType.NULL);
				if (Math.abs(dRightEvaluate) > Constants.ETA)
				{
					dLeftDerivative = pLeft.Derivative(wrt, functionContext);
					dRightDerivative = pRight.Derivative(wrt, functionContext);
					dLeftEvaluate = pLeft.Evaluate(Constants.FunctionType.NULL);

					Derivative = ((dLeftDerivative * dRightEvaluate) - (dLeftEvaluate * dRightDerivative)) / (dRightEvaluate * dRightEvaluate);
					return Derivative;
				}
				else
				{
					Derivative = Constants.LARGEVALUE * (int)Math.signum((int)Math.signum(dLeftEvaluate) * (int)Math.signum(dRightEvaluate));
					return Derivative;
				}
			case Expon:
				// f = x^y
				// ln(f) = y.ln(x) 
				// dln(f)/dz = 1/f.df/dz = d(y.ln(x))/dz = dy/dz.ln(x) + y/x.dx/dz
				// df/dz = f.(dy/dz.ln(x) + y/z .dx/dz) = x^y.(dy/dz.ln(x) + y/z.dx/dz)= ln(x).x^y.dy/dz + y.x^(y-1).dx/dz 
				// df/dz = ln(x).x^y.dy/dz + y.x^(y-1).dx/dz 
				dRightEvaluate = pRight.Evaluate(functionContext);
				dLeftEvaluate = pLeft.Evaluate(functionContext);
				dLeftDerivative = pRight.Derivative(wrt, functionContext);
				dLeftDerivative = pLeft.Derivative(wrt, functionContext);

				//Derivative = System.Math.Pow(dRightEvaluate * dLeftEvaluate, dRightEvaluate - 1);
				if (dLeftEvaluate > Constants.ETA)
				{
					Derivative = Math.log(dLeftEvaluate) * Math.pow(dLeftEvaluate, dRightEvaluate) * dRightDerivative + dRightEvaluate * Math.pow(dLeftEvaluate, dRightEvaluate - 1) * dLeftDerivative;
				}
				else
				{
					Derivative = 0.0;
				}
				return Derivative;
			case Equal:
				Derivative = pLeft.Derivative(wrt, functionContext) - pRight.Derivative(wrt, functionContext);
				return Derivative;
			case Grt:
				Derivative = pLeft.Derivative(wrt, functionContext) - pRight.Derivative(wrt, functionContext);
				return Derivative;
			case GrtEqual:
				Derivative = pLeft.Derivative(wrt, functionContext) - pRight.Derivative(wrt, functionContext);
				return Derivative;
			case Less:
				Derivative = -pLeft.Derivative(wrt, functionContext) + pRight.Derivative(wrt, functionContext);
				return Derivative;
			case LessEqual:
				Derivative = -pLeft.Derivative(wrt, functionContext) + pRight.Derivative(wrt, functionContext);
				return Derivative;
			case Range:
			case ListSeparator:
				switch (functionContext)
				{
					case SUM:
						Derivative = userFunctions.SUM_Derivative(this, wrt, functionContext);
						return Derivative;
					case PRODUCT:
						Derivative = userFunctions.PRODUCT_Derivative(this, wrt, functionContext);
						return Derivative;
					case USERDEF:
						Derivative = userFunctions.USERDEF_Derivative(this, wrt, functionContext);
						return Derivative;
					default:
						Derivative = -12121212;
						return Derivative;
				}
			case Function:
				lastFunctionContext = functionContext;
				functionContext = this.getFunction();
				switch (functionContext)
				{
					case SUM:
						Derivative = userFunctions.SUM_Derivative(this, wrt, functionContext);
						functionContext = lastFunctionContext;
						return Derivative;
					case PRODUCT:
						Derivative = userFunctions.PRODUCT_Derivative(this, wrt, functionContext);
						functionContext = lastFunctionContext;
						return Derivative;
					case USERDEF:
						Derivative = userFunctions.USERDEF_Derivative(this, wrt, functionContext);
						functionContext = lastFunctionContext;
						return Derivative;
					default:
						functionContext = lastFunctionContext;
						Derivative = -12121212;
						return Derivative;
				}
			default:
				Derivative = -12121212;
				return Derivative;
		}
	}
	public double DerivativeResidual(CellClass wrt, Constants.FunctionType functionContext)
	{
		double DerivativeResidual = 0.0;
		double dLeftDerivativeResidual = 0.0;
		double dRightDerivativeResidual = 0.0;
		double dLeftEvaluateResidual = 0.0;
		double dRightEvaluateResidual = 0.0;
		Constants.FunctionType lastFunctionContext = Constants.FunctionType.NULL;

		switch (pOperatorType)
		{
			case Real:
				DerivativeResidual = 0.0;
				return DerivativeResidual;
			case ptrVariable:
				if (pOperand == wrt)
				{
					DerivativeResidual = 1.0;
				}
				else
				{
					DerivativeResidual = 0.0;
				}
				return DerivativeResidual;
			case ptrExpression:
				return pOperand.getNode().DerivativeResidual(wrt, functionContext);
			case UnaryMinus:
				DerivativeResidual = -pLeft.DerivativeResidual(wrt, functionContext);
				return DerivativeResidual;
			case UnaryPlus:
				DerivativeResidual = pLeft.DerivativeResidual(wrt, functionContext);
				return DerivativeResidual;
			case Plus:
				DerivativeResidual = pLeft.DerivativeResidual(wrt, functionContext) + pRight.DerivativeResidual(wrt, functionContext);
				return DerivativeResidual;
			case Minus:
				DerivativeResidual = pLeft.DerivativeResidual(wrt, functionContext) - pRight.DerivativeResidual(wrt, functionContext);
				return DerivativeResidual;
			case Multiply:
				dLeftDerivativeResidual = pLeft.DerivativeResidual(wrt, functionContext);
				dRightDerivativeResidual = pRight.DerivativeResidual(wrt, functionContext);

				DerivativeResidual = (pLeft.EvaluateResidual(functionContext) * dRightDerivativeResidual) + (dLeftDerivativeResidual * pRight.EvaluateResidual(functionContext));
				return DerivativeResidual;
			case Divide:
				dRightEvaluateResidual = pRight.EvaluateResidual(functionContext);
				if (Math.abs(dRightEvaluateResidual) > Constants.ETA)
				{
					dLeftDerivativeResidual = pLeft.DerivativeResidual(wrt, functionContext);
					dRightDerivativeResidual = pRight.DerivativeResidual(wrt, functionContext);
					dLeftEvaluateResidual = pLeft.EvaluateResidual(functionContext);

					DerivativeResidual = ((dLeftDerivativeResidual * dRightEvaluateResidual) - (dLeftEvaluateResidual * dRightDerivativeResidual)) / (dRightEvaluateResidual * dRightEvaluateResidual);
					return DerivativeResidual;
				}
				else
				{
					DerivativeResidual = Constants.LARGEVALUE * (int)Math.signum((int)Math.signum(dLeftEvaluateResidual) * (int)Math.signum(dRightEvaluateResidual));
					return DerivativeResidual;
				}
			case Expon:
				// f = x^y
				// ln(f) = y.ln(x) 
				// dln(f)/dz = 1/f.df/dz = d(y.ln(x))/dz = dy/dz.ln(x) + y/x.dx/dz
				// df/dz = f.(dy/dz.ln(x) + y/z .dx/dz) = x^y.(dy/dz.ln(x) + y/z.dx/dz)= ln(x).x^y.dy/dz + y.x^(y-1).dx/dz 
				// df/dz = ln(x).x^y.dy/dz + y.x^(y-1).dx/dz 
				dRightEvaluateResidual = pRight.EvaluateResidual(functionContext);
				dLeftEvaluateResidual = pLeft.EvaluateResidual(functionContext);
				dLeftDerivativeResidual = pRight.DerivativeResidual(wrt, functionContext);
				dLeftDerivativeResidual = pLeft.DerivativeResidual(wrt, functionContext);
				if (dLeftEvaluateResidual > Constants.ETA)
				{
					//DerivativeResidual = System.Math.Pow(dRightEvaluateResidual * pLeft.EvaluateResidual(), dRightEvaluateResidual - 1);
					DerivativeResidual = Math.log(dLeftEvaluateResidual) * Math.pow(dLeftEvaluateResidual, dRightEvaluateResidual) * dRightDerivativeResidual + dRightEvaluateResidual * Math.pow(dLeftEvaluateResidual, dRightEvaluateResidual - 1) * dLeftDerivativeResidual;
				}
				else
				{
					DerivativeResidual = 0.0;
				}
				return DerivativeResidual;
			case Equal:
				DerivativeResidual = pLeft.DerivativeResidual(wrt, functionContext) - pRight.DerivativeResidual(wrt, functionContext);
				return DerivativeResidual;
			case Grt:
				DerivativeResidual = pLeft.DerivativeResidual(wrt, functionContext) - pRight.DerivativeResidual(wrt, functionContext);
				return DerivativeResidual;
			case GrtEqual:
				DerivativeResidual = pLeft.DerivativeResidual(wrt, functionContext) - pRight.DerivativeResidual(wrt, functionContext);
				return DerivativeResidual;
			case Less:
				DerivativeResidual = -pLeft.DerivativeResidual(wrt, functionContext) + pRight.DerivativeResidual(wrt, functionContext);
				return DerivativeResidual;
			case LessEqual:
				DerivativeResidual = -pLeft.DerivativeResidual(wrt, functionContext) + pRight.DerivativeResidual(wrt, functionContext);
				return DerivativeResidual;
			case Range:
			case ListSeparator:
				switch (functionContext)
				{
					case SUM:
						DerivativeResidual = userFunctions.SUM_DerivativeResidual(this, wrt, functionContext);
						return DerivativeResidual;
					case PRODUCT:
						DerivativeResidual = userFunctions.PRODUCT_DerivativeResidual(this, wrt, functionContext);
						return DerivativeResidual;
					case USERDEF:
						DerivativeResidual = userFunctions.USERDEF_DerivativeResidual(this, wrt, functionContext);
						return DerivativeResidual;
					default:
						DerivativeResidual = -12121212;
						return DerivativeResidual;
				}
			case Function:
				lastFunctionContext = functionContext;
				functionContext = this.getFunction();
				switch (functionContext)
				{
					case SUM:
						DerivativeResidual = userFunctions.SUM_DerivativeResidual(this, wrt, functionContext);
						functionContext = lastFunctionContext;
						return DerivativeResidual;
					case PRODUCT:
						DerivativeResidual = userFunctions.PRODUCT_DerivativeResidual(this, wrt, functionContext);
						functionContext = lastFunctionContext;
						return DerivativeResidual;
					case USERDEF:
						DerivativeResidual = userFunctions.USERDEF_DerivativeResidual(this, wrt, functionContext);
						functionContext = lastFunctionContext;
						return DerivativeResidual;
				   default:
						DerivativeResidual = -12121212;
						functionContext = lastFunctionContext;
						return DerivativeResidual;
				}

			default:
				DerivativeResidual = -12121212;
				return DerivativeResidual;
		}
	}
	public final String Serialize(boolean bVariableNames)
	{
		String Serialize = "";

		switch (pOperatorType)
		{
			case Real:
				Serialize = strOperand;
				return Serialize;
			case ptrVariable:
				if (bVariableNames)
				{
					Serialize = pOperand.getName(); // Address;
				}
				else
				{
					Serialize = (new Double(pOperand.getReconciledVariable().getValue())).toString();
				}
				return Serialize;
			case ptrExpression:
				Serialize = Constants.OpenBracket + pOperand.getNode().Serialize(bVariableNames) + Constants.CloseBracket;
				return Serialize;
			case UnaryMinus:
				Serialize = Constants.OperatorString(Constants.OperatorType.UnaryMinus) + pLeft.Serialize(bVariableNames);
				return Serialize;
			case UnaryPlus:
				Serialize = Constants.OperatorString(Constants.OperatorType.UnaryPlus) + pLeft.Serialize(bVariableNames);
				return Serialize;
			case Plus:
				Serialize = Constants.OpenParen + pLeft.Serialize(bVariableNames) + Constants.OperatorString(Constants.OperatorType.Plus) + pRight.Serialize(bVariableNames) + Constants.CloseParen;
				return Serialize;
			case Minus:
				Serialize = Constants.OpenParen + pLeft.Serialize(bVariableNames) + Constants.OperatorString(Constants.OperatorType.Minus) + pRight.Serialize(bVariableNames) + Constants.CloseParen;
				return Serialize;
			case Multiply:
				Serialize = Constants.OpenParen + pLeft.Serialize(bVariableNames) + Constants.OperatorString(Constants.OperatorType.Multiply) + pRight.Serialize(bVariableNames) + Constants.CloseParen;
				return Serialize;
			case Divide:
				Serialize = Constants.OpenParen + pLeft.Serialize(bVariableNames) + Constants.OperatorString(Constants.OperatorType.Divide) + pRight.Serialize(bVariableNames) + Constants.CloseParen;
				return Serialize;
			case Expon:
				Serialize = Constants.OpenParen + pLeft.Serialize(bVariableNames) + Constants.OperatorString(Constants.OperatorType.Expon) + pRight.Serialize(bVariableNames) + Constants.CloseParen;
				return Serialize;
			case Equal:
				Serialize = Constants.OpenParen + pLeft.Serialize(bVariableNames) + Constants.OperatorString(Constants.OperatorType.Equal) + pRight.Serialize(bVariableNames) + Constants.CloseParen;
				return Serialize;
			case Grt:
				Serialize = Constants.OpenParen + pLeft.Serialize(bVariableNames) + Constants.OperatorString(Constants.OperatorType.Grt) + pRight.Serialize(bVariableNames) + Constants.CloseParen;
				return Serialize;
			case GrtEqual:
				Serialize = Constants.OpenParen + pLeft.Serialize(bVariableNames) + Constants.OperatorString(Constants.OperatorType.GrtEqual) + pRight.Serialize(bVariableNames) + Constants.CloseParen;
				return Serialize;
			case Less:
				Serialize = Constants.OpenParen + pLeft.Serialize(bVariableNames) + Constants.OperatorString(Constants.OperatorType.Less) + pRight.Serialize(bVariableNames) + Constants.CloseParen;
				return Serialize;
			case LessEqual:
				Serialize = Constants.OpenParen + pLeft.Serialize(bVariableNames) + Constants.OperatorString(Constants.OperatorType.LessEqual) + pRight.Serialize(bVariableNames) + Constants.CloseParen;
				return Serialize;
			case ListSeparator:
				Serialize = Constants.Comma + pLeft.Serialize(bVariableNames);
				if (pRight != null) //.OperatorType == Constants.OperatorType.ListSeparator)
				{
					Serialize += pRight.Serialize(bVariableNames);
				}
				return Serialize;
			case Function:
				Serialize = this.strOperand + Constants.OpenParen + pLeft.Serialize(bVariableNames);

				if (pRight != null) //.OperatorType == Constants.OperatorType.ListSeparator)
				{
					Serialize += pRight.Serialize(bVariableNames);
				}
				Serialize += Constants.CloseParen;
				return Serialize;
			case Range:
				Serialize = Constants.OpenBrace + pLeft.Serialize(bVariableNames);
				if (pRight != null) //.OperatorType == Constants.OperatorType.ListSeparator)
				{
					Serialize += pRight.Serialize(bVariableNames);
				}
				return Serialize + Constants.CloseBrace;
			default:
				Serialize = Constants.OperatorString(Constants.OperatorType.NullType);
				return Serialize;
		}

	}
	public final void CountOperators()
	{
		switch (pOperatorType)
		{
			case Real:
				pLinearOperatorCount = 0;
				pNonlinearOperatorCount = 0;
				pVariableCount = 0;
				return;
			case ptrVariable:
				pLinearOperatorCount = 0;
				pNonlinearOperatorCount = 0;
				pVariableCount = 1;
				return;
			case ptrExpression:
				pOperand.getNode().CountOperators();
				pLinearOperatorCount = pOperand.getNode().getLinearOperatorCount();
				pNonlinearOperatorCount = pOperand.getNode().getNonlinearOperatorCount();
				pVariableCount = pOperand.getNode().getVariableCount();
				return;
			case UnaryMinus:
				pLeft.CountOperators();
				pLinearOperatorCount = pLeft.getLinearOperatorCount();
				pNonlinearOperatorCount = pLeft.getNonlinearOperatorCount();
				pVariableCount = pLeft.getVariableCount();
				return;
			case UnaryPlus:
				pLeft.CountOperators();
				pLinearOperatorCount = pLeft.getLinearOperatorCount();
				pNonlinearOperatorCount = pLeft.getNonlinearOperatorCount();
				pVariableCount = pLeft.getVariableCount();
				return;
			case Plus:
			case Minus:
				pLeft.CountOperators();
				pRight.CountOperators();
				pLinearOperatorCount = pLeft.getLinearOperatorCount() + pRight.getLinearOperatorCount();
				pNonlinearOperatorCount = pLeft.getNonlinearOperatorCount() + pRight.getNonlinearOperatorCount();
				pVariableCount = pLeft.getVariableCount() + pRight.getVariableCount();
				if (pLeft.getVariableCount() > 0 && pRight.getVariableCount() > 0)
				{
					pLinearOperatorCount += 1;
				}
				return;
			case Multiply:
			case Divide:
				pLeft.CountOperators();
				pRight.CountOperators();
				pLinearOperatorCount = pLeft.getLinearOperatorCount() + pRight.getLinearOperatorCount();
				pNonlinearOperatorCount = pLeft.getNonlinearOperatorCount() + pRight.getNonlinearOperatorCount();
				pVariableCount = pLeft.getVariableCount() + pRight.getVariableCount();
				if (pLeft.getVariableCount() > 0 && pRight.getVariableCount() > 0)
				{
					pNonlinearOperatorCount += 1;
				}
				return;
			case Expon:
				pLeft.CountOperators();
				pRight.CountOperators();
				pLinearOperatorCount = pLeft.getLinearOperatorCount() + pRight.getLinearOperatorCount();
				pNonlinearOperatorCount = pLeft.getNonlinearOperatorCount() + pRight.getNonlinearOperatorCount();
				pVariableCount = pLeft.getVariableCount() + pRight.getVariableCount();
				if (pRight.getVariableCount() > 0)
				{
					pNonlinearOperatorCount += 1;
				}
				return;
			case Equal:
			case Grt:
			case GrtEqual:
			case Less:
			case LessEqual:
				pLeft.CountOperators();
				pRight.CountOperators();
				pLinearOperatorCount = pLeft.getLinearOperatorCount() + pRight.getLinearOperatorCount() + 1;
				pNonlinearOperatorCount = pLeft.getNonlinearOperatorCount() + pRight.getNonlinearOperatorCount();
				pVariableCount = pLeft.getVariableCount() + pRight.getVariableCount();
				return;
			case Range:
			case ListSeparator:
				pLeft.CountOperators();
				if (pRight != null)
				{
					pRight.CountOperators();
					pLinearOperatorCount = pLeft.getLinearOperatorCount() + pRight.getLinearOperatorCount() + 1;
					pNonlinearOperatorCount = pLeft.getNonlinearOperatorCount() + pRight.getNonlinearOperatorCount();
					pVariableCount = pLeft.getVariableCount() + pRight.getVariableCount();
				}
				else
				{
					pLinearOperatorCount = pLeft.getLinearOperatorCount() + 1;
					pNonlinearOperatorCount = pLeft.getNonlinearOperatorCount();
					pVariableCount = pLeft.getVariableCount();
				}
				return;
			case Function:
				pLeft.CountOperators();
				if (pRight != null)
				{
					pRight.CountOperators();
					pLinearOperatorCount = pLeft.getLinearOperatorCount() + pRight.getLinearOperatorCount() + 1;
					pNonlinearOperatorCount = pLeft.getNonlinearOperatorCount() + pRight.getNonlinearOperatorCount();
					pVariableCount = pLeft.getVariableCount() + pRight.getVariableCount();
				}
				else
				{
					pLinearOperatorCount = pLeft.getLinearOperatorCount() + 1;
					pNonlinearOperatorCount = pLeft.getNonlinearOperatorCount();
					pVariableCount = pLeft.getVariableCount();
				}
				return;
			default:
				return;
		}
	}
	private Constants.LinearityType DeduceLinearity()
	{
		Constants.LinearityType DeduceLinearity = Constants.LinearityType.forValue(0);
		Constants.LinearityType dLeftLinearity = Constants.LinearityType.forValue(0);
		Constants.LinearityType dRightLinearity = Constants.LinearityType.forValue(0);

		switch (pOperatorType)
		{
			case Real:
				DeduceLinearity = Constants.LinearityType.Constant;
				return DeduceLinearity;
			case ptrVariable:
				DeduceLinearity = Constants.LinearityType.Linear;
				return DeduceLinearity;
			case ptrExpression:
				DeduceLinearity = pOperand.getNode().DeduceLinearity();
				return DeduceLinearity;
			case UnaryMinus:
				DeduceLinearity = pLeft.getLinearity();
				return DeduceLinearity;
			case UnaryPlus:
				DeduceLinearity = pLeft.getLinearity();
				return DeduceLinearity;
			case Plus:
			case Minus:
				dLeftLinearity = pLeft.getLinearity();
				dRightLinearity = pRight.getLinearity();
				if ((dLeftLinearity == Constants.LinearityType.Nonlinear) || (dRightLinearity == Constants.LinearityType.Nonlinear))
				{
					DeduceLinearity = Constants.LinearityType.Nonlinear;
				}
				else
				{
					if ((dLeftLinearity == Constants.LinearityType.Linear) || (dRightLinearity == Constants.LinearityType.Linear))
					{
						DeduceLinearity = Constants.LinearityType.Linear;
					}
					else
					{
						DeduceLinearity = Constants.LinearityType.Constant;
					}
				}
				return DeduceLinearity;
			case Multiply:
			case Divide:
			case Expon:
				dLeftLinearity = pLeft.getLinearity();
				dRightLinearity = pRight.getLinearity();
				if ((dLeftLinearity == Constants.LinearityType.Nonlinear) || (dRightLinearity == Constants.LinearityType.Nonlinear))
				{
					DeduceLinearity = Constants.LinearityType.Nonlinear;
				}
				else
				{
					if ((dLeftLinearity == Constants.LinearityType.Linear) && (dRightLinearity == Constants.LinearityType.Linear))
					{
						DeduceLinearity = Constants.LinearityType.Nonlinear;
					}
					else
					{
						if ((dLeftLinearity == Constants.LinearityType.Linear) || (dRightLinearity == Constants.LinearityType.Linear))
						{
							DeduceLinearity = Constants.LinearityType.Linear;
						}
						else
						{
							DeduceLinearity = Constants.LinearityType.Constant;
						}
					}
				}
				return DeduceLinearity;
			case Equal:
			case Grt:
			case GrtEqual:
			case Less:
			case LessEqual:
				dLeftLinearity = pLeft.getLinearity();
				dRightLinearity = pRight.getLinearity();
				if ((dLeftLinearity == Constants.LinearityType.Nonlinear) || (dRightLinearity == Constants.LinearityType.Nonlinear))
				{
					DeduceLinearity = Constants.LinearityType.Nonlinear;
				}
				else
				{
					if ((dLeftLinearity == Constants.LinearityType.Linear) || (dRightLinearity == Constants.LinearityType.Linear))
					{
						DeduceLinearity = Constants.LinearityType.Linear;
					}
					else
					{
						DeduceLinearity = Constants.LinearityType.Constant;
					}
				}
				return DeduceLinearity;
			case Range:
			case ListSeparator:
				dLeftLinearity = pLeft.getLinearity();
				if (pRight != null)
				{
					dRightLinearity = pRight.getLinearity();
				}
				if ((dLeftLinearity == Constants.LinearityType.Nonlinear) || (dRightLinearity == Constants.LinearityType.Nonlinear))
				{
					DeduceLinearity = Constants.LinearityType.Nonlinear;
				}
				return DeduceLinearity;
			case Function:
				dLeftLinearity = pLeft.getLinearity();
				if (pRight != null)
				{
					dRightLinearity = pRight.getLinearity();
				}
				if ((dLeftLinearity == Constants.LinearityType.Nonlinear) || (dRightLinearity == Constants.LinearityType.Nonlinear))
				{
					DeduceLinearity = Constants.LinearityType.Nonlinear;
				}
				if (!this.strOperand.equals("SUM"))
				{
					DeduceLinearity = Constants.LinearityType.Nonlinear;
				}
				return DeduceLinearity;
			default:
				DeduceLinearity = Constants.LinearityType.Unknown;
				return DeduceLinearity;
		}
	}
	public final void LinkExpressions()
	{

		switch (pOperatorType)
		{
			case Real:
				return;
			case ptrVariable:
				if (pOperand.getNode() != null)
				{
					pOperatorType = Constants.OperatorType.ptrExpression;
					pOperand.getNode().LinkExpressions();
				}
				return;
			case ptrExpression:
				//pOperand.ReconciledVariable = null;
				return;
			case UnaryMinus:
			case UnaryPlus:
				pLeft.LinkExpressions();
				return;
			case Plus:
			case Minus:
			case Multiply:
			case Divide:
			case Expon:
			case Equal:
			case Grt:
			case GrtEqual:
			case Less:
			case LessEqual:
				pLeft.LinkExpressions();
				pRight.LinkExpressions();
				return;
			case Range:
			case ListSeparator:
				pLeft.LinkExpressions();
				if (pRight != null)
				{
					pRight.LinkExpressions();
				}
				return;
			case Function:
				pLeft.LinkExpressions();
				if (pRight != null)
				{
					pRight.LinkExpressions();
				}
				return;
			default:
				return;
		}
	}
	public final void LocateArguments(ConstraintClass Balance)
	{
		switch (pOperatorType)
		{
			case Real:
				return;
			case ptrVariable:
				Balance.AddDependent(pOperand);
				return;
			case ptrExpression:
				pOperand.getNode().LocateArguments(Balance);
				return;
			case UnaryMinus:
			case UnaryPlus:
				pLeft.LocateArguments(Balance);
				return;
			case Plus:
			case Minus:
			case Multiply:
			case Divide:
			case Expon:
			case Equal:
			case Grt:
			case GrtEqual:
			case Less:
			case LessEqual:
				pLeft.LocateArguments(Balance);
				pRight.LocateArguments(Balance);
				return;
			case Range:
			case ListSeparator:
				pLeft.LocateArguments(Balance);
				if (pRight != null)
				{
					pRight.LocateArguments(Balance);
				}
				return;
			case Function:
				pLeft.LocateArguments(Balance);
				if (pRight != null)
				{
					pRight.LocateArguments(Balance);
				}
				return;
			default:
				return;
		}
	}
	public final void LocateDependentArguments(DependentClass Expression)
	{
		switch (pOperatorType)
		{
			case Real:
				return;
			case ptrVariable:
				Expression.AddDependent(pOperand);
				return;
			case ptrExpression:
				pOperand.getNode().LocateDependentArguments(Expression);
				return;
			case UnaryMinus:
			case UnaryPlus:
				pLeft.LocateDependentArguments(Expression);
				return;
			case Plus:
			case Minus:
			case Multiply:
			case Divide:
			case Expon:
			case Equal:
			case Grt:
			case GrtEqual:
			case Less:
			case LessEqual:
				pLeft.LocateDependentArguments(Expression);
				pRight.LocateDependentArguments(Expression);
				return;
			case Range:
			case ListSeparator:
				pLeft.LocateDependentArguments(Expression);
				if (pRight != null)
				{
					pRight.LocateDependentArguments(Expression);
				}
				return;
			case Function:
				pLeft.LocateDependentArguments(Expression);
				if (pRight != null)
				{
					pRight.LocateDependentArguments(Expression);
				}
				return;
			default:
				return;
		}
	}
}