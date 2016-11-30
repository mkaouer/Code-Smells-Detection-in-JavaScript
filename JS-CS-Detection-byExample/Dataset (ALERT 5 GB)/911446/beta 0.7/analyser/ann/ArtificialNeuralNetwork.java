/* Merchant of Venice - technical analysis software for the stock market.
 Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mov.analyser.ann;

/* In this class we manage all the Joone relations.
 * Here you can find all the import of the Joone classes,
 * the Joone engine package can be downloaded from:
 * http://www.jooneworld.com/
 */
import java.io.*;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;
import org.mov.util.Locale;

/* Joone import */
import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.io.MemoryInputSynapse;
import org.joone.net.*;
/* End Joone import */

/* XStream import */
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
/* End XStream import */

/**
 * @author Alberto Nacher
 */
public class ArtificialNeuralNetwork implements NeuralNetListener {
    
    private JDesktopPane desktop;
    private ProgressDialog progress;
    
    /* Neural network and objects related to it*/
    private NeuralNet nnet;
    /*
     * If we close the ANN window, we need to know if the ANN has been saved or not.
     * If ANN is not saved, we ask for saving, because we don't save the ANN in the preferences
     * due to the excessive load that that could carry.
     * All that work is done by saved boolean variable.
     */
    private boolean saved;
    
    /* Constants */
    /* index for buy and sell signals of output array*/
    public static final int OUTPUT_BUY = 0;
    public static final int OUTPUT_SELL = 1;
    /* Length of output array of neural network*/
    public static final int OUTPUT_NEURONS = 2;
    
    /*
     * The firing of buy/sell signals
     * HIGH_BOOL = fire
     * LOW_BOOL = do not fire
     */
    public static final double HIGH_BOOL = 1.0D;
    public static final double LOW_BOOL = 0.0D;
    
    /*
     * Thresholds for outputs, these are defaut values,
     * but the user can modify these values through MoV GUI
     */
    private double buyThreshold = 0.5D;
    private double sellThreshold = 0.5D;


    /**
     * Create a new instance of ArtificialNeuralNetwork class.
     *
     * @param desktop the desktop
     */
    public ArtificialNeuralNetwork(JDesktopPane desktop) {
        
        this.desktop = desktop;
        
        this.nnet = new NeuralNet();

        // At the begin we don't care about ANN saved or not,
        // set it true, so that there are no asking question to the user.
        this.saved = true;
        
    }
    
    /**
     * Run the current nnet neural network.
     *
     * @param	inputDoubles    an array of input values of the artificial neural network
     * @return	an array with output values,
     * returnValue[OUTPUT_BUY] is the buy signal returned,
     * returnValue[OUTPUT_SELL] is the sell signal returned.
     */
    public boolean[] run(double[] inputDoubles) {
        
        boolean[] ANNOutput = new boolean[this.OUTPUT_NEURONS];

        if (nnet != null) {
        
            /* We get the first layer of the net (the input layer),
            then remove all the input synapses attached to it
            and attach a DirectSynapse */
            Layer input = nnet.getInputLayer();
            input.removeAllInputs();
            DirectSynapse memInpDS = new DirectSynapse();
            input.addInputSynapse(memInpDS);

            /* We get the last layer of the net (the output layer),
            then remove all the output synapses attached to it
            and attach a DirectSynapse */
            Layer output = nnet.getOutputLayer();
            output.removeAllOutputs();
            DirectSynapse memOutDS = new DirectSynapse();
            output.addOutputSynapse(memOutDS);

            // Run the neural network
            nnet.start();

            // Prepare the next input pattern
            Pattern iPattern = new Pattern(inputDoubles);
            iPattern.setCount(1);
            // Interrogate the net
            memInpDS.fwdPut(iPattern);
            // Read the output pattern and print out it
            Pattern pattern = memOutDS.fwdGet();
            
            // set the output and put it in a temporary variable,
            // should just be one output i.e. one array as we have setup this up before
            double[] theNetOutput = pattern.getArray();
            
            // stop the ANN
            Pattern stop = new Pattern(new double[inputDoubles.length]);
            stop.setCount(-1);
            memInpDS.fwdPut(stop);
            memOutDS.fwdGet();

            // Get the return values
            ANNOutput[this.OUTPUT_BUY] = theNetOutput[this.OUTPUT_BUY]>this.buyThreshold;
            ANNOutput[this.OUTPUT_SELL] = theNetOutput[this.OUTPUT_SELL]>this.sellThreshold;
            
        }
        
        // return the array of booleans according to thresholds
        return ANNOutput;
    }

