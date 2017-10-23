package com.inova8.resolver;

public class ConstraintsClass implements Iterable<ConstraintClass>
{
	private java.util.ArrayList<ConstraintClass> pConstraints = new java.util.ArrayList<ConstraintClass>();
	private int nDistinctBalanceErrors = 0;
	private double dConstraintCriticalValue = 0.0;

	public java.util.Iterator<ConstraintClass> iterator()
	{
		return pConstraints.iterator();
	}
	public final int getCount()
	{
		return pConstraints.size();
	}
	public final int getDistinctBalanceErrors()
	{
		return nDistinctBalanceErrors;
	}
	public final double getConstraintCriticalValue()
	{
		return dConstraintCriticalValue;
	}
	public final void UpdateActiveConstraints()
	{
		for (ConstraintClass Balance : pConstraints)
		{
			if (Balance.getNode().getOperatorType() != Constants.OperatorType.Equal)
			{
				Balance.setActive(false);
			}
			else
			{
				Balance.setActive(true);
			}
		}
	}
	public final void CalculateConstraintError()
	{
		double dTemp = 0.0;

		for (ConstraintClass Constraint : pConstraints)
		{
			Constraint.setMeasuredResidual(Constraint.getNode().EvaluateResidual(Constants.FunctionType.NULL));
			Constraint.setReconciledResidual(Constraint.getNode().Evaluate(Constants.FunctionType.NULL));

			if ((Constraint.getActive() == true) || (Constraint.getNode().getOperatorType() == Constants.OperatorType.Equal))
			{
				if (Constraint.getDependents().size() > 0)
				{
					Constraint.setMeasuredDeviation(0.0);
					Constraint.setReconciledDeviation(0.0);

					for (CellClass Argument : Constraint.getDependents())
					{
						//if (Argument.hasMeasurement)
						//{
						//    dTemp = Constraint.Node.DerivativeResidual(Argument,Function) * Argument.ReconciledVariable.InitialTolerance;
						//    Constraint.ReconciledDeviation += dTemp * dTemp;
						//}
						//else
						//{
						dTemp = Constraint.getNode().DerivativeResidual(Argument, Constants.FunctionType.NULL) * Argument.getReconciledVariable().getReconciledTolerance();
						Constraint.setReconciledDeviation(Constraint.getReconciledDeviation() + dTemp * dTemp);
						//}

						if (Argument.gethasMeasurement())
						{
							dTemp = Constraint.getNode().Derivative(Argument, Constants.FunctionType.NULL) * Argument.getReconciledVariable().getInitialTolerance();
							Constraint.setMeasuredDeviation(Constraint.getMeasuredDeviation() + dTemp * dTemp);
						}
						else
						{
							dTemp = Constraint.getNode().Derivative(Argument, Constants.FunctionType.NULL) * Argument.getReconciledVariable().getReconciledTolerance();
							Constraint.setMeasuredDeviation(Constraint.getMeasuredDeviation() + dTemp * dTemp);
						}

					}
				}
				Constraint.setReconciledDeviation(Math.sqrt(Constraint.getReconciledDeviation()));
				Constraint.setMeasuredDeviation(Math.sqrt(Constraint.getMeasuredDeviation()));
			}
		}
	}
	public final void CalculateMeasuredDeviation()
	{
		double dTemp = 0.0;

		for (ConstraintClass Constraint : pConstraints)
		{
			if ((Constraint.getActive() == true) || (Constraint.getNode().getOperatorType() == Constants.OperatorType.Equal))
			{
				Constraint.setMeasuredDeviation(0.0);
				for (CellClass Argument : Constraint.getDependents())
				{
					if (Argument.gethasMeasurement())
					{
						dTemp = Constraint.getNode().Derivative(Argument, Constants.FunctionType.NULL) * Argument.getReconciledVariable().getInitialTolerance();
						Constraint.setMeasuredDeviation(Constraint.getMeasuredDeviation() + dTemp * dTemp);
					}
					else
					{
						dTemp = Constraint.getNode().Derivative(Argument, Constants.FunctionType.NULL) * Argument.getReconciledVariable().getReconciledTolerance();
						Constraint.setMeasuredDeviation(Constraint.getMeasuredDeviation() + dTemp * dTemp);
					}
				}
				Constraint.setMeasuredDeviation(Math.sqrt(Constraint.getMeasuredDeviation()));
			}
		}
	}
	public final void DiagnoseBalanceErrors(double dConfidence, double dStandardTolerance, int nFixedValues) //, Microsoft.Office.Interop.Excel.Application xl
	{
		double dBalanceMaximumValue = 0.0;

		nDistinctBalanceErrors = 0;
		this.CalculateConstraintError();

		// used to correct results to 95% confidence and 2*sd
		//dBalanceMaximumValue = xl.WorksheetFunction.NormSInv((1.0 - dConfidence)) / dStandardTolerance;
		//dBalanceMaximumValue = Functions.NormSInv((1.0 - dConfidence)) / dStandardTolerance;
		dBalanceMaximumValue = StatisticsClass.inv_normal((1.0 - dConfidence)) / dStandardTolerance;

		for (ConstraintClass Balance : pConstraints)
		{
			if (Balance.getActive())
			{
				nDistinctBalanceErrors++;
			}
		}
		//credit the 0 tolerance measurements as equivalent to an active constraint.
		nDistinctBalanceErrors += nFixedValues;

		if (nDistinctBalanceErrors == 0)
		{
			dConstraintCriticalValue = dBalanceMaximumValue; // xl.WorksheetFunction.NormSInv(1.0);
		}
		else
		{
			double beta = (1 - Math.pow((1 - dConfidence), (1.0 / nDistinctBalanceErrors)));
			//dConstraintCriticalValue = xl.WorksheetFunction.NormSInv(1.0 - beta / 2);
			//dConstraintCriticalValue = Functions.NormSInv(1.0 - beta / 2);
			dConstraintCriticalValue = StatisticsClass.inv_normal(1.0 - beta / 2);

		}

		for (ConstraintClass Balance : pConstraints)
		{
			if (Balance.getReconciledDeviation() < Constants.ETA)
			{
				Balance.setReconciledTest(Balance.getMeasuredResidual() / Constants.ETA);
			}
			else
			{
				Balance.setReconciledTest(Balance.getMeasuredResidual() / Balance.getReconciledDeviation());
			}
			if (Balance.getMeasuredDeviation() < Constants.ETA)
			{
				Balance.setMeasuredTest(Balance.getMeasuredResidual() / Constants.ETA);
			}
			else
			{
				Balance.setMeasuredTest(Balance.getMeasuredResidual() / Balance.getMeasuredDeviation());
			}
		}
	}
	public final Constants.LinearityType DetermineLinearity()
	{
	   Constants.LinearityType tLinearity = Constants.LinearityType.Linear;
		for (ConstraintClass Balance : pConstraints)
		{
			tLinearity = Balance.getNode().getLinearity();
		}
		return tLinearity;
	}
	public final void CountOperators()
	{
		for (ConstraintClass Balance : pConstraints)
		{
			Balance.getNode().CountOperators();
		}
	}
	public final void LinkExpressions()
	{
		for (ConstraintClass Constraint : pConstraints)
		{
			Constraint.getNode().LinkExpressions();
		}
	}
	public final void LocateConstraintVariables()
	{
		for (ConstraintClass Constraint : pConstraints)
		{
			Constraint.getNode().LocateArguments(Constraint);
		}
	}
	public final int getActiveConstraints()
	{
		int ac = 0;
		for (ConstraintClass constraint : pConstraints)
		{
			if (constraint.getActive())
			{
				ac++;
			}
		}
		return ac;
	}
	public final int getActiveLinearInequalityConstraints()
	{
		int ac = 0;
		for (ConstraintClass constraint : pConstraints)
		{
			if (constraint.getActive() && (constraint.getNode().getOperatorType() != Constants.OperatorType.Equal) && (constraint.getNode().getLinearity() == Constants.LinearityType.Linear))
			{
				ac++;
			}
		}
		return ac;
	}
	public final int getActiveNonlinearInequalityConstraints()
	{
		int ac = 0;
		for (ConstraintClass constraint : pConstraints)
		{
			if (constraint.getActive() && (constraint.getNode().getOperatorType() != Constants.OperatorType.Equal) && (constraint.getNode().getLinearity() != Constants.LinearityType.Linear))
			{
				ac++;
			}
		}
		return ac;
	}
	public final int getNumberNonLinearConstraints()
	{
		int n = 0;
		for (ConstraintClass Balance : pConstraints)
		{
			if (Balance.getNode().getLinearity() == Constants.LinearityType.Nonlinear)
			{
				n = n + 1;
			}
		}
		return n;
	}
	public final int getLinearConstraints()
	{
		int n = 0;
		for (ConstraintClass Balance : pConstraints)
		{
			if (Balance.getNode().getLinearity() == Constants.LinearityType.Linear && (Balance.getNode().getOperatorType() == Constants.OperatorType.Equal))
			{
				n = n + 1;
			}
		}
		return n;
	}
	public final int getNonlinearConstraints()
	{
		int n = 0;
		for (ConstraintClass Balance : pConstraints)
		{
			if (Balance.getNode().getLinearity() == Constants.LinearityType.Nonlinear && (Balance.getNode().getOperatorType() == Constants.OperatorType.Equal))
			{
				n = n + 1;
			}
		}
		return n;
	}
	public final void Add(NodeClass Constraint)
	{
		ConstraintClass pConstraint = new ConstraintClass();
		pConstraint.setNode(Constraint);
		pConstraints.add(pConstraint);
	}
	public final ConstraintClass Item(int index)
	{
		ConstraintClass Item = null;
		Item = (ConstraintClass)pConstraints.get(index - 1);
		return Item;
	}
	public final void Sort()
	{
		ComplexityComparerClass myComparer = new ComplexityComparerClass();
		java.util.Collections.sort(pConstraints, myComparer);
		return;
	}
//C# TO JAVA CONVERTER TODO TASK: The interface type was changed to the closest equivalent Java type, but the methods implemented will need adjustment:
	public static class ComplexityComparerClass implements java.util.Comparator<ConstraintClass>
	{
		public final int compare(ConstraintClass xConstraint, ConstraintClass yConstraint)
		{
			if (xConstraint == null)
			{
				if (yConstraint == null)
				{
					return 0;
				}
				else
				{
					return -1;
				}
			}
			else
			{
				if (yConstraint == null)
				{
					return 1;
				}
				else
				{
					return (int)Math.signum(yConstraint.getComplexity() - xConstraint.getComplexity());
				}
			}
		}
	}
	//internal static int byComplexity(ConstraintClass xConstraint, ConstraintClass yConstraint)
	//{
	//    if (xConstraint == null)
	//    {
	//        if (yConstraint == null)
	//        {
	//            return 0;
	//        }
	//        else
	//        {
	//            return -1;
	//        }
	//    }
	//    else
	//    {
	//        if (yConstraint == null)
	//        {
	//            return 1;
	//        }
	//        else
	//        {
	//            return Math.Sign(yConstraint.Complexity - xConstraint.Complexity);
	//        }
	//    }
	//}
}