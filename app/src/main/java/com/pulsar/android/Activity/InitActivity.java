package com.pulsar.android.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pulsar.android.GlobalVar;
import com.pulsar.android.R;
import com.pulsar.android.auth.WalletManager;

import java.util.Arrays;

public class InitActivity extends AppCompatActivity {
    Button btn_new, btn_restore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_init);
        initView();
    }
    public void initView(){
        btn_new = findViewById(R.id.btn_create);
        btn_restore = findViewById(R.id.btn_restore);
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InitActivity.this, IntroNewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });
        btn_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InitActivity.this, IntroExistActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });
        GlobalVar.strSeeds = WalletManager.createWalletSeed(this);
        GlobalVar.mSeeds = Arrays.asList(GlobalVar.strSeeds.split(" "));
    }
}
