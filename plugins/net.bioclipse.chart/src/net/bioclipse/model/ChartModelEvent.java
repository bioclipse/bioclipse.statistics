package net.bioclipse.model;

public class ChartModelEvent 
{
	private boolean modelUpdated;

	public ChartModelEvent(boolean modelUpdated) {
		this.modelUpdated = modelUpdated;
	}

	public boolean isModelUpdated() {
		return modelUpdated;
	}
}
