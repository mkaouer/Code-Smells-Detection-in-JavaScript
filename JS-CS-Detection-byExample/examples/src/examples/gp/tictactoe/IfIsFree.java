/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package examples.gp.tictactoe;

import org.jgap.*;
import org.jgap.gp.*;
import org.jgap.gp.impl.*;

/**
 * The if-then construct. If-Condition is: if specific color, then
 * execute X else do nothing.
 *
 * @author Klaus Meffert
 * @since 3.2
 */
public class IfIsFree
    extends CommandGene {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.2 $";

  private Class m_type;

  private Board m_board;

  public IfIsFree(final GPConfiguration a_conf, Board a_board, Class a_type)
      throws InvalidConfigurationException {
    this(a_conf, a_board, a_type, 0, null);
  }

  public IfIsFree(final GPConfiguration a_conf, Board a_board, Class a_type,
                  int a_subReturnType, int[] a_subChildTypes)
      throws InvalidConfigurationException {
    super(a_conf, 3, CommandGene.VoidClass, a_subReturnType, a_subChildTypes);
    m_type = a_type;
    m_board = a_board;
  }

  public String toString() {
    return "if free(&1, &2) then (&3)";
  }

  /**
   * @return textual name of this command
   *
   * @author Klaus Meffert
   * @since 3.2
   */
  public String getName() {
    return "If Free";
  }

  public void execute_void(ProgramChromosome c, int n, Object[] args) {
    check(c);
    boolean condition;
    if (m_type == CommandGene.IntegerClass) {
      int x = c.execute_int(n, 0, args);
      if (x < 0 || x >= Board.WIDTH) {
        throw new IllegalStateException("x must be 0.." + Board.WIDTH);
      }
      int y = c.execute_int(n, 1, args);
      if (y < 0 || x >= Board.HEIGHT) {
        throw new IllegalStateException("y must be 0.." + Board.HEIGHT);
      }
      condition = m_board.readField(x, y) == 0;
    }
    else {
      throw new IllegalStateException("IfIsFree: cannot process type " + m_type);
    }
    if (condition) {
      c.execute_void(n, 2, args);
    }
  }

  /**
   * Determines which type a specific child of this command has.
   *
   * @param a_ind ignored here
   * @param a_chromNum index of child
   * @return type of the a_chromNum'th child
   *
   * @author Klaus Meffert
   * @since 3.2
   */
  public Class getChildType(IGPProgram a_ind, int a_chromNum) {
    if (a_chromNum == 0 || a_chromNum == 1) {
      return m_type;
    }
    return CommandGene.VoidClass;
  }
}
