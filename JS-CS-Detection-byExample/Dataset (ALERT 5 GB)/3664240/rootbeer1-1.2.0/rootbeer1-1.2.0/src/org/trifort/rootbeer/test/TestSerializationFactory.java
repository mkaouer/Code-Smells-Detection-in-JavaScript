/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.test;

import java.util.List;

public interface TestSerializationFactory {

  List<TestSerialization> getProviders();
  void makeHarder();
}
