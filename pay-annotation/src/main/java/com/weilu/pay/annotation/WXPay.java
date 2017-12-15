package com.weilu.pay.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description:微信支付注解
 * @Author: weilu
 * @Time: 2017/12/12 15:10.
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface WXPay {
    String value();
}
