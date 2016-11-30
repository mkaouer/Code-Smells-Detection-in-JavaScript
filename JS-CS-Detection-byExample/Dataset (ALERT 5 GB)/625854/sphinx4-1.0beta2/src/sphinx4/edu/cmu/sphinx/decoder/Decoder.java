/*
 * 
 * Copyright 1999-2004 Carnegie Mellon University.  
 * Portions Copyright 2004 Sun Microsystems, Inc.  
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 *
 */
package edu.cmu.sphinx.decoder;

import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4Integer;

/** The primary decoder class */
public class Decoder extends AbstractDecoder {

    /** The sphinx property name for the number of features to recognize at once. */
    @S4Integer(defaultValue = 100000)
    public final static String PROP_FEATURE_BLOCK_SIZE = "featureBlockSize";
    private int featureBlockSize;


    @Override
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);

        featureBlockSize = ps.getInt(PROP_FEATURE_BLOCK_SIZE);
    }


    /**
     * Decode frames until recognition is complete.
     *
     * @param referenceText the reference text (or null)
     * @return a result
     */
    public Result decode(String referenceText) {
        searchManager.startRecognition();
        Result result;
        do {
            result = searchManager.recognize(featureBlockSize);
            if (result != null) {
                result.setReferenceText(referenceText);
                fireResultListeners(result);
            }
        } while (result != null && !result.isFinal());
        searchManager.stopRecognition();
        return result;
    }
}
