package com.ford.kcooley8.SYNCController;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.HashMap;

public class AudioSourcesActivity extends BaseActivity {
    public static AudioSource[] sources = null;
    public static HashMap<String, AudioSource> sourcesMap = null;
    ImageAdapter imageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;

        if (sources == null || sourcesMap == null) {
            initializeSources();
        }

        setContentView(R.layout.entertainment_sources);

        GridView gridView = (GridView) findViewById(R.id.sourcesGrid);
        imageAdapter = new ImageAdapter(this, sources);
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*if (sources[position].source == Source.FM) {
                    AudioSource oldSource = AudioActivity.audioSource;
                    AudioActivity.audioSource = sources[position];
                    for (int i = 0; i < sources.length; ++i) {
                        if (sources[i] == oldSource) {
                            LinearLayout oldSourceButton = (LinearLayout) parent.getChildAt(i).
                                    findViewById(R.id.gridLayout);
                            imageAdapter.deactivateButton(oldSourceButton);
                            break;
                        }
                    }
                    LinearLayout newSourceButton = (LinearLayout) view.findViewById(
                            R.id.gridLayout);
                    imageAdapter.activateButton(newSourceButton);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Functionality not yet implemented",
                            Toast.LENGTH_SHORT).show();
                }*/
                Toast.makeText(getApplicationContext(), "Functionality not yet implemented",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void initializeSources() {
        sources = new AudioSource[5];
        sourcesMap = new HashMap<>();

        sources[0] = new AudioSource("AM", R.drawable.am, Source.AM);
        sources[1] = new AudioSource("FM", R.drawable.fm, Source.FM);
        sources[2] = new AudioSource("SIRIUS", R.drawable.sirius, Source.XM);
        sources[3] = new AudioSource("CD", R.drawable.cd, Source.CD);
        sources[4] = new AudioSource("Bluetooth Stereo", R.drawable.bluetooth, Source.BLUETOOTH);

        sourcesMap.put("AM", sources[0]);
        sourcesMap.put("FM", sources[1]);
        sourcesMap.put("XM", sources[2]);
        // TODO handle other sources
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        returnToMain();
    }

    @Override
    public void onPause() {
        currentActivity = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        currentActivity = this;
    }

    public void backPressed(View view) {
        returnToMain();
    }

    public void returnToMain() {
        /*Intent returnIntent = new Intent();
        returnIntent.putExtra("disconnected", false);
        setResult(Activity.RESULT_OK, returnIntent);*/
        currentActivity = null;
        finish();
    }

    @Override
    public void disconnected() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("disconnected", true);
        setResult(Activity.RESULT_OK, returnIntent);
        /*currentActivity = null;
        finish();*/
        returnToMain();
    }
}
