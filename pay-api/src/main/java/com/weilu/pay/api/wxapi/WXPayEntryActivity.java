package com.weilu.pay.api.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weilu.pay.api.RxWxPay;
import com.weilu.pay.api.utils.BusUtil;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
  private IWXAPI api;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    api = WXAPIFactory.createWXAPI(this, RxWxPay.getInstance().getAppId());
    api.handleIntent(getIntent(), this);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    api.handleIntent(intent, this);
  }

  @Override
  public void onReq(BaseReq baseReq) {
  }

  @Override
  public void onResp(BaseResp baseResp) {
    if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
      BusUtil.getDefault().post(baseResp);
      finish();
    }
  }
}
