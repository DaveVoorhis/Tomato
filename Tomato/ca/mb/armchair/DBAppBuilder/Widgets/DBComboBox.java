package ca.mb.armchair.DBAppBuilder.Widgets;

/*
 * DBComboBox.java
 *
 * Created on December 28, 2001, 3:56 AM
 */

import ca.mb.armchair.DBAppBuilder.Beans.*;
import ca.mb.armchair.DBAppBuilder.Interfaces.*;

/**
 *
 * @author  creatist
 */
public class DBComboBox extends javax.swing.JComboBox implements DBViewableRowModel, DBBindableComponent {

    private java.util.Vector listeners = new java.util.Vector();

    /** Creates a new instance of DBComboBox, a database-enabled JComboBox. */
    public DBComboBox() {
        setModel(new DBComboBoxModel());
        addListener();
    }

    /** Creates new instance of DBCombobox given a DatabaseConnection and SQL string */
    public DBComboBox(DatabaseConnection db, java.lang.String DisplayColumns, java.lang.String SQL) {
        setModel(new DBComboBoxModel(db,DisplayColumns,SQL));
        addListener();
    }
    
    /** Creates new instance of DBComboBox given a DBComboBox model */
    public DBComboBox(DBComboBoxModel m) {
        setModel(m);
        addListener();
    }

    /** Set up state change listener */
    private void addListener() {
        addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                for (int i=0; i<listeners.size(); i++)
                    ((java.awt.event.ActionListener)listeners.elementAt(i)).actionPerformed(new java.awt.event.ActionEvent(this,i,"Update"));
            }
        });
    }
    
    /** Getter for property SQL.
     * @return Value of property SQL.
     */
    public String getSQL() {
        return ((DBComboBoxModel)getModel()).getSQL();
    }
    
    /** Setter for property SQL.
     * @param SQL New value of property SQL.
     */
    public void setSQL(String SQL) {
        setModel(new DBComboBoxModel(getDatabaseConnection(),
                                     getDisplayColumns(),
                                     SQL));
    }
    
    /** Getter for property displayColumns.
     * @return Value of property displayColumns.
     */
    public String getDisplayColumns() {
        return ((DBComboBoxModel)getModel()).getDisplayColumns();
    }
    
    /** Setter for property displayColumns.
     * @param displayColumns New value of property displayColumns.
     */
    public void setDisplayColumns(String displayColumns) {
        setModel(new DBComboBoxModel(getDatabaseConnection(),
                                     displayColumns,
                                     getSQL()));
    }
    
    public void requery() {
        int i = getSelectedIndex();
        setModel(new DBComboBoxModel(getDatabaseConnection(),
                                     getDisplayColumns(),
                                     getSQL()));
        try {
            setSelectedIndex(i);
        } catch (IllegalArgumentException e) {
            try {
                setSelectedIndex(0);
            } catch (IllegalArgumentException f) {
            }
        }
    }
    
    /** Getter for property databaseConnection.
     * @return Value of property databaseConnection.
     */
    public DatabaseConnection getDatabaseConnection() {
        return ((DBComboBoxModel)getModel()).getDatabaseConnection();
    }
    
    /** Setter for property databaseConnection.
     * @param databaseConnection New value of property databaseConnection.
     */
    public void setDatabaseConnection(DatabaseConnection databaseConnection) {
        setModel(new DBComboBoxModel(databaseConnection,
                                     getDisplayColumns(),
                                     getSQL()));
    }
    
    /** Get the value of the component.  */
    public String getColumnValue() {
        try {
            return getSelectedItem().toString();
        } catch (java.lang.Exception e) {
            return "";
        }
    }
    
    /** Set the value of the component, updating it appropriately.  */
    public void setColumnValue(String v) {
        setSelectedItem(v);
    }
        
    /** Add a listener to listen for a change in value to this component  */
    public void addActionListener(java.awt.event.ActionListener l) {
        listeners.add(l);
    }
    
    /** Remove a given listener  */
    public void removeActionListener(java.awt.event.ActionListener l) {
        listeners.remove(l);
    }
    
}
