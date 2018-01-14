package Nasti.goodbadevil_12;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;

import android.os.AsyncTask;

public class Connection extends AsyncTask<String, Void, String> implements Thread.UncaughtExceptionHandler{

    private String ip;
    private String response;
    private Player player;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private Chat c;
    public Connection(Player player, String ip, Chat c) {
        this.ip = ip;
        this.player = player;
        this.c= c;
    }

    protected String  doInBackground(String... params) {
        try {
            socket = new Socket(ip, 9000);
            player.setSocket(socket);

            in = new BufferedReader(new InputStreamReader(player.getSocket().getInputStream()));
            response = in.readLine();   //ricevo Submit

            out = new PrintWriter(player.getSocket().getOutputStream());
            out.println(player.getNickName());  //invio il mio nick per vedere se disponibile
            out.flush();

            response = in.readLine();

            if (response.equals("FAIL")) {
                player.setNickName("FAIL");
                player.setConnection(false);
            }
            else if (response.startsWith("T")) {
                player.setBlack(Integer.parseInt(response.substring(1, response.indexOf("&"))));
                player.setWhite(Integer.parseInt(response.substring(response.indexOf("&") + 1)));
                player.setConnection(true);
            }
        } catch (IOException e) {e.printStackTrace();}
    return null;

    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("Game", "throwed unchaught exception on thread  "+t);
        e.printStackTrace();
    }
}
