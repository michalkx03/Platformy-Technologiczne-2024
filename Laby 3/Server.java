package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        Thread thread=null;
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            LOGGER.info("Server started");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("New connect " + clientSocket.getInetAddress());
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());

                thread = new Thread(new S_Thread(output, input, LOGGER));
                thread.start();
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        if(thread!=null){
            thread.interrupt();
        }
    }
}
