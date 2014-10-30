/*
 * RowChangeAdapter.java
 *
 * Created on January 30, 2002, 11:55 PM
 */

package ca.mb.armchair.DBAppBuilder.Helpers;

import ca.mb.armchair.DBAppBuilder.Interfaces.*;

/**
 *
 * Row change event adapter.  Extends jPanel, because most derivations of this class will probably be multi-button
 * control panels.
 *
 * @author  creatist
 */
public class RowChangeAdapter extends javax.swing.JPanel implements RowChangeListener {

    /** Creates a new instance of RowChangeAdapter */
    public RowChangeAdapter() {
    }

    /** Override to implement pre-newrecordset handler */
    public void beforeNewResultSet(RowChangeEvent e) {
    }

    /** Override to implement post-newrecordset handler */
    public void afterNewResultSet(RowChangeEvent e) {
    }

    /** Override to implement confirmation for InsertMode  */
    public boolean confirmInsertMode(RowChangeEvent e) {
        return true;
    }    
    
    /** Override to implement pre-insertmode handler */
    public void beforeInsertMode(RowChangeEvent e) {
    }
    
    /** Override to implement post-insertmode handler */
    public void afterInsertMode(RowChangeEvent e) {
    }
    
    /** Override to implement confirmation for Insert  */
    public boolean confirmInsert(RowChangeEvent e) {
        return true;
    }
    
    /** Override to implement pre-insert handler  */
    public void beforeInsert(RowChangeEvent e) {
    }
    
    /** Override to implement button enabling process  */
    public void refreshController(RowChangeEvent e) {
    }
    
    /** Override to implement post-insert handler  */
    public void afterInsert(RowChangeEvent e) {
    }
    
    /** Override to implement confirmation for Delete  */
    public boolean confirmDelete(RowChangeEvent e) {
        return true;
    }
    
    /** Override to implement pre-delete handler  */
    public void beforeDelete(RowChangeEvent e) {
    }
    
    /** Override to implement confirmation for Update  */
    public boolean confirmUpdate(RowChangeEvent e) {
        return true;
    }
    
    /** Override to implement pre-update handler  */
    public void beforeUpdate(RowChangeEvent e) {
    }
    
    /** Override to implement post-delete handler  */
    public void afterDelete(RowChangeEvent e) {
    }
    
    /** Override to implement post-update handler  */
    public void afterUpdate(RowChangeEvent e) {
    }

}
