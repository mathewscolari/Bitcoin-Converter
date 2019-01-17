package com.mscolari.bitcoinconverter.Activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mscolari.bitcoinconverter.Models.Currency;
import com.mscolari.bitcoinconverter.R;

import org.fabiomsr.moneytextview.MoneyTextView;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ConvertActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private MoneyTextView tvConvert;
    private ProgressBar progressBar;

    private Currency selectedCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        // initialize views
        constraintLayout = findViewById(R.id.activity_convert_constraint);
        tvConvert = findViewById(R.id.activity_convert_tv_convert);
        progressBar = findViewById(R.id.activity_convert_pb_progress);

        // make HTTP request for conversion on selected currency
        selectedCurrency = (Currency) getIntent().getExtras().get(SelectionActivity.KEY);
        queryCurrency(selectedCurrency.getCode(), "1");
    }

    // retrieve (BTC -> chosen currency) conversion
    private float queryCurrency(final String currencyType, String amount) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://apiv2.bitcoinaverage.com/convert/global?from=BTC&to="
                        + currencyType + "&amount= "+ amount,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            // retrieve JSONObject from response and show on screen
                            JSONObject response = new JSONObject(new String(responseBody));
                            displayConversion(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Snackbar snackbar = Snackbar
                                .make(constraintLayout, "Error receiving price information.",
                                        Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        tvConvert.setVisibility(View.GONE);
                                        queryCurrency(currencyType, "1");
                                    }
                                });
                        snackbar.show();
                    }
                });
        return 0;
    }

    // INPUT: JSONObject with requested conversion information
    // OUTPUT: Output appropriate information to screen
    private void displayConversion(JSONObject response) throws JSONException {
        // remove loading animation and show price information on success
        if (response.getBoolean("success")) {
            progressBar.setVisibility(View.GONE);
            tvConvert.setVisibility(View.VISIBLE);

            tvConvert.setAmount(Float.valueOf(response.getString("price")));
            tvConvert.setSymbol(selectedCurrency.getCode());
        }
    }

    // prevent activity from showing after being minimized
    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
