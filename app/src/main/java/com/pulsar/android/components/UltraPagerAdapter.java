package com.pulsar.android.components;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pulsar.android.GlobalVar;
import com.pulsar.android.R;

public class UltraPagerAdapter extends PagerAdapter {
    private boolean isMultiScr;
    SparseArray<View> views = new SparseArray<View>();
    private Context mContext;
    public UltraPagerAdapter(boolean isMultiScr, Context mContext) {
        this.isMultiScr = isMultiScr;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RelativeLayout linearLayout = (RelativeLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.card_slider_child, null);
        ImageView img = linearLayout.findViewById(R.id.img_card);
        TextView tx_balance = linearLayout.findViewById(R.id.tx_balance);
        linearLayout.setId(R.id.item_id);
        switch (position) {
            case 0:
                tx_balance.setText("" + GlobalVar.balances[0]);
                img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.img_card_1));
                break;
            case 1:
                tx_balance.setText("" + GlobalVar.balances[1]);
                img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.img_card_2));
                break;
            case 2:
                tx_balance.setText("" + GlobalVar.balances[2]);
                img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.img_card_3));
                break;
        }
        linearLayout.setTag(position);
        container.addView(linearLayout);
        views.put(position, linearLayout);
        return linearLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View)object;
        ((ViewPager) container).removeView(view);
        views.remove(position);
        view = null;
    }

    @Override
    public void notifyDataSetChanged() {
        int key = 0;
        for(int i = 0; i < views.size(); i++) {
            key = views.keyAt(i);
            View view = views.get(key);
            TextView  tx = view.findViewById(R.id.tx_balance);
            tx.setText("" + GlobalVar.balances[i]);
        }
        super.notifyDataSetChanged();
    }
}
