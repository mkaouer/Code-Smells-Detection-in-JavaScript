/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package org.jgap.data.config;



/**
 * Exception throw when there is an error with configuring JGAP via the GUI.
 *
 * @author Siddhartha Azad
 * @since 2.3
 * */
public class ConfigException
    extends Exception {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.3 $";

  /**
   * Constructs a new ConfigException instance with the
   * given error message.
   *
   * @param a_message an error message describing the reason this exception
   * is being thrown.
   *
   * @author Siddhartha Azad
   * @since 2.3
   */
  public ConfigException(final String a_message) {
    super(a_message);
  }
}
