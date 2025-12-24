package com.cco;

/*This class represents the whole tree. It is intended to provide the full interface for
 *building and interacting with the tree.
 * */

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
        params = parameters;
        segments = new HashMap<>();
        isBuilt = false;
        target = 0;
    }

    public void buildTree(){
        SupportingCircle supportingCircle = new SupportingCircle(params);
        supportingCircle.initRoot(segments, params);
        supportingCircle.addBif(segments,segments.get(1L),supportingCircle.toss(params.perfRadius),true);
        target = supportingCircle.getTarget(segments);
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
            System.out.println("Target function value: " + target + "\n");
        }
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
