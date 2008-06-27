package net.bioclipse.model;

import org.eclipse.ui.IEditorPart;

/**
 * Manages data about a chart. Where it comes from, which indices should be marked in its source
 * @author Eskil Andersen
 *
 */
public class ChartDescriptor {
	private IEditorPart source;
	private int[] indices;
	private int plotType;
	private String xLabel, yLabel;
	
	public ChartDescriptor(IEditorPart source, int[] indices, int plotType,
			String xLabel, String yLabel) {
		super();
		this.source = source;
		this.indices = indices;
		this.plotType = plotType;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
	}

	public String getXLabel() {
		return xLabel;
	}

	public String getYLabel() {
		return yLabel;
	}

	public IEditorPart getSource() {
		return source;
	}
	
	public int getPlotType() {
		return plotType;
	}

	/**
	 * 
	 * @return The indices of the columns from where the data was collected (i.e. MatrixEditor)
	 */
	public int[] getSourceIndices() {
		return indices;
	}
}
