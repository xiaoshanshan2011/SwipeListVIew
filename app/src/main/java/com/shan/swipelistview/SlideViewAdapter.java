package com.shan.swipelistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 陈俊山 on 2016/9/4.
 */
public class SlideViewAdapter extends BaseAdapter {
    private List<String> list;
    private LayoutInflater inflater;
    private int itemtype = 1;
    private OnMoveListener onMoveListener;

    public int getItemtype() {
        return itemtype;
    }

    public void setItemtype(int itemtype) {
        this.itemtype = itemtype;
    }

    public SlideViewAdapter(Context context, List<String> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    public void setRemoveListener(OnMoveListener onMoveListener) {
        this.onMoveListener = onMoveListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_product_listview, null);
            holder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
            holder.tv_save = (TextView) convertView.findViewById(R.id.tv_save);
            holder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_text.setText(list.get(position));

        holder.tv_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMoveListener != null)
                    onMoveListener.onRemoveItem(position);
            }
        });

        holder.tv_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onMoveListener.onSaveItem(position);
            }
        });
        return convertView;
    }

    public interface OnMoveListener {
        void onRemoveItem(int position);

        void onSaveItem(int position);
    }

    class ViewHolder {
        private TextView tv_delete;// 隐藏的侧滑删除按钮
        private TextView tv_save;
        private TextView tv_text;
    }

    public void notifyData(List<String> list){
        this.list = list;
        notifyDataSetChanged();
    }

}
