//----------------------------------------------------------------------
// RegServer.java
// Author: Osita Ighodaro Ben Musoke-Lubega
//----------------------------------------------------------------------

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.File;
import java.util.regex.Pattern;

//----------------------------------------------------------------------

class RegServerThread extends Thread
{
   private Socket socket;
   private SocketAddress clientAddr;

   public RegServerThread(Socket socket, SocketAddress clientAddr)
   {
        this.socket = socket;
        this.clientAddr = clientAddr;
   }

   public String courseInfo(String classID)
   {
       try
       {
        final String DATABASE_NAME = "reg.sqlite";
        String output = "";

        File databaseFile = new File(DATABASE_NAME);
            if (! databaseFile.isFile())
                System.err.println("regdetails: Database connection failed");

        if (!Pattern.matches("[0-9]*", classID))
        {
            System.err.println("regdetails: classid is not an integer");
            System.exit(1);
        }

        Connection connection =
                DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);

            String stmtStr = "SELECT courses.courseid, days, starttime, endtime, bldg, roomnum, area, title, " +
                "descrip, prereqs " + 
                "FROM courses, classes " +
                "WHERE classes.courseid = courses.courseid " +
                "AND classid = ?";

            String stmtStr2 = "SELECT dept, coursenum FROM crosslistings, classes " + 
                "WHERE crosslistings.courseid = classes.courseid " +
                "AND classid = ? ORDER BY dept, coursenum";

            String stmtStr3 = "SELECT profname FROM profs, coursesprofs, classes " + 
                "WHERE coursesprofs.courseid = classes.courseid " +
                "AND coursesprofs.profid = profs.profid AND classid = ? " + 
                "ORDER BY profname";

            // Create a prepared statement and substitute values.
            PreparedStatement statement = 
                connection.prepareStatement(stmtStr);
            statement.setString(1, classID);

            PreparedStatement statement2 = 
                connection.prepareStatement(stmtStr2);
            statement2.setString(1, classID);

            PreparedStatement statement3 = 
                connection.prepareStatement(stmtStr3);
            statement3.setString(1, classID);

            ResultSet resultSet = statement.executeQuery();
            ResultSet resultSet2 = statement2.executeQuery();
            ResultSet resultSet3 = statement3.executeQuery();

            if (resultSet.isClosed())
            {
                System.err.println("regdetails: classid does not exist");
                System.exit(1);
            }

            String courseID = resultSet.getString("courseid");
            String days = resultSet.getString("days");
            String startTime = resultSet.getString("starttime");
            String endTime = resultSet.getString("endtime");
            String bldg = resultSet.getString("bldg");
            String roomNum = resultSet.getString("roomnum");
            String area = resultSet.getString("area");
            String title = resultSet.getString("title");
            String descrip = resultSet.getString("descrip");
            String prereqs = resultSet.getString("prereqs");

            output += "\nCourse ID:\n" + courseID + "\n";    
            output += "\nDays:\n" + days + "\n";
            output += "\nStart Time:\n" + startTime + "\n";
            output += "\nEnd Time:\n" + endTime + "\n";
            output += "\nBuilding:\n" + bldg + "\n";
            output += "\nRoom Number:\n" + roomNum + "\n";

            output += "\nDepartment and Course Number:\n";
            while(resultSet2.next())
            {
                String dept = resultSet2.getString("dept");
                String courseNum = resultSet2.getString("coursenum");

                output += dept + " ";
                output += courseNum + "\n";
            }
            
            output += "\nDistribution Area: (if applicable)\n" + area + "\n";
            output += "\nTitle:\n" + title + "\n";
            output += "\nDescription:\n" + descrip + "\n";
            output += "\nPrerequisites:\n" + prereqs + "\n";
            output += "\nProfessor Names:\n";
            while(resultSet3.next())
            {
                String profName = resultSet3.getString("profname");
                output += profName + "\n";
            }

            connection.close();
            return output;
        }
        catch (Exception e) 
        {
            System.err.println("regdetails: database reg.sqlite not found");
            System.exit(1); 
        }
        return null;
   }

   public void run()
   {
      try
      {  
         System.out.println("Spawned thread for " + clientAddr);

         InputStream inputStream = socket.getInputStream();
         Scanner scanner = new Scanner(inputStream);

         OutputStream os = socket.getOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(os);

        // while (scanner.hasNextLine())
         {
         //   String line = scanner.nextLine();
           // System.out.println("Read from " + clientAddr + ": " + line);
         }

         oos.writeObject(courseInfo("8321"));
         oos.flush();
         System.out.println("Wrote courses to " + clientAddr);

         socket.close();
         System.out.println("Closed socket for " + clientAddr);
         System.out.println("Exiting thread for " + clientAddr);
      }
      catch (Exception e) { System.err.println(e); }
   }
}

public class RegServer
{
   public static void main(String[] args)
   {
      if (args.length != 1)
      {  
         System.err.println("Usage: java RegServer port");
         System.exit(1);
      }

      try
      {  
         int port = Integer.parseInt(args[0]);

         ServerSocket serverSocket = new ServerSocket(port);
         System.out.println("Opened server socket");
         
         while (true)
         {  
            Socket socket = serverSocket.accept();
            SocketAddress clientAddr = socket.getRemoteSocketAddress();

            System.out.println("Accepted connection for " + clientAddr);
            System.out.println("Opened socket for " + clientAddr);
            RegServerThread regServerThread = new RegServerThread(socket, clientAddr);
            regServerThread.start();
         }
      }
      catch (Exception e) { System.err.println(e); }
   }
}