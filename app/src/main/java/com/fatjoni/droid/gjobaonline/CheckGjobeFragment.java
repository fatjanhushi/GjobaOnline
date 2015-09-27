package com.fatjoni.droid.gjobaonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fatjoni.droid.gjobaonline.data.GjobaDbHelper;
import com.fatjoni.droid.gjobaonline.model.Vehicle;
import com.fatjoni.droid.gjobaonline.network.NetworkUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by me on 9/12/2015.
 */
public class CheckGjobeFragment extends Fragment {
    @Bind(R.id.tv_plate)
    EditText txtPlate;
    @Bind(R.id.tv_vin)
    EditText txtVin;
    @Bind(R.id.response)
    TextView mTextView;
    @Bind(R.id.responseContainer)
    ViewGroup responseContainer;
    @Bind(R.id.add_to_my_vehicles)
    Button add_to_my_vehicles;
    View rootView;
    ProgressDialog pd;
    String targa, vin;
    int nrGjobaTotalPerAutomjet;
    Double vleraGjobaTotalPerAutomjet;

    public CheckGjobeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_check_gjobe, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.btn_clean)
    public void clean() {
        txtPlate.setText("");
        txtVin.setText("");
        responseContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_search)
    public void search() {

        if (NetworkUtils.isNetworkAvailable(getContext())) {
            if (isPlateValid() && isVinValid()) {
                NetworkTask task = new NetworkTask();
                task.execute(NetworkUtils.REQUEST_URL);
            } else if (isPlateValid())
                Snackbar.make(rootView, "Numri i shasise nuk eshte i sakte.", Snackbar.LENGTH_LONG).show();
            else
                Snackbar.make(rootView, "Targa nuk eshte e sakte.", Snackbar.LENGTH_LONG).show();
        } else
            Snackbar.make(rootView, "Ju nuk jeni i lidhur ne internet.", Snackbar.LENGTH_LONG).show();

        hideKeyboard();
    }

    @OnClick(R.id.add_to_my_vehicles)
    public void adToMyVehicles() {
        Vehicle vehicle = new Vehicle(targa.toUpperCase(), vin.toUpperCase());
        GjobaDbHelper gjobaDbHelper = new GjobaDbHelper(getContext());
        long vehicleId = gjobaDbHelper.createVehicle(vehicle);
        int id = (int) vehicleId;
        gjobaDbHelper.createGjobe(id, nrGjobaTotalPerAutomjet, vleraGjobaTotalPerAutomjet);

        Snackbar.make(rootView, "Automjeti eshte shtuar tek Automjetet e mia.", Snackbar.LENGTH_LONG).show();
    }

    private boolean isPlateValid() {
        targa = txtPlate.getText().toString().replace(" ", "");

        boolean checkPlate = false;
        if (targa.length() == 7 && (targa.matches("^[a-zA-Z]{2}[0-9]{3}[a-zA-Z]{2}") ||
                (targa.matches("^[a-zA-Z]{2}[0-9]{4}[a-zA-Z]"))))
            checkPlate = true;

        return checkPlate;
    }

    private boolean isVinValid() {
        vin = txtVin.getText().toString().replace(" ", "");

        boolean checkVin = false;
        if (vin.length() == 17)
            checkVin = true;

        return checkVin;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class NetworkTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getContext());
            pd.setMessage("Ju lutem prisni...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> parametrat = new HashMap<>();
            parametrat.put("plate", targa);
            parametrat.put("vin", vin);

            NetworkUtils networkUtils = new NetworkUtils();
            String s = networkUtils.performPostCall(params[0], parametrat);

            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Document doc = Jsoup.parse(s);
                Elements data = doc.select("td[style]");
                if (data.size() > 0) {
                    Log.d("data object returned", data.toString());
                    Element elementNrGjobave = data.get(0);
                    String stringNrGjobave = elementNrGjobave.text();
                    nrGjobaTotalPerAutomjet = Integer.parseInt(stringNrGjobave);

                    Element elementVleraTotal = data.get(1);
                    String stringVleraTotal = elementVleraTotal.text();
                    vleraGjobaTotalPerAutomjet = Double.parseDouble(stringVleraTotal.replace("LEK", ""));

                    Element elementShkeljet = data.get(2);
                    String stringShkeljet = elementShkeljet.text();

                    Element elementPershkrimet = data.get(3);
                    String stringPershkrimet = elementPershkrimet.text();

                    s = "Nr.Gjobave: " + stringNrGjobave + "\n" +
                            "Vlera Total: " + stringVleraTotal + "\n" +
                            "Shkeljet: " + stringShkeljet + "\n" +
                            "Pershkrimet: " + stringPershkrimet;

                    mTextView.setText(s);
                    responseContainer.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(rootView, "Nuk u gjet automjet me kete targe dhe nr. shasie.", Snackbar.LENGTH_LONG).show();
                }

            }
            pd.dismiss();
        }


    }
}
