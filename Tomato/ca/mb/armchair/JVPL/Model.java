/*
 * Model.java
 *
 * Created on July 17, 2002, 4:00 PM
 */

package ca.mb.armchair.JVPL;

/**
 * Defines a layered pane in which visualised classes may be manipulated.
 *
 * It may be used on its own or managed by a Shell.
 *
 * JLayeredPane functionality is not yet used, hence it behaves as a JPanel,
 * and Connector/Connection overlapping suffers as a result.
 *
 * @author  Dave Voorhis
 */
public class Model extends javax.swing.JScrollPane {

    // Spacing between automatically-placed Visualisers.
    private final int spacing = 20;
    
    // When model size increases, by how much?
    private final int ViewSizeIncrementX = 2048;
    private final int ViewSizeIncrementY = 2048;

    // Cursors
    private final java.awt.Cursor CursorWait = new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR);
    private final java.awt.Cursor CursorMove = new java.awt.Cursor(java.awt.Cursor.MOVE_CURSOR);
    private final java.awt.Cursor CursorDefault = new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR);
    private final java.awt.Cursor CursorHand = new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR);
    
    // panel upon which Visualisers are drawn
    private javax.swing.JLayeredPane modelPane = new javax.swing.JLayeredPane();
    
    // visualiser management
    private Visualiser FocusVisualiser = null;
    private Visualiser DropCandidate = null;
    private Shell theShell = null;
    private int ClickOffsetX = 0;
    private int ClickOffsetY = 0;
    private int RecommendedNewVisualiserX = 0;
    private int RecommendedNewVisualiserY = 0;
    
    // Visualisers under management by this Model
    private java.util.Vector Visualisers = new java.util.Vector();

    // This invisible panel always appears well below and to the right
    // of the lowest, rightmost Visualiser.  Used to force the editable
    // area to a region outside of any Visualiser.
    private javax.swing.JPanel lowerRight = new javax.swing.JPanel();
    
    // Modifier string.
    private String ModifierString = "public class";
    
    /** Ctor */
    public Model() {
        setName("<New>");
        setupModel();
    }
    
    /** Ctor */
    public Model(String title) {
        setName(title);
        setupModel();
    }
        
    /** Get the modifier string.  This is merely a stored property, used
     * by ModelToJava. */
    public String getModifierString() {
        return ModifierString;
    }
    
    /** Set the modifier string. */
    public void setModifierString(String s) {
        ModifierString = s;
    }

    // Changed flag.
    private boolean Changed = false;

    /** Refresh display.  This is kludgey but presently necessary. */
    public void refresh() {
        modelPane.validate();
    }
    
    /** Reset 'Changed' flag */
    public void clearChanged() {
        Changed = false;
    }
    
    /** get 'Changed' flag */
    public boolean isChanged() {
        return Changed;
    }
    
    /** Set 'Changed' flag */
    public void setChanged() {
        Changed = true;
    }
    
    /** Redraw all known Connections. */
    public void redrawConnections() {
        for (int i=0; i<this.getVisualiserCount(); i++)
            getVisualiser(i).redrawConnections();
    }
    
    /** Identify the Shell in charge of this Model. */
    public void setShell(Shell aShell) {
        theShell = aShell;
    }
    
    /** Get Shell in charge of this Model. */
    public Shell getShell() {
        return theShell;
    }
    
    /** Return a visualiser, given its visualiser ID.
     * Return null if not found. */
    public Visualiser getVisualiser(long VisualiserReference) {
        for (int i=0; i<getVisualiserCount(); i++) 
            if (getVisualiser(i).getID()==VisualiserReference)
                    return getVisualiser(i);
        return null;
    }
    
    /** Return quantity of visualisers managed by this Model */
    public int getVisualiserCount() {
        return Visualisers.size();
    }
    
    /** Get the ith visualiser managed by this Model */
    public Visualiser getVisualiser(int i) {
        return (Visualiser)Visualisers.get(i);
    }
    
    /** Get the pane upon which visualisation is drawn */
    public javax.swing.JLayeredPane getModelPane() {
        return modelPane;
    }
    
    /** Add a Visualiser to the Model. */
    public void addVisualiser(Visualiser v) {
        setCursor(CursorWait);
        Visualisers.add(v);
        modelPane.add(v);
        v.setModel(this);       // Tell the visualiser which Model owns it
        int vposX = v.getX();   // Keep it from hiding on the upper or left side
        int vposY = v.getY();
        if (vposX<0)
            vposX = spacing;
        if (vposY<0)
            vposY = spacing;
        v.setLocation(vposX, vposY);
        setModelDimensionsToInclude(v);
        refresh();
        RecommendedNewVisualiserX = v.getX() + spacing;    // update suggested position
        RecommendedNewVisualiserY = v.getY() + spacing;
        if (RecommendedNewVisualiserX > modelPane.getWidth() - spacing)
            RecommendedNewVisualiserX = spacing;
        if (RecommendedNewVisualiserY > modelPane.getHeight() - spacing)
            RecommendedNewVisualiserY = spacing;
        setChanged();
        setCursor(CursorDefault);
    }
    
    /** Remove a Visualiser and its Connections from the Model. */
    public void removeVisualiser(Visualiser v) {
        v.hideProperties();                             // hide properties window
        v.removeConnections();                          // remove connections to visualiser
        modelPane.remove((java.awt.Component)v);        // remove visualiser from pane
        Visualisers.remove(v);                          // remove visualiser from list of visualisers
        refresh();
        setChanged();
    }
    
    /** Get recommended location for new visualisers. */
    public java.awt.Point getRecommendedNewVisualiserPoint() {
        return new java.awt.Point(RecommendedNewVisualiserX, RecommendedNewVisualiserY);
    }
    
    /** Set effective size of editable area */
    public void setModelDimensions(int width, int height) {
        lowerRight.setLocation(width, height);        
    }

    /** Set effective size of editable area to include given Visualiser */
    public void setModelDimensionsToInclude(Visualiser v) {
        if (v.getX()>lowerRight.getX() || v.getY()>lowerRight.getY())
            setModelDimensions(v.getX() + ViewSizeIncrementX, v.getY() + ViewSizeIncrementY);
    }
    
    /** Position view to an absolute position. */
    public void setViewPosition(int x, int y) {
        getHorizontalScrollBar().setValue(x);
        getVerticalScrollBar().setValue(y);
    }
    
    /** Position view to a given location relative to the current location. */
    public void setViewPositionOffset(int offsetX, int offsetY) {
        setViewPosition(offsetX + getHorizontalScrollBar().getValue(),
                        offsetY + getVerticalScrollBar().getValue());
    }
    
    /** Set up visual environment. */
    protected void setupModel() {
    
        // User-defined runtime layout.
        modelPane.setLayout(new ca.mb.armchair.Utilities.Layouts.UserLayout());

        // This invisible panel always appears well below and to the right
        // of the lowest, rightmost Visualiser.  Makes editing more palatable
        // when used within a ScrollPane.
        modelPane.add(lowerRight);

        // Set up JScrollPane behaviour
        setViewportView(modelPane);
        setAutoscrolls(true);
       
        // Mouse listeners to enable interactive editing
        modelPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                doMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                doMouseReleased(evt);
            }
        });        
        modelPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                doMouseDragged(evt);
            }
        });
    }
    
    /** Select all. */
    public void doSelectAll() {
        setSelectAll(true);
    }
    
    /** Select none. */
    public void doSelectNone() {
        setSelectAll(false);
    }
    
    /** Invert selections. */
    public void doSelectInvert() {
        for (int i=0; i<this.getVisualiserCount(); i++)
            getVisualiser(i).setSelected(!getVisualiser(i).isSelected());
    }
    
    /** Delete selections. */
    public void doSelectedDelete() {
        Visualiser[] selected = getSelected();
        for (int i=0; i<selected.length; i++)
            removeVisualiser(selected[i]);
    }
    
    /** Create a new class from selections.  NOTE: Not implemented yet. */
    public void doSelectToNewClass() {
        Log.println("Model: doSelectToNewClass not yet implemented.");
        // setChanged();
    }
    
    /** Get the Visualiser currently in focus.  Null if none. */
    public Visualiser getFocusVisualiser() {
        return FocusVisualiser;
    }
    
    /** Set all Visualisers to a given selection state. */
    public void setSelectAll(boolean selectAll) {
        for (int i=0; i<this.getVisualiserCount(); i++)
            getVisualiser(i).setSelected(selectAll);
    }
    
    /** Set a particular visualiser to be uniquely selected. */
    public void setSelected(Visualiser v) {
        doSelectNone();
        v.setSelected(true);
    }
    
    /** Get count of selections. */
    public int getSelectedCount() {
        int count = 0;
        for (int i=0; i<this.getVisualiserCount(); i++)
            if (getVisualiser(i).isSelected())
                count++;
        return count;
    }
    
    /** Get array of selected visualisers. */
    public Visualiser[] getSelected() {
        Visualiser v[] = new Visualiser[getSelectedCount()];
        int index = 0;
        for (int i=0; i<getVisualiserCount(); i++)
            if (getVisualiser(i).isSelected())
                v[index++] = getVisualiser(i);
        return v;
    }
    
    /** Return number of connections in the entire Model. */
    public int getConnectionCount() {
        int count = 0;
        for (int i=0; i<getVisualiserCount(); i++)
            count += getVisualiser(i).getConnectionCount();
        return count;
    }
    
    /** Move a Visualiser to a given location, adjusting edit area as needed. */
    public void moveVisualiser(Visualiser v, int x, int y) {
        v.setLocation(x, y);
        setModelDimensionsToInclude(v);
        v.redrawConnections();                
    }
    
    // True if a component is a Visualiser.
    private static boolean isVisualiser(java.awt.Component c) {
        return (Visualiser.class.isAssignableFrom(c.getClass()));
    }
    
    // return the component under the mouse
    private java.awt.Component getUnderMouse(java.awt.event.MouseEvent evt) {
        return modelPane.getComponentAt(evt.getPoint());
    }
    
    // true if there is a visualiser under the mouse
    private boolean isVisualiserUnderMouse(java.awt.event.MouseEvent evt) {
        return isVisualiser(getUnderMouse(evt));
    }
    
    // return the visualiser under the mouse, null if there isn't one
    private Visualiser getVisualiserUnderMouse(java.awt.event.MouseEvent evt) {
        java.awt.Component underMouse = getUnderMouse(evt);
        if (isVisualiser(underMouse))
            return (Visualiser)underMouse;
        return null;
    }
    
    // Return true if shift key was held during mouse event
    private boolean isShiftKeyHeld(java.awt.event.MouseEvent evt) {
        int onmask = java.awt.event.MouseEvent.SHIFT_DOWN_MASK;
        int offmask = 0;
        return ((evt.getModifiersEx() & (onmask | offmask)) == onmask);
    }
    
    // Handle mouse press
    private void doMousePressed(java.awt.event.MouseEvent evt) {
        FocusVisualiser = getVisualiserUnderMouse(evt);
        if (FocusVisualiser==null) {
            theShell.showProperties();
            ClickOffsetX = evt.getX();
            ClickOffsetY = evt.getY();
        } else {
            FocusVisualiser.showProperties();
            if (evt.getButton()==1)
                if (isShiftKeyHeld(evt))
                    FocusVisualiser.setSelected(!FocusVisualiser.isSelected());
                else
                    setSelected(FocusVisualiser);
            ClickOffsetX = evt.getX() - FocusVisualiser.getX();
            ClickOffsetY = evt.getY() - FocusVisualiser.getY();
        }
    }
    
    // Find first visualiser (bounded by a given coordinate in the Model) that
    // is a compatible drop target for Dragged Visualiser.
    private Visualiser getPossibleDropTarget(int x, int y, Visualiser DraggedVisualiser) {
        if (DraggedVisualiser.getExposedConnectorCount()>0)
            return null;
        for (int i=0; i<getVisualiserCount(); i++) {
            Visualiser v = getVisualiser(i);
            if (x >= v.getX() && x <= v.getX() + v.getWidth() && y >= v.getY() && y <= v.getY() + v.getHeight())
                if (v.isDropCandidateFor(DraggedVisualiser))
                    return v;
        }
        return null;
    }

    // Handle mouse drag event.
    private void doMouseDragged(java.awt.event.MouseEvent evt) {
        // Handle draggables
        int newX = evt.getX() - ClickOffsetX;
        int newY = evt.getY() - ClickOffsetY;
        if (FocusVisualiser==null) {
            setCursor(CursorHand);
            setViewPositionOffset(-newX, -newY);    // move view
        } else {
            setCursor(CursorMove);
            // Move focus visualiser
            if (!FocusVisualiser.isSelected())
                setSelected(FocusVisualiser);
            int oldX = FocusVisualiser.getX();
            int oldY = FocusVisualiser.getY();
            moveVisualiser(FocusVisualiser, newX, newY);
            int deltaX = FocusVisualiser.getX() - oldX;
            int deltaY = FocusVisualiser.getY() - oldY;
            if (getSelectedCount()>1) {             // move multiple, no d&d
                Visualiser[] moveables = getSelected();
                for (int i=0; i<moveables.length; i++)
                    if (moveables[i] != FocusVisualiser)
                        moveVisualiser(moveables[i], moveables[i].getX() + deltaX, moveables[i].getY() + deltaY);
            } else {                                // d&d support
                Visualiser DropTarget = getPossibleDropTarget(evt.getX(), evt.getY(), FocusVisualiser);
                if (DropCandidate!=null && DropCandidate!=DropTarget)
                    DropCandidate.setDropCandidate(false);
                if (DropTarget!=null) {
                    DropCandidate = DropTarget;
                    DropCandidate.setDropCandidate(true);
                }
            }
            setChanged();
            // Make drag position visible via autoscroll
            java.awt.Rectangle r = new java.awt.Rectangle(evt.getX(), evt.getY(), 1, 1);
            modelPane.scrollRectToVisible(r);
        }
    }

    // Handle mouse release event.
    private void doMouseReleased(java.awt.event.MouseEvent evt) {
        setCursor(CursorDefault);
        if (FocusVisualiser!=null) {
            if (DropCandidate!=null) {
                if (DropCandidate == getPossibleDropTarget(evt.getX(), evt.getY(), FocusVisualiser) && 
                    DropCandidate.receiveDrop(FocusVisualiser))
                        FocusVisualiser = DropCandidate;
                DropCandidate.setDropCandidate(false);
                DropCandidate = null;
            }
            FocusVisualiser.redrawConnections();
            refresh();
        }
    }
}
