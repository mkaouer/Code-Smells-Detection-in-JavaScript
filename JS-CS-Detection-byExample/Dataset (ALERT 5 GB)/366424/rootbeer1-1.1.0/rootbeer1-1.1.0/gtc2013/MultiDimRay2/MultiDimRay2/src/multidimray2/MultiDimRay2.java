/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multidimray2;

import java.awt.GridLayout;
import javax.swing.JFrame;

/**
 *
 * @author thorsten
 */
public class MultiDimRay2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean useGPU = false;

        if (args.length == 1 && args[0].equals("-gpu")) {
            useGPU = true;
        }

        JFrame f = new JFrame();
        f.setSize(512, 512);
        f.setLayout(new GridLayout());
        f.add(new NewJPanel(useGPU));
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
