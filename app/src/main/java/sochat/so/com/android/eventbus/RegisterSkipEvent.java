package sochat.so.com.android.eventbus;

/**
 * Created by Administrator on 2017/4/19.
 */

public class RegisterSkipEvent {
        private String username;
    private String password;

    public RegisterSkipEvent(){

    }
        public RegisterSkipEvent(String username,String password){
            this.username = username;
            this.password = password;
        }

        public String getUsername(){
            return username;
        }
        public String getPassword(){
            return password;
        }

}
