/*
 * Editor.java
 *
 * Created on October 15, 2002, 4:10 PM
 */

package ca.mb.armchair.IDE;

/**
 * Editor is a RootPanelTabbed.
 *
 * @author  Dave Voorhis
 */
public class Editor extends RootPanelTabbed {
    
    /** Ctor */
    public Editor() {
        setName("Editor");
        setTitle(getName());
        setUserCloseable(true);
    }

}
