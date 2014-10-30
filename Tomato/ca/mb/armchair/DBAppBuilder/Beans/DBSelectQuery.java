package ca.mb.armchair.DBAppBuilder.Beans;

/*
 * DBSelectQuery.java
 *
 * Created on December 30, 2001, 3:02 AM
 */

import java.sql.*;

/**
 * Implements a SELECT query.
 *
 * @author  creatist
 */
public class DBSelectQuery extends java.lang.Object implements java.io.Serializable {

    private DatabaseConnection databaseConnection = new DatabaseConnection();
    private String SQL = "";
    
    /** Creates a new instance of DBSelectQuery */
    public DBSelectQuery() {
    }

    /** Creates a new instance of DBSelectQuery */
    public DBSelectQuery(DatabaseConnection db, String sql)
    {
        databaseConnection = db;
        SQL = sql;
    }
    
    /** Getter for property databaseConnection.
     * @return Value of property databaseConnection.
     */
    public DatabaseConnection getDatabaseConnection() {
        return this.databaseConnection;
    }
    
    /** Setter for property databaseConnection.
     * @param databaseConnection New value of property databaseConnection.
     */
    public void setDatabaseConnection(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    
    /** Getter for property SQL.
     * @return Value of property SQL.
     */
    public String getSQL() {
        return this.SQL;
    }
    
    /** Setter for property SQL.
     * @param SQL New value of property SQL.
     */
    public void setSQL(String SQL) {
        this.SQL = SQL;
    }
    
    /** Execute the query and get the ResultSet */
    public ResultSet getResultSet()
    {
        try {
            return databaseConnection.executeQuery(SQL);
        } catch(java.lang.NullPointerException e) {
            return null;
        }
    }
    
    /** Get database connection status */
    public String getStatus()
    {
        return databaseConnection.getStatus();
    }
    
    /** Get short form of database connection status */
    public String getShortStatus()
    {
       return databaseConnection.getShortStatus();
    }
}

