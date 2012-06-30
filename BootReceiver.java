package com.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{
    public static final String ACTION = "android.intent.action.BOOT_COMPLETED";   
    @Override  
    public void onReceive(Context context, Intent intent) {   
        // TODO Auto-generated method stub   
        if (intent.getAction().equals(ACTION)) {
            Intent mainactivity=new Intent();
            mainactivity.setClass(context, JustatestActivity.class);
            context.startActivity(mainactivity);
        }   
    }   
}