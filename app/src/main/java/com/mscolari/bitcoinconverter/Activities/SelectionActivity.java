package com.mscolari.bitcoinconverter.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.*;
import com.mscolari.bitcoinconverter.Adapters.CurrencyAdapter;
import com.mscolari.bitcoinconverter.Models.Currency;
import com.mscolari.bitcoinconverter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SelectionActivity extends AppCompatActivity
    implements CurrencyAdapter.OnItemClickListener{

    private RecyclerView rvCurrencies;
    private CurrencyAdapter currencyAdapter;
    private TextView tvDisplayText;
    private Button btnConvert;

    public static final String TAG = "SelectionActivityLog";
    public static final String KEY = "CURRENCY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        // initialize and fill recycler view with currency information
        rvCurrencies = findViewById(R.id.activity_selection_rv_currencies);
        rvCurrencies.setLayoutManager(new LinearLayoutManager(this));
        displayCurrencies();
}

    @Override
    public void onItemClick(Currency currency) {
        Intent intent = new Intent(this, ConvertActivity.class);
        intent.putExtra(KEY, currency);
        startActivity(intent);
    }

    // displays list of supported currencies
    private void displayCurrencies() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://apiv2.bitcoinaverage.com/constants/exchangerates/global",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject response = new JSONObject(new String(responseBody));
                            ArrayList<Currency> currencyList =
                                    convertCurrencyToList(response.getJSONObject("rates"));
                            buildList(currencyList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }

                });
    }

    // INPUT: list of currency objects
    // OUTPUT: fills recycler view with available currency
    private void buildList(ArrayList<Currency> currencies) {
        currencyAdapter = new CurrencyAdapter(currencies);
        rvCurrencies.setAdapter(currencyAdapter);
        currencyAdapter.setOnItemClickListener(this);
    }

    // INPUT: JSONObject
    // OUTPUT: ArrayList of Currency
    private static ArrayList<Currency> convertCurrencyToList(JSONObject rates) throws JSONException {
        ArrayList<Currency> currencyList = new ArrayList<>();
        JSONArray names = rates.names();

        // Iterate through JSON response to build list of currency objects
        for (int i = 0; i < names.length(); i++) {
            JSONObject currCurrency = rates.getJSONObject(names.getString(i));
            currencyList.add(new Currency(names.getString(i), currCurrency.getString("name")));
        }
        return currencyList;
    }
}
