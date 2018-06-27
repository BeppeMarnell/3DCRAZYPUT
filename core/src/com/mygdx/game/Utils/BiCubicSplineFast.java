package com.mygdx.game.Utils;


/**
 * Inspired from Michael Thomas Flanagan's Java Scientific Library
 * explanation and pseudo-code found on his website
 */

public class BiCubicSplineFast {
    //amount of indices
    private int nPoints;
    private int mPoints;

    private double[][] y;
    private double[] x1;
    private double[] x2;

    //cubic spline for the two dimension
    private CubicSplineFast[] csn;
    private CubicSplineFast csm;


    /**
     *Creates an instance of the BiCubicSpline object with its internal data arrays initialised to copies
     * of the values in the x1, x2 and y arrays where y is the tabulated function y = f(x1,x2)
     * @param x1
     * @param x2
     * @param y
     */
    public BiCubicSplineFast(double[] x1, double[] x2, double[][] y) {
        //initialize variables
        this.nPoints = x1.length;
        this.mPoints = x2.length;
        this.csm = new CubicSplineFast(this.nPoints);
        this.csn = CubicSplineFast.oneDarray(this.nPoints, this.mPoints);
        this.x1 = new double[this.nPoints];
        this.x2 = new double[this.mPoints];
        this.y = new double[this.nPoints][this.mPoints];


        //copy values
        for(int i = 0; i < this.nPoints; i++) {
            this.x1[i] = x1[i];
        }

        //copy values
        for(int i = 0; i < this.mPoints; i++) {
            this.x2[i] = x2[i];
        }

        //copy values
        for(int i = 0; i < this.nPoints; i++) {
            for(int j = 0; j < this.mPoints; j++) {
                this.y[i][j] = y[i][j];
            }
        }

        double[] resArray = new double[this.mPoints];


        //interpolate the data and calculate the derivatives for each row
        for(int j = 0; j < this.mPoints; j++) {
            for(int f = 0; f < this.mPoints; f++) resArray[f] = y[j][f];

            this.csn[j].resetData(x2, resArray);
            this.csn[j].calcDeriv();
        }

    }

    /**
     * Returns the interpolated value of y, y1, for given values of xx1 and xx2, using the y = f(x1,x2) data entered via the constructor.
     * @param xx1
     * @param xx2
     * @return
     */
    public double interpolate(double xx1, double xx2) {
        double[] interpArray = new double[this.nPoints];

        for(int i = 0; i < this.nPoints; ++i) {
            interpArray[i] = this.csn[i].interpolate(xx2);
        }

        this.csm.resetData(this.x1, interpArray);
        return this.csm.interpolate(xx1);
    }
}
