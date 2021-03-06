package com.mscolari.bitcoinconverter.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mscolari.bitcoinconverter.Activities.SelectionActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

/*
* Holds both the name and abbreviated code for all currencies
* Allows for passing to activities through intents with Parcelable
 */

public class Currency implements Parcelable {
    private String code;
    private String name;

    public Currency(String code, String name) {
        this.code = code;
        this.name = name;
    }

    protected Currency(Parcel in) {
        code = in.readString();
        name = in.readString();
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(code);
        parcel.writeString(name);
    }
}
