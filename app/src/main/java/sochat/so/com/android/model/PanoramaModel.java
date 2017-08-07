package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/7/12.
 */

public class PanoramaModel {
    private String image_url;
    private String addr_url;
    private String panorama_name;
    private String panorama_info;
    private String index;
    private String click;

    private String poetry;
    private String author;
    private String detailed;

    @Override
    public String toString() {
        return "PanoramaModel{" +
                "image_url='" + image_url + '\'' +
                ", addr_url='" + addr_url + '\'' +
                ", panorama_name='" + panorama_name + '\'' +
                ", panorama_info='" + panorama_info + '\'' +
                ", index='" + index + '\'' +
                ", click='" + click + '\'' +
                ", poetry='" + poetry + '\'' +
                ", author='" + author + '\'' +
                ", detailed='" + detailed + '\'' +
                '}';
    }

    public String getPoetry() {
        return poetry;
    }

    public void setPoetry(String poetry) {
        this.poetry = poetry;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDetailed() {
        return detailed;
    }

    public void setDetailed(String detailed) {
        this.detailed = detailed;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getAddr_url() {
        return addr_url;
    }

    public void setAddr_url(String addr_url) {
        this.addr_url = addr_url;
    }

    public String getPanorama_name() {
        return panorama_name;
    }

    public void setPanorama_name(String panorama_name) {
        this.panorama_name = panorama_name;
    }

    public String getPanorama_info() {
        return panorama_info;
    }

    public void setPanorama_info(String panorama_info) {
        this.panorama_info = panorama_info;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public PanoramaModel() {

    }

    public PanoramaModel(String image_url, String addr_url, String panorama_name, String panorama_info, String index, String click) {

        this.image_url = image_url;
        this.addr_url = addr_url;
        this.panorama_name = panorama_name;
        this.panorama_info = panorama_info;
        this.index = index;
        this.click = click;
    }
}
