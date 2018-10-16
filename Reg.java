
//----------------------------------------------------------------------
// Reg.java
// Authors: Benjamin Musoke-Lubega (bpm3) Osita Ighodaro (ighodaro)
//----------------------------------------------------------------------

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.net.Socket;

public class Reg {

    public static void main(String[] args) 
    {
        if (args.length != 2)
        {
            System.err.println("Usage: java Reg.java host port");
            System.exit(1);
        }
        try
        {
            String domainName = args[0];
            int portNumber = Integer.parseInt(args[1]); 

            Socket socket = new Socket(domainName, portNumber);

            InputStream socketInputStream = socket.getInputStream();
            //Scanner socketScanner = new Scanner(socketInputStream);

            EventQueue.invokeLater(new MyRunnable());
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
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
        
        // Organize query area
        JPanel queryArea = new JPanel();

        JTextField deptField = new JTextField("Enter dept", 10);
        JTextField coursenumField = new JTextField("Enter coursenum", 20);
        JTextField areaField = new JTextField("Enter area", 10);
        JTextField titleField = new JTextField("Enter title", 20);
        queryArea.add(deptField, BorderLayout.NORTH);
        queryArea.add(coursenumField, BorderLayout.NORTH);
        queryArea.add(areaField, BorderLayout.NORTH);
        queryArea.add(titleField, BorderLayout.NORTH);

        JButton searchButton = new JButton("Search");
        JList<String> queryResults = new JList<String>();
        queryArea.add(searchButton);
        queryArea.add(queryResults);

        frame.add(queryArea);
        

        // Insert code here.
        frame.setLocationByPlatform(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

/*
Text fields that allow the user to specify a dept, a coursenum, an area, and a title. 
The application must communicate with regserver to query the database each time the user types a 
key in any of the text fields.

A list box whose entries are the results of the query. The list box must have vertical and 
horizontal scroll bars (as needed), and so must be able to scroll vertically and/or horizontally. */