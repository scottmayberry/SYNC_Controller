package com.ford.kcooley8.SYNCController;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ford.kcooley8.applink.AppLinkService;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.rc.datatypes.ModuleData;
import com.smartdevicelink.proxy.rc.enums.RadioBand;

public class MainActivity extends BaseActivity {

    public static int aqi_now = 0;
    public static float aqi_avg = 0.0f;
    public static float[] aqi_bar = {0.0f, 0.0f, 0.0f};


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private static Fragment[] fragments;

    public static boolean connected = false, retrievedClimateData = false,
            retrievedAudioData = false;
    private static final int NUM_PAGES = 3;
    private SYNCViewPager pager;
    private PagerAdapter pagerAdapter;
    private AlertDialog dialog;
    Handler mHandler = new Handler();

    public static AudioSource audioSource;
    public static int frequency = -1, subFrequency = -1;
    private static final int SOURCES_DISCONNECTED_REQUEST = 1;
    private static Dialog directTuneDialog = null;

    public static boolean power, auto, upperBlower,
            lowerBlower, dual, defrost, maxDefrost, rearDefrost,
            maxAC, ac, recirc;
    public static double driverTemp, passTemp;
    public static int fanSpeed;

    public static Dialog defrostMenuDialog = null, acMenuDialog = null;

    public static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothGatt mGatt;     // for BLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentActivity = this;
        super.onCreate(savedInstanceState);

        fragments = new Fragment[NUM_PAGES];
        fragments[0] = new AudioFragment();
        fragments[1] = new MenuFragment();
        fragments[2] = new ClimateFragment();

        startSyncProxyService();
        setContentView(R.layout.activity_main);

        pager = (SYNCViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(1);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.e("BLUETOOTH", "This device does not support BlueTooth");
        }

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(android.os.Build.VERSION.SDK_INT >= 23) {
            getThePermission();
        }

