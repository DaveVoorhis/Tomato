package ca.mb.armchair.JVPL;

/*
 * Library.java
 *
 * Created on June 7, 2002, 7:19 PM
 */

import java.lang.reflect.*;
import javax.swing.tree.*;
import java.util.*;
import ca.mb.armchair.Utilities.ClassLoaders.*;

/**
 * Class to maintain and obtain loadable class names.
 *
 * Eventually will evolve into general resource manager.
 *
 * @author  Dave Voorhis
 */
public class Library {

    public final static int COMPILER_JAVAC = 0;
    public final static int COMPILER_JIKES = 1;
    
    public static final String ClassSubtreeName = "Classes";
    public static final String ClassHeirarchySubtreeName = "Class Heirarchy";
    public static final String FavoritesSubtreeName = "Favorites";
    public static final String RecentlyUsedSubtreeName = "Recently Used";
    
    private int CompilerSelection = COMPILER_JAVAC;
    private String LibraryName;
    private LibraryEntry LibraryModel = null;
    private Vector mountedDirectories = new Vector();

    // Used internally to recognized visited directories, to prevent infinite
    // recursion.
    private TreeSet visitedDirectories = new TreeSet();
    
    /** Ctor */
    public Library(String Name) {
        LibraryName = Name;
        setLibraryModel(new LibraryEntry(LibraryName));
        clearChanged();
    }
    
    /** Get library name */
    public String getName() {
        return LibraryName;
    }
    
    // Changed flag.
    private boolean Changed = false;

    /** Reset 'Changed' flag */
    public void clearChanged() {
        Changed = false;
    }
    
    /** Get 'Changed' flag */
    public boolean isChanged() {
        return Changed;
    }
    
    /** Set 'Changed' flag */
    public void setChanged() {
        Changed = true;
    }
    
    /** Set the selected compiler. */
    public void setCompiler(int Compiler) {
        CompilerSelection = Compiler;
    }
    
    /** Get the selected compiler. */
    public int getCompiler() {
        return CompilerSelection;
    }
    
    /** true if library is empty */
    public boolean isEmpty() {
        LibraryEntry main = findLibraryEntry(ClassHeirarchySubtreeName, LibraryModel);
        if (main==null) {
            Log.println("Library: Unable to find 'Class Heirarchy' subtree of library");
            return true;
        }
        return (!main.children().hasMoreElements());
    }

    /** Get the vector of mounted directories. */
    public Vector getMountedDirectories() {
        return mountedDirectories;
    }
    
    /** Set the vector of mounted directories. */
    public void setMountedDirectories(Vector v) {
        mountedDirectories = v;
        setChanged();
    }
    
    /** Add a mounted directory. */
    public void addMountedDirectory(String directoryPath) {
        mountedDirectories.add(directoryPath);
    }
    
    /** Remove a mounted directory. */
    public void removeMountedDirectory(String directoryPath) {
        for (int i=0; i<mountedDirectories.size(); i++)
            if (directoryPath.compareTo((String)mountedDirectories.get(i))==0) {
                mountedDirectories.remove(i);
                return;
            }
        return;
    }
    
    /** Get the entire tree of classes. */
    public LibraryEntry getLibraryModel() {
        return LibraryModel;
    }

    /** set LibraryModel of classes */
    public void setLibraryModel(LibraryEntry e) {
        LibraryModel = e;
        setChanged();
    }
    
    /** Get a subtree of entries, given a subtree name. */
    public DefaultMutableTreeNode getLibraryModel(String subtree) {
        return findLibraryEntry(subtree, LibraryModel);
    }
    
    /** Get a class name, with messy array syntax cleaned. */
    public static String getClassName(Class s) {
        String ClassName;
        if (s.isArray())
            try {
                ClassName = s.getName().substring(2, s.getName().length()-1) + "[]";
            } catch (IndexOutOfBoundsException e) {
                return s.getName();
            }
        else
            ClassName = s.getName();
        return ClassName;
    }
    
