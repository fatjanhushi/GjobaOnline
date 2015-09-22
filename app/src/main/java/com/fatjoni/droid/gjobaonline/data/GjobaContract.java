package com.fatjoni.droid.gjobaonline.data;

import android.provider.BaseColumns;

/**
 * Created by me on 9/15/2015.
 */
public class GjobaContract {

    public static final class VehicleEntry implements BaseColumns {

        //Table name
        public static final String TABLE_NAME = "vehicle";

        //Columns
        public static final String COLUMN_BRAND = "brand";
        public static final String COLUMN_MODEL = "model";
        public static final String COLUMN_VIN = "vin";
        public static final String COLUMN_PLATE = "plate";

        public static final String COLUMN_MY_VEHICLE = "myVehicle";

    }

}
