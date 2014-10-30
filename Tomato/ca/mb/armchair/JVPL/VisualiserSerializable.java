/*
 * ConnectionSerializable.java
 *
 * Created on July 20, 2002, 11:34 PM
 */

package ca.mb.armchair.JVPL;

import ca.mb.armchair.Utilities.ContentTranslation.Base64;

/**
 * A serializable representation of a Visualiser.  Visualisers
 * are deserialized by VisualiserFactory.
 *
 * @author  Dave Voorhis
 */
public class VisualiserSerializable implements java.io.Serializable {
    
    private String Name;                        // base visualiser data
    private long ID;                                // visualiser ID
    private int xpos;                               // visualiser position
    private int ypos;
    private String ModifierString;             // modifiers
    private boolean isMessage;                  // true if it's a message visualiser
    private boolean InvokesAfterParmChange;         // message visualiser features
    private boolean InvokesBeforeReturnUse;
    private boolean InvokesAfterReferenceChange;
    private boolean InvokesBeforeReferenceUse;
    private boolean Gated;
    private boolean Counted;
    private boolean Repeated;
    
    private String serializedInstance;  // class visualiser's data
    private String VType;                   // class name
    
    private int SerializationMode;  // serialization mode
    
    /** Create a serializable visualiser. */
    public VisualiserSerializable() {
    }

    /** Create a serializable visualiser. */
    public VisualiserSerializable(Visualiser v) {
        serializeVisualiser(v);
    }
    
    /** Attempt to deserialize a given Stringified instance. */
    public static Object getDeserializedInstance(int Mode, String SI) {
        if (SI==null)
            return null;
        Object instance = null;
        java.io.ByteArrayInputStream buffer = new java.io.ByteArrayInputStream(Base64.decode(SI).getBytes());
        try {
            switch (Mode) {
                case Visualiser.SERIALIZE_BINARY:
                    java.io.ObjectInputStream binarystream = new java.io.ObjectInputStream(buffer);
                    instance = binarystream.readObject();
                    break;
                case Visualiser.SERIALIZE_XML:
                    java.beans.XMLDecoder xmlstream = new java.beans.XMLDecoder(buffer);
                    instance = xmlstream.readObject();
                    break;
                default:
                    instance = null;
            }
        } catch (Throwable t) {
            instance = null;
            Log.println("VisualiserSerializable: Unable to unserialize instance: " + t.toString());
        }
        return instance;
    }
    
    /** Attempt to deserialize the instance and return it as an object. */
    public Object getDeserializedInstance() {
        return getDeserializedInstance(getSerializationMode(), serializedInstance);
    }
    
    /** Get a string serialization of given instance.  Return null if failed. */
    public static String getSerializedInstance(int Mode, Object o) {
        String serializedInstance;
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();            
        try {
            if (Mode==Visualiser.SERIALIZE_BINARY) {
                java.io.ObjectOutputStream stream = new java.io.ObjectOutputStream(buffer);
                stream.writeObject(o);
                stream.flush();                
                serializedInstance = Base64.encode(buffer.toString());
            } else if (Mode==Visualiser.SERIALIZE_XML) {
                java.beans.XMLEncoder stream = new java.beans.XMLEncoder(buffer);
                stream.writeObject(o);
                stream.close();
                serializedInstance = Base64.encode(buffer.toString());
            } else
                serializedInstance = null;
        } catch (Throwable t) {
            serializedInstance = null;
            Log.println("VisualiserSerializable: Unable to serialize instance: " + t.toString());
        }
        return serializedInstance;
    }
    
    /** Set the Visualiser serialized by this. */
    public void serializeVisualiser(Visualiser v) {
        ID = v.getID();
        xpos = v.getX();
        ypos = v.getY();
        Name = v.getName();
        ModifierString = v.getModifierString();
        SerializationMode = v.getSerializationMode();
        isMessage = false;
        if (v.isVisualiserOfMessage()) {
            VisualiserOfMessage VOM = (VisualiserOfMessage)v;
            InvokesAfterParmChange = VOM.isInvokesAfterParmChange();
            InvokesBeforeReturnUse = VOM.isInvokesBeforeReturnUse();
            InvokesAfterReferenceChange = VOM.isInvokesAfterReferenceChange();
            InvokesBeforeReferenceUse = VOM.isInvokesBeforeReferenceUse();
            Gated = VOM.isGated();
            Counted = VOM.isCounted();
            Repeated = VOM.isRepeated();
            isMessage = true;
        } else if (v.isSerializableInstance() && v.getInstance()!=null && 
                   (SerializationMode==Visualiser.SERIALIZE_BINARY || SerializationMode==Visualiser.SERIALIZE_XML))
            serializedInstance = getSerializedInstance(getSerializationMode(), v.getInstance());
        else
            serializedInstance = null;
        if (v.getBoundClass()!=null)
            VType = v.getBoundClass().getName();
        else
            VType = null;
    }

