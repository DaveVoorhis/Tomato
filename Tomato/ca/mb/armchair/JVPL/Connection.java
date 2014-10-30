/*
 * Connection.java
 *
 * Created on July 17, 2002, 4:00 PM
 */

package ca.mb.armchair.JVPL;

import ca.mb.armchair.Utilities.Visualisation.Lines.*;
import ca.mb.armchair.Utilities.Visualisation.Glyphs.*;

/**
 * Establishes and displays a connection from a Connector to a Visualiser.
 *
 * @author  Dave Voorhis
 */
public class Connection {

    private final static java.awt.Color BaseColor = new java.awt.Color(25, 75, 75);
    private final static java.awt.Color BaseColorMethod = new java.awt.Color(25, 75, 75);
    private final static java.awt.Color BaseColorConstructor = new java.awt.Color(75, 75, 25);
    private final static java.awt.Color BaseColorInstance = new java.awt.Color(75, 25, 75);
    private final static java.awt.Color SelectedColor = new java.awt.Color(150, 25, 25);
    private final static java.awt.Color PulseColor = new java.awt.Color(255, 255, 75);
    private final static int PulseDuration = 250;
    private final static int NormalArrowWidth = 5;
    private final static int NormalLineWidth = 1;
    private final static int SelectedArrowWidth = 8;
    private final static int SelectedLineWidth = 2;
    
    private Model theModel;
    private Connector theConnector;
    private Visualiser theVisualiser;
    private ConnectionSerializable theSerializedConnection;

    private LineHorizontal ConnectorExtension = new LineHorizontal();
    private LineVertical VisualiserExtension = new LineVertical();
    private LineVertical VerticalLink = new LineVertical();
    private LineHorizontal VisualiserLink = new LineHorizontal();
    private Arrow VisualiserArrow = new Arrow();
    private Arrow ConnectorArrow = new Arrow();

    private int lineWidth = NormalLineWidth;

    private boolean pulsed = false;
    private boolean selected = false;
    
    /** Create a connection between a Connector and a Visualiser. */
    public Connection(Model w, Connector c, Visualiser v) {
        theModel = w;
        theConnector = c;
        theVisualiser = v;
        theSerializedConnection = null;
        initComponents();
        if (isDangling())
            Log.println("Connection: Attempt to create a dangling connection!");
        else
            connect();
    }

    /** Create a connection, given a serializable representation of it */
    public Connection(Model w, ConnectionSerializable cs) {
        theModel = w;
        theSerializedConnection = cs;
        theConnector = null;
        theVisualiser = null;
        initComponents();
        unserialize();
    }

    // Get recommended color
    private java.awt.Color getRecommendedColor() {
        if (pulsed)
            return PulseColor;
        else if (selected)
            return SelectedColor;
        else if (theConnector==null)
            return BaseColor;
        else {
            if (theConnector.isConstructor())
                return BaseColorConstructor;
            else if (theConnector.isMethod())
                return BaseColorMethod;
            else if (theConnector.isInstance())
                return BaseColorInstance;
            else
                return BaseColor;
        }
    }
    
    // Intitialise widgets
    private void initComponents() {
        theModel.getModelPane().add(VisualiserExtension);
        theModel.getModelPane().add(VisualiserLink);
        theModel.getModelPane().add(VerticalLink);
        theModel.getModelPane().add(ConnectorExtension);
        theModel.getModelPane().add(VisualiserArrow);
        theModel.getModelPane().add(ConnectorArrow);
    }
    
    /** Set connection as selected or unselected */
    public void setSelected(boolean flag) {
        selected = flag;
        if (selected)
            lineWidth = SelectedLineWidth;
        else
            lineWidth = NormalLineWidth;
        redraw();
    }

    private javax.swing.Timer pulseTimer = null;
    
