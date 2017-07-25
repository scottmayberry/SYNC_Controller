package com.ford.kcooley8.SYNCController;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ford.kcooley8.applink.AppLinkService;

import java.util.Locale;
import java.util.Vector;

public class ClimateActivity extends BaseActivity {
    public static boolean power, auto, upperBlower,
            lowerBlower, dual, defrost, maxDefrost, rearDefrost,
            maxAC, ac, recirc;
    public static double driverTemp, passTemp;
    public static int fanSpeed;

    public static Dialog defrostMenuDialog = null, acMenuDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;

        setContentView(R.layout.climate);
        if (savedInstanceState == null) {
            initializeClimateLayout();
        }
    }

    @Override
    public void onDestroy() {
        defrostMenuDialog = null;
        acMenuDialog = null;
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
        updateClimateLayout();
    }

    @Override
    public void onBackPressed() {
        returnToMenu();
    }

    public void initializeClimateLayout() {
        final LinearLayout layout = (LinearLayout) findViewById(R.id.climateLayout);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                updateClimateLayout();
            }
        });
    }

    public void updateClimateLayout() {
        final ImageButton powerButton = (ImageButton) findViewById(R.id.power);
        final TextView driverTempDisplay = (TextView) findViewById(R.id.driverTempDisplay);
        final TextView passTempDisplay = (TextView) findViewById(R.id.passTempDisplay);
        final Button dualButton = (Button) findViewById(R.id.dual);
        final Button autoButton = (Button) findViewById(R.id.auto);
        final ImageButton recircButton = (ImageButton) findViewById(R.id.recircButton);
  //      final LinearLayout acMenuButton = (LinearLayout) findViewById(R.id.acMenu);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateFanSpeedLayout();

                if (power) {
                    activateButton(powerButton);
                }
                else {
                    deactivateButton(powerButton);
                }

                if (ac) {
                    if (acMenuDialog != null) {
                        Button acButton = (Button) acMenuDialog.findViewById(R.id.ac);
                        activateButton(acButton);
                    }
           //         activateButton(acMenuButton);
                }
                else if (acMenuDialog != null) {
                    Button acButton = (Button) acMenuDialog.findViewById(R.id.ac);
                    deactivateButton(acButton);
         //           deactivateButton(acMenuButton);
                }
                else {
         //           deactivateButton(acMenuButton);
                }

                driverTempDisplay.setText(String.format(Locale.US, "%1$.1f", driverTemp) + "°");
                passTempDisplay.setText(String.format(Locale.US, "%1$.1f", passTemp) + "°");

                if (dual) {
                    activateButton(dualButton);
                }
                else {
                    deactivateButton(dualButton);
                }

                if (recirc) {
                    activateButton(recircButton);
                } else {
                    deactivateButton(recircButton);
                }


                if (auto) {
                    activateButton(autoButton);
                }
                else {
                    deactivateButton(autoButton);
                }
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

        ImageButton recircButton = (ImageButton) acMenuDialog.findViewById(R.id.recirc);
        recircButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recircPressed(view);
            }
        });

        updateClimateLayout();
    }

    public void activateButton(View button) {
        button.setBackground(getResources().getDrawable(R.drawable.button_activated));
    }

    public void deactivateButton(View button) {
        button.setBackground(getResources().getDrawable(R.drawable.button));
    }

    public void updateFanSpeedLayout() {
        ImageView fanDisplay = (ImageView) findViewById(R.id.fanDisplay);
        Drawable newImage = null;
        switch (fanSpeed) {
            case 0:
                newImage = getDrawable(R.drawable.fan_display0);
                break;
            case 1:
                newImage = getDrawable(R.drawable.fan_display1);
                break;
            case 2:
                newImage = getDrawable(R.drawable.fan_display2);
                break;
            case 3:
                newImage = getDrawable(R.drawable.fan_display3);
                break;
            case 4:
                newImage = getDrawable(R.drawable.fan_display4);
                break;
            case 5:
                newImage = getDrawable(R.drawable.fan_display5);
                break;
            case 6:
                newImage = getDrawable(R.drawable.fan_display6);
                break;
            case 7:
                newImage = getDrawable(R.drawable.fan_display7);
                break;
        }
        if (auto) {
            newImage = getDrawable(R.drawable.fan_display_off);
        }
        fanDisplay.setImageDrawable(newImage);
    }

    /*-------------------------------------------------
    Climate button presses
    -------------------------------------------------*/

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
            AppLinkService.getInstance().setClimateDriverTemp(driverTemp - .5);
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
            AppLinkService.getInstance().setClimatePassTemp(passTemp - .5);
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

    /*-------------------------------------------------
    End climate button presses
    -------------------------------------------------*/

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public void climateMenuPressed(View view) {
        returnToMenu();
    }

    public void returnToMenu() {
        currentActivity = null;
        finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
    }

    public void disconnected() {
        if (defrostMenuDialog != null && defrostMenuDialog.isShowing()) {
            defrostMenuDialog.cancel();
        }
        if (acMenuDialog != null && acMenuDialog.isShowing()) {
            acMenuDialog.cancel();
        }
        MainActivity.connected = false;
        returnToMenu();
    }
}
