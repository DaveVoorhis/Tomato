/*
 * VisualiserOfMessage.java
 *
 * Created on June 24, 2002, 10:25 PM
 */

package ca.mb.armchair.JVPL;

import java.lang.reflect.*;

/**
 * A message Visualiser provides a visual representation of a
 * method or constructor invocation.
 *
 * @author  Dave Voorhis
 */
public class VisualiserOfMessage extends Visualiser implements java.awt.event.ActionListener {
    
    private final java.awt.Color InvokePukedColor = new java.awt.Color(255, 75, 75);
    private final java.awt.Color InvokeOkColor = new java.awt.Color(75, 255, 75);
    
    private final String InvokeIconFile = "/ca/mb/armchair/JVPL/resources/PlayIconTiny.png";
    private final String RepeatIconFile = "/ca/mb/armchair/JVPL/resources/RepeatIconTiny.png";
    private final int ButtonWidth = 18;
    private final int ButtonHeight = 18;
    
    // Stuff
    private java.util.Vector Invocables = new java.util.Vector();
    private java.util.Vector ParameterConnectors = new java.util.Vector();
    private java.util.Vector ThrowableConnectors = new java.util.Vector();
    private Connector ConnectorToReturnType = null;
    private Connector ConnectorToDefaultThrowable = null;
    private Connector ConnectorToGate = null;
    private boolean InvokesAfterParmChange = false;
    private boolean InvokesBeforeReturnUse = false;
    private boolean InvokesAfterReferenceChange = false;
    private boolean InvokesBeforeReferenceUse = false;
    private javax.swing.JButton jButtonInvoke;
    private javax.swing.ImageIcon InvokeIcon = null;
    private javax.swing.ImageIcon RepeatIcon = null;
    
    /** Creates a new instance of a thing that can invoke a method.
     */
    public VisualiserOfMessage() {
        setModifierString("public");
        addInvoke();
    }
    
    /** Return true if a given visualiser can be dropped on this one, with something
     * good possibly taking place thereafter via a receiveDrop() operation. */
    public boolean isDropCandidateFor(Visualiser DraggedVisualiser) {
        return (this != DraggedVisualiser && 
                    getExposedConnectorCount()==0 &&
                    DraggedVisualiser.isVisualiserOfMessage() &&
                    DraggedVisualiser.getExposedConnectorCount()==0);
    }
    
    // Return true if invocation must be gated because it accesses get/set wrapped Visualisers. */
    private boolean invokeRequiresGate(ModelToJava compiler) {
        // Check return type
        if (getConnectorToReturnType()!=null)
            if (getConnectorToReturnType().getConnectionCount()>0) {
                Visualiser VR = getConnectorToReturnType().getConnection(0).getVisualiser();
                if (compiler.isGatedSetAccess(VR))
                    return true;
            } else
                return false;
        // Check 'count'
        if (getConnectorToCount()!=null)
            if (getConnectorToCount().getConnectionCount()>0) {
                Visualiser VC = getConnectorToCount().getConnection(0).getVisualiser();
                if (compiler.isGatedAccess(VC))
                    return true;
            }
        // Check 'gate'
        if (getConnectorToGate()!=null)
            if (getConnectorToGate().getConnectionCount()>0) {
                Visualiser VG = getConnectorToGate().getConnection(0).getVisualiser();
                if (compiler.isGatedGetAccess(VG))
                    return true;
            }
        // Check connections
        for (int j=0; j<getConnectionCount(); j++) {
            Connector c = getConnection(j).getConnector();
            Visualiser target = c.getVisualiser();
            if (c.isConstructor() && compiler.isGatedSetAccess(target))
                return true;
            else if (c.isMethod()) {
                if (!c.getVisualiser().isVisualiserOfMessage() && !c.isStatic() && compiler.isGatedGetAccess(target))
                    return true;
            }
        }
        // Check throwables
        for (int j=getThrowableConnectorCount()-1; j>=-1; j--) {
            Connector x = (j==-1) ? getConnectorToDefaultThrowable() : getThrowableConnector(j);
            if (x != null) {
                Connection c = x.getConnection(0);
                if (c != null && compiler.isGatedSetAccess(c.getVisualiser()))
                    return true;
            }
        }
        return false;
    }
    
