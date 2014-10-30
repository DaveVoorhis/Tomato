/*
 * Visualiser.java
 *
 * Created on June 9, 2002, 1:28 AM
 */

package ca.mb.armchair.JVPL;

import java.lang.reflect.*;

/**
 * General-purpose class Visualiser.
 *
 * @author  Dave Voorhis
 */
public class VisualiserOfClass extends Visualiser {
    
    // class wrapped by this visualiser
    private Class theClass = null;
    
    /** Creates a new visualiser for a class. */
    public VisualiserOfClass() {
    }

    /** Obtain Java source equivalent to this Visualiser. */
    public String getJavaSource(ModelToJava compiler) {
        String varClassName = getBoundClass().getName();
        String vars = compiler.beginLine() + getModifierString() + " " + varClassName + " " + compiler.getIdentifier(getName());
        switch (getSerializationMode()) {
            case Visualiser.SERIALIZE_AUTOINSTANTIATE:
                vars += " = new " + varClassName + "()";
                break;
            case Visualiser.SERIALIZE_XML:
            case Visualiser.SERIALIZE_BINARY:
                if (getPrimitiveInitialisation()!=null)
                    if (getInstance()!=null)
                        vars += " = " + getPrimitiveInitialisation();
                    else
                        vars += " = null";
                else {
                    String s = VisualiserSerializable.getSerializedInstance(getSerializationMode(), getInstance());
                    if (s==null)
                        vars += " = null";
                    else
                        vars += " = (" + varClassName + ")" + 
                                "ca.mb.armchair.JVPL.VisualiserSerializable.getDeserializedInstance(" + 
                                getSerializationMode() + ", " + "\"" + s + "\")";
                }
                break;
            case Visualiser.SERIALIZE_NULL:
                vars += " = null";
                break;
        }
        vars += ";";
        return vars;
    }
    
    /* Obtain the class represented by this Visualiser */
    /* Null if the class wasn't found. */
    public Class getBoundClass() {
        return theClass;
    }
    
    /** Build a class Visualiser. */
    public void setBoundClass(Class aClass) {
        if (aClass == null) {
            Log.println("VisualiserOfClass: Null passed to setBoundClass");
        } else {
            theClass = aClass;
            setTitle(Library.getClassName(theClass));
            addConstructors(theClass.getConstructors());
            addMethods(theClass.getMethods(), false);
            addFields(theClass.getFields(), false);
            addArrayVisualiserMethods();
        }
    }

    /** Array 'get'. */
    public java.lang.Object get(Integer i) {
        return java.lang.reflect.Array.get(getInstance(), i.intValue());
    }
    
    /** Array 'set'. */
    public void set(java.lang.Object o, Integer i) {
        java.lang.reflect.Array.set(getInstance(), i.intValue(), o);
    }
    
    /** Array 'length'. */
    public int length() {
        return java.lang.reflect.Array.getLength(getInstance());
    }
    
    /** Add a visualiser method, i.e., a method bound to a class's visualiser,
     * rather than to the class itself.  Return false if it
     * couldn't be added because it wasn't found. */
    protected boolean addVisualiserMethod(String methodName, Class parms[]) {
        Method m = null;
        try {
            m = getClass().getMethod(methodName, parms);
        } catch (java.lang.NoSuchMethodException e) {
            return false;
        }
        Connector c = new Connector(this, m);
        c.setLayoutDirection(Connector.EASTTOWEST);
        c.setProxyInstance(this);
        addConnector(c, false);
        return true;
    }
    
    // Add array access methods
    private void addArrayVisualiserMethods() {
        if (!theClass.isArray())
            return;
        Class getParms[] = {Integer.class};
        if (!addVisualiserMethod("get", getParms))
            Log.println("VisualiserOfClass: Could not find get(Integer)");
        Class setParms[] = {java.lang.Object.class, Integer.class};
        if (!addVisualiserMethod("set", setParms))
            Log.println("VisualiserOfClass: Could not find set(java.lang.Object, Integer)");
        if (!addVisualiserMethod("length", null))
            Log.println("VisualiserOfClass: Could not find length()");
    }
    
    // add methods
    private void addMethods(Method m[], boolean isDeclaredThisClass) {
        if (m==null)
            return;
        for (int i = 0; i < m.length; i++) {
            Connector c = new Connector(this, m[i]);
            if (c.isPublic()) {
                c.setDeclaredThisClass(isDeclaredThisClass);
                addConnector(c, false);
            }
        }
    }
    
    // add field types
    private void addFields(Field f[], boolean isDeclaredThisClass) {
        if (f==null)
            return;
        for (int i = 0; i < f.length; i++) {
            Connector c = new Connector(this, f[i]);
            if (c.isPublic()) {
                c.setDeclaredThisClass(isDeclaredThisClass);
                addConnector(c, false);
            }
        }
    }
    
    // add constructor parameter types to the class set
    private void addConstructors(Constructor constructors[]) {
        if (constructors==null)
            return;
        for (int i=0; i<constructors.length; i++) {
            Connector c = new Connector(this, constructors[i]);
            if (c.isPublic())
                addConnector(c, false);
        }
    }

}
