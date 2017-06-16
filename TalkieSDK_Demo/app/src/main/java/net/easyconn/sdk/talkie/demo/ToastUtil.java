package net.easyconn.sdk.talkie.demo;

import android.content.Context;
import android.widget.Toast;

/**
 * Created user:young
 * Created data:17-6-7
 * Description:
 */

public class ToastUtil {

    private static Toast sToast;

    public static void show(Context context, String message) {
        if (sToast == null) {
            sToast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
        } else {
            sToast.setText(message);
        }

        sToast.show();
    }

}
