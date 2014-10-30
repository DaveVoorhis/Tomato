/*
 * DBBoolean.java
 *
 * Created on January 11, 2002, 10:32 PM
 */

package ca.mb.armchair.DBAppBuilder.Helpers;

import java.lang.*;

/**
 * Utility class that implements mappings between numeric database fields
 * and Java integer types.
 *
 * @author  creatist
 */
public class DBInteger {

    /** Convert a Java integer to a representation suitable for storing
     * in a numeric database column 
     */
    public static String toColumn(int b) {
        return (new Integer(b)).toString();
    }
    
    /** Convert the contents of a numeric-type database column to a Java integer */
    public static int fromColumn(String s) {
        int i;
        try {
            i = (new Integer(s).intValue());
        } catch (NumberFormatException e) {
            i = 0;
        }
        return i;
    }
    
}
