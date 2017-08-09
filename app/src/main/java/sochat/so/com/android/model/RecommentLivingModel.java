package sochat.so.com.android.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/28.
 */

public class RecommentLivingModel implements Serializable{
    private String thumb;
    private int count;
    private String name;
    private String theme;
    private String url;
    private int roomid;
    private String live_id;
    private String school_id;
    private String school_name;
    private String master_id;
    private String schoo_info;
    private int is_college;
    private int is_myschool;
    private int is_power;
    private int usercount;

    private String vip;
    private String wobi;

    @Override
    public String toString() {
        return "RecommentLivingModel{" +
                "thumb='" + thumb + '\'' +
                ", count=" + count +
                ", name='" + name + '\'' +
                ", theme='" + theme + '\'' +
                ", url='" + url + '\'' +
                ", roomid=" + roomid +
                ", live_id='" + live_id + '\'' +
                ", school_id='" + school_id + '\'' +
                ", school_name='" + school_name + '\'' +
                ", master_id='" + master_id + '\'' +
                ", schoo_info='" + schoo_info + '\'' +
                ", is_college=" + is_college +
                ", is_myschool=" + is_myschool +
                ", is_power=" + is_power +
                ", usercount=" + usercount +
                ", vip='" + vip + '\'' +
                ", wobi='" + wobi + '\'' +
                ", foll=" + foll +
                '}';
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getWobi() {
        return wobi;
    }

    public void setWobi(String wobi) {
        this.wobi = wobi;
    }

    private int foll;

    public int getFoll() {
        return foll;
    }

    public void setFoll(int foll) {
        this.foll = foll;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public String getLive_id() {
        return live_id;
    }

    public void setLive_id(String live_id) {
        this.live_id = live_id;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getSchoo_info() {
        return schoo_info;
    }

    public void setSchoo_info(String schoo_info) {
        this.schoo_info = schoo_info;
    }

    public int getIs_college() {
        return is_college;
    }

    public void setIs_college(int is_college) {
        this.is_college = is_college;
    }

    public int getIs_myschool() {
        return is_myschool;
    }

    public void setIs_myschool(int is_myschool) {
        this.is_myschool = is_myschool;
    }

    public int getIs_power() {
        return is_power;
    }

    public void setIs_power(int is_power) {
        this.is_power = is_power;
    }

    public int getUsercount() {
        return usercount;
    }

    public void setUsercount(int usercount) {
        this.usercount = usercount;
    }

}
