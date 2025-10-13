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
        this.supportArea = Math.pow(parameters.perfRadius, 2) / parameters.nTerminal;
        this.supportRadius = Math.sqrt(this.supportArea / Math.PI);
        this.threshDistance = Math.sqrt(this.supportArea / this.kTerm);
    }

    private Point toss(TreeParams parameters){
        Random rand = new Random();
        double perfRadius = parameters.perfRadius;
        double x = rand.nextDouble() * (2 * perfRadius) -  perfRadius;
        double circleBorder = Math.sqrt(Math.pow(perfRadius, 2) - Math.pow(x, 2));
        double y = rand.nextDouble() * (2 * circleBorder) -  circleBorder;

        return new Point(x,y);
    }

    private double findCritDistance(Segment segment, Point point){
        double dCrit = 0;
        double dProjection = findProjection(segment, point);
        if(0 <= dProjection && dProjection <=1)
            dCrit = findOrthogonal(segment, point);
        else
            dCrit = findEndpoints(segment, point);
        return dCrit;
    }

    private double findProjection(Segment segment, Point point){
        return (
                ((segment.proximal.x - segment.distal.x) * (point.x - segment.distal.x)) +
                ((segment.proximal.y - segment.distal.y) * (point.y - segment.distal.y))
                ) /
                Math.pow(segment.length,2);
    }

    private double findOrthogonal(Segment segment, Point point){
        return Math.abs(
                ((-segment.proximal.y + segment.distal.y) * (point.x - segment.distal.x)) +
                ((segment.proximal.x - segment.distal.x) * (point.y - segment.distal.y))
                ) /
                segment.length;
    }

    private double findEndpoints(Segment segment, Point point){
        return Math.min(
                Math.sqrt( Math.pow(point.x - segment.distal.x, 2) +
                        Math.pow(point.y - segment.distal.y, 2) )
                ,
                Math.sqrt( Math.pow(point.x - segment.proximal.x, 2) +
                        Math.pow(point.y - segment.proximal.y, 2) )
        );
    }

    private double findRadius(TreeParams parameters, double length, double pressDiff, double flow){
        return Math.pow(
                (8 * parameters.viscosity * length * flow) /
                (Math.PI * pressDiff)
                ,0.25);
    }

    private void findOptimal(HashMap<Long, Segment> tree){

    }

    //Calculate and return the target function value for the tree
    double getTarget(HashMap<Long, Segment> tree){
        double sum = 0;
        for(Segment s: tree.values()) {
            sum += Segment.findVolume(s.radius, s.length);
        }
        return sum;
    }

    /*
    ====================================================================================================================
     Beginning of interface between ArterialTree and SupportingCircle class
    */

    /**
     *
     * @param segments: Hashmap of all segments in the arterial tree.
     * @param parameters: Physical parameters of the tree.
     */
    void initRoot(HashMap<Long, Segment> segments, TreeParams parameters){
        Random rand = new Random();
        double perfRadius = parameters.perfRadius;
        double x = rand.nextDouble() * (2 * perfRadius) -  perfRadius;
        double y = Math.sqrt(Math.pow(perfRadius, 2) - Math.pow(x, 2));
        if(rand.nextDouble() - 0.5 < 0) y *= -1;
        Point rootProximal = new Point(x,y);

        boolean distalFound = false;
        Point rootDistal = new Point(0,0);
        int loopCount = 0;
        double critDistance = 0;
        double threshDist = this.threshDistance;
        while(!distalFound){
            rootDistal = toss(parameters);
            loopCount++;
            if(loopCount == nToss){
                threshDist -= this.threshDistance * 0.1;
                loopCount = 0;
            }
            critDistance = Segment.findLength(rootProximal, rootDistal);
            if(critDistance < threshDist) continue;
            distalFound = true;
        }

        double length = Segment.findLength(rootProximal, rootDistal);
        double pressDiff = parameters.perfPress - parameters.distalPress;
        double flow = parameters.perfFlow;
        double radius = findRadius(parameters, length, pressDiff, flow);

        Segment root = new Segment(rootProximal, rootDistal, radius);
        segments.put(root.index, root);
    }

    void stretchCircle(HashMap<Long, Segment> tree){

    }

    double addBif(HashMap<Long, Segment> tree, boolean keepChanges){
        return 0;
    }


    void rescaleTree(HashMap<Long, Segment> tree){

    }

    /*
    TODO:
     - Implement bifurcation addition and more generally
        improve on tree building procedure.
     - Implement procedure for finding the optimal bifurcation.
     - Implement supporting tree stretching.
     - Implement tree rescaling after bifurcation addition.
     - Move getTarget to SupportingCircle and name it findTarget
     */
}
