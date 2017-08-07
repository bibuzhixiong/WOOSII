package sochat.so.com.android.llpay_util;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Administrator on 2017/5/19.
 */

public class RiskParameterUtil {
    private TelephonyManager tm;
    public RiskParameterUtil(Activity activity) {
         tm = (TelephonyManager) activity.getSystemService(TELEPHONY_SERVICE);
    }



    /**
     * 获取网关IP地址
     */
    public static String getIpAddr(){
        Enumeration<NetworkInterface> en = null;
        try {
            en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()){
                NetworkInterface net = en.nextElement();
                Enumeration<InetAddress> ipAddrs = net.getInetAddresses();
                while (ipAddrs.hasMoreElements()){
                    InetAddress inetAddr = ipAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress() && !(inetAddr instanceof Inet6Address)){
                        return inetAddr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 获取国际移动设备身份码
     *
     * @return
     */
    public static String getPhoneIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        if (TextUtils.isEmpty(imei)){
            return "";
        }else {
            return imei;
        }
    }

    /**
     * 获取手机设置唯一号， 无SIM卡，无网络，WIFI及GPRS环境，模拟器均测试通过
     *
     * @return
     */
    public static String getDeviceMachineId(Context context) {
        String tmDevice = getPhoneIMEI(context) + getSimCardIMSI(context);
        String mac = getMacAddress(context);
        UUID deviceUuid = new UUID(tmDevice.hashCode(), ((long) mac.hashCode() << 32) | mac.hashCode());
        String uniqueId = deviceUuid.toString();
        if (TextUtils.isEmpty(uniqueId)){
            uniqueId = UUID.randomUUID().toString();;
        }
        return uniqueId.replace("-", "");  //应服务器端要求，去掉“-”
    }
    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
            return info.getMacAddress();
        }
        return "";
    }

