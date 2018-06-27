package com.pulsar.android.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;
import com.pulsar.android.GlobalVar;
import com.pulsar.android.R;
import com.pulsar.android.request.TransferTransactionRequest;
import com.pulsar.android.util.AddressUtil;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer;
import com.wavesplatform.wavesj.PrivateKeyAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;
import com.pulsar.android.components.UltraPagerAdapter;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SendActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, AdapterView.OnItemSelectedListener {
    UltraViewPager ultraViewPager;
    EditText et_amount, et_reais, et_desc, et_address, et_fee;
    TextView tx_feavelas, tx_option, tx_amount;
    Spinner spinFee;
    Dialog dialog;

    String strAddressTo = "";
    String strDesc = "";
    long balance, amount;
    long customFee = (long) 1000000;
    String strId = null;
    int nFeeType = 0, nCardType = 0;
    String[] strCards = new String[]{"Favelas", "Nertia", "UXSG"};
    //3KZuM35DfVjyyyzNqD3puFnYR21vbnatsuc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_send);
        strAddressTo = "3KhZCZkEc69Q2swU8TQNZSx862VWAUUukMA";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    initSlider();
                }
            }
        });
        initView();
    }

    public void initView() {
        tx_option = findViewById(R.id.tx_option);
        String strCrie = "<font color='black'>Sent transactions are </font><font color='red'> <b>NOT</b> </font><font color='black'>reversible.</font>";
        tx_option.setText(Html.fromHtml(strCrie), TextView.BufferType.SPANNABLE);

        et_amount = findViewById(R.id.edt_amount);
        et_address = findViewById(R.id.edt_address);
        et_desc = findViewById(R.id.edt_desc);
        et_reais = findViewById(R.id.edt_converted);
        et_fee = findViewById(R.id.edt_tax);
        tx_amount = findViewById(R.id.tx_amount);
        spinFee = findViewById(R.id.spin_fee);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, strCards);
        spinFee.setAdapter(adapter);
        spinFee.setOnItemSelectedListener(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                strAddressTo = intent.getStringExtra("address");
                try {
                    strAddressTo = GlobalVar.decryptMsg(strAddressTo);
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
                } catch (Exception e) {
                }
                et_address.setText(strAddressTo);
            }
        }
    }

    public void gotoQRScan(View view){
        Intent intent = new Intent(SendActivity.this, ScanQRActivity.class);
        startActivityForResult(intent, 100);
    }
    public void gotoBack(View view){
        this.finish();
    }
    public void gotoCardDetail(View view){

    }
    public void changeCurrency(View view){

    }
    public void checkSend(View view){
        strAddressTo = String.valueOf(et_address.getText());
        strDesc = String.valueOf(et_desc.getText());
        if(strDesc.equals("")){
            strDesc = "String";
        }
        try {
            amount = (long) (Float.parseFloat(String.valueOf(et_amount.getText())) * 100000000);
        } catch (Exception e) {

        }
        int res = validateTransfer();
        if (res == 0) {
            showDialog(this);
        } else {
            Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
        }
    }
    public void showDialog(Activity activity) {
        dialog = new Dialog(activity);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_transfer);
        Button btn_cancel = dialog.findViewById(R.id.btn_back);
        Button btn_confirm = dialog.findViewById(R.id.btn_confirm);
        TextView tx_type = dialog.findViewById(R.id.tx_type);
        TextView tx_amount = dialog.findViewById(R.id.tx_amount);
        TextView tx_fee = dialog.findViewById(R.id.tx_fee);
        TextView tx_title = dialog.findViewById(R.id.tx_title);
        tx_title.setText("Send " + strCards[nCardType] + "?");
        String strFavels = "<font color='red'>" + String.valueOf(et_amount.getText()) + "</font> <font color='black'>" + strCards[nCardType] + "</font>";
        tx_type.setText(Html.fromHtml(strFavels), TextView.BufferType.SPANNABLE);
        String strfees = "<font color='black'>Fee</font><font color='red'> - 0.010000 </font><font color='black'>" +strCards[nFeeType]+"</font>";
        tx_fee.setText(Html.fromHtml(strfees), TextView.BufferType.SPANNABLE);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTransfer();
            }
        });
        dialog.show();
    }
    Intent intent;
    @SuppressLint("CheckResult")
    public void doTransfer() {
        dialog.dismiss();
        long timestamp = System.currentTimeMillis();
        dialog = ProgressDialog.show(SendActivity.this, "",
                "Processing Transcation...", true);
        TransferTransactionRequest tx = new TransferTransactionRequest(
                GlobalVar.assetID[nCardType],
                nFeeType,
                GlobalVar.strPublic,
                strAddressTo, amount, timestamp, customFee, strDesc);
        PrivateKeyAccount mTemp = PrivateKeyAccount.fromPrivateKey(GlobalVar.strPrivate, 'N');
        byte[] mbyte = mTemp.getPrivateKey();
        tx.sign(mbyte);
        String strSignature = tx.getSignature();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String URL = GlobalVar.BASE_URL + "/assets/broadcast/transfer";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("senderPublicKey", GlobalVar.strPublic);
            jsonBody.put("assetId", GlobalVar.assetID[nCardType]);
            jsonBody.put("recipient", strAddressTo);
            jsonBody.put("amount", amount);
            jsonBody.put("fee", customFee);
            jsonBody.put("feeAssetId", GlobalVar.assetID[nFeeType]);
            jsonBody.put("timestamp", timestamp);
            jsonBody.put("attachment", tx.attachment);
            jsonBody.put("signature", strSignature);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                Log.i("Response", String.valueOf(response));
                long nTimeStamp = 0;
                try {
                    strId = response.getString("id");
                    nTimeStamp = response.getLong("timestamp");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent = new Intent(SendActivity.this, TransactionDetails.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("receipient", strAddressTo);
                mBundle.putString("sender", GlobalVar.strAddress);
                mBundle.putString("id", strId);
                mBundle.putLong("timestamp", nTimeStamp);
                mBundle.putString("description", tx.attachment);
                mBundle.putBoolean("unconfirmed", true);
                mBundle.putString("amount",String.valueOf(et_amount.getText()));
                mBundle.putInt("isSend", 1);
                mBundle.putInt("cardid", nCardType);
                mBundle.putInt("feeid", nFeeType);
                intent.putExtras(mBundle);
                checkBalance();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Transaction Failed! Try again later", Toast.LENGTH_SHORT).show();
                VolleyLog.e("Error: ", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
                return headers;
            }

//            @Override
//            public byte[] getBody() {
//                try {
//                    return requestBody == null ? null : requestBody.getBytes("utf-8");
//                } catch (UnsupportedEncodingException uee) {
//                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
//                            requestBody, "utf-8");
//                    return null;
//                }
//            }
//            @Override
//            public String getBodyContentType() {
//                return "application/json";
//            }
        };
        requestQueue.add(jsonObjectRequest);
    }
    public void checkBalance(){
        dialog = ProgressDialog.show(SendActivity.this, "",
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
                startActivityForResult(intent, 101);
                Intent _result = new Intent();
                _result.putExtra("Card", nCardType);
                SendActivity.this.setResult(101, _result);
                SendActivity.this.finish();
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

    public void chckInfo(String url, long nTimeStamp) {
        RequestQueue chckConfirmReque = Volley.newRequestQueue(this);
        Intent intent = new Intent(SendActivity.this, TransactionDetails.class);
        Bundle mBundle = new Bundle();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mBundle.putString("receipient", strAddressTo);
                mBundle.putString("id", strId);
                mBundle.putLong("timestamp", nTimeStamp);
                mBundle.putString("description", strDesc);
                mBundle.putBoolean("unconfirmed", false);
                intent.putExtras(mBundle);
                startActivity(intent);
                SendActivity.this.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mBundle.putString("receipient", strAddressTo);
                mBundle.putString("id", strId);
                mBundle.putLong("timestamp", nTimeStamp);
                mBundle.putString("description", strDesc);
                mBundle.putBoolean("unconfirmed", false);
                intent.putExtras(mBundle);
                startActivity(intent);
                SendActivity.this.finish();
            }
        });
        chckConfirmReque.add(req);
    }

    private int validateTransfer() {
        if (!AddressUtil.isValidAddress(strAddressTo)) {
            return R.string.invalid_address;
        } else if (strDesc.length() > TransferTransactionRequest.MaxAttachmentSize) {
            return R.string.attachment_too_long;
        } else if (amount <= 0) {
            return R.string.invalid_amount;
        } else if (amount > Long.MAX_VALUE - customFee) {
            return R.string.invalid_amount;
        } else if (customFee <= 0 || customFee < TransferTransactionRequest.MinFee) {
            return R.string.insufficient_fee;
        } else if (GlobalVar.strAddress.equals(strAddressTo)) {
            return R.string.send_to_same_address_warning;
        }
        return 0;
    }

    @Override
    public void handleResult(Result result) {

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initSlider(){
        ultraViewPager = findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        PagerAdapter adapter = new UltraPagerAdapter(true, this);
        ultraViewPager.setAdapter(adapter);
        ultraViewPager.initIndicator();
        ultraViewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(getColor(R.color.colorTabSelect))
                .setNormalColor(getColor(R.color.colorSpliter))
                .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        ultraViewPager.getIndicator().build();
        ultraViewPager.setInfiniteLoop(false);
        ultraViewPager.setMultiScreen(0.6f);
        ultraViewPager.setItemRatio(1.0f);
        ultraViewPager.setRatio(2.0f);
        ultraViewPager.setAutoMeasureHeight(true);
        ultraViewPager.setPageTransformer(false, new UltraScaleTransformer());
        ultraViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            public void onPageSelected(int position) {
                // Check if this is the page you want.
                Log.d("Page","changed" + position);
                nCardType = position;
                nFeeType = position;
                spinFee.setSelection(nFeeType);
                switch (position){
                    case 0:
                        tx_amount.setText("Amount: ƒ");
                        break;
                    case 1:
                        tx_amount.setText("Amount: ₦");
                        break;
                    case 2:
                        tx_amount.setText("Amount: Ʉ");
                        break;
                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        nFeeType = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
