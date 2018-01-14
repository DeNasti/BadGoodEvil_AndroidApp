package ibiondissimiprogrammatori.goodbadevil_12;

import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import android.os.Looper;
import android.os.Handler;
import android.widget.Button;
import android.widget.ViewFlipper;


public class Player extends Observable implements Thread.UncaughtExceptionHandler{

    private static int myLocation;
    private static int myIndex;
    private static String nickName;
    private static Socket socket;
    private int nBlack; //indica il numero di giocatori neri
    private int nWhite; //indica il numero di giocatori bianchi
    private static String myTeam;
    private static Boolean isConnected = false;
    private static GraphInfo g = new GraphInfo();
    private ObjectInputStream objIn;
    private int ammo = 3;
    private ObjectOutputStream objOut;
    private int difference = 0;

    public static int getMyLocation() {
        return myLocation;
    }

    public int getMyIndex() {
        return myIndex;
    }

    public String getNickName(){
        return nickName;
    }

    public void setNickName(String nickName){
       this.nickName = nickName;
        notifyObservers();
    }

    public Socket getSocket(){
        return socket;
    }

    public void setSocket(Socket socket){
        this.socket = socket;
    }

    public void setWhite(int white){
        this.nWhite = white;
        Log.d("Player", "white settati");

    }

    public int getWhite(){
        return nWhite;
    }

    public void setBlack(int black){
        this.nBlack = black;
        Log.d("Player", "black settati");

    }

    public int getBlack(){
        return nBlack;
    }

    public void setConnection(Boolean a){
        Log.d("Player", "isConnected modificata a "+a);
        isConnected = a;
        notifyObservers();
    }

    public Boolean getConnectionState() {

        return isConnected;
    }

    public GraphInfo getGraphInfo(){return g;}

    public void updateGUI(final ViewFlipper viewFlipper, String w, String b, Button whitesButton, Button blacksButton){

        Handler mainHandler = new Handler(Looper.getMainLooper());

        final String w1 = w;
        final String b1 = b;
        final Button wb = whitesButton;
        final Button bb = blacksButton;
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {

                wb.setText(w1);
                bb.setText(b1);
                viewFlipper.setDisplayedChild(1);
            }
        };
        mainHandler.post(myRunnable);
    }

    public void setMyTeam(String team){
        myTeam =team;
    }

    public String getMyTeam() {return  myTeam;}

    @Override
    public void notifyObservers(){
        setChanged();
        super.notifyObservers();
    }

    public static void setMyLocation(int i) {
        myLocation = i;
    }

    public static void setMyIndex(int i) {myIndex = i;    }

    public void setObjIn(ObjectInputStream objIn) {
        this.objIn = objIn;
    }

    public ObjectInputStream getObjIn() {
        return objIn;
    }

    public void addAmmo(int i) {
        ammo += i;
        difference = i;
    }

    public int getAmmo() {
        return ammo;
    }

    public ObjectOutputStream getObjOut() {
        return objOut;
    }

    public void setObjOut(ObjectOutputStream objOut) {
        this.objOut = objOut;
    }

    public void decreaseAmmo(int drop) {
        ammo-=drop;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("Game", "throwed unchaught exception on thread  "+t);
        e.printStackTrace();
    }
}
