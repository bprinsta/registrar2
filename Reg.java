
//----------------------------------------------------------------------
// Reg.java
// Authors: Benjamin Musoke-Lubega (bpm3) Osita Ighodaro (ighodaro)
//----------------------------------------------------------------------

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Reg {

    public static void main(String[] args) {
       /* int address = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]); */

        EventQueue.invokeLater(new MyRunnable());
    }
}

class MyRunnable implements Runnable {
    public void run() {
        JFrame frame = new JFrame();
        frame.setTitle("Registrar");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        frame.setSize(screenWidth / 2, screenHeight / 2);
        // Insert code here.
        frame.setLocationByPlatform(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}