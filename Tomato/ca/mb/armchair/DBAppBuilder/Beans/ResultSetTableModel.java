package ca.mb.armchair.DBAppBuilder.Beans;

/*
 * MetaDataTableModel.java
 *
 * Created on December 24, 2001, 5:55 AM
 */

import java.sql.*;
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.*;


/**
 *
 * @author  creatist
 */
public class ResultSetTableModel extends AbstractTableModel {

    private Vector Rows = new Vector();
    private ResultSetMetaData resultSetMetaData = null;
    private String output = "";
    
    /** Given nothing, create an error instance of ResultSetTableModel */
    public ResultSetTableModel() {
    }
    
    /** Given a resultset, creates a new instance of ResultSetTableModel */
    public ResultSetTableModel(ResultSet rS) {
        if (rS==null)
            return;
        try {
            resultSetMetaData=rS.getMetaData();
            while (rS.next())
            {
                Vector celldata = new Vector();
                for (int column=1; column<=resultSetMetaData.getColumnCount(); column++)
                    celldata.add(rS.getObject(column));
                Rows.add(celldata);
            }
        } catch (SQLException e) {
            resultSetMetaData=null;
        }
    }
    
    /** Return textual equivalent to table contents.  Nice for logs and such. */
    public String getText() {
        return output;
    }
    
    public int getRowCount() {
        if (resultSetMetaData==null)
            return 1;
        return Rows.size();
    }
    
    public int getColumnCount() {
        if (resultSetMetaData==null)
            return 1;
        try {
            return resultSetMetaData.getColumnCount();
        } catch (SQLException e) {
            return 0;
        }
    }
    
    public String getColumnName(int column)
    {
        if (resultSetMetaData==null)
            return "Error";
        try {
            return resultSetMetaData.getColumnName(column+1);
        } catch (SQLException e) {
            return "???";
        }
    }
    
    public Object getValueAt(int row, int column) {
        if (resultSetMetaData==null)
            return new String("Error");
        try {
            return (((Vector)Rows.get(row)).get(column));
        } catch (Exception e) {
            return new String("???");
        }
    }
}
