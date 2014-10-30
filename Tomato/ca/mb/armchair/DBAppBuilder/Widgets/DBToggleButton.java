/*
 * DBToggleButton.java
 *
 * Created on January 10, 2002, 8:50 PM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

import ca.mb.armchair.DBAppBuilder.Helpers.*;

/**
 * A DB-aware JToggleButton
 *
 * @author  creatist
 */
public class DBToggleButton extends javax.swing.JToggleButton implements ca.mb.armchair.DBAppBuilder.Interfaces.DBBindableComponent {

    private java.util.Vector listeners = new java.util.Vector();

    /** Creates a new instance of DBToggleButton */
    public DBToggleButton() {
        addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                for (int i=0; i<listeners.size(); i++)
                    ((java.awt.event.ActionListener)listeners.elementAt(i)).actionPerformed(new java.awt.event.ActionEvent(this,i,"Update"));
            }
        });
    }

    /** Get the value of the component.  */
    public String getColumnValue() {
        return DBBoolean.toColumn(getModel().isSelected());
    }
    
    /** Set the value of the component, updating it appropriately.  */
    public void setColumnValue(String v) {
        setSelected(DBBoolean.fromColumn(v));
    }
    
    /** Add a listener to listen for a change in value to this component  */
    public void addActionListener(java.awt.event.ActionListener l) {
        super.addActionListener(l);
        listeners.add(l);
    }
    
    /** Remove a given listener  */
    public void removeActionListener(java.awt.event.ActionListener l) {
        super.removeActionListener(l);
        listeners.remove(l);
    }
}
