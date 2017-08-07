package sochat.so.com.android.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/6.
 */
public class Child implements Serializable{
    private String cu_id;
    private String name;
    private String thumb;
    private String url;
    private String add_time;
    private String vr_type;
    private String info;
    private String nick_name;
    private String click_count;
    private String icon;
    private String comment;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getClick_count() {
        return click_count;
    }

    public void setClick_count(String click_count) {
        this.click_count = click_count;
    }

    public Child(String cu_id, String name, String thumb, String url, String add_time, String vr_type, String info, String nick_name, String click_count) {
        this.cu_id = cu_id;
        this.name = name;
        this.thumb = thumb;
        this.url = url;
        this.add_time = add_time;
        this.vr_type = vr_type;
        this.info = info;
        this.nick_name = nick_name;
        this.click_count = click_count;
    }

    public Child(String cu_id, String name, String thumb, String url, String add_time, String vr_type, String info, String nick_name, String click_count, String icon, String comment) {
        this.cu_id = cu_id;
        this.name = name;
        this.thumb = thumb;
        this.url = url;
        this.add_time = add_time;
        this.vr_type = vr_type;
        this.info = info;
        this.nick_name = nick_name;
        this.click_count = click_count;
        this.icon = icon;
        this.comment = comment;
    }

    public Child() {
    }

    public Child(String cu_id, String name, String thumb, String url, String add_time, String vr_type, String info) {
        this.cu_id = cu_id;
        this.name = name;
        this.thumb = thumb;
        this.url = url;
        this.add_time = add_time;
        this.vr_type = vr_type;
        this.info = info;
    }

    public String getCu_id() {
        return cu_id;
    }

    public void setCu_id(String cu_id) {
        this.cu_id = cu_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getVr_type() {
        return vr_type;
    }

    public void setVr_type(String vr_type) {
        this.vr_type = vr_type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Child{" +
                "cu_id='" + cu_id + '\'' +
                ", name='" + name + '\'' +
                ", thumb='" + thumb + '\'' +
                ", url='" + url + '\'' +
                ", add_time='" + add_time + '\'' +
                ", vr_type='" + vr_type + '\'' +
                ", info='" + info + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", click_count='" + click_count + '\'' +
                ", icon='" + icon + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
