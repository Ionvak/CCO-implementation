package com.cco;

/**
 * This class represents the physical parameters of the tree. It is data
 * container class used to shorten the parameter lists of methods in
 * methods for other classes.
 */
public class TreeParams {
    final double viscosity;
    final double bifExponent;
    final double distalPress;
    final double perfPress;
    final double perfFlow;
    final double perfArea;
    final double perfRadius;
    final double termFlow;
    final int nTerminal;

    public TreeParams(double viscosity, double bifExponent, double distalPress, double perfPress, double perfFlow, double perfArea, double perfRadius, double termFlow, int numTerminal) {
        this.viscosity = viscosity;
        this.bifExponent = bifExponent;
        this.distalPress = distalPress;
        this.perfPress = perfPress;
        this.perfFlow = perfFlow;
        this.perfArea = perfArea;
        this.perfRadius = perfRadius;
        this.termFlow = termFlow;
        this.nTerminal = numTerminal;
    }
}
