package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/3/28.
 */

public class CollectVedioModel {
    private String url;
    private String name;
    private String cu_id;
    private String thumb;

    //标记是否被选中
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "CollectVedioModel{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", cu_id='" + cu_id + '\'' +
                ", thumb='" + thumb + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCu_id() {
        return cu_id;
    }

    public void setCu_id(String cu_id) {
        this.cu_id = cu_id;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public CollectVedioModel() {

    }

    public CollectVedioModel(String url, String name, String cu_id, String thumb) {

        this.url = url;
        this.name = name;
        this.cu_id = cu_id;
        this.thumb = thumb;
    }
}
