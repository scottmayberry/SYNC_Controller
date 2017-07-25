package com.ford.kcooley8.SYNCController;

import android.app.DialogFragment;
import android.app.IntentService;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by omakke on 7/25/2016.
 */
public class AirqualityDialog extends DialogFragment {
    private int pm2p5;
    TextView aqiNowText;
    TextView aqiAvgText;
    TextView barView[] = {null, null, null, null, null, null};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.aqi_dialog, container, false);

        aqiNowText = (TextView) v.findViewById(R.id.txtActualPMValue);
        aqiAvgText = (TextView) v.findViewById(R.id.txtAvgPMValue);
        barView[0] = (TextView) v.findViewById(R.id.txtBar1);
        barView[1] = (TextView) v.findViewById(R.id.txtBar2);
        barView[2] = (TextView) v.findViewById(R.id.txtBar3);
        barView[3] = (TextView) v.findViewById(R.id.txtBar4);
        barView[4] = (TextView) v.findViewById(R.id.txtBar5);
        barView[5] = (TextView) v.findViewById(R.id.txtBar6);
        SharedData.output = this;
        aqiAvgText.setText(Float.toString(SharedData.aqi_avg));
        aqiNowText.setText(Integer.toString(SharedData.aqi_now));

        Button clear = (Button) v.findViewById(R.id.btnClear);
        Button close = (Button) v.findViewById(R.id.btnClose);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedData.clearData();
                SharedData.setCabinAQI(0); // To trigger an update
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = 6*metrics.widthPixels/7;
        int height = 6*metrics.heightPixels/7;
        getDialog().getWindow().setLayout(width, height);

    }

    public void updateView(final int aqi_now, final float aqi_avg, final int[] aqi_bar){
        if(aqi_bar.length != 3){
            Log.e("AirQualityDialog", "Aqi bar dimension is wrong (not 3)");
        }
        if(aqiNowText == null || aqiAvgText == null || barView == null){
            return;
        }
        for(int ii = 0; ii < barView.length; ii++){
            if(barView[ii] == null) return;
        }

        MainActivity.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                aqiNowText.setText(Integer.toString(aqi_now));
                setBackground(aqiNowText, aqi_now);
                setForeground(aqiNowText, aqi_now);
                aqiAvgText.setText(Integer.toString((int) aqi_avg));
                setBackground(aqiAvgText, (int) aqi_avg);
                setForeground(aqiAvgText, (int) aqi_avg);

                ViewGroup.LayoutParams params =
                        new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                  ViewGroup.LayoutParams.MATCH_PARENT, (float) aqi_bar[0]);

                barView[0].setLayoutParams(params);
                barView[0].setBackgroundColor(Color.rgb(0,255,0));
                if(aqi_bar[0] > 0) {
                    barView[0].setText(Integer.toString(aqi_bar[0]));
                } else {
                    barView[0].setText("");
                }


                params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                   ViewGroup.LayoutParams.MATCH_PARENT, (float) aqi_bar[1]);

                barView[1].setLayoutParams(params);
                barView[1].setBackgroundColor(Color.rgb(255,255,0));
                if(aqi_bar[1] > 0) {
                    barView[1].setText(Integer.toString(aqi_bar[1]));
                } else {
                    barView[1].setText("");
                }

                params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                   ViewGroup.LayoutParams.MATCH_PARENT, (float) aqi_bar[2]);
                barView[2].setLayoutParams(params);
                barView[2].setBackgroundColor(Color.rgb(255,185,0));
                if(aqi_bar[2] > 0) {
                    barView[2].setText(Integer.toString(aqi_bar[2]));
                } else {
                    barView[2].setText("");
                }

                params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, (float) aqi_bar[3]);
                barView[3].setLayoutParams(params);
                barView[3].setBackgroundColor(Color.rgb(255,0,0));
                if(aqi_bar[3] > 0) {
                    barView[3].setText(Integer.toString(aqi_bar[3]));
                } else {
                    barView[3].setText("");
                }

                params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, (float) aqi_bar[4]);
                barView[4].setLayoutParams(params);
                barView[4].setBackgroundColor(Color.rgb(128,0,128));
                if(aqi_bar[4] > 0) {
                    barView[4].setText(Integer.toString(aqi_bar[4]));
                } else {
                    barView[4].setText("");
                }

                params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, (float) aqi_bar[5]);
                barView[5].setLayoutParams(params);
                barView[5].setBackgroundColor(Color.rgb(128,0,0));
                if(aqi_bar[5] > 0) {
                    barView[5].setText(Integer.toString(aqi_bar[5]));
                } else {
                    barView[5].setText("");
                }

            }
        });


    }

    public void setBackground(TextView view, int value){
        if(value <= 35){
            view.setBackgroundColor(Color.rgb(0,255,0));
        } else if(value <= 75){
            view.setBackgroundColor(Color.rgb(255,255,0));
        } else if(value <= 116){
            view.setBackgroundColor(Color.rgb(255,185,0));
        }
        else if(value <= 151){
            view.setBackgroundColor(Color.rgb(255,0,0));
        }
        else if(value <= 251){
            view.setBackgroundColor(Color.rgb(128,0,128));
        }
        else {
            view.setBackgroundColor(Color.rgb(128,0,0));
        }
    }

    public void setForeground(TextView view, int value){
        if(value <= 35){
            view.setTextColor(Color.rgb(0,0,0));
        } else if(value <= 75){
            view.setTextColor(Color.rgb(0,0,0));
        } else if(value <= 116){
            view.setTextColor(Color.rgb(0,0,0));
        }
        else if(value <= 151){
            view.setTextColor(Color.rgb(0,0,0));
        }
        else if(value <= 251){
            view.setTextColor(Color.rgb(255,255,255));
        }
        else {
            view.setTextColor(Color.rgb(255,255,255));
        }
    }

}
