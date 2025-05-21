package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

public class FrontEndServer {
    private final int serverPort;
    private final String host;
    public FrontEndServer(String host, int serverPort) {
        this.host = host;
        this.serverPort = serverPort;
    }
    public void launchServer() {
       try {
           Socket socket = new Socket(host, serverPort);
           BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
           BufferedReader backEndServerReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

           try {
               new Thread(() -> {
                   String message;
                   while (true) {
                       try {
                           if ((message = backEndServerReader.readLine()) != null) {
                               System.out.println("Front End Server received message: " + message);
                           }
                       } catch (IOException e) {
                           System.out.println("Exception. Closing stream.");
                           System.exit(0);
                       }
                   }
               }
               ).start();
           } catch(Exception e) {
               System.out.println("Exception has been caught. Ending stream now.");
               System.exit(0);
           }

           String line;
           while(true) {
               try {
                   line = Optional.ofNullable(userInputReader.readLine()).orElse("");
               } catch (Exception e) {
                   throw new RuntimeException(e);
               }
               if (!line.isEmpty()) {
                   writer.println(line);
                   System.out.println("Front end server sent message: " + line);
               }
           }
       } catch(Exception e) {
           System.out.println("Exception. Closing stream now.");
           System.exit(0);
       }
    }
}
