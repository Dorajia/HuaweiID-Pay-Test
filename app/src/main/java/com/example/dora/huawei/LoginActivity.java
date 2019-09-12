package com.example.dora.huawei;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.hwid.handler.SignInHandler;
import com.huawei.android.hms.agent.hwid.handler.SignOutHandler;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hms.support.api.hwid.SignOutResult;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        findViewById(R.id.btn_id).setBackgroundColor(Color.WHITE);

        findViewById(R.id.btn_signin).setOnClickListener(this);
        findViewById(R.id.btn_forcesignin).setOnClickListener(this);
        findViewById(R.id.btn_signout).setOnClickListener(this);
    }

    /**
     * 登录授权 | Login Authorization
     * @param forceLogin 如果已经授权登录则直接回调结果，否则：forceLogin为true时会拉起界面，为false时直接回调错误码 | If the login is authorized, the result is directly callback, otherwise: When Forcelogin is true, the activity is pulled and the error code is directly invoked when false
     */
    private void signIn(boolean forceLogin) {
        HMSAgent.Hwid.signIn(true, new SignInHandler() {
            @Override
            public void onResult(int rtnCode, SignInHuaweiId signInResult) {
                if (rtnCode == HMSAgent.AgentResultCode.HMSAGENT_SUCCESS && signInResult != null) {
                    Log.i(TAG, "success=========" );
                    Log.i(TAG, "nickname: " + signInResult.getDisplayName());
                    Log.i(TAG, "accessToken:" + signInResult.getAccessToken());
                    Log.i(TAG, "photourl:" + signInResult.getPhotoUrl());
                } else {

                    Log.i(TAG, "error:" + rtnCode);
                }
            }
        });
    }

    /**
     * 退出。此接口调用后，下次再调用signIn会拉起界面，请谨慎调用。如果不确定就不要调用了。 | Exit。 After this method is called, the next time you call signIn will pull the activity, please call carefully. Do not call if you are unsure.
     */
    private void signOut(){
        HMSAgent.Hwid.signOut(new SignOutHandler() {
            @Override
            public void onResult(int rtnCode, SignOutResult signOutResult) {
                if (rtnCode == HMSAgent.AgentResultCode.HMSAGENT_SUCCESS && signOutResult != null) {
                    Log.i(TAG, "SignOut successful");
                } else {
                    Log.i(TAG, "SignOut fail:" + rtnCode);
                }
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_id) {
            // 本页面切换到本页面的按钮事件不处理 | "This page switches to itself" button event does not need to be handled
            return;
        } else{
            // 如果不是tab切换按钮则处理业务按钮事件 | Handle Business button events without the TAB toggle button
            switch (id) {
                case R.id.btn_signin:
                    signIn(false);
                    break;
                case R.id.btn_forcesignin:
                    signIn(true);
                    break;
                case R.id.btn_signout:
                    signOut();
                    break;
                default:
            }
        }
    }
}
