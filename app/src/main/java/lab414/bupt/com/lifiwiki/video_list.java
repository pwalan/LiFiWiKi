package lab414.bupt.com.lifiwiki;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lab414.bupt.com.lifiwiki.utils.C;


public class video_list extends Activity {

    private String things;

    private JSONObject response;

    Handler handler;

    private ListView videolist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        Intent intent = getIntent();
        things = intent.getStringExtra("things");

        videolist = (ListView) findViewById(R.id.videolist);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x123) {
                    List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();
                    if(response != null) {
                        //读取response中的数据
                        try {
                            JSONArray jarray=new JSONArray(response.get("message").toString());
                            Log.d("Debug",jarray.toString());
                            for(int i=0;i<jarray.length();i++){
                                Map<String, String> listItem = new HashMap<String, String>();
                                JSONObject jobject=jarray.getJSONObject(i);
                                listItem.put("username", jobject.get("username").toString());
                                listItem.put("url", jobject.get("video").toString());
                                listItems.add(listItem);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SimpleAdapter adapter = new SimpleAdapter(video_list.this, listItems, R.layout.simple_item,
                                new String[]{"username", "url"},
                                new int[]{R.id.name, R.id.url});
                        videolist.setAdapter(adapter);
                        videolist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                String type = "video/* ";
                                Uri uri = Uri.parse(listItems.get(position).get("url"));
                                intent.setDataAndType(uri, type);
                                startActivity(intent);
                            }
                        });
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
