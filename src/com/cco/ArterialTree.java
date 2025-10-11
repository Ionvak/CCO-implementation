package com.cco;

/*This class represents the whole tree. It is intended to provide the full interface for
 *building and interacting with the tree.
 * */

import java.util.HashMap;

public class ArterialTree{

    private final HashMap<Long, Segment> segments;
    private final HashMap<Long, Point> endPoints;
    private final TreeParams params;
    private boolean isBuilt;
    private double target;

    //Initialize the tree given the constants passed to the constructor
    public ArterialTree(TreeParams parameters) {
        this.params = parameters;
        this.segments = new HashMap<Long, Segment>();
        this.endPoints = new HashMap<Long, Point>();
        this.isBuilt = false;
        this.target = 0;
    }

    public void buildTree(){

    }

    public double getTarget(){
        if(!this.isBuilt) return 0;
        double sum = 0;
        for(Segment s: segments.values()) {
            sum += Segment.findVolume(s.radius, s.length);
        }
        return sum;
    }

    public void treeDetails(){
        if(!this.isBuilt){
            System.out.println("Tree is not built. Nothing to display.");
            return;
        };

        System.out.println("Segments:");
        for(Segment s: segments.values()){
            System.out.println(s.index + ":");
            String segString = """
                    Proximal:   %s,
                    Distal:     %s,
                    Length:     %f,
                    Radius:     %f,
                    Parent:     %d,
                    Left Child: %d,
                    Right Child:%d,
                    """;
            String result = String.format(
                    s.proximal.toString(),
                    s.distal.toString(),
                    s.length,
                    s.radius,
                    s.parent.index,
                    s.childLeft.index,
                    s.childRight.index
            );

            System.out.println(result);
            System.out.println("Target function value: " + target);
        }
    }

}
