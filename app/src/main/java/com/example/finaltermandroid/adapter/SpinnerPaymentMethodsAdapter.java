package com.example.finaltermandroid.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.finaltermandroid.model.Discount;

import java.util.List;

public class SpinnerPaymentMethodsAdapter extends BaseAdapter {
    private Context context;
    private List<String> items;
    public SpinnerPaymentMethodsAdapter(Context context, List<String> items) {
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
        String item = (String) getItem(position);
        TextView textView = new TextView(context);
        textView.setText(item);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }
}
