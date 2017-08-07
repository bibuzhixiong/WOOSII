package sochat.so.com.android.eventbus;

import sochat.so.com.android.model.BankCardModel;

/**
 * Created by Administrator on 2017/6/20.
 */

public class ChooseBankWithDrawEvent {
    private BankCardModel item;

    public BankCardModel getItem() {
        return item;
    }

    public void setItem(BankCardModel item) {
        this.item = item;
    }

    public ChooseBankWithDrawEvent(BankCardModel item) {
        this.item = item;
    }
}
