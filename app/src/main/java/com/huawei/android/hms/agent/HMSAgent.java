package com.huawei.android.hms.agent;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.huawei.android.hms.agent.common.ActivityMgr;
import com.huawei.android.hms.agent.common.ApiClientMgr;
import com.huawei.android.hms.agent.common.CheckUpdateApi;
import com.huawei.android.hms.agent.common.HMSAgentLog;
import com.huawei.android.hms.agent.common.IClientConnectCallback;
import com.huawei.android.hms.agent.common.INoProguard;
import com.huawei.android.hms.agent.common.handler.CheckUpdateHandler;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.huawei.android.hms.agent.hwid.CheckSignInApi;
import com.huawei.android.hms.agent.hwid.SignInApi;
import com.huawei.android.hms.agent.hwid.SignOutApi;
import com.huawei.android.hms.agent.hwid.handler.SignInHandler;
import com.huawei.android.hms.agent.hwid.handler.SignOutHandler;
import com.huawei.android.hms.agent.pay.GetPayOrderApi;
import com.huawei.android.hms.agent.pay.GetProductDetailsApi;
import com.huawei.android.hms.agent.pay.GetPurchaseInfoApi;
import com.huawei.android.hms.agent.pay.PayApi;
import com.huawei.android.hms.agent.pay.ProductPayApi;
import com.huawei.android.hms.agent.pay.handler.GetOrderHandler;
import com.huawei.android.hms.agent.pay.handler.GetProductDetailsHandler;
import com.huawei.android.hms.agent.pay.handler.GetPurchaseInfoHandler;
import com.huawei.android.hms.agent.pay.handler.PayHandler;
import com.huawei.android.hms.agent.pay.handler.ProductPayHandler;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.entity.pay.OrderRequest;
import com.huawei.hms.support.api.entity.pay.PayReq;
import com.huawei.hms.support.api.entity.pay.ProductDetailRequest;
import com.huawei.hms.support.api.entity.pay.ProductPayRequest;
import com.huawei.hms.support.api.entity.pay.PurchaseInfoRequest;

/**
 * HMSAgent 封装入口类。 提供了HMS SDK 功能的封装，使开发者更聚焦业务的处理。
 * HMSAgent encapsulates the entry class. Provides a encapsulation of the HMS SDK functionality that enables developers to focus more on business processing.
 */
public final class HMSAgent implements INoProguard {

    /**
     * 基础版本 | Base version
     */
    private static final String VER_020503001 = "020503001";

    /**
     * 2.6.0 版本1                                            | 2.6.0 version 1
     * 对外：接口不变                                         | External: interface unchanged
     * 对内：HMSSDK connect 接口增加activity参数              | Internal: HMSSDK connect interface to increase activity parameters
     *      HMSSDK sign 接口增加activity参数                      | HMSSDK sign interface to increase activity parameters
     * 自身优化：                                             | Self optimization:
     *      1、增加了升级时被其他界面覆盖的处理                  | Increased handling of other interface coverage issues when upgrading
     *      2、game模块savePlayerInfo接口，去掉activity的判断    | Game Module Saveplayerinfo method to remove activity judgments
     *      3、解决错误回调成功，增加重试次数3次                 | Resolve error callback succeeded, increase retry count 3 times
	 *      4、提供了多种HMSAgent初始化方法                      | Provides a variety of hmsagent initialization methods
     *      5、初始化时增加了版本号校验                          | Increased version number checksum during initialization
     */
    private static final String VER_020600001 = "020600001";

    /**
     * 2.6.0.200                                         | 2.6.0.200
     * 自身优化：                                        | Self optimization:
     *      1、增加shell脚本用来抽取代码和编译成jar            | Add shell script to extract code and compile into jar
     *      2、示例中manifest里面升级配置错误修复              | Example manifest upgrade configuration error Repair
     *      3、抽取代码中去掉manifest文件，只留纯代码          | Remove manifest files in the extraction code, leaving only pure code
     */
    private static final String VER_020600200 = "020600200";

    private static final String VER_020601002 = "020601002";

    private static final String VER_020601302 = "020601302";

    private static final String VER_020603300 = "020603300";

    /**
     * 当前版本号 | Current version number
     */
    public static final String CURVER = VER_020603300;

    public static final class AgentResultCode {

        /**
         * HMSAgent 成功 | success
         */
        public static final int HMSAGENT_SUCCESS = 0;

        /**
         * HMSAgent 没有初始化 | Hmsagent not initialized
         */
        public static final int HMSAGENT_NO_INIT = -1000;

        /**
         * 请求需要activity，但当前没有可用的activity | Request requires activity, but no active activity is currently available
         */
        public static final int NO_ACTIVITY_FOR_USE = -1001;

