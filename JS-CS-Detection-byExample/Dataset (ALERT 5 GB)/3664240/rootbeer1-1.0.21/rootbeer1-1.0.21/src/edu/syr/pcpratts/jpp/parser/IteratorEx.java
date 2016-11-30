/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser;

import java.util.Iterator;

public interface IteratorEx<T> extends Iterator<T> {

  T peek(int tokens);
  void putback(T item);
}
