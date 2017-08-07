package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/4/25.
 */

public class MostNewModel {
    private int type;
    private String url;
    private String cu_id;
    private String v_name;
    private String v_thumb;
    private String click_count;
    private String vip;
    private String addtime;
    private String info;
    private String longtime;
    private String real_name;


    public MostNewModel() {
    }

    public String getReal_name() {
        return real_name;
    }

    @Override
    public String toString() {
        return "MostNewModel{" +
                "type=" + type +
                ", url='" + url + '\'' +
                ", cu_id='" + cu_id + '\'' +
                ", v_name='" + v_name + '\'' +
                ", v_thumb='" + v_thumb + '\'' +
                ", click_count='" + click_count + '\'' +
                ", vip='" + vip + '\'' +
                ", addtime='" + addtime + '\'' +
                ", info='" + info + '\'' +
                ", longtime='" + longtime + '\'' +
                ", real_name='" + real_name + '\'' +
                '}';
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCu_id() {
        return cu_id;
    }

    public void setCu_id(String cu_id) {
        this.cu_id = cu_id;
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

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
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

    public String getLongtime() {
        return longtime;
    }

    public void setLongtime(String longtime) {
        this.longtime = longtime;
    }
}
