package com.ford.kcooley8.SYNCController;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

abstract public class BaseActivity extends FragmentActivity {
    public static BaseActivity currentActivity = null;

    abstract public void disconnected();
}
