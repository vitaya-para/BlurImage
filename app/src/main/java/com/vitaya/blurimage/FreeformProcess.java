package com.vitaya.blurimage;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class FreeformProcess extends Service {
    public FreeformProcess() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //mPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }
}