/*
 * LogSession.java
 *
 * Created on October 19, 2002, 1:48 AM
 */

package ca.mb.armchair.IDE;

/**
 *
 * @author  Dave Voorhis
 */
public class LogSession extends RootPanel {
    
    private javax.swing.JTextArea textThing;
    private Log owner;
    
    /** Creates a new instance of LogSession within a given RootPanelTabbed. */
    public LogSession(Log aLog, String Title) {
        super(Title);
        setName(Title);
        textThing = new javax.swing.JTextArea();
        textThing.setEditable(false);
        textThing.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getButton()!=e.BUTTON1)
                    rightClick(e.getX(), e.getY());
            }
        });
        javax.swing.JScrollPane jScrollPane = new javax.swing.JScrollPane();
        jScrollPane.setViewportView(textThing);
        addContentPanel(jScrollPane);
        owner = aLog;
    }

    // Create menu item.
    private javax.swing.JMenuItem createMenuItem(String text) {
        javax.swing.JMenuItem m = new javax.swing.JMenuItem(text);
        return m;
    }

    // Handle right click
    private void rightClick(int mouseX, int mouseY) {
        javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();

        javax.swing.JMenuItem clear = createMenuItem("Clear");
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                textThing.setText("");
            }
        });
        popup.add(clear);

        popup.show(textThing, mouseX, mouseY);
    }
        

    /** Print a string to the log. */
    public void print(String s) {
        owner.addContentPanel(this);
        textThing.append(s);
        System.out.print(s);      // echo to system out
    }
    
    /** Print a line to the log. */
    public void println(String s) {
        owner.addContentPanel(this);
        textThing.append(s + '\n');
        System.out.println(getName() + ": " + s);      // echo to system out
    }
}
