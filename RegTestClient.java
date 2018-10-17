//----------------------------------------------------------------------
// RegTestClient.java
// Author: Osita Ighodaro
//----------------------------------------------------------------------

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.net.Socket;

public class RegTestClient
{
   public static void main(String[] args)
   {
      if (args.length != 2)
      {  
         System.err.println("Usage: java CourseClient5 host port");
         System.exit(1);
      }

      try
      {  
         String host = args[0];
         int port = Integer.parseInt(args[1]);

         Socket socket = new Socket(host, port);

         InputStream is = socket.getInputStream();
         ObjectInputStream ois = new ObjectInputStream(is);

         String courseInfo = (String) ois.readObject();
         socket.close();

         System.out.println(courseInfo);
      }
      catch (Exception e) { System.err.println(e); }
   }
}