/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package examples.gp.anttrail;

import java.io.*;
import java.util.*;
import org.jgap.*;
import org.jgap.event.*;
import org.jgap.gp.*;
import org.jgap.gp.function.*;
import org.jgap.gp.impl.*;
import org.jgap.util.*;
import org.jgap.util.tree.*;

/**
 * The ant trail problem.
 *
 * @author Klaus Meffert
 * @since 3.01
 */
public class AntTrailProblem
    extends GPProblem {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.15 $";

  private int[][] m_map;

  private static int foodAvail;

  private static int m_maxx;

  private static int m_maxy;

  private static int totalFood;

  /**
   * Maximum number of moves allowed.
   */
  private static int m_maxMoves = 400;

  public AntTrailProblem(GPConfiguration a_conf)
      throws InvalidConfigurationException {
    super(a_conf);
  }

  /**
   * Sets up the functions to use and other parameters. Then creates the
   * initial genotype.
   *
   * @return the genotype created
   * @throws InvalidConfigurationException
   *
   * @author Klaus Meffert
   * @since 3.0
   */
  public GPGenotype create()
      throws InvalidConfigurationException {
    Class[] types = {CommandGene.VoidClass};
    Class[][] argTypes = { {}
    };
    int[] minDepths = new int[] {6};
    int[] maxDepths = new int[] {9};
    GPConfiguration conf = getGPConfiguration();
    CommandGene[][] nodeSets = { {
        new SubProgram(conf, new Class[] {CommandGene.VoidClass,
                       CommandGene.VoidClass, CommandGene.VoidClass}),
        new SubProgram(conf, new Class[] {CommandGene.VoidClass, //nonclassic
                       CommandGene.VoidClass, CommandGene.VoidClass,
                       CommandGene.VoidClass}),
        new Left(conf),
        new Right(conf),
        new Move(conf),
        new Move(conf, 3), //nonclassic
        new IfFoodAheadElse(conf),
        new IfFoodAheadLeft(conf), //nonclassic
        new IfFoodAheadRight(conf), //nonclassic
        new Loop(conf, CommandGene.IntegerClass, 3), //nonclassic
        new TurnToFood(conf), //nonclassic
    }
    };
    // Create genotype with initial population.
    // ----------------------------------------
    return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodeSets,
        minDepths, maxDepths, 5000, new boolean[] {true}, true);
  }

  private int[][] readTrail(String a_filename)
      throws Exception {
    LineNumberReader lnr;
    try {
      lnr = new LineNumberReader(new FileReader(a_filename));
    } catch (FileNotFoundException fex) {
      throw new FileNotFoundException("File not found: " +
                                      new File(".").getAbsolutePath() +
                                      a_filename);
    }
    // Read dimensions of trail.
    // -------------------------
    try {
      StringTokenizer st = new StringTokenizer(lnr.readLine());
      m_maxx = Integer.parseInt(st.nextToken());
      m_maxy = Integer.parseInt(st.nextToken());
      int[][] result = new int[m_maxx][m_maxy];
      int y;
      foodAvail = 0;
      for (y = 0; y < m_maxy; y++) {
        String s = lnr.readLine();
        if (s == null) {
          throw new RuntimeException("Ant trail file ended prematurely");
        }
        int x;
        for (x = 0; x < s.length(); x++) {
          if (s.charAt(x) == ' ') {
            result[x][y] = AntMap.EMPTY;
          }
          else if (s.charAt(x) == '#') {
            result[x][y] = AntMap.FOOD;
            foodAvail++;
          }
          else if (s.charAt(x) == '.') {
            result[x][y] = AntMap.TRAIL;
          }
          else {
            throw new RuntimeException("Bad character '" + s.charAt(x) +
                                       "' on line number " + lnr.getLineNumber() +
                                       " of the Ant trail file.");
          }
        }
        // fill out rest of X's
        for (int z = x; z < m_maxx; z++) {
          result[z][y] = AntMap.EMPTY;
        }
      }
      // fill out rest of Y's
      for (int z = y; z < m_maxy; z++) {
        for (int x = 0; x < m_maxx; x++) {
          result[x][z] = AntMap.EMPTY;
        }
      }
      return result;
    } catch (NumberFormatException e) {
      throw new RuntimeException(
          "The Ant trail file does not begin with x and y integer values.");
    } catch (IOException e) {
      throw new RuntimeException(
          "The Ant trail file could not be read due to an IOException:\n" + e);
    }
  }

  /**
   * Starts the example.
   *
   * @param args ignored
   * @throws Exception
   *
   * @author Klaus Meffert
   * @since 3.0
   */
  public static void main(String[] args) {
    try {
      System.out.println("Ant trail problem");
      GPConfiguration config = new GPConfiguration();
      config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
      int popSize = 500;
      String filename;
      if (args.length == 1) {
        filename = args[0];
      }
      else {
        filename = "santafe.trail";
      }
      System.out.println("Using population size of " + popSize);
      System.out.println("Using map " + filename);
      config.setMaxInitDepth(7);
      config.setPopulationSize(popSize);
      final AntTrailProblem problem = new AntTrailProblem(config);
      GPFitnessFunction func = problem.createFitFunc();
      config.setFitnessFunction(func);
      config.setCrossoverProb(0.9f);
      config.setReproductionProb(0.1f);
      config.setNewChromsPercent(0.3f);
      config.setStrictProgramCreation(true);
      config.setUseProgramCache(true);
      // Read the trail from file.
      // -------------------------
      problem.m_map = problem.readTrail(filename);
      AntMap antmap = new AntMap(problem.m_map, m_maxMoves);
      totalFood = countFood(antmap);
      System.out.println("Food to consume by ant: " + totalFood);
      GPGenotype gp = problem.create();
      gp.setVerboseOutput(true);
      // Simple implementation of running evolution in a thread.
      // -------------------------------------------------------
      final Thread t = new Thread(gp);
      config.getEventManager().addEventListener(GeneticEvent.
          GPGENOTYPE_EVOLVED_EVENT, new GeneticEventListener() {
        public void geneticEventFired(GeneticEvent a_firedEvent) {
          GPGenotype genotype = (GPGenotype) a_firedEvent.getSource();
          int evno = genotype.getGPConfiguration().getGenerationNr();
          double freeMem = SystemKit.getFreeMemoryMB();
          if (evno % 100 == 0) {
            double bestFitness = genotype.getFittestProgram().
                getFitnessValue();
            System.out.println("Evolving generation " + evno
                               + ", best fitness: " + bestFitness
                               + ", memory free: " + freeMem + " MB");
          }
          if (evno > 500000) {
            t.stop();
          }
          else {
            try {
              // Collect garbage if memory low.
              // ------------------------------
              if (freeMem < 50) {
                System.gc();
                t.sleep(500);
              }
              else {
                // Avoid 100% CPU load.
                // --------------------
                t.sleep(30);
              }
            } catch (InterruptedException iex) {
              iex.printStackTrace();
              System.exit(1);
            }
          }
        }
      });

      GeneticEventListener myGeneticEventListener =
      new GeneticEventListener() {
        /**
         * New best solution found.
         *
         * @param a_firedEvent GeneticEvent
         */
        public void geneticEventFired(GeneticEvent a_firedEvent) {
          GPGenotype genotype = (GPGenotype) a_firedEvent.getSource();
          int evno = genotype.getGPConfiguration().getGenerationNr();
          String indexString = "" + evno;
          while (indexString.length() < 5) {
            indexString = "0" + indexString;
          }
          String filename = "anttrail_best" + indexString + ".png";
          IGPProgram best = genotype.getAllTimeBest();
          try {
            // Create graphical tree of GPProgram.
            // -----------------------------------
            TreeBranchRenderer antBranchRenderer = new AntTreeBranchRenderer();
            TreeNodeRenderer antNodeRenderer = new AntTreeNodeRenderer();
            problem.showTree(best, filename, antBranchRenderer, antNodeRenderer);
            // Display solution's trail.
            // -------------------------
            AntMap antmap = (AntMap) best.getApplicationData();
            problem.displaySolution(antmap.getMovements());
            System.out.println(" Number of moves: " + antmap.getMoveCount());
            System.out.println(" Food taken: " + antmap.getFoodTaken());
          } catch (InvalidConfigurationException iex) {
            iex.printStackTrace();
          }
          double bestFitness = genotype.getFittestProgram().
              getFitnessValue();
          if (bestFitness < 0.001) {
            genotype.outputSolution(best);
            t.stop();
            System.exit(0);
          }
        }
      };

      config.getEventManager().addEventListener(GeneticEvent.
          GPGENOTYPE_NEW_BEST_SOLUTION, myGeneticEventListener);
      t.start();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Display ant trail as found by GP.
   *
   * @param a_antmap the map containing the trail
   */
  private void displaySolution(int[][] a_antmap) {
    for (int y = 0; y < m_maxy; y++) {
      for (int x = 0; x < m_maxx; x++) {
        char toPrint;
        int c = a_antmap[x][y];
        if (c < 32) {
          switch (c) {
            case AntMap.FOOD:
              toPrint = '#';
              break;
            case AntMap.TRAIL:
              toPrint = '.';
              break;
            default:
              toPrint = ' ';
          }
        }
        else {
          toPrint = (char) c;
        }
        System.out.print(toPrint);
      }
      System.out.println();
    }
  }

  private GPFitnessFunction createFitFunc() {
    return new AntFitnessFunction();
  }

//  static class MyGeneticEventListener implements GeneticEventListener {
//
//    private Thread m_t;
//    private AntTrailProblem m_problem;
//
//    public MyGeneticEventListener(Thread a_t, AntTrailProblem a_problem) {
//      m_t = a_t;
//      m_problem =a_problem;
//    }

  class AntFitnessFunction
      extends GPFitnessFunction {
    private static final int VALUE1 = 100;

    protected double evaluate(final IGPProgram a_subject) {
      return computeRawFitness(a_subject);
    }

    public double computeRawFitness(final IGPProgram a_program) {
      double error = 0.0f;
      Object[] noargs = new Object[0];
      // Initialize local stores.
      // ------------------------
      a_program.getGPConfiguration().clearStack();
      a_program.getGPConfiguration().clearMemory();
      AntMap antmap = new AntMap(m_map, m_maxMoves);
      a_program.setApplicationData(antmap);
      try {
        // Execute the program.
        // --------------------
        a_program.execute_void(0, noargs);
        // Determine success of individual.
        // --------------------------------
        antmap = (AntMap) a_program.getApplicationData();
        // The remaining food is the defect rate here.
        // -------------------------------------------
        int foodTaken = antmap.getFoodTaken(); // countFood(antmap);
        error = (VALUE1 + totalFood - foodTaken) * 4;
        if (a_program.getGPConfiguration().stackSize() > 0) {
          error = GPFitnessFunction.MAX_FITNESS_VALUE;
        }
        if (error < 0.000001) {
          error = 0.0d;
        }
        else if (error < GPFitnessFunction.MAX_FITNESS_VALUE) {
          // Add penalty for longer trails.
          // ------------------------------
          int moves = antmap.getMoveCount();
          error = error + moves * 1.5;
        }
      } catch (IllegalStateException iex) {
        error = GPFitnessFunction.MAX_FITNESS_VALUE;
      }
      return error;
    }
  }
  private static int countFood(AntMap a_map) {
    int result = 0;
    for (int x = 0; x < m_maxx; x++) {
      for (int y = 0; y < m_maxy; y++) {
        if (a_map.getFromMap(x, y) == AntMap.FOOD) {
          result++;
        }
      }
    }
    return result;
  }
}
/*
 abcd
    e
    f                    abcdef
    g                    Z    g
    h                    Y    h
    ijklmnopqr       TUVWX    i
             s       S        j
             t       R        k
             u       Q        l
             v       P        m
             w       O        n
             x       N        o
             y       M        p
             z       L        q
             A       K  xwvutsr
             B   FGHIJ  y
             C   E      z
             D   D      A
             E   C      BC
             F   B
             G   A
             H   z
             I   y
             J   x
  VUTSRQPONMLK   w
  W              v
  X              u
  Y     klmnopqrst
  Z     j
  a     i
  bcdefgh

  Number of moves: 193
  Best solution fitness: 3.0
  Best solution: loop(3, (loop(3, (loop(3, (sub[(sub[right --> left --> move --> turn-to-food]) --> (if-food (left) else (move)) --> (loop(3, turn-to-food }) --> (loop(3, (loop(3, turn-to-food }) })]) }) }) }
  Depth of chromosome: 6

 */
