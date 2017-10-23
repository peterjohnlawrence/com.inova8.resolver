package com.inova8.resolver;

public class NamesClass
{
	//public struct CellKey
	//{
	//    public readonly string Address;
	//    public readonly string Name;
	//    public CellKey(string address, string name)
	//    {
	//        Address = address;
	//        Name = name;
	//    }
	//    public override int GetHashCode()
	//    {
	//        return Address.GetHashCode() ^ Name.GetHashCode();
	//    }
	//    public override bool Equals(object o)
	//    {
	//        if (!(o is CellKey)) return false;
	//        return Address.Equals(((CellKey)o).Address) || Name.Equals(((CellKey)o).Name);
	//    }
	//}
	private java.util.HashMap<String, String> pNames = new java.util.HashMap<String, String>();
	private java.util.HashMap<String, String> pAddresses = new java.util.HashMap<String, String>();

	public final void Add(String Address, String Name)
	{
		if (!pNames.containsKey(Address))
		{
			pNames.put(Address, Name);
		}
		if (!pAddresses.containsKey(Name))
		{
			pAddresses.put(Name, Address);
		}
		return;
	}
	public final int getCount()
	{
		return pNames.size();
	}
	public final String Name(String address)
	{
		return pNames.get(address);
	}
	public final String Address(String name)
	{
		return pAddresses.get(name);
	}
	public final String Label(String address)
	{
		if (pNames.containsKey(address))
		{
			return pNames.get(address);
		}
		else
		{
			return address;
		}
	}
	public final String Key(String label)
	{
		if (label.charAt(0) == '$')
		{
			return label;
		}
		else
		{
			return pAddresses.get(label);
		}
	}
	public final boolean ContainsKey(String address)
	{
		return pNames.containsKey(address);
	}
}