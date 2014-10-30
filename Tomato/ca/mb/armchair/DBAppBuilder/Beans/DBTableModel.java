package ca.mb.armchair.DBAppBuilder.Beans;

/*
 * DBTableModel.java
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
public class DBTableModel extends AbstractTableModel implements ca.mb.armchair.DBAppBuilder.Interfaces.DBViewableRowModel {

    private Vector Rows = new Vector();
    private ResultSetMetaData resultSetMetaData = null;
    private String output = "";
    private DBSelectQueryViewable query;
 
    /** Utility field used by bound properties. */
    private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);
 
    /** Given nothing, create an empty instance of DBTableModel */
    public DBTableModel() {
        setDBSelectQueryViewable(new DBSelectQueryViewable());
    }
    
    /** Create a DBTableModel given a DatabaseConnection, SQL string, and column spec. */
    public DBTableModel(DatabaseConnection db, java.lang.String DisplayColumns, java.lang.String SQL) {
        setDBSelectQueryViewable(new DBSelectQueryViewable(db,DisplayColumns,SQL));
    }
  
    /** Create a DBTableModel given a DBSelectQueryViewable */
    public DBTableModel(DBSelectQueryViewable qv) {
        setDBSelectQueryViewable(qv);
    }
    
    /** Given a resultset, populate the DBTableModel */
    public void setResultSet(ResultSet rS) {
        Rows = new Vector();
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
            propertyChangeSupport.firePropertyChange("Status", "", getStatus());
            fireTableChanged(new javax.swing.event.TableModelEvent(this));
        } catch (SQLException e) {
            resultSetMetaData=null;
            propertyChangeSupport.firePropertyChange("Status", "", getStatus());
        }
    }
    
    /** Given a viewable query, populate the DBTableModel */
    public void setDBSelectQueryViewable(DBSelectQueryViewable viewableQuery)
    {
        query = viewableQuery;
        requery();
    }


    /** Requery */
    public void requery() {
        setResultSet(query.getResultSet());
    }

    /** Return textual equivalent to table contents.  Nice for logs and such. */
    public String getText() {
        return output;
    }
    
    public int getRowCount() {
        if (resultSetMetaData==null)
            return 0;
        return Rows.size();
    }
    
    public int getColumnCount() {
        if (resultSetMetaData==null)
            return 0;
        try {
            return resultSetMetaData.getColumnCount();
        } catch (SQLException e) {
            return 0;
        }
    }
    
    public String getColumnName(int column)
    {
        if (resultSetMetaData==null)
            return "";
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
    
    /** Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }
    
    /** Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
      
    /** Getter for property Status.
     * @return Value of property Status.
     */
    public String getStatus() {
        if (resultSetMetaData==null)
            return "Error: " + query.getStatus();
        else
            return "Ok: " + query.getStatus();
    }
    
    /** Getter for property SQL.
     * @return Value of property SQL.
     */
    public String getSQL() {
        return query.getSQL();
    }
    
    /** Setter for property SQL.
     * @param SQL New value of property SQL.
     */
    public void setSQL(String SQL) {
        String oldSQL = getSQL();
        query.setSQL(SQL);
        requery();
        propertyChangeSupport.firePropertyChange("SQL", oldSQL, SQL);
    }
    
    /** Getter for property databaseConnection.
     * @return Value of property databaseConnection.
     */
    public DatabaseConnection getDatabaseConnection() {
        return query.getDatabaseConnection();
    }
    
    /** Setter for property databaseConnection.
     * @param databaseConnection New value of property databaseConnection.
     */
    public void setDatabaseConnection(DatabaseConnection databaseConnection) {
        DatabaseConnection oldDatabaseConnection = query.getDatabaseConnection();
        query.setDatabaseConnection(databaseConnection);
        requery();
        propertyChangeSupport.firePropertyChange("databaseConnection", oldDatabaseConnection, databaseConnection);
    }
    
    /** Getter for property displayColumns.
     * @return Value of property displayColumns.
     */
    public String getDisplayColumns() {
        return query.getDisplayColumns();
    }
    
    /** Setter for property displayColumns.
     * @param displayColumns New value of property displayColumns.
     */
    public void setDisplayColumns(String displayColumns) {
        String oldDisplayColumns = getDisplayColumns();
        query.setDisplayColumns(displayColumns);
// reformat display here
        propertyChangeSupport.firePropertyChange("displayColumns", oldDisplayColumns, displayColumns);
    }
    
    /** Indexed getter for property selectedColumn.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public int getSelectedColumn(int index) {
        return 0;
    }
    
}
