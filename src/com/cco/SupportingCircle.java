package com.cco;

import java.util.HashMap;
import java.lang.Math;
import java.util.Random;

public class SupportingCircle {
    private double supportRadius;
    private double supportArea;
    private double threshDistance;
    private int kTerm;
    private int kTot;
    private static final int nToss = 10;

    SupportingCircle(HashMap<Long, Segment> tree, TreeParams parameters){
        kTot = 1;
        kTerm = 1;

        this.supportArea = parameters.perfArea / parameters.nTerminal;
        this.supportRadius = Math.sqrt(this.supportArea / Math.PI);
        this.threshDistance = Math.sqrt(this.supportArea / this.kTerm);

        initRoot(tree, parameters);
    }

    private void initRoot(HashMap<Long, Segment> tree, TreeParams parameters){
        Random rand = new Random();
        double x = rand.nextDouble() * (2 * parameters.perfRadius) -  parameters.perfRadius;
        double y = Math.sqrt(Math.pow(parameters.perfRadius, 2) - Math.pow(x, 2));
        Point rootProximal = new Point(x,y);

        boolean distalFound = false;
        Point rootDistal = new Point(0,0);
        while(!distalFound){
            rootDistal = toss();
            if(!testProjection(tree, rootDistal)) continue;
            if(!testEndpoints(tree, rootDistal)) continue;
            if(!testThreshold(tree, rootDistal)) continue;
            distalFound = true;
        }
        
        double length = Segment.findLength(rootProximal, rootDistal);
        double radius = 0;
        /*
        TODO: Implement a way to calculate pressure difference.
            Use it to find the the resistance using the flow, and
            thus the radius that the segment should have.
        */
        Segment root = new Segment(rootProximal, rootDistal, radius);
        tree.put(root.index, root);
    }

    /*
    TODO: Implement interface functions beginning with toss.
    */

    private Point toss(){
        double x=0;
        double y=0;

        return new Point(x,y);
    }

    private boolean testProjection(HashMap<Long, Segment> tree, Point point){
        return false;
    }

    private boolean testEndpoints(HashMap<Long, Segment> tree, Point point){
        return false;
    }

    private boolean testThreshold(HashMap<Long, Segment> tree, Point point){
        return false;
    }

    private void stretchCircle(HashMap<Long, Segment> tree){

    }

    double addBif(HashMap<Long, Segment> tree, boolean keepChanges){

        return 0;
    }

    private void findOptimal(HashMap<Long, Segment> tree){

    }

    private void rescaleTree(HashMap<Long, Segment> tree){

    }
}
