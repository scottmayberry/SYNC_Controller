package com.ford.kcooley8.applink;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.ford.kcooley8.SYNCController.AIRequestSender;
import com.ford.kcooley8.SYNCController.AIResponseHandler;
import com.ford.kcooley8.SYNCController.ActivityType;
import com.ford.kcooley8.SYNCController.AudioActivity;
import com.ford.kcooley8.SYNCController.BaseActivity;
import com.ford.kcooley8.SYNCController.ClimateActivity;
import com.ford.kcooley8.SYNCController.MainActivity;
import com.ford.kcooley8.SYNCController.R;
import com.ford.kcooley8.SYNCController.AudioSourcesActivity;
import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.rc.datatypes.ClimateControlData;
import com.smartdevicelink.proxy.rc.datatypes.InteriorZone;
import com.smartdevicelink.proxy.rc.datatypes.ModuleData;
import com.smartdevicelink.proxy.rc.datatypes.ModuleDescription;
import com.smartdevicelink.proxy.rc.datatypes.RadioControlData;
import com.smartdevicelink.proxy.rc.enums.DefrostZone;
import com.smartdevicelink.proxy.rc.enums.ModuleType;
import com.smartdevicelink.proxy.rc.enums.RadioBand;
import com.smartdevicelink.proxy.rc.rpc.ButtonPressResponse;
import com.smartdevicelink.proxy.rc.rpc.GetInteriorVehicleData;
import com.smartdevicelink.proxy.rc.rpc.GetInteriorVehicleDataCapabilitiesResponse;
import com.smartdevicelink.proxy.rc.rpc.GetInteriorVehicleDataResponse;
import com.smartdevicelink.proxy.rc.rpc.OnInteriorVehicleData;
import com.smartdevicelink.proxy.rc.rpc.SetInteriorVehicleData;
import com.smartdevicelink.proxy.rc.rpc.SetInteriorVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.*;
import com.smartdevicelink.proxy.rpc.enums.*;

import java.io.IOException;
import java.io.InputStream;
import android.app.Service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

enum Zone {DRIVER, PASSENGER}

public class AppLinkService extends Service implements IProxyListenerALM {

	private static AppLinkService instance = null;
	private SdlProxyALM proxy = null;
	public int correlationID = 0;
    private int passTempCorrelationID;

    private static ArrayList<Byte> audioPassThru;

	public static AppLinkService getInstance() {
		return instance;
	}

    private static int voiceCommandButtonID = 0;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if( intent != null &&
				BluetoothAdapter.getDefaultAdapter() != null &&
				BluetoothAdapter.getDefaultAdapter().isEnabled() ) {
			startProxy();
		}

