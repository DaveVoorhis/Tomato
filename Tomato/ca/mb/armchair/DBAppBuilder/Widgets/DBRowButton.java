/*
 * DBRowButton.java
 *
 * Created on February 15, 2002, 4:41 AM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

import ca.mb.armchair.DBAppBuilder.Beans.*;
import ca.mb.armchair.DBAppBuilder.Helpers.*;

/**
 * An accessory for DBRow that implements an action when the button is pressed.  Useful widgets may
 * be derived from this class.  It's not likely to be useful on its own.
 *
 * @author  creatist
 */
public class DBRowButton extends javax.swing.JButton {

    /** Delay between repeated actions */
    private static int RepeatDelay = 100;

    /** Set repetition delay */
    public static void setRepeatDelay(int n) {
        RepeatDelay = n;
    }
    
    /** Get repetition delay */
    public static int getRepeatDelay() {
        return RepeatDelay;
    }
    
    /** Repetition timer */
    private javax.swing.Timer repeater = new javax.swing.Timer(getRepeatDelay(), new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            action(evt);
        }
    });
    
    /** Creates a new instance of DBRowButton */
    public DBRowButton() {
        setEnabled(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (isRepeating())
                    repeater.start();
            }
            public void mouseReleased(java.awt.event.MouseEvent e) {
                repeater.stop();
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                repeater.stop();
            }
        });
        addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                action(evt);
            }
        });
    }

    /** The DBRow we're controlling */
    DBRow dbRow = null;

    /** Set the DBRow to control */
    public void setDBRow(DBRow dbR) {
        dbRow = dbR;
        if (dbRow != null) {
            dbRow.addRowChangeListener(new RowChangeAdapter() {
                public void refreshController(RowChangeEvent e) {
                    refresh();
                }
            });
        }
    }
    
    /** Get the DBRow we're controlling */
    public DBRow getDBRow() {
        return dbRow;
    }

    /** Override to replace default button enabling process, which is to enable the button unless
     *  the DBRow is broken or unspecified. 
     */
    public void refresh() {
        if (getDBRow() != null)
            setEnabled(!getDBRow().isBroken());
    }

    /** Override to determine what the button does when it's pressed. */
    public void action(java.awt.event.ActionEvent evt) {
    }
    
    /** Repeating flag */
    private boolean Repeating = false;
    
    /** Set to true to automatically repeat the action as long as the button is held down. */
    public void setRepeating(boolean flag) {
        Repeating = flag;
    }
    
    /** True if action repeats as long as the button is held down. */
    public boolean isRepeating() {
        return Repeating;
    }
    
}
