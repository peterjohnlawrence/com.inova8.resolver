package com.inova8.resolver;

public class SolutionClass
{
	public enum Termination
	{
		LinearConvergence,
		MinimumPrecision,
		MinimumConvergence,
		MaximumIterations,
		MaximumTime;

		public int getValue()
		{
			return this.ordinal();
		}

		public static Termination forValue(int value)
		{
			return values()[value];
		}
	}
	public static class Stopwatch
	{
		  private java.util.Date startTime = new java.util.Date(0);
		  private java.util.Date stopTime = new java.util.Date(0);
		  private boolean running = false;

		  public final void Start()
		  {
			this.startTime = new java.util.Date();
			this.running = true;
		  }

		  public final void Stop()
		  {
			this.stopTime = new java.util.Date();
			this.running = false;
		  }

		  //elaspsed time in milliseconds
		  public final int getElapsed()
		  {
			  if (running)
			  {
				return (int)(new java.util.Date().getTime() - startTime.getTime());
				  //return new java.util.Date().Subtract(startTime).Milliseconds;
			  }
			  else
			  {
				return (int)(stopTime.getTime() - startTime.getTime()); 
				 // return stopTime.Subtract(startTime).Milliseconds;
			  }
		  }

	}

	public static class ConvergenceClass
	{
		private static String pLog = "";
		private int nConvergenceTime = 0;
		private boolean bConverged;
		private int nIterations = 0;
		private double dCalculatedPrecision = 0.0;
		private double dCost = 0.0;
		private double dPreviousCalculatedPrecision = 0.0;
		private double dPreviousCost = 0.0;
		private Stopwatch pStopwatch;
		private double dReconciledCost;
		public Termination eTermination = Termination.values()[0];

		public final String getLog()
		{
			return pLog;
		}
		public final void setLog(String value)
		{
			pLog = value;
		}
		public final void AddIterationToLog(SolutionClass Solution)
		{
			pLog += "#Iteration=" + (new Integer(nIterations)).toString() + "\r\n";
			pLog += "  dPrecision=" + (new Double(dCalculatedPrecision)).toString() + "\r\n";
			pLog += "  dPrecisionChange=" + (new Double(getPrecisionChange())).toString() + "\r\n";
			pLog += "  dCost=" + (new Double(dCost)).toString() + "\r\n";
			pLog += "  dCostChange=" + (new Double(getCostChange())).toString() + "\r\n";
			pLog += "  ActivateLinearInequalities=" + Solution.getConstraints().getActiveLinearInequalityConstraints() + "\r\n";
			pLog += "  ActivateNonlinearInequalities=" + Solution.getConstraints().getActiveNonlinearInequalityConstraints() + "\r\n";
		}
		public final int getConvergenceTime()
		{
			return nConvergenceTime;
		}
		public final void setConvergenceTime(int value)
		{
			nConvergenceTime = value;
		}
		public final boolean getConverged()
		{
			return bConverged;
		}
		public final void setConverged(boolean value)
		{
			bConverged = value;
		}
		public final String getTerminated()
		{
			switch (eTermination)
			{
				case LinearConvergence:
					return "Linear";
				case MinimumPrecision:
					return "Minimum precision";
				case MinimumConvergence:
					return "Minimum cost change";
				case MaximumIterations:
					return "Maximum iterations";
				case MaximumTime:
					return "Maximum time";
				default:
					return "Unknown";
			}
		}
		public final int getIterations()
		{
			return nIterations;
		}
		public final void setIterations(int value)
		{
			nIterations = value;
		}
		public final double getCalculatedPrecision()
		{
			return Math.abs(dCalculatedPrecision);
		}
		public final void setCalculatedPrecision(double value)
		{
			dCalculatedPrecision = value;
		}
		public final double getCost()
		{
			return dCost;
		}
		public final void setCost(double value)
		{
			dCost = value;
		}
		public final double getPreviousCalculatedPrecision()
		{
			return dPreviousCalculatedPrecision;
		}
		public final void setPreviousCalculatedPrecision(double value)
		{
			dPreviousCalculatedPrecision = value;
		}
		public final double getPreviousCost()
		{
			return dPreviousCost;
		}
		public final void setPreviousCost(double value)
		{
			dPreviousCost = value;
		}
		public final double getPrecisionChange()
		{
			return Math.abs(dCalculatedPrecision / dPreviousCalculatedPrecision);
		}

