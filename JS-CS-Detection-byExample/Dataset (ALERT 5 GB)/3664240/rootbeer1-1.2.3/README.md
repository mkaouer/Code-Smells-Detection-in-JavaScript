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
        //create a context with 4212880 bytes objectMemory.
        //you can leave the 4212880 missing at first to
        //use all available GPU memory. after you run you
        //can call context0.getRequiredMemory() to see
        //what value to enter here
        Context context0 = device0.createContext(4212880);
        //use more die area for shared memory instead of
        //cache. the shared memory is a software defined
        //cache that, if programmed properly, can perform
        //better than the hardware cache
        //see (CUDA Occupancy calculator)[http://developer.download.nvidia.com/compute/cuda/CUDA_Occupancy_calculator.xls]
        context0.setCacheConfig(CacheConfig.PREFER_SHARED);
        //wire thread config for throughput mode. after
        //calling buildState, the book-keeping information
        //will be cached in the JNI driver
        context0.setThreadConfig(sizeBy2, outerCount, outerCount * sizeBy2);
        //configure to use kernel templates. rather than
        //using kernel lists where each thread has a Kernel
        //object, there is only one kernel object (less memory copies)
        //when using kernel templates you need to differetiate
        //your data using thread/block indexes
        context0.setKernel(new GPUSortKernel(array));
        //cache the state and get ready for throughput mode
        context0.buildState();

        while(true){
          //randomize the array to be sorted
          for(int i = 0; i < outerCount; ++i){
            fisherYates(array[i]);
          }
          long gpuStart = System.currentTimeMillis();
          //run the cached throughput mode state.
          //the data now reachable from the only
          //GPUSortKernel is serialized to the GPU
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
* `-computecapability` = specify the Compute Capability {sm_11,sm_12,sm_20,sm_21,sm_30,sm_35} (default ALL)

Once you get started, you will find you want to use a combination of -maxregcount, -shared-mem-size and the thread count sent to the GPU to control occupancy.


### Debugging

You can use System.out.println in a limited way while on the GPU. Printing in Java requires StringBuilder support to concatenate strings/integers/etc. Rootbeer has a custom StringBuilder runtime (written with great improvements from Martin Illecker) that allows most normal printlns to work.

Since you are running on a parallel GPU, it is nice to print from a single thread

    public void gpuMethod(){
      if(RootbeerGpu.getThreadIdxx() == 0 && RootbeerGpu.getBlockIdxx() == 0){
        System.out.println("hello world");
      }
    }

Once you are done debugging, you can get a performance improvement by disabling exceptions and array bounds checks (see command line options).

### Multi-GPUs (untested)

    List<GpuDevice> devices = rootbeer.getDevices();
    GpuDevice device0 = devices.get(0);
    GpuDevice device1 = devices.get(1);

    Context context0 = device0.createContext(4212880);
    Context context1 = device1.createContext(4212880);

    context0.setCacheConfig(CacheConfig.PREFER_SHARED);
    context`.setCacheConfig(CacheConfig.PREFER_SHARED);

    context0.setThreadConfig(sizeBy2, outerCount, outerCount * sizeBy2);
    context1.setThreadConfig(sizeBy2, outerCount, outerCount * sizeBy2);

    context0.setKernel(new GPUSortKernel(array0));
    context1.setKernel(new GPUSortKernel(array1));

    context0.buildState();
    context1.buildState();

    while(true){
      //run using two gpus without blocking the current thread
      GpuFuture future0 = context0.runAsync();
      GpuFuture future1 = context1.runAsync();
      future1.take();
      future2.take();
    }

### RootbeerGpu Builtins (compiles directly to CUDA statements)

    public class RootbeerGpu (){
        //returns true if on the gpu
        public static boolean isOnGpu();

        //returns blockIdx.x * blockDim.x + threadIdx.x
        public static int getThreadId();

        //returns threadIdx.x
        public static int getThreadIdxx();

        //returns blockIdx.x
        public static int getBlockIdxx();

        //returns blockDim.x
        public static int getBlockDimx();

        //returns gridDim.x;
        public static long getGridDimx();

        //__syncthreads
        public static void syncthreads();

        //__threadfence
        public static void threadfence();

        //__threadfence_block
        public static void threadfenceBlock();

        //__threadfence_system
        public static void threadfenceSystem();

        //given an object, returns the long handle
        //in GPU memory
        public static long getRef(Object obj);

        //get/set byte in shared memory. requires 1 byte.
        //index is byte offset into shared memory
        public static byte getSharedByte(int index);
        public static void setSharedByte(int index, byte value);

        //get/set char in shared memory. requires 2 bytes.
        //index is byte offset into shared memory
        public static char getSharedChar(int index);
        public static void setSharedChar(int index, char value);

        //get/set boolean in shared memory. requires 1 byte.
        //index is byte offset into shared memory
        public static boolean getSharedBoolean(int index);
        public static void setSharedBoolean(int index, boolean value);

        //get/set short in shared memory. requires 2 bytes.
        //index is byte offset into shared memory
        public static short getSharedShort(int index);
        public static void setSharedShort(int index, short value);

        //get/set integer in shared memory. requires 4 bytes.
        //index is byte offset into shared memory
        public static int getSharedInteger(int index);
        public static void setSharedInteger(int index, int value);

        //get/set long in shared memory. requires 8 bytes.
        //index is byte offset into shared memory
        public static long getSharedLong(int index);
        public static void setSharedLong(int index, long value);

        //get/set float in shared memory. requires 4 bytes.
        //index is byte offset into shared memory
        public static float getSharedFloat(int index);
        public static void setSharedFloat(int index, float value);

        //get/set double in shared memory. requires 8 bytes.
        //index is byte offset into shared memory
        public static double getSharedDouble(int index);
        public static void setSharedDouble(int index, double value);

        //atomic add value to array at index
        public static void atomicAddGlobal(int[] array, int index, int value);
        public static void atomicAddGlobal(long[] array, int index, long value);
        public static void atomicAddGlobal(float[] array, int index, float value);

        //atomic sub value from array at index
        public static void atomicSubGlobal(int[] array, int index, int value);

        //atomic exch value at index in array. old is retured
        public static int atomicExchGlobal(int[] array, int index, int value);
        public static long atomicExchGlobal(long[] array, int index, long value);
        public static float atomicExchGlobal(float[] array, int index, float value);

        //from CUDA programming guide: "reads the 32-bit word old located at the
        //address address in global memory, computes the minimum of old and val,
        //and stores the result back to memory at the same address.
        //These three operations are performed in one atomic transaction.
        //The function returns old."
        public static int atomicMinGlobal(int[] array, int index, int value);

        //from CUDA programming guide: "reads the 32-bit word old located at the
        //address address in global memory, computes the maximum of old and val,
        //and stores the result back to memory at the same address.
        //These three operations are performed in one atomic transaction.
        //The function returns old."
        public static int atomicMaxGlobal(int[] array, int index, int value);

        //from CUDA programming guide: "reads the 32-bit word old located at the
        //address address in global memory, computes (old == compare ? val : old),
        //and stores the result back to memory at the same address.
        //These three operations are performed in one atomic transaction. The function
        //returns old (Compare And Swap)."
        public static int atomicCASGlobal(int[] array, int index, int compare, int value);

        //from CUDA programming guide: "reads the 32-bit word old located at the
        //address address in global memory, computes (old & val), and stores the
        //result back to memory at the same address.
        //These three operations are performed in one atomic transaction.
        //The function returns old."
        public static int atomicAndGlobal(int[] array, int index, int value);

        //from CUDA programming guide: "reads the 32-bit word old located at the
        //address address in global memory, computes (old | val), and stores the
        //result back to memory at the same address.
        //These three operations are performed in one atomic transaction.
        //The function returns old."
        public static int atomicOrGlobal(int[] array, int index, int value);

        //from CUDA programming guide: "reads the 32-bit word old located at the
        //address address in global memory, computes (old ^ val), and stores the
        //result back to memory at the same address.
        //These three operations are performed in one atomic transaction.
        //The function returns old."
        public static int atomicXorGlobal(int[] array, int index, int value);
    }

### Viewing Code Generation

CUDA code is generated and placed in ~/.rootbeer/generated.cu  

You can use this to find out the register / shared memory usage

    $/usr/local/cuda/bin/nvcc --ptxas-options=-v -arch sm_20 ~/.rootbeer/generated.cu

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
