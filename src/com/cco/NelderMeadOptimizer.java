package com.cco;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

// This class was provided entirely by Dr. Jakub Wagner

public class NelderMeadOptimizer {

    private final static double TOL_X_DEFAULT = 1e-4,
        TOL_FUN_DEFAULT = 1e-4;

    protected double objectiveFunction(double[] x) {
        return Double.NaN;
    }

    /**
     * Finds minimum of {@code objectiveFunction} using the Nelder-Mead iterative algorithm.
     * @param x0        starting point for optimization
     * @param tolX      termination tolerance on norm of step
     * @param tolFun    termination tolerance on function value
     * @param maxIter   maximum number of iterations
     */
    protected double[] fminsearch(double[] x0, double tolX, double tolFun, int maxIter) {
        /* Nelder-Mead optimization.
        Implementation based on: https://www.mathworks.com/help/matlab/math/optimizing-nonlinear-functions.html#bsgpq6p-11 */

        int n = x0.length;

        double[][] simplex = new double[n + 1][n];
        double[] f = new double[n + 1];

        simplex[0] = Arrays.copyOf(x0, n);

        f[0] = objectiveFunction(simplex[0]);

        for (int i = 0; i < n; i++) {
            simplex[i + 1] = Arrays.copyOf(x0, n);
            if (x0[i] == 0)
                simplex[i + 1][i] = 0.00025;
            else
                simplex[i + 1][i] = x0[i] * 1.05;

            f[i + 1] = objectiveFunction(simplex[i + 1]);
        }

        for (int k = 0; k < maxIter; k++) {
            int[] order = getSortedOrder(f);
            int best = order[0];
            int worst = order[n];
            int nextWorst = order[n - 1];

            // Check stopping criteria
            double dxMax = Double.NEGATIVE_INFINITY,
                    dfMax = Double.NEGATIVE_INFINITY;
            for (int i = 1; i < n + 1; i++) // for each point except best
            {
                for (int j = 0; j < n; j++) // for each coordinate
                    dxMax = Math.max(dxMax, Math.abs(simplex[order[i]][j] - simplex[best][j]));
                dfMax = Math.max(dfMax, Math.abs(f[i] - f[best]));
            }
            if (dxMax <= tolX && dfMax <= tolFun)
                return simplex[best];

            double[] m = new double[n];
            Arrays.fill(m, 0.0d);
            for (int i = 0; i < n; i++)  // from 0 to n-1, not n (!)
                for (int j = 0; j < n; j++)
                    m[j] += simplex[order[i]][j] / n;

            double[] r = new double[n];
            for (int i = 0; i < n; i++)
                r[i] = 2 * m[i] - simplex[worst][i];

            double fr = objectiveFunction(r);

            if (fr >= f[best] && fr < f[nextWorst]) {
                simplex[worst] = Arrays.copyOf(r, n);
                f[worst] = fr;
                continue;
            }

            if (fr < f[best]) {
                double[] s = new double[n];
                for (int i = 0; i < n; i++)
                    s[i] = m[i] + 2 * (m[i] - simplex[worst][i]);
                double fs = objectiveFunction(s);
                if (fs < fr) {
                    simplex[worst] = Arrays.copyOf(s, n);
                    f[worst] = fs;
                } else {
                    simplex[worst] = Arrays.copyOf(r, n);
                    f[worst] = fr;
                }
                continue;
            }

            if (fr >= f[nextWorst]) {
                if (fr < f[worst]) {
                    double[] c = new double[n];
                    for (int i = 0; i < n; i++)
                        c[i] = m[i] + (r[i] - m[i]) / 2;
                    double fc = objectiveFunction(c);
                    if (fc < fr) {
                        simplex[worst] = Arrays.copyOf(c, n);
                        f[worst] = fc;
                        continue;
                    }
                } else {
                    double[] cc = new double[n];
                    for (int i = 0; i < n; i++)
                        cc[i] = m[i] + (simplex[worst][i] - m[i]) / 2;
                    double fcc = objectiveFunction(cc);
                    if (fcc < f[worst]) {
                        simplex[worst] = Arrays.copyOf(cc, n);
                        f[worst] = fcc;
                        continue;
                    }
                }
            }

            for (int i = 0; i < n; i++) {
                int point = order[i + 1];
                for (int j = 0; j < n; j++)
                    simplex[point][j] = 0.5 * (simplex[best][j] + simplex[point][j]);
                f[point] = objectiveFunction(simplex[point]);
            }
        }

        // Maximum number of iterations reached without satisfying stopping criteria.
        // Pick optimum simplex
        double fOpt = Double.POSITIVE_INFINITY;
        int iOpt = -1;
        for (int i = 0; i < n + 1; i++)
            if (f[i] < fOpt) {
                fOpt = f[i];
                iOpt = i;
            }
        return simplex[iOpt];
    }

    protected double[] fminsearch(double[] x0, double tolX, double tolFun) {
        int maxIter = x0.length * 200;
        return fminsearch(x0, tolX, tolFun, maxIter);
    }

    protected double[] fminsearch(double[] x0) {
        return fminsearch(x0, TOL_X_DEFAULT, TOL_FUN_DEFAULT);
    }

    /**
     * Return order of sorted elements of given array
     */
    private static int[] getSortedOrder(double[] v)
    {
        return IntStream.range(0, v.length)
                .boxed()
                .sorted(Comparator.comparingDouble(i -> v[i]))
                .mapToInt(e -> e)
                .toArray();
    }
}
