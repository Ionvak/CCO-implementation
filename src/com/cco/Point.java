package com.cco;

/*This class represents a Point within a 2D coordinate space*/

class Point {
    static private long ID = 0;
    long id;
    double x; //x coordinate of the point
    double y; //y coordinate of the point

    public Point(double x, double y) {
        id = ID++;
        this.x = x;
        this.y = y;
    }
}