    // Recursive tree-structured library builder.  Return node associated with theClass className.
    private LibraryEntry addLibraryEntry(LibraryEntry newEntry, LibraryEntry localRoot) {
        String className = newEntry.getDisplayText();
        int dotPosition = className.indexOf('.');               // Does className contain a dot?
        String searchFor;
        if (dotPosition==-1)
            searchFor = className;                              // If not, look it up
        else
            searchFor = className.substring(0, dotPosition);    // If so, parse off first part and do lookup
        // look for it
        LibraryEntry foundNode = null;
        for (java.util.Enumeration e = localRoot.children(); e.hasMoreElements();) {
            LibraryEntry iteratedNode = (LibraryEntry)e.nextElement();
            if (iteratedNode.getDisplayText().compareTo(searchFor)==0) {
                foundNode = iteratedNode;
                break;
            }
        }
        // tree node wasn't found.  create a new one
        if (foundNode == null) {
            if (dotPosition==-1)
                foundNode = newEntry;   // leaf
            else
                foundNode = new LibraryEntry(searchFor);        // non-leaf
            localRoot.add(foundNode);
            setChanged();
        }
        // If className contains dot, invoke addClassName with remainder of string and new/existing child
        if (dotPosition==-1)
            return foundNode;           // done
        else
            return addLibraryEntry(new LibraryEntry(
                    className.substring(dotPosition + 1), 
                    newEntry.getLoadableClassName()), 
                    foundNode);
    }
    
    /** Return a class if it's in the given library heirarchy, null otherwise */
    public LibraryEntry findLibraryEntry(String className, LibraryEntry localRoot) {
        int dotPosition = className.indexOf('.');               // Does className contain a dot?
        String searchFor;
        if (dotPosition==-1)
            searchFor = className;                              // If not, look it up
        else
            searchFor = className.substring(0, dotPosition);    // If so, parse off first part and do lookup
        // look for it
        LibraryEntry foundNode = null;
        for (java.util.Enumeration e = localRoot.children(); e.hasMoreElements();) {
            LibraryEntry iteratedNode = (LibraryEntry)e.nextElement();
            if (iteratedNode.getDisplayText().compareTo(searchFor)==0) {
                foundNode = iteratedNode;
                break;
            }
        }
        // If className contains dot, invoke findClassName with remainder of string and new/existing child
        if (dotPosition==-1 || foundNode==null)
            return foundNode;           // done
        else
            return findLibraryEntry(className.substring(dotPosition + 1), foundNode);
    }

    // Get a particular subheading node.  Create it if it does not exist.
    private LibraryEntry getHeadingNode(String SubHeading, LibraryEntry Heading) {
        LibraryEntry heading = findLibraryEntry(SubHeading, Heading);
        if (heading==null) {
            heading = new LibraryEntry(SubHeading);
            Heading.add(heading);
            setChanged();
        }
        return heading;
    }
    
    // Get a particular top-level heading node.  Create it if it does not exist.
    private LibraryEntry getHeadingNode(String Heading) {
        return getHeadingNode(Heading, LibraryModel);
    }
    
    /** Add a library entry to a particular heirarchy. */
    public LibraryEntry addEntryUnderHeading(LibraryEntry le, LibraryEntry heading) {
        LibraryEntry l = le;
        l = findLibraryEntry(le.getLoadableClassName(), heading);
        if (l==null)
            l = addLibraryEntry(new LibraryEntry(le), heading);
        return l;
    }
    
    /** Add a library entry to a particular main heirarchy.  Create the heirarchy entry
     * if it does not exist. */
    public LibraryEntry addEntryUnderHeading(LibraryEntry le, String Heading) {
        return addEntryUnderHeading(le, getHeadingNode(Heading));
    }
    
    /** Add a library entry to the Favorites heirarchy */
    public LibraryEntry addFavorite(LibraryEntry le) {
        return addEntryUnderHeading(le, getHeadingNode(FavoritesSubtreeName, getHeadingNode(ClassSubtreeName)));
    }
    
