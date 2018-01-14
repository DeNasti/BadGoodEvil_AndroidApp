package Nasti.goodbadevil_12;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;


public class Chat  implements  Runnable, Thread.UncaughtExceptionHandler {
    private static Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Player player;
    private String ip;
    private ArrayAdapter adapter;
    private String team;
    private ArrayList<String> messages = new ArrayList<String>();
    static private ListView msgList;
    private Handler mainHandler;

    public Chat(String ip, Player player, String team) {
        this.player = player;
        this.ip = ip;
        this.team = team;
    }

    public static  void setListView(ListView l){
        msgList =l;
    }

    public  void close(){
        out.println("true");
        try {
            if (!socket.isInputShutdown())
                socket.shutdownInput();

            if (!socket.isOutputShutdown())
                socket.shutdownOutput();
        }catch(IOException e){Log.e("socket",e.toString());}
    }

    @Override
    public void run() {
        try {
             mainHandler = new Handler(Looper.getMainLooper());

            socket = new Socket(ip, 9001);
            socket.setTcpNoDelay(true);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            ChatSocketHandler.setSocket(socket);

        } catch (IOException e) { Log.e("Chat", e.toString()); }

        out.println(team);
        out.println(player.getNickName());

        while (true){
            try {
                String line = in.readLine();
                if(line==null){
                    socket.close();
                    return;
                }
                    if(adapter == null){
                        adapter = new ArrayAdapter<String>(MyAppliccation.getContext(),android.R.layout.simple_list_item_1, android.R.id.text1,messages);
                        Runnable myRun = new Runnable() {  @Override public void run() { msgList.setAdapter(adapter);   }};
                        mainHandler.post(myRun);
                    }

                    messages.add(line.substring(8)+"");
                Runnable myRun2 = new Runnable() {  @Override public void run() {adapter.notifyDataSetChanged();}};
                mainHandler.post(myRun2);

            } catch (IOException e) {Log.d("chat", e.toString());}
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("Game", "throwed unchaught exception on thread  "+t);
        e.printStackTrace();
    }

}
