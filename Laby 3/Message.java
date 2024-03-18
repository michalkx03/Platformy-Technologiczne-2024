package org.example;

import java.io.Serializable;
public class Message implements Serializable {
    private int number;
    private String content;
    public Message(int id, String message) {
        this.number = number;
        this.content = message;
    }
    public String getContent(){
        return content;
    }

    public int getNumber(){
        return number;
    }

}