package Nasti.goodbadevil_12;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MyTurn extends AsyncTask<Void, Void, Void> implements Thread.UncaughtExceptionHandler {
	private static Bitmap baseBitMap;
	private static Bitmap toUseBitmap;
	private ObjectInputStream objIn;
	private Player p;
	private GraphInfo graph;
	private ArrayList<String> bottoni = new ArrayList<>();
	private int[] locs;
	private int[] ammos;
	private Handler mainHandler;
	private static LinearLayout linearLl0;
	private static Boolean isSet =  false;
	private Canvas canvas;
	private static ViewFlipper viewFlip;
    final  private static Object setLock = new Object();

	static void setLLayoutAndViewFlipper(LinearLayout l, ViewFlipper v) {
		synchronized (setLock) {
			linearLl0 = l;
			viewFlip = v;
            setLock.notify();
		}
	}

	public MyTurn(Player p) {
		this.p = p;
		this.graph = p.getGraphInfo();
		objIn = p.getObjIn();
	}

	public static Bitmap getImage() {
        return  toUseBitmap;
	}

	private void update() {
		graph.setLocations(locs);
		Player.setMyLocation(graph.getLocations()[p.getMyIndex()]);
		graph.setAmmos(ammos);
		updateMap();
	}

	private void nextTurn(String state) {
		if (state.equals("GO"))
			bottoni = getAdjacentCities(Player.getMyLocation(), graph.getMatriceDiAdiacenza(), graph.getCityNames());

		 else if (state.equals("WAIT")) {
				if (!bottoni.isEmpty()) bottoni.clear();
				bottoni.add("WAIT");
			}
	}

	private void fightTurn(String state) {
		if (state.startsWith("FIGHT")) {

			try {
				String vsUsername = (String) objIn.readObject();
				int vsAmmo = (int) objIn.readObject();


                Object lock = new Object();

				if (!bottoni.isEmpty()) bottoni.clear();
				bottoni.add("FIGHT");

				setBottoni(bottoni);
				Activity l = MyAppliccation.getCurrentActivity();
				Intent i = new Intent(l, figth_activity.class);

				l.startActivity(i);
                figth_activity.setVsInfo(vsUsername, vsAmmo, p, lock);
				synchronized(lock){lock.wait();}

			} catch (ClassNotFoundException |InterruptedException | IOException e) {
				e.printStackTrace();
            }

        } else if (state.equals("NOF")) {
			if (!bottoni.isEmpty()) bottoni.clear();
			bottoni.add("FIGHT");
		}
		mainHandler.post(new Runnable(){
			@Override
			public void run() {
				viewFlip.setDisplayedChild(0);
			}
		});
		updateMap();
	}

	@Override
	protected Void doInBackground(Void... params) {
		mainHandler = new Handler(Looper.getMainLooper());
		try {
            ObjectOutputStream objOut = new ObjectOutputStream(p.getSocket().getOutputStream());
			p.setObjOut(objOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			nextTurn((String) objIn.readObject());
			setBottoni(bottoni);

			while (true) {
				if (objIn.readObject().equals("true")) {
					p.getSocket().close();
					System.exit(0);
				}

				locs = (int[]) objIn.readObject();
				ammos = (int[]) objIn.readObject();
				update();

				setBottoni(bottoni);
				fightTurn((String) objIn.readObject());

				locs = (int[]) objIn.readObject();
				ammos = (int[]) objIn.readObject();
				graph.setDeadPlayers((boolean[]) objIn.readObject());
				update();

				setBottoni(bottoni);
				String state = (String) objIn.readObject();
				if (state.equals("VICTORY") | state.equals("DEFEAT")) {
					Activity l = MyAppliccation.getCurrentActivity();
					Result.setState(state);
					Intent i = new Intent(l, Result.class);
					l.startActivity(i);
				}

				state = (String) objIn.readObject();
				if (state.equals("UGLY")) {
					int drop = (int) objIn.readObject();
					p.decreaseAmmo(drop);
					//cambia il numero di munizioni
				}
				nextTurn((String) objIn.readObject()); //tutti gli altri turni
				setBottoni(bottoni);
				//if state is GO scrivi quante ammo hai preso
				updateMap();

			}
			} catch (IOException | ClassNotFoundException e) {e.printStackTrace();}

		return null;
	}

	private void updateMap() {
		toUseBitmap= baseBitMap.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(toUseBitmap);
        drawPlayers();
	}

	private void setBottoni(ArrayList<String> bottoni) {
		int buttonsNumber = bottoni.size();
		Button button;
		android.view.View.OnClickListener listener = new android.view.View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String s = ((Button) view).getText().toString();

				if(!s.equals("WAIT"))
					new Thread(new Commands(s, p)).start();
			}
		};
		Log.d("MyTurn", "setBottoni - going to set bottoni");

		synchronized (setLock) {
			while (!isSet)
				try {
                    setLock.wait();
					isSet = true;
					Log.d("MyTurn", "setBottoni - in while");

				} catch (InterruptedException e) {e.printStackTrace();}
		}
		Log.d("MyTurn", "setBottoni - end of syncronizelion");

		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				linearLl0.removeAllViews();
			}
		});
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);



		int[] colors = new int[8];
		colors[0] = Color.parseColor("#15ffb9");
		colors[1] = Color.parseColor("#00fab0");
		colors[2] = Color.parseColor("#00e19e");
		colors[3] = Color.parseColor("#00c78c");
		colors[4] = Color.parseColor("#00ae7a");
		colors[5] = Color.parseColor("#009468");
		colors[6] =Color.parseColor("#007b56");
		colors[7] = Color.parseColor("#006244");

		for (int i = 0; i < buttonsNumber; i++) {
			button = new Button(MyAppliccation.getContext());
			button.setText(bottoni.get(i));
			button.setOnClickListener(listener);
            button.setLayoutParams(param);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(5, Color.DKGRAY);
			drawable.setColor(colors[i]);
			button.setBackgroundDrawable(drawable);
			mainHandler.post(new addButton(button));
			}
		}

	private ArrayList<String> getAdjacentCities(int location, int[][] matriceDiAdiacenza, ArrayList<String> cityNames) {
		ArrayList<String> adjCities = new ArrayList<String>();

		for (int i = 0; i < matriceDiAdiacenza[location].length; i++)
			if (matriceDiAdiacenza[i][location] == 1)
				adjCities.add(cityNames.get(i));

		return adjCities;
	}

	public static void manipulateImage(Bitmap baseImage, Bitmap bmpImage) {
		MyTurn.baseBitMap = baseImage;
		MyTurn.toUseBitmap = bmpImage;
	}

	private class Commands implements Runnable {

		private Player p;
		private ObjectOutputStream out;
		private String nextCity;
		private GraphInfo g;
		private ArrayList<String> cityNames;
		private int[] locations;
		private int[] fighters = new int[2];

		public Commands(String nextCity, Player p) {
			this.p = p;
			this.nextCity = nextCity;
			g = p.getGraphInfo();
			cityNames = g.getCityNames();
			locations = g.getLocations();
			out = p.getObjOut();
		}

		public int[] checkFight(int loc) {
			int[] locs = g.getLocations();
			fighters[0] = p.getMyIndex();
			fighters[1] = -1;
			for (int i = 0; i < locs.length; i++) {
				if ((p.getMyIndex() != i) && (loc == locs[i]) && !(p.getMyTeam().equals(g.getTeams().get(i)))) {
					fighters[1] = i;
				}
			}
			return fighters;
		}

		public int[] getRandomAmmo() {
			if (g.getAmmos()[p.getMyLocation()] != 0) {

				System.out.println("Ammo sul nodo in cui mi trovo " + g.getAmmos()[p.getMyLocation()]);

				int takenAmmo = (int) ((Math.random() * (g.getAmmos()[p.getMyLocation()] / 2)));
				if (takenAmmo == 0) takenAmmo = 1;
				System.out.println(p.getAmmo());
				System.out.println(takenAmmo + " ammo prese");
				p.addAmmo(takenAmmo);
				g.decreaseAmmos(p.getMyLocation(), takenAmmo);
				System.out.println(p.getAmmo());
			}
			return g.getAmmos();
		}

		public void sendMessage(Object obj) {
			try {
				out.reset();
				out.writeObject(obj);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		@Override
		public void run() {
			if (nextCity.equals("Exit")) {
				if (!p.getSocket().isClosed())
					sendMessage(true);
			} else {
				int newLoc = cityNames.indexOf(nextCity);
				p.setMyLocation(newLoc);
				locations[p.getMyIndex()] = newLoc;
				g.setLocations(locations);
                updateMap();
                sendMessage(false);
				sendMessage(locations);
				sendMessage(checkFight(Player.getMyLocation()));
				sendMessage(getRandomAmmo());
				sendMessage(p.getAmmo()); //si potrebbe fare aggiornamento nel server con le ammo
//   random prese sottratte a quelle del player
			}
			return;
		}
	}

	private class addButton implements Runnable {
		private Button b;
		public addButton(Button b) {this.b = b;}

		@Override
		public void run() {
            Log.d("wui", "string su bottone ");
            linearLl0.addView(b);}
		}

	private void drawPlayers(){

		Paint black = new Paint();
		black.setAntiAlias(true);
		black.setColor(Color.BLACK);
		black.setStyle(Paint.Style.STROKE);
        float testTextSize = 48f;
        String text = p.getNickName();
        Rect bounds = new Rect();
        black.getTextBounds(text, 0, text.length(), bounds);
        float desiredTextSize = testTextSize * 25f / bounds.width();
        black.setTextSize(desiredTextSize);

		Paint paintGood = new Paint();
		paintGood.setAntiAlias(true);
		paintGood.setColor(Color.BLUE);
		paintGood.setStyle(Paint.Style.STROKE);
		paintGood.setStrokeWidth(4f);

		Paint paintBad = new Paint();
		paintBad.setAntiAlias(true);
		paintBad.setColor(Color.RED);
		paintBad.setStyle(Paint.Style.STROKE);
		paintBad.setStrokeWidth(4f);


		ArrayList<String> teams = graph.getTeams();
		int[] coordinateX = graph.getCoordinateX();
		int[] coordinateY = graph.getCoordinateY();

		for(int i=0; i<graph.getUsernames().size(); i++) {
			int id = graph.getLocations()[i];



			if(teams.get(i).equals("Good"))
				canvas.drawCircle(coordinateX[id], coordinateY[id], 15, paintGood);


			else  //equals bad
				canvas.drawCircle(coordinateX[id], coordinateY[id], 15, paintBad);

			if(i==graph.getUsernames().indexOf(p.getNickName())) {//controllo di essere il player che sta venendo disegnato


                canvas.drawText(p.getNickName(), coordinateX[id]-33, coordinateY[id] - 33, black);
            }

		}
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Log.d("Game", "throwed unchaught exception on thread  "+t);
		e.printStackTrace();
	}
}