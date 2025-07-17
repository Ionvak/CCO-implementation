package com.cco;

public class Point {

    private static long ID;
    long id;
    final double x;
    final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.id = ID++;
    }

}
