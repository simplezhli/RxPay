package com.weilu.pay;

import android.app.Application;

import com.weilu.pay.api.RxWxPay;

public class MyApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        RxWxPay.init(this);
    }
   
}
