/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.bytecode.Constants;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import soot.Scene;
import soot.SootClass;

public class ObjectCloneGenerate {
 
  public String get(Set<OpenCLArrayType> arrays, Map<String, OpenCLClass> classes, Map<String, String> ocl_to_soot){
    StringBuilder ret = new StringBuilder();
    String device_function_qual = Tweaks.v().getDeviceFunctionQualifier();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    ret.append(device_function_qual+" int \n");
    ret.append("java_lang_Object_clone("+address_qual+" char * gc_info, int thisref, int * exception){\n");
    ret.append("  char * src_deref = edu_syr_pcpratts_gc_deref(gc_info, thisref);\n");
    ret.append("  GC_OBJ_TYPE_TYPE type = edu_syr_pcpratts_gc_get_type(src_deref);\n");
    ret.append("  if(0){}\n");
    Iterator<OpenCLArrayType> iter1 = arrays.iterator();
    while(iter1.hasNext()){
      OpenCLArrayType atype = iter1.next();
      cloneArray(ret, atype);
    }
    Iterator<String> iter2 = classes.keySet().iterator();
    while(iter2.hasNext()){
      String key = iter2.next();
      if(key.contains(".")){
        continue; 
      }
      OpenCLClass ocl_class = classes.get(key);
      String soot_class = ocl_to_soot.get(key);
      cloneRefType(ret, soot_class, ocl_class);
    }
    ret.append("  return -1;\n");
    ret.append("}\n");
    return ret.toString();
  }

  private void cloneArray(StringBuilder ret, OpenCLArrayType atype) {
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    ret.append("  else if(type == "+atype.getTypeInteger()+"){\n");
    ret.append("    int size = edu_syr_pcpratts_array_length(gc_info, thisref);\n");
    ret.append("    int new_ref = "+atype.getDerefTypeString()+"_new(gc_info, size, exception);\n");
    ret.append("    int total_size = (size * "+atype.getElementSize()+") + "+Constants.ArrayOffsetSize+";\n");
    ret.append("    "+address_qual+" char * dest_deref = edu_syr_pcpratts_gc_deref(gc_info, new_ref);\n");
    ret.append("    edu_syr_pcpratts_gc_memcpy(dest_deref, src_deref, total_size);\n");
    ret.append("    edu_syr_pcpratts_gc_set_ctor_used(dest_deref, 1);\n");
    ret.append("    return new_ref;\n");
    ret.append("  }\n");
  }

  private void cloneRefType(StringBuilder ret, String key, OpenCLClass ocl_class) {
    SootClass soot_class = Scene.v().getSootClass(key);
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    int type = OpenCLScene.v().getClassType(soot_class.getType());
    ret.append("  else if(type == "+type+"){\n");
    ret.append("    int size = "+ocl_class.getSize()+";\n");
    ret.append("    long long new_ref = edu_syr_pcpratts_gc_malloc(gc_info, size);\n");
    ret.append("    if(new_ref == -1){\n");
    ret.append("      *exception = -1;\n");
    ret.append("      return -1;\n");
    ret.append("    }\n");
    ret.append("    "+address_qual+" char * dest_deref = edu_syr_pcpratts_gc_deref(gc_info, new_ref);\n");
    ret.append("    edu_syr_pcpratts_gc_memcpy(dest_deref, src_deref, size);\n");
    ret.append("    edu_syr_pcpratts_gc_set_ctor_used(dest_deref, 1);\n");
    ret.append("    return new_ref;\n");
    ret.append("  }\n");
  }
}