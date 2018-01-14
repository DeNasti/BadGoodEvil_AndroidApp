package ibiondissimiprogrammatori.goodbadevil_12;


import android.util.Log;

import java.util.ArrayList;

public class GraphInfo implements Thread.UncaughtExceptionHandler {

    private static int[][] matriceDiAdiacenza;
    private static int[] coordinateX;
    private static int[] coordinateY;
    private static int[] gradoNodo;
    private static ArrayList<String> cityNames;
    private static ArrayList<String> usernames;
    private static ArrayList<String> teams;
    private static int[] locations;
    private static int[] ammos;
    private boolean[] deadPlayers;

    public GraphInfo() {}

    public int[] getCoordinateX() {
        return coordinateX;
    }

    public int[] getCoordinateY() {
        return coordinateY;
    }

    public int[] getGradoNodo() {
        return gradoNodo;
    }

    public ArrayList<String> getCityNames() {
        return cityNames;
    }

    public int[][] getMatriceDiAdiacenza() {
        return matriceDiAdiacenza;
    }

    public ArrayList<String> getUsernames() {
        return usernames;
    }

    public ArrayList<String> getTeams() {
        return teams;
    }

    public int[] getLocations() {
        return locations;
    }

    public int[] getAmmos() {
        return ammos;
    }

    public int getNodesNumber() {
        return coordinateX.length;
    }

    public void setCoordinateX(int[] coordX) {
        coordinateX = coordX;
    }

    public void setCoordinateY(int[] coordY) {
        coordinateY = coordY;
    }

    public void setMatriceDiAdiacenza(int[][] matr) {
        matriceDiAdiacenza = matr;
    }

    public void setGradoNodo(int[] grado) {
        gradoNodo = grado;
    }

    public void setCityNames(ArrayList<String> cityNames) {
        GraphInfo.cityNames = cityNames;
    }

    public void setUsernames(ArrayList<String> usernames) {
        GraphInfo.usernames = usernames;
    }

    public void setTeams(ArrayList<String> teams) {
        GraphInfo.teams = teams;
    }

    public void setLocations(int[] locations) {
        GraphInfo.locations = locations;
    }

    public void setAmmos(int[] ammos) {
        GraphInfo.ammos = ammos;
    }

    public void decreaseAmmos(int myLoc, int takenAmmo) {
        ammos[myLoc] -= takenAmmo;
    }

    public int getBadLocation() {
        return coordinateX.length-1;
    }

    public void setDeadPlayers(boolean[] deadPlayers) {
        this.deadPlayers = deadPlayers;
    }

    public  static String getMyCity(int loc) {
       return cityNames.get(loc);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d("Game", "throwed unchaught exception on thread  "+t);
        e.printStackTrace();
    }
}
