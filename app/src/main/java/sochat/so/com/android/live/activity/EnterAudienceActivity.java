package sochat.so.com.android.live.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;

import sochat.so.com.android.R;
import sochat.so.com.android.live.DemoCache;
import sochat.so.com.android.live.base.LiveBaseActivity;
import sochat.so.com.android.live.entity.RoomInfoEntity;
import sochat.so.com.android.live.im.sever.DemoServerHttpClient;
import sochat.so.com.android.live.utils.sys.AndTools;


/**
 * Created by zhukkun on 1/12/17.
 */
public class EnterAudienceActivity extends LiveBaseActivity {

    public static final int MODE_ROOM = 0;
    public static final int MODE_ADDRESS =1;

    private RelativeLayout rl_select_room;
    private RelativeLayout rl_select_address;
    private View hint_line_room;
    private View hint_line_address;
    private ClearableEditTextWithIcon editText;
    private Button btn_enter;
    private ImageView iv_scan;
    private TextView tv_title;
    private View back;

    private int currentMode = MODE_ROOM;
    private boolean cancelEnterRoom;

    public static void start(Context context){
        Intent intent = new Intent(context, EnterAudienceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_enter_room;
    }

    @Override
    protected void initView() {
        bindView();
        clickView();
        tv_title.setText("我是观众");
        editText.addTextChangedListener(textWatcher);
        editText.setDeleteImage(R.drawable.btn_close_enter_room);
    }

    private void clickView() {
        rl_select_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode(MODE_ROOM);
            }
        });

        rl_select_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode(MODE_ADDRESS);
            }
        });

//这里是扫码获取地址部分
//        iv_scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(EnterAudienceActivity.this, QRCodeScanActivity.class);
//                startActivityForResult(intent, 100);
//            }
//        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkUriValidate()) return;

                cancelEnterRoom = false;
                DialogMaker.showProgressDialog(EnterAudienceActivity.this, null, "进入房间中", true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancelEnterRoom = true;
                    }
                }).setCanceledOnTouchOutside(false);

                DemoServerHttpClient.getInstance().getRoomInfo(currentMode, editText.getText().toString(), new DemoServerHttpClient.DemoServerHttpCallback<RoomInfoEntity>() {
                    @Override
                    public void onSuccess(RoomInfoEntity roomInfoEntity) {
                        DialogMaker.dismissProgressDialog();
                        DemoCache.setRoomInfoEntity(roomInfoEntity);
                        if(roomInfoEntity.getStatus() !=1 && roomInfoEntity.getStatus()!=3){
                            showToast("当前房间, 不在直播中");
                            return;
                        }
                        if(!cancelEnterRoom) {
                            if(currentMode == MODE_ROOM) {
                                LiveRoomActivity.startAudience(EnterAudienceActivity.this, roomInfoEntity.getRoomid() + "", roomInfoEntity.getRtmpPullUrl(), true,0,"");//0本来应该是填是否关注，空字符串应该是school_id
                            }else{
                                LiveRoomActivity.startAudience(EnterAudienceActivity.this, roomInfoEntity.getRoomid() + "", editText.getText().toString(), true,0,"");
                            }
                        }
                    }

                    @Override
                    public void onFailed(int code, String errorMsg) {
                        showToast(errorMsg);
                        DialogMaker.dismissProgressDialog();
                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    /**
     * 检查输入的地址有效性
     * @return
     */
    private boolean checkUriValidate() {
        if(currentMode == MODE_ROOM){
            //房间号只允许数字
            if(!editText.getText().toString().matches("\\d+")){
                showToast("请输入正确的房间号");
                return false;
            }
        }
        return true;
    }

    private void bindView() {
        rl_select_room = findView(R.id.select_layout_room);
        rl_select_address = findView(R.id.select_layout_address);
        hint_line_room = findView(R.id.hint_line_room);
        hint_line_address = findView(R.id.hint_line_address);
        editText = findView(R.id.edit_room_address);
        btn_enter = findView(R.id.btn_enter_room);
        iv_scan = findView(R.id.iv_scan);
        tv_title = findView(R.id.tv_title);
        back = findView(R.id.iv_back);
    }

    private void switchMode(int mode) {
        currentMode = mode;
        if(mode == MODE_ROOM) {
            hint_line_room.setVisibility(View.VISIBLE);
            hint_line_address.setVisibility(View.INVISIBLE);
            editText.setText("");
            editText.setHint(getString(R.string.input_hint_audience_room));
            iv_scan.setVisibility(View.GONE);
        }else{
            hint_line_room.setVisibility(View.INVISIBLE);
            hint_line_address.setVisibility(View.VISIBLE);
            editText.setText("");
            editText.setHint(getString(R.string.input_hint_audience_address));
            iv_scan.setVisibility(View.VISIBLE);
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length()>0){
                btn_enter.setEnabled(true);
                iv_scan.setVisibility(View.INVISIBLE);
            }else{
                btn_enter.setEnabled(false);
                if(currentMode == MODE_ADDRESS)
                    iv_scan.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data ==null || data.getExtras() == null || TextUtils.isEmpty(data.getExtras().getString("result"))) {
            return;
        }
        String result = data.getExtras().getString("result");
        if(editText != null){
            editText.setText(result);
        }
        iv_scan.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AndTools.hideIME(EnterAudienceActivity.this);
    }
}
