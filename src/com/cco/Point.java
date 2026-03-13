package com.cco;

/*This class represents a Point within a 2D coordinate space*/

class Point {
    double x; //x coordinate of the point
    double y; //y coordinate of the point

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return "(" + x + ", " + y + ")";
    }

    /**
     * Returns the cross product of the vector and the vector in the method parameter.
     * @param v
     * The vector with which to perform the cross product operation.
     * @return
     * The result of the cross product operation.
     */
    public double crossProd(Point v){
        return x * v.y - y * v.x;
    }

    /**
     * Returns the dot product of the vector and the vector in the method parameter.
     * @param v
     * The vector with which to perform the dot product operation.
     * @return
     * The result of the dot product operation.
     */
    public double dotProd(Point v){
        return x * v.x + y * v.y;
    }

    /**
     * Returns the vector addition result of the vector and the vector in the method parameter.
     * @param v
     * The vector with which to perform the vector addition operation.
     * @return
     * The result of the vector addition operation.
     */
    public Point vecAdd(Point v){
        return new Point(x + v.x, y + v.y);
    }

    /**
     * Returns the vector subtraction result of the vector and the vector in the method parameter.
     * @param v
     * The vector with which to perform the vector subtraction operation.
     * @return
     * The result of the vector subtraction operation.
     */
    public Point vecDiff(Point v){
        return new Point(x - v.x, y - v.y);
    }
}
