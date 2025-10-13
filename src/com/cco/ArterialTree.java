package com.cco;

/*This class represents the whole tree. It is intended to provide the full interface for
 *building and interacting with the tree.
 * */

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.HashMap;

/**
 * This class represents the full tree. The build logic and internal operations
 * are handled by the SupportingCircle class. This class handles the UI and
 * allows for high level control over the tree.
 */

public class ArterialTree{
    private final HashMap<Long, Segment> segments; //Hashmap storing all segments of the tree.
    private final TreeParams params; //Physical parameters of the tree
    private boolean isBuilt; //Check for tree build status. False if tree is initialized but not built, true if tree is initialized and built.
    private double target; //The value of the target function for the tree.


    public ArterialTree(TreeParams parameters) {
        this.params = parameters;
        this.segments = new HashMap<Long, Segment>();
        this.isBuilt = false;
        this.target = 0;
    }

    public void buildTree(){
        SupportingCircle supportingCircle = new SupportingCircle(this.params);
        supportingCircle.initRoot(this.segments, this.params);
        this.target = supportingCircle.getTarget(this.segments);
        this.isBuilt = true;
    }

    //Print a list of the segments with their parameters, and the target function value for the tree.
    public void treeDetails(){
        if(!this.isBuilt){
            System.out.println("Tree is not built. Nothing to display.");
            return;
        }

        System.out.println("Segments:");
        String segString;
        String result;
        for(Segment s: this.segments.values()){
            System.out.println(s.index + ":");
            segString =
                    """
                    Proximal:   %s,
                    Distal:     %s,
                    Length:     %f,
                    Radius:     %f,
                    """;
                    if(s.parent != null) segString = segString + ",\nParent: %d";
                    if(s.childLeft != null) segString = segString + ",\nChild Left: %d";
                    if(s.childRight != null) segString = segString + ",\nChild Right:%d";

            result = String.format(segString,
                    s.proximal.toString(),
                    s.distal.toString(),
                    s.length,
                    s.radius,
                    s.parent != null ? s.parent.index : null,
                    s.childLeft != null ? s.childLeft.index : null,
                    s.childRight != null ? s.childRight.index : null);

            System.out.println(result);
            System.out.println("Target function value: " + target);
        }
    }

    public double[][] getSeries(){
        int count = 0;
        double[][] series = new double[2][2 * this.segments.size()];
        for(Segment s: this.segments.values()){
            series[0][count] = s.proximal.x;
            series[1][count] = s.proximal.y;
            count++;
            series[0][count] = s.distal.x;
            series[1][count] = s.distal.y;
            count++;
        }
        return series;
    }

    public double[][] getPerfArea(){
        double STEP = 0.0001;
        int PRECISION = 2 * (int)(params.perfRadius / STEP);
        double[][] series = new double[2][2 * PRECISION];

        int i = 0;
        double x = -params.perfRadius;
        while(i < PRECISION){
            series[0][i] = x;
            series[1][i] = Math.sqrt( Math.pow(params.perfRadius, 2) - Math.pow(x, 2) );
            x += STEP;
            i++;
        }
        i = 0;
        x = params.perfRadius;
        while(i < PRECISION){
            series[0][i + PRECISION] = x;
            series[1][i + PRECISION] = - Math.sqrt( Math.pow(params.perfRadius, 2) - Math.pow(x, 2) );
            x -= STEP;
            i++;
        }

        return series;
    }

    public void plotTree(XYChart chart){
        double[][] series = new double[2][2];
        for(Segment s: this.segments.values()){
            series[0][0] = s.proximal.x;
            series[1][0] = s.proximal.y;
            series[0][1] = s.distal.x;
            series[1][1] = s.distal.y;

            XYSeries segment = chart.addSeries("Segment: " + s.index, series[0], series[1]);
                segment.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
                segment.setLineColor(Color.RED);
                segment.setMarker(SeriesMarkers.CIRCLE);
                segment.setMarkerColor(Color.YELLOW);
        }
    }
}
