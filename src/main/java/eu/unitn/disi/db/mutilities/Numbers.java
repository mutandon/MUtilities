/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.unitn.disi.db.mutilities;

/**
 * Utilities for Numbers
 *
 * @author Kuzeko
 */
public class Numbers {

    /**
     * Splits a long into 2 integers
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
     * Given two ints, reconstruct a long with the concatenation fo the bits
     * @param left higher value bits
     * @param right lower value bits
     * @return long concatenation of the two ints
     */
    public static long join(int left, int right) {
        return (long) left << 32 | right & 0xFFFFFFFFL;
        
    }
}