		public final double getCostChange()
		{
			return Math.abs(1 - dCost / dPreviousCost);
		}
		public final void Stop()
		{
			pStopwatch.Stop();
			nConvergenceTime = pStopwatch.getElapsed();
		}
		public final double getElapsedSeconds()
		{
			return pStopwatch.getElapsed() / 1000.0;
		}
		public final double getReconciledCost()
		{
			return dReconciledCost;
		}
		public final void setReconciledCost(double value)
		{
			dReconciledCost = value;
		}
		public final void Retain()
		{
			dPreviousCost = dCost;
			dPreviousCalculatedPrecision = dCalculatedPrecision;
		}
		public final void Calculate(SolutionClass Solution, CovClass dUT)
		{
			dCost = Solution.getResults().CalculateCost();
			dCalculatedPrecision = Solution.CalculatePrecision(dUT);
		}
		public ConvergenceClass()
		{
			pStopwatch = new Stopwatch();
			pStopwatch.Start();
		}
	}
	private int nResults = 0;
	private ResultsClass pResults = new ResultsClass();
	private ResolverClass pProblem = null;
	public double dConfidence = 0.95;
	public double dStandardTolerance = 2.0;
	private double dGlobalCriticalValue = 0.0;
	private ConvergenceClass pConvergence;

