package net.sourceforge.pmd.util.viewer.model;

/**
 * identiefie a listener of the ViewerModel
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: ViewerModelListener.java,v 1.8 2006/02/10 14:15:31 tomcopeland Exp $
 */
public interface ViewerModelListener {
    void viewerModelChanged(ViewerModelEvent e);
}
