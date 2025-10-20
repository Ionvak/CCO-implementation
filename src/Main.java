import com.cco.*;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;

public class Main {

    public static void main(String[] args) {

        //Build tree
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

        //Build and style chart
        XYChart chart = new XYChartBuilder().width(800).height(600).build();
            chart.getStyler().setChartTitleVisible(false);
            chart.getStyler().setLegendVisible(false);
            chart.getStyler().setXAxisMin(-0.05);
            chart.getStyler().setXAxisMax(0.05);
            chart.getStyler().setYAxisMin(-0.05);
            chart.getStyler().setYAxisMax(0.05);

        //Plot perfusion area
        double[][] perfCircle = arterialTree.getPerfArea();
            XYSeries circle = chart.addSeries("Perfusion Area", perfCircle[0], perfCircle[1]);
            circle.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            circle.setLineColor(Color.BLACK);
            circle.setMarker(SeriesMarkers.NONE);

        //Plot the tree
        double[][] series = arterialTree.getSeries();
            XYSeries segments = chart.addSeries("tree",series[0], series[1]);
            segments.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            segments.setMarker(SeriesMarkers.CIRCLE);
            segments.setLineColor(Color.RED);
            segments.setMarkerColor(Color.YELLOW);


        //Show the chart
        new SwingWrapper(chart).displayChart();
        }
}
