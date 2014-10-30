/*
 * ModelToJava.java
 *
 * Created on August 31, 2002, 10:17 PM
 */

package ca.mb.armchair.JVPL;

/**
 * Output contents of given model as a Java source string or file.
 *
 * This class contains various mechanisms to facilitate
 * generating Java source from Visualisers.
 *
 * @author  Dave Voorhis
 */
public class ModelToJava {
    
    private Library theLibrary;
    private Shell theShell;
    
    /** Create a compiler interface. */
    public ModelToJava(Library l, Shell s) 
    {
        theLibrary = l;
        theShell = s;
    }
    
    /** Given an arbitrary string, turn it into a Java identifier. */
    public static String getIdentifier(String s) {
        if (s==null)
            return "null";
        String outstr = "";
        if (!Character.isJavaIdentifierStart(s.charAt(0)))
            outstr += "v";
        for (int i=0; i<s.length(); i++)
            if (!Character.isJavaIdentifierPart(s.charAt(i)))
                outstr += "_";
            else
                outstr += s.charAt(i);
        return outstr;
    }

    //
    // The following consitutes a simple "database" of wrapped visualisers
    // used by the code generator.
    //
    
    // Visualisers that are to invoke a message visualiser before return use 
    private java.util.Vector wrappedGetVisualisers = new java.util.Vector();
    
    // A vector of vectors containing the VisualiserOfMessages associated
    // with a message visualiser
    private java.util.Vector wrappedGetVisualiserMessages = new java.util.Vector();

    // Visualisers that are to invoke a message visualiser after a parm change.
    private java.util.Vector wrappedSetVisualisers = new java.util.Vector();

    // A vector of vectors containing the VisualiserOfMessages associated
    // with a message visualiser
    private java.util.Vector wrappedSetVisualiserMessages = new java.util.Vector();
    
    // Add a wrapped visualiser to a 'database'
    private void addWrappedVisualiser(java.util.Vector wrappedVisualiserMessages, 
                                             java.util.Vector wrappedVisualisers,
                                             VisualiserOfMessage vom,
                                             Visualiser v) {
        java.util.Vector messages;
        int where = wrappedVisualisers.indexOf(v);
        if (where == -1) {
            wrappedVisualisers.add(v);
            messages = new java.util.Vector();
            wrappedVisualiserMessages.add(messages);
        } else
            messages = (java.util.Vector)wrappedVisualiserMessages.get(where);
        messages.add(vom);
    }
    
    // Add a 'get' wrapped visualiser to the 'database'
    private void addGetWrappedVisualiser(VisualiserOfMessage vom, Visualiser v) {
        addWrappedVisualiser(wrappedGetVisualiserMessages, wrappedGetVisualisers, vom, v);
    }
    
    // Add a 'set' wrapped visualiser to the 'database'
    private void addSetWrappedVisualiser(VisualiserOfMessage vom, Visualiser v) {
        addWrappedVisualiser(wrappedSetVisualiserMessages, wrappedSetVisualisers, vom, v);
    }
    
    // Clear wrapper database
    private void clearWrapperDatabase() {
        wrappedSetVisualisers.clear();
        wrappedSetVisualiserMessages.clear();
        wrappedGetVisualisers.clear();
        wrappedGetVisualiserMessages.clear();
    }

    //
    // 'Database' functionality ends here.
    //
        
    // Given a visualiser on right that will be assigned to on object of a given class on the left,
    // create an appropriate typecast clause.  Empty string if the types are identical.
    public static String getCast(Class vTo, Visualiser vFrom) {
        if (vTo != vFrom.getBoundClass())
            return "(" + vTo.getName() + ")";
        else
            return "";
    }
    
    // Given a visualiser on right that will be assigned to a visualiser on the left,
    // create an appropriate typecast clause.  Empty string if the types are identical.
    public static String getCast(Visualiser vTo, Visualiser vFrom) {
        return getCast(vTo.getBoundClass(), vFrom);
    }
    
    // Construct unique identifier from visualiser name, which may not be unique.
    public static String getUniqueVisualiserName(Visualiser v) {
        return v.getName() + v.getID();
    }
    
    // Generate code to dereference a Visualiser.
    public String getDereference(Visualiser x) {
        if (wrappedGetVisualisers.contains(x))
            return "get" + getIdentifier(getUniqueVisualiserName(x)) + "()";
        else
            return getIdentifier(x.getName());
    }

