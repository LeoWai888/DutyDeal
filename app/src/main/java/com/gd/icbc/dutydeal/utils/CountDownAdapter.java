package com.gd.icbc.dutydeal.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gd.icbc.dutydeal.R;
import com.gd.icbc.dutydeal.json.TimeDownBean;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class CountDownAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<TimeDownBean> mTimeDownBeanList;
    private Voice mvoice;
    private static int ringTime;

    public CountDownAdapter(Context context, List<TimeDownBean> mTimeDownBeanList) {
        this.mContext = context;
        this.mTimeDownBeanList = mTimeDownBeanList;
        mvoice = new Voice(context);
    }

    public static void setRingTime(String number) {
        CountDownAdapter.ringTime = Integer.parseInt(number);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false );
        return new ViewHolder(view);
    }

    //holder复用
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            TimeDownBean timeDownBean = mTimeDownBeanList.get(position);
            viewHolder.contentTv.setText(timeDownBean.getContent());
            Bitmap bitmap=BitmapUtils.stringtoBitmap(timeDownBean.getPhoto());    //2018
            viewHolder.photoData.setImageBitmap(bitmap);   //设置图片
            try {
                setTime(viewHolder, position);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void setTime(ViewHolder holder, int position) throws ParseException {
        TimeDownBean timeDownBean = mTimeDownBeanList.get(position);
        holder.timeTv.setVisibility(View.VISIBLE);
        long useTime = timeDownBean.getUseTime();
        setTimeShow(useTime, holder);
    }

    @SuppressLint({"ResourceAsColor", "NewApi"})
    private void setTimeShow(long useTime, ViewHolder holder) {
        boolean isCountup = false;
        if (useTime < 0) {
            useTime = -useTime;
            isCountup = true;
        }
        int hour = (int) (useTime / 3600);
        int min = (int) (useTime / 60 % 60);
        int second = (int) (useTime % 60);
        int day = (int) (useTime / 3600 / 24);
        String strTime = String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second);
        holder.timeTv.setText(strTime);
        holder.timeTv.setTextColor(Color.BLACK);

        if (hour == 0 && (min <= ringTime) && (min>=1))  //进入报警时间，变成黄色
        {
            holder.itemView.setBackgroundResource(R.drawable.yellowitem);
            if (hour == 0 && min > 0 && (second == 0 || second == 30) && !isCountup)
            {
                mvoice.speak(holder.contentTv.getText()+"距离下次打卡时间还有" + min + "分钟");
            }
        }
        else if (hour == 0 && (min >= 0 && min <1))   //最后一分钟变成红色框
        {
            holder.itemView.setBackgroundResource(R.drawable.reditem);
            if (hour == 0 && min == 0 && (second == 0 || second == 20 || second == 40) && !isCountup)  //最后一分钟
            {
                mvoice.speak(String.valueOf(second) + "秒");
            }

        } else {
            holder.itemView.setBackgroundResource(R.drawable.greenitem);
        }
        if (isCountup) {
            //TODO:报警
            holder.itemView.setBackgroundResource(R.drawable.reditem);
        }
    }


    @Override
    public int getItemCount() {
        return mTimeDownBeanList != null ? mTimeDownBeanList.size() : 0;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView contentTv;
        private TextView timeTv;
        private CircleImageView photoData;

        private ViewHolder(View itemView) {
            super(itemView);
            init(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, getLayoutPosition());
                    }
                }
            });
        }


        private void init(View itemView) {
            contentTv = (TextView) itemView.findViewById(R.id.content_tv);
            timeTv = (TextView) itemView.findViewById(R.id.time_tv);
            photoData=(CircleImageView)itemView.findViewById(R.id.registrar_img);

        }
    }

    //设置item间距的辅助类
    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration
    {
        private  int space;
        public SpacesItemDecoration(int space)
        {
            this.space=space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,RecyclerView parent, RecyclerView.State state)
        {
            outRect.left=space;
            outRect.right=space;

        }
    }
    //对外部暴漏点击事件接口
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public OnItemClickListener itemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}

