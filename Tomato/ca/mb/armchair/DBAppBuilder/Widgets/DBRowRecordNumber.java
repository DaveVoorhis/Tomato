/*
 * DBRowRecordNumber.java
 *
 * Created on February 15, 2002, 4:41 AM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

import ca.mb.armchair.DBAppBuilder.Beans.*;
import ca.mb.armchair.DBAppBuilder.Helpers.*;

/**
 * An accessory for DBRow that allows the user to select a row from a DBRow by record number.
 *
 * @author  creatist
 */
public class DBRowRecordNumber extends javax.swing.JTextField {
    
    /** Creates a new instance of DBRowButton */
    public DBRowRecordNumber() {
        setText("0");
        setEnabled(false);
        addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                action(evt);
            }
        });
    }

    /** The DBRow we're controlling */
    DBRow dbRow = null;

    /** Set the DBRow to control */
    public void setDBRow(DBRow dbR) {
        dbRow = dbR;
        if (dbRow != null) {
            dbRow.addRowChangeListener(new RowChangeAdapter() {
                public void refreshController(RowChangeEvent e) {
                    refresh();
                }
            });
        }
    }
    
    /** Get the DBRow we're controlling */
    public DBRow getDBRow() {
        return dbRow;
    }

    /** Override to replace default widget enabling process. */
    public void refresh() {
        setEnabled(!getDBRow().isBroken() && !getDBRow().isInsertMode() && !getDBRow().isChanged() && !getDBRow().isEmpty() &&
                    getDBRow().getRowNumber()>0);
        if (getDBRow().getRowNumber()>0)
            setText(Integer.toString(getDBRow().getRowNumber()));
        else
            setText("???");
    }

    /** Override to determine what happens when the user changes the record number */
    public void action(java.awt.event.ActionEvent evt) {
        try {
            getDBRow().setRowNumber(Integer.parseInt(getText()));
        } catch (Exception e) {
        }        
    }
    
}
