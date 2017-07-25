package com.ford.kcooley8.SYNCController;

import com.smartdevicelink.proxy.rc.enums.RadioBand;

enum Source {AM, FM, XM, CD, BLUETOOTH}

public class AudioSource {
    int icon;
    String name;
    Source source;
    RadioBand band;

    public AudioSource(String nameIn, int iconIn, Source sourceIn) {
        name = nameIn;
        icon = iconIn;
        source = sourceIn;
        switch (source) {
            case AM:
                band = RadioBand.AM;
                break;
            case FM:
                band = RadioBand.FM;
                break;
            case XM:
                band = RadioBand.XM;
                break;
            default:
                band = null;
                break;
        }
    }
}
