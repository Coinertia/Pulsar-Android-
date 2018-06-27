package com.pulsar.android.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.Dash;
import com.pulsar.android.GlobalVar;
import com.pulsar.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextView tx_login;
    EditText ed_pwd;
    Button btn_continue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        initView();
    }
    public void initView(){
        ed_pwd = findViewById(R.id.edt_pwd);
        btn_continue = findViewById(R.id.bn_login);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strPwd = String.valueOf(ed_pwd.getText());
                if(strPwd.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please enter password.",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(strPwd.equals(GlobalVar.strPwd)){
                        checkBalance();

                    }else{
                        Toast.makeText(getApplicationContext(),"Password not match.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    Dialog dialog;
    public void checkBalance(){
        dialog = ProgressDialog.show(LoginActivity.this, "",
                "Please wait...", true);
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
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
                LoginActivity.this.finish();
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
