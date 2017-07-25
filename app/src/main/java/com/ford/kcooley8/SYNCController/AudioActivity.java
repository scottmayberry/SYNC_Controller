package com.ford.kcooley8.SYNCController;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ford.kcooley8.applink.AppLinkService;
import com.smartdevicelink.proxy.rc.enums.RadioBand;

import java.util.Locale;

public class AudioActivity extends BaseActivity {
    public static AudioSource audioSource;
    public static int frequency = -1, subFrequency = -1;
    private static final int SOURCES_DISCONNECTED_REQUEST = 1;

    private static Dialog directTuneDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentActivity = this;
        setContentView(R.layout.audio);
        updateAudioLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        directTuneDialog = null;
        super.onDestroy();
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
        updateAudioLayout();
    }

    @Override
    public void onBackPressed() {
        returnToMenu();
    }

    public void updateAudioLayout() {
        final TextView frequencyText = (TextView) findViewById(R.id.frequency);
        final ImageView sourcesIcon = (ImageView) findViewById(R.id.sourcesIcon);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                frequencyText.setText(String.format(Locale.ENGLISH, "%d.%d", frequency,
                        subFrequency));
                sourcesIcon.setImageDrawable(getDrawable(audioSource.icon));
            }
        });
    }


    @Override
    public void disconnected() {
        if (directTuneDialog != null && directTuneDialog.isShowing()) {
            directTuneDialog.cancel();
        }
        MainActivity.connected = false;
        returnToMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SOURCES_DISCONNECTED_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra("disconnected", false)) {
                    disconnected();
                }
            }
        }
    }

    /**********************************************************
     Audio button presses
     ***********************************************************/

    public void sourcesPressed(View view) {
        currentActivity = null;
        Intent intent = new Intent(this, AudioSourcesActivity.class);
        startActivityForResult(intent, SOURCES_DISCONNECTED_REQUEST);
    }

    public void menuPressed(View view) {
        returnToMenu();
    }

    public void directTunePressed(View view) {
        if (directTuneDialog == null) {
            directTuneDialog = new Dialog(this);
            directTuneDialog.setContentView(R.layout.direct_tune_dialog);
            initializeDirectTuneDialog();
        }

        directTuneDialog.show();
    }

    public void changePresetsPressed(View view) {
        Toast.makeText(getApplicationContext(), "Functionality not yet implemented",
                Toast.LENGTH_SHORT).show();
    }

    public void presetPressed(View view) {
        Toast.makeText(getApplicationContext(), "Functionality not yet implemented",
                Toast.LENGTH_SHORT).show();
    }

    /**********************************************************
     End audio button presses
     ***********************************************************/

    public void returnToMenu() {
        currentActivity = null;
        finish();
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
    }

    private void initializeDirectTuneDialog() {
        directTuneDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        directTuneDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        Button enterButton = (Button) directTuneDialog.findViewById(R.id.enter);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directTuneEnterPressed();
            }
        });

        Button cancelButton = (Button) directTuneDialog.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText directTuneInput = (EditText) directTuneDialog.findViewById(
                        R.id.directTuneInput);
                directTuneDialog.cancel();
                directTuneInput.setText("");
            }
        });
    }

    private void directTuneEnterPressed() {
        EditText directTuneInput = (EditText) directTuneDialog.findViewById(
                R.id.directTuneInput);
        String directTuneString = directTuneInput.getText().toString();

        RadioStation station = new RadioStation();
        station.band = audioSource.band;

        if (audioSource.band == RadioBand.FM) {
            if (!station.stringToRadioStation(directTuneString)) {
                Toast.makeText(getApplicationContext(), "Input is not valid",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            AppLinkService.getInstance().setRadioFrequency(station.freq, station.subFreq,
                    station.band);
        }
        else {
            Toast.makeText(getApplicationContext(), "Functionality not yet implemented",
                Toast.LENGTH_SHORT).show();
        }

        directTuneDialog.dismiss();
        directTuneInput.setText("");
    }
}
