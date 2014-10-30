/*
 * Log.java
 *
 * Created on October 25, 2002, 5:42 PM
 */

package ca.mb.armchair.JVPL;

/**
 * Handles text message logging
 *
 * @author  Dave Voorhis
 */
public class Log {
    
    // If attached to an IDE, the IDE will handle logging.
    private static ca.mb.armchair.IDE.IDEInterface ide = null;
    
    /** Creates a new instance of Log */
    private Log() {
    }
    
    /** Identify an IDE that will handle logging on our behalf. */
    public static void setIDE(ca.mb.armchair.IDE.IDEInterface anIDE) {
        ide = anIDE;
    }
    
    /** Log an entry. */
    public static void println(String s) {
        if (ide != null)
            ide.LogPrintln(s);
        else
            System.out.println("Log: " + s);
    }
    
    /** Log an entry. */
    public static void print(String s) {
        if (ide != null)
            ide.LogPrint(s);
        else
            System.out.print(s);
    }
    
    /** Log a stacktrace. */
    public static void printStackTrace(java.lang.StackTraceElement[] elements) {
        if (ide != null)
            ide.printStackTrace(elements);
        else
            for (int i=0; i<elements.length; i++)
                println("\t" + elements[i].toString());
    }
}
