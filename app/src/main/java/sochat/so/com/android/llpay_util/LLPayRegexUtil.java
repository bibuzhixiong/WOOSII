package sochat.so.com.android.llpay_util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/5/23.
 */

public class LLPayRegexUtil {

    public static boolean checkMatchChina(String regex,String content){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()){
            return true ;
        }else{
            return false ;
        }
    }

}
