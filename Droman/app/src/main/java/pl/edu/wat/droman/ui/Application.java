package pl.edu.wat.droman.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import com.secneo.sdk.Helper;

import pl.edu.wat.droman.ui.djiconnectioncontrol.DJIConnectionControlActivity;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BroadcastReceiver br = new OnDJIUSBAttachedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DJIConnectionControlActivity.ACCESSORY_ATTACHED);
        registerReceiver(br, filter);
    }

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(Application.this);
    }
}
