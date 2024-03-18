package org.example;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;
public class S_Thread implements Runnable{
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Logger LOGGER;

    public S_Thread(ObjectOutputStream output, ObjectInputStream input, Logger logger) {
        this.output = output;
        this.input = input;
        this.LOGGER = logger;
    }


    @Override
    public void run() {
        try{
            output.writeObject(Protocol.r);

            int n = input.readInt();
            LOGGER.info("Recived "+n);

            output.writeObject(Protocol.r_m);
            for(int i =0;i<n;i++){
                Message message = (Message) input.readObject();
                LOGGER.info("Received message " + message.getContent());
            }
            output.writeObject(Protocol.f);
        }
        catch(IOException | ClassNotFoundException e){
            LOGGER.severe("Server exception: " + e.getMessage());
        }
    }
}
