package org.trifort.rootbeer.examples.multigpu;

import java.util.List;
import java.util.ArrayList;
import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.Rootbeer;
import org.trifort.rootbeer.runtime.GpuDevice;
import org.trifort.rootbeer.runtime.Context;
import edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch;

public class MultiGpuApp {

  public void multArray(int[] array1, int[] array2){
    List<Kernel> work0 = new ArrayList<Kernel>();
    for(int i = 0; i < array1.length; ++i){
      work0.add(new ArrayMult(array1, i));
    }
    List<Kernel> work1 = new ArrayList<Kernel>();
    for(int i = 0; i < array2.length; ++i){
      work1.add(new ArrayMult(array2, i));
    }

    Rootbeer rootbeer = new Rootbeer();
    List<GpuDevice> devices = rootbeer.getDevices();
    if(devices.size() >= 2){
      System.out.println("device count: "+devices.size());
      for(GpuDevice device : devices){
        System.out.println("  name: "+device.getDeviceName());
      }
      Stopwatch watch = new Stopwatch();
      watch.start();
      GpuDevice device0 = devices.get(0);
      GpuDevice device1 = devices.get(1); 
      Context context0 = device0.createContext(4096);
      Context context1 = device1.createContext(4096);

      rootbeer.run(work0, context0);
      rootbeer.run(work1, context1);
      watch.stop();
      System.out.println("time: "+watch.elapsedTimeMillis());
    } else {
      System.out.println("This example needs two gpu devices");
      System.out.println("device count: "+devices.size());
      for(GpuDevice device : devices){
        System.out.println("  name: "+device.getDeviceName());
      }
    }
    
  }
  
  public static void main(String[] args){
    MultiGpuApp app = new MultiGpuApp();
    int[] array1 = new int[5];
    int[] array2 = new int[5];
    for(int i = 0; i < array1.length; ++i){
      array1[i] = i;
      array2[i] = i;
    }
    for(int i = 0; i < array1.length; ++i){
      System.out.println("start arrays["+i+"]: "+array1[i]+" "+array2[i]);
    }
    
    app.multArray(array1, array2);
    for(int i = 0; i < array1.length; ++i){
      System.out.println("final arrays["+i+"]: "+array1[i]+" "+array2[i]);
    }
  }
}
