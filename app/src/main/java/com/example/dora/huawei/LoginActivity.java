package com.example.dora.huawei;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.auth.api.signin.HuaweiIdSignIn;
import com.huawei.hms.auth.api.signin.HuaweiIdSignInClient;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MyActivity";

    HuaweiIdSignInOptions signInOptions;
    HuaweiIdSignInClient client;
    HttpsURLConnection conn = null;
    String token = "";
    String userName = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        findViewById(R.id.btn_SignInIDToken).setOnClickListener(this);

        findViewById(R.id.btn_revokeAuthorization).setOnClickListener(this);
        findViewById(R.id.btn_silentSignin).setOnClickListener(this);
        findViewById(R.id.btn_signout).setOnClickListener(this);
        signInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN).requestIdToken("").build();
        client = HuaweiIdSignIn.getClient(LoginActivity.this, signInOptions);
    }


    private void signInbyIDToken() {
        startActivityForResult(client.getSignInIntent(), 8888);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //Process the sign-in and authorization result and obtain an ID token from SignInHuaweiId.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888) {
            Task<SignInHuaweiId> signInHuaweiIdTask = HuaweiIdSignIn.getSignedInAccountFromIntent(data);
            if (signInHuaweiIdTask.isSuccessful()) {
                //The sign-in is successful, and the user's HUAWEI ID information and ID token are obtained.
                SignInHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();
                token = huaweiAccount.getIdToken();
                Log.i(TAG, "idToken:" + huaweiAccount.getIdToken());
                tokenVerifyCon task = new tokenVerifyCon(LoginActivity.this);
                task.execute();
            }
            else
            {
                //The sign-in failed.
                Log.e(TAG, "sign in failed : " + ((ApiException)signInHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }


    private class tokenVerifyCon extends AsyncTask<String, Void, String>  {
        // private ProgressDialog dialog;

        public tokenVerifyCon(LoginActivity activity) {
            //dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
/*            dialog.setMessage("Downloading the places, please waiting");
            dialog.show();*/
        }

        @Override
        public String doInBackground(String... params) {

            String get_url = "https://oauth-login.cloud.huawei.com/oauth2/v3/tokeninfo?id_token="+token;;
            // category URL with category
            JSONObject json = HTTPManager.getData(get_url);
            if (json != null) {
                userName = JSONConverter.parseFeed(json);
            }
            return userName;
        }

        @Override
        protected void onPostExecute(String result){
            Toast toast=Toast.makeText(getApplicationContext(),"Welcome "+ userName + " to Log In",Toast.LENGTH_SHORT);
            toast.setMargin(50,50);
            toast.show();
        }

    }

    private void revokeAuth(){
        client.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>(){
            //Perform operations after the withdrawal.
            @Override
            public void onComplete(Task<Void> task){
                if (task.isSuccessful()){
                    //do some thing while revoke success
                    Toast toast=Toast.makeText(getApplicationContext(),"Revoke Success",Toast.LENGTH_SHORT);
                    toast.setMargin(50,50);
                    toast.show();
                    Log.i(TAG, "onSuccess: ");
                }else{
                    //do some thing while revoke success
                    Exception exception = task.getException();
                    if (exception instanceof ApiException){
                        int statusCode = ((ApiException) exception).getStatusCode();
                        Log.i(TAG, "onFailure: " + statusCode);
                    }
                }
            }
        });
    }

    private void silentSignIn(){
        Task<SignInHuaweiId> task = client.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<SignInHuaweiId>() {
            @Override
            public void onSuccess(SignInHuaweiId signInHuaweiId) {
                Log.i(TAG, "silentSignIn success");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //if Failed use getSignInIntent
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    signInbyIDToken();
                }
            }
        });

    }

    /**
     * 退出。此接口调用后，下次再调用signIn会拉起界面，请谨慎调用。如果不确定就不要调用了。 | Exit。 After this method is called, the next time you call signIn will pull the activity, please call carefully. Do not call if you are unsure.
     */
    private void signOut(){
        Task<Void> signOutTask = client.signOut();
        signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast toast=Toast.makeText(getApplicationContext(),"Successfully Sign out",Toast.LENGTH_SHORT);
                toast.setMargin(50,50);
                toast.show();
                Log.i(TAG, "signOut Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "signOut fail");
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
            // 如果不是tab切换按钮则处理业务按钮事件 | Handle Business button events without the TAB toggle button
            switch (id) {
                case R.id.btn_SignInIDToken:
                    signInbyIDToken();
                    break;
                case R.id.btn_revokeAuthorization:
                    revokeAuth();
                    break;
                case R.id.btn_silentSignin:
                    silentSignIn();
                    break;
                case R.id.btn_signout:
                    signOut();
                    break;
                default:
            }
    }
}
