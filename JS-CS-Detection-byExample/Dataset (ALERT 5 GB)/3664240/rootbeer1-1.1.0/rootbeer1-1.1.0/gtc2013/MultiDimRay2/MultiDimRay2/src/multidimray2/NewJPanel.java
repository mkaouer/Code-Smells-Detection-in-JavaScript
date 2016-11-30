/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multidimray2;

import de.metzingen.thorstenkiefer.RayGenerator;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thorsten
 */
public class NewJPanel extends javax.swing.JPanel {

    private BufferedImage img;
    private double minx = -10;
    private double maxx = 10;
    private double miny = -10;
    private double maxy = 10;
    private final double[][] spheres;
    private final static int numDimensions = 4;
    private double[] vx = new double[numDimensions];
    private double[] vy = new double[numDimensions];
    private double[] observer = new double[numDimensions];
    private double[][] rotationMatrix = identity();

    private static class HyperCube {

        public List<double[]> points = new ArrayList<double[]>();
        public List<double[]> lines = new ArrayList<double[]>();
    }

    private static HyperCube hypercube(int i, int n, double pos) {
        HyperCube hc = new HyperCube();

        if (i == -1) {
            hc.points.add(new double[n]);
        } else {
            HyperCube h1 = hypercube(i - 1, n, pos);
            HyperCube h2 = hypercube(i - 1, n, pos);
            for (double[] nd : h1.points) {
                nd[i] = -pos;
            }
            for (double[] nd : h1.lines) {
                nd[i] = -pos;
            }
            for (double[] nd : h2.points) {
                nd[i] = pos;
            }
            for (double[] nd : h2.lines) {
                nd[i] = pos;
            }

            hc.points.addAll(h1.points);
            hc.points.addAll(h2.points);
            hc.lines.addAll(h1.lines);
            hc.lines.addAll(h2.lines);

            for (int j = 0; j < h1.points.size(); ++j) {
                for (int k = 1; k < 5; ++k) {
                    double[] ys = new double[n];
                    for (int l = 0; l < n; ++l) {
                        ys[l] = (h1.points.get(j)[l]
                                - h2.points.get(j)[l]) * k / 5
                                + h2.points.get(j)[l];
                    }
                    hc.lines.add(ys);
                }
            }
        }
        return hc;
    }

    /**
     * Creates new form NewJPanel
     */
    public NewJPanel(final boolean useGPU) {
        initComponents();
        img = new BufferedImage(10, 10, BufferedImage.TYPE_3BYTE_BGR);

        HyperCube hc = hypercube(3, 4, 2.5);
        ArrayList<double[]> xs = new ArrayList<double[]>();
        xs.addAll(hc.points);
        xs.addAll(hc.lines);
        System.err.println(xs.size());
        spheres = new double[xs.size()][];
        for (int i = 0; i < xs.size(); ++i) {
            spheres[i] = xs.get(i);
        }

        for (int i = 0; i < spheres.length; ++i) {
            for (int j = 3; j < numDimensions; ++j) {
                spheres[i][j] += 2.5;
            }
        }

        observer[2] = -10;
        vx[0] = 1;
        vy[1] = 1;

        final double[] light = new double[]{0, 0, -5, 0};
        final double[] dlight = new double[]{0, 0, 0, 0};
        //final Random random = new Random();

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    for (int i = 0; i < dlight.length; ++i) {
                        //light[i] += 2 * (random.nextDouble() - 0.5);
                        //light[i] += dlight[i];
                    }

                    double[] vx2 = mul(vx, rotationMatrix);
                    double[] vy2 = mul(vy, rotationMatrix);
                    double[] observer2 = mul(observer, rotationMatrix);

                    BufferedImage im = img;
                    int[] pixels = new int[im.getWidth() * im.getHeight()];
                    if (useGPU) {
                        RayGenerator.generateGPU(
                                im.getWidth(), im.getHeight(), minx, maxx, miny, maxy,
                                pixels, spheres, light, observer2,
                                1, vx2, vy2, numDimensions);
                    }else{
                        RayGenerator.generateCPU(
                                im.getWidth(), im.getHeight(), minx, maxx, miny, maxy,
                                pixels, spheres, light, observer2,
                                1, vx2, vy2, numDimensions);
                    }
                    im.setRGB(0, 0, im.getWidth(), im.getHeight(), pixels, 0, im.getWidth());

                    repaint();
                    requestFocus();

                    try {
                        sleep(1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NewJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
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

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
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

    private static double[][] identity() {
        double[][] result = new double[numDimensions][numDimensions];
        for (int i = 0; i < numDimensions; ++i) {
            result[i][i] = 1;
        }
        return result;
    }

    private static double[] mul(double[] v, double[][] matrix) {
        double[] result = new double[v.length];
        for (int a = 0; a < v.length; ++a) {
            double x = 0;
            for (int b = 0; b < v.length; ++b) {
                x += v[b] * matrix[a][b];
            }
            result[a] = x;
        }
        return result;
    }

    public static double[][] mul(double[][] matrix1, double[][] matrix2) {
        double[][] result = new double[matrix1.length][];
        for (int i = 0; i < matrix1.length; ++i) {
            result[i] = mul(matrix1[i], matrix2);
        }
        return result;
    }

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        int i = 2, j = 1;
        double alpha = 0.01;

        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            i = 1;
            j = 2;
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            i = 2;
            j = 1;
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            i = 0;
            j = 2;
        } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            i = 2;
            j = 0;
        } else if (evt.getKeyCode() == KeyEvent.VK_W) {
            i = 3;
            j = 1;
        } else if (evt.getKeyCode() == KeyEvent.VK_S) {
            i = 1;
            j = 3;
        } else if (evt.getKeyCode() == KeyEvent.VK_A) {
            i = 3;
            j = 0;
        } else if (evt.getKeyCode() == KeyEvent.VK_D) {
            i = 0;
            j = 3;
        }

        double[][] matrix = identity();
        matrix[i][j] = Math.sin(alpha);
        matrix[j][i] = -Math.sin(alpha);
        matrix[i][i] = Math.cos(alpha);
        matrix[j][j] = Math.cos(alpha);

        rotationMatrix = mul(rotationMatrix, matrix);
    }//GEN-LAST:event_formKeyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