    // Obtain Java source equivalent to invokeConnector.
    private String getJavaSourceForInvoke(ModelToJava compiler, Connector c) {
        String methodBody = "";
        Visualiser target = c.getVisualiser();
        String targetIdentifier = compiler.getIdentifier(target.getName());
        // Handle constructor.
        if (c.isConstructor()) {
            String newinstance = "new " + target.getBoundClass().getName() +
            "(" + compiler.buildParms(c.getConstructor().getParameterTypes(), this) + ")";
            methodBody += compiler.beginLine() + compiler.getAssignment(target, newinstance);
        }
        // Handle method
        else if (c.isMethod()) {
            String methodInvoke = "";
            if (c.getVisualiser().isVisualiserOfMessage()) {
                methodInvoke += targetIdentifier;
            } else {
                if (c.isStatic())
                    methodInvoke += target.getBoundClass().getName();
                else
                    methodInvoke += compiler.getDereference(target);
                methodInvoke += "." + c.getMethod().getName();
            }
            methodInvoke += "(" + compiler.buildParms(c.getMethod().getParameterTypes(), this) + ")";
            if (getConnectorToReturnType()!=null)
                if (getConnectorToReturnType().getConnectionCount()>0) {
                    Visualiser VR = getConnectorToReturnType().getConnection(0).getVisualiser();
                    if (c.getMethod().getReturnType().isPrimitive() && VR.getPrimitiveName()!=null)
                        methodInvoke = "new " + VR.getBoundClass().getName() + "(" + methodInvoke + ")";
                    methodBody += compiler.beginLine() + compiler.getAssignment(VR, ModelToJava.getCast(VR, target) + methodInvoke);
                } else
                        methodBody += compiler.beginLine() + "return " + methodInvoke + ";";
            else
                methodBody += compiler.beginLine() + methodInvoke + ";";
        } else
            methodBody += compiler.beginLine() + "// Don't know how to compile non-method or non-constructor!";
        return methodBody;
    }
    
    /** Obtain java source for method body */
    private String getJavaSourceForMethodBody(ModelToJava compiler, boolean throwables) {
        String methodBody = "";
        // Construct method gate or loop
        boolean usesFlowControl = false;
        if (isGated() || isRepeated()) {
            Connection connectionToGate = getConnectorToGate().getConnection(0);
            if (connectionToGate!=null && connectionToGate.getVisualiser()!=null) {
                Visualiser Gate = connectionToGate.getVisualiser();
                if (isRepeated())
                    methodBody += compiler.beginLine() + "while (";
                else
                    methodBody += compiler.beginLine() + "if (";
                methodBody += compiler.getDereference(Gate) +  ".booleanValue())" + compiler.beginBlock();
                usesFlowControl = true;
            } else if (isRepeated()) {
                methodBody += compiler.beginLine() + "while (true)" + compiler.beginBlock();
                usesFlowControl = true;
            }
        }
        // Build code to handle 'count' update.  Used later.
        String countCode = "";
        if (isCounted()) {
            Connection connectionToCounter = getConnectorToCount().getConnection(0);
            if (connectionToCounter!=null && connectionToCounter.getVisualiser()!=null) {
                Visualiser Counter = connectionToCounter.getVisualiser();
                String reInstantiate = "new java.lang.Long(" + compiler.getDereference(Counter) + ".longValue() + 1)";
                countCode = compiler.getAssignment(Counter, reInstantiate);
                if (getConnectionCount()>0)
                    methodBody += compiler.beginLine() + "try" + compiler.beginBlock();
            }
        }
        // Handle 'throw' side of exception trapping.
        if (throwables)
            methodBody += compiler.beginLine() + "try" + compiler.beginBlock();
        // For each connection...
        for (int j=0; j<getConnectionCount(); j++)
            methodBody += getJavaSourceForInvoke(compiler, getConnection(j).getConnector());
        // Handle 'catch' side of thrown exceptions.
        if (throwables) {
            methodBody += compiler.endBlock();
            for (int j=getThrowableConnectorCount()-1; j>=-1; j--) {
                Connection x = (j==-1) ? getConnectorToDefaultThrowable().getConnection(0) :
                                        getThrowableConnector(j).getConnection(0);
                if (x != null) {
                    Visualiser throwable = x.getVisualiser();
                    String var = "t" + (j + 1);
                    methodBody += " catch (" + throwable.getBoundClass().getName() + " " + var + ")" + compiler.beginBlock();
                    methodBody += compiler.beginLine() + compiler.getAssignment(throwable, var);
                    methodBody += compiler.endBlock();
                }
            }
        }
        // Invocation counter
        if (countCode.length()>0) {
            if (getConnectionCount()>0) {
                methodBody += compiler.endBlock() + "finally" + compiler.beginBlock();
                methodBody += compiler.beginLine() + countCode + compiler.endBlock();
            } else
                methodBody += compiler.beginLine() + countCode;
        }
        // Terminate flow control block, if necessary.
        if (usesFlowControl)
            methodBody += compiler.endBlock();
        return methodBody;
    }
    
