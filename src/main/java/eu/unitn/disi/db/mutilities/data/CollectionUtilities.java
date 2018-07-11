/*
 * Copyright (C) 2012 Davide Mottin <mottin@disi.unitn.eu>
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

import eu.unitn.disi.db.mutilities.exceptions.ParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This is a utility class with fast methods to access files, split strings and
 * print maps and other things.
 *
 * @author Davide Mottin <mottin@disi.unitn.eu>
 */
public final class CollectionUtilities {

    public static final int PAGE_SIZE = 1024;

    private CollectionUtilities() {
    }

    public static <T> int intersectionSize(Set<T> checkCollection, Collection<T> inputCollection) {
        int count = 0;
        for (T t : inputCollection) {
            if (checkCollection.contains(t)) {
                count++;
            }
        }
        return count;
    }

    public static <T> int unionSize(Set<T> s1, Set<T> s2) {
        Set<T> min, max;
        if (s1.size() < s2.size()) {
            min = s1;
            max = s2;
        } else {
            min = s2;
            max = s1;
        }

        int count = max.size();
        for (T t : min) {
            if (!max.contains(t)) {
                count++;
            }
        }
        return count;
    }

    public static <T> int differenceSize(Set<T> set1, Set<T> set2) {
        int count = set1.size();
        for (T t : set1) {
            if (set2.contains(t)) {
                count--;
            }
        }
        return count;
    }

