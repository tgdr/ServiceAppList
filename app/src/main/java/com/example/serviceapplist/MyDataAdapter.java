package com.example.serviceapplist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyDataAdapter extends BaseAdapter {

    List<AppBean> datas;
    Context ct;

    public MyDataAdapter(Context ct, List<AppBean> datas) {
        this.datas = datas;
        this.ct = ct;
    }

    /**
     * 重新封装一下notifyDataSetChanged直接调用方法传入新数据
     *
     * @param datas 要更新的数据
     */
    public void notifyChanged(List<AppBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(ct).inflate(R.layout.adapter_appinfo, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imgAppicon.setImageDrawable(datas.get(position).getAppIcon());
        holder.txtAppname.setText(datas.get(position).getAppName() + "");
        /**
         * 格式化一下获取到的app大小，这里会把long转换为double，小数点位数太多用不到所以按照惯例保留2位
         */
        holder.txtSize.setText(new DecimalFormat("0.00").format(datas.get(position).getAppSize() / (1024.0 * 1024.0)) + "mb");

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.img_appicon)
        ImageView imgAppicon;
        @BindView(R.id.txt_appname)
        TextView txtAppname;
        @BindView(R.id.txt_size)
        TextView txtSize;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
