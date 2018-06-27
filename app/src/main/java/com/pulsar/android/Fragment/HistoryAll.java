package com.pulsar.android.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pulsar.android.Activity.DashboardActivity;
import com.pulsar.android.Activity.TransactionDetails;
import com.pulsar.android.Adapter.HistoryListAdapter;
import com.pulsar.android.Adapter.HistoryListAllAdapter;
import com.pulsar.android.GlobalVar;
import com.pulsar.android.Models.HistoryItem;
import com.pulsar.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryAll extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public HistoryAll() {
        // Required empty public constructor
    }

    ListView mListView;
    HistoryListAllAdapter adapter;
    public SwipeRefreshLayout swipeAll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_all, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initList();
    }

    public void initList() {
        swipeAll = getView().findViewById(R.id.lyt_swipe);
        mListView = getView().findViewById(R.id.list_view);
        ArrayList<HistoryItem> mTemp = new ArrayList<>();
        boolean isFirst = true;
        for (int i = 0; i < GlobalVar.mUnconfirmedDataAll.size(); i++) {
            HistoryItem mItem = GlobalVar.mUnconfirmedDataAll.get(i);
            mItem.setFirst(isFirst);
            mTemp.add(mItem);
            isFirst = false;
        }
        isFirst = true;
        for (int i = 0; i < GlobalVar.mHistoryDataAll.size(); i++) {
            HistoryItem mItem = GlobalVar.mHistoryDataAll.get(i);
            mItem.setFirst(isFirst);
            mTemp.add(mItem);
            isFirst = false;
        }
        adapter = new HistoryListAllAdapter(getActivity(), mTemp);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HistoryItem mItem = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), TransactionDetails.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("receipient", mItem.getStrReceipt());
                mBundle.putString("sender", mItem.getStrSender());
                mBundle.putString("id", mItem.getStrId());
                mBundle.putLong("timestamp", mItem.getnTime());
                mBundle.putString("description", mItem.getStrDesc());
                mBundle.putBoolean("unconfirmed", false);
                mBundle.putString("amount", mItem.getStrAmount());
                mBundle.putInt("isSend", mItem.getIsSender());
                mBundle.putInt("cardid", mItem.getCardId());
                mBundle.putInt("feeid", mItem.getFeeId());
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });

        swipeAll = getView().findViewById(R.id.lyt_swipe);
        swipeAll .setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeAll .post(new Runnable() {
                            @Override
                            public void run() {
                                swipeAll .setRefreshing(true);
                                updateData();
                            }
                        }
        );
    }

    public void updateList() {
        if (adapter != null) {
            ArrayList<HistoryItem> mTemp = new ArrayList<>();
            boolean isFirst = true;
            for (int i = 0; i < GlobalVar.mUnconfirmedDataAll.size(); i++) {
                HistoryItem mItem = GlobalVar.mUnconfirmedDataAll.get(i);
                mItem.setFirst(isFirst);
                mTemp.add(mItem);
                isFirst = false;
            }
            isFirst = true;
            for (int i = 0; i < GlobalVar.mHistoryDataAll.size(); i++) {
                HistoryItem mItem = GlobalVar.mHistoryDataAll.get(i);
                mItem.setFirst(isFirst);
                mTemp.add(mItem);
                isFirst = false;
            }
            adapter.setData(mTemp);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        updateList();
        super.onResume();
    }
    @Override
    public void onRefresh() {
        updateData();
    }
    public void updateData(){
        swipeAll .setRefreshing(true);
        checkBalance();
        getUnconfirm();
        getHistory();
    }

    public void getHistory() {
        RequestQueue chckConfirmReque = Volley.newRequestQueue(getActivity());
        String url = GlobalVar.BASE_URL + "/transactions/address/" + GlobalVar.strAddress + "/limit/100";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONArray mData = response.getJSONArray(0);
                    GlobalVar.mHistoryData.clear();
                    GlobalVar.mHistoryDataAll.clear();
                    for (int i = 0; i < mData.length(); i++) {
                        JSONObject mItem = mData.getJSONObject(i);
                        HistoryItem mTemp = new HistoryItem(
                                mItem.getString("recipient"),
                                mItem.getString("sender"),
                                mItem.getString("id"),
                                mItem.getString("assetId"),
                                mItem.getString("feeAsset"),
                                mItem.getString("attachment"),
                                mItem.getLong("timestamp"),
                                mItem.getLong("amount"),
                                mItem.getLong("fee"),
                                false
                        );
                        GlobalVar.mHistoryData.add(mTemp);
                    }
                    GlobalVar.mHistoryDataAll = new ArrayList<>(GlobalVar.mHistoryData);
                    updateList();
                    for (int i = 0; i < GlobalVar.mHistoryData.size(); i++) {
                        if (GlobalVar.mHistoryData.get(i).getIsSender() != 3) {
                            GlobalVar.mLastSend = GlobalVar.mHistoryData.get(i);
                            break;
                        }
                    }
                    for (int i = 0; i < GlobalVar.mHistoryData.size(); i++) {
                        if (GlobalVar.mHistoryData.get(i).getIsSender() == 3) {
                            GlobalVar.mLastReceive = GlobalVar.mHistoryData.get(i);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeAll .setRefreshing(false);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred
                        Log.d("errr", "err");

                    }
                }
        );
        chckConfirmReque.add(jsonArrayRequest);
    }

    public void getUnconfirm() {
        RequestQueue chckConfirmReque = Volley.newRequestQueue(getActivity());
        String url = GlobalVar.BASE_URL + "/transactions/unconfirmed/";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    GlobalVar.mUnconfirmedData.clear();
                    GlobalVar.mUnconfirmedDataAll.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject mItem = response.getJSONObject(i);
                        HistoryItem mTemp = new HistoryItem(
                                mItem.getString("recipient"),
                                mItem.getString("sender"),
                                mItem.getString("id"),
                                mItem.getString("assetId"),
                                mItem.getString("feeAsset"),
                                mItem.getString("attachment"),
                                mItem.getLong("timestamp"),
                                mItem.getLong("amount"),
                                mItem.getLong("fee"),
                                true
                        );
                        if (mTemp.getStrSender().equals(GlobalVar.strAddress) || mTemp.getStrReceipt().equals(GlobalVar.strAddress)) {
                            GlobalVar.mUnconfirmedData.add(mTemp);
                        }
                    }
                    GlobalVar.mUnconfirmedDataAll = new ArrayList<>(GlobalVar.mUnconfirmedData);
                    updateList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred
                        Log.d("errr", "err");

                    }
                }
        );
        chckConfirmReque.add(jsonArrayRequest);
    }
    Dialog dialog;
    public void checkBalance(){
        RequestQueue chckConfirmReque = Volley.newRequestQueue(getActivity());
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
