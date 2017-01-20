package net.easyconn.sdk.talkie.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.easyconn.talkie.sdk.TalkieClient;
import net.easyconn.talkie.sdk.TalkieManager;
import net.easyconn.talkie.utils.SpUtil;

public class MainActivity extends AppCompatActivity {

    private TextView txRegist = null;
    private TextView txOnline = null;
    private TextView txReqSpeak = null;
    private TextView txStopSpeak = null;
    private TextView txOtherSpeak = null;
    private EditText edRegist = null;
    private boolean isClickStop = false;

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
        txReqSpeak = (TextView) findViewById(R.id.tx_req_speak);
        txStopSpeak = (TextView) findViewById(R.id.tx_stop_speak);
        txOtherSpeak = (TextView) findViewById(R.id.tx_other_speak);
        edRegist = (EditText) findViewById(R.id.ed_regist);

        findViewById(R.id.regist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkieManager.login("", edRegist.getText().toString(), new TalkieClient.ConnectCallback() {
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
                TalkieManager.online("1", new TalkieClient.OnlineCallback() {
                    @Override
                    public void onSuccess() {
                        showToast("加入成功");
                        txOnline.setText("加入成功");
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