    /**
     * Trains the current nnet neural network.
     *
     * @param	inputArray    an array of input values of the artificial neural network
     * @param	outputDesiredArray    an array of desired output values
     * of the artificial neural network
     */
    public void runTraining(double[][] inputArray, double[][] outputDesiredArray,
            //boolean newLearning,
            //int batchSize,
            //int learningMode,
            double newLearningRate,
            double newMomentum,
            int newPreLearning,
            //boolean supervisioned,
            int newTotCicles,
            int newTrainingPatterns) {

        if (nnet != null) {
        
            /* We get the first layer of the net (the input layer),
            then remove all the input synapses attached to it
            and attach a MemoryInputSynapse */
            Layer input = nnet.getInputLayer();
            input.removeAllInputs();
            MemoryInputSynapse memInp = new MemoryInputSynapse();
            memInp.setInputArray(inputArray);
            memInp.setFirstRow(0);
            memInp.setLastRow(inputArray.length);
            memInp.setAdvancedColumnSelector("1-" + input.getRows());
            input.addInputSynapse(memInp);
            
            /* training values */
            Layer output = nnet.getOutputLayer();
            output.removeAllOutputs();
            MemoryInputSynapse memOut = new MemoryInputSynapse();
            memOut.setFirstRow(0);
            memOut.setLastRow(outputDesiredArray.length);
            memOut.setAdvancedColumnSelector("1-" + this.OUTPUT_NEURONS);
            memOut.setInputArray(outputDesiredArray);
            
            /* Set the monitor and teacher for the ANN training */
            Monitor monitor = new Monitor();
            TeachingSynapse trainer = new TeachingSynapse();
    
            /* Set monitor values */
            this.setMonitor(monitor, true, newLearningRate, newMomentum,
                    newPreLearning, newTotCicles, newTrainingPatterns);
            
            /* Set values for the teacher */
            trainer.setDesired(memOut);
            
            /* Attach teacher to the monitor */
            trainer.setMonitor(monitor);
            output.addOutputSynapse(trainer);
            
            /* Attach monitor and teacher to the ANN */
            nnet.setTeacher(trainer);
            nnet.setMonitor(monitor);
            monitor.addNeuralNetListener(this);
        
            // run the ANN
            nnet.start();
            nnet.getMonitor().Go();
            
            this.saved = false;
        }
    }

    /**
     * Set the progress bar, so that we can manage it when cycle terminated event is raised.
     *
     * @param	progress    the progress bar shown to the user during training.
     */
    public void setProgressBar(ProgressDialog progress) {
        this.progress = progress;
    }

    /**
     * Set the ANN to null.
     */
    public void setANNNull() {
        this.nnet = null;
    }
    
    /**
     * Return true if nnet neural network is null or
     * if it has no valid input/output layers.
     *
     * @return	true as described above, otherwise false.
     */
    public boolean isANNNull() {
        boolean retValue = false;
        retValue = (this.nnet == null);
        if (!retValue) {
            Layer input = this.nnet.getInputLayer();
            retValue = (input == null);
        }
        if (!retValue) {
            Layer output = this.nnet.getOutputLayer();
            retValue = (output == null);
        }            
        return retValue;
    }
    
    /**
     * Set the buy threshold.
     * The buy output signal fires only if the value is higher than this threshold .
     *
     * @param	buyThreshold    the buy threshold.
     */
    public void setBuyThreshold(double buyThreshold) {
        
        this.buyThreshold = buyThreshold;
    }
    
