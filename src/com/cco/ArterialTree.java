package com.cco;

/*This class represents the whole tree. It is intended to provide the full interface for
 *building and interacting with the tree.
 * */

import java.util.HashMap;

public class ArterialTree{

    final HashMap<Integer, Segment> body;
    final TreeParams params;
    private boolean isBuilt;

    //Initialize the tree given the constants passed to the constructor
    public ArterialTree(TreeParams parameters) {
        this.params = parameters;
        this.body = new HashMap<Integer, Segment>();
        this.isBuilt = false;
    }



}
