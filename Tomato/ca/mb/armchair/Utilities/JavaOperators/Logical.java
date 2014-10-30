/*
 * Bool.java
 *
 * Created on August 18, 2002, 5:58 PM
 */

package ca.mb.armchair.Utilities.JavaOperators;

/**
 *
 * @author  creatist
 */
public class Logical {

    private Logical() {
    }

    // Integer
    
    /** right shift of 'x' by distance 'y' */
    public static final int rightshift(int x, int y) {
        return x >> y;
    }
    
    /** left shift of 'x' by distance 'y' */
    public static final int leftshift(int x, int y) {
        return x << y;
    }
    
    /** right shift of 'x' by unsigned distance 'y' */
    public static final int rightshiftunsigned(int x, int y) {
        return x >>> y;
    }
    
    /** bitwise 'AND' */
    public static final int and(int x, int y) {
        return x & y;
    }
    
    /** bitwise 'OR' */
    public static final int or(int x, int y) {
        return x | y;
    }
    
    /** bitwise 'XOR' */
    public static final int xor(int x, int y) {
        return x ^ y;
    }
    
    /** bitwise 'NOT' */
    public static final int not(int x) {
        return ~x;
    }

    // Long

    /** right shift of 'x' by distance 'y' */
    public static final long rightshift(long x, long y) {
        return x >> y;
    }
    
    /** left shift of 'x' by distance 'y' */
    public static final long leftshift(long x, long y) {
        return x << y;
    }
    
    /** right shift of 'x' by unsigned distance 'y' */
    public static final long rightshiftunsigned(long x, long y) {
        return x >>> y;
    }
    
    /** bitwise 'AND' */
    public static final long and(long x, long y) {
        return x & y;
    }
    
    /** bitwise 'OR' */
    public static final long or(long x, long y) {
        return x | y;
    }
    
    /** bitwise 'XOR' */
    public static final long xor(long x, long y) {
        return x ^ y;
    }
    
    /** bitwise 'NOT' */
    public static final long not(long x) {
        return ~x;
    }
}