    /**
     * Set the sell threshold.
     * The sell output signal fires only if the value is higher than this threshold .
     *
     * @param	sellThreshold    the sell threshold.
     */
    public void setSellThreshold(double sellThreshold) {
        
        this.sellThreshold = sellThreshold;
    }
    
    /**
     * Get the network status (saved/not saved).
     *
     * @return	network status (true = is saved, false = is not saved)
     */
    public boolean isSaved() {
        
        return this.saved;
    }
    
    /**
     * Load the neural network from a snet/xml file into the nnet object.
     * the file must contain a serialized version of a neural network.
     *
     * @param	fileName    the file name where nnet will be saved
     */
    public void loadNeuralNet(String fileName)
        throws FileExtensionException, FileNotFoundException, IOException,
            NullPointerException, SecurityException {
        
        // Check Exceptions:
        // Wrong extension
        int len = fileName.length();
        if (!((fileName.substring(len-ANNConstants.SNET.length(),len).equals(ANNConstants.SNET)) ||
         (fileName.substring(len-ANNConstants.XML.length(),len).equals(ANNConstants.XML)))) {
            /* throw exception due to wrong extension file */
            throw new FileExtensionException();
        }
        
        // if the fileName is null, it throws the NullPointerException
        if (fileName == null) {
            throw new NullPointerException();
        }
        
        // if the file is not found, it throws the FileNotFoundException
        FileInputStream stream = new FileInputStream(fileName);
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
        
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	// if the rights are not enough for reading it, it throws the SecurityException
            security.checkRead(fileName);
	}
        
