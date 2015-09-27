package com.fatjoni.droid.gjobaonline.model;

/**
 * Created by me on 9/12/2015.
 */
public class Vehicle {
    private int _id;
    private String vin, plate;

    //Constructors

    public Vehicle(String plate, String vin) {
        this.plate = plate;
        this.vin = vin;
    }
    //end Constructors

    //public methods

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
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

}
