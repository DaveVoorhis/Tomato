/*
 * Shell.java
 *
 * Created on June 8, 2002, 2:27 AM
 */

package ca.mb.armchair.JVPL;

import java.io.*;

/**
 * A Shell provides a wrapper around a Model allowing it to be
 * controlled by an IDE.
 *
 * This should probably establish itself in a new VM, but currently doesn't.
 *
 * @author  Dave Voorhis
 */
 
public class Shell extends javax.swing.JPanel implements ca.mb.armchair.IDE.RootPanelTabbedSession {

    // Fonts
    private final static java.awt.Font ButtonFont = new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 10);
    private final static java.awt.Font ListFontPlain = new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 10);

    // The Model we're controlling.
    private Model theModel = null;
    
    // Current Model file
    private java.io.File ModelFile = null;

    // We're managed by this IDE.
    private ca.mb.armchair.IDE.IDEInterface theIDE = null;

    // Thread for background saving and loading
    private java.lang.Thread LoadingOrSavingThread = null;

    // List of visualisers managed by the model
    private javax.swing.JList jListVisualisers;
        
    // Progress display
    private ca.mb.armchair.Utilities.Widgets.ProgressPanel ProgressPanel = new ca.mb.armchair.Utilities.Widgets.ProgressPanel(); 
    
    // Library
    private Library theLibrary;
    
    /** Create new Shell for a given Model, using a given Library.  Presumably,
     * the Model will be explicitly added to some Container and displayed, but
     * need not be. */
    public Shell(Library l, Model w) {
        setModel(w);
        setLibrary(l);

        setLayout(new java.awt.BorderLayout());
        add(getModelIOPanel(), java.awt.BorderLayout.NORTH);
        add(getModelButtons(), java.awt.BorderLayout.SOUTH);
        
        jListVisualisers = new javax.swing.JList();
        jListVisualisers.setFont(ListFontPlain);
        jListVisualisers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                theModel.doSelectNone();
                Object[] selections = jListVisualisers.getSelectedValues();
                for (int i=0; i<selections.length; i++) {
                    Visualiser v = (Visualiser)selections[i];
                    v.setSelected(true);
                }
            }
        });        
        
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane1.setViewportView(jListVisualisers);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        // Add to registry.
        addShellToRegistry();

        // Redraw connections.
        theModel.redrawConnections();
    }

    /** Set Library used by this Shell. */
    public void setLibrary(Library l) {
        theLibrary = l;
    }
    
    /** Get library used by this Shell. */
    public Library getLibrary() {
        return theLibrary;
    }
    
    /** Set IDE to manage this Shell. */
    public void setIDE(ca.mb.armchair.IDE.IDEInterface ide) {
        theIDE = ide;
    }
    
    /** Get the IDE that manages this Shell. */
    public ca.mb.armchair.IDE.IDEInterface getIDE() {
        return theIDE;
    }
    
    /** Display control panel. */
    public void displayControlPanel(javax.swing.JPanel ControlPanel, String Title) {
        if (getIDE() != null)
            getIDE().displayControlPanel(ControlPanel, Title);
    }

    /** Display overall properties for the Model. */
    public void showProperties() {
        displayControlPanel(this, theModel.getName());
    }
    
    // Handle notification that Model is closing. 
    private void doModelClosing() {
        promptForSaveModelUrgent();
        removeShellFromRegistry();
    }
    
    // Update Model title
    private void updateModelTitle() {
        setBorder(new javax.swing.border.TitledBorder(getModelTitle()));
    }
    
    /** Get Model title. */
    public String getModelTitle() {
        if (getModelFile()==null)
            return "<new>";
        else
            return getModelFile().getName();
    }
    
    /** Populate list of visualisers. */
    public void populateVisualiserList() {
        javax.swing.DefaultListModel listModelVisualisers = new javax.swing.DefaultListModel();
        jListVisualisers.setModel(listModelVisualisers);
        int selectedIndices[] = new int[theModel.getSelected().length];
        int selectionCount = 0;
        for (int i=0; i<theModel.getVisualiserCount(); i++) {
            Visualiser v = theModel.getVisualiser(i);
            listModelVisualisers.addElement(v);
            if (v.isSelected()) {
                selectedIndices[selectionCount++] = i;
                jListVisualisers.setSelectedIndex(i);
            }
        }
        jListVisualisers.setSelectedIndices(selectedIndices);
    }
    
    // Build panel of Model controls
    private javax.swing.JPanel getModelButtons() {
        
        javax.swing.JPanel ModelPanel = new javax.swing.JPanel();
        ModelPanel.setLayout(new java.awt.BorderLayout());
        
        javax.swing.JPanel j = new javax.swing.JPanel();
        
        javax.swing.JButton jButtonNewClass = new javax.swing.JButton(il8n._("Selection -> New Class") + "...");
        j.add(jButtonNewClass);
        jButtonNewClass.setFont(ButtonFont);
        jButtonNewClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                theModel.doSelectToNewClass();
                populateVisualiserList();
            }
        });
        
        javax.swing.JButton jButtonSelectAll = new javax.swing.JButton(il8n._("Select All"));
        j.add(jButtonSelectAll);
        jButtonSelectAll.setFont(ButtonFont);
        jButtonSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                theModel.doSelectAll();
                populateVisualiserList();
            }
        });
        
        javax.swing.JButton jButtonSelectNone = new javax.swing.JButton(il8n._("Select None"));
        j.add(jButtonSelectNone);
        jButtonSelectNone.setFont(ButtonFont);
        jButtonSelectNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                theModel.doSelectNone();
                populateVisualiserList();
            }
        });
        
        javax.swing.JButton jButtonSelectInvert = new javax.swing.JButton(il8n._("Invert selection"));
        j.add(jButtonSelectInvert);
        jButtonSelectInvert.setFont(ButtonFont);
        jButtonSelectInvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                theModel.doSelectInvert();
                populateVisualiserList();
            }
        });
        
        javax.swing.JButton jButtonDeleteSelected = new javax.swing.JButton(il8n._("Deleted Selected"));
        j.add(jButtonDeleteSelected);
        jButtonDeleteSelected.setFont(ButtonFont);
        jButtonDeleteSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                theModel.doSelectedDelete();
                populateVisualiserList();
            }
        });
        
        ModelPanel.add(j, java.awt.BorderLayout.CENTER);
        
        javax.swing.JPanel k = new javax.swing.JPanel();
        k.setLayout(new java.awt.BorderLayout());
               
        k.add(new ModelModifiersControlPanel(theModel), java.awt.BorderLayout.NORTH);
        
        ModelPanel.add(k, java.awt.BorderLayout.NORTH);
        
        return ModelPanel;
    }
    
    // Add a new method to the Model.
    private void addNewMethodToModel() {
        Visualiser v = VisualiserFactory.newVisualiser();
        if (v!=null) {
            v.setName("<new>");
            v.setLocation(theModel.getRecommendedNewVisualiserPoint());
            theModel.addVisualiser(v);
            populateVisualiserList();
        } else
            Log.println("ShellControlPanel: unable to add method.");
    }

    // Base directory.
    private java.io.File BaseDirectory = null;
    
    /** Set the base directory for this shell.  Subdirectories within
     * the base directory constitute packages.
     */
    public void setBaseDirectory(java.io.File baseDir) {
        BaseDirectory = baseDir;
    }
    
    /** Get the base directory for this shell. */
    public java.io.File getBaseDirectory() {
        return BaseDirectory;
    }
    
    // Compile to Java
    public void CompileToJava() {
        addBusy();
        ProgressPanel.setVisible(true);
        ProgressPanel.resetProgressBar(0);
        ProgressPanel.setStatus(il8n._("Compiling") + "...");
        try {
            ModelToJava compiler = new ModelToJava(theLibrary, this);
            int retval = compiler.compileJavaSource();
            if (retval==0) {
                Log.println("Compilation successful.");
                try {
                    theLibrary.addClass((String)theModel.getName());
                    getIDE().updateExplorer();
                    Log.println("Shell: " + theModel.getName() + " added to library.");
                } catch (java.lang.ClassNotFoundException cnfe) {
                    Log.println("Shell: Unable to add " + theModel.getName() + " to library: " + cnfe.toString());
                }
            } else
                Log.println("Compilation failed and returned exit code " + retval);
        } finally {
            ProgressPanel.setVisible(false);
            removeBusy();
        }
    }
    
    // Threaded compile to Java.
    public void CompileToJavaThreaded() {
        Thread CompilingThread = new Thread() {
            public void run() {
                while (isBusy())
                    try {
                        sleep(1000);
                    } catch (java.lang.InterruptedException e) {
                    }
                CompileToJava();
            }
        };
        CompilingThread.start();   
    }
    
    // Get Model I/O panel 
    private javax.swing.JPanel getModelIOPanel() {        
        javax.swing.JPanel jPanel4 = new javax.swing.JPanel();

        javax.swing.JButton jButtonNewMethodToModel = new javax.swing.JButton(il8n._("New Method"));
        jButtonNewMethodToModel.setFont(ButtonFont);
        jButtonNewMethodToModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewMethodToModel();
            }
        });        
        jPanel4.add(jButtonNewMethodToModel);
        
        javax.swing.JButton jButtonCompileToJava = new javax.swing.JButton(il8n._("Compile"));
        jButtonCompileToJava.setFont(ButtonFont);
        jButtonCompileToJava.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveModelThreaded();
                CompileToJavaThreaded();
            }
        });
        jPanel4.add(jButtonCompileToJava);

        javax.swing.JButton jButtonImport = new javax.swing.JButton();
        jButtonImport.setText(il8n._("Import") + "...");
        jButtonImport.setFont(ButtonFont);
        jButtonImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Log.println("ShellControlPanel: Import not implemented yet.");
            }
        });
        jPanel4.add(jButtonImport);

        javax.swing.JButton jButtonSave = new javax.swing.JButton();        
        jButtonSave.setText(il8n._("Save"));
        jButtonSave.setFont(ButtonFont);
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSaveModel();
            }
        });
        jPanel4.add(jButtonSave);
        
        javax.swing.JPanel ioPanel = new javax.swing.JPanel();
        ioPanel.setLayout(new java.awt.BorderLayout());
        ioPanel.add(jPanel4, java.awt.BorderLayout.NORTH);
        ioPanel.add(ProgressPanel, java.awt.BorderLayout.SOUTH);
        ProgressPanel.setVisible(false);
        
        return ioPanel;
    }
    
    private boolean doNewModel() {
        if (!promptForSaveModel())
            return false;
        clearModel();
        populateVisualiserList();
        updateModelTitle();
        return true;
    }
     
    // Busy count.
    private int BusyCount = 0;
    
    // Increment 'Busy' count.
    private void addBusy() {
        BusyCount++;
        if (getIDE() != null)
            getIDE().registerDaemon();
    }
    
    // Decrement 'Busy' count.
    private void removeBusy() {
        BusyCount--;
        if (getIDE() != null)
            getIDE().unregisterDaemon();
    }
    
    // True if busy.
    private boolean isBusy() {
        return (BusyCount>0);
    }
    
    // Invoked by the shell when a threaded Model save begins.
    private void saveBegins() {
        addBusy();
    }
    
    // Invoked by the shell when a threaded Model save ends.
    private void saveEnds() {
        removeBusy();
        updateModelTitle();
    }
    
    // Invoked by the shell when a threaded Model load begins.
    private void loadBegins() {
        addBusy();
    }
    
    // Invoked by the shell when a threaded Model load ends.
    private void loadEnds() {
        removeBusy();
        populateVisualiserList();
        updateModelTitle();
    }
    
    // Window is closing, last chance to save.
    // Save - save model
    // Discard - NOP
    public void promptForSaveModelUrgent() {
        if (!theModel.isChanged())
            return;
        while (true) {
            int retval = javax.swing.JOptionPane.showConfirmDialog(null, getModelTitle() + " " + 
                                    il8n._("model has changed.  Save it?"), getModelTitle(), 
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (retval == javax.swing.JOptionPane.YES_OPTION)
                if (doSaveModel())
                    return;
            if (retval == javax.swing.JOptionPane.NO_OPTION)
                return;
        }
    }

    // Ask users if they wish to save Model
    // Save - save Model and return true
    // Discard - return true
    // Cancel - return false
    private boolean promptForSaveModel() {
        if (!theModel.isChanged())
            return true;
        int retval = javax.swing.JOptionPane.showConfirmDialog(null, getModelTitle() + " " + 
                                    il8n._("model has changed.  Save it?"), getModelTitle(),
            javax.swing.JOptionPane.YES_NO_CANCEL_OPTION,
            javax.swing.JOptionPane.QUESTION_MESSAGE);
        switch (retval) {
            case javax.swing.JOptionPane.CANCEL_OPTION: 
                return false;
            case javax.swing.JOptionPane.YES_OPTION:
                doSaveModel();
            default:
                return true;
        }        
    }
    
    private boolean doSaveModel() {
        return saveModelThreaded();
    }

    // Set Model
    private void setModel(Model l) {
        theModel = l;
        l.setShell(this);
    }
    
    /** Get Model */
    public Model getModel() {
        return theModel;
    }
    
    /** Add shell to shell registry */
    private void addShellToRegistry() {
        Shells.addShell(this);
    }
    
    /** Remove shell from shell registry */
    private void removeShellFromRegistry() {
        Shells.removeShell(this);
    }
    
    /** Get the threaded load or save thread.  Null if it's not running. */
    public java.lang.Thread getLoadingOrSavingThread() {
        return LoadingOrSavingThread;
    }
    
    /** Threaded save. */
    public boolean saveModelThreaded() {
        if (getModelFile()==null)
            return false;
        if (LoadingOrSavingThread != null)
            return true;
        LoadingOrSavingThread = new Thread() {
            public void run() {
                while (isBusy())
                    try {
                        sleep(1000);
                    } catch (java.lang.InterruptedException e) {
                    }
                saveBegins();
                ProgressPanel.setVisible(true);
                try {
                    ModelIO.saveModel(theModel, getModelFile(), ProgressPanel);
                } finally {
                    getIDE().updateExplorer();
                    ProgressPanel.setVisible(false);
                    saveEnds();
                    LoadingOrSavingThread = null;
                }
            }
        };
        LoadingOrSavingThread.start();
        return true;
    }
    
    /** Non-Threaded load. */
    public boolean loadModel() {
        if (getModelFile() == null)
            return false;
        clearModel();
        try {
            ProgressPanel.setVisible(true);
            return ModelIO.loadModel(theLibrary, theModel, getModelFile(), ProgressPanel);
        } finally {
            ProgressPanel.setVisible(false);
        }
    }
    
    /** Threaded load. */
    public boolean loadModelThreaded() {
        if (LoadingOrSavingThread != null)
            return true;
        LoadingOrSavingThread = new Thread() {
            public void run() {
                while (isBusy())
                    try {
                        sleep(1000);
                    } catch (java.lang.InterruptedException e) {
                    }
                loadBegins();
                try {
                    loadModel();
                } finally {
                    loadEnds();
                    LoadingOrSavingThread = null;
                }
            }
        };
        LoadingOrSavingThread.start();
        return true;
    }
    
    /** Get Model file */
    public java.io.File getModelFile() {
        return ModelFile;
    }
    
    /** Set Model file */
    public void setModelFile(java.io.File s) {
        ModelFile = s;
    }

    /** Delete all Visualisers from the Model. */
    public void clearModel() {
        int count = theModel.getVisualiserCount();
        while (theModel.getVisualiserCount()>0)
            theModel.removeVisualiser(theModel.getVisualiser(0));
        theModel.clearChanged();
    }
    
    /** Invoked by RootPanelTabbed to obtain content for Tab.  */
    public java.awt.Component getTabContent() {
        return theModel;
    }
    
    /** Invoked by RootPanelTabbed when tab is selected.  May be
     * used to display properties, or other content.
     */
    public void tabWasSelected() {
        showProperties();
    }
    
    /** Invoked by RootPanelTabbed when tab is removed.  May be
     * used to save-before-close, and other suchlike.
     */
    public void tabIsClosing() {
        doModelClosing();
    }
}
