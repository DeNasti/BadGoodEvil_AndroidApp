package Nasti.goodbadevil_12;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MyAppliccation extends Application implements Thread.UncaughtExceptionHandler {
    private static Context context;
    private  static Activity act;
    @Override
    public void onCreate(){
        super.onCreate();
        MyAppliccation.context=getApplicationContext();
    }

    public static Context getContext(){
        return MyAppliccation.context;
    }

    public static Activity getCurrentActivity(){
        return  MyAppliccation.act;
    }

    public static void setActivity(Activity a){
        MyAppliccation.act = a;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("Game", "throwed unchaught exception on thread  "+t);
        e.printStackTrace();
    }
}
