package sochat.so.com.android.interface_method;

import java.util.List;

import sochat.so.com.android.model.BankCardModel;

/**
 * Created by Administrator on 2017/6/20.
 */

public interface ILLPayGetBankListCallBack {
    void getBabkLists(List<BankCardModel> bankCardLists);
}