    // Generate code to assign a Visualiser from some arbitary code.
    public String getAssignment(Visualiser x, String assignFrom) {
        if (wrappedSetVisualisers.contains(x))
            return "set" + getIdentifier(getUniqueVisualiserName(x)) + "(" + assignFrom + ");";
        else
            return getIdentifier(x.getName()) + " = " + assignFrom + ";";
    }
    
    // True if dereference to a given Visualiser will use a wrapper.
    public boolean isGatedGetAccess(Visualiser x) {
        return (wrappedGetVisualisers.contains(x));
    }
    
    // True if assignment to a given Visualiser will use a wrapper.
    public boolean isGatedSetAccess(Visualiser x) {
        return (wrappedSetVisualisers.contains(x));
    }
    
    // True if dereference to assignment to a given Visualiser will use a wrapper.
    public boolean isGatedAccess(Visualiser x) {
        return (wrappedSetVisualisers.contains(x) || wrappedGetVisualisers.contains(x));
    }
    
    // Build parameters for method or constructor invocation
    public String buildParms(Class[] invokeParameters, VisualiserOfMessage vom) {
        String parms = "";
        for (int i=0; i<vom.getParameterConnectorCount(); i++) {
            if (parms.length()>0)
                parms += ", ";
            if (vom.getParameterConnector(i)!=null && vom.getParameterConnector(i).getConnection(0)!=null) {
                Visualiser Argument = vom.getParameterConnector(i).getConnection(0).getVisualiser();
                parms += getCast(invokeParameters[i], Argument) + getDereference(Argument);
                if (Argument.getPrimitiveName()!=null && !Argument.getBoundClass().isPrimitive() && invokeParameters[i].isPrimitive())
                        parms += "." + Argument.getPrimitiveName() + "Value()";
            } else
                parms += "p" + i;
        }
        return parms;
    }
       
    // Determine which Visualisers need to be wrapped, because they're
    // either connected to an isInvokesAfterParmChange, isInvokesBeforeReturnUse,
    // isInvokesAfterReferenceChange, or isInvokesBeforeReferenceUse message visualiser.  Store these
    // Visualisers in appropriate containers.
    private void buildWrappers() {
        Model m = theShell.getModel();
        clearWrapperDatabase();
        for (int i=0; i<m.getVisualiserCount(); i++) {
            Visualiser v = m.getVisualiser(i);
            if (v.isVisualiserOfMessage()) {
                VisualiserOfMessage vom = (VisualiserOfMessage)v;
                if (vom.isInvokesBeforeReturnUse() && 
                    vom.getConnectorToReturnType()!=null && 
                    vom.getConnectorToReturnType().getConnectionCount()>0)
                        addGetWrappedVisualiser(vom, vom.getConnectorToReturnType().getConnection(0).getVisualiser());
                if (vom.isInvokesAfterParmChange())
                    for (int p=0; p<vom.getParameterConnectorCount(); p++)
                        if (vom.getParameterConnector(p)!=null && vom.getParameterConnector(p).getConnectionCount()>0)
                            addSetWrappedVisualiser(vom, vom.getParameterConnector(p).getConnection(0).getVisualiser());
                if (vom.isInvokesAfterReferenceChange() || vom.isInvokesBeforeReferenceUse())
                    for (int c=0; c<vom.getConnectionCount(); c++) {
                        if (vom.isInvokesAfterReferenceChange())
                            addSetWrappedVisualiser(vom, vom.getConnection(c).getConnector().getVisualiser());
                        if (vom.isInvokesBeforeReferenceUse())
                            addGetWrappedVisualiser(vom, vom.getConnection(c).getConnector().getVisualiser());
                    }
            }
        }
    }

    // Build code for Wrapper functions around visualisers that issue triggers before return use
    private String getJavaGetWrappers() {
        String methods = "";
        for (int i=0; i<wrappedGetVisualisers.size(); i++) {
            Visualiser v = (Visualiser)wrappedGetVisualisers.get(i);
            String VID = getIdentifier(v.getName());
            methods += beginLine() + "private final " + v.getBoundClass().getName() + " get" + getIdentifier(getUniqueVisualiserName(v));
            methods += "()" + beginBlock();
            java.util.Vector messages = (java.util.Vector)wrappedGetVisualiserMessages.get(i);
            String methodsInvokedBeforeReturnUse = "";
            String methodsInvokedBeforeReferenceUse = "";
            for (int j=0; j<messages.size(); j++) {
                VisualiserOfMessage VM = (VisualiserOfMessage)messages.get(j);
                String invokeLine = beginLine() + getIdentifier(VM.getName()) + "();";
                if (VM.isInvokesBeforeReturnUse())
                    methodsInvokedBeforeReturnUse += invokeLine;
                else if (VM.isInvokesBeforeReferenceUse())
                    methodsInvokedBeforeReferenceUse += invokeLine;
                else {
                    methods += beginLine() + "// Unknown VisualiserOfMessage state:";
                    methods += invokeLine;
                }
            }
            methods += methodsInvokedBeforeReturnUse + methodsInvokedBeforeReferenceUse;
            methods += beginLine() + "return " + VID + ";";
            methods += endBlock();
        }
        return methods;
    }
    
