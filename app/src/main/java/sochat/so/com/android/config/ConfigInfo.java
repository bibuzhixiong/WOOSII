package sochat.so.com.android.config;

import sochat.so.com.android.R;

/**
 * Created by Administrator on 2017/2/18.
 * 常量工具类
 */

public class ConfigInfo {
        public static String ApiUrl = "http://app.woosii.com/";// 正式
//    public static String ApiUrl = "http://apptest.woosii.com/";// 测试
    /**
     * 连连支付相关
     */
    //连连支付钱包基本API
    public static final String LL_PAY_WALLET_BASEAPI ="https://wallet.lianlianpay.com/llwalletapi/";
    //用户所属商户号(正式用)认证实物
    public static final String CERT_PHYSICAL_OID_PARTNER  ="201705261001776572";
    //用户所属商户号(正式用)快捷实物
    public static final String QUICK_PHYSICAL_OID_PARTNER  ="201705261001775729";
    //用户所属商户号(正式用)认证虚拟
    public static final String CERT_VIRTUAL_OID_PARTNER  ="201705261001775732";
//    //用户所属商户号(正式用)快捷虚拟
    public static final String QUICK_VIRTUAL_OID_PARTNER  ="201705261001776575";
    //用户所属商户号(正式用)钱包商户号（认证）
    public static final String CERT_WALLET_OID_PARTNER  ="201706121001810463";
    //用户所属商户号(正式用)钱包商户号（快捷）
    public static final String QUICK_WALLET_OID_PARTNER  ="201706121001810469";
    //用户所属商户号(正式用)O2O
    public static final String O2O_OID_PARTNER  ="201705261001776578";


    public static final String RET_CODE_SUCCESS = "0000";// 0000 交易成功
    public static final String RET_CODE_PASSWORD_ERROR = "1004";// 1004 密码错误
    public static final String RET_CODE_USER_NO_AUTH = "3007";// 3007 用户钱包未开户
    public static final String RET_CODE_PROCESS = "2008";// 2008 支付处理中
    public static final String PAY_SUCCESS = "SUCCESS";// 2008 支付处理中

    //异步通知回调地址(支付成功的回调接口)
    public static final String NOTIFY_URL = ConfigInfo.ApiUrl+"/index.php/Vr/lianlianpayback/llpaymentback";
    //异步通知回调地址(从商户号到钱包转账成功的回调接口)
    public static final String OID_TO_WALLET_NOTIFY_URL = ConfigInfo.ApiUrl+"index.php/Vr/Lianlianpayback/llpayback";
    //异步通知回调地址(从连连钱包到银行卡)
    public static final String WALLET_TO_BANK_NOTIFY_URL = ConfigInfo.ApiUrl+"index.php/Vr/Lianlianpayback/llbanksback";



