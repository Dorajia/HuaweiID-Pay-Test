package com.example.dora.huawei;

import android.app.Application;
import android.util.Log;

import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;

public class App extends Application {

    private static final String TAG = "MyActivity";
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
       boolean init = HMSAgent.init(this);
       if (init) {
           Log.e(TAG, "App onCreate: Init HMSAgent Success");
       }else {
           Log.e(TAG, "App onCreate: Init HMSAgent failed");
       }

    }


    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
}
