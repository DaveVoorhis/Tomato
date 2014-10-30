/*
 * RowChangeSupport.java
 *
 * Created on January 30, 2002, 7:07 PM
 */

package ca.mb.armchair.DBAppBuilder.Helpers;

import ca.mb.armchair.DBAppBuilder.Interfaces.*;
import ca.mb.armchair.DBAppBuilder.Beans.*;
import java.util.*;

/**
 * This class implements mechanisms for firing RowChange events, primarily implemented by DBRow.
 *
 * @author  creatist
 */
public class RowChangeSupport {

    Vector listeners = new Vector();
    DBRow dbRow;
    
    /** Creates a new instance of RowChangeSupport */
    public RowChangeSupport(DBRow d) {
        dbRow = d;
    }

    /** Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addRowChangeListener(RowChangeListener l) {
        listeners.add(l);
    }
    
    /** Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removeRowChangeListener(RowChangeListener l) {
        listeners.remove(l);
    }
   
    /** Fire pre-newresultset handler */
    public void fireBeforeNewResultSet() {
        for (int i=0; i<listeners.size(); i++)
            ((RowChangeListener)listeners.elementAt(i)).beforeNewResultSet(new RowChangeEvent(dbRow,"BeforeNewResultSet"));
    }
    
    /** Fire post-newresultset handler */
    public void fireAfterNewResultSet() {
        for (int i=0; i<listeners.size(); i++)
            ((RowChangeListener)listeners.elementAt(i)).afterNewResultSet(new RowChangeEvent(dbRow,"BeforeNewResultSet"));
    }
    
    /** Fire confirmation for InsertMode  */
    public boolean fireConfirmInsertMode() {
        for (int i=0; i<listeners.size(); i++)
            if (!((RowChangeListener)listeners.elementAt(i)).confirmInsertMode(new RowChangeEvent(dbRow,"ConfirmInsertMode")))
                return false;
        return true;
    }    

    /** Fire pre-insertmode handler */
    public void fireBeforeInsertMode() {
        for (int i=0; i<listeners.size(); i++)
            ((RowChangeListener)listeners.elementAt(i)).beforeInsertMode(new RowChangeEvent(dbRow,"BeforeInsertMode"));
    }
    
    /** Fire post-insertmode handler */
    public void fireAfterInsertMode() {
        for (int i=0; i<listeners.size(); i++)
            ((RowChangeListener)listeners.elementAt(i)).afterInsertMode(new RowChangeEvent(dbRow,"AfterInsertMode"));
    }
    
    /** Fire confirmation for Insert  */
    public boolean fireConfirmInsert() {
        for (int i=0; i<listeners.size(); i++)
            if (!((RowChangeListener)listeners.elementAt(i)).confirmInsert(new RowChangeEvent(dbRow,"ConfirmInsert")))
                return false;
        return true;
    }
    
    /** Fire pre-insert handler  */
    public void fireBeforeInsert() {
        for (int i=0; i<listeners.size(); i++)
            ((RowChangeListener)listeners.elementAt(i)).beforeInsert(new RowChangeEvent(dbRow,"BeforeInsert"));
    }
    
    /** Fire button enabling process  */
    public void fireRefreshController() {
        for (int i=0; i<listeners.size(); i++)
            ((RowChangeListener)listeners.elementAt(i)).refreshController(new RowChangeEvent(dbRow,"RefreshController"));
    }
    
    /** Fire post-insert handler  */
    public void fireAfterInsert() {
        for (int i=0; i<listeners.size(); i++)
            ((RowChangeListener)listeners.elementAt(i)).afterInsert(new RowChangeEvent(dbRow,"AfterInsert"));
    }
    
    /** Fire confirmation for Delete  */
    public boolean fireConfirmDelete() {
        for (int i=0; i<listeners.size(); i++)
            if (!((RowChangeListener)listeners.elementAt(i)).confirmDelete(new RowChangeEvent(dbRow,"ConfirmDelete")))
                return false;
        return true;
    }
    
    /** Fire pre-delete handler  */
    public void fireBeforeDelete() {
        for (int i=0; i<listeners.size(); i++)
            ((RowChangeListener)listeners.elementAt(i)).beforeDelete(new RowChangeEvent(dbRow,"BeforeDelete"));
    }
    
    /** Fire confirmation for Update  */
    public boolean fireConfirmUpdate() {
        for (int i=0; i<listeners.size(); i++)
            if (!((RowChangeListener)listeners.elementAt(i)).confirmUpdate(new RowChangeEvent(dbRow,"ConfirmUpdate")))
                return false;
        return true;
    }
    
    /** Fire pre-update handler  */
    public void fireBeforeUpdate() {
        for (int i=0; i<listeners.size(); i++)
            ((RowChangeListener)listeners.elementAt(i)).beforeUpdate(new RowChangeEvent(dbRow,"BeforeUpdate"));
    }
    
    /** Fire post-delete handler  */
    public void fireAfterDelete() {
        for (int i=0; i<listeners.size(); i++)
            ((RowChangeListener)listeners.elementAt(i)).afterDelete(new RowChangeEvent(dbRow,"AfterDelete"));
    }
    
    /** Fire post-update handler  */
    public void fireAfterUpdate() {
        for (int i=0; i<listeners.size(); i++)
            ((RowChangeListener)listeners.elementAt(i)).afterUpdate(new RowChangeEvent(dbRow,"AfterUpdate"));
    }
    
}
