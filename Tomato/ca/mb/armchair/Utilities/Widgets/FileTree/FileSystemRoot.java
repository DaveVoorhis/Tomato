/*
 * FileSystemRoot.java
 *
 * Created on October 29, 2002, 9:06 PM
 */

package ca.mb.armchair.Utilities.Widgets.FileTree;

/**
 * A derivation of java.io.File that exists only to distinguish a
 * file within a filesystem from a root directory of a filesystem.
 * Used by FileSystemsTreeModel.
 *
 * @author  Dave Voorhis
 */

public class FileSystemRoot extends java.io.File {
    public FileSystemRoot(String name) {
        super(name);
    }
}
