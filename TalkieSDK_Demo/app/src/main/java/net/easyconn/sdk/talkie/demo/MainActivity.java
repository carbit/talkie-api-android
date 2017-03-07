package net.easyconn.sdk.talkie.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.easyconn.talkie.sdk.TalkieClient;
import net.easyconn.talkie.sdk.TalkieManager;
import net.easyconn.talkie.utils.SpUtil;

public class MainActivity extends AppCompatActivity {

    private TextView txRegist = null;
    private TextView txOnline = null;
    private EditText mIdEditText;
    private TextView txReqSpeak = null;
    private TextView txStopSpeak = null;
    private TextView txOtherSpeak = null;
    private EditText edRegist = null;
    private boolean isClickStop = false;

    private TextView mTvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TalkieManager.init(this);

        initView();

    }

    private void initView() {
        txRegist = (TextView) findViewById(R.id.tx_regist);
        txOnline = (TextView) findViewById(R.id.tx_online);
        mIdEditText = (EditText) findViewById(R.id.ed_id);
        txReqSpeak = (TextView) findViewById(R.id.tx_req_speak);
        txStopSpeak = (TextView) findViewById(R.id.tx_stop_speak);
        txOtherSpeak = (TextView) findViewById(R.id.tx_other_speak);
        edRegist = (EditText) findViewById(R.id.ed_regist);
        mTvLocation = (TextView) findViewById(R.id.tv_location);

        findViewById(R.id.regist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkieManager.login("demo", edRegist.getText().toString().trim(), new TalkieClient.ConnectCallback() {
                    @Override
                    public void onSuccess(String openid) {
                        showToast("登录成功");
                        txRegist.setText(openid);
                        findViewById(R.id.regist).setEnabled(false);
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        showToast("登录失败，errorCode： " + errorCode);
                    }

                });
            }
        });
        findViewById(R.id.online).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mIdEditText.getText().toString().trim();
                if (TextUtils.isEmpty(id)) {
                    showToast("id不能为空");
                    return;
                }
                if (!id.matches("\\d{1,5}")) {
                    showToast("id必须为5位内数字");
                    return;
                }

                TalkieManager.online(id, new TalkieClient.OnlineCallback() {
                    @Override
                    public void onSuccess() {
                        showToast("加入成功");
                        txOnline.setText("加入成功");

                        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(mIdEditText.getWindowToken(), 0);
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        txReqSpeak.setText("加入失败，errorCode： " + errorCode);
                    }

                });
            }
        });

        findViewById(R.id.req_speak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkieManager.reqSpeak(new TalkieClient.ReqSpeakCallback() {
                    @Override
                    public void onSuccess() {
                        showToast("抢麦成功");
                        txReqSpeak.setText("抢麦成功，请说话！");
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        txReqSpeak.setText("抢麦失败，errorCode： " + errorCode);
                    }
                });
            }
        });

        findViewById(R.id.stop_speak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkieManager.stopSpeak();
                txReqSpeak.setText("手动丢麦");
                isClickStop = true;
            }
        });

        TalkieManager.setOtherStartSpeakListener(new TalkieClient.StartSpeakListener() {
            @Override
            public void onStartSpeak(String openid) {
                txOtherSpeak.setText(openid+" -> 抢麦 ");
            }
        });

        TalkieManager.setOtherStopSpeakListener(new TalkieClient.StopSpeakListener() {
            @Override
            public void onStopSpeak() {
                txOtherSpeak.setText("");
            }
        });

        TalkieManager.setStopSpeakNtfListener(new TalkieClient.StopSpeakNtfListener() {
            @Override
            public void onStopSpeakNtfListener() {
                if(!isClickStop) {
                    txReqSpeak.setText("抢麦超过30秒，服务器通知丢麦");
                }
                isClickStop = false;
            }
        });

        findViewById(R.id.btn_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkieManager.location(30.574305D, 114.286212D, 10, 30);
                mTvLocation.setText("位置上报 纬度:30.574305 经度:114.286212");
            }
        });

        TalkieManager.setOtherLocationListener(new TalkieClient.LocationListener() {
            @Override
            public void onLocationListener(String openid, double lat, double lon, int speed, int direction) {
                mTvLocation.setText(String.format("%s位置变更纬度:%s 经度:%s", openid, lat, lon));
            }
        });
    }

    @Override
    protected void onDestroy() {
        TalkieManager.destroy();

        super.onDestroy();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
