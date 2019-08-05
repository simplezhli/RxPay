# RxPay

## 本项目实现支付宝、微信支付。

基于 [Vinctor/RxPay](https://github.com/Vinctor/RxPay) 修改实现，感谢原作者。有如下不同：

- 支持RxJava2。

- 对支付结果的二次封装。

RxPay的优点：

- 使用简单、清晰。
      
- 不需要编写`WXPayEntryActivity`类以及声明微信要求的广播类`AppRegister`。

## 使用方式

添加依赖

```xml
    implementation 'com.github.simplezhli.RxPay:pay-api:v1.0.9'
    annotationProcessor 'com.github.simplezhli.RxPay:pay-compiler:v1.0.9'
```

### 支付宝

```java
   new RxAliPay()
           .with(MainActivity.this, sign) //服务器端返回签名
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
           

```

### 微信

1.在你自定义的`Application`或者`Activity`的`onCreate`中初始化。

```java
   RxWxPay.init(this);
```

2.对你任意一个`activity`类进行如下注解：

```java
   @WXPay("微信支付注册时的包名")
   public class MainActivity extends AppCompatActivity {}
```

之后Make Project

3.`AndroidManifest.xml`加入

```xml
    <activity
          android:name=".wxapi.WXPayEntryActivity"
          android:exported="true"
          android:theme="@android:style/Theme.Translucent.NoTitleBar"
          android:launchMode="singleTop" />
```

适配8.0注意透明主题的设置：[Android 8.0适配指北](https://weilu.blog.csdn.net/article/details/80965631#t5)

4.调用方法

```java
   RxWxPay.WXPayBean payBean = new RxWxPay.WXPayBean("appid", "partnerid", "noncestr",
           "timestamp", "prepayid", "sign");
   
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

```

5.**注意**

防止内存泄露，记得要取消订阅。

```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxWxPay.getInstance().onDestroy();
    }
```

## License

	Copyright 2017 simplezhli

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
