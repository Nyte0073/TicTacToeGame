package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

public class BackEndServer {
    private final int serverPort;
    public BackEndServer(int serverPort) {
        this.serverPort = serverPort;
    }
    public void launchServer() {
        try {

            ServerSocket serverSocket = new ServerSocket(serverPort);
            Socket socket = serverSocket.accept();

            System.out.println("Client is now connected.");

            BufferedReader frontendServerReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true);

            try {
                new Thread(() -> {
                    String message;
                    while (true) {
                        try {
                            if ((message = frontendServerReader.readLine()) != null) {
                                System.out.println("Back end server message received: " + message);
                            }
                        } catch (IOException e) {
                            System.out.println("Exception. Closing stream.");
                            System.exit(0);
                        }
                    }
                }
                ).start();
            } catch(Exception e) {
                System.out.println("Exception here. Closing stream.");
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
                    serverWriter.println(line);
                    System.out.println("Back end server sent message: " + line);
                }
            }
        } catch(Exception e) {
            System.out.println("Exception. Closing stream.");
            System.exit(0);
        }
    }
}
