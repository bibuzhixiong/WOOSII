package sochat.so.com.android.wxapi;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import sochat.so.com.android.R;
import sochat.so.com.android.activity.BaseActivity;
import sochat.so.com.android.activity.IncomeToCashActivity;
import sochat.so.com.android.activity.LoginActivity;

/**
 * 微信回调
 * @author Administrator
 *
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
//	@Override
//	public void onReq(BaseReq baseReq) {
//
//	}
//
//	@Override
//	public void onResp(BaseResp baseResp) {
//
//	}

//	private ToastUtils toastCommom = ToastUtils.createToastConfig();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (LoginActivity.api!=null){
			LoginActivity.api.handleIntent(getIntent(),this);
		}

		if (IncomeToCashActivity.api!=null){
			IncomeToCashActivity.api.handleIntent(getIntent(),this);
		}

	}

	public void onReq(BaseReq baseReq) {

	}
	private int result = 0;

	public void onResp(BaseResp baseResp) {
		Log.i(ConfigInfo.TAG,"resp.errCode:"+ baseResp.errCode);
		switch (baseResp.errCode){
			case BaseResp.ErrCode.ERR_OK://发送成功
				if (LoginActivity.isWXLogin&&LoginActivity.loginActivity!=null){
					SendAuth.Resp sendResp= (SendAuth.Resp) baseResp;
					LoginActivity.WX_CODE = sendResp.code;
					LoginActivity.WX_BACK_CODE = BaseResp.ErrCode.ERR_OK;
					Log.i(ConfigInfo.TAG,"sendResp.code_login:"+sendResp.code);
					finish();
				}else if(LoginActivity.loginActivity!=null){
					result = R.string.errcode_success;
				}else {
					result = R.string.errcode_success;
				}

				if (IncomeToCashActivity.isWXLogin&&IncomeToCashActivity.incomeToCashActivity!=null){
					SendAuth.Resp sendResp= (SendAuth.Resp) baseResp;
					IncomeToCashActivity.WX_CODE = sendResp.code;
					IncomeToCashActivity.WX_BACK_CODE = BaseResp.ErrCode.ERR_OK;
					Log.i(ConfigInfo.TAG,"sendResp.code_income:"+sendResp.code);
					finish();
				}else if(LoginActivity.loginActivity!=null){
					result = R.string.errcode_success;
				}else {
					result = R.string.errcode_success;
				}



				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED://认证失败
				if(LoginActivity.loginActivity!=null){
					result = R.string.authorization_failure;
					LoginActivity.isWXLogin=false;
				}else{
					result = R.string.errcode_deny;
				}


				if(IncomeToCashActivity.incomeToCashActivity!=null){
					result = R.string.authorization_failure;
					IncomeToCashActivity.isWXLogin=false;
				}else{
					result = R.string.errcode_deny;
				}

				break;
			case BaseResp.ErrCode.ERR_UNSUPPORT://不支持
				if(LoginActivity.loginActivity!=null){
					result = R.string.authorization_unsupport;
					LoginActivity.isWXLogin=false;
				}else{
					result = R.string.errcode_deny;
				}


				if(IncomeToCashActivity.incomeToCashActivity!=null){
					result = R.string.authorization_unsupport;
					IncomeToCashActivity.isWXLogin=false;
				}else{
					result = R.string.errcode_deny;
				}


				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL://取消
				if(IncomeToCashActivity.incomeToCashActivity!=null){
					result = R.string.cancel_authorization;
					IncomeToCashActivity.isWXLogin=false;
				}else{
					result = R.string.errcode_cancel;
				}


				if(IncomeToCashActivity.incomeToCashActivity!=null){
					result = R.string.cancel_authorization;
					IncomeToCashActivity.isWXLogin=false;
				}else{
					result = R.string.errcode_cancel;
				}


				break;
			default:
				if(LoginActivity.loginActivity!=null){
					LoginActivity.isWXLogin=false;
				}else{
					result = R.string.errcode_unknown;
				}


				if(IncomeToCashActivity.incomeToCashActivity!=null){
					IncomeToCashActivity.isWXLogin=false;
				}else{
					result = R.string.errcode_unknown;
				}

				break;
		}

		if(result!=0){
			Toast.makeText(WXEntryActivity.this,getResources().getString(result),Toast.LENGTH_SHORT).show();
		}
	}
}
