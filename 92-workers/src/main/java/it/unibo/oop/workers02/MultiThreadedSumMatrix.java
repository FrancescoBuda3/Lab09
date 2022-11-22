package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix{

    private int nthreads;

    public MultiThreadedSumMatrix(int nthreads) {
        this.nthreads = nthreads;
    }


    private static class Worker extends Thread{
        private final double[][] matrix;
        private final int start;
        private final int nelem;
        private double res;

        public Worker(double[][] matrix, int start, int nelem) throws IllegalArgumentException {
            super();
            if (matrix == null) {
                throw new IllegalArgumentException();
            }

            this.matrix = matrix;

            int totElem = matrix.length * matrix[0].length;
            if (start + nelem > totElem) {
                throw new IllegalArgumentException();
            }

            this.start = start;
            this.nelem = nelem;    
        }

        public void run() {
            System.out.println("Working from element " + this.start + "to element " + (this.start + this.nelem -1));
            double pRes = 0.0;
            for(int i = 0; i < this.nelem ; i++){
                int pos = this.start+i;
                pRes += matrix[pos/this.matrix[0].length][pos%this.matrix[0].length];
            }
            this.res = pRes;
        }

        public double getRes() {
            return this.res;
        }
    }

    @Override
    public double sum(double[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException();
        }

        final int totElem = matrix.length * matrix[0].length;
        final int size = totElem / this.nthreads + totElem % this.nthreads;
        final List<Worker> workers = new ArrayList<>(nthreads);

        for(int i = 0; i < totElem; i += size) {
            workers.add(new Worker(matrix, i, (i + size > totElem) ? (totElem - i) : size));
        }

        for (final Worker w : workers) {
            w.start();
        }

        double pRes = 0.0;
        for (final Worker w : workers) {
            try{
                w.join();
                pRes += w.getRes();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        return pRes;
    }
    
}
