package Nasti.goodbadevil_12;
import android.util.Log;

import java.net.Socket;


public class ChatSocketHandler implements Thread.UncaughtExceptionHandler{

    private static Socket socket = null;
    private static Object lock = new Object();

    public static synchronized Socket getChatSocket(){
        while(socket == null) {
             synchronized (lock){  try {lock.wait();} catch (InterruptedException e) { e.printStackTrace();}}
            }
        return ChatSocketHandler.socket;
    }

    public  static synchronized void setSocket(Socket s){
        Log.d("ChatOutputHandler", "setted Socket");
        synchronized (lock){
        ChatSocketHandler.socket = s;
        lock.notify();}
    }
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("Game", "throwed unchaught exception on thread  "+t);
        e.printStackTrace();
    }
}