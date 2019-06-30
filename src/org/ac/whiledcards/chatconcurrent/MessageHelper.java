package org.ac.whiledcards.chatconcurrent;

public class MessageHelper {
    public static boolean isCommnand(String message){

        String command = message.split(" ")[0];
        switch (message){
            case "/list":
            case "/name":
            case "/kick":
                return true;
        }

        return false;
    }
}
