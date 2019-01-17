package com.mscolari.bitcoinconverter.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    implements CurrencyAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    private ProgressBar progressBar;
    private RecyclerView rvCurrencies;

    private ArrayList<Currency> currencyList;
    private CurrencyAdapter currencyAdapter;

    private SharedPreferences sharedPreferences;
    private String name;

    public static final String TAG = "SelectionActivityLog";
    public static final String KEY = "CURRENCY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        // check shared preferences for stored name
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!nameExists()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            // show name found in shared preferences
            Toast.makeText(this, "Hello, " + name + "!",
                    Toast.LENGTH_LONG).show();

            // initialize views
            progressBar = findViewById(R.id.activity_selection_progress_bar);
            rvCurrencies = findViewById(R.id.activity_selection_rv_currencies);

            // setup recycler view and fill with values
            rvCurrencies.setLayoutManager(new LinearLayoutManager(this));
            displayCurrencies();
        }
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
                            currencyList = convertCurrencyToList(response.getJSONObject("rates"));
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
        progressBar.setVisibility(View.GONE);
        rvCurrencies.setVisibility(View.VISIBLE);
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

    // inflate search item in menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_menu_search);

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    // check shared preferences for stored name
    private boolean nameExists() {
        name = sharedPreferences.getString("Name", "");
        if (name.equals(""))
            return false;
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String searchQuery = s.toLowerCase();
        ArrayList<Currency> filteredList = new ArrayList<>();

        // only perform search if dataset is non-empty
        if (currencyList == null) {
            Toast.makeText(getApplicationContext(), "No items to search. Please wait"
                    , Toast.LENGTH_LONG).show();
            return false;
        }

        // retrieve all currencies that fit search query
        for (Currency currency : currencyList) {
            if ((currency.getName().toLowerCase().contains(searchQuery)) ||
                    currency.getCode().toLowerCase().contains(searchQuery)) {
                filteredList.add(currency);
            }
        }

        currencyAdapter.filterList(filteredList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

}
