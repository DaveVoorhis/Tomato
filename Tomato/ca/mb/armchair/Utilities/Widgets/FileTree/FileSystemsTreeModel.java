/*
 * FileSystemsTreeModel.java
 *
 * Created on October 31, 2002, 7:12 PM
 */

package ca.mb.armchair.Utilities.Widgets.FileTree;

import javax.swing.*;
import javax.swing.tree.*;
import java.io.*;

/**
 * TreeModel for a group of file systems.
 *
 * @author  Dave Voorhis
 */

public class FileSystemsTreeModel extends AbstractTreeModel implements Serializable {

    private java.util.Vector Directories;

    /** Specified directories */
    public FileSystemsTreeModel(java.util.Vector DirectoryList) {
        Directories = DirectoryList;
    }
    
    /** Specified directories */
    public FileSystemsTreeModel(String[] DirectoryList) {
        for (int i=0; i<DirectoryList.length; i++)
            Directories.add(DirectoryList[i]);
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
        return this;
    }

    public Object getChild( Object parent, int index ) {
        if (parent instanceof FileSystemsTreeModel) {
            FileSystemsTreeModel fstm = (FileSystemsTreeModel)parent;
            return new FileSystemRoot((String)(fstm.Directories.get(index)));
        } else {
            File directory = (File)parent;
            return directory.listFiles(theFilter)[index];
        }
    }

    public int getChildCount( Object parent ) {
        if (parent instanceof FileSystemsTreeModel) {
            FileSystemsTreeModel fstm = (FileSystemsTreeModel)parent;
            return fstm.Directories.size();
        } else {
            File fileSysEntity = (File)parent;
            if (fileSysEntity.isDirectory())
                return fileSysEntity.listFiles(theFilter).length;
            else
                return 0;
        }
    }

    public boolean isLeaf( Object node ) {
        if (node instanceof FileSystemsTreeModel)
            return false;
        else
            return ((File)node).isFile();
    }

    public void valueForPathChanged( TreePath path, Object newValue ) {
    }

    public int getIndexOfChild( Object parent, Object child ) {
        if (parent instanceof FileSystemsTreeModel) {
            File fileSysEntity = (File)child;
            FileSystemsTreeModel fstm = (FileSystemsTreeModel)parent;
            for (int i=0; i<fstm.Directories.size(); i++)
                if (fileSysEntity.getName().equals((String)Directories.get(i)))
                    return i;
        } else {
            File directory = (File)parent;
            File fileSysEntity = (File)child;
            File[] children = directory.listFiles(theFilter);
            for (int i=0; i<children.length; i++)
                if (fileSysEntity.getName().equals(children[i].getName()))
                    return i;
        }
        return -1;
    }

}
