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

     Point toss(double perfRadius){
        Random rand = new Random();
        double x = rand.nextDouble() * (2 * perfRadius) -  perfRadius;
        double circleBorder = Math.sqrt(Math.pow(perfRadius, 2) - Math.pow(x, 2));
        double y = rand.nextDouble() * (2 * circleBorder) -  circleBorder;
        return new Point(x,y);
    }

    private double findCritDistance(Segment segment, Point point){
        double dCrit;
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

    private void stretchSegments(Segment s, double factor, double aggregateX,  double aggregateY){
        double deltaX = aggregateX;
        double deltaY = aggregateY;

        s.proximal.x += deltaX;
        s.proximal.y += deltaY;

        deltaX += (s.distal.x - s.proximal.x) * factor;
        deltaY += (s.distal.y - s.proximal.y) * factor;

        s.distal.x += deltaX;
        s.distal.y += deltaY;

        s.length = Segment.findLength(s.proximal, s.distal);
        //double flow = Segment.findFlow();
        //double pressDiff = ;
        //s.radius = Segment.findRadius(parameters, s.length, pressDiff, flow);

        if(s.childLeft != null) stretchSegments(s.childLeft, factor, deltaX, deltaY);
        if(s.childRight != null) stretchSegments(s.childRight, factor, deltaX, deltaY);
    }

     double addBif(HashMap<Long, Segment> tree, Segment iConn, Point iNewDistal, boolean keepChanges){

        Point iConnDistPrev = new Point(iConn.distal.x, iConn.distal.y);
        Point iConnProxPrev = new Point(iConn.proximal.x, iConn.proximal.y);

        iConn.distal.x = iConn.proximal.x + 0.5 * (iConn.distal.x - iConn.proximal.x);
        iConn.distal.y = iConn.proximal.y + 0.5 * (iConn.distal.y - iConn.proximal.y);
        iConn.length = Segment.findLength(iConn.proximal, iConn.distal);
        iConn.proximal = iConn.distal;
        iConn.distal = iConnDistPrev;

        Segment iBif = new Segment(iConnProxPrev, iConn.proximal, 0);
        Segment iNew = new Segment(iBif.distal, iNewDistal, 0);
        iBif.parent = iConn.parent;
        iConn.parent = iBif;
        iNew.parent = iBif;
        iBif.childLeft = iBif.distal.x > iNew.distal.x ? iNew : iConn;
        iBif.childRight = iBif.distal.x <= iNew.distal.x ? iNew : iConn;
        tree.put(iBif.index,  iBif);
        tree.put(iNew.index,  iNew);

        //readjust parameters
        //rescale tree
        return 0;
    }

    private void rescaleTree(HashMap<Long, Segment> tree){

    }


    /*
    ====================================================================================================================
     Beginning of interface between ArterialTree and SupportingCircle class
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
            rootDistal = toss(parameters.perfRadius);
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
        double radius = Segment.findRadius(parameters, length, pressDiff, flow);

        Segment root = new Segment(rootProximal, rootDistal, radius);
        segments.put(root.index, root);
    }

    //Calculate and return the target function value for the tree
    double getTarget(HashMap<Long, Segment> tree){
        double sum = 0;
        for(Segment s: tree.values()) {
            sum += Segment.findVolume(s.radius, s.length);
        }
        return sum;
    }

    void stretchCircle(Segment root, TreeParams parameters){
        double prevArea = this.supportArea;
        this.supportArea = (this.kTot + 1) * Math.pow(parameters.perfRadius, 2) / parameters.nTerminal;
        this.supportRadius = Math.sqrt(this.supportArea / Math.PI);
        double factor = ( this.supportArea / prevArea ) - 1;
        this.threshDistance = Math.sqrt(this.supportArea / this.kTerm);
        stretchSegments(root, factor, 0, 0);
    }

    private void addBifOptimal(HashMap<Long, Segment> tree){

    }


    /*
    TODO:
     - Implement bifurcation addition.
     - Implement procedure for finding the optimal bifurcation.
     - Implement tree rescaling after bifurcation addition.
     */
}