	public SolutionClass()
	{
		pConvergence = new ConvergenceClass();
	}
	public final double getMeasurementCriticalValue()
	{
		return pProblem.getResults().getMeasurementCriticalValue();
	}
	public final double getConstraintCriticalValue()
	{
		return pProblem.getConstraints().getConstraintCriticalValue();
	}
	public final double getGlobalCriticalValue()
	{
		return dGlobalCriticalValue;
	}
	public final int getRedundancyDegree()
	{
		return pProblem.getResults().getRedundancyDegree();
	}
	public final ConstraintsClass getConstraints()
	{
		return pProblem.getConstraints();
	}
	public final DependentsClass getDependents()
	{
		return pProblem.getDependents();
	}
	public final ResultsClass getResults()
	{
		return pResults;
	}
	public final ConvergenceClass getConvergence()
	{
		return pConvergence;
	}
	public final int getFixedValues()
	{
		return pProblem.getResults().getFixedValues();
	}
	public final boolean Resolve(ResolverClass Problem, int MaximumTime, int Iterations, double Precision, double Convergence)
	{
		double InitialGradient = Precision;
		double GradientChange = 2.0;
		double dGradient = Precision;
		CovClass dUT;
		double[] dCp;

		pConvergence.setConverged(false);
		pProblem = Problem;

		pProblem.getConstraints().LinkExpressions();
		pProblem.getDependents().LinkExpressions();

		LocateReconciledVariables();
		pProblem.getConstraints().LocateConstraintVariables();
		pProblem.getDependents().LocateDependentVariables();

		Problem.getConstraints().DetermineLinearity();
		Problem.getConstraints().CountOperators();
		Problem.getConstraints().Sort();
		//Resize();
		dCp = new double[nResults + 1];
		dUT = new CovClass(nResults);
		//UpdateActiveConstraints();
		Problem.getConstraints().UpdateActiveConstraints();
		//InitializeMeasurements();
		pResults.InitializeMeasurements();
		//InitializeCovariance(dUT);
		dUT.InitializeCovariance(pResults);
		pConvergence.Calculate(this, dUT);

		pConvergence.Retain();
		ReconcileLinearEqualities(0, dCp, dUT);

		if (Problem.getConstraints().getNumberNonLinearConstraints() == 0)
		{
			ReconcileInequalities(dGradient, dCp, dUT);
			pResults.RetainFinalValues();
			pConvergence.eTermination = Termination.LinearConvergence;
			pConvergence.setConverged(true);
		}
		else
		{
			pConvergence.setIterations(1);
			dGradient *= nResults; // scale according to the number of variables
			ReconcileNonLinearEqualities(dGradient, dCp, dUT);
			//ReconcileInequalities(dGradient, dCp, dUT);  //causes problems should delay until the precision/convergence is complete
			pResults.RetainFinalValues();
			pConvergence.Calculate(this, dUT);
			pConvergence.AddIterationToLog(this);
			pConvergence.Retain();
			pConvergence.setLog(pConvergence.getLog() + "Action=  Retaining values" + "\r\n");
			do
			{
				do
				{
					pConvergence.setIterations(pConvergence.getIterations() + 1);
					UpdateCovariance(dUT);
					pResults.UpdateMeasurement();
					Problem.getConstraints().UpdateActiveConstraints();
					ReconcileLinearEqualities(0, dCp, dUT);
					ReconcileNonLinearEqualities(dGradient, dCp, dUT);
					//ReconcileInequalities(dGradient, dCp, dUT);
					pConvergence.Calculate(this, dUT);
					pConvergence.AddIterationToLog(this);
					if (pConvergence.getCalculatedPrecision() > pConvergence.getPreviousCalculatedPrecision())
					{
						if (dGradient == 0.0)
						{
							if (InitialGradient == 0.0)
							{
								InitialGradient = Precision;
								pConvergence.setLog(pConvergence.getLog() + "Action=  Setting initial descent =" + (new Double(InitialGradient)).toString() + "\r\n");
							}
							dGradient = InitialGradient;
						}
						else
						{
							dGradient = dGradient * GradientChange;
						}
						pConvergence.setLog(pConvergence.getLog() + "Action=  Diverging precision, so increasing descent, rotating towards steepest descent direction=" + (new Double(dGradient)).toString() + "\r\n");
					}
					else if (pConvergence.getCalculatedPrecision() < Precision)
					{
						pConvergence.eTermination = Termination.MinimumPrecision;
						pConvergence.setConverged(true);
					}
					else if (pConvergence.getCostChange() < Convergence)
					{
						pConvergence.eTermination = Termination.MinimumConvergence;
						pConvergence.setConverged(true);
					}
					else
					{
						pConvergence.Retain();
						pResults.RetainFinalValues();
						pConvergence.setLog(pConvergence.getLog() + "Action=  Retaining values" + "\r\n");
					}
				}
				while (!pConvergence.getConverged() && (pConvergence.getIterations() < Iterations) && pConvergence.getElapsedSeconds() < MaximumTime);
				if (pConvergence.getConverged())
                        
				{
					pConvergence.setLog(pConvergence.getLog() + "Action=  Checking for inequalities" + "\r\n");
					ReconcileInequalities(dGradient, dCp, dUT);
					pConvergence.Calculate(this, dUT);
					pConvergence.AddIterationToLog(this);

					if (pConvergence.getCalculatedPrecision() < Precision)
					{
						pConvergence.eTermination = Termination.MinimumPrecision;
						pConvergence.setConverged(true);
					}
					else if (pConvergence.getCostChange() < Convergence)
					{
						pConvergence.eTermination = Termination.MinimumConvergence;
						pConvergence.setConverged(true);
					}
					else
					{
						pConvergence.Retain();
						pResults.RetainFinalValues();
						pConvergence.setLog(pConvergence.getLog() + "Action=  Retaining values" + "\r\n");
					}
				}
			}
			while (!pConvergence.getConverged() && (pConvergence.getIterations() < Iterations) && pConvergence.getElapsedSeconds() < MaximumTime);

			if (!pConvergence.getConverged())
			{
				if (pConvergence.getIterations() >= Iterations)
				{
					pConvergence.eTermination = Termination.MaximumIterations;
				}
				else
				{
					pConvergence.eTermination = Termination.MaximumTime;
				}
			}
		}
		pResults.RetainFinalValues();
		Diagnose(dUT);
		pConvergence.Stop();
		return pConvergence.getConverged();
	}

	private void LocateReconciledVariables() //CellsClass Variables)
	{
		//foreach (KeyValuePair<string, CellClass> pKeyVariable in pProblem.Cells)
		//{
		//    if (pKeyVariable.Value.Node == null) //not an expression could test for reconciledvariable
		//    {
		//        AddReconciledVariable(pKeyVariable.Value);
		//    }
		//}
		for (CellClass pCell : pProblem.getCells().CellsCollection())
		{
			if (pCell.getNode() == null) //not an expression could test for reconciledvariable
			{
				AddReconciledVariable(pCell);
			}
		}
	}

