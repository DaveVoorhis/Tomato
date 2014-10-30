/*
 * AbstractTreeModel.java
 *
 * Created on October 29, 2002, 9:06 PM
 */

package ca.mb.armchair.Utilities.Widgets.FileTree;

import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;

/**
 * This class takes care of the event listener lists required by TreeModel.
 * It also adds "fire" methods that call the methods in TreeModelListener.
 * Look in TreeModelSupport for all of the pertinent code.
 *
 * @author  http://java.sun.com/products/jfc/tsc/articles/jtree/
 * @author  Dave Voorhis
 */
import javax.swing.tree.*;

public abstract class AbstractTreeModel extends TreeModelSupport implements TreeModel {
}


