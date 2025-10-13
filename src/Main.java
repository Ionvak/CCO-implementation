import com.cco.*;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class Main {

    public static void main(String[] args) {
            TreeParams params = new TreeParams(
                    3.6e-3, //viscosity
                    3,      //bifExponent
                    8.38e3, //distalPress
                    1.33e4, //perfPress
                    8.33e-6,//perfFlow
                    0.05,   //perfRadius
                    250     //numTerminal
            );
            ArterialTree arterialTree = new ArterialTree(params);
            arterialTree.buildTree();
            arterialTree.treeDetails();

            XYChart chart = new XYChartBuilder().width(800).height(600).build();

            // Customize Chart
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            chart.getStyler().setChartTitleVisible(false);
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
            chart.getStyler().setMarkerSize(8);
            chart.getStyler().setXAxisMin(-0.05);
            chart.getStyler().setXAxisMax(0.05);
            chart.getStyler().setYAxisMin(-0.05);
            chart.getStyler().setYAxisMax(0.05);

        double[][] series = arterialTree.getSeries();
            XYSeries SegmentsProximal = chart.addSeries("Proximal", series[0], series[1]);
            XYSeries SegmentsDistal = chart.addSeries("Distal", series[2], series[3]);

        double[][] perfCircle = arterialTree.getPerfArea();
            XYSeries topHalf = chart.addSeries("Top half", perfCircle[0], perfCircle[1]);
            XYSeries bottomHalf = chart.addSeries("Bottom Half", perfCircle[0], perfCircle[2]);

            topHalf.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            bottomHalf.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            topHalf.setMarker(SeriesMarkers.NONE);
            bottomHalf.setMarker(SeriesMarkers.NONE);

        // Show it
            new SwingWrapper(chart).displayChart();



        }
}
