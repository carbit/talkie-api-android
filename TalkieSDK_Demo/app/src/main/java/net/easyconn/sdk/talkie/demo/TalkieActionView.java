package net.easyconn.sdk.talkie.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created user:young
 * Created data:17-5-16
 * Description:
 */

public class TalkieActionView extends FrameLayout {

    private View vLeisureParent;
    private View vRequestSpeakingParent;
    private View vSpeakingParent;
    private View vRequestFailureParent;

    public TalkieActionView(Context context) {
        this(context, null);
    }

    public TalkieActionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TalkieActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.view_talkie_action, this);

        vLeisureParent = findViewById(R.id.ll_leisure);
        vRequestSpeakingParent = findViewById(R.id.ll_request_speaking);
        vSpeakingParent = findViewById(R.id.ll_speaking);
        vRequestFailureParent = findViewById(R.id.ll_request_failure);

        onLeisure();
    }

    public void onLeisure() {
        show(vLeisureParent);
    }

    //抢麦中
    public void onRequestSpeaking() {
        show(vRequestSpeakingParent);
    }

    //发言中
    public void onSpeaking() {
        show(vSpeakingParent);
    }

    //抢麦失败
    public void onRequestFailure(int code) {
        show(vRequestFailureParent);
    }

    private void show(View view) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.equals(view)) {
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }

}
