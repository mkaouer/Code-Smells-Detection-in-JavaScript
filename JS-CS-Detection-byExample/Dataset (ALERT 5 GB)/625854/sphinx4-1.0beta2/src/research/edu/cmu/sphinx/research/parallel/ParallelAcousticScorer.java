/*
 * Copyright 1999-2002 Carnegie Mellon University.  
 * Portions Copyright 2002 Sun Microsystems, Inc.  
 * Portions Copyright 2002 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.research.parallel;

import edu.cmu.sphinx.decoder.scorer.AbstractScorer;
import edu.cmu.sphinx.decoder.scorer.Scoreable;
import edu.cmu.sphinx.decoder.search.Token;
import edu.cmu.sphinx.frontend.Data;
import edu.cmu.sphinx.frontend.FrontEnd;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

import java.util.Iterator;
import java.util.List;


/** A parallel acoustic scorer that is capable of scoring multiple feature streams. */
public class ParallelAcousticScorer extends AbstractScorer {


    @Override
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
    }


    /**
     * Scores the given set of Tokens. All Tokens in the given list are assumed to belong to the same acoustic model.
     *
     * @param scoreableList a list containing StateToken objects to be scored
     * @return the best scoring scorable, or null if there are no more frames to score
     */
    public Scoreable calculateScores(List<Token> scoreableList) {
        frontEnd = getFrontEnd(scoreableList);
        return super.calculateScores(scoreableList);
    }


    protected Scoreable doScoring(List<Token> scoreableList, Data data) {
        float logMaxScore = -Float.MAX_VALUE;
        Scoreable bestScoreable = null;

        for (Iterator i = scoreableList.iterator(); i.hasNext();) {
            Scoreable scoreable = (Scoreable) i.next();
            float logScore = scoreable.calculateScore(data);
            if (logScore > logMaxScore) {
                logMaxScore = logScore;
                bestScoreable = scoreable;
            }
        }
        return bestScoreable;
    }


    /**
     * Returns the acoustic model name of the Tokens in the given list .
     *
     * @return the acoustic model name of the Tokens
     */
    private FrontEnd getFrontEnd(List activeList) {
        if (activeList.size() > 0) {
            Iterator i = activeList.iterator();
            if (i.hasNext()) {
                ParallelToken token = (ParallelToken) i.next();
                return token.getFeatureStream().getFrontEnd();
            }
        }
        return null;
    }
}