    /** Obtain a Visualiser from a serializable Visualiser. */
    public Visualiser deserializeVisualiser(Library l) {
        Visualiser v = null;
        if (getMessage()) {
            v = VisualiserFactory.newVisualiser();
            VisualiserOfMessage VOM = (VisualiserOfMessage)v;
            VOM.setInvokesAfterParmChange(getInvokesAfterParmChange());
            VOM.setInvokesBeforeReturnUse(getInvokesBeforeReturnUse());
            VOM.setInvokesAfterReferenceChange(getInvokesAfterReferenceChange());
            VOM.setInvokesBeforeReferenceUse(getInvokesBeforeReferenceUse());
            VOM.setGated(getGated());
            VOM.setCounted(getCounted());
            VOM.setRepeated(getRepeated());
        } else {
            v = VisualiserFactory.newVisualiser(l, getVType());
            if (v==null) {
                Log.println("VisualiserSerializable: Unable to create Visualiser for type: " + getVType());
                return null;
            } 
            else if (getSerializedInstance()!=null)
                v.setInstance(getDeserializedInstance());
            else if (getSerializationMode()==Visualiser.SERIALIZE_AUTOINSTANTIATE)
                v.instantiate();
            else
                v.setInstance(null);
        }
        v.setSerializationMode(getSerializationMode());
        v.setID(getID());
        v.setLocation(getXpos(), getYpos());
        v.setName(getName());
        v.setModifierString(getModifierString());
        return v;
    }
    
    /** Stringize. */
    public String toString() {
        return "VisualiserSerializable: " + ID + "'" + Name + "'" + " (" + xpos + ", " + ypos + ") ";
    }
    
    public void setXpos(int x) {
        xpos = x;
    }
    
    public int getXpos() {
        return xpos;
    }
    
    public void setYpos(int y) {
        ypos = y;
    }
    
    public int getYpos() {
        return ypos;
    }
    
    public void setMessage(boolean b) {
        isMessage = b;
    }
    
    public boolean getMessage() {
        return isMessage;
    }
    
    public void setSerializedInstance(String serialized) {
        serializedInstance = serialized;
    }
    
    public String getSerializedInstance() {
        return serializedInstance;
    }
    
    public void setVType(String s) {
        VType = s;
    }
    
    public String getVType() {
        return VType;
    }
    
    public void setName(String s) {
        Name = s;
    }
    
    public String getName() {
        return Name;
    }
    
    public void setID(long l) {
        ID = l;
    }
    
    public long getID() {
        return ID;
    }
    
    public void setInvokesAfterParmChange(boolean b) {
        InvokesAfterParmChange = b;
    }
    
    public boolean getInvokesAfterParmChange() {
        return InvokesAfterParmChange;
    }
    
    public void setInvokesBeforeReturnUse(boolean b) {
        InvokesBeforeReturnUse = b;
    }
    
    public boolean getInvokesBeforeReturnUse() {
        return InvokesBeforeReturnUse;
    }
    
    public void setInvokesAfterReferenceChange(boolean b) {
        InvokesAfterReferenceChange = b;
    }
    
    public boolean getInvokesAfterReferenceChange() {
        return InvokesAfterReferenceChange;
    }
    
    public void setInvokesBeforeReferenceUse(boolean b) {
        InvokesBeforeReferenceUse = b;
    }
    
    public boolean getInvokesBeforeReferenceUse() {
        return InvokesBeforeReferenceUse;
    }
    
    public void setGated(boolean b) {
        Gated = b;
    }
    
    public boolean getGated() {
        return Gated;
    }
    
    public void setCounted(boolean b) {
        Counted = b;
    }
    
    public boolean getCounted() {
        return Counted;
    }
    
    public void setRepeated(boolean b) {
        Repeated = b;
    }
    
    public boolean getRepeated() {
        return Repeated;
    }
            
    public void setSerializationMode(int n) {
        SerializationMode = n;
    }
    
    public int getSerializationMode() {
        return SerializationMode;
    }
    
    public String getModifierString() {
        return ModifierString;
    }
    
    public void setModifierString(String s) {
        ModifierString = s;
    }
}