        DeviceScanActivity BLEScanner = new DeviceScanActivity(this);
        BLEScanner.scanLeDevice(true);
    }

    @TargetApi(23)
    void getThePermission(){
        requestPermissions(new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        }, 1);
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
        currentActivity = null;
        super.onDestroy();
    }

    public void startSyncProxyService() {
        boolean isPaired = false;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter != null) {
            if (btAdapter.isEnabled() && btAdapter.getBondedDevices() != null && !btAdapter.getBondedDevices().isEmpty()) {
                for (BluetoothDevice device : btAdapter.getBondedDevices()) {
                    if (device.getName() != null && device.getName().contains(getString(R.string.device_name))) {
                        isPaired = true;
                        break;
                    }
                }
            }
        }

        if (isPaired) {
            if (AppLinkService.getInstance() == null) {
                startService(new Intent(this, AppLinkService.class));
            }
            else {
                SdlProxyALM proxyInstance = AppLinkService.getInstance().getProxy();
                if (proxyInstance == null) {
                    AppLinkService.getInstance().startProxy();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 1) {
            if (AppLinkService.getInstance() != null) {
                AppLinkService.getInstance().onDestroy();
            }
            if (connected) {
                disconnected();
            }
            super.onBackPressed();
        }
        else {
            pager.setCurrentItem(1);
        }
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
        if (AppLinkService.getInstance() == null) {
            startSyncProxyService();
        }
        if (connected && pager.getCurrentItem() == 1) {
            connected();
        }
        else if (!connected) {
            disconnected();
        }
        else if (pager.getCurrentItem() == 0 && ((AudioFragment) fragments[0]).isInitialized()) {
            ((AudioFragment) fragments[0]).updateAudioLayout();
        }
        else if (pager.getCurrentItem() == 2 && ((ClimateFragment) fragments[2]).isInitialized()) {
            ((ClimateFragment) fragments[2]).updateClimateLayout();
        }
    }

    public void resetSYNC(View view) {
        if (AppLinkService.getInstance() != null) {
            AppLinkService.getInstance().resetProxy();
        }
        disconnected();
    }

    public void audioButtonPressed(View view) {
        pager.setCurrentItem(0);
    }

    public void climateButtonPressed(View view) {
        pager.setCurrentItem(2);
    }

    public void climateDataRetrieved() {
        retrievedClimateData = true;
        if (retrievedAudioData) {
            dataRetrieved();
        }
    }

    public void audioDataRetrieved() {
        retrievedAudioData = true;
        if (retrievedClimateData) {
            dataRetrieved();
        }
    }

    private void dataRetrieved() {
        dialog.dismiss();
        AppLinkService.getInstance().showVoiceCommandButton();
        if (((MenuFragment) fragments[1]).isInitialized()) {
            ((MenuFragment) fragments[1]).dataRetrieved();
        }
        if (((AudioFragment) fragments[0]).isInitialized()) {
            ((AudioFragment) fragments[0]).updateAudioLayout();
        }
        if (((ClimateFragment) fragments[2]).isInitialized()) {
            ((ClimateFragment) fragments[2]).updateClimateLayout();
        }
    }

    public void dataNotRetrieved() {
        if (dialog.isShowing()) {
            dialog.cancel();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve data",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void connected() {
        connected = true;
        if (((MenuFragment) fragments[1]).isInitialized()) {
            ((MenuFragment) fragments[1]).connected();
        }
        pager.setEnabled(true);
    }

    public void disconnected() {
        connected = retrievedAudioData = retrievedClimateData = false;
        if (directTuneDialog != null && directTuneDialog.isShowing()) {
            directTuneDialog.cancel();
        }
        if (acMenuDialog != null && acMenuDialog.isShowing()) {
            acMenuDialog.cancel();
        }
        if (defrostMenuDialog != null && defrostMenuDialog.isShowing()) {
            defrostMenuDialog.cancel();
        }
        if (pager.getCurrentItem() != 1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pager.setCurrentItem(1);
                }
            });
        }
        if (((MenuFragment) fragments[1]).isInitialized()) {
            ((MenuFragment) fragments[1]).disconnected();
        }
        pager.setEnabled(false);
    }

    public void getData() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Retrieving Data From SYNC...");
        builder.setCancelable(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = builder.create();
                dialog.show();
            }
        });
        AppLinkService.getInstance().getAudioData();
        AppLinkService.getInstance().getClimateData();
    }

    public void updateAudioLayout() {
        if (((AudioFragment) fragments[0]).isInitialized()) {
            ((AudioFragment) fragments[0]).updateAudioLayout();
        }
    }

    public void updateClimateLayout() {
        if (((ClimateFragment) fragments[2]).isInitialized()) {
            ((ClimateFragment) fragments[2]).updateClimateLayout();
        }
  //      updateACMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        currentActivity = this;
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

    /**********************************************************
     End audio button presses
     ***********************************************************/

    /*******************************************************
    Climate button presses
    ********************************************************/

    public void acMenuPressed(View view) {
        if (acMenuDialog == null) {
            acMenuDialog = new Dialog(this);
            acMenuDialog.setContentView(R.layout.ac_menu_dialog);
            initializeACMenu();
        }

        acMenuDialog.show();
    }

    public void defrostMenuButtonPressed(View view) {
        if (defrostMenuDialog == null) {
            defrostMenuDialog = new Dialog(this);
            defrostMenuDialog.setContentView(R.layout.defrost_menu_dialog);
            initializeDefrostMenu();
        }
        defrostMenuDialog.show();
    }

    public void autoPressed(View view) {
        if (AppLinkService.getInstance() != null && !auto) {
            AppLinkService.getInstance().setClimateAutoOn();
        }
    }

    public void powerPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            AppLinkService.getInstance().setClimatePower(!power);
        }
    }

    public void dualPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            AppLinkService.getInstance().setClimateDual(!dual);
        }
    }

    public void driverTempUpPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            AppLinkService.getInstance().setClimateDriverTemp(driverTemp + .5);
        }
    }

    public void driverTempDownPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            AppLinkService.getInstance().setClimateDriverTemp(driverTemp - 1);
        }
    }

    public void upperBlowerPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            // TODO
            AppLinkService.getInstance().setClimateUpperBlower(!upperBlower);
            Toast.makeText(getApplicationContext(), "Functionality not yet implemented",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void lowerBlowerPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            // TODO
            AppLinkService.getInstance().setClimateLowerBlower(!lowerBlower);
            Toast.makeText(getApplicationContext(), "Functionality not yet implemented",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void fanUpPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            AppLinkService.getInstance().setClimateFanSpeed(fanSpeed + 1);
        }
    }

    public void fanDownPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            AppLinkService.getInstance().setClimateFanSpeed(fanSpeed - 1);
        }
    }

    public void passTempUpPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            // TODO
            AppLinkService.getInstance().setClimatePassTemp(passTemp + .5);
        }
    }

    public void passTempDownPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            // TODO
            AppLinkService.getInstance().setClimatePassTemp(passTemp - 1);
        }
    }

    public void maxACPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            // TODO
            Toast.makeText(getApplicationContext(), "Functionality not yet implemented",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void acPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            AppLinkService.getInstance().setClimateAC(!ac);
        }
    }

    public void recircPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            AppLinkService.getInstance().setClimateRecirc(!recirc);
            PopupDialog popup = new PopupDialog();
            if(!recirc == false && !PopupDialog.visible ){

                popup.type = popup.FILTERED_AIR;
                popup.show(getFragmentManager(), "Popup");

            } else if(!PopupDialog.visible) {
                popup.type = popup.ULTRA_FILTERED;
                popup.show(getFragmentManager(), "Popup");
            }
        }
    }

    public void maxDefrostPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            // TODO
            Toast.makeText(getApplicationContext(), "Functionality not yet implemented",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void defrostPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            AppLinkService.getInstance().setClimateDefrost(!defrost);
            Toast.makeText(getApplicationContext(), "Functionality not yet implemented",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void rearDefrostPressed(View view) {
        if (AppLinkService.getInstance() != null) {
            // TODO
            Toast.makeText(getApplicationContext(), "Functionality not yet implemented",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void viewAirQuality(View view) {
        AirqualityDialog dialog = new AirqualityDialog();
        dialog.show(getFragmentManager(), "AirQuality");


    }

    public void doRefresh(final View view) {
        if (AppLinkService.getInstance() != null) {
            mHandler.removeCallbacksAndMessages(null); // clear all
            AppLinkService.getInstance().setClimateRecirc(false);
            ((Button) view).setText("Refreshing");

            PopupDialog popup = new PopupDialog();
            if(!PopupDialog.visible ) {
                popup.type = popup.REFRESH;
                popup.show(getFragmentManager(), "Popup");
            }

            mHandler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    AppLinkService.getInstance().setClimateFanSpeed(7);
                }
            },500);

            mHandler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    AppLinkService.getInstance().setClimateFanSpeed(0);
                    PopupDialog popup = new PopupDialog();
                    if(!PopupDialog.visible ) {
                        popup.type = popup.REFRESH_COMPLETED;
                        popup.show(getFragmentManager(), "Popup");
                    } else{
                        PopupDialog prev = (PopupDialog) getFragmentManager().findFragmentByTag("Popup");
                        prev.dismiss();
                        popup.type = popup.REFRESH_COMPLETED;
                        popup.show(getFragmentManager(), "Popup");
                    }
                    ((Button) view).setText("Refresh");
                }
            },30000);
        }
    }

    /****************************************************
    End climate button presses
     *****************************************************/

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

    public void initializeDefrostMenu() {
        defrostMenuDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        Button closeDefrostMenu = (Button) defrostMenuDialog.findViewById(R.id.closeDefrostMenu);
        closeDefrostMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defrostMenuDialog.dismiss();
            }
        });
        ImageButton maxDefrostButton = (ImageButton) defrostMenuDialog.findViewById(R.id.maxDefrost);
        maxDefrostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maxDefrostPressed(view);
            }
        });

        ImageButton defrostButton = (ImageButton) defrostMenuDialog.findViewById(R.id.defrost);
        defrostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defrostPressed(view);
            }
        });

        ImageButton rearDefrostButton = (ImageButton) defrostMenuDialog.findViewById(R.id.rearDefrost);
        rearDefrostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rearDefrostPressed(view);
            }
        });
    }

    public void initializeACMenu() {
        acMenuDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        Button closeACMenu = (Button) acMenuDialog.findViewById(R.id.closeACMenu);
        closeACMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acMenuDialog.dismiss();
            }
        });

        Button maxACButton = (Button) acMenuDialog.findViewById(R.id.maxAC);
        maxACButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maxACPressed(view);
            }
        });

        Button acButton = (Button) acMenuDialog.findViewById(R.id.ac);
        acButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acPressed(view);
            }
        });

        ImageButton recircButton = (ImageButton) acMenuDialog.findViewById(R.id.recircButton);
        recircButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recircPressed(view);
            }
        });

        updateACMenu();
    }

    public void updateACMenu() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (acMenuDialog != null) {
                    Button acButton = (Button) MainActivity.acMenuDialog.findViewById(R.id.ac);
                    if (MainActivity.ac) {
                        activateButton(acButton);

                    }
                    else {
                        deactivateButton(acButton);
                    }
                    ImageButton recircButton = (ImageButton) MainActivity.acMenuDialog.findViewById(R.id.recirc);
                    if (recirc) {
                        activateButton(recircButton);
                    }
                    else {
                        deactivateButton(recircButton);
                    }
                }
            }
        });
    }

    public void activateButton(View button) {
        button.setBackground(getDrawable(R.drawable.button_activated));
    }

    public void deactivateButton(View button) {
        button.setBackground(getDrawable(R.drawable.button));
    }
}

