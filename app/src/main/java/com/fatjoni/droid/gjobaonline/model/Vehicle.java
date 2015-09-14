package com.fatjoni.droid.gjobaonline.model;

/**
 * Created by me on 9/12/2015.
 */
public class Vehicle {
    private int _id;
    private String brand, model, vin, plate;
    private boolean myVehicle;

    //Constructors
    public Vehicle(String brand, String model, String vin, String plate, boolean myVehicle) {
        this.brand = brand;
        this.model = model;
        this.vin = vin;
        this.plate = plate;
        this.myVehicle = myVehicle;
    }

    public Vehicle(String vin, String plate, boolean myVehicle) {
        this.vin = vin;
        this.plate = plate;
        this.myVehicle = myVehicle;
    }

    public Vehicle(String vin, String plate) {
        this.vin = vin;
        this.plate = plate;
        this.myVehicle = false;
    }
    //end Constructors

    //public methods


    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public boolean isMyVehicle() {
        return myVehicle;
    }

    public void setMyVehicle(boolean mVehicle) {
        this.myVehicle = mVehicle;
    }
}