    /** Obtain Java source equivalent to this Visualiser. */
    public String getJavaSource(ModelToJava compiler) {
        String methodName = compiler.getIdentifier(getName());
        String methodDeclaration = getModifierString() + " ";
        if (getConnectorToReturnType()==null || getConnectorToReturnType().getConnectionCount()>0)
            methodDeclaration += "void ";
        else
            methodDeclaration += getConnectorToReturnType().getInstanceClass().getName() + " ";
        methodDeclaration += methodName;
        String parms = "";
        for (int p=0; p<getParameterConnectorCount(); p++)
            if (getParameterConnector(p)!=null && getParameterConnector(p).getConnectionCount()==0) {
                if (parms.length()>0)
                    parms += ", ";
                parms += getParameterConnector(p).getInstanceClass().getName() + " p" + p;
            }
        methodDeclaration += "(" + parms + ")";
        // Any throwables?
        String unhandledExceptions = "";
        boolean throwables = false;
        for (int j=0; j<getThrowableConnectorCount(); j++) {
            Connector throwableConnector = getThrowableConnector(j);
            if (throwableConnector.getConnection(0)!=null)
                throwables = true;
            else
                if (throwableConnector.isCheckedException()) {
                    if (unhandledExceptions.length()>0)
                        unhandledExceptions += ", ";
                    unhandledExceptions += getThrowableConnector(j).getInstanceClass().getName();
                }
        }
        if (getConnectorToDefaultThrowable()!=null && getConnectorToDefaultThrowable().getConnection(0)!=null)
            throwables = true;
        if (unhandledExceptions.length()>0)
            methodDeclaration += " throws " + unhandledExceptions.trim();

        // Gating to prevent (?) infinite recursion.  Currently, rather brute-force.
        // Later on, use clever algorithms to determine which methods need to be
        // gated and which do not.
        String methods = "";
        if (invokeRequiresGate(compiler)) {
            String uniqueMethodName = compiler.getIdentifier(compiler.getUniqueVisualiserName(this));
            methods += compiler.beginLine() + "private boolean gate" + uniqueMethodName + " = false;";
            methods += compiler.beginLine() + methodDeclaration + compiler.beginBlock();
            methods += compiler.beginLine() + "if (gate" + uniqueMethodName + ")" + compiler.beginBlock();
            methods += compiler.beginLine() + "return;" + compiler.endBlock();
            methods += compiler.beginLine() + "try" + compiler.beginBlock();
            methods += compiler.beginLine() + "gate" + uniqueMethodName + " = true;";
            methods += getJavaSourceForMethodBody(compiler, throwables);
            methods += compiler.endBlock() + "finally" + compiler.beginBlock();
            methods += compiler.beginLine() + "gate" + uniqueMethodName + " = false;";
            methods += compiler.endBlock();
        } else
            methods += compiler.beginLine() + methodDeclaration + compiler.beginBlock() + 
                            getJavaSourceForMethodBody(compiler, throwables);
        methods += compiler.endBlock();
        
        return methods;
    }
    
    /** Populate custom section. */
    protected void populateCustom() {
        jButtonInvoke = new javax.swing.JButton();
        jButtonInvoke.setBounds(0, 0, ButtonWidth, ButtonHeight);
        jButtonInvoke.setMinimumSize(new java.awt.Dimension(ButtonWidth, ButtonHeight));
        jButtonInvoke.addActionListener(this);
        jButtonInvoke.setEnabled(false);
        setButtonIcon();
        add(jButtonInvoke, java.awt.BorderLayout.CENTER);
    }
    
    // Set appropriate icon on button.
    private void setButtonIcon() {
        if (InvokeIcon==null)
            InvokeIcon = new javax.swing.ImageIcon(getClass().getResource(InvokeIconFile));
        if (RepeatIcon==null)
            RepeatIcon = new javax.swing.ImageIcon(getClass().getResource(RepeatIconFile));
        if (isRepeated())
            jButtonInvoke.setIcon(RepeatIcon);
        else
            jButtonInvoke.setIcon(InvokeIcon);
    }
    