    /**
     * 获取国际移动用户识别码 储存在SIM卡中，可用于区别移动用户的有效信息 IMSI共有15位，其结构如下：MCC+MNC+MIN
     *
     * @return
     */
    public static String getSimCardIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String sid = telephonyManager.getSubscriberId();
        if (sid == null) {
            return "";
        }
        return sid;
    }




    /*
   * 电话状态：
   * 1.tm.CALL_STATE_IDLE=0          无活动
   * 2.tm.CALL_STATE_RINGING=1  响铃
   * 3.tm.CALL_STATE_OFFHOOK=2  摘机
   */

    public int getCallState(){
        return tm.getCallState();
    }

  /*
   * 电话方位：
   *
   */
  public CellLocation getCellLocation(){
        return  tm.getCellLocation();//CellLocation
    }


  /*
   * 唯一的设备ID：
   * GSM手机的 IMEI 和 CDMA手机的 MEID.
   * Return null if device ID is not available.
   */
  public String getDeviceId(){
      return tm.getDeviceId();//String
  }


  /*
   * 设备的软件版本号：
   * 例如：the IMEI/SV(software version) for GSM phones.
   * Return null if the software version is not available.
   */
  public String getDeviceSoftwareVersion(){
      return tm.getDeviceSoftwareVersion();//String
  }

  /*
   * 手机号：
   * GSM手机的 MSISDN.
   * Return null if it is unavailable.
   */
  public String getLine1Number(){
      return tm.getLine1Number();//String
  }

  /*
   * 附近的电话的信息:
   * 类型：List<NeighboringCellInfo>
   * 需要权限：android.Manifest.permission#ACCESS_COARSE_UPDATES
   */
  public List<NeighboringCellInfo> getNeighboringCellInfo(){
      return tm.getNeighboringCellInfo();
  }

  /*
   * 获取ISO标准的国家码，即国际长途区号。
   * 注意：仅当用户已在网络注册后有效。
   *       在CDMA网络中结果也许不可靠。
   */
  public String getNetworkCountryIso(){
      return tm.getNetworkCountryIso();//String
  }

  /*
   * MCC+MNC(mobile country code + mobile network code)
   * 注意：仅当用户已在网络注册时有效。
   *    在CDMA网络中结果也许不可靠。
   */
  public String getNetworkOperator(){
      return tm.getNetworkOperator();//String
  }

  /*
   * 按照字母次序的current registered operator(当前已注册的用户)的名字
   * 注意：仅当用户已在网络注册时有效。
   *    在CDMA网络中结果也许不可靠。
   */
  public String getNetworkOperatorName(){
      return tm.getNetworkOperatorName();//String
  }

  /*
   * 当前使用的网络类型：
   * 例如： NETWORK_TYPE_UNKNOWN  网络类型未知  0
     NETWORK_TYPE_GPRS     GPRS网络  1
     NETWORK_TYPE_EDGE     EDGE网络  2
     NETWORK_TYPE_UMTS     UMTS网络  3
     NETWORK_TYPE_HSDPA    HSDPA网络  8
     NETWORK_TYPE_HSUPA    HSUPA网络  9
     NETWORK_TYPE_HSPA     HSPA网络  10
     NETWORK_TYPE_CDMA     CDMA网络,IS95A 或 IS95B.  4
     NETWORK_TYPE_EVDO_0   EVDO网络, revision 0.  5
     NETWORK_TYPE_EVDO_A   EVDO网络, revision A.  6
     NETWORK_TYPE_1xRTT    1xRTT网络  7
   */
  public int getNetworkType(){
      return tm.getNetworkType();//String
  }

  /*
   * 手机类型：
   * 例如： PHONE_TYPE_NONE  无信号
     PHONE_TYPE_GSM   GSM信号
     PHONE_TYPE_CDMA  CDMA信号
   */
  public int getPhoneType(){
      return tm.getPhoneType();
  }

  /*
   * Returns the ISO country code equivalent for the SIM provider's country code.
   * 获取ISO国家码，相当于提供SIM卡的国家码。
   *
   */
  public String getSimCountryIso(){
      return tm.getSimCountryIso();//String
  }

  /*
   * Returns the MCC+MNC (mobile country code + mobile network code) of the provider of the SIM. 5 or 6 decimal digits.
   * 获取SIM卡提供的移动国家码和移动网络码.5或6位的十进制数字.
   * SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
   */
  public String getSimOperator(){
      return tm.getSimOperator();//String
  }

  /*
   * 服务商名称：
   * 例如：中国移动、联通
   * SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
   */
  public String getSimOperatorName(){
      return tm.getSimOperatorName();//String
  }

  /*
   * SIM卡的序列号：
   * 需要权限：READ_PHONE_STATE
   */
  public String getSimSerialNumber(){
      return tm.getSimSerialNumber();//String
  }

  /*
   * SIM的状态信息：
   *  SIM_STATE_UNKNOWN          未知状态 0
   SIM_STATE_ABSENT           没插卡 1
   SIM_STATE_PIN_REQUIRED     锁定状态，需要用户的PIN码解锁 2
   SIM_STATE_PUK_REQUIRED     锁定状态，需要用户的PUK码解锁 3
   SIM_STATE_NETWORK_LOCKED   锁定状态，需要网络的PIN码解锁 4
   SIM_STATE_READY            就绪状态 5
   */
  public int getSimState(){
      return tm.getSimState();//String
  }

  /*
   * 唯一的用户ID：
   * 例如：IMSI(国际移动用户识别码) for a GSM phone.
   * 需要权限：READ_PHONE_STATE
   */
  public String getSubscriberId(){
      return tm.getSubscriberId();//String
  }

  /*
   * 取得和语音邮件相关的标签，即为识别符
   * 需要权限：READ_PHONE_STATE
   */
  public String getVoiceMailAlphaTag(){
      return tm.getVoiceMailAlphaTag();//String
  }

  /*
   * 获取语音邮件号码：
   * 需要权限：READ_PHONE_STATE
   */
  public String getVoiceMailNumber(){
      return tm.getVoiceMailNumber();//String
  }

  /*
   * ICC卡是否存在
   */
  public boolean hasIccCard(){
      return tm.hasIccCard();
  }

  /*
   * 是否漫游:
   * (在GSM用途下)
   */
  public boolean isNetworkRoaming(){
      return tm.isNetworkRoaming();//String
  }

}
