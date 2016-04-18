package lab414.bupt.com.lifiwiki;

import android.app.Application;

/**
 * Created by school-miao on 2016-03-18.
 */
public class MyApplication extends Application{

    private boolean isLogin;

    private String ip;

    private int port;

    private int id;

    @Override
    public void onCreate() {
        //对两个值进行初始化
        isLogin = false;
        id = -1;
        super.onCreate();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
