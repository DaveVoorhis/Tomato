/*
 * vString.java
 *
 * Created on July 28, 2002, 11:15 PM
 */

package ca.mb.armchair.JVPL.Visualisers;

/**
 * String wrapper visualiser.
 *
 * @author  Dave Voorhis
 */
public class vString extends ca.mb.armchair.JVPL.VisualiserOfClass {

    private javax.swing.JTextArea jTextArea;
    private boolean insideUpdate;
    
    /** Creates a new visualiser of java.lang.String */
    public vString() {
        insideUpdate = false;
    }

    /** This method is overridden by visualisers of primitives, and provides
     * the appropriate string to intantiate a primitive's wrapper class
     * with the primitive value.  It is used by ModelToJava.  Returns null
     * if not primitive.
     */
    public String getPrimitiveInitialisation() {
        return "new java.lang.String(\"" + 
                ca.mb.armchair.Utilities.ContentTranslation.QuotedString.getQuotedString(jTextArea.getText()) + 
                "\")";
    }

    // Set instance.
    public void setInstance(Object o) {
        super.setInstance(o);
        if (!insideUpdate)
            if (o!=null)
                jTextArea.setText((String)getInstance());
            else
                jTextArea.setText("");
    }
    
    // populate custom section
    protected void populateCustom() {
        jTextArea = new javax.swing.JTextArea();
        jTextArea.setBounds(0, 0, 100, 20);
        jTextArea.setMinimumSize(new java.awt.Dimension(100, 20));
        jTextArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                insideUpdate = true;
                setInstance(jTextArea.getText());
                insideUpdate = false;
            }
        });
        add(jTextArea, java.awt.BorderLayout.CENTER);
    }
}
