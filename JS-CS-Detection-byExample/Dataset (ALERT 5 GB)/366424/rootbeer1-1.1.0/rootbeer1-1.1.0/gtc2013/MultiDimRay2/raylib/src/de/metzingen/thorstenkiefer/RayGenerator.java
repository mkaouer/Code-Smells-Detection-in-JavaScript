/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.metzingen.thorstenkiefer;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thorsten
 */
public class RayGenerator {

    private static Rootbeer rb = new Rootbeer();

    private static class MyThread extends Thread {

        public boolean compute = false;
        public MyKernel kernel = null;
        public int y;
        public int w;

        @Override
        public void run() {
            while (true) {
                while (!compute) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                }
                for (int x = 0; x < w; ++x) {
                    kernel.compute(x, y);
                }
                compute = false;
            }
        }
    }
    private static MyThread[] threads = new MyThread[16];

    public static void generateGPU(
            int w, int h, double minx, double maxx, double miny, double maxy,
            int[] pixels, double[][] spheres, double[] light, double[] observer,
            double radius, double[] vx, double[] vy, int numDimensions) {
        st.start();
        rb.setThreadConfig(w, h);
        MyKernel myKernel = new MyKernel(pixels, minx, maxx, miny, maxy,
                w, h, spheres, light, observer, vx, vy, radius,
                numDimensions);
        rb.runAll(myKernel);
        st.stop();
        System.out.println(st.elapsedTimeMillis());
        System.out.println(rb.getStats().size());
    }

    private static Stopwatch st = new Stopwatch();
    
    public static void generateCPU(
            int w, int h, double minx, double maxx, double miny, double maxy,
            int[] pixels, double[][] spheres, double[] light, double[] observer,
            double radius, double[] vx, double[] vy, int numDimensions) {
        st.start();
        
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new MyThread();
            threads[i].kernel = new MyKernel(pixels, minx, maxx, miny, maxy,
                    w, h, spheres, light, observer, vx, vy, radius,
                    numDimensions);
            threads[i].w = w;
            threads[i].start();
        }
                
        for (int y = 0; y < h; ++y) {
            boolean found = false;
            while (!found) {
                for (MyThread mt : threads) {
                    if (!mt.compute) {
                        found = true;
                        mt.y = y;
                        mt.compute = true;
                        mt.interrupt();
                        break;
                    }
                }
            }
        }
        st.stop();
        System.out.println(st.elapsedTimeMillis());
    }
}
