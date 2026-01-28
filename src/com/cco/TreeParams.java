package com.cco;

/**
 * This class represents the physical parameters of the tree. It is data
 * container class used to shorten the parameter lists of methods in
 * methods for other classes.
 */
public class TreeParams {
    final double viscosity;
    final double bifExponent;
    final double perfPress;
    final double termPress;
    final double perfFlow;
    final double perfRadius;
    final int nTerminal;

    public TreeParams(double viscosity, double bifExponent, double perfPress, double termPress, double perfFlow, double perfRadius, int nTerminal) {
        this.viscosity = viscosity;
        this.bifExponent = bifExponent;
        this.perfPress = perfPress;
        this.termPress = termPress;
        this.perfFlow = perfFlow;
        this.perfRadius = perfRadius;
        this.nTerminal = nTerminal;
    }
}
