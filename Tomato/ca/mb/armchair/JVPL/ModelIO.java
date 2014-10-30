/*
 * ModelIO.java
 *
 * Created on October 10, 2002, 1:50 AM
 */

package ca.mb.armchair.JVPL;

import java.io.*;
import ca.mb.armchair.Utilities.Widgets.ProgressPanel;

/**
 * All Model saving and loading is centralised here, in order
 * to simply changing file formats.
 *
 * @author  Dave Voorhis
 */
 
public class ModelIO {
    
    /** Static from here on. */
    private ModelIO() {
    }
    
    /** Return array of file extensions that are probably a Model.  The 0th item is the default. */
    public static String[] getModelFileExtensions() {
        return new String[] {"vpl", "xml", "shl"};
    }
    
    /** Return true if a given file is probably a Model.  Based on file extension. */
    public static boolean isModelFile(java.io.File f) {
        if (!f.isFile())
            return false;
        for (int i=0; i<getModelFileExtensions().length; i++)
            if (f.getName().toUpperCase().endsWith("." + getModelFileExtensions()[i].toUpperCase()))
                return true;
        return false;
    }
    
    /** Save Model contents to an ObjectOutputStream. */
    public static boolean saveModel(Model theModel, ObjectOutputStream s, ProgressPanel progress) {
        int VisualiserCount = theModel.getVisualiserCount();
        int ConnectionCount = theModel.getConnectionCount();

        progress.resetProgressBar(VisualiserCount + ConnectionCount);
        progress.setStatus(il8n._("Saving") + " " + VisualiserCount + " " + il8n._("visualisers."));
        try {
            try {
                s.writeObject(new ModelSerializable(theModel));
            } catch (Throwable t) {
                Log.println("ModelIO: Unable to write to output stream: " + t.toString());
                return false;
            }
            for (int i=0; i<VisualiserCount; i++) {
                try {
                    s.writeObject(new VisualiserSerializable(theModel.getVisualiser(i)));
                } catch (Throwable t) {
                    Log.println("ModelIO: Unable to write serialization of " + theModel.getVisualiser(i).toString());
                    Log.printStackTrace(t.getStackTrace());
                }
                progress.updateProgressBar();
            }
            progress.setStatus(il8n._("Saving") + " " + ConnectionCount + " " + il8n._("connections."));
            try {
                for (int i=0; i<theModel.getVisualiserCount(); i++) {
                    Visualiser v = theModel.getVisualiser(i);
                    for (int j=0; j<v.getConnectionCount(); j++) {        // iterate visualiser's references
                        try {
                            s.writeObject(new ConnectionSerializable(v.getConnection(j)));
                        } catch (Throwable t) {
                            Log.println("ModelIO: Unable to save " + v.getConnection(j).toString() + ": " + t.toString());
                            Log.printStackTrace(t.getStackTrace());
                        }
                        progress.updateProgressBar();
                    }
                }
            } catch (Throwable t) {
                Log.println("ModelIO: Unable to write to connection storage: " + t.toString());
                Log.printStackTrace(t.getStackTrace());
                return false;
            }
            return true;
        } finally {
            try {
                s.flush();
            } catch (java.io.IOException exp) {
                Log.println("ModelIO: Unable to flush connection storage: " + exp.toString());
            }
        }
    }

    /** Save Model contents to an XMLEncoder. */
    public static boolean saveModel(Model theModel, java.beans.XMLEncoder x, ProgressPanel progress) {
        int VisualiserCount = theModel.getVisualiserCount();
        int ConnectionCount = theModel.getConnectionCount();

        progress.resetProgressBar(VisualiserCount + ConnectionCount);
        progress.setStatus(il8n._("Saving") + " " + VisualiserCount + " " + il8n._("visualisers."));
        try {
            x.writeObject(new ModelSerializable(theModel));
            for (int i=0; i<VisualiserCount; i++) {
                try {
                    x.writeObject(new VisualiserSerializable(theModel.getVisualiser(i)));
                } catch (Throwable t) {
                    Log.println("Unable to write serialization of " + theModel.getVisualiser(i).toString());
                    Log.printStackTrace(t.getStackTrace());
                }
                progress.updateProgressBar();
            }
            progress.setStatus(il8n._("Saving") + " " + ConnectionCount + " " + il8n._("connections."));
            try {
                for (int i=0; i<VisualiserCount; i++) {
                    Visualiser v = theModel.getVisualiser(i);
                    for (int j=0; j<v.getConnectionCount(); j++) {        // iterate visualiser's references
                        try {
                            x.writeObject(new ConnectionSerializable(v.getConnection(j)));
                        } catch (Throwable t) {
                            Log.println("ModelIO: Unable to save " + v.getConnection(j).toString() + ": " + t.toString());
                            Log.printStackTrace(t.getStackTrace());
                        }
                        progress.updateProgressBar();
                    }
                }
            } catch (Throwable t) {
                Log.println("ModelIO: Unable to write to connection storage: " + t.toString());
                Log.printStackTrace(t.getStackTrace());
                return false;
            }
            return true;
        } finally {
            x.close();
        }
    }
    
