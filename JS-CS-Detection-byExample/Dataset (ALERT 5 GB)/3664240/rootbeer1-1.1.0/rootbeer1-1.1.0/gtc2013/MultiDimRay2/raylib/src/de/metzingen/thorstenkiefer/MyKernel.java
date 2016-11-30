/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.metzingen.thorstenkiefer;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu;

/**
 *
 * @author thorsten
 */
public class MyKernel implements Kernel {

    private int[] result;
    private double minx;
    private double maxx;
    private double miny;
    private double maxy;
    private int w;
    private int h;
    private double[][] spheres;
    private double[] light;
    private double[] observer;
    private double[] sight_start;
    private double[] sight_dir;
    private double[][] regs;
    private double[] intersections = new double[100];
    private int numIntersections = 0;
    private double[][] intersectionso = new double[100][];
    private double[] vx;
    private double[] vy;
    private double radius;
    private int numDimensions;

    public MyKernel(int[] result, double minx, double maxx, double miny,
            double maxy, int w, int h, double[][] spheres, double[] light,
            double[] observer, double[] vx, double[] vy,
            double radius, int numDimensions) {
        this.result = result;
        this.minx = minx;
        this.maxx = maxx;
        this.miny = miny;
        this.maxy = maxy;
        this.w = w;
        this.h = h;
        this.spheres = spheres;
        this.light = light;
        this.observer = observer;
        this.radius = radius;
        this.vx = vx;
        this.vy = vy;
        this.numDimensions = numDimensions;

        regs = new double[5][numDimensions];
        sight_start = observer;
        sight_dir = new double[numDimensions];
    }

    public static void zeroOut(double[] xs) {
        for (int i = 0; i < 100; ++i) {
            xs[i] = 0;
        }
    }

    public void add(double[] a, double[] b, double[] result) {
        for (int i = 0; i < numDimensions; ++i) {
            result[i] = a[i] + b[i];
        }
    }

    public void sub(double[] a, double[] b, double[] result) {
        for (int i = 0; i < numDimensions; ++i) {
            result[i] = a[i] - b[i];
        }
    }

    public double mul(double[] a, double[] b) {
        double x = 0;
        for (int i = 0; i < numDimensions; ++i) {
            x += a[i] * b[i];
        }
        return x;
    }

    public void mul(double[] v, double lambda, double[] result) {
        for (int i = 0; i < numDimensions; ++i) {
            result[i] = v[i] * lambda;
        }
    }

    public void intersect(double[] start, double[] dir, double[][] spheres) {
        for (int i = 0; i < spheres.length; ++i) {
            intersect(start, dir, spheres[i]);
        }
    }

    public static double sqrt(double x) {
        double x0 = 0, x1 = x;

        for (int i = 0; i < 10; ++i) {
            double y = (x1 + x0) / 2;
            if (y * y < x) {
                x0 = y;
            } else {
                x1 = y;
            }
        }
        return x0;
    }

    public void intersect(double[] start, double[] dir, double[] sphere) {
        sub(observer, sphere, regs[0]);
        double a = mul(dir, dir);
        double b = 2 * mul(regs[0], dir);
        double c = mul(regs[0], regs[0]) - radius * radius;

        double x = b * b - 4 * a * c;
        if (x < 0) {
        } else if (x == 0) {
            intersections[numIntersections] = -b / 2 / a;
            intersectionso[numIntersections] = sphere;
            numIntersections++;
        } else {
            intersections[numIntersections] = (-b + Math.sqrt(x)) / a / 2;
            intersectionso[numIntersections] = sphere;
            numIntersections++;
            intersections[numIntersections] = (-b - Math.sqrt(x)) / a / 2;
            intersectionso[numIntersections] = sphere;
            numIntersections++;
        }
    }

    public void compute(int xpixel, int ypixel) {
        if (xpixel >= w || ypixel >= h) {
            return;
        }

        mul(vx, (maxx - minx) * xpixel / w + minx, regs[0]);
        mul(vy, (maxy - miny) * ypixel / h + miny, regs[1]);
        add(regs[0], regs[1], regs[0]);
        sub(regs[0], observer, sight_dir);
        double minlambda = Double.NaN;
        double[] mino = null;
        numIntersections = 0;
        intersect(sight_start, sight_dir, spheres);
        for (int j = 0; j < numIntersections; ++j) {
            if (Double.isNaN(minlambda)) {
                minlambda = intersections[j];
                mino = intersectionso[j];
            }
            if (intersections[j] < minlambda) {
                minlambda = intersections[j];
                mino = intersectionso[j];
            }
        }

        if (Double.isNaN(minlambda)) {
            result[xpixel + w * ypixel] = 0xff;
        } else {
            mul(sight_dir, minlambda, regs[0]);
            add(regs[0], observer, regs[0]); //intersection

            sub(regs[0], mino, regs[1]);
            mul(regs[1], 1.0 / radius, regs[1]); //normal

            sub(light, regs[0], regs[2]);
            double l = Math.sqrt(mul(regs[2], regs[2]));
            mul(regs[2], 1.0 / l, regs[2]); // light

            boolean intersects = false;

            double alpha = mul(regs[1], regs[2]);
            if (alpha < 0 || intersects) {
                alpha = 0;
            }
            if (alpha > 1) {
                //System.err.println(alpha);
            }
            result[xpixel + w * ypixel] = (int) (0xff * alpha) << 8;
        }
    }

    @Override
    public void gpuMethod() {
        int xpixel = RootbeerGpu.getBlockIdxx();
        int ypixel = RootbeerGpu.getThreadIdxx();
        compute(xpixel, ypixel);
    }
}