    /** Add 'only invoke if true' parameter-like connector. */
    public void setGated(boolean enabled) {
        if (enabled) {
            if (ConnectorToGate != null)
                return;
            Connector c = new Connector("Gate", this, java.lang.Boolean.class, "?",
            il8n._("Gate (java.lang.Boolean to control invocation)"));
            c.setValueUsed(true);
            c.setLayoutDirection(Connector.EASTTOWEST);
            c.setParameterNumber(10000);
            ConnectorToGate = c;
            addConnector(c, true);
        } else
            if (ConnectorToGate != null) {
                ConnectorToGate.removeConnections();
                ConnectorToGate.unexpose();
                ConnectorToGate = null;
            }
    }
    
    /** True if gated. */
    public boolean isGated() {
        return (ConnectorToGate != null);
    }
    
    /** Get gate connector.  Null if not gated. */
    public Connector getConnectorToGate() {
        return ConnectorToGate;
    }
    
    private Connector ConnectorToCount = null;
    
    /** Add 'count' return-like connector. */
    public void setCounted(boolean enabled) {
        if (enabled) {
            if (ConnectorToCount != null)
                return;
            Connector c = new Connector("Count", this, java.lang.Long.class, "#",
            il8n._("Count (java.lang.Long to hold invocation count)"));
            c.setValueGenerated(true);
            c.setLayoutDirection(Connector.EASTTOWEST);
            c.setParameterNumber(10001);
            ConnectorToCount = c;
            addConnector(c, true);
        } else
            if (ConnectorToCount != null) {
                ConnectorToCount.removeConnections();
                ConnectorToCount.unexpose();
                ConnectorToCount = null;
            }
    }
    
    /** True if counted. */
    public boolean isCounted() {
        return (ConnectorToCount != null);
    }
    
    /** Get count connector.  Null if not counted. */
    public Connector getConnectorToCount() {
        return ConnectorToCount;
    }
    
    private boolean Repeated = false;       // true if repeating
    
    /** Set repeating. */
    public void setRepeated(boolean enabled) {
        Repeated = enabled;
        setButtonIcon();
    }
    
    /** True if repeated. */
    public boolean isRepeated() {
        return Repeated;
    }
    
    /** True to execute when referenced Visualiser's instance changes. */
    public void setInvokesAfterReferenceChange(boolean enabled) {
        InvokesAfterReferenceChange = enabled;
    }
    
    /** True if executes when referenced Visualiser's instance changes. */
    public boolean isInvokesAfterReferenceChange() {
        return InvokesAfterReferenceChange;
    }
    
    /** True to execute before referenced Visualiser's instance is used. */
    public void setInvokesBeforeReferenceUse(boolean enabled) {
        InvokesBeforeReferenceUse = enabled;
    }
    
    /** True if executes when referenced Visualiser's instance changes. */
    public boolean isInvokesBeforeReferenceUse() {
        return InvokesBeforeReferenceUse;
    }
    
    // Add invocation connector.  Sets the instance of this visualiser
    // to this visualiser.  Thus, it can be sent a 'void invoke()' message just like
    // any other visualiser with methods.  Proxy, this is.
    private void addInvoke() {
        Method m;
        try {
            // Find our own invoke method
            m = getClass().getMethod("invoke", null);
        } catch (java.lang.NoSuchMethodException e) {
            m = null;
            Log.println("VisualiserOfMessage: Could not find 'void invoke()':" + e.toString());
            Log.printStackTrace(e.getStackTrace());
        }
        if (m!=null) {
            Connector c = new Connector(this, m);
            c.setLayoutDirection(Connector.EASTTOWEST);
            addConnector(c, false);
            c.setProxyInstance(this);
        }
    }
    
    // Attach this VisualiserOfMessage to a given (other Visualiser's) connector.
    private void addConnector(Connector c) {
        Invocables.add(c);
        if (Invocables.size()==1) {         // Add these once, the first time.
            if (c.isConstructor()) {        // addConnector only invoked multiple times under drag-'n'-drop
                addConstructor(c.getConstructor());
                addThrowables(c.getExceptionTypes());
                addThrowable(java.lang.NullPointerException.class, false, false);
            }
            else if (c.isMethod()) {
                addMethod(c.getMethod());
                addReturnValue(c.getMethod());
                addThrowables(c.getExceptionTypes());
                addThrowable(java.lang.NullPointerException.class, false, false);
            }
            else if (c.isField())
                addField(c.getField());
        }
        setInvocable();
    }
    
