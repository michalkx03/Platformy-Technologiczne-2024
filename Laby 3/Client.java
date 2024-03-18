package org.example;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;
public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(System.in);
            Socket socket = new Socket("127.0.1.1", 8080);
            LOGGER.info("Conected");
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            String ready = (String) input.readObject();

            if (!ready.equals(Protocol.r)) {
                LOGGER.info("Not ready");
                socket.close();
                return;
            }
            else {
                LOGGER.info("Server ready");
            }

            int n = scanner.nextInt();
            output.writeInt(n);
            output.flush();

            String ready_for_m = (String) input.readObject();

            if (!ready_for_m.equals(Protocol.r_m)) {
                LOGGER.info("Not ready for messages");
                socket.close();
                return;
            }else {
                LOGGER.info("Server ready for messages");
            }

            for (int i = 0; i < n; i++) {
                Message message = new Message(i, "Message: " + i);
                output.writeObject(message);
                output.flush();
                LOGGER.info("Sent: " + message.getContent());
            }

            String finish = (String) input.readObject();
            if (!finish.equals(Protocol.f)) {
                LOGGER.info("Server did not finish properly.");
            }
            else{
                LOGGER.info("Server finished properly.");
            }

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        finally {
            LOGGER.info("Clossing");
            if(scanner!=null){
                scanner.close();
            }
        }
    }
}
