/*
 * ModelSerializable.java
 *
 * Created on September 26, 2002  9:20 PM
 */

package ca.mb.armchair.JVPL;

/**
 * A serializable representation of summary information about a Model.  This
 * does NOT include Connections or Visualisers belonging to the Model, as those
 * are handled by ConnectionSerializable and VisualiserSerializable.  Shell
 * saves and loads all three, using ModelSerializable as a header.
 *
 * @author  Dave Voorhis
 */

public class ModelSerializable extends java.lang.Object implements java.io.Serializable {

    private int VisualiserCount;
    private int ConnectionCount;
    private String ModifierString;
    
    /** Ctor. */
    public ModelSerializable(Model m) {
        setVisualiserCount(m.getVisualiserCount());
        setConnectionCount(m.getConnectionCount());
        setModifierString(m.getModifierString());
    }
    
    /** Ctor. */
    public ModelSerializable() {
        VisualiserCount = 0;
        ConnectionCount = 0;
        ModifierString = "";
    }

    public void setVisualiserCount(int n) {
        VisualiserCount = n;
    }
    
    public int getVisualiserCount() {
        return VisualiserCount;
    }
    
    public void setConnectionCount(int n) {
        ConnectionCount = n;
    }
    
    public int getConnectionCount() {
        return ConnectionCount;
    }
    
    public void setModifierString(String s) {
        ModifierString = s;
    }
    
    public String getModifierString() {
        return ModifierString;
    }
}
