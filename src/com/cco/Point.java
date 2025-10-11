package com.cco;

/*This class represents a Point within a 2D coordinate space*/

class Point {
    private static long ID = 1; //Variable used to generate IDs.
    final long id; //ID of the point.
    double x; //x coordinate of the point
    double y; //y coordinate of the point

    public Point(double x, double y) {
        this.id = ID++;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return "(" + this.x + ", " + this.y + ")";
    }
}
