package com.pulsar.android.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pulsar.android.GlobalVar;
import com.pulsar.android.R;
import com.pulsar.android.components.EditComponent.EditSpacebar;

import java.util.ArrayList;
import java.util.List;

public class IntroNewConfirm extends AppCompatActivity {
    TextView tx_back;
    Button btn_continue;
    EditSpacebar edt_words;
    public List<String> mSelectedSeeds = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_new_confirm);
        initView();
    }
    public void initView(){
        tx_back = findViewById(R.id.tx_back);
        String strBack = "<font color='#333'>Verify your backup phrase or </font><font color='#438cb7'>go back</font>";
        tx_back.setText(Html.fromHtml(strBack), TextView.BufferType.SPANNABLE);
        tx_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntroNewConfirm.this.finish();
            }
        });

        btn_continue = findViewById(R.id.btn_continue);
        edt_words = findViewById(R.id.edit_tag_view);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedSeeds = edt_words.getTagList();
                CheckSeeds();
            }
        });
    }

    public void CheckSeeds(){
        boolean isMatch = true;
        for(int i = 0; i < mSelectedSeeds.size() ; i ++){
            if(!mSelectedSeeds.get(i).equals(GlobalVar.mSeeds.get(i)))
            {
                isMatch = false;
            }
        }
        if(!isMatch){
            Toast.makeText(getApplicationContext(),"Words Not Match. Try again",Toast.LENGTH_SHORT).show();
            cleanWords();
        }
        else{
            if(mSelectedSeeds.size() == 15) {
                Intent intent = new Intent(IntroNewConfirm.this, IntroPassword.class);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(),"Please fill words in correct order.",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void cleanWords(){
        edt_words.reset();

    }
}
