package com.spectrochips.spectrumsdk.DeviceConnectionModule;

/**
 * Created by  on 02/11/18.
 */

public class Matrix {


    public double[] backwardSubstitution(double[][] anyMatrix, double[] arr, int row, int col) {

        double [][] anyMatrix1 = anyMatrix;
        double[] arr1 = arr;
        if(row < 0 || col < 0){
            return arr1;
        } else{
            int rows = anyMatrix1.length;
            int cols = anyMatrix1[0].length - 1;
            double current = 0.0;
            int counter = 0;


            for(int i = cols - 1; i >= col; i--){

                if(i == col){
                    current = anyMatrix1[row][cols] / anyMatrix1[row][i];

                } else{
                    anyMatrix1[row][cols] -= anyMatrix1[row][i] * arr1[rows - 1 - counter];
                    counter += 1 ;
                }
            }

            arr1[row] = current;
            return this.backwardSubstitution(anyMatrix1,arr1,row- 1,col- 1);
        }
    }

    public double[][] combineMatrices(double[][] left,double[] right) {

        int rows = right.length;
        int cols = left[0].length;
        double[][] returnMatrix = new double[rows][cols+1];


        for (int i = 0; i < rows; i++) {
            {
                for (int j = 0; j <= cols; j++) {
                    {
                        if (j == cols) {

                            returnMatrix[i][j] = right[i];

                        } else {

                            returnMatrix[i][j] = left[i][j];
                        }
                    }
                }


            }
        }

       // Log.e("Combine Matriess",""+ Arrays.deepToString(returnMatrix));

        return returnMatrix;
    }

    public double[][] forwardElimination(double[][] anyMatrix){

                int rows = anyMatrix.length;
                int cols = anyMatrix[0].length;
                double[][] aMatrix = new double[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        aMatrix[i][j] = anyMatrix[i][j];
                    }
                }

                for (int x = 0; x < rows - 1; x++) {

                    for (int z = x; z < rows - 1; z++) {

                        double numerator   = aMatrix[z + 1][x];
                        double denominator = aMatrix[x][x];
                        double result      = numerator / denominator;
                        for (int i = 0; i < cols; i++) {
                            aMatrix[z + 1][i] = aMatrix[z + 1][i] - (result * aMatrix[x][i]);
                        }
                    }
                }
       // Log.e("Farward Matriess",""+ Arrays.deepToString(aMatrix));
                return aMatrix;
    }

    public double[] gaussianJordanElimination(double[][] leftMatrix,double[] rightMatrix)  {

        double[][] combined = this.combineMatrices(leftMatrix,rightMatrix);
        double[][]  fwdIntegration = this.forwardElimination(combined);

        double [] emptyArray = new double[fwdIntegration.length];
        //NOW, FINAL STEP IS BACKWARD SUBSTITUTION WHICH RETURNS THE TERMS NECESSARY FOR POLYNOMIAL REGRESSION
        return  backwardSubstitution(fwdIntegration,emptyArray,fwdIntegration.length-1,fwdIntegration[0].length-2);

    }

    public int[][] identityMatrix(int[][] anyMatrix) {

        int rows = anyMatrix.length;
        int cols = anyMatrix[0].length;
        int[][] identityMatrix = new int[0][];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j == i) {
                    identityMatrix[i][j] = 1;
                } else {
                    identityMatrix[i][j] = 0;
                }
            }
        }
        return identityMatrix;

    }


    public int[][] matrixProduct(int[][] matrix1,int[][] matrix2){

        int numCols1 = matrix1[0].length;
        int numRows2 = matrix2.length;

        if(numCols1 != numRows2){
            return null;
        }

        int[][] product = new int[0][];

        for (int rows = 0; rows < numRows2; rows++) {
            for (int cols = 0; cols < numCols1; cols++) {
                product[rows][cols] = this.doMultiplication(matrix1, matrix2, rows,
                        cols, numCols1);
            }
        }

        return product;
    }

    public int doMultiplication (int[][] matrix1, int[][] matrix2, int row, int col,int numCol){
        int counter = 0;
        int result = 0;

        while (counter < numCol) {
            result += matrix1[row][counter] * matrix2[counter][col];
            counter++;
        }
        return result ;
    }

    public int[][] multiplyRow(int[][] anyMatrix, int rowNum, int multiplier)  {

        int rows = anyMatrix.length;
        int cols = anyMatrix[0].length;
        int[][] mMatrix = new int[0][];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == rowNum) {
                    mMatrix[i][j] = anyMatrix[i][j] * multiplier;
                } else {
                    mMatrix[i][j] = anyMatrix[i][j];
                }
            }
        }
        return mMatrix;
    }

}
