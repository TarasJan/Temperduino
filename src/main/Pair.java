package main;

public class Pair {

	protected float value;
	protected String label;
	
	public Pair(String label,float value)
	{
		this.label = label;
		this.value = value;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public float getValue()
	{
		return value;
	}
	
}
