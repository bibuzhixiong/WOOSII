package sochat.so.com.android.live.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.netease.nim.uikit.common.util.media.BitmapDecoder;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.session.emoji.MoonUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;

import java.util.Timer;
import java.util.TimerTask;

import sochat.so.com.android.R;
import sochat.so.com.android.live.DemoCache;
import sochat.so.com.android.live.base.LiveBaseActivity;
import sochat.so.com.android.live.im.session.input.InputConfig;
import sochat.so.com.android.live.utils.ScreenUtil;
import sochat.so.com.android.view.SoftKeyBoardListener;


/**
 * 键盘弹起时挤压输入框的解决方案：
 * 1、弹起一个透明的带输入框的去掉进出动画的Activity，并弹出键盘。当按下收起键盘、表情、更多、非键盘区域时，finish该InputActivity，
 * 在setResult里返回当前模式和输入框文本内容。
 * 2、如果在InputActivity的输入框中直接发送文本，那么走InputActivityProxy回调给发起方Activity去发送消息。
 * Created by hzxuwen on 2016/4/12.
 */
public class InputActivity extends LiveBaseActivity {

    // callback
    public interface InputActivityProxy {
        void onSendMessage(String text);
    }

    // constant
    private final static String EXTRA_INPUT_CONFIG = "EXTRA_INPUT_CONFIG";
    public final static String EXTRA_TEXT = "EXTRA_TEXT";
    public final static String EXTRA_MODE = "EXTRA_MODE";
    public final static int REQ_CODE = 20;

    // mode
    public final static int MODE_KEYBOARD_COLLAPSE = 0; // 键盘收起
    public final static int MODE_SHOW_EMOJI = 1; // 点击emoji按钮
    public final static int MODE_SHOW_MORE_FUNC = 2; // 点击更多布局按钮

    // view
    private View rootView;
    private EditText messageEditText; // 输入框
    private FrameLayout textAudioSwitchLayout; // 语音键盘切换按钮
    private View sendMessageButtonInInputBar; // 发送消息按钮
    private View moreFunctionButtonInInputBar; // 更多布局按钮
    private View emojiButtonInInputBar; // 表情按钮

    // data
    private String text;
    private InputConfig inputConfig;

    // calculate keyboard height to listen keyboard collapse event
    private int screenHeight = 0;
    private int keyboardOldHeight = -1;
    private int keyboardNowHeight = -1;
    private boolean quit = false;

    private RelativeLayout textMessageLayout;
    private int softheight=0;

    public static void startActivityForResult(Context context, String text, InputConfig inputConfig, InputActivityProxy proxy) {
        InputActivityProxyManager.getInstance().setProxy(proxy);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TEXT, text);
        intent.putExtra(EXTRA_INPUT_CONFIG, inputConfig);
        intent.setClass(context, InputActivity.class);
        ((Activity) context).startActivityForResult(intent, REQ_CODE);
    }

    @Override
    protected void handleIntent(Intent intent) {
        this.text = intent.getStringExtra(EXTRA_TEXT);
        this.inputConfig = (InputConfig) intent.getSerializableExtra(EXTRA_INPUT_CONFIG);
    }

    @Override
    protected int getContentView() {
        return R.layout.input_activity;
    }

    @Override
    protected void initView() {

        //监听键盘谈起，设置margin ,（仿制小米手机6.0.1，edittext被遮住）
        SoftKeyBoardListener.setListener(this,new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                softheight=height;
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)textMessageLayout.getLayoutParams();
                            params.setMargins(0,0,0,   ScreenUtil.dip2px(4));
                            textMessageLayout.setLayoutParams(params);
                        }
                    });