    /** Add a library entry to the Recently Used heirarchy */
    public LibraryEntry addRecentlyUsed(LibraryEntry le) {
        return addEntryUnderHeading(le, getHeadingNode(RecentlyUsedSubtreeName, getHeadingNode(ClassSubtreeName)));
    }
    
    /** Add a class to the library */
    public void addClass(Class s) {
        if (s==null || s.getName()=="void")
            return;
        String ClassName = getClassName(s);
        LibraryEntry main = getHeadingNode(ClassHeirarchySubtreeName, getHeadingNode(ClassSubtreeName));
        if (findLibraryEntry(ClassName, main)==null) {
            addEntryUnderHeading(new LibraryEntry(ClassName, s.getName()), main);
            addClass(s.getSuperclass());
            addClass(s.getComponentType());
            addClass(s.getInterfaces());
            addClass(s.getDeclaredClasses());
            addMethodClasses(s.getMethods());
            addFieldTypes(s.getFields());
            addConstructors(s.getConstructors());
            setChanged();
        }
    }
    
    /** Add an array of classes to the library */
    public void addClass(Class c[]) {
        if (c==null)
            return;
        for (int i=0; i<c.length; i++) {
            addClass(c[i]);
        }
    }
    
    /** Add a named class to the library. */
    public void addClass(String s) throws java.lang.ClassNotFoundException {
        addClass(ClassPathLoader.forName(this, s));
    }
    
    /** Return true if a File has a given extension.  Return the extension, null if not found. */
    public static String isFileExtensionInExtensionList(java.io.File theFile, String[] extensions) {
        for (int i=0; i<extensions.length; i++)
            try {
                String upperExtensionToTest = "." + extensions[i].toUpperCase();
                if (theFile.getCanonicalPath().toUpperCase().endsWith(upperExtensionToTest))
                    return upperExtensionToTest;
            } catch (java.io.IOException e) {
            }
        return null;
    }
    
    /** Return a filename with its extension (obtained via isFileExtensionInExtensionList()) stripped,
     * and converted to a raw class name.  It may still have a mount point prepended to it! */
    public static String getFileNameAsRawClassName(String CanonicalPath, String foundextension) {
        return CanonicalPath.substring(0, CanonicalPath.toUpperCase().lastIndexOf(foundextension)).replace(java.io.File.separatorChar, '.');
    }
    
    /** Given a raw class name from getFileNameAsRawClassName(), convert it to a true
     * class name relative to a given mount point. */
    public static String getClassNameFromRawClassName(String MountPoint, String ClassPath) {
        String convertedMountPoint = MountPoint.replace(java.io.File.separatorChar, '.');
        if (ClassPath.startsWith(convertedMountPoint))
            return ClassPath.substring(convertedMountPoint.length() + 1);
        return ClassPath;
    }

    /** Given a filename with a specified extension, convert it to a class name. */
    public static String getClassNameFromFileName(String MountPath, String CanonicalFilePath, String foundExtension) {
        return getClassNameFromRawClassName(MountPath, getFileNameAsRawClassName(CanonicalFilePath, foundExtension));
    }
    
    /** Recursively add contents of a Java file (ie., directory or normal file) to the class set. */
    public void addFile(java.io.File MountPoint, java.io.File f) {
        String CanonicalPath;
        try {
            CanonicalPath = f.getCanonicalPath();
        } catch (java.io.IOException e) {
            Log.println("Library: Unable to determine canonical path for " + f.getAbsolutePath() + ": " + e.toString());
            return;
        }
        if (visitedDirectories.contains(CanonicalPath))
            return;
        visitedDirectories.add(CanonicalPath);
        if (f.isDirectory()) {
            // Add directory
            Log.println("Library: Scanning directory: " + CanonicalPath);
            java.io.File flist[] = f.listFiles();
            for (int i=0; i<flist.length; i++)
                addFile(MountPoint, flist[i]);
        } else {
            String MountPath;
            try {
                MountPath = MountPoint.getCanonicalPath();
            } catch (java.io.IOException e) {
              Log.println("Library: Unable to determine canonical path for " + MountPoint.getAbsolutePath() + ": " + e.toString());
              return;
            }
            String foundExtension = isFileExtensionInExtensionList(f, new String[] {"CLASS"});
            if (foundExtension != null && CanonicalPath.indexOf('$')==-1 ) {
                // Add class
                String ClassName = getClassNameFromFileName(MountPath, CanonicalPath, foundExtension);
                try {
                    addClass(ClassName);
                    Log.println("Library: Class " + ClassName + " loaded from file " + CanonicalPath);
                } catch (Throwable t) {
                    Log.println("Library: Class " + ClassName + " not loaded from file " + CanonicalPath);
                }
            }
        }
    }
    
