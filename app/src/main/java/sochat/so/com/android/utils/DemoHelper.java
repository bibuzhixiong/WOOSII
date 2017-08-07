package sochat.so.com.android.utils;

/**
 * Created by Administrator on 2017/2/20.
 * 项目工具类
 */

public class DemoHelper {
    /**
     * 第一次安装
     */
    public static void setFristInstall(boolean firstInstall){
        SharedPreferencesMgr.setBoolean("WelcomeActivity_firstInstall", firstInstall);
    }
    public static boolean getFristInstall(){
        return SharedPreferencesMgr.getBoolean("WelcomeActivity_firstInstall", false);
    }
    /**
     * 第一次跳转
     */
    public static void setFristStart(int frist_start){
        SharedPreferencesMgr.setInt("frist_start", frist_start);
    }

    public static int getFristStart(){
        return SharedPreferencesMgr.getInt("frist_start",0);
    }
    /**
     * 记住用户名
     */
    public static void setRemenberPasswordUsername(String username){
        SharedPreferencesMgr.setString("login_RemenberPasswordUsername", username);
    }
    public static String getRemenberPasswordUsername(){
        return SharedPreferencesMgr.getString("login_RemenberPasswordUsername", "");
    }
    /**
     * 记住密码
     */

    public static void setRemenberPasswordPassword(String password){
        SharedPreferencesMgr.setString("login_RemenberPasswordPassword", password);
    }
    public static String getRemenberPasswordPassword(){
        return SharedPreferencesMgr.getString("login_RemenberPasswordPassword", "");
    }

    /**
     * 判断之前是否点击了记住了密码
     */
    public static void setRemenberPassword(Boolean RemenberPassword) {
        SharedPreferencesMgr.setBoolean("RemenberPassword", RemenberPassword);
    }

    public static boolean getRemenberPassword() {
        return SharedPreferencesMgr.getBoolean("RemenberPassword", false);
    }

    /**
     * 第一次登录后默认同意Woosii协议
     */
    public static boolean getDefaultAgreeAgreement(){
        return SharedPreferencesMgr.getBoolean("DefaultAgreeAgreement", false);
    }
    public static void setDefaultAgreeAgreement(boolean isDefaultAgreeAgreement){
        SharedPreferencesMgr.setBoolean("DefaultAgreeAgreement", isDefaultAgreeAgreement);
    }

    /**
     * Uid
     */
    public static void setUid(String Uid){
        SharedPreferencesMgr.setString("Uid", Uid);
    }

    public static String getUid(){
        return SharedPreferencesMgr.getString("Uid", "");
    }

    /**
     * 中小学界面的title，总数据
     */
    public static void setCourseTitle(String CourseTitle){
        SharedPreferencesMgr.setString("CourseTitle", CourseTitle);
    }

    public static String getCourseTitle(){
        return SharedPreferencesMgr.getString("CourseTitle", "");
    }
    /**
     * 中小学界面的title,显示的数据
     */
    public static void setCourseTitleShow(String CourseTitleShow){
        SharedPreferencesMgr.setString("CourseTitleShow", CourseTitleShow);
    }

    public static String getCourseTitleShow(){
        return SharedPreferencesMgr.getString("CourseTitleShow", "");
    }
    /**
     * 用户的身份
     */
    public static void setUserType(String UserType){
        SharedPreferencesMgr.setString("UserType", UserType);
    }

    public static String getUserType(){
        return SharedPreferencesMgr.getString("UserType", "0");
    }
    /**
     * 环信用户名
     */
    public static void setHXusername(String HXusername){
        SharedPreferencesMgr.setString("HXusername", HXusername);
    }

    public static String getHXusername(){
        return SharedPreferencesMgr.getString("HXusername", "");
    }
    /**
     * 环信密码
     */
    public static void setHXpassword(String HXpassword){
        SharedPreferencesMgr.setString("HXpassword", HXpassword);
    }

    public static String getHXpassword(){
        return SharedPreferencesMgr.getString("HXpassword", "");
    }

