package com.ford.kcooley8.SYNCController;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class AudioFragment extends Fragment {
    private ViewGroup rootView = null;
    private boolean initialized = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.audio, container, false);
        initialized = true;
        if (MainActivity.retrievedAudioData) {
            updateAudioLayout();
        }
        return rootView;
    }

    public void updateAudioLayout() {
        final TextView frequencyText = (TextView) rootView.findViewById(R.id.frequency);
        final ImageView sourcesIcon = (ImageView) rootView.findViewById(R.id.sourcesIcon);
        BaseActivity.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                frequencyText.setText(String.format(Locale.ENGLISH, "%d.%d", MainActivity.frequency,
                        MainActivity.subFrequency));
//                sourcesIcon.setImageDrawable(BaseActivity.currentActivity.getDrawable(
//                        MainActivity.audioSource.icon));
            }
        });
    }

    public boolean isInitialized() {
        return initialized;
    }

}
