/*
 * VisualiserFactory.java
 *
 * Created on June 30, 2002, 1:57 AM
 */

package ca.mb.armchair.JVPL;

/**
 * VisualiserFactory creates new Visualisers.
 *
 * @author  Dave Voorhis
 */
public class VisualiserFactory {
    
    private static java.util.Vector ClassVisualiserMap = new java.util.Vector();
    private static java.util.Vector WrapperMap = new java.util.Vector();
    
    /** Obtain a visualiser for a class, given its name.  May return
     * special visualisers.  Searches paths specified in Library, if
     * the class can't be loaded via the system loader. */
    public static Visualiser newVisualiser(Library l, String ClassName) {
        if (WrapperMap.size()==0)       // No wrappers?  Must require initialisation...
            buildDefaultClassMaps();
        Class V;
        int SerializationMode;
        ClassMapEntry mapEntry = getClassVisualiserMapEntry(ClassName);
        if (mapEntry!=null) {
            V = mapEntry.getTargetClass();
            SerializationMode = mapEntry.getSerializationMode();
        } else {
            V = VisualiserOfClass.class;
            SerializationMode = Visualiser.SERIALIZE_AUTOINSTANTIATE;
        }
        if (V==null) {
            Log.println("VisualiserFactory: Unable to obtain class for " + ClassName);
            return null;
        }
        else {
            Visualiser v;
            try {
                v = (Visualiser)V.newInstance();
            } catch (Throwable t) {
                Log.println("VisualiserFactory: newVisualiser unable to create instance of " + V.toString() + ": " + t.toString());
                Log.printStackTrace(t.getStackTrace());
                v = null;
            }
            if (v!=null)
                v.setBoundClass(getWrapperClass(l, ClassName));
            v.setSerializationMode(SerializationMode);
            return v;
        }
    }
    
    /** Obtain a Visualiser for a class.  Because this class may require wrapping,
     * the wrapper class may be looked up in the given library, if not found
     * by the system class loader. */
    public static Visualiser newVisualiser(Library l, Class C) {
        return newVisualiser(l, C.getName());
    }
    
    /** Obtain a new Visualiser for an existing Object.   Because this class may require wrapping,
     * the wrapper class may be looked up in the given library, if not found
     * by the system class loader. */
    public static Visualiser newVisualiser(Library l, Object anObject) {
        Visualiser V = newVisualiser(l, anObject.getClass());
        V.setInstance(anObject);
        return V;
    }
    
    /** Obtain a new instance of a message Visualiser -- the thing that can invoke a method or constructor. */
    public static Visualiser newVisualiser() {
        return new VisualiserOfMessage();
    }
    
    /** Add a class-visualiser map entry. */
    private static void addVisualiserMapEntry(String classNameFrom, Class Visualiser, int DefaultSerialization) {
        ClassVisualiserMap.add(new ClassMapEntry(classNameFrom, Visualiser, DefaultSerialization));
    }
    
    /** Add a wrapper map entry. */
    private static void addWrapperMapEntry(String classNameFrom, Class Wrapper) {
        WrapperMap.add(new ClassMapEntry(classNameFrom, Wrapper, Visualiser.SERIALIZE_AUTOINSTANTIATE));
    }
    
    /** Get the ith class-visualiser map entry. */
    private static ClassMapEntry getClassVisualiserMapEntry(int i) {
        return (ClassMapEntry)ClassVisualiserMap.get(i);
    }
    
    /** Find a class-visualiser map entry for a given class name.  Null if not mapped. */
    private static ClassMapEntry getClassVisualiserMapEntry(String classNameFrom) {
        if (classNameFrom==null)
            return null;
        for (int i=0; i<ClassVisualiserMap.size(); i++) {
            ClassMapEntry e = getClassVisualiserMapEntry(i);
            if (e.getClassName().compareTo(classNameFrom)==0)
                return e;
        }
        return null;
    }
    
    /** Get the ith wrapper map entry. */
    private static ClassMapEntry getWrapperMapEntry(int i) {
        return (ClassMapEntry)WrapperMap.get(i);
    }
    
    /** Find a wrapper-map entry for a given class name.  Null if not mapped. */
    private static ClassMapEntry getWrapperMapEntry(String classNameFrom) {
        if (classNameFrom==null)
            return null;
        for (int i=0; i<WrapperMap.size(); i++) {
            ClassMapEntry e = getWrapperMapEntry(i);
            if (e.getClassName().compareTo(classNameFrom)==0)
                return e;
        }
        return null;
    }
    
    /** Given a class name, return class that should be used to wrap it.
     * Searches for class in paths specified by Library, if class can't
     * be loaded by system loader.
     */
    private static Class getWrapperClass(Library l, String classNameFrom) {
        ClassMapEntry mapEntry = getWrapperMapEntry(classNameFrom);
        if (mapEntry!=null)
            return mapEntry.getTargetClass();
        try {
            return ClassPathLoader.forName(l, classNameFrom);
        } catch (java.lang.ClassNotFoundException e) {
            Log.println("VisualiserFactory: Unable to load class '" + classNameFrom + "': " + e.toString());
            return null;
        }
    }
    
    // Build default class maps.
    private static void buildDefaultClassMaps() {
        // String has special visualiser, to allow user to edit directly.
        addVisualiserMapEntry("java.lang.String", ca.mb.armchair.JVPL.Visualisers.vString.class, Visualiser.SERIALIZE_XML);
        // Primitives and primitive wrappers have special visualisers, to allow users
        // to edit values directly.
        addVisualiserMapEntry("int", ca.mb.armchair.JVPL.Visualisers.vInteger.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("java.lang.Integer", ca.mb.armchair.JVPL.Visualisers.vInteger.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("boolean", ca.mb.armchair.JVPL.Visualisers.vBoolean.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("java.lang.Boolean", ca.mb.armchair.JVPL.Visualisers.vBoolean.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("long", ca.mb.armchair.JVPL.Visualisers.vLong.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("java.lang.Long", ca.mb.armchair.JVPL.Visualisers.vLong.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("char", ca.mb.armchair.JVPL.Visualisers.vCharacter.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("java.lang.Character", ca.mb.armchair.JVPL.Visualisers.vCharacter.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("float", ca.mb.armchair.JVPL.Visualisers.vFloat.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("java.lang.Float", ca.mb.armchair.JVPL.Visualisers.vFloat.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("double", ca.mb.armchair.JVPL.Visualisers.vDouble.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("java.lang.Double", ca.mb.armchair.JVPL.Visualisers.vDouble.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("byte", ca.mb.armchair.JVPL.Visualisers.vByte.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("java.lang.Byte", ca.mb.armchair.JVPL.Visualisers.vByte.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("short", ca.mb.armchair.JVPL.Visualisers.vShort.class, Visualiser.SERIALIZE_XML);
        addVisualiserMapEntry("java.lang.Short", ca.mb.armchair.JVPL.Visualisers.vShort.class, Visualiser.SERIALIZE_XML);
        // Primitives must be wrapped.  Unwrapping occurs automatically within Constructor.newInstance()
        // and Method.invoke().
        addWrapperMapEntry("int", java.lang.Integer.class);
        addWrapperMapEntry("boolean", java.lang.Boolean.class);
        addWrapperMapEntry("long", java.lang.Long.class);
        addWrapperMapEntry("char", java.lang.Character.class);
        addWrapperMapEntry("float", java.lang.Float.class);
        addWrapperMapEntry("double", java.lang.Double.class);
        addWrapperMapEntry("byte", java.lang.Byte.class);
        addWrapperMapEntry("short", java.lang.Short.class);
    }
}
