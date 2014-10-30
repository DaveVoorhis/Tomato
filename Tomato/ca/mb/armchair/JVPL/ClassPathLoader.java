/*
 * ClassPathLoader.java
 *
 * Created on November 8, 2002, 12:57 AM
 */

package ca.mb.armchair.JVPL;

/**
 * Mechanisms to load classes from arbitrarily-specified paths.
 *
 * @author  Dave Voorhis
 */
public class ClassPathLoader {
    
    /** Static access only. */
    private ClassPathLoader() {}
    
    /** Attempt to load a class given its name.  Will first attempt the
     * system class loader, then will try specified directories and files found
     * in the specified Library.  If the specified Library is null, only
     * attempt to load from the system class loader.
     *
     * @param l Library
     * @param n Class name
     * @throws java.lang.ClassNotFoundException
     */
    public static java.lang.Class forName(Library l, String n) throws java.lang.ClassNotFoundException {
        Class c;
        try {
            c = Class.forName(n);
        } catch (java.lang.ClassNotFoundException e) {
            java.net.URLClassLoader loader = new java.net.URLClassLoader(getURLs(l));
            c = loader.loadClass(n);
        }
        return c;
    }
    
    // Return specified class paths as URLs.
    private static java.net.URL[] getURLs(Library l) {
        java.net.URL[] urls = new java.net.URL[l.getMountedDirectories().size()];
        for (int i=0; i<urls.length; i++) {
            String urlString = "file:" + (String)(l.getMountedDirectories().get(i) + "/");
            try {
                urls[i] = new java.net.URL(urlString);
            } catch (java.net.MalformedURLException e) {
                Log.println("ClassPathLoader: malformed URL: " + urlString);
            }
        }
        return urls;
    }
}
