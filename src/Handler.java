import Entity.MsgToClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Handler {

    /**
     *  {
     *      "action": 0;
     *      "data":"";
     *  }
     * */

    public static final int ACTION_MATCHING = 0;
    public static final int ACTION_CANCLE_MATCHING = 1;
    public static final int ACTION_BATTLE_PlAY = 2;
    public static final int ACTION_BATTLE_EXIT= 3;
    public static Map<Integer,Battle> battleMap = new HashMap<>();

    public static void battleExit(Integer Hash){
        Battle battle = battleMap.get(Hash);
        battle.exit();
        battleMap.remove(Hash);
    }
    public static void battleExit(Integer Hash,String s){
        Battle battle = battleMap.get(Hash);
        battle.exit(s);
        battleMap.remove(Hash);
    }


    public static void HandlerMsg(String s,User user){
        JSONObject jsonObject = JSON.parseObject(s);
        Integer action = jsonObject.getInteger("action");

        if (action.equals(ACTION_MATCHING)){
            UserList.matchinglist.add(user);
            //如果匹配列表有两个 表示匹配成功
            if (UserList.matchinglist.size() == 2){
                User user1 = UserList.matchinglist.get(0);
                User user2 = UserList.matchinglist.get(1);
                String success = JSON.toJSONString(new MsgToClient(MsgToClient.MATCHING_SUCCESS, "匹配成功"));
                user1.sendMsg(success);
                user2.sendMsg(success);
                Battle battle = new Battle(user1,user2);
                battleMap.put(battle.hashCode(),battle);
                //通知客户端匹配成功
            }
        }

        if (action.equals(ACTION_BATTLE_PlAY)){
            Battle battle = Handler.battleMap.get(user.getBATTLEHASH());
            battle.handler(jsonObject.getString("data"),user);
        }

        if (action.equals(ACTION_CANCLE_MATCHING)){
            UserList.matchinglist.remove(user);
        }

        if (action.equals(ACTION_BATTLE_EXIT)){
            battleExit(user.getBATTLEHASH());
        }
    }
}
