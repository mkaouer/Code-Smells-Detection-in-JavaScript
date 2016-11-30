package ij.measure;
import ij.*;
import ij.gui.*;

/** Curve fitting class based on the Simplex method described
 *  in the article "Fitting Curves to Data" in the May 1984
 *  issue of Byte magazine, pages 340-362.
 *
 *	2001/02/14: Midified to handle a gamma variate curve.
 *  Uses altered Simplex method based on method in "Numerical Recipes in C".
 *  This method tends to converge closer in less iterations.
 *  Has the option to restart the simplex at the initial best solution in
 *  case it is "stuck" in a local minimum (by default, restarted once).  Also includes
 *  settings dialog option for user control over simplex parameters and functions to
 *  evaluate the goodness-of-fit.  The results can be easily reported with the
 *  getResultString() method.
 *
 * @author             Kieran Holland (email: holki659@student.otago.ac.nz)
 * @version            1.0
 *
 */
public class CurveFitter {    
    public static final int STRAIGHT_LINE=0,POLY2=1,POLY3=2,POLY4=3,
    EXPONENTIAL=4,POWER=5,LOG=6,RODBARD=7,GAMMA_VARIATE=8;
    
    public static final int IterFactor = 500;
    
    public static final String[] fitList = {"Straight Line","2nd Degree Polynomial",
    "3rd Degree Polynomial", "4th Degree Polynomial","Exponential","Power",
    "log","Rodbard", "Gamma Variate"};
    
    public static final String[] fList = {"y = a+bx","y = a+bx+cx^2",
    "y = a+bx+cx^2+dx^3", "y = a+bx+cx^2+dx^3+ex^4","y = a*exp(bx)","y = ax^b",
    "y = a*ln(bx)","y = c*((a-x)/(x-d))^(1/b)", "y = a*(x-b)^c*exp(-(x-b)/d)"};
           
    private static final double alpha = -1.0;	  // reflection coefficient
    private static final double beta = 0.5;	  // contraction coefficient
    private static final double gamma = 2.0;	  // expansion coefficient
    private static final double root2 = 1.414214; // square root of 2
    
    private int fit;                // Number of curve type to fit
    private double[] xData, yData;  // x,y data to fit
    private int numPoints;          // number of data points
    private int numParams;          // number of parametres
    private int numVertices;        // numParams+1 (includes sumLocalResiduaalsSqrd)
    private int worst;			// worst current parametre estimates
    private int nextWorst;		// 2nd worst current parametre estimates
    private int best;			// best current parametre estimates
    private double[][] simp; 		// the simplex (the last element of the array at each vertice is the sum of the square of the residuals)
    private double[] next;		// new vertex to be tested
    private int numIter;		// number of iterations so far
    private int maxIter; 	// maximum number of iterations per restart
    private int restarts; 	// number of times to restart simplex after first soln.
    private double maxError;     // maximum error tolerance
    
    /** Construct a new CurveFitter. */
    public CurveFitter (double[] xData, double[] yData) {
        this.xData = xData;
        this.yData = yData;
        numPoints = xData.length;
    }
    
    /**  Perform curve fitting with the simplex method
     *          doFit(fitType) just does the fit
     *          doFit(fitType, true) pops up a dialog allowing control over simplex parameters
     *  	alpha is reflection coefficient  (-1)
     *  	beta is contraction coefficient (0.5)
     *  	gamma is expansion coefficient (2)
     */
    public void doFit(int fitType) {
        doFit(fitType, false);
    }
    
