/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.unitn.disi.db.mutilities.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A set which is backed by a list of sets, does not assure that things are
 * unique
 *
 * @author Matteo Lissandrini <ml@disi.unitn.eu>
 * @param <T>
 */
public class CompoundSet<T> implements Set<T> {

    private final List<Set<T>> sets;

    public CompoundSet(List<Set<T>> sets) {
        this.sets = sets;
    }

    public CompoundSet() {
        this.sets = new ArrayList<>();
    }

    public void addSet(Set<T> set) {
        this.sets.add(set);
    }

    /**
     *
     * @return the apparent size
     */
    @Override
    public int size() {
        int apparentSize = 0;
        for (Set<? extends T> s : sets) {
            apparentSize += s.size();
        }
        return apparentSize;
    }

    @Override
    public boolean isEmpty() {
        if (this.sets.isEmpty()) {
            return true;
        }

        for (Set<? extends T> s : sets) {
            if (!s.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        for (Set<? extends T> s : this.sets) {
            if (s.contains(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        ArrayList<Iterator<T>> iters = new ArrayList<>();
        for(Set<T> s : this.sets){
            iters.add(s.iterator());
        }
        return new CompoundIterator<>(iters);
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException("This set does not support modification");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("This set does not support modification");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    public boolean addAll(Set<T> c) {
        return this.sets.add(c);
    }
    
    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("This set does not support modification");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
          throw new UnsupportedOperationException("This set does not support modification");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
           throw new UnsupportedOperationException("This set does not support modification");
    }

    @Override
    public void clear() {
                throw new UnsupportedOperationException("This set does not support modification");
    }

}
