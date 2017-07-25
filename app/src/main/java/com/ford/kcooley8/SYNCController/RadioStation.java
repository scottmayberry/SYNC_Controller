package com.ford.kcooley8.SYNCController;

import android.text.TextUtils;

import com.smartdevicelink.proxy.rc.enums.RadioBand;

public class RadioStation {
    public RadioBand band;
    public int freq = -1;
    public int subFreq = -1;

    public boolean stringToRadioStation(String input) {
        int count = input.length() - input.replace(".", "").length();
        if (count != 1) {
            return false;
        }
        int index = input.indexOf(".");
        String freqString = input.substring(0, index);
        String subFreqString = input.substring(index + 1);
        if (freqString.length() == 0 || subFreqString.length() == 0 ||
                !TextUtils.isDigitsOnly(freqString) || !TextUtils.isDigitsOnly(subFreqString)) {
            return false;
        }
        freq = Integer.parseInt(freqString);
        subFreq = Integer.parseInt(subFreqString);

        if (freq < 87 || freq > 108 || subFreq % 2 == 0 || subFreq > 9) {
            return false;
        }
        return true;
    }
}
