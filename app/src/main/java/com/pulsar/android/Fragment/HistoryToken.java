package com.pulsar.android.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pulsar.android.Activity.DashboardActivity;
import com.pulsar.android.Activity.LoginActivity;
import com.pulsar.android.Activity.SendActivity;
import com.pulsar.android.Activity.TransactionDetails;
import com.pulsar.android.Adapter.HistoryListAdapter;
import com.pulsar.android.GlobalVar;
import com.pulsar.android.Models.HistoryItem;
import com.pulsar.android.R;
import com.pulsar.android.components.UltraPagerAdapter;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryToken extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public UltraViewPager ultraViewPager;
    public ListView mListView;
    SearchView sv_key;
    public int nCardType = 0;
    public HistoryListAdapter adapter;
    public SwipeRefreshLayout swipeToken;
    PagerAdapter ultraPagerAdapter;
    public HistoryToken() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_token, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initSlider();
        initList();
    }

    public void initList() {
        swipeToken = getView().findViewById(R.id.lyt_swipe);
        mListView = getView().findViewById(R.id.list_view);
        sv_key = getView().findViewById(R.id.search_view);
        ArrayList<HistoryItem> mTemp = new ArrayList<>();
        boolean isFirst = true;
        for (int i = 0; i < GlobalVar.mUnconfirmedData.size(); i++) {
            if (GlobalVar.mUnconfirmedData.get(i).getStrAsset().equals(GlobalVar.assetID[nCardType])) {
                HistoryItem mItem = GlobalVar.mUnconfirmedData.get(i);
                mItem.setFirst(isFirst);
                mTemp.add(mItem);
                isFirst = false;
            }
        }
        isFirst = true;
        for (int i = 0; i < GlobalVar.mHistoryData.size(); i++) {
            if (GlobalVar.mHistoryData.get(i).getStrAsset().equals(GlobalVar.assetID[nCardType])) {
                HistoryItem mItem = GlobalVar.mHistoryData.get(i);
                mItem.setFirst(isFirst);
                mTemp.add(mItem);
                isFirst = false;
            }
        }
        adapter = new HistoryListAdapter(getActivity(), mTemp);
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
        sv_key.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // TODO Auto-generated method stub
                adapter.getFilter().filter(query);
                return false;
            }
        });

        swipeToken.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeToken.post(new Runnable() {
                            @Override
                            public void run() {
                                swipeToken.setRefreshing(true);
                                updateData();
                            }
                        }
        );
    }

    public void updateList() {
        if (adapter != null) {
            ArrayList<HistoryItem> mTemp = new ArrayList<>();
            boolean isFirst = true;
            for (int i = 0; i < GlobalVar.mUnconfirmedData.size(); i++) {
                if (GlobalVar.mHistoryData.get(i).getStrAsset().equals(GlobalVar.assetID[nCardType])) {
                    HistoryItem mItem = GlobalVar.mUnconfirmedData.get(i);
                    mItem.setFirst(isFirst);
                    mTemp.add(mItem);
                    isFirst = false;
                }
            }
            isFirst = true;
            for (int i = 0; i < GlobalVar.mHistoryData.size(); i++) {
                if (GlobalVar.mHistoryData.get(i).getStrAsset().equals(GlobalVar.assetID[nCardType])) {
                    HistoryItem mItem = GlobalVar.mHistoryData.get(i);
                    mItem.setFirst(isFirst);
                    mTemp.add(mItem);
                    isFirst = false;
                }
            }
            adapter.setData(mTemp);
            adapter.notifyDataSetChanged();
        }
        if(ultraPagerAdapter!=null){
            ultraPagerAdapter.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initSlider() {
        ultraViewPager = getView().findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        ultraPagerAdapter= new UltraPagerAdapter(true, getActivity());
        ultraViewPager.setAdapter(ultraPagerAdapter);
        ultraViewPager.initIndicator();
        ultraViewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(getActivity().getColor(R.color.colorTabSelect))
                .setNormalColor(getActivity().getColor(R.color.colorSpliter))
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
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                // Check if this is the page you want.
                Log.d("Page", "changed" + position);
                nCardType = position;
                updateList();
            }
        });
        ultraViewPager.setCurrentItem(nCardType);
    }

    @Override
    public void onRefresh() {
        updateData();
    }
    public void updateData(){
        sv_key.setQuery("",false);
        swipeToken.setRefreshing(true);
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
                swipeToken.setRefreshing(false);
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
