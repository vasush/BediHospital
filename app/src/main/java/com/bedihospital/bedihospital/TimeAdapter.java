package com.bedihospital.bedihospital;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

/**
 * Created by Vasu on 06-Nov-17.
 */

public class TimeAdapter extends BaseAdapter {

    private Context mContext;

    String [] time = {"10:00 am ", "10:15 am ", "10:30 am ", "10:45 am ", "11:00 am ", "11:15 am ", "11:30 am ", "11:45 am ", "12:00 pm ",
            "12:15 pm ", "12:30 pm ", "12:45 pm ", "01:00 pm ", "01:15 pm ", "01:30 pm ", "01:45 pm ", "02:00 pm ", "02:15 pm ",
            "02:30 pm ", "02:45 pm ", "03:00 pm ", "03:15 pm ", "03:30 pm ", "03:45 pm ", "04:00 pm ", "04:15 pm ", "04:30 pm ",
            "04:45 pm "};

    //constructor
    public  TimeAdapter(Context c) {
        mContext = c;
    }
    @Override
    public int getCount() {
        return time.length;
    }

    @Override
    public Object getItem(int position) {
        return time[position];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int postion, View view, ViewGroup viewGroup) {

        //Resources r = Resources.getSystem();
        //float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());

        TextView textView = new TextView(mContext);
        textView.setText(time[postion]);
        textView.setLayoutParams(new GridView.LayoutParams(220,100));
        //textView.setLayoutParams(new GridView.LayoutParams((int)px, (int)px));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundResource(R.drawable.home_layout_border);
        return textView;
    }
}
