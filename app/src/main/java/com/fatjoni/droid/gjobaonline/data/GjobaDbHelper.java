package com.fatjoni.droid.gjobaonline.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fatjoni.droid.gjobaonline.model.Vehicle;

import java.util.ArrayList;

/**
 * Created by me on 9/19/2015.
 */
public class GjobaDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "gjoba.db";

    public GjobaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_VEHICLE_TABLE = "CREATE TABLE " +
                GjobaContract.VehicleEntry.TABLE_NAME+"(" +
                GjobaContract.VehicleEntry._ID + " INTEGER PRIMARY KEY," +
                GjobaContract.VehicleEntry.COLUMN_PLATE + " TEXT UNIQUE NOT NULL," +
                GjobaContract.VehicleEntry.COLUMN_VIN + " TEXT UNIQUE NOT NULL" + ");";

        db.execSQL(CREATE_VEHICLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GjobaContract.VehicleEntry.TABLE_NAME);
        onCreate(db);
    }

    public void createVehicle(Vehicle vehicle){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GjobaContract.VehicleEntry.COLUMN_PLATE, vehicle.getPlate());
        values.put(GjobaContract.VehicleEntry.COLUMN_VIN, vehicle.getVin());

        db.insert(GjobaContract.VehicleEntry.TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Vehicle> getAllVehicles(){
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM "+ GjobaContract.VehicleEntry.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Vehicle vehicle = null;
        if (cursor.moveToFirst()){
            do{
                vehicle = new Vehicle(cursor.getString(1), cursor.getString(2));
                vehicles.add(vehicle);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return vehicles;
    }

    public Vehicle getVehicle(int _id){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + GjobaContract.VehicleEntry.TABLE_NAME +
                " WHERE " + GjobaContract.VehicleEntry._ID + "= " + _id;

        Vehicle vehicle = null;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            vehicle = new Vehicle(cursor.getString(1), cursor.getString(2));
        }else {
            Log.e("Gabim", "Nuk ekziston automjet me kete id ne dtabaze");
        }

        cursor.close();

        return vehicle;
    }

    public void deleteVehicle(Vehicle vehicle){

    }

}
