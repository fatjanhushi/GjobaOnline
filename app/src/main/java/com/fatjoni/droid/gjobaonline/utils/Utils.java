package com.fatjoni.droid.gjobaonline.utils;

import android.content.Context;
import android.util.Log;

import com.fatjoni.droid.gjobaonline.data.GjobaDbHelper;
import com.fatjoni.droid.gjobaonline.model.Gjobe;
import com.fatjoni.droid.gjobaonline.model.Vehicle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by me on 04-Feb-17.
 */

public class Utils {
    public static final String DEVICE_ID_I9100 = "98E463DED05B421E4BFBEE5DAE49A162";
    public static final String DEVICE_ID_LGD850 = "65A585F7892DDBCBF3AD864D79257995";
    public static final String REQUEST_URL = "https://www.asp.gov.al/index.php/sherbime/kontrolloni-gjobat-tuaja";


    public Utils(){

    }

    public ArrayList<String> parseHtml(final String responseData){

        Document doc = Jsoup.parse(responseData);
        ArrayList<String> arrayList = null;
        Elements data = doc.select("td[style]");

        if (data.size()>0){
            arrayList = new ArrayList<>();
            arrayList.add(data.get(0).text());
            arrayList.add(data.get(1).text());
            arrayList.add(data.get(2).text());
            arrayList.add(data.get(3).text());

        }

        return arrayList;
    }

    public int saveToDatabase(Context context, String vin, String targa, String numriGjobave, String vleraGjobave){

        targa = targa.toUpperCase().trim();
        vin = vin.toUpperCase().trim();
        vleraGjobave = vleraGjobave.replace("LEK", "").replace(",","");

        GjobaDbHelper dbHelper = new GjobaDbHelper(context);

        int vehicleId = dbHelper.getVehicleIDByPlate(targa);

        if (vehicleId != -9){
            //update gjobe for this vehicle
            Log.d("Vehicle exists", String.valueOf(vehicleId));
            int i = dbHelper.updateGjobe(vehicleId,
                    Integer.parseInt(numriGjobave),
                    Double.parseDouble(vleraGjobave)
            );
            Log.d("Gjoba e apdejtuar:",String.valueOf(i));
            return 0;

        }else {
            //insert vehicle for the first time
            Vehicle vehicle = new Vehicle(vin, targa);
            long insertedVehicleId = dbHelper.insertVehicle(vehicle);


            Gjobe gjobe = new Gjobe(Integer.parseInt(numriGjobave),
                    Double.parseDouble(vleraGjobave),
                    insertedVehicleId
            );
            long gjobeId = dbHelper.insertGjobe(gjobe);
            return 1;
        }

        //rikthen 1 nqs eshte shtuar makine e re dhe 0 nqs eshte apdejtuar makina ekzistuese
    }


}
