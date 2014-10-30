/*
 * vLong.java
 *
 * Created on July 28, 2002, 11:15 PM
 */

package ca.mb.armchair.JVPL.Visualisers;

/**
 *
 * @author  creatist
 */
public class vLong extends ca.mb.armchair.JVPL.VisualiserOfClass {

    private javax.swing.JTextField jTextField;
    private boolean insideUpdate;
    
    /** Creates a new visualiser of Long */
    public vLong() {        
        insideUpdate = false;
    }

    /** This method is overridden by visualisers of primitives, and provides
     * the appropriate string to intantiate a primitive's wrapper class
     * with the primitive value.  It is used by ModelToJava.  Returns null
     * if not primitive.
     */
    public String getPrimitiveInitialisation() {
        return "new java.lang.Long(" + jTextField.getText() + ")";
    }

    /** This method is overridden by Visualisers of primitives, and returns
     * the name of the primitive.
     */
    public String getPrimitiveName() {
        return "long";
    }
    
    // Set instance.
    public void setInstance(Object o) {
        super.setInstance(o);
        if (!insideUpdate)
            if (o!=null)
                jTextField.setText(o.toString());
            else
                jTextField.setText("null");
    }
    
    // populate custom section
    protected void populateCustom() {
        jTextField = new javax.swing.JTextField();
        jTextField.setBounds(0, 0, 100, 20);
        jTextField.setMinimumSize(new java.awt.Dimension(100, 20));
        jTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                insideUpdate = true;
                try {
                    setInstance(new Long(jTextField.getText()));
                    jTextField.setForeground(new java.awt.Color(0, 0, 0));
                } catch (java.lang.NumberFormatException e) {
                    jTextField.setForeground(new java.awt.Color(255, 128, 128));
                }
                insideUpdate = false;
            }
        });
        add(jTextField, java.awt.BorderLayout.CENTER);
    }
}
