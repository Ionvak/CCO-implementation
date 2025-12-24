package com.cco;

import java.util.HashMap;
import java.lang.Math;
import java.util.Random;

public class SupportingCircle {
    private double supportArea; //Supporting circle area.
    private double threshDistance; //Threshold distance.
    private int kTerm; //Number of terminal segments in supporting circle.
    private int kTot; //Number of segments in supporting circle.
    private double scale;
    private static final int nToss = 10; //Number of tosses before threshold distance increase.


    SupportingCircle(TreeParams parameters){
        kTot = 1;
        kTerm = 1;
        scale = 1;
        supportArea = Math.PI * Math.pow(parameters.perfRadius, 2) / parameters.nTerminal;
        threshDistance = Math.sqrt(this.supportArea / this.kTerm);
    }

     Point toss(double radius){
        Random rand = new Random();
        double x = rand.nextDouble() * (2 * radius) -  radius;
        double circleBorder = Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2));
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
                Math.pow(segment.length(),2);
    }

    private double findOrthogonal(Segment segment, Point point){
        return Math.abs(
                ((-segment.proximal.y + segment.distal.y) * (point.x - segment.distal.x)) +
                ((segment.proximal.x - segment.distal.x) * (point.y - segment.distal.y))
                ) /
                segment.length();
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

     private double addBif(HashMap<Long, Segment> arterialTree, Segment where, Point iNewDistal, boolean keepChanges){
        Segment iConn;
        HashMap<Long, Segment> tree;

        if(keepChanges){
            iConn = where;
            tree = arterialTree;
        }
        else{
            iConn = new Segment(where.proximal, where.distal, 0);     //find radius
            tree = new HashMap<Long, Segment>(arterialTree);
        }

        Point iConnProxPrev = new Point(iConn.proximal.x, iConn.proximal.y);

        iConn.proximal.x = iConn.proximal.x + 0.5 * (iConn.distal.x - iConn.proximal.x);
        iConn.proximal.y = iConn.proximal.y + 0.5 * (iConn.distal.y - iConn.proximal.y);

        Segment iBif = new Segment(iConnProxPrev, iConn.proximal, 0); //find radius
        Segment iNew = new Segment(iBif.distal, iNewDistal, 0);       //find radius
        iBif.parent = iConn.parent;
        iConn.parent = iBif;
        iNew.parent = iBif;
        iBif.childLeft = iBif.distal.x > iNew.distal.x ? iNew : iConn;
        iBif.childRight = iBif.distal.x <= iNew.distal.x ? iNew : iConn;

        tree.put(iBif.index, iBif);
        tree.put(iNew.index, iNew);
        kTot = kTot + 2;
        kTerm++;

        //readjust segment parameters
        //rescale tree

        return getTarget(tree);
    }

    private void rescaleTree(HashMap<Long, Segment> tree){

    }

    void initRoot(HashMap<Long, Segment> segments, TreeParams parameters){
        Random rand = new Random();
        double perfRadius = parameters.perfRadius;
        double x = rand.nextDouble() * (2 * perfRadius) -  perfRadius;
        double y = Math.sqrt(Math.pow(perfRadius, 2) - Math.pow(x, 2));
        if(rand.nextDouble() - 0.5 < 0) y *= -1;
        Point rootProximal = new Point(x,y);

        boolean distalFound = false;
        int loopCount = 0;
        double critDistance = 0;
        double threshDist = threshDistance;
        Point rootDistal = new Point(0,0);
        while(!distalFound){
            rootDistal = toss(perfRadius);
            loopCount++;
            if(loopCount == nToss){
                threshDist -= threshDistance * 0.1;
                loopCount = 0;
            }
            critDistance = Math.sqrt( Math.pow(rootProximal.x - rootDistal.x, 2) +
                                      Math.pow(rootProximal.y - rootDistal.y, 2) );
            if(critDistance < threshDist) continue;
            distalFound = true;
        }

        double length = critDistance;
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
            sum += s.volume();
        }
        return sum;
    }

    void stretchCircle(HashMap<Long, Segment> tree, TreeParams parameters){
        scale += 1.0 / kTerm;
        supportArea *= scale;
        threshDistance = Math.sqrt(supportArea / kTerm);
    }

    private void getCandidates(){

    }

    private void addBifOptimal(HashMap<Long, Segment> tree){

    }


    /*
    TODO:
     - Implement procedure for finding candidates
     - Implement procedure for finding the optimal bifurcation from among candidates.
     - Implement tree rescaling after bifurcation addition.
     */
}
