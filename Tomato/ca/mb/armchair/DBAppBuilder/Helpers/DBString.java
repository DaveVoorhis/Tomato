/*
 * DBBoolean.java
 *
 * Created on January 11, 2002, 10:32 PM
 */

package ca.mb.armchair.DBAppBuilder.Helpers;

import java.lang.*;

/**
 * Utility class that implements mappings between database fields
 * and Java string types, with escaping/unescaping of special
 * characters.
 *
 * @author  creatist
 */
public class DBString {
    
    /** Special characters that must be quoted */
    private static final String specialSaveChars = "\";'";

    /** Convert a string to a representation suitable for storing
     *  in a database column, by escaping all unpleasantnesses.
     */
    public static String toColumn(String s) {
        int len = s.length();
        StringBuffer outBuffer = new StringBuffer(len*2);

        for(int x=0; x<len; x++) {
            char aChar = s.charAt(x);
            switch (aChar) {
                case '\\':outBuffer.append('\\'); outBuffer.append('\\');
                          break;
                case '\t':outBuffer.append('\\'); outBuffer.append('t');
                          break;
                case '\n':outBuffer.append('\\'); outBuffer.append('n');
                          break;
                case '\r':outBuffer.append('\\'); outBuffer.append('r');
                          break;
                case '\f':outBuffer.append('\\'); outBuffer.append('f');
                          break;
                default:
                    if ((aChar < 0x0020) || (aChar > 0x007e)) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >>  8) & 0xF));
                        outBuffer.append(toHex((aChar >>  4) & 0xF));
                        outBuffer.append(toHex( aChar        & 0xF));
                    } else {
                        if (specialSaveChars.indexOf(aChar) != -1)
                            outBuffer.append('\\');
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }
    
    /** Convert the contents of a database column to a string, unescaping any
        escape sequences.   Liberally glommed from Sun source code.   Thanks, guys. */
    public static String fromColumn(String s) {
        char aChar;
        int len = s.length();
        StringBuffer outBuffer = new StringBuffer(len);

        for (int x=0; x<len; ) {
            aChar = s.charAt(x++);
            if (aChar == '\\') {
                aChar = s.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value=0;
		    for (int i=0; i<4; i++) {
		        aChar = s.charAt(x++);
		        switch (aChar) {
		          case '0': case '1': case '2': case '3': case '4':
		          case '5': case '6': case '7': case '8': case '9':
		             value = (value << 4) + aChar - '0';
			     break;
			  case 'a': case 'b': case 'c':
                          case 'd': case 'e': case 'f':
			     value = (value << 4) + 10 + aChar - 'a';
			     break;
			  case 'A': case 'B': case 'C':
                          case 'D': case 'E': case 'F':
			     value = (value << 4) + 10 + aChar - 'A';
			     break;
			  default:
                              throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                        }
                    }
                    outBuffer.append((char)value);
                } else {
                    if (aChar == 't') aChar = '\t';
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();        
    }
    
    /**
     * Convert a nibble to a hex character
     * @param	nibble	the nibble to convert.
     */
    private static char toHex(int nibble) {
	return hexDigit[(nibble & 0xF)];
    }

    /** A table of hex digits */
    private static final char[] hexDigit = {
	'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };

}
