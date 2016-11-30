/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.Rootbeer;
import org.trifort.rootbeer.runtime.RootbeerGpu;
import org.trifort.rootbeer.runtime.ThreadConfig;
import org.trifort.rootbeer.runtime.util.Stopwatch;
import org.trifort.rootbeer.util.ForceGC;

public class RootbeerTestAgent {

  private long m_cpuTime;
  private long m_gpuTime;
  private boolean m_passed;
  private String m_message;
  private List<String> m_failedTests;
  
  public RootbeerTestAgent(){
    m_failedTests = new ArrayList<String>();
  }
  
  public void testOne(ClassLoader cls_loader, String test_case) throws Exception {
    Class test_case_cls = cls_loader.loadClass(test_case);
    Object test_case_obj = test_case_cls.newInstance();
    if(test_case_obj instanceof TestSerialization){
      TestSerialization test_ser = (TestSerialization) test_case_obj;
      System.out.println("[TEST 1/1] "+test_ser.toString());
      if(test_case.equals("org.trifort.rootbeer.testcases.rootbeertest.gpurequired.ChangeThreadTest")){
        testChangeThread(test_ser, true);
      } else {
        test(test_ser, true);
      }
      if(m_passed){
        System.out.println("  PASSED");
        System.out.println("  Cpu time: "+m_cpuTime+" ms");
        System.out.println("  Gpu time: "+m_gpuTime+" ms");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+m_message);
      }   
    } else if(test_case_obj instanceof TestException){
      TestException test_ex = (TestException) test_case_obj;
      System.out.println("[TEST 1/1] "+test_ex.toString());
      ex_test(test_ex, true);
      if(m_passed){
        System.out.println("  PASSED");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+m_message);
      }        
    } else if(test_case_obj instanceof TestKernelTemplate){
      TestKernelTemplate test_kernel_template = (TestKernelTemplate) test_case_obj;
      System.out.println("[TEST 1/1] "+test_kernel_template.toString());
      test(test_kernel_template, true);
      if(m_passed){
        System.out.println("  PASSED");
        System.out.println("  Cpu time: "+m_cpuTime+" ms");
        System.out.println("  Gpu time: "+m_gpuTime+" ms");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+m_message);
      }   
    } else if(test_case_obj instanceof TestApplication){ 
      TestApplication test_application = (TestApplication) test_case_obj;
      System.out.println("[TEST 1/1] "+test_application.toString());
      if(test_application.test()){
        System.out.println("  PASSED");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+test_application.errorMessage());
      }
    } else {
      throw new RuntimeException("unknown test case type");
    }
  }
  
  public void test(ClassLoader cls_loader, boolean run_hard_tests) throws Exception {
    LoadTestSerialization loader = new LoadTestSerialization();
    List<TestSerialization> creators = loader.load(cls_loader, "org.trifort.rootbeer.test.Main", run_hard_tests);
    List<TestException> ex_creators = loader.loadException(cls_loader, "org.trifort.rootbeer.test.ExMain");
    List<TestSerialization> change_thread = loader.load(cls_loader, "org.trifort.rootbeer.test.ChangeThread", run_hard_tests);
    List<TestKernelTemplate> kernel_template_creators = loader.loadKernelTemplate(cls_loader, "org.trifort.rootbeer.test.KernelTemplateMain");
    List<TestApplication> application_creators = loader.loadApplication(cls_loader, "org.trifort.rootbeer.test.ApplicationMain");
    int num_tests = creators.size() + ex_creators.size() + change_thread.size() + 
      kernel_template_creators.size() + application_creators.size();
    int test_num = 1;

    for(TestSerialization creator : creators){
      System.out.println("[TEST "+test_num+"/"+num_tests+"] "+creator.toString());
      test(creator, false);
      ForceGC.gc();
      if(m_passed){
        System.out.println("  PASSED");
        System.out.println("  Cpu time: "+m_cpuTime+" ms");
        System.out.println("  Gpu time: "+m_gpuTime+" ms");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+m_message);
        m_failedTests.add(creator.toString());
      }        
      ++test_num;
    }

    for(TestException ex_creator : ex_creators){
      System.out.println("[TEST "+test_num+"/"+num_tests+"] "+ex_creator.toString());
      ex_test(ex_creator, false);
      if(m_passed){
        System.out.println("  PASSED");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+m_message);
        m_failedTests.add(ex_creator.toString());
      }        
      ++test_num;
    }
    
    for(TestSerialization creator : change_thread){
      System.out.println("[TEST "+test_num+"/"+num_tests+"] "+creator.toString());
      testChangeThread(creator, false);
      if(m_passed){
        System.out.println("  PASSED");
        System.out.println("  Cpu time: "+m_cpuTime+" ms");
        System.out.println("  Gpu time: "+m_gpuTime+" ms");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+m_message);
        m_failedTests.add(creator.toString());
      }        
      ++test_num;
    }
    
    for(TestKernelTemplate kernel_template : kernel_template_creators){
      System.out.println("[TEST "+test_num+"/"+num_tests+"] "+kernel_template.toString());
      test(kernel_template, false);
      ForceGC.gc();
      if(m_passed){
        System.out.println("  PASSED");
        System.out.println("  Cpu time: "+m_cpuTime+" ms");
        System.out.println("  Gpu time: "+m_gpuTime+" ms");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+m_message);
        m_failedTests.add(kernel_template.toString());
      }        
      ++test_num;
    }
    
    for(TestApplication application : application_creators){
      System.out.println("[TEST "+test_num+"/"+num_tests+"] "+application.toString());
      if(application.test()){
        System.out.println("  PASSED");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+application.errorMessage());
        m_failedTests.add(application.toString());
      }
      ++test_num;
    }

    
    int test_passed = num_tests - m_failedTests.size();
    System.out.println(test_passed+"/"+num_tests+" tests PASS");
    if(test_passed == num_tests){
      System.out.println("ALL TESTS PASS!");
    } else {
      System.out.println("Failing tests:");
      for(String failure : m_failedTests){
        System.out.println("  "+failure);
      }
    } 
  }
  
  private void test(TestSerialization creator, boolean print_mem) {
    int i = 0;
    try {      
      Rootbeer rootbeer = new Rootbeer();
      Configuration.setPrintMem(print_mem);
      List<Kernel> known_good_items = creator.create();
      List<Kernel> testing_items = creator.create();
      Stopwatch watch = new Stopwatch();
      watch.start();
      rootbeer.run(testing_items);
      m_passed = true;
      watch.stop();
      m_gpuTime = watch.elapsedTimeMillis();
      watch.start();
      for(i = 0; i < known_good_items.size(); ++i){       
        Kernel known_good_item = known_good_items.get(i);
        known_good_item.gpuMethod();
      }
      watch.stop();
      m_cpuTime = watch.elapsedTimeMillis();
      for(i = 0; i < known_good_items.size(); ++i){
        Kernel known_good_item = known_good_items.get(i);
        Kernel testing_item = testing_items.get(i);
        if(!creator.compare(known_good_item, testing_item)){
          m_message = "Compare failed at: "+i;
          m_passed = false;
          return;
        }
      }
    } catch(Throwable ex){
      ex.printStackTrace(System.out);
      m_message = "Exception thrown at index: "+i+"\n";
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter(os);
      ex.printStackTrace(writer);
      writer.flush();
      writer.close();
      m_message += os.toString();
      m_passed = false;
    }
  }

  private void test(TestKernelTemplate creator, boolean print_mem) {
    int i = 0;
    try {      
      Rootbeer rootbeer = new Rootbeer();
      Configuration.setPrintMem(print_mem);
      Kernel known_good_item = creator.create();
      Kernel testing_item = creator.create();
      ThreadConfig thread_config = creator.getThreadConfig();
      Stopwatch watch = new Stopwatch();
      watch.start();
      rootbeer.run(testing_item, thread_config);
      m_passed = true;
      watch.stop();
      m_gpuTime = watch.elapsedTimeMillis();
      watch.start();
      for(int block = 0; block < thread_config.getGridShapeX(); ++block){
        for(int thread = 0; thread < thread_config.getBlockShapeX(); ++thread){
          RootbeerGpu.setBlockIdxx(block);
          RootbeerGpu.setThreadIdxx(thread);
          known_good_item.gpuMethod();
        }
      }
      watch.stop();
      m_cpuTime = watch.elapsedTimeMillis();
      if(!creator.compare(known_good_item, testing_item)){
        m_message = "Compare failed at: "+i;
        m_passed = false;
        return;
      }
    } catch(Throwable ex){
      ex.printStackTrace(System.out);
      m_message = "Exception thrown at index: "+i+"\n";
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter(os);
      ex.printStackTrace(writer);
      writer.flush();
      writer.close();
      m_message += os.toString();
      m_passed = false;
    }
  }
  
  private void ex_test(TestException creator, boolean print_mem) {
    Rootbeer rootbeer = new Rootbeer();
    Configuration.setPrintMem(print_mem);
    List<Kernel> testing_items = creator.create();
    try {
      rootbeer.run(testing_items);
      m_passed = false;
      m_message = "No exception thrown when expecting one.";
    } catch(Throwable ex){
      m_passed = creator.catchException(ex);
      if(m_passed == false){
        m_message = "Exception is: "+ex.toString(); 
      }
    }
  }

  private void testChangeThread(TestSerialization creator, boolean print_mem) {
    Thread t = new Thread(new ChangeThread(creator, print_mem));
    t.start();
    try {
      t.join();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  private class ChangeThread implements Runnable {

    private TestSerialization m_creator;
    private boolean m_printMem;
    private Rootbeer m_rootbeer;
    
    public ChangeThread(TestSerialization creator, boolean print_mem){
      m_creator = creator;
      m_printMem = print_mem;
      m_rootbeer = new Rootbeer();
    }
    
    public void run() {
      int i = 0;
      try {      
        Configuration.setPrintMem(m_printMem);
        List<Kernel> known_good_items = m_creator.create();
        List<Kernel> testing_items = m_creator.create();
        Stopwatch watch = new Stopwatch();
        watch.start();
        m_rootbeer.run(testing_items);
        m_passed = true;
        watch.stop();
        m_gpuTime = watch.elapsedTimeMillis();
        watch.start();
        for(i = 0; i < known_good_items.size(); ++i){       
          Kernel known_good_item = known_good_items.get(i);
          known_good_item.gpuMethod();
        }
        watch.stop();
        m_cpuTime = watch.elapsedTimeMillis();
        for(i = 0; i < known_good_items.size(); ++i){
          Kernel known_good_item = known_good_items.get(i);
          Kernel testing_item = testing_items.get(i);
          if(!m_creator.compare(known_good_item, testing_item)){
            m_message = "Compare failed at: "+i;
            m_passed = false;
            return;
          }
        }
      } catch(Throwable ex){
        ex.printStackTrace(System.out);
        m_message = "Exception thrown at index: "+i+"\n";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(os);
        ex.printStackTrace(writer);
        writer.flush();
        writer.close();
        m_message += os.toString();
        m_passed = false;
      }
    }
  }
}
