package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/3/14.
 */

public class PlayVedioComment {
    private String thumb;
    private String name;
    private String addtime;
    private String content;
    private String user_id;


    public PlayVedioComment() {
    }

    @Override
    public String toString() {
        return "PlayVedioComment{" +
                "thumb='" + thumb + '\'' +
                ", name='" + name + '\'' +
                ", addtime='" + addtime + '\'' +
                ", content='" + content + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public PlayVedioComment(String thumb, String name, String addtime, String content, String user_id) {

        this.thumb = thumb;
        this.name = name;
        this.addtime = addtime;
        this.content = content;
        this.user_id = user_id;
    }
}
