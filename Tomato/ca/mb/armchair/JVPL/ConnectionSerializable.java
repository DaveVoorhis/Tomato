/*
 * ConnectionSerializable.java
 *
 * Created on July 20, 2002, 11:34 PM
 */

package ca.mb.armchair.JVPL;

/**
 * A serializable representation of a Connection.
 *
 * @author  Dave Voorhis
 */
public class ConnectionSerializable extends java.lang.Object implements java.io.Serializable {
    
    private long SourceVisualiser;
    private TargetSerializable Target;
    
    /** Ctor */
    public ConnectionSerializable() {
    }
    
    /** Create a serialized representation of a connection */
    public ConnectionSerializable(Connection c) {
        setConnection(c);
    }
    
    /** Set up this serialized connection to reflect an actual Connection. */
    public void setConnection(Connection c) {
        Target = new TargetSerializable(c.getConnector());
        SourceVisualiser = c.getVisualiser().getID();
    }
    
    /** Try to look up the target connector associated with this serializable connection.
     * Return null if not found. */
    public Connector getConnector(Model w) {
        return Target.getConnector(w);
    }
    
    /** Look up the source visualiser associated with this connection.  Null if not found. */
    public Visualiser getVisualiser(Model w) {
        return w.getVisualiser(SourceVisualiser);
    }
    
    /** Stringize */
    public String toString() {
        return "connection from (" + SourceVisualiser + ") " + Target.toString();
    }
    
    /** Set the source visualiser reference */
    public void setSourceVisualiser(long l) {
        SourceVisualiser = l;
    }
    
    /** Get the source visualiser reference */
    public long getSourceVisualiser() {
        return SourceVisualiser;
    }
    
    /** Set the target */
    public void setTarget(TargetSerializable t) {
        Target = t;
    }
    
    /** Get the target */
    public TargetSerializable getTarget() {
        return Target;
    }
}
