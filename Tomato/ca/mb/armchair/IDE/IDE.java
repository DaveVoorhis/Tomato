package ca.mb.armchair.IDE;

/*
 * IDE.java
 *
 * Created on October 13, 2002, 8:26 PM
 */

/**
 * 
 *
 * @author  Dave Voorhis
 */
public class IDE extends javax.swing.JFrame implements IDEInterface {
    
    // Default IDE display dimensions.
    public final static int InitialWidth = 640;
    public final static int InitialHeight = 480;
    
    // Fonts
    public final static java.awt.Font MenuFont = new java.awt.Font("Dialog", 0, 10);
    
    // Root windows
    private Editor theEditor;
    private Explorer theExplorer;
    private Properties theProperties;
    private Log theLog;
    
    // IDE Logger
    private LogSession IDELogger;
    
    // Widgets that change state.
    private javax.swing.JMenuItem jMenuItemSave;        
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JMenuItem jMenuItemSaveAll;        
    private javax.swing.JMenuItem jMenuItemPrint;        
    private javax.swing.JMenuItem jMenuItemUndo;        
    private javax.swing.JMenuItem jMenuItemRedo;
    private javax.swing.JMenuItem jMenuItemCut;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemPaste;        
    private javax.swing.JMenuItem jMenuItemDelete;        
    private javax.swing.JMenuItem jMenuItemFind;        
    private javax.swing.JMenuItem jMenuItemReplace;
    private javax.swing.JMenuItem jMenuItemUnDock;
    private javax.swing.JMenu jMenuDock;        
    private javax.swing.JMenuItem jMenuItemMount;
    private javax.swing.JMenuItem jMenuItemUnMount;
    private javax.swing.JMenuItem jMenuItemImport;

    // Split panes.
    private IDESplitPane jSplitPaneMain;
    private IDESplitPane jSplitPaneLeft;
    private IDESplitPane jSplitPaneRight;
    
    /** Get the IDE. */
    public static IDE getIDE() {
        return theIDE;
    }
    
    /** Get the default menu item font. */
    public static java.awt.Font getMenuFont() {
        return MenuFont;
    }
     
