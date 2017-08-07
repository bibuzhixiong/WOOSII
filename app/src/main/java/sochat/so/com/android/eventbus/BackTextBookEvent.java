package sochat.so.com.android.eventbus;

/**
 * Created by Administrator on 2017/4/26.
 */

public class BackTextBookEvent {
    private String textbook;
    private String textbook_id;

    public BackTextBookEvent(String textbook, String textbook_id){
        this.textbook = textbook;
        this.textbook_id =textbook_id;
    }

    public String getTextBook(){
        return textbook;
    }
    public String getTextBook_id(){
        return textbook;
    }
}
