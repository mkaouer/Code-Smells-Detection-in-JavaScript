/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.test.execution;

import java.io.File;

public class SubsystemTest {

  public static void main(String[] args){
    SubsystemTest test = new SubsystemTest();
    //test.testJar("../../FruitingCompute/ofcoarse/ofcoarse-1.0.jar");
    test.testJar("Rootbeer-Test1");
    //test.testJar("Rootbeer-Test2");
    //test.testJar("Rootbeer-Test3");
    //test.testJar("Rootbeer-Test4");
    //test.testJar("Rootbeer-Test5");
    //test.testJar("SciMark");
  }

  private void testJar(String test_name) {
    String sep = File.separator;
    String jar_file = ".." + sep + test_name + sep + "dist" + sep + test_name + ".jar";
    ExecutionTest tester = new ExecutionTest();
    boolean ret = tester.run(jar_file);
    if(ret){
      System.out.println("All tests pass");
    } else {
      System.out.println("Tests FAIL");
    }
  }
}