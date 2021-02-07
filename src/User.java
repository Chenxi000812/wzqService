import Entity.MsgToClient;
import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.Socket;

public class User {
    public static final int FREE = 0;
    public static final int BATTLEING = 1;

    private Socket socket;
    private int status = FREE;
    private int BATTLEHASH;
    private boolean isBlack;

    public boolean isBlack() {
        return isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
        if (black){
            sendMsg(JSON.toJSONString(new MsgToClient(MsgToClient.URBLACK,"你是黑棋")));
        }else {
            sendMsg(JSON.toJSONString(new MsgToClient(MsgToClient.URWHITE,"你是白棋")));
        }

    }

    public void setFree(){
        BATTLEHASH = 0;
        this.status = FREE;
        UserList.matchinglist.remove(User.this);
    }
    public void battleing(int hash){
        status = BATTLEING;
        this.BATTLEHASH = hash;
        UserList.matchinglist.remove(User.this);
    }

    public Socket getSocket() {
        return socket;
    }

    public int getStatus() {
        return status;
    }

    public int getBATTLEHASH() {
        return BATTLEHASH;
    }

    public User(Socket socket) {
        this.socket = socket;
        startHeartBet();
        ReceiveMsg();
    }

    public void startHeartBet(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream out = null;
                try {
                    while (true){
                        Thread.sleep(1000);
                        out = socket.getOutputStream();
                        out.write("44332255 \n".getBytes());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (status == BATTLEING){
                        Battle battle = Handler.battleMap.get(BATTLEHASH);
                        battle.exit();
                    }
                    try {
                        socket.close();
                        out.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    UserList.UserDisconnected(User.this);
                }

            }
        }).start();
    }

    public void ReceiveMsg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                        String line;
                        while ((line = reader.readLine())!=null){
                            line = line.trim();
                            Handler.HandlerMsg(line,User.this);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        UserList.UserDisconnected(User.this);
                        break;
                    }
                }
            }
        }).start();
    }

    public void sendMsg(String s){
        OutputStream out = null;
        try {
            out = socket.getOutputStream();
            out.write((s+"\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
