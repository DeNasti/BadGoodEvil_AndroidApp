package Nasti.goodbadevil_12;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

public class Result extends AppCompatActivity implements Thread.UncaughtExceptionHandler {

   private static String state;

    public static void setState(String state) {
        Result.state = state;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView resultView = (ImageView) findViewById(R.id.resultView);

        if(state.equals("VICTORY"))
            resultView.setImageResource(R.drawable.victory);

        else resultView.setImageResource(R.drawable.defeat);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("Game", "throwed unchaught exception on thread  "+t);
        e.printStackTrace();
    }

    @Override
    public void onBackPressed() {
        //la overrido per non permettere di tornare all'activity precendente
    }
}