	private ResultClass AddReconciledVariable(CellClass pVariable)
	{
		ResultClass AddReconciledVariable = null;
		ResultClass Result = new ResultClass();
		nResults = nResults + 1;
		pResults.Add(Result);
		Result.setCell(pVariable);
		pVariable.setReconciledVariable(Result);
		AddReconciledVariable = Result;
		Result.setIndex(nResults);
		return AddReconciledVariable;
	}

	private void UpdateCovariance(CovClass dUT)
	{
		int j = 0;

		dUT.Reset();
		for (j = 1; j <= nResults; j++)
		{
			dUT.setItem(j, j, ((ResultClass)pResults.getItem(j - 1)).getInitialTolerance());
		}
	}
	private double CalculatePrecision(CovClass dUT)
	{
		double dCalculatePrecision = 0.0;
		int j = 0;
		//double dStep = 0.0;
		double dStepTotal = 0.0;
		double dTemp1 = 0.0;
		//double dTemp2 = 0.0;
		//double dDerivative = 0.0;

		//dStep = 0.0;
		dStepTotal = 0.0;

		for (j = 0; j < nResults; j++)
		{
			((ResultClass)pResults.getItem(j)).setReconciledTolerance(Math.sqrt(dUT.getItem(j + 1)));
		}

		for (ConstraintClass Balance : pProblem.getConstraints())
		{
			if ((Balance.getActive() == true) || (Balance.getNode().getOperatorType() == Constants.OperatorType.Equal))
			{
				Balance.setMeasuredResidual(Balance.getNode().Evaluate(Constants.FunctionType.NULL));
				Balance.setReconciledDeviation(0.0);
				for (CellClass Argument : Balance.getDependents())
				{
					dTemp1 = Balance.getNode().Derivative(Argument, Constants.FunctionType.NULL) * Argument.getReconciledVariable().getReconciledTolerance();
					//dTemp1 = Balance.Node.DerivativeResidual(Argument) * Argument.ReconciledVariable.ReconciledTolerance;
					Balance.setReconciledDeviation(Balance.getReconciledDeviation() + dTemp1 * dTemp1);
				}
				if (Balance.getReconciledDeviation() != 0.0)
				{

					dStepTotal += (Balance.getMeasuredResidual() * Balance.getMeasuredResidual()) / Balance.getReconciledDeviation();
					//dStepTotal += (Balance.ReconciledResidual * Balance.ReconciledResidual) / Balance.ReconciledDeviation;
				}
				Balance.setReconciledDeviation(Math.sqrt(Balance.getReconciledDeviation()));
			}
		}
		dCalculatePrecision = Math.sqrt(dStepTotal) / pProblem.getConstraints().getActiveConstraints();
		return dCalculatePrecision;
	}

	private void ReconcileLinearEqualities(double dTolerance, double[] dCp, CovClass dUT)
	{
		double dResidual = 0.0;
		for (ConstraintClass Balance : pProblem.getConstraints())
		{
			if ((Balance.getNode().getLinearity() != Constants.LinearityType.Nonlinear) && (Balance.getNode().getOperatorType() == Constants.OperatorType.Equal))
			{
				dResidual = Balance.getNode().Evaluate(Constants.FunctionType.NULL);
				CalculateGain(Balance, dCp, dUT);
				UpdateEstimate(dResidual, 0, dCp, dUT);
				Balance.setActive(true);
			}
		}
	}

	private void ReconcileNonLinearEqualities(double dTolerance, double[] dCp, CovClass dUT)
	{
		double dResidual = 0.0;
		for (ConstraintClass Balance : pProblem.getConstraints())
		{
			if ((Balance.getNode().getLinearity() == Constants.LinearityType.Nonlinear) && (Balance.getNode().getOperatorType() == Constants.OperatorType.Equal))
			{
				dResidual = Balance.getNode().Evaluate(Constants.FunctionType.NULL);
				CalculateGain(Balance, dCp, dUT);
				UpdateEstimate(dResidual, dTolerance, dCp, dUT);
				Balance.setActive(true);
			}
		}
	}

