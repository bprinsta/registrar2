//----------------------------------------------------------------------
// RegServer.java
// Author: Osita Ighodaro Ben Musoke-Lubega
//----------------------------------------------------------------------

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

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

   public void run()
   {
      try
      {  
         System.out.println("Spawned thread for " + clientAddr);

         InputStream inputStream = socket.getInputStream();
         Scanner scanner = new Scanner(inputStream);

         OutputStream outputStream = socket.getOutputStream();
         PrintWriter printWriter = new PrintWriter(outputStream);

         while (scanner.hasNextLine())
         {
            String line = scanner.nextLine();
            System.out.println("Read from " +clientAddr + ": " + line); 
            printWriter.println(line);
            printWriter.flush();
            System.out.println("Wrote to " + clientAddr + ": " + line); 
         }

         socket.close();
         System.out.println("Closed socket for " + clientAddr);
         System.out.println("Exiting thread for " + clientAddr);
      }
      catch (Exception e) { System.err.println(e); }
   }
}

//----------------------------------------------------------------------

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