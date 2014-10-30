package ca.mb.armchair.DBAppBuilder.Widgets;

/*
 * DBTable.java
 *
 * Created on December 28, 2001, 3:56 AM
 */

import ca.mb.armchair.DBAppBuilder.Interfaces.*;
import ca.mb.armchair.DBAppBuilder.Beans.*;

/**
 * A database-aware JTable
 *
 * @author  creatist
 */
public class DBTable extends javax.swing.JTable implements DBViewableRowModel {

    /** Creates a new instance of DBTable, a database-enabled JTable. */
    public DBTable() {
        setModel(new DBTableModel());
    }

    /** Creates new instance of DBTable given a DatabaseConnection and SQL string */
    public DBTable(DatabaseConnection db, java.lang.String DisplayColumns, java.lang.String SQL) {
        setModel(new DBTableModel(db,DisplayColumns,SQL));
    }
    
    /** Creates new instance of DBTable given a DBTable model */
    public DBTable(DBTableModel m) {
        setModel(m);
    }
    
    /** Getter for property SQL.
     * @return Value of property SQL.
     */
    public String getSQL() {
        return ((DBTableModel)getModel()).getSQL();
    }
    
    /** Setter for property SQL.
     * @param SQL New value of property SQL.
     */
    public void setSQL(String SQL) {
        setModel(new DBTableModel(getDatabaseConnection(),
                                  getDisplayColumns(),
                                  SQL));
    }
    
    /** Getter for property displayColumns.
     * @return Value of property displayColumns.
     */
    public String getDisplayColumns() {
        return ((DBTableModel)getModel()).getDisplayColumns();
    }
    
    /** Setter for property displayColumns.
     * @param displayColumns New value of property displayColumns.
     */
    public void setDisplayColumns(String displayColumns) {
        setModel(new DBTableModel(getDatabaseConnection(),
                                  displayColumns,
                                  getSQL()));
    }
    
    public void requery() {
        setModel(new DBTableModel(getDatabaseConnection(),
                                  getDisplayColumns(),
                                  getSQL()));
    }
    
    /** Getter for property databaseConnection.
     * @return Value of property databaseConnection.
     */
    public DatabaseConnection getDatabaseConnection() {
        return ((DBTableModel)getModel()).getDatabaseConnection();
    }
    
    /** Setter for property databaseConnection.
     * @param databaseConnection New value of property databaseConnection.
     */
    public void setDatabaseConnection(DatabaseConnection databaseConnection) {
        setModel(new DBTableModel(databaseConnection,
                                  getDisplayColumns(),
                                  getSQL()));
    }
}
