/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */
package org.trifort.rootbeer.runtime2.cuda;

/**
 * An exception object that the cudaruntime.dll throws if it can't allocate
 * enough memory or some error is thrown.
 * 
 * @author Daniel Brown
 */
public class CudaErrorException extends RuntimeException{
    /** Enum value of error */
    private int cudaError_enum;
    
    /**
     * Returns the cudaError_enum value that caused the exception
     */
    public int getError(){
        return cudaError_enum;
    }
    
    public CudaErrorException(String message) {
        super(message);
    }
}
