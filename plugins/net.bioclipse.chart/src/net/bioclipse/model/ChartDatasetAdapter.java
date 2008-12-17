package net.bioclipse.model;
import java.util.ArrayList;
import java.util.List;
import net.bioclipse.chart.events.CellData;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
/**
 * XYDataset adapter that maps cells to data points.
 */
public class ChartDatasetAdapter {
        private XYDataset data;
        private List<CellData> cellData;
        private String xHeader, yHeader;
        public ChartDatasetAdapter( XYDataset dataset){
                setData(dataset);
                cellData = new ArrayList<CellData>();
        }
        public ChartDatasetAdapter( XYDataset dataset, List<CellData> cellDataList){
                setData(dataset);
                this.cellData = cellData;
        }
        public void setYHeader(String yHeader) {
                this.yHeader = yHeader;
        }
        public String getYHeader() {
                return yHeader;
        }
        public void setXHeader(String xHeader) {
                this.xHeader = xHeader;
        }
        public String getXHeader() {
                return xHeader;
        }
        public void setData(XYDataset data) {
                this.data = data;
        }
        public XYDataset getData() {
                return data;
        }
}
