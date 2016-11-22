/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licencing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package org.jgap.gp.function;

import org.jgap.*;
import org.jgap.gp.*;
import org.jgap.gp.impl.*;

/**
 * The modulo operation.
 *
 * @author Konrad Odell
 * @author Klaus Meffert
 * @since 3.0
 */
public class Modulo
    extends MathCommand {
  /** String containing the CVS revision. Read out via reflection!*/
  private static final String CVS_REVISION = "$Revision: 1.7 $";

  public Modulo(final GPConfiguration a_conf, Class a_type)
      throws InvalidConfigurationException {
    super(a_conf, 2, a_type);
  }

  public String toString() {
    return "&1 % &2";
  }

  /**
   * @return textual name of this command
   *
   * @author Klaus Meffert
   * @since 3.2
   */
  public String getName() {
    return "Modulo";
  }

  public int execute_int(ProgramChromosome c, int n, Object[] args) {
    int v1 = c.execute_int(n, 0, args);
    int v2 = c.execute_int(n, 1, args);
    if (v2 == 0) {
      return 0;
    }
    return v1 % v2;
  }

  public long execute_long(ProgramChromosome c, int n, Object[] args) {
    long v1 = c.execute_long(n, 0, args);
    long v2 = c.execute_long(n, 1, args);
    if (v2 == 0) {
      return 0;
    }
    return v1 % v2;
  }

  public float execute_float(ProgramChromosome c, int n, Object[] args) {
    float v1 = c.execute_float(n, 0, args);
    float v2 = c.execute_float(n, 1, args);
    if (Math.abs(v2) < DELTA) {
      return 0;
    }
    return v1 % v2;
  }

  public double execute_double(ProgramChromosome c, int n, Object[] args) {
    double v1 = c.execute_double(n, 0, args);
    double v2 = c.execute_double(n, 1, args);
    if (Math.abs(v2) < DELTA) {
      return 0;
    }
    return v1 % v2;
  }

  public Object execute_object(ProgramChromosome c, int n, Object[] args) {
    try {
      return ( (Compatible) c.execute_object(n, 0, args)).execute_mod(c.
          execute_object(n, 1, args));
    } catch (ArithmeticException aex) {
      throw new IllegalStateException("mod with illegal arguments");
    }
  }

  protected interface Compatible {
    public Object execute_mod(Object o);
  }
}
