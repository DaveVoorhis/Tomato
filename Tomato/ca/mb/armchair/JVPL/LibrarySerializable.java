/*
 * LibrarySerializable.java
 *
 * Created on October 31, 2002  1:01 PM
 */

package ca.mb.armchair.JVPL;

/**
 * A serializable representation of a Library.
 *
 * @author  Dave Voorhis
 */

public class LibrarySerializable extends java.lang.Object implements java.io.Serializable {
        
    private String LibraryName;
    private LibraryEntry LibraryModel;
    private java.util.Vector mountedDirectories = new java.util.Vector();
    private int Compiler;
    
    /** Ctor. */
    public LibrarySerializable(Library l) {
        LibraryName = l.getName();
        LibraryModel = l.getLibraryModel();
        mountedDirectories = l.getMountedDirectories();
        Compiler = l.getCompiler();
    }
    
    /** Ctor. */
    public LibrarySerializable() {
        LibraryName = null;
        LibraryModel = null;
        mountedDirectories = new java.util.Vector();
        Compiler = Library.COMPILER_JAVAC;
    }
    
    /** Get a library from this serializable representation of a library. */
    public Library getLibrary() {
        Library l = new Library(getLibraryName());
        l.setLibraryModel(getLibraryModel());
        l.setMountedDirectories(getMountedDirectories());
        l.setCompiler(getCompiler());
        l.clearChanged();
        return l;
    }
    
    /** Get compiler. */
    public int getCompiler() {
        return Compiler;
    }
    
    /** Set compiler. */
    public void setCompiler(int C) {
        Compiler = C;
    }
    
    /** Get library name. */
    public String getLibraryName() {
        return LibraryName;
    }
    
    /** Set library name. */
    public void setLibraryName(String n) {
        LibraryName = n;
    }
    
    /** Get the library model. */
    public LibraryEntry getLibraryModel() {
        return LibraryModel;
    }
    
    /** Set the library model. */
    public void setLibraryModel(LibraryEntry le) {
        LibraryModel = le;
    }
    
    /** Get the mounted directories. */
    public java.util.Vector getMountedDirectories() {
        return mountedDirectories;
    }
    
    /** Set the mounted directories. */
    public void setMountedDirectories(java.util.Vector v) {
        mountedDirectories = v;
    }
}