    // 商户（RSA）私钥(正式用)
    public static final String RSA_PRIVATE ="MIICXAIBAAKBgQCmRtKAj1GQ+EwNFSSi7iVFwP91p+b+HO0HTRI3W5/uOOO2El5cvks0pc+VCzKOexHAPiCRBwQSFv3ltKXXlVVzG/z24Kp07X1CpX8mtHjCtpESwOnXRiPxSpUOwcoAQ0biq9LSpDBlybbVTHPbRhkf67OYS72ipNcwllB0/58E2wIDAQABAoGAREG4cevjaOZ0mb4/Lt8hz9OdSpglO9V1+9RIkQ78xRb3ayt5Lwsy5ibW4vEYzOJ6c/dM+RvvMNndWilhFvGHhLKV4/a7M4ho6DBPk1g3Gl1iAI3QbvZ0kslla96dotnKt+lWvEFj+dKEM6+GGJy79ze1awCxrFU8xaH0AqoB6skCQQDbkDKlBKopdk3+lTt0lgfJgMBUqFlGtym1uow7C87pbLNEiVEsN2bSjT/eRyJ0eJAGEN/pZL2naBl2SD6IJGFAkEAwd7TW2PsDiP5BvK29oP/Bmj2BJoOfoVFSKGOWp0tsWGpnNXMkKnhc+TBRYt8uDIiG7xUZHjqGcoloXG8sZHa3wJAM7NYuZLHnsvEwdXSEjnhy4kNwOGDZCttHuW2b5DwP/bgn5rWClthUuL/zw9hBZv+De+bShH2tpzEog3DHiLdIQJARz0sCT8UdLW/QlBZ7wfd5GsZS+OYQ+GQ7fl/p8huC+pJOzpM6zOhhsaeRoHVGJJDelGf6TONQmDHpyV2IwHnnQJBAKuwB/xw+Qvh5u2OPy6k8M/fwygPAXyfQ89AOdr8OGuwFiDtcgwGQfyGMMkvxauN8zt+YWnWtFXdvGA9EFk0cy0=";
    // 银通支付（RSA）公钥(正式用)
    public static final String RSA_YT_PUBLIC ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmRtKAj1GQ+EwNFSSi7iVFwP91p+b+HO0HTRI3W5/uOOO2El5cvks0pc+VCzKOexHAPiCRBwQSFv3ltKXXlVVzG/z24Kp07X1CpX8mtHjCtpESwOnXRiPxSpUOwcoAQ0biq9LSpDBlybbVTHPbRhkf67OYS72ipNcwllB0/58E2wIDAQAB";
    //用户密码加密使用
    public static final String RSA_PASSWORD_ENCRYPT_PUBLIC ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSS/DiwdCf/aZsxxcacDnooGph3d2JOj5GXWi+q3gznZauZjkNP8SKl3J2liP0O6rU/Y/29+IUe+GTMhMOFJuZm1htAtKiu5ekW0GlBMWxf4FPkYlQkPE0FtaoMP3gYfh+OwI+fIRrpW3ySn3mScnc6Z700nU/VYrRkfcSCbSnRwIDAQAB";



    //签名方式
    public static final String SIGN_TYPE_RSA  ="RSA";
    public static final String SIGN_TYPE_MD5 = "MD5";

    /**
     * TODO 商户号，商户MD5 key 配置。本测试Demo里的“PARTNER”；强烈建议将私钥配置到服务器上，以免泄露。“MD5_KEY”字段均为测试字段。正式接入需要填写商户自己的字段
     */

    //用户所属商户号测试用
    public static final String OID_PARTNER_TEST  ="201606221000921503";
    public static final String PARTNER_PREAUTH = "201606221000921503"; // 短信
    public static final String MD5_KEY_PREAUTH = "201504071000272504_test_20150417";
    public static final String PARTNER = "201606221000921503";
    public static final String MD5_KEY = "201408071000001546_test_20140815";
    // 商户（RSA）私钥 TODO 强烈建议将私钥配置到服务器上，否则有安全隐患(测试用)
//    public static final String RSA_PRIVATE ="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKZGXpmfgya2gGh6UdFPqPqi6e2z/HX4aIlMH394FOXTVwErnSGY5S0YFw5WskJrQLU2RHwFiA5P9Yt8VPxwgLDpdIm1/a7NlyjvWNvBd8V7wyITH8teJA1Ae5yWmRRrWFcVRSjpBq3xfwv76lVl+Nq/jR08p/ugVYJgtYEIM53JAgMBAAECgYA17SarPj+j45a7y8gTUXmlaAbkb/ZWMG1+8fBZQBHPA/74wzNf/R1+xYxcuyNvRSekXehSLN0WfzpMtdM+WCJ0ODqHRFsbAxmi784hzBZHOAxoJV49P8PVy6HIPthXxiSNUcacSt/HKJrUI6zACpymJLiVxMb9GqQAyx3BJl7rjQJBANG+RDszZYl3J1z1AtD0WggycrH2YOB7v5o3qKOz2AQ6CHWApSN6cuvqFwaUtHK9gMpDhvWR6zbYVRP+f4AxoQ8CQQDK8fTkpHNrHc011E8jjk3Uq5PWTJ0jAvcqk4rqZa4eV9953YSJYtJ2Fk2JnL3Ba7AU+qStnyD6MvSIpwIPSaOnAkEAptbFaZ4Jn55jdmMC2Xn1f925NGx6RTbKg37Qq18sbrhG8Ejjk2QctCIiLL7vBvJM1xd97CslQhw1GNFxVGSl6wJAQzwFtfoFgudMpRjBXzY18s8lG0omhQLmf+SBkUY+eS8Diowo7Jsgvp6E8aJL+1iB7XFcPWkKs9lNyjgKJqZu4QJAM22ULfWKrNIqaBJaYDmQSupUkHR/WL5rQJtAuVo8Zg3+rBrtMTXfIHJpR0MNpMgRSsPK6pZ3n4i+VvC5WxKUzA==";
    // 银通支付（RSA）公钥(测试用)
//    public static final String RSA_YT_PUBLIC ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSS/DiwdCf/aZsxxcacDnooGph3d2JOj5GXWi+q3gznZauZjkNP8SKl3J2liP0O6rU/Y/29+IUe+GTMhMOFJuZm1htAtKiu5ekW0GlBMWxf4FPkYlQkPE0FtaoMP3gYfh+OwI+fIRrpW3ySn3mScnc6Z700nU/VYrRkfcSCbSnRwIDAQAB";

