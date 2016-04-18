package lab414.bupt.com.lifiwiki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.MissingFormatArgumentException;

import lab414.bupt.com.lifiwiki.utils.C;

public class MainActivity extends AppCompatActivity {

    private ImageButton shipinchakan;

    private ImageButton baikechakna;

    private ImageButton gerenzhongxin;

    private ImageButton caozuozhinan;

    private ImageButton zhuye;

    private ImageButton pengyouquan;

    private ImageButton fabiao;

    Handler handler;

    private MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myApplication = (MyApplication) getApplication();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //如果消息来自子线程
                if (msg.what == 0x123) {
                    Toast.makeText(MainActivity.this, "尝试的登录中...", Toast.LENGTH_SHORT).show();
                }
                else if(msg.what == 0x234) {
                    Toast.makeText(MainActivity.this, "登录成功...", Toast.LENGTH_SHORT).show();
                }
                else if(msg.what == 0x345) {//登录失败
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }
            }
        };

        initView();

    }

    private void initView() {
        shipinchakan = (ImageButton) findViewById(R.id.shipinchakan);
        shipinchakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myApplication.isLogin()) {
                    Intent intent = new Intent(MainActivity.this, video_surface.class);
                    startActivity(intent);
                } else {
                    login();
                }
            }
        });

        baikechakna = (ImageButton) findViewById(R.id.baikechakan);
        baikechakna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, surface.class);
                startActivity(intent);
            }
        });


        gerenzhongxin = (ImageButton) findViewById(R.id.gerenzhongxin);
        gerenzhongxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myApplication.isLogin()) {
                    Intent intent = new Intent(MainActivity.this, Personal.class);
                    startActivity(intent);
                } else {
                    login();
                }
            }
        });

        caozuozhinan = (ImageButton) findViewById(R.id.caozuozhinan);

        zhuye = (ImageButton) findViewById(R.id.zhuye);
        zhuye.setBackgroundResource(R.drawable.mainone);

        pengyouquan = (ImageButton) findViewById(R.id.pengyouquan);
        pengyouquan.setBackgroundResource(R.drawable.pengyouquantwo);
        pengyouquan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myApplication.isLogin()) {
                    Intent intent = new Intent(MainActivity.this, Quanzi.class);
                    startActivity(intent);
                } else {
                    login();
                }
            }
        });

        fabiao = (ImageButton) findViewById(R.id.fabiao);
        fabiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myApplication.isLogin()) {
                    Intent intent = new Intent(MainActivity.this, uplpad.class);
                    startActivity(intent);
                } else {
                    login();
                }
            }
        });
    }

    //视频查看事件
    private void login() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }

    //验证用户是否登录（检查MyApplication的值，如果没有登录，则检查sharedpreferences中是否有账号信息
    //有的话后台进行登陆验证，如果登录失败，则进入到登录页面
    private void LoginAction() {
        if(myApplication.getId() != -1) {
            handler.sendEmptyMessage(0x234);
        } else {
            handler.sendEmptyMessage(0x123);
            SharedPreferences sharedPreferences= getSharedPreferences("user", MODE_PRIVATE);
            //editor.putString("username", username);
//            editor.putString("password", password);
            if(sharedPreferences.getString("username", "0").equals("0")) {//说明没有登陆过的用户
                handler.sendEmptyMessage(0x345);
            }
            else {
                final String username = sharedPreferences.getString("username", "0");
                final String password = sharedPreferences.getString("password", "0");
                new Thread() {
                    @Override
                    public void run() {

                        HashMap map = new HashMap();
                        map.put("name", username);
                        map.put("passwd", password);
                        try {
                            JSONObject result = C.asyncPost(C.getTestJson, map);
                            String users = result.getString("status");
                            if(users.equals("true")) {//登录成功
                                int id = result.getInt("id");
                                myApplication.setIsLogin(true);
                                myApplication.setId(id);
                                handler.sendEmptyMessage(0x234);
                            }
                            else {
                                handler.sendEmptyMessage(0x345);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }
    }
}