    // Remove a connector from the list of invocables
    private void removeConnector(Connector c) {
        Invocables.remove(c);
    }
    
    /** Obtain count of parameter Connectors. */
    public int getParameterConnectorCount() {
        return ParameterConnectors.size();
    }
    
    /** Obtain the ith parameter Connector. */
    public Connector getParameterConnector(int i) {
        return (Connector)ParameterConnectors.get(i);
    }
    
    /** Obtain the return value Connector.  Will be 'null' on void methods and constructors. */
    public Connector getConnectorToReturnType() {
        return ConnectorToReturnType;
    }
    
    /** Obtain count of throwable Connectors. */
    public int getThrowableConnectorCount() {
        return ThrowableConnectors.size();
    }
    
    /** Obtain the ith throwable Connector. */
    public Connector getThrowableConnector(int i) {
        return (Connector)ThrowableConnectors.get(i);
    }
    
    /** Obtain the default throwable Connector. */
    public Connector getConnectorToDefaultThrowable() {
        return (Connector)ConnectorToDefaultThrowable;
    }
    
    /** Update Visualiser as a result of some change in state. */
    public void updateVisualiser() {
        setInvocable();
    }
    
    private boolean Invocable = false;
    
    // If all requirements are met, enable invocation.  Otherwise, disable it.
    private void setInvocable() {
        Invocable = false;
        // All parms filled?
        int ConnectedParms = 0;
        for (int i=0; i<getParameterConnectorCount(); i++) {
            Connector x = getParameterConnector(i);
            Connection c = x.getConnection(0);
            if (c!=null && c.getVisualiser()!=null)  // should check for primitive parm vs. null here...
                ConnectedParms++;
        }
        // All checked exceptions handled?
        boolean unHandledCheckedExceptionsExist = false;
        for (int i=0; i<getThrowableConnectorCount(); i++) {
            Connector x = getThrowableConnector(i);
            if (x.isCheckedException()) {
                Connection c = x.getConnection(0);
                if (c==null || c.getVisualiser()==null) {
                    unHandledCheckedExceptionsExist = true;
                    break;
                }
            }
        }
        Invocable = (ConnectedParms==getParameterConnectorCount() && !unHandledCheckedExceptionsExist);
        jButtonInvoke.setEnabled(Invocable);
    }
    
    /** Return true if it's invocable. */
    public boolean isInvocable() {
        return Invocable;
    }
    
    /** Set 'auto-invoke after parm instance change' feature. */
    public void setInvokesAfterParmChange(boolean f) {
        InvokesAfterParmChange = f;
    }
    
    /** True if 'auto-invoke after parm instance change' is enabled. */
    public boolean isInvokesAfterParmChange() {
        return InvokesAfterParmChange;
    }
    
    /** Set 'auto-invoke before return value useage' feature. */
    public void setInvokesBeforeReturnUse(boolean f) {
        InvokesBeforeReturnUse = f;
    }
    
    /** True if 'auto-invoke after parm instance change' is enabled. */
    public boolean isInvokesBeforeReturnUse() {
        return InvokesBeforeReturnUse;
    }
    
    // this is invoked by a connected visualiser when it transitions from
    // one instance to another, or when it transitions from null to
    // an instance, or when it turns null.
    void notifyInstantiation(Visualiser v) {
        super.notifyInstantiation(v);
        setInvocable();
    }
    
    // this is invoked by a connector when it receives a new connection
    void notifyConnectionAdded(Connector x, Connection c) {
        super.notifyConnectionAdded(x, c);
        setInvocable();
    }
    
    // this is invoked by a connector when it loses a connection
    void notifyConnectionRemoved(Connector x, Connection c) {
        super.notifyConnectionRemoved(x, c);
        setInvocable();
    }
    
    // Let visualiser know it got a new reference connection
    void addConnection(Connection c) {
        super.addConnection(c);
        addConnector(c.getConnector());
        setInvocable();
    }
    
    // Let visualiser know it lost a reference connection
    void removeConnection(Connection c) {
        super.removeConnection(c);
        removeConnector(c.getConnector());
        setInvocable();
    }
    
