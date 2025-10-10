package com.cco;

public class TreeParams {
    final double viscosity;
    final double bifExponent;
    final double distalPress;
    final double perfPress;
    final double perfFlow;
    final double perfArea;
    final double perfRadius;
    final int nTerminal;

    public TreeParams(double viscosity, double bifExponent, double distalPress, double perfPress, double perfFlow, double perfArea, double perfRadius, int numTerminal) {
        this.viscosity = viscosity;
        this.bifExponent = bifExponent;
        this.distalPress = distalPress;
        this.perfPress = perfPress;
        this.perfFlow = perfFlow;
        this.perfArea = perfArea;
        this.perfRadius = perfRadius;
        this.nTerminal = numTerminal;
    }
}
