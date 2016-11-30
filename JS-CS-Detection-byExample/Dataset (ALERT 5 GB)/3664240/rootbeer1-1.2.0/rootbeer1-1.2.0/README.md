#Rootbeer

The Rootbeer GPU Compiler lets you use GPUs from within Java. It allows you to use almost anything from Java on the GPU:

  1. Composite objects with methods and fields
  2. Static and instance methods and fields
  3. Arrays of primitive and reference types of any dimension.

ROOTBEER IS PRE-PRODUCTION BETA. IF ROOTBEER WORKS FOR YOU, PLEASE LET ME KNOW AT PCPRATTS@TRIFORT.ORG

Be aware that you should not expect to get a speedup using a GPU by doing something simple
like multiplying each element in an array by a scalar. Serialization time is a large bottleneck
and usually you need an algorithm that is O(n^2) to O(n^3) per O(n) elements of data.

GPU PROGRAMMING IS NOT FOR THE FAINT OF HEART, EVEN WITH ROOTBEER. EXPECT TO SPEND A MONTH OPTIMIZING TRIVIAL EXAMPLES.

FEEL FREE TO EMAIL ME FOR DISCUSSIONS BEFORE ATTEMPTING TO USE ROOTBEER

An experienced GPU developer will look at existing code and find places where control can
be transfered to the GPU. Optimal performance in an application will have places with serial
code and places with parallel code on the GPU. At each place that a cut can be made to transfer
control to the GPU, the job needs to be sized for the GPU.

