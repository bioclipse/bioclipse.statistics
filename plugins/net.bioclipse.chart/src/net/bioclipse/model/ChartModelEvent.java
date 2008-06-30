package net.bioclipse.model;

public class ChartModelEvent 
{
	private ChartEventType type;

	public ChartModelEvent(ChartEventType eventType) {
		type = eventType;
	}
	
	public ChartEventType getEventType()
	{
		return type;
	}

}
