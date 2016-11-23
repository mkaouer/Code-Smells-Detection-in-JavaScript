package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class PairHmmJimpleRunOnGpu implements Kernel {

  private double m_result;
  private PairHMM pairHMM;
  
  public PairHmmJimpleRunOnGpu(){
    pairHMM = new PairHMM();
  }
  
  public void gpuMethod() {
    byte[] haplotype_bases = new byte[20];
    byte[] read_bases = new byte[20];
    byte[] read_quals = new byte[20];
    byte[] insertion_gop = new byte[20];
    byte[] deletion_gop = new byte[20];
    byte[] overall_gcp = new byte[20];
    haplotype_bases[0] = 1;
    read_quals[0] = 10;
    int hap_start_index = 50;
    boolean recache_read_value = true;
    m_result = hmm(haplotype_bases, read_bases, read_quals, insertion_gop, 
      deletion_gop, overall_gcp, hap_start_index, recache_read_value);
  }
  
  public double hmm(final byte[] haplotypeBases, final byte[] readBases, final byte[] readQuals, final byte[] insertionGOP,
                             final byte[] deletionGOP, final byte[] overallGCP,
                             final int hapStartIndex, final boolean recacheReadValues) {
  
    for (int i = 0; i < readQuals.length; i++) {
      readQuals[i] = (readQuals[i] < PairHmmQualityUtils.MIN_USABLE_Q_SCORE ? PairHmmQualityUtils.MIN_USABLE_Q_SCORE : (readQuals[i] > Byte.MAX_VALUE ? Byte.MAX_VALUE : readQuals[i]));
    }
 
    double ret = pairHMM.computeReadLikelihoodGivenHaplotypeLog10( haplotypeBases, readBases, readQuals, insertionGOP, deletionGOP, overallGCP, hapStartIndex, recacheReadValues);
    return ret;
  }

  public boolean compare(PairHmmJimpleRunOnGpu rhs) {
    if(m_result != rhs.m_result){
      System.out.println("m_result");
      System.out.println("lhs: "+m_result);
      System.out.println("rhs: "+rhs.m_result);
      return false;
    }
    return true;
  }
}
