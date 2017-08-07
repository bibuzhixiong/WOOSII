package sochat.so.com.android.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/20.
 */

public class TeacherCourseList implements Serializable{
    private String url;
    private String v_name;
    private String v_thumb;
    private String click_count;
    private String cu_id;
    private String addtime;
    private String info;
    private int type;
    private int vip;
    private int longtime;
    private int free;
    private String real_name;

    private int vr ;

    @Override
    public String toString() {
        return "TeacherCourseList{" +
                "url='" + url + '\'' +
                ", v_name='" + v_name + '\'' +
                ", v_thumb='" + v_thumb + '\'' +
                ", click_count='" + click_count + '\'' +
                ", cu_id='" + cu_id + '\'' +
                ", addtime='" + addtime + '\'' +
                ", info='" + info + '\'' +
                ", type=" + type +
                ", vip=" + vip +
                ", longtime=" + longtime +
                ", free=" + free +
                ", real_name='" + real_name + '\'' +
                ", vr=" + vr +
                ", isSelect=" + isSelect +
                '}';
    }

    public int getVr() {
        return vr;
    }

    public void setVr(int vr) {
        this.vr = vr;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public int getLongtime() {
        return longtime;
    }

    public void setLongtime(int longtime) {
        this.longtime = longtime;
    }

    //标记是否被选中
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getV_name() {
        return v_name;
    }

    public void setV_name(String v_name) {
        this.v_name = v_name;
    }

    public String getV_thumb() {
        return v_thumb;
    }

    public void setV_thumb(String v_thumb) {
        this.v_thumb = v_thumb;
    }

    public String getClick_count() {
        return click_count;
    }

    public void setClick_count(String click_count) {
        this.click_count = click_count;
    }

    public String getCu_id() {
        return cu_id;
    }

    public void setCu_id(String cu_id) {
        this.cu_id = cu_id;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public TeacherCourseList() {

    }

    public TeacherCourseList(String url, String v_name, String v_thumb, String click_count, String cu_id, String addtime, String info, int type, int vip) {

        this.url = url;
        this.v_name = v_name;
        this.v_thumb = v_thumb;
        this.click_count = click_count;
        this.cu_id = cu_id;
        this.addtime = addtime;
        this.info = info;
        this.type = type;
        this.vip = vip;
    }
}
