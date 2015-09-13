package com.fatjoni.droid.gjobaonline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by me on 9/12/2015.
 */
public class CheckGjobeFragment extends Fragment{
    @Bind(R.id.tv_plate) EditText txtPlate;
    @Bind(R.id.tv_vin) EditText txtVin;
    @Bind(R.id.response) TextView mTextView;

    public CheckGjobeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_gjobe, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }


    @OnClick(R.id.btn_clean)
    public void clean() {
        txtPlate.setText("");
        txtVin.setText("");
    }

    @OnClick(R.id.btn_search)
    public void search(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url = "http://www.asp.gov.al/index.php/sherbime/kontrolloni-gjobat-tuaja";

        //myVIN: ZAR93200001150366
        //myPLATE: AA016FJ

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document doc = Jsoup.parse(response);
                        Elements data = doc.select("td[style]");
                        if (data != null){
                            Element elementNrGjobave = data.get(0);
                            String stringNrGjobave = elementNrGjobave.text();

                            Element elementVleraTotal = data.get(1);
                            String stringVleraTotal = elementVleraTotal.text();

                            Element elementShkeljet = data.get(2);
                            String stringShkeljet = elementShkeljet.text();

                            Element elementPershkrimet = data.get(3);
                            String stringPershkrimet = elementPershkrimet.text();

                            response = "Nr.Gjobave: " + stringNrGjobave + "\n" +
                                        "Vlera Total: " + stringVleraTotal + "\n" +
                                        "Shkeljet: " + stringShkeljet + "\n" +
                                        "Pershkrimet: " + stringPershkrimet;
                        }


                        mTextView.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        }){//shton argumentat per metoden POST gjate kerkeses
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("plate", txtPlate.getText().toString());
                params.put("vin", txtVin.getText().toString());
                return params;
            };
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