    /** Add contents of a directory to the library. */
    public void addDirectory(java.io.File mountPoint) {
        addFile(mountPoint, mountPoint);
    }
    
    /** Add contents of a directory to the library with a given mount point. */
    public void addDirectory(String mountPoint) {
        addDirectory(new java.io.File(mountPoint));
    }
    
    /** Add contents of a JAR to the class set by iterating its entries. */
    private void addJarContents(JarClassLoader jcl, java.net.URL u) {
        try {
            java.net.JarURLConnection uc = (java.net.JarURLConnection)u.openConnection();
            java.util.jar.JarFile jf = uc.getJarFile();
            for (java.util.Enumeration e = jf.entries(); e.hasMoreElements(); ) {
                Object o = (Object)e.nextElement();
                java.util.jar.JarEntry je = jf.getJarEntry(o.toString());
                if (je!=null && je.getName().toUpperCase().endsWith(".CLASS")) {
                    String ClassName = je.getName().substring(0, je.getName().toUpperCase().lastIndexOf(".CLASS")).replace(java.io.File.separatorChar, '.');
                    try {
                        addClass(jcl.loadClass(ClassName));
                    } catch (Throwable y) {
                        Log.println("Library: Unable to load class " + ClassName + " from jar " + y.toString());
                    }
                }
            }
        } catch (Exception e) {
            Log.println("Library: Unable to open jar: " + u.toString());
        }
    }
    
    /** Add contents of a JAR to the class set. */
    public void addJar(String jarname) {
        Log.println("Library: Scanning jar: " + jarname);
        try {
            java.net.URL u = new java.net.URL("jar:file:" + jarname + "!/");
            JarClassLoader jcl = new JarClassLoader(u);
            addJarContents(jcl, u);
        } catch (Exception e) {
            Log.println("Library: Unable to load jar: " + jarname + ": " + e.toString());
        }
    }
    
    /** Add a jar file or directory, depending on extension */
    public void addClassStorage(String path) {
        String upperPath = path.toUpperCase();
        if (upperPath.indexOf(".JAR")!=-1)
            addJar(path);
        else if (upperPath.indexOf(".ZIP")!=-1)
            addJar(path);
        else
            addDirectory(path);
    }
    
    /** Add a class path, which may include directories or .jar file names, separated by colons */
    public void addClassPath(String classpath) {
        int colonPosition = classpath.indexOf(':');
        if (colonPosition==-1)
            addClassStorage(classpath);
        else {
            addClassStorage(classpath.substring(0, colonPosition));
            addClassPath(classpath.substring(colonPosition + 1));
        }
    }
    
    // add method parameter and return type classes to the class set
    private void addMethodClasses(Method m[]) {
        if (m==null)
            return;
        for (int i = 0; i < m.length; i++) {
            addClass(m[i].getReturnType());
            addClass(m[i].getParameterTypes());
            addClass(m[i].getExceptionTypes());
        }
    }
    
    // add field types to the class set
    private void addFieldTypes(Field f[]) {
        if (f==null)
            return;
        for (int i = 0; i < f.length; i++)
            addClass(f[i].getType());
    }
    
    // add constructor parameter types to the class set
    private void addConstructors(Constructor constructors[]) {
        if (constructors==null)
            return;
        for (int i=0; i<constructors.length; i++) {
            addClass(constructors[i].getParameterTypes());
            addClass(constructors[i].getExceptionTypes());
        }
    }
}
