package com.fatjoni.droid.gjobaonline.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fatjoni.droid.gjobaonline.model.Gjobe;
import com.fatjoni.droid.gjobaonline.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by me on 9/19/2015.
 */
public class GjobaDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "gjoba.db";

    public GjobaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_VEHICLE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                GjobaContract.VehicleEntry.TABLE_NAME + "(" +
                GjobaContract.VehicleEntry._ID + " INTEGER PRIMARY KEY," +
                GjobaContract.VehicleEntry.COLUMN_PLATE + " TEXT UNIQUE NOT NULL," +
                GjobaContract.VehicleEntry.COLUMN_VIN + " TEXT UNIQUE NOT NULL);";

        final String CREATE_GJOBA_TABLE = "CREATE TABLE " +
                GjobaContract.GjobaEntry.TABLE_NAME + "(" +
                GjobaContract.GjobaEntry._ID + " INTEGER PRIMARY KEY," +
                GjobaContract.GjobaEntry.COLUMN_VEHICLE_ID + " INTEGER UNIQUE NOT NULL REFERENCES " + GjobaContract.VehicleEntry.TABLE_NAME +
                " (" + GjobaContract.VehicleEntry._ID + ") ON DELETE CASCADE," +
                GjobaContract.GjobaEntry.COLUMN_NR_TOTAL_PER_AUTOMJET + " INTEGER NOT NULL DEFAULT (0)," +
                GjobaContract.GjobaEntry.COLUMN_VLERA_TOTAL_PER_AUTOMJET + " DOUBLE NOT NULL DEFAULT (0));";

        db.execSQL(CREATE_VEHICLE_TABLE);
        db.execSQL(CREATE_GJOBA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + GjobaContract.VehicleEntry.TABLE_NAME);
        onCreate(db);
    }

    public boolean areGjobaOnDB(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + GjobaContract.VehicleEntry._ID + " FROM " + GjobaContract.VehicleEntry.TABLE_NAME + " LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            return true;
        }
        cursor.close();

        return false;
    }

    public int getVehicleIDByPlate(String plate){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + GjobaContract.VehicleEntry._ID + " FROM " + GjobaContract.VehicleEntry.TABLE_NAME +
                " WHERE " + GjobaContract.VehicleEntry.COLUMN_PLATE + " = '" + plate + "'";

        Cursor cursor = db.rawQuery(query, null);

        int vehicleId = -9;
        if (cursor.moveToFirst()){
            vehicleId = cursor.getInt(0);
        }
        cursor.close();

        return vehicleId;

    }

    public long insertVehicle(Vehicle vehicle){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GjobaContract.VehicleEntry.COLUMN_PLATE, vehicle.getPlate());
        values.put(GjobaContract.VehicleEntry.COLUMN_VIN, vehicle.getVin());

        long vehicleId = db.insert(GjobaContract.VehicleEntry.TABLE_NAME, null, values);
        db.close();
        return vehicleId;
    }

    public long insertGjobe(Gjobe gjobe){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GjobaContract.GjobaEntry.COLUMN_VLERA_TOTAL_PER_AUTOMJET, gjobe.getVlera());
        values.put(GjobaContract.GjobaEntry.COLUMN_NR_TOTAL_PER_AUTOMJET, gjobe.getNumri());
        values.put(GjobaContract.GjobaEntry.COLUMN_VEHICLE_ID, gjobe.getVehicleId());

        long vehicleId = db.insert(GjobaContract.GjobaEntry.TABLE_NAME, null, values);
        db.close();
        return vehicleId;
    }

    public int updateGjobe(int vehicleId, int numri, Double vlera){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GjobaContract.GjobaEntry.COLUMN_NR_TOTAL_PER_AUTOMJET, numri);
        values.put(GjobaContract.GjobaEntry.COLUMN_VLERA_TOTAL_PER_AUTOMJET, vlera);

        final String whereClause = GjobaContract.GjobaEntry.COLUMN_VEHICLE_ID + "=" + vehicleId;
        int i = db.update(GjobaContract.GjobaEntry.TABLE_NAME, values, whereClause, null);
        db.close();
        return i;
    }

    public List<Gjobe> getAllGjobat() {
        ArrayList<Gjobe> gjobat = new ArrayList<>();
        String query = "SELECT * FROM " + GjobaContract.GjobaEntry.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Gjobe gjobe;
        if (cursor.moveToFirst()) {
            do {
                gjobe = new Gjobe(cursor.getInt(0), cursor.getInt(2), cursor.getDouble(3), cursor.getInt(1));
                gjobat.add(gjobe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return gjobat;
    }

    public List<Vehicle> getAllVehicles() {
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM " + GjobaContract.VehicleEntry.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Vehicle vehicle;
        if (cursor.moveToFirst()) {
            do {
                vehicle = new Vehicle(cursor.getInt(0), cursor.getString(2), cursor.getString(1));
                vehicles.add(vehicle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return vehicles;
    }

    public Double getTotalGjobeValue() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT SUM(" + GjobaContract.GjobaEntry.COLUMN_VLERA_TOTAL_PER_AUTOMJET +
                ") FROM " + GjobaContract.GjobaEntry.TABLE_NAME;

        Double total = 0.0;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        } else {
            Log.e("Gabim", "Nuk ka gjoba ne databaze");
        }

        cursor.close();

        return total;
    }

    public Vehicle getVehicle(int _id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + GjobaContract.VehicleEntry.TABLE_NAME +
                " WHERE " + GjobaContract.VehicleEntry._ID + " = " + _id;

        Vehicle vehicle = null;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            vehicle = new Vehicle(cursor.getInt(0), cursor.getString(2), cursor.getString(1));
        } else {
            Log.e("Gabim", "Nuk ekziston automjet me kete id ne databaze");
        }

        cursor.close();

        return vehicle;
    }

    public void deleteFromDB(int vehicleId){
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();

        //Execute sql query to remove from database
        //NOTE: When removing by String in SQL, value must be enclosed with ''
        database.execSQL("DELETE FROM " + GjobaContract.GjobaEntry.TABLE_NAME + " WHERE " + GjobaContract.GjobaEntry.COLUMN_VEHICLE_ID + "= '" + vehicleId + "'");
        database.execSQL("DELETE FROM " + GjobaContract.VehicleEntry.TABLE_NAME + " WHERE " + GjobaContract.VehicleEntry._ID + "= '" + vehicleId + "'");
        //Close the database
        database.close();
    }

/*
    public long createVehicle(Vehicle vehicle) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GjobaContract.VehicleEntry.COLUMN_PLATE, vehicle.getPlate());
        values.put(GjobaContract.VehicleEntry.COLUMN_VIN, vehicle.getVin());

        long vehicleId = db.insert(GjobaContract.VehicleEntry.TABLE_NAME, null, values);
        db.close();
        return vehicleId;
    }

    public ArrayList<Vehicle> getAllVehicles() {
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM " + GjobaContract.VehicleEntry.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Vehicle vehicle = null;
        if (cursor.moveToFirst()) {
            do {
                //vehicle = new Vehicle(cursor.getString(1), cursor.getString(2));
                vehicles.add(vehicle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return vehicles;
    }

    public Vehicle getVehicle(int _id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + GjobaContract.VehicleEntry.TABLE_NAME +
                " WHERE " + GjobaContract.VehicleEntry._ID + " = " + _id;

        Vehicle vehicle = null;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            //vehicle = new Vehicle(cursor.getString(1), cursor.getString(2));
        } else {
            Log.e("Gabim", "Nuk ekziston automjet me kete id ne databaze");
        }

        cursor.close();

        return vehicle;
    }

    // Gjobe DB helper methods.
    public void createGjobe(int vehicleId, int nr, Double vlera) {
        */
/*
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GjobaContract.GjobaEntry.COLUMN_NR_TOTAL_PER_AUTOMJET, nr);
        values.put(GjobaContract.GjobaEntry.COLUMN_VLERA_TOTAL_PER_AUTOMJET, vlera);
        values.put(GjobaContract.GjobaEntry.COLUMN_VEHICLE_ID, vehicleId);

        if (getGjobe(vehicleId) != null) {
            db.update(GjobaContract.GjobaEntry.TABLE_NAME,
                    values,
                    GjobaContract.GjobaEntry.COLUMN_VEHICLE_ID + " = " + vehicleId,
                    null);
        } else {
            db.insert(GjobaContract.GjobaEntry.TABLE_NAME, null, values);
        }

        db.close();
        *//*

        String query = "REPLACE INTO " + GjobaContract.GjobaEntry.TABLE_NAME +
                " ("+ GjobaContract.GjobaEntry.COLUMN_VEHICLE_ID+", "+ GjobaContract.GjobaEntry.COLUMN_NR_TOTAL_PER_AUTOMJET +
                ", "+ GjobaContract.GjobaEntry.COLUMN_VLERA_TOTAL_PER_AUTOMJET+") VALUES (" +
                vehicleId + "," + nr + "," + vlera + ")";

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);

    }

    public void updateGjobe(Gjobe gjobe){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(GjobaContract.GjobaEntry.COLUMN_NR_TOTAL_PER_AUTOMJET, gjobe.getNrTotalPerAutomjet());
        //values.put(GjobaContract.GjobaEntry.COLUMN_VLERA_TOTAL_PER_AUTOMJET, gjobe.getVleraTotalPerAutomjet());

        //final String whereClause = GjobaContract.GjobaEntry.COLUMN_VEHICLE_ID + "=" + gjobe.getVehicleId();
        //db.update(GjobaContract.GjobaEntry.TABLE_NAME, values, whereClause, null);
        db.close();
    }

    public ArrayList<Gjobe> getAllGjoba() {
        ArrayList<Gjobe> gjobaList = new ArrayList<>();
        String query = "SELECT * FROM " + GjobaContract.GjobaEntry.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Gjobe gjobe = null;
        if (cursor.moveToFirst()) {
            do {
                gjobe = new Gjobe(cursor.getInt(2), cursor.getDouble(3));
                gjobaList.add(gjobe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return gjobaList;
    }

    public int getTotalGjobeNumber() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT SUM(" + GjobaContract.GjobaEntry.COLUMN_NR_TOTAL_PER_AUTOMJET +
                ") FROM " + GjobaContract.GjobaEntry.TABLE_NAME;

        int total = 0;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        } else {
            Log.e("Gabim", "Nuk ka gjoba ne databaze");
        }

        cursor.close();

        return total;
    }

    public Double getTotalGjobeValue() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT SUM(" + GjobaContract.GjobaEntry.COLUMN_VLERA_TOTAL_PER_AUTOMJET +
                ") FROM " + GjobaContract.GjobaEntry.TABLE_NAME;

        Double total = 0.0;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        } else {
            Log.e("Gabim", "Nuk ka gjoba ne databaze");
        }

        cursor.close();

        return total;
    }

    public Gjobe getGjobe(int _id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + GjobaContract.GjobaEntry.TABLE_NAME +
                " WHERE " + GjobaContract.GjobaEntry._ID + "= " + _id;

        Gjobe gjobe = null;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            //gjobe = new Gjobe(_id, cursor.getInt(2), cursor.getDouble(3));
        } else {
            Log.e("Gabim", "Nuk ekziston automjet me kete id " + _id + " ne databaze");
        }

        cursor.close();

        return gjobe;
    }

    public int getMaxVehicleId() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT MAX(" + GjobaContract.VehicleEntry._ID +
                ") FROM " + GjobaContract.VehicleEntry.TABLE_NAME;

        int maxVal = 0;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            maxVal = cursor.getInt(0);
        } else {
            Log.e("Gabim", "Nuk ka automjete ne databaze");
        }

        cursor.close();

        return maxVal;
    }
*/

}
