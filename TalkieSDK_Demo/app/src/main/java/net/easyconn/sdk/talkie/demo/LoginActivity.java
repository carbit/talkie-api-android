package net.easyconn.sdk.talkie.demo;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.easyconn.talkie.sdk.TalkieClient;
import net.easyconn.talkie.sdk.TalkieManager;

public class LoginActivity extends AppCompatActivity {

    private EditText vEditText;

    private Button vLogin;

    private TextView vOpenId, vToken;

    private Button vOauth;

    private String mOpenId, mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListener();

        mOpenId = SpUtil.getOpenId(this);
        mToken = SpUtil.getToken(this);

        if (TextUtils.isEmpty(mOpenId) || TextUtils.isEmpty(mToken)) {
            vOauth.setEnabled(false);

            vEditText.setEnabled(true);
            vLogin.setEnabled(true);
        } else {
            vEditText.setEnabled(false);
            vLogin.setEnabled(false);

            vOpenId.setText(String.format("openId: %s", mOpenId));
            vToken.setText(String.format("token: %s", mToken));
            vOauth.setEnabled(true);
        }
    }

    private void initView() {
        TextInputLayout inputLayout = (TextInputLayout) findViewById(R.id.input_edit_text);
        inputLayout.setHint("请输入客户端用户唯一id");
        vEditText = inputLayout.getEditText();
        vLogin = (Button) findViewById(R.id.btn_login);

        vOpenId = (TextView) findViewById(R.id.tv_open_id);
        vToken = (TextView) findViewById(R.id.tv_token);
        vOauth = (Button) findViewById(R.id.btn_oauth);
    }

    private void initListener() {
        vLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vEditText != null) {
                    String userId = vEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(userId)) {
                        ToastUtil.show(LoginActivity.this, "id不能为空");
                        return;
                    }
                    TalkieManager.login("B78E9DC78F238A7", userId, new TalkieClient.ConnectCallback() {
                        @Override
                        public void onSuccess(String openId, String token) {
                            ToastUtil.show(LoginActivity.this, "登录成功");

                            SpUtil.putOpenId(LoginActivity.this, openId);
                            SpUtil.putToken(LoginActivity.this, token);
                            setResult(1001);
                            finish();
                        }

                        @Override
                        public void onError(int errorCode, String errorMsg) {
                            ToastUtil.show(LoginActivity.this, "登录失败，errorCode： " + errorCode + " errorMsg:" + errorMsg);
                        }
                    });
                }
            }
        });
        vOauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkieManager.oauth(mOpenId, mToken, new TalkieClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        ToastUtil.show(LoginActivity.this, "授权成功");
                        setResult(1001);
                        finish();
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        ToastUtil.show(LoginActivity.this, "授权失败，errorCode： " + errorCode + " errorMsg:" + errorMsg);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(1003);
        super.onBackPressed();
    }
}
