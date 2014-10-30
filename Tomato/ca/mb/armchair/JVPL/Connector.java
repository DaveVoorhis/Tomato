/*
 * Connector.java
 *
 * Created on June 11, 2002, 2:55 AM
 */

package ca.mb.armchair.JVPL;

import java.lang.reflect.*;

/**
 * A Connector is a widget bound to a Visualiser that represents
 * some attribute or member of the class associated with the Visualiser.
 * 
 * Normally, you will not use this class directly, except perhaps when
 * creating custom Visualisers.
 *
 * @author  Dave Voorhis
 */
public class Connector extends javax.swing.JLabel {
    
    // Fonts
    private final static java.awt.Font ConnectorLabelFontBold = new java.awt.Font("sans-serif", java.awt.Font.BOLD, 8);
    private final static java.awt.Font ConnectorLabelFontPlain = new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 8);
    private final static java.awt.Font LabelFontBold = new java.awt.Font("sans-serif", java.awt.Font.BOLD, 9);
    private final static java.awt.Font LabelFontPlain = new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 9);
    
    // Sizes
    private final static int distance = 20;    // starting distance for connection extension
    
    // Visualiser to which this connector is bound
    private Visualiser visualiser;
    
    // Connector properties
    private String Name = "";
    private boolean Abstract = false;
    private boolean Final = false;
    private boolean Interface = false;
    private boolean Native = false;
    private boolean Private = false;
    private boolean Protected = false;
    private boolean Public = false;
    private boolean Static = false;
    private boolean Strict = false;
    private boolean Synchronized = false;
    private boolean Transient = false;
    private boolean Volatile = false;
    private boolean Constructor = false;
    private boolean Method = false;
    private boolean Field = false;
    private boolean Instance = false;
    private boolean CheckedException = false;
    
    // Connector attributes
    private Field theField = null;
    private Constructor theConstructor = null;
    private Method theMethod = null;
    private Class theInterface = null;
    private Class theInstance = null;
    private Object ProxyInstance = null;
    private Class[] theExceptionTypes = null;
    private long ID = -1;
    private long ConnectionCount = 0;
    private boolean Exposed = false;        // Merely a flag used by Visualiser
    private boolean DeclaredThisClass = false;  // Another flag used by Visualiser
    private int ParameterNumber = -1;       // used to resolve ambiguous connectors
    
    // Layout control
    public static final int EASTTOWEST = 0;
    public static final int WESTTOEAST = 1;
    private int LayoutDirection = -1;
    private static int nextLayoutDirection = EASTTOWEST;
    private int ExtensionLength = 10;
    
    // Connections
    private java.util.Vector ConnectionList = new java.util.Vector();

    /** Create an invisible, null connector */
    public Connector(Visualiser v) {
        Name = "";
        ConnectionCount = 0;
        visualiser = v;
        ID = visualiser.getNextConnectorID();
        setLayoutDirection();
        setOpaque(true);
        addConnector(" ");
        setVisible(false);
    }

    /** Create a connector for a given Method */
    public Connector(Visualiser v, Method m) {
        theMethod = m;
        Name = m.getName();
        ConnectionCount = 0;
        visualiser = v;
        ID = visualiser.getNextConnectorID();
        setLayoutDirection();
        setOpaque(true);
        Method = true;
        setModifiers(m.getModifiers());
        if (isPublic() || isStatic()) {
            setToolTipText(m.toString());
            setBorder(new javax.swing.border.EtchedBorder());
            addReturnType(m.getReturnType());
            theExceptionTypes = m.getExceptionTypes();
            addExceptionTypes(theExceptionTypes);
            if (isAbstract())
                addConnector("o", il8n._("Trigger") + " " + Name);
            else
                addConnector(".", il8n._("Method") + " " + Name);
            addConnectorLabel();
            addParameterTypes(m.getParameterTypes());
            configureMouse();
        }
    }
    
    /** Create a connector for a given Constructor */
    public Connector(Visualiser v, Constructor c) {
        theConstructor = c;
        Name = "<init>";
        ConnectionCount = 0;
        visualiser = v;
        ID = visualiser.getNextConnectorID();
        setLayoutDirection();
        setOpaque(true);
        Constructor = true;
        setModifiers(c.getModifiers());
        if (isPublic() || isStatic()) {
            setToolTipText(c.toString());
            setBorder(new javax.swing.border.EtchedBorder());
            theExceptionTypes = c.getExceptionTypes();
            addExceptionTypes(theExceptionTypes);
            addConnector("n", il8n._("Constructor"));
            addConnectorLabel();
            addParameterTypes(c.getParameterTypes());
            configureMouse();
        }
    }
    
    /** Create a connector for a given Field */
    public Connector(Visualiser v, Field f) {
        theField = f;
        Name = f.getName();
        ConnectionCount = 0;
        visualiser = v;
        ID = visualiser.getNextConnectorID();
        setLayoutDirection();
        setOpaque(true);
        Field = true;
        setModifiers(f.getModifiers());
        if (isPublic() || isStatic()) {
            setToolTipText(f.toString());
            setBorder(new javax.swing.border.EtchedBorder());
            addReturnType(f.getType());
            addConnector("-", il8n._("Field"));
            addConnectorLabel();
            configureMouse();
        }
    }
    
    /** Create a connector to a class or interface */
    public Connector(Visualiser v, Class c, boolean isInterface) {
        Name = c.getName();
        ConnectionCount = 0;
        visualiser = v;
        ID = visualiser.getNextConnectorID();
        setLayoutDirection();
        setOpaque(true);
        Public = true;
        setToolTipText(c.toString());
        setBorder(new javax.swing.border.EtchedBorder());
        if (isInterface) {
            theInterface = c;
            addConnector("I", il8n._("Interface"));
            addConnectorLabel();
            Interface = true;
        } else {
            theInstance = c;
            addConnector("C", il8n._("Class"));
            Instance = true;
            addConnectorLabel();
            configureMouse();
        }
    }

    /** Create a custom connector */
    public Connector(Visualiser v, Class c, String ConnectorKind, String Tip) {
        Name = c.getName();
        ConnectionCount = 0;
        visualiser = v;
        ID = visualiser.getNextConnectorID();
        setLayoutDirection();
        setOpaque(true);
        Public = true;
        setToolTipText(c.toString());
        setBorder(new javax.swing.border.EtchedBorder());
        Interface = false;
        theInstance = c;
        addConnector(ConnectorKind, Tip);
        Instance = true;
        addConnectorLabel();
        configureMouse();
    }
    
    /** Create an even more custom connector. */
    public Connector(String name, Visualiser v, Class c, String ConnectorKind, String Tip) {
        Name = name;
        ConnectionCount = 0;
        visualiser = v;
        ID = visualiser.getNextConnectorID();
        setLayoutDirection();
        setOpaque(true);
        Public = true;
        setToolTipText(c.toString());
        setBorder(new javax.swing.border.EtchedBorder());
        Interface = false;
        theInstance = c;
        addConnector(ConnectorKind, Tip);
        Instance = true;
        addConnectorLabel();
        configureMouse();
    }
    
    /** True if this connector represents a checked exception */
    public boolean isCheckedException() {
        return CheckedException;
    }

    /** Set by creators of Connector. */
    public void setCheckedException(boolean flag) {
        CheckedException = flag;
    }
    
    private boolean ValueGenerated = false;
    
    /** True if this connector returns a value.
     * Normally will be a VisualiserOfMessage's return value or thrown exception.
     */
    public boolean isValueGenerated() {
        return ValueGenerated;
    }
    
    private boolean ValueUsed = false;
    
    /** True if this connector uses a value.
     * Normally will be a VisualiserOfMessage's parameter or gate controller.
     */
    public boolean isValueUsed() {
        return ValueUsed;
    }
    
    /** Set by creators of Connector */
    void setValueGenerated(boolean flag) {
        ValueGenerated = flag;
    }
    
    /** Set by creators of Connector */
    void setValueUsed(boolean flag) {
        ValueUsed = flag;
    }
    
    /** Extend this connector to an appropriate new Visualiser via a Connection.
        If 'SuperExtend' is enabled, then the new Visualiser will automatically
        have its parameter connectors extended. */
    public void createConnectorExtensionVisualiser(boolean SuperExtend) {
        if (isInstance() && getConnectionCount()>0)     // no more than one connection for object references
            return;
        Model s = visualiser.getModel();
        if (s!=null) {
            Visualiser nV = null;
            if (isInstance()) {
                nV = VisualiserFactory.newVisualiser(null, getInstanceClass());    // object
                if (nV!=null)
                    nV.instantiate();
            } else {
                if (isAbstract()) {
                    Log.println("Connector: request to build an abstract (trigger) visualiser.");
                } if (isField()) {
                    Log.println("Connector: request to build a field visualiser.");
                } else {
                    nV = VisualiserFactory.newVisualiser();     // message
                    if (nV!=null)
                        if (isConstructor())
                            nV.setTitle(il8n._("New") + " " + getVisualiser().getName());
                        else if (getBoundClass()==null)
                            nV.setTitle(il8n._("Invoke") + " " + getVisualiser().getName());
                        else
                            nV.setTitle(il8n._("Invoke") + " " + getVisualiser().getName() + "." + getName());
                }
            }
            if (nV!=null) {
                nV.setLocation(getRecommendedNewVisualiserPoint(nV.getWidth())); // default position
                s.addVisualiser(nV);                                // add visualiser
                new Connection(s, this, nV);                        // hook it up with a connection
                if (SuperExtend && nV instanceof VisualiserOfMessage) {    // extend message visualiser, if desired 
                    VisualiserOfMessage vom = (VisualiserOfMessage)nV;
                    for (int i=0; i<vom.getParameterConnectorCount(); i++)
                        vom.getParameterConnector(i).createConnectorExtensionVisualiser(true);
                }
            }
            else
                Log.println("Connector: Unable to obtain a class for connector extension.");                
        }
    }
    
    /** Obtain suggested new message visualiser position, relative to Model,
        and offset (if necessary) by the new visualiser's width. */
    java.awt.Point getRecommendedNewVisualiserPoint(int NewVisualiserWidth) {
        return new java.awt.Point(getAttachmentX() +
                ((getLayoutDirection()==EASTTOWEST) ? -(distance + NewVisualiserWidth) : distance),
                getAttachmentY());
    }
    
    /** Obtain the runtime exception types that may be thrown by this connector */
    public Class[] getExceptionTypes() {
        return theExceptionTypes;
    }
    
    /** Get 'exposed' flag. */
    public boolean isExposed() {
        return Exposed;
    }
    
    /** Set 'exposed' flag.  You should not manipulate this flag directly!!!  Use expose() and unexpose() to
     expose and unexpose this connector. */
    void setExposed(boolean flag) {
        Exposed = flag;
    }
    
    /** Get 'DeclaredThisClass' flag */
    public boolean isDeclaredThisClass() {
        return DeclaredThisClass;
    }
    
    /** Set 'DeclaredThisClass' flag */
    public void setDeclaredThisClass(boolean flag) {
        DeclaredThisClass = flag;
    }
    
    /** 'ExtensionLength' value */
    public int getExtensionLength() {
        return ExtensionLength;
    }
    
    /** Set 'ExtensionLength' value */
    public void setExtensionLength(int v) {
        ExtensionLength = v;
    }
    
    /** Explicitly set layout direction */
    public void setLayoutDirection(int Direction) {
        LayoutDirection = Direction;
    }
    
    /** layout direction */
    public int getLayoutDirection() {
        return LayoutDirection;
    }
    
    /** Get this connector's visualiser */
    public Visualiser getVisualiser() {
        return visualiser;
    }
    
    /** Set this connector's parameter number. */
    public void setParameterNumber(int n) {
        ParameterNumber = n;
    }
    
    /** Get this connector's parameter number.  Return -1 if not set.
     *
     * Use getParameterNumber() in conjunction with getConnectorID() to
     * resolve to specific connectors when the ConnectorID is ambiguous. This
     * occurs (for example) on message visualisers with multiple 
     * parameters of the same type. 
     *
     */
    public int getParameterNumber() {
        return ParameterNumber;
    }
    
    /** The class that can be invoked by this connector.
        If null, it's attached to something special. */  
    public Class getBoundClass() {
        return visualiser.getBoundClass();
    }
    
    /** Set a proxy class to handle this connector's invocation. */
    public void setProxyInstance(Object p) {
        ProxyInstance = p;
    }
 
    /** Get the instance to which invocations should be sent. */
    public Object getInvocationTarget() {
        if (ProxyInstance != null)
            return ProxyInstance;
        if (getVisualiser()!=null)
            return getVisualiser().getInstance();
        return null;
    }
    
    /** Stringize */
    public String toString() {
        return  ((isConstructor()) ? getConstructor().toString() : "") +
                    ((isMethod()) ? getMethod().toString() : "") +
                    ((isField()) ? getField().toString() : "") +
                    ((isInstance()) ? getInstanceClass().toString() : "");
    }
    
    /** Return raw connector ID number */
    public long getID() {
        return ID;
    }
    
    /** Unexpose this Connector */
    public void unexpose() {
        getVisualiser().unexpose(this);
    }
    
    /** Expose this Connector */
    public void expose() {
        getVisualiser().expose(this);
    }
    
    /** Move this Connector to the other side of the Visualiser */
    public void switchSides() {
        getVisualiser().switchSides(this);
    }
    
    /** Change the ranking of this Connector in the Visualiser's display */
    public void changeRank(int n) {
        java.awt.Container container = this.getParent();
        container.remove(this);
        container.add(this, n);
        getVisualiser().updateVisualiser();
        redrawConnections();
        getVisualiser().getModel().refresh();
    }
    
    /** Redraw all Connections that reference this Connector */
    public void redrawConnections() {
        for (int i=0; i<getConnectionCount(); i++)
            getConnection(i).redraw();
    }
    
    /** Remove all Connections from this Connector */
    public void removeConnections() {
        while (getConnectionCount()>0) {
            Visualiser Source = getConnection(0).getVisualiser();
            if (Source!=null && Source.isVisualiserOfMessage())
                Source.getModel().removeVisualiser(Source);
            else
                getConnection(0).disconnect();
        }
        ConnectionList = new java.util.Vector();
    }
    
    /** Advise this connector that it's received a connection.  Do not invoke directly! */
    void addConnection(Connection c) {
        ConnectionList.add(c);
        getVisualiser().notifyConnectionAdded(this, c);
        getVisualiser().updateVisualiser();
    }
    
    /** Advise this connector that it's lost a connection.  Do not invoke directly! */
    void removeConnection(Connection c) {
        ConnectionList.remove(c);
        getVisualiser().notifyConnectionRemoved(this, c);
        getVisualiser().updateVisualiser();
    }
    
    /** How many connections are there to this connector? */
    public long getConnectionCount() {
        return ConnectionList.size();
    }
    
    /** Get the i'th connection */
    public Connection getConnection(int i) {
        try {
            return (Connection)ConnectionList.get(i);
        } catch (java.lang.ArrayIndexOutOfBoundsException j) {
            return null;
        }
    }
    
    /** Return the connector ID, which is used to identify connectors on
     * a Visualiser.
     *
     * Use getParameterNumber() in conjunction with getConnectorID() to
     * resolve to specific connectors when the ConnectorID is ambiguous. This
     * occurs (for example) on message visualisers with multiple 
     * parameters of the same type. 
     *
     */
    public String getConnectorID() {
        return toString();
    }
    
    /** Get the Connector's name */
    public String getName() {
        return Name;
    }
    
    // Return connector modifiers
    public boolean isAbstract() {
        return Abstract;
    }
    
    public boolean isFinal() {
        return Final;
    }
    
    public boolean isInterface() {
        return Interface;
    }
    
    public Class getInterface() {
        return theInterface;
    }
    
    public boolean isNative() {
        return Native;
    }
    
    public boolean isPrivate() {
        return Private;
    }
    
    public boolean isProtected() {
        return Protected;
    }
    
    public boolean isPublic() {
        return Public;
    }
    
    public boolean isStatic() {
        return Static;
    }
    
    public boolean isStrict() {
        return Strict;
    }
    
    public boolean isSynchronized() {
        return Synchronized;
    }
    
    public boolean isTransient() {
        return Transient;
    }
    
    public boolean isVolatile() {
        return Volatile;
    }
    
    public boolean isConstructor() {
        return Constructor;
    }
    
    public Constructor getConstructor() {
        return theConstructor;
    }
    
    public boolean isMethod() {
        return Method;
    }
    
    public Method getMethod() {
        return theMethod;
    }
    
    public boolean isField() {
        return Field;
    }
    
    public Field getField() {
        return theField;
    }
    
    public boolean isInstance() {
        return Instance;
    }
    
    public Class getInstanceClass() {
        return theInstance;
    }
    
    /** get attachment point in Model coordinates */
    public int getAttachmentX() {
        if (getLayoutDirection()==EASTTOWEST)
            return getParent().getParent().getX() + getParent().getX() + getX();
        else
            return getParent().getParent().getX() + getParent().getX() + getX() + getWidth();
    }
    
    /** get attachment point in Model coordinates */
    public int getAttachmentY() {
        return getParent().getParent().getY() + getParent().getY() + getY() + getHeight() / 2;
    }
    
    // Set up label
    private void addConnectorLabel() {
        if (isDeclaredThisClass())
            setFont(LabelFontBold);
        else
            setFont(LabelFontPlain);
        setText(getText() + getName());
    }
    
    /** implement a connector with associated tip */
    protected void addConnector(String ConnectorKind, String Tip) {
        setText(getText() + ConnectorKind);
        if (Tip.length()>0)
            setToolTipText(Tip);
    }
    
    /** implement a connector without a tip */
    protected void addConnector(String ConnectorKind) {
        addConnector(ConnectorKind, "");
    }
    
    /** implement a return type */
    protected void addReturnType(Class Type) {
        String ClassName = Library.getClassName(Type);
        if (ClassName!="void")
            setText(getText() + "R");
    }
    
    /** implement exception types */
    protected void addExceptionTypes(Class c[]) {
        if (c!=null && c.length>0)
            setText(getText() + "!");
    }
    
    /** implement parameter types */
    protected void addParameterTypes(Class c[]) {
        if (c!=null && c.length>0)
            setText(getText() + "()");
    }
    
    // Set up modifiers
    private void setModifiers(int ModifierInt) {
        Abstract = java.lang.reflect.Modifier.isAbstract(ModifierInt);
        Final = java.lang.reflect.Modifier.isFinal(ModifierInt);
        Interface = java.lang.reflect.Modifier.isInterface(ModifierInt);
        Native = java.lang.reflect.Modifier.isNative(ModifierInt);
        Private = java.lang.reflect.Modifier.isPrivate(ModifierInt);
        Protected = java.lang.reflect.Modifier.isProtected(ModifierInt);
        Public = java.lang.reflect.Modifier.isPublic(ModifierInt);
        Static = java.lang.reflect.Modifier.isStatic(ModifierInt);
        Strict = java.lang.reflect.Modifier.isStrict(ModifierInt);
        Synchronized = java.lang.reflect.Modifier.isSynchronized(ModifierInt);
        Transient = java.lang.reflect.Modifier.isTransient(ModifierInt);
        Volatile = java.lang.reflect.Modifier.isVolatile(ModifierInt);
    }
  
    // mouse handler -- creates a new message visualiser
    private void doMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getButton()==1)
            createConnectorExtensionVisualiser(true);
        else
            visualiser.connectorForwardsClick(this, evt);
    }
    
    // Set up mouse handler
    private void configureMouse() {
        for (int i=0; i<this.getComponentCount(); i++)
            getComponent(i).addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    doMouseClicked(evt);
                }
            });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                doMouseClicked(evt);
            }
        });
    }
    
    // Obtain default layout direction
    private void setLayoutDirection() {
        LayoutDirection = nextLayoutDirection;
        if (nextLayoutDirection==EASTTOWEST)
            nextLayoutDirection=WESTTOEAST;
        else
            nextLayoutDirection=EASTTOWEST;
    }
}
