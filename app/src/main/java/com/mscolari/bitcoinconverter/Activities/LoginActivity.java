package com.mscolari.bitcoinconverter.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mscolari.bitcoinconverter.R;

public class LoginActivity extends AppCompatActivity {

    private EditText edtName;
    private Button btnSave;

    private SharedPreferences sharedPreferences;
    public static final String PREF_KEY = "com.mscolari.bitcoinconverter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize views
        edtName = findViewById(R.id.activity_login_edt_name);
        btnSave = findViewById(R.id.activity_login_btn_save);

        // save name to shared preferences
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                writeSharedPreferences(name);

                // switch to currency selection page
                Intent intent = new Intent(view.getContext(), SelectionActivity.class);
                startActivity(intent);
            }
        });
    }

    // save user's name to shared preferences
    private void writeSharedPreferences(String name) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", name);
        editor.apply();
    }
}
