package server_side;
import config.Resources;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static void main(String[]args){
        File folder = new File(Resources.SERVER_SHARED_FOLDER);
        System.out.println("Server has begun on port " + Resources.SERVER_PORT);
        try (ServerSocket serverSocket = new ServerSocket(Resources.SERVER_PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientConnectionHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
