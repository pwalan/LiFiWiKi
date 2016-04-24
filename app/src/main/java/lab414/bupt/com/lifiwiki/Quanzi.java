package lab414.bupt.com.lifiwiki;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import lab414.bupt.com.lifiwiki.fragment.FragmentAdapter;
import lab414.bupt.com.lifiwiki.fragment.Pengyouquan;
import lab414.bupt.com.lifiwiki.fragment.Wodequanzi;

public class Quanzi extends FragmentActivity implements View.OnClickListener,ViewPager.OnPageChangeListener {

    private TextView tab1Tv, tab2Tv;
    // 指示器
    private ImageView cursorImg;

    private ViewPager viewPager;

    private ImageButton zhuye;

    private ImageButton fabiao;

    private ArrayList<Fragment> fragmentArrayList;

    private int currentIndex = 0;

    private int offset = 0;

    private int leftMargin = 0;

    private int screenWidth = 0;

    private int screen1_2;

    private LinearLayout.LayoutParams lp;

    private JSONObject response_pyq;

    private JSONObject response_wode;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quanzi);

        zhuye=(ImageButton)findViewById(R.id.zhuye);
        zhuye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Quanzi.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        fabiao=(ImageButton)findViewById(R.id.fabiao);
        fabiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Quanzi.this, uplpad.class);
                startActivity(intent);
                finish();
            }
        });

        init();
    }

    private void init() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screen1_2 = screenWidth / 2;

        cursorImg = (ImageView) findViewById(R.id.cursor);
        lp = (LinearLayout.LayoutParams) cursorImg.getLayoutParams();
        leftMargin = lp.leftMargin;

        tab1Tv = (TextView) findViewById(R.id.tab1_tv);
        tab2Tv = (TextView) findViewById(R.id.tab2_tv);

        tab1Tv.setOnClickListener(this);
        tab2Tv.setOnClickListener(this);

        initViewPager();

    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.third_vp);
        fragmentArrayList = new ArrayList<>();
        Fragment pengyou = new Pengyouquan();
        fragmentArrayList.add(pengyou);
        Fragment wodequan = new Wodequanzi();
        fragmentArrayList.add(wodequan);

        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(),
                fragmentArrayList));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab1_tv:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tab2_tv:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        offset = (screen1_2 - cursorImg.getLayoutParams().width) / 2;
        Log.d("111", position + "--" + positionOffset + "--"
                + positionOffsetPixels);
        final float scale = getResources().getDisplayMetrics().density;
        if (position == 0) {// 0<->1
            lp.leftMargin = (int) (positionOffsetPixels / 2) + offset;
        }
        cursorImg.setLayoutParams(lp);
        currentIndex = position;
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
