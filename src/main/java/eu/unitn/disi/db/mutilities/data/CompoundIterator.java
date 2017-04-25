/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.unitn.disi.db.mutilities.data;

/**
 * 
 *                                    
 * JBoss: The OpenSource J2EE WebOS 
 * Distributable under LGPL license. 
 * See terms of license at gnu.org.
 * 
 * package org.jboss.util.collection; 
 * 
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A compound iterator, which iterates over all of the elements in the given
 * iterators.
 *
 * @version <tt>$Revision: 1.1 $</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @param <E>
 */
public class CompoundIterator<E> implements Iterator<E> {

    /**
     * The array of iterators to iterate over.
     */
    protected final ArrayList<Iterator<E>> iters;

    /**
     * The index of the current iterator.
     */
    protected int index;

    /**
     * Construct a CompoundIterator over the given array of iterators.
     *
     * @param iters ArrayList of iterators to iterate over.
     *
     * @throws IllegalArgumentException Array is null or empty.
     */
    public CompoundIterator(final ArrayList<Iterator<E>> iters) {
        if (iters == null) {
            throw new IllegalArgumentException("List of iterators is null or empty");
        }

        this.iters = new ArrayList<>(iters);
    }

    
    
    /**
     * Check if there are more elements.
     *
     * @return True if there are more elements.
     */
    @Override
    public boolean hasNext() {
        for (; index < iters.size(); index++) {
            if (iters.get(index) != null && iters.get(index).hasNext()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return the next element from the current iterator.
     *
     * @return The next element from the current iterator.
     *
     * @throws NoSuchElementException There are no more elements.
     */
    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        return iters.get(index).next();
    }

    /**
     * Remove the current element from the current iterator.
     *
     * @throws IllegalStateException
     * @throws UnsupportedOperationException
     */
    @Override
    public void remove() {
        iters.get(index).remove();
    }
}
