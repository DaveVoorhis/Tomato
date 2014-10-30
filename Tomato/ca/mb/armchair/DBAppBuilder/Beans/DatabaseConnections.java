package ca.mb.armchair.DBAppBuilder.Beans;

/*
 * databaseConnections.java
 *
 * Created on December 20, 2001, 4:40 AM
 */

import java.io.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author  creatist
 */
public class DatabaseConnections implements Serializable {

    private Vector databases = new Vector();
    
    /** Creates a new instance of databaseConnections, a collection of databaseConnection */
    public DatabaseConnections() {
        load();
    }

    /** Get a new, preloaded ListModel of databases suitable for use with a JList */
    public DefaultListModel getListModel() {
        DefaultListModel model=new DefaultListModel();
        for (int i=0; i<databases.size(); i++)
            model.addElement(((DatabaseConnection)databases.get(i)).getDescription());
        return model;
    }
    
    /** Get a new, preloaded ComboboxModel of databases, suitable for use with a JCombobox */
    public DefaultComboBoxModel getComboBoxModel()
    {
        DefaultComboBoxModel model=new DefaultComboBoxModel();
        for (int i=0; i<databases.size(); i++)
            model.addElement(((DatabaseConnection)databases.get(i)).getDescription());
        return model;        
    }

    /** Get the database connection at a specified index.  Useful
     * in conjunction with a JList, JComboBox or similar mechanism.
     */
    public DatabaseConnection getDatabaseConnection(int index)
    {
        if (index<0 || index>=databases.size())
            return null;
        return (DatabaseConnection)databases.get(index);
    }
    
    /** Return JDBC Connection at specified index. */
    public Connection getConnection(int index)
    {
        return getDatabaseConnection(index).getConnection();
    }
    
    /** Return DatabaseConnection at given JList index */
    public DatabaseConnection getDatabaseConnectionFromSelection(JList list)
    {
        int index = list.getSelectedIndex();
        if (index!=-1)
            return getDatabaseConnection(index);
        return null;
    }
    
    /** Return Connection at given JList index */
    public Connection getConnectionFromSelection(JList list)
    {
        DatabaseConnection dbC = getDatabaseConnectionFromSelection(list);
        if (dbC!=null)
            return dbC.getConnection();
        return null;
    }

    /** Return DatabaseConnection at given JComboBox */
    public DatabaseConnection getDatabaseConnectionFromSelection(JComboBox box)
    {
        int index = box.getSelectedIndex();
        if (index!=-1)
            return getDatabaseConnection(index);
        return null;
    }
    
    /** Return Connection at given JComboBox */
    public Connection getConnectionFromSelection(JComboBox box)
    {
        DatabaseConnection dbC = getDatabaseConnectionFromSelection(box);
        if (dbC!=null)
            return dbC.getConnection();
        return null;
    }
    
    /** Return the number of DatabaseConnections in this container */
    public int getSize() {
        return databases.size();
    }
    
    /** Test the database connection at a specified index.  Useful
     * in conjunction with a JList, or similar mechanism.
     * Returns a status message.
     */
    public String testConnection(int index)
    {
        if (index<0 || index>=databases.size())
            return "Note: No database selected.";
        else
            return ((DatabaseConnection)databases.get(index)).test();
    }
    
    /** Replace a connection at a specified index.  There's no guarantee
     * the new connection will have the same index!  Saves when done.
     */
    public void replace(int index, DatabaseConnection dbC) 
    {
        if (index<0 || index>=databases.size())
            return;
        databases.remove(index);
        databases.add(dbC);
        save();
    }
    
    /** Remove a connection at a specified index.  Saves when done. */
    public void remove(int index)
    {
        if (index<0 || index>=databases.size())
            return;
        databases.remove(index);
        save();
    }
    
    /** Add a connection.  Saves when done. */
    public void add(DatabaseConnection dbC)
    {
        databases.add(dbC);
        save();
    }
    
    /** Save the list of databases.  Return true if succeeded. */
    public boolean save() {
        try {
            FileOutputStream out = new FileOutputStream(".dbApp.connections");
            ObjectOutputStream s = new ObjectOutputStream(out);
            s.writeObject(databases);
            s.flush();
            return true;
        } catch (Exception e) {
            System.err.println("databaseConnections::save failed.");
            return false;
        }
    }
    
    /** Restore the list of databases.  Return true if succeeded. */
    public boolean load() {
        try {
            FileInputStream in = new FileInputStream(".dbApp.connections");
            ObjectInputStream s = new ObjectInputStream(in);
            databases = (Vector)s.readObject();
            return true;
        } catch (Exception e) {
            databases = new Vector();
            // Automatically toss in the default database
            if (databases.size()==0)
                databases.add(new DatabaseConnection());
            return false;
        }
    }
}