//                Toast.makeText(InputActivity.this, "键盘显示 高度" + height, Toast.LENGTH_SHORT).show();
//                Log.e("OOO","height_shwo:"+height);

            }

            @Override
            public void keyBoardHide(int height) {
                softheight=height;
//                Toast.makeText(InputActivity.this, "键盘隐藏 高度" + height, Toast.LENGTH_SHORT).show();
//                Log.e("OOO","height_hide:"+height);
            }
        });

        findViews();
        setListeners();
        setInputListener();
        initTextEdit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerObservers(true);


    }

    @Override
    protected void onDestroy() {
        InputActivityProxyManager.getInstance().clearProxy();
        registerObservers(false);
        super.onDestroy();
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, register);
    }

    Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
        @Override
        public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
            hideInputMethod();
        }
    };


    private void findViews() {
        // 根布局
        rootView = findView(R.id.input_layout);
        textMessageLayout= (RelativeLayout) findViewById(R.id.textMessageLayout);
        // 输入框
        messageEditText = findView(R.id.editTextMessage);
        MoonUtil.identifyFaceExpression(DemoCache.getContext(), messageEditText, text, ImageSpan.ALIGN_BOTTOM);
        messageEditText.setSelection(text.length());

        // 发送消息按钮
        sendMessageButtonInInputBar = findView(R.id.buttonSendMessage);

        // 文本录音按钮切换布局
        textAudioSwitchLayout = (FrameLayout) rootView.findViewById(R.id.switchLayout);
        textAudioSwitchLayout.setVisibility(inputConfig.isTextAudioSwitchShow ? View.VISIBLE : View.GONE);

        // emoji表情按钮
        emojiButtonInInputBar = rootView.findViewById(R.id.emoji_button);
        emojiButtonInInputBar.setVisibility(inputConfig.isEmojiButtonShow ? View.VISIBLE : View.GONE);

        // 更多布局按钮
        moreFunctionButtonInInputBar = rootView.findViewById(R.id.buttonMoreFuntionInText);
        moreFunctionButtonInInputBar.setVisibility(inputConfig.isMoreFunctionShow ? View.VISIBLE : View.GONE);


    }

    private void setListeners() {
        rootView.setOnClickListener(clickListener);
        sendMessageButtonInInputBar.setOnClickListener(clickListener);
        emojiButtonInInputBar.setOnClickListener(clickListener);
        moreFunctionButtonInInputBar.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonSendMessage:
                    InputActivityProxy proxy = InputActivityProxyManager.getInstance().getProxy();
                    if (proxy != null) {
                        proxy.onSendMessage(messageEditText.getText().toString());
                    }
                    messageEditText.setText("");
                    break;
                case R.id.input_layout:
                    setActivityResult(MODE_KEYBOARD_COLLAPSE);
                    break;
                case R.id.emoji_button:
                    setActivityResult(MODE_SHOW_EMOJI);
                    break;
                case R.id.buttonMoreFuntionInText:
                    setActivityResult(MODE_SHOW_MORE_FUNC);
                    break;
            }
        }
    };

    /**
     * 监听键盘收起事件，键盘收起，则该Activity finish
     */
    private void setInputListener() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                if (screenHeight == 0) {
                    screenHeight = r.bottom;
                }
                keyboardNowHeight = screenHeight - r.bottom;
                if (keyboardOldHeight != -1 && keyboardNowHeight != keyboardOldHeight && keyboardNowHeight <= 0) {
                    // 只适用于没有表情和更多布局的修改
                    setActivityResult(MODE_KEYBOARD_COLLAPSE);
                    doFinish();
                }
                keyboardOldHeight = keyboardNowHeight;
            }
        });
    }



    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

    private void initTextEdit() {
        messageEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                messageEditText.setHint("");
                checkSendButtonEnable(messageEditText);
            }
        });

        messageEditText.addTextChangedListener(new TextWatcher() {
            private int start;
            private int count;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.start = start;
                this.count = count;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkSendButtonEnable(messageEditText);
                MoonUtil.replaceEmoticons(InputActivity.this, s, start, count);
                int editEnd = messageEditText.getSelectionEnd();
                messageEditText.removeTextChangedListener(this);
                while (StringUtil.counterChars(s.toString()) > 5000 && editEnd > 0) {
                    s.delete(editEnd - 1, editEnd);
                    editEnd--;
                }
                messageEditText.setSelection(editEnd);
                messageEditText.addTextChangedListener(this);
            }
        });

        messageEditText.setFocusableInTouchMode(true);
        messageEditText.setFocusable(true);
        messageEditText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)messageEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(messageEditText, 0);



    }

    /**
     * 显示发送或更多
     */
    private void checkSendButtonEnable(EditText editText) {
        String textMessage = editText.getText().toString();
        if (!TextUtils.isEmpty(StringUtil.removeBlanks(textMessage)) && editText.hasFocus()) {
            moreFunctionButtonInInputBar.setVisibility(View.GONE);
            sendMessageButtonInInputBar.setVisibility(View.VISIBLE);
        } else if (inputConfig.isMoreFunctionShow) {
            sendMessageButtonInInputBar.setVisibility(View.GONE);
            moreFunctionButtonInInputBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 收起键盘，返回调用Activity
     */
    private void setActivityResult(final int mode) {
        quit = true;
        // hide keyboard
        hideInputMethod();

        // return result to pre activity
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TEXT, messageEditText.getText().toString());
        intent.putExtra(EXTRA_MODE, mode);
        setResult(Activity.RESULT_OK, intent);
    }

    private void doFinish() {
        // 解决返回到上一个页面，上个页面的键盘没有收起（依然能看到输入框处于挤压状态）的问题。
        getHandler().postDelayed(new Runnable() {
            //            @Override
            public void run() {
                finish();
                // 解决部分小米手机，在Theme中设置android:windowAnimationStyle指向null无效的问题。
                overridePendingTransition(0, 0);
            }
        }, 50);
    }

    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
    }

    private static class InputActivityProxyManager {
        private InputActivityProxy proxy;

        public static InputActivityProxyManager getInstance() {
            return InstanceHolder.instance;
        }

        public void setProxy(InputActivityProxy proxy) {
            this.proxy = proxy;
        }

        public InputActivityProxy getProxy() {
            return proxy;
        }

        public void clearProxy() {
            this.proxy = null;
        }

        static class InstanceHolder {
            final static InputActivityProxyManager instance = new InputActivityProxyManager();
        }
    }

}
