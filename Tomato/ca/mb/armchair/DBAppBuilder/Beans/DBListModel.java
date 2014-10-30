/*
 * DBListModel.java
 *
 * Created on January 4, 2002, 6:59 PM
 */

package ca.mb.armchair.DBAppBuilder.Beans;

import ca.mb.armchair.DBAppBuilder.Interfaces.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author  creatist
 */
public class DBListModel extends javax.swing.DefaultListModel implements DBViewableRowModel {

    private DBSelectQueryViewable query;
 
    /** Utility field used by bound properties. */
    private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);

    /** Creates a new instance of DBListModel */
    public DBListModel() {
        setDBSelectQueryViewable(new DBSelectQueryViewable());
    }
    
    /** Create a DBListModel given a DatabaseConnection, SQL string, and column spec. */
    public DBListModel(DatabaseConnection db, java.lang.String DisplayColumns, java.lang.String SQL) {
        setDBSelectQueryViewable(new DBSelectQueryViewable(db,DisplayColumns,SQL));
    }
    
    /** Create a DBListModel given a DBSelectQueryViewable query */
    public DBListModel(DBSelectQueryViewable q) {
        setDBSelectQueryViewable(q);
    }

    /** Given a resultset, populate the DBListModel */
    public void setResultSet(ResultSet rS) {
        removeAllElements();
        if (rS==null)
            return;
        try {
            ResultSetMetaData resultSetMetaData = rS.getMetaData();
            while (rS.next())
                if (resultSetMetaData.getColumnCount()==1)
                    addElement(new String(rS.getString(1)));
                else
                {
                    String r = "";
                    for (int column=1; column<=resultSetMetaData.getColumnCount(); column++)
                    {
                        if (r.length()>0)
                            r = r + " | ";
                        r = r + rS.getString(column);
                    }
                    addElement(r);
                }
            propertyChangeSupport.firePropertyChange("Status", "", getStatus());
        } catch (SQLException e) {
            propertyChangeSupport.firePropertyChange("Status", "", getStatus());
        }
    }
    
    /** Given a viewable query, populate the DBListModel */
    public void setDBSelectQueryViewable(DBSelectQueryViewable viewableQuery)
    {
        query = viewableQuery;
        setResultSet(query.getResultSet());
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
        if (this.getSize()==0)
            return "Error";
        else
            return "Ok";
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
        setResultSet(query.getResultSet());
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
        setResultSet(query.getResultSet());
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
}
