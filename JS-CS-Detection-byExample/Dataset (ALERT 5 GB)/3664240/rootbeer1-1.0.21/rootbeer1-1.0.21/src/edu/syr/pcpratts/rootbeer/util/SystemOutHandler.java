package edu.syr.pcpratts.rootbeer.util;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class SystemOutHandler extends Handler {

  @Override
  public void publish(LogRecord record) {
    System.out.println(record.getMessage());
  }

  @Override
  public void flush() {
    System.out.flush();
  }

  @Override
  public void close() throws SecurityException {
  }
  
}
