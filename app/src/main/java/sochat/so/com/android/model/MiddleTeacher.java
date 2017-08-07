package sochat.so.com.android.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/13.
 */

public class MiddleTeacher implements Serializable {

    private String name;
    private String url;
    private String content;
    private String courseCount;
    private String attentionCount;
    private boolean isAttention;

    public MiddleTeacher() {
    }

    public MiddleTeacher(String name, String url, String content, String courseCount, String attentionCount, boolean isAttention) {
        this.name = name;
        this.url = url;
        this.content = content;
        this.courseCount = courseCount;
        this.attentionCount = attentionCount;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(String courseCount) {
        this.courseCount = courseCount;
    }

    public String getAttentionCount() {
        return attentionCount;
    }

    public void setAttentionCount(String attentionCount) {
        this.attentionCount = attentionCount;
    }

    public boolean isAttention() {
        return isAttention;
    }

    public void setAttention(boolean attention) {
        isAttention = attention;
    }

    @Override
    public String toString() {
        return "MiddleTeacher{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", content='" + content + '\'' +
                ", courseCount='" + courseCount + '\'' +
                ", attentionCount='" + attentionCount + '\'' +
                ", isAttention=" + isAttention +
                '}';
    }
}
