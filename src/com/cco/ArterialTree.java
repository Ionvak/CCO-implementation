package com.cco;

/*This class represents the whole tree. It is intended to provide the full interface for
 *building and interacting with the tree.
 * */

import java.util.HashMap;

public class ArterialTree extends HashMap<Long, Segment> {

    private static double viscosity;
    private static double distalPressure;
    private static double perfusionPressure;
    private static double N_terminal;
    private static double bifurcationExponent;

}
