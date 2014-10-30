/*
 * DBRowButtonFirst.java
 *
 * Created on February 15, 2002, 10:46 PM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

/**
 * DBRowButton to perform a first record operation on a DBRow.
 *
 * @author  creatist
 */
public class DBRowButtonFirst extends DBRowButton {
    
    /** Creates a new instance */
    public DBRowButtonFirst() {
    }

    /** Override to replace default button enabling process, which is to enable the button unless
     *  the DBRow is broken or unspecified. 
     */
    public void refresh() {
        setEnabled(!getDBRow().isBroken() && !getDBRow().isFirst() && !getDBRow().isInsertMode() && !getDBRow().isEmpty());
    }

    /** Override to determine what the button does when it's pressed. */
    public void action(java.awt.event.ActionEvent evt) {
        getDBRow().first();
    }
}
