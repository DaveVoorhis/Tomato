/*
 * DirectoryFilter.java
 *
 * Created on August 9, 2002, 2:25 AM
 */

package ca.mb.armchair.Utilities.FileFilters;

/**
 *
 * @author  Dave Voorhis
 */

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * A convenience implementation of FileFilter that filters out
 * all files except for directories.
 */

public class DirectoryFilter extends FileFilter implements java.io.FileFilter {

    String description = null;
    
    /**
     * Creates a file filter that displays only directories.
     */
    public DirectoryFilter() {
    }
    
    /**
     * Creates a file filter that displays only directories.
     */
    public DirectoryFilter(String Description) {
        description = Description;
    }

    /**
     * Return true if this file should be shown in the directory pane,
     * false if it shouldn't.
     */
    public boolean accept(File f) {
	if (f != null)
	    return f.isDirectory();
        else
            return false;
    }

    /**
     * Returns the human readable description of this filter.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the human readable description of this filter.
     */
    public void setDescription(String Description) {
	description = Description;
    }
}
