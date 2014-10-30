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
public class Bool {
    
    private Bool() {
    }
    
    /** 'AND' operator */
    public static final boolean and(boolean x, boolean y) {
        return x && y;
    }
    
    /** 'OR' operator */
    public static final boolean or(boolean x, boolean y) {
        return x || y;
    }
    
    /** 'NOT' operator */
    public static final boolean not(boolean x) {
        return !x;
    }
    
    /** 'XOR' or difference (true if different) operator */
    public static final boolean xor(boolean x, boolean y) {
        return x ^ y;
    }
}
