package com.cco;

/*This class represents the whole tree. It is intended to provide the full interface for
 *building and interacting with the tree.
 * */

import java.util.HashMap;

public class ArterialTree extends HashMap<Long, Segment> {

    //The constants used to initialize the tree.
    final double viscosity;
    final double bifurcationExponent;
    final double distalPressure;
    final double perfusionPressure;
    final double perfusionFlow;
    final double perfusionArea;
    final int N_terminal;

    //Process variables for the tree
    private double supportRadius;
    private int k_term;
    private int k_tot;

    //Initialize the tree given the constants passed to the constructor
    public ArterialTree(double viscosity, double bifurcationExponent, double distalPressure,  double perfusionPressure, double perfusionFlow, double perfusionArea, int N_terminal) {
        this.viscosity = viscosity;
        this.bifurcationExponent = bifurcationExponent;
        this.distalPressure = distalPressure;
        this.perfusionPressure = perfusionPressure;
        this.perfusionFlow = perfusionFlow;
        this.perfusionArea = perfusionArea;
        this.N_terminal = N_terminal;

        while(k_term != N_terminal){
            //code for building the tree
        }
    }

    public void displayTree(){
        //code for displaying the tree
    }

}
