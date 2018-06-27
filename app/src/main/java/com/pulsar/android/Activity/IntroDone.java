package com.pulsar.android.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.pulsar.android.GlobalVar;
import com.pulsar.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class IntroDone extends AppCompatActivity implements View.OnClickListener {
    TextView tx_first, tx_second, tx_third, tx_done;
    CheckBox chk_first, chk_second, chk_third;
    Button btn_continue;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_intro_done);
        initView();
    }
    public void initView(){
        String strFirst = "<font color='#000'>I understand that my information is held</font> <font color='red'>securely</font> <font color='#000'>on this device,</font> <font color='red'>not by a company.</font>";
        String strSec = "<font color='#000'>I understand that if this app is moved to another device or deleted, </font><font color='red'>my account can only be recovered with the backup phrase.</font>";
        String strThird = "<font color='#000'>I would like to help improve the platform by sending anonymous usage statistics to the developers.</font>";

        tx_first = findViewById(R.id.tx_welcom_first);
        tx_second = findViewById(R.id.tx_welcom_sec);
        tx_third = findViewById(R.id.tx_welcom_third);
        tx_first.setOnClickListener(this);
        tx_second.setOnClickListener(this);
        tx_third.setOnClickListener(this);

        chk_first = findViewById(R.id.chk_first);
        chk_second = findViewById(R.id.chk_second);
        chk_third = findViewById(R.id.chk_third);

        btn_continue = findViewById(R.id.btn_continue);

        tx_first.setText(Html.fromHtml(strFirst), TextView.BufferType.SPANNABLE);
        tx_second.setText(Html.fromHtml(strSec), TextView.BufferType.SPANNABLE);
        tx_third.setText(Html.fromHtml(strThird), TextView.BufferType.SPANNABLE);

        btn_continue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(chk_first.isChecked() && chk_second.isChecked()) {
                    updateFirebase();
                }else{
                    Toast.makeText(getApplicationContext(),"Please accept terms.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void saveDataToLoacal(){
        SharedPreferences myPreferences= PreferenceManager.getDefaultSharedPreferences(IntroDone.this);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        String strPwd = GlobalVar.strPwd;
        String strPrivate = GlobalVar.mWallet.getPrivateKeyStr();
        String strAddress = GlobalVar.mWallet.getAddress();
        String strPublic = GlobalVar.mWallet.getPublicKeyStr();
        GlobalVar.strAddress = strAddress;
        GlobalVar.strPrivate = strPrivate;
        GlobalVar.strPublic = strPublic;
        try {
            strPwd= GlobalVar.encryptMsg(GlobalVar.strPwd);
            strPrivate= GlobalVar.encryptMsg(GlobalVar.mWallet.getPrivateKeyStr());
            strAddress = GlobalVar.encryptMsg(GlobalVar.mWallet.getAddress());
            strPublic = GlobalVar.encryptMsg(GlobalVar.mWallet.getPublicKeyStr());
            GlobalVar.strAddressEncrypted = GlobalVar.encryptMsg(GlobalVar.strAddress);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        myEditor.putString(GlobalVar.KEY_INTENT_PASSWORD, strPwd);
        myEditor.putString(GlobalVar.KEY_INTENT_PRIVATE, strPrivate);
        myEditor.putString(GlobalVar.KEY_INTENT_PUBLIC, strPublic);
        myEditor.putString(GlobalVar.KEY_INTENT_ADDRESS, strAddress);
        myEditor.commit();
        checkBalance();

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tx_welcom_first:
                chk_first.setChecked(!chk_first.isChecked());
                break;
            case R.id.tx_welcom_sec:
                chk_second.setChecked(!chk_second.isChecked());
                break;
            case R.id.tx_welcom_third:
                chk_third.setChecked(!chk_third.isChecked());
                break;

        }
    }

    public void updateFirebase() {
        dialog = ProgressDialog.show(IntroDone.this, "",
                "Please wait...", true);
        GlobalVar.mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        GlobalVar.mStorageRef = FirebaseStorage.getInstance().getReference();
        GlobalVar.mDatabaseRef.child("addresslist/").orderByKey().equalTo(GlobalVar.mWallet.getAddress()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot == null) {
                        Toast.makeText(getApplicationContext(), "No Data Available",
                                Toast.LENGTH_LONG).show();
                        addAddressToFB();
                        return;
                    }
                    Map<String, Object> dataList = (Map<String, Object>) dataSnapshot.getValue();
                    if (dataList == null) {
                        addAddressToFB();
                        return;
                    }
                    saveDataToLoacal();
                } catch (Exception e) {
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }
    public void addAddressToFB(){
        DatabaseReference myRef;
        myRef = GlobalVar.mDatabaseRef.child("addresslist").child(GlobalVar.mWallet.getAddress());
        myRef.setValue(GlobalVar.mWallet.getPublicKeyStr());
        saveDataToLoacal();
    }
//    Dialog dialog;
    public void checkBalance(){
        RequestQueue chckConfirmReque = Volley.newRequestQueue(this);
        String url = GlobalVar.BASE_URL + "/assets/balance/" + GlobalVar.strAddress;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray mbalance = response.getJSONArray("balances");
                    for(int j = 0; j < GlobalVar.assetID.length; j++) {
                        for (int i = 0; i < mbalance.length(); i++) {
                            JSONObject mdata = mbalance.getJSONObject(i);
                            String strBalance = mdata.getString("balance");
                            long balance = Long.parseLong(strBalance);
                            double dBalance = (double) balance / 100000000;
                            if(mdata.getString("assetId").equals(GlobalVar.assetID[j])){
                                GlobalVar.balances[j] = dBalance;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                Intent intent = new Intent(IntroDone.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                IntroDone.this.finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("key", "Value");
                return headers;
            }
        };
        chckConfirmReque.add(req);
    }
}
