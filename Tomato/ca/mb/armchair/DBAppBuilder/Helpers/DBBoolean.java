/*
 * DBBoolean.java
 *
 * Created on January 11, 2002, 10:32 PM
 */

package ca.mb.armchair.DBAppBuilder.Helpers;

import java.lang.*;

/**
 * Utility class that implements mappings between boolean database fields
 * and Java boolean types.
 *
 * @author  creatist
 */
public class DBBoolean {

    /** Convert a Java boolean to a representation suitable for storing
     * in a boolean-type database column 
     */
    public static String toColumn(boolean b) {
        if (b)
            return "t";
        return "f";
    }
    
    /** Convert the contents of a boolean-type database column to a Java boolean */
    public static boolean fromColumn(String s) {
        try {
            if ((new Long(s).longValue())!=0)               // attempt conversion to integer
                return true;
        } catch (NumberFormatException e1) {
            try {
                if ((new Double(s).doubleValue())!=0)       // attempt conversion to float
                    return true;
            } catch (NumberFormatException e2) {
                try {
                char c = s.charAt(0);                       // interpret as string
                if (c=='Y' || c=='y' || c=='T' || c=='t')
                    return true;
                } catch (StringIndexOutOfBoundsException e3) {
                    return false;
                }
            }
        }
        return false;                                       // false, unless unambiguously true
    }
    
}
