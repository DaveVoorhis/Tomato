/*
 * ClassVisualiserMapEntry.java
 *
 * Created on July 28, 2002, 9:46 PM
 */

package ca.mb.armchair.JVPL;

/**
 * Implements a relationship between a class name and a class.
 *
 * Used internally to implement mappings between primitive
 * names and class wrappers, etc.
 *
 * @author  Dave Voorhis
 */
public class ClassMapEntry implements java.io.Serializable {
    
    private String ClassName;
    private Class TargetClass;
    private int DefaultSerializationMode;

    /** Constructor */
    ClassMapEntry(String className, Class c, int SerializationMode) {
        ClassName = className;
        TargetClass = c;
        DefaultSerializationMode = SerializationMode;
    }
    
    /** Stringize */
    public String toString() {
        return "ClassMapEntry: " + ClassName + " -> " + TargetClass.getName();
    }
    
    /** Get target class */
    public Class getTargetClass() {
        return TargetClass;
    }
    
    /** Get source class name */
    public String getClassName() {
        return ClassName;
    }
    
    /** Get suggested serialization mode */
    public int getSerializationMode() {
        return DefaultSerializationMode;
    }
}
