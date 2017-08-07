package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/6/29.
 */

public class WoosiiEmployeeFoundTeacherResult {
    private String m_thumb;
    private String m_name;
    private String m_money;
    private String m_day_money;
    private String m_tel;
    private String m_region_id;
    private List<WoosiiEmployeeFoundTeacherModel> child;

    public WoosiiEmployeeFoundTeacherResult() {
    }

    @Override
    public String toString() {
        return "WoosiiEmployeeFoundTeacherResult{" +
                "m_thumb='" + m_thumb + '\'' +
                ", m_name='" + m_name + '\'' +
                ", m_money='" + m_money + '\'' +
                ", m_day_money='" + m_day_money + '\'' +
                ", m_tel='" + m_tel + '\'' +
                ", m_region_id='" + m_region_id + '\'' +
                ", child=" + child +
                '}';
    }

    public String getM_thumb() {
        return m_thumb;
    }

    public void setM_thumb(String m_thumb) {
        this.m_thumb = m_thumb;
    }

    public String getM_name() {
        return m_name;
    }

    public void setM_name(String m_name) {
        this.m_name = m_name;
    }

    public String getM_money() {
        return m_money;
    }

    public void setM_money(String m_money) {
        this.m_money = m_money;
    }

    public String getM_day_money() {
        return m_day_money;
    }

    public void setM_day_money(String m_day_money) {
        this.m_day_money = m_day_money;
    }

    public String getM_tel() {
        return m_tel;
    }

    public void setM_tel(String m_tel) {
        this.m_tel = m_tel;
    }

    public String getM_region_id() {
        return m_region_id;
    }

    public void setM_region_id(String m_region_id) {
        this.m_region_id = m_region_id;
    }

    public List<WoosiiEmployeeFoundTeacherModel> getChild() {
        return child;
    }

    public void setChild(List<WoosiiEmployeeFoundTeacherModel> child) {
        this.child = child;
    }
}
