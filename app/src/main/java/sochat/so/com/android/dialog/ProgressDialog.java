package sochat.so.com.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import sochat.so.com.android.R;

/**
 * 数据加载dialog
 * @author Administrator
 */
public class ProgressDialog {
	public Dialog progressDialog;
	
	public TextView msg;
	
	public void showDialog(Context context,int height,String text)
	{
		 progressDialog = new Dialog(context, R.style.progress_dialog);
         progressDialog.setContentView(R.layout.dialog);
         progressDialog.setCancelable(true);
         progressDialog.setCanceledOnTouchOutside(false);//设置点击空白，弹出层也不会消失
         progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
         /*LayoutParams params = progressDialog.getWindow().getAttributes();	
         progressDialog.getWindow().setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);	
         params.y = (int)(height);	
         progressDialog.getWindow().setAttributes(params);	*/
         msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
         msg.setText(text);
         progressDialog.show();
	}
	
	public void updataProgress(String progress){
		try {
			if(msg !=null){
				msg.setText(progress);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void destroy()
	{
		if(progressDialog !=null){
			 progressDialog.dismiss();
		}
	}

}
