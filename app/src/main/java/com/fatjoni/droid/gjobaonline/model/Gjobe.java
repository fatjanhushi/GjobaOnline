package com.fatjoni.droid.gjobaonline.model;

/**
 * Created by me on 9/24/2015.
 */
public class Gjobe {
    private int id, numri;
    private long vehicleId;
    private double vlera;

    public Gjobe(int numri, double vlera, long vehicleId) {
        this.numri = numri;
        this.vehicleId = vehicleId;
        this.vlera = vlera;
    }

    public Gjobe(int id, int numri, double vlera, long vehicleId) {
        this.id = id;
        this.numri = numri;
        this.vehicleId = vehicleId;
        this.vlera = vlera;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumri() {
        return numri;
    }

    public void setNumri(int numri) {
        this.numri = numri;
    }

    public double getVlera() {
        return vlera;
    }

    public void setVlera(double vlera) {
        this.vlera = vlera;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }
}
