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

package edu.cmu.sphinx.trainer;

import edu.cmu.sphinx.util.props.*;

import java.io.IOException;
import java.util.*;


/**
 * Trains models given a set of audio files.
 * <p/>
 * At this point, a very simple file that helps us debug the code.
 */
public class Trainer implements Configurable {

    @S4Component(type = TrainManager.class)
    public static final String TRAIN_MANAGER = "trainManager";

    /** The SphinxProperty name for the initial trainer stage to be processed. */
    @S4String(defaultValue = "_00_INITIALIZATION")
    public final static String PROP_INITIAL_STAGE = "initialStage";

    /** The SphinxProperty name for the final trainer stage to be processed. */
    @S4String(defaultValue = "_40_TIED_CD_TRAIN")
    public final static String PROP_FINAL_STAGE = "finalStage";

    private String initialStage;
    private String finalStage;
    private boolean isStageActive = false;
    private List StageList = new LinkedList();
    private Set StageNames = new HashSet();

    private TrainManager trainManager;


    public void newProperties(PropertySheet ps) throws PropertyException {
        trainManager = (TrainManager) ps.getComponent(TRAIN_MANAGER);

        initialStage = ps.getString(PROP_INITIAL_STAGE);
        finalStage = ps.getString(PROP_FINAL_STAGE);

        addStage(Stage._00_INITIALIZATION);
        addStage(Stage._10_CI_TRAIN);
        addStage(Stage._20_UNTIED_CD_TRAIN);
        addStage(Stage._30_STATE_PRUNING);
        addStage(Stage._40_TIED_CD_TRAIN);
        addStage(Stage._90_CP_MODEL);
    }


    /**
     * Add Stage to a list of stages.
     *
     * @param stage the Stage to add
     */
    private void addStage(Stage stage) {
        StageList.add(stage);
        StageNames.add(stage.toString());
    }


    /**
     * Process this stage.
     *
     * @param context this trainer's context
     */
    private void processStages(String context) {
        if (!(StageNames.contains(initialStage) &&
                StageNames.contains(finalStage))) {
            return;
        }
        for (Iterator iterator = StageList.iterator();
             iterator.hasNext();) {
            Stage stage = (Stage) iterator.next();
            if (!isStageActive) {
                if (initialStage.equals(stage.toString())) {
                    isStageActive = true;
                }
            }
            if (isStageActive) {
                /*
             * Not sure of an elegant way to do it. For each
             * stage, it should call a different method.  Switch
             * would be a good solution, but it works with int,
             * and stage is of type Stage.
             *
             * run();
            */
                try {
                    if (stage.equals(Stage._00_INITIALIZATION)) {
                        System.out.println("00 - Initializing");
                        trainManager.initializeModels(context);
                        System.out.println("Saving");
                        trainManager.saveModels(context);
                    } else if (stage.equals(Stage._10_CI_TRAIN)) {
                        System.out.println("01 - CI train");
                        trainManager.trainContextIndependentModels(context);
                        System.out.println("Saving");
                        trainManager.saveModels(context);
                    } else if (stage.equals(Stage._20_UNTIED_CD_TRAIN)) {
                        System.out.println("02 - Untied CD train");
                    } else if (stage.equals(Stage._30_STATE_PRUNING)) {
                        System.out.println("03 - State pruning");
                    } else if (stage.equals(Stage._40_TIED_CD_TRAIN)) {
                        System.out.println("04 - Tied CD train");
                    } else if (stage.equals(Stage._90_CP_MODEL)) {
                        System.out.println("Copying");
                        trainManager.copyModels(context);
                    } else {
                        assert false : "stage not implemented";
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    throw new Error("IOE: Can't finish trainer " + ioe, ioe);
                }

                if (finalStage.equals(stage.toString())) {
                    isStageActive = false;
                }
            }
        }
    }


    /**
     * Main method of this Trainer.
     *
     * @param argv argv[0] : SphinxProperties file
     */
    public static void main(String[] argv) {

        if (argv.length < 1) {
            System.out.println
                    ("Usage: Trainer propertiesFile");
            System.exit(1);
        }

        String context = "trainer";
        String propertiesFile = argv[0];
        String pwd = System.getProperty("user.dir");

        //            SphinxProperties.initContext(context, new URL("file:///" + pwd + "/" + propertiesFile));

        Trainer trainer = ConfigurationManager.getInstance(Trainer.class);
        trainer.processStages(context);
    }
}
