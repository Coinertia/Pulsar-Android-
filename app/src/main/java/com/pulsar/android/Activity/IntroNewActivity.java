package com.pulsar.android.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
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

public class IntroNewActivity extends AppCompatActivity {

    Button btn_contiue;
    EditText edt_address;
    EditSpacebar et_words;
    String strSeeds = "";
    WavesWallet newWallet = null;
    TextView tx_notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_intro_new);
        initView();
    }
    public void initView(){
        btn_contiue = findViewById(R.id.btn_continue);
        et_words = findViewById(R.id.edit_tag_view);
        tx_notify = findViewById(R.id.tx_notify);
        edt_address = findViewById(R.id.edt_address);

        String strBack = "<font color='#000'>PLEASE</font><<font color='red'> CAREFULLY </font> <font color='#000'>WRITE DOWN THE 15 WORDS</font>";
        tx_notify.setText(Html.fromHtml(strBack), TextView.BufferType.SPANNABLE);
        et_words.setEditable(false);
        et_words.setTagList(GlobalVar.mSeeds);
        WavesWallet newWallet = new WavesWallet(GlobalVar.strSeeds.getBytes(Charsets.UTF_8));
        GlobalVar.mWallet = newWallet;
        edt_address.setText(GlobalVar.mWallet.getAddress());
//        et_words.setTagAddCallBack(new EditTag.TagAddCallback() {
//            @Override
//            public boolean onTagAdd(String s) {
//                strSeeds = "";
//                List<String> mTemp = et_words.getTagList();
//                for(int i = 0; i < mTemp.size(); i ++){
//                    strSeeds += mTemp.get(i);
//                    strSeeds += " ";
//                }
//                strSeeds += s;
//                if (strSeeds != null && strSeeds.length() > 0 && strSeeds.charAt(strSeeds.length() - 1) == ' ') {
//                    strSeeds = strSeeds.substring(0, strSeeds.length() - 1);
//                }
//                getAddress();
//                return true;
//            }
//        });
//        et_words.setTagDeletedCallback(new EditTag.TagDeletedCallback() {
//            @Override
//            public void onTagDelete(String s) {
//                strSeeds = "";
//                List<String> mTemp = et_words.getTagList();
//                for(int i = 0; i < mTemp.size(); i ++){
//                    strSeeds += mTemp.get(i);
//                    strSeeds += " ";
//                }
//                if (strSeeds != null && strSeeds.length() > 0 && strSeeds.charAt(strSeeds.length() - 1) == ' ') {
//                    strSeeds = strSeeds.substring(0, strSeeds.length() - 1);
//                }
//                if(strSeeds != "")
//                    getAddress();
//                else{
//                    newWallet = null;
//                    edt_address.setText("");
//                }
//            }
//        });

        btn_contiue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (newWallet != null) {
//                    int nCount = et_words.getTagList().size();
//                    if(nCount   != 15) {
//                        Toast.makeText(getApplicationContext(),"Should be 15 words",Toast.LENGTH_SHORT).show();
//                    }else{
//                        GlobalVar.mWallet = newWallet;
//                        GlobalVar.strSeeds = strSeeds;
//                        GlobalVar.mSeeds = et_words.getTagList();
                        Intent intent = new Intent(IntroNewActivity.this, IntroNewConfirm.class);
                        startActivityForResult(intent, 999);
//                    }
//                }else{
//                    Toast.makeText(getApplicationContext(),"Please enter 15 words",Toast.LENGTH_SHORT).show();
//                }
            }
        });

    }
    public void getAddress() {
        newWallet = new WavesWallet(strSeeds.getBytes(Charsets.UTF_8));
        edt_address.setText(newWallet.getAddress());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 999) {
            this.finish();
        }
    }
}
