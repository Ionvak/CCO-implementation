package com.cco;

import java.lang.Math;

class Segment {

    private static long INDEX = 1;
    final long index; //index of the segment
    Segment parent = null; //index of the parent segment. null means the segment has no parent (only possible for root).
    Segment childLeft = null; //index of the left child of the segment. null means the segment has no left child.
    Segment childRight = null; //index of the right child of the segment. null means the segment has no right child.
    Point proximal; //proximal point of the segment
    Point distal; //distal point of the segment
    double length; //length of the segment
    double radius; // radius of the segment


    //Get the total Blood volume within a given segment
     double findVolume() {
        return Math.PI*Math.pow(radius, 2)*length;
    }

    //Get the length of the segment. It is the distance between its distal and proximal points.
     double findLength() {
        return Math.sqrt( Math.pow(proximal.x - distal.x, 2) + Math.pow(proximal.y - distal.y, 2) );
    }

    //Initialize a segment using the desired radius.
    public Segment(Point proximal, Point distal, double radius) {
        this.proximal = new Point(proximal.x, proximal.y);
        this.distal = new Point(distal.x, distal.y);
        this.index = INDEX++;
        this.length = findLength();
        this.radius = radius;
    }

}
