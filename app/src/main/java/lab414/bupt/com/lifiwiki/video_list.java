package lab414.bupt.com.lifiwiki;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import lab414.bupt.com.lifiwiki.utils.C;


public class video_list extends Activity {

    private String things;

    private JSONObject response;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        Intent intent = getIntent();
        things = intent.getStringExtra("things");


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x123) {
                    final TextView show = (TextView) findViewById(R.id.show);
                    if(response != null) {
                        try {
                            show.setText(response.toString()+"\n");
                            JSONArray jarray=new JSONArray(response.get("message").toString());
                            Log.d("Debug",jarray.toString());
                            for(int i=0;i<jarray.length();i++){
                                show.append("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        getInfor();
    }

    private void getInfor() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("things", things);
                response = C.asyncPost(C.getthings, map);
                //发送消息通知UI线程更新UI组件
                handler.sendEmptyMessage(0x123);
            }
        }).start();

    }
}
