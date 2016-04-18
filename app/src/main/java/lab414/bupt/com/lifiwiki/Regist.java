package lab414.bupt.com.lifiwiki;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import lab414.bupt.com.lifiwiki.utils.C;

public class Regist extends AppCompatActivity {
    private String username;

    private String password;

    private String phone;

    private ImageButton getCode;//获取验证码

    private String code;//验证码变量存储

    private Button regist;

    Handler handler;
    private JSONObject response;

    private JSONObject response1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        getCode = (ImageButton)findViewById(R.id.getCode);
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final  EditText phone = (EditText) findViewById(R.id.phone);
//                Log.e("debug", phone.getText().toString());
                if(phone.getText().toString().matches("")) {
                    Toast.makeText(Regist.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                } else if (!phone.getText().toString().matches("^1[3,5]{1}[0-9]{1}[0-9]{8}$")){
                    Toast.makeText(Regist.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //发送短信通知
                            HashMap map = new HashMap();
                            map.put("phoneNumber", phone.getText().toString());
                            Log.e("debug", phone.getText().toString());
                            response1 = C.asyncPost(C.getPhoneCode, map);
                            //发送消息通知UI线程更新UI组件
                            handler.sendEmptyMessage(0x345);
                        }
                    }).start();
                }
            }
        });

        //点击注册发送消息
        regist=(Button)findViewById(R.id.regist);
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test","regist");
                //获取用户名密码
                username=((EditText)findViewById(R.id.username)).getText().toString();
                password=((EditText)findViewById(R.id.password)).getText().toString();
                phone =((EditText)findViewById(R.id.phone)).getText().toString();
                String codes = ((EditText)findViewById(R.id.checknum)).getText().toString();
                if(username.isEmpty()||password.isEmpty()||phone.isEmpty()){
                    Toast.makeText(Regist.this,"请确保用户名、密码、手机都已输入！", Toast.LENGTH_SHORT).show();
                }else if(!codes.equals(code)) {
                    Toast.makeText(Regist.this, "验证码输入错误，请检查！", Toast.LENGTH_SHORT).show();
                } else {
                    //开启线程来跑注册
                    new Thread(){
                        @Override
                        public void run(){
                            HashMap map=new HashMap();
                            map.put("name",username);
                            map.put("passwd",password);
                            map.put("phone",phone);
                            response= C.asyncPost(C.registJson,map);
                            handler.sendEmptyMessage(0x123);
                        }
                    }.start();
                }
            }
        });

        //处理消息
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(0x123==msg.what){
                    try{
                        String result=response.getString("status");
                        Log.d("test","regist: "+result);
                        if(result.equals("true")){
                            //注册成功，跳转登录界面
                            Toast.makeText(Regist.this,"注册成功！请登录",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(Regist.this, Login.class);
                            startActivity(intent);
                            finish();
                        }else{
                            //注册失败
                            Toast.makeText(Regist.this,"注册失败！用户已存在，请更换用户名",Toast.LENGTH_LONG).show();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                if(msg.what == 0x345) {
                    Toast.makeText(Regist.this, "验证码已发送，请注意查收！", Toast.LENGTH_SHORT).show();
                    try {
                        code = response1.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_regist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
