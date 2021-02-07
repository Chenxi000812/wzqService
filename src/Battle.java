import Entity.MsgToClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.net.Socket;
import java.util.Random;

public class Battle {
    private User userB;
    private User userW;
    private int[][] collList;
    private int round = 1;

    //分配黑白棋  定义 0:空白  1:白棋  2:黑棋
    public Battle(User userB, User userW) {
        this.userB = userB;
        this.userW = userW;
        init();
        userB.battleing(this.hashCode());
        userW.battleing(this.hashCode());
        userB.setBlack(true);
        userW.setBlack(false);
    }

    //开局初始化
    public void init() {
        collList = new int[21][21];
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        userW.sendMsg(JSON.toJSONString(new MsgToClient(MsgToClient.UROUND, null)));
    }

    //战斗结束
    public void exit() {
        this.userB.setFree();
        this.userW.setFree();
        String msg = JSON.toJSONString(new MsgToClient(MsgToClient.BATTLE_FINISHED, "游戏结束"));
        userB.sendMsg(msg);
        userW.sendMsg(msg);
        Handler.battleMap.remove(this.hashCode());
    }

    public void exit(String s) {
        this.userB.setFree();
        this.userW.setFree();
        String msg = JSON.toJSONString(new MsgToClient(MsgToClient.BATTLE_FINISHED, s));
        userB.sendMsg(msg);
        userW.sendMsg(msg);
        Handler.battleMap.remove(this.hashCode());
    }

    public void handler(String s, User user) {
        JSONObject jsonObject = JSON.parseObject(s);
        int a = jsonObject.getInteger("a");
        int b = jsonObject.getInteger("b");
        JSONObject jo = new JSONObject();
        jo.put("a",a);
        jo.put("b",b);
        int c;
        if (user.isBlack()){
            c = 2;
        }else {
            c = 1;
        }
        if (a<21&&b<21){
            if (collList[b][a] != 0){
                user.sendMsg(JSON.toJSONString(new MsgToClient(MsgToClient.ACCESS_DENIED, "此格子已用")));
                return;
            }
            if(round%2==1&& c == 1){
                collList[b][a] = c;
                userW.sendMsg(JSON.toJSONString(new MsgToClient(MsgToClient.ACCESS_SUCCESS, "")));
                userB.sendMsg(JSON.toJSONString(new MsgToClient(MsgToClient.UROUND, jo.toJSONString())));
                if (checkWin(a,b,c)){
                    return;
                }
            }else {
                collList[b][a] = c;
                userB.sendMsg(JSON.toJSONString(new MsgToClient(MsgToClient.ACCESS_SUCCESS, "")));
                userW.sendMsg(JSON.toJSONString(new MsgToClient(MsgToClient.UROUND, jo.toJSONString())));
                if (checkWin(a,b,c)){
                    return;
                }
            }

            round += 1;
        }
    }
    private boolean checkWin(int a ,int b,int c){
        int count = 1;
        //上下
        for (int i = 1;i<=5;i++){
            if (b - i >= 0){
                if (collList[b-i][a] == c){
                    count ++;
                }else {
                    break;
                }
            }
        }
        for (int i = 1;i<=5;i++){
            if (b + i < 21){
                if (collList[b+i][a] == c){
                    count ++;
                }else {
                    break;
                }
            }
        }
        if (count >=5){
            if (c==1){
                Handler.battleExit(this.hashCode(),"白棋赢了");
            }else {
                Handler.battleExit(this.hashCode(),"黑棋赢了");
            }
            return true;
        }

        count = 1;

        //左右
        for (int i = 1;i<=5;i++){
            if (a - i >= 0){
                if (collList[b][a-i] == c){
                    count ++;
                }
                else {
                    break;
                }
            }
        }
        for (int i = 1;i<=5;i++){
            if (a + i < 21){
                if (collList[b][a+i] == c){
                    count ++;
                }else {
                    break;
                }
            }
        }
        if (count >=5){
            if (c==1){
                Handler.battleExit(this.hashCode(),"白棋赢了");
            }else {
                Handler.battleExit(this.hashCode(),"黑棋赢了");
            }
            return true;
        }


        count = 1;


        //左上右下
        for (int i = 1;i<=5;i++){
            if (b - i >= 0 && a - i >= 0){
                if (collList[b-i][a-i] == c){
                    count ++;
                }else {
                    break;
                }
            }
        }
        for (int i = 1;i<=5;i++){
            if (a + i < 21 && b + i < 21){
                if (collList[b+i][a+i] == c){
                    count ++;
                }else {
                    break;
                }
            }
        }
        if (count >=5){
            if (c==1){
                Handler.battleExit(this.hashCode(),"白棋赢了");
            }else {
                Handler.battleExit(this.hashCode(),"黑棋赢了");
            }
            return true;
        }

        count = 1;


        //左下右上
        for (int i = 1;i<=5;i++){
            if (a - i >= 21 && b + i < 21){
                if (collList[b+i][a-i] == c){
                    count ++;
                }else {
                    break;
                }
            }
        }
        for (int i = 1;i<=5;i++){
            if (b - i >= 0 && a + i < 21){
                if (collList[b-i][a+i] == c){
                    count ++;
                }else {
                    break;
                }
            }
        }
        if (count >=5){
            if (c==1){
                Handler.battleExit(this.hashCode(),"白棋赢了");
            }else {
                Handler.battleExit(this.hashCode(),"黑棋赢了");
            }
            return true;
        }
        return false;
    }
}
