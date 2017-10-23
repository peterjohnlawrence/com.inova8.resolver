package com.inova8.resolver;

public class DependentsClass implements Iterable<DependentClass>
{
	private java.util.ArrayList<DependentClass> pDependents = new java.util.ArrayList<DependentClass>();
	public java.util.Iterator<DependentClass> iterator()
	{
		return pDependents.iterator();
	}
	//public void Sort()
	//{
	//    pDependents.Sort();
	//    return;
	//}
	public final void LinkExpressions()
	{
		for (DependentClass Expression : pDependents)
		{
			Expression.getNode().LinkExpressions();
		}
	}
	public final void LocateDependentVariables()
	{
		for (DependentClass dependent : pDependents)
		{
			dependent.getNode().LocateDependentArguments(dependent);
		}
	}
	public final int getCount()
	{
		return pDependents.size();
	}
	public final void Add(NodeClass Dependent)
	{
		DependentClass pDependent = new DependentClass();
		pDependent.setNode(Dependent);
		pDependents.add(pDependent);
	}
	public final DependentClass Item(int index)
	{
		DependentClass Item = null;
		Item = (DependentClass)pDependents.get(index - 1);
		return Item;
	}
}