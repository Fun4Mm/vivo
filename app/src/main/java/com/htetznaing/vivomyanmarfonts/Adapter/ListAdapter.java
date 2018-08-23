package com.htetznaing.vivomyanmarfonts.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htetznaing.vivomyanmarfonts.R;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<String> title;
    ArrayList<Typeface> typeface;

    public ListAdapter(Activity activity, ArrayList<String> title,ArrayList<Typeface> typeface) {
        this.activity = activity;
        this.title = title;
        this.typeface=typeface;
    }

    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.item,null);
        TextView textView = view.findViewById(R.id.title);
        textView.setText(title.get(i));
        textView.setTypeface(typeface.get(i));
        return view;
    }
}
