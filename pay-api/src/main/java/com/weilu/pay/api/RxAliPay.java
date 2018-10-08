package com.weilu.pay.api;

import android.app.Activity;

import com.alipay.sdk.app.PayTask;
import com.weilu.pay.api.ali.PayResult;
import com.weilu.pay.api.exception.PayFailedException;
import com.weilu.pay.api.utils.ErrCode;
import com.weilu.pay.api.utils.RxPayUtils;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;

/**
 * Created by weilu on 2017/12/14.
 */

public class RxAliPay {
    private Activity activity;
    private String paySign;

    public RxAliPay with(Activity activity, String paySign) {
        this.activity = activity;
        this.paySign = paySign;
        return this;
    }

    public RxAliPay with(Activity activity) {
        this.activity = activity;
        return this;
    }

    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }

    public Observable<PayResult> requestPay() {
        return Observable
                .create(new ObservableOnSubscribe<PayTask>() {
                    @Override
                    public void subscribe(ObservableEmitter<PayTask> emitter) throws Exception {
                        if (emitter.isDisposed()) {
                            return;
                        }
                        if (activity == null) {
                            emitter.onError(new PayFailedException(String.valueOf(ErrCode.ACTIVITY_IS_NULL), "activity cannot be null"));
                            emitter.onComplete();
                            return;
                        }
                        if (paySign == null || "".equals(paySign)) {
                            emitter.onError(new PayFailedException(String.valueOf(ErrCode.PAY_SIGN_IS_NULL), "paySign cannot be null"));
                            emitter.onComplete();
                            return;
                        }
                        emitter.onNext(new PayTask(activity));
                        emitter.onComplete();
                    }
                })
                .map(new Function<PayTask, PayResult>() {
                    @Override
                    public PayResult apply(PayTask payTask) {
                        Map<String, String> result = payTask.payV2(paySign, true);
                        PayResult payResult = new PayResult(result);
                        return payResult;
                    }
                })
                .compose(RxPayUtils.<PayResult> checkAliPayResult())
                .compose(RxPayUtils.<PayResult> applySchedulers());
    }

}
