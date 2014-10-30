/*
 * Splash.java
 *
 * Created on August 1, 2002, 8:07 PM
 */

package ca.mb.armchair.IDE;

import ca.mb.armchair.JVPL.*;

/**
 * Splash screen.
 *
 * @author  Dave Voorhis
 */
public class Splash extends javax.swing.JWindow {
    
    /** Creates new form Splash */
    private Splash() {
        initComponents();
    }
    
    // widgets
    private void initComponents() {
        
        getContentPane().setLayout(new ca.mb.armchair.Utilities.Layouts.UserLayout());
        javax.swing.JLabel jLabelVersion = new javax.swing.JLabel();
        jLabelVersion.setOpaque(false);
        jLabelVersion.setLocation(10, 65);
        jLabelVersion.setText(Version.getVersion());
        jLabelVersion.setFont(new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 10));
        getContentPane().add(jLabelVersion);
        
        jProgressBar = new javax.swing.JProgressBar();
        jProgressBar.setOpaque(false);
        jProgressBar.setForeground(new java.awt.Color(150, 210, 150));
        jProgressBar.setBounds(10, 200, 300, 2);
        jProgressBar.setValue(0);
        getContentPane().add(jProgressBar);

        jProgressBar2 = new javax.swing.JProgressBar();
        jProgressBar2.setOpaque(false);
        jProgressBar2.setForeground(new java.awt.Color(150, 210, 150));
        jProgressBar2.setBounds(10, 216, 300, 2);
        jProgressBar2.setValue(0);
        jProgressBar2.setVisible(false);
        getContentPane().add(jProgressBar2);
        
        jLabelStatus = new javax.swing.JLabel();
        jLabelStatus.setText(il8n._("Starting") + "...");
        jLabelStatus.setOpaque(false);
        jLabelStatus.setBounds(10, 180, 300, 20);
        getContentPane().add(jLabelStatus);
        
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        jLabel1.setIcon(Version.getAppSplashImage());
        getContentPane().add(jLabel1);
        
        pack();
        
        // Centered
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int)(screenSize.getWidth() - 320) / 2, (int)(screenSize.getHeight() - 240) / 2);
    }
    
    // Update status display with a string.
    private void printlnRaw(String s) {
        jLabelStatus.setText(s);
    }
    
    // Reset progress bar length
    private void resetProgressBarRaw(int n) {
        jProgressBar.setMinimum(0);
        jProgressBar.setMaximum(n);
        jProgressBar.setValue(0);
    }
    
    // Set progress bar length
    private void setProgressBarRaw(int p) {
        jProgressBar.setValue(p);
    }
    
    // Get progress bar length
    private int getProgressBarRaw() {
        return jProgressBar.getValue();
    }

    // Set progress bar to completed
    private void completeProgressBarRaw() {
        jProgressBar.setValue(jProgressBar.getMaximum());
    }

    // Get secondary progress bar
    private javax.swing.JProgressBar getProgressBar2Raw() {
        return jProgressBar2;
    }
    
    // Sole instance of splash screen.
    private static Splash Splash = null;
    
    /** Show splash */
    public static void showSplash() {
        if (Splash==null)
            Splash = new Splash();
        Splash.show();
    }
    
    /** Hide splash */
    public static void hideSplash() {
        if (Splash!=null) {
            Splash.hide();
            Splash.dispose();
            Splash = null;
        }
    }
    
    /** Dismiss the splash.  Set status display to 100% and set timer.  When timer
     * goes off, window closes. */
    public static void dismissSplash() {
        if (Splash==null)
            return;
        javax.swing.Timer t = new javax.swing.Timer(1000, new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideSplash();
            }
        });
        t.setRepeats(false);
        t.start();
        Splash.completeProgressBar();
    }
    
    /** Reset progress bar length. */
    public static void resetProgressBar(int n) {
        if (Splash==null)
            return;
        Splash.resetProgressBarRaw(n);
    }
    
    /** Set progress bar length. */
    public static void setProgressBar(int p) {
        if (Splash==null)
            return;
        Splash.setProgressBarRaw(p);
    }
    
    /** Increment progress bar length by one tick. */
    public static void incrementProgressBar() {
        if (Splash==null)
            return;
        Splash.setProgressBarRaw(Splash.getProgressBarRaw() + 1);
    }
    
    /** Show status as completed. */
    public static void completeProgressBar() {
        if (Splash==null)
            return;
        Splash.completeProgressBarRaw();
    }

    /** Get secondary progress bar.  Null if splash is closed. */
    public static javax.swing.JProgressBar getProgressBar2() {
        if (Splash==null)
            return null;
        return Splash.getProgressBar2Raw();
    }
        
    /** Print status to splash and Log.println. */
    public static void println(String s) {
        if (Splash!=null)
            Splash.printlnRaw(s);
        IDE.getIDE().LogPrintln(s);
    }
    
    // Widgets
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JProgressBar jProgressBar2;
}
