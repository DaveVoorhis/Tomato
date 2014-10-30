/*
 * DBRow.java
 *
 * Created on January 18, 2002, 7:18 AM
 */

package ca.mb.armchair.DBAppBuilder.Beans;

import ca.mb.armchair.DBAppBuilder.Interfaces.*;
import ca.mb.armchair.DBAppBuilder.Helpers.*;
import java.util.*;
import java.sql.*;

/**
 * A DBRow extends a DBSelectQuery by handling updates to the database.  
 * Mechanisms are provided for iterating the result set and automatically
 * updating the DBColumnValues.  DBRow is the basis for multi-row editors.
 *
 * @author  creatist
 */
public class DBRow extends DBSelectQuery {

    public final static int COLUMNATTRIB_KEY = 1;
    public final static int COLUMNATTRIB_AUTOINSERT = 2;
    public final static int COLUMNATTRIB_AUTOKEY = COLUMNATTRIB_KEY | COLUMNATTRIB_AUTOINSERT;
    public final static int COLUMNATTRIB_READONLY = 4;
    public final static int COLUMNATTRIB_LOCATOR = COLUMNATTRIB_AUTOINSERT | COLUMNATTRIB_READONLY;
    
    private DBColumnValues Key = new DBColumnValues();
    private DBColumnValues Row = new DBColumnValues();
    private String UpdateableViewName = "";
    private String SQLFilter = "";
    private String SQLOrder = "";
    private ResultSet rSet = null;
    private boolean InsertMode = false;
    private boolean Empty = true;
    private boolean Broken = true;
    
    /** Helper to implement row change events. */
    private RowChangeSupport rowChangeSupport =  new RowChangeSupport(this);
    
    /** Creates a new instance of DBRow */
    public DBRow() {
    }

    /** Adds a RowChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addRowChangeListener(RowChangeListener l) {
        rowChangeSupport.addRowChangeListener(l);
    }
    
    /** Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removeRowChangeListener(RowChangeListener l) {
        rowChangeSupport.removeRowChangeListener(l);
    }
        
    /** Set a candidate key that will uniquely identify rows in the result set.  This must
      * be specified in order for database updates or deletes to occur.
      */
    public void setKey(DBColumnValues k) {
        Key = k;
    }
    
    /** Get the key identifier */
    public DBColumnValues getKey() {
        return Key;
    }

    /** Get the row DBColumnValues */
    public DBColumnValues getRow() {
        return Row;
    }
    
    /** Getter for SQL updateable view or table name */
    public String getUpdateableViewName() {
        return UpdateableViewName;
    }
    
    /** Setter for SQL updateable view or table name */
    public void setUpdateableViewName(String s) {
        UpdateableViewName = s;
    }

    /** Generate SQL property from DBColumns */
    public void generateSQL()
    {
        if (getUpdateableViewName().length()==0)
        {
            System.err.println("Note: DBRow:generateSQL() cannot automatically generate SQL because UpdateableViewName is not yet specified.");
            return;
        }
        String Columns = "";
        for (int i=0; i<Row.getSize(); i++)
        {
            if (Columns.length()>0)
                Columns += ", ";
            Columns += Row.getColumnName(i);
        }
        for (int i=0; i<Key.getSize(); i++)
        {
            if (Columns.length()>0)
                Columns += ", ";
            Columns += Key.getColumnName(i);
        }
        String SQL = "SELECT " + Columns + " FROM " + getUpdateableViewName();
        if (getSQLFilter().length()>0)
            SQL += " WHERE " + getSQLFilter();
        if (getSQLOrder().length()>0)
            SQL += " ORDER BY " + getSQLOrder();
            
        setSQL(SQL);
        cancelUpdate();
        first();
    }
    
    /** Used in conjunction with generateSQL() to specify desired where clause, etc. */
    public void setSQLFilter(String s) {
        SQLFilter = s;
        generateSQL();
    }
        
    /** Used in conjunction with generateSQL() to specify desired where clause, etc. */
    public String getSQLFilter() {
        return SQLFilter;
    }
   
    /** Used in conjunction with generateSQL() to specify desired order */
    public void setSQLOrder(String s) {
        SQLOrder = s;
        generateSQL();
    }

    /** Used in conjunction with generateSQL() to specify desired order */
    public String getSQLOrder() {
        return SQLOrder;
    }