    // Wrapper functions around visualisers that issue triggers after instance change
    private String getJavaSetWrappers() {
        String methods = "";
        for (int i=0; i<wrappedSetVisualisers.size(); i++) {
            Visualiser v = (Visualiser)wrappedSetVisualisers.get(i);
            String VID = getIdentifier(v.getName());
            methods += beginLine() + "private final void set" + getIdentifier(getUniqueVisualiserName(v));
            methods += "(" + v.getBoundClass().getName() + " p0)" + beginBlock();
            methods += beginLine() + VID + " = p0;";
            java.util.Vector messages = (java.util.Vector)wrappedSetVisualiserMessages.get(i);
            String methodsInvokedAfterParmChange = "";
            String methodsInvokedAfterReferenceChange = "";
            for (int j=0; j<messages.size(); j++) {
                VisualiserOfMessage VM = (VisualiserOfMessage)messages.get(j);
                String invokeLine = beginLine() + getIdentifier(VM.getName()) + "();";
                if (VM.isInvokesAfterParmChange())
                    methodsInvokedAfterParmChange += invokeLine;
                else if (VM.isInvokesAfterReferenceChange())
                    methodsInvokedAfterReferenceChange += invokeLine;
                else {
                    methods += beginLine() + "// Unknown VisualiserOfMessage state:";
                    methods += invokeLine;
                }
            }
            methods += methodsInvokedAfterParmChange + methodsInvokedAfterReferenceChange + endBlock();
        }
        return methods;
    }
    
    // Get code for wrapper functions.
    private String getJavaWrappers() {
        return getJavaGetWrappers() + getJavaSetWrappers();
    }

    // Initialise wrapper database.  Generate wrapper Java
    // code and any other necessary extensions.
    private String getJVPLExtensions() {
        buildWrappers();                // Construct wrapper database.
        return getJavaWrappers();       // Get code for wrapper functions
    }
    
    // Block nesting level.
    private int blockNestingLevel = 0;
    
    /** Return source to begin a new block. */
    public String beginBlock() {
        blockNestingLevel++;
        return " {";
    }
    
    /** Return source to begin a new line. */
    public String beginLine() {
        String s = "\n";
        for (int i=0; i<blockNestingLevel; i++)
            s += "\t";
        return s;
    }
    
    /** Return source to end a block. */
    public String endBlock() {
        blockNestingLevel--;
        String s = beginLine();
        return s + "} ";
    }
        
    /** Obtain the class name associated with this model. */
    public String getClassName() {
        String rawName = theShell.getModel().getName();
        int lastDotPosition = rawName.lastIndexOf('.');
        if (lastDotPosition==-1)
            return rawName;                // class name is model name
        return rawName.substring(lastDotPosition + 1);
    }
    
    /** Obtain the package name (in dotted form) associated with this model from the name. 
     * Null if there isn't one. */
    public String getPackageName() {
        String rawName = theShell.getModel().getName();
        int lastDotPosition = rawName.lastIndexOf('.');
        if (lastDotPosition==-1)
            return null;                    // unpackaged
        return rawName.substring(0, lastDotPosition);
    }
    
    /** Generate a big blob of Java source from the given model.
     * NOTE: As of this writing, the code generated
     * has not been formally validated against the source code (i.e.,
     * the model).  Use at your own risk.*/
    public String getJavaSource() {
        String source = "";
        if (getPackageName()!=null)
            source += beginLine() + "package " + getPackageName() + ";";
        source += beginLine() + theShell.getModel().getModifierString() + " " + getClassName() + beginBlock();
        source += getJVPLExtensions();                 // Build wrapped and special functions
        for (int i=0; i<theShell.getModel().getVisualiserCount(); i++)                // Build methods and field declarations.
            source += theShell.getModel().getVisualiser(i).getJavaSource(this);
        source += endBlock();
        return source;
    }
    
