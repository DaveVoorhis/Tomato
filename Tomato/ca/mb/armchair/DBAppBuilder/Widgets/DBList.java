/*
 * DBList.java
 *
 * Created on January 4, 2002, 7:46 PM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

import ca.mb.armchair.DBAppBuilder.Beans.*;
import ca.mb.armchair.DBAppBuilder.Interfaces.*;

/**
 *
 * @author  creatist
 */
public class DBList extends javax.swing.JList implements DBViewableRowModel, DBBindableComponent {

    private java.util.Vector listeners = new java.util.Vector();

    /** Creates a new instance of DBList, a database-enabled JList. */
    public DBList() {
        setModel(new DBListModel());
        addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                for (int i=0; i<listeners.size(); i++)
                    ((java.awt.event.ActionListener)listeners.elementAt(i)).actionPerformed(new java.awt.event.ActionEvent(this,i,"Update"));
            }
        });
    }

    /** Creates new instance of DBList given a DatabaseConnection and SQL string */
    public DBList(DatabaseConnection db, java.lang.String DisplayColumns, java.lang.String SQL) {
        setModel(new DBListModel(db,DisplayColumns,SQL));
    }
    
    /** Creates new instance of DBList given a DBList model */
    public DBList(DBListModel m) {
        setModel(m);
    }
    
    /** Getter for property SQL.
     * @return Value of property SQL.
     */
    public String getSQL() {
        return ((DBListModel)getModel()).getSQL();
    }
    
    /** Setter for property SQL.
     * @param SQL New value of property SQL.
     */
    public void setSQL(String SQL) {
        setModel(new DBListModel(getDatabaseConnection(),
                                     getDisplayColumns(),
                                     SQL));
    }
    
    /** Getter for property displayColumns.
     * @return Value of property displayColumns.
     */
    public String getDisplayColumns() {
        return ((DBListModel)getModel()).getDisplayColumns();
    }
    
    /** Setter for property displayColumns.
     * @param displayColumns New value of property displayColumns.
     */
    public void setDisplayColumns(String displayColumns) {
        setModel(new DBListModel(getDatabaseConnection(),
                                     displayColumns,
                                     getSQL()));
    }
    
    public void requery() {
        setModel(new DBListModel(getDatabaseConnection(),
                                     getDisplayColumns(),
                                     getSQL()));
    }
    
    /** Getter for property databaseConnection.
     * @return Value of property databaseConnection.
     */
    public DatabaseConnection getDatabaseConnection() {
        return ((DBListModel)getModel()).getDatabaseConnection();
    }
    
    /** Setter for property databaseConnection.
     * @param databaseConnection New value of property databaseConnection.
     */
    public void setDatabaseConnection(DatabaseConnection databaseConnection) {
        setModel(new DBListModel(databaseConnection,
                                     getDisplayColumns(),
                                     getSQL()));
    }    
    
    /** Get the value of the component.  */
    public String getColumnValue() {
        try {
            return getSelectedValue().toString();
        } catch (java.lang.Exception e) {
            return "";
        }
    }
    
    /** Set the value of the component, updating it appropriately.  */
    public void setColumnValue(String v) {
        setSelectedValue(v, true);
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
