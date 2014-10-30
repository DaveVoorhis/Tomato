/*
 * DBRowButtonUndo.java
 *
 * Created on February 15, 2002, 10:46 PM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

/**
 * DBRowButton to perform an undo on a DBRow.
 *
 * @author  creatist
 */
public class DBRowButtonDeleteSelected extends DBRowButton {
    
    /** Creates a new instance */
    public DBRowButtonDeleteSelected() {
    }

    /** The row selector that handles multi-record deletes on our behalf. */
    private DBRowSelector RowSelector = null;
    
    /** Identify a DBRowSelector that will manage record deletion when there are multiple selections. */
    public void setDBRowSelector(DBRowSelector rs) {
        RowSelector = rs;
    }
    
    /** Return the DBRowSelector that is managing record deletion.  Return null if there isn't one. */
    public DBRowSelector getDBRowSelector() {
        return RowSelector;
    }

    /** Override to replace default button enabling process, which is to enable the button unless
     *  the DBRow is broken or unspecified. 
     */
    public void refresh() {
        if (getDBRowSelector()!=null) {
            setEnabled(!getDBRow().isBroken() && !getDBRow().isInsertMode() && !getDBRow().isChanged() && !getDBRow().isEmpty() && 
                        getDBRowSelector().getSelectionCount()>0); 
            if (isEnabled())
                setText("delete selected (" + getDBRowSelector().getSelectionCount() + ")");
            else
                setText("delete selected");
        }
    }

    /** Override to determine what the button does when it's pressed. */
    public void action(java.awt.event.ActionEvent evt) {
        if (getDBRowSelector()!=null)
            getDBRow().delete(getDBRowSelector().getSelections());
    }
}
