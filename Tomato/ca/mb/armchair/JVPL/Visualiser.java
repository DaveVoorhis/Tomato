/*
 * Visualiser.java
 *
 * Created on June 9, 2002, 1:28 AM
 */

package ca.mb.armchair.JVPL;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.lang.reflect.*;

/**
 * An object visualiser provides a standardised visual
 * representation of any object.  An object is not necessarily an instance, 
 * as some classes are static, and others may not be instantiated until one of their
 * parameterised constructors is invoked.
 *
 * @author  Dave Voorhis
 */
public class Visualiser extends javax.swing.JPanel {

    /** Pass to setSerializationMode() to recommend serializing as XML. */
    public final static int SERIALIZE_XML = 1;
    
    /** Pass to setSerializationMode() to recommend serializing as binary. */
    public final static int SERIALIZE_BINARY = 2;
    
    /** Pass to setSerializationMode() to recommend saving and loading as null. */
    public final static int SERIALIZE_NULL = 3;
    
    /** Pass to setSerializationMode() to recommend auto-instantiation. */
    public final static int SERIALIZE_AUTOINSTANTIATE = 4;
    
    private final static java.awt.Color NullColor = new java.awt.Color(175, 175, 175);
    private final static java.awt.Color MessageColor = new java.awt.Color(153, 175, 175);
    private final static java.awt.Color BaseColor = new java.awt.Color(153, 153, 255);
    private final static java.awt.Color SelectedColor = new java.awt.Color(100, 100, 255);
    private final static java.awt.Color DropCandidateColor = new java.awt.Color(100, 255, 100);
    private final static java.awt.Color PulseColor = new java.awt.Color(255, 255, 75);
    private final static java.awt.Color BackgroundColor = new java.awt.Color(198, 198, 198);
    private final static int PulseDuration = 250;

    private final static java.awt.Dimension CustomPanelSize = new java.awt.Dimension(150, 35);

    private final static int DEFAULT_EXTENSION_STEP_LENGTH = 3;
    private final static int DEFAULT_EXTENSION_BASE_LENGTH = 15;

