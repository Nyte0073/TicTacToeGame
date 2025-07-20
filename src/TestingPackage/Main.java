package TestingPackage;


import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


record FrontEndServer(String host, int serverPort) {
   private static Item item = new Item();
    public void launchServer() throws Exception {
        Socket socket = new Socket(host, serverPort);
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        while(!Thread.currentThread().isInterrupted()) {
            if(item.item != null) {
                writer.println(item);
            }
        }
    }
    public static void setItemAndClass(Object item, Class <?> cl, String name) {
        FrontEndServer.item.item = item;
        FrontEndServer.item.cl = cl;
        FrontEndServer.item.name = name;
    }

    public static void main(String[] args) {

    }
}

record BackEndServer(int serverPort) {
    private static Item item = new Item();
    public void launchServer() throws Exception {
        ServerSocket serverSocket = new ServerSocket(serverPort);
        Socket socket = serverSocket.accept();
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        while(!Thread.currentThread().isInterrupted()) {
            writer.println(item);
        }
    }
    public static void setItemAndClass(Object item, Class <?> cl, String name) {
        BackEndServer.item.item = item;
        BackEndServer.item.cl = cl;
        BackEndServer.item.name = name;
    }

    public static void main(String[] args) throws Exception {

    }
}

class Item {
    Class <?> cl = null;
    Object item = null;
    String name = "";
}