        /**
         * 结果为空 | Result is empty
         */
        public static final int RESULT_IS_NULL = -1002;

        /**
         * 状态为空 | Status is empty
         */
        public static final int STATUS_IS_NULL = -1003;

        /**
         * 拉起activity异常，需要检查activity有没有在manifest中配置 | Pull up an activity exception and need to check if the activity is configured in manifest
         */
        public static final int START_ACTIVITY_ERROR = -1004;

        /**
         * onActivityResult 回调结果错误 | Onactivityresult Callback Result Error
         */
        public static final int ON_ACTIVITY_RESULT_ERROR = -1005;

        /**
         * 重复请求 | Duplicate Request
         */
        public static final int REQUEST_REPEATED = -1006;

        /**
         * 连接client 超时 | Connect Client Timeout
         */
        public static final int APICLIENT_TIMEOUT = -1007;

        /**
         * 调用接口异常 | Calling an interface exception
         */
        public static final int CALL_EXCEPTION = -1008;

        /**
         * 接口参数为空 | Interface parameter is empty
         */
        public static final int EMPTY_PARAM = -1009;
    }

    private HMSAgent(){}

    private static boolean checkSDKVersion(Context context){
        long sdkMainVerL = HuaweiApiAvailability.HMS_SDK_VERSION_CODE/1000;
        long agentMainVerL = Long.parseLong(CURVER)/1000;
        if (sdkMainVerL != agentMainVerL) {
            String errMsg = "error: HMSAgent major version code ("+agentMainVerL+") does not match HMSSDK major version code ("+sdkMainVerL+")";
            HMSAgentLog.e(errMsg);
            Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * 初始化方法，传入第一个界面的activity   | Initialization method, passing in the first interface activity
     * @param activity 当前界面             | Current interface
     * @return true：成功 false：失败        | True: Success false: Failed
     */
    public static boolean init(Activity activity) {
        return init(null, activity);
    }

    /**
     * 初始化方法，建议在Application onCreate里面调用    | Initialization method, it is recommended to call in creator OnCreate
     * @param app 应用程序                              | Application
     * @return true：成功 false：失败                   | True: Success false: Failed
     */
    public static boolean init(Application app) {
        return init(app, null);
    }

    /**
     * 初始化方法，建议在Application onCreate里面调用 | Initialization method, it is recommended to call in creator OnCreate
     * @param app 应用程序 | Application
     * @param activity 当前界面 | Current activity
     * @return true：成功 false：失败 | True: Success false: Failed
     */
    public static boolean init(Application app, Activity activity) {

        Application appTmp = app;
        Activity activityTmp = activity;

        // 两个参数都为null，直接抛异常 | Two parameters are null, throwing exceptions directly
        if (appTmp == null && activityTmp == null) {
            HMSAgentLog.e("the param of method HMSAgent.init can not be null !!!");
            return false;
        }

        // 如果application实例为null，则从activity里面取 | If the creator instance is null, it is taken from the activity
        if (appTmp == null) {
            appTmp = activityTmp.getApplication();
        }

        // 如果application实例仍然为null，抛异常 | Throws an exception if the creator instance is still null
        if (appTmp == null) {
            HMSAgentLog.e("the param of method HMSAgent.init app can not be null !!!");
            return false;
        }

        // activity 已经失效，则赋值null | Assignment NULL if activity has been invalidated
        if (activityTmp != null && activityTmp.isFinishing()) {
            activityTmp = null;
        }

        // 检查HMSAgent 和 HMSSDK 版本匹配关系 | Check hmsagent and HMSSDK version matching relationships
        if (!checkSDKVersion(appTmp)) {
            return false;
        }

        HMSAgentLog.i("init HMSAgent " + CURVER + " with hmssdkver " + HuaweiApiAvailability.HMS_SDK_VERSION_CODE);

        // 初始化activity管理类 | Initializing Activity Management Classes
        ActivityMgr.INST.init(appTmp, activityTmp);

        // 初始化HuaweiApiClient管理类 | Initialize Huaweiapiclient Management class
        ApiClientMgr.INST.init(appTmp);

        return true;
    }

    /**
     * 释放资源，这里一般不需要调用 | Frees resources, which are generally not required to call
     */
    public static void destroy() {
        HMSAgentLog.i("destroy HMSAgent");
        ActivityMgr.INST.release();
        ApiClientMgr.INST.release();
    }

    /**
     * 连接HMS SDK， 可能拉起界面(包括升级引导等)，建议在第一个界面进行连接。 | Connecting to the HMS SDK may pull up the activity (including upgrade guard, etc.), and it is recommended that you connect in the first activity.
     * 此方法可以重复调用，没必要为了只调用一次做复杂处理 | This method can be called repeatedly, and there is no need to do complex processing for only one call at a time
     * 方法为异步调用，调用结果在主线程回调 | Method is called asynchronously, and the result is invoked in the main thread callback
     * @param activity 当前界面的activity， 不能传空 | Activity of the current activity, cannot be empty
     * @param callback 连接结果回调 | Connection Result Callback
     */
    public static void connect(Activity activity, final ConnectHandler callback) {
        HMSAgentLog.i("start connect");
        ApiClientMgr.INST.connect(new IClientConnectCallback() {
            @Override
            public void onConnect(final int rst, HuaweiApiClient client) {
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onConnect(rst);
                        }
                    });
                }
            }
        }, true);
    }

    /**
     * 检查本应用的升级 | Check for upgrades to this application
     * @param activity 上下文 | context
     * @param callback 升级结果回调 | check update Callback
     */
    public static void checkUpdate (Activity activity, CheckUpdateHandler callback) {
        new CheckUpdateApi().checkUpdate(activity, callback);
    }


    /**
     * 支付接口封装 | Pay method Encapsulation
     */
    public static final class Pay {
        /**
         * 支付接口 | Pay method
         * @param request 支付请求类实例 | Request body for payment
         * @param handler 支付结果回调（结果会在主线程回调） | Payment result callback (result will be callback in main thread)
         */
        public static void pay(PayReq request, PayHandler handler){
            PayApi.INST.pay(request, handler);
        }

        /**
         * 商品编码支付接口 | PMS Payment Method
         * @param requ 商品编码支付请求类 | Request body for PMS Payment
         * @param handler 商品编码支付结果回调（结果会在主线程回调）| PMS Payment Result Callback (results are in main thread callback)
         */
        public static void productPay(ProductPayRequest requ, final ProductPayHandler handler){
            ProductPayApi.INST.productPay(requ, handler);
        }

        /**
         * 查询订单接口 | Query Order Status method
         * @param request 查询订单请求体 | Request body for Query order status
         * @param handler 查询订单结果回调（结果会在主线程回调） | Query order result callback (results are in main thread callback)
         */
        public static void getOrderDetail(OrderRequest request, GetOrderHandler handler) {
            new GetPayOrderApi().getOrderDetail(request, handler);
        }

        /**
         * 查询商品信息接口 | Method for querying product information in PMS payment
         * @param request 查询商品信息请求体 | Request body for querying product information
         * @param handler 查询商品信息结果回调（结果会在主线程回调） | Query product information result callback (result will be callback in main thread)
         */
        public static void getProductDetails(ProductDetailRequest request, GetProductDetailsHandler handler) {
            new GetProductDetailsApi().getProductDetails(request, handler);
        }

        /**
         * 查询非消耗商品订单接口 | Query non-expendable commodity order interface
         * @param request “查询非消耗商品”请求体 | Query non-consumable item request body
         * @param handler 查询非消耗商品结果回调（结果会在主线程回调） | Query for Non-expendable product result callback (result will be callback in main thread)
         */
        public static void getPurchaseInfo(PurchaseInfoRequest request, GetPurchaseInfoHandler handler) {
            new GetPurchaseInfoApi().getPurchaseInfo(request, handler);
        }
    }


    /**
     * 帐号接口封装 | Account Interface Encapsulation
     */
    public static final class Hwid {
        /**
         * 帐号登录请求 | Account Login Request
         * 当forceLogin为false时，如果当前没有登录授权，则直接回调错误码。| When Forcelogin is false, the error code is directly invoked if there is currently no login authorization.
         * 当forceLogin为true时，如果当前没有登录授权，则会拉起相应界面引导用户登录授权。 | When Forcelogin is true, if there is currently no login authorization, the corresponding interface is pulled to boot the user to logon authorization.
         * @param forceLogin 是否强制登录。 | Whether to force a login.
         * @param handler 登录结果回调（结果会在主线程回调） | Login result Callback (result is in main thread callback)
         */
        public static void signIn(boolean forceLogin, SignInHandler handler){
            if (forceLogin) {
                SignInApi.INST.signIn(handler);
            } else {
                new CheckSignInApi().checkSignIn(handler);
            }
        }

        /**
         * 帐号登出请求。此接口调用后，下次再调用signIn会拉起界面，请谨慎调用。如果不确定就不要调用了。 | Account Login Request. After this method is called, the next time you call signIn will pull the interface, please call carefully. Do not call if you are unsure.
         * @param handler 登出结果回调（结果会在主线程回调） | Logout result callback (result will be callback in main thread)
         */
        public static void signOut(SignOutHandler handler){
            new SignOutApi().signOut(handler);
        }
    }

}