    /**
     * 微信登录头像url
     */
    public static void setWXThumbUrl(String WXThumbUrl){
        SharedPreferencesMgr.setString("WXThumbUrl", WXThumbUrl);
    }

    public static String getWXThumbUrl(){
        return SharedPreferencesMgr.getString("WXThumbUrl", "");
    }
    /**
     * 微信登录名字
     */
    public static void setWXNickName(String WXNickName){
        SharedPreferencesMgr.setString("WXNickName", WXNickName);
    }

    public static String getWXNickName(){
        return SharedPreferencesMgr.getString("WXNickName", "");
    }
    /**
     * 微信来源
     */
    public static void setWXSource(String WXSource){
        SharedPreferencesMgr.setString("WXSource", WXSource);
    }

    public static String getWXSource(){
        return SharedPreferencesMgr.getString("WXSource", "");
    }
    /**
     * 微信Aite_id
     */
    public static void setWXAite_id(String WXAite_id){
        SharedPreferencesMgr.setString("WXAite_id", WXAite_id);
    }

    public static String getWXAite_id(){
        return SharedPreferencesMgr.getString("WXAite_id", "");
    }
    /**
     * 微信Open_id
     */
    public static void setWXOpen_id(String WXOpen_id){
        SharedPreferencesMgr.setString("WXOpen_id", WXOpen_id);
    }

    public static String getWXOpen_id(){
        return SharedPreferencesMgr.getString("WXOpen_id", "");
    }



    /**
     * 上传头像图片的地址
     */
    public static void setThumb(String Thumb){
        SharedPreferencesMgr.setString("Thumb", Thumb);
    }

    public static String getThumb(){
        return SharedPreferencesMgr.getString("Thumb", "");
    }

    /**
     * 昵称
     */
    public static void setNickName(String NickName){
        SharedPreferencesMgr.setString("NickName", NickName);
    }

    public static String getNickName(){
        return SharedPreferencesMgr.getString("NickName", "");
    }
    /**
     * 真实姓名
     */
    public static void setRealName(String RealName){
        SharedPreferencesMgr.setString("RealName", RealName);
    }

    public static String getRealName(){
        return SharedPreferencesMgr.getString("RealName", "");
    }

    /**
     * 电话
     */
    public static void setTel(String Tel){
        SharedPreferencesMgr.setString("Tel", Tel);
    }

    public static String getTel(){
        return SharedPreferencesMgr.getString("Tel", "");
    }

    /**
     * 性别
     */
    public static void setGender(String Gender){
        SharedPreferencesMgr.setString("Gender", Gender);
    }

    public static String getGender(){
        return SharedPreferencesMgr.getString("Gender", "");
    }

    /**
     * 个人简介
     */
    public static void setDetail(String Detail){
        SharedPreferencesMgr.setString("Detail", Detail);
    }

    public static String getDetail(){
        return SharedPreferencesMgr.getString("Detail", "");
    }

    /**
     * 手机号
     */
    public static void setPhone(String Phone){
        SharedPreferencesMgr.setString("Phone", Phone);
    }

    public static String getPhone(){
        return SharedPreferencesMgr.getString("Phone", "");
    }

    /**
     * 注册时的地区
     */
    public static void setArea(String Area){
        SharedPreferencesMgr.setString("Area", Area);
    }

    public static String getArea(){
        return SharedPreferencesMgr.getString("Area", "全国");
    }

    /**
     * 注册时的地区id
     */
    public static void setRegion_id(String Region_id){
        SharedPreferencesMgr.setString("Region_id", Region_id);
    }
    public static String getRegion_id(){
        return SharedPreferencesMgr.getString("Region_id", "0");
    }

    /**
     * 注册时的地区
     */
    public static void setVedioArea(String VedioArea){
        SharedPreferencesMgr.setString("VedioArea", VedioArea);
    }

    public static String getVedioArea(){
        return SharedPreferencesMgr.getString("VedioArea", "全国");
    }