        try {
            if (fileName.substring(len-ANNConstants.SNET.length(),len).equals(ANNConstants.SNET)) {
                /* import .snet file */
                NeuralNetLoader loader = new NeuralNetLoader(fileName); 
                this.nnet = loader.getNeuralNet();
            } else if (
                    fileName.substring(len-ANNConstants.XML.length(),len).equals(ANNConstants.XML)) {
                /* import .xml file */
                byte[] buf = new byte[(int)randomAccessFile.length()];
                randomAccessFile.read(buf);
                String xml = new String(buf);
                // Get the neural network
                XStream xstream = new XStream(new DomDriver());
                this.nnet = (NeuralNet)xstream.fromXML(xml);
            } else {
                // do nothing
            }
        } catch (Exception ex) {
            throw new IOException();
        } finally {
            stream.close();
            randomAccessFile.close();
        }
        // After sucessful loading, we update the saved boolean variable,
        // because we have a saved ANN equal to the ANN in memory.
        this.saved = true;
    }

    /**
     * Save the nnet neural network in a snet/xml file.
     * nnet serialization takes place.
     *
     * @param	fileName    the file name where nnet will be saved
     */
    public void saveNeuralNet(String fileName)
        throws FileExtensionException, FileNotFoundException, IOException,
            NullPointerException, SecurityException {

        // Check Exceptions:
        // Wrong extension
        int len = fileName.length();
        if (!((fileName.substring(len-ANNConstants.SNET.length(),len).equals(ANNConstants.SNET)) ||
         (fileName.substring(len-ANNConstants.XML.length(),len).equals(ANNConstants.XML)))) {
            /* throw exception due to wrong extension file */
            throw new FileExtensionException();
        }

        // if the fileName is null, it throws the NullPointerException
        if (fileName == null) {
            throw new NullPointerException();
        }
        
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	// if the rights are not enough for reading it, it throws the SecurityException
            security.checkRead(fileName);
	}
        
        // if the file is not found, it throws the FileNotFoundException
        FileOutputStream stream = new FileOutputStream(fileName);
        
        try {
            // remove all useless things before saving the neural network
            this.nnet.removeAllInputs();
            this.nnet.removeAllOutputs();
            this.nnet.removeAllListeners();
            
            // save in different ways according to file extension
            if (fileName.substring(len-ANNConstants.SNET.length(),len).equals(ANNConstants.SNET)) {
                /* export .snet file */
                ObjectOutputStream out = new ObjectOutputStream(stream);
                out.writeObject(this.nnet);
            } else if (
                    fileName.substring(len-ANNConstants.XML.length(),len).equals(ANNConstants.XML)) {
                /* export .xml file */
                XStream xstream = new XStream(new DomDriver());
                String xml = xstream.toXML(this.nnet);
                xml = "<?xml version=\"1.0\"?>\n" + xml;
                stream.write(xml.getBytes());
            } else {
                // do nothing
            }
        } catch (Exception ex) {
            throw new IOException();
        } finally {
            stream.close();
        }

        // After sucessful saving, we update the saved boolean variable
        this.saved = true;
    }

    /**
     * Set a default artificial neural network with the following characteristics:
     * - input layer defined as LinearLayer (see Joone documentation for details)
     * - hidden layer defined as SigmoidLayer (see Joone documentation for details)
     * - output layer defined as SigmoidLayer (see Joone documentation for details).
     * The input layer has a number of neurons equal to the input parameter inputRows,
     * which is the number of input defined in the ANN Page user interface.
     * The hidden layer has a number of neurons equal to the max between
     * three times the input and five times the output. The reason for that is having
     * a enough balanced ANN.
     * The output has a number of neurons equal to two.
     * One output is a signal to buy, the other is a signal to sell.
     *
     * @param	inputRows       the input of the neural network
     */
    public void setDefaultANN(int inputRows) {
        
        // layers
        LinearLayer input = new LinearLayer();
        SigmoidLayer hidden = new SigmoidLayer();
        SigmoidLayer output = new SigmoidLayer();
        
        // number of rows for each level
        // inputRows is equal to the input specified in the ANN Page
        // default input layer has neurons equal to inputRows
        input.setRows(inputRows);
        // default hidden layer has the maximum between:
        // 3 times the neurons of the input layer
        // and 5 times the neurons of the output layer
        hidden.setRows(Math.max(3 * inputRows, 5 * this.OUTPUT_NEURONS));
        // default output layer has an output for buy, one for sell
        // and one for the capital
        output.setRows(this.OUTPUT_NEURONS);
        
        // synapses for connections among layers
        FullSynapse synapse_IH = new FullSynapse(); /* Input -> Hidden conn. */
        FullSynapse synapse_HO = new FullSynapse(); /* Hidden -> Output conn. */
        input.addOutputSynapse(synapse_IH);
        hidden.addInputSynapse(synapse_IH);
        hidden.addOutputSynapse(synapse_HO);
        output.addInputSynapse(synapse_HO);
        
        // Build ANN Object (NeuralNet)
        nnet = new NeuralNet();
        nnet.addLayer(input, NeuralNet.INPUT_LAYER);
        nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
        nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
        
        this.saved = false;
    }

    /**
     * Recalculate the inputs so that they fit with input expressions.
     *
     * @param	inputRows       the inputs of the neural network to be set
     */
    public void setANNInput(int inputRows) {

        // Detach loaded network input and attach the new ones
        Layer input = nnet.getInputLayer();
        if (input.getRows() != inputRows) {
            input.setRows(inputRows);
            this.saved = false;
        }
    }
    
    /**
     * Recalculate the outputs so that they fit with number of buy/sell signals.
     * Now we have only 2 output signal (one for buy signal and another for sell signal).
     */
    public void setANNOutput() {

        // Detach loaded network input and attach the new ones
        Layer output = nnet.getOutputLayer();
        if (output.getRows() != this.OUTPUT_NEURONS) {
            output.setRows(this.OUTPUT_NEURONS);
            this.saved = false;
        }
    }

    /**
     * Check if the actual neural network has the same of input neurons as given.
     *
     * @param inputRows number of input rows of ANN to be checked
     *
     * @return if the actual number of input neurons is equal to inputRows, it returns true.
     */
    public boolean isInputOK(int inputRows) {
        boolean retValue = false;
        if (nnet != null) {
            Layer input = nnet.getInputLayer();
            retValue = (input.getRows() == inputRows);
        }
        return retValue;
    }

    /**
     * Check if the actual neural network has a number of output neurons as desired.
     *
     * @return if the actual number of output neurons is equal to what desired, it returns true.
     */
    public boolean isOutputOK() {
        boolean retValue = false;
        if (nnet != null) {
            Layer output = nnet.getOutputLayer();
            retValue = (output.getRows() == (this.OUTPUT_NEURONS));
        }
        return retValue;
    }
        
    /**
     * Set the monitor parameters.
     */
    private void setMonitor(
            Monitor monitor,
            boolean newLearning,
            //int batchSize,
            //int learningMode,
            double newLearningRate,
            double newMomentum,
            int newPreLearning,
            //boolean supervisioned,
            int newTotCicles,
            int newTrainingPatterns
            //boolean aModeUseRMSE,
            //boolean validation,
            //int newValidationPatterns
            ) {
        
        /* Only if Batch BackProp algorithm is used, otherwise set to zero */
        //monitor.setBatchSize(batchSize);
        /* Learning mode (0=On-line, 1=Batch, 2=RPROP, ...) */
        //monitor.setLearningMode(learningMode);
        /* 0.7 in the Joone example */
        monitor.setLearningRate(newLearningRate);
        /* 0.5 in the Joone example */
        monitor.setMomentum(newMomentum);
        /* Sets the initial ignored input patterns (during the training phase) */
        monitor.setPreLearning(newPreLearning);
        /* supervised/unsupervised network
        * true->supervised; false->unsupervised */
        //monitor.setSupervisioned(supervisioned); 
        /* How many times the net must be trained on the input patterns */
        monitor.setTotCicles(newTotCicles);
        /* # Of rows contained in the input */
        monitor.setTrainingPatterns(newTrainingPatterns);
        /* It is the way in which TechingSynapse give the output
        * false->MSE, mean square error; true->RMSE, square MSE */
        //monitor.setUseRMSE(aModeUseRMSE);
        /* Set the validation process */
        //monitor.setValidation(validation);
        /* # Of rows contained in the input used for validation */
        //monitor.setValidationPatterns(newValidationPatterns);
        /* The net must be trained, if learning is equal to true */
        monitor.setLearning(newLearning);
        
    }
    
    /*
     * The following methods are all methods implementing
     * the NeuralNetListener interface.
     * The events will be raised according to what happens when ANN is training
     */
    public void cicleTerminated(NeuralNetEvent e) {
        // Every cycle, we increment by one the progress bar
        if (progress!=null) {
            progress.setNote(Locale.getString("TRAINING"));
            progress.increment();
        }
    }
            
    public void errorChanged(NeuralNetEvent e) {
    }
            
    public void netStarted(NeuralNetEvent e) {
    }
            
    public void netStopped(NeuralNetEvent e) {
        // Close the progress bar
        progress.hide();
        // Stop the ANN
        nnet.stop();
        // Detach Monitor
        nnet.getMonitor().removeNeuralNetListener(this);
        // Artificial neural network stopped correctly message
        JOptionPane.showInternalMessageDialog(desktop,
            Locale.getString("ANN_STOPPED_OK",
                Double.toString(nnet.getMonitor().getGlobalError())),
            Locale.getString("ANN_STOPPED_OK_TITLE"),
            JOptionPane.INFORMATION_MESSAGE);
    }
            
    public void netStoppedError(NeuralNetEvent e, java.lang.String error) {
        // Close the progress bar
        progress.hide();
        // Stop the ANN
        nnet.stop();
        // Detach Monitor
        nnet.getMonitor().removeNeuralNetListener(this);
        // Artificial neural network stopped abnormally message
        JOptionPane.showInternalMessageDialog(desktop,
            Locale.getString("ANN_STOPPED_ERROR",
                Double.toString(nnet.getMonitor().getGlobalError())),
            Locale.getString("ANN_STOPPED_ERROR_TITLE"),
            JOptionPane.ERROR_MESSAGE);
    }

}
