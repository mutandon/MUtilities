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
package eu.unitn.disi.db.mutilities;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.Flushable;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Utilities for strings
 *
 * @author Davide Mottin <mottin@disi.unitn.eu>
 */
public final class StringUtils {

    public static final String PUNCT_BLANK_SPLITTER = "[\\\\p{Space}\\\\p{Punct}]+";
    public static final Pattern PUNCT_BLANK_PATTERN = Pattern.compile(PUNCT_BLANK_SPLITTER);

    public static final int PAGE_SIZE = 1024;

    private StringUtils() {
    }

    /**
     * Collapse an array of strings into a single array using a separator
     * character
     *
     * @param array The array to be collapsed
     * @param separator The separator to use
     * @return The joined String
     */
    public static String join(String[] array, char separator) {
        return join(array, separator + "");
    }

    public static String join(String[] array, String separator) {
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < array.length - 1; i++) {
            sb.append(array[i]).append(separator);
        }
        sb.append(array[i]);
        return sb.toString();
    }

    /**
     * Split the string using the separator. Since it might contain special
     * characters {@link Pattern.quote} is used instead
     *
     * @param string The input string to be splitted
     * @param separator The separator string (may contian special characters)
     * @return The splitted string
     * @see Pattern
     */
    public static String[] split(String string, String separator) {
        String[] splittedString = string.split(Pattern.quote(separator));
        return splittedString;
    }

    public static void closeAll(Closeable... cls) {
        for (Closeable c : cls) {
            close(c);
        }
    }

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ex) {
            }
        }
    }

    public static void flushAll(Flushable... fs) throws IOException {
        for (Flushable f : fs) {
            flush(f);
        }
    }

    public static void flush(Flushable f) throws IOException {
        if (f != null) {
            f.flush();
        }
    }

    /**
     * Count the number of lines in a file in a very fast way. <br>
     * <i>This method may have a problem with non-ascii files</i>
     *
     * @param file The input file for which you want to know the number of lines
     * @return The number of lines
     * @throws IOException If an error occurs while reading the file
     */
    public static int countLines(String file) throws IOException {
        final byte nl = 0xA;
        BufferedInputStream in = null;
        int count = 0;
        byte[] buf = new byte[1024];
        int len, i;

        try {
            in = new BufferedInputStream(new FileInputStream(file));
            while ((len = in.read(buf)) != -1) {
                for (i = 0; i < len; i++) {
                    if (buf[i] == nl) {
                        count++;
                    }
                }
            }
        } catch (IOException ex) {
            throw new IOException(ex);
        } finally {
            close(in);
        }
        return count;
    }

    /**
     * Method to fast Split a string using a character as separator
     *
     * @param line
     * @param split
     * @param numberOfChunks
     * @return
     */
    public static String[] fastSplit(String line, char split, int numberOfChunks) {
        String[] temp = new String[numberOfChunks + 1];
        String chunk;
        int wordCount = 0;
        int i = 0;
        int j = line.indexOf(split);  // First substring
        while (j >= 0) {
            chunk = line.substring(i, j);
            if (chunk.trim().length() > 0) {
                temp[wordCount++] = chunk;
            }
            i = j + 1;
            j = line.indexOf(split, i);   // Rest of substrings
        }
        temp[wordCount++] = line.substring(i); // Last substring
        String[] result = new String[wordCount];
        System.arraycopy(temp, 0, result, 0, wordCount);
        return result;
    }

    /**
     * Read from file using optimized functions to get data as fast as possible
     *
     * @param file The file to be loaded.
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String readFile(String file) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        ByteBuffer buffer = new ByteBuffer();
        byte[] buf = new byte[PAGE_SIZE];
        int len;
        while ((len = in.read(buf)) != -1) {
            buffer.put(buf, len);
        }
        in.close();
        return new String(buffer.buffer, 0, buffer.write);
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

}
