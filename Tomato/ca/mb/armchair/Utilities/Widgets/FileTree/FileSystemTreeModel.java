/*
 * FileSystemTreeModel.java
 *
 * Created on October 29, 2002, 9:06 PM
 */

package ca.mb.armchair.Utilities.Widgets.FileTree;

import javax.swing.*;
import javax.swing.tree.*;
import java.io.*;

/**
 * TreeModel for a file system.
 *
 * @author  http://java.sun.com/products/jfc/tsc/articles/jtree/
 * @author  Dave Voorhis
 */

public class FileSystemTreeModel extends AbstractTreeModel implements Serializable {

    private String root;

    /** Default directory. */
    public FileSystemTreeModel() {
        this( System.getProperty( "user.home" ) );
    }

    /** Specified directory. */
    public FileSystemTreeModel(String startPath) {
        root = startPath;
    }

    private java.io.FileFilter theFilter = null;
    
    /** Set file filter. */
    public void setFileFilter(java.io.FileFilter ff) {
        theFilter = ff;
    }
    
    /** Get file filter. */
    public java.io.FileFilter getFileFilter() {
        return theFilter;
    }
    
    public Object getRoot() {
        return new File( root );
    }

    public Object getChild( Object parent, int index ) {
        File directory = (File)parent;
        return directory.listFiles(theFilter)[index];
    }

    public int getChildCount( Object parent ) {
        File fileSysEntity = (File)parent;
        if ( fileSysEntity.isDirectory() )
            return fileSysEntity.listFiles(theFilter).length;
        else
            return 0;
    }

    public boolean isLeaf( Object node ) {
        return ((File)node).isFile();
    }

    public void valueForPathChanged( TreePath path, Object newValue ) {
    }

    public int getIndexOfChild( Object parent, Object child ) {
        File directory = (File)parent;
        File fileSysEntity = (File)child;
        File[] children = directory.listFiles(theFilter);
        int result = -1;
        for (int i = 0; i < children.length; i++) {
            if ( fileSysEntity.getName().equals( children[i].getName() ) ) {
                result = i;
                break;
            }
        }
        return result;
    }

}
