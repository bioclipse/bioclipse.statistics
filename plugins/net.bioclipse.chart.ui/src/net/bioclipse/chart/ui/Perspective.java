package net.bioclipse.chart.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class Perspective implements IPerspectiveFactory {

    @Override
    public void createInitialLayout( IPageLayout layout ) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);
        layout.setFixed(true);

        layout.addView("net.bioclipse.plugins.views.ChartView", 
                       IPageLayout.LEFT, 1.0f, editorArea);
    }

}
