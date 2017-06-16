package net.easyconn.sdk.talkie.demo;

import android.app.Application;

import net.easyconn.talkie.sdk.TalkieManager;

/**
 * Created user:young
 * Created data:17-5-26
 * Description:
 */

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TalkieManager.init(this, "DEMO");
    }
}
