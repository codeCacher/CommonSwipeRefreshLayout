package com.cs.refresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/16.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final Context mContext;
    private List<Integer> mData;

    public MyAdapter(Context context) {
        this.mContext = context;
        this.mData = new ArrayList<>();
    }

    public void setData(List<Integer> list) {
        if (list == null) {
            this.mData.clear();
        } else {
            this.mData = list;
        }
        notifyDataSetChanged();
    }

    public void addData(List<Integer> list) {
        if (list == null) {
            return;
        }
        this.mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTV.setText(String.valueOf(mData.get(position)));
        holder.mLl.setBackgroundColor(0);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTV;
        LinearLayout mLl;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTV = itemView.findViewById(R.id.tv);
            mLl = itemView.findViewById(R.id.ll);
        }
    }
}
