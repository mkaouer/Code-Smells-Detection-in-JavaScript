package org.trifort.rootbeer.runtime;

public enum CacheConfig {
  /**
   * default
   */
  PREFER_NONE,
  
  /**
   * prefer more shared memory
   */
  PREFER_SHARED,
  
  /**
   * prefer more L1 cache
   */
  PREFER_L1,
  
  /**
   * prefer equal shared memory and L1 cache
   */
  PREFER_EQUAL
}