/*
 * Version.java
 *
 * Created on August 2, 2002, 2:38 AM
 */

package ca.mb.armchair.JVPL;

/**
 * Version information.
 *
 * @author  Dave Voorhis
 */
public class Version {
    
    private static String AppName = "Tomato";         // for titles and displays
    private static String ShortAppName = "Tomato";    // for filenames and identifiers
    private static String AppIcon = "/ca/mb/armchair/IDE/resources/Tomato32x32.png";
    private static String AppSplash = "/ca/mb/armchair/IDE/resources/Tomato320x240.png";
    private static int MajorVersion = 0;
    private static int MinorVersion = 0;
    private static int Revision = 0;
    private static String Release = "pre";

    private static java.net.URL ImageIconResource = null;
    private static java.net.URL SplashImageResource = null;
    
    private Version() {
    }
    
    /** Get resource filename of splash screen image. */
    public static String getAppSplashFilename() {
        return AppSplash;
    }
    
    /** Get ImageIcon of splash screen image. */
    public static javax.swing.ImageIcon getAppSplashImage() {
        if (SplashImageResource==null)
            SplashImageResource = (new Object().getClass()).getResource(getAppSplashFilename());
        return new javax.swing.ImageIcon(SplashImageResource);
    }
    
    /** Get resource filename of application icon image. */
    public static String getAppIconFilename() {
        return AppIcon;
    }
    
    /** Get ImageIcon of application icon image. */
    public static javax.swing.ImageIcon getAppIcon() {
        if (ImageIconResource==null)
            ImageIconResource = (new Object().getClass()).getResource(getAppIconFilename());
        return new javax.swing.ImageIcon(ImageIconResource);        
    }
    
    /** Major version number. */
    public static int getMajorVersion() {
        return MajorVersion;
    }
    
    /** Minor version number. */
    public static int getMinorVersion() {
        return MinorVersion;
    }
    
    /** Revision or patch number. */
    public static int getRevision() {
        return Revision;
    }
    
    /** Release type. */
    public static String getRelease() {
        return Release;
    }
    
    /** Stringize */
    public static String getVersion() {
        return MajorVersion + "." + MinorVersion + "." + Revision + "-" + Release;
    }
    
    /** Application name.  May have spaces, illegal filename characters, etc.  Used for display
    * titles, etc. */
    public static String getAppName() {
        return AppName;
    }
    
    /** Application name.  May not have spaces, illegal filename characters, etc.  Used for constructing
     * identifiers, filenames, etc. */
    public static String getShortAppName() {
        return ShortAppName;
    }
}
