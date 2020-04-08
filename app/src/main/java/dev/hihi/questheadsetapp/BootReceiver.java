package dev.hihi.questheadsetapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent i) {
        Intent intent = new Intent();
        intent.setClass(context, MyService.class);
        context.startService(intent);
    }
}
