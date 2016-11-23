/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.trifort.rootbeer.generate.opencl.tweaks.Tweaks;

public class ArrayCopyGenerate {
  
  private List<OpenCLArrayType> m_ArrayTypes;

  public String get(Set<OpenCLArrayType> array_types_set){
    createSet(array_types_set);
    StringBuilder ret = new StringBuilder();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    ret.append(getDecl()+"{\n");
    ret.append("  int i;\n");
    ret.append("  int src_index;\n");
    ret.append("  int dest_index;\n");
    ret.append("  "+address_qual+" char * src_deref = org_trifort_gc_deref(gc_info, src_handle);\n");
    ret.append("  "+address_qual+" char * dest_deref = org_trifort_gc_deref(gc_info, dest_handle);\n");
    ret.append("  \n");
    ret.append("  GC_OBJ_TYPE_TYPE src_type = org_trifort_gc_get_type(src_deref);\n");
    ret.append("  GC_OBJ_TYPE_TYPE dest_type = org_trifort_gc_get_type(dest_deref);\n");
    ret.append("  \n");
    ret.append("  if(srcPos < destPos){\n");
    ret.append("      if(0){}\n");
    ret.append(generateForList());
    ret.append("  } else {\n");
    ret.append("      if(0){}\n");    
    ret.append(generateForList());
    ret.append("  }\n");
    ret.append("}\n");
    return ret.toString();
  }
  
  private String generateForList(){
    StringBuilder ret = new StringBuilder();
    for(int i = 0; i < m_ArrayTypes.size(); ++i){
      for(int j = 0; j < m_ArrayTypes.size(); ++j){
        OpenCLArrayType src = m_ArrayTypes.get(i);
        OpenCLArrayType dest = m_ArrayTypes.get(j);
        ret.append("      else if(src_type == "+src.getTypeInteger()+" && dest_type == "+dest.getTypeInteger()+"){\n");
        ret.append("        for(i = length - 1; i >= 0; --i){\n");
        ret.append("          src_index = srcPos + i;\n");
        ret.append("          dest_index = destPos + i;\n");
        ret.append("        "+dest.getDerefTypeString()+"_set(gc_info, dest_handle, dest_index, ");
        ret.append(src.getDerefTypeString()+"_get(gc_info, src_handle, src_index, exception), exception);\n");
        ret.append("        }\n");
        ret.append("      }\n");
      }
    }
    return ret.toString();
  }

  private void createSet(Set<OpenCLArrayType> array_types_set) {
    m_ArrayTypes = new ArrayList<OpenCLArrayType>();
    Iterator<OpenCLArrayType> iter = array_types_set.iterator();
    while(iter.hasNext()){
      m_ArrayTypes.add(iter.next());
    }
  }
  
  private String getDecl(){
    String device_function_qual = Tweaks.v().getDeviceFunctionQualifier();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    StringBuilder ret = new StringBuilder();
    ret.append(device_function_qual+" void \n");
    ret.append("java_lang_System_arraycopy("+address_qual+" char * gc_info, ");
    ret.append("int src_handle, int srcPos, int dest_handle, int destPos, int length, int * exception)"); 
    return ret.toString();
  }

  public String getProto() {
    return getDecl()+";\n";
  }
}
