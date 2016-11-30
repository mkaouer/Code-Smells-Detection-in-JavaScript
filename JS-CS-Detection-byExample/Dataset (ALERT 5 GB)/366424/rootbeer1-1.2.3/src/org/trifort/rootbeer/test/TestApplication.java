/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.test;

public interface TestApplication {

  public boolean test();
  public String errorMessage();
  public String getEntrySignature();
  
}
