/*
 * Copyright (C) 2014 Davide Mottin <mottin@disi.unitn.eu>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package eu.unitn.disi.db.mutilities.data;

import eu.unitn.disi.db.mutilities.Pair;
import java.util.Comparator;

/**
 * Builds a pair comparator on the second member of the pair. 
 * @author Davide Mottin <mottin@disi.unitn.eu>
 */
public class PairSecondComparator implements Comparator<Pair<?,? extends Comparable>> {
    private final boolean asc; 

    /**
     *  A pair comparator on the second member of the pair.  
     * @param asc if false it is descending order, opposite of normal/natural order for compareTo
     */
    public PairSecondComparator(boolean asc) {
        this.asc = asc;
    }

    /**
     * A pair comparator on the second member of the pair.  
     * Default is ascending order (normal/natural order for compareTo)
     */
    public PairSecondComparator() {
        this.asc = true; 
    }
    
    
    @Override
    public int compare(Pair<?, ? extends Comparable> o1, Pair<?, ? extends Comparable> o2) {
        return asc? o1.getSecond().compareTo(o2.getSecond()) : -o1.getSecond().compareTo(o2.getSecond());
    }

}
