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
public class DBRowButtonDelete extends DBRowButton {
    
    /** Creates a new instance */
    public DBRowButtonDelete() {
    }

    /** Override to replace default button enabling process, which is to enable the button unless
     *  the DBRow is broken or unspecified. 
     */
    public void refresh() {
        setEnabled(!getDBRow().isBroken() && !getDBRow().isInsertMode() && !getDBRow().isChanged() && !getDBRow().isEmpty());
    }

    /** Override to determine what the button does when it's pressed. */
    public void action(java.awt.event.ActionEvent evt) {
        getDBRow().delete();
    }
}
