package org.ac.whiledcards.chatconcurrent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    public static void main(String[] args) {
        Client client = new Client();
    }



    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private ExecutorService executorService;

    public Client(){
        start();
    }

    public void start(){
        String host = "";
        int port = 0;
        System.out.println("Hostmane");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            host = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Port");
        try {
            port = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        connect(host,port);
        msgConsoleToServer(in);
    }

    private void connect(String host, int port){
        try {
            socket = new Socket(InetAddress.getByName(host), port);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new MessageReader());
    }

    private void msgConsoleToServer(BufferedReader in){
        System.out.println("HERE 1");

        while (true){
            try {
                String message = in.readLine();
                output.println(message);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class MessageReader implements Runnable{

        @Override
        public void run() {
            System.out.println("HERE 2");
            readMsgFromConsole();
        }

        private void readMsgFromConsole(){
            while (true){

                try {
                    String message = input.readLine();
                    System.out.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
