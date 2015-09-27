package com.fatjoni.droid.gjobaonline.model;

/**
 * Created by me on 9/24/2015.
 */
public class Gjobe {
    private int _id, vehicleId, nrTotalPerAutomjet;
    private double vleraTotalPerAutomjet;

    public Gjobe(int vehicleId, int nrTotalPerAutomjet, double vleraTotalPerAutomjet) {
        this.vehicleId = vehicleId;
        this.nrTotalPerAutomjet = nrTotalPerAutomjet;
        this.vleraTotalPerAutomjet = vleraTotalPerAutomjet;
    }

    public Gjobe(int nrTotalPerAutomjet, double vleraTotalPerAutomjet) {
        this.nrTotalPerAutomjet = nrTotalPerAutomjet;
        this.vleraTotalPerAutomjet = vleraTotalPerAutomjet;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getNrTotalPerAutomjet() {
        return nrTotalPerAutomjet;
    }

    public void setNrTotalPerAutomjet(int nrTotalPerAutomjet) {
        this.nrTotalPerAutomjet = nrTotalPerAutomjet;
    }

    public double getVleraTotalPerAutomjet() {
        return vleraTotalPerAutomjet;
    }

    public void setVleraTotalPerAutomjet(double vleraTotalPerAutomjet) {
        this.vleraTotalPerAutomjet = vleraTotalPerAutomjet;
    }

    @Override
    public String toString() {
        String s = nrTotalPerAutomjet + "  " + vleraTotalPerAutomjet;
        return s;
    }
}
