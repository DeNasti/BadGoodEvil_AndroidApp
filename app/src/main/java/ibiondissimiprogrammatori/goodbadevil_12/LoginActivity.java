package ibiondissimiprogrammatori.goodbadevil_12;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

public class LoginActivity extends AppCompatActivity implements Observer, Thread.UncaughtExceptionHandler {

    private ViewFlipper viewFlipper;
    private EditText usernameView;
    private EditText ipView;
    private Player player = new Player();
    private Connection connection;
  //  private String ip = "192.168.1.88";
    private String ip = "192.168.1.79";
    //private String ip = "192.168.43.163";
    private String[] RandomNames = {"Scar"," Walker", "Django", "Backfire","Bullseye", "Loner", "Eagle", "Spenzow", "Talon", "Brew", "Trivet", "Gonzalo", "Rodrigo", "Caruso","Capone", "Mastio" };
    private  Button whitesButton;
    private  Button blacksButton;
    private Chat c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyAppliccation.setActivity(this);
        setContentView(R.layout.switching ); //setto il layout
        viewFlipper = (ViewFlipper) findViewById(R.id.switching);   //inizializzo il viewFlipper

        player.addObserver(this);
        usernameView = (EditText) findViewById(R.id.username);
        ipView = (EditText) findViewById(R.id.ip);
        ipView.setText(ip);

        usernameView.setError(null);

        Button logInButton = (Button) findViewById(R.id.loginButton);
        logInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String username =  usernameView.getText().toString();
                ip = ipView.getText().toString();

                if(username.equals("")) {
                     usernameView.setError(getString(R.string.needed_username));
                }

                else {
                    Log.d("in \"LoginActivity\"","going to get the username and call new connection");
                    player.setNickName(username);
                    connection = new Connection(player, ip, null);
                    connection.execute("START");
                }
                Log.d("in \"LoginActivity\"","end of click");

            }
        });

        Button RandomNameButton = (Button) findViewById(R.id.RandomPlayer);
        RandomNameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    String aux = RandomNames[(int)(Math.random() * RandomNames.length)];
                    usernameView.setText(aux);
                    usernameView.setError(null);
            }
        });

        whitesButton = (Button) findViewById(R.id.whites);
        blacksButton = (Button) findViewById(R.id.blacks);
        whitesButton.setTypeface(Typeface.SANS_SERIF);
        blacksButton.setTypeface(Typeface.SANS_SERIF);

        OnClickListener listen = new OnClickListener() {
            @Override
            public void onClick(View view) {
                        String team1 = ((Button)view).getText().toString();

                if (team1.contains("Bad"))
                    team1="Bad";

                else team1 = "Good";

                final String team= team1;

                player.setMyTeam(team1);

                new Thread(new Runnable() {
                    @Override
                    public void run(){
                        try {
                        PrintWriter out = new PrintWriter(player.getSocket().getOutputStream());
                        Log.d("login Actity", "sto per madare il team scelto a"+player.getSocket());
                        out.println(team);
                        out.flush();
                        Log.d("login Actity", "ho mandato il team scelto");

                        c = new Chat(ip, player, team);
                        new Thread(c).start();

                        StartingGame start = new StartingGame(player, LoginActivity.this, c);
                        start.execute();
                        }catch (IOException e){e.printStackTrace();}
                    }
                }).start();
                viewFlipper.setDisplayedChild(2);
                    }
                };

        whitesButton.setOnClickListener(listen);
        blacksButton.setOnClickListener(listen);
    }

    @Override
    public void update(Observable observable, Object data) {  //il player manda la notifica dopo aver modificato la variabile

        if(player.getConnectionState()) {
                String w1 = "Good "+player.getWhite();
                String b1 = "Bad "+player.getBlack();
                player.updateGUI(viewFlipper, w1, b1,  whitesButton, blacksButton);
        }

            else if(player.getNickName().equals("FAIL")){
                usernameView.setError(getString(R.string.invalid_username));
                usernameView.setText("");
            }
        }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("Game", "throwed unchaught exception on thread  "+t);
        e.printStackTrace();
    }

}