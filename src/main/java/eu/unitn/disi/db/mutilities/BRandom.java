/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.unitn.disi.db.mutilities;

import java.util.Random;

/**
 * A better Random
 *
 * @author Kuzeko
 */
public class BRandom extends Random {

    private static final long serialVersionUID = 1L;

    public BRandom(long seed) {
        super(seed);
    }

    public long nextPositiveLong() {
        return this.nextPositiveLong(Long.MAX_VALUE);
    }

    public long nextPositiveLong(long max) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = (this.nextLong() << 1) >>> 1;
            val = bits % max;
        } while (bits - val + (max - 1) < 0L);
        return val;
    }

    /**
     * https://diveintodata.org/2009/09/13/zipf-distribution-generator-in-java/
     */
    public static class ZipfGenerator {

        private Random rnd;
        private int size;
        private double skew;
        private double bottom = 0;

        public ZipfGenerator(int size, double skew) {
            this(size, skew, System.currentTimeMillis());
        }

        public ZipfGenerator(int size, double skew, long seed) {
            this.size = size;
            this.skew = skew;
            rnd = new Random();
            for (int i = 1; i < size; i++) {
                this.bottom += (1 / Math.pow(i, this.skew));
            }
        }

        // the next() method returns an random rank id.
        // The frequency of returned rank ids are follows Zipf distribution.
        public int next() {
            int rank;
            double friquency = 0;
            double dice;

            rank = rnd.nextInt(size);
            friquency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
            dice = rnd.nextDouble();

            while (!(dice < friquency)) {
                rank = rnd.nextInt(size);
                friquency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
                dice = rnd.nextDouble();
            }

            return rank;
        }

// This method returns a probability that the given rank occurs.
        public double getProbability(int rank) {
            return (1.0d / Math.pow(rank, this.skew)) / this.bottom;
        }

    }

    

}
