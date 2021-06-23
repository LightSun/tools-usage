package com.heaven7.tool.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public final class SocketServerManager {

    public static void main(String[] args) {
        int port = 50051;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        SocketServerManager manager = new SocketServerManager();
        manager.doListen(port);
    }

    public void doListen(int port) {
        ServerSocket server;
        try {
            server = new ServerSocket(port);
            System.out.println("start listen at port: " + port);
            while (true) {
                Socket client = server.accept();
                new Thread(new SSocket(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class SSocket implements Runnable {
        Socket client;

        public SSocket(Socket client) {
            this.client = client;
        }
        public void run() {
            DataInputStream input;
            DataOutputStream output;
            try {
                input = new DataInputStream(client.getInputStream());
                output = new DataOutputStream(client.getOutputStream());
                Thread.sleep(5000L);
                String data = input.readUTF();
                output.writeUTF("Recive1:  " + data);

                System.out.println("接口名称 access {}: " + data);
                data = input.readUTF();
                output.writeUTF("Recive2:  " + data);
                System.out.println("接收的数据 {}" + data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}