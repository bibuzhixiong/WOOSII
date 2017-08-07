package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/8/5.
 */

public class LiveAttrentionModel {
    private int free;
    private int foll;
    private int person_count;
    private int state_id;
    private String name;
    private String thumb;
    private String url;
    private String vip;
    private String wobi;
    private String school_id;
    private String roomid;
    private String live_id;
    private String theme;

    public LiveAttrentionModel() {
    }

    @Override
    public String toString() {
        return "LiveAttrentionModel{" +
                "free=" + free +
                ", foll=" + foll +
                ", person_count=" + person_count +
                ", state_id=" + state_id +
                ", name='" + name + '\'' +
                ", thumb='" + thumb + '\'' +
                ", url='" + url + '\'' +
                ", vip='" + vip + '\'' +
                ", wobi='" + wobi + '\'' +
                ", school_id='" + school_id + '\'' +
                ", roomid='" + roomid + '\'' +
                ", live_id='" + live_id + '\'' +
                ", theme='" + theme + '\'' +
                '}';
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public int getFoll() {
        return foll;
    }

    public void setFoll(int foll) {
        this.foll = foll;
    }

    public int getPerson_count() {
        return person_count;
    }

    public void setPerson_count(int person_count) {
        this.person_count = person_count;
    }

    public int getState_id() {
        return state_id;
    }

    public void setState_id(int state_id) {
        this.state_id = state_id;
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

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getLive_id() {
        return live_id;
    }

    public void setLive_id(String live_id) {
        this.live_id = live_id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
