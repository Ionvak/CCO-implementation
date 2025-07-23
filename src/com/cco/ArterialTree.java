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
    final Segment root;
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

        //Root proximal and distal should be randomly selected
        this.root = new Segment(0,0,1,1,perfusionFlow/N_terminal,viscosity,perfusionPressure,distalPressure);

        while(k_term != N_terminal){
            //code for building the tree
        }
    }

    //Used exclusively for testing
    Segment getRoot(){
        return this.root;
    }

    //Helper method for getting the threshold distance
    static private double thresholdDistance(){

        return 0;
    }

    //Helper method for stretching the supporting circle. Returns true if successful, false otherwise
    static private boolean supportCircleStretch(){

        return false;
    }

    //Helper method for adding a bifurcation, and thus a new segment, into the tree. returns the
    //index of the added segment if successful, 0 otherwise
    static private long addBifurcation(){

        return 0;
    }

    //Helper method for testing a bifurcation from among the viable candidates. Returns the blood volume
    //after geometric optimization
    static private double testBifurcation(){

        return 0;
    }

    //Helper method for deciding if the projection distance (d_proj) is such that orthogonal distance
    //is critical
    static private boolean findProjection(){

        return false;
    }

    //Helper method for getting the orthogonal distance
    static private double orthogonalDistance(){

        return 0;
    }

    //Helper method for getting the distance between the new location and one of the endpoints of
    //an already existing segment
    static private double endpointDistance(){

        return 0;
    }

    //Helper method for rescaling the tree
    static private boolean rescaleTree(){

        return false;
    }

    //Used for getting a graphical representation of the tree
    public void displayTree(){
        //code for displaying the tree
    }

}
