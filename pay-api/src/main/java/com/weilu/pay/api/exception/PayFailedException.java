package com.weilu.pay.api.exception;

/**
 * Created by Vinctor on 2016/6/21.
 */
public class PayFailedException extends RuntimeException {
    public PayFailedException(String detailMessage) {
        super(detailMessage);
    }
}
