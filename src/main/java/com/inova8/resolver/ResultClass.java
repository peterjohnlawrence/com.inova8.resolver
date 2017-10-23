package com.inova8.resolver;

public class ResultClass //: IComparable<ResultClass>
{
	private double dValue = 0.0;
	//private double dInitialValue = 0.0;
	private Double dInitialValue = 0.0;

	private double dReconciledValue = 0.0;
	private double dReconciledTolerance = 0.0;
	private double dInitialTolerance = 0.0;

	private double dMeasuredTest = 0.0;
	private double dReconciledTest = 0.0;
	private double dMeasuredError = 0.0;
	private CellClass pCell = null;
	private Constants.SolvabilityType eSolvable = Constants.SolvabilityType.Unobservable;
	private int nIndex = 0;

	public final CellClass getCell()
	{
		return pCell;
	}
	public final void setCell(CellClass value)
	{
		pCell = value;
	}
	public final double getValue()
	{
		return dValue;
	}
	public final void setValue(double value)
	{
		dValue = value;
	}
	public final boolean gethasMeasurement()
	{
		return pCell.gethasMeasurement();
	}
	public final Double getInitialValue()
	{
		return dInitialValue;
	}
	public final void setInitialValue(Double value)
	{
		dInitialValue = value;
	}
	public final Double getMeasuredValue()
	{
		return pCell.getMeasuredValue();
	}
	public final double getReconciledValue()
	{
		return dReconciledValue;
	}
	public final void setReconciledValue(double value)
	{
		dReconciledValue = value;
	}
	public final double getMeasuredTest()
	{
		return dMeasuredTest;
	}
	public final void setMeasuredTest(double value)
	{
		dMeasuredTest = value;
	}

	public final double getReconciledTest()
	{
		return dReconciledTest;
	}
	public final void setReconciledTest(double value)
	{
		dReconciledTest = value;
	}
	public final double getMeasuredError()
	{
		return dMeasuredError;
	}
	public final void setMeasuredError(double value)
	{
		dMeasuredError = value;
	}

	public final double getInitialTolerance()
	{
		return dInitialTolerance;
	}
	public final void setInitialTolerance(double value)
	{
		dInitialTolerance = value;
	}
	public final Double getMeasuredTolerance()
	{
		return pCell.getMeasuredTolerance();
	}

	public final double getReconciledTolerance()
	{
		return dReconciledTolerance;
	}
	public final void setReconciledTolerance(double value)
	{
		dReconciledTolerance = value;
	}

	public final String getCellAddress()
	{
		return pCell.getAddress();
	}
	public final String getCellName()
	{
		return pCell.getName();
	}
	public final String getSolvabilityText()
	{
		return Constants.SolvableString(eSolvable);
	}

	public final Constants.SolvabilityType getSolvability()
	{
		return eSolvable;
	}
	public final void setSolvability(Constants.SolvabilityType value)
	{
		eSolvable = value;
	}

	public final int getIndex()
	{
		return nIndex;
	}
	public final void setIndex(int value)
	{
		nIndex = value;
	}
}