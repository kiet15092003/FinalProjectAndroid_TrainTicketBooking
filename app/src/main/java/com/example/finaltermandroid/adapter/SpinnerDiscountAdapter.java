package com.example.finaltermandroid.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.finaltermandroid.model.Discount;
import com.example.finaltermandroid.model.TrainStation;

import java.util.List;

public class SpinnerDiscountAdapter  extends BaseAdapter {
    private Context context;
    private List<Discount> items;
    public SpinnerDiscountAdapter(Context context, List<Discount> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Discount item = (Discount) getItem(position);
        TextView textView = new TextView(context);
        if (position==0){
            textView.setText("No discount");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            textView.setPadding(16, 16, 16, 16);
        } else {
            textView.setText("Discount code: " + item.getDiscountKey() +  "\n" + "Discount value: " + item.getDiscountValue()*100 + "%");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            textView.setPadding(16, 16, 16, 16);
        }
        return textView;
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
