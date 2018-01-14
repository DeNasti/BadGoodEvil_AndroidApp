package ibiondissimiprogrammatori.goodbadevil_12;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class figth_activity extends AppCompatActivity implements Thread.UncaughtExceptionHandler {

    private static String vsUsername;
    private static Object lock;
    private Button plus, minus, fight;
    private TextView enemyInfo, myInfo, ammo;
    private static int vsAmmo;
    private int actualAmmo = 1;
    private int maxAmmo;
    private static Player player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_figth_activity);

        MyAppliccation.setActivity(this);
        maxAmmo = player.getAmmo();


        View.OnClickListener c = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = ((Button) v).getText().toString();
                if(s.equals("plus")){
                    ammoUp();
                    ammo.setText(""+actualAmmo);
                }
                else  if(s.equals("minus")){
                    ammoDown();
                    ammo.setText(""+actualAmmo);
                }
                else if(s.equals("fight")){
                   new Thread(new Shoot()).start();
                }
            }
        };

        plus = (Button) findViewById(R.id.plus);
        minus  = (Button) findViewById(R.id.minus);
        fight= (Button) findViewById(R.id.send);
        plus.setOnClickListener(c);
        minus.setOnClickListener(c);
        fight.setOnClickListener(c);

        enemyInfo = (TextView) findViewById(R.id.enemyInfo);
        enemyInfo.setText(vsUsername+"\n"+"enemy ammo :"+vsAmmo);

        myInfo = (TextView) findViewById(R.id.myInfo);
        myInfo.setText(player.getNickName()+"\n"+"ammo :"+maxAmmo);

        ammo = (TextView) findViewById(R.id.ammos);
        ammo.setText(""+actualAmmo);
    }

    void ammoUp(){
        if(actualAmmo<maxAmmo)
            actualAmmo++;
        return;
    }

    void ammoDown(){
        if(actualAmmo>1)
            actualAmmo--;
        return;
    }

    public static void setVsInfo(String vsUsername, int vsAmmo, Player p, Object lock) {
        figth_activity.vsAmmo = vsAmmo;
        figth_activity.player = p;
        figth_activity.vsUsername = vsUsername;
        figth_activity.lock = lock;
    }

    private class Shoot implements Runnable{
        String state;
        private Shoot(){
          state = "errore!";
        }
        @Override
        public void run() {
            synchronized (lock) {
                player.decreaseAmmo(actualAmmo);
                try {
                    player.getObjOut().writeObject(actualAmmo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                actualAmmo = 1;

                try {
                    String result = (String) player.getObjIn().readObject();
                    if (result.equals("WIN")) {
                        state = "YOU WIN!";
                        player.addAmmo((int) player.getObjIn().readObject());
                    } else state = "YOU LOSE!";
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        enemyInfo.setText("");
                        myInfo.setText(state);
                    }
                };
                new Handler(Looper.getMainLooper()).post(r);
                SystemClock.sleep(2500);
                lock.notify();
            }
           finish();
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("Game", "throwed unchaught exception on thread  "+t);
        e.printStackTrace();
    }
}
