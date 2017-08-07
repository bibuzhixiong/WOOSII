package sochat.so.com.android.llpay_util;

import android.os.Handler;

import sochat.so.com.android.llpay_util.pay.PayOrder;

/**
 * Created by Administrator on 2017/5/24.
 */

public interface OrderCallBack {
    void getHandler(Handler mHandler);
    void getPayOrder(PayOrder order);
}
