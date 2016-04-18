package lab414.bupt.com.lifiwiki;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.search.cse.search.CseSearch;
import com.baidu.search.cse.vo.QueryInfo;
import com.baidu.search.cse.vo.ResultInfo;
import com.baidu.search.cse.vo.ReturnInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lab414.bupt.com.lifiwiki.common.RecogPic;
import lab414.bupt.com.lifiwiki.common.VideoCloud;
import lab414.bupt.com.lifiwiki.utils.HttpOp;
import lab414.bupt.com.lifiwiki.utils.StringSubClass;
import lab414.bupt.com.lifiwiki.utils.Upload;

public class surface extends Activity {

    private int getImage = 0;

    private SurfaceView sView;

    private SurfaceHolder surfaceHolder;

    private int screenWidth;
    private int screenHeight;
    //定义系统所使用的相机
    private android.hardware.Camera camera;
    //是否在预览中
    boolean isPreview = false;

    WindowManager wm;

    Handler handler;

    private String mScreenshotPath = Environment.getExternalStorageDirectory() + "/LifeWiki";
    private long times1;
    private long times2;
    private int cixu = -1;

    private String imageUri;
    private String baikeUri;
    private String things;

    private CseSearch search;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_surface);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x123) {

                    if (dialog == null) dialog = new ProgressDialog(surface.this);
                    dialog.setTitle("请稍候");
                    dialog.setMessage("识别中......");
                    dialog.show();

                    cixu = 0;
                    getImage=0;
                    times1 = System.currentTimeMillis();
                    saveImage();
                }
                if(msg.what == 0x234) {
                    cixu = 1;
                    times2 = System.currentTimeMillis();
                    getImage = 0;
                    saveImage();
                }
                if(msg.what == 0x345) {//上传图片并获取地址
                    uploadPic();
                }
                if(msg.what == 0x666) {
                    //取消进度框
                    if(dialog!=null) dialog.dismiss();
                    Toast.makeText(surface.this,"识别成功！",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(surface.this, BaikeView.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("things", things);
                    bundle.putString("baikeUri", baikeUri);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                if(msg.what==0x404){
                    //取消进度框
                    if(dialog!=null) dialog.dismiss();
                    Toast.makeText(surface.this,"很抱歉，识别失败！",Toast.LENGTH_SHORT).show();
                }
            }
        };

        initCom();

        //task();//定时抓取帧任务

        //surfaceveiw触屏事件
        sView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("debug","You touch the surfaceview");
                camera.autoFocus(new android.hardware.Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, android.hardware.Camera camera) {
                        if (success) {
                            Log.d("debug", "Focus succeed!");
                            handler.sendEmptyMessage(0x123);
                        } else {
                            Log.d("debug", "Focus failed!");
                            Toast.makeText(surface.this, "聚焦失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return false;
            }
        });
    }
    private void uploadPic() {
        Log.e("debug", "start_upload!");

        Upload upload = new Upload(3);
        upload.setUpload(mScreenshotPath + "/" + times1 + ".jpg", times1 + ".jpg");
        Log.e("path", mScreenshotPath + "/" + times1 + ".jpg");
        //创建一个线程池
        ExecutorService pool = Executors.newFixedThreadPool(1);
        //执行任务并获取Future对象
        Future f1 = pool.submit(upload);

        try {
            imageUri = f1.get().toString();//获取图片的uri
            Log.d("debug","start recogPIc!");
            RecogPic recogPic = new RecogPic(imageUri);
            //创建一个线程池
            ExecutorService pool1 = Executors.newFixedThreadPool(1);
            Future fu = pool1.submit(recogPic);
                things = fu.get().toString();
                Log.e("things", things);

            search = new CseSearch("1701124582921055767", this);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    QueryInfo queryInfo = new QueryInfo();
                    queryInfo.setResultType(1);
                    queryInfo.setQuery(things);
                    String[] subDomains = {"baike.baidu.com"};
                    queryInfo.setSearchRange(2, subDomains);
                    ReturnInfo returnInfo = search.getResult(queryInfo);
                    Log.e("debug + searchInfo", queryInfo.getCustomParaKey());
                    Log.e("debug + errorNo", returnInfo.getErroNo() + "");
                    int size = returnInfo.getResultInfoList().size();//qq
                    for (int i = 0; i < size; i++) {
                        ResultInfo resultInfo = returnInfo.getResultInfoList().get(i);
                        Log.e("result:", resultInfo.getLinkurl());
                        break;
                    }
                    baikeUri = returnInfo.getResultInfoList().get(0).getLinkurl(); //获得url
                    Log.e("debug", "baikeUri:"+baikeUri);
                    if(baikeUri.isEmpty()){
                        handler.sendEmptyMessage(0x404);
                    }else{
                        handler.sendEmptyMessage(0x666);
                    }
                }
            }).start();

        }catch(StringIndexOutOfBoundsException e){
            e.printStackTrace();
            handler.sendEmptyMessage(0x404);
        } catch (InterruptedException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(0x404);
        } catch (ExecutionException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(0x404);
        }
    }

    private void initCom() {
        //获取窗口管理器
        wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        //获取屏幕的宽和高
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        //获取界面中SurfaceView组件
        sView = (SurfaceView) findViewById(R.id.surfaceView);
        //设置该surface不需要自己维护缓冲区
        sView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //获得surfaceView的surfaceHolder
        surfaceHolder = sView.getHolder();

        //为surfaceHolder添加一个回掉监听器
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.e("Debug", "holder:" + holder.toString() + "&& format:" + format + "&& width_height:" + width + "," + height);
                screenWidth = width;
                screenHeight = height;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (camera != null) {
                    camera.setPreviewCallback(null);
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        });
    }

    /*private void task() {
        Timer timer1 = new Timer();
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                Log.d("debug","do task1");
                handler.sendEmptyMessage(0x123);
            }
        };
        timer1.schedule(task1, 1000);

//        Timer timer2 = new Timer();
//        TimerTask task2 = new TimerTask() {
//            @Override
//            public void run() {
//                handler.sendEmptyMessage(0x234);
//            }
//        };
//        timer2.schedule(task2, 1500);


        Timer timer3 = new Timer();
        TimerTask task3 = new TimerTask() {
            @Override
            public void run() {
                Log.d("debug","do task2");
                handler.sendEmptyMessage(0x345);
            }
        };
        timer3.schedule(task3, 1005);
    }
*/
    private void saveImage() {
        camera.setPreviewCallback(new android.hardware.Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {
                if(getImage == 0) {
                    android.hardware.Camera.Size size = camera.getParameters().getPreviewSize();
                    final int w = size.width;
                    final int h = size.height;
                    final YuvImage image = new YuvImage(data, ImageFormat.NV21, w, h, null);
                    ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
                    if(!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
                        return;
                    }
                    byte[] tmp = os.toByteArray();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
                    saveScreenshot(bitmap);
                    getImage++;
                    Log.e("debug", "finish save!");
                    handler.sendEmptyMessage(0x345);
                }
            }
        });
    }

    private void initCamera() {
        if(!isPreview) {
            //默认打开后置摄像头
            camera = android.hardware.Camera.open(0);
            camera.setDisplayOrientation(90);
        }
        if(camera != null && !isPreview) {
            try {
                android.hardware.Camera.Parameters parameters = camera.getParameters();
                //设置预览照片的大小
                parameters.setPreviewSize(screenHeight, screenWidth);
                //设置预览照片时每秒显示多少帧的最小值和最大值
                parameters.setPreviewFpsRange(4, 10);
                //设置图片格式
                parameters.setPictureFormat(PixelFormat.JPEG);
                //设置JPG照片的质量
                parameters.set("jpeg-quality", 80);
                //设置照片的大小
                parameters.setPictureSize(screenHeight, screenWidth);
                camera.setDisplayOrientation(90);
                //通过SurfaceView显示取景画面
                camera.setPreviewDisplay(surfaceHolder);
                //开始预览
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }

    public void saveScreenshot(Bitmap bitmap) {
        if (ensureSDCardAccess()) {
            File file = null;
            if(cixu == 0) {
                file = new File(mScreenshotPath + "/" + times1 + ".jpg");
            } else {
                file = new File(mScreenshotPath + "/" + times2 + ".jpg");
            }
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e("Panel", "FileNotFoundException", e);
            } catch (IOException e) {
                Log.e("Panel", "IOEception", e);
            }
        }
    }

    /**
     * Helper method to ensure that the given path exists.
     * TODO: check external storage state
     */
    private boolean ensureSDCardAccess() {
        File file = new File(mScreenshotPath);
        if (file.exists()) {
            return true;
        } else if (file.mkdirs()) {
            return true;
        }
        return false;
    }
}