/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.unitn.disi.db.mutilities.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * http://stackoverflow.com/questions/7878026/is-there-a-priorityqueue-implementation-with-fixed-capacity-and-custom-comparato
 * @author Matteo Lissandrini <ml@disi.unitn.eu>
 * @param <E>
 */
public class FixedSizePriorityQueue<E  extends Comparable<E>> implements Iterable<E>{
    private final PriorityQueue<E> queue; /* backing data structure */
    private final Comparator<? super E> comparator;
    private final int maxSize;

    
    /**
     * Constructs a {@link FixedSizePriorityQueue} with the specified {@code maxSize}
     * and a NaturalOrder Comparator.
     *
     * @param maxSize    - The maximum size the queue can reach, must be a positive integer.
     */
    public FixedSizePriorityQueue(final int maxSize) {
        super();
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize = " + maxSize + "; expected a positive integer.");
        }
        
        this.queue = new PriorityQueue<>(Comparator.<E>naturalOrder());
        this.comparator = queue.comparator();
        this.maxSize = maxSize;
    }

    
    
    /**
     * Constructs a {@link FixedSizePriorityQueue} with the specified {@code maxSize}
     * and {@code comparator}.
     *
     * @param maxSize    - The maximum size the queue can reach, must be a positive integer.
     * @param comparator - The comparator to be used to compare the elements in the queue, must be non-null.
     */
    public FixedSizePriorityQueue(final int maxSize, final Comparator<? super E> comparator) {
        super();
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize = " + maxSize + "; expected a positive integer.");
        }
        if (comparator == null) {
            throw new NullPointerException("Comparator is null.");
        }
        this.queue = new PriorityQueue<>(comparator);
        this.comparator = queue.comparator();
        this.maxSize = maxSize;
    }

    /**
     * Adds an element to the queue. If the queue contains {@code maxSize} elements, {@code e} will
     * be compared to the lowest element in the queue using {@code comparator}.
     * If {@code e} is greater than or equal to the lowest element, that element will be removed and
     * {@code e} will be added instead. Otherwise, the queue will not be modified
     * and {@code e} will not be added.
     *
     * @param e - Element to be added, must be non-null.
     * @return true if the element has been added
     */
    public boolean add(final E e) {
        if (e == null) {
            throw new NullPointerException("e is null.");
        }
        if (maxSize <= queue.size()) {
            final E firstElm = queue.peek();
            if (comparator.compare(e, firstElm) < 1) {
                return false;
            } else {
                queue.poll();
            }
        }
        return queue.add(e);
    }

    
    /**
     * 
     * @return Returns true if this collection contains no elements
     */
    public boolean isEmpty(){
        return this.queue.isEmpty();
    }
    
    /**
     * 
     * @return  the number of elements in the queue, at most {@code maxSize}
     *  
     */
    public int size(){
       return queue.size();
               
    }
    
    public E peek(){

        
        return this.queue.peek();
                
    }
    
    /**
     * @return Returns a sorted view of the queue as a {@link Collections#unmodifiableList(java.util.List)}
     *         unmodifiableList.
     */
    public List<E> asList() {
        List<E> mutableList = new ArrayList<>(queue); 
        Collections.sort(mutableList, comparator); 
        return Collections.unmodifiableList( mutableList );
    }

    @Override
    public Iterator<E> iterator() {
        return this.asList().iterator();
    }
    
  
    
    
}