    public void doFit(int fitType, boolean showSettings) {
        if (fitType < STRAIGHT_LINE || fitType > GAMMA_VARIATE)
            throw new IllegalArgumentException("Invalid fit type");
        fit = fitType;
        initialize();
        if (showSettings) settingsDialog();
        restart(0);
        
        numIter = 0;
        boolean done = false;
        double[] center = new double[numParams];  // mean of simplex vertices
        while (!done) {
            numIter++;
            for (int i = 0; i < numParams; i++) center[i] = 0.0;
            // get mean "center" of vertices, excluding worst
            for (int i = 0; i < numVertices; i++)
                if (i != worst)
                    for (int j = 0; j < numParams; j++)
                        center[j] += simp[i][j];
            // Reflect worst vertex through centre
            for (int i = 0; i < numParams; i++) {
                center[i] /= numParams;
                next[i] = center[i] + alpha*(simp[worst][i] - center[i]);
            }
            sumResiduals(next);
            // if it's better than the best...
            if (next[numParams] <= simp[best][numParams]) {
                newVertex();
                // try expanding it
                for (int i = 0; i < numParams; i++)
                    next[i] = center[i] + gamma * (simp[worst][i] - center[i]);
                sumResiduals(next);
                // if this is even better, keep it
                if (next[numParams] <= simp[worst][numParams])
                    newVertex();
            }
            // else if better than the 2nd worst keep it...
            else if (next[numParams] <= simp[nextWorst][numParams]) {
                newVertex();
            }
            // else try to make positive contraction of the worst
            else {
                for (int i = 0; i < numParams; i++)
                    next[i] = center[i] + beta*(simp[worst][i] - center[i]);
                sumResiduals(next);
                // if this is better than the second worst, keep it.
                if (next[numParams] <= simp[nextWorst][numParams]) {
                    newVertex();
                }
                // if all else fails, contract simplex in on best
                else {
                    for (int i = 0; i < numVertices; i++) {
                        if (i != best) {
                            for (int j = 0; j < numVertices; j++)
                                simp[i][j] = beta*(simp[i][j]+simp[best][j]);
                            sumResiduals(simp[i]);
                        }
                    }
                }
            }
            order();
            
            double rtol = 2 * Math.abs(simp[best][numParams] - simp[worst][numParams]) /
            (Math.abs(simp[best][numParams]) + Math.abs(simp[worst][numParams]) + 0.0000000001);
            
            if (numIter >= maxIter) done = true;
            else if (rtol < maxError) {
                //System.out.print(getResultString());
                restarts--;
                if (restarts < 0) {
                    done = true;
                }
                else {
                    restart(best);
                }
            }
        }
    }
        
    /** Initialise the simplex
     */
    void initialize() {
        // Calculate some things that might be useful for predicting parametres
        numParams = getNumParams();
        numVertices = numParams + 1;      // need 1 more vertice than parametres,
        simp = new double[numVertices][numVertices];
        next = new double[numVertices];
        
        double firstx = xData[0];
        double firsty = yData[0];
        double lastx = xData[numPoints-1];
        double lasty = yData[numPoints-1];
        double xmean = (firstx+lastx)/2.0;
        double ymean = (firsty+lasty)/2.0;
        double slope;
        if ((lastx - firstx) != 0.0)
            slope = (lasty - firsty)/(lastx - firstx);
        else
            slope = 1.0;
        double yintercept = firsty - slope * firstx;
        maxIter = IterFactor * numParams * numParams;  // Where does this estimate come from?
        restarts = 1;
        maxError = 1e-9;
        switch (fit) {
            case STRAIGHT_LINE:
                simp[0][0] = yintercept;
                simp[0][1] = slope;
                break;
            case POLY2:
                simp[0][0] = yintercept;
                simp[0][1] = slope;
                simp[0][2] = 0.0;
                break;
            case POLY3:
                simp[0][0] = yintercept;
                simp[0][1] = slope;
                simp[0][2] = 0.0;
                simp[0][3] = 0.0;
                break;
            case POLY4:
                simp[0][0] = yintercept;
                simp[0][1] = slope;
                simp[0][2] = 0.0;
                simp[0][3] = 0.0;
                simp[0][4] = 0.0;
                break;
            case EXPONENTIAL:
                simp[0][0] = 0.1;
                simp[0][1] = 0.01;
                break;
            case POWER:
                simp[0][0] = 0.0;
                simp[0][1] = 1.0;
                break;
            case LOG:
                simp[0][0] = 0.5;
                simp[0][1] = 0.05;
                break;
            case RODBARD:
                simp[0][0] = firsty;
                simp[0][1] = 1.0;
                simp[0][2] = xmean;
                simp[0][3] = lasty;
                break;
            case GAMMA_VARIATE:
                //  First guesses based on following observations:
                //  t0 [b] = time of first rise in gamma curve - so use the user specified first limit
                //  tm = t0 + a*B [c*d] where tm is the time of the peak of the curve
                //  therefore an estimate for a and B is sqrt(tm-t0)
                //  K [a] can now be calculated from these estimates
                simp[0][0] = firstx;
                double ab = xData[getMax(yData)] - firstx;
                simp[0][2] = Math.sqrt(ab);
                simp[0][3] = Math.sqrt(ab);
                simp[0][1] = yData[getMax(yData)] / (Math.pow(ab, simp[0][2]) * Math.exp(-ab/simp[0][3]));
                break;
        }
    }
    
