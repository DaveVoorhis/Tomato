/*
 * DBTextField.java
 *
 * Created on January 11, 2002, 5:22 AM
 */

package ca.mb.armchair.DBAppBuilder.Widgets;

/**
 *
 * @author  creatist
 */
public class DBTextField extends javax.swing.JTextField implements ca.mb.armchair.DBAppBuilder.Interfaces.DBBindableComponent {

    private java.util.Vector listeners = new java.util.Vector();
    
    /** Creates a new instance of DBTextField */
    public DBTextField() {
        addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                for (int i=0; i<listeners.size(); i++)
                    ((java.awt.event.ActionListener)listeners.elementAt(i)).actionPerformed(new java.awt.event.ActionEvent(this,i,"Update"));
            }
        });
    }

    /** Get the value of the component.  */
    public String getColumnValue() {
        return getText();
    }
    
    /** Set the value of the component, updating it appropriately.  */
    public void setColumnValue(String v) {
        setText(v);
    }
       
    /** Add a listener to listen for a change in value to this component  */
    public void addActionListener(java.awt.event.ActionListener l) {
        super.addActionListener(l);
        listeners.add(l);
    }
    
    /** Remove a given listener  */
    public void removeActionListener(java.awt.event.ActionListener l) {
        super.removeActionListener(l);
        listeners.remove(l);
    }
}