    public static <T> boolean intersectionNotEmpty(Set<T> checkCollection, Collection<T> inputCollection) {
        for (T t : inputCollection) {
            if (checkCollection.contains(t)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean intersectionNotEmpty(Collection<T> checkCollection, Collection<T> inputCollection) {
        for (T t : inputCollection) {
            if (checkCollection.contains(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Produce a new set
     * @param <T>
     * @param set1
     * @param set2
     * @return 
     */
    public static <T> Set<T> intersect(Set<T> set1, Set<T> set2) {
        Set<T> a;
        Set<T> b;

        if (set1.size() <= set2.size()) {
            a = set1;
            b = set2;
        } else {
            a = set2;
            b = set1;
        }
        Set<T> intersection = new HashSet<>(a.size() * 4 / 3);
        for (T e : a) {
            if (b.contains(e)) {
                intersection.add(e);
            }
        }
        return intersection;
    }
    
    /**
     * Produce a new set
     * @param <T>
     * @param set1
     * @param set2
     * @return 
     */
    public static <T> Set<T> intersect(Set<T> set1, Collection<T> set2) {        
        Set<T> intersection = new HashSet<>(set1.size() * 4 / 3);
        for (T e : set2) {
            if (set1.contains(e)) {
                intersection.add(e);
            }
        }
        return intersection;
    }
    
    

    public static <T> List<T> intersect(List<T> list1, Set<T> set2) {
        List<T> intersection = new ArrayList<>(set2.size());

        for (T e : list1) {
            if (set2.contains(e)) {
                intersection.add(e);
            }
        }
        return intersection;
    }

    public static <T> List<T> intersect(List<T> list1, List<T> list2) {
        List<T> a;
        List<T> b;
        List<T> intersection;
        if (list1.size() <= list2.size()) {
            a = list1;
            b = list2;
            intersection = new ArrayList<>(list1.size());
        } else {
            a = list2;
            b = list1;
            intersection = new ArrayList<>(list2.size());
        }
        for (T e : a) {
            if (b.contains(e)) {
                intersection.add(e);
            }
        }
        return intersection;
    }

    static class ByteBuffer {

        public byte[] buffer = new byte[256];
        public int write;

        public void put(byte[] buf, int len) {
            ensure(len);
            System.arraycopy(buf, 0, buffer, write, len);
            write += len;
        }

        private void ensure(int amt) {
            int req = write + amt;
            if (buffer.length <= req) {
                byte[] temp = new byte[req * 2];
                System.arraycopy(buffer, 0, temp, 0, write);
                buffer = temp;
            }
        }
    }

    /**
     * Sort a bidimensional array using the first value of each array
     *
     * @param table The multidimensional array to be sorted
     */
    public static void binaryTableSort(long[][] table) {
        Arrays.sort(table, new TableComparator());
    }

    private static final class SortTask implements Callable<Void> {

        private final long[][] src;
        private final int from;
        private final int to;
        private final Comparator<? super long[]> comparator;

        public SortTask(long[][] src, int from, int to, Comparator<? super long[]> comparator) {
            this.src = src;
            this.from = from;
            this.to = to;
            this.comparator = comparator;
        }

        @Override
        public Void call() throws Exception {
            Arrays.sort(src, from, to > src.length ? src.length : to, comparator);
            return null;
        }
    }

    private static final class MergeTask implements Callable<Void> {

        private final long[][] src;
        private final long[][] dest;
        private final int from1, to1;
        private final int from2, to2;
        private final Comparator<? super long[]> comparator;

        public MergeTask(long[][] src, long[][] dest, int from1, int to1, int from2, int to2, Comparator<? super long[]> comparator) {
            this.src = src;
            this.dest = dest;
            this.from1 = from1;
            this.to1 = to1;
            this.from2 = from2;
            this.to2 = to2;
            this.comparator = comparator;
        }

        @Override
        public Void call() throws Exception {
            int destLow = from1 < from2 ? from1 : from2;
            int destHigh = to1 > to2 ? to1 : to2;

            for (int i = destLow, p = from1, q = from2; i < destHigh; i++) {
                if (q >= to2 || p < to1 && comparator.compare(src[p], src[q]) <= 0) {
                    dest[i] = src[p++];
                } else {
                    dest[i] = src[q++];
                }
            }
            return null;
        }
    }

    private static final class CopyTask implements Callable<Void> {

        private final long[][] src;
        private final long[][] dest;
        private final int from, to;

        public CopyTask(long[][] src, long[][] dest, int from, int to) {
            this.src = src;
            this.dest = dest;
            this.from = from;
            this.to = to;
        }

        @Override
        public Void call() throws Exception {
            System.arraycopy(src, from, dest, from, to - from);
            return null;
        }
    }

    private static class TableComparator implements Comparator<long[]> {

        @Override
        public int compare(long[] o1, long[] o2) {
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            if (o1[0] > o2[0]) {
                return 1;
            }
            if (o1[0] < o2[0]) {
                return -1;
            }
            return 0;
        }
    }

    /**
     * Sort a bidimensional array using a parallelized version of merge sort
     *
     * @param table The bidimensional array to be sorted
     * @param numThreads The number of threads to be created
     * @return
     * @throws java.lang.InterruptedException
     * @throws java.util.concurrent.ExecutionException
     */
    public static long[][] parallelBinaryTableSort(long[][] table, int numThreads)
            throws InterruptedException, ExecutionException, IllegalArgumentException {
        int chunkSize = (int) Math.round(table.length / (double) numThreads + 0.5);
        List<Future<Void>> tasks = new ArrayList<>();
        int threads = numThreads, lastThreads;
        int rest = 0, offset, start;
        int multiplier;
        long[][] dest = new long[table.length][], tmp, src;
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        TableComparator comparator = new TableComparator();
        int i;

        try {
            //SORT
            for (i = 0; i < threads && i * chunkSize < table.length; i++) {
                tasks.add(pool.submit(new SortTask(table, i * chunkSize, (i + 1) * chunkSize, comparator)));
            }
            numThreads = threads = i;
            //Check completion
            for (Future<Void> task : tasks) {
                task.get();
            }
            //MERGE                
            src = table;
            tasks.clear();
            multiplier = 1;
            lastThreads = threads;
            while (threads > 0) {
                threads >>>= 1;

                offset = chunkSize * multiplier;
                start = 0;
                for (i = 0; i < threads + rest; i++) {
                    tasks.add(pool.submit(new MergeTask(src, dest,
                            start * offset, (start + 1) * offset, //First chunk limits
                            (start + 1) * offset, (start + 2) * offset > table.length ? table.length : (start + 2) * offset, //Second chunk limits
                            comparator)));
                    start += 2;
                }
                if (start * offset < table.length) {
                    tasks.add(pool.submit(new CopyTask(src, dest, start * offset, table.length)));
                }
                for (Future<Void> task : tasks) {
                    task.get();
                }
                tasks.clear();
                //swap
                tmp = src;
                src = dest;
                dest = tmp;
                //update rest
                rest = lastThreads % 2;
                lastThreads = threads;
                multiplier <<= 1;
            }
            //COPY (if needed)
            if (src != table) {
                System.out.println("warn: dest != table");
                for (i = 0; i < numThreads; i++) {
                    offset = (i + 1) * chunkSize;
                    tasks.add(pool.submit(new CopyTask(src, table, i * chunkSize, offset > table.length ? table.length : offset)));
                }
                for (Future<Void> task : tasks) {
                    task.get();
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            throw ex;
        } finally {
            pool.shutdown();
        }
        return dest;
    }

    public static int binaryTableSearch(long[][] table, long vertex) {
        return binaryTableSearch(table, 0, vertex);
    }

    public static int binaryTableSearch(long[][] table, int searchField, long vertex) {
        return binaryTableSearch0(table, searchField, 0, table.length, vertex);
    }

    public static int binaryTableSearch0(long[][] a, int searchField, int fromIndex, int toIndex, long key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            long midVal = a[mid][searchField]; //Take the first

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1);  // key not found.
    }

    /**
     * Read a file into a collection of strings
     *
     * @param file The input file to be read
     * @param collection The collection of elements to be populated
     * @throws NullPointerException If some of the inputs is null
     * @throws IOException If the file is not readable
     */
    public static void readFileIntoCollection(String file, Collection<String> collection)
            throws NullPointerException, IOException {
        if (collection == null || file == null) {
            throw new NullPointerException("Input cannot be null");
        }
        BufferedReader fileReader;
        String line;
        int lineNo = 0;

        fileReader = new BufferedReader(new FileReader(file));
        while ((line = fileReader.readLine()) != null) {
            lineNo++;
            if (!"".equals(line.trim())) {
                collection.add(line);
            }
        }
    }

    /**
     * Read a file into a collection of numbers
     *
     * @param <T> The type into which converting the string in the lines
     * @param file The input file (each line represents a record in the
     * collection)
     * @param collection The collection of elements to be populated
     * @param castType The type into which casting the lines
     * @throws IOException If the file is not readable
     * @throws NullPointerException If some of the inputs is null
     * @throws InvalidClassException If the input type does not allow a string
     * constructor
     * @throws ParseException If the line is not of the correct type
     */
    public static <T extends Number> void
            readFileIntoCollection(String file, Collection<T> collection, Class<T> castType)
            throws IOException, NullPointerException, InvalidClassException, ParseException {
        if (collection == null || file == null || castType == null) {
            throw new NullPointerException("Input cannot be null");
        }
        File reader = new File(file);
        if (!reader.exists() || !reader.canRead()) {
            throw new ParseException("Cannot read file %s", file);
        }

        Constructor<T> numConstructor;
        BufferedReader fileReader;
        String line;
        int lineNo = 0;

        try {
            numConstructor = castType.getConstructor(String.class);
            fileReader = new BufferedReader(new FileReader(reader));
            while ((line = fileReader.readLine()) != null) {
                lineNo++;
                if (!line.trim().isEmpty()) {
                    collection.add(numConstructor.newInstance(line));
                }
            }

        } catch (NoSuchMethodException ex) {
            throw new InvalidClassException(String.format("The input class %s cannot be parsed since it does not contain a constructor that takes in input a String", castType.getCanonicalName()));
        } catch (SecurityException | IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ParseException("Cannot convert line %s into class %s", ex, lineNo, castType.getCanonicalName());
        }
    }

    /**
     * Read a file into a key-value map, the keys and values can be cast to any
     * arbitrary NUMERIC class
     *
     * @param <K> The class of the keys
     * @param <V> The class of the values
     * @param file The input file to be stored in a map
     * @param separator The sepator used to identify fields
     * @param map The map to be populated
     * @param keyCastType The class of the keys to be casted
     * @param valueCastType The class of the values to be casted
     * @throws IOException If the file is not readable
     * @throws NullPointerException If some of the inputs is null
     * @throws InvalidClassException If the input type does not allow a string
     * constructor
     * @throws ParseException If the line is not of the correct type
     */
    public static <K extends Number, V extends Number> void
            readFileIntoMap(String file, String separator, Map<K, V> map, Class<K> keyCastType, Class<V> valueCastType)
            throws IOException, NullPointerException, InvalidClassException, ParseException {
        if (map == null || file == null || keyCastType == null || valueCastType == null || separator == null) {
            throw new NullPointerException("Input cannot be null");
        }
        Constructor<K> keyConstructor;
        Constructor<V> valueConstructor;
        BufferedReader fileReader;
        String line;
        String[] splittedLine;
        int lineNo = 0;

        try {
            keyConstructor = keyCastType.getConstructor(String.class);
            valueConstructor = valueCastType.getConstructor(String.class);
            fileReader = new BufferedReader(new FileReader(file));
            while ((line = fileReader.readLine()) != null) {
                lineNo++;
                if (!"".equals(line.trim())) {
                    splittedLine = line.split(separator);
                    if (splittedLine.length != 2) {
                        throw new ParseException("Line %d has an invalid format", lineNo);
                    }
                    try {
                        map.put(keyConstructor.newInstance(splittedLine[0]), valueConstructor.newInstance(splittedLine[1]));
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        throw new ParseException("Cannot convert line %d into classes %s and %s", ex, lineNo, keyCastType.getCanonicalName());
                    }
                }
            }
        } catch (NoSuchMethodException ex) {
            throw new InvalidClassException(String.format("The input class %s or %s cannot be parsed since it does not contain a constructor that takes in input a String", keyCastType.getCanonicalName(), valueCastType.getCanonicalName()));
        }
    }

    /**
     * Read a file into a key-value map, the keys can be cast to any arbitrary
     * NUMERIC class the values will be Strings
     *
     * @param <K> The class of the keys
     * @param file The input file to be stored in a map
     * @param separator The sepator used to identify fields
     * @param map The map to be populated
     * @param keyCastType The class of the keys to be casted
     * @throws IOException If the file is not readable
     * @throws NullPointerException If some of the inputs is null
     * @throws InvalidClassException If the input type does not allow a string
     * constructor
     * @throws ParseException If the line is not of the correct type
     */
    public static <K extends Number> void
            readFileIntoMap(String file, String separator, Map<K, String> map, Class<K> keyCastType)
            throws IOException, NullPointerException, InvalidClassException, ParseException {
            readFileIntoMap(file,  separator, map, keyCastType, 0,1);
    }

    /**
     * Read a file into a key-value map, the keys can be cast to any arbitrary
     * NUMERIC class the values will be Strings
     *
     * @param <K> The class of the keys
     * @param file The input file to be stored in a map
     * @param separator The sepator used to identify fields
     * @param map The map to be populated
     * @param keyCastType The class of the keys to be casted
     * @param keyPos position in each line of the key value (count from 0)
     * @param valPos position in each line of the map value (count from 0)
     * @throws IOException If the file is not readable
     * @throws NullPointerException If some of the inputs is null
     * @throws InvalidClassException If the input type does not allow a string
     * constructor
     * @throws ParseException If the line is not of the correct type
     */            
    public static <K extends Number> void
            readFileIntoMap(String file, String separator, Map<K, String> map, Class<K> keyCastType, int keyPos, int valPos)
            throws IOException, NullPointerException, InvalidClassException, ParseException {
        if (map == null) {
            throw new NullPointerException("Input MAP cannot be null");
        } else if (file == null) {
            throw new NullPointerException("Input FILE cannot be null");
        } else if (keyCastType == null) {
            throw new NullPointerException("Input Key Class cannot be null");
        } else if (separator == null) {
            throw new NullPointerException("Input Seprator cannot be null");
        }
        Constructor<K> keyConstructor;        
        String line;
        String[] splittedLine;
        int lineNo = 0;
        int numFields = Math.max(keyPos, valPos)+1;

        try {
            keyConstructor = keyCastType.getConstructor(String.class);
            File toRead = new File(file);
            if (!toRead.exists()) {
                throw new FileNotFoundException(file + " not found!");
            }
            if (!toRead.isFile()) {
                throw new IllegalArgumentException(file + " is not a regular file!");
            }
            if (!toRead.canRead()) {
                throw new IOException(file + " is not readable!");
            }

            try (BufferedReader br = Files.newBufferedReader(toRead.toPath())) {

                while ((line = br.readLine()) != null) {
                    lineNo++;
                    line = line.trim();
                    if (!line.isEmpty() && line.contains("\t")) {
                        splittedLine = line.split(separator);
                        if (splittedLine.length < numFields) {
                            throw new ParseException("Line %d has an invalid format", lineNo);
                        }
                        try {
                            map.put(keyConstructor.newInstance(splittedLine[keyPos]), splittedLine[valPos]);
                        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            throw new ParseException("Cannot convert line %d into classes %s and %s", ex, lineNo, keyCastType.getCanonicalName());
                        }
                    }
                }

            } catch (IOException e) {
                throw new IOException("Convert to map failed", e);
            }

        } catch (NoSuchMethodException ex) {
            throw new InvalidClassException(String.format("The input class %s cannot be parsed since it does not contain a constructor that takes in input a String", keyCastType.getCanonicalName()));
        }
    }

    public static <A, B> String mapToString(Map<A, B> m) {
        StringBuilder sb = new StringBuilder();
        Set<A> keys = m.keySet();
        sb.append("{");
        for (A key : keys) {
            sb.append("(").append(key).append(",").append(m.get(key)).append(")");
        }
        sb.append("}");
        return sb.toString();
    }

    public static int[] convertListIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next();
        }
        return ret;
    }

    public static long[] convertListLongs(List<Long> longs) {
        long[] ret = new long[longs.size()];
        Iterator<Long> iterator = longs.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next();
        }
        return ret;
    }

    /**
     *
     * @param <T> Type
     * @param lst A sorted list of values to de duplicate
     * @return a list of values without duplicates
     */
    public static <T> List<T> uniq(List<T> lst) {
        ArrayList<T> list = new ArrayList<>(lst.size());
        T last = null;
        for (T t : lst) {
            if (!Objects.equals(last, t)) {
                list.add(t);
                last = t;
            }
        }
        return list;
    }

    /**
     *
     * @param <T>
     * @param c the collection
     * @return the first element in no particular order
     */
    public static <T> T getOne(Collection<T> c) {
        return c.iterator().hasNext() ? c.iterator().next() : null;
    }

    /**
     *
     * @param <T>
     * @param c
     * @return a sorted list copy of the passed collection
     */
    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<>(c);
        Collections.sort(list);
        return list;
    }

    /**
     *
     * @param <T>
     * @param list
     * @param element
     * @param c
     * @return the position of the element if present, -1 otherwise
     */
    public static <T extends Comparable<? super T>> int binarySearchOf(List<T> list, T element, Comparator<? super T> c) {
        return binarySearchOf(list, element, 0, list.size(), c);
    }

    /**
     *
     * @param <T>
     * @param list
     * @param element
     * @return the position of the element if present, -1 otherwise
     */
    public static <T extends Comparable<? super T>> int binarySearchOf(List<T> list, T element) {
        return binarySearchOf(list, element, 0, list.size(), null);
    }

    /**
     *
     * @param <T>
     * @param list
     * @param element
     * @param start
     * @param end
     * @param c
     * @return the position of the element if present, -1 otherwise
     */
    public static <T extends Comparable<? super T>> int binarySearchOf(List<T> list, T element, int start, int end, Comparator<? super T> c) {
        int mid = (start + end) / 2;
        if (start > end || mid >= end) {
            return -1;
        }
        int compared = -1;
        try {
            compared = c == null ? list.get(mid).compareTo(element) : c.compare(list.get(mid), element);
        } catch (Exception e) {
            throw new IllegalStateException("Exception while searching for " + element + " from " + start + " to " + end, e);
        }

        if (compared == 0) {
            return mid;
        } else if (compared < 0) {
            return binarySearchOf(list, element, mid + 1, end, c);
        } else {
            return binarySearchOf(list, element, start, mid, c);
        }
    }

    
    public static <K> void normalizeMap(Map<K, Double> map) {
        Set<K> keys = map.keySet();
        double sum = 0.0 + map.values().stream().mapToDouble(Double::doubleValue).sum();

        for (K key : keys) {
            map.put(key, map.get(key)/sum);
        }
    }

    
    
}
