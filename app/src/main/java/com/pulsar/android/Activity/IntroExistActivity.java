package com.pulsar.android.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Charsets;
import com.pulsar.android.GlobalVar;
import com.pulsar.android.R;
import com.pulsar.android.auth.WavesWallet;
import com.pulsar.android.components.EditComponent.EditSpacebar;

import java.util.List;

import me.originqiu.library.EditTag;

public class IntroExistActivity extends AppCompatActivity {
    TextView tx_new;
    EditSpacebar edt_words;
    EditText edt_address;
    Button btn_continue;
    WavesWallet newWallet = null;
    String strSeeds = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_intro_exist);
        initView();

    }
    public void initView(){
        tx_new = findViewById(R.id.tx_new);
        edt_words = findViewById(R.id.edit_tag_view);
        edt_address = findViewById(R.id.tx_address);
        btn_continue = findViewById(R.id.btn_continue);

        tx_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroExistActivity.this, IntroNewActivity.class);
                startActivity(intent);
                IntroExistActivity.this.finish();
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newWallet != null) {
                    int nCount = edt_words.getTagList().size();
                    if(nCount   != 15) {
                        Toast.makeText(getApplicationContext(),"Should be 15 words",Toast.LENGTH_SHORT).show();
                    }else{
                        GlobalVar.mWallet = newWallet;
                        GlobalVar.strSeeds = strSeeds;
                        GlobalVar.mSeeds = edt_words.getTagList();
                        Intent intent = new Intent(IntroExistActivity.this, IntroPassword.class);
                        startActivityForResult(intent, 999);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter 15 words",Toast.LENGTH_SHORT).show();
                }

            }
        });

        edt_words.setTagAddCallBack(new EditTag.TagAddCallback() {
            @Override
            public boolean onTagAdd(String s) {
                strSeeds = "";
                List<String> mTemp = edt_words.getTagList();
                for(int i = 0; i < mTemp.size(); i ++){
                    strSeeds += mTemp.get(i);
                    strSeeds += " ";
                }
                strSeeds += s;
                if (strSeeds != null && strSeeds.length() > 0 && strSeeds.charAt(strSeeds.length() - 1) == ' ') {
                    strSeeds = strSeeds.substring(0, strSeeds.length() - 1);
                }
                getAddress();
                return true;
            }
        });
        edt_words.setTagDeletedCallback(new EditTag.TagDeletedCallback() {
            @Override
            public void onTagDelete(String s) {
                strSeeds = "";
                List<String> mTemp = edt_words.getTagList();
                for(int i = 0; i < mTemp.size(); i ++){
                    strSeeds += mTemp.get(i);
                    strSeeds += " ";
                }
                if (strSeeds != null && strSeeds.length() > 0 && strSeeds.charAt(strSeeds.length() - 1) == ' ') {
                    strSeeds = strSeeds.substring(0, strSeeds.length() - 1);
                }
                if(strSeeds != "")
                    getAddress();
                else{
                    newWallet = null;
                    edt_address.setText("");
                }
            }
        });
    }
    public void getAddress() {
        newWallet = new WavesWallet(strSeeds.getBytes(Charsets.UTF_8));
        edt_address.setText(newWallet.getAddress());
    }
}
