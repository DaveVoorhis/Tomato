/*
 * DBRowButtonNext.java
 *
 * Created on February 15, 2002, 10:46 PM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

/**
 * DBRowButton to perform a next record operation on a DBRow.
 *
 * @author  creatist
 */
public class DBRowButtonNext extends DBRowButtonLast {
    
    /** Creates a new instance */
    public DBRowButtonNext() {
        setRepeating(true);
    }

    /** Override to determine what the button does when it's pressed. */
    public void action(java.awt.event.ActionEvent evt) {
        getDBRow().next();
    }
}