    /** Creates new IDE window. */
    private IDE() {
        theLog = new Log();
        IDELogger = new LogSession(theLog, "IDE");

        // Tell the JVPL logger to use this IDE.
        ca.mb.armchair.JVPL.Log.setIDE(this);
        
        LogPrintln("Creating new IDE...");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                doExit();
            }
        });

        LogPrintln("Creating display panels.");

        jSplitPaneMain = new IDESplitPane();
        jSplitPaneMain.setOneTouchExpandable(true);
        jSplitPaneMain.setContinuousLayout(true);
        jSplitPaneMain.setResizeWeight(0);

        getContentPane().setLayout(new java.awt.BorderLayout());

        jSplitPaneLeft = new IDESplitPane();
        jSplitPaneLeft.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPaneLeft.setOneTouchExpandable(true);
        jSplitPaneLeft.setContinuousLayout(true);
        jSplitPaneLeft.setResizeWeight(0);

        jSplitPaneMain.setLeftComponent(jSplitPaneLeft);

        jSplitPaneRight = new IDESplitPane();
        jSplitPaneRight.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPaneRight.setOneTouchExpandable(true);
        jSplitPaneRight.setContinuousLayout(true);
        jSplitPaneRight.setResizeWeight(1);

        jSplitPaneMain.setRightComponent(jSplitPaneRight);
        
        getContentPane().add(jSplitPaneMain, java.awt.BorderLayout.CENTER);

        LogPrintln("Setting IDE display bounds.");
        
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((int)(screenSize.getWidth() - InitialWidth) / 2, (int)(screenSize.getHeight() - InitialHeight) / 2, 
                                    InitialWidth, InitialHeight);
    }
    
    private javax.swing.JMenuBar jMenuBarMain;

    /** Launch IDE. */
    private void launch() {
        Splash.showSplash();
        Splash.resetProgressBar(15);
        SplashPrintln("IDE Launch begins...");
        SplashPrintln("Creating menu bar.");
        jMenuBarMain = new javax.swing.JMenuBar();
        setJMenuBar(jMenuBarMain);
        SplashPrintln("Building 'File' menu.");
        buildMenuFile();
        SplashPrintln("Building 'Edit' menu.");
        buildMenuEdit();
        SplashPrintln("Setting initial IDE state.");
        setState();
        SplashPrintln("Initializing Explorer.");
        theExplorer = new Explorer();
        SplashPrintln("Initializing Editor.");
        theEditor = new Editor();
        SplashPrintln("Initializing Properties.");
        theProperties = new Properties();
        SplashPrintln("Configuring panel layout.");
        jSplitPaneRight.setTopComponent(theEditor);
        jSplitPaneRight.setBottomComponent(theProperties);
        jSplitPaneLeft.setTopComponent(theExplorer);
        jSplitPaneLeft.setBottomComponent(theLog);
        SplashPrintln("Building 'View' menu.");
        buildMenuView();
        SplashPrintln("Building 'Tools' menu.");
        buildMenuTools();
        SplashPrintln("Building 'Window' menu.");
        buildMenuWindow();
        SplashPrintln("Building 'Help' menu.");
        buildMenuHelp();
        SplashPrintln("Displaying IDE window.");
        show();
        jSplitPaneMain.setDividerLocation(0.5);
        jSplitPaneLeft.setDividerLocation(0.75);
        jSplitPaneRight.setDividerLocation(0.75);
        SplashPrintln("Ready.");
        Splash.hideSplash();
    }
    
    /** Get Explorer */
    public Explorer getExplorer() {
        return theExplorer;
    }
    
    /** Get Log */
    public Log getLog() {
        return theLog;
    }
    
    /** Get Editor */
    public Editor getEditor() {
        return theEditor;
    }
    
    /** Get Properties */
    public Properties getProperties() {
        return theProperties;
    }
    
    /** Convenience function to print to Splash log, for startup use. */
    private void SplashPrintln(String s) {
        Splash.println(s);
        Splash.incrementProgressBar();
    }

    /** Convenience function to print to IDE log. */
    public void LogPrintln(String s) {
        IDELogger.println(s);
    }
    
    /** Send a string to the Log.  */
    public void LogPrint(String s) {
        IDELogger.print(s);
    }
        
    /** Log a stacktrace.  */
    public void printStackTrace(java.lang.StackTraceElement[] elements) {
        for (int i=0; i<elements.length; i++)
            LogPrintln("\t" + ((i==0) ? "at" : "in") + " " + elements[i].toString());
    }

    /** Get desktop area width */
    public int getDesktopWidth() {
        return getWidth() - (getInsets().right + getInsets().left);
    }
    
    /** Get desktop area height */
    public int getDesktopHeight() {
        return getHeight() - (getInsets().top + getInsets().bottom);
    }
    
    private Object ClipBoard = null;
    
    // Set IDE state on basis of focusPanel
    private void setState() {        
        jMenuItemSave.setEnabled(focusPanel!=null && focusPanel.isChanged());
        jMenuItemSaveAs.setEnabled(focusPanel!=null && focusPanel.isChanged());
        jMenuItemSaveAll.setEnabled(focusPanel!=null && focusPanel.isChanged());
        jMenuItemPrint.setEnabled(focusPanel!=null && focusPanel.canPrint());
        jMenuItemUndo.setEnabled(focusPanel!=null && focusPanel.canUndo());
        jMenuItemRedo.setEnabled(focusPanel!=null && focusPanel.canRedo());
        jMenuItemCut.setEnabled(focusPanel!=null && focusPanel.canCut());
        jMenuItemCopy.setEnabled(focusPanel!=null && focusPanel.canCopy());
        jMenuItemPaste.setEnabled(focusPanel!=null && ClipBoard!=null && focusPanel.canPaste(ClipBoard));
        jMenuItemDelete.setEnabled(focusPanel!=null && focusPanel.canDelete());
        jMenuItemFind.setEnabled(focusPanel!=null && focusPanel.canFind());
        jMenuItemReplace.setEnabled(focusPanel!=null && focusPanel.canReplace());
    }

    private RootPanel focusPanel = null;
    
    /** A given window or panel has taken over the focus.  Adjust appropriately. */
    public void updateIDE(RootPanel p) {
        if (focusPanel != p) {
            focusPanel = p;
            setState();
        }
    }
    
    // Initialise recently-used project files from list, and
    // place on File menu.
    private void initRecentlyUsedFileMenuEntries() {
    }
    
    // Add a menu item to a menu.  Return the item.
    private javax.swing.JMenuItem addMenuItem(javax.swing.JMenuItem target, javax.swing.JMenuItem item) {
        item.setFont(getMenuFont());
        target.add(item);
        return item;
    }
    
    // Add a menu item to a menu.  Return the item.
    private javax.swing.JMenuItem addMenuItem(javax.swing.JMenuItem target, String item) {
        return addMenuItem(target, new javax.swing.JMenuItem(item));
    }
    
    // Add a menu item to a menu.  Return the item.
    private javax.swing.JMenuItem addMenuItem(javax.swing.JMenuItem target, String item, char mnemonic) {
        javax.swing.JMenuItem j = addMenuItem(target, item);
        j.setMnemonic(mnemonic);
        return j;
    }
    
    // Add a menu item to a menu.  Return the item.
    private javax.swing.JMenuItem addMenuItem(javax.swing.JMenuItem target, String item, javax.swing.KeyStroke accel) {
        javax.swing.JMenuItem j = addMenuItem(target, item);
        j.setAccelerator(accel);
        return j;
    }
    
    // Add a menu item to a menu.  Return the item.
    private javax.swing.JMenuItem addMenuItem(javax.swing.JMenuItem target, String item, char mnemonic, javax.swing.KeyStroke accel) {
        javax.swing.JMenuItem j = addMenuItem(target, item, mnemonic);
        j.setAccelerator(accel);
        return j;
    }
    
    // Add an array of menu items to a menu.
    private void addMenuItems(javax.swing.JMenuItem target, javax.swing.JMenuItem items[]) {
        for (int i=0; i<items.length; i++)
            addMenuItem(target, items[i]);
    }

    // Add an entry to the main menu.
    private javax.swing.JMenu addMainMenuItem(String s, char mnemonic) {
        javax.swing.JMenu j = new javax.swing.JMenu(s);
        j.setMnemonic(mnemonic);
        j.setFont(MenuFont);
        jMenuBarMain.add(j);
        return j;
    }
    
    private javax.swing.JMenu jMenuFile;
    
    // Build a new 'File' menu
    private void buildMenuFile() {
        jMenuFile = addMainMenuItem("File", 'F');
        addMenuItem(jMenuFile, "New Project");
        jMenuFile.add(new javax.swing.JSeparator());        
        addMenuItem(jMenuFile, "Open File...");
        jMenuItemMount = addMenuItem(jMenuFile, "Mount Directory...");
        jMenuItemUnMount = addMenuItem(jMenuFile, "Unmount Directory...");
        jMenuItemImport = addMenuItem(jMenuFile, "Import into Project...");
        jMenuItemSave = addMenuItem(jMenuFile, "Save");
        jMenuItemSaveAs = addMenuItem(jMenuFile, "Save As...");
        jMenuItemSaveAll = addMenuItem(jMenuFile, "Save All");        
        jMenuFile.add(new javax.swing.JSeparator());
        jMenuItemPrint = addMenuItem(jMenuFile, "Print...");
        addMenuItem(jMenuFile, "Page setup...");
        jMenuFile.add(new javax.swing.JSeparator());
        addMenuItem(jMenuFile, "Exit").addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                doExit();
            }
        });
        jMenuFile.add(new javax.swing.JSeparator());        
        initRecentlyUsedFileMenuEntries();
    }
    
    private javax.swing.JMenu jMenuEdit;
    
    // Build a new 'Edit' menu
    private void buildMenuEdit() {
        jMenuEdit = addMainMenuItem("Edit", 'E');
        jMenuItemUndo = addMenuItem(jMenuEdit, "Undo", javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UNDO, 0));
        jMenuItemRedo = addMenuItem(jMenuEdit, "Redo", javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        jMenuEdit.add(new javax.swing.JSeparator());
        jMenuItemCut = addMenuItem(jMenuEdit, "Cut", javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_CUT, 0));
        jMenuItemCopy = addMenuItem(jMenuEdit, "Copy", javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_COPY, 0));
        jMenuItemPaste = addMenuItem(jMenuEdit, "Paste", javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PASTE, 0));
        jMenuItemDelete = addMenuItem(jMenuEdit, "Delete", javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        jMenuEdit.add(new javax.swing.JSeparator());
        jMenuItemFind = addMenuItem(jMenuEdit, "Find", javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_FIND, 0));
        jMenuItemReplace = addMenuItem(jMenuEdit, "Replace", javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
    }
    
    private javax.swing.JMenu jMenuView;

    private static int viewCount = 0;
    
    // Add a new 'View' menu item.
    private javax.swing.JMenuItem addViewItem(String description, final RootPanel w) {
        return addMenuItem(jMenuView, description, 
            javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1 + viewCount++, 
                java.awt.event.InputEvent.CTRL_MASK));
    }
    
    // Build a new 'View' menu
    private void buildMenuView() {
        jMenuView = addMainMenuItem("View", 'V');

        addViewItem("Explorer", theExplorer).addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                switch (theExplorer.getPaneState()) {
                    case RootPanel.PANEL_NORMALIZED:            // normalized to maximized
                        jSplitPaneMain.setZoom(IDESplitPane.ZOOM_LEFT);
                        jSplitPaneLeft.setZoom(IDESplitPane.ZOOM_TOP);
                        theExplorer.setPaneState(theExplorer.PANEL_MAXIMIZED);
                        break;
                    case RootPanel.PANEL_MAXIMIZED:             // maximized to minimized
                        jSplitPaneMain.setZoom(IDESplitPane.ZOOM_RIGHT);
                        jSplitPaneLeft.setZoom(IDESplitPane.ZOOM_BOTTOM);
                        theExplorer.setPaneState(theExplorer.PANEL_MINIMIZED);
                    case RootPanel.PANEL_MINIMIZED:             // minimized to normalized
                        jSplitPaneMain.setZoom(IDESplitPane.ZOOM_NORMALIZED);
                        jSplitPaneLeft.setZoom(IDESplitPane.ZOOM_NORMALIZED);
                        theExplorer.setPaneState(theExplorer.PANEL_NORMALIZED);
                }
            }
        });
        
        addViewItem("Editor", theEditor).addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                switch (theEditor.getPaneState()) {
                    case RootPanel.PANEL_NORMALIZED:            // normalized to maximized
                        jSplitPaneMain.setZoom(IDESplitPane.ZOOM_RIGHT);
                        jSplitPaneRight.setZoom(IDESplitPane.ZOOM_TOP);
                        theEditor.setPaneState(theEditor.PANEL_MAXIMIZED);
                        break;
                    case RootPanel.PANEL_MAXIMIZED:             // maximized to minimized
                        jSplitPaneMain.setZoom(IDESplitPane.ZOOM_LEFT);
                        jSplitPaneRight.setZoom(IDESplitPane.ZOOM_BOTTOM);
                        theEditor.setPaneState(theEditor.PANEL_MINIMIZED);
                    case RootPanel.PANEL_MINIMIZED:             // minimized to normalized
                        jSplitPaneMain.setZoom(IDESplitPane.ZOOM_NORMALIZED);
                        jSplitPaneRight.setZoom(IDESplitPane.ZOOM_NORMALIZED);
                        theEditor.setPaneState(theEditor.PANEL_NORMALIZED);
                }
            }
        });
        
        addViewItem("Log", theLog).addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                switch (theLog.getPaneState()) {
                    case RootPanel.PANEL_NORMALIZED:            // normalized to maximized
                        jSplitPaneMain.setZoom(IDESplitPane.ZOOM_LEFT);
                        jSplitPaneLeft.setZoom(IDESplitPane.ZOOM_BOTTOM);
                        theLog.setPaneState(theLog.PANEL_MAXIMIZED);
                        break;
                    case RootPanel.PANEL_MAXIMIZED:             // maximized to minimized
                        jSplitPaneMain.setZoom(IDESplitPane.ZOOM_RIGHT);
                        jSplitPaneLeft.setZoom(IDESplitPane.ZOOM_TOP);
                        theLog.setPaneState(theLog.PANEL_MINIMIZED);
                    case RootPanel.PANEL_MINIMIZED:             // minimized to normalized
                        jSplitPaneMain.setZoom(IDESplitPane.ZOOM_NORMALIZED);
                        jSplitPaneLeft.setZoom(IDESplitPane.ZOOM_NORMALIZED);
                        theLog.setPaneState(theLog.PANEL_NORMALIZED);
                }
            }
        });

        addViewItem("Properties", theProperties).addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                switch (theProperties.getPaneState()) {
                    case RootPanel.PANEL_NORMALIZED:            // normalized to maximized
                        jSplitPaneMain.setZoom(IDESplitPane.ZOOM_RIGHT);
                        jSplitPaneRight.setZoom(IDESplitPane.ZOOM_BOTTOM);
                        theProperties.setPaneState(theProperties.PANEL_MAXIMIZED);
                        break;
                    case RootPanel.PANEL_MAXIMIZED:             // maximized to minimized
                        jSplitPaneMain.setZoom(IDESplitPane.ZOOM_LEFT);
                        jSplitPaneRight.setZoom(IDESplitPane.ZOOM_TOP);
                        theProperties.setPaneState(theProperties.PANEL_MINIMIZED);
                    case RootPanel.PANEL_MINIMIZED:             // minimized to normalized
                        jSplitPaneMain.setZoom(IDESplitPane.ZOOM_NORMALIZED);
                        jSplitPaneRight.setZoom(IDESplitPane.ZOOM_NORMALIZED);
                        theProperties.setPaneState(theProperties.PANEL_NORMALIZED);
                }
            }
        });
    }
    
    private javax.swing.JMenu jMenuTools;
    
    // Build a new 'Tools' menu
    private void buildMenuTools() {
        jMenuTools = addMainMenuItem("Tools", 'T');
        addMenuItem(jMenuTools, "Add Plug-In...");
    }
    
    private javax.swing.JMenu jMenuWindow;
    
    // Build a new 'Window' menu
    private void buildMenuWindow() {
        jMenuWindow = addMainMenuItem("Window", 'W');
        
        addMenuItem(jMenuWindow, "Vertical Split", javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK))
        .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                jSplitPaneMain.nextZoom();
            }
        });
        
        addMenuItem(jMenuWindow, "Left Split", javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, java.awt.event.InputEvent.CTRL_MASK))
        .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                jSplitPaneLeft.nextZoom();
            }
        });
        
        addMenuItem(jMenuWindow, "Right Split", javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_MASK))
        .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                jSplitPaneRight.nextZoom();
            }
        });
    }
    
    private javax.swing.JMenu jMenuHelp;
    
    // Build a new 'Help' menu
    private void buildMenuHelp() {
        jMenuHelp = addMainMenuItem("Help", 'H');
        addMenuItem(jMenuHelp, "Contents");
        addMenuItem(jMenuHelp, "Index");
        jMenuHelp.add(new javax.swing.JSeparator());
        addMenuItem(jMenuHelp, "About");
    }
    
    /** True if we're run as an application. */
    private static boolean Application = false;
    
    // Timer used for exit poll of running daemons.
    private javax.swing.Timer ShutdownTimer = null;
    
    /** Exit the Application */
    public void doExit() {
        // Save and close editor sessions.
        for (int i=0; i<theEditor.getTabCount(); i++) {
            ca.mb.armchair.JVPL.Shell s = (ca.mb.armchair.JVPL.Shell)theEditor.getTab(i);
            s.promptForSaveModelUrgent();
        }
        // Save library
        theExplorer.promptForSaveLibraryUrgent();
        // outta here...
        if (Application) {
            LogPrintln("Waiting for tasks to finish.");
            ShutdownTimer = new javax.swing.Timer(1000, new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    // Anybody still busy?
                    int RegisteredDaemonCount = getRegisteredDaemonCount();
                    if (RegisteredDaemonCount > 0) {
                        LogPrintln("Waiting for " + RegisteredDaemonCount + " tasks to finish.");
                        return;
                    }
                    ShutdownTimer.stop();
                    LogPrintln("Application exiting.");
                    System.exit(0);
                }
            });
            ShutdownTimer.setRepeats(true);
            ShutdownTimer.start();             
        }
    }
    
    /** Run without System.exit(0) when window closes. */
    public static void run() {
        theIDE.launch();
    }
    
    /** The IDE instance. */
    private static IDE theIDE = new IDE();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Application = true;
        theIDE.launch();
    }
    
    /** Display a control panel (typically a Properties display) with a given title.  */
    public void displayControlPanel(javax.swing.JPanel controlPanel, String title) {
        if (theProperties != null)
            theProperties.addContentPanel(controlPanel);
    }
    
    // Count of registered daemons.
    private int RegisteredDaemons = 0;
    
    /** Register the fact that a background process has begun.  */
    public void registerDaemon() {
        RegisteredDaemons++;
    }
    
    /** Return number of running background processes.  */
    public int getRegisteredDaemonCount() {
        return RegisteredDaemons;
    }
    
    /** Register the fact that a background process has ended.  */
    public void unregisterDaemon() {
        RegisteredDaemons--;
    }
    
    /** Tell the IDE to update the Explorer, because we've modified something the Explorer displays.  */
    public void updateExplorer() {
        theExplorer.refresh();
    }
    
}
