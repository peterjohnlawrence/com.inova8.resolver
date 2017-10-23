package com.inova8.resolver;

public class CellsClass implements Iterable<CellClass>
{
	private java.util.HashMap<String, CellClass> pCellAddresses = new java.util.HashMap<String, CellClass>();
	private NamesClass pNames;
	public CellsClass(NamesClass Names)
	{
		pNames = Names;
	}
	public final java.util.Collection<CellClass> CellsCollection()
	{
		return pCellAddresses.values();
	}
	public java.util.Iterator<CellClass> iterator()
	{
		return null; // pCellAddresses.GetEnumerator();
	}
	public final int getCount()
	{
		return pCellAddresses.size();
	}
	public final void Add(CellClass Cell)
	{
		pCellAddresses.put(Cell.getAddress(), Cell);
		if (pNames.ContainsKey(Cell.getAddress()))
		{
			Cell.setName(pNames.Name(Cell.getAddress()));
		}
		else
		{
			Cell.setName(Cell.getAddress());
		}
	}
	public final CellClass AddAddress(String Address)
	{
		CellClass AddCell = null;
		CellClass Cell = null;
		if (pCellAddresses.containsKey(Address))
		{
			Cell = pCellAddresses.get(Address);
			Cell.setAddress(Address);
			if (pNames.ContainsKey(Address))
			{
				Cell.setName(pNames.Name(Address));
			}
			else
			{
				Cell.setName(Address);
			}

			AddCell = Cell;
		}
		else
		{
			Cell = new CellClass();
			pCellAddresses.put(Address, Cell);

			//optional
			Cell.setAddress(Address);
			if (pNames.ContainsKey(Address))
			{
				Cell.setName(pNames.Name(Address));
			}
			else
			{
				Cell.setName(Address);
			}
			AddCell = Cell;

		}
		return AddCell;

	}
	public final CellClass AddAddress(String Address, NodeClass CellNode)
	{
		CellClass AddCell = null;
		CellClass Cell = null;
		if (pCellAddresses.containsKey(Address))
		{
			Cell = pCellAddresses.get(Address);
			if (null != CellNode && Cell.getNode() == null)
			{
				Cell.setNode(CellNode);
			}
			Cell.setAddress(Address);
			if (pNames.ContainsKey(Address))
			{
				Cell.setName(pNames.Name(Address));
			}
			else
			{
				Cell.setName(Address);
			}

			AddCell = Cell;
		}
		else
		{
			Cell = new CellClass();
			if (null != CellNode)
			{
				Cell.setNode(CellNode);
			}
			pCellAddresses.put(Address, Cell);

			//optional
			Cell.setAddress(Address);
			if (pNames.ContainsKey(Address))
			{
				Cell.setName(pNames.Name(Address));
			}
			else
			{
				Cell.setName(Address);
			}
			AddCell = Cell;

		}
		return AddCell;

	}
	public final CellClass AddMeasurement(String Address, Double Initial, Double Measurement, Double Tolerance)
	{
		CellClass Cell = null;
		if (pCellAddresses.containsKey(Address))
		{
			Cell = pCellAddresses.get(Address);
			Cell.setInitialValue(Initial);
			Cell.setMeasuredValue(Measurement);
			Cell.setMeasuredTolerance(Tolerance);
			if (Measurement != null)
			{
				Cell.sethasMeasurement(true);
			}
			else
			{
				Cell.sethasMeasurement(false);
			}
			return Cell;
		}
		else
		{
			return null;
		}

	}
	//internal CellClass ItemByAddress(string address)
	//{
	//    return (CellClass)pCellAddresses[address];
	//}
}