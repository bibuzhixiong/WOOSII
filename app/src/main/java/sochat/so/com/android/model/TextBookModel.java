package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/4/25.
 */

public class TextBookModel {

    private String v_id;
    private String v_name;

    @Override
    public String toString() {
        return "TextBookModel{" +
                "v_id='" + v_id + '\'' +
                ", v_name='" + v_name + '\'' +
                '}';
    }

    public String getV_id() {
        return v_id;
    }

    public void setV_id(String v_id) {
        this.v_id = v_id;
    }

    public String getV_name() {
        return v_name;
    }

    public void setV_name(String v_name) {
        this.v_name = v_name;
    }

    public TextBookModel(String v_id, String v_name) {

        this.v_id = v_id;
        this.v_name = v_name;
    }
}
