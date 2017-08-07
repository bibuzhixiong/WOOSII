package sochat.so.com.android.interface_method_realize;

import android.app.Activity;

import java.util.List;

import sochat.so.com.android.interface_method.ILLPayGetBankListCallBack;
import sochat.so.com.android.interface_method.ILLPayGetBankListModel;
import sochat.so.com.android.interface_method.ILLPayGetBankListPresenter;
import sochat.so.com.android.interface_method.ILLPayGetBankListView;
import sochat.so.com.android.model.BankCardModel;

/**
 * Created by Administrator on 2017/6/20.
 */

public class LLPayGetBankListPresenter implements ILLPayGetBankListPresenter {
    private ILLPayGetBankListModel model;
    private ILLPayGetBankListView view;

    public LLPayGetBankListPresenter(ILLPayGetBankListView view) {
        this.model = new LLPayGetBankListModel();
        this.view = view;
    }

    @Override
    public void getBankList(Activity context) {
        model.getBankList(context, new ILLPayGetBankListCallBack() {
            @Override
            public void getBabkLists(List<BankCardModel> bankCardLists) {
                view.getBankList(bankCardLists);
            }
        });

    }
}
