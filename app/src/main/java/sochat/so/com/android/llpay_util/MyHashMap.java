package sochat.so.com.android.llpay_util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/5/22.
 */

public class MyHashMap<K, V> extends HashMap<K, V> {
    private static final long serialVersionUID = -5894887960346129860L;
    // 重写HashMapSon类的toString()方法
    @Override
    public String toString() {
        Set<Entry<K, V>> keyset = this.entrySet();
        Iterator<Entry<K, V>> i = keyset.iterator();
        if (!i.hasNext())
            return "";
        StringBuffer buffer = new StringBuffer();
        // buffer.append("{");//注意此程序与源代码的区别
        buffer.append("{");
        for (;;) {
            Entry<K, V> me = i.next();
            K key = me.getKey();
            V value = me.getValue();
            buffer.append("\""+key.toString() +"\"");
            buffer.append(':');
            buffer.append("\""+value.toString() +"\"");
            if (! i.hasNext())
                return buffer.append('}').toString();
            buffer.append(',');
        }
    }


}
