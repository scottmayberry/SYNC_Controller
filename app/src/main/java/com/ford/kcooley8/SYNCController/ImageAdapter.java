package com.ford.kcooley8.SYNCController;

import android.content.Context;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Vector;

public class ImageAdapter extends BaseAdapter {
    private static Context context;
    private static AudioSource[] sources;


    public ImageAdapter(Context contextIn, AudioSource[] sourcesIn) {
        context = contextIn;
        sources = sourcesIn;
    }

    @Override
    public int getCount() {
        return sources.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {
            gridView = inflater.inflate(R.layout.source_layout, null);
        }
        else {
            gridView = convertView;
        }

        ImageView imageView = (ImageView) gridView.findViewById(R.id.gridImage);
        imageView.setImageDrawable(BaseActivity.currentActivity.getResources().
                getDrawable(sources[position].icon, null));

        TextView textView = (TextView) gridView.findViewById(R.id.gridText);
        textView.setText(sources[position].name);

        LinearLayout button = (LinearLayout) gridView.findViewById(
                R.id.gridLayout);

        if (AudioSourcesActivity.sources[position] == MainActivity.audioSource) {
            activateButton(button);
        }

        return gridView;
    }

    public void activateButton(View button) {
        button.setBackground(BaseActivity.currentActivity.getDrawable(R.drawable.button_activated));
    }

    public void deactivateButton(View button) {
        button.setBackground(BaseActivity.currentActivity.getDrawable(R.drawable.button));
    }
}
