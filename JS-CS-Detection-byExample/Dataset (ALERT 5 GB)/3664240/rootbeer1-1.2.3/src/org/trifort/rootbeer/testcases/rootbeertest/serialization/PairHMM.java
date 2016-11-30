package org.trifort.rootbeer.testcases.rootbeertest.serialization;

public class PairHMM {

  public double computeReadLikelihoodGivenHaplotypeLog10(byte[] haplotypeBases, byte[] readBases, byte[] readQuals, byte[] insertionGOP, byte[] deletionGOP, byte[] overallGCP, int hapStartIndex, boolean recacheReadValues) {
    return haplotypeBases[0] + readBases[0] + readQuals[0] + insertionGOP[0] + deletionGOP[0] + overallGCP[0];
  }
  
}
