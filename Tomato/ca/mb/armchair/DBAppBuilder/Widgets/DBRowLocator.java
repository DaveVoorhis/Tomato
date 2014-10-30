/*
 * DBFinder.java
 *
 * Created on February 5, 2002, 9:40 PM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

import ca.mb.armchair.DBAppBuilder.Helpers.*;
import ca.mb.armchair.DBAppBuilder.Beans.*;
import ca.mb.armchair.DBAppBuilder.Interfaces.*;

/**
 * Implements a combobox for quickly locating a given row in a DBRow.
 *
 * @author  creatist
 */
public class DBRowLocator extends DBComboBox {

    private String columnName = "";
    private DBRow dBRows = null;
    
    /** Creates a new instance of DBLocator */
    public DBRowLocator() {
    }

    /** Creates a new instance of DBLocator, given DBRow and column name. */
    public DBRowLocator(DBRow row, String cName) {
        setColumnName(cName);
        setDBRow(row);
    }
    
    /** Assign column name to this Locator */
    public void setColumnName(String cName) {
        columnName = cName;
        attach();
    }
    
    /** Get assigned column name */
    public String getColumnName() {
        return columnName;
    }
    
    /** Attach this finder to a DBRow */
    public void setDBRow(DBRow row) {
        dBRows = row;
        attach();
    }

    /** Return the DBRow associated with this finder */
    public DBRow getDBRow() {
        return dBRows;
    }

    /** Attach to the specified DBRow */
    private void attach()
    {
        // don't do anything, unless we've got all the data we need
        if (getDBRow()==null || getColumnName().length()==0)
            return;
        // requery this component when the DBRow adds or deletes records
        getDBRow().addRowChangeListener(new RowChangeAdapter() {
            public void afterNewResultSet(RowChangeEvent evt) {
                requery();
            }
        });
        // add self to the DBRow as a locator column
        getDBRow().addColumn(getColumnName(),this,DBRow.COLUMNATTRIB_LOCATOR);
        // use selection to hunt down a new record
        addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getDBRow().gotoKey(new DBColumnValue(getColumnName(),DBRowLocator.this));    
            }
        });
    }
    
}
