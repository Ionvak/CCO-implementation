package com.cco;

/*This class represents a Point within the coordinate space*/

class Point {

    private static long ID;
    final long id; //id of the point
    private double x; //x coordinate of the point
    private double y; //y coordinate of the point

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.id = ID++;
    }

}
