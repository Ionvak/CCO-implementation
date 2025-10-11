package com.cco;

import java.util.HashMap;
import java.lang.Math;
import java.util.Random;

public class SupportingCircle {
    private double supportRadius; //Supporting circle radius.
    private double supportArea; //Supporting circle area.
    private double threshDistance; //Threshold distance.
    private int kTerm; //Number of terminal segments in supporting circle.
    private int kTot; //Number of segments in supporting circle.
    private static final int nToss = 10; //Number of tosses before threshold distance increase.


    SupportingCircle(TreeParams parameters){
        this.kTot = 1;
        this.kTerm = 1;
        this.supportArea = parameters.perfArea / parameters.nTerminal;
        this.supportRadius = Math.sqrt(this.supportArea / Math.PI);
        this.threshDistance = Math.sqrt(this.supportArea / this.kTerm);
    }

    private Point toss(){
        double x=0;
        double y=0;

        return new Point(x,y);
    }

    private double findCritDistance(HashMap<Long, Segment> tree, Point point){
        return 0;
    }

    private double findProjection(HashMap<Long, Segment> tree, Point point){
        return 0;
    }

    private double findEndpoints(HashMap<Long, Segment> tree, Point point){
        return 0;
    }

    private double findRadius(TreeParams parameters, double length, double pressDiff, double flow){
        return Math.pow(
                (8 * parameters.viscosity * length * flow) /
                (Math.PI * pressDiff)
                ,0.25);
    }

    private void findOptimal(HashMap<Long, Segment> tree){

    }

    /*
    ====================================================================================================================
     Beginning of interface between ArterialTree and SupportingCircle class
    */

    /**
     *
     * @param segments: Hashmap of all segments in the arterial tree.
     * @param points: Hashmap of all segment endpoints in the arterial tree.
     * @param parameters: Physical parameters of the tree.
     */
    void initRoot(HashMap<Long, Segment> segments, HashMap<Long, Point> points, TreeParams parameters){
        Random rand = new Random();
        double perfRadius = parameters.perfRadius;
        double x = rand.nextDouble() * (2 * perfRadius) -  perfRadius;
        double y = Math.sqrt(Math.pow(perfRadius, 2) - Math.pow(x, 2));
        Point rootProximal = new Point(x,y);

        boolean distalFound = false;
        Point rootDistal = new Point(0,0);
        int loopCount = 0;
        double critDistance = 0;
        while(!distalFound){
            rootDistal = toss();
            loopCount++;
            if(loopCount == nToss){
                threshDistance += this.threshDistance * 0.1;
                loopCount = 0;
            }
            critDistance = findCritDistance(segments, rootDistal);
            if(critDistance > threshDistance) continue;
            distalFound = true;
        }

        double length = Segment.findLength(rootProximal, rootDistal);
        double pressDiff = parameters.perfPress;
        double flow = parameters.termFlow;
        double radius = findRadius(parameters, length, pressDiff, flow);

        Segment root = new Segment(rootProximal, rootDistal, radius);
        segments.put(root.index, root);
        points.put(rootDistal.id, rootDistal);
        points.put(rootProximal.id, rootProximal);
    }

    void stretchCircle(HashMap<Long, Segment> tree){

    }

    double addBif(HashMap<Long, Segment> tree, boolean keepChanges){
        return 0;
    }


    void rescaleTree(HashMap<Long, Segment> tree){

    }
}
