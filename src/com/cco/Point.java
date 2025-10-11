package com.cco;

/*This class represents a Point within a 2D coordinate space*/

class Point {
    private static long ID = 0;
    final long id;
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
