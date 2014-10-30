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
public class RootPanelTabbed extends RootPanel {

    // Fonts
    public final static java.awt.Font TabFont = new java.awt.Font("Dialog", 0, 10);

    // Tabs
    private javax.swing.JTabbedPane tabs = new javax.swing.JTabbedPane();
    
    // Sessions managed by tabs.
    private java.util.Vector Sessions = new java.util.Vector();
    
    /** Creates a new root panel */
    public RootPanelTabbed() {
        initialize();
    }
    
    /** Creates a new root panel */
    public RootPanelTabbed(String title) {
        super(title);
        initialize();
    }

    // True if user may close tabs.
    private boolean UserCloseable = false;
    
    /** Set to 'true' to make tabs user-closeable. */
    public void setUserCloseable(boolean b) {
        UserCloseable = b;
    }
    
    /** Get value of UserCloseable property. */
    public boolean getUserCloseable() {
        return UserCloseable;
    }
    
    // Advise selected Tab contents of selection.
    private void fireTabWasSelected() {
        if (getSelectedTab() != null)
            getSelectedTab().tabWasSelected();
    }
    
    // Initialization
    private void initialize() {
        tabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                fireTabWasSelected();
            }
        });
        tabs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                fireTabWasSelected();
                if (UserCloseable)
                    if (e.getButton() != e.BUTTON1)
                        popupMenu(e.getX(), e.getY());
            }
        });
        tabs.setTabLayoutPolicy(javax.swing.JTabbedPane.WRAP_TAB_LAYOUT);
        tabs.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabs.setFont(TabFont);
        addContentPanel(tabs);
    }
    
    // Create menu item.
    private javax.swing.JMenuItem createMenuItem(String text) {
        javax.swing.JMenuItem m = new javax.swing.JMenuItem(text);
        return m;
    }

    // Pop up tab control menu.
    private void popupMenu(int mouseX, int mouseY) { 
        javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();

        javax.swing.JMenuItem close = createMenuItem("Close");
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                removeTab(getSelectedTab());
            }
        });
        popup.add(close);

        popup.show(tabs, mouseX, mouseY);
    }
    
    /** Add content into a tab. */
    public void addTab(RootPanelTabbedSession p) {
        Sessions.add(p);
        java.awt.Component c = p.getTabContent();
        tabs.addTab(c.getName(), c);
        tabs.setSelectedComponent(c);
    }
    
    /** Set the selected panel. */
    public void setSelectedTab(int i) {
        tabs.setSelectedIndex(i);
    }
    
    /** Set the selected panel. */
    public void setSelectedTab(RootPanelTabbedSession p) {
        tabs.setSelectedComponent(p.getTabContent());
    }
    
    /** Return the currently-selected content panel. */
    public RootPanelTabbedSession getSelectedTab() {
        if (Sessions == null || tabs.getSelectedIndex() == -1)
            return null;
        return getTab(tabs.getSelectedIndex());
    }
    
    /** Remove content panel. */
    public void removeTab(RootPanelTabbedSession p) {
        p.tabIsClosing();
        Sessions.remove(p);
        tabs.remove(p.getTabContent());
        if (Sessions.size()==0)                 // prevent lingering control panels
            ide().displayControlPanel(null, "");
    }
    
    /** Obtain the number of sessions being managed. */
    public int getTabCount() {
        return Sessions.size();
    }
    
    /** Obtain the ith session. */
    public RootPanelTabbedSession getTab(int i) {
        return (RootPanelTabbedSession)Sessions.get(i);
    }
}
