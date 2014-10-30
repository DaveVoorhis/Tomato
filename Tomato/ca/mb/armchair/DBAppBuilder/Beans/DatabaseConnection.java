package ca.mb.armchair.DBAppBuilder.Beans;

/*
 * databaseConnection.java
 *
 * Created on December 15, 2001, 10:13 PM
 */

import java.io.*;
import java.sql.*;

/**
 *
 * @author  creatist
 * @version 1.0
 */
public class DatabaseConnection extends Object implements java.io.Serializable {

    private String Description;
    private String Driver;
    private String Login;
    private String Password;
    private String URL;
    private Boolean Logging;

    private transient String DefaultFileName = "Default.DBC";
    private transient Connection connection = null;
    private transient String Status = "";
    private transient String ShortStatus = "";
        
    /** Utility field used by bound properties. */
    private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);
    
    /** Creates default database connection. */
    public DatabaseConnection()
    {
        if (!loadDefault())
        {
            Description="Default";          // Some useful (?) default defaults
            Driver="org.postgresql.Driver";
            Login="postgres";
            Password="";
            URL="jdbc:postgresql://localhost.localdomain/template1";
            Logging=new Boolean(false);
            getConnection();
        }
        
    }
    
    /** Creates new databaseConnection given explicit parameters. */
    public DatabaseConnection(String _Description, String _Driver, String _Login, String _Password, String _URL) 
    {
        Description=_Description;
        Driver=_Driver;
        Login=_Login;
        Password=_Password;
        URL=_URL;
        Logging=new Boolean(false);
        getConnection();
    }
    
    /** Creates new databaseConnection given existing connection. */
    public DatabaseConnection(DatabaseConnection dbC) 
    {
        Description=dbC.Description;
        Driver=dbC.Driver;
        Login=dbC.Login;
        Password=dbC.Password;
        URL=dbC.URL;
        Logging=dbC.Logging;
        getConnection();
    }

    /** Load settings from a specified file.  Return true if successfully loaded.
     * A successful load does not guarantee that the connection can be made!
     */
    public boolean load(String fname)
    {
        boolean result = false;
        try {
            FileInputStream defs = new FileInputStream(fname);
            ObjectInputStream s = new ObjectInputStream(defs);
            Description=(String)s.readObject();
            Driver=(String)s.readObject();
            Login=(String)s.readObject();
            Password=(String)s.readObject();
            URL=(String)s.readObject();
            Logging=(Boolean)s.readObject();
            defs.close();
            result = true;
        } catch (Exception e) {
            System.err.println("Couldn't load file: " + fname);
        }
        getConnection();
        return result;
    }
    
    /** Save settings to a specified file.  Return true if successful. */
    public boolean save(String fname)
    {
        boolean result = false;
        try {
            FileOutputStream defs = new FileOutputStream(fname);
            ObjectOutputStream s = new ObjectOutputStream(defs);
            s.writeObject(Description);
            s.writeObject(Driver);
            s.writeObject(Login);
            s.writeObject(Password);
            s.writeObject(URL);
            s.writeObject(Logging);
            defs.close();
            result = true;
        } catch (Exception e) {
            System.err.println("Couldn't save file: " + fname);
        }
        return result;
    }
    
    /** Get the default filename */
    public String getDefaultFileName()
    {
        return DefaultFileName;
    }
    
    /** Set the default filename */
    public void setDefaultFileName(String s)
    {
        DefaultFileName = s;
    }
    
    /** Load settings from default file.  Return true if successful. */
    public boolean loadDefault()
    {
        return load(getDefaultFileName());
    }
    
    /* Save settings to default file.  Return true if successful. */
    public boolean saveDefault()
    {
        return save(getDefaultFileName());
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
    
    /** Set statuses (statii?) */
    private void setStatus(String shortOne, String longOne)
    {
        String oldShortStatus = ShortStatus;
        ShortStatus = shortOne;
        String oldStatus = Status;
        Status = longOne;
        propertyChangeSupport.firePropertyChange("status", oldStatus, Status);
        propertyChangeSupport.firePropertyChange("shortStatus", oldShortStatus, ShortStatus);
    }
    
    /** Clear statii */
    private void clearStatus()
    {
        ShortStatus="";
        Status="";
    }

    /** True to enable logging */
    public void setLogging(boolean flag) {
        Logging = new Boolean(flag);
    }
    
    /** Return true if logging is enabled */
    public boolean isLogging() {
        return Logging.booleanValue();
    }
    
    /** Send a message to the SQL log, if logging is enabled. */
    private void logSQL(String s) {
        if (isLogging())
            System.err.println(s);
    }
    
    /** Get connection description */
    public String getDescription() {return Description;}
    
    /** Set connection description */
    public void setDescription(String s) {Description=s;}

    /** Get connection driver */
    public String getDriver() {return Driver;}
    
    /** Set connection driver, and attempt to establish connection */
    public void setDriver(String s) 
    {
        Driver=s;
        getConnection();
    }

    /** Get connection user ID */
    public String getLogin() {return Login;}

    /** Set connection user ID, and attempt to establish connection */
    public void setLogin(String s)
    {
        Login=s;
        getConnection();
    }
    
    /** Get connection password */
    public String getPassword() {return Password;}
    
    /** Set connection password, and attempt to establish connection */
    public void setPassword(String s)
    {
        Login=s;
        getConnection();
    }

    /** Get connection URL */
    public String getURL() {return URL;}
    
    /** Set connection URL, and attempt to establish connection */
    public void setURL(String s)
    {
        URL=s;
        getConnection();
    }

    /** Return status string. */
    public String getStatus() {
        return Status;
    }

    /** Return short status string */
    public String getShortStatus() {return ShortStatus;}
    
    /** Execute an update or DDL query.  Return false if failed. */
    public boolean executeUpdate(String sql)
    {
        if (getConnection()==null)
            return false;
        clearStatus();
        if (sql.length()==0)
        {
            setStatus("Fail","1 No query.");
            return false;
        }
        logSQL(sql);
        try
        {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            String msg = "0 Ok: Execution successful.";
            setStatus("Ok",msg);
            logSQL(msg);
            return true;
        }
        catch (SQLException ex)
        {
            while (ex!=null)
            {
                String msg = getStatus() +
                    "1 Problem: Unable to execute query:\n\n" + sql + "\n\n" +
                    "Due to vendor error #" + ex.getErrorCode() + "\n\n" +
                    ex.getMessage() + "\n\n";
                setStatus("Fail",msg);
                logSQL(msg);
                ex=ex.getNextException();
            }
        }
        return false;
    }
    
    /** Execute a query.  Return resultset. */
    public ResultSet executeQuery(String sql)
    {
        if (getConnection()==null)
            return null;
        clearStatus();
        if (sql.length()==0)
        {
            setStatus("Fail","1 No query.");
            return null;
        }
        logSQL(sql);
        try
        {
            Statement stmt = connection.createStatement(
                                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                                        ResultSet.CONCUR_UPDATABLE);
            ResultSet result = stmt.executeQuery(sql);
            String msg;
            if (result==null)
            {
                msg = "1 No result set.";
                setStatus("Fail",msg);
            }
            else
            {
                msg = "0 Ok: Execution successful.";
                setStatus("Ok",msg);
            }
            logSQL(msg);
            return result;
        }
        catch (SQLException ex)
        {
            while (ex!=null)
            {
                String statusMsg = getStatus() +
                    "1 Problem: Unable to execute query:\n\n" + sql + "\n\n" +
                    "Due to vendor error #" + ex.getErrorCode() + "\n\n" +
                    ex.getMessage() + "\n\n";
                setStatus("Fail",statusMsg);
                logSQL(statusMsg);
                ex=ex.getNextException();
            }
        }
        return null;
    }
    
    /** Return the connection, or null if failed */
    public Connection getConnection() {
        try {
            clearStatus();
            Class.forName(Driver);
            try {
                if (connection!=null)
                    connection.close();
                connection = DriverManager.getConnection(URL,Login,Password);
                setStatus("Ok","0 Ok: Connection '" + Description + "' established to " + URL);            
            } catch (SQLException ex) {
                while (ex!=null)
                {
                    setStatus("Fail",getStatus() + 
                        "1 Problem: Unable to establish connection to " + URL +
                        " due to vendor error #" + ex.getErrorCode() + "\n\n" +
                        ex.getMessage() + "\n\n");
                    ex=ex.getNextException();
                }
                connection = null;
            }
        } catch (ClassNotFoundException ex) {
            setStatus("Fail", "2 Problem: Driver " + Driver + " not found.");
            connection = null;
        } catch (java.lang.NullPointerException e) {
            setStatus("Fail", "3 Problem: Null pointer exception while loading driver.");
            connection = null;
        } catch (Exception edef) {
            setStatus("Fail", "4 Problem: General exception while attempting connection: " + edef.getMessage());
            connection = null;
        }

        return connection;
    }
    
    /** Test the connection.  Return a status message. */
    public String test()
    {
        getConnection();
        return getStatus();
    }
    
    /** Return metadata about connection */
    public DatabaseMetaData getMetaData()
    {
        getConnection();
        if (connection!=null)
            try {
                return connection.getMetaData();
            } catch (SQLException e) {
                return null;
            }
        else
            return null;
    }
}
