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

import eu.unitn.disi.db.mutilities.Triple;
import java.util.Comparator;

/**
 * Comparator for the first member of a pair
 * @author Davide Mottin <mottin@disi.unitn.eu>
 * @param <T>
 */
public class TripleFirstComparator<T extends Comparable<T>> implements Comparator<Triple<T,?,?>> {
    private final boolean asc; 

    public TripleFirstComparator(boolean asc) {
        this.asc = asc;
    }

    public TripleFirstComparator() {
        this.asc = true; 
    }
    
    

    @Override
    public int compare(Triple<T, ?,?> o1, Triple<T, ?,?> o2) {
        return asc? o1.getFirst().compareTo(o2.getFirst()) : -o1.getFirst().compareTo(o2.getFirst());
    }
}
