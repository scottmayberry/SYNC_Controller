package com.ford.kcooley8.SYNCController;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartdevicelink.proxy.rc.datatypes.ModuleData;

import java.util.Locale;


public class ClimateFragment extends Fragment {
    private ViewGroup rootView = null;

    private boolean initialized = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.climate, container, false);
        initialized = true;
        if (MainActivity.retrievedClimateData) {
            updateClimateLayout();
        }
        return rootView;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void updateClimateLayout() {
        final ImageButton powerButton = (ImageButton) rootView.findViewById(R.id.power);
        final TextView driverTempDisplay = (TextView) rootView.findViewById(R.id.driverTempDisplay);
        final TextView passTempDisplay = (TextView) rootView.findViewById(R.id.passTempDisplay);
        final Button dualButton = (Button) rootView.findViewById(R.id.dual);
        final Button autoButton = (Button) rootView.findViewById(R.id.auto);
        final Button acMenuButton = (Button) rootView.findViewById(R.id.ACButton);
        final ImageButton recircButton = (ImageButton) rootView.findViewById(R.id.recircButton);

        BaseActivity.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateFanSpeedLayout();

                if (MainActivity.power) {
                    activateButton(powerButton);
                }
                else {
                    deactivateButton(powerButton);
                }

                if (MainActivity.ac) {
                    activateButton(acMenuButton);
                }
                else {
                    deactivateButton(acMenuButton);
                }

                driverTempDisplay.setText(String.format(Locale.US, "%1$.1f", MainActivity.driverTemp) + "°");
                passTempDisplay.setText(String.format(Locale.US, "%1$.1f", MainActivity.passTemp) + "°");

                if (MainActivity.dual) {
                    activateButton(dualButton);
                }
                else {
                    deactivateButton(dualButton);
                }

                if (MainActivity.auto) {
                    activateButton(autoButton);
                }
                else {
                    deactivateButton(autoButton);
                }
                if(MainActivity.recirc) {
                    activateButton(recircButton);
                }
                else {
                   deactivateButton(recircButton);
                }
            }
        });
    }

    public void updateFanSpeedLayout() {
        ImageView fanDisplay = (ImageView) rootView.findViewById(R.id.fanDisplay);
        Drawable newImage = null;
        switch (MainActivity.fanSpeed) {
            case 0:
                newImage = BaseActivity.currentActivity.getDrawable(R.drawable.fan_display0);
                break;
            case 1:
                newImage = BaseActivity.currentActivity.getDrawable(R.drawable.fan_display1);
                break;
            case 2:
                newImage = BaseActivity.currentActivity.getDrawable(R.drawable.fan_display2);
                break;
            case 3:
                newImage = BaseActivity.currentActivity.getDrawable(R.drawable.fan_display3);
                break;
            case 4:
                newImage = BaseActivity.currentActivity.getDrawable(R.drawable.fan_display4);
                break;
            case 5:
                newImage = BaseActivity.currentActivity.getDrawable(R.drawable.fan_display5);
                break;
            case 6:
                newImage = BaseActivity.currentActivity.getDrawable(R.drawable.fan_display6);
                break;
            case 7:
                newImage = BaseActivity.currentActivity.getDrawable(R.drawable.fan_display7);
                break;
        }
        if (MainActivity.auto) {
            newImage = BaseActivity.currentActivity.getDrawable(R.drawable.fan_display_off);
        }
        fanDisplay.setImageDrawable(newImage);
    }

    public static void activateButton(View button) {
        button.setBackground(BaseActivity.currentActivity.getDrawable(R.drawable.button_activated));
    }

    public static void deactivateButton(View button) {
        button.setBackground(BaseActivity.currentActivity.getDrawable(R.drawable.button));
    }
}
