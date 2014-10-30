package ca.mb.armchair.DBAppBuilder.Interfaces;

/*
 * DBViewableRowModel.java
 *
 * Created on December 30, 2001, 2:13 AM
 */

import ca.mb.armchair.DBAppBuilder.Beans.*;

/**
 *
 * @author  creatist
 *
 * Provides the basic kit of tools needed to implement a database-aware component 
 *
 */
public interface DBViewableRowModel {
    
    /** Getter for property SQL.
     * @return Value of property SQL.
     */
    public String getSQL();
    
    /** Setter for property SQL.
     * @param SQL New value of property SQL.
     */
    public void setSQL(String SQL);
    
    /** Getter for property databaseConnection.
     * @return Value of property databaseConnection.
     */
    public DatabaseConnection getDatabaseConnection();
    
    /** Setter for property databaseConnection.
     * @param databaseConnection New value of property databaseConnection.
     */
    public void setDatabaseConnection(DatabaseConnection databaseConnection);
    
    /** Getter for property displayColumns.
     * @return Value of property displayColumns.
     */
    public String getDisplayColumns();
    
    /** Setter for property displayColumns.
     * @param displayColumns New value of property displayColumns.
     */
    public void setDisplayColumns(String displayColumns);
}

