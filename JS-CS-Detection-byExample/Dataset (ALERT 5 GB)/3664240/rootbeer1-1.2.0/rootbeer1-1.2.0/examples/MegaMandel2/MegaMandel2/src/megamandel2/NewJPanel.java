/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package megamandel2;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import mandellib.MandelGenerator;

import org.trifort.rootbeer.runtime.util.Stopwatch;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author thorsten
 */
public class NewJPanel extends javax.swing.JPanel {

    private BufferedImage img;
    private float minx = -2;
    private float maxx = 2;
    private float miny = -2;
    private float maxy = 2;
    private static final int maxdepth = 2000;
    private float fx = 0;
    private float fy = 0;
    private float dx = 0;
    private float dy = 0;
    private boolean m_cpu;
    private static Stopwatch m_cpuWatch = new Stopwatch();
    public MyThread[] threads = new MyThread[4];

    /**
     * Creates new form NewJPanel
     */
    public NewJPanel(boolean cpu) {
        initComponents();
        img = new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);
        m_cpu = cpu;

        if (cpu) {
            for (int i = 0; i < threads.length; ++i) {
                threads[i] = new MyThread();
            }
            for (MyThread mt : threads) {
                mt.start();
            }
        }

        new Thread() {
            @Override
            public void run() {

                while (true) {
                    BufferedImage im = img;

                    int width = im.getWidth();
                    int height = im.getHeight();
                    int[] ps = new int[width * height];

                    if (m_cpu) {
                        cpuGenerate(width, height, minx, maxx, miny, maxy, maxdepth, ps);
                    } else {
                        MandelGenerator.gpuGenerate(width, height, minx, maxx, miny, maxy, maxdepth, ps);
                    }

                    im.setRGB(0, 0, width, height, ps, 0, width);

                    float dfx = (maxx - minx) * fx;
                    float dfy = (maxy - miny) * fy;
                    maxx -= dfx;
                    minx += dfx;
                    maxy -= dfy;
                    miny += dfy;
                    maxx += dx;
                    minx += dx;
                    maxy += dy;
                    miny += dy;

                    repaint();

                    try {
                        sleep(1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NewJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
    }

    private static class MyThread extends Thread {

        public boolean compute = false;
        public int w;
        public int h;
        public double minx;
        public double maxx;
        public double miny;
        public double maxy;
        public int maxdepth;
        public int[] pixels;
        public int y;

        @Override
        public void run() {
            while (true) {
                while (!compute) {
                    try {
                        sleep(20);
                    } catch (InterruptedException ex) {
                    }
                }

                for (int x = 0; x < w; ++x) {
                    double xr = 0;
                    double xi = 0;
                    double cr = (maxx - minx) * x / w + minx;
                    double ci = (maxy - miny) * y / h + miny;
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
                    //int r = (int) (0xff * (Math.sin((double) (0.01 * d + 0) + 1)) / 2);
                    //int g = (int) (0xff * (Math.sin((double) (0.02 * d + 0.01) + 1)) / 2);
                    //int b = (int) (0xff * (Math.sin((double) (0.04 * d + 0.1) + 1)) / 2);
                    int dest_index = y * w + x;

                    pixels[dest_index] =
                            (int) ((0xff * (0.01 * d + 0) + 1) / 2) << 16
                            | (int) ((0xff * (0.02 * d + 0.01) + 1) / 2) << 8
                            | (int) ((0xff * (0.04 * d + 0.1) + 1) / 2);
                }
                compute = false;
            }
        }
    }

    private void cpuGenerate(int w, int h, double minx, double maxx, double miny, double maxy, int maxdepth, int[] pixels) {
        m_cpuWatch.start();
        for (MyThread mt : threads) {
            mt.h = h;
            mt.w = w;
            mt.maxdepth = maxdepth;
            mt.maxx = maxx;
            mt.maxy = maxy;
            mt.minx = minx;
            mt.miny = miny;
            mt.pixels = pixels;
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
        m_cpuWatch.stop();
        System.out.println("avg cpu: " + m_cpuWatch.getAverageTime());
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
    }//GEN-LAST:event_formComponentResized

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        dx = 0.0001f * (evt.getX() - getWidth() / 2) * (maxx - minx);
        dy = 0.0001f * (evt.getY() - getHeight() / 2) * (maxy - miny);

        if (evt.getButton() == MouseEvent.BUTTON1) {
            fx = 0.01f;
            fy = 0.01f;
        } else if (evt.getButton() == MouseEvent.BUTTON3) {
            fx = -0.01f;
            fy = -0.01f;
        }
    }//GEN-LAST:event_formMousePressed

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        fx = 0;
        fy = 0;
        dx = 0;
        dy = 0;
    }//GEN-LAST:event_formMouseReleased

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        dx = 0.0001f * (evt.getX() - getWidth() / 2) * (maxx - minx);
        dy = 0.0001f * (evt.getY() - getHeight() / 2) * (maxy - miny);
    }//GEN-LAST:event_formMouseDragged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