	private void ReconcileInequalities(double dGradient, double[] dCp, CovClass dUT)
	{
		ConstraintClass BalanceMax = null;
		double dResidual = 0.0;
		double dResidualMax = 0.0;
		double dAlpha = 0.0;
		//double dAlphaMax = 0.0;
		double dMultiplier = 0.0;
		double dMultiplierMax = 0.0;
		//double dLastMultiplierMax = -Constants.LARGEVALUE;
		double dPreviousMultiplierMax = Constants.LARGEVALUE;
		boolean diverging = false;

		do
		{
			dAlpha = 0.0;
			dMultiplier = 0.0;
			dMultiplierMax = 0.0;
			BalanceMax = null;

			//find the inactive constraint with the largest alpha coefficient

			for (ConstraintClass Balance : pProblem.getConstraints())
			{
				if ((Balance.getNode().getOperatorType() != Constants.OperatorType.Equal) && (Balance.getActive() == false))
				{
					dResidual = Balance.getNode().Evaluate(Constants.FunctionType.NULL);
					if (dResidual < 0)
					{
						CalculateGain(Balance, dCp, dUT);
						dAlpha = Alpha(dCp);
						dMultiplier = dResidual / dAlpha;
						if (dMultiplier < dMultiplierMax)
						//if (dAlpha > dAlphaMax)
						{
							//dAlphaMax = dAlpha;
							BalanceMax = Balance;
							dResidualMax = dResidual;
							dMultiplierMax = dMultiplier;
						}
					}
				}
			}
			//as long as it is not null then activate this constraint unless we are moving away as indicated by the multiplier increasing (negatively)
			if (!(BalanceMax == null))
			{
				if (dPreviousMultiplierMax < dMultiplierMax)
				{
					diverging = true;
				}
				else
				{
					//dLastMultiplierMax = dMultiplierMax;
					dResidualMax = BalanceMax.getNode().Evaluate(Constants.FunctionType.NULL);
					CalculateGain(BalanceMax, dCp, dUT);
					if (BalanceMax.getNode().getLinearity() == Constants.LinearityType.Nonlinear)
					{
						UpdateEstimate(dResidualMax, dGradient, dCp, dUT);
					}
					else
					{
						UpdateEstimate(dResidualMax, 0, dCp, dUT);
					}
					BalanceMax.setActive(true);
					dPreviousMultiplierMax = dMultiplierMax;
				}
			}
		}
		while (!(BalanceMax == null) && !diverging);
	}

