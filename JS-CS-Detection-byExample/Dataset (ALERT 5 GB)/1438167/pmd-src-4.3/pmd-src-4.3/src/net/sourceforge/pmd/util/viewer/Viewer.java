package net.sourceforge.pmd.util.viewer;

import net.sourceforge.pmd.jaxen.MatchesFunction;
import net.sourceforge.pmd.jaxen.TypeOfFunction;
import net.sourceforge.pmd.util.viewer.gui.MainFrame;

/**
 * viewer's starter
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: Viewer.java 5449 2007-08-09 14:21:16Z allancaplan $
 */
public class Viewer {
    public static void main(String[] args) {
        MatchesFunction.registerSelfInSimpleContext();
        TypeOfFunction.registerSelfInSimpleContext();
        new MainFrame();
    }
}
