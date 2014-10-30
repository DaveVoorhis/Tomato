/*
 * TargetSerializable.java
 *
 * Created on July 24, 2002, 12:18 AM
 */

package ca.mb.armchair.JVPL;

/**
 * A serializable representation of a Connection between a Visualiser and a Connector.
 *
 * @author  Dave Voorhis
 */

public class TargetSerializable extends java.lang.Object implements java.io.Serializable {
    
    private long TargetVisualiser;
    private String TargetConnector;
    private int TargetParameterNumber;
    
    /** Ctor. */
    public TargetSerializable(Connector c) {
        setConnector(c);
    }
    
    /** Ctor. */
    public TargetSerializable() {
        v = null;
    }

    public long getTargetVisualiser() {
        return TargetVisualiser;
    }
    
    public void setTargetVisualiser(long v) {
        TargetVisualiser = v;
    }
    
    public String getTargetConnector() {
        return TargetConnector;
    }
    
    public void setTargetConnector(String s) {
        TargetConnector = s;
    }
    
    public void setTargetParameterNumber(int n) {
        TargetParameterNumber = n;
    }
    
    public int getTargetParameterNumber() {
        return TargetParameterNumber;
    }
    
    /** Set the connector. */
    public void setConnector(Connector c) {
        TargetConnector = c.getConnectorID();
        TargetVisualiser = c.getVisualiser().getID();
        TargetParameterNumber = c.getParameterNumber();
        v = null;
    }

    // Cache visualiser for getConnector, so repeated lookups aren't required.
    private transient Visualiser v;         
    
    /** Try to look up the target Connector associated with this serializable target.  Return
     * null if not found. */
    public Connector getConnector(Model w) {
        if (v==null) {
            v = w.getVisualiser(TargetVisualiser);
            if (v==null)        // got visualiser?
                return null;
        }
        return v.getConnector(TargetConnector, TargetParameterNumber);
    }

    /** Stringize */
    public String toString() {
        return "to (" + TargetVisualiser + ")::" + TargetConnector;
    }
}