    private final static java.awt.Font LabelFont = new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 10);

    // display widgets
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JLabel jLabelTitle;
    
    // Connectors on this visualiser
    private java.util.Vector Connectors = new java.util.Vector();
    
    // References to this visualiser
    private java.util.Vector References = new java.util.Vector();
    
    // layout control
    private int LeftPosition;
    private int RightPosition;
    
    // Model in which this visualiser lives
    private Model theModel;
    
    // Instance wrapped by this visualiser
    private Object theInstance;
    
    // Unique visualiser ID stamp
    private static long IDNumberStamp = 1;
    
    // Unique visualiser ID
    private long IDNumber = 0;
    
    // Next connector ID
    private long NextConnectorID = 0;
    
    // Title
    private String Title = "<Undefined>";
    
    // True if selected
    private boolean Selected;
    
    // True if drop candidate
    private boolean DropCandidate;
    
    // Property editor
    private VisualiserControlPanel ControlPanel = null;
    
    // Serialization mode - default is non-serialized auto-instantiate.
    private int SerializationMode = SERIALIZE_AUTOINSTANTIATE;
    
    // Modifier string.
    private String ModifierString;
    
    /** Ctor */
    Visualiser() {
        IDNumber = IDNumberStamp++;
        NextConnectorID = 0;
        LeftPosition = 0;
        RightPosition = 0;
        theModel = null;
        theInstance = null;
        Selected = false;
        DropCandidate = false;
        setModifierString("private");
        buildWidgets();
    }
    
    /** Return true if a given visualiser can be dropped on this one, with something
       good possibly taking place thereafter via a receiveDrop() operation. */
    public boolean isDropCandidateFor(Visualiser DraggedVisualiser) {
        if (this != DraggedVisualiser && getBoundClass()!=null && DraggedVisualiser.getBoundClass()!=null)
            if ((getBoundClass()==DraggedVisualiser.getBoundClass() ||
                    DraggedVisualiser.getBoundClass().isAssignableFrom(getBoundClass())))
                return true;
        return false;
    }
    
    /** Drop a visualiser on this one.  Return true if succeeded. */
    public boolean receiveDrop(Visualiser DraggedVisualiser) {
        if (isDropCandidateFor(DraggedVisualiser)) {
            while (DraggedVisualiser.getConnectionCount()>0)
                DraggedVisualiser.getConnection(0).setVisualiser(this);
            DraggedVisualiser.getModel().removeVisualiser(DraggedVisualiser);
            return true;
        }
        return false;
    }
    
    /** Obtain Java source equivalent to this Visualiser. */
    public String getJavaSource(ModelToJava compiler) {
        return "";                      // Generic visualiser returns nothing.  Derivations handle this.
    }
    
    /** Invoke properties editor. */
    public void showProperties() {
        if (ControlPanel==null)
            ControlPanel = new VisualiserControlPanel(this);
        if (theModel.getShell() != null) {
            theModel.getShell().displayControlPanel(ControlPanel, "Visualiser " + getTitle());
            ControlPanel.requestFocusInWindow();
        }
    }
    
    /** Invoke properties editor and point to given Connector. */
    public void showProperties(Connector c) {
        showProperties();
        ControlPanel.gotoConnector(c);
    }
    
    /** Hide properties editor. */
    public void hideProperties() {
        if (ControlPanel!=null)
            ControlPanel.hide();
    }
    
    private javax.swing.Timer pulseTimer = null;
    
    /** Pulse the visualiser with a given color for a moment.  Used to indicate
     * activity, instance changes, thrown exceptions, etc. */
    public void pulse(java.awt.Color theColor) {
        if (pulseTimer!=null)
            return;
        setVisualiserColor(theColor);
        pulseTimer = new javax.swing.Timer(PulseDuration, new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setVisualiserColor();
                pulseTimer = null;
            }
        });
        pulseTimer.setRepeats(false);
        pulseTimer.start();
    }
    
    /** Pulse the visualiser, i.e., set background to pulse color for a moment. */
    public void pulse() {
        pulse(PulseColor);
    }

    // set visualiser to given color
    private void setVisualiserColor(java.awt.Color theColor) {
        jLabelTitle.setBackground(theColor);
    }
    
    // set visualiser to state-appropriate color
    private void setVisualiserColor() {
        if (DropCandidate)
            setVisualiserColor(DropCandidateColor);
        else if (Selected)
            setVisualiserColor(SelectedColor);
        else if (isVisualiserOfMessage())
            setVisualiserColor(MessageColor);
        else if (getInstance()==null)
            setVisualiserColor(NullColor);
        else
            setVisualiserColor(BaseColor);
    }
    
    // prevent-infinite-recursion flag
    private boolean inSetSelected = false;
    
    /** Set selected status. */
    public void setSelected(boolean flag) {
        if (inSetSelected)
            return;
        inSetSelected = true;
        Selected = flag;
        setVisualiserColor();
        for (int i=0; i<getConnectionCount(); i++)
            getConnection(i).setSelected(flag);
        for (int i=0; i<getConnectorCount(); i++)
            for (int j=0; j<getConnector(i).getConnectionCount(); j++)
                getConnector(i).getConnection(j).setSelected(flag);
        inSetSelected = false;
    }
    
    /** Get selected status. */
    public boolean isSelected() {
        return Selected;
    }

    /** Set drop candidate status */
    public void setDropCandidate(boolean flag) {
        DropCandidate = flag;
        setVisualiserColor();
    }
    
    /** Get drop candidate status */
    public boolean isDropCandidate() {
        return DropCandidate;
    }
    
    // get connector extension step length, i.e., the amount each new connector extends
    // the recommended extension length
    int getConnectorExtensionStepLength() {
        return DEFAULT_EXTENSION_STEP_LENGTH;
    }
    
    // get the connector extension base length
    int getConnectorExtensionBaseLength() {
        return DEFAULT_EXTENSION_BASE_LENGTH;
    }

    /** Return number of connectors. */
    public int getConnectorCount() {
        return Connectors.size();
    }

    /** Get the ith connector. */
    public Connector getConnector(int i) {
        return (Connector)Connectors.get(i);
    }
    
    /** Return number of exposed connectors. */
    public int getExposedConnectorCount() {
        int count = 0;
        for (int i=0; i<getConnectorCount(); i++)
            if (getConnector(i).isExposed())
                count++;
        return count;
    }
    
    /** Return a Connector given its Connector ID and ParameterNumber, as found via getConnectorID() and
     * getParameterNumber().  If ParameterNumber is -1, ignore it.  
     * Return null if not found. */
    public Connector getConnector(String ID, int ParameterNumber) {
        if (ParameterNumber==-1) {
            for (int i=0; i<getConnectorCount(); i++)
                if (getConnector(i).getConnectorID().compareTo(ID)==0)
                    return getConnector(i);
        } else {
            for (int i=0; i<getConnectorCount(); i++)
                if (getConnector(i).getConnectorID().compareTo(ID)==0 &&
                    getConnector(i).getParameterNumber() == ParameterNumber)
                    return getConnector(i);
        }
        return null;
    }
    
    /** Get next assignable connector ID. */
    public long getNextConnectorID() {
        return NextConnectorID++;
    }
    
    /** Set the serialization mode.  This is merely stored property, used
     * by serialization mechanisms found in Shell and possibly
     * elsewhere.
     */
    public void setSerializationMode(int mode) {
        SerializationMode = mode;
    }
    
    /** Get the serialization mode. */
    public int getSerializationMode() {
        return SerializationMode;
    }
    
    /** Get the modifier string.  This is merely a stored property, used
     * by ModelToJava and updated by VisualiserControlPanelModifiers. */
    public String getModifierString() {
        return ModifierString;
    }
    
    /** Set the modifier string. */
    public void setModifierString(String s) {
        ModifierString = s;
    }
    
    // Let visualiser know it got a new reference connection
    void addConnection(Connection c) {
        References.add(c);
        updateVisualiser();
    }
    
    // Remove a reference
    void removeConnection(Connection c) {
        References.remove(c);
        updateVisualiser();
    }
    
    /** Return number of reference Connections. */
    public long getConnectionCount() {
        return References.size();
    }
    
    /** Get the i'th reference Connection. */
    public Connection getConnection(int i) {
        return (Connection)References.get(i);
    }
    
    /** Redraw all Connections to this Visualiser. */
    public void redrawConnections() {
        // redraw references
        for (int i=0; i<getConnectionCount(); i++)
            getConnection(i).redraw();
        // redraw connector's connections
        for (int i=0; i<getConnectorCount(); i++)
            getConnector(i).redrawConnections();
    }
    
    /** Returns true if Visualiser has an available parameterless
     * constructor, which might allow auto-instantiation. */
    public boolean isAutoInstantiable() {
        Class bc = getBoundClass();
        if (bc==null)
            return false;
        try {
            Constructor c = bc.getConstructor(null);
            return (c!=null);
        } catch (Throwable t) {
            return false;
        }
    }
    
    /** Attempt to auto-instantiate this Visualiser. */
    public void instantiate() {
        Class bc = getBoundClass();
        if (bc!=null)
            try {
                Object o = bc.newInstance();
                setInstance(o);
            } catch (Throwable t) {
                if (SerializationMode==SERIALIZE_AUTOINSTANTIATE)
                    SerializationMode=SERIALIZE_NULL;
                setInstance(null);
            }
    }
    
    /** Get visualiser ID, which uniquely identifies it in the Model. */
    public long getID() {
        return IDNumber;
    }
    
    /** Set visualiser ID. */
    public void setID(long ID) {
        IDNumber = ID;
        if (IDNumber>IDNumberStamp)
            IDNumberStamp = IDNumber + 1;
    }
    
    /** Get the class to which this visualiser refers.  Will be null in some derivations. */
    public Class getBoundClass() {
        return null;
    }
      
    /** Set the class to which this visualiser refers.  Primarily meaningful in the
    * VisualiserOfClass heirarchy. */
    public void setBoundClass(Class aClass) {
    }

    /** Set the Model to which this visualiser belongs. */
    public void setModel(Model w) {
        theModel = w;
    }
    
    /** Get the Model that may be accessed by this Visualiser.  Null if none. */
    public Model getModel() {
        return theModel;
    }
    
    /** True if this Visualiser is wholly owned by its Connector, i.e., it is a message
     * visualiser and should be deleted along with its Connection. */
    public boolean isVisualiserOfMessage() {
        return false;
    }
    
    /** This method is overridden by Visualisers of primitives, and provides
     * the appropriate Java source code string to intantiate a primitive's wrapper class
     * with the primitive value held by the Visualiser.  It is used by ModelToJava.  Returns null
     * if the Visualiser is not primitive.
     */
    public String getPrimitiveInitialisation() {
        return null;
    }
    
    /** This method is overridden by Visualisers of primitives, and returns
     * the name of the primitive.  Null if not primitive.
     */
    public String getPrimitiveName() {
        return null;
    }
    
    // this is invoked by a connected visualiser when its instance
    // changes.
    void notifyInstantiation(Visualiser v) {
        updateVisualiser();
    }
    
    // this is invoked by a connector when it receives a new connection
    void notifyConnectionAdded(Connector x, Connection c) {
        expose(x);
    }
    
    // this is invoked by a connector when it loses a connection
    void notifyConnectionRemoved(Connector x, Connection c) {
        redrawConnections();
    }
    
    /** Set instance.  If this Visualiseris a parameter to a VisualiserOfMessage 
     * that has 'invoke after parm change' turned on, we must invoke it.  
     * If this Visualiser is a reference to a VisualiserOfMessage 
     * that has 'invoke after reference change turned on, we must invoke it. */
    private void attachInstance(Object o) {
        theInstance = o;
        // Handle parameters to VisualiserOfMessages with 'invoke after parm change'
        for (int i=0; i<getConnectionCount(); i++) {
            Connector c = getConnection(i).getConnector();
            if (c != null) {
                Visualiser vReference = c.getVisualiser();
                vReference.notifyInstantiation(this);
                if (vReference.isVisualiserOfMessage()) {
                    VisualiserOfMessage vParmMessage = (VisualiserOfMessage)vReference;
                    if (vParmMessage.isInvokesAfterParmChange())
                        vParmMessage.invoke();
                }
            }
        }
        // Handle being a referenced connection to VisualiserOfMessages with 'invoke after reference change'
        for (int i=0; i<getConnectorCount(); i++) {
            Connector referencedConnector = getConnector(i);
            if (referencedConnector.isConstructor() || referencedConnector.isMethod())
                for (int j=0; j<referencedConnector.getConnectionCount(); j++) {
                    Visualiser vReferenced = referencedConnector.getConnection(j).getVisualiser();
                    if (vReferenced!=null && vReferenced.isVisualiserOfMessage()) {
                        VisualiserOfMessage vReferenceMessage = (VisualiserOfMessage)vReferenced;
                        if (vReferenceMessage.isInvokesAfterReferenceChange())
                            vReferenceMessage.invoke();
                    }
                }
        }
        pulse();
        updateVisualiser();
    }
    
    /** Set instance managed by this Visualiser. */
    public void setInstance(Object o) {
        attachInstance(o);
        setLabel();
    }
    
    /** Get Instance managed by this Visualiser. */
    public Object getInstance() {
        return theInstance;
    }
    
    /** 'Use' the Instance managed by this Visualiser.  In other words,
     * if this Visualiser is the return value of a  VisualiserOfMessage that 
     * has 'invoke before return value use' turned on, we must invoke it.
     * If this Visualiser is a reference to a VisualiserOfMessage 
     * that has 'invoke before reference use' turned on, we must invoke it. */
    public Object useInstance() {
        for (int i=0; i<getConnectionCount(); i++) {
            Visualiser vReference = getConnection(i).getConnector().getVisualiser();
            if (vReference.isVisualiserOfMessage()) {
                VisualiserOfMessage vSourceMessage = (VisualiserOfMessage)vReference;
                if (vSourceMessage.isInvokesBeforeReturnUse())
                    if (vSourceMessage.getConnectorToReturnType()!=null && 
                        vSourceMessage.getConnectorToReturnType().getConnectionCount()==1) {    
                        Connection ConnectionToReturnValue = vSourceMessage.getConnectorToReturnType().getConnection(0);
                        Visualiser ReturnValue = ConnectionToReturnValue.getVisualiser();
                        if (ReturnValue==this)
                            vSourceMessage.invoke();
                    }
            }
        }
        // Handle being a referenced connection to VisualiserOfMessages with 'invoke before reference use'
        for (int i=0; i<getConnectorCount(); i++) {
            Connector referencedConnector = getConnector(i);
            if (referencedConnector.isConstructor() || referencedConnector.isMethod())
                for (int j=0; j<referencedConnector.getConnectionCount(); j++) {
                    Visualiser vReferenced = referencedConnector.getConnection(j).getVisualiser();
                    if (vReferenced!=null && vReferenced.isVisualiserOfMessage()) {
                        VisualiserOfMessage vReferenceMessage = (VisualiserOfMessage)vReferenced;
                        if (vReferenceMessage.isInvokesBeforeReferenceUse())
                            vReferenceMessage.invoke();
                    }
                }
        }
        return getInstance();
    }
    
    /** True if the instance *might* be serializable. */
    public boolean isSerializableInstance() {
        try {
            java.io.Serializable I = (java.io.Serializable)getInstance();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
    
    /** Set label (blue bar at top of every Visualiser). */
    public void setLabel() {
        jLabelTitle.setText(getName());
    }
    
    /** Get long title, for use in control panel, etc. */
    public String getLongTitle() {
        String out = getName() + ": ";
        if (isVisualiserOfMessage())
            out += il8n._("Message");
        else if (getBoundClass()!=null) {
            if (getBoundClass().isArray())
                out+= il8n._("Array") + " ";
            else
                out+= il8n._("Class") + " ";
            out += getBoundClass().getName() + " ";
            if (getInstance()==null)
                out += il8n._("null");
            else {
                if (!isSerializableInstance())
                    out += il8n._("transient") + " ";
                out += il8n._("instance");
            }
        }
        return out;
    }
    
    /** Set name and set label to name. */
    public void setName(String name) {
        super.setName(name);
        setLabel();
    }
    
    /** Set title.  Set name to title if it hasn't been set yet. */
    public void setTitle(String title) {
        Title = title;
        if (getName()==null || getName().length()==0)
            setName(Title + getID());       // auto-naming via title
        setLabel();
    }
    
    /** Get title. */
    public String getTitle() {
        return Title;
    }
    
    /** Stringify as long title. */
    public String toString() {
        return getLongTitle();
    }
    
    /** Get reference attachment point in Model coordinates. */
    public int getAttachmentX(Connection c) {
        int indexOfConnection = References.indexOf(c);
        if (indexOfConnection==-1)
            return getX() + getWidth() / 2;
        int marginWidth = getWidth() / 5;               // 5%
        int drawWidth = getWidth() - marginWidth;
        int drawStep = drawWidth / (int)getConnectionCount();
        if (drawStep<0)
            return getX() + getWidth() / 2;
        return getX() + (getWidth() - drawWidth) / 2 + drawStep / 2 + indexOfConnection * drawStep;
    }
    
    /** Get reference attachment point in Model coordinates. */
    public int getAttachmentY() {
        return getY();
    }
    
    /** Get appropriate connection extension length given connection. */
    public int getExtensionLength(Connection c) {
        int indexOfConnection = References.indexOf(c);
        if (indexOfConnection==0)
            return DEFAULT_EXTENSION_BASE_LENGTH;
        return DEFAULT_EXTENSION_BASE_LENGTH + indexOfConnection * DEFAULT_EXTENSION_STEP_LENGTH;
    }
    
    // build widgets
    protected void buildWidgets() {                
        setLayout(new java.awt.BorderLayout());
        setBackground(BackgroundColor);
        
        jPanelLeft = new javax.swing.JPanel();
        jPanelLeft.setLayout(new java.awt.GridBagLayout());
        jPanelLeft.setOpaque(false);
        add(jPanelLeft, java.awt.BorderLayout.WEST);
        
        jPanelRight = new javax.swing.JPanel();
        jPanelRight.setLayout(new java.awt.GridBagLayout());
        jPanelRight.setOpaque(false);
        add(jPanelRight, java.awt.BorderLayout.EAST);
        
        // Visualiser customisations
        populateCustom();
        
        jLabelTitle = new javax.swing.JLabel();
        jLabelTitle.setOpaque(true);
        jLabelTitle.setBackground(BaseColor);
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setFont(LabelFont);
        add(jLabelTitle, java.awt.BorderLayout.NORTH);
    }
    
    // Default custom section widget
    protected javax.swing.JTextArea jLabelMain = null;

    // Container for custom section in default Visualiser
    private javax.swing.JScrollPane jScrollPane = null; 
    
    /** Hide or show custom section. */
    protected void setCustomVisible(boolean t) {
        if (jScrollPane != null)
            jScrollPane.setVisible(t);
    }
    
    /** True if custom section is visible. */
    protected boolean isCustomVisible() {
        if (jScrollPane == null)
            return false;
        return jScrollPane.isVisible();
    }
    
    /** Populate custom section. */
    protected void populateCustom() {
        jLabelMain = new javax.swing.JTextArea();
        jLabelMain.setEnabled(true);

        jScrollPane = new javax.swing.JScrollPane();
        jScrollPane.setVisible(false);
        jScrollPane.setMinimumSize(CustomPanelSize);
        jScrollPane.setPreferredSize(CustomPanelSize);
        jScrollPane.setViewportView(jLabelMain);
        jScrollPane.setAutoscrolls(true);
       
        add(jScrollPane, java.awt.BorderLayout.CENTER);
        
        updateVisualiser();
    }
    
    /** Update Visualiser as a result of some change in state. */
    public void updateVisualiser() {
        if (jLabelMain!=null)
            if (getInstance()==null)
                jLabelMain.setText("null");
            else
                jLabelMain.setText(getInstance().toString());
    }
    
    /** Get main panel. */
    public javax.swing.JPanel getMainPanel() {
        return jPanelMain;
    }
   
    /** Expose a Connector. */
    public void expose(Connector c) {
        if (c.isExposed())
            return;
        
        c.setAlignmentY(0.0F);
        c.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        c.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 0;
        
        // Position of connector
        if (c.getLayoutDirection()==Connector.EASTTOWEST) {
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            jPanelLeft.add(c, gridBagConstraints);
            c.setExtensionLength(jPanelLeft.getComponentCount() * getConnectorExtensionStepLength() + getConnectorExtensionBaseLength());
        } else {
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            jPanelRight.add(c, gridBagConstraints);
            c.setExtensionLength(jPanelRight.getComponentCount() * getConnectorExtensionStepLength() + getConnectorExtensionBaseLength());
        }
        c.setExposed(true);
                
        // Redraw connections on this visualiser.  
        redrawConnections();
        if (theModel != null)
            theModel.refresh();
    }

    // Temporarily unexposes a connector (while leaving its connections in visual limbo)
    // so that it can shortly be re-exposed, possibly on a new side of the whazzit.
    private void unexposeTemporary(Connector c) {
        c.setExposed(false);
        c.getParent().remove(c);
    }
    
    /** Unexpose a Connector.  Will only work if the Connector has no Connections. */
    public void unexpose(Connector c) {
        if (!c.isExposed())
            return;
        if (c.getConnectionCount()>0)
            return;
        unexposeTemporary(c);
        redrawConnections();
        theModel.refresh();
    }
    
    /** Make a connector switch sides.  Only works if connector is exposed. */
    public void switchSides(Connector c) {
        if (!c.isExposed())
            return;
        unexposeTemporary(c);
        if (c.getLayoutDirection()==Connector.EASTTOWEST)
            c.setLayoutDirection(Connector.WESTTOEAST);
        else
            c.setLayoutDirection(Connector.EASTTOWEST);
        expose(c);
    }
    
    /** Remove all connections and any message visualisers associated with the connections. */
    public void removeConnections() {
        // detach reference connections
        while (getConnectionCount()>0)
            getConnection(0).disconnect();
        // clear all reference connections
        References = new java.util.Vector();
        // remove connector connections
        for (int i=0; i<getConnectorCount(); i++)
           getConnector(i).removeConnections();
    }
    
    /** Called by a Connector when it receives a mouse click that it hasn't handled. */
    protected void connectorForwardsClick(Connector c, java.awt.event.MouseEvent e) {
        showProperties(c);
    }
    
    /** Add a connector. */
    protected void addConnector(Connector c, boolean Expose) {
        Connectors.add(c);
        if (Expose)
            expose(c);
    }
}
