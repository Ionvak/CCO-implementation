package com.cco;

/*This class represents a Point within the coordinate space*/

class Point {

    private double x; //x coordinate of the point
    private double y; //y coordinate of the point

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }
}
