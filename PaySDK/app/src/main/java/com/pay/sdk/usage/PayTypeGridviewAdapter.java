package com.pay.sdk.usage;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pay.sdk.R;

import java.util.List;

import sdk.pay.model.PayTypeModel;

/**
 * Created by lj on 2017/9/25 0025.
 */

public class PayTypeGridviewAdapter extends BaseAdapter {
    private List<PayTypeModel> mTypeModelList;
    private LayoutInflater mLayoutInflater;
    private int mSelectedPosition = 0;
    private boolean mPortrait;

    public PayTypeGridviewAdapter(Context context, List<PayTypeModel> payTypeModelList) {
        mTypeModelList = payTypeModelList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mTypeModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTypeModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_listviewlayout, null);
            holder.image_junpay_type = (ImageView) convertView.findViewById(R.id.image_junpay_type);
            holder.textView_junpay_type_name = (TextView) convertView.findViewById(R.id.TextView_junpay_type_name);
            holder.textView_junpay_type_tips = (TextView) convertView.findViewById(R.id.TextView_junpay_type_tips);
            holder.image_Checked = (ImageView) convertView.findViewById(R.id.ImageButton_junpay_type_Checked);
            holder.land_layout = (ViewGroup) convertView.findViewById(R.id.item_listview_land_layout);
            holder.port_line_view = convertView.findViewById(R.id.item_listview_layout_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String description = "暂无";
        switch (mTypeModelList.get(position).getTypeid()) {
            case 3:
                holder.image_junpay_type.setImageResource(R.drawable.u3);
                description = "推荐微信用户使用";
                break;
            case 4:
                holder.image_junpay_type.setImageResource(R.drawable.u4);
                description = "推荐支付宝用户使用";
                break;
            case 11:
                holder.image_junpay_type.setImageResource(R.drawable.u11);
                description = "推荐QQ用户使用";
                break;
            case 12:
                holder.image_junpay_type.setImageResource(R.drawable.u12);
                description = "推荐京东用户使用";
                break;
            default:
                holder.image_junpay_type.setImageURI(null);
                break;
        }
        if (mPortrait) {
            holder.image_Checked.setImageResource(mSelectedPosition == position ? R.drawable.image_icon_radiobutton_yes : R.drawable.image_icon_radiobutton_no);
            holder.port_line_view.setVisibility(position == mTypeModelList.size() - 1 ? View.INVISIBLE : View.VISIBLE);
        } else {
            holder.image_Checked.setVisibility(mSelectedPosition == position ? View.VISIBLE : View.INVISIBLE);
            holder.land_layout.setBackgroundResource(mSelectedPosition == position ? R.drawable.bg_item_paytype_selected : R.drawable.bg_item_paytype_unselected);
        }
        holder.textView_junpay_type_name.setText(String.format("%s", mTypeModelList.get(position).getTypename()));
        String contactWay = mTypeModelList.get(position).getContactWay();
        if (!TextUtils.isEmpty(contactWay)) {
            description = contactWay;
        }
        holder.textView_junpay_type_tips.setText(String.format("%s", description));
        return convertView;
    }

    private static final class ViewHolder {
        ViewGroup land_layout;
        ImageView image_junpay_type;
        TextView textView_junpay_type_name;
        TextView textView_junpay_type_tips;
        ImageView image_Checked;
        View port_line_view;
    }

    public void Portrait(boolean portrait) {
        mPortrait = portrait;
    }

    public void selectItem(int position) {
        this.mSelectedPosition = position;
        notifyDataSetChanged();
    }

    public void setList(List<PayTypeModel> payTypeModel) {
        mTypeModelList = payTypeModel;
    }
}