    /** Bind an existing DBColumnValue to this query, and listen for it to change.  Send refreshController RowChangeEvent if it does. */
    public void addColumn(DBColumnValue v) {
        Row.add(v);
        v.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rowChangeSupport.fireRefreshController();
            }
        });
        cancelUpdate();
        first();
    }

    /** Bind an existing DBColumnValue to this query, with special attributes */
    public void addColumn(DBColumnValue v, int attributes) {
            addColumn(v);
            if ((attributes & COLUMNATTRIB_KEY)!=0)
                Key.add(v);
            if ((attributes & COLUMNATTRIB_AUTOINSERT)!=0)
                v.setAutomaticInsert(true);
            if ((attributes & COLUMNATTRIB_READONLY)!=0)
                v.setReadOnly(true);
    }
    
    /** Unbound column */
    public void addColumn(String columnName) {
        addColumn(new DBColumnValue(columnName));
    }
    
    /** Unbound column, with special attributes */
    public void addColumn(String columnName, int attributes) {
        addColumn(new DBColumnValue(columnName), attributes);
    }

    /** Equivalent to addColumn(..., COLUMNATTRIB_KEY) */
    public void addKey(String columnName) {
        addColumn(columnName, COLUMNATTRIB_KEY);
    }
        
    /** Equivalent to addColumn(..., COLUMNATTRIB_AUTOKEY) */
    public void addAutoKey(String columnName) {
        addColumn(columnName, COLUMNATTRIB_AUTOKEY);
    }

    /** Equivalent to addAutoKey("oid") */
    public void addOID() {
        addAutoKey("oid");
    }
    
    /** Bind a widget to a column */
    public void addColumn(String columnName, DBBindableComponent widget) {
        addColumn(new DBColumnValue(columnName, widget));
    }

    /** Bind a widget to a column, with special attributes */
    public void addColumn(String columnName, DBBindableComponent widget, int attributes) {
        addColumn(new DBColumnValue(columnName, widget), attributes);
    }

    /** Equivalent to addColumn(..., COLUMNATTRIB_KEY) */
    public void addKey(String columnName, DBBindableComponent widget) {
        addColumn(columnName, widget, COLUMNATTRIB_KEY);
    }
    
    /** Equivalent to addColumn(..., COLUMNATTRIB_AUTOKEY) */
    public void addAutoKey(String columnName, DBBindableComponent widget) {
        addColumn(columnName, widget, COLUMNATTRIB_AUTOKEY);
    }
    
    /** Return true if any DBColumnValues have changed. */
    public boolean isChanged() {
        return Row.isChanged();
    }

    /** Cancel any pending changes, and update all bindings from current row in result set */
    public boolean current() {
        if (rSet==null)
            return false;
        for (int i=0; i<Row.getSize(); i++)
            try {
                Row.setColumnValueEscaped(i,rSet.getString(Row.getColumnName(i)).trim());
            } catch (SQLException e) {
                Row.clear(i);
            } catch (Exception ex) {
                Row.clear(i);
            }
        cancelUpdate();
        rowChangeSupport.fireRefreshController();
        return true;
    }
    
    /** Return true if the current resultset row matches a given set of DBColumnValues */
    public boolean rowMatchesKey(DBColumnValues v)
    {
        if (rSet==null)
            return false;
        for (int i=0; i<v.getSize(); i++)
            try {
                if (v.getColumnValueEscaped(i).compareTo(rSet.getString(v.getColumnName(i)))!=0)
                    return false;
            } catch (SQLException e) {
                return false;
            } catch (Exception ex) {
                return false;
            }
        return true;
    }
    
    /** Return true if the current resultset row matches the Key */
    public boolean rowMatchesKey() {
        return rowMatchesKey(Key);
    }
    
    /** Explicitly fire a RefreshController event.  Typically used by DBRow accessory classes to invoke control
     *  updates in other accessory classes.  For example, a DBRowSelector will invoke this method whenever
     *  a selection is changed.  This causes the DBRowNavigator to update the 'Delete Selected' button's display
     *  of the number of selections.
     */
    public void refreshControllers() {
        rowChangeSupport.fireRefreshController();
    }
    
    /** Flag used to prevent event loops when gotoKey() calls current(), which calls gotoKey(), which calls current()... */
    private boolean insideGotoKey = false;

    /** Attempt to position the current row at a given set of DBColumnValues.  True if sucessful. */
    public boolean gotoKey(DBColumnValues v) {
        if (isChanged())
            update();
        if (v.getSize()==0 || rSet==null || insideGotoKey)
            return false;
        boolean returnvalue = false;
        insideGotoKey = true;
        if (rowMatchesKey(v))            // are we there already?
            returnvalue = true;
        else
        {
            // For now, do a linear search if we aren't already on the key row.  Later, use the current
            // position as a starting point and expand the search outward from there.
            try {
                if (rSet.first())
                {
                    do
                        if (rowMatchesKey(v))        // are we there yet?
                        {
                            returnvalue = true;
                            break;
                        }
                    while (rSet.next());
                    if (!rowMatchesKey(v))           // who knows...
                    {
                        returnvalue = false;
                        rSet.first();
                    }
                }
            } catch (SQLException ex) {
                System.err.println("DBSelectQueryEditable:gotoKey() failed: " + ex.getMessage());
            }
        }
        current();
        insideGotoKey = false;
        return returnvalue;
    }
    
    /** Convenience function for using the above with a key consisting of a single DBColumnValue */
    public boolean gotoKey(DBColumnValue v) {
        DBColumnValues vS = new DBColumnValues();
        vS.add(v);
        return gotoKey(vS);
    }
    
    /** Attempt to position the current row at the Key */
    public boolean gotoKey() {
        return gotoKey(Key);
    }
    
    /** Obtain result set */
    private void obtainResultSet() {
        Empty = true;
        Broken = true;
        rowChangeSupport.fireBeforeNewResultSet();
        rSet = getResultSet();
        if (rSet!=null)
            try {
                if (rSet.first())
                    Empty = false;
                Broken = false;
            } catch (SQLException e) {
            }
    }
    
    /** Return true if the query is broken, and can't obtain results */
    public boolean isBroken() {
        return Broken;
    }
    
    /** Return true if the RecordSet is empty */
    public boolean isEmpty() {
        return Empty;
    }
    
    /** Enable Insert mode.  I.e., next update() will perform an Insert. */
    public void setInsertMode(boolean flag) {
        InsertMode = flag;
    }
    
    /** Return true if an Insert is pending.  A call to current() or cancelUpdate() will cancel a pending Insert. */
    public boolean isInsertMode() {
        return InsertMode;
    }

    /** Cancel a pending update or insert.  Does not refresh bindings; use current() to visibly cancel an update. */
    public void cancelUpdate() {
        setInsertMode(false);
        Row.clearChanged();
        Key.clearChanged();
    }
    
    /** Clear bindings to default values, and prepare for Insert on next update() invocation */
    public void insert() {
        if (!rowChangeSupport.fireConfirmInsertMode())
            return;
        for (int i=0; i<Row.getSize(); i++)
            Row.clear(i);
        rowChangeSupport.fireBeforeInsertMode();
        setInsertMode(true);
        rowChangeSupport.fireAfterInsertMode();
        rowChangeSupport.fireRefreshController();
    }
    
    /** Insert a new record into the database from the bindings */
    private boolean insertImmediate() {
        if (!rowChangeSupport.fireConfirmInsert())
            return false;
        rowChangeSupport.fireBeforeInsert();

        String Columns = "";
        String Values = "";
        for (int i=0; i<Row.getSize(); i++)
            if (!Row.isAutomaticInsert(i)) {
                if (Columns.length()>0)
                        Columns += ", ";
                Columns += Row.getColumnName(i);
                if (Values.length()>0)
                        Values += ", ";
                Values += "'" + Row.getColumnValueEscaped(i) + "'";
            }
        for (int i=0; i<Key.getSize(); i++)
            if (!Key.isAutomaticInsert(i)) {
                if (Columns.length()>0)
                        Columns += ", ";
                Columns += Key.getColumnName(i);
                if (Values.length()>0)
                        Values += ", ";
                Values += "'" + Key.getColumnValueEscaped(i) + "'";
            }
        String SQL = "INSERT INTO " + getUpdateableViewName();
        if (Columns.length()>0)
            SQL += " (" + Columns + ") VALUES (" + Values + ")";
        else
            SQL += " DEFAULT VALUES";
        if (!getDatabaseConnection().executeUpdate(SQL))
            return false;
        
        // Insert was successful.  Now we must return to the inserted record.
        try {
            rSet.refreshRow();
            cancelUpdate();
        } catch (SQLException e) {
            // Looks like rSet.refreshRow() isn't implemented.  Must requery and reposition.
            int CurrentPosition;
            try {
                CurrentPosition = rSet.getRow();    // Current position
            } catch (SQLException q) {
                CurrentPosition = 1;
            }
            obtainResultSet();                     // Get new result set
            try {
                rSet.absolute(CurrentPosition);         // Return to previous position
            } catch (SQLException q) {
                try {
                    rSet.absolute(1);
                } catch (SQLException r) {
                }
            } catch (java.lang.NullPointerException x) {
                System.err.println("rSet is null.");
            }
            cancelUpdate();
            gotoKey();                                    // Find key, i.e., return to edited record
        } catch (java.lang.NullPointerException en) {
            System.err.println("rSet is null.");
        }

        cancelUpdate();
        rowChangeSupport.fireAfterInsert();
        rowChangeSupport.fireAfterNewResultSet();
        rowChangeSupport.fireRefreshController();

        return true;
    }
    
    /** Update the database from the bindings */
    public boolean updateImmediate() {
        if (Row.getSize()==0 || rSet==null)
            return false;
        if (!rowChangeSupport.fireConfirmUpdate())
            return false;
        rowChangeSupport.fireBeforeUpdate();

        // Attempt update via ResultSet; if that fails, use explicit query.
        String Assignments = "";
        boolean somethingUpdated = false;
        for (int i=0; i<Row.getSize(); i++)
            if (Row.isChanged(i))
            {
                somethingUpdated = true;
                try {
                    rSet.updateString(i+1,Row.getColumnValueEscaped(i));
                } catch (SQLException e) {
                    // Looks like rSet.updatexxx() isn't implemented.  Must use explicit query.
                    if (Assignments.length()>0)
                        Assignments += ", ";
                    Assignments += Row.getSQLAssignment(i);
                }
            }
        // Explicit query, if needed.
        if (Assignments.length()>0)
        {
            if (Key.getSize()==0)
            {
                System.err.println("DBSelectQueryEditable:update() cannot update database without Key property.");
                return false;
            }
            String SQL = "UPDATE " + getUpdateableViewName() + " SET " + Assignments + " WHERE " + Key.getSQLWhereClause();
            if (!getDatabaseConnection().executeUpdate(SQL))
                return false;
        }
        
        // Update was successful.  Now we must return to the updated record.
        if (somethingUpdated)
        {
            try {
                rSet.refreshRow();
            } catch (SQLException e) {
                // Looks like rSet.refreshRow() isn't implemented.  Must requery and reposition.
                int CurrentPosition;
                try {
                    CurrentPosition = rSet.getRow();    // Current position
                } catch (SQLException q) {
                    CurrentPosition = 1;
                }
                obtainResultSet();                      // Get new result set
                try {
                    rSet.absolute(CurrentPosition);         // Return to previous position
                } catch (SQLException q) {
                    try {
                        rSet.absolute(1);
                    } catch (SQLException r) {
                    }
                } catch (java.lang.NullPointerException z) {  // could things get any worse?
                    System.err.println("rSet is null.");
                }
                cancelUpdate();
                gotoKey();                                    // Find key, i.e., return to editted record
            } catch (java.lang.NullPointerException en) {
                System.err.println("rSet is null.");
            }
        }

        cancelUpdate();
        rowChangeSupport.fireAfterUpdate();
        rowChangeSupport.fireRefreshController();
        
        return true;
    }
     
    /** Delete current record immediately without stopping for confirmation.  Only allowed if Key is specified. */
    public boolean deleteImmediate() {
        if (Key.getSize()==0)
            return false;
        rowChangeSupport.fireBeforeDelete();

        String SQL = "DELETE FROM " + getUpdateableViewName() + " WHERE " + Key.getSQLWhereClause();
        if (!getDatabaseConnection().executeUpdate(SQL))
            return false;

        // Delete was successful.  Now we must position to a meaningful record.
        try {
            rSet.refreshRow();
        } catch (SQLException e) {
            // Looks like rSet.refreshRow() isn't implemented.  Must requery and reposition.
            int CurrentPosition;
            try {
                CurrentPosition = rSet.getRow();    // Current position
            } catch (SQLException q) {
                CurrentPosition = 1;
            }
            obtainResultSet();                      // Get new result set
            try {
                if (!rSet.absolute(CurrentPosition))    // Return to previous position
                    rSet.absolute(CurrentPosition-1);
            } catch (SQLException q) {
                try {
                    rSet.absolute(1);
                } catch (SQLException r) {
                }
            } catch (java.lang.NullPointerException x) {  // from bad to worse
                System.err.println("rSet is null.");
            }
            cancelUpdate();
            current();
        } catch (java.lang.NullPointerException en) {
            System.err.println("rSet is null.");
        }
        
        cancelUpdate();
        rowChangeSupport.fireAfterDelete();
        rowChangeSupport.fireAfterNewResultSet();
        rowChangeSupport.fireRefreshController();
 
        return true;
    }
    
    /** Delete current record.  Only allowed if Key is specified. */
    public boolean delete() {
        if (Key.getSize()==0)
            return false;
        if (!rowChangeSupport.fireConfirmDelete())
            return false;
        return deleteImmediate();
    }
    
    /** Delete multiple records.  Only allowed if Key is specified.
     * @param Selections Array of key snapshots.
     */
    public boolean delete(DBColumnValues Selections[]) {
        if (Key.getSize()==0)
            return false;
        if (!rowChangeSupport.fireConfirmDelete())
            return false;
        for (int i=0; i<Selections.length; i++)
            if (gotoKey(Selections[i]))
                deleteImmediate();
        return true;
    }
    
    /** Update or Insert into the database */
    public boolean update() {
        if (isInsertMode() || (isEmpty() && isChanged()))
            return insertImmediate();
        else
            return updateImmediate();
    }
    
    /** Attempt to position the current row at an absolute row number */
    public void setRowNumber(int rnum) {
        if (isChanged())
            update();
        if (rSet==null)
            requery();
        try {
            rSet.absolute(rnum);
        } catch (SQLException q) {
            try {
                rSet.absolute(1);
            } catch (SQLException r) {
            }
        }
        current();
    }
    
    /** Get the current record number.  Return -1 if not positioned at a record. */
    public int getRowNumber() {
        try {
            return rSet.getRow();
        } catch (Exception q) {
            return -1;
        }
    }
        
    /** Re-run the query, and update all bindings appropriately. */
    public boolean requery() {
        if (isChanged())
            update();
        obtainResultSet();
        if (rSet==null)
            return false;
        return first();
    }

    /** Return true if we're on the first record */
    public boolean isFirst() {
        if (rSet==null)
            requery();
        try {
            return rSet.isFirst();
        } catch (SQLException e) {
            return false;
        } catch (java.lang.NullPointerException q) {
            return false;
        }
    }
    
    /** Get the first record, and update all bindings appropriately. */
    public boolean first() {
        if (isChanged())
            update();
        if (rSet==null)
            return requery();
        try {
            rSet.first();
        } catch (Exception e) {
            return false;
        }
        return current();
    }
    
    /** Return true if we're on the last record */
    public boolean isLast() {
        if (rSet==null)
            requery();
        try {
            return rSet.isLast();
        } catch (SQLException e) {
            return false;
        } catch (java.lang.NullPointerException q) {
            return false;
        }
    }
        
    /** Get the last record, and update all bindings appropriately. */
    public boolean last() {
        if (isChanged())
            update();
        if (rSet==null)
            if (!requery())
                return false;
        try {
            rSet.last();
        } catch (Exception e) {
            return false;
        }
        return current();
    }
    
    /** Get the next record, and update all bindings appropriately. */
    public boolean next() {
        if (isChanged())
            update();
        if (rSet==null)
            if (!requery())
                return false;
        try {
            if (!rSet.next())
                rSet.last();
        } catch (Exception e) {
            return false;
        }
        return current();
    }
    
    /** Get the previous record, and update all bindings appropriately. */
    public boolean previous() {
        if (isChanged())
            update();
        if (rSet==null)
            if (!requery())
                return false;
        try {
            if (!rSet.previous())
                rSet.first();
        } catch (Exception e) {
            return false;
        }
        return current();
    }
    
}