For the best performance, you should be using shared memory (NVIDIA term). The shared memory is
basically a software managed cache. You want to have more threads per block, but this often
requires using more shared memory. If you see the [CUDA Occupancy Calculator](http://developer.download.nvidia.com/compute/cuda/CUDA_Occupancy_calculator.xls) you can see
that for best occupancy you will want more threads and less shared memory. There is a tradeoff
between thread count, shared memory size and register count. All of these are configurable
using Rootbeer.

## Programming  
<b>Kernel Interface:</b> Your code that will run on the GPU will implement the Kernel interface.
You send data to the gpu by adding a field to the object implementing kernel. `gpuMethod` will access the data.

    package org.trifort.rootbeer.runtime;

    public interface Kernel {
      void gpuMethod();
    }

###Simple Example:
This simple example uses kernel lists and no thread config or context. Rootbeer will create a thread config and select the best device automatically. If you wish to use multiple GPUs you need to pass in a Context.

<b>ScalarAddApp.java:</b>  
See the [example](https://github.com/pcpratts/rootbeer1/tree/master/examples/ScalarAddApp)

    package org.trifort.rootbeer.examples.scalaradd;

    import java.util.List;
    import java.util.ArrayList;
    import org.trifort.rootbeer.runtime.Kernel;
    import org.trifort.rootbeer.runtime.Rootbeer;
    import org.trifort.rootbeer.runtime.util.Stopwatch;

    public class ScalarAddApp {

      public void multArray(int[] array){
        List<Kernel> tasks = new ArrayList<Kernel>();
        for(int index = 0; index < array.length; ++index){
          tasks.add(new ScalarAddKernel(array, index));
        }

        Rootbeer rootbeer = new Rootbeer();
        rootbeer.run(tasks);
      }

      private void printArray(String message, int[] array){
        for(int i = 0; i < array.length; ++i){
          System.out.println(message+" array["+i+"]: "+array[i]);
        }
      }

      public static void main(String[] args){
        ScalarAddApp app = new ScalarAddApp();
        int length = 10;
        int[] array = new int[length];
        for(int index = 0; index < array.length; ++index){
          array[index] = index;
        }

        app.printArray("start", array);
        app.multArray(array);
        app.printArray("end", array);
      }
    }


<b>ScalarAddKernel:</b>

    package org.trifort.rootbeer.examples.scalaradd;

    import org.trifort.rootbeer.runtime.Kernel;

    public class ScalarAddKernel implements Kernel {

      private int[] array;
      private int index;

      public ScalarAddKernel(int[] array, int index){
        this.array = array;
        this.index = index;
      }

      public void gpuMethod(){
        array[index] += 1;
      }
    }

## Results
start array[0]: 0  
start array[1]: 1  
start array[2]: 2  
start array[3]: 3  
start array[4]: 4  
start array[5]: 5  
start array[6]: 6  
start array[7]: 7  
start array[8]: 8  
start array[9]: 9  
end array[0]: 1  
end array[1]: 2  
end array[2]: 3  
end array[3]: 4  
end array[4]: 5  
end array[5]: 6  
end array[6]: 7  
end array[7]: 8  
end array[8]: 9  
end array[9]: 10  

### High Performance Example - Batcher's Even Odd Sort
See the [example](https://github.com/pcpratts/rootbeer1/tree/master/examples/sort)  
See the [slides](http://trifort.org/ads/index.php/lecture/index/27/)  

<b>GPUSort.java</b>  

    package org.trifort.rootbeer.sort;

    import org.trifort.rootbeer.runtime.Rootbeer;
    import org.trifort.rootbeer.runtime.GpuDevice;
    import org.trifort.rootbeer.runtime.Context;
    import org.trifort.rootbeer.runtime.ThreadConfig;
    import org.trifort.rootbeer.runtime.StatsRow;
    import org.trifort.rootbeer.runtime.CacheConfig;
    import java.util.List;
    import java.util.Arrays;
    import java.util.Random;

    public class GPUSort {

      private int[] newArray(int size){
        int[] ret = new int[size];

        for(int i = 0; i < size; ++i){
          ret[i] = i;
        }
        return ret;
      }

      public void checkSorted(int[] array, int outerIndex){
        for(int index = 0; index < array.length; ++index){
          if(array[index] != index){
            for(int index2 = 0; index2 < array.length; ++index2){
              System.out.println("array["+index2+"]: "+array[index2]);
            }
            throw new RuntimeException("not sorted: "+outerIndex);
          }
        }
      }

      public void fisherYates(int[] array)
      {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--){
          int index = random.nextInt(i + 1);
          int a = array[index];
          array[index] = array[i];
          array[i] = a;
        }
      }

      public void sort(){
        //should have at least 192 threads per SM
        int size = 2048;
        int sizeBy2 = size / 2;
        //int numMultiProcessors = 14;
        //int blocksPerMultiProcessor = 512;
        int numMultiProcessors = 2;
        int blocksPerMultiProcessor = 256;
        int outerCount = numMultiProcessors*blocksPerMultiProcessor;
        int[][] array = new int[outerCount][];
        for(int i = 0; i < outerCount; ++i){
          array[i] = newArray(size);
        }

        Rootbeer rootbeer = new Rootbeer();
        List<GpuDevice> devices = rootbeer.getDevices();
        GpuDevice device0 = devices.get(0);
        Context context0 = device0.createContext(4212880);
        context0.setCacheConfig(CacheConfig.PREFER_SHARED);
        context0.setThreadConfig(sizeBy2, outerCount, outerCount * sizeBy2);
        context0.setKernel(new GPUSortKernel(array));
        context0.buildState();

        while(true){
          for(int i = 0; i < outerCount; ++i){
            fisherYates(array[i]);
          }
          long gpuStart = System.currentTimeMillis();
          context0.run();
          long gpuStop = System.currentTimeMillis();
          long gpuTime = gpuStop - gpuStart;

          StatsRow row0 = context0.getStats();
          System.out.println("serialization_time: "+row0.getSerializationTime());
          System.out.println("execution_time: "+row0.getExecutionTime());
          System.out.println("deserialization_time: "+row0.getDeserializationTime());
          System.out.println("gpu_required_memory: "+context0.getRequiredMemory());
          System.out.println("gpu_time: "+gpuTime);

          for(int i = 0; i < outerCount; ++i){
            checkSorted(array[i], i);
            fisherYates(array[i]);
          }

          long cpuStart = System.currentTimeMillis();
          for(int i = 0; i < outerCount; ++i){
            Arrays.sort(array[i]);
          }
          long cpuStop = System.currentTimeMillis();
          long cpuTime = cpuStop - cpuStart;
          System.out.println("cpu_time: "+cpuTime);
          double ratio = (double) cpuTime / (double) gpuTime;
          System.out.println("ratio: "+ratio);
        }
        //context0.close();
      }

      public static void main(String[] args){
        GPUSort sorter = new GPUSort();
        while(true){
          sorter.sort();
        }
      }
    }

<b>GPUSortKernel.java</b>

    package org.trifort.rootbeer.sort;

    import org.trifort.rootbeer.runtime.Kernel;
    import org.trifort.rootbeer.runtime.RootbeerGpu;


    public class GPUSortKernel implements Kernel {

      private int[][] arrays;

      public GPUSortKernel(int[][] arrays){
        this.arrays = arrays;
      }

      @Override
      public void gpuMethod(){
        int[] array = arrays[RootbeerGpu.getBlockIdxx()];
        int index1a = RootbeerGpu.getThreadIdxx() << 1;
        int index1b = index1a + 1;
        int index2a = index1a - 1;
        int index2b = index1a;
        int index1a_shared = index1a << 2;
        int index1b_shared = index1b << 2;
        int index2a_shared = index2a << 2;
        int index2b_shared = index2b << 2;

        RootbeerGpu.setSharedInteger(index1a_shared, array[index1a]);
        RootbeerGpu.setSharedInteger(index1b_shared, array[index1b]);
        //outer pass
        int arrayLength = array.length >> 1;
        for(int i = 0; i < arrayLength; ++i){
          int value1 = RootbeerGpu.getSharedInteger(index1a_shared);
          int value2 = RootbeerGpu.getSharedInteger(index1b_shared);
          int shared_value = value1;
          if(value2 < value1){
            shared_value = value2;
            RootbeerGpu.setSharedInteger(index1a_shared, value2);
            RootbeerGpu.setSharedInteger(index1b_shared, value1);
          }
          RootbeerGpu.syncthreads();
          if(index2a >= 0){
            value1 = RootbeerGpu.getSharedInteger(index2a_shared);
            //value2 = RootbeerGpu.getSharedInteger(index2b_shared);
            value2 = shared_value;
            if(value2 < value1){
              RootbeerGpu.setSharedInteger(index2a_shared, value2);
              RootbeerGpu.setSharedInteger(index2b_shared, value1);
            }
          }
          RootbeerGpu.syncthreads();
        }
        array[index1a] = RootbeerGpu.getSharedInteger(index1a_shared);
        array[index1b] = RootbeerGpu.getSharedInteger(index1b_shared);
      }
    }


### Compiling Rootbeer Enabled Projects
1. Download the latest Rootbeer.jar from the releases
2. Program using the Kernel, Rootbeer, GpuDevice and Context class.
3. Compile your program normally with javac.
4. Pack all the classes used into a single jar using [pack](https://github.com/pcpratts/pack/)
5. Compile with Rootbeer to enable the GPU
   `java -Xmx8g -jar Rootbeer.jar App.jar App-GPU.jar`

### Building Rootbeer from Source

1. Clone the github repo to `rootbeer1/`
2. `cd rootbeer1/`
3. `ant jar`
4. `./pack-rootbeer` (linux) or `./pack-rootbeer.bat` (windows)
5. Use the `Rootbeer.jar` (not `dist/Rootbeer1.jar`)

### Command Line Options

* `-runeasytests` = run test suite to see if things are working
* `-runtest` = run specific test case
* `-printdeviceinfo` = print out information regarding your GPU
* `-maxrregcount` = sent to CUDA compiler to limit register count
* `-noarraychecks` = remove array out of bounds checks once you get your application to work
* `-nodoubles` = you are telling rootbeer that there are no doubles and we can compile with older versions of CUDA
* `-norecursion` = you are telling rootbeer that there are no recursions and we can compile with older versions of CUDA
* `-noexceptions` = remove exception checking
* `-keepmains` = keep main methods
* `-shared-mem-size` = specify the shared memory size
* `-32bit` = compile with 32bit
* `-64bit` = compile with 64bit (if you are on a 64bit machine you will want to use just this)

Once you get started, you will find you want to use a combination of -maxregcount, -shared-mem-size and the thread count sent to the GPU to control occupancy.

### CUDA Setup

You need to have the CUDA Toolkit and CUDA Driver installed to use Rootbeer.
Download it from http://www.nvidia.com/content/cuda/cuda-downloads.html

### License

Rootbeer is licensed under the MIT license. If you use rootbeer for any reason, please
star the repository and email me your usage and comments. I am preparing my dissertation
now.

### Examples

See [here](https://github.com/pcpratts/rootbeer1/tree/master/examples) for a variety of
examples.


### Consulting

GPU Consulting available for Rootbeer and CUDA. Please email pcpratts@trifort.org  

### Credit

Rootbeer was partially supported by both the National Science Foundation and
Syracuse University and God is Most High.

### Author

Phil Pratt-Szeliga  
http://trifort.org/
