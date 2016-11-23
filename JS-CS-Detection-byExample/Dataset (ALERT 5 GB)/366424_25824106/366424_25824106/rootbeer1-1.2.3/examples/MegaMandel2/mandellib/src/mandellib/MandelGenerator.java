/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mandellib;

import org.trifort.rootbeer.runtime.Rootbeer;
import org.trifort.rootbeer.runtime.util.Stopwatch;
import org.trifort.rootbeer.runtime.ThreadConfig;
import org.trifort.rootbeer.runtime.Context;

/**
 *
 * @author thorsten
 */
public class MandelGenerator {

  private static Rootbeer rootbeer;
  private static Stopwatch m_gpuWatch = new Stopwatch();
  private static final int numThreads = 10000;
  private static Context context;

  static {
    rootbeer = new Rootbeer();
    context = rootbeer.createDefaultContext();
    context.init(512*1024);
  }

  public static void gpuGenerate(int w, int h, double minx, double maxx, double miny, double maxy, int maxdepth, int[] pixels) {
    m_gpuWatch.start();
    if (h <= 10 || w <= 10) {
      return;
    }
    int h2 = numThreads / w;
    int y;
    for (y = 0; y < h - h2; y += h2) {
      ThreadConfig config = new ThreadConfig(100, 100, h2 * w);
      double miny2 = (maxy - miny) * y / h + miny;
      double maxy2 = (maxy - miny) * (y + h2) / h + miny;
      MyKernel myKernel = new MyKernel(pixels, maxdepth, w, h2, maxx, minx, maxy2, miny2, w * y);
      rootbeer.run(myKernel, config, context);
    }
    m_gpuWatch.stop();
    System.out.println("avg gpu: " + m_gpuWatch.elapsedTimeMillis());
  }
}
