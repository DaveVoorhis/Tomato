/*
 * DBRowButtonNew.java
 *
 * Created on February 15, 2002, 10:46 PM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

/**
 * DBRowButton to switch a DBRow into Insert mode.
 *
 * @author  creatist
 */
public class DBRowButtonNew extends DBRowButton {
    
    /** Creates a new instance of DBRowButtonNew */
    public DBRowButtonNew() {
    }

    /** Override to replace default button enabling process, which is to enable the button unless
     *  the DBRow is broken or unspecified. 
     */
    public void refresh() {
        setEnabled(!getDBRow().isBroken() && !getDBRow().isInsertMode());
    }

    /** Override to determine what the button does when it's pressed. */
    public void action(java.awt.event.ActionEvent evt) {
        getDBRow().insert();
    }
}
