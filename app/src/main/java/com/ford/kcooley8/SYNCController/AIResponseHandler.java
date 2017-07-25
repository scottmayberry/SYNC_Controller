package com.ford.kcooley8.SYNCController;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.ford.kcooley8.applink.AppLinkService;
import com.smartdevicelink.proxy.rc.enums.RadioBand;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class AIResponseHandler {
    public static void handleResponse(String response) {
        boolean badCommand = false;
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject result = jsonResponse.getJSONObject("result");
            String action = result.getString("action");
            JSONObject parameters = result.getJSONObject("parameters");
            switch (action) {
                case "setFanSpeed":
                    if (!parameters.has("fanSpeed")) {
                        badCommand = true;
                        badCommand(result.getString("resolvedQuery"));
                        break;
                    }
                    AppLinkService.getInstance().setClimateFanSpeed(parameters.getInt("fanSpeed"));
                    break;
                case "setFanSpeedHiLo":
                    if (!parameters.has("fanSpeedHiLo")) {
                        badCommand(result.getString("resolvedQuery"));
                        badCommand = true;
                        break;
                    }
                    String hiLo = parameters.getString("fanSpeedHiLo");
                    if (hiLo.equals("high")) {
                        AppLinkService.getInstance().setClimateFanSpeed(7);
                    }
                    else if (hiLo.equals("low")) {
                        AppLinkService.getInstance().setClimateFanSpeed(1);
                    }
                    break;
                case "decreaseTemperature":
                    if (parameters.has("location")) {
                        if (parameters.getString("location").equals("passenger")) {
                            AppLinkService.getInstance().setClimatePassTemp(
                                    ClimateActivity.passTemp - 5);
                        }
                        else if (parameters.getString("location").equals("driver")) {
                            if (!ClimateActivity.dual) {
                                AppLinkService.getInstance().setClimateDual(true);
                            }
                            AppLinkService.getInstance().setClimateDriverTemp(
                                    ClimateActivity.driverTemp - 5);
                        }
                    }
                    else {
                        if (ClimateActivity.dual) {
                            AppLinkService.getInstance().setClimateDual(false);
                        }
                        AppLinkService.getInstance().setClimateDriverTemp(
                                ClimateActivity.driverTemp - 5);
                    }
                    break;
                case "increaseTemperature":
                    if (parameters.has("location")) {
                        if (parameters.getString("location").equals("passenger")) {
                            AppLinkService.getInstance().setClimatePassTemp(
                                    ClimateActivity.passTemp + 5);
                        }
                        else if (parameters.getString("location").equals("driver")) {
                            if (!ClimateActivity.dual) {
                                AppLinkService.getInstance().setClimateDual(true);
                            }
                            AppLinkService.getInstance().setClimateDriverTemp(
                                    ClimateActivity.driverTemp + 5);
                        }
                    }
                    else {
                        if (ClimateActivity.dual) {
                            AppLinkService.getInstance().setClimateDual(false);
                        }
                        AppLinkService.getInstance().setClimateDriverTemp(
                                ClimateActivity.driverTemp + 5);
                    }
                    break;
                case "setTemperature":
                    if (!parameters.has("temp")) {
                        badCommand = true;
                        badCommand(result.getString("resolvedQuery"));
                        break;
                    }
                    if (parameters.has("location")) {
                        if (parameters.getString("location").equals("passenger")) {
                            AppLinkService.getInstance().setClimatePassTemp(
                                    parameters.getInt("temp"));
                        }
                        else if (parameters.getString("location").equals("driver")) {
                            if (!ClimateActivity.dual) {
                                AppLinkService.getInstance().setClimateDual(true);
                            }
                            AppLinkService.getInstance().setClimateDriverTemp(
                                    parameters.getInt("temp"));
                        }
                    }
                    else {
                        if (ClimateActivity.dual) {
                            AppLinkService.getInstance().setClimateDual(false);
                        }
                        AppLinkService.getInstance().setClimateDriverTemp(
                                parameters.getInt("temp"));
                    }
                    break;
                case "toggleAC":
                    if (!parameters.has("onOff")) {
                        badCommand = true;
                        badCommand(result.getString("resolvedQuery"));
                        break;
                    }
                    AppLinkService.getInstance().setClimateAC(parameters.getString("onOff")
                            .equals("on"));
                    break;
                case "toggleRecirc":
                    if (!parameters.has("onOff")) {
                        badCommand = true;
                        badCommand(result.getString("resolvedQuery"));
                        break;
                    }
                    AppLinkService.getInstance().setClimateRecirc(parameters.getString("onOff")
                            .equals("on"));
                    break;
                case "directTune":
                    if (!parameters.has("frequency") || !parameters.has("subFrequency")) {
                        badCommand = true;
                        badCommand(result.getString("resolvedQuery"));
                        break;
                    }
                    AppLinkService.getInstance().setRadioFrequency(parameters.getInt("frequency"),
                            parameters.getInt("subFrequency"), RadioBand.FM); // TODO
                default:
                    badCommand = true;
                    badCommand(result.getString("resolvedQuery"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!badCommand) {
            //AppLinkService.getInstance().speakMessage(); // TODO
        }
    }

    private static void badCommand(String heard) {
        if (BaseActivity.currentActivity != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    BaseActivity.currentActivity);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    // TODO
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    // TODO
                }
            });
            builder.setMessage("We didn't recognize that command. Here's what we heard:\n\"" + heard
                    + "\"\nIs this what you said?");
            builder.setCancelable(false);

            BaseActivity.currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

    }
}
