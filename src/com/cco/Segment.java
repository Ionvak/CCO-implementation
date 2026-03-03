package com.cco;

import java.lang.Math;

 // This class represents a single segment in the arterial tree.

class Segment {
    private static long INDEX = 1; //Variable used to generate Indexes.
    final long index; //index of the segment.
    Segment parent = null; //index of the parent segment. null means the segment has no parent (only possible for root).
    Segment childLeft = null; //index of the left child of the segment. null means the segment has no left child.
    Segment childRight = null; //index of the right child of the segment. null means the segment has no right child.
    Point proximal; //proximal point of the segment.
    Point distal; //distal point of the segment.
    double radius = 0; //radius of the segment.
    double resistance = 0; //reduced resistance of the segment.
    double childRatio = 0; //radius ratio of the left child to the right child.
    double rightRatio = 0; //radius ratio of the right child to segment.
    double leftRatio = 0; //radius ratio of the left child to segment.


    public Segment(Point proximal, Point distal) {
        this.proximal = new Point(proximal.x, proximal.y);
        this.distal = new Point(distal.x, distal.y);
        this.index = INDEX++;
    }

    /**
     * Calculates and returns the volume of the segment.
     * @return
     * The volume of the segment.
     */
    double volume() {
        return Math.PI * Math.pow(radius, 2) * length();
    }

    /**
     * Calculates and returns the length of the segment.
      * @return
     * The length of the segment.
     */
    double length() {
        return Math.sqrt( Math.pow(proximal.x - distal.x, 2) +
                Math.pow(proximal.y - distal.y, 2) );
    }

    /**
     * Calculates and returns the flow of the segment.
     * @param termFlow
     * The terminal flow of the tree in which the segment resides.
     * @return
     * The flow of the segment.
     */
    double flow(double termFlow) {
        return nDIST() * termFlow;
    }

    /**
     * Calculates and returns the number of terminal segments downstream of the segment.
     * @return
     * The number of terminal segments downstream of the segment
     */
    private int nDIST() {
        if (childLeft == null)
            return 1;
        return childLeft.nDIST() + childRight.nDIST();
    }
}
