package com.inova8.resolver;

public class DependentClass //: IComparable<DependentClass>
{
	private java.util.ArrayList<CellClass> pDependents = new java.util.ArrayList<CellClass>(20);
	private NodeClass pNode = null;

	//public virtual int CompareTo(DependentClass value)
	//{
	//    return pNode.Address.CompareTo(value.Node.Address);
	//}
	public final double getEvaluation()
	{
		return pNode.Evaluate(Constants.FunctionType.NULL);
	}
	public final String getSerialize()
	{
		return pNode.Serialize(true);
	}
	public final String getTrace()
	{
		return pNode.Serialize(false);
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
}