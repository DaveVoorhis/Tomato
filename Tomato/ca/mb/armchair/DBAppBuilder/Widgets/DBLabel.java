/*
 * DBJLabel.java
 *
 * Created on January 10, 2002, 8:50 PM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

/**
 * A DB-aware JLabel
 *
 * @author  creatist
 */
public class DBLabel extends javax.swing.JLabel implements ca.mb.armchair.DBAppBuilder.Interfaces.DBBindableComponent {

    /** Creates a new instance of DBJLabel */
    public DBLabel() {
    }

    /** Get the value of the component.  */
    public String getColumnValue() {
        return getText();
    }
    
    /** Set the value of the component, updating it appropriately.  */
    public void setColumnValue(String v) {
        setText(v);
    }
    
    /** Add a listener to listen for a change in value to this component.  This is a No-Op.  */
    public void addActionListener(java.awt.event.ActionListener l) {
    }
    
    /** Remove a given listener.  This is a No-Op.  */
    public void removeActionListener(java.awt.event.ActionListener l) {
    }
    
}
