package sochat.so.com.android.interface_method_realize;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.interface_method.ILLPayGetBankListCallBack;
import sochat.so.com.android.interface_method.ILLPayGetBankListModel;
import sochat.so.com.android.interface_method.JsonCallBack;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.model.BankCardModel;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.DemoHelper;

/**
 * Created by Administrator on 2017/6/20.
 */

public class LLPayGetBankListModel implements ILLPayGetBankListModel{
    //银行卡列表
    private List<BankCardModel> bankCardLists;
    //验证码的参数
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    //签名的参数
    private Map<String, Object> sign_parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new MyHashMap<>();
    //被选择支付的银行卡对象
    private BankCardModel choosedPayBank;

    public LLPayGetBankListModel() {
        bankCardLists = new ArrayList<>();
    }

    @Override
    public void getBankList(Activity context, final ILLPayGetBankListCallBack callBack) {
        if (bankCardLists!=null){
            bankCardLists.clear();
        }
        sign_parameters.clear();
        parameters.clear();
        sign_parameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        sign_parameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        sign_parameters.put("user_id", DemoHelper.getUid());
        sign_parameters.put("offset", 1);
        parameters.put("data",sign_parameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_USERBANKCARD);


        MyNetWorkUtil.getNovate(context, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(context, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"getBankData_info:"+info);
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        String bank_array = jsonObject.getString("agreement_list");
                        Log.i(ConfigInfo.TAG,"RechargeTimeActivity_getBankData_bank_array:"+bank_array);

                        //Json的解析类对象
                        JsonParser parser = new JsonParser();
                        //将JSON的String 转成一个JsonArray对象
                        JsonArray jsonArray = parser.parse(bank_array).getAsJsonArray();
                        Gson gson = new Gson();
                        //加强for循环遍历JsonArray
                        for (JsonElement user : jsonArray) {
                            //使用GSON，直接转成Bean对象
                            BankCardModel bankCardModel = gson.fromJson(user, BankCardModel.class);
                            bankCardLists.add(bankCardModel);
                        }

                        callBack.getBabkLists(bankCardLists);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
