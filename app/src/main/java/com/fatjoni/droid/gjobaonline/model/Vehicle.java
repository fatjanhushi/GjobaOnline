package com.fatjoni.droid.gjobaonline.model;

/**
 * Created by me on 9/12/2015.
 */
public class Vehicle {
    private int id;
    private String vin, plate, name;

    public Vehicle(String vin, String plate) {
        this.vin = vin;
        this.plate = plate;
    }

    public Vehicle(int id, String vin, String plate) {
        this.id = id;
        this.vin = vin;
        this.plate = plate;
    }

    public Vehicle(int id, String vin, String plate, String name) {
        this.id = id;
        this.vin = vin;
        this.plate = plate;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
