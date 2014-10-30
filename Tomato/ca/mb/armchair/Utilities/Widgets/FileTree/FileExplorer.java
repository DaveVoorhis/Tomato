/*
 * FileExplorer.java
 *
 * Created on October 29, 2002, 9:06 PM
 */

package ca.mb.armchair.Utilities.Widgets.FileTree;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

/**
 * Demonstration of FileSystemTreeModel, FileSystemTreePanel, etc.
 *
 * @author  http://java.sun.com/products/jfc/tsc/articles/jtree/
 * @author  Dave Voorhis
 */

public class FileExplorer {    
    public static void main( String[] argv ) {
        JFrame frame = new JFrame( "File Explorer" );

        frame.addWindowListener( new WindowAdapter() {
                                     public void windowClosing( WindowEvent e ) {
                                         System.exit( 0 );
                                     }
                                 });

        // Multiple root example
        java.util.Vector model = new java.util.Vector();
        model.add(new String(System.getProperty("user.home")));
        model.add(new String("/"));         // not exactly portable to use '/', but this IS a demo...

        // Single root example
//        FileSystemTreeModel model = new FileSystemTreeModel();
                                 
        FileSystemTreePanel fileTree = new FileSystemTreePanel( model );

        JScrollPane treeScroller = new JScrollPane( fileTree );

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add( treeScroller, BorderLayout.CENTER );

        frame.pack();
        frame.show();
        frame.setSize(400, 400);
    }
}
