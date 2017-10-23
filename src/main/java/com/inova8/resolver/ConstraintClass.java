package com.inova8.resolver;

public class ConstraintClass //: IComparable<ConstraintClass>
{
	private boolean pActive = false;
	private java.util.ArrayList<CellClass> pDependents = new java.util.ArrayList<CellClass>(20);
	private NodeClass pNode = null;
	private double pMeasuredResidual = 0.0;
	private double pReconciledResidual = 0.0;
	private boolean bVisited = false;
	private double pReconciledDeviation = 0.0;
	private double pMeasuredDeviation = 0.0;
	private double pMeasuredTest = 0.0;
	private double pReconciledTest = 0.0;

	//public virtual int CompareTo(ConstraintClass value)
	//{
	//    return pNode.Address.CompareTo(value.Node.Address);
	//}
	public final boolean getActive()
	{
		return pActive;
	}
	public final void setActive(boolean value)
	{
		pActive = value;
	}
	public final boolean getVisited()
	{
		return bVisited;
	}
	public final void setVisited(boolean value)
	{
		bVisited = value;
	}
	public final int getLinearOperatorCount()
	{
		return pNode.getLinearOperatorCount();
	}
	public final int getNonlinearOperatorCount()
	{
		return pNode.getNonlinearOperatorCount();
	}
	public final int getVariableCount()
	{
		return pNode.getVariableCount();
	}
	public final int getComplexity()
	{
		return pNode.getVariableCount() + pNode.getNonlinearOperatorCount();
	}
	public final java.util.ArrayList<CellClass> getDependents()
	{
		return pDependents;
	}
	public final double getMeasuredResidual()
	{
		return pMeasuredResidual;
	}
	public final void setMeasuredResidual(double value)
	{
		pMeasuredResidual = value;
	}
	public final double getReconciledResidual()
	{
		return pReconciledResidual;
	}
	public final void setReconciledResidual(double value)
	{
		pReconciledResidual = value;
	}
	public final String getSerialize()
	{
		return pNode.Serialize(true);
	}
	public final String getTrace()
	{
		return pNode.Serialize(false);
	}
	public final double getReconciledDeviation()
	{
		return pReconciledDeviation;
	}
	public final void setReconciledDeviation(double value)
	{
		pReconciledDeviation = value;
	}
	public final double getMeasuredDeviation()
	{
		return pMeasuredDeviation;
	}
	public final void setMeasuredDeviation(double value)
	{
		pMeasuredDeviation = value;
	}
	public final double getMeasuredTest()
	{
		return pMeasuredTest;
	}
	public final void setMeasuredTest(double value)
	{
		pMeasuredTest = value;
	}

	public final double getReconciledTest()
	{
		return pReconciledTest;
	}
	public final void setReconciledTest(double value)
	{
		pReconciledTest = value;
	}

	public final NodeClass getNode()
	{
		return pNode;
	}
	public final void setNode(NodeClass value)
	{
		pNode = value;
	}

	public final String getAddress()
	{
		return pNode.getAddress();
	}
	public final void setAddress(String value)
	{
		pNode.setAddress(value);
	}

	public final void AddDependent(CellClass nDependent)
	{
		pDependents.add(nDependent);
	}

	//public CellClass DependentItem(int index)
	//{
	//    CellClass DependentItem = null;
	//    DependentItem = (CellClass)pDependents[index - 1];
	//    return DependentItem;
	//}

	//internal int DependentItemCount
	//{
	//    get { return pDependents.Count; }
	//}

	public ConstraintClass()
	{
		pActive = false;
	}
}