package sochat.so.com.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.adapter.TextBookAdapter;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.TextBookModel;
import sochat.so.com.android.utils.HttpUtils;

/**
 * Created by Administrator on 2017/4/25.
 */

public class TextBookActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.tv_commit)
    TextView tvCommit;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    private TextBookAdapter mTextBookAdapter;
    private List<TextBookModel>lists;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    mTextBookAdapter.setLists((ArrayList<TextBookModel>) lists);
                    break;
                case 1:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_textbook);
        ButterKnife.bind(this);
        inits();
    }

    private void inits() {
        tvTopText.setText("教程版本");
        lists = new ArrayList<TextBookModel>();
        mTextBookAdapter = new TextBookAdapter(lists,TextBookActivity.this);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,3);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(mTextBookAdapter);

        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mTextBookAdapter.setOnItemClickLitener(new TextBookAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onEnableButton(boolean isEnabled) {
            if (isEnabled){
                tvCommit.setEnabled(isEnabled);
                tvCommit.setBackgroundResource(R.drawable.selector_login_or_register_pressed);
            }
            }
        });

        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextBookActivity.this.finish();
            }
        });

        getData();
    }

    @OnClick(R.id.iv_top_back)
    public void onViewClicked() {
        TextBookActivity.this.finish();
    }

    private void getData(){
        String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/teach";
        HttpUtils.doGetAsyn(null, false, url, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"getData_result:"+result);
                //Json的解析类对象
                JsonParser parser = new JsonParser();
                //将JSON的String 转成一个JsonArray对象
                JsonArray jsonArray = parser.parse(result).getAsJsonArray();
                Gson gson = new Gson();
                //加强for循环遍历JsonArray
                for (JsonElement user : jsonArray) {
                    //使用GSON，直接转成Bean对象
                    TextBookModel textbook = gson.fromJson(user, TextBookModel.class);
                    lists.add(textbook);
                }
                Log.i(ConfigInfo.TAG,"lists:"+lists.toString());
                handler.sendEmptyMessage(0);
            }
        });
    }




}