    /** Save a model to a filesystem, via its shell, to a location appropriate to its 
     * package and class name, within the given base directory.
     * Returns a java.io.File to the source if successful, null otherwise. */
    public java.io.File saveJavaSource() {
        if (theShell.getModelFile() == null) {
            Log.println("ModelToJava: Shell does not have a defined source file.");
            return null;
        } else if (theShell.getBaseDirectory() == null) {
            Log.println("ModelToJava: Shell does not have a defined base directory.");
            return null;
        } else {
            String source = getJavaSource();
            String packageName = getPackageName();
            java.io.File outf = new java.io.File(theShell.getBaseDirectory() + java.io.File.separator + 
                    ((packageName==null) ? "" : (packageName.replace('.', java.io.File.separatorChar) + java.io.File.separator)) + 
                    getClassName().replace('.', java.io.File.separatorChar) + ".java");
            Log.println("ModelToJava: Create source file " + outf.getAbsolutePath());
            try {
                outf.createNewFile();
                java.io.FileWriter f = new java.io.FileWriter(outf);
                f.write(source);
                f.close();
                return outf;
            } catch (Throwable w) {
                Log.println("ModelToJava: couldn't save Java source: " + w.toString());
                return null;
            }
        }
    }
    
    /** Shutdown flag for threaded capture. */
    private static boolean endCapture = true;
    
    // Given an input stream, spew it to stdout.  Return the thread that
    // handles this capture.
    private static java.lang.Thread captureStream(final java.io.InputStream s) {
        java.lang.Thread t = new Thread() {
            public void run() {
                java.io.BufferedInputStream bs = new java.io.BufferedInputStream(s);
                while (true) {
                    try {
                        int available = bs.available();
                        if (available==0)
                            if (endCapture)
                                return;
                            else
                                yield();
                        else {
                            byte buffer[] = new byte[available];
                            int readcount = bs.read(buffer, 0, available);
                            if (readcount>0)
                                Log.print(new String(buffer));
                        }
                    } catch (java.io.IOException e) {
                        Log.println("ModelToJava: Error reading stream: " + e.toString());
                    }
                }
            }
        };
        t.start();
        return t;
    }
    
    /** Given a library, return its mounted directories in classpath notation. */
    private String getClassPathFromLibrary() {
        String s = "";
        for (int i=0; i<theLibrary.getMountedDirectories().size(); i++) {
            if (s.length()>0)
                s += ":";
            s += (String)(theLibrary.getMountedDirectories().get(i)) + java.io.File.separator;
        }
        return s;
    }
    
    /** Return a classpath cleaned of non-existent files. */
    public static String cleanClassPath(String s) {
        String outstr = "";
        java.util.StringTokenizer st = new java.util.StringTokenizer(s, ":");
        while (st.hasMoreElements()) {
            String element = (String)st.nextElement();
            java.io.File f = new java.io.File(element);
            if (f.exists())
                outstr += ((outstr.length()>0) ? ":" : "") + element;
        }
        return outstr;
    }
    
    /** Generate and compile Java source.  Use Library to obtain classpath.
     * Return the exitValue of the process used to invoke the compiler.  Results
     * might be compiler and/or environment dependent.  Return -1 if compiler
     * invocation failed. */
    public int compileJavaSource() {
        java.io.File sourcef = saveJavaSource();
        if (sourcef==null)
            return -1;
        String bootclasspath = cleanClassPath(java.lang.System.getProperty("sun.boot.class.path"));
        String sysclasspath = cleanClassPath(java.lang.System.getProperty("java.class.path"));
        String devclasspath = cleanClassPath(getClassPathFromLibrary());
        String command;
        switch (theLibrary.getCompiler()) {
            case Library.COMPILER_JAVAC:
                command = "javac -classpath " + sysclasspath + ":" + devclasspath + " " + sourcef.getAbsolutePath();
                break;
            case Library.COMPILER_JIKES:
                command = "jikes -classpath .:" + bootclasspath + ":" + sysclasspath + ":" + devclasspath + " " + 
                                sourcef.getAbsolutePath();
                break;
            default:
                Log.println("ModelToJava: Unknown compiler!");
                return -1;
        }
        Log.println("ModelToJava: Compile via '" + command + "'");
        try {
            Process p = Runtime.getRuntime().exec(command);
            endCapture = false;
            captureStream(p.getInputStream());
            captureStream(p.getErrorStream());
            try {
             	p.waitFor();
                endCapture = true;
                return p.exitValue();
            } catch (java.lang.InterruptedException x) {
                Log.println("ModelToJava: Waiting for process threw InterruptedException: " + x.toString());
            }
        } catch (java.io.IOException e) {
            Log.println("ModelToJava: Execution of compiler threw IOException: " + e.toString());
        }
        return -1;
    }
}
