package com.mygdx.game.Math;

public class BiCubicSplineFast {
    private int nPoints = 0;
    private int mPoints = 0;
    private double[][] y = (double[][])null;
    private double[] x1 = null;
    private double[] x2 = null;
    private CubicSplineFast[] csn = null;
    private CubicSplineFast csm = null;

    public BiCubicSplineFast(double[] var1, double[] var2, double[][] var3) {
        this.nPoints = var1.length;
        this.mPoints = var2.length;
        this.csm = new CubicSplineFast(this.nPoints);
        this.csn = CubicSplineFast.oneDarray(this.nPoints, this.mPoints);
        this.x1 = new double[this.nPoints];
        this.x2 = new double[this.mPoints];
        this.y = new double[this.nPoints][this.mPoints];

        int var4;
        for(var4 = 0; var4 < this.nPoints; ++var4) {
            this.x1[var4] = var1[var4];
        }

        for(var4 = 0; var4 < this.mPoints; ++var4) {
            this.x2[var4] = var2[var4];
        }

        int var5;
        for(var4 = 0; var4 < this.nPoints; ++var4) {
            for(var5 = 0; var5 < this.mPoints; ++var5) {
                this.y[var4][var5] = var3[var4][var5];
            }
        }

        double[] var7 = new double[this.mPoints];

        for(var5 = 0; var5 < this.nPoints; ++var5) {
            for(int var6 = 0; var6 < this.mPoints; ++var6) {
                var7[var6] = var3[var5][var6];
            }

            this.csn[var5].resetData(var2, var7);
            this.csn[var5].calcDeriv();
        }

    }

    public double interpolate(double var1, double var3) {
        double[] var5 = new double[this.nPoints];

        for(int var6 = 0; var6 < this.nPoints; ++var6) {
            var5[var6] = this.csn[var6].interpolate(var3);
        }

        this.csm.resetData(this.x1, var5);
        return this.csm.interpolate(var1);
    }
}
