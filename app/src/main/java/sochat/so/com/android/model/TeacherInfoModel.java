package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/3/15.
 */

public class TeacherInfoModel {
    private String name;
    private String url;
    private String attentionCount;
    private String content;
    private String vedioCount;
    private String isAttention;

    public TeacherInfoModel() {
    }

    public TeacherInfoModel(String name, String url, String attentionCount, String content, String vedioCount, String isAttention) {

        this.name = name;
        this.url = url;
        this.attentionCount = attentionCount;
        this.content = content;
        this.vedioCount = vedioCount;
        this.isAttention = isAttention;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAttentionCount() {
        return attentionCount;
    }

    public void setAttentionCount(String attentionCount) {
        this.attentionCount = attentionCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVedioCount() {
        return vedioCount;
    }

    public void setVedioCount(String vedioCount) {
        this.vedioCount = vedioCount;
    }

    public String getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(String isAttention) {
        this.isAttention = isAttention;
    }

    @Override
    public String toString() {
        return "TeacherInfoModel{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", attentionCount='" + attentionCount + '\'' +
                ", content='" + content + '\'' +
                ", vedioCount='" + vedioCount + '\'' +
                ", isAttention='" + isAttention + '\'' +
                '}';
    }
}
