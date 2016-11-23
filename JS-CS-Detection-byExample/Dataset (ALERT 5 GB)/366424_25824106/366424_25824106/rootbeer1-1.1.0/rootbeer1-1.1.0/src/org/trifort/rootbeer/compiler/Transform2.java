/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.compiler;

import java.util.List;

import org.trifort.rootbeer.generate.bytecode.GenerateRuntimeBasicBlock;
import org.trifort.rootbeer.generate.opencl.OpenCLScene;

import soot.*;
import soot.rbclassload.DfsInfo;
import soot.rbclassload.RootbeerClassLoader;

public class Transform2 {
  
  private int m_Uuid;
  
  public Transform2(){
    m_Uuid = 1;
  }

  public void run(String cls){    
    OpenCLScene scene = new OpenCLScene();
    OpenCLScene.setInstance(scene);
    scene.init();
    
    SootClass soot_class1 = Scene.v().getSootClass(cls);
    SootMethod method = soot_class1.getMethod("void gpuMethod()");
    
    //generate RuntimeBasicBlock and Serializer
    String uuid = getUuid();
    GenerateRuntimeBasicBlock generate = new GenerateRuntimeBasicBlock(method, uuid);
    try {
      generate.makeClass();
    } catch(Exception ex){
      ex.printStackTrace();
      OpenCLScene.releaseV();
      return;
    }

    //add an interface to the class
    SootClass soot_class = method.getDeclaringClass();
    SootClass iface_class = Scene.v().getSootClass("org.trifort.rootbeer.runtime.CompiledKernel");
    soot_class.addInterface(iface_class);
    
    System.out.println("added interface CompiledKernel");
    
    OpenCLScene.releaseV();
  }
  
  private String getUuid(){
    int uuid = m_Uuid;
    m_Uuid++;
    return Integer.toString(uuid);
  }
}
