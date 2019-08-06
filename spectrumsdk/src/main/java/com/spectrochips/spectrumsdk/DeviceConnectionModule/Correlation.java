package com.spectrochips.spectrumsdk.DeviceConnectionModule;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by ming-enliu on 02/11/18.
 */

public class Correlation {

    double[] x ;
    double[] y ;

    public void  Correlation(double[] x, double[] y)
    {
        this.x = x;
        this.y = y;
    }

    public double diffFromAvg(){

        double sum = 0.0;

        for(int i=0;i<this.x.length;i++){

            sum += (this.x[i] - this.avg( this.x)) * (this.y[i] - this.avg(this.y));
        }
        return  sum;
    }

    public double avg(double[] aList) {

        double sum = 0.0;

        for (int i = 0; i < aList.length; i++) {
                sum += aList[i];
            }
            return sum / aList.length;
    }

    public double diffFromAvgSqrd(double[] aList) {

        double sum = 0.0;
           for(int i=0;i<aList.length;i++){
                sum += pow((aList[i]-avg(aList)), 2);
            }
            return sum;

    }

        public double stdv(double[] aList) {
            return sqrt(diffFromAvgSqrd(aList)/aList.length-1) ;
        }

        public double b0() {

            return avg(this.y) - b1() * this.avg(this.x);
        }

        public double  b1()  {

            return this.diffFromAvg() / this.diffFromAvgSqrd(this.x);
        }

    public double sumList(double[] aList){

        double sum = 0.0;
        for (int i = 0; i < aList.length; i++) {
            sum += aList[i];
        }
        return sum;
    }

    public double sumSquares(double[] aList) {

        double sum = 0;
        for (int i = 0; i < aList.length; i++) {
            sum += pow(aList[i], 2);
        }
        return sum;
    }

    public double sumXTimesY() {
        double sum = 0.0;
        for (int i = 0; i < this.x.length; i++) {
            sum += (this.y[i] * this.x[i]);
        }
        return sum;
    }

    public double linearRegression( double independentVariable) {
        return this.b1() * independentVariable + this.b0();
    }
}
