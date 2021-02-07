import java.util.ArrayList;
import java.util.List;

public class UserList {
    public static List<User> users = new ArrayList<>();
    public static List<User> matchinglist = new ArrayList<>();
    public static void addUser(User user){
        users.add(user);
    }

    public static void UserDisconnected(User user){
        users.remove(user);
        matchinglist.remove(user);
        if (user.getBATTLEHASH()!=0){
            Handler.battleExit(user.getBATTLEHASH(),"您的对手离开了人世");
        }
    }
}