    /** True if this visualiser is owned by its connector, i.e., it is a message
     * visualiser and should be deleted if its connector is deleted. */
    public boolean isVisualiserOfMessage() {
        return true;
    }
    
    // Send a value down a given connection, via a connector.
    private void sendData(Connector connector, Object data) {
        if (connector==null)
            return;
        Connection c = connector.getConnection(0);
        if (c!=null && c.getVisualiser()!=null && c.getVisualiser().getBoundClass().isAssignableFrom(data.getClass())) {
            c.getVisualiser().setInstance(data);
            c.pulse();
        }
    }
    
    // Used by 'invoke' to shove thrown exceptions down appropriate connectors
    private void assignThrowableToConnector(Throwable t) {
        for (int i=0; i<getThrowableConnectorCount(); i++)  // specific exceptions to specific connectors
            sendData(getThrowableConnector(i), t);
        sendData(ConnectorToDefaultThrowable, t);           // any exception also goes here
    }
    
    // Invoke a connector
    private void invokeConnector(Connection viaConnection, Connector connector) {
        viaConnection.pulse();
        if (connector.isConstructor()) {                    // constructor
            try {
                Object instance = connector.getConstructor().newInstance(buildArguments());
                pulse(InvokeOkColor);
                connector.getVisualiser().setInstance(instance);
            } catch (java.lang.IllegalAccessException e1) {
                Log.println("VisualiserOfMessage: constructor failed: " + e1.toString());
            } catch (java.lang.IllegalArgumentException e2) {
                Log.println("VisualiserOfMessage: constructor failed: " + e2.toString());
            } catch (java.lang.reflect.InvocationTargetException e3) {
                Log.println("VisualiserOfMessage: constructor failed: " + e3.toString());
            } catch (java.lang.InstantiationException e4) {
                Log.println("VisualiserOfMessage: constructor failed: " + e4.toString());
            } catch (java.lang.ExceptionInInitializerError e5) {
                Log.println("VisualiserOfMessage: constructor failed: " + e5.toString());
            } catch (Throwable t) {
                assignThrowableToConnector(t);
                pulse(InvokePukedColor);
            }
        }
        else if (connector.isMethod()) {                    // method
            Method method = connector.getMethod();
            try {
                if (!connector.isStatic())
                    connector.getVisualiser().useInstance();
                Object retval = method.invoke(connector.getInvocationTarget(), buildArguments());
                pulse(InvokeOkColor);
                sendData(ConnectorToReturnType, retval);
            } catch (java.lang.IllegalAccessException e1) {
                Log.println("VisualiserOfMessage: method invocation failed: " + e1.toString());
            } catch (java.lang.IllegalArgumentException e2) {
                Log.println("VisualiserOfMessage: method invocation failed: " + e2.toString());
            } catch (java.lang.reflect.InvocationTargetException e3) {
                Log.println("VisualiserOfMessage: method invocation failed: " + e3.toString());
            } catch (Throwable t) {
                assignThrowableToConnector(t);
                pulse(InvokePukedColor);
            }
            connector.getVisualiser().updateVisualiser();
        }
        else if (connector.isField()) {                     // field
            Log.println("VisualiserOfMessage: Field invocation not yet supported.");
        }
    }
    
    // Internal invoke.  Iterates through reference visualiser connections, invoking
    // as needed.
    private void invokeConnections() {
        for (int i=0; i<Invocables.size(); i++)
            invokeConnector(getConnection(i), (Connector)Invocables.get(i));
        if (isCounted()) {
            Connection c = ConnectorToCount.getConnection(0);
            if (c!=null && c.getVisualiser()!=null) {
                Object instance = c.getVisualiser().useInstance();
                c.pulse();
                if (instance == null)
                    c.getVisualiser().setInstance(new java.lang.Long(1));
                else if (instance instanceof java.lang.Long)
                    c.getVisualiser().setInstance(new Long(((Long)instance).longValue() + 1));
            }
        }
    }
    
    // Is gate open?
    private boolean isGateOpen() {
        if (isGated()) {
            Connection c = ConnectorToGate.getConnection(0);
            if (c!=null && c.getVisualiser()!=null) {
                Object instance = c.getVisualiser().useInstance();
                if (instance instanceof java.lang.Boolean) {
                    c.pulse();
                    if (!((Boolean)instance).booleanValue())
                        return false;
                }
            }
        }
        return true;
    }
    
