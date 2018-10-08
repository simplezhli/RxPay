package com.weilu.pay.api.utils;

/**
 * @Description:
 * @Author: weilu
 * @Time: 2018/9/30 0030 17:52.
 */
public class ErrCode {
    /**
     * 未安装微信
     */
    public final static int NOT_INSTALLED_WECHAT = -900;
    /**
     * 支付宝支付签名为空
     */
    public final static int PAY_SIGN_IS_NULL = -901;
    /**
     * activity为空
     */
    public final static int ACTIVITY_IS_NULL = -902;
    /**
     * 参数为空
     */
    public final static int PARAMETER_IS_NULL = -903;
    /**
     * 未初始化
     */
    public final static int NOT_INIT = -904;
}
