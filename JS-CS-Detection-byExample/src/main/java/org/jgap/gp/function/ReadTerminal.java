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

import org.apache.commons.lang.builder.*;
import org.jgap.*;
import org.jgap.gp.*;
import org.jgap.gp.impl.*;

/**
 * Reads a value from the internal memory.
 *
 * @author Klaus Meffert
 * @since 3.0
 */
public class ReadTerminal
    extends CommandGene {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.11 $";

  /**
   * Symbolic name of the storage. Must correspond with a chosen name for
   * an other command working with memory (otherwise it would make no sense).
   */
  private String m_storageName;

  public ReadTerminal(final GPConfiguration a_conf, Class a_type,
                      String a_storageName)
      throws InvalidConfigurationException {
    this(a_conf, a_type, a_storageName, 0);
  }

  public ReadTerminal(final GPConfiguration a_conf, Class a_type,
                      String a_storageName, int a_subReturnType)
      throws InvalidConfigurationException {
    super(a_conf, 0, a_type, a_subReturnType, null);
    if (a_storageName == null || a_storageName.length() < 1) {
      throw new IllegalArgumentException("Memory name must not be empty!");
    }
    m_storageName = a_storageName;
  }

  public String toString() {
    return "read_from(" + m_storageName + ")";
  }

  /**
   * @return textual name of this command
   *
   * @author Klaus Meffert
   * @since 3.2
   */
  public String getName() {
    return "Read Terminal";
  }

  public int execute_int(ProgramChromosome c, int n, Object[] args) {
    check(c);
    // Read from memory.
    // -----------------
    try {
      return ( (Integer) getGPConfiguration().readFromMemory(
          m_storageName)).intValue();
    } catch (IllegalArgumentException iex) {
      throw new IllegalStateException(
          "ReadTerminal without preceeding StoreTerminal");
    }
  }

  public long execute_long(ProgramChromosome c, int n, Object[] args) {
    check(c);
    try {
      return ( (Long) getGPConfiguration().readFromMemory(
          m_storageName)).longValue();
    } catch (IllegalArgumentException iex) {
      throw new IllegalStateException(
          "ReadTerminal without preceeding StoreTerminal");
    }
  }

  public double execute_double(ProgramChromosome c, int n, Object[] args) {
    check(c);
    try {
      return ( (Double) getGPConfiguration().readFromMemory(
          m_storageName)).doubleValue();
    } catch (IllegalArgumentException iex) {
      throw new IllegalStateException(
          "ReadTerminal without preceeding StoreTerminal");
    }
  }

  public float execute_float(ProgramChromosome c, int n, Object[] args) {
    check(c);
    try {
      return ( (Float) getGPConfiguration().readFromMemory(
          m_storageName)).floatValue();
    } catch (IllegalArgumentException iex) {
      throw new IllegalStateException(
          "ReadTerminal without preceeding StoreTerminal");
    }
  }

  public Object execute_object(ProgramChromosome c, int n, Object[] args) {
    check(c);
    try {
      return getGPConfiguration().readFromMemory(m_storageName);
    } catch (IllegalArgumentException iex) {
      throw new IllegalStateException(
          "ReadTerminal without preceeding StoreTerminal");
    }
  }

  public boolean isValid(ProgramChromosome a_program) {
    return a_program.getIndividual().getCommandOfClass(0, StoreTerminal.class)
        >= 0;
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
      ReadTerminal other = (ReadTerminal) a_other;
      return new CompareToBuilder()
          .append(m_storageName, other.m_storageName)
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
        ReadTerminal other = (ReadTerminal) a_other;
        return new EqualsBuilder()
            .append(m_storageName, other.m_storageName)
            .isEquals();
      } catch (ClassCastException cex) {
        return false;
      }
    }
  }
}
