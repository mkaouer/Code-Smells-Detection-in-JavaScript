package net.sourceforge.pmd.util.viewer.util;

import java.util.ResourceBundle;

/**
 * helps with internationalization
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: NLS.java,v 1.6 2006/02/10 14:15:31 tomcopeland Exp $
 */
public class NLS {
    private final static ResourceBundle bundle;

    static {
        bundle = ResourceBundle.getBundle("net.sourceforge.pmd.util.viewer.resources.viewer_strings");
    }

    /**
     * translates the given key to the message
     *
     * @param key key to be translated
     * @return translated string
     */
    public static String nls(String key) {
        return bundle.getString(key);
    }
}

