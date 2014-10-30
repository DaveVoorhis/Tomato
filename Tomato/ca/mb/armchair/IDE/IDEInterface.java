/*
 * IDEInterface.java
 *
 * Created on October 22, 2002, 1:47 AM
 */

package ca.mb.armchair.IDE;

/**
 * Defines the interface necessary for a mechanism to interface
 * with an IDE as the IDE server.
 *
 * @author  creatist
 */
public interface IDEInterface {

    /** Display a control panel (typically a Properties display) with a given title. */
    public void displayControlPanel(final javax.swing.JPanel controlPanel, String title);
    
    /** Ensure IDE is visible. */
    public void show();
    
    /** Send a line to the Log. */
    public void LogPrintln(String s);
    
    /** Send a string to the Log. */
    public void LogPrint(String s);

    /** Log a stacktrace. */
    public void printStackTrace(java.lang.StackTraceElement[] elements);
    
    /** Register the fact that a background process has begun. */
    public void registerDaemon();
    
    /** Register the fact that a background process has ended. */
    public void unregisterDaemon();
    
    /** Return number of running background processes. */
    public int getRegisteredDaemonCount();
    
    /** Tell the IDE to update the Explorer, because we've modified something the Explorer displays. */
    public void updateExplorer();
}
