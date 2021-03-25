package com.weilu.pay.api.utils;

import com.weilu.pay.api.ali.PayResult;
import com.weilu.pay.api.exception.PayFailedException;
import com.weilu.pay.api.wx.WxPayResult;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;


/**
 * Created by weilu on 2017/12/14.
 */

public class RxPayUtils {

    public static ObservableTransformer<PayResult, PayResult> checkAliPayResult() {
        return new ObservableTransformer<PayResult, PayResult>() {
            @Override
            public ObservableSource<PayResult> apply(Observable<PayResult> upstream) {
                return upstream.map(new Function<PayResult, PayResult>() {
                    @Override
                    public PayResult apply(PayResult payResult) throws Exception {
                        if (!payResult.isSucceed()) {
                            throw new PayFailedException(payResult.getResultStatus(), payResult.getErrInfo());
                        }
                        return payResult;
                    }
                });
            }
        };
    }

    public static ObservableTransformer<WxPayResult, WxPayResult> checkWechatResult() {
        return new ObservableTransformer<WxPayResult, WxPayResult>() {
            @Override
            public ObservableSource<WxPayResult> apply(Observable<WxPayResult> payResultObservable) {
                return payResultObservable.map(new Function<WxPayResult, WxPayResult>() {
                    @Override
                    public WxPayResult apply(WxPayResult wxPayResult) {
                        if (!wxPayResult.isSucceed()) {
                            throw new PayFailedException(String.valueOf(wxPayResult.getErrCode()), wxPayResult.getErrInfo());
                        }
                        return wxPayResult;
                    }
                });
            }
        };
    }

    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
                    @Override
                    public ObservableSource<T> apply(Observable<T> observable) {
                        return observable
                                .subscribeOn(Schedulers.io())
                                .unsubscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                };
    }
}
