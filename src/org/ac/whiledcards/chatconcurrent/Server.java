package org.ac.whiledcards.chatconcurrent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) {
        Server server = new Server();
    }



    private ServerSocket socket;
    private MessageManager manager;
    private LinkedList<ServerWorker> list;
    private ExecutorService threadPool;

    public Server() {
        try {
            socket = new ServerSocket(8081);
        } catch (IOException e) {
            e.printStackTrace();
        }

        list = new LinkedList<>();
        threadPool = Executors.newCachedThreadPool();
        manager = new MessageManager();
        Thread managerThread = new Thread(manager);
        managerThread.setName("Manager Thread");
        managerThread.run();
        start();

    }

    private void start() {
        System.out.println("HERE");
            try {
                Socket clientSocket = socket.accept();
                ServerWorker client = new ServerWorker(clientSocket);
                list.add(client);
                threadPool.submit(client);
            } catch (IOException e) {
                e.printStackTrace();
            }
            start();
    }

    private void performCommand(String line){
        switch (line){
            case "/list":
                for (ServerWorker client: list) {
                    manager.sendMessage(client.getName());
                }
        }

    }




    private class ServerWorker implements Runnable {

        private Socket clientSocket;
        private String name;
        private PrintWriter output;
        private BufferedReader input;

        private ServerWorker(Socket clientSocket){
            this.clientSocket = clientSocket;
            name = "USER " + (list.size() + 1);
            System.out.println(name + " is now connected");
            try {
                output = new PrintWriter(this.clientSocket.getOutputStream(), true);
                input = new BufferedReader( new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean condition = true;
            System.out.println(name + " is on thread n " + Thread.currentThread().getName());
            while (condition){
                try {
                    String line = input.readLine();
                    if(MessageHelper.isCommnand(line)){
                        performCommand(line);
                    }else{
                        manager.sendMessage(name + " :" + line);
                        //System.out.println(line);

                    }/* else {
                        input.close();
                        output.close();
                        clientSocket.close();
                        condition = false;
                    }*/

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void sendMessage(String message){
            output.println(message);
            output.flush();
        }

        public String getName(){
            return name;
        }
    }




    private class MessageManager implements Runnable{
        private String message = null;
        @Override

        public void run() {
        }

        public void sendMessage(String message){
            for (ServerWorker client: list) {
                client.sendMessage(message);
            }
        }
    }
}