    /** Pop up a dialog allowing control over simplex starting parameters */
    private void settingsDialog() {
        GenericDialog gd = new GenericDialog("Simplex Fitting Options", IJ.getInstance());
        gd.addMessage("Function name: " + fitList[fit] + "\n" +
        "Formula: " + fList[fit]);
        char pChar = 'a';
        for (int i = 0; i < numParams; i++) {
            gd.addNumericField("Initial "+(new Character(pChar)).toString()+":", simp[0][i], 2);
            pChar++;
        }
        gd.addNumericField("Maximum iterations:", maxIter, 0);
        gd.addNumericField("Number of restarts:", restarts, 0);
        gd.addNumericField("Error tolerance [1*10^(-x)]:", -(Math.log(maxError)/Math.log(10)), 0);
        gd.showDialog();
        if (gd.wasCanceled() || gd.invalidNumber()) {
            IJ.error("Parameter setting canceled.\nUsing default parameters.");
        }
        // Parametres:
        for (int i = 0; i < numParams; i++) {
            simp[0][i] = gd.getNextNumber();
        }
        maxIter = (int) gd.getNextNumber();
        restarts = (int) gd.getNextNumber();
        maxError = Math.pow(10.0, -gd.getNextNumber());
    }
    
    /** Restart the simplex at the nth vertex */
    void restart(int n) {
        // Copy nth vertice of simplex to first vertice
        for (int i = 0; i < numParams; i++) {
            simp[0][i] = simp[n][i];
        }
        sumResiduals(simp[0]);          // Get sum of residuals^2 for first vertex
        double[] step = new double[numParams];
        for (int i = 0; i < numParams; i++) {
            step[i] = simp[0][i] / 2.0;     // Step half the parametre value
            if (step[i] == 0.0)             // We can't have them all the same or we're going nowhere
                step[i] = 0.01;
        }
        // Some kind of factor for generating new vertices
        double[] p = new double[numParams];
        double[] q = new double[numParams];
        for (int i = 0; i < numParams; i++) {
            p[i] = step[i] * (Math.sqrt(numVertices) + numParams - 1.0)/(numParams * root2);
            q[i] = step[i] * (Math.sqrt(numVertices) - 1.0)/(numParams * root2);
        }
        // Create the other simplex vertices by modifing previous one.
        for (int i = 1; i < numVertices; i++) {
            for (int j = 0; j < numParams; j++) {
                simp[i][j] = simp[i-1][j] + q[j];
            }
            simp[i][i-1] = simp[i][i-1] + p[i-1];
            sumResiduals(simp[i]);
        }
        // Initialise current lowest/highest parametre estimates to simplex 1
        best = 0;
        worst = 0;
        nextWorst = 0;
        order();
    }
        
    // Display simplex [Iteration: s0(p1, p2....), s1(),....] in ImageJ window
    void showSimplex(int iter) {
        ij.IJ.write("" + iter);
        for (int i = 0; i < numVertices; i++) {
            String s = "";
            for (int j=0; j < numVertices; j++)
                s += "  "+ ij.IJ.d2s(simp[i][j], 6);
            ij.IJ.write(s);
        }
    }
        
    /** Get number of parameters for current fit function */
    public int getNumParams() {
        switch (fit) {
            case STRAIGHT_LINE: return 2;
            case POLY2: return 3;
            case POLY3: return 4;
            case POLY4: return 5;
            case EXPONENTIAL: return 2;
            case POWER: return 2;
            case LOG: return 2;
            case RODBARD: return 4;
            case GAMMA_VARIATE: return 4;
        }
        return 0;
    }
        
    /** Returns "fit" function value for parametres "p" at "x" */
    public static double f(int fit, double[] p, double x) {
        switch (fit) {
            case STRAIGHT_LINE:
                return p[0] + p[1]*x;
            case POLY2:
                return p[0] + p[1]*x + p[2]* x*x;
            case POLY3:
                return p[0] + p[1]*x + p[2]*x*x + p[3]*x*x*x;
            case POLY4:
                return p[0] + p[1]*x + p[2]*x*x + p[3]*x*x*x + p[4]*x*x*x*x;
            case EXPONENTIAL:
                return p[0]*Math.exp(p[1]*x);
            case POWER:
                if (x == 0.0)
                    return 0.0;
                else
                    return p[0]*Math.exp(p[1]*Math.log(x)); //y=ax^b
            case LOG:
                if (x == 0.0)
                    x = 0.5;
                return p[0]*Math.log(p[1]*x);
            case RODBARD:
                double ex;
                if (x == 0.0)
                    ex = 0.0;
                else
                    ex = Math.exp(Math.log(x/p[2])*p[1]);
                double y = p[0]-p[3];
                y = y/(1.0+ex);
                return y+p[3];
            case GAMMA_VARIATE:
                if (p[0] >= x) return 0.0;
                if (p[1] <= 0) return -100000.0;
                if (p[2] <= 0) return -100000.0;
                if (p[3] <= 0) return -100000.0;
                
                double pw = Math.pow((x - p[0]), p[2]);
                double e = Math.exp((-(x - p[0]))/p[3]);
                return p[1]*pw*e;
            default:
                return 0.0;
        }
    }

