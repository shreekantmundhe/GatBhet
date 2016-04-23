package com.gatbhet.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ADMINIBM on 4/23/2016.
 */
public class RestartReceiver extends BroadcastReceiver{

    Context context;
    private final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        context = context;
        String action = intent.getAction();
        if (action.equalsIgnoreCase(BOOT_ACTION)) {
            //check for boot complete event & start your service
            startService();
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction("com.gatbhet.activities.BackgroundGPSService");
        context.startService(serviceIntent);
    }
}
