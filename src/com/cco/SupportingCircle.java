package com.cco;

import java.util.HashMap;
import java.lang.Math;

public class SupportingCircle {
    private double supportRadius;
    private double supportArea;
    private double threshDistance;
    private int kTerm;
    private int kTot;
    private int nToss;

    SupportingCircle(HashMap<Integer, Segment> tree, TreeParams parameters){
        kTot = 1;
        kTerm = 1;

        this.supportArea = parameters.perfArea / parameters.nTerminal;
        this.supportRadius = Math.sqrt(this.supportArea / Math.PI);
        this.threshDistance = Math.sqrt(this.supportArea / this.kTerm);

        initRoot(tree);
    }

    private void initRoot(HashMap<Integer, Segment> tree){

    }

    double addBif(HashMap<Integer, Segment> tree, boolean keepChanges){

        return 0;
    }

    private void findOptimal(HashMap<Integer, Segment> tree){

    }

    private void stretchCircle(HashMap<Integer, Segment> tree){

    }

    private void rescaleTree(HashMap<Integer, Segment> tree){

    }
}
