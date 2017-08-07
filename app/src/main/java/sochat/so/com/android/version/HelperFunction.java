package sochat.so.com.android.version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("rawtypes")
public class HelperFunction {
	
	public static int getCid(JSONObject reserJson, boolean isConsumer) {
		if (isConsumer) {
			try {
				return Integer.parseInt(reserJson.getString("userId"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return 0;
			} catch (JSONException e) {
				e.printStackTrace();
				return 0;
			}
		} else {
			try {
				return Integer.parseInt(reserJson.getString("memberId"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return 0;
			} catch (JSONException e) {
				e.printStackTrace();
				return 0;
			}
		}
	}

	// map转换为json字符串
	public static String hashMapToJson(HashMap map) {
		String string = "{";
		string = string.substring(0, string.lastIndexOf(","));
		string += "}";
		return string;
	}

	public static void JsonObject2HashMap(JSONObject jo, List rstList) {
		for (Iterator keys = jo.keys(); keys.hasNext();) {
			try {
				String key1 = (String) keys.next();
				System.out.println("key1---" + key1 + "------" + jo.get(key1)
						+ (jo.get(key1) instanceof JSONObject) + jo.get(key1)
						+ (jo.get(key1) instanceof JSONArray));
				if (jo.get(key1) instanceof JSONObject) {

					JsonObject2HashMap((JSONObject) jo.get(key1), rstList);
					continue;
				}
				if (jo.get(key1) instanceof JSONArray) {
					JsonArray2HashMap((JSONArray) jo.get(key1), rstList);
					continue;
				}
				System.out.println("key1:" + key1 + "----------jo.get(key1):"
						+ jo.get(key1));
				json2HashMap(key1, jo.get(key1), rstList);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void JsonArray2HashMap(JSONArray joArr, List rstList) {
		for (int i = 0; i < joArr.length(); i++) {
			try {
				if (joArr.get(i) instanceof JSONObject) {

					JsonObject2HashMap((JSONObject) joArr.get(i), rstList);
					continue;
				}
				if (joArr.get(i) instanceof JSONArray) {

					JsonArray2HashMap((JSONArray) joArr.get(i), rstList);
					continue;
				}
				System.out.println("Excepton~~~~~");

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	@SuppressWarnings("unchecked")
	public static void json2HashMap(String key, Object value, List rstList) {
		HashMap map = new HashMap();
		map.put(key, value);
		rstList.add(map);
	}
}
