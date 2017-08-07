package sochat.so.com.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.activity.LLPayBindCardActivity;
import sochat.so.com.android.adapter.LLPayBankListAdapter;
import sochat.so.com.android.model.BankCardModel;
import sochat.so.com.android.utils.CommonUtils;

/**
 * Created by Administrator on 2017/6/15.
 */

public class LLPayBankDialog extends Dialog implements LLPayBankListAdapter.setOnChoosedBankCallBack {
    private TextView tvAddNewBank;
    private ImageView ivBack;
    private ListView mListView;
    private LLPayBankListAdapter mAdapter;
    private Context context;
    private LLPayBankDialog dialog;

    private List<BankCardModel>bankCardLists;
    public LLPayBankDialog(@NonNull Context context,List<BankCardModel>bankCardLists) {
        super(context, R.style.dialog);
        dialog =this;
        this.context = context;
        this.bankCardLists =bankCardLists;
    }

    public LLPayBankDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected LLPayBankDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ll_pay_choose_bank);
        inits();
        listeners();
    }

    private void inits() {
        tvAddNewBank = (TextView) findViewById(R.id.tv_add_new_bank);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        mListView = (ListView) findViewById(R.id.mListView);

        mAdapter = new LLPayBankListAdapter(context,bankCardLists);
        mAdapter.setChoosedBankListener(dialog);
        mListView.setAdapter(mAdapter);

    }

    private void listeners() {
        ivBack.setOnClickListener(onClickListener);
        tvAddNewBank.setOnClickListener(onClickListener);
    }

    public void reFreshBankList(List<BankCardModel>bankCardLists){
        if (mAdapter!=null){
            mAdapter.setLists(bankCardLists);
        }
    }


    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_back :
                    dialog.dismiss();
                    break;
                case R.id.tv_add_new_bank:
                    Intent intent = new Intent(context, LLPayBindCardActivity.class);
                    CommonUtils.startActivity(context,intent);
                    break;
            }
        }
    };

    @Override
    public void choosedBankCallBack(BankCardModel item) {
        dismiss();
        if (onThisDialogFinishCallBack!=null){
            onThisDialogFinishCallBack.showChoosedBank(item);
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if (onThisDialogFinishCallBack!=null){
            onThisDialogFinishCallBack.showFirstDialog();
        }
    }

    private setOnThisDialogFinishCallBack onThisDialogFinishCallBack;
    public interface setOnThisDialogFinishCallBack{
        void showFirstDialog();
        void showChoosedBank(BankCardModel item);
    }
    public void setOnThisDialogFinishCallBackListener(setOnThisDialogFinishCallBack onThisDialogFinishCallBack){
        this.onThisDialogFinishCallBack =onThisDialogFinishCallBack;
    }


}
