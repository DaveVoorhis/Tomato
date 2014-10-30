/*
 * DBColumnValue.java
 *
 * Created on January 8, 2002, 9:34 PM
 */

package ca.mb.armchair.DBAppBuilder.Beans;

import javax.swing.*;
import ca.mb.armchair.DBAppBuilder.Interfaces.*;
import ca.mb.armchair.DBAppBuilder.Helpers.*;

/**
 * A DBColumnValue represents a value (a binding to a widget, or unbound) that can be stored
 * in a database column via a DBColumnModel.
 *
 *
 * @author  creatist
 */
public class DBColumnValue extends java.lang.Object implements java.io.Serializable {

    private String Description = "";
    private String ColumnName = "";
    private String DefaultColumnValue = "";
    private boolean Changed = false;
    private boolean AutomaticInsert = false;
    private boolean ReadOnly = false;
    private DBBindableComponent BoundComponent = new DBColumnUnbound();
    
    /** Creates a new instance of DBColumnValue */
    public DBColumnValue() {
    }
    
    /** Creates a new instance of DBColumnValue given the column name */
    public DBColumnValue(String columnName) {
        setColumnName(columnName);
    }
    
    /** Creates a new instance of DBColumnValue given the column name and a bound component */
    public DBColumnValue(String columnName, DBBindableComponent widget) {
        setColumnName(columnName);
        setBinding(widget);
    }
    
    /** Return true if this DBColumnValue functionally equals another */
    public boolean equals(Object vO) {
        try {
            DBColumnValue v = (DBColumnValue)vO;
            return (v.getColumnName().compareTo(getColumnName())==0 &&
                    v.getColumnValue().compareTo(getColumnValue())==0);
        } catch (Exception e) {
            return false;               // failure considered lack of equality
        }
    }
    
    /** Set this object's description */
    public void setDescription(String s) {
        Description = s;
    }
    
    /** Get this object's name */
    public String getDescription() {
        return Description;
    }
    
    /** Identify this object's column name, as it is known in the database */
    public void setColumnName(String s) {
        if (Description.length()==0)
            Description = s;
        ColumnName = s;
    }
    
    /** Get this object's column name, as it is known in the database */
    public String getColumnName() {
        return ColumnName;
    }
    
    /** Add an action listener.  This is passed down to the bound component. */
    public void addActionListener(java.awt.event.ActionListener l) {
        BoundComponent.addActionListener(l);
    }
    
    /** Remove an action listener.  This is passed down to the bound component. */
    public void removeActionListener(java.awt.event.ActionListener l) {
        BoundComponent.removeActionListener(l);
    }
    
    /** Bind this to a bindable component */
    public void setBinding(DBBindableComponent c) {
        String oldValue = getColumnValue();
        BoundComponent = c;
        setColumnValue(oldValue);
        addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setChanged(true);
            }
        });
    }
    
    /** Set the value */
    private void setValueInternal(String s) {
        BoundComponent.setColumnValue(s);
    }

    /** Explicitly set the value, and set isChanged() to false. */
    public void setInitialValue(String s) {
        setValueInternal(s);
        setChanged(false);
    }

    /** Set a default value, to be used when clear() is invoked. */
    public void setDefaultColumnValue(String s) {
        DefaultColumnValue = s;
    }
    
    /** Get the default value to be used when clear() is invoked. */
    public String getDefaultColumnValue() {
        return DefaultColumnValue;
    }
    
    /** Clear to default value, and set isChanged() to false.  Typically used prior to Insert. */
    public void clear() {
        setInitialValue(getDefaultColumnValue());
    }
    
    /** Explicitly set the value. */
    public void setColumnValue(String s) {
        setValueInternal(s);
        setChanged(true);
    }
    
    /** Get the value */
    public String getColumnValue() {
        return BoundComponent.getColumnValue();
    }

    /** Explicitly set the value.  Any escape sequences are decoded.  isChanged() is set to false. 
        Designed for updating widgets from resultset values. */
    public void setColumnValueEscaped(String s) {
        setInitialValue(s);
    }
    
    /** Get the value.  Escape special characters.
        Designed for updating the database from the widget state. */
    public String getColumnValueEscaped() {
        return DBString.toColumn(getColumnValue());
    }
    
    /** Explicitly set 'changed' flag to a given value.  Defaults to 'false'.
     *  Automatically set to true by setValue() 
     */
    public void setChanged(boolean flag) {
        Changed = flag;
    }
    
    /** Return true if the value has been changed */
    public boolean isChanged() {
        if (isReadOnly())
            return false;
        return Changed;
    }
    
    /** Explicitly set 'ReadOnly' mode.  If 'ReadOnly' is true, changes to this control will not set isChanged. */
    public void setReadOnly(boolean flag) {
        ReadOnly = flag;
    }
    
    /** Return true if in 'ReadOnly' mode. */
    public boolean isReadOnly() {
        return ReadOnly;
    }
    
    /** Return true if this column's value will be automatically generated by an INSERT, and should therefore
     * NOT be explicitly inserted into the database */
    public boolean isAutomaticInsert() {
        return AutomaticInsert;
    }

    /** If true, this column's value will be automatically generated on an INSERT, and should therefore
     * NOT be explicitly inserted into the database */
    public void setAutomaticInsert(boolean flag) {
        AutomaticInsert = flag;
    }
    
    /** Get this column name and its value as a SQL assignment clause */
    public String getSQLAssignment() {
        return getColumnName() + " = '" + getColumnValueEscaped() + "'";
    }
    
    /** Get this column name and its value as a SQL comparison clause */
    public String getSQLComparison() {
        return getSQLAssignment();
    }
}
