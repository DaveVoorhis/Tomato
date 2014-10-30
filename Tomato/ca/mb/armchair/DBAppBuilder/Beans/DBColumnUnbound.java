/*
 * DBColumnUnbound.java
 *
 * Created on January 10, 2002, 9:01 PM
 */

package ca.mb.armchair.DBAppBuilder.Beans;

/**
 * This class implements an unbound (i.e., not associated with a widget) column.
 *
 * @author  creatist
 */
public class DBColumnUnbound extends java.lang.Object implements java.io.Serializable, ca.mb.armchair.DBAppBuilder.Interfaces.DBBindableComponent {

    private String Value = "";
    
    /** Creates a new instance of DBColumnUnbound */
    public DBColumnUnbound() {
    }
    
    /** Get the value of the component.  */
    public String getColumnValue() {
        return Value;
    }
    
    /** Set the value of the component, updating it appropriately.  */
    public void setColumnValue(String v) {
        Value = v;
    }
    
    /** Add a listener to listen for a change in value to this component  */
    public void addActionListener(java.awt.event.ActionListener l) {
        System.err.println("DBColumnUnbound:addActionListener not implemented yet.");
    }
    
    /** Remove a given listener  */
    public void removeActionListener(java.awt.event.ActionListener l) {
        System.err.println("DBColumnUnbound:removeActionListener not implemented yet.");
    }
    
}
