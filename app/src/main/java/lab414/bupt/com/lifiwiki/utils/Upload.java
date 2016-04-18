package lab414.bupt.com.lifiwiki.utils;

import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.util.concurrent.Callable;

import lab414.bupt.com.lifiwiki.MyApplication;
import lab414.bupt.com.lifiwiki.common.VideoCloud;

/**
 * Created by school-miao on 2016-03-21.
 */
public class Upload implements Callable{

    private String photo_bucketName = "photo01";

    private String video_bucketName = "video01";

    private String touxiang_bucketName = "asysy01";

    private String temp_bucketName = "systemcover";

    //通过控制台获取AppId,SecretId,SecretKey
    public static final int APP_ID = 10023565;
    public static final String SECRET_ID = "AKIDtwIIJ2ehYIfVZvuPUrlRQQiHXjcI5v7g";
    public static final String SECRET_KEY = "v4wQ5DdXyIjS11UDombgKSTjOPqO6kwd";

    private VideoCloud cloud;

    private String filePath;

    private String fileName;

    private int type = -1;

    String access_url = null;

    public Upload(int type) {
        this.type = type;
    }

    public void setUpload(String filePath, String fileName) {
        Log.e("Debug", filePath);
        this.filePath = filePath;
        this.fileName = fileName;
    }

//    private String uploadAction() {
//        final String access_url = "";
//        try{
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    String result = null;
//                    try {
//                        if(type == 0) {
//                            result = cloud.uploadFile(video_bucketName, fileName, filePath);
//                        } else if(type == 1){
//                            result = cloud.uploadFile(photo_bucketName, fileName, filePath);
//                        } else {
//                            result = cloud.uploadFile(touxiang_bucketName, fileName, filePath);
//                        }
//                        JSONObject JS = new JSONObject(result);
//                        JSONObject data = JS.getJSONObject("data");
//                        access_url = data.getString("access_url");
//                        Log.e("Debug","=======uploadFile========\n" + result);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        }
//        catch(Exception e){
//            System.out.println(e.getMessage());
//        }
//    }

    @Override
    public Object call() throws Exception {
        cloud = new VideoCloud(APP_ID, SECRET_ID, SECRET_KEY);
        String result = "";
        try {
            if(type == 0) {
                result = cloud.uploadFile(photo_bucketName, fileName, filePath);
            } else if(type == 1){
                result = cloud.uploadFile(video_bucketName, fileName, filePath);
            } else if(type == 2){
                result = cloud.uploadFile(touxiang_bucketName, fileName, filePath);
            } else {
                result = cloud.uploadFile(temp_bucketName, fileName, filePath);
            }
            JSONObject JS = new JSONObject(result);
            JSONObject data = JS.getJSONObject("data");
            access_url = data.getString("access_url");
            Log.e("Debug", result);
            Log.e("Debug","=======uploadFile========\n" + result);
            return access_url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return access_url;
    }
}
