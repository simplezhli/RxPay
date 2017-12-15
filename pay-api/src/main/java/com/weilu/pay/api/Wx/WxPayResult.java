package com.weilu.pay.api.Wx;

/**
 * Created by weilu on 2017/12/14.
 */

public class WxPayResult {
    int errCode;

    public WxPayResult(int errCode) {
        this.errCode = errCode;
    }

    /**
     * 获取微信支付结果状态码
     *
     * @return 0成功  -1错误  -2取消  -7未安装微信
     */
    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCood) {
        this.errCode = errCode;
    }

    public String getErrInfo() {
        String result = "";
        switch (errCode){
            case 0:
                result = "支付成功";
                break;
            case -1:
                result = "支付错误";
                break;
            case -2:
                result = "支付取消";
                break;
            case -4:
                result = "拒绝支付";
                break;
            case -7:
                result = "您未安装最新版本微信，不支持微信支付，请安装或升级微信版本";
                break;
            default:
                 break;
        }
        return result;
    }

    public boolean isSucceed(){
        // errCode 为0则代表支付成功
        return errCode == 0;
    }

}
