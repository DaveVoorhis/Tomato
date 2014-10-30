/*
 * LibraryIO.java
 *
 * Created on October 10, 2002, 3:40 PM
 */

package ca.mb.armchair.JVPL;

import java.io.*;

/**
 * Library IO is centralised here, in order
 * to simply changing file formats.
 *
 * @author  Dave Voorhis
 */
 
public class LibraryIO {
    
    // Static from here on.
    private LibraryIO() {
    }
    
    /** Return array of file extensions that are probably a Library.  The 0th item is the default. */
    public static String[] getLibraryFileExtensions() {
        return new String[] {"vlb", "xml", "wlb"};
    }
    
    /** Save specified library to an XMLEncoder. */
    public static boolean saveLibrary(Library l, java.beans.XMLEncoder x) {
        try {
            x.writeObject(new LibrarySerializable(l));
        } catch (Throwable t) {
            Log.println("LibraryIO: Unable to save library to XMLEncoder: " + t.toString());
        }
        return true;
    }
    
    /** Save specified library to a binary stream. */
    public static boolean saveLibrary(Library l, ObjectOutputStream s) {
        try {
            s.writeObject(new LibrarySerializable(l));
            return true;
        } catch (Throwable t) {
            Log.println("LibraryIO: Unable to save library to ObjectOutputStream: " + t.toString());
            return false;
        }
    }
    
    /** Save specified library.  If file name ends with .wlb, save as binary.  Otherwise XML. */
    public static boolean saveLibrary(Library l, java.io.File theFile) {
        try {
            FileOutputStream out = new FileOutputStream(theFile);
            if (theFile.getName().toLowerCase().endsWith(".wlb")) {
                ObjectOutputStream s = new ObjectOutputStream(out);
                saveLibrary(l, s);
                s.flush();
            } else {    
                java.beans.XMLEncoder x = new java.beans.XMLEncoder(out);
                saveLibrary(l, x);
                x.close();
            }
            l.clearChanged();
            return true;
        } catch (Exception eee) {
            Log.println("LibraryIO: Unable to save library to " + theFile.getName() + ": " + eee.toString());
            Log.printStackTrace(eee.getStackTrace());
            return false;
        }
    }
    
    /** Load a library from an ObjectInputStream.  Return null if unable to load. */
    public static Library loadLibrary(ObjectInputStream s) {
        try {
            LibrarySerializable ls = (LibrarySerializable)s.readObject();
            return ls.getLibrary();
        } catch (Throwable t) {
            Log.println("LibraryIO: Unable to load library from ObjectInputStream: " + t.toString());
            return null;
        }
    }
    
    /** Load a library from an XMLDecoder.  Return null if unable to load. */
    public static Library loadLibrary(java.beans.XMLDecoder x) {
        try {
            LibrarySerializable ls = (LibrarySerializable)x.readObject();
            return ls.getLibrary();
        } catch (Throwable t) {
            Log.println("LibraryIO: Unable to load library from XMLDecoder: " + t.toString());
            return null;
        }
    }

    /** Load a given library from a file.  If file name ends in .wlb, it is assumed
     * to be a binary stream.  Otherwise XML.  Return null if failed. */
    public static Library loadLibrary(java.io.File f) {
        try {
            FileInputStream in = new FileInputStream(f);
            try {
                if (f.getName().toLowerCase().endsWith(".wlb"))
                    return loadLibrary(new ObjectInputStream(in));
                else
                    return loadLibrary(new java.beans.XMLDecoder(in));
            } catch (Exception e) {
                Log.println("LibraryIO: Unable to load library from " + f.getName() + ": " + e.toString());
                Log.printStackTrace(e.getStackTrace());
                return null;
            }
        } catch (Exception e) {
            Log.println("LibraryIO: Unable to load library from " + f.getName() + ": " + e.toString());
            return null;
        }
    }
}
