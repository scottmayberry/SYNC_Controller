package com.ford.kcooley8.SYNCController;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.smartdevicelink.proxy.rc.datatypes.InteriorZone;

import java.util.Random;

/**
 * Created by omakke on 6/8/2016.
 */
public class SharedData {

    public static int aqi_now = 0;
    public static float aqi_avg = 0.0f;
    public static final int maxSamples = 300; // receive maximum of 300 samples. At 10 Hz, this is 30 s
    public static int[] values = new int[maxSamples];
    public static int[] aqi_bar = {0, 0, 0, 0, 0, 0}; // Values of aqi_bar
    public static int recvSamples = 0;        // total recevied samples % max samples;
    public static int avgFactor = 0;           // min(recvSamples, 300)
    public static AirqualityDialog output = null;
    static int testval =299;

    private static synchronized int scaleLinear(int Ya, int Yb, int Xa, int Xb, int value){
        float answer;
        answer = ((float) Ya - (float) Yb)/((float) Xa - (float) Xb) * ((float) value - (float) Xb) + (float) Yb;
        Log.e("LinearScale", "In: " + Integer.toString(value) + "  Out: " + Float.toString(answer));
        return (int) answer;
    }

    public static synchronized void setCabinAQI(int AQI) {
        int Ya, Yb, Xa, Xb;

//        Random rnd = new Random();
//        rnd.setSeed(System.currentTimeMillis());
//        aqi_now = rnd.nextInt(115);
         testval= testval - 20;
        if(testval <=0 ){
            testval = 400;
        }
  //      AQI = testval;
//        if(AQI <= 35){
//            Ya = 50; Yb = 0; Xa = 35; Xb = 0;
//        } else if(AQI <= 75){
//            Ya = 100; Yb = 50; Xa = 75; Xb = 35;
//        } else if(AQI <= 115){
//            Ya = 150; Yb = 100; Xa = 115; Xb = 75;
//        } else if(AQI <= 150){
//            Ya = 200; Yb = 150; Xa = 150; Xb = 115;
//        } else if(AQI <= 250){
//            Ya = 300; Yb = 200; Xa = 250; Xb = 150;
//        } else if(AQI <= 350){
//            Ya = 400; Yb = 300; Xa = 350; Xb = 250;
//        } else{
//            Ya = 500; Yb = 400; Xa = 500; Xb = 350;
//        }
//        aqi_now = scaleLinear(Ya, Yb, Xa, Xb, AQI);
 //       aqi_now = testval;
        aqi_now = AQI *2;

        /*if aqinow if larger than what it should be, trigger map event

         */


 //       aqi_now = testval;
        // Implement recvSampels = received samples % max samples -1
        if(recvSamples < maxSamples - 1) {
            recvSamples++;
        } else {
            recvSamples = 0;
        }

        // Implement avgFactor = min(total received samples, 300)
        if(avgFactor < maxSamples) avgFactor++;    // this will reach maximum of 300.
        values[recvSamples-1] = aqi_now;

        // Calculate average. Also accumulate each region
        float aqi = 0;
        for(int ii = 0; ii < aqi_bar.length ; ii++){
            aqi_bar[ii] = 0;
        }

        for(int ii = 0; ii < avgFactor; ii++){
            aqi += (float) values[ii];
  //          aqi++;
            if(values[ii] <= 35){
             //   aqi_bar[0] += aqi;
                aqi_bar[0]++;
            } else if(values[ii] <= 75){
               // aqi_bar[1] += aqi;
                aqi_bar[1]++;
            } else if(values[ii] <= 116 ){
               // aqi_bar[2] += aqi;
                aqi_bar[2]++;
            }
            else if(values[ii] <= 151 ){
               // aqi_bar[3] += aqi;
                aqi_bar[3]++;
            } else if(values[ii] <= 251 ) {
               // aqi_bar[4] += aqi;
                aqi_bar[4]++;

            } else{
               // aqi_bar[5] += aqi;
                aqi_bar[5]++;
            }
        }
        aqi_avg = aqi/avgFactor;

        int aqi_sum = 0;
        for(int ii = 0; ii < aqi_bar.length; ii++){
            aqi_sum += aqi_bar[ii];
        }
        float aqi1 = ((float) aqi_bar[0])/aqi_sum;
        float aqi2 = ((float) aqi_bar[1])/aqi_sum;
        float aqi3 = ((float) aqi_bar[2])/aqi_sum;
        float aqi4 = ((float) aqi_bar[3])/aqi_sum;
        float aqi5 = ((float) aqi_bar[4])/aqi_sum;
        float aqi6 = ((float) aqi_bar[5])/aqi_sum;

        aqi_bar[0] = (int) (aqi1*100f);
        aqi_bar[1] = (int) (aqi2*100f);
        aqi_bar[2] = (int) (aqi3*100f);
        aqi_bar[3] = (int) (aqi4*100f);
        aqi_bar[4] = (int) (aqi5*100f);
        aqi_bar[5] = (int) (aqi6*100f);

   //     aqi_bar[0] = 100 - aqi_bar[1] - aqi_bar[2] - aqi_bar[3] - aqi_bar[4] - aqi_bar[5];
        int totalsum = aqi_bar[0] + aqi_bar[1] + aqi_bar[2] + aqi_bar[3] + aqi_bar[4] + aqi_bar[5];
        int factor = 100 - totalsum;
        int maxnum = aqi_bar[0];
        int maxindex = 0;

        for(int ii = 0; ii < aqi_bar.length; ii++){
           if(aqi_bar[ii] > maxnum) {
               maxnum = aqi_bar[ii];
               maxindex = ii;
           }
        }
        aqi_bar[maxindex] += factor;

        if(output != null){
            output.updateView(aqi_now, aqi_avg, aqi_bar);
        }
    }

    public static synchronized Integer getCabinAQI() {
        return aqi_now;
    }

    public static synchronized void clearData() {
        aqi_now = 0;
        aqi_avg = 0.0f;
        values = new int[maxSamples];
        for(int ii = 0; ii < aqi_bar.length; ii++){
            aqi_bar[ii] = 0;
        }
        recvSamples = 0;        // total recevied samples % max samples;
        avgFactor = 0;           // min(recvSamples, 300)
        testval = 0;
        for(int ii = 0; ii < values.length; ii++){
            values[ii] = 0;
        }
    }
}

