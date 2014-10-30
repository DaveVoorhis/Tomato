/*
 * RootPanelTabbedSession.java
 *
 * Created on October 22, 2002, 1:47 AM
 */

package ca.mb.armchair.IDE;

/**
 * Defines the interface necessary for a tab component of
 * a RootPanelTabbed
 *
 * @author  Dave Voorhis
 */
public interface RootPanelTabbedSession {
    
    /** Invoked by RootPanelTabbed to obtain content for Tab. */
    java.awt.Component getTabContent();
    
    /** Invoked by RootPanelTabbed when tab is selected.  May be
     * used to display properties, or other content. */
    public void tabWasSelected();

    /** Invoked by RootPanelTabbed when tab is removed.  May be
     * used to save-before-close, and other suchlike. */
    public void tabIsClosing();
    
}
