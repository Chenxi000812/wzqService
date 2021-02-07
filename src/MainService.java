import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainService {
    public static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(8686);
        //监听用户进入
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Socket socket = serverSocket.accept();
                        System.out.println("有用户加入");
                        UserList.addUser(new User(socket));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
