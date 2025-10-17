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
        this.params = parameters;
        this.segments = new HashMap<Long, Segment>();
        this.isBuilt = false;
        this.target = 0;
    }

    public void buildTree(){
        SupportingCircle supportingCircle = new SupportingCircle(this.params);
        supportingCircle.initRoot(this.segments, this.params);
        supportingCircle.addBif(this.segments,this.segments.get(1L),supportingCircle.toss(this.params.perfRadius),true);
        this.target = supportingCircle.getTarget(this.segments);
        this.isBuilt = true;
    }

    //Print a list of the segments with their parameters, and the target function value for the tree.
    public void treeDetails(){
        if(!this.isBuilt){
            System.out.println("Tree is not built. Nothing to display.");
            return;
        }

        System.out.println("Segments:");
        String segString;
        String result;
        for(Segment s: this.segments.values()){
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
                    s.length,
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
        double[][] series = new double[2][2 * this.segments.size()];
        for(Segment s: this.segments.values()){
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
        double STEP = 0.0001;
        int PRECISION = 2 * (int)(params.perfRadius / STEP);
        double[][] series = new double[2][2 * PRECISION];

        int i = 0;
        double x = -params.perfRadius;
        while(i < PRECISION){
            series[0][i] = x;
            series[1][i] = Math.sqrt( Math.pow(params.perfRadius, 2) - Math.pow(x, 2) );
            x += STEP;
            i++;
        }
        i = 0;
        x = params.perfRadius;
        while(i < PRECISION){
            series[0][i + PRECISION] = x;
            series[1][i + PRECISION] = - Math.sqrt( Math.pow(params.perfRadius, 2) - Math.pow(x, 2) );
            x -= STEP;
            i++;
        }

        return series;
    }

}
