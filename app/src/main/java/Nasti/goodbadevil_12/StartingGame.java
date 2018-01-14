package Nasti.goodbadevil_12;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.net.Socket;
import java.util.ArrayList;

public class StartingGame extends AsyncTask<String, Void, String> implements Thread.UncaughtExceptionHandler {
        private ObjectInputStream objIn;
        private GraphInfo graphInfo;
        private Socket s;
        private Player player;
        private LoginActivity l;
        private Chat c;
        private Bitmap bmp;

    public StartingGame(Player player, LoginActivity l, Chat c) {
        s = player.getSocket();
        graphInfo = player.getGraphInfo();
        this.player = player;
        this.l = l;
        this.c = c;
        try {
            objIn = new ObjectInputStream(player.getSocket().getInputStream());
            player.setObjIn(objIn);
        }catch(IOException e){e.printStackTrace();}
    }

    protected String  doInBackground(String... params) {

    try{
        bmp = receiveFile(player.getSocket().getInputStream(), "myImage.png");
        Game.setBitmap(bmp);
        }
    catch (Exception e) {Log.e("FileStreams",e.toString());}

        try {

            graphInfo = new GraphInfo();
            graphInfo.setCoordinateX((int[]) objIn.readObject());
            graphInfo.setCoordinateY((int[]) objIn.readObject());
            graphInfo.setGradoNodo((int[]) objIn.readObject());
            graphInfo.setCityNames((ArrayList<String>) objIn.readObject());
            graphInfo.setMatriceDiAdiacenza((int[][]) objIn.readObject());
            graphInfo.setAmmos((int[]) objIn.readObject());
            graphInfo.setUsernames((ArrayList<String>)objIn.readObject());
            graphInfo.setTeams((ArrayList<String>) objIn.readObject());
            graphInfo.setLocations((int[]) objIn.readObject());

            Log.d("Nasti", "ho concluso l'inzializzazioe dei dati");

            if (player.getMyTeam().equals("Good"))
                Player.setMyLocation(0);
            else
                Player.setMyLocation(graphInfo.getBadLocation());

            Log.d("Nasti", "eccomiiii");

            Player.setMyIndex(graphInfo.getUsernames().indexOf(player.getNickName()));

        } catch (IOException | ClassNotFoundException e) {e.printStackTrace();}

        Log.d("Nasti", "StartingGame -return from Doing in background");

        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        Log.d("Nasti", "Startig Game- On Post Execute");

        Intent i = new Intent(l, Game.class);
        l.startActivity(i);

        player.setObjIn(objIn);

        MyTurn m = new MyTurn(player);
        m.execute();
    }

    public Bitmap receiveFile(InputStream is, String fileName) throws Exception
    {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileInES = baseDir + File.separator + fileName;

        int fileSize = (int) objIn.readObject();
        byte[] data = new byte[8 * 1024];

        int bToRead;
        FileOutputStream fos = new FileOutputStream(fileInES);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        while (fileSize > 0)
        {
            if (fileSize > data.length)
                bToRead = data.length;
            else
                bToRead = fileSize;

            int bytesRead = is.read(data, 0, bToRead);

            if (bytesRead > 0) {
                bos.write(data, 0, bytesRead);
                fileSize -= bytesRead;
            }
        }

        bos.close();

        Bitmap bmp = null;
        FileInputStream fis = new FileInputStream(fileInES);

        try {
            bmp = BitmapFactory.decodeStream(fis);
            return bmp;
        }
        finally{ fis.close();}
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("Game", "throwed unchaught exception on thread  "+t);
        e.printStackTrace();
    }

}