    /** Pulse the connector with a given color for a moment.  Used to indicate
     * activity, instance changes, thrown exceptions, etc. */
    public void pulse() {
        if (pulseTimer!=null)
            return;
        pulsed = true;
        redrawColor();
        pulseTimer = new javax.swing.Timer(PulseDuration, new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pulsed = false;
                redrawColor();
                pulseTimer = null;
            }
        });
        pulseTimer.setRepeats(false);
        pulseTimer.start();
    }
    
    /** Set Connector end of connection. */
    public void setConnector(Connector c) {
        if (theConnector!=null)
            theConnector.removeConnection(this);         // remove connection from old connector
        theConnector = c;
        theConnector.addConnection(this);               // attach connection to new connector
        // must disconnect and reconnect visualiser here, because this
        // is essentially a new connection
        if (theVisualiser!=null) {
            theVisualiser.removeConnection(this);
            theVisualiser.addConnection(this);
        }
        redraw();
    }
    
    /** Set Visualiser at visualiser end of Connection. */
    public void setVisualiser(Visualiser v) {
        if (theVisualiser!=null)
            theVisualiser.removeConnection(this);
        theVisualiser = v;
        theVisualiser.addConnection(this);
        // must disconnect and reconnect connector here, because this
        // is essentially a new connection
        if (theConnector!=null) {
            theConnector.removeConnection(this);
            theConnector.addConnection(this);
        }
        redraw();
    }
    
    /** Get Connector at connector end of Connection */
    public Connector getConnector() {
        if (theSerializedConnection!=null)
            unserialize();
        return theConnector;
    }
    
    /** Get Visualiser at visualiser end of Connection. */
    public Visualiser getVisualiser() {
        if (theSerializedConnection!=null)
            unserialize();
        return theVisualiser;
    }
    
    /** Get Model in which this Connection lives. */
    public Model getModel() {
        return theModel;
    }

    // Unvisualise it
    private void undraw() {
        theModel.getModelPane().remove(VisualiserExtension);
        theModel.getModelPane().remove(VisualiserLink);
        theModel.getModelPane().remove(VerticalLink);
        theModel.getModelPane().remove(ConnectorExtension);
        theModel.getModelPane().remove(VisualiserArrow);
        theModel.getModelPane().remove(ConnectorArrow);
    }

    /** Recolor existing lines. */
    public void redrawColor() {
        if (isDangling())
            return;        
        java.awt.Color c = getRecommendedColor();
        VisualiserExtension.setBackground(c);
        ConnectorExtension.setBackground(c);
        VerticalLink.setBackground(c);
        VisualiserLink.setBackground(c);
        VisualiserArrow.setColor(c);
        ConnectorArrow.setColor(c);
    }
    
    /** True if the connection is dangling, and therefore invalid. */
    public boolean isDangling() {
        return (theConnector==null || theVisualiser==null);
    }

    // Set connector arrow to given arrow type.
    private void setConnectorArrow(boolean In, int arrowSize) {
        if (theConnector.getLayoutDirection()==Connector.EASTTOWEST) {
            ConnectorArrow.setArrow((In) ? Arrow.DIRECTION_RIGHT : Arrow.DIRECTION_LEFT, arrowSize);
            ConnectorArrow.setLocation(theConnector.getAttachmentX() - arrowSize - 2, lineWidth - 1 + theConnector.getAttachmentY() - arrowSize / 2);
        } else {
            ConnectorArrow.setArrow((In) ? Arrow.DIRECTION_LEFT : Arrow.DIRECTION_RIGHT, arrowSize);
            ConnectorArrow.setLocation(theConnector.getAttachmentX() + 2, lineWidth - 1 + theConnector.getAttachmentY() - arrowSize / 2);
        }
    }

    // Set visualiser arrow to given arrow type.
    private void setVisualiserArrow(boolean In, int arrowSize) {
        VisualiserArrow.setArrow((In) ? Arrow.DIRECTION_DOWN : Arrow.DIRECTION_UP, arrowSize);            
        VisualiserArrow.setLocation(lineWidth - 1 + theVisualiser.getAttachmentX(this) - arrowSize / 2, theVisualiser.getAttachmentY() - arrowSize - 2);
    }
    
    // Gate to prevent infinite recursion
    private boolean gateRedraw = false;
    
    /** Draw lines */
    public void redraw() {
        if (isDangling())
            unserialize();
        if (isDangling())
            return;

        if (gateRedraw)
            return;
        gateRedraw = true;
        
        // Force connector to a side nearest its Visualisers
        long vxSum = 0;
        for (int i=0; i<theConnector.getConnectionCount(); i++)
            vxSum += theConnector.getConnection(i).getVisualiser().getAttachmentX(this);
        long vxAverage = vxSum / theConnector.getConnectionCount();
        Visualiser ConnectorVisualiser = theConnector.getVisualiser();
        if (vxAverage > ConnectorVisualiser.getWidth() / 2 + ConnectorVisualiser.getX()) {
            if (theConnector.getLayoutDirection()==Connector.EASTTOWEST)
                theConnector.switchSides();
        } else {
            if (theConnector.getLayoutDirection()==Connector.WESTTOEAST)
                theConnector.switchSides();
        }
        
        // Set up connection visualisation.

        int vx2 = theVisualiser.getAttachmentX(this);
        
        int cextlen = theConnector.getExtensionLength();

        int vx1 = theConnector.getAttachmentX() +
            ((theConnector.getLayoutDirection()==Connector.EASTTOWEST) ? -cextlen : cextlen);

        int vy2 = theVisualiser.getAttachmentY() - theVisualiser.getExtensionLength(this);
       
        ConnectorExtension.setLine(theConnector.getAttachmentX(), vx1 +
            ((theConnector.getLayoutDirection()!=Connector.EASTTOWEST) ? lineWidth : 0),
            theConnector.getAttachmentY(), lineWidth);
           
        VisualiserExtension.setLine(vx2, theVisualiser.getAttachmentY(), vy2, lineWidth);
        
        VerticalLink.setLine(vx1, theConnector.getAttachmentY(), vy2, lineWidth);
        
        VisualiserLink.setLine(vx2, vx1 +
            ((vy2 > theConnector.getAttachmentY() && vx2 < vx1) ? lineWidth : 0), vy2, lineWidth);

        // Determine connection type here, and create appropriate connection
        // decorations.
        int arrowSize;
        if (lineWidth == NormalLineWidth)
            arrowSize = NormalArrowWidth;
        else
            arrowSize = SelectedArrowWidth;
        if (theConnector.isConstructor()) {
            setConnectorArrow(true, arrowSize);
        } else if (theConnector.isValueGenerated()) {
            setVisualiserArrow(true, arrowSize);
            setConnectorArrow(false, arrowSize);
        } else if (theConnector.isValueUsed()) {
            setVisualiserArrow(false, arrowSize);
            setConnectorArrow(true, arrowSize);
        }
        
        redrawColor();
        
        gateRedraw = false;
    }

    /** Disconnect from the Connector and the Visualiser. */
    public void disconnect() {
        undraw();
        if (theConnector!=null)
            theConnector.removeConnection(this);
        theConnector = null;
        if (theVisualiser!=null)
            theVisualiser.removeConnection(this);
        theVisualiser = null;
    }
    
    // Attach this Connection to the Connector and the Visualiser
    private void connect() {
        if (isDangling())
            return;
        theConnector.addConnection(this);
        theVisualiser.addConnection(this);
        redraw();
    }

    // Attempt to unserialize connection
    private void unserialize() {
        if (theSerializedConnection!=null) {
            if (theConnector==null) {       // get connector
                theConnector = theSerializedConnection.getConnector(theModel);
                if (theConnector!=null)
                    theConnector.addConnection(this);
            }
            if (theVisualiser==null) {      // get visualiser
                theVisualiser = theSerializedConnection.getVisualiser(theModel);
                if (theVisualiser!=null)
                    theVisualiser.addConnection(this);
            }
            if (!isDangling()) {            // if we've got 'em...
                redraw();
                theSerializedConnection = null;
            }
        }
    }    
}
