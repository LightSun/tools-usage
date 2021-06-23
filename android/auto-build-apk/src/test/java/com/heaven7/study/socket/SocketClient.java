package com.heaven7.study.socket;
 
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
 
public final class SocketClient {
 
    public static void main(String[] args) throws Exception {
        //execute(args[0], Integer.parseInt(args[1]));
        execute("server.natappfree.cc",38371);
    }
 
    public static void execute(String host, int port) throws Exception {
        Socket socket = null;
        socket = new Socket(host, port);
        //向服务器端第一次发送
        OutputStream netOut = socket.getOutputStream();
        DataOutputStream doc = new DataOutputStream(netOut);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        //向服务器端第二次发送
        doc.writeUTF("save");
        String res = in.readUTF();

        System.out.println("返回的接口名称 {}"+ res);
        String data = "{name:zhangsan, age:22}";
        doc.writeUTF(data);
        res = in.readUTF();
        System.out.println("返回的数据 {}" + res);
        doc.close();
        in.close();
        socket.close();
    }
}