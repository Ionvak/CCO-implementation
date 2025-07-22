package com.cco;

import java.lang.Math;

/*This class represents a segment within the tree
*
* */

class Segment {

    private static long INDEX = 1;
    final long index; //index of the segment
    long parent = 0; //index of the parent segment. 0 means the segment has no parent (only possible for root).
    long childLeft = 0; //index of the left child of the segment. 0 means the segment has no left child.
    long childRight = 0; //index of the right child of the segment. 0 means the segment has no right child.
    Point proximal; //proximal point of the segment
    Point distal; //distal point of the segment
    double length; //length of the segment
    double radius; // radius of the segment

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
        return Math.PI*Math.pow(radius, 2)*length;
    }

    //Get the distance between two points (the length of the segment)
    public static double getDistance(Point proximal, Point distal) {
        return Math.sqrt( Math.pow(proximal.x - distal.x, 2) + Math.pow(proximal.y - distal.y, 2) );
    }

    //Initialize a segment using the desired radius (usingRadius is set to true), or using the radius inferred from the flow (usingRadius is set to false)
    public Segment(double proximalX, double proximalY,  double distalX, double distalY,  double radiusOrFlow, boolean usingRadius) {
        this.proximal = new Point(proximalX, proximalY);
        this.distal = new Point(distalX, distalY);
        this.index = INDEX++;
        this.length = getDistance(proximal, distal);

        if (usingRadius)
            this.radius = radiusOrFlow;
        else
            this.radius = getRadius(this.length, radiusOrFlow);

    }

}
