/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package org.jgap.gp.function;

import org.jgap.*;
import org.jgap.gp.*;
import org.apache.commons.lang.builder.*;
import org.jgap.gp.impl.*;

/**
 * The for-loop. You can preset the start index and the end index. If the latter
 * is not given, it is dynamically computed from a child.
 *
 * @author Klaus Meffert
 * @since 3.0
 */
public class ForLoop
    extends CommandGene {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.15 $";

  private static String INTERNAL_COUNTER_STORAGE = "FORLOOPSTORAGE_INT";

  private Class m_typeVar;

  private int m_startIndex;

  private int m_endIndex;

  private int m_increment;

  private int m_maxLoop;

  private String m_memory_name_int;

  private String m_varName;

  /**
   * Constructor.
   *
   * @param a_conf the configuration to use
   * @param a_typeVar Class of the loop counter terminakl (e.g. IntegerClass)
   * @param a_maxLoop the maximum number of loops to perform
   * @throws InvalidConfigurationException
   *
   * @author Klaus Meffert
   * @since 3.0
   */
  public ForLoop(final GPConfiguration a_conf, Class a_typeVar, int a_maxLoop)
      throws InvalidConfigurationException {
    this(a_conf, a_typeVar, 0, a_maxLoop);
  }

  /**
   * Constructor allowing to preset the starting index of the loop.
   *
   * @param a_conf the configuration to use
   * @param a_typeVar Class of the loop counter terminakl (e.g. IntegerClass)
   * @param a_startIndex index to start the loop with (normally 0)
   * @param a_maxLoop the maximum number of loops to perform
   * @throws InvalidConfigurationException
   *
   * @author Klaus Meffert
   * @since 3.0
   */
  public ForLoop(final GPConfiguration a_conf, Class a_typeVar,
                 int a_startIndex, int a_maxLoop)
      throws InvalidConfigurationException {
    this(a_conf, a_typeVar, a_startIndex, a_maxLoop, "i");
  }

  public ForLoop(final GPConfiguration a_conf, Class a_typeVar,
                 int a_startIndex, int a_maxLoop, String a_varName)
      throws InvalidConfigurationException {
    super(a_conf, 2, CommandGene.VoidClass);
    m_typeVar = a_typeVar;
    m_maxLoop = a_maxLoop;
    m_startIndex = a_startIndex;
    m_endIndex = -1;
    m_increment = 1;
    m_varName = a_varName;
    init();
  }

  /**
   * Constructor allowing to preset the starting and the ending index of the
   * loop.
   *
   * @param a_conf the configuration to use
   * @param a_typeVar Class of the loop counter terminal (e.g. IntegerClass)
   * @param a_startIndex index to start the loop with
   * @param a_endIndex index to end the loop with
   * @param a_increment the maximum number of loops to perform
   * @param a_varName informal textual name of the loop counter variable
   * @throws InvalidConfigurationException
   *
   * @author Klaus Meffert
   * @since 3.2
   */
  public ForLoop(final GPConfiguration a_conf, Class a_typeVar,
                 int a_startIndex, int a_endIndex, int a_increment,
                 String a_varName)
      throws InvalidConfigurationException {
    this(a_conf, a_typeVar, a_startIndex, a_endIndex, a_increment, a_varName, 0,
         0);
  }

  public ForLoop(final GPConfiguration a_conf, Class a_typeVar,
                 int a_startIndex, int a_endIndex, int a_increment,
                 String a_varName, int a_subReturnType, int a_subChildType)
      throws InvalidConfigurationException {
    super(a_conf, 1, CommandGene.VoidClass, a_subReturnType, a_subChildType);
    m_typeVar = a_typeVar;
    m_increment = a_increment;
    m_startIndex = a_startIndex;
    m_endIndex = a_endIndex;
    m_varName = a_varName;
    init();
  }

  protected void init() {
    super.init();
    // Generate unique name.
    // ---------------------
    m_memory_name_int = INTERNAL_COUNTER_STORAGE;
    m_memory_name_int += m_varName;
    m_memory_name_int += getGPConfiguration().getRandomGenerator().nextDouble();
  }

  public String toString() {
    if (m_endIndex == -1) {
      return "for(int i=" + m_startIndex + ";i<&1;i++) { &2 }";
    }
    else {
      String incrString;
      if (m_increment == 1) {
        incrString = m_varName + "++";
      }
      else {
        incrString = m_varName + "=" + m_varName + "+1";
      }
      return "for(int " + m_varName + "=" + m_startIndex + ";" + m_varName +
          "<" + m_endIndex + ";" +
          incrString + ") { &1 }";
    }
  }

  /**
   * @return textual name of this command
   *
   * @author Klaus Meffert
   * @since 3.2
   */
  public String getName() {
    return "ForLoop";
  }

  public void execute_void(ProgramChromosome c, int n, Object[] args) {
    // Determine the end index of the loop (child at index 0).
    // -------------------------------------------------------
    int x;
    if (m_endIndex == -1) {
      if (m_typeVar == CommandGene.IntegerClass) {
        x = c.execute_int(n, 0, args);
      }
      else if (m_typeVar == CommandGene.LongClass) {
        x = (int) c.execute_long(n, 0, args);
      }
      else if (m_typeVar == CommandGene.DoubleClass) {
        x = (int) Math.round(c.execute_double(n, 0, args));
      }
      else if (m_typeVar == CommandGene.FloatClass) {
        x = (int) Math.round(c.execute_float(n, 0, args));
      }
      else {
        throw new RuntimeException("Type "
                                   + m_typeVar
                                   + " not supported by ForLoop");
      }
      if (x > m_maxLoop) {
        x = m_maxLoop;
      }
      // Repeatedly execute the second child (index = 1).
      // ------------------------------------------------
      for (int i = m_startIndex; i < x; i++) {
        c.execute_void(n, 1, args);
      }
    }
    else {
      // Repeatedly execute the first child (index = 0).
      // -----------------------------------------------
      for (int i = m_startIndex; i < m_endIndex; i = i + m_increment) {
        // Store counter in memory.
        // ------------------------
        getGPConfiguration().storeInMemory(ForLoop.INTERNAL_COUNTER_STORAGE,
            new Integer(i));
        c.execute_void(n, 0, args);
      }
    }
  }

  public boolean isValid(ProgramChromosome a_program) {
    return true;
  }

  public Class getChildType(IGPProgram a_ind, int a_chromNum) {
    if (m_endIndex == -1) {
      // Variant A: dynamic end index
      if (a_chromNum == 0) {
        // Loop counter variable.
        // ----------------------
        return m_typeVar;
      }
      else {
        // Subprogram.
        // -----------
        return CommandGene.VoidClass;
      }
    }
    else {
      // Variant B: fixed end index
      return CommandGene.VoidClass;
    }
  }

  /**
   * The compareTo-method.
   *
   * @param a_other the other object to compare
   * @return -1, 0, 1
   *
   * @author Klaus Meffert
   * @since 3.0
   */
  public int compareTo(Object a_other) {
    if (a_other == null) {
      return 1;
    }
    else {
      ForLoop other = (ForLoop) a_other;
      return new CompareToBuilder()
          .append(m_typeVar, other.m_typeVar)
          .append(m_maxLoop, other.m_maxLoop)
          .toComparison();
    }
  }

  /**
   * The equals-method.
   *
   * @param a_other the other object to compare
   * @return true if the objects are seen as equal
   *
   * @author Klaus Meffert
   * @since 3.0
   */
  public boolean equals(Object a_other) {
    if (a_other == null) {
      return false;
    }
    else {
      try {
        ForLoop other = (ForLoop) a_other;
        return new EqualsBuilder()
            .append(m_typeVar, other.m_typeVar)
            .append(m_maxLoop, other.m_maxLoop)
            .isEquals();
      } catch (ClassCastException cex) {
        return false;
      }
    }
  }

  /**
   * @return Name of the memory cell where the current value of the loop
   * variable is stored
   *
   * @author Klaus Meffert
   * @since 3.2
   */
  public String getCounterMemoryName() {
    return m_memory_name_int;
  }
}
