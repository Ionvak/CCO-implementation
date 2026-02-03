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



    private double childRadiiRatio(double flowi, double flowj, double resistancei, double resistancej){
        return Math.pow((flowi * resistancei)/(flowj * resistancej), 0.25);
    }

    private double parentRadiiRatio(double childRatio){
        return Math.pow(1 + Math.pow(childRatio, 3), -1.0/3);
    }

    private double rootRadius(double rootResistance, double rootFlow, double pressDiff){
        return Math.pow(rootResistance * rootFlow / pressDiff, 1.0/4);
    }

    private double reducedResistance(double viscosity, double length, double leftRatio, double rightRatio, double leftResistance, double rightResistance){
        if(leftResistance == 0){
            return (8 * viscosity * length)/(Math.PI);
        }
        else return (8 * viscosity * length)/(Math.PI) +
                Math.pow(Math.pow(leftRatio,4) / (leftResistance) + Math.pow(rightRatio,4) / (rightResistance) ,-1);
    }

    private void segmentRescale(Segment segment, double viscosity, double termFlow){
        Segment left = segment.childLeft;
        Segment right = segment.childRight;

        if(left != null){
            segmentRescale(left, viscosity, termFlow);
            segmentRescale(right, viscosity, termFlow);
            segment.childRatio = childRadiiRatio(left.flow(termFlow), right.flow(termFlow), left.resistance, right.resistance);
            segment.leftRatio = parentRadiiRatio(1 / segment.childRatio);
            segment.rightRatio = parentRadiiRatio(segment.childRatio);
            segment.resistance = reducedResistance(viscosity, segment.length(), segment.leftRatio, segment.rightRatio, left.resistance, right.resistance);
        }
        else{
            segment.resistance = reducedResistance(viscosity, segment.length(),0,0,0,0);
        }
    }

    private void calculateRadii(Segment segment, double radius){
        if(segment.childLeft != null) {
            calculateRadii(segment.childLeft, segment.radius * segment.leftRatio);
            calculateRadii(segment.childRight, segment.radius * segment.rightRatio);
        }
        segment.radius = radius;
    }

    private void rescaleTree(HashMap<Long, Segment> Tree, TreeParams params){
        Segment root = Tree.get(1L);
        while(root.parent != null) root = root.parent;
        segmentRescale(root, params.viscosity, params.perfFlow/params.nTerminal);

        root.radius = rootRadius(root.resistance, root.flow(params.perfFlow/params.nTerminal), params.perfPress-params.termPress);
        calculateRadii(root.childLeft, root.radius * root.leftRatio);
        calculateRadii(root.childRight, root.radius * root.rightRatio);
    }

     public void addBif(HashMap<Long, Segment> arterialTree, TreeParams params, Segment where, Point iNewDistal, boolean keepChanges){
        Segment iConn;
        HashMap<Long, Segment> tree;

        if(keepChanges){
            iConn = where;
            tree = arterialTree;
        }
        else{
            iConn = new Segment(where.proximal, where.distal);     //find radius
            tree = new HashMap<>(arterialTree);
        }

        Point iConnProxPrev = new Point(iConn.proximal.x, iConn.proximal.y);

        iConn.proximal.x = iConn.proximal.x + 0.5 * (iConn.distal.x - iConn.proximal.x);
        iConn.proximal.y = iConn.proximal.y + 0.5 * (iConn.distal.y - iConn.proximal.y);

        Segment iBif = new Segment(iConnProxPrev, iConn.proximal); //find radius
        Segment iNew = new Segment(iBif.distal, iNewDistal);       //find radius
        iBif.parent = iConn.parent;
        iConn.parent = iBif;
        iNew.parent = iBif;
        iBif.childLeft = iBif.distal.x > iNew.distal.x ? iNew : iConn;
        iBif.childRight = iBif.distal.x <= iNew.distal.x ? iNew : iConn;

        tree.put(iBif.index, iBif);
        tree.put(iNew.index, iNew);
        kTot = kTot + 2;
        kTerm++;

        rescaleTree(arterialTree, params);
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
        double critDistance;
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

        Segment root = new Segment(rootProximal, rootDistal);
        segments.put(root.index, root);

    }

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

}
