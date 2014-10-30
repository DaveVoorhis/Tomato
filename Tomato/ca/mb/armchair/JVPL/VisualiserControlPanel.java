/*
 * DlgVisualiser.java
 *
 * Created on August 01, 2002, 4:50PM
 */

package ca.mb.armchair.JVPL;

/**
 *
 * Control panel for a Visualiser.  Currently created by default by Visualiser.
 *
 * @author  Dave Voorhis
 */
public class VisualiserControlPanel extends javax.swing.JPanel {

    // Fonts
    private final java.awt.Font ListFontBold = new java.awt.Font("sans-serif", java.awt.Font.BOLD, 10);
    private final java.awt.Font ListFontPlain = new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 10);
    private final java.awt.Font ButtonFont = new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 10);
    
    // Stuff
    private Visualiser theVisualiser;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldSearch;

    // List data models for lists
    private javax.swing.DefaultListModel listModelMethods;
    private javax.swing.DefaultListModel listModelConstructors;
    private javax.swing.DefaultListModel listModelExceptions;
    private javax.swing.DefaultListModel listModelFields;
    private javax.swing.DefaultListModel listModelProperties;
    private javax.swing.DefaultListModel listModelLeftConnectors;
    private javax.swing.DefaultListModel listModelRightConnectors;
    
    // Widgies.
    private javax.swing.JList jListMethods;
    private javax.swing.JList jListExceptions;
    private javax.swing.JList jListFields;
    private javax.swing.JList jListConstructors;
    private javax.swing.JList jListProperties;
    private javax.swing.JList jListRight;
    private javax.swing.JList jListLeft;
    private javax.swing.JTabbedPane jTabPaneMain;
    private javax.swing.JPanel ConnectorPane;

    // Modifier Widgets
    private javax.swing.JCheckBox jCheckBoxStatic;
    private javax.swing.JCheckBox jCheckBoxSynchronized;
    private javax.swing.JRadioButton jRadioPrivate;
    private javax.swing.JRadioButton jRadioPublic;
    private javax.swing.JRadioButton jRadioProtected;
    private javax.swing.JRadioButton jRadioDefault;
    private javax.swing.JCheckBox jCheckBoxFinal;
    private javax.swing.JCheckBox jCheckBoxNative;
    private javax.swing.JCheckBox jCheckBoxTransient;
    private javax.swing.JCheckBox jCheckBoxVolatile;

    // Connector management    
    private javax.swing.JButton jButtonDisconnect;
    private javax.swing.JButton jButtonHide;

    /** Creates new panel VisualiserControlPanel */
    public VisualiserControlPanel(Visualiser v) {
        theVisualiser = v;
        initComponents();
        populateLists();
    }
    
    /** Show. */
    public void show() {
        super.show();
        populateLists();
        configureWidgets();
        configureTitle();
    }
    
    /** Point to a specific connector. */
    public void gotoConnector(Connector c) {
        jTabPaneMain.setSelectedComponent(ConnectorPane);
        if (listModelLeftConnectors.contains(c)) {
            jListLeft.setSelectedValue(c, true);
            jListRight.clearSelection();
        }
        else if (listModelRightConnectors.contains(c)) {
            jListRight.setSelectedValue(c, true);
            jListLeft.clearSelection();
        }
        configureWidgets();
    }
    
    // Obtain new title
    private void configureTitle() {
//        setTitle(il8n._("Control panel for") + " " + theVisualiser.getLongTitle());
    }
    
    // repopulate left side connector list
    private void populateLeft() {
        listModelLeftConnectors = new javax.swing.DefaultListModel();
        for (int i=0; i<theVisualiser.getConnectorCount(); i++) {
            Connector c = theVisualiser.getConnector(i);
            if (c.isExposed() && c.getLayoutDirection()==c.EASTTOWEST)
                listModelLeftConnectors.addElement(theVisualiser.getConnector(i));
        }
        jListLeft.setModel(listModelLeftConnectors);
    }
    
    // repopulate right side connector list
    private void populateRight() {
        listModelRightConnectors = new javax.swing.DefaultListModel();
        for (int i=0; i<theVisualiser.getConnectorCount(); i++) {
            Connector c = theVisualiser.getConnector(i);
            if (c.isExposed() && c.getLayoutDirection()==c.WESTTOEAST)
                listModelRightConnectors.addElement(theVisualiser.getConnector(i));
        }
        jListRight.setModel(listModelRightConnectors);
    }

    // return true if jTextFieldSearch contents are found in class description
    private boolean isFound(Connector c) {
        if (jTextFieldSearch==null || jTextFieldSearch.getText().trim().length()==0)
            return true;
        else
            return (c.toString().indexOf(jTextFieldSearch.getText())>=0);
    }
    
    // repopulate selection lists
    private void populateSelectionLists() {
        listModelMethods = new javax.swing.DefaultListModel();
        listModelConstructors = new javax.swing.DefaultListModel();
        listModelExceptions = new javax.swing.DefaultListModel();
        listModelFields = new javax.swing.DefaultListModel();
        listModelProperties = new javax.swing.DefaultListModel();

        for (int i=0; i<theVisualiser.getConnectorCount(); i++) {
            Connector c = theVisualiser.getConnector(i);
            if (!c.isExposed() && isFound(c))
                if (c.isMethod())
                    listModelMethods.addElement(theVisualiser.getConnector(i));
                else if (c.isConstructor())
                    listModelConstructors.addElement(theVisualiser.getConnector(i));
                else if (c.isInstance() && !c.isInterface())
                    listModelExceptions.addElement(theVisualiser.getConnector(i));
                else if (c.isField())
                    listModelFields.addElement(theVisualiser.getConnector(i));
        }
        
        jListMethods.setModel(listModelMethods);
        jListConstructors.setModel(listModelConstructors);
        jListExceptions.setModel(listModelExceptions);
        jListFields.setModel(listModelFields);
        jListProperties.setModel(listModelProperties);
    }
    
    // Populate all lists from the visualiser's connectors
    private void populateLists() {
        listModelMethods = new javax.swing.DefaultListModel();
        listModelConstructors = new javax.swing.DefaultListModel();
        listModelExceptions = new javax.swing.DefaultListModel();
        listModelFields = new javax.swing.DefaultListModel();
        listModelProperties = new javax.swing.DefaultListModel();
        listModelLeftConnectors = new javax.swing.DefaultListModel();
        listModelRightConnectors = new javax.swing.DefaultListModel();
        
        for (int i=0; i<theVisualiser.getConnectorCount(); i++) {
            Connector c = theVisualiser.getConnector(i);
            if (c.isExposed())
                if (c.getLayoutDirection()==c.EASTTOWEST)
                    listModelLeftConnectors.addElement(theVisualiser.getConnector(i));
                else
                    listModelRightConnectors.addElement(theVisualiser.getConnector(i));
            else
                if (isFound(c))
                    if (c.isMethod())
                        listModelMethods.addElement(theVisualiser.getConnector(i));
                    else if (c.isConstructor())
                        listModelConstructors.addElement(theVisualiser.getConnector(i));
                    else if (c.isInstance() && !c.isInterface())
                        listModelExceptions.addElement(theVisualiser.getConnector(i));
                    else if (c.isField())
                        listModelFields.addElement(theVisualiser.getConnector(i));
        }
        
        jListLeft.setModel(listModelLeftConnectors);
        jListRight.setModel(listModelRightConnectors);
        jListMethods.setModel(listModelMethods);
        jListConstructors.setModel(listModelConstructors);
        jListExceptions.setModel(listModelExceptions);
        jListFields.setModel(listModelFields);
        jListProperties.setModel(listModelProperties);
    }
    
    // Set sliders and buttons
    private void configureWidgets() {
        int TotalSelected = jListLeft.getSelectedValues().length + 
                            jListRight.getSelectedValues().length;
        jButtonDisconnect.setEnabled(TotalSelected>0);
        jButtonHide.setEnabled(TotalSelected>0);
    }

    // handle an available (unexposed) connector list click
    private void doAvailableListClick(java.awt.event.MouseEvent e, javax.swing.JList jList) {
        if (e.getClickCount() == 2) {
            Connector c = (Connector)jList.getSelectedValue();
            theVisualiser.expose(c);
            ((javax.swing.DefaultListModel)jList.getModel()).removeElement(c);
            if (c.getLayoutDirection()==c.EASTTOWEST)
                listModelLeftConnectors.addElement(c);
            else
                listModelRightConnectors.addElement(c);
        }
    }

    // handle an exposed connector list click
    private void doExposedListClick(java.awt.event.MouseEvent e) {
        configureWidgets();
    }
    
    // build and return connector management pane
    private javax.swing.JPanel getConnectorPane() {
        
        javax.swing.JPanel thePane = new javax.swing.JPanel();

        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        javax.swing.JSplitPane jSplitPane1 = new javax.swing.JSplitPane();
        jListLeft = new javax.swing.JList();
        jListRight = new javax.swing.JList();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
        jButtonHide = new javax.swing.JButton();
        jButtonDisconnect = new javax.swing.JButton();
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setContinuousLayout(true);
        
        jButtonDisconnect.setEnabled(false);
        jButtonHide.setEnabled(false);

        // set up here...
        
        jListLeft.setFont(ListFontPlain);
        jListRight.setFont(ListFontPlain);
        
        jListLeft.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                doExposedListClick(e);
            }
        });
        
        jListRight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                doExposedListClick(e);
            }
        });
        
        thePane.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setViewportView(jListRight);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setDividerLocation(0.5);
        
        jSplitPane1.setRightComponent(jPanel2);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(jListLeft);

        jPanel5.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel5);

        jPanel3.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        thePane.add(jPanel3, java.awt.BorderLayout.CENTER);

        jButtonDisconnect.setText(il8n._("Disconnect"));
        jButtonDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDisconnectActionPerformed(evt);
            }
        });

        jPanel4.add(jButtonDisconnect);

        jButtonHide.setText(il8n._("Hide"));
        jButtonHide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHideActionPerformed(evt);
            }
        });

        jPanel4.add(jButtonHide);

        thePane.add(jPanel4, java.awt.BorderLayout.SOUTH);
        
        return thePane;
    }

    private void jButtonDisconnectActionPerformed(java.awt.event.ActionEvent evt) {
        Object[] left = jListLeft.getSelectedValues();
        for (int i=0; i<left.length; i++)
            ((Connector)left[i]).removeConnections();
        Object[] right = jListRight.getSelectedValues();
        for (int i=0; i<right.length; i++)
            ((Connector)right[i]).removeConnections();
    }

    private void jButtonHideActionPerformed(java.awt.event.ActionEvent evt) {
        Object[] left = jListLeft.getSelectedValues();
        for (int i=0; i<left.length; i++)
            ((Connector)left[i]).unexpose();
        Object[] right = jListRight.getSelectedValues();
        for (int i=0; i<right.length; i++)
            ((Connector)right[i]).unexpose();
        populateLists();
        configureWidgets();
    }

    // Move selected connectors to the other side.
    private void switchSides(javax.swing.JList j) {
        Object[] list = j.getSelectedValues();
        for (int i=0; i<list.length; i++)
            ((Connector)list[i]).switchSides();
        populateLeft();
        populateRight();
        configureWidgets();
    }

    /** Get appropriate Java modifier string based on button state. */
    private String getModifierString() {
        String mods = "";
        if (jRadioPrivate.isSelected())
            mods += "private ";
        else if (jRadioPublic.isSelected())
            mods += "public ";
        else if (jRadioProtected.isSelected())
            mods += "protected ";
        if (jCheckBoxStatic.isSelected())
            mods += "static ";
        if (jCheckBoxSynchronized.isSelected())
            mods += "synchronized ";
        if (jCheckBoxFinal.isSelected())
            mods += "final ";
        if (jCheckBoxNative.isSelected())
            mods += "transient ";
        if (jCheckBoxVolatile.isSelected())
            mods += "volatile ";
        return mods.trim();
    }
    
    /** Set button state based on Java modifier string. */
    private void setButtons(String s) {
        jRadioPrivate.setSelected(s.indexOf("private")>=0);
        jRadioPublic.setSelected(s.indexOf("public")>=0);
        jRadioProtected.setSelected(s.indexOf("protected")>=0);
        jRadioDefault.setSelected(!jRadioPrivate.isSelected() && !jRadioPublic.isSelected() && !jRadioProtected.isSelected());
        jCheckBoxStatic.setSelected(s.indexOf("static")>=0);
        jCheckBoxSynchronized.setSelected(s.indexOf("synchronized")>=0);
        jCheckBoxFinal.setSelected(s.indexOf("final")>=0);
        jCheckBoxNative.setSelected(s.indexOf("transient")>=0);
        jCheckBoxVolatile.setSelected(s.indexOf("volatile")>=0);
    }
    
    // Set buttons from the visualiser
    private void setupButtons() {
        setButtons(theVisualiser.getModifierString());
    }
    
    // Set visualiser from the buttons, and update buttons if appropriate
    private void setupVisualiser() {
        theVisualiser.setModifierString(getModifierString());
    }
        
    // Get Settings panel.
    private javax.swing.JPanel getSettingsPanel() {
        
        javax.swing.JPanel jPanelBottom = new javax.swing.JPanel();
        jPanelBottom.setLayout(new java.awt.GridLayout(1, 0));

        jRadioPrivate = new javax.swing.JRadioButton();
        jRadioPublic = new javax.swing.JRadioButton();
        jRadioProtected = new javax.swing.JRadioButton();
        jRadioDefault = new javax.swing.JRadioButton();
        
        javax.swing.JPanel jPanelScope = new javax.swing.JPanel();
        jPanelScope.setLayout(new javax.swing.BoxLayout(jPanelScope, javax.swing.BoxLayout.Y_AXIS));
        jPanelScope.setBorder(new javax.swing.border.TitledBorder(il8n._("Scope")));

        javax.swing.ButtonGroup buttonGroupScope = new javax.swing.ButtonGroup();

        jRadioPrivate.setSelected(true);
        jRadioPrivate.setText("private");
        buttonGroupScope.add(jRadioPrivate);
        jRadioPrivate.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioPrivate.setFont(ButtonFont);
        jRadioPrivate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupVisualiser();
            }
        });

        jPanelScope.add(jRadioPrivate);

        jRadioPublic.setText("public");
        buttonGroupScope.add(jRadioPublic);
        jRadioPublic.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioPublic.setFont(ButtonFont);
        jRadioPublic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupVisualiser();
            }
        });

        jPanelScope.add(jRadioPublic);

        jRadioProtected.setText("protected");
        buttonGroupScope.add(jRadioProtected);
        jRadioProtected.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioProtected.setFont(ButtonFont);
        jRadioProtected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupVisualiser();
            }
        });

        jPanelScope.add(jRadioProtected);

        jRadioDefault.setText(il8n._("<Default>"));
        buttonGroupScope.add(jRadioDefault);
        jRadioDefault.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioDefault.setFont(ButtonFont);
        jRadioDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupVisualiser();
            }
        });

        jPanelScope.add(jRadioDefault);

        jPanelBottom.add(jPanelScope);

        jCheckBoxStatic = new javax.swing.JCheckBox();
        jCheckBoxSynchronized = new javax.swing.JCheckBox();        
        jCheckBoxFinal = new javax.swing.JCheckBox();
        jCheckBoxNative = new javax.swing.JCheckBox();
        jCheckBoxTransient = new javax.swing.JCheckBox();
        jCheckBoxVolatile = new javax.swing.JCheckBox();

        javax.swing.JPanel jPanelModifiers = new javax.swing.JPanel();
        jPanelModifiers.setLayout(new javax.swing.BoxLayout(jPanelModifiers, javax.swing.BoxLayout.Y_AXIS));
        jPanelModifiers.setBorder(new javax.swing.border.TitledBorder(il8n._("Modifiers")));

        jCheckBoxStatic.setText("static");
        jCheckBoxStatic.setBorder(null);
        jCheckBoxStatic.setFont(ButtonFont);
        jCheckBoxStatic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupVisualiser();
            }
        });

        jPanelModifiers.add(jCheckBoxStatic);

        jCheckBoxSynchronized.setText("synchronized");
        jCheckBoxSynchronized.setBorder(null);
        jCheckBoxSynchronized.setFont(ButtonFont);
        jCheckBoxSynchronized.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupVisualiser();
            }
        });

        jPanelModifiers.add(jCheckBoxSynchronized);
        
        jCheckBoxFinal.setText("final");
        jCheckBoxFinal.setBorder(null);
        jCheckBoxFinal.setFont(ButtonFont);
        jCheckBoxFinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupVisualiser();
            }
        });

        jPanelModifiers.add(jCheckBoxFinal);
        
        jCheckBoxNative.setText("native");
        jCheckBoxNative.setBorder(null);
        jCheckBoxNative.setFont(ButtonFont);
        jCheckBoxNative.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupVisualiser();
            }
        });

        jPanelModifiers.add(jCheckBoxNative);

        jCheckBoxTransient.setText("transient");
        jCheckBoxTransient.setBorder(null);
        jCheckBoxTransient.setFont(ButtonFont);
        jCheckBoxTransient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupVisualiser();
            }
        });

        jPanelModifiers.add(jCheckBoxTransient);

        jCheckBoxVolatile.setText("volatile");
        jCheckBoxVolatile.setBorder(null);
        jCheckBoxVolatile.setFont(ButtonFont);
        jCheckBoxVolatile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupVisualiser();
            }
        });

        jPanelModifiers.add(jCheckBoxVolatile);

        jPanelBottom.add(jPanelModifiers);
        
        setupButtons();
        
        if (theVisualiser.isVisualiserOfMessage()) {

            final VisualiserOfMessage VOM = (VisualiserOfMessage)theVisualiser;
            
            //
            // Message visualiser controls
            //
            
            javax.swing.JPanel jPanelInvokers = new javax.swing.JPanel();
            jPanelInvokers.setLayout(new javax.swing.BoxLayout(jPanelInvokers, javax.swing.BoxLayout.Y_AXIS));
            jPanelInvokers.setBorder(new javax.swing.border.TitledBorder(il8n._("Auto-invocation")));
            
            final javax.swing.JCheckBox jCheckBoxInvokeAfterParmChange = 
                new javax.swing.JCheckBox(il8n._("Invoke after parameter change."),
                                          VOM.isInvokesAfterParmChange());
            jCheckBoxInvokeAfterParmChange.setFont(ListFontPlain);
            jCheckBoxInvokeAfterParmChange.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    VOM.setInvokesAfterParmChange(jCheckBoxInvokeAfterParmChange.isSelected());
                }
            });
            jPanelInvokers.add(jCheckBoxInvokeAfterParmChange);

            final javax.swing.JCheckBox jCheckBoxInvokeBeforeReturnUse = 
                new javax.swing.JCheckBox(il8n._("Invoke before using return value."),
                                          VOM.isInvokesBeforeReturnUse());
            jCheckBoxInvokeBeforeReturnUse.setFont(ListFontPlain);
            jCheckBoxInvokeBeforeReturnUse.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    VOM.setInvokesBeforeReturnUse(jCheckBoxInvokeBeforeReturnUse.isSelected());
                }
            });
            jPanelInvokers.add(jCheckBoxInvokeBeforeReturnUse);

            final javax.swing.JCheckBox jCheckBoxInvokeAfterReferenceChange = 
                new javax.swing.JCheckBox(il8n._("Invoke after reference change."),
                                          VOM.isInvokesAfterReferenceChange());
            jCheckBoxInvokeAfterReferenceChange.setFont(ListFontPlain);
            jCheckBoxInvokeAfterReferenceChange.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    VOM.setInvokesAfterReferenceChange(jCheckBoxInvokeAfterReferenceChange.isSelected());
                }
            });
            jPanelInvokers.add(jCheckBoxInvokeAfterReferenceChange);

            final javax.swing.JCheckBox jCheckBoxInvokeBeforeReferenceUse = 
                new javax.swing.JCheckBox(il8n._("Invoke before reference use."),
                                          VOM.isInvokesBeforeReferenceUse());
            jCheckBoxInvokeBeforeReferenceUse.setFont(ListFontPlain);
            jCheckBoxInvokeBeforeReferenceUse.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    VOM.setInvokesBeforeReferenceUse(jCheckBoxInvokeBeforeReferenceUse.isSelected());
                }
            });
            jPanelInvokers.add(jCheckBoxInvokeBeforeReferenceUse);
            
            jPanelBottom.add(jPanelInvokers);

            javax.swing.JPanel jPanelFlowControl = new javax.swing.JPanel();
            jPanelFlowControl.setLayout(new javax.swing.BoxLayout(jPanelFlowControl, javax.swing.BoxLayout.Y_AXIS));
            jPanelFlowControl.setBorder(new javax.swing.border.TitledBorder(il8n._("Flow Control")));
            
            final javax.swing.JCheckBox jCheckBoxGate = 
                new javax.swing.JCheckBox(il8n._("Gate: Control invocation."), VOM.isGated());
            jCheckBoxGate.setFont(ListFontPlain);
            jCheckBoxGate.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (jCheckBoxGate.isSelected() && VOM.getConnectorToReturnType()==null)
                        VOM.setGated(true);
                    else {
                        VOM.setGated(false);
                        jCheckBoxGate.setSelected(false);
                    }
                }
            });
            jPanelFlowControl.add(jCheckBoxGate);            

            final javax.swing.JCheckBox jCheckBoxCount = 
                new javax.swing.JCheckBox(il8n._("Count: Increment after invocation."), VOM.isCounted());
            jCheckBoxCount.setFont(ListFontPlain);
            jCheckBoxCount.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    VOM.setCounted(jCheckBoxCount.isSelected());
                }
            });
            jPanelFlowControl.add(jCheckBoxCount);            

            final javax.swing.JCheckBox jCheckBoxRepeat = 
                new javax.swing.JCheckBox(il8n._("Repeat: Invoke continuously."), VOM.isRepeated());
            jCheckBoxRepeat.setFont(ListFontPlain);
            jCheckBoxRepeat.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (jCheckBoxRepeat.isSelected() && VOM.getConnectorToReturnType()==null)
                        VOM.setRepeated(true);
                    else {
                        VOM.setRepeated(false);
                        jCheckBoxRepeat.setSelected(false);
                    }
                }
            });
            jPanelFlowControl.add(jCheckBoxRepeat);            
            
            jPanelBottom.add(jPanelFlowControl);
            
        } else {
            
            //
            // Non-message visualiser controls
            //
            
            javax.swing.JPanel jPanelInstance = new javax.swing.JPanel();
            jPanelInstance.setLayout(new javax.swing.BoxLayout(jPanelInstance, javax.swing.BoxLayout.Y_AXIS));
            jPanelInstance.setBorder(new javax.swing.border.TitledBorder(il8n._("Instance")));

            javax.swing.JButton jButtonNullify = new javax.swing.JButton(il8n._("Set to Null"));
            jButtonNullify.setFont(ListFontPlain);
            jButtonNullify.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    theVisualiser.setInstance(null);
                    configureTitle();
                }
            });
            jPanelInstance.add(jButtonNullify);

            javax.swing.JButton jButtonAutoInstantiate = new javax.swing.JButton(il8n._("Auto-instantiate"));
            jButtonAutoInstantiate.setFont(ListFontPlain);
            jButtonAutoInstantiate.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    theVisualiser.instantiate();
                    configureTitle();
                }
            });
            jButtonAutoInstantiate.setEnabled(theVisualiser.isAutoInstantiable());
            jPanelInstance.add(jButtonAutoInstantiate);

            jPanelBottom.add(jPanelInstance);
            
            boolean isSerializableInstance = theVisualiser.isSerializableInstance();
            
            final javax.swing.JRadioButton jRadioButtonSerializeXML = 
                new javax.swing.JRadioButton(il8n._("XML Serialization"),
                                          (theVisualiser.getSerializationMode()==VisualiserOfMessage.SERIALIZE_XML));
            jRadioButtonSerializeXML.setFont(ListFontPlain);
            jRadioButtonSerializeXML.setEnabled(isSerializableInstance);

            final javax.swing.JRadioButton jRadioButtonSerializeBinary = 
                new javax.swing.JRadioButton(il8n._("Binary Serialization"),
                                          (theVisualiser.getSerializationMode()==VisualiserOfMessage.SERIALIZE_BINARY));
            jRadioButtonSerializeBinary.setFont(ListFontPlain);
            jRadioButtonSerializeBinary.setEnabled(isSerializableInstance);

            final javax.swing.JRadioButton jRadioButtonSerializeNull = 
                new javax.swing.JRadioButton(il8n._("Null on load"),
                                          (theVisualiser.getSerializationMode()==Visualiser.SERIALIZE_NULL));
            jRadioButtonSerializeNull.setFont(ListFontPlain);

            final javax.swing.JRadioButton jRadioButtonSerializeAutoInstantiate = 
                new javax.swing.JRadioButton(il8n._("Auto-instantiate"),
                                          (theVisualiser.getSerializationMode()==Visualiser.SERIALIZE_AUTOINSTANTIATE));
            jRadioButtonSerializeAutoInstantiate.setFont(ListFontPlain);
            jRadioButtonSerializeAutoInstantiate.setEnabled(theVisualiser.isAutoInstantiable());

            javax.swing.ButtonGroup bg = new javax.swing.ButtonGroup();
            bg.add(jRadioButtonSerializeXML);
            bg.add(jRadioButtonSerializeBinary);
            bg.add(jRadioButtonSerializeNull);
            bg.add(jRadioButtonSerializeAutoInstantiate);

            javax.swing.JPanel jPanelSerialization = new javax.swing.JPanel();
            jPanelSerialization.setLayout(new javax.swing.BoxLayout(jPanelSerialization, javax.swing.BoxLayout.Y_AXIS));
            jPanelSerialization.setBorder(new javax.swing.border.TitledBorder(il8n._("Serialization")));

            jRadioButtonSerializeXML.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                   theVisualiser.setSerializationMode((jRadioButtonSerializeXML.isSelected()) ?
                        Visualiser.SERIALIZE_XML : (jRadioButtonSerializeBinary.isSelected()) ?
                        Visualiser.SERIALIZE_BINARY : (jRadioButtonSerializeNull.isSelected()) ?
                        Visualiser.SERIALIZE_NULL : Visualiser.SERIALIZE_AUTOINSTANTIATE);
                }
            });
            jPanelSerialization.add(jRadioButtonSerializeXML);

            jRadioButtonSerializeBinary.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                   theVisualiser.setSerializationMode((jRadioButtonSerializeXML.isSelected()) ?
                        Visualiser.SERIALIZE_XML : (jRadioButtonSerializeBinary.isSelected()) ?
                        Visualiser.SERIALIZE_BINARY : (jRadioButtonSerializeNull.isSelected()) ?
                        Visualiser.SERIALIZE_NULL : Visualiser.SERIALIZE_AUTOINSTANTIATE);
                }
            });
            jPanelSerialization.add(jRadioButtonSerializeBinary);

            jRadioButtonSerializeNull.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                   theVisualiser.setSerializationMode((jRadioButtonSerializeXML.isSelected()) ?
                        Visualiser.SERIALIZE_XML : (jRadioButtonSerializeBinary.isSelected()) ?
                        Visualiser.SERIALIZE_BINARY : (jRadioButtonSerializeNull.isSelected()) ?
                        Visualiser.SERIALIZE_NULL : Visualiser.SERIALIZE_AUTOINSTANTIATE);
                }
            });
            jPanelSerialization.add(jRadioButtonSerializeNull);

            jRadioButtonSerializeAutoInstantiate.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                   theVisualiser.setSerializationMode((jRadioButtonSerializeXML.isSelected()) ?
                        Visualiser.SERIALIZE_XML : (jRadioButtonSerializeBinary.isSelected()) ?
                        Visualiser.SERIALIZE_BINARY : (jRadioButtonSerializeNull.isSelected()) ?
                        Visualiser.SERIALIZE_NULL : Visualiser.SERIALIZE_AUTOINSTANTIATE);
                }
            });
            jPanelSerialization.add(jRadioButtonSerializeAutoInstantiate);

            jPanelBottom.add(jPanelSerialization);
        }

        return jPanelBottom;
    }

    // get general properties panel
    private javax.swing.JPanel getMainPropertiesPanel() {
        
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        
        javax.swing.JPanel jPanelName = new javax.swing.JPanel();
        jPanelName.add(new javax.swing.JLabel(il8n._("Name") + ":"));
        jTextFieldName = new javax.swing.JTextField();
        jTextFieldName.setPreferredSize(new java.awt.Dimension(200, 20));
        jTextFieldName.setMinimumSize(new java.awt.Dimension(10, 20));
        jTextFieldName.setText(theVisualiser.getName());
        jTextFieldName.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                theVisualiser.setName(jTextFieldName.getText());
                configureTitle();
            }
        });
        jPanelName.add(jTextFieldName);
        jPanel2.add(jPanelName);
        
        javax.swing.JButton jButtonDelete = new javax.swing.JButton(il8n._("Delete"));
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                theVisualiser.getModel().removeVisualiser(theVisualiser);
            }
        });
        jPanel2.add(jButtonDelete);
        
        final javax.swing.JCheckBox jCheckBoxHideCustom = new javax.swing.JCheckBox(il8n._("Show custom"));
        jCheckBoxHideCustom.setSelected(theVisualiser.isCustomVisible());
        jCheckBoxHideCustom.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent ce) {
                theVisualiser.setCustomVisible(jCheckBoxHideCustom.isSelected());
            }
        });
        jPanel2.add(jCheckBoxHideCustom);
        
        javax.swing.JPanel jPanelSearch = new javax.swing.JPanel();
        jPanelSearch.add(new javax.swing.JLabel(il8n._("Search") + ":"));
        jTextFieldSearch = new javax.swing.JTextField();
        jTextFieldSearch.setPreferredSize(new java.awt.Dimension(60, 20));
        jTextFieldSearch.setMinimumSize(new java.awt.Dimension(10, 20));
        jTextFieldSearch.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent e) {
                populateSelectionLists();
            }
        });
        jPanelSearch.add(jTextFieldSearch);
        jPanel2.add(jPanelSearch);
        
        return jPanel2;
    }
    
    // init widgets
    private void initComponents() {
        
        setLayout(new java.awt.BorderLayout());
        
        add(getMainPropertiesPanel(), java.awt.BorderLayout.NORTH);
        
        jTabPaneMain = new javax.swing.JTabbedPane();
        jTabPaneMain.setTabPlacement(javax.swing.JTabbedPane.TOP);
        
        jTabPaneMain.addTab(il8n._("Settings"), getSettingsPanel());
        
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        jListConstructors = new javax.swing.JList();
        jListConstructors.setFont(ListFontPlain);
        jListConstructors.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                doAvailableListClick(e, jListConstructors);
            }
        });
        jScrollPane1.setViewportView(jListConstructors);
        jTabPaneMain.addTab(il8n._("Constructors"), jScrollPane1);
        
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        jListMethods = new javax.swing.JList();
        jListMethods.setFont(ListFontPlain);        
        jListMethods.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                doAvailableListClick(e, jListMethods);
            }
        });
        jScrollPane2.setViewportView(jListMethods);
        jTabPaneMain.addTab(il8n._("Methods"), jScrollPane2);
        
        javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
        jListFields = new javax.swing.JList();
        jListFields.setFont(ListFontPlain);
        jListFields.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                doAvailableListClick(e, jListFields);
            }
        });
        jScrollPane3.setViewportView(jListFields);
        jTabPaneMain.addTab(il8n._("Fields"), jScrollPane3);
        
        javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
        jListExceptions = new javax.swing.JList();
        jListExceptions.setFont(ListFontPlain);
        jListExceptions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                doAvailableListClick(e, jListExceptions);
            }
        });
        jScrollPane4.setViewportView(jListExceptions);
        jTabPaneMain.addTab(il8n._("Exceptions"), jScrollPane4);

        javax.swing.JScrollPane jScrollPane5 = new javax.swing.JScrollPane();
        jListProperties = new javax.swing.JList();
        jListProperties.setFont(ListFontPlain);
        jListProperties.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                doAvailableListClick(e, jListProperties);
            }
        });
        jScrollPane5.setViewportView(jListProperties);
        jTabPaneMain.addTab(il8n._("Properties"), jScrollPane5);
        
        ConnectorPane = getConnectorPane();
        jTabPaneMain.addTab(il8n._("Connectors"), ConnectorPane);
        
        add(jTabPaneMain, java.awt.BorderLayout.CENTER);
    }
}
