/*
 * Explorer.java
 *
 * Created on October 15, 2002, 4:10 PM
 */

package ca.mb.armchair.IDE;

import ca.mb.armchair.JVPL.*;
import ca.mb.armchair.Utilities.Widgets.FileTree.*;

/**
 *
 * @author  Dave Voorhis
 */
public class Explorer extends RootPanelTabbed {

    // Fonts
    private final static java.awt.Font ButtonFont = new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 10);
    private final static java.awt.Font ListFontBold = new java.awt.Font("sans-serif", java.awt.Font.BOLD, 10);
    private final static java.awt.Font ListFontPlain = new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 10);

    // Widgets
    private FileSystemTreePanel fstp; 
    private javax.swing.JTree ClassTree;
    private Library theLibrary;
    private java.io.File LibraryFile;
    private javax.swing.JFileChooser jFileChooserLibrary;
    private javax.swing.JPanel ControlPanel;
    
    // Explorer's "Class" session.
    private class ExplorerClassSession implements RootPanelTabbedSession {
        
        private javax.swing.JScrollPane scroller;
        
        /** Ctor */
        public ExplorerClassSession() {
            ClassTree = new javax.swing.JTree(theLibrary.getLibraryModel(Library.ClassSubtreeName));
            ClassTree.setRootVisible(false);
            ClassTree.setShowsRootHandles(true);
            ClassTree.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    tabWasSelected();
                }
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount()==2)
                        loadNode(ClassTree.getSelectionPath());
                    else if (e.getButton()!=e.BUTTON1)
                        rightClickNode(e.getX(), e.getY());
                }
            });
            ClassTree.getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            
            scroller = new javax.swing.JScrollPane(ClassTree);
            scroller.setName(Library.ClassSubtreeName);            
        }

        // Handle right click
        private void rightClickNode(int mouseX, int mouseY) {
            javax.swing.JTree theTree = ClassTree;
            if (theTree.getSelectionPaths()==null)
                return;
            if (theTree.getSelectionPaths().length <= 1)
                theTree.setSelectionPath(theTree.getClosestPathForLocation(mouseX, mouseY));
            else
                theTree.addSelectionPath(theTree.getClosestPathForLocation(mouseX, mouseY));
            
            javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();

            javax.swing.JMenuItem load = createMenuItem("Load");
            load.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectedLoad();
                }
            });
            popup.add(load);
            
            popup.show(theTree, mouseX, mouseY);
        }
        
        // Load class at end of selected path
        private void loadNode(javax.swing.tree.TreePath path) {
            if (path==null)
                return;
            Object o = path.getLastPathComponent();
            if (o instanceof LibraryEntry)
                addLibraryEntryToModel((LibraryEntry)o);            
        }
        
        // Edit selected nodes.
        private void selectedLoad() {
            javax.swing.tree.TreePath[] paths = ClassTree.getSelectionPaths();
            for (int p=0; p<paths.length; p++)
                loadNode(paths[p]);
        }
        
        /** Invoked by RootPanelTabbed to obtain content for Tab.  */
        public java.awt.Component getTabContent() {
            return scroller;
        }
        
        /** Invoked by RootPanelTabbed when tab is selected.  May be
         * used to display properties, or other content.
         */
        public void tabWasSelected() {
            displayControlPanel();
        }        
        
        /** Invoked by RootPanelTabbed when tab is removed.  May be
         * used to save-before-close, and other suchlike.
         */
        public void tabIsClosing() {
        }        
    }
    
    // Explorer's "File Systems" session.
    private class ExplorerFileSession implements RootPanelTabbedSession {
        
        /** Ctor */
        public ExplorerFileSession() {
            FileSystemsTreeModel treeModel = new FileSystemsTreeModel(theLibrary.getMountedDirectories());
            treeModel.setFileFilter(new ca.mb.armchair.Utilities.FileFilters.UserFileFilter(ModelIO.getModelFileExtensions()));
            javax.swing.JTree tree = new javax.swing.JTree(treeModel) {       
                public String convertValueToText(Object value, boolean selected,
                                                 boolean expanded, boolean leaf, int row,
                                                 boolean hasFocus) {                                            
                    if (value instanceof FileSystemsTreeModel)
                        return "FileSystems";
                    else if (value instanceof FileSystemRoot)
                        return ((FileSystemRoot)value).getAbsolutePath();
                    else {
                        java.io.File f = (java.io.File)value;
                        if (f.isFile()) {
                            String fName = f.getPath();
                            int dotposition = fName.lastIndexOf('.');
                            if (dotposition != -1)
                                fName = fName.substring(0, dotposition);
                            java.io.File compiledFile = new java.io.File(fName + ".class");
                            if (!compiledFile.exists() || (f.lastModified() > compiledFile.lastModified()))
                                return getClassName(f.getName()) + " *";
                            else
                                return getClassName(f.getName());
                        } else
                            return f.getName();
                    }
                }
            };
            fstp = new FileSystemTreePanel(tree);
            fstp.getTree().addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    tabWasSelected();
                    displayNode(fstp.getTree().getSelectionPath());
                }
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount()==2)
                        editNode(fstp.getTree().getSelectionPath());
                    else if (e.getButton()!=e.BUTTON1)
                        rightClickNode(e.getX(), e.getY());
                }
            });
            fstp.getTree().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            fstp.setName("File Systems");
        }

        // Compile a node.
        private void compileNode(javax.swing.tree.TreePath path) {
            if (path.getLastPathComponent() instanceof java.io.File)
                compile(getBaseDirectory(path), (java.io.File)path.getLastPathComponent());
        }
        
        // Return true if a file contains a compilable component.
        private boolean isFileCompilable(java.io.File f) {
            if (f.isDirectory()) {
                for (int i=0; i<f.listFiles().length; i++)
                    if (isFileCompilable(f.listFiles()[i]))
                        return true;
                return false;
            } else 
                return ModelIO.isModelFile(f);
        }
        
        // Return true if Node is compilable. */
        private boolean isNodeCompilable(javax.swing.tree.TreePath path) {
            if (path.getLastPathComponent() instanceof java.io.File)
                return isFileCompilable((java.io.File)path.getLastPathComponent());
            else
                return false;
        }
        
        // Get the base directory for a path.
        private java.io.File getBaseDirectory(javax.swing.tree.TreePath path) {
            for (int i=0; i<path.getPathCount(); i++) {
                Object o = path.getPathComponent(i);
                if (o instanceof FileSystemRoot)
                    return (java.io.File)o;
            }
            return null;
        }
        
        // If a node is found in the editor sessions, select its tab.
        private void displayNode(javax.swing.tree.TreePath path) {
            if (path == null)
                return;
            if (path.getLastPathComponent() instanceof java.io.File) {
                java.io.File f = (java.io.File)path.getLastPathComponent();
                if (f.isDirectory())
                    return;
                findEditorSession(getBaseDirectory(path), f);
            }
        }
        
        // If appropriate, display an editor session for a node at the end of a path.
        private void editNode(javax.swing.tree.TreePath path) {
            if (path.getLastPathComponent() instanceof java.io.File) {
                java.io.File f = (java.io.File)path.getLastPathComponent();
                if (f.isDirectory())
                    return;
                displayEditorSession(getBaseDirectory(path), f);
            }
        }
        
        // Return true if Node is editable.
        private boolean isNodeEditable(javax.swing.tree.TreePath path) {
            if (path.getLastPathComponent() instanceof java.io.File) {
                java.io.File f = (java.io.File)path.getLastPathComponent();
                if (f.isFile())
                    return true;
            }
            return false;
        }
        
        // Compile selected nodes.
        private void selectedCompile() {
            Thread CompilingThread = new Thread() {
                public void run() {
                    javax.swing.tree.TreePath[] paths = fstp.getTree().getSelectionPaths();
                    for (int p=0; p<paths.length; p++)
                        compileNode(paths[p]);
                }
            };
            CompilingThread.start();        
        }
        
        // Edit selected nodes.
        private void selectedEdit() {
            javax.swing.tree.TreePath[] paths = fstp.getTree().getSelectionPaths();
            for (int p=0; p<paths.length; p++)
                editNode(paths[p]);
        }

        // True if selection path contains compilable nodes.
        private boolean isSelectionPathCompilable() {
            javax.swing.tree.TreePath[] paths = fstp.getTree().getSelectionPaths();
            for (int p=0; p<paths.length; p++)
                if (isNodeCompilable(paths[p]))
                    return true;
            return false;
        }
        
        // True if selection path contains editable nodes.
        private boolean isSelectionPathEditable() {
            javax.swing.tree.TreePath[] paths = fstp.getTree().getSelectionPaths();
            for (int p=0; p<paths.length; p++)
                if (isNodeEditable(paths[p]))
                    return true;
            return false;
        }

        // Delete selected nodes.  Scary.
        private void selectedDelete() {
            javax.swing.tree.TreePath[] paths = fstp.getTree().getSelectionPaths();
            if (paths.length==0)
                return;
            int retval = javax.swing.JOptionPane.showConfirmDialog(null, "Delete " + paths.length + " files?", getTitle(),
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (retval != javax.swing.JOptionPane.YES_OPTION)
                return;
            for (int p=0; p<paths.length; p++) {
                Object o = paths[p].getLastPathComponent();
                if (o instanceof java.io.File) {
                    java.io.File f = (java.io.File)o;
                    if (f.delete())
                        LogPrintln("Explorer: Deleted file: " + f.getPath());
                    else
                        LogPrintln("Explorer: Unable to delete file: " + f.getPath());
                }
            }
            updateFilesystemTree();
        }
        
        // Handle right click
        private void rightClickNode(int mouseX, int mouseY) {
            javax.swing.JTree theTree = fstp.getTree();
            if (theTree.getSelectionPaths()==null)
                return;
            if (theTree.getSelectionPaths().length <= 1)
                theTree.setSelectionPath(theTree.getClosestPathForLocation(mouseX, mouseY));
            else
                theTree.addSelectionPath(theTree.getClosestPathForLocation(mouseX, mouseY));
            
            javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();

            javax.swing.JMenuItem edit = createMenuItem("Edit");
            edit.setEnabled(isSelectionPathEditable());
            edit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectedEdit();
                }
            });
            popup.add(edit);
            
            javax.swing.JMenuItem newFolder = createMenuItem("New Folder");
            newFolder.setEnabled(false);
            if (theTree.getSelectionPaths().length==1) {
                Object o = fstp.getTree().getLastSelectedPathComponent();
                if (o instanceof java.io.File) {
                    final java.io.File f = (java.io.File)o;
                    if (f.isDirectory()) {
                        newFolder.setEnabled(true);
                        newFolder.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent e) {
                                String newName = ca.mb.armchair.Utilities.Widgets.PopupTextField.getText("New Folder", "Name:");
                                if (newName != null) {
                                    java.io.File newF = new java.io.File(f, newName);
                                    try {
                                        newF.mkdir();
                                    } catch (Throwable t) {
                                        LogPrintln("Explorer: Unable to create " + newF.toString() + ": " + t.toString());
                                    }
                                    updateFilesystemTree();
                                }
                            }
                        });
                    }
                }
            }
            popup.add(newFolder);
            
            javax.swing.JMenuItem newClass = createMenuItem("New Class");
            newClass.setEnabled(false);
            if (theTree.getSelectionPaths().length==1) {
                Object o = fstp.getTree().getLastSelectedPathComponent();
                if (o instanceof java.io.File) {
                    final java.io.File f = (java.io.File)o;
                    if (f.isDirectory()) {
                        newClass.setEnabled(true);
                        newClass.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent e) {
                                String newName = ca.mb.armchair.Utilities.Widgets.PopupTextField.getText("New Class", "Name:");
                                if (newName != null) {
                                    java.io.File newF = new java.io.File(f, newName + "." + ModelIO.getModelFileExtensions()[0]);
                                    javax.swing.tree.TreePath path = fstp.getTree().getSelectionPath();
                                    displayEditorSession(getBaseDirectory(path), newF);
                                }
                            }
                        });
                    }
                }
            }
            popup.add(newClass);

            javax.swing.JMenuItem compile = createMenuItem("Compile");
            compile.setEnabled(isSelectionPathCompilable());
            compile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectedCompile();
                }
            });
            popup.add(compile);

            if (theTree.getSelectionPaths().length==1 && 
                fstp.getTree().getLastSelectedPathComponent() instanceof FileSystemRoot) {
                    javax.swing.JMenuItem unmount = createMenuItem("Unmount");
                    unmount.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            FileSystemRoot fsr = (FileSystemRoot)fstp.getTree().getLastSelectedPathComponent();
                            theLibrary.removeMountedDirectory(fsr.getPath());
                            updateFilesystemTree();
                        }
                    });
                    popup.add(unmount);
            } else {
                javax.swing.JMenuItem delete = createMenuItem("Delete");
                delete.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        selectedDelete();
                    }
                });
                popup.add(delete);
            }
            
            popup.show(fstp, mouseX, mouseY);
        }
        
        /** Invoked by RootPanelTabbed to obtain content for Tab.  */
        public java.awt.Component getTabContent() {
            return fstp;
        }
        
        /** Invoked by RootPanelTabbed when tab is selected.  May be
         * used to display properties, or other content.
         */
        public void tabWasSelected() {
            displayControlPanel();
        }
        
        /** Invoked by RootPanelTabbed when tab is removed.  May be
         * used to save-before-close, and other suchlike.
         */
        public void tabIsClosing() {
        }
        
    }
    
    /** Ctor */
    public Explorer() {
        setName("Explorer");
        setTitle(getName());
        initLibraryFileChooser();
        ide().setTitle(Version.getAppName() + " (" + Version.getVersion() + ")");
        LibraryFile = new java.io.File("Default" + Version.getShortAppName() + "Library." + 
                                                                LibraryIO.getLibraryFileExtensions()[0]);
        theLibrary = getUserDefaultLibrary(LibraryFile);
        ControlPanel = getControlPanel();
        
        // Classes
        addTab(new ExplorerFileSession());

        // File systems
        addTab(new ExplorerClassSession());
    }
        
    // Create menu item.
    private javax.swing.JMenuItem createMenuItem(String text) {
        javax.swing.JMenuItem m = new javax.swing.JMenuItem(text);
        return m;
    }
    
    /** Obtain the class name, stripped of any extensions or leading '.'s. */
    private static String getClassName(String cName) {
        String className = cName;
        if (className.startsWith("."))
            className = cName.substring(1);
        String[] extensions = ModelIO.getModelFileExtensions();
        for (int i=0; i<extensions.length; i++) {
            int lastDotPosition = className.toUpperCase().lastIndexOf("." + extensions[i].toUpperCase());
            if (lastDotPosition != -1)
                return className.substring(0, lastDotPosition);
        }
        return className;
    }

    /** Obtain class name given a base directory and file. */
    private static String getClassName(java.io.File BaseDirectory, java.io.File f) {
        String baseDir = BaseDirectory.getPath().replace(java.io.File.separatorChar, '.');
        String fPath = f.getPath().replace(java.io.File.separatorChar, '.');
        String className = getClassName(fPath);
        if (fPath.toUpperCase().startsWith(baseDir.toUpperCase() + '.'))
            className = getClassName(fPath.substring(baseDir.length() + 1));
        return className;
    }
    
    /** Compile a given file. */
    private void compile(java.io.File BaseDirectory, java.io.File f) {
        if (f.isDirectory()) {
            for (int i=0; i<f.listFiles().length; i++)
                compile(BaseDirectory, new java.io.File(f, f.listFiles()[i].getName()));
        } else {
            if (ModelIO.isModelFile(f)) {
                Shell tabbedShell = findEditorSession(BaseDirectory, f);
                if (tabbedShell == null) {
                    tabbedShell = new Shell(theLibrary, new Model(getClassName(BaseDirectory, f)));
                    tabbedShell.setBaseDirectory(BaseDirectory);
                    tabbedShell.setIDE(ide());
                    tabbedShell.setModelFile(f);
                    tabbedShell.loadModelThreaded();
                } else
                    tabbedShell.saveModelThreaded();
                tabbedShell.CompileToJavaThreaded();
            }
        }
    }
    
    /** Create an editor session. */
    private Shell createEditorSession(java.io.File BaseDirectory, Model m) {
        Shell theShell = new Shell(theLibrary, m);
        theShell.setBaseDirectory(BaseDirectory);
        theShell.setName(m.getName());
        theShell.setIDE(ide());
        ide().getEditor().addTab(theShell);
        return theShell;
    }
    
    /** Locate an editor session. */
    private Shell findEditorSession(java.io.File BaseDirectory, java.io.File f) {
        Editor theEditor = ide().getEditor();
        Shell tabbedShell;
        for (int i=0; i<theEditor.getTabCount(); i++) {
            tabbedShell = (Shell)theEditor.getTab(i);
            if (tabbedShell.getBaseDirectory().equals(BaseDirectory) && 
                tabbedShell.getModel().getName().equals(getClassName(BaseDirectory, f))) {
                    theEditor.setSelectedTab(tabbedShell);
                    return tabbedShell;
            }
        }
        return null;
    }
    
    /** Display an editor session.  Highlight an existing session if it's already
     * in the editor; create a new session if it isn't.  Return the session's Shell. */
    private Shell displayEditorSession(java.io.File BaseDirectory, java.io.File f) {
        Shell tabbedShell = findEditorSession(BaseDirectory, f);
        if (tabbedShell == null) {
            tabbedShell = createEditorSession(BaseDirectory, new Model(getClassName(BaseDirectory, f)));
            tabbedShell.setModelFile(f);
            tabbedShell.loadModelThreaded();
        }
        return tabbedShell;
    }
    
    /** Display the scratchpad session. */
    private Shell displayScratchpadEditorSession() {
        return displayEditorSession(new java.io.File(java.lang.System.getProperty("user.home")),    // base dir 
                                    new java.io.File("Scratchpad.vpl"));                            // class
    }
    
    /** Display the control panel. */
    private void displayControlPanel() {
        ide().displayControlPanel(ControlPanel, theLibrary.getName());
    }
    
    /** Build and return the control panel. */
    private javax.swing.JPanel getControlPanel() {
        javax.swing.JPanel newControlPanel = new javax.swing.JPanel();
        
        newControlPanel.setLayout(new java.awt.BorderLayout());
        newControlPanel.add(getLibraryIDE(), java.awt.BorderLayout.NORTH);
        newControlPanel.add(getModelMakerPanel(), java.awt.BorderLayout.SOUTH);
        newControlPanel.add(new LibraryControlPanel(this), java.awt.BorderLayout.CENTER);

        return newControlPanel;
    }
    
    /** Set current library. */
    public void setLibrary(Library l) {
        theLibrary = l;
    }

    /** Get current library. */
    public Library getLibrary() {
        return theLibrary;
    }
    
    /** Get default library. */
    public static Library getDefaultLibrary() {
        Library l = new Library("Default");
        try {
            l.addClass(IDE.class);
        } catch (Throwable e) {
            LogPrintln("Explorer: Unable to load default classes into library: " + e.toString());
        }
        return l;
    }
    
    /** Load user's default library from given file.  Create it if necessary. */
    public static Library getUserDefaultLibrary(java.io.File LibraryFile) {
        Library l = LibraryIO.loadLibrary(LibraryFile);
        if (l != null)
            return l;
        else {
            Splash.println(il8n._("Creating new default library."));
            l = getDefaultLibrary();
            Splash.println(il8n._("Saving new default library."));
            LibraryIO.saveLibrary(l, LibraryFile);
        }
        return l;
    }
    
    /** Save current library to file. */
    public boolean saveLibrary(java.io.File theFile) {
        if (LibraryIO.saveLibrary(theLibrary, theFile)) {
            setLibraryFile(theFile);
            return true;
        }
        return false;
    }
        
    /** Load current library from a file. */
    public boolean loadLibrary(java.io.File f) {
        Library l = LibraryIO.loadLibrary(f);
        if (l != null) {
            setLibrary(l);
            setLibraryFile(f);
            return true;
        }
        return false;
    }
    
    /** Create an empty library, and set the current library to it. */
    public void clearLibrary() {
        setLibrary(new Library("Classes"));
        setLibraryFile(null);
    }
        
    /** Get current library file. */
    public java.io.File getLibraryFile() {
        return LibraryFile;
    }
    
    // Set library file.
    private void setLibraryFile(java.io.File f) {
        LibraryFile = f;
    }

    private javax.swing.JPanel getLibraryIDE() {
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();

        javax.swing.JButton jButtonLibraryNew = new javax.swing.JButton();
        jButtonLibraryNew.setText(il8n._("New"));
        jButtonLibraryNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doNewLibrary();
            }
        });
        jPanel3.add(jButtonLibraryNew);

        javax.swing.JButton jButtonLibraryOpen = new javax.swing.JButton();
        jButtonLibraryOpen.setText(il8n._("Open Project") + "...");
        jButtonLibraryOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doOpenLibrary();
            }
        });
        jPanel3.add(jButtonLibraryOpen);

        javax.swing.JButton jButtonLibrarySave = new javax.swing.JButton();        
        jButtonLibrarySave.setText(il8n._("Save Project"));
        jButtonLibrarySave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSaveLibrary();
            }
        });
        jPanel3.add(jButtonLibrarySave);

        javax.swing.JButton jButtonLibrarySaveAs = new javax.swing.JButton();        
        jButtonLibrarySaveAs.setText(il8n._("Save Project As") + "...");
        jButtonLibrarySaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSaveAsLibrary();
            }
        });
        jPanel3.add(jButtonLibrarySaveAs);
        
        return jPanel3;
    }
    
    /** Get Library title. */
    public String getLibraryTitle() {
        if (getLibraryFile()==null)
            return "<new>";
        else
            return getLibraryFile().getName();
    }

    // Window is closing, last chance to save.
    // Save - save library
    // Discard - NOP
    public void promptForSaveLibraryUrgent() {
        if (!getLibrary().isChanged())
            return;
        while (true) {
            int retval = javax.swing.JOptionPane.showConfirmDialog(null, getLibraryTitle() + " " + 
                                    il8n._("has changed.  Save it?"), getTitle(),
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (retval == javax.swing.JOptionPane.YES_OPTION)
                    if (doSaveLibrary())
                        return;
            if (retval == javax.swing.JOptionPane.NO_OPTION)
                return;
        }
    }
    
    // Ask users if they wish to save library
    // Save - save library and return true
    // Discard - return true
    // Cancel - return false
    private boolean promptForSaveLibrary() {
        if (!getLibrary().isChanged())
            return true;
        int retval = javax.swing.JOptionPane.showConfirmDialog(null, getLibraryTitle() + " " + 
                                    il8n._("has changed.  Save it?"), getTitle(), 
            javax.swing.JOptionPane.YES_NO_CANCEL_OPTION,
            javax.swing.JOptionPane.QUESTION_MESSAGE);
        switch (retval) {
            case javax.swing.JOptionPane.CANCEL_OPTION: 
                return false;
            case javax.swing.JOptionPane.YES_OPTION:
                doSaveLibrary();
            default:
                return true;
        }
    }

    private boolean doNewLibrary() {
        if (!promptForSaveLibrary())
            return false;
        clearLibrary();
        updateClassTree();
        return true;
    }

    private void doOpenLibrary() {
        int returnVal = jFileChooserLibrary.showOpenDialog(null);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            if (!promptForSaveLibrary())
                return;
            loadLibrary(jFileChooserLibrary.getSelectedFile());
            updateClassTree();
        }
    }

    private boolean doSaveLibrary() {
        boolean retval = true;
        if (getLibraryFile()==null)
           retval = doSaveAsLibrary();
        else
            saveLibrary(getLibraryFile());
        return retval;
    }
    
    private boolean doSaveAsLibrary() {
        if (getLibraryFile()!=null)
            jFileChooserLibrary.setSelectedFile(getLibraryFile());
        int returnVal = jFileChooserLibrary.showSaveDialog(null);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            saveLibrary(jFileChooserLibrary.getSelectedFile());
            return true;
        }
        return false;
    }
    
    // Add selected tree nodes to Model as editable inner classes
    private void addSelectedLibraryDerivationEntriesToModel() {
        LogPrintln("Explorer: addSelectedLibraryDerivationEntriesToModel() not implemented yet.");
    }
    
    private void initLibraryFileChooser() {
        jFileChooserLibrary = new javax.swing.JFileChooser();
        jFileChooserLibrary.setFileHidingEnabled(false);
        jFileChooserLibrary.addChoosableFileFilter(
            new ca.mb.armchair.Utilities.FileFilters.UserFileFilter(LibraryIO.getLibraryFileExtensions(), 
                Version.getShortAppName() + " " + il8n._("Library Files")));
    }

    private void addNewClassToModel() {
        LogPrintln("Explorer: addNewClassToModel() not implemented yet.");
    }
    
    private javax.swing.JPanel getModelMakerPanel() {
        javax.swing.JPanel adderPanel = new javax.swing.JPanel();
        adderPanel.setLayout(new java.awt.FlowLayout());
        
        javax.swing.JButton jButtonNewClassToModel = new javax.swing.JButton(il8n._("New Class"));
        jButtonNewClassToModel.setFont(ButtonFont);
        jButtonNewClassToModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewClassToModel();
            }
        });        
        adderPanel.add(jButtonNewClassToModel);
        
        javax.swing.JButton jButtonLibraryDerivationToModel = new javax.swing.JButton(il8n._("New Derivation"));
        jButtonLibraryDerivationToModel.setFont(ButtonFont);
        jButtonLibraryDerivationToModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSelectedLibraryDerivationEntriesToModel();
            }
        });        
        adderPanel.add(jButtonLibraryDerivationToModel);
        
        return adderPanel;
    }
    
    /** Refresh a tree.  Would be better handled as a tree listener. */
    private void refreshTree(javax.swing.JTree tree) {
        javax.swing.tree.TreePath selections[] = tree.getSelectionPaths();
        javax.swing.tree.TreeModel m = tree.getModel();
        tree.setModel(null);
        tree.setModel(m);
        tree.setSelectionPaths(selections);
    }
    
    /** Update tree.  Called explicitly by LibraryImportControlPanel.  Would be better
     * handled as a tree listener of some sort. */
    public synchronized void updateClassTree() {
        refreshTree(ClassTree);
    }

    /** Update tree.  Called explicitly by LibraryImportControlPanel.  Would be better
     * handled as a tree listener of some sort. */
    public synchronized void updateFilesystemTree() {
        refreshTree(fstp.getTree());
    }
    
    // Add a library entry to Model
    private void addLibraryEntryToModel(LibraryEntry node) {
        if (node!=null && node.isLeaf()) {
            setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            String theClassName = node.getLoadableClassName();
            if (theClassName!=null) {
                // Note use of (String) cast to force selection of correct overloaded method
                Visualiser v = VisualiserFactory.newVisualiser(getLibrary(), (String)theClassName);
                if (v!=null) {
                    Shell s = (Shell)ide().getEditor().getSelectedTab();
                    if (s == null)
                        s = displayScratchpadEditorSession();         // use scratchpad
                    Model m = s.getModel();
                    v.setLocation(m.getRecommendedNewVisualiserPoint());
                    v.instantiate();
                    m.addVisualiser(v);
                    getLibrary().addRecentlyUsed(node);
                    updateClassTree();
                    s.populateVisualiserList();
                } else
                    LogPrintln("Explorer: Unable to create a visualiser for " + theClassName);
            }
            setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
    }
    
    // Edit selected tree node.
    private void editSelectedLibraryEntry(LibraryEntry le) {
        LogPrintln("Explorer: editSelectedLibraryEntry not implemented yet.");
    }
    
    // Add selected tree nodes to Model
    private void addSelectedLibraryEntriesToModel() {
        javax.swing.tree.TreePath paths[] = ClassTree.getSelectionPaths();
        if (paths!=null)
            for (int i=0; i<paths.length; i++)
                addLibraryEntryToModel((LibraryEntry)(paths[i].getLastPathComponent()));
    }
    
    /** Update the explorer, because something the explorer displays has been updated. */
    public void refresh() {
        updateFilesystemTree();
        updateClassTree();
    }
}
