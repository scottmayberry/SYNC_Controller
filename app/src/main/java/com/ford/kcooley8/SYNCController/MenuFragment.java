package com.ford.kcooley8.SYNCController;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MenuFragment extends Fragment {

    private ViewGroup rootView = null;
    private boolean initialized = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.menu, container, false);
        initialized = true;
        initializeMenuLayout();
        return rootView;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initializeMenuLayout() {
        final RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.menuLayout);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if (BaseActivity.currentActivity != null) {
                    if (MainActivity.connected) {
                        connected();
                    } else {
                        disconnected();
                    }
                }
            }
        });
    }

    public void connected() {
        MainActivity.connected = true;
        BaseActivity.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.retrievedAudioData && MainActivity.retrievedClimateData) {
                    LinearLayout audioButton = (LinearLayout) rootView.findViewById(R.id.audioButton);
                    enable(audioButton);
                    LinearLayout climateButton = (LinearLayout) rootView.findViewById(R.id.climateButton);
                    enable(climateButton);
                    ImageView swipe_right = (ImageView) rootView.findViewById(R.id.swipe_right);
                    swipe_right.setAlpha((float) .3);
                    ImageView swipe_left = (ImageView) rootView.findViewById(R.id.swipe_left);
                    swipe_left.setAlpha((float) .3);
                }

                Button resetSYNC = (Button) rootView.findViewById(R.id.resetSYNC);
                enable(resetSYNC);
            }
        });
        if (!MainActivity.retrievedAudioData || !MainActivity.retrievedClimateData) {
            ((MainActivity) BaseActivity.currentActivity).getData();
        }
    }

    public void disconnected() {
        MainActivity.connected = MainActivity.retrievedAudioData = MainActivity.retrievedClimateData = false;
        BaseActivity.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout audioButton = (LinearLayout) rootView.findViewById(R.id.audioButton);
                disable(audioButton);

                LinearLayout climateButton = (LinearLayout) rootView.findViewById(R.id.climateButton);
                disable(climateButton);

                Button resetSYNC = (Button) rootView.findViewById(R.id.resetSYNC);
                disable(resetSYNC);

                ImageView swipe_right = (ImageView) rootView.findViewById(R.id.swipe_right);
                swipe_right.setAlpha((float) 0);
                ImageView swipe_left = (ImageView) rootView.findViewById(R.id.swipe_left);
                swipe_left.setAlpha((float) 0);
            }
        });
    }

    private static void enable(View layout) {
        layout.setEnabled(true);
        if (layout instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) layout).getChildCount(); ++i) {
                View child = ((ViewGroup) layout).getChildAt(i);
                if (child instanceof ViewGroup) {
                    enable(child);
                } else {
                    child.setEnabled(true);
                    if (child instanceof ImageView) {
                        child.setAlpha(1);
                    }
                }
            }
        }
    }

    private static void disable(View layout) {
        layout.setEnabled(false);
        if (layout instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) layout).getChildCount(); ++i) {
                View child = ((ViewGroup) layout).getChildAt(i);
                if (child instanceof ViewGroup) {
                    disable(child);
                } else {
                    child.setEnabled(false);
                    if (child instanceof ImageView) {
                        child.setAlpha((float) .25);
                    }
                }
            }
        }
    }

    public void dataRetrieved() {
        BaseActivity.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout audioButton = (LinearLayout) rootView.findViewById(R.id.audioButton);
                enable(audioButton);
                LinearLayout climateButton = (LinearLayout) rootView.findViewById(R.id.climateButton);
                enable(climateButton);
                ImageView swipe_right = (ImageView) rootView.findViewById(R.id.swipe_right);
                swipe_right.setAlpha((float) .3);
                ImageView swipe_left = (ImageView) rootView.findViewById(R.id.swipe_left);
                swipe_left.setAlpha((float) .3);
            }
        });
    }

}