    //商户业务类型
    public static final String LL_BUSI_PARTNER_VIRTUAL = "101001";
    //商户版本号
    public static final String LL_VERSION_NO = "1.1";
    //支付方式 快捷支付（借记卡）
    public static final String LL_PAY_CARD = "2";
    //商户版本号（认证支付）
    public static final String LL_PAY_ = "D";



    //连连支付url
    //钱包下发短信接口
    public static final String LL_Pay_SMSSEND ="https://wallet.lianlianpay.com/llwalletapi/smssend.htm";
    //钱包短信验证接口
    public static final String LL_Pay_SMSCHECK ="https://wallet.lianlianpay.com/llwalletapi/smscheck.htm";
    //钱包开户接口（通过短信验证）
    public static final String LL_Pay_OPERUSER ="https://wallet.lianlianpay.com/llwalletapi/openuser.htm";
    //钱包用户查询接口
    public static final String LL_Pay_SINGLEUSERQUERY ="https://wallet.lianlianpay.com/llwalletapi/singleuserquery.htm";
    //钱包用户查询接口
    public static final String LL_Pay_USERBANKCARD ="https://wallet.lianlianpay.com/llwalletapi/userbankcard.htm";
    //银行卡帮卡认证接口
    public static final String LL_Pay_BANKCARDOPENAUTH ="https://wallet.lianlianpay.com/llwalletapi/bankcardopenauth.htm";
    //银行卡帮卡认证接口
    public static final String LL_Pay_BANKCARDAUTHVERFY ="https://wallet.lianlianpay.com/llwalletapi/bankcardauthverfy.htm";
    //用户订单查询
    public static final String LL_Pay_USERPAYMENT ="https://wallet.lianlianpay.com/llwalletapi/userpayment.htm";
    //解绑银行卡
    public static final String LL_Pay_BANKCARDUNBIND ="https://wallet.lianlianpay.com/llwalletapi/bankcardunbind.htm";
    //找回支付密码银行卡
    public static final String LL_Pay_BANKPAYPWDSET ="https://wallet.lianlianpay.com/llwalletapi/bankpaypwdset.htm";
    //找回支付密码验证银行卡
    public static final String LL_Pay_PAYPWDVERIFY ="https://wallet.lianlianpay.com/llwalletapi/paypwdverify.htm";
    //找回支付密码验证银行卡
    public static final String LL_Pay_PAYPWDSET ="https://wallet.lianlianpay.com/llwalletapi/paypwdset.htm";
    //用户实名认证，和钱包开户的实名认证是不同的
    public static final String LL_Pay_AUTHUSER ="https://wallet.lianlianpay.com/llwalletapi/authuser.htm";
    //钱包签约支付预处理接口
    public static final String LL_Pay_BANKCARDPREPAY ="https://wallet.lianlianpay.com/llwalletapi/bankcardprepay.htm";
    //4.2 钱包银行卡签约支付验证
    public static final String LL_Pay_BANKCARDEPAY ="https://wallet.lianlianpay.com/llwalletapi/bankcardpay.htm";
    //修改支付密码
    public static final String LL_Pay_PAYPWDMODIFY ="https://wallet.lianlianpay.com/llwalletapi/paypwdmodify.htm";
    //商户付款 B2C（所属商户号）
    public static final String LL_Pay_TRADERPAYMENT ="https://wallet.lianlianpay.com/llwalletapi/traderpayment.htm";
    //从钱包到银行卡提现
    public static final String LL_Pay_CASHOUTCOMBINEAPPLY ="https://wallet.lianlianpay.com/llwalletapi/cashoutcombineapply.htm";
    //6.4 钱包支付密码验证授权接口
    public static final String LL_Pay_PWDAUTH ="https://wallet.lianlianpay.com/llwalletapi/pwdauth.htm";
    //6.9 用户绑定手机修改接口
    public static final String LL_Pay_MODIFYUSERMOB ="https://wallet.lianlianpay.com/llwalletapi/modifyusermob.htm";



