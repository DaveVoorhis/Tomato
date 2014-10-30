/*
 * vBoolean.java
 *
 * Created on July 28, 2002, 11:15 PM
 */

package ca.mb.armchair.JVPL.Visualisers;

/**
 *
 * @author  creatist
 */
public class vBoolean extends ca.mb.armchair.JVPL.VisualiserOfClass {

    private javax.swing.JCheckBox jCheckBox;
    private boolean insideUpdate;
     
    /** Creates a new visualiser of Boolean */
    public vBoolean() {
        insideUpdate = false;
     }

    /** This method is overridden by visualisers of primitives, and provides
     * the appropriate string to intantiate a primitive's wrapper class
     * with the primitive value.  It is used by ModelToJava.  Returns null
     * if not primitive.
     */
    public String getPrimitiveInitialisation() {
        return "new java.lang.Boolean(" + jCheckBox.isSelected() + ")";
    }
    
    /** This method is overridden by Visualisers of primitives, and returns
     * the name of the primitive.
     */
    public String getPrimitiveName() {
        return "boolean";
    }
    
    // Set instance.
    public void setInstance(Object o) {
        super.setInstance(o);
        if (!insideUpdate)
            if (o!=null)
                jCheckBox.setSelected(((Boolean)o).booleanValue());
            else
                jCheckBox.setSelected(false);
    }
    
    // populate custom section
    protected void populateCustom() {
        jCheckBox = new javax.swing.JCheckBox();
        jCheckBox.setBounds(0, 0, 100, 20);
        jCheckBox.setMinimumSize(new java.awt.Dimension(100, 20));
        jCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insideUpdate = true;
                setInstance(new Boolean(jCheckBox.isSelected()));
                insideUpdate = false;
            }
        });
        add(jCheckBox, java.awt.BorderLayout.CENTER);
    }

}
