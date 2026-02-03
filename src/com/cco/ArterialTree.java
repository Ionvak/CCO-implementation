package com.cco;

/*This class represents the whole tree. It is intended to provide the full interface for
 *building and interacting with the tree.
 * */

import java.util.HashMap;
import java.util.Random;
import java.lang.Math;

/**
 * This class represents the full tree. The build logic and internal operations
 * are handled by the SupportingCircle class. This class handles the UI and
 * allows for high level control over the tree.
 */

public class ArterialTree extends NelderMeadOptimizer{
    private final HashMap<Long, Segment> segments; //Hashmap storing all segments of the tree.
    private final TreeParams params; //Physical parameters of the tree
    private boolean isBuilt; //Check for tree build status. False if tree is initialized but not built, true if tree is initialized and built.
    private double target; //The value of the target function for the tree.
    private final double threshDistance; //Threshold distance.
    private static final int nToss = 10; //Number of tosses before threshold distance increase.
    private int kTerm; //Number of terminal segments in supporting circle.
    private int kTot; //Number of segments in supporting circle.
    private Point movedPoint;

    public ArterialTree(TreeParams parameters) {
        params = parameters;
        segments = new HashMap<>();
        isBuilt = false;
        target = 0;
        kTot = 1;
        kTerm = 1;
        double supportArea = Math.PI * Math.pow(parameters.perfRadius, 2) / parameters.nTerminal;
        threshDistance = Math.sqrt(supportArea / kTerm);
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


    private void initRoot(){
        Random rand = new Random();
        double perfRadius = params.perfRadius;
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
            rootDistal = toss();
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

    private void addBif(Long where, double xNewDistal, double yNewDistal, boolean keepChanges){
        Segment iConn;
        HashMap<Long, Segment> tree;
        Point iNewDistal = new Point(xNewDistal, yNewDistal);

        if(keepChanges){
            iConn = segments.get(where);
            tree = segments;
        }
        else{
            iConn = new Segment(segments.get(where).proximal, segments.get(where).distal);     //find radius
            tree = new HashMap<>(segments);
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

        movedPoint = iBif.distal;
        double[] x0 = {movedPoint.x, movedPoint.y};
        double[] optimalPoint = fminsearch(x0);
        movedPoint.x = optimalPoint[0];
        movedPoint.y = optimalPoint[1];
        rescaleTree();
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

    // End of internal methods, beginning of tree interface.



    public double getTarget(){
        double sum = 0;
        for(Segment s: segments.values()) {
            sum += s.volume();
        }
        return sum;
    }

    public void buildTree(){
        initRoot();
        Point newDistal = toss();
        addBif(1L, newDistal.x, newDistal.y, true);
        target = getTarget();
        isBuilt = true;
    }

    //Print a list of the segments with their parameters, and the target function value for the tree.
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
        System.out.println("Target function value: " + target + "\n");
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