    // 微信平台
    // appid
    public static final String APP_ID = "wxcac78bb693d4095e";
    public static final String AppSecret = "cdb0763910898e009c1f4f6660259115";

    //网络连接异常
    public static final int CONNECT_ERROR = -101;
    //版本控制的地址
    public static final String CHECK_UPDATE_URL="http://app.woosii.com/APP/version.xml";

    //登录
    public static final String LOGIN_URL = ConfigInfo.ApiUrl+"/index.php/Api/User/phone_login?phone=";
    //注册(新)
    public static final String REGISTER_URL = ConfigInfo.ApiUrl+"/index.php/Api/User/new_register?type=";
    //微信绑定手机号
    public static final String WXBIND_URL = ConfigInfo.ApiUrl+"index.php/Api/User/binding_phone?user_id=";
    //忘记密码
    public static final String FORGET_PASSWORD_URL = ConfigInfo.ApiUrl + "/index.php/Api/User/forget?phone=";
    //验证码
    public static final String CAPTCHA_URL = ConfigInfo.ApiUrl+"/index.php/Api/User/getsmscode?phone=";
    //woosii协议地址
    public static final String WOOSII_AGREEMENT_URL = ConfigInfo.ApiUrl+"index.php/api/Question?classify_id=5";
    //woosii下载
    public static final String WOOSII_DOWNLOAD_URL = ConfigInfo.ApiUrl+"/index.php/Api/downx/share?user_id=";
    //VR轮播图
    public static final String NETWORK_CYCLE_PHOTO = ConfigInfo.ApiUrl+"/index.php/Api/AdIndex/vr_index";
    //问题反馈
    public static final String ANSWER_FEEDBACK = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/feedback?";
//    public static final String ANSWER_FEEDBACK = ConfigInfo.ApiUrl+"/index.php/Api/CourseManger/feedback?";
    //答案
    public static final String ANSWER_URL = ConfigInfo.ApiUrl+"/index.php/Api/Program/answer";
    //营销模块视频地址
    public static final String VR_VIDEO_URL = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/smallwo_video?pindex=1&psize=10";
    //中小学界面头部显示的课程科目
    public static final String SCHOOL_COURSE_URL = ConfigInfo.ApiUrl+ "/index.php/Vr/Vlive/type?";

    //LOG
    public static final String TAG = "WS log";
    public static final boolean YES_UID = true;
    public static final boolean NO_UID = false;
    public static final boolean YES_CACHE = true;
    public static final boolean NO_CACHE = false;
    public static final boolean YES_COOKIE = true;
    public static final boolean NO_COOKIE = false;


    public static final int[] BANK_CODE_ICON = {R.mipmap.b_01000000,R.mipmap.b_01020000,R.mipmap.b_01030000,R.mipmap.b_01040000,R.mipmap.b_01050000,R.mipmap.b_03010000,R.mipmap.b_03020000,R.mipmap.b_03040000,R.mipmap.b_03050000,R.mipmap.b_03070000,R.mipmap.b_03080000,R.mipmap.b_03090000,R.mipmap.b_03100000,R.mipmap.b_04012900,R.mipmap.b_04031000,R.mipmap.b_63030000};
    public static final String[] BANK_CODE_ICON_STRING = {"R.mipmap.b_01000000","R.mipmap.b_01020000","R.mipmap.b_01030000","R.mipmap.b_01040000","R.mipmap.b_01050000","R.mipmap.b_03010000","R.mipmap.b_03020000","R.mipmap.b_03040000","R.mipmap.b_03050000","R.mipmap.b_03070000","R.mipmap.b_03080000","R.mipmap.b_03090000","R.mipmap.b_03100000","R.mipmap.b_04012900","R.mipmap.b_04031000","R.mipmap.b_63030000"};


    public static final String LiveAppKey = "5db3cc39a09c4873b1b0ea4dd8f30135";

}
