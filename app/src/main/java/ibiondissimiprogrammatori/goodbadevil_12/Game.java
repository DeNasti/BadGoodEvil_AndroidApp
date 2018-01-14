package ibiondissimiprogrammatori.goodbadevil_12;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.io.PrintWriter;


import android.widget.Button;


public class Game extends  Activity implements View.OnTouchListener, Thread.UncaughtExceptionHandler {

    static int SCREEN_WIDTH;
    static int SCREEN_HEIGHT ;
    private ViewFlipper viewFlip;

    private float startX = 0;
    private ImageView imageView;
    private Bitmap bmpImage;
    private int xPos = 0;
    private EditText chatInMessage;

    final private static Object mutex1 = new Object();
    private TextView currentCity;

    private static Bitmap bitmap;
    private PrintWriter out;

    public static void setBitmap(Bitmap bitmap) {
        synchronized (mutex1){
            Game.bitmap = bitmap;
            mutex1.notify();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_game);
        MyAppliccation.setActivity(this);

        viewFlip = (ViewFlipper) findViewById(R.id.content_game);   //inizializzo il viewFlipper

        ListView listView = (ListView) findViewById(R.id.msgview);
        currentCity = (TextView) findViewById(R.id.cityView);
        currentCity.setText("");
        updateCity();
        Chat.setListView(listView);

        MyTurn.setLLayoutAndViewFlipper((LinearLayout) findViewById(R.id.col0) , viewFlip);   //(TextView)findViewById(R.id.v0),

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnTouchListener(this);

        FloatingActionButton sw1 = (FloatingActionButton) findViewById(R.id.switchFromMap);
        sw1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                viewFlip.setDisplayedChild(1);
            }});

        FloatingActionButton swMove = (FloatingActionButton) findViewById(R.id.switchToMove);
        swMove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                viewFlip.setDisplayedChild(2);
            }});

        Button sendMessage = (Button) findViewById(R.id.send);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        chatInMessage = (EditText) findViewById(R.id.chatmsg);
                            try {
                                if(out==null)
                                    out = new PrintWriter(ChatSocketHandler.getChatSocket().getOutputStream(), true);
                                else Log.d("in chat-game","out is not null");

                                Log.d("in chat-game", "here is kinda of fine");
                                out.println("false");
                                out.println(chatInMessage.getText().toString());
                            } catch (IOException e){e.printStackTrace();}
                        runOnUiThread(new Runnable() {  @Override public void run() { chatInMessage.setText("");}});
                    }
            }).start();
            }
        }
        );
        createMapView();
        }

    public  void createMapView(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;

            while(bitmap == null)
                synchronized (mutex1){
                    try {mutex1.wait();} catch (InterruptedException e) { e.printStackTrace(); }
                }
        bmpImage = bitmap;
        MyTurn.manipulateImage(Bitmap.createBitmap(bmpImage) , bmpImage);
        renderImage();
        }

    private void setPos(int x){
        if (x <= 0)
            xPos = 0;

        else if (x > bmpImage.getWidth()-SCREEN_WIDTH)
            xPos =  bmpImage.getWidth()-SCREEN_WIDTH;
        else xPos = x;
        }

    private void renderImage(){

        runOnUiThread(new Runnable(){ @Override
        public void run(){
            Bitmap b =  MyTurn.getImage();
            Bitmap renderImg = Bitmap.createBitmap(b, xPos ,0, SCREEN_WIDTH,  b.getHeight());
            imageView.setImageBitmap(renderImg);
            }});
     }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            startX = event.getRawX()+xPos;
            break;

            case MotionEvent.ACTION_MOVE:
            float x = event.getRawX();
            setPos (-(int)(x - startX));
            renderImage();
            break;
        }
        return true;
        }

    @Override
    public void onBackPressed() {
    updateCity();
    }

    private void updateCity() {
        viewFlip.setDisplayedChild(0);
        String s = "you are in \n"+GraphInfo.getMyCity(Player.getMyLocation());
        currentCity.setText(s);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("Game", "throwed unchaught exception on thread  "+t);
        e.printStackTrace();
    }
}