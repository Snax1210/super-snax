package org.snax.supersnax.util;

/**
 * @author maoth
 * @date 2022/1/21 22:09
 * @description
 */
public class Point {
    private final Double intercept;
    private final Double slope;

    public Point(Double intercept, Double slope) {
        this.intercept = intercept;
        this.slope = slope;
    }

    public Double getIntercept() {
        return intercept;
    }

    public Double getSlope() {
        return slope;
    }
}
