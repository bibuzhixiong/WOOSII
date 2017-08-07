package sochat.so.com.android.interface_method;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */

public interface ILLPayIDUploadView {
    List<File> getIDImageFile(File front, File back);
    void setIDImageFile();
}
