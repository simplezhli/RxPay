package com.weilu.pay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.weilu.pay.annotation.WXPay;
import com.weilu.pay.api.RxAliPay;
import com.weilu.pay.api.RxWxPay;
import com.weilu.pay.api.ali.PayResult;
import com.weilu.pay.api.exception.PayFailedException;
import com.weilu.pay.api.wx.WxPayResult;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

@WXPay(BuildConfig.APPLICATION_ID)
public class MainActivity extends AppCompatActivity {
    
    private String sign1 ="partner=\"2088621720869834\"&seller_id=\"jingzhaozhifu3@163.com\"&out_trade_no=\"154094871710285\"&subject=\"%E7%9F%AD%E4%BF%A1%2F%E8%AF%AD%E9%9F%B3%E5%85%85%E5%80%BC%3A0.01%E5%85%83\"&body=\"%E7%9F%AD%E4%BF%A1%2F%E8%AF%AD%E9%9F%B3%E5%85%85%E5%80%BC%3A0.01%E5%85%83\"&total_fee=\"0.01\"&notify_url=\"http://ex.shoppingyizhan.com/index.php/ServicePoint/CallBack/alipay_success.html\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&sign=\"SoaYfQP7a3BeSsay5AtU2n2q8pW2W1ywWx6rLrEcCKN8gr4g9q0fvXPNA1Zvc73Y4jnIUKVSmzfk0aesswcxXl1pce%2BRbh8N98fuhX7nG9Oyu8f%2BD4EDvE7go1EWO7sJQmPbTB%2B6lojei5L8g2xRugu5DMWjRyLnFiq9duQd7uM%3D\"&sign_type=\"RSA\"";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void aLiPay(View view){
        new RxAliPay()
                .with(MainActivity.this, sign1)
                .requestPay()
                .subscribe(new Observer<PayResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(PayResult payResult) {
                        Toast.makeText(MainActivity.this, "支付成功！" , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ErrCode:", ((PayFailedException)e).getErrCode());
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
                .subscribe(new Observer<WxPayResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(WxPayResult wxPayResult) {
                        Toast.makeText(MainActivity.this, "支付成功！" , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ErrCode:", ((PayFailedException)e).getErrCode());
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
