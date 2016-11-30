#Rootbeer

The Rootbeer GPU Compiler lets you use GPUs from within Java. It allows you to use almost anything from Java on the GPU:

  1. Composite objects with methods and fields
  2. Static and instance methods and fields
  3. Arrays of primitive and reference types of any dimension.

Be aware that you should not expect to get a speedup using a GPU by doing something simple
like multiplying each element in an array by a scalar. Serialization time is a large bottleneck
and usually you need an algorithm that is O(n^2) to O(n^3).

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

<b>ScalarMult:</b>

    import org.trifort.rootbeer.runtime.Rootbeer;
    import org.trifort.rootbeer.runtime.Kernel;
    
    public class ScalarMult {
      public void test(){
        List<Kernel> kernels = new ArrayList<Kernel>();
          for(int i = 0; i < 10; ++i){
            kernels.add(new ScalarMultKernel(i));
          }
          Rootbeer rootbeer = new Rootbeer();
          rootbeer.run(kernels);
          for(int i = 0; i < 10; ++i){
            ScalarMultKernel kernel = kernels.get(i);
            System.out.println(kernel.getValue());
          }
        }
      }
    }
    
<b>ScalarMultKernel:</b>

    import org.trifort.rootbeer.runtime.Kernel;
    
    public class ScalarMultKernel implements Kernel {
      private int m_value;
        
      public ScalarMultKernel(int value){
        m_value = value;
      }
        
      public void gpuMethod(){
        m_value++;
      }
        
      public int getValue(){
        return m_value;
      }
    }

### MultiGPU Example
See the [example](https://github.com/pcpratts/rootbeer1/tree/master/examples/MultiGpu)

    public void multArray(int[] array1, int[] array2){
      List<Kernel> work0 = new ArrayList<Kernel>();
      for(int i = 0; i < array1.length; ++i){
        work0.add(new ArrayMult(array1, i));
      }
      List<Kernel> work1 = new ArrayList<Kernel>();
      for(int i = 0; i < array2.length; ++i){
        work1.add(new ArrayMult(array2, i));
      }

      //////////////////////////////////////////////////////
      //create the Rootbeer runtime and query the devices
      //////////////////////////////////////////////////////
      Rootbeer rootbeer = new Rootbeer();
      List<GpuDevice> devices = rootbeer.getDevices();

      if(devices.size() >= 2){
        ///////////////////////////////////////////////////
        //get two devices and create two contexts. memory
        //  allocations and launches are per context. you
        //  can pass in the memory size you would like to
        //  use or don't and Rootbeer will use all the gpu
        //  memory
        ///////////////////////////////////////////////////
        GpuDevice device0 = devices.get(0);
        GpuDevice device1 = devices.get(1); 
        Context context0 = device0.createContext(4096);
        Context context1 = device1.createContext(4096);

        ///////////////////////////////////////////////////
        //run the work on the two contexts
        ///////////////////////////////////////////////////
        rootbeer.run(work0, context0);
        rootbeer.run(work1, context1);
      } 
    }

### Shared Memory Example
See the [example](https://github.com/pcpratts/rootbeer1/tree/master/gtc2013/Matrix)

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
5. Use the `rootbeer1/Rootbeer.jar` (not `dist/Rootbeer1.jar`)

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

### Author

Phil Pratt-Szeliga  
http://trifort.org/
