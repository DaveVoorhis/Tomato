/*
 * Shells.java
 *
 * Created on August 17, 2002, 3:57 PM
 */

package ca.mb.armchair.JVPL;

/**
 * 'Shells' is a static class that keeps track of all active Shells
 * currently in use within the VM.  This provides an access point
 * to the running environment.
 *
 * @author  Dave Voorhis
 */
public class Shells {
    
    private static java.util.Vector shells = new java.util.Vector();
    
    private Shells() {
    }

    /** Obtain quantity of active Shells. */
    public static int getShellCount() {
        return shells.size();
    }
    
    /** Obtain the i'th Shell. */
    public static Shell getShell(int i) {
        return (Shell)shells.get(i);
    }
    
    // Add a Shell.  Invoked by Shell.
    static void addShell(Shell m) {
        shells.add(m);
    }
        
    // Remove a Shell.  Invoked by Shell.
    static void removeShell(Shell m) {
        shells.remove(m);
    }
}
