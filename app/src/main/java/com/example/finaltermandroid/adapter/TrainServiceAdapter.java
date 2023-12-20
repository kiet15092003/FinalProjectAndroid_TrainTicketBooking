package com.example.finaltermandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.model.TrainSchedule;
import com.example.finaltermandroid.model.TrainService;
import com.example.finaltermandroid.model.TrainStation;

import java.util.List;

public class TrainServiceAdapter extends RecyclerView.Adapter<TrainServiceAdapter.ViewHolder>{
    private Context context;
    private List<TrainService> items;

    public TrainServiceAdapter(Context context, List<TrainService> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_service, parent, false);
        return new TrainServiceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrainService trainService = items.get(position);
        holder.tv_name.setText(trainService.getName());
        holder.tv_description.setText(trainService.getDescription());
        holder.tv_price.setText(String.valueOf( trainService.getPrice() / 1000) + "k");
        holder.btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onButtonClick(holder.tv_name.getText().toString(), trainService.getPrice());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_price,tv_description;
        Button btn_choose;

        ViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_price = itemView.findViewById(R.id.tv_price);
            btn_choose = itemView.findViewById(R.id.btn_choose);
        }
    }
    public interface OnButtonClickListener {
        void onButtonClick(String serviceName, long price);
    }
    private TrainServiceAdapter.OnButtonClickListener listener;

    public void setOnButtonClickListener(TrainServiceAdapter.OnButtonClickListener listener) {
        this.listener = listener;
    }
}
