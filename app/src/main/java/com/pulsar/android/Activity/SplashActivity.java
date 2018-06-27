package com.pulsar.android.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pulsar.android.GlobalVar;
import com.pulsar.android.R;
import com.pulsar.android.auth.EnvironmentManager;
import com.pulsar.android.util.AppUtil;
import com.pulsar.android.util.PrefsUtil;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        AppUtil appUtil = new AppUtil(this);
        EnvironmentManager.init(new PrefsUtil(this), appUtil);
        initApp();
    }

    public void initApp(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String strPwd = prefs.getString(GlobalVar.KEY_INTENT_PASSWORD, "");
        String strPrivate = prefs.getString(GlobalVar.KEY_INTENT_PRIVATE, "");
        String strAddress = prefs.getString(GlobalVar.KEY_INTENT_ADDRESS, "");
        String strPublic = prefs.getString(GlobalVar.KEY_INTENT_PUBLIC, "");
        GlobalVar.strAddressEncrypted = strAddress;
        try {
            strPwd = GlobalVar.decryptMsg(strPwd);
            strPrivate = GlobalVar.decryptMsg(strPrivate);
            strAddress = GlobalVar.decryptMsg(strAddress);
            strPublic = GlobalVar.decryptMsg(strPublic);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        GlobalVar.strPwd = strPwd;
        GlobalVar.strPrivate= strPrivate;
        GlobalVar.strAddress = strAddress;
        GlobalVar.strPublic = strPublic;

        if(!strPrivate.equals("") && !strPwd.equals("")){
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    SplashActivity.this.finish();
                }
            },4000);
        }else{
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, InitActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    SplashActivity.this.finish();
                }
            },4000);
        }
    }
}
