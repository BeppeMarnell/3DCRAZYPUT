package com.mygdx.game.Utils;

public class CubicSplineFast {
    private int nPoints;
    private double[] y;
    private double[] x;
    private double[] d2ydx2;

    /**
     * Constructor for the cubic spline
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

        this.calcDeriv();
    }

    public static CubicSplineFast zero(int var0) {
        if (var0 < 3) {
            throw new IllegalArgumentException("A minimum of three data points is needed");
        } else {
            CubicSplineFast var1 = new CubicSplineFast(var0);
            return var1;
        }
    }

    public static CubicSplineFast[] oneDarray(int var0, int var1) {
        CubicSplineFast[] var2 = new CubicSplineFast[var0];

        for(int var3 = 0; var3 < var0; ++var3) {
            var2[var3] = zero(var1);
        }

        return var2;
    }

    public void calcDeriv() {
        double var1;
        double var3;
        double var5;
        double var7;
        double[] var9 = new double[this.nPoints];
        this.d2ydx2[0] = var9[0] = 0.0D;

        int var10;
        for(var10 = 1; var10 <= this.nPoints - 2; ++var10) {
            var5 = (this.x[var10] - this.x[var10 - 1]) / (this.x[var10 + 1] - this.x[var10 - 1]);
            var1 = var5 * this.d2ydx2[var10 - 1] + 2.0D;
            this.d2ydx2[var10] = (var5 - 1.0D) / var1;
            var9[var10] = (this.y[var10 + 1] - this.y[var10]) / (this.x[var10 + 1] - this.x[var10]) - (this.y[var10] - this.y[var10 - 1]) / (this.x[var10] - this.x[var10 - 1]);
            var9[var10] = (6.0D * var9[var10] / (this.x[var10 + 1] - this.x[var10 - 1]) - var5 * var9[var10 - 1]) / var1;
        }

        var7 = 0.0D;
        var3 = 0.0D;
        this.d2ydx2[this.nPoints - 1] = (var7 - var3 * var9[this.nPoints - 2]) / (var3 * this.d2ydx2[this.nPoints - 2] + 1.0D);

        for(var10 = this.nPoints - 2; var10 >= 0; --var10) {
            this.d2ydx2[var10] = this.d2ydx2[var10] * this.d2ydx2[var10 + 1] + var9[var10];
        }
    }

    public double interpolate(double var1) {
        double var3;
        double var5;
        double var7;
        double var9;
        int var12 = 0;
        int var13 = this.nPoints - 1;

        while(var13 - var12 > 1) {
            int var14 = var13 + var12 >> 1;
            if (this.x[var14] > var1) {
                var13 = var14;
            } else {
                var12 = var14;
            }
        }

        var3 = this.x[var13] - this.x[var12];
        if (var3 == 0.0D) {
            throw new IllegalArgumentException("Two values of x are identical: point " + var12 + " (" + this.x[var12] + ") and point " + var13 + " (" + this.x[var13] + ")");
        } else {
            var7 = (this.x[var13] - var1) / var3;
            var5 = (var1 - this.x[var12]) / var3;
            var9 = var7 * this.y[var12] + var5 * this.y[var13] + ((var7 * var7 * var7 - var7) * this.d2ydx2[var12] + (var5 * var5 * var5 - var5) * this.d2ydx2[var13]) * var3 * var3 / 6.0D;
            return var9;
        }
    }
}
