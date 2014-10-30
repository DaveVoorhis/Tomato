/*
 * DBColumnValues.java
 *
 * Created on January 8, 2002, 9:53 PM
 */

package ca.mb.armchair.DBAppBuilder.Beans;

import java.util.*;
import javax.swing.*;


/**
 * A collection of DBColumnValue objects.  Useful for representing keys, rows, etc.
 *
 * @author  creatist
 */
public class DBColumnValues extends java.lang.Object implements java.io.Serializable {

    private Vector ColumnValues = new Vector();
        
    /** Creates a new instance of DBColumnValues */
    public DBColumnValues() {
    }

    /** Obtain a static snapshot of this DBColumnValues.  Useful for memorising key values, etc. */
    public DBColumnValues snapshot() {
        DBColumnValues v = new DBColumnValues();
        for (int i=0; i<getSize(); i++) {
            DBColumnValue val = new DBColumnValue(getColumnName(i));
            val.setInitialValue(getColumnValue(i));
            v.add(val);
        }
        return v;
    }
    
    /** Add a DBColumnValue. */
    public void add(DBColumnValue dbC) {
        ColumnValues.add(dbC);
    }

    /** Return true if this DBColumnValues collection equals another */
    public boolean equals(Object vO) {
        try {
            DBColumnValues v = (DBColumnValues)vO;
            if (v.getSize()!=getSize())
                return false;
            for (int i=0; i<v.getSize(); i++)
                if (!v.getDBColumnValue(i).equals(getDBColumnValue(i)))
                    return false;
            return true;
        } catch (Exception e) {
            return false;           // breakage considered inequal
        }
    }
    
    /** Return the number of DBColumnValues in this container */
    public int getSize() {
        return ColumnValues.size();
    }
    
    /** Get the DBColumnValue at a specified index.*/
    public DBColumnValue getDBColumnValue(int index)
    {
        if (index<0 || index>=getSize())
            return null;
        return (DBColumnValue)ColumnValues.get(index);
    }

    /** Get a database column name given its index */
    public String getColumnName(int index) {
        return getDBColumnValue(index).getColumnName();
    }
    
    /** Get a database column description given its index */
    public String getDescription(int index) {
        return getDBColumnValue(index).getDescription();
    }
    
    /** Get a new, preloaded ListModel of ColumnValues suitable for use with a JList */
    public DefaultListModel getListModel() {
        DefaultListModel model=new DefaultListModel();
        for (int i=0; i<getSize(); i++)
            model.addElement(getDescription(i));
        return model;
    }
    
    /** Get a new, preloaded ComboboxModel of ColumnValues, suitable for use with a JCombobox */
    public DefaultComboBoxModel getComboBoxModel() {
        DefaultComboBoxModel model=new DefaultComboBoxModel();
        for (int i=0; i<getSize(); i++)
            model.addElement(getDescription(i));
        return model;
    }
    
    /** Set a default value, to be used when clear() is invoked. */
    public void setDefaultColumnValue(int index, String s) {
        getDBColumnValue(index).setDefaultColumnValue(s);
    }
    
    /** Get the default value to be used when clear() is invoked. */
    public String getDefaultColumnValue(int index) {
        return getDBColumnValue(index).getDefaultColumnValue();
    }
    
    /** Clear to default value, and set isChanged() to false.  Typically used prior to Insert. */
    public void clear(int index) {
        getDBColumnValue(index).clear();
    }

    /** Return the value at a given index */
    public String getColumnValue(int index) {
        return getDBColumnValue(index).getColumnValue();
    }
    
    /** Set the value at a given index */
    public void setColumnValue(int index, String s) {
        getDBColumnValue(index).setColumnValue(s);
    }
    
    /** Explicitly set the value.  Any escape sequences are decoded.  isChanged() is set to false. 
        Designed for updating widgets from resultset values. */
    public void setColumnValueEscaped(int index, String s) {
        getDBColumnValue(index).setColumnValueEscaped(s);
    }
    
    /** Get the value.  Escape special characters.
        Designed for updating the database from the widget state. */
    public String getColumnValueEscaped(int index) {
        return getDBColumnValue(index).getColumnValueEscaped();
    }

    /** Return the SQL representing a given column's assignment. */
    public String getSQLAssignment(int index) {
        return getDBColumnValue(index).getSQLAssignment();
    }
    
    /** Return the SQL representing a given column's comparison */
    public String getSQLComparison(int index) {
        return getDBColumnValue(index).getSQLComparison();
    }
    
    /** Return DBColumnValue at given JList index */
    public DBColumnValue getDBColumnValueFromSelection(JList list)
    {
        int index = list.getSelectedIndex();
        if (index!=-1)
            return getDBColumnValue(index);
        return null;
    }

    /** Return the value at a given JList index */
    public String getValueFromSelection(JList list)
    {
        int index = list.getSelectedIndex();
        if (index!=-1)
            return getColumnValue(index);
        return null;
    }
    
    /** Return the DBColumnValue at a given JComboBox selection */
    public DBColumnValue getDBColumnValueFromSelection(JComboBox box)
    {
        int index = box.getSelectedIndex();
        if (index!=-1)
            return getDBColumnValue(index);
        return null;
    }
    
    /** Return the value at a given JComboBox selection */
    public String getValueFromSelection(JComboBox box)
    {
        int index = box.getSelectedIndex();
        if (index!=-1)
            return getColumnValue(index);
        return null;
    }
    
    /** Return true if a value has been changed */
    public boolean isChanged(int index) {
        return getDBColumnValue(index).isChanged();
    }
    
    /** Explicitly set 'changed' flag to a given value.  Defaults to 'false'.
     *  Automatically set to true by setValue() 
     */
    public void setChanged(int index, boolean flag) {
        getDBColumnValue(index).setChanged(flag);
    }

    /** Explicitly clear all 'changed' flags. */
    public void clearChanged() {
        for (int i=0; i<getSize(); i++)
            setChanged(i, false);
    }
    
    /** Return true if any values have been changed */
    public boolean isChanged() {
        for (int i=0; i<getSize(); i++)
            if (isChanged(i))
                return true;
        return false;
    }
    
    /** Return true if given column's value will be automatically generated by an INSERT, and should therefore
    * NOT be explicitly inserted into the database */
    public boolean isAutomaticInsert(int index) {
        return getDBColumnValue(index).isAutomaticInsert();
    }

    /** If true, given column's value will be automatically generated on an INSERT, and should therefore
     * NOT be explicitly inserted into the database */
    public void setAutomaticInsert(int index, boolean flag) {
        getDBColumnValue(index).setAutomaticInsert(flag);
    }
    
    /** Return an appropriate SQL 'where' clause that
     *  can be used to obtain row(s) associated with this set of ColumnValues 
     */
    public String getSQLWhereClause()
    {
        String SQL = "";
        for (int i=0; i<getSize(); i++)
        {
            if (SQL.length()>0)
                SQL = SQL + " AND ";
            SQL = SQL + getSQLComparison(i);
        }
        return SQL;
    }
}
