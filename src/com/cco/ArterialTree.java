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
        this.target = this.getTarget();
        this.isBuilt = true;
    }

    //Calculate and return the target function value for the tree
    public double getTarget(){
        if(!this.isBuilt) return 0;
        double sum = 0;
        for(Segment s: segments.values()) {
            sum += Segment.findVolume(s.radius, s.length);
        }
        return sum;
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
        for(Segment s: segments.values()){
            System.out.println(s.index + ":");
            segString = """
                        Proximal:   %s,
                        Distal:     %s,
                        Length:     %f,
                        Radius:     %f
                        """;
            if(s.parent != null) segString = segString + ",\nParent: %d";
            if(s.childLeft != null) segString = segString + ",\nChild Left: %d";
            if(s.childRight != null) segString = segString + ",\nChild Right:%d";

            result = String.format(segString,
                    s.proximal.toString(),
                    s.distal.toString(),
                    s.length,
                    s.radius,
                    s.parent != null ? s.parent.index: null,
                    s.childLeft != null ? s.childLeft.index : null,
                    s.childRight != null ? s.childRight.index : null);

            System.out.println(result);
            System.out.println("Target function value: " + target);
        }
    }

}