	private double Alpha(double[] dCp)
	{
		int i = 0;
		double dAlpha = 0.0;

		for (i = 1; i <= nResults; i++)
		{
			if (dCp[i] != 0.0)
			{
				dAlpha += (dCp[i] * dCp[i]);
			}
		}
		return dAlpha;
	}
	private void CalculateGain(ConstraintClass Constraint, double[] dCp, CovClass dUT)
	{
		double dDerivative = 0.0;
		int i = 0;
		int j = 0;

		for (i = 1; i <= nResults; i++)
		{
			dCp[i] = 0.0;
		}

		for (CellClass Argument : Constraint.getDependents())
		{
			j = Argument.getReconciledVariable().getIndex();
			dDerivative = Constraint.getNode().Derivative(Argument, Constants.FunctionType.NULL);

			dCp[j] -= (dUT.getItem(j, j) * dDerivative);

			if (j < nResults)
			{
				for (i = j + 1; i <= nResults; i++)
				{
					dCp[i] -= (dUT.getItem(i, j) * dDerivative);
				}
			}
		}
	}
	private void UpdateEstimate(double dResidual, double dTolerance, double[] dCp, CovClass dUT)
	{

		int j = 0;
		//int i = 0;
		int k = 0;
		double dAlpha = 0.0;
		double dSAlpha = 0.0;
		//double dLastAlpha = 0.0;
		double dLastSAlpha = 0.0;
		//double dGamma = 0.0;
		double dSGamma = 0.0;
		double dDelta = 0.0;
		double dLambda = 0.0;
		double dSigma = 0.0;
		double dLastSAlphaSGamma = 0.0;
		double dLambdaSAlpha = 0.0;
		double dTemp = 0.0;
		double[] dTemporary = new double[nResults + 1];
		//bool eSingularity = true;

		// SSt Factorization
		for (j = 1; j <= nResults; j++)
		{
			dTemporary[j] = dCp[j];
		}

		dAlpha = (dTolerance * dTolerance) + (dTemporary[1] * dTemporary[1]);
		dSAlpha = Math.sqrt(dAlpha);

		//dGamma = 0.0;
		//dSGamma = 0.0;

		if (dAlpha > 0.0)
		{
			dSGamma = 1 / dSAlpha;
		}

		if (dTemporary[1] != 0.0)
		{
			dTemporary[1] = dTemporary[1] * dUT.getItem(1, 1);
			dUT.setItem(1, 1, dUT.getItem(1, 1) * dTolerance * dSGamma);
		}
		for (j = 2; j <= nResults; j++)
		{

			dLastSAlpha = dSAlpha;
			//dLastAlpha = dAlpha;

			dDelta = dCp[j];
			dAlpha = dAlpha + (dDelta * dCp[j]);

			if (Math.abs(dAlpha) > Constants.SMALLVALUE)
			{
				//eSingularity = false;
				dSAlpha = Math.sqrt(dAlpha);
				dLambda = (-dDelta) * dSGamma;

				dSGamma = 1.0 / dSAlpha;

				dLastSAlphaSGamma = dLastSAlpha / dSAlpha;
				dLambdaSAlpha = dLambda / dSAlpha;

				if (dDelta != 0.0)
				{
					for (k = 1; k <= (j - 1); k++)
					{
						dSigma = dUT.getItem(j, k);
						if (dSigma != 0.0)
						{
							dUT.setItem(j, k, (dSigma * dLastSAlphaSGamma) + (dLambdaSAlpha * dTemporary[k]));
							dTemporary[k] += (dDelta * dSigma);
						}
						else
						{
							dUT.setItem(j, k, dLambdaSAlpha * dTemporary[k]);
						}
					}
				}
				else
				{
					for (k = 1; k <= (j - 1); k++)
					{
						dSigma = dUT.getItem(j, k);
						if (dSigma != 0.0)
						{
							dUT.setItem(j, k, (dSigma * dLastSAlphaSGamma) + (dLambdaSAlpha * dTemporary[k]));
						}
						else
						{
							dUT.setItem(j, k, dLambdaSAlpha * dTemporary[k]);
						}
					}
				}
				dTemporary[j] *= dUT.getItem(j, j);
				dUT.setItem(j, j, dUT.getItem(j, j) * dLastSAlpha * dSGamma);
			}
			else
			{
				//singularity?
				dAlpha = 0.0;
			}
		}

		dTemp = dResidual * dSGamma * dSGamma;
		if (Math.abs(dTemp) < Constants.LARGEVALUE) //Test to ensure that Kalman gain has not become unstable due to singularity
		{
			for (j = 1; j <= nResults; j++)
			{
				((ResultClass)pResults.getItem(j - 1)).setValue(((ResultClass)pResults.getItem(j - 1)).getValue() + (dTemporary[j] * dTemp));
			}
		}
		else
		{
			//Kalman gain has become unstable due to singularity
			dTemp = 0.0;
		}
	}


	private void Diagnose(CovClass dUT)
	{
		pProblem.getResults().Diagnose(dUT);
		pConvergence.setReconciledCost(pProblem.getResults().CalculateCost() * nResults);
		pConvergence.setReconciledCost(pConvergence.getReconciledCost() * pConvergence.getReconciledCost());

		//WorksheetFunction wf = xl.WorksheetFunction;
		if (pProblem.getResults().getRedundancyDegree() > 0)
		{
			//dGlobalCriticalValue = wf.ChiInv((1.0 - dConfidence), (double)pProblem.Results.RedundancyDegree);
			//dGlobalCriticalValue = Functions.ArcChiSquare((1.0 - dConfidence), pProblem.Results.RedundancyDegree);
			dGlobalCriticalValue = StatisticsClass.inv_chi_sq((dConfidence), pProblem.getResults().getRedundancyDegree());
		}
		else
		{
			dGlobalCriticalValue = 0;
		}
		pProblem.getResults().DiagnoseMeasurements(dConfidence, dStandardTolerance); //, xl);
		pProblem.getConstraints().DiagnoseBalanceErrors(dConfidence, dStandardTolerance, pProblem.getResults().getFixedValues()); //, xl
	}
}