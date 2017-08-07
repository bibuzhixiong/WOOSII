package sochat.so.com.android.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sochat.so.com.android.model.CourseModel1;


/**
 * Created by Administrator on 2017/6/6.
 */

public class CourseTitleJson2Model1 {
    private static ArrayList<CourseModel1>titles= new ArrayList<>();

    public CourseTitleJson2Model1() {
    }


    public static ArrayList<CourseModel1> getCourseTitleArrayList1() {
        //记住在每次使用前要清空
        if (titles != null) {
            titles.clear();
        }

        try {
            JSONArray array = new JSONArray(DemoHelper.getCourseTitleShow());
            JSONObject jsonObject;
            for (int i = 0; i < array.length(); i++) {
                jsonObject = array.getJSONObject(i);
                titles.add(new CourseModel1(jsonObject.getString("s_id"), jsonObject.getString("s_name")));
            }

            return titles;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ArrayList<CourseModel1> getCourseTitleArrayList1(String jsonString){
        //记住在每次使用前要清空
        if (titles!=null){
            titles.clear();
        }

        try {
            JSONArray array = new JSONArray(jsonString);
            JSONObject jsonObject ;
            for (int i=0;i<array.length();i++){
                jsonObject = array.getJSONObject(i);
                titles.add(new CourseModel1(jsonObject.getString("s_id"),jsonObject.getString("s_name")));
            }

            return titles;

        } catch (JSONException e) {
            e.printStackTrace();
        }


//        //Json的解析类对象
//        JsonParser parser = new JsonParser();
//        //将JSON的String 转成一个JsonArray对象
//        JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
//        Gson gson = new Gson();
//        //加强for循环遍历JsonArray
//        for (JsonElement user : jsonArray) {
//            //使用GSON，直接转成Bean对象
//            CourseModel city = gson.fromJson(user, CourseModel.class);
//            titles.add(city);
//        }

        return null;
    }




    public static ArrayList<CourseModel1> setRemoveCourseTitleArrayList1(String title){
        try {
            boolean isHave = false;
            int position =0;
            JSONArray array = new JSONArray(DemoHelper.getCourseTitleShow());
            JSONObject jsonObject ;
            for(int i=0;i<array.length();i++){
                jsonObject = array.getJSONObject(i);
                if (title.equals(jsonObject.getString("s_name"))){
                    isHave = true;
                    position = i;
                }
            }

            if (isHave){
                array.remove(position);
                DemoHelper.setCourseTitleShow(array.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getCourseTitleArrayList1(DemoHelper.getCourseTitleShow());
    }
    public static ArrayList<CourseModel1> setAddCourseTitleArrayList1(String s_id,String s_name){
//        {"s_id":"1","s_name":"\u8bed\u6587"}

        try {
            boolean isHave = false;
            int position =0;
            JSONArray array = new JSONArray(DemoHelper.getCourseTitleShow());
            JSONObject jsonObject = new JSONObject() ;
            jsonObject.put("s_id",s_id);
            jsonObject.put("s_name",s_name);
            array.put(jsonObject);

//            for(int i=0;i<array.length();i++){
//                jsonObject = array.getJSONObject(i);
//                if (title.equals(jsonObject.getString("s_name"))){
//                    isHave = true;
//                    position = i;
//                }
//            }
                DemoHelper.setCourseTitleShow(array.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getCourseTitleArrayList1(DemoHelper.getCourseTitleShow());
    }


    public static ArrayList<CourseModel1>getRemainCourseTitleArrayList1(){
        try {
            JSONArray array = new JSONArray(DemoHelper.getCourseTitle());
            JSONArray array1 = new JSONArray(DemoHelper.getCourseTitleShow());
            JSONObject jsonObject ;
            JSONObject jsonObject1 ;


            for (int j=0;j<array1.length();j++){
                jsonObject1 = array1.getJSONObject(j);
                String findTitle =jsonObject1.getString("s_name");
                for(int i=0;i<array.length();i++){
                    jsonObject = array.getJSONObject(i);
                    if (findTitle.equals(jsonObject.getString("s_name"))){
                        array.remove(i);
                    }
                }
            }
            return getCourseTitleArrayList1(array.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

}
