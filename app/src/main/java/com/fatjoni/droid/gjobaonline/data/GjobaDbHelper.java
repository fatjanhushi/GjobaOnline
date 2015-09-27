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

/**
 * Created by me on 9/19/2015.
 */
public class GjobaDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "gjoba.db";

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
                vehicle = new Vehicle(cursor.getString(1), cursor.getString(2));
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
            vehicle = new Vehicle(cursor.getString(1), cursor.getString(2));
        } else {
            Log.e("Gabim", "Nuk ekziston automjet me kete id ne databaze");
        }

        cursor.close();

        return vehicle;
    }

    // Gjobe DB helper methods.
    public void createGjobe(int vehicleId, int nr, Double vlera) {
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
        */
        String query = "REPLACE INTO " + GjobaContract.GjobaEntry.TABLE_NAME +
                " ("+ GjobaContract.GjobaEntry.COLUMN_VEHICLE_ID+", "+ GjobaContract.GjobaEntry.COLUMN_NR_TOTAL_PER_AUTOMJET +
                ", "+ GjobaContract.GjobaEntry.COLUMN_VLERA_TOTAL_PER_AUTOMJET+") VALUES (" +
                vehicleId + "," + nr + "," + vlera + ")";

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);

    }

    public long createGjobe(Gjobe gjobe){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GjobaContract.GjobaEntry.COLUMN_VEHICLE_ID, gjobe.getVehicleId());
        values.put(GjobaContract.GjobaEntry.COLUMN_NR_TOTAL_PER_AUTOMJET, gjobe.getNrTotalPerAutomjet());
        values.put(GjobaContract.GjobaEntry.COLUMN_VLERA_TOTAL_PER_AUTOMJET, gjobe.getVleraTotalPerAutomjet());

        long id = db.insert(GjobaContract.GjobaEntry.TABLE_NAME, null, values);
        db.close();
        return id;
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
            gjobe = new Gjobe(_id, cursor.getInt(2), cursor.getDouble(3));
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

}
