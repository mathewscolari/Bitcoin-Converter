package com.mscolari.bitcoinconverter.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private Currency currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        // retrieve selected currency from last activity
        currency = (Currency) getIntent().getExtras().get(SelectionActivity.KEY);

        // initialize views
        tvConvert = findViewById(R.id.activity_convert_tv_convert);

        // make HTTP request for BTC -> currency conversion
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
        if (response.getBoolean("success"))
            tvConvert.setAmount(Float.valueOf(response.getString("price")));
            tvConvert.setSymbol(currency.getCode());
    }
}
