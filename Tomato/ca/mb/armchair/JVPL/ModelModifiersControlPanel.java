package ca.mb.armchair.JVPL;

/*
 * ModelModifiersControlPanel.java
 *
 * Created on September 26, 2002, 2:56 PM
 */

/**
 * This class implements a JPanel for editting the class modifiers.
 *
 * @author  Dave Voorhis
 */
public class ModelModifiersControlPanel extends javax.swing.JPanel {

    private final java.awt.Font ButtonFont = new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 10);
        
    // Widgets
    private javax.swing.JCheckBox jCheckBoxPublic;
    private javax.swing.JPanel jPanelClassType;
    private javax.swing.JRadioButton jRadioClassTypeDefault;
    private javax.swing.JRadioButton jRadioClassTypeAbstract;
    private javax.swing.JRadioButton jRadioClassTypeFinal;
    private javax.swing.JCheckBox jCheckBoxInterface;
    
    // The Model we're managing
    private Model theModel;

    /** Creates new panel */
    public ModelModifiersControlPanel(Model v) {
        setModel(v);
        initComponents();
    }

    /** Set Model to manage. */
    public void setModel(Model v) {
        theModel = v;
    }
    
    /** Get Model being managed. */
    public Model getModel() {
        return theModel;
    }
    
    private void initComponents() {
        jCheckBoxPublic = new javax.swing.JCheckBox();
        jPanelClassType = new javax.swing.JPanel();
        jRadioClassTypeDefault = new javax.swing.JRadioButton();
        jRadioClassTypeAbstract = new javax.swing.JRadioButton();
        jRadioClassTypeFinal = new javax.swing.JRadioButton();
        jCheckBoxInterface = new javax.swing.JCheckBox();

        jCheckBoxPublic.setSelected(true);
        jCheckBoxPublic.setText("public");
        jCheckBoxPublic.setBorder(null);
        jCheckBoxPublic.setFont(ButtonFont);
        jCheckBoxPublic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupShell();
            }
        });

        add(jCheckBoxPublic);

        javax.swing.ButtonGroup buttonGroupClassType = new javax.swing.ButtonGroup();

        jPanelClassType.setLayout(new javax.swing.BoxLayout(jPanelClassType, javax.swing.BoxLayout.X_AXIS));

        jPanelClassType.setBorder(new javax.swing.border.EtchedBorder());
        jRadioClassTypeDefault.setSelected(true);
        jRadioClassTypeDefault.setText(il8n._("<Default>"));
        jRadioClassTypeDefault.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioClassTypeDefault.setFont(ButtonFont);
        buttonGroupClassType.add(jRadioClassTypeDefault);
        jRadioClassTypeDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupShell();
            }
        });

        jPanelClassType.add(jRadioClassTypeDefault);

        jRadioClassTypeAbstract.setText("abstract");
        jRadioClassTypeAbstract.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioClassTypeAbstract.setFont(ButtonFont);
        buttonGroupClassType.add(jRadioClassTypeAbstract);
        jRadioClassTypeAbstract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupShell();
            }
        });

        jPanelClassType.add(jRadioClassTypeAbstract);

        jRadioClassTypeFinal.setText("final");
        jRadioClassTypeFinal.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioClassTypeFinal.setFont(ButtonFont);
        buttonGroupClassType.add(jRadioClassTypeFinal);
        jRadioClassTypeFinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupShell();
            }
        });

        jPanelClassType.add(jRadioClassTypeFinal);

        add(jPanelClassType);

        jCheckBoxInterface.setText("interface");
        jCheckBoxInterface.setBorder(null);
        jCheckBoxInterface.setFont(ButtonFont);
        jCheckBoxInterface.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setupShell();
            }
        });

        add(jCheckBoxInterface);
        
        setupButtons();
    }

    /** Get appropriate Java modifier string based on button state. */
    public String getModifierString() {
        String mods = "";
        if (jCheckBoxPublic.isSelected())
            mods += "public ";
        if (jRadioClassTypeAbstract.isSelected())
            mods += "abstract ";
        else if (jRadioClassTypeFinal.isSelected())
            mods += "final ";
        if (jCheckBoxInterface.isSelected())
            mods += "interface";
        else
            mods += "class";
        return mods;
    }
    
    /** Set button state based on Java modifier string. */
    public void setButtons(String s) {
        jCheckBoxPublic.setSelected(s.indexOf("public")>=0);
        jRadioClassTypeAbstract.setSelected(s.indexOf("abstract")>=0);
        jRadioClassTypeFinal.setSelected(s.indexOf("final")>=0);
        jRadioClassTypeDefault.setSelected(!jRadioClassTypeAbstract.isSelected() && !jRadioClassTypeFinal.isSelected());
        jCheckBoxInterface.setSelected(s.indexOf("interface")>=0);
    }
    
    // Set buttons from the Model managed by the Shell
    private void setupButtons() {
        setButtons(theModel.getModifierString());
    }
    
    // Set Shell's Model from the buttons, and update buttons if appropriate
    private void setupShell() {
        theModel.setModifierString(getModifierString());
    }
}

