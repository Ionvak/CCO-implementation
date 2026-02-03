package com.cco;

/*This class represents the whole tree. It is intended to provide the full interface for
 *building and interacting with the tree.
 * */

import java.util.HashMap;
Moimport java.util.Random;

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
    private double supportArea; //Supporting circle area.
    private double threshDistance; //Threshold distance.
    private static final int nToss = 10; //Number of tosses before threshold distance increase.
    private int kTerm; //Number of terminal segments in supporting circle.
    private int kTot; //Number of segments in supporting circle.

    public ArterialTree(TreeParams parameters) {
        params = parameters;
        segments = new HashMap<>();
        isBuilt = false;
        target = 0;
        kTot = 1;
        kTerm = 1;
        supportArea = Math.PI * Math.pow(parameters.perfRadius, 2) / parameters.nTerminal;
        threshDistance = Math.sqrt(this.supportArea / this.kTerm);
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

    Point toss(){
        double radius = params.perfRadius;
        Random rand = new Random();
        double x = rand.nextDouble() * (2 * radius) -  radius;
        double circleBorder = Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2));
        double y = rand.nextDouble() * (2 * circleBorder) -  circleBorder;
        return new Point(x,y);
    }


    private void initRoot(double threshDistance, int nToss){
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

    public void addBif(Long where, double xNewDistal, double yNewDistal, boolean keepChanges){
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

        rescaleTree(arterialTree, params);
    }

    public double getTarget(){
        double sum = 0;
        for(Segment s: segments.values()) {
            sum += s.volume();
        }
        return sum;
    }

    public void buildTree(){
        initRoot(segments, params);
        addBif(segments,params,segments.get(1L), toss(),true);
        target = getTarget(segments);
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
