package edu.cmu.sphinx.decoder.scorer;

import edu.cmu.sphinx.decoder.search.Token;
import edu.cmu.sphinx.frontend.*;
import edu.cmu.sphinx.frontend.endpoint.SpeechEndSignal;
import edu.cmu.sphinx.frontend.endpoint.SpeechStartSignal;
import edu.cmu.sphinx.frontend.util.DataUtil;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4Boolean;
import edu.cmu.sphinx.util.props.S4Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Implements some basic scorer functionality but keeps specific scoring open for sub-classes.
 *
 * @author Holger Brandl
 */
public abstract class AbstractScorer implements AcousticScorer {

    /** Property the defines the frontend to retrieve features from for scoring */
    @S4Component(type = BaseDataProcessor.class)
    public final static String FEATURE_FRONTEND = "frontend";
    protected BaseDataProcessor frontEnd;

    /**
     * An opotional post-processor for computed scores that will normalize scores. If not set, no normalization will
     * applied and the token scores will be returned unchanged.
     */
    @S4Component(type = ScoreNormalizer.class, mandatory = false)
    public final static String SCORE_NORMALIZER = "scoreNormalizer";
    private ScoreNormalizer scoreNormalizer;

    private Boolean isVadEmbeddedStream;

    protected Logger logger;
    protected String name;


    public void newProperties(PropertySheet ps) throws PropertyException {
        frontEnd = (BaseDataProcessor) ps.getComponent(FEATURE_FRONTEND);
        scoreNormalizer = (ScoreNormalizer) ps.getComponent(SCORE_NORMALIZER);

        logger = ps.getLogger();
        name = ps.getInstanceName();
    }


    /**
     * Scores the given set of states.
     *
     * @param scoreableList A list containing scoreable objects to be scored
     * @return The best scoring scoreable, or <code>null</code> if there are no more features to score
     */
    public Scoreable calculateScores(List<Token> scoreableList) {
        if (scoreableList.size() <= 0) {
            return null;
        }

        try {
            Data data = getNextData();
            while (data instanceof Signal) {
                if (data instanceof SpeechEndSignal)
                    return null;

                data = getNextData();
            }

            if (data == null)
                return null;

            // convert the data to FloatData if not yet done
            if (data instanceof DoubleData)
                data = DataUtil.DoubleData2FloatData((DoubleData) data);

            Scoreable bestToken = doScoring(scoreableList, data);

            // apply optional score normalization
            if (scoreNormalizer != null)
                bestToken = scoreNormalizer.normalize(scoreableList, bestToken);

            return bestToken;
        } catch (DataProcessingException dpe) {
            dpe.printStackTrace();
            return null;
        }
    }


    private Data getNextData() {
        Data data = frontEnd.getData();

        // reconfigure the scorer for the coming data stream
        if (data instanceof DataStartSignal) {
            Map<String, Object> dataProps = ((DataStartSignal) data).getProps();
            if (dataProps.containsKey(DataStartSignal.VAD_TAGGED_FEAT_STREAM))
                isVadEmbeddedStream = (Boolean) dataProps.get(DataStartSignal.VAD_TAGGED_FEAT_STREAM);
            else
                isVadEmbeddedStream = false;
        }

        return data;
    }


    public void startRecognition() {
        if (isVadEmbeddedStream == null) {
            Data firstData = getNextData();
            assert firstData instanceof DataStartSignal;
        }

        if (!isVadEmbeddedStream)
            return;

        Data data = getNextData();
        while (!((data) instanceof SpeechStartSignal)) {
            if (data == null) {
                break;
            }

            data = getNextData();
        }

        if (data == null)
            logger.warning("Not enough data in frontend to start recognition");
    }


    public void stopRecognition() {
    }


    /**
     * Scores a a list of <code>Token</code>s given a <code>Data</code>-object.
     *
     * @param scoreableList The list of Tokens to be scored
     * @param data          The <code>Data</code>-object to be used for scoring.
     * @return the best scoring <code>Token</code> or <code>null</code> if the list of tokens was empty.
     */
    protected abstract Scoreable doScoring(List<Token> scoreableList, Data data);


    public void allocate() {
    }


    public void deallocate() {
    }


    @Override
    public String toString() {
        return name;
    }
}
