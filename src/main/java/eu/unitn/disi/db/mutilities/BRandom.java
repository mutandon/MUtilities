/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.unitn.disi.db.mutilities;

import java.util.Random;

/**
 * A better Random
 * @author Kuzeko
 */
public class BRandom extends Random {
    private static final long serialVersionUID = 1L;
                  
    public BRandom(long seed){
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
    
}
