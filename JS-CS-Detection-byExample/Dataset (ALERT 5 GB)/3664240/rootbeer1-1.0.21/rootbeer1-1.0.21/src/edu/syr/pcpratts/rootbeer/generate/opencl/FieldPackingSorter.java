/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldPackingSorter {

  public List<OpenCLField> sort(List<OpenCLField> input) {

    SortableField[] fields = new SortableField[input.size()];
    for(int i = 0; i < input.size(); ++i){
      fields[i] = new SortableField(input.get(i));
    }

    Arrays.sort(fields);

    List<OpenCLField> ret = new ArrayList<OpenCLField>();
    for(int i = 0; i < fields.length; ++i){
      ret.add(fields[i].getField());
    }
    return ret;

  }

  private class SortableField implements Comparable<SortableField> {
    private OpenCLField m_Field;

    public SortableField(OpenCLField field){
      m_Field = field;
    }

    public int compareTo(SortableField o) {
      Integer this_size = m_Field.getSize();
      Integer other_size = o.m_Field.getSize();

      //sorting from highest to lowest
      int ret = other_size.compareTo(this_size);
      if(ret == 0){
        return m_Field.getName().compareTo(o.m_Field.getName());
      } else {
        return ret;
      }
    }

    public OpenCLField getField(){
      return m_Field;
    }
  }
}
