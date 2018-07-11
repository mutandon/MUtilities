/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.unitn.disi.db.mutilities;

import java.util.Collections;
import java.util.List;

/**
 * Utilities for Numbers
 *
 * @author Kuzeko
 */
public class Numbers {

    /**
     * Splits a long into 2 integers
     *
     * @param toSplit
     * @return long splitted in to int's with bitwise unsigned right shift
     */
    public static int[] split(long toSplit) {
        int[] splitted = new int[2];

        splitted[0] = (int) (toSplit >>> 32);
        splitted[1] = (int) toSplit;

        return splitted;
    }

    /**
     * Given two ints, reconstruct a long with the concatenation of the bits
     *
     * @param left higher value bits
     * @param right lower value bits
     * @return long concatenation of the two ints
     */
    public static long join(int left, int right) {
        return (long) left << 32 | right & 0xFFFFFFFFL;

    }

    /**
     *
     * @param m
     * @return the median as int
     */
    public static double median(List<Double> m) {
        if (m.size() < 2) {
            return m.get(0);
        }
        int middle = m.size() / 2;
        Collections.sort(m);
        if (m.size() % 2 == 1) {
            return m.get(middle);
        }
        return (m.get(middle - 1) + m.get(middle)) / 2.0;

    }

    /**
     * 
     * @param str
     * @return 
     */
    public static Long isLongNumber(String str) {
        Long d;
        try {
            d = Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return null;
        }
        return d;
    }

    /**
     *
     * @param N
     * @param K
     * @return number of combinations of from N choose K
     */
    public static long binomial(final long N, final int K) {
        long nCk = 1;
        for (int k = 0; k < K; k++) {
            nCk = nCk * (N - k) / (k + 1);
        }
        return nCk;
    }

}
