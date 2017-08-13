package com.fatjoni.droid.gjobaonline.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.fatjoni.droid.gjobaonline.databinding.FragmentCheckGjobeBinding;
import com.fatjoni.droid.gjobaonline.network.NetworkUtils;
import com.fatjoni.droid.gjobaonline.utils.Utils;


import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by me on 9/12/2015.
 */
public class CheckGjobeFragment extends Fragment {

    private FragmentCheckGjobeBinding binding;
    private ProgressDialog progressDialog;
    private Utils myUtils;
    private ArrayList<String> responseValues;
    String pergjigja, vin, targa;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckGjobeBinding.inflate(inflater, container, false);
        progressDialog = new ProgressDialog(getActivity());

        binding.btnKerko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isNetworkAvailable(getContext())) {
                    if (isPlateValid() && isVinValid()) {
                        try {
                            run();
                            progressDialog.setMessage("Duke kerkuar...");
                            progressDialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (isPlateValid())
                        Snackbar.make(binding.getRoot(), "Numri i shasise nuk eshte i sakte.", Snackbar.LENGTH_LONG).show();
                    else
                        Snackbar.make(binding.getRoot(), "Targa nuk eshte e sakte.", Snackbar.LENGTH_LONG).show();
                } else
                    Snackbar.make(binding.getRoot(), "Ju nuk jeni i lidhur ne internet.", Snackbar.LENGTH_LONG).show();

                hideKeyboard();
            }
        });

        binding.btnSaveToDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int isNewVehicle = myUtils.saveToDatabase(getContext(), vin, targa, responseValues.get(0), responseValues.get(1));

                if (isNewVehicle == 1) {
                    Snackbar.make(binding.getRoot(), "Makina juaj u shtua!", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(binding.getRoot(), "Makina juaj ekziston! Gjobat u perditesuan!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        binding.btnPastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.textTarga.setText("");
                binding.textVin.setText("");
            }
        });

        return binding.getRoot();
    }

    public void run() throws Exception {
        final OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("plate", binding.textTarga.getText().toString().trim())
                .addFormDataPart("vin", binding.textVin.getText().toString().trim())
                .build();

        Request request = new Request.Builder()
                .url(Utils.REQUEST_URL)
                //.method("POST", RequestBody.create(null, new byte[0]))
                .post(requestBody)
                .build();

        //Response response = client.newCall(request).execute();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }

                        // Read data on the worker thread
                        final String responseData = response.body().string();

                        // Run view-related code back on the main thread
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myUtils = new Utils();
                                responseValues = myUtils.parseHtml(responseData);

                                if (responseValues != null){
                                    pergjigja = "Nr.Gjobave: " + responseValues.get(0) + "\n" +
                                            "Vlera Total: " + responseValues.get(1) + "\n" +
                                            "Shkeljet: " + responseValues.get(2) + "\n" +
                                            "Pershkrimet: " + responseValues.get(3);

                                    binding.resultLayout.setVisibility(View.VISIBLE);
                                    binding.resultText.setText(pergjigja);
                                }else {
                                    Snackbar.make(binding.getRoot(), "Nuk u gjet automjet me keto te dhena. Provo perseri.", Snackbar.LENGTH_LONG).show();
                                }


                                progressDialog.dismiss();
                            }
                        });

                    }
                });
    }


    private boolean isPlateValid() {
        targa = binding.textTarga.getText().toString().replace(" ", "");

        boolean checkPlate = false;
        if (targa.length() == 7 && (targa.matches("^[a-zA-Z]{2}[0-9]{3}[a-zA-Z]{2}") ||
                (targa.matches("^[a-zA-Z]{2}[0-9]{4}[a-zA-Z]"))))
            checkPlate = true;

        return checkPlate;
    }

    private boolean isVinValid() {
        vin = binding.textVin.getText().toString().replace(" ", "");

        boolean checkVin = false;
        if (vin.length() == 17)
            checkVin = true;

        return checkVin;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
    }

}
