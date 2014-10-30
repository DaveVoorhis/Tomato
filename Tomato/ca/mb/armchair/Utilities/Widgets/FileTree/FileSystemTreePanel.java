/*
 * FileSystemTreePanel.java
 *
 * Created on October 29, 2002, 9:06 PM
 */

package ca.mb.armchair.Utilities.Widgets.FileTree;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import java.io.*;
import java.awt.*;

/**
 * A panel that contains a JTree of a filesystem.
 *
 * @author  http://java.sun.com/products/jfc/tsc/articles/jtree/
 * @author  Dave Voorhis
 */

public class FileSystemTreePanel extends JPanel {
    private JTree tree;

    public FileSystemTreePanel() {
        this( new FileSystemTreeModel() );
    }

    public FileSystemTreePanel( String startPath ) {
        this( new FileSystemTreeModel( startPath ) );
    }
    
    public FileSystemTreePanel( java.util.Vector startPaths ) {
        this( new FileSystemsTreeModel( startPaths ) );
    }
    
    public FileSystemTreePanel( String[] startPaths ) {
        this( new FileSystemsTreeModel( startPaths ) );
    }
    
    public FileSystemTreePanel( FileSystemTreeModel model ) {
        this( new JTree( model ) {       
            public String convertValueToText(Object value, boolean selected,
                                             boolean expanded, boolean leaf, int row,
                                             boolean hasFocus) {
                return ((File)value).getName();
            }
        });
    }
    
    public FileSystemTreePanel( FileSystemsTreeModel model ) {
        this( new JTree( model ) {       
            public String convertValueToText(Object value, boolean selected,
                                             boolean expanded, boolean leaf, int row,
                                             boolean hasFocus) {                                            
                if (value instanceof FileSystemsTreeModel)
                    return "FileSystems";
                else if (value instanceof FileSystemRoot)
                    return ((FileSystemRoot)value).getAbsolutePath();
                else
                    return ((File)value).getName();
            }
        });
    }
    
    public FileSystemTreePanel( JTree aTree ) {
        tree = aTree;
        tree.setLargeModel( true );        
        tree.setRootVisible( false );
        tree.setShowsRootHandles( true );
        tree.putClientProperty( "JTree.lineStyle", "Angled" );

        setLayout( new BorderLayout() );
        add( new javax.swing.JScrollPane(tree), BorderLayout.CENTER );
    }
    
    public JTree getTree() {
       return tree;
    }
}
