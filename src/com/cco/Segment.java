package com.cco;

/*This class represents a segment within the tree
*
* */

class Segment {

    private static long INDEX = 1;
    final long index; //index of the segment
    private long parent = 0; //index of the parent segment. 0 means the segment has no parent (only possible for root).
    private long childLeft = 0; //index of the left child of the segment. 0 means the segment has no left child.
    private long childRight = 0; //index of the right child of the segment. 0 means the segment has no right child.
    private Point proximal; //proximal point of the segment
    private Point distal; //distal point of the segment
    private double length; //length of the segment
    private double radius; // radius of the segment

    //Get the flow based on the radius
    public static double getFlow(double length, double radius) {

        return 0;
    }

    //Get the radius based on the flow
    public static double getRadius(double length, double flow) {

        return 0;
    }

    //Get the total Blood volume within a given segment
    public static double getVolume(double length, double radius) {

        return 0;
    }

    //Get the distance between two points (the length of the segment)
    public static double getDistance(Point proximal, Point distal) {

        return 0;
    }

    //Initialize a segment using the desired radius
    public Segment(Point proximal, Point distal, double radius) {
        this.proximal = proximal;
        this.distal = distal;
        this.index = INDEX++;
        this.length = getDistance(proximal, distal);
        this.radius = radius;
    }

    //Initialize a segment using the desired blood flow
    public Segment(double flow, Point proximal, Point distal) {
        this.proximal = proximal;
        this.distal = distal;
        this.index = INDEX++;
        this.length = getDistance(proximal, distal);
        this.radius = getRadius(this.length, flow);
    }

}
