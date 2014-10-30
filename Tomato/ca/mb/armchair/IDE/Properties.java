/*
 * Properties.java
 *
 * Created on October 15, 2002, 4:10 PM
 */

package ca.mb.armchair.IDE;

/**
 *
 * @author  creatist
 */
public class Properties extends RootPanel {
    
    private javax.swing.JScrollPane scroller = new javax.swing.JScrollPane();
    
    /** Ctor */
    public Properties() {
        setName("Properties");
        setTitle(getName());
        addContentPanel(scroller);
    }
    
    /** Add content panel. */
    public void addContentPanel(javax.swing.JPanel p) {
        scroller.setViewportView(p);
    }
}
