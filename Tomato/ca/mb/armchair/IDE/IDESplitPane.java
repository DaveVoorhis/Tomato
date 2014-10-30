/*
 * IDESplitPane.java
 *
 * Created on October 24, 2002, 5:02 PM
 */

package ca.mb.armchair.IDE;

/**
 * A standard JSplitPane with extensions to programmatically minimize,
 * maximized, and restore to standard size.
 *
 * @author  Dave Voorhis
 */
public class IDESplitPane extends javax.swing.JSplitPane {
    
    // Zoom states
    public static final int ZOOM_NORMALIZED = 0;
    public static final int ZOOM_TOP = 1;
    public static final int ZOOM_LEFT = 1;
    public static final int ZOOM_BOTTOM = 2;
    public static final int ZOOM_RIGHT = 2;    
    
    // Current state.
    private int Zoom;
    
    // Normal position.
    private int ZoomNormalPosition;
    
    /** Creates a new instance of IDESplitPane */
    public IDESplitPane() {
        Zoom = ZOOM_NORMALIZED;
        ZoomNormalPosition = getDividerLocation();
    }
    
    /** Get max/min/normal state. */
    public int getZoom() {
        return Zoom;
    }
    
    /** Set max/min/normal state. */
    public void setZoom(int n) {
        if (Zoom == ZOOM_NORMALIZED)
            ZoomNormalPosition = getDividerLocation();
        Zoom = n;
        switch (Zoom) {
            case ZOOM_NORMALIZED:
                setDividerLocation(ZoomNormalPosition);
                break;
            case ZOOM_TOP:
                setDividerLocation(1.0F);
                break;
            case ZOOM_BOTTOM:
                setDividerLocation(0);
                break;
        }
    }
    
    /** Move to next Zoom state. */
    public void nextZoom() {
        switch (Zoom) {
            case ZOOM_NORMALIZED:
                setZoom(ZOOM_TOP);
                break;
            case ZOOM_TOP:
                setZoom(ZOOM_BOTTOM);
                break;
            case ZOOM_BOTTOM:
                setZoom(ZOOM_NORMALIZED);
                break;
        }
    }
}
