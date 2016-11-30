## Rootbeer

The Rootbeer GPU Compiler makes it easy to use Graphics Processing Units from
within Java.

Rootbeer is more advanced than CUDA or OpenCL Java Language Bindings. With 
bindings the developer must serialize complex graphs of objects into arrays
of primitive types. With Rootbeer this is done automatically. Also with language
bindings, the developer must write the GPU kernel in CUDA or OpenCL. With
Rootbeer a static analysis of the Java Bytecode is done (using Soot) and CUDA
code is automatically generated.

See `doc/hpcc_rootbeer.pdf` for the conference slides from HPCC-2012.
See `doc/rootbeer1_paper.pdf` for the conference paper from HPCC-2012.

Rootbeer is licensed under the MIT license.

## Development notes

Rootbeer was created using Test Driven Development and testing is essentially
important in Rootbeer. Rootbeer is 20k lines of product code and 7k of test code
and all tests pass on both Windows and Linux. The Rootbeer test case suite 
covers every aspect of the Java Programming language except:
  1. native methods
  2. reflection
  3. dynamic method invocation
  4. sleeping while inside a monitor. 
  
This means that all of the familar Java code you have been writing can be
executed on the GPU

The original publication for Rootbeer was in HPCC-2012.<br />
  "Rootbeer: Seamlessly using GPUs from Java"<br />
  Philip C. Pratt-Szeliga, James W. Fawcett, Roy D. Welch.<br />
  To appear in HPCC-2012.

This work is supported by the National Science Foundation.

## Building

1. Clone the github repo to `rootbeer1/`
2. `cd rootbeer1/`
3. `ant`
4. `./pack-rootbeer` (linux) or `./pack-rootbeer.bat` (windows)
5. Use the `rootbeer1/Rootbeer.jar` (not `dist/Rootbeer1.jar`)

## CUDA Setup

You need to have the CUDA Toolkit and CUDA Driver installed to use Rootbeer.
Download it from http://www.nvidia.com/content/cuda/cuda-downloads.html

## Information about Rootbeer

Rootbeer announcements are pushed to @rootbeer_gpu

Rootbeer mailing list: [http://chirrup.org/pipermail/rootbeer-dev/](http://chirrup.org/pipermail/rootbeer-dev/)

## Clarifications

Rootbeer does NOT automatically parallelize code. It provides help serializing
state and writing CUDA code, but you still must specify what each GPU core
is going to do.

Phil Pratt-Szeliga is still a PhD student at Syracuse University. All the code
was written by Phil. Dr. Jim Fawcett and Dr. Roy Welch are Phil's PhD advisors.

## About

Rootbeer is written by:

Phil Pratt-Szeliga<br />
Syracuse University<br />
pcpratts@chirrup.org<br />
http://chirrup.org/