    /** Flag to prevent infinite self-invocation loops */
    private boolean invoking = false;
    
    /** Invoke reference visualisers via their connectors, which will always be methods or
     * constructors. */
    public void invoke() {
        if (!isInvocable() || invoking)
            return;
        boolean ButtonState = jButtonInvoke.isEnabled();
        jButtonInvoke.setEnabled(false);
        invoking = true;
        if (Repeated)
            while (isGateOpen() && Repeated)
                invokeConnections();
        else if (isGateOpen())
            invokeConnections();
        invoking = false;
        jButtonInvoke.setEnabled(ButtonState);
    }
    
    /** Invoke the method or constructor in its own thread. */
    public void invokeThreaded() {
        java.lang.Thread t = new Thread(new java.lang.Runnable() {
            public void run() {
                invoke();
            }
        });
        t.start();
    }
    
    /** Invoke the method in its own thread.  Usually called by listeners. */
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        invokeThreaded();
    }
    
    // Build argument array from parameters prior to invoke.
    private Object[] buildArguments() {
        Object arguments[] = new Object[getParameterConnectorCount()];
        for (int i=0; i<getParameterConnectorCount(); i++) {
            Connection ConnectionToParm = getParameterConnector(i).getConnection(0);
            Visualiser Parameter = ConnectionToParm.getVisualiser();
            arguments[i] = Parameter.useInstance();
            ConnectionToParm.pulse();
        }
        return arguments;
    }
    
    // Add a connector as a parameter connector
    private void addParameter(Connector c) {
        c.setValueUsed(true);
        c.setLayoutDirection(Connector.EASTTOWEST);
        c.setParameterNumber(ParameterConnectors.size() + 1);
        ParameterConnectors.add(c);
        addConnector(c, true);
    }
    
    // Implement a connector for a parameter
    private void addParameter(Class cl) {
        addParameter(new Connector(this, cl, false));
    }
    
    // Implement a connector for each parameter
    private void addParameters(Class p[]) {
        if (p!=null)
            for (int i=0; i<p.length; i++)
                addParameter(p[i]);
    }
    
    // Implement connector to hold method invocation's return value
    private void addReturnValue(Method m) {
        if (m.getReturnType().getName()!="void") {
            ConnectorToReturnType = new Connector(this, m.getReturnType(), "R", "Returns " + m.getReturnType().getName());
            ConnectorToReturnType.setValueGenerated(true);
            ConnectorToReturnType.setLayoutDirection(Connector.WESTTOEAST);
            ConnectorToReturnType.setParameterNumber(0);
            addConnector(ConnectorToReturnType, true);
        }
    }
    
    // Implement a connector to hold a thrown exception
    private void addThrowable(Class t, boolean VisibleByDefault, boolean isChecked) {
        Connector ConnectorToThrowable = new Connector(this, t, "!", "Throws " + t.getClass().getName());
        ConnectorToThrowable.setCheckedException(isChecked);
        ConnectorToThrowable.setValueGenerated(true);
        ConnectorToThrowable.setLayoutDirection(Connector.EASTTOWEST);
        addConnector(ConnectorToThrowable, VisibleByDefault);
        ThrowableConnectors.add(ConnectorToThrowable);
    }
    
    // Implement a connector to hold a thrown exception not handled by any specific connector
    private void addThrowableDefault() {
        Connector ConnectorToThrowable = new Connector(this, Throwable.class, "!", "Throws " + Throwable.class.getName());
        ConnectorToThrowable.setValueGenerated(true);
        ConnectorToThrowable.setLayoutDirection(Connector.EASTTOWEST);
        addConnector(ConnectorToThrowable, false);
        ConnectorToDefaultThrowable = ConnectorToThrowable;
    }
    
    // Implement connector to hold method or constructor checked exceptions
    private void addThrowables(Class p[]) {
        if (p==null)
            return;
        for (int i=0; i<p.length; i++)
            addThrowable(p[i], true, true);
        addThrowableDefault();
    }
    
    // Implement a message to invoke a constructor
    private void addConstructor(Constructor c) {
        addParameters(c.getParameterTypes());
    }
    
    // Implement a message to invoke a method
    private void addMethod(Method m) {
        addParameters(m.getParameterTypes());
    }
    
    // Implement a message to access a field
    private void addField(Field f) {
        Log.println("VisualiserOfMessage: We shouldn't be in addField()");
    }
}
