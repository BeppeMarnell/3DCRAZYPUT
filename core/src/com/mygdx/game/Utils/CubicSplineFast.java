package com.mygdx.game.Utils;


/**
 * Inspired from Michael Thomas Flanagan's Java Scientific Library
 * explanation and pseudo-code found on his website
 */

public class CubicSplineFast {
    private int nPoints;
    private double[] y;
    private double[] x;
    private double[] d2ydx2;

    /**
     * Creates an instance of the CubicSplineFast object that will hold an array of data points of length n and with all data arrays initialised to zero.
     * @param nPoints number of points needed for the data
     */
    public CubicSplineFast(int nPoints) {
        this.nPoints = nPoints;
        this.x = new double[nPoints];
        this.y = new double[nPoints];
        this.d2ydx2 = new double[nPoints];
    }

    /**
     * reset data of points
     * @param x
     * @param y
     */
    public void resetData(double[] x, double[] y) {
        for(int i = 0; i < this.nPoints; i++) {
            this.x[i] = x[i];
            this.y[i] = y[i];
        }

        //then calculate the derivate
        calcDeriv();
    }

    /**
     * Creates and returns a new instance of a cubic spline object with all data array values set at zero.
     * @param dataPoints data points
     * @return
     */
    public static CubicSplineFast zero(int dataPoints) {
        if (dataPoints < 3) {
            throw new IllegalArgumentException("A minimum of three data points is needed");
        } else {
            CubicSplineFast splineFast = new CubicSplineFast(dataPoints);
            return splineFast;
        }
    }

    /**
     * Creates an array, of length n, of CubicSpline instances with all data array values set at zero.
     * @param length
     * @param m indices
     * @return
     */
    public static CubicSplineFast[] oneDarray(int length, int m) {
        CubicSplineFast[] cubicSplineFasts = new CubicSplineFast[length];

        for(int i = 0; i < length; i++) {
            cubicSplineFasts[i] = zero(m);
        }

        return cubicSplineFasts;
    }

    /**
     * Calculates the array of second derivatives for the data set, y = f(x).
     */
    public void calcDeriv() {

        double[] crossDer = new double[this.nPoints];
        this.d2ydx2[0] = crossDer[0] = 0.0D; // se to zero

        for(int i = 1; i <= this.nPoints - 2; i++) {
            double val2 = (this.x[i] - this.x[i - 1]) / (this.x[i + 1] - this.x[i - 1]);
            double val1 = val2 * this.d2ydx2[i - 1] + 2.0D;

            //main cross derivate
            this.d2ydx2[i] = (val2 - 1.0D) / val1;
            crossDer[i] = (this.y[i + 1] - this.y[i]) / (this.x[i + 1] - this.x[i]) - (this.y[i] - this.y[i - 1]) / (this.x[i] - this.x[i - 1]);
            crossDer[i] = (6.0D * crossDer[i] / (this.x[i + 1] - this.x[i - 1]) - val2 * crossDer[i - 1]) / val1;
        }


        this.d2ydx2[this.nPoints - 1] = 0.0D;

        for(int i = this.nPoints - 2; i >= 0; i--) {
            this.d2ydx2[i] = this.d2ydx2[i] * this.d2ydx2[i + 1] + crossDer[i];
        }
    }

    /**
     * Returns the interpolated value of y, y1, for a given value of xx1, using the y = f(x) data stored in aa by the constructor.
     * This method may be called as often as required.
     * The second derivatives needed for the interpolation are calculated and stored on instantiation so that they need not be
     * recalculated on each call to this method. This method throws an IllegalArgumentException if xx1 is outside the range of
     * the values of x[] supplied to the constructor or if two x values within the y = f(x) data set are identical.
     * @param xx1
     * @return
     */
    public double interpolate(double xx1) {

        //two point to compare
        int ind1 = 0;
        int ind2 = this.nPoints - 1;

        while(ind2 - ind1 > 1) {
            int var14 = ind2 + ind1 >> 1;
            if (this.x[var14] > xx1) {
                ind2 = var14;
            } else {
                ind1 = var14;
            }
        }

        double dist = this.x[ind2] - this.x[ind1];
        if (dist == 0.0D) {
            throw new IllegalArgumentException("Two values of x are identical: point " + ind1 + " (" + this.x[ind1] + ") and point " + ind2 + " (" + this.x[ind2] + ")");
        } else {
            double val1 = (this.x[ind2] - xx1) / dist;
            double val2 = (xx1 - this.x[ind1]) / dist;
            double val3 = val1 * this.y[ind1] + val2 * this.y[ind2] + ((val1 * val1 * val1 - val1) * this.d2ydx2[ind1] +
                    (val2 * val2 * val2 - val2) * this.d2ydx2[ind2]) * dist * dist / 6.0D;
            return val3;
        }
    }
}
