/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package megamandel2;

import java.awt.GridLayout;
import javax.swing.JFrame;

/**
 *
 * @author thorsten
 */
public class MegaMandel2 {

  /**
    * @param args the command line arguments
    */
  public static void main(String[] args) {
    boolean cpu = true;
    if(args.length == 1 && args[0].equals("-gpu")){
      cpu = false;
    }    

    JFrame f = new JFrame();
    f.setSize(256, 256);
    f.setLayout(new GridLayout());
    f.add(new NewJPanel(cpu));
    f.setVisible(true);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
