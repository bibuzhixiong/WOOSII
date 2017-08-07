package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/2/25.
 */

public class VrModel {
    private String title;
    private String content;
    private String imgUrl;
    private int type;
    private String playUrl;

    public VrModel() {
    }

    public VrModel(String title, String content, String imgUrl) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
    }

    public VrModel(String title, String content, String imgUrl, int type, String playUrl) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.type = type;
        this.playUrl = playUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    @Override
    public String toString() {
        return "VrModel{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", type=" + type +
                ", playUrl='" + playUrl + '\'' +
                '}';
    }
}
