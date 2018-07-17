/*
 * The MIT License
 *
 * Copyright 2015 Matteo Lissandrini <ml@disi.unitn.eu>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package eu.unitn.disi.db.mutilities.data;

import java.util.Comparator;
import java.util.Map;

/**
 * A Comparator for a map <T, Double>
 * Where T is any object associated with a Double Weight The Weight is used for
 * the sorting order
 *
 * @author Matteo Lissandrini <ml@disi.unitn.eu>
 * @param <T>
 */
public class WeightedComparator<T> implements Comparator<T> {

    private final Map<T, Double> weights;
    private final boolean inverse;

    public WeightedComparator(Map<T, Double> weights) {
        this.weights = weights;
        this.inverse = false;
    }

    public WeightedComparator(Map<T, Double> weights, boolean inverse) {
        this.weights = weights;
        this.inverse = inverse;
    }

    @Override
    public int compare(T i1, T i2) {
        return (inverse ? -1 : 1) * weights.get(i1).compareTo(weights.get(i2));
    }
}
