package lab414.bupt.com.lifiwiki;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import lab414.bupt.com.lifiwiki.utils.C;


public class Personal extends Activity {

    private ImageButton head;//头像

    private TextView username;//用户名

    private Button changePasswd;//修改密码

    private Button changePhone;//修改绑定手机

    private Button changeEmail;//修改绑定邮箱

    private Button addFriend;//添加好友

    private TextView pengyouquanNumber;//朋友圈数

    private TextView frientNumber;//好友数

    private TextView zanNumber;//收到的赞数目

    private int userid;//用户id

    Handler handler;
    private JSONObject response;
    private String actionURL = "http://10.125.109.17:8080/LifeWIKI/usertest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        initView();

        //发送请求获取相关信息
        getInfor();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x123) {
                    //{"email":"qq@qq.com","frientNumber":1,"head":null,"pengyouquanNumber":0,"phone":null,"username":"testuser","zanNumber":0}
                    try {
                        String headUri = response.getString("head");
                        username.setText(response.getString("username"));
                        pengyouquanNumber.setText(response.getString("pengyouquanNumber"));
                        frientNumber.setText(response.getString("frientNumber"));
                        zanNumber.setText(response.getString("zanNumber"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

    }

    private void getInfor() {
        final MyApplication myApplication = (MyApplication) getApplication();
        userid = myApplication.getId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("userID", userid);
                response = C.asyncPost(C.getInforJson, map);
                //发送消息通知UI线程更新UI组件
                handler.sendEmptyMessage(0x123);
            }
        }).start();
    }

    private void initView() {
        head = (ImageButton) findViewById(R.id.head);
        username = (TextView) findViewById(R.id.username);
        changePasswd = (Button) findViewById(R.id.changePasswd);
        changePhone = (Button) findViewById(R.id.changePhone);
        changeEmail = (Button) findViewById(R.id.changeEmail);
        addFriend = (Button) findViewById(R.id.addFriend);
        pengyouquanNumber = (TextView) findViewById(R.id.pengyouquanNumber);
        frientNumber = (TextView) findViewById(R.id.frientNumber);
        zanNumber = (TextView) findViewById(R.id.zanNumber);
    }
}
