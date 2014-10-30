/*
 * RootWindow.java
 *
 * Created on October 14, 2002, 6:38 PM
 */

package ca.mb.armchair.IDE;

/**
 * Base class for IDE root window panels
 *
 * @author  Dave Voorhis
 */
public class RootPanel extends javax.swing.JPanel {

    // Allowable panel states.
    public final static int PANEL_NORMALIZED = 0;
    public final static int PANEL_MAXIMIZED = 1;
    public final static int PANEL_MINIMIZED = 2;
    
    private String Title = "";
    private int PaneState = PANEL_NORMALIZED;
    
    /** Convenience function to obtain IDE. */
    public static IDE ide() {
        return IDE.getIDE();
    }
    
    /** Convenience function to print to log. */
    public static void LogPrintln(String s) {
        ide().LogPrintln(s);
    }
    
    /** Convenience function to update IDE as a result of status change. */
    public void updateIDE() {
        ide().updateIDE(this);
    }
    
    /** Get desktop area width */
    public static int getDesktopWidth() {
        return ide().getDesktopWidth();
    }
    
    /** Get desktop area height */
    public static int getDesktopHeight() {
        return ide().getDesktopHeight();
    }
    
    /** Creates a new root panel */
    public RootPanel() {
        initialize("");
    }
    
    /** Creates a new root panel */
    public RootPanel(String title) {
        initialize(title);
    }
    
    // Initialize
    private void initialize(String title) {
        setTitle(title);
        setLayout(new java.awt.BorderLayout());
        PaneState = PANEL_NORMALIZED;
    }

    // Set pane state.
    public void setPaneState(int n) {
        PaneState = n;
    }
    
    // Get pane state.
    public int getPaneState() {
        return PaneState;
    }
    
    // Purge content
    private void purgeContent() {
        while (getComponentCount()>0)
            remove(getComponent(0));
    }
    
    /** Add content panel. */
    public void addContentPanel(java.awt.Component p) {
        purgeContent();
        add(p);
    }
    
    /** Remove content. */
    public void removeContentPanel(java.awt.Component p) {
        remove(p);
    }
    
    /** Set title */
    public void setTitle(String t) {
        Title = t;
    }
    
    /** Get title */
    public String getTitle() {
        return Title;
    }
    
    /**
     * Status of the following used to update Edit menu items and other IDE bits. 
     */
    
    public boolean isChanged() {return false;}
    public boolean canPrint() {return false;}
    public boolean canUndo() {return false;}
    public boolean canRedo() {return false;}
    public boolean canCut() {return false;}
    public boolean canCopy() {return false;}
    public boolean canPaste(java.lang.Object clip) {return false;}
    public boolean canDelete() {return false;}
    public boolean canFind() {return false;}
    public boolean canReplace() {return false;}
}
