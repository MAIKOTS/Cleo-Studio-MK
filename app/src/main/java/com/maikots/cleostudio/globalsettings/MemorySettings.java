package com.maikots.cleostudio.globalsettings;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;

public class MemorySettings {

    public static void registrarMonitorDeMemoria(Context context, Runnable callbackLiberarMemoria) {
        if (context == null) return;

        context.registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                if (level >= TRIM_MEMORY_MODERATE) {
                    if (callbackLiberarMemoria != null) {
                        callbackLiberarMemoria.run();
                    }
                    System.gc();
                }
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {}

            @Override
            public void onLowMemory() {
                if (callbackLiberarMemoria != null) {
                    callbackLiberarMemoria.run();
                }
            }
        });
    }
}
