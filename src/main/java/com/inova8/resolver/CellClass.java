package com.inova8.resolver;


public class CellClass //: IComparable<CellClass>
{
	private String pAddress = "";
	private String pName = "";
	private NodeClass pNode = null;
	private ResultClass pReconciledVariable = null;
	private Double pInitialValue = null;
	private Double pMeasuredTolerance = null;
	private Double pMeasuredValue = null;
	private boolean phasMeasurement = false;
	//public virtual int CompareTo(CellClass value)
	//{
	//    return pAddress.CompareTo(value.Address);
	//}
	public final Double getInitialValue()
	{
		if (pInitialValue != null)
		{
			return pInitialValue;
		}
		else if (pMeasuredValue != null)
		{
			return pMeasuredValue;
		}
		else
		{
			return 0.0;
		}
	}
	public final void setInitialValue(Double value)
	{
		pInitialValue = value;
	}
	public final Double getMeasuredTolerance()
	{
		return pMeasuredTolerance;
	}
	public final void setMeasuredTolerance(Double value)
	{
		pMeasuredTolerance = value;
	}
	public final Double getMeasuredValue()
	{
		return pMeasuredValue;
	}
	public final void setMeasuredValue(Double value)
	{
		pMeasuredValue = value;
	}
	public final boolean gethasMeasurement()
	{
		return phasMeasurement;
	}
	public final void sethasMeasurement(boolean value)
	{
		phasMeasurement = value;
	}
	public final ResultClass getReconciledVariable()
	{
		return pReconciledVariable;
	}
	public final void setReconciledVariable(ResultClass value)
	{
		pReconciledVariable = value;
	}

	public final String getAddress()
	{
		return pAddress;
	}
	public final void setAddress(String value)
	{
		pAddress = value;
	}

	public final String getName()
	{
		return pName;
	}
	public final void setName(String value)
	{
		pName = value;
	}

	public final NodeClass getNode()
	{
		return pNode;
	}
	public final void setNode(NodeClass value)
	{
		pNode = value;
	}
}