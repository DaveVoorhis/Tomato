/*
 * LibraryEntry.java
 *
 * Created on June 22, 2002, 7:33 AM
 */

package ca.mb.armchair.JVPL;

/**
 * A single Library entry, implemented as a TreeNode.
 *
 * Manipulated by Library.
 *
 * @author  Dave Voorhis
 */
public class LibraryEntry extends javax.swing.tree.DefaultMutableTreeNode implements java.io.Serializable {
    
    private String DisplayText;                 // Display list name (short name, eg: Boolean)
    private String LoadableClassName;           // Loadable representation to pass to getClassForName()
    
    /** Empty ctor */
    public LibraryEntry() {
        setUserObject(this);
    }
    
    /** Leaf ctor. */
    public LibraryEntry(String DisplayText, String LoadableClassName) {
        this.DisplayText = DisplayText;
        this.LoadableClassName = LoadableClassName;
        setUserObject(this);
    }
    
    /** Create a copy of a given leaf */
    public LibraryEntry(LibraryEntry l) {
        DisplayText = l.DisplayText;
        LoadableClassName = l.LoadableClassName;
        setUserObject(this);
    }
    
    /** Non-leaf ctor */
    public LibraryEntry(String DisplayText) {
        this.DisplayText = DisplayText;
        LoadableClassName = null;
        setUserObject(this);
    }
    
    /** Stringize as DisplayText */
    public String toString() {
        return DisplayText;
    }
    
    /** Set full, raw class name */
    public void setLoadableClassName(String s) {
        LoadableClassName = s;
    }
    
    /** Get full, raw class name */
    public String getLoadableClassName() {
        return LoadableClassName;
    }
    
    /** Set display text */
    public void setDisplayText(String s) {
        DisplayText = s;
    }
    
    /** Get display text */
    public String getDisplayText() {
        return DisplayText;
    }
}
