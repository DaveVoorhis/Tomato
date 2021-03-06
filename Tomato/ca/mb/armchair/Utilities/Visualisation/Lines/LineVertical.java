/*
 * LineVertical.java
 *
 * Created on July 18, 2002, 3:56 PM
 */

package ca.mb.armchair.Utilities.Visualisation.Lines;

/**
 *
 * @author  creatist
 */
public class LineVertical extends Block {
    
    /** Creates a new instance of VerticalLine */
    public LineVertical(int x, int y1, int y2, int lineWidth) {
        super(x, (y1<y2) ? y1 : y2, lineWidth, Math.abs(y1 - y2));
    }
    
    // Creates a new instance of vertical line
    public LineVertical() {
    }
    
    // Set the line
    public void setLine(int x, int y1, int y2, int lineWidth) {
        setBlock(x, (y1<y2) ? y1 : y2, lineWidth, Math.abs(y1 - y2));
    }
}
