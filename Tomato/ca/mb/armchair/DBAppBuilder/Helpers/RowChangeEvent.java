/*
 * RowChangeEvent.java
 *
 * Created on January 30, 2002, 7:45 PM
 */

package ca.mb.armchair.DBAppBuilder.Helpers;

import ca.mb.armchair.DBAppBuilder.Beans.*;

/**
 * A row change event.  Primarily issued by DBRow.
 *
 * @author  creatist
 */
public class RowChangeEvent extends java.util.EventObject {
    
    private String EventDescription;
    
    /** Creates a new instance of RowChangeEvent */
    public RowChangeEvent(DBRow dbr, String s) {
        super(dbr);
        setEventDescription(s);
    }

    /** Obtain event ID */
    public String getEventDescription() {
        return EventDescription;
    }
    
    /** Set event ID */
    public void setEventDescription(String s) {
        EventDescription = s;
    }
    
    /** Obtain string representation of event */
    public String toString() {
        return getEventDescription();
    }
    
}
