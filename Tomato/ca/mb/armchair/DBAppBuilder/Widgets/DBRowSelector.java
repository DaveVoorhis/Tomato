/*
 * dbRowelector.java
 *
 * Created on February 11, 2002, 6:50 PM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

import ca.mb.armchair.DBAppBuilder.Beans.*;
import ca.mb.armchair.DBAppBuilder.Helpers.*;

/**
 * Widget for selecting records in a DBRow.
 *
 * @author  creatist
 */
public class DBRowSelector extends javax.swing.JToggleButton {

    /** The DBRow we're controlling */
    DBRow dbRow = new DBRow();

    /** Creates a new instance of DBRowSelector */
    public DBRowSelector() {
        initialize();
    }

    /** Creates a new instance of DBRowSelector, given an editable query (DBRow) */
    public DBRowSelector(DBRow dbRow) {
        initialize();
        setDBRow(dbRow);
    }

    /** Initialize */
    private void initialize() {
        setText("*");
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                select();
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                select();
            }
        });
    }
    
    /** Set the DBRow over which to make selections */
    public void setDBRow(DBRow dbR) {
        dbRow = dbR;
        dbRow.addRowChangeListener(new RowChangeAdapter() {
            public void afterNewResultSet(RowChangeEvent e) {
                clearSelectedRows();
            }
            public void refreshController(RowChangeEvent e) {
                refresh();
            }
        });
    }
    
    /** Get the DBRow associated with this selector */
    public DBRow getDBRow() {
        return dbRow;
    }

    /** Handle select/unselect event */
    public void select() {
        if (dbRow.isChanged() || dbRow.isInsertMode())
            dbRow.update();
        else
            setSelectedRow(dbRow.getKey(),isSelected());
    }
    
    /** Set state of selector from DBRow */
    public void refresh() {
        if (dbRow.isBroken() || dbRow.isEmpty()) {
            setEnabled(false);
            setSelected(false);
            setText("X");
        } else if (dbRow.isInsertMode() && !dbRow.isChanged()) {
            setEnabled(true);
            setSelected(false);
            setText("+");
        } else if (dbRow.isChanged()) {
            setEnabled(true);
            setSelected(false);
            setText("*");
        } else {
            setEnabled(true);
            setSelected(isSelectedRow(dbRow.getKey()));
            setText("<");
        }
    }

    /** Collection of selections */
    private java.util.Vector Selected = new java.util.Vector();

    /** Return number of selections */
    public int getSelectionCount() {
        return Selected.size();
    }
    
    /** Mark all rows as unselected */
    public void clearSelectedRows() {
        Selected.clear();
    }
   
    /** Mark given row as selected or unselected */
    public void setSelectedRow(DBColumnValues key, boolean flag) {
        Selected.remove(key);
        if (flag)
            Selected.add(key.snapshot());
        getDBRow().refreshControllers();
    }
    
    /** Return true if given row is selected */
    public boolean isSelectedRow(DBColumnValues key) {
        return Selected.contains(key);
    }

    /** Return an array of selected keys. */
    public DBColumnValues[] getSelections() {
        DBColumnValues [] a = new DBColumnValues [1];
        return (DBColumnValues [])Selected.toArray(a);
    }
    
}
