package net.sourceforge.pmd.util.viewer;

import net.sourceforge.pmd.jaxen.MatchesFunction;
import net.sourceforge.pmd.util.viewer.gui.MainFrame;

/**
 * viewer's starter
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: Viewer.java,v 1.10 2006/02/10 14:26:31 tomcopeland Exp $
 */
public class Viewer {
    public static void main(String[] args) {
        MatchesFunction.registerSelfInSimpleContext();
        new MainFrame();
    }
}