    /** Save Model contents to a file.  If file name ends in .shl, the
     * file will be binary serialized.  Otherwise, it will be XML. */
    public static boolean saveModel(Model theModel, java.io.File f, ProgressPanel progress) {
        try {
            FileOutputStream out = new FileOutputStream(f);
            if (f.getName().toLowerCase().endsWith(".shl"))
                saveModel(theModel, new ObjectOutputStream(out), progress);
            else
                saveModel(theModel, new java.beans.XMLEncoder(out), progress);
            theModel.clearChanged();
            return true;
        } catch (Exception eee) {
            Log.println("ModelIO: Unable to write to " + f.getName() + ": " + eee.toString());
            Log.printStackTrace(eee.getStackTrace());
            return false;
        }
    }
    
    /** Load Model from an ObjectInputStream, using Library l to supply classpaths. */
    public static boolean loadModel(Library l, Model theModel, ObjectInputStream s, ProgressPanel progress) {
        ModelSerializable mS;
        try {
            mS = (ModelSerializable)s.readObject();
        } catch (Throwable t) {
            Log.println("ModelIO: Unable to read from object stream.");
            return false;
        }

        progress.resetProgressBar(mS.getVisualiserCount() + mS.getConnectionCount());
        progress.setStatus(il8n._("Loading") + mS.getVisualiserCount() + " " + il8n._("visualisers."));
        for (int i=0; i<mS.getVisualiserCount(); i++) {
            VisualiserSerializable vs;
            try {
                vs = (VisualiserSerializable)s.readObject();
            } catch (Throwable t) {
                vs = null;
                Log.println("Unable to read serialization of visualiser #" + i);
                Log.printStackTrace(t.getStackTrace());
            }
            if (vs!=null)
                theModel.addVisualiser(vs.deserializeVisualiser(l));
            progress.updateProgressBar();
        }
        progress.setStatus(il8n._("Loading") + " " + mS.getConnectionCount() + " " + il8n._("connections."));
        try {
            for (int i=0; i<mS.getConnectionCount(); i++) {
                ConnectionSerializable cs;
                try {
                    cs = (ConnectionSerializable)s.readObject();
                } catch (Throwable t) {
                    cs = null;
                    Log.println("ModelIO: Unable to load a connection from the stream: " + t.toString());
                    Log.printStackTrace(t.getStackTrace());
                }
                if (cs!=null)
                    new Connection(theModel, cs);
                progress.updateProgressBar();
            }
        } catch (Throwable t) {
            Log.println("ModelIO: Unable to read from connection storage: " + t.toString());
            Log.printStackTrace(t.getStackTrace());
            return false;
        }
        theModel.setModifierString(mS.getModifierString());
        theModel.refresh();
        theModel.redrawConnections();
        theModel.clearChanged();
        return true;
    }
    
    /** Load Model from an XMLDecoder, using Library l to supply classpaths. */
    public static boolean loadModel(Library l, Model theModel, java.beans.XMLDecoder x, ProgressPanel progress) {

        ModelSerializable mS;
        try {
            mS = (ModelSerializable)x.readObject();
        } catch (Throwable t) {
            Log.println("ModelIO: Unable to read from XML decoder.");
            return false;
        }
        
        progress.resetProgressBar(mS.getVisualiserCount() + mS.getConnectionCount());
        progress.setStatus(il8n._("Loading") + " " + mS.getVisualiserCount() + " " + il8n._("visualisers."));
        for (int i=0; i<mS.getVisualiserCount(); i++) {
            VisualiserSerializable vs;
            try {
                vs = (VisualiserSerializable)x.readObject();
            } catch (Throwable t) {
                vs = null;
                Log.println("Unable to read serialization of visualiser #" + i);
                Log.printStackTrace(t.getStackTrace());
            }
            if (vs!=null)
                theModel.addVisualiser(vs.deserializeVisualiser(l));
            progress.updateProgressBar();
        }
        progress.setStatus(il8n._("Loading") + " " + mS.getConnectionCount() + " " + il8n._("connections."));
        try {
            for (int i=0; i<mS.getConnectionCount(); i++) {
                ConnectionSerializable cs;
                try {
                    cs = (ConnectionSerializable)x.readObject();
                } catch (Throwable t) {
                    cs = null;
                    Log.println("ModelIO: Unable to load a connection from the stream: " + t.toString());
                    Log.printStackTrace(t.getStackTrace());
                }
                if (cs!=null)
                    new Connection(theModel, cs);
                progress.updateProgressBar();
            }
        } catch (Throwable t) {
            Log.println("ModelIO: Unable to read from connection storage: " + t.toString());
            Log.printStackTrace(t.getStackTrace());
            return false;
        }
        theModel.setModifierString(mS.getModifierString());
        theModel.refresh();
        theModel.redrawConnections();
        theModel.clearChanged();
        return true;
    }
    
    /** Load Model contents from a file.  If the file name ends in .shl,
     * it is assumed to be a binary serialization.  Otherwise, XML.  Library l provides classpaths. */
    public static boolean loadModel(Library l, Model theModel, java.io.File f, ProgressPanel progress) {
        try {
            FileInputStream in = new FileInputStream(f);
            try {
                if (f.getName().toLowerCase().endsWith(".shl"))
                    loadModel(l, theModel, new ObjectInputStream(in), progress);
                else
                    loadModel(l, theModel, new java.beans.XMLDecoder(in), progress);
            } catch(Exception e) {
                Log.println("ModelIO: Unable to load Model " + e.toString());
                Log.printStackTrace(e.getStackTrace());
            }
            return true;
        } catch (Exception e) {
            Log.println("ModelIO: Unable to read from " + f.getName() + ": " + e.toString());
            return false;
        }
    }
}
