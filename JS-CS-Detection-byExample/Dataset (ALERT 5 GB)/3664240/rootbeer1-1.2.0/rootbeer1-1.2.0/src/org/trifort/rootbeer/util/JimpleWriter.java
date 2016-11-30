/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import soot.Printer;
import soot.SootClass;
import soot.util.EscapedWriter;

public class JimpleWriter {

  public void write(String filename, SootClass cls) throws Exception {
    OutputStream streamOut = new FileOutputStream(filename);
    PrintWriter writerOut = null;

    writerOut = new PrintWriter(
                new EscapedWriter(new OutputStreamWriter(streamOut)));
    Printer.v().printTo(cls, writerOut);

    writerOut.flush();
    streamOut.close();
  }
  
}
