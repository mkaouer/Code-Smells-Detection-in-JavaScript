package org.trifort.rootbeer.runtime;

import java.util.List;

import com.lmax.disruptor.EventFactory;

public class GpuEvent {
  private GpuEventCommand value;
  private List<Kernel> work;
  final private GpuFuture future;
  
  public GpuEvent(){
    future = new GpuFuture();
  }

  public GpuEventCommand getValue() {
    return value;
  }
  
  public GpuFuture getFuture(){
    return future;
  }

  public void setValue(GpuEventCommand value) {
    this.value = value;
  }

  public void setKernelList(List<Kernel> work) {
    this.work = work;
  }
  
  public List<Kernel> getKernelList(){
    return work;
  }

  public final static EventFactory<GpuEvent> EVENT_FACTORY = new EventFactory<GpuEvent>() {
    public GpuEvent newInstance() {
      return new GpuEvent();
    }
  };
}