    /**
     * 用户选择观看视频的地区
     */
    public static void setVedioRegion_id(String VedioRegion_id){
        SharedPreferencesMgr.setString("VedioRegion_id", VedioRegion_id);
    }
    public static String getVedioRegion_id(){
        return SharedPreferencesMgr.getString("VedioRegion_id", "0");
    }


    /**
     * 教材版本
     */
    public static void setTextBook(String TextBook){
        SharedPreferencesMgr.setString("TextBook", TextBook);
    }
    public static String getTextBook(){
        return SharedPreferencesMgr.getString("TextBook", "人教版");
    }

    public static void setTextBook_id(String TextBook_id){
        SharedPreferencesMgr.setString("TextBook_id", TextBook_id);
    }
    public static String getTextBook_id(){
        return SharedPreferencesMgr.getString("TextBook_id", "1");
    }

    /**
     * 存放搜索历史记录
     */
    public static void setSearchHistory(String SearchHistory){
        SharedPreferencesMgr.setString("SearchHistory", SearchHistory);
    }
    public static String getSearchHistory(){
        return SharedPreferencesMgr.getString("SearchHistory", "");
    }


    /**
     * 存放课外课Fragment显示的标题
     */
    public static void setTitle(String Title){
        SharedPreferencesMgr.setString("Title", Title);
    }
    public static String getTitle(){
        return SharedPreferencesMgr.getString("Title", "0");
    }


    /**
     * 存放课外课Fragment显示的标题
     */
    public static void setUserAccounte(String UserAccount){
        SharedPreferencesMgr.setString("UserAccount", UserAccount);
    }
    public static String getUserAccount(){
        return SharedPreferencesMgr.getString("UserAccount", "0");
    }


    /**
     * 存放课外课Fragment显示的标题
     */
    public static void setUserToken(String UserToken){
        SharedPreferencesMgr.setString("UserToken", UserToken);
    }
    public static String getUserToken(){
        return SharedPreferencesMgr.getString("UserToken", "0");
    }

    /**
     * 存放课外课Fragment显示的标题
     */
    public static void setHonor(String Honor){
        SharedPreferencesMgr.setString("Honor", Honor);
    }
    public static String getHonor(){
        return SharedPreferencesMgr.getString("Honor", "0");
    }

    /**
     * 存放校区的id
     */
    public static void setSchool_id(String School_id){
        SharedPreferencesMgr.setString("School_id", School_id);
    }
    public static String getSchool_id(){
        return SharedPreferencesMgr.getString("School_id", "");
    }

    /**
     * 存放用于分享的地址
     */
    public static void setLive_id(String Live_id){
        SharedPreferencesMgr.setString("Live_id", Live_id);
    }
    public static String getLive_id(){
        return SharedPreferencesMgr.getString("Live_id", "");
    }



    /**
     * 存放搜索历史记录
     */
    public static void setSchool_code(int School_code){
        SharedPreferencesMgr.setInt("School_code", School_code);
    }
    public static int getSchool_code(){
        return SharedPreferencesMgr.getInt("School_code", 0);
    }








    /**
     * 存放搜索历史记录
     */
    public static void setOne(int One){
        SharedPreferencesMgr.setInt("One", One);
    }
    public static int getOne(){
        return SharedPreferencesMgr.getInt("One", 0);
    }


    /**
     * 清空本地数据
     */
    public static void clear() {
        setUid("");
        setWXNickName("");
        setWXThumbUrl("");
        setHXpassword("");
        setHXusername("");
        setThumb("");
        setNickName("");
        setGender("");
        setDetail("");
        setTel("");
        setArea("全国");
        setRegion_id("0");
        setWXAite_id("");
        setWXSource("");
        setWXOpen_id("");
        setPhone("");
        setUserType("0");
        setTextBook("人教版");
        setTextBook_id("1");
        setSearchHistory("");
        setOne(0);
//        setCourseTitleShow("");
//        setCourseTitle("");
        setTitle("");
        setVedioArea("全国");
        setVedioRegion_id("0");
        setHonor("0");
        setSchool_id("");
        setSchool_code(0);

    }
}


