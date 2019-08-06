package com.spectrochips.spectrumsdk.DeviceConnectionModule;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by ming-enliu on 02/11/18.
 */


public class PolynomialRegression {

        DataPoint [] data;
        Matrix  matrix ;
        int degree ;
        double[][]  leftMatrix ;
        double[] rightMatrix;


    public PolynomialRegression(DataPoint[] theData,int degrees) {
            this.data = theData ;
            this.degree = degrees ;
            this.matrix      =  new  Matrix();

        }

    public void fillMatrix()  {
            generateLeftMatrix();
            generateRightMatrix();

        }


        public double sumX(DataPoint[] anyData,double power)  {
            printAnyData();
            double sum = 0;
            for (int i = 0; i < anyData.length; i++) {
                sum += pow(anyData[i].x, power);
            }

            return sum;
        }

        public void printAnyData(){

            for (int i = 0; i < this.data.length; i++) {
                DataPoint objDataPoint = this.data[i];
                Log.e("DataPoint1::",objDataPoint.x + ":" +objDataPoint.y);
            }
        }

        public double sumXTimesY(DataPoint[] anyData,double power) {


            double sum = 0;
            for (int i = 0; i < anyData.length; i++) {
                sum += pow(anyData[i].x, power) * anyData[i].y;
            }
            return sum;
        }

        public double sumY(DataPoint[] anyData,double power)  {


            double sum = 0;

            for (int i = 0; i < anyData.length; i++) {
                sum += pow(anyData[i].y, power);
            }
            return sum;
        }

        public void generateLeftMatrix()  {

           this.leftMatrix = new double[this.degree+1][this.degree+1];

            for (int i = 0; i <= this.degree; i++) {

                for (int j = 0; j <= this.degree; j++) {
                    if (i == 0 && j == 0) {
                        this.leftMatrix[i][j] = this.data.length;
                    } else {
                        this.leftMatrix[i][j] = this.sumX(this.data, (i + j));
                        DecimalFormat df = new DecimalFormat("#.##");
                        Log.e("Left:unMatched",""+df.format(this.leftMatrix[i][j]));
                    }
                }
            }

        }

        public void generateRightMatrix()  {

            this.rightMatrix = new double[this.degree+1];

            for (int i = 0; i <= this.degree; i++) {
                if (i == 0) {
                    this.rightMatrix[i] = this.sumY(this.data, 1);
                } else {
                    this.rightMatrix[i] = this.sumXTimesY(this.data, i);
                }
            }


        }

        public double predictY(double[] terms ,double x) {

            double result = 0;

            for (int i = terms.length - 1; i >= 0; i--) {
                if (i == 0) {
                    result += terms[i];
                } else {
                    result += terms[i] * pow(x, i);
                }

            }
            return result;

        }

       public HashMap<String,Double>  linearRegression(double[] y, double[] x) {

           HashMap<String, Double> lr = new HashMap<>();

           double n = y.length;
           double sum_x = 0;
           double sum_y = 0;
           double sum_xy = 0;
           double sum_xx = 0;
           double sum_yy = 0;


           for (int i = 0; i < y.length; i++) {

               sum_x += x[i];
               sum_y += y[i];
               sum_xy += (x[i] * y[i]);
               sum_xx += (x[i] * x[i]);
               sum_yy += (y[i] * y[i]);
           }

           double exp1 = n * sum_xy - sum_x * sum_y ;
           double exp2 = sqrt((n * sum_xx - sum_x * sum_x) * (n * sum_yy - sum_y * sum_y)) ;
           double exp3 = exp1 / exp2 ;
           lr.put("r2",pow(exp3, 2));

           return lr;


       }



        public int getRandomInt(int min,int max) {

        Random randomNum = new Random();
            int randomInt = min + randomNum.nextInt(max);
            return randomInt;
        }


       public double[] getTerms()  {
            return this.matrix.gaussianJordanElimination(this.leftMatrix,this.rightMatrix);
        }


    }


