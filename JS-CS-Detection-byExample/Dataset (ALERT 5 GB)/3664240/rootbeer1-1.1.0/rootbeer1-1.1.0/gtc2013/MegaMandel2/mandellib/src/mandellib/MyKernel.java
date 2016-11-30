/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mandellib;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu;

/**
 *
 * @author thorsten
 */
public class MyKernel implements Kernel {

    public int[] result;
    public int maxdepth;
    public int w;
    public int h;
    public double maxx, minx;
    public double maxy, miny;
    public int offset;

    public MyKernel(int[] result, int maxdepth, int w, int h, double maxx, double minx, double maxy, double miny,int offset) {
        this.result = result;
        this.maxdepth = maxdepth;
        this.w = w;
        this.h = h;
        this.maxx = maxx;
        this.minx = minx;
        this.maxy = maxy;
        this.miny = miny;
        this.offset = offset;
    }

    @Override
    public void gpuMethod() {
        double xr = 0;
        double xi = 0;
        int id = RootbeerGpu.getBlockIdxx() * RootbeerGpu.getBlockDimx() + RootbeerGpu.getThreadIdxx();
        int i = id % w;
        int j = id / w;
        if (i >= w || j >= h) {
            return;
        }
        double cr = (maxx - minx) * i / w + minx;
        double ci = (maxy - miny) * j / h + miny;
        int d = 0;
        while (true) {
            double xr2 = xr * xr - xi * xi + cr;
            double xi2 = 2.0f * xr * xi + ci;
            xr = xr2;
            xi = xi2;
            d++;
            if (d >= maxdepth) {
                break;
            }
            if (xr * xr + xi * xi >= 4) {
                break;
            }
        }
        //int r = (int) (0xff * (RootbeerGpu.sin((double) (0.01 * d + 0) + 1)) / 2);
        //int g = (int) (0xff * (RootbeerGpu.sin((double) (0.02 * d + 0.01) + 1)) / 2);
        //int b = (int) (0xff * (RootbeerGpu.sin((double) (0.04 * d + 0.1) + 1)) / 2);
        int dest_index = j * w + i + offset;

        //result[dest_index] = r;  
        //result[dest_index] = g;
        //result[dest_index] = b;      
        result[dest_index] =
                (int) ((0xff * (0.01 * d + 0) + 1) / 2) << 16
                | (int) ((0xff * (0.02 * d + 0.01) + 1) / 2) << 8
                | (int) ((0xff * (0.04 * d + 0.1) + 1) / 2);
    }
}
