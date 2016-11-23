package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import java.util.Comparator;

public class DescendingComparator implements Comparator<Integer>{

  @Override
  public int compare(Integer lhs, Integer rhs) {
    return rhs.compareTo(lhs);
  }
}
