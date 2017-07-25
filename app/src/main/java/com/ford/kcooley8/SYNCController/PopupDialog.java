package com.ford.kcooley8.SYNCController;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by omakke on 7/25/2016.
 */
public class PopupDialog extends DialogFragment {

    public static boolean visible = false;
    public final int REFRESH = 0;
    public final int REFRESH_COMPLETED = 1;
    public final int ULTRA_FILTERED = 2;
    public final int FILTERED_AIR = 3;
    public int type;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup, container, false);

        Button close = (Button) v.findViewById(R.id.btnPopupClose);
        ImageView img = (ImageView) v.findViewById(R.id.imgPopupPic);
        img.setClickable(true);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        visible = true;
        if(type == FILTERED_AIR){
            img.setBackgroundResource(R.drawable.filtered_air);
        } else if(type == ULTRA_FILTERED){
            img.setBackgroundResource(R.drawable.ultra_filtered);
        } else if(type == REFRESH) {
            img.setBackgroundResource(R.drawable.refresh_started);
        } else if(type == REFRESH_COMPLETED){
            img.setBackgroundResource(R.drawable.refresh_completed);
        }


        return v;
    }
    @Override
    public void onDestroy(){
        visible = false;
        super.onDestroy();
    }

    @Override
    public void onStart(){
        super.onStart();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = 6*metrics.widthPixels/7;
        int height = 6*metrics.heightPixels/7;
        getDialog().getWindow().setLayout(width, height);

    }

}
