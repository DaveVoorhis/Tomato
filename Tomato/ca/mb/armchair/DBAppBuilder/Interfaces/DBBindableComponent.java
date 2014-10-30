/*
 * DBBindableComponent.java
 *
 * Created on January 10, 2002, 8:35 PM
 */

package ca.mb.armchair.DBAppBuilder.Interfaces;

/**
 * This interface identifies the functions necessary to bind a widget to 
 * a database via a DBColumnModel.
 *
 * @author  creatist
 */
public interface DBBindableComponent {

    /** Add a listener to listen for a change in value to this component */
    public void addActionListener(java.awt.event.ActionListener l);
    
    /** Remove a given listener */
    public void removeActionListener(java.awt.event.ActionListener l);
    
    /** Set the value of the component, updating it appropriately. */
    public void setColumnValue(String v);
    
    /** Get the value of the component. */
    public String getColumnValue();
}

