/*
 * LibraryControlPanel.java
 *
 * Created on August 12, 2002, 4:02 AM
 */

package ca.mb.armchair.IDE;

import ca.mb.armchair.JVPL.*;

/**
 * Panel for managing a library.  This is as crude as a sack of rocks.
 *
 * @author  Dave Voorhis
 */
public class LibraryControlPanel extends javax.swing.JPanel {
    
    // Library
    private Library theLibrary;
    
    // Explorer
    private Explorer theExplorer;
    
    // Widgets
    private javax.swing.JTextField jTextFieldStatus;
    private javax.swing.JButton jButtonLoadClassName;
    private javax.swing.JButton jButtonLoadDefault;
    
    /** Creates new form ShellControlPanelLibraryImport */
    public LibraryControlPanel(Explorer ex) {
        theExplorer = ex;
        theLibrary = ex.getLibrary();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        add(getCompilerTypePanel());
        add(getImportClassNamePanel());
        add(getDefaultClassesPanel());
        add(getMountDirectoryPanel());
        add(getStatusPanel());
    }
    
    // Get 'compiler type' panel
    private javax.swing.JPanel getCompilerTypePanel() {
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        
        javax.swing.ButtonGroup buttonGroupCompilerType = new javax.swing.ButtonGroup();

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));
        jPanel1.setBorder(new javax.swing.border.TitledBorder(il8n._("Compiler")));
        
        javax.swing.JRadioButton jRadioJavac = new javax.swing.JRadioButton("javac");
        if (theLibrary.getCompiler()==Library.COMPILER_JAVAC)
            jRadioJavac.setSelected(true);
        jRadioJavac.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioJavac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                theLibrary.setCompiler(Library.COMPILER_JAVAC);
            }
        });
        buttonGroupCompilerType.add(jRadioJavac);
        jPanel1.add(jRadioJavac);
       
        javax.swing.JRadioButton jRadioJikes = new javax.swing.JRadioButton("jikes");
        if (theLibrary.getCompiler()==Library.COMPILER_JIKES)
            jRadioJikes.setSelected(true);
        jRadioJikes.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioJikes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                theLibrary.setCompiler(Library.COMPILER_JIKES);
            }
        });
        buttonGroupCompilerType.add(jRadioJikes);
        jPanel1.add(jRadioJikes);
       
        return jPanel1;
    }
    
    // Get 'import default classes' panel
    private javax.swing.JPanel getDefaultClassesPanel() {
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));

        jPanel1.add(new javax.swing.JLabel(il8n._("Default " + Version.getShortAppName() + " Classes") + ": "));
        
        jButtonLoadDefault = new javax.swing.JButton();
        jButtonLoadDefault.setText(il8n._("Load"));
        jButtonLoadDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IDE.getIDE().LogPrintln("ShellControlPanelLibraryImport: jButtonLoadDefaultActionPerformed not implemented yet.");
            }
        });

        jPanel1.add(jButtonLoadDefault);
        
        return jPanel1;
    }
    
    // Get 'import class name' panel
    private javax.swing.JPanel getImportClassNamePanel() {
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel2.add(new javax.swing.JLabel(il8n._("Class name") + ": "), java.awt.BorderLayout.WEST);

        final javax.swing.JButton jButtonLoadClassName = new javax.swing.JButton();

        final javax.swing.JTextField jTextFieldClassName = new javax.swing.JTextField();
        jTextFieldClassName.setMinimumSize(new java.awt.Dimension(10, 20));
        jTextFieldClassName.setPreferredSize(new java.awt.Dimension(320, 20));
        jTextFieldClassName.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jButtonLoadClassName.setEnabled(jTextFieldClassName.getText().length()>0);
            }
        });

        jPanel2.add(jTextFieldClassName, java.awt.BorderLayout.CENTER);

        jButtonLoadClassName.setText(il8n._("Load"));
        jButtonLoadClassName.setEnabled(false);
        jButtonLoadClassName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    theLibrary.addClass((String)jTextFieldClassName.getText());
                    theExplorer.updateClassTree();
                    jTextFieldClassName.setText("");
                    jTextFieldStatus.setText(jTextFieldClassName.getText() + " " + il8n._("added to library"));
                } catch (Throwable t) {
                    jTextFieldStatus.setText(t.getLocalizedMessage());
                }
            }
        });

        jPanel2.add(jButtonLoadClassName, java.awt.BorderLayout.EAST);
        
        return jPanel2;
    }

    // Get a status panel
    private javax.swing.JPanel getStatusPanel() {
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        
        jPanel3.setLayout(new java.awt.BorderLayout());

        jTextFieldStatus = new javax.swing.JTextField();
        jTextFieldStatus.setMinimumSize(new java.awt.Dimension(10, 20));
        jTextFieldStatus.setPreferredSize(new java.awt.Dimension(320, 100));
        jTextFieldStatus.setEnabled(false);

        jPanel3.add(jTextFieldStatus, java.awt.BorderLayout.CENTER);
        
        return jPanel3;
    }

    private javax.swing.JFileChooser getDirectoryChooser() {
        javax.swing.JFileChooser jFileChooserDirectory = new javax.swing.JFileChooser();
        jFileChooserDirectory.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        jFileChooserDirectory.setFileHidingEnabled(false);
        jFileChooserDirectory.addChoosableFileFilter(
            new ca.mb.armchair.Utilities.FileFilters.DirectoryFilter(il8n._("Directory")));
        return jFileChooserDirectory;
    }
    
    // Get a mount directory panel.
    private javax.swing.JPanel getMountDirectoryPanel() {
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        
        jPanel2.setLayout(new java.awt.BorderLayout());

        final javax.swing.JFileChooser jFileChooserDirectory = getDirectoryChooser();
        
        javax.swing.JButton jButtonMount = new javax.swing.JButton();
        jButtonMount.setText(il8n._("Mount Directory"));
        jButtonMount.setEnabled(true);
        jButtonMount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int returnVal = jFileChooserDirectory.showOpenDialog(null);
                if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                    try {
                        theLibrary.addMountedDirectory(jFileChooserDirectory.getSelectedFile().getPath());
                        theLibrary.addDirectory(jFileChooserDirectory.getSelectedFile());
                        theExplorer.updateFilesystemTree();
                        jTextFieldStatus.setText(il8n._(jFileChooserDirectory.getSelectedFile() + " added to library"));
                    } catch (Throwable t) {
                        jTextFieldStatus.setText(t.getLocalizedMessage());
                        IDE.getIDE().printStackTrace(t.getStackTrace());
                    }
                }
            }
        });

        jPanel2.add(jButtonMount, java.awt.BorderLayout.CENTER);
        
        return jPanel2;
    }
}

