/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.ofcoarse;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.ArrayList;
import java.util.List;

public class OfCoarse implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 32; ++i){
      CoarseningPhysics physics = new CoarseningPhysics();
      VariableAgeCutoff cutoff = new VariableAgeCutoff(1600, 1200, 100);
      cutoff.ageReplicate(10);
      physics.setParameters(2.8, 1.0, 800, 10, cutoff);
      GpuList<Droplet> prev = createDroplets();
      GpuWorkItem curr = new GpuWorkItem(i, physics, prev);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    GpuWorkItem glhs = (GpuWorkItem) lhs;
    GpuWorkItem grhs = (GpuWorkItem) rhs;
    return glhs.compare(grhs);
  }

  private GpuList<Droplet> createDroplets() {
    GpuList<Droplet> ret = new GpuList<Droplet>();
    for(int i = 0; i < 113; ++i){
      Droplet curr = new Droplet(new Point(i*50, i*50), 30);
      ret.add(curr);
    }
    return ret;
  }
}
