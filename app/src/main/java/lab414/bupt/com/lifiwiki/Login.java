package lab414.bupt.com.lifiwiki;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import lab414.bupt.com.lifiwiki.utils.C;
import lab414.bupt.com.lifiwiki.utils.GetPostUtil;

public class Login extends Activity {

    private String username;

    private String password;

    private Button login;

    private Button newcount;

    private Button forgetpassword;

    private static ProgressDialog dialog;

    Handler handler;
    private JSONObject response;
    private String actionURL = "http://10.125.109.23:8080/LifeWIKI/usertest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //如果消息来自子线程
                if(msg.what == 0x123) {

                    //取消进度框
                    if(dialog!=null) dialog.dismiss();

                    try {
                        String result = response.getString("status");
                        Log.i("test","login: "+result);
                        if(result.equals("true")) {//登录成功
                            int id = response.getInt("id");
                            //把记录写入文件，并给MyApplication赋值
                            SharedPreferences sharedPreferences= getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", username);
                            editor.putString("password", password);
                            editor.commit();

                            final MyApplication myApplication = (MyApplication) getApplication();
                            myApplication.setIsLogin(true);
                            myApplication.setId(id);

                            Toast.makeText(Login.this, "登录成功！", Toast.LENGTH_SHORT).show();

                            //关闭该页面
                            finish();
                        }
                        else {
                            ((EditText) findViewById(R.id.username)).setText("");
                            ((EditText) findViewById(R.id.password)).setText("");
                            Toast.makeText(Login.this, "账号验证失败，请重试！", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        initView();
    }

    private void initView() {

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dialog==null) dialog=new ProgressDialog(Login.this);
                dialog.setTitle("请稍候");
                dialog.setMessage("登录中......");
                dialog.show();

                sendMessage();
            }
        });

        newcount = (Button) findViewById(R.id.newcount);
        newcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Regist.class);
                startActivity(intent);
                finish();
            }
        });

        forgetpassword = (Button) findViewById(R.id.forgetpassword);

    }

    private void sendMessage() {

        username = ((EditText) findViewById(R.id.username)).getText().toString().trim();

        password = ((EditText) findViewById(R.id.password)).getText().toString().trim();

        if(username.length() < 0 || username.length() > 20) {
            Toast.makeText(this,"用户名输入错误，请重试（长度为5~20）",Toast.LENGTH_SHORT);
        } else if(password.length() < 0 || password.length() > 20) {
            Toast.makeText(this,"密码输入错误，请重试（长度为5~20）",Toast.LENGTH_SHORT);
        } else {
            Log.d("test","login...");
            new Thread() {
                @Override
                public void run() {

                    HashMap map = new HashMap();
                    map.put("name", username);
                    map.put("passwd", password);
                    response = C.asyncPost(C.getTestJson, map);
                    //发送消息通知UI线程更新UI组件
                    handler.sendEmptyMessage(0x123);
                }
            }.start();

        }
    }

}
