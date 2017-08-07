package sochat.so.com.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.ChooseWithDrawBankCardListAdapter;
import sochat.so.com.android.eventbus.ChooseBankWithDrawEvent;
import sochat.so.com.android.interface_method.ILLPayGetBankListPresenter;
import sochat.so.com.android.interface_method.ILLPayGetBankListView;
import sochat.so.com.android.interface_method_realize.LLPayGetBankListPresenter;
import sochat.so.com.android.model.BankCardModel;

/**
 * 选择提现的银行卡
 * Created by Administrator on 2017/6/20.
 */

public class LLPayChooseBankWithdrawActivity extends BaseActivity implements ILLPayGetBankListView,ChooseWithDrawBankCardListAdapter.setOnItemCilckListener {
    private TextView tvTopTitle;
    private ImageView ivTopBack;
    private RecyclerView mRecyclerView;
    private ChooseWithDrawBankCardListAdapter mAdapter;
    private List<BankCardModel> lists;

    private String bankCardNo;
    //通过MVP开发模式获得银行卡列表
    private ILLPayGetBankListPresenter getBankListPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ll_pay_choose_bank_withdraw);
        findById();
        inits();
        setListenter();
    }

    private void findById() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_text);
        ivTopBack = (ImageView) findViewById(R.id.iv_top_back);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_bank_card);
    }

    private void inits() {
        tvTopTitle.setText("请选择要提现的银行卡");
        bankCardNo = getIntent().getStringExtra("current_bank");
        //声明对象，获得银行卡号
        getBankListPresenter = new LLPayGetBankListPresenter(LLPayChooseBankWithdrawActivity.this);
        getBankListPresenter.getBankList(LLPayChooseBankWithdrawActivity.this);
        //初始化数组
        lists = new ArrayList<BankCardModel>();
        mAdapter = new ChooseWithDrawBankCardListAdapter(LLPayChooseBankWithdrawActivity.this,lists,bankCardNo);
        mAdapter.setListener(LLPayChooseBankWithdrawActivity.this);
        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
        LLPayChooseBankWithdrawActivity.this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void setListenter() {
        ivTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LLPayChooseBankWithdrawActivity.this.finish();
            }
        });
    }

    @Override
    public void getBankList(List<BankCardModel> bankCardLists) {
    if (mAdapter!=null){
        mAdapter.setLists((ArrayList<BankCardModel>) bankCardLists);
    }
    }

    @Override
    public void onItemClickListener(int position,BankCardModel item) {
        EventBus.getDefault().post(new ChooseBankWithDrawEvent(item));
        finish();
    }

}
