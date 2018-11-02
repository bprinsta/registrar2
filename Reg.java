
//----------------------------------------------------------------------
// Reg.java
// Authors: Benjamin Musoke-Lubega (bpm3) Osita Ighodaro (ighodaro)
//----------------------------------------------------------------------
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.FocusEvent;
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
        
        if (!Pattern.matches("[0-9]*", args[1]))
        {
            System.err.println("reg: port is not an integer");
            System.exit(1);
        }

        try
        {
            String domainName = args[0];
            int portNumber = Integer.parseInt(args[1]); 

            MyRunnable runnable = new MyRunnable(domainName, portNumber);
            EventQueue.invokeLater(runnable);

            Socket socket = new Socket(domainName, portNumber);
                    
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(runnable.returnTerms());
            oos.flush();
                
            InputStream is = socket.getInputStream(); 
            ObjectInputStream ois = new ObjectInputStream(is);
            Object courseInput = ois.readObject();
            runnable.takeInput(courseInput);

            socket.close();
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }
}

 class MyRunnable implements Runnable {

    private HashMap<String, ArrayList<Character>> tempQueries;
    private ArrayList<CourseStuff> courseBasics;
    private String courseInfo;

    private String domainName;
    private int portNumber;
    DefaultListModel<String> scrollingListModel;

    public MyRunnable(String domain, int port)
    {
        this.domainName = domain;
        this.portNumber = port;
        scrollingListModel = new DefaultListModel<String>();

        tempQueries = new HashMap<String, ArrayList<Character>>();
        tempQueries.put("-dept", new ArrayList<Character>());
        tempQueries.put("-coursenum", new ArrayList<Character>());
        tempQueries.put("-area", new ArrayList<Character>());
        tempQueries.put("-title", new ArrayList<Character>());
    }
    
    public HashMap<String, ArrayList<Character>> returnTerms()
    {
        return tempQueries;
    }

    public void takeInput(Object o)
    {
        if (o instanceof ArrayList) 
        {
            scrollingListModel.removeAllElements();
            courseBasics = (ArrayList<CourseStuff>) o;
            for (CourseStuff c: courseBasics)
            {
                scrollingListModel.addElement(c.getCourseData());
            }
        }
        else if (o instanceof String)
        {
            courseInfo = (String) o;
        }
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
                try 
                {
                    Socket socket = new Socket(domainName, portNumber);

                    OutputStream os = socket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(os);
                    oos.writeObject(tempQueries);
                    oos.flush();
                    System.out.println("Flushed Object");

                    InputStream is = socket.getInputStream(); 
                    ObjectInputStream ois = new ObjectInputStream(is);
                    Object courseInput = ois.readObject();
                    takeInput(courseInput);
                    System.out.println("Retrieved Object");

                    socket.close();
                }
                catch (Exception exc)
                {
                    System.err.println(exc);
                }
            }
            else
            {
                ArrayList<Character> term = tempQueries.get(this.field);
                if (key == KeyEvent.VK_BACK_SPACE) {
                    if(term.size() > 0) term.remove(term.size() - 1);
                }
                else term.add(key);
                tempQueries.put(this.field, term);
            }
        }
    }

    private class listBoxListener implements KeyListener, FocusListener, MouseListener
    {
        public void mousePressed(MouseEvent event)
        {
            System.out.println("mousePressed");
        }
        public void mouseReleased(MouseEvent event)
        {
            System.out.println("mouseReleased");
        }
        public void mouseEntered(MouseEvent event)
        {
            System.out.println("mouseEntered");
        }
        public void mouseExited(MouseEvent event)
        {
            System.out.println("mouseExited");
        }
        public void mouseClicked(MouseEvent event)
        {
            System.out.println("mouseClicked");
        }

        public void focusGained(FocusEvent event)
        {
            System.out.println("focusGained");
        }
        public void focusLost(FocusEvent event)
        {
            System.out.println("focusLost");
        }
        public void keyPressed(KeyEvent event)
        {
        }
        public void keyReleased(KeyEvent event)
        {
        }
        public void keyTyped(KeyEvent event)
        {
            System.out.println("keyTyped");
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
        JList<String> results = new JList<String>(scrollingListModel);
        results.addKeyListener(new listBoxListener());
        results.addFocusListener(new listBoxListener());
        results.addMouseListener(new listBoxListener());
        results.setFont(font);

        // add courseBacics to scrollingListModel
        if (courseBasics != null)
        {
            for (CourseStuff c: courseBasics)
            {
                scrollingListModel.addElement(c.getCourseData());
            }
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