package com.pulsar.android.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pulsar.android.GlobalVar;
import com.pulsar.android.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void gotoWallet(View view){
        Intent intent = new Intent(SettingActivity.this, IntroNewActivity.class);
        startActivity(intent);

    }
    public void doLogout(View view){
        clearPreference();
        Intent intent = new Intent(SettingActivity.this, InitActivity.class);
        startActivity(intent);
    }
    public void clearPreference(){
        SharedPreferences myPreferences= PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putString(GlobalVar.KEY_INTENT_PASSWORD, "");
        myEditor.putString(GlobalVar.KEY_INTENT_PRIVATE, "");
        myEditor.putString(GlobalVar.KEY_INTENT_PUBLIC, "");
        myEditor.putString(GlobalVar.KEY_INTENT_ADDRESS, "");
        myEditor.commit();
    }
}