		return START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
        audioPassThru = new ArrayList<>();
	}

	@Override
	public void onDestroy() {
		removeSyncProxy();
		instance = null;
		super.onDestroy();
	}

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        removeSyncProxy();
    }

	public void removeSyncProxy() {
		if( proxy == null ) {
            return;
        }
        resetProxy();
		proxy = null;
	}

	public void startProxy() {
		if( proxy == null ) {
			try {
				proxy = new SdlProxyALM(this, this, getString( R.string.display_title ), false, getString( R.string.app_link_id ) );

			} catch( SdlException e ) {
				if( proxy == null ) {
					stopSelf();
				}
			}
		}
	}

	public SdlProxyALM getProxy() {
		return proxy;
	}

	@Override
	public void onDiagnosticMessageResponse(DiagnosticMessageResponse response) {

	}

	public void resetProxy() {
        if (proxy != null) {
            try {
                proxy.resetProxy();
            } catch (SdlException e) {
                if (proxy == null)
                    stopSelf();
            }
        } else {
            startProxy();
        }
    }

	@Override
	public void onOnHMIStatus(OnHMIStatus onHMIStatus) {
		switch( onHMIStatus.getSystemContext() ) {
			case SYSCTXT_MAIN:
			case SYSCTXT_VRSESSION:
			case SYSCTXT_MENU:
				break;
			default:
				return;
		}

		if( proxy == null )
			return;

		if( onHMIStatus.getHmiLevel().equals( HMILevel.HMI_FULL )) {
            if (BaseActivity.currentActivity instanceof MainActivity) {
                ((MainActivity) BaseActivity.currentActivity).connected();
            }

            try {
                proxy.setdisplaylayout("GRAPHIC_WITH_TILES", correlationID++);
                byte[] data = read(R.raw.voice_command);
                proxy.putfile("voice_command.png", FileType.GRAPHIC_PNG, true, data,
                        correlationID++);
                data = read(R.raw.sync_logo);
                proxy.putfile("sync_logo.png", FileType.GRAPHIC_PNG, true, data, correlationID++);

                Image syncLogo = new Image();
                syncLogo.setImageType(ImageType.DYNAMIC);
                syncLogo.setValue("sync_logo.png");
                proxy.show(null, null, null, null, syncLogo, null, null,
                        TextAlignment.CENTERED, correlationID++);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if (onHMIStatus.getHmiLevel().equals(HMILevel.HMI_NONE)) {
            if (BaseActivity.currentActivity != null) {
                BaseActivity.currentActivity.disconnected();
            }
            else {
                MainActivity.connected = false;
            }
        }
	}

    public void showVoiceCommandButton() {
        Vector<SoftButton> softButtons = new Vector<>();
        SoftButton softButton = new SoftButton();
        Image buttonImage = new Image();
        buttonImage.setImageType(ImageType.DYNAMIC);
        buttonImage.setValue("voice_command.png");
        softButton.setImage(buttonImage);
        softButton.setType(SoftButtonType.SBT_IMAGE);
        softButton.setSoftButtonID(voiceCommandButtonID); // TODO
        softButtons.add(softButton);

        Image syncLogo = new Image();
        syncLogo.setImageType(ImageType.DYNAMIC);
        syncLogo.setValue("sync_logo.png");

        try {
            proxy.show(null, null, null, null, syncLogo, softButtons, null,
                    TextAlignment.CENTERED, correlationID++);
        } catch (SdlException e) {
            e.printStackTrace();
        }
    }

    public void getClimateData() {
        GetInteriorVehicleData getData = new GetInteriorVehicleData();

        ModuleDescription moduleDescription = new ModuleDescription();
        moduleDescription.setModuleType(ModuleType.CLIMATE);
        moduleDescription.setZone(makeDriverZone());

        getData.setModuleDescription(moduleDescription);
        getData.setCorrelationID(correlationID++);
        try {
            proxy.sendRPCRequest(getData);
        }
        catch (SdlException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOnButtonPress(OnButtonPress notification) {
        int buttonID = (int) notification.getParameters("customButtonID");
        if (buttonID == voiceCommandButtonID) {
            getAudioPassThru();
        }
    }

    public void getAudioData() {
        GetInteriorVehicleData getData = new GetInteriorVehicleData();

        ModuleDescription moduleDescription = new ModuleDescription();
        moduleDescription.setModuleType(ModuleType.RADIO);
        moduleDescription.setZone(makeDriverZone());

        getData.setModuleDescription(moduleDescription);
        getData.setCorrelationID(correlationID++);

        try {
            proxy.sendRPCRequest(getData);
        } catch (SdlException e) {
            e.printStackTrace();
        }
    }

    public void getPassengerClimateData() {
        GetInteriorVehicleData getData = new GetInteriorVehicleData();

        ModuleDescription moduleDescription = new ModuleDescription();
        moduleDescription.setModuleType(ModuleType.CLIMATE);
        moduleDescription.setZone(makePassengerZone());

        getData.setModuleDescription(moduleDescription);
        getData.setCorrelationID(correlationID++);
        try {
            proxy.sendRPCRequest(getData);
        }
        catch (SdlException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void onGetInteriorVehicleDataResponse(GetInteriorVehicleDataResponse response) {
        Hashtable moduleData = (Hashtable) response.getParameters("moduleData");
		if (response.getSuccess() && moduleData != null) {
            String moduleType = (String) moduleData.get("moduleType");
            if (moduleType.equals("CLIMATE")) {
                updateClimateData(moduleData);
                /*if (MainActivity.instance.dual) {
                    passTempCorrelationID = correlationID;
                    getPassengerClimateData();
                }*/
                ((MainActivity) BaseActivity.currentActivity).climateDataRetrieved();
            }
            else if (moduleType.equals("RADIO")) {
                updateAudioData(moduleData);
                ((MainActivity) BaseActivity.currentActivity).audioDataRetrieved();
            }
		}
        else {
            ((MainActivity) BaseActivity.currentActivity).dataNotRetrieved();
        }
	}

    @Override
    public void onOnInteriorVehicleData(OnInteriorVehicleData notification) {
        Hashtable moduleData = (Hashtable) notification.getParameters("moduleData");
        if (moduleData != null) {
            String moduleType = (String) moduleData.get("moduleType");
            if (moduleType.equals("CLIMATE")) {
                updateClimateData((Hashtable) notification.getParameters("moduleData"));
                if (BaseActivity.currentActivity instanceof MainActivity) {
                    ((MainActivity) BaseActivity.currentActivity).updateClimateLayout();
                }
            }
            else if (moduleType.equals("RADIO")) {
                updateAudioData(moduleData);
                if (BaseActivity.currentActivity instanceof MainActivity) {
                    ((MainActivity) BaseActivity.currentActivity).updateAudioLayout();
                }
            }
        }
    }

    public void speakMessage(String message) {
        try {
            proxy.speak(message, correlationID++);
        } catch (SdlException e) {
            e.printStackTrace();
        }
    }

    public void updateClimateData(Hashtable moduleHashtable) {
        Hashtable climateHashtable = (Hashtable) moduleHashtable.get("climateControlData");
 //       MainActivity.power = true;
        if(climateHashtable.get("acEnable") != null)
            MainActivity.ac = (boolean) climateHashtable.get("acEnable");
        // TODO check passenger vs driver temp
        if(climateHashtable.get("desiredTemp") != null)
            MainActivity.driverTemp = (int) climateHashtable.get("desiredTemp");
        if(climateHashtable.get("desiredTemp") != null)
            MainActivity.passTemp = (int) climateHashtable.get("desiredTemp");
        if(climateHashtable.get("fanSpeed") != null)
            MainActivity.fanSpeed = (int) climateHashtable.get("fanSpeed") / 14;
        if(climateHashtable.get("dualModeEnable") != null)
            MainActivity.dual = (boolean) climateHashtable.get("dualModeEnable");
        if(climateHashtable.get("recirculateAirEnable") != null)
            MainActivity.recirc = (boolean) climateHashtable.get("recirculateAirEnable");
        if(climateHashtable.get("autoModeEnable") != null)
            MainActivity.auto = (boolean) climateHashtable.get("autoModeEnable");
        if(climateHashtable.get("defrostZone") != null){
            String power = (String) climateHashtable.get("defrostZone");
            if(power.compareTo("REAR") == 0){
                MainActivity.power = false;
            } else {
                MainActivity.power = true;
            }
        }
    }



    // TODO bug in SYNC
    // does not send "band" field until station is changed once after starting SYNC
    private void updateAudioData(Hashtable moduleHashtable) {
        Hashtable audioHashtable = (Hashtable) moduleHashtable.get("radioControlData");
        MainActivity.frequency = (int) audioHashtable.get("frequencyInteger");
        MainActivity.subFrequency = (int) audioHashtable.get("frequencyFraction");
        if (AudioSourcesActivity.sourcesMap == null) {
            AudioSourcesActivity.initializeSources();
        }
        MainActivity.audioSource = AudioSourcesActivity.sourcesMap.get(audioHashtable.get("band"));
    }

    public void getAudioPassThru() {
        audioPassThru.clear();
        PerformAudioPassThru request = new PerformAudioPassThru();
        request.setAudioPassThruDisplayText1("text1");
        request.setAudioPassThruDisplayText2("text2");
        request.setBitsPerSample(BitsPerSample._16_BIT);
        request.setAudioType(AudioType.PCM);
        request.setSamplingRate(SamplingRate._16KHZ);
        request.setMaxDuration(5000);
        request.setMuteAudio(true);
        request.setCorrelationID(correlationID++);
        try {
            proxy.sendRPCRequest(request);
        } catch (SdlException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse performAudioPassThruResponse) {
        scaleAudio();
        byte[] data = new byte[audioPassThru.size()];
        for (int i = 0; i < audioPassThru.size(); ++i) {
            data[i] = audioPassThru.get(i);
        }
        String response = AIRequestSender.sendRequest(data);
        if (response != null) {
            AIResponseHandler.handleResponse(response);
        }
    }

    @Override
    public void onOnAudioPassThru(OnAudioPassThru onAudioPassThru) {
        byte[] aptData = onAudioPassThru.getAPTData();
        for (int i = 0; i < aptData.length; ++i) {
            audioPassThru.add(aptData[i]);
        }
    }

    private void scaleAudio() {
        ArrayList<Short> shorts = new ArrayList<>(audioPassThru.size() / 2);
        ByteBuffer bytesIn = ByteBuffer.allocate(2);
        bytesIn.order(ByteOrder.LITTLE_ENDIAN);
        ByteBuffer bytesOut = ByteBuffer.allocate(2);
        bytesOut.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < audioPassThru.size(); i += 2) {
            bytesIn.clear();
            bytesIn.put(audioPassThru.get(i));
            bytesIn.put(audioPassThru.get(i + 1));
            bytesIn.position(0);
            shorts.add(bytesIn.getShort());
        }

        short max = 0;

        for (int i = 0; i < shorts.size(); ++i) {
            max = (short) Math.abs(Math.max(shorts.get(i), max));
        }

        double scale = Math.abs(Short.MAX_VALUE / max);

        for (int i = 0; i < shorts.size(); ++i) {
            shorts.set(i, (short) Math.round(shorts.get(i) * scale));
        }

        for (int i = 0; i < shorts.size(); ++i) {
            audioPassThru.set(i * 2, (byte) (shorts.get(i) & 0xFF));
            audioPassThru.set((i * 2) + 1, (byte) ((shorts.get(i) >> 8) & 0xFF));
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void setRadioFrequency(int freq, int subFreq, RadioBand band) {
        if (proxy != null) {
            SetInteriorVehicleData setData = new SetInteriorVehicleData();
            RadioControlData radio = new RadioControlData();
            radio.setInteriorDataType(ModuleType.RADIO);
            radio.setBand(band);
            radio.setFrequencyInteger(freq);
            radio.setFrequencyFraction(subFreq);
            ModuleData data = new ModuleData();
            data.setModuleZone(makeDriverZone());
            data.setModuleType(ModuleType.RADIO);
            data.setControlData(radio);

            setData.setModuleData(data);
            setData.setCorrelationID(correlationID++);
            try {
                proxy.sendRPCRequest(setData);
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }
    }

    public void setClimateAutoOn() {
        if (proxy != null) {
            SetInteriorVehicleData setData = new SetInteriorVehicleData();
            ClimateControlData climate = new ClimateControlData();
            climate.setInteriorDataType(ModuleType.CLIMATE);
            climate.setAutoModeEnabled(true);
            ModuleData data = new ModuleData();
            data.setModuleZone(makeDriverZone());
            data.setModuleType(ModuleType.CLIMATE);
            data.setControlData(climate);

            setData.setModuleData(data);
            setData.setCorrelationID(correlationID++);
            try {
                proxy.sendRPCRequest(setData);
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }
    }

    public void setClimatePower(boolean on) {
        // TODO
        if (proxy != null) {
            SetInteriorVehicleData setData = new SetInteriorVehicleData();
           ClimateControlData climate = new ClimateControlData();
            climate.setDefrostZone(DefrostZone.FRONT); // doesn't matter, this toggles
           climate.setInteriorDataType(ModuleType.CLIMATE);
           // climate.set ??????
            ModuleData data = new ModuleData();
            data.setModuleZone(makeDriverZone());
            data.setModuleType(ModuleType.CLIMATE);
            data.setControlData(climate);

            setData.setModuleData(data);
            setData.setCorrelationID(correlationID++);
            try {
                proxy.sendRPCRequest(setData);
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }
    }

    public void setClimateDual(boolean on) {
        if (proxy != null) {
            SetInteriorVehicleData setData = new SetInteriorVehicleData();
            ClimateControlData climate = new ClimateControlData();
            climate.setInteriorDataType(ModuleType.CLIMATE);
            climate.setDualModeEnabled(on);
            ModuleData data = new ModuleData();
            data.setModuleZone(makeDriverZone());
            data.setModuleType(ModuleType.CLIMATE);
            data.setControlData(climate);

            setData.setModuleData(data);
            setData.setCorrelationID(correlationID++);
            try {
                proxy.sendRPCRequest(setData);
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }
    }

    public void setClimateDriverTemp(double temp) {
        if(temp < 15.5) return;
        if(temp > 28) return;
        if (proxy != null) {
            SetInteriorVehicleData setData = new SetInteriorVehicleData();
            ClimateControlData climate = new ClimateControlData();
            climate.setInteriorDataType(ModuleType.CLIMATE);
            climate.setDesiredTemp((int) Math.round(temp));
            ModuleData data = new ModuleData();
            data.setModuleZone(makeDriverZone());
            data.setModuleType(ModuleType.CLIMATE);
            data.setControlData(climate);

            setData.setModuleData(data);
            setData.setCorrelationID(correlationID++);
            try {
                proxy.sendRPCRequest(setData);
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }
    }

    public void setClimateFanSpeed(int speed) {
        if (proxy != null) {
            SetInteriorVehicleData setData = new SetInteriorVehicleData();
            ClimateControlData climate = new ClimateControlData();
            climate.setInteriorDataType(ModuleType.CLIMATE);
            climate.setFanSpeed(speed * 14);
            ModuleData data = new ModuleData();
            data.setModuleZone(makeDriverZone());
            data.setModuleType(ModuleType.CLIMATE);
            data.setControlData(climate);

            setData.setModuleData(data);
            setData.setCorrelationID(correlationID++);
            try {
                proxy.sendRPCRequest(setData);
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }
    }

    public void setClimateUpperBlower(boolean on) {
        // TODO
        /*if (proxy != null) {
            SetInteriorVehicleData setData = new SetInteriorVehicleData();
            ClimateControlData climate = new ClimateControlData();
            climate.setInteriorDataType(ModuleType.CLIMATE);
            //climate.set
        }*/
    }

    public void setClimateLowerBlower(boolean on) {
        // TODO
        /*if (proxy != null) {
            SetInteriorVehicleData setData = new SetInteriorVehicleData();
            ClimateControlData climate = new ClimateControlData();
            climate.setInteriorDataType(ModuleType.CLIMATE);
            //climate.set
        }*/
    }

    public void setClimatePassTemp(double temp) {
        if(temp < 15.5) return;
        if(temp > 28) return;
        if (proxy != null) {
            SetInteriorVehicleData setData = new SetInteriorVehicleData();
            ClimateControlData climate = new ClimateControlData();
            climate.setInteriorDataType(ModuleType.CLIMATE);
            climate.setDesiredTemp((int) Math.round(temp));
            ModuleData data = new ModuleData();
            data.setModuleZone(makePassengerZone());
            data.setModuleType(ModuleType.CLIMATE);
            data.setControlData(climate);

            setData.setModuleData(data);
            setData.setCorrelationID(correlationID++);
            try {
                proxy.sendRPCRequest(setData);
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }
    }

    public void setClimateAC(boolean on) {
        if (proxy != null) {
            SetInteriorVehicleData setData = new SetInteriorVehicleData();
            ClimateControlData climate = new ClimateControlData();
            climate.setInteriorDataType(ModuleType.CLIMATE);
            climate.setACEnabled(on);
            ModuleData data = new ModuleData();
            data.setModuleZone(makeDriverZone());
            data.setModuleType(ModuleType.CLIMATE);
            data.setControlData(climate);

            setData.setModuleData(data);
            setData.setCorrelationID(correlationID++);
            try {
                proxy.sendRPCRequest(setData);
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }
    }

    public void setClimateRecirc(boolean on) {
        if (proxy != null) {
            SetInteriorVehicleData setData = new SetInteriorVehicleData();
            ClimateControlData climate = new ClimateControlData();
            climate.setInteriorDataType(ModuleType.CLIMATE);
            climate.setRecirculateEnabled(on);
            ModuleData data = new ModuleData();
            data.setModuleZone(makeDriverZone());
            data.setModuleType(ModuleType.CLIMATE);
            data.setControlData(climate);

            setData.setModuleData(data);
            setData.setCorrelationID(correlationID++);
            try {
                proxy.sendRPCRequest(setData);
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO
    public void setClimateDefrost(boolean on) {
        /*if (proxy != null) {
            SetInteriorVehicleData setData = new SetInteriorVehicleData();
            ClimateControlData climate = new ClimateControlData();
            climate.setInteriorDataType(ModuleType.CLIMATE);

            ModuleData data = new ModuleData();
            data.setModuleZone(makeDriverZone());
            data.setModuleType(ModuleType.CLIMATE);
            data.setControlData(climate);

            setData.setModuleData(data);
            setData.setCorrelationID(correlationID++);
            try {
                proxy.sendRPCRequest(setData);
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }*/
    }

	@Override
	public void onProxyClosed(String s, Exception e, SdlDisconnectedReason syncDisconnectedReason) {

	}

	@Override
	public void onServiceEnded(OnServiceEnded serviceEnded) {

	}

	@Override
	public void onServiceNACKed(OnServiceNACKed serviceNACKed) {

	}

	@Override
	public void onOnStreamRPC(OnStreamRPC notification) {

	}

	@Override
	public void onStreamRPCResponse(StreamRPCResponse response) {

	}

	@Override
	public void onError(String s, Exception e) {

	}

	@Override
	public void onGenericResponse(GenericResponse genericResponse) {

	}

	@Override
	public void onOnCommand(OnCommand onCommand) {
	}

	@Override
	public void onAddCommandResponse(AddCommandResponse addCommandResponse) {

	}

	@Override
	public void onAddSubMenuResponse(AddSubMenuResponse addSubMenuResponse) {

	}

	@Override
	public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse createInteractionChoiceSetResponse) {

	}

	@Override
	public void onAlertResponse(AlertResponse alertResponse) {

	}

	@Override
	public void onDeleteCommandResponse(DeleteCommandResponse deleteCommandResponse) {

	}

	@Override
	public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse deleteInteractionChoiceSetResponse) {

	}

	@Override
	public void onDeleteSubMenuResponse(DeleteSubMenuResponse deleteSubMenuResponse) {

	}

	@Override
	public void onPerformInteractionResponse(PerformInteractionResponse performInteractionResponse) {

	}

	@Override
	public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse resetGlobalPropertiesResponse) {

	}

	@Override
	public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse setGlobalPropertiesResponse) {

	}

	@Override
	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse setMediaClockTimerResponse) {

	}

	@Override
	public void onShowResponse(ShowResponse showResponse) {
	}

	@Override
	public void onSpeakResponse(SpeakResponse speakResponse) {

	}

	@Override
	public void onOnButtonEvent(OnButtonEvent onButtonEvent) {
	}

	@Override
	public void onSubscribeButtonResponse(SubscribeButtonResponse subscribeButtonResponse) {
	}

	@Override
	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse unsubscribeButtonResponse) {

	}

	@Override
	public void onOnPermissionsChange(OnPermissionsChange onPermissionsChange) {

	}

	@Override
	public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse subscribeVehicleDataResponse) {
        Log.e("onSubscribeVehicleDataR", "here");
	}

	@Override
	public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse unsubscribeVehicleDataResponse) {

	}

	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse getVehicleDataResponse) {
        Log.e("onGetVehicleDataRespons", "here");
	}

	@Override
	public void onReadDIDResponse(ReadDIDResponse readDIDResponse) {

	}

	@Override
	public void onGetDTCsResponse(GetDTCsResponse getDTCsResponse) {

	}

	@Override
	public void onOnLockScreenNotification(OnLockScreenStatus notification) {
	}

	@Override
	public void onButtonPressResponse(ButtonPressResponse response) {

	}

	@Override
	public void onGetInteriorVehicleDataCapabilitiesResponse(GetInteriorVehicleDataCapabilitiesResponse response) {

	}

	@Override
	public void onSetInteriorVehicleDataResponse(SetInteriorVehicleDataResponse response) {
	}

	@Override
	public void onDialNumberResponse(DialNumberResponse response) {

	}

	@Override
	public void onSendLocationResponse(SendLocationResponse response) {

	}

	@Override
	public void onShowConstantTbtResponse(ShowConstantTbtResponse response) {

	}

	@Override
	public void onAlertManeuverResponse(AlertManeuverResponse response) {

	}

	@Override
	public void onUpdateTurnListResponse(UpdateTurnListResponse response) {

	}

	@Override
	public void onServiceDataACK() {

	}

	/*@Override
    public void onTunerControlResponse(TunerControlResponse msg) {
        if (msg.getSuccess() != null) {
            Log.e("onTunerControlResponse", msg.getMessageType());
            if (!msg.getSuccess()) {
                MainActivity.instance.invalidFrequency();
            }
            else {
                MainActivity.instance.cancelDirectTunePressed(null);
            }
        }
    }

    @Override
    public void onTuneRadioNotification(OnTuneRadio notification) {
        MainActivity.frequency = notification.getFrequency();
        MainActivity.subFrequency = notification.getSubFrequency();
        MainActivity.instance.updateFrequency();
    }

    @Override
    public void onMyFordMemoryNotification(OnMyFordMemory msg) {

    }

    @Override
    public void onMyFordMemoryResponse(MyFordMemoryResponse msg) {

    }

    @Override
    public void onClimateControlResponse(ClimateControlResponse msg) {
        Log.e("ClimateControlResponse", "here");
    }

    @Override
    public void onClimateNotification(OnClimateInfo msg) {
        if (msg.getPower() != null) {
            MainActivity.power = (msg.getPower() == 1);
        }
        if (msg.getRecirc() != null) {
            MainActivity.recirc = (msg.getRecirc() == 1);
        }
        if (msg.getBlowerSpeed() != null) {
            MainActivity.blowerSpeed = msg.getBlowerSpeed();
        }
    } */

    @Override
	public void onOnVehicleData(OnVehicleData onVehicleData) {

	}

	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse endAudioPassThruResponse) {
        Log.e("onEndAudioPassThruRespo", "here");
	}

	@Override
	public void onPutFileResponse(PutFileResponse putFileResponse) {
	}

	@Override
	public void onDeleteFileResponse(DeleteFileResponse deleteFileResponse) {

	}

	@Override
	public void onListFilesResponse(ListFilesResponse listFilesResponse) {
    }

	@Override
	public void onSetAppIconResponse(SetAppIconResponse setAppIconResponse) {

	}

	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse scrollableMessageResponse) {

	}

	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse changeRegistrationResponse) {

	}

	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse setDisplayLayoutResponse) {

	}

	@Override
	public void onOnLanguageChange(OnLanguageChange onLanguageChange) {

	}

	@Override
	public void onOnHashChange(OnHashChange onHashChange) {

	}

	@Override
	public void onSliderResponse(SliderResponse sliderResponse) {

	}

	@Override
	public void onOnDriverDistraction(OnDriverDistraction onDriverDistraction) {

	}

	@Override
	public void onOnTBTClientState(OnTBTClientState onTBTClientState) {

	}

	@Override
	public void onOnSystemRequest(OnSystemRequest onSystemRequest) {

	}

	@Override
	public void onSystemRequestResponse(SystemRequestResponse systemRequestResponse) {

	}

	@Override
	public void onOnKeyboardInput(OnKeyboardInput onKeyboardInput) {

	}

	@Override
	public void onOnTouchEvent(OnTouchEvent onTouchEvent) {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

    public byte[] read(int id) throws IOException {

        Resources res = getResources();
        InputStream ios=res.openRawResource(id);
        int count = 0;
        while (ios.read() != -1) {
            ++count;
        }

        ios.reset();
        byte []buffer = new byte[count];

        try {
            if ( ios.read(buffer) == -1 ) {
                throw new IOException("EOF reached while trying to read the whole file");
            }
        } finally {
            try {
                if ( ios != null )
                    ios.close();
            } catch ( IOException e) {
                Log.e("read", e.getMessage());
            }
        }

        return buffer;
    }

	/*public void tuneRadio(double frequency) {
        TunerControl tuner = new TunerControl();
        if (((MainActivity.source == MainActivity.AudioSource.AM || MainActivity.source == MainActivity.AudioSource.SIRIUS) &&
                Double.toString(frequency).contains(".")) || (Double.toString(frequency).contains(".") &&
                Double.toString(frequency).length() - Double.toString(frequency).indexOf(".") > 2) ||
                (MainActivity.source == MainActivity.AudioSource.FM && !Double.toString(frequency).contains("."))) {
            MainActivity.instance.invalidFrequency(); // Bad input
        }
        else {
            tuner.setCorrelationID(correlationID++);
            if (MainActivity.source == MainActivity.AudioSource.FM) {
                tuner.setTunerFrequency((int) (frequency + 0.01));
                tuner.setSubFreqency((int) ((frequency - (int) (frequency) + 0.01) * 10));
            }
            else {
                tuner.setTunerFrequency((int) frequency);
            }
        }
        if (proxy != null) {
            try {
                proxy.sendRPCRequest(tuner);
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }
    }*/

    public InteriorZone makeDriverZone() {
        InteriorZone zone = new InteriorZone();
        zone.setColumn(0);
        zone.setRow(0);
        zone.setLevel(0);
        zone.setLevelSpan(0);
        zone.setColumnSpan(0);
        zone.setRowSpan(0);
        return zone;
    }

    public InteriorZone makePassengerZone() {
        InteriorZone zone = new InteriorZone();
        zone.setColumn(1);
        zone.setRow(0);
        zone.setLevel(0);
        zone.setLevelSpan(0);
        zone.setColumnSpan(0);
        zone.setRowSpan(0);
        return zone;
    }
}