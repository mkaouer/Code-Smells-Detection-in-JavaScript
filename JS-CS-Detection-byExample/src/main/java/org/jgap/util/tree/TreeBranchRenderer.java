/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licencing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package org.jgap.util.tree;

//
//  TreeRenderer.java
//  MetaEvolve
//
//  Version 1
//
//  Created by Brian Risk of Geneffects on March 18, 2004.
//  Last Modified on March 19, 2004.
//  www.geneffects.com
//

import java.awt.*;

public interface TreeBranchRenderer {
  /** String containing the CVS revision. Read out via reflection!*/
  final static String CVS_REVISION = "$Revision: 1.1 $";

  public Color getBranchColor(Object node, int level);
}
