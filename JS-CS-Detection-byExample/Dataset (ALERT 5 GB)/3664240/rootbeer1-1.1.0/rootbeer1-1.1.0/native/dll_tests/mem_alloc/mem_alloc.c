/*
* This is a test program that was born out of the discusion at https://github.com/pcpratts/rootbeer1/issues/25
*
* All it does is try and allocate to the host and GPU memory and outputs whether that worked or not
*/

#include <stdio.h>
#include <cuda.h>

int main(int argc, char *argv[])
{
	char c;
	CUcontext ctx;
	CUdevice dev = 0;
	void *toSpace;
	int status, free, total;
	CUdeviceptr ptr = (CUdeviceptr)NULL;
	int size;
	
	if(argc != 2){
		fprintf(stderr,"Usage: mem_alloc.exe [MEMORY TO ALLOCATE IN MB]\n");
		exit(1);
	}
	
	printf("All status results should be 0, if not an error has occured.\nIf 2 is reported an out of memory error has occured for\nwhich you should decrease the memory input\n");
	size = atoi(argv[1]);
	
	printf("\nTrying to allocate %iMB of memory on host and GPU\n",size);
	
	if(size <= 0){
		fprintf(stderr,"\nERROR: Memory must be greater than 0\n");
		exit(1);
	}
	
	status = cuInit(0);
	printf("Init status: %i\n",status); 

	status = cuCtxCreate(&ctx, 0, dev);
	printf("Context creation status: %i\n",status); 
	
	cuMemGetInfo(&free, &total);
	printf("Get memory info status: %i\n",status); 
	
	printf("\n%.1f/%.1f (Free/Total) MB\n", free/1024.0/1024.0, total/1024.0/1024.0);
	
	status = cuMemHostAlloc(&toSpace, size*1024*1024, 0); 
	printf("Host allocation status: %i %s\n",status, (status==CUDA_SUCCESS) ? "SUCCESS" : "FAILED"); 

	status = cuMemAlloc(&ptr, size*1024*1024);
	printf("GPU allocation status: %i %s\n",status, (status==CUDA_SUCCESS) ? "SUCCESS" : "FAILED");

	printf("\nPress any key to exit...");
	scanf("%c", &c);
	
	status = cuCtxDestroy(ctx);
	printf("Context destroy status: %i\n",status); 

	return 0;
}