package com.pulsar.android.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pulsar.android.GlobalVar;
import com.pulsar.android.R;

public class IntroPassword extends AppCompatActivity {
    EditText edt_pwd, edt_confirm;
    Button btn_continue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_intro_password);
        initView();
    }
    public void initView(){
        edt_pwd = findViewById(R.id.edt_pwd);
        edt_confirm = findViewById(R.id.edt_confirm);
        btn_continue = findViewById(R.id.btn_continue);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strPwd = String.valueOf(edt_pwd.getText());
                String strConfirm = String.valueOf(edt_confirm.getText());
                if (strPwd.length() < 6)
                    Toast.makeText(getApplicationContext(), "Password must longer than 6 characters", Toast.LENGTH_LONG).show();
                else if (strPwd.equals(strConfirm)) {
                    GlobalVar.strPwd = String.valueOf(edt_pwd.getText());
                    Intent intent = new Intent(IntroPassword.this, IntroDone.class);
                    startActivity(intent);
                } else if (!strPwd.equals(strConfirm)) {
                    Toast.makeText(getApplicationContext(), "Password not match", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
