
//----------------------------------------------------------------------
// Reg.java
// Authors: Benjamin Musoke-Lubega (bpm3) Osita Ighodaro (ighodaro)
//----------------------------------------------------------------------
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.lang.model.util.ElementScanner6;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.JScrollPane;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
            MyRunnable runnable = new MyRunnable();
            EventQueue.invokeLater(runnable);

            String domainName = args[0];
            int portNumber = Integer.parseInt(args[1]); 
            Socket socket = new Socket(domainName, portNumber);

            while (true)
            {
                HashMap<String, ArrayList<Character>> queries =  runnable.returnTerms();

                OutputStream os = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject(queries);
                oos.flush();
            
                InputStream is = socket.getInputStream(); 
                ObjectInputStream ois = new ObjectInputStream(is);
                Object courseInput = ois.readObject();
                runnable.takeInput(courseInput);

            // store input in variable outside while loop
            // edit myrunnable to take input from regserver
            // Classes[] classes = (Classes[])ois.readObject();
            }

           // socket.close();
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }
}

 class MyRunnable implements Runnable {

    private HashMap<String, ArrayList<Character>> tempQueries;
    private ArrayList<String> courseBasics;
    private String courseInfo;

    public MyRunnable()
    {
        tempQueries = new HashMap<String, ArrayList<Character>>();
        tempQueries.put("-dept", new ArrayList<Character>());
        tempQueries.put("-coursenum", new ArrayList<Character>());
        tempQueries.put("-area", new ArrayList<Character>());
        tempQueries.put("-title", new ArrayList<Character>());
    }

    public void takeInput(Object o)
    {
        if (o instanceof String) 
        {
            courseInfo = (String) o;
        }
        else if (o instanceof ArrayList)
        {
            courseBasics = (ArrayList<String>) o;
        }

    }
    
    public HashMap<String, ArrayList<Character>> returnTerms()
    {
        return tempQueries;
    }

    private class TextFieldListener implements KeyListener
    {
        private String field;

        public TextFieldListener(String field)
        {
            this.field = field;
        }
        
        public void keyPressed(KeyEvent e){}

        public void keyReleased(KeyEvent e){}

        public void keyTyped(KeyEvent e)
        {
            char key = e.getKeyChar();
            if (key == KeyEvent.VK_ENTER)
            {
                for(Map.Entry<String, ArrayList<Character>> entry : tempQueries.entrySet())
                {
                    System.out.print(entry.getKey());
                    System.out.print(" " + entry.getValue());
                    System.out.println();
                } 
            }
            else
            {
                ArrayList<Character> term = tempQueries.get(this.field);
                if (key == KeyEvent.VK_BACK_SPACE) 
                {
                    if(term.size() > 0) term.remove(term.size() - 1);
                }
                else term.add(key);

                tempQueries.put(this.field, term);
            }
        }
 
    }

    public void run() {
        JFrame frame = new JFrame();
        frame.setTitle("Princeton University Class Search");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        frame.setSize(screenWidth / 2, screenHeight / 2);
        //-----------------------------------------------------------------------
        Font font =  new Font("Monospaced", Font.PLAIN, 12);

        // Organize query area
        JPanel queryArea = new JPanel();
        queryArea.setLayout(new GridLayout(4, 1));

        // Add labels and textfields to north of frame
        JLabel deptLabel = new JLabel("   Dept:");
        JLabel coursenumLabel = new JLabel(" Number:");
        JLabel areaLabel = new JLabel("   Area:");
        JLabel titleLabel = new JLabel("  Title:");

        JTextField coursenumField = new JTextField("");
        TextFieldListener coursenumTFL = new TextFieldListener("-coursenum");
        coursenumField.addKeyListener(coursenumTFL);
        coursenumLabel.setFont(font);
    
        Dimension dim = coursenumField.getPreferredSize();

        JTextField deptField = new JTextField("");
        TextFieldListener deptTFL = new TextFieldListener("-dept");
        deptField.addKeyListener(deptTFL);
        deptField.setMinimumSize(dim);
        deptLabel.setFont(font);

        JTextField areaField = new JTextField("");
        TextFieldListener areaTFL = new TextFieldListener("-area");
        areaField.addKeyListener(areaTFL);
        areaField.setMinimumSize(dim);
        areaLabel.setFont(font);

        JTextField titleField = new JTextField("");
        TextFieldListener titleTFL = new TextFieldListener("-title");
        titleField.addKeyListener(titleTFL);
        titleField.setMinimumSize(dim);
        titleLabel.setFont(font);

        JPanel deptEntry = new JPanel();
        deptEntry.setLayout(new BorderLayout());
        deptEntry.add(deptLabel, BorderLayout.WEST);
        deptEntry.add(deptField, BorderLayout.CENTER);

        JPanel coursenumEntry = new JPanel();
        coursenumEntry.setLayout(new BorderLayout());
        coursenumEntry.add(coursenumLabel, BorderLayout.WEST);
        coursenumEntry.add(coursenumField, BorderLayout.CENTER);

        JPanel areaEntry = new JPanel();
        areaEntry.setLayout(new BorderLayout());
        areaEntry.add(areaLabel, BorderLayout.WEST);
        areaEntry.add(areaField, BorderLayout.CENTER);

        JPanel titleEntry = new JPanel();
        titleEntry.setLayout(new BorderLayout());
        titleEntry.add(titleLabel, BorderLayout.WEST);
        titleEntry.add(titleField, BorderLayout.CENTER);

        queryArea.add(deptEntry);
        queryArea.add(coursenumEntry);
        queryArea.add(areaEntry);
        queryArea.add(titleEntry);
        queryArea.setSize(60,10);

        frame.add(queryArea);
        frame.getContentPane().add(queryArea, "North");
        
        // Add listbox to center of frame
        JPanel listboxArea = new JPanel();
        listboxArea.setLayout(new GridLayout(1, 1));
        DefaultListModel<String> scrollingListModel = new DefaultListModel<String>();
        JList<String> results = new JList<String>(scrollingListModel);
        results.setFont(font);

        
       /* for(Map.Entry<String, ArrayList<Character>> entry : query.entrySet()) {
            //String key = entry.getKey();
            ArrayList<Character> chars = entry.getValue();
            scrollingListModel.addElement(chars.toArray().toString());
        } */
        
        for (int i = 0; i < 5; i++)
        {
            scrollingListModel.addElement("9032	COS	233   ST   An Integrated, Quantitative Introduction to the Natural Sciences II");
            scrollingListModel.addElement("9363	COS	314   QR   Computer and Electronic Music through Programming, Performance, and Composition");
            scrollingListModel.addElement("8333	COS	598A	      Advanced Topics in Computer Science: Economic and Systems Design for Electronic Marketplaces");
            scrollingListModel.addElement("8336	COS	598D	      Advanced Topics in Computer Science: Formal Methods in Networking");
        }

        JScrollPane listScrollPane = new JScrollPane(results);
        listboxArea.add(listScrollPane);

        frame.getContentPane().add(listboxArea, "Center");
        

        //-----------------------------------------------------------------------
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