package com.weilu.pay;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.weilu.pay.annotation.WXPay;
import com.weilu.pay.api.RxAliPay;
import com.weilu.pay.api.RxWxPay;
import com.weilu.pay.api.ali.PayResult;
import com.weilu.pay.api.exception.PayFailedException;
import com.weilu.pay.api.wx.WxPayResult;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

@WXPay(BuildConfig.APPLICATION_ID)
public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RxWxPay.init(this.getApplication());
    }
    
    public void aLiPay(View view){
        new RxAliPay()
                .with(MainActivity.this, "")
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
