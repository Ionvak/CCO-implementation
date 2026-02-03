package com.cco;

import java.lang.Math;

/**
 * This class represents a single segment in the arterial tree.
 */

class Segment {
    private static long INDEX = 1; //Variable used to generate Indexes.
    final long index; //index of the segment.
    Segment parent = null; //index of the parent segment. null means the segment has no parent (only possible for root).
    Segment childLeft = null; //index of the left child of the segment. null means the segment has no left child.
    Segment childRight = null; //index of the right child of the segment. null means the segment has no right child.
    Point proximal; //proximal point of the segment.
    Point distal; //distal point of the segment.
    double radius = 0; // radius of the segment.
    double resistance = 0;
    double childRatio = 0;
    double rightRatio = 0;
    double leftRatio = 0;


    public Segment(Point proximal, Point distal) {
        this.proximal = new Point(proximal.x, proximal.y);
        this.distal = new Point(distal.x, distal.y);
        this.index = INDEX++;
    }

    //Get the total Blood volume within a given segment
    double volume() {
        return Math.PI * Math.pow(radius, 2) * length();
    }

    //Get the length of the segment. It is the cartesian distance between its distal and proximal points.
    double length() {
        return Math.sqrt( Math.pow(proximal.x - distal.x, 2) +
                Math.pow(proximal.y - distal.y, 2) );
    }

    double flow( double termFlow) {
        return nDIST() * termFlow;
    }

    private int nDIST() {
        if (childLeft == null)
            return 1;
        return childLeft.nDIST() + childRight.nDIST();
    }
}
