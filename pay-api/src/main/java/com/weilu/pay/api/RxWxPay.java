package com.weilu.pay.api;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weilu.pay.api.exception.PayFailedException;
import com.weilu.pay.api.utils.BusUtil;
import com.weilu.pay.api.utils.ErrCode;
import com.weilu.pay.api.utils.RxPayUtils;
import com.weilu.pay.api.wx.WxPayResult;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by weilu on 2017/12/14.
 */

public class RxWxPay {
    
    private static RxWxPay singleton;

    public static RxWxPay getInstance() {
        if (singleton == null) {
            synchronized (RxAliPay.class) {
                if (singleton == null) {
                    singleton = new RxWxPay();
                    return singleton;
                }
            }
        }
        return singleton;
    }

    private static Application context;
    private static String packageName = "";
    private static BroadcastReceiver receiver;
    
    public static void init(Application application) {
        context = application;
        packageName = context.getPackageName();

        if (receiver == null){
            try {
                Class c = Class.forName(packageName + ".AppRegister");
                receiver = (BroadcastReceiver) c.getDeclaredConstructor().newInstance();
                context.registerReceiver(receiver, new IntentFilter("com.tencent.mm.plugin.openapi.Intent.ACTION_REFRESH_WXAPP"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String partnerid;

    private String noncestr;

    private String timestamp;

    private String prepayid;

    private String sign;

    private String appID;

    private WXPayBean payBean;

    public RxWxPay withAppID(String Appid) {
        this.appID = Appid;
        return this;
    }

    public RxWxPay withPartnerID(String partnerid) {
        this.partnerid = partnerid;
        return this;
    }

    public RxWxPay withNoncestr(String noncestr) {
        this.noncestr = noncestr;
        return this;
    }

    public RxWxPay withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public RxWxPay withPrepayID(String prepayid) {
        this.prepayid = prepayid;
        return this;
    }

    public RxWxPay withSign(String sign) {
        this.sign = sign;
        return this;
    }

    public String getAppid() {
        return appID;
    }

    public RxWxPay withWxPayBean(WXPayBean payBean) {
        this.payBean = payBean;
        appID = payBean.appid;
        partnerid = payBean.partnerid;
        noncestr = payBean.noncestr;
        timestamp = payBean.timestamp;
        prepayid = payBean.prepayid;
        sign = payBean.sign;
        return this;
    }

    public Observable<WxPayResult> requestPay() {
        if (payBean == null) {
            payBean = new WXPayBean(appID, partnerid, noncestr, timestamp, prepayid, sign);
            appID = payBean.appid;
            partnerid = payBean.partnerid;
            noncestr = payBean.noncestr;
            timestamp = payBean.timestamp;
            prepayid = payBean.prepayid;
            sign = payBean.sign;
        }
       
        return
                Observable
                        .create(new ObservableOnSubscribe<WxPayResult>() {
                            @Override
                            public void subscribe(final ObservableEmitter<WxPayResult> emitter) throws Exception {
                                if (emitter.isDisposed()) {
                                    return;
                                }

                                String checkResult = checkisEmpty();
                                
                                if (!isEmpty(checkResult)) {
                                    emitter.onError(new PayFailedException(String.valueOf(ErrCode.PARAMETER_IS_NULL), checkResult + " cannot be null"));
                                    emitter.onComplete();
                                    return;
                                }
                                
                                if (context == null) {
                                    emitter.onError(new PayFailedException(String.valueOf(ErrCode.NOT_INIT), "you have not init the WxPay in your Application!"));
                                    emitter.onComplete();
                                    return;
                                }

                                final IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
                                // 将该app注册到微信
                                msgApi.registerApp(payBean.getAppid());
                                
                                if (msgApi.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
                                    emitter.onNext(new WxPayResult(ErrCode.NOT_INSTALLED_WECHAT));
                                    emitter.onComplete();
                                    return;
                                }
                                PayReq request = new PayReq();
                                request.appId = payBean.appid;
                                request.partnerId = payBean.partnerid;
                                request.prepayId = payBean.prepayid;
                                request.packageValue = "Sign=WXPay";
                                request.nonceStr = payBean.noncestr;
                                request.timeStamp = payBean.timestamp;
                                request.sign = payBean.sign;
                                boolean isSend = msgApi.sendReq(request);
                                if (!isSend) {
                                    emitter.onNext(new WxPayResult(-1));
                                    emitter.onComplete();
                                } else {
                                    Disposable disposable = BusUtil.getDefault()
                                            .doSubscribe(
                                                    BaseResp.class, new Consumer<BaseResp>() {
                                                        @Override
                                                        public void accept(BaseResp baseResp) throws Exception {
                                                            emitter.onNext(new WxPayResult(baseResp.errCode));
                                                            emitter.onComplete();
                                                        }
                                                    },
                                                    new Consumer<Throwable>() {
                                                        @Override
                                                        public void accept(Throwable throwable) throws Exception {
                                                            Log.e("NewsMainPresenter", throwable.toString());
                                                        }
                                                    });
                                    BusUtil.getDefault().addSubscription(payBean, disposable);
                                }
                            }
                        })
                        .compose(RxPayUtils.<WxPayResult> checkWechatResult())
                        .compose(RxPayUtils.<WxPayResult> applySchedulers());
    }

    private String checkisEmpty() {
        return isEmpty(appID) ? "appid"
                : isEmpty(partnerid) ? "partnerid"
                : isEmpty(noncestr) ? "noncestr"
                : isEmpty(timestamp) ? "timestamp"
                : isEmpty(prepayid) ? "prepayid"
                : isEmpty(sign) ? "sign" : "";
    }

    public void onDestroy() {
        if (payBean != null){
            BusUtil.getDefault().unSubscribe(payBean);
        }
    }

    public static class WXPayBean {

        private String appid;

        private String partnerid;

        private String noncestr;

        private String timestamp;

        private String prepayid;

        private String sign;

        public WXPayBean(String appid, String partnerid, String noncestr, String timestamp, String prepayid, String sign) {
            this.appid = appid;
            this.partnerid = partnerid;
            this.noncestr = noncestr;
            this.timestamp = timestamp;
            this.prepayid = prepayid;
            this.sign = sign;
        }

        public String getAppid() {
            return appid;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public String getSign() {
            return sign;
        }
    }

    public static boolean isEmpty(@Nullable CharSequence str) {
        if (str == null || str.length() == 0){
            return true;
        }else{
            return false;
        }
    }
}
