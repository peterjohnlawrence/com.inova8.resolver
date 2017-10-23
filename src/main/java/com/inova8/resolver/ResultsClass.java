package com.inova8.resolver;

public class ResultsClass implements Iterable<ResultClass>
{
	private java.util.ArrayList<ResultClass> pResults = new java.util.ArrayList<ResultClass>();
	private int nRedundancyDegree = 0;
	private int nFixedValues = 0;
	private int nDistinctMeasurementErrors = 0;
	private double dMeasurementCriticalValue = 0.0;

	public java.util.Iterator<ResultClass> iterator()
	{
		return pResults.iterator();
	}
	//public void Sort()
	//{
	//    pResults.Sort();
	//    return;
	//}
	public final int getCount()
	{
		return pResults.size();
	}
		//get{
		//    int Count =0;
		//    foreach (ResultClass result in pResults)
		//    {
		//        if (result.Cell.hasMeasurement && (result.Solvability == Constants.SolvabilityType.Fixed)) Count++;
		//    }
		//    return Count;
		//}
	public final int getFixedValues()
	{
		return nFixedValues;
	}
	public final void setFixedValues(int value)
	{
		nFixedValues = value;
	}
	public final int getRedundancyDegree()
	{
		return nRedundancyDegree;
	}
	public final void setRedundancyDegree(int value)
	{
		nRedundancyDegree = value;
	}
	public final int getDistinctMeasurementErrors()
	{
		return nDistinctMeasurementErrors;
	}
	public final void setDistinctMeasurementErrors(int value)
	{
		nDistinctMeasurementErrors = value;
	}
	public final double getMeasurementCriticalValue()
	{
		return dMeasurementCriticalValue;
	}
	public final void setMeasurementCriticalValue(double value)
	{
		dMeasurementCriticalValue = value;
	}
	public final ResultClass getItem(int index)
	{
		return pResults.get(index);
	}
	public final void setItem(int index, ResultClass value)
	{
		pResults.set(index, value);
	}
	public final void Add(ResultClass Result)
	{
		pResults.add(Result);
	}
	public final void UpdateMeasurement()
	{
		for (ResultClass result : pResults)
		{
			result.setValue(result.getReconciledValue());
		}
	}
	public final void InitializeMeasurements()
	{
		for (ResultClass result : pResults)
		{
			if (result.getCell().gethasMeasurement())
			{
				result.setInitialValue(result.getCell().getMeasuredValue());
				result.setInitialTolerance(result.getCell().getMeasuredTolerance());
			}
			else
			{
				result.setInitialValue(result.getCell().getInitialValue());
				result.setInitialTolerance(Constants.LARGEVALUE);
			}
		}
	}
	public final void RetainFinalValues()
	{
		for (ResultClass result : pResults)
		{
			result.setReconciledValue(result.getValue());
		}
	}
	public final void DeduceSolvability()
	{
		for (ResultClass result : pResults)
		{
			if (result.getCell().gethasMeasurement())
			{
				// check for redundancy
				if (result.getInitialTolerance() == 0)
				{
					result.setSolvability(Constants.SolvabilityType.Fixed);
				}
				else if ((result.getInitialTolerance() - result.getReconciledTolerance()) < (Constants.ETA * result.getInitialTolerance()))
				{
					result.setSolvability(Constants.SolvabilityType.Determined);
				}
				else
				{
					result.setSolvability(Constants.SolvabilityType.Redundant);
				}
			}
			else
			{
				// check for solvability
				if (result.getReconciledTolerance() > (result.getInitialTolerance() * Constants.ACCURACY))
				{
					result.setSolvability(Constants.SolvabilityType.Unobservable);
				}
				else
				{
					result.setSolvability(Constants.SolvabilityType.Observable);
				}
			}
		}
	}
	public final void Diagnose(CovClass dUT)
	{
		int i = 0;
		int nNonSolvable = 0;
		for (i = 0; i < pResults.size(); i++)
		{
			((ResultClass)pResults.get(i)).setReconciledTolerance(Math.sqrt(dUT.getItem(i + 1)));
		}

		nNonSolvable = 0;
		nRedundancyDegree = 0;
		nFixedValues = 0;
		this.DeduceSolvability();

		for (ResultClass result : pResults)
		{
			if (result.getSolvability() == Constants.SolvabilityType.Unobservable)
			{
				nNonSolvable += 1;
			}
			if (result.getSolvability() == Constants.SolvabilityType.Redundant)
			{
				nRedundancyDegree += 1;
			}
//C# TO JAVA CONVERTER TODO TASK: Comparisons involving nullable type instances are not converted to null-value logic:
			if (result.gethasMeasurement() && result.getMeasuredTolerance().equals(0))
			{
				//count the number of measurements which should really be constraints because they have a tolerance of 0
				nFixedValues += 1;
				//debit the Redundancy degree if this 0 tol measurement was classified as redundant.
				if (result.getSolvability() == Constants.SolvabilityType.Redundant)
				{
					nRedundancyDegree -= 1;
				}
			}
		}
	}
	public final void DiagnoseMeasurements(double dConfidence, double dStandardTolerance)
	{
		//double dMeasurementMaximumValue = 0.0;
		// used to correct measurements to assumption of 95% confidence and tolerance provided at 2*sd
		//dMeasurementMaximumValue = StatisticsClass.inv_normal(1.0 - dConfidence) / dStandardTolerance;

		for (ResultClass result : pResults)
		{
			if (result.getCell().gethasMeasurement())
			{
				result.setMeasuredError(result.getValue() - result.getInitialValue());
				if (result.getReconciledTolerance() > Constants.ETA)
				{
					result.setReconciledTest(result.getMeasuredError() / result.getReconciledTolerance());
					//Only count if tolerance is not 0
					nDistinctMeasurementErrors = nDistinctMeasurementErrors + 1;
				}
				else
				{
					result.setReconciledTest(0.0);
				}
			}
			else
			{
				result.setMeasuredError(0.0);
				result.setReconciledTest(0.0);
			}
		}

		if (nDistinctMeasurementErrors == 0)
		{
			dMeasurementCriticalValue = 0; // xl.WorksheetFunction.NormSInv(0);
		}
		else
		{
			double beta = (1 - Math.pow((1 - dConfidence), (1.0 / nDistinctMeasurementErrors)));
			//dMeasurementCriticalValue = xl.WorksheetFunction.NormSInv(1.0 - beta / 2);
			//dMeasurementCriticalValue = Functions.NormSInv(1.0 - beta / 2);
			dMeasurementCriticalValue = StatisticsClass.inv_normal(1.0 - beta / 2);

			for (ResultClass result : pResults)
			{
				if (result.getInitialTolerance() < Constants.ETA)
				{
					result.setMeasuredTest(result.getMeasuredError() / Constants.ETA);
				}
				else
				{
					result.setMeasuredTest(result.getMeasuredError() / result.getInitialTolerance()); //   / dMeasurementMaximumValue;
				}
				//result.MeasurementTest = result.MeasurementTest / dMeasurementCriticalValue;
			}
		}
	}
	public final double CalculateCost()
	{
		double CalculateCost = 0.0;
		double dInitialTolerance = 0.0;
		double dIncrementalCost = 0.0;

		for (ResultClass result : pResults)
		{
			dInitialTolerance = result.getInitialTolerance();
			if (result.getCell().gethasMeasurement() && dInitialTolerance > 0.0)
			{
				dIncrementalCost = (result.getValue() - result.getInitialValue()) / dInitialTolerance;
				CalculateCost += dIncrementalCost * dIncrementalCost;
			}
		}
		return Math.sqrt(CalculateCost) / pResults.size();
	}

}