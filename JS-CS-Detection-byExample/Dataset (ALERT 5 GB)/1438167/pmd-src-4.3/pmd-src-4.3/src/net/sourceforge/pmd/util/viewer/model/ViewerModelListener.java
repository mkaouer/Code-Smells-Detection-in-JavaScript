package net.sourceforge.pmd.util.viewer.model;

/**
 * identiefie a listener of the ViewerModel
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: ViewerModelListener.java 4217 2006-02-10 14:15:31Z tomcopeland $
 */
public interface ViewerModelListener {
    void viewerModelChanged(ViewerModelEvent e);
}
