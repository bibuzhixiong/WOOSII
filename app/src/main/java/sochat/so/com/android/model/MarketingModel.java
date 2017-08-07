package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/2/27.
 */

public class MarketingModel {
    private String name;
    private String content;
    private String coverUrl;

    public MarketingModel(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public MarketingModel(String name, String content, String coverUrl) {
        this.name = name;
        this.content = content;
        this.coverUrl = coverUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
