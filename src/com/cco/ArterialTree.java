package com.cco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.lang.Math;

public class ArterialTree extends NelderMeadOptimizer{
    private final HashMap<Long, Segment> segments; //Hashmap storing all segments of the tree.
    private final TreeParams params; //Physical parameters of the tree
    private boolean isBuilt; //Check for tree build status. False if tree is initialized but not built, true if tree is initialized and built.
    private final double threshDistance; //Threshold distance.
    private static final int nToss = 10; //Number of tosses before threshold distance increase.
    private int kTerm; //Number of terminal segments in supporting circle.
    private int kTot; //Number of segments in supporting circle.
    private Point movedPoint;

    public ArterialTree(TreeParams parameters) {
        params = parameters;
        segments = new HashMap<>();
        isBuilt = false;
        kTot = 1;
        kTerm = 1;
        double supportArea = Math.PI * Math.pow(parameters.perfRadius, 2) / parameters.nTerminal;
        threshDistance = 0.05 * Math.sqrt(supportArea / kTerm);
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

    private Point toss(){
        double radius = params.perfRadius;
        Random rand = new Random();
        double x = rand.nextDouble() * (2 * radius) -  radius;
        double circleBorder = Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2));
        double y = rand.nextDouble() * (2 * circleBorder) -  circleBorder;
        return new Point(x,y);
    }

    private Point newDistal(){
        boolean invalidDistal = true;
        int loopCount = 0;
        double critDistance;
        double threshDist = threshDistance;
        Point proposedDistal = toss();

        while(invalidDistal){
            proposedDistal = toss();
            invalidDistal = false;
            for(Segment s: segments.values()){
                critDistance = findCritDistance(s, proposedDistal);
                if(critDistance < threshDist)
                    invalidDistal = true;
            }
            loopCount++;
            if(loopCount == nToss){
                threshDist -= threshDistance * 0.1;
                loopCount = 0;
            }
        }

        return proposedDistal;
    }

    private void initRoot(){
        Random rand = new Random();
        double perfRadius = params.perfRadius;
        double x = rand.nextDouble() * (2 * perfRadius) -  perfRadius;
        double y = Math.sqrt(Math.pow(perfRadius, 2) - Math.pow(x, 2));
        if(rand.nextDouble() - 0.5 < 0) y *= -1;
        Point rootProximal = new Point(x,y);
        Point rootDistal = newDistal();
        Segment root = new Segment(rootProximal, rootDistal);
        segments.put(root.index, root);
    }

    private double addBif(Long where, Point iNewDistal, boolean keepChanges){
        HashMap<Long, Segment> tree = segments;
        if(!keepChanges)
            tree = new HashMap<>(segments);
        Segment iConn = tree.get(where);

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

        movedPoint = iBif.distal;
        iConn.proximal = movedPoint;
        iNew.proximal = movedPoint;
        double[] x0 = {movedPoint.x, movedPoint.y};
        double[] optimalPoint = fminsearch(x0);
        movedPoint.x = optimalPoint[0];
        movedPoint.y = optimalPoint[1];

        rescaleTree();
        return getTarget();
    }

    private long optimalCandidate(Point distal){
        ArrayList<Double> distances = new ArrayList<>();
        List<Double> candidates = new ArrayList<>();

        for(Segment s: segments.values())
            distances.add(findCritDistance(s,distal));
        distances.sort(null);
        int distancesSize = distances.size();
        if(distancesSize > 20)
            candidates = distances.subList(distancesSize - 20, distancesSize);
        else
            candidates = distances;

        double minTarget = 1, temp;
        long optimal = 0;
        for(Segment s: segments.values()) {
            if((temp = addBif(s.index,distal,false)) < minTarget) {
                minTarget = temp;
                optimal = s.index;
            }
        }

        return optimal;
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

    private void segmentRescale(Segment segment){
        Segment left = segment.childLeft;
        Segment right = segment.childRight;
        double termFlow = params.perfFlow / params.nTerminal;

        if(left != null){
            segmentRescale(left);
            segmentRescale(right);
            segment.childRatio = childRadiiRatio(left.flow(termFlow), right.flow(termFlow), left.resistance, right.resistance);
            segment.leftRatio = parentRadiiRatio(1 / segment.childRatio);
            segment.rightRatio = parentRadiiRatio(segment.childRatio);
            segment.resistance = reducedResistance(params.viscosity, segment.length(), segment.leftRatio, segment.rightRatio, left.resistance, right.resistance);
        }
        else{
            segment.resistance = reducedResistance(params.viscosity, segment.length(),0,0,0,0);
        }
    }

    private void calculateRadii(Segment segment, double radius){
        if(segment.childLeft != null) {
            calculateRadii(segment.childLeft, segment.radius * segment.leftRatio);
            calculateRadii(segment.childRight, segment.radius * segment.rightRatio);
        }
        segment.radius = radius;
    }

    private void rescaleTree(){
        Segment root = segments.get(1L);
        while(root.parent != null) root = root.parent;
        segmentRescale(root);

        root.radius = rootRadius(root.resistance, root.flow(params.perfFlow/params.nTerminal), params.perfPress-params.termPress);
        calculateRadii(root.childLeft, root.radius * root.leftRatio);
        calculateRadii(root.childRight, root.radius * root.rightRatio);
    }

    @Override
    protected double objectiveFunction(double[] x) {
        movedPoint.x = x[0];
        movedPoint.y = x[1];
        rescaleTree();
        return getTarget();
    }

    // End of internal methods. Beginning of tree interface.



    public double getTarget(){
        double sum = 0;
        for(Segment s: segments.values()) {
            sum += s.volume();
        }
        return sum;
    }

    public void buildTree(){
        initRoot();

        Segment root = segments.get(1L);
        while(kTerm < params.nTerminal){
            if(root.childLeft != null)
                root = root.childLeft;
            addBif(root.index,newDistal(),true);
        }

        isBuilt = true;
    }

    public void treeDetails(){
        if(!isBuilt){
            System.out.println("Tree is not built. Nothing to display.");
            return;
        }

        System.out.println("Segments:");
        String segString;
        String result;
        for(Segment s: segments.values()){
            if(s.parent == null) System.out.println("(root)");
            if(s.childLeft == null) System.out.println("(terminal)");
            System.out.println("Segment " + s.index + ":");
            segString =
                    """
                    Proximal:   %s
                    Distal:     %s
                    Length:     %f
                    Radius:     %f""";
            result = String.format(segString,
                    s.proximal.toString(),
                    s.distal.toString(),
                    s.length(),
                    s.radius);

            System.out.println(result);
            if(s.parent != null)
                System.out.println("Parent index: " + s.parent.index);
            if(s.childLeft != null)
                System.out.println("Left child index: " + s.childLeft.index);
            if(s.childRight != null)
                System.out.println("Right child index: " + s.childRight.index);
            System.out.println("\n");
        }
        System.out.println("Target function value: " + getTarget() + "\n");
    }

    public double[][] getSeries(){
        int count = 0;
        double[][] series = new double[2][2 * segments.size()];
        for(Segment s: segments.values()){
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

        int PRECISION = 300;
        double phi;
        double phi_step = 2*Math.PI / PRECISION;
        double[][] series = new double[2][2 * PRECISION];

        for(int i = 0; i < 2 * PRECISION; i++){
            phi = i * phi_step;
            series[0][i] = params.perfRadius * Math.sin(phi);
            series[1][i] = params.perfRadius * Math.cos(phi);
        }

        return series;
    }

}

//TODO:
//visualization fix
//comments
//candidate selection