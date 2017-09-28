/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.unitn.disi.db.mutilities.data;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 * A Fixed Size priority {@linkplain Queue queue} based on a priority heap. The
 * elements of the priority queue are ordered according to their
 * {@linkplain Comparable natural ordering}, or by a {@link Comparator} provided
 * at queue construction time, depending on which constructor is used. A
 * priority queue does not permit {@code null} elements. A priority queue
 * relying on natural ordering also does not permit insertion of non-comparable
 * objects (doing so may result in {@code ClassCastException}).
 *
 * Only the TOP K greatest elements will be kept.
 * 
 * E.g, from. { 1.0, 2.0, 3.0, 3.1, 2.1, 1.1 } -> [3.1, 3.0, 2.1]
 *
 * http://stackoverflow.com/questions/7878026/is-there-a-priorityqueue-implementation-with-fixed-capacity-and-custom-comparato
 *
 * @author Matteo Lissandrini <ml@disi.unitn.eu>
 * @param <E>
 */
public class FixedSizePriorityQueue<E> extends AbstractQueue<E> implements Iterable<E> {

    private final PriorityQueue<E> queue;
    /* backing data structure */
    private final Comparator<? super E> comparator;
    private final int maxSize;

    /**
     * Constructs a {@link FixedSizePriorityQueue} with the specified
     * {@code maxSize} and a NaturalOrder Comparator.
     *
     * @param maxSize - The maximum size the queue can reach, must be a positive
     * integer.
     */
    public FixedSizePriorityQueue(final int maxSize) {
        super();
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize = " + maxSize + "; expected a positive integer.");
        }

        this.queue = new PriorityQueue<>();
        this.comparator = queue.comparator();

        this.maxSize = maxSize;
    }

    /**
     * Constructs a {@link FixedSizePriorityQueue} with the specified
     * {@code maxSize} and {@code comparator}.
     *
     * @param maxSize - The maximum size the queue can reach, must be a positive
     * integer.
     * @param comparator - The comparator to be used to compare the elements in
     * the queue, must be non-null.
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
     * Adds an element to the queue. If the queue contains {@code maxSize}
     * elements, {@code e} will be compared to the lowest element in the queue
     * using {@code comparator}. If {@code e} is greater than or equal to the
     * lowest element, that element will be removed and {@code e} will be added
     * instead. Otherwise, the queue will not be modified and {@code e} will not
     * be added.
     *
     * @param e - Element to be added, must be non-null.
     * @return true if the element has been added
     */
    @Override
    public boolean add(final E e) {
        if (e == null) {
            throw new NullPointerException("e is null.");
        }
        if (maxSize <= queue.size()) {
            final E firstElm = queue.peek();
            if (forceCompare(e, firstElm) < 1) {
                return false;
            } else {
                queue.poll();
            }
        }
        return queue.add(e);
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c){
        if (c == null) {
            throw new NullPointerException("c is null.");
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        
        if(this.queue.size() + c.size() <= maxSize){
            return this.queue.addAll(c);
        }
        
        
        boolean modified = false;
        for (E e : c){
            if (add(e)){
                modified = true;
            }
        }        
        return modified;
    }
    
    @SuppressWarnings("unchecked")
    private int forceCompare(E e1, E e2) {
        if (this.comparator != null) {
            return comparator.compare(e1, e2);
        }
        Comparable<? super E> c1 = (Comparable<? super E>) e1;
        return c1.compareTo(e2);
    }

    /**
     *
     * @return Returns true if this collection contains no elements
     */
    @Override
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    /**
     *
     * @return the number of elements in the queue, at most {@code maxSize}
     *
     */
    @Override
    public int size() {
        return queue.size();

    }

    /**
     * Returns the current element with lowest priority
     * @return 
     */
    @Override
    public E peek() {
        return this.queue.peek();
    }

    
    
    /**
     * @return Returns a sorted view of the queue as a
     * {@link Collections#unmodifiableList(java.util.List)} unmodifiableList.
     * This is sorted from high priority to low priority
     */
    public List<E> asList() {
        List<E> mutableList = new ArrayList<>(queue);
        if(this.comparator != null){
            Collections.sort(mutableList, comparator.reversed());
        } else {
            Collections.sort(mutableList, Collections.reverseOrder());
        }
        return Collections.unmodifiableList(mutableList);
    }

    @Override
    public Iterator<E> iterator() {
        return this.asList().iterator();
    }

    @Override
    public boolean offer(E e) {
        this.add(e);
        return true;
    }

    @Override
    public E poll() {
        return this.queue.poll();
 
    }
    
    

}
