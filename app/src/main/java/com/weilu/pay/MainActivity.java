package com.weilu.pay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.weilu.pay.annotation.WXPay;
import com.weilu.pay.api.ali.PayResult;
import com.weilu.pay.api.RxAliPay;
import com.weilu.pay.api.RxWxPay;
import com.weilu.pay.api.wx.WxPayResult;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

@WXPay(BuildConfig.APPLICATION_ID)
public class MainActivity extends AppCompatActivity {
    
    private String sign1 ="";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void aLiPay(View view){
        new RxAliPay()
                .with(MainActivity.this, sign1)
                .requestPay()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PayResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(PayResult payResult) {
                        Toast.makeText(MainActivity.this, "支付成功！" , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {}
                });        
        
    }

    public void wechatPay(View view){

        RxWxPay.WXPayBean payBean = new RxWxPay.WXPayBean("",
                "", "",
                "", "",
                "");

        RxWxPay.getInstance()
                .withWxPayBean(payBean)
                .requestPay()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WxPayResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(WxPayResult wxPayResult) {
                        Toast.makeText(MainActivity.this, "支付成功！" , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {}
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxWxPay.getInstance().onDestroy();
    }
}
