/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mandellib;

import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch;

/**
 *
 * @author thorsten
 */
public class MandelGenerator {

  private static Rootbeer rb = new Rootbeer();
  private static Stopwatch m_gpuWatch = new Stopwatch();
  private static final int numThreads = 10000;

  public static void gpuGenerate(int w, int h, double minx, double maxx, double miny, double maxy, int maxdepth, int[] pixels) {
    m_gpuWatch.start();
    if (h <= 10 || w <= 10) {
      return;
    }
    int h2 = numThreads / w;
    int y;
    for (y = 0; y < h - h2; y += h2) {
      rb.setThreadConfig(100, 100, h2 * w);
      double miny2 = (maxy - miny) * y / h + miny;
      double maxy2 = (maxy - miny) * (y + h2) / h + miny;
      MyKernel myKernel = new MyKernel(pixels, maxdepth, w, h2, maxx, minx, maxy2, miny2, w * y);
      rb.runAll(myKernel);
    }
    m_gpuWatch.stop();
    System.out.println("avg gpu: " + m_gpuWatch.elapsedTimeMillis());
  }
}
