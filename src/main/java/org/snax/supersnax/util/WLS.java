package org.snax.supersnax.util;

import java.util.Arrays;

public class WLS {
    private final double[] y;
    private final double[] x;
    private final double[] w;

    public WLS(double[] y, double[] x, double[] w) {
        assert x.length == y.length && x.length == w.length;
        assert x.length > 2;

        this.x = x;
        this.y = y;
        this.w = w;
    }

    public WLS(double[] x, double[] y) {
        assert x.length == y.length;
        assert x.length > 2;

        this.x = x;
        this.y = y;
        this.w = new double[x.length];
        Arrays.fill(this.w, 1);
    }

    public WLS(double[] x, double[] y, double w) {
        assert x.length == y.length;
        assert x.length > 2;

        this.x = x;
        this.y = y;
        this.w = new double[x.length];
        Arrays.fill(this.w, w);
    }

    public Point fitLinearRegression() {
        double sumOfWeights = 0.0;
        double sumOfWeightsByXSquared = 0.0;
        double sumOfXByYByWeights = 0.0;
        double sumOfXByWeights = 0.0;
        double sumOfYByWeights = 0.0;

        double Xi, Yi, Wi, XiByWi;

        for (int i = 0; i < this.x.length; i++) {
            Xi = this.x[i];
            Yi = this.y[i];
            Wi = this.w[i];

            sumOfWeights += Wi;
            XiByWi = Xi * Wi;

            sumOfXByWeights += XiByWi;
            sumOfXByYByWeights += XiByWi * Yi;
            sumOfYByWeights += Yi * Wi;
            sumOfWeightsByXSquared += XiByWi * Xi;
        }

        double dividend = sumOfWeights * sumOfXByYByWeights - sumOfXByWeights * sumOfYByWeights;
        double divisor = sumOfWeights * sumOfWeightsByXSquared - sumOfXByWeights * sumOfXByWeights;

        if (divisor == 0) {
            return new Point(null, null);
        }

        double slope = dividend / divisor;
        double intercept = (sumOfYByWeights - slope * sumOfXByWeights) / sumOfWeights;

        return new Point(intercept, slope);
    }
}