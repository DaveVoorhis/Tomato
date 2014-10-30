/*
 * DBRowButtonSave.java
 *
 * Created on February 15, 2002, 10:46 PM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

/**
 * DBRowButton to perform an update on a DBRow.
 *
 * @author  creatist
 */
public class DBRowButtonSave extends DBRowButtonUndo {
    
    /** Creates a new instance */
    public DBRowButtonSave() {
    }

    /** Override to determine what the button does when it's pressed. */
    public void action(java.awt.event.ActionEvent evt) {
        getDBRow().update();
    }
}
