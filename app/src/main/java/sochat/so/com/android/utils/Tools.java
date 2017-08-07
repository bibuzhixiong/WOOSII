package sochat.so.com.android.utils;

import android.os.Environment;

/**
 * 
 * @author qiy
 * Create at 2015-4-24
 */
public class Tools {
	/**
	 * 检查是否存在SDCard
	 */
	public static boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 检查对象是否为空或者等于“”,为空返回true，不为空返回false
	 */
	public static boolean isBlank(Object obj){
		boolean result=false;
		if(obj==null||"".equals(obj.toString()))
		{
			result=true;
		}
		return result;
	}

}