    /** Get the set of parameter values from the best corner of the simplex */
    public double[] getParams() {
        order();
        return simp[best];
    }
    
    /** Returns residuals array ie. differences between data and curve. */
    public double[] getResiduals() {
        double[] params = getParams();
        double[] residuals = new double[numPoints];
        for (int i = 0; i < numPoints; i++)
            residuals[i] = yData[i] - f(fit, params, xData[i]);
        return residuals;
    }
    
    /* Last "parametre" at each vertex of simplex is sum of residuals
     * for the curve described by that vertex
     */
    public double getSumResidualsSqr() {
        double sumResidualsSqr = (getParams())[getNumParams()];
        return sumResidualsSqr;
    }
    
    /**  SD = sqrt(sum of residuals squared / number of params+1)
     */
    public double getSD() {
        double sd = Math.sqrt(getSumResidualsSqr() / numVertices);
        return sd;
    }
    
    /**  Get a measure of "goodness of fit" where 1.0 is best.
     *
     */
    public double getFitGoodness() {
        double sumY = 0.0;
        for (int i = 0; i < numPoints; i++) sumY += yData[i];
        double mean = sumY / numVertices;
        double sumMeanDiffSqr = 0.0;
        int degreesOfFreedom = numPoints - getNumParams();
        double fitGoodness = 0.0;
        for (int i = 0; i < numPoints; i++) {
            sumMeanDiffSqr += sqr(yData[i] - mean);
        }
        if (sumMeanDiffSqr > 0.0 && degreesOfFreedom != 0)
            fitGoodness = 1.0 - (getSumResidualsSqr() / degreesOfFreedom) * ((numParams) / sumMeanDiffSqr);
        
        return fitGoodness;
    }
    
    /** Get a string description of the curve fitting results
     * for easy output.
     */
    public String getResultString() {
        StringBuffer results = new StringBuffer("\nNumber of iterations: " + getIterations() +
        "\nMaximum number of iterations: " + getMaxIterations() +
        "\nSum of residuals squared: " + getSumResidualsSqr() +
        "\nStandard deviation: " + getSD() +
        "\nGoodness of fit: " + getFitGoodness() +
        "\nParameters:");
        char pChar = 'a';
        double[] pVal = getParams();
        for (int i = 0; i < numParams; i++) {
            results.append("\n" + pChar + " = " + pVal[i]);
            pChar++;
        }
        return results.toString();
    }
        
    double sqr(double d) { return d * d; }
    
    /** Adds sum of square of residuals to end of array of parameters */
    void sumResiduals (double[] x) {
        x[numParams] = 0.0;
        for (int i = 0; i < numPoints; i++) {
            x[numParams] = x[numParams] + sqr(f(fit,x,xData[i])-yData[i]);
            //        if (IJ.debugMode) ij.IJ.log(i+" "+x[n-1]+" "+f(fit,x,xData[i])+" "+yData[i]);
        }
    }

    /** Keep the "next" vertex */
    void newVertex() {
        for (int i = 0; i < numVertices; i++)
            simp[worst][i] = next[i];
    }
    
    /** Find the worst, nextWorst and best current set of parameter estimates */
    void order() {
        for (int i = 0; i < numVertices; i++) {
            if (simp[i][numParams] < simp[best][numParams])	best = i;
            if (simp[i][numParams] > simp[worst][numParams]) worst = i;
        }
        nextWorst = best;
        for (int i = 0; i < numVertices; i++) {
            if (i != worst) {
                if (simp[i][numParams] > simp[nextWorst][numParams]) nextWorst = i;
            }
        }
        //        IJ.write("B: " + simp[best][numParams] + " 2ndW: " + simp[nextWorst][numParams] + " W: " + simp[worst][numParams]);
    }

    /** Get number of iterations performed */
    public int getIterations() {
        return numIter;
    }
    
    /** Get maximum number of iterations allowed */
    public int getMaxIterations() {
        return maxIter;
    }
    
    /** Set maximum number of iterations allowed */
    public void setMaxIterations(int x) {
        maxIter = x;
    }
    
    /** Get number of simplex restarts to do */
    public int getRestarts() {
        return restarts;
    }
    
    /** Set number of simplex restarts to do */
    public void setRestarts(int x) {
        restarts = x;
    }

    /**
     * Gets index of highest value in an array.
     * 
     * @param              Double array.
     * @return             Index of highest value.
     */
    public static int getMax(double[] array) {
        double max = array[0];
        int index = 0;
        for(int i = 1; i < array.length; i++) {
            if(max < array[i]) {
            	max = array[i];
            	index = i;
            }
        }
        return index;
    }
 
}
