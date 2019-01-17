package com.mscolari.bitcoinconverter.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mscolari.bitcoinconverter.Models.Currency;
import com.mscolari.bitcoinconverter.R;

import org.fabiomsr.moneytextview.MoneyTextView;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ConvertActivity extends AppCompatActivity {

    private MoneyTextView tvConvert;
    private ProgressBar progressBar;

    private Currency currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        // initialize views
        tvConvert = findViewById(R.id.activity_convert_tv_convert);
        progressBar = findViewById(R.id.activity_convert_pb_progress);

        // make HTTP request for conversion on selected currency
        currency = (Currency) getIntent().getExtras().get(SelectionActivity.KEY);
        queryCurrency(currency.getCode(), "1");
    }

    // retrieve (BTC -> chosen currency) conversion
    private float queryCurrency(String currencyType, String amount) {
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

                    }
                });
        return 0;
    }

    // INPUT: JSONObject with requested conversion information
    // OUTPUT: Output appropriate information to screen
    private void displayConversion(JSONObject response) throws JSONException {
        // remove loading animation and show price information
        if (response.getBoolean("success")) {
            progressBar.setVisibility(View.GONE);
            tvConvert.setVisibility(View.VISIBLE);
            tvConvert.setAmount(Float.valueOf(response.getString("price")));
            tvConvert.setSymbol(currency.getCode());
        }
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
