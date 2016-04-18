package lab414.bupt.com.lifiwiki;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lab414.bupt.com.lifiwiki.common.RecogPic;
import lab414.bupt.com.lifiwiki.common.VideoCloud;
import lab414.bupt.com.lifiwiki.utils.C;
import lab414.bupt.com.lifiwiki.utils.Upload;
import lab414.bupt.com.lifiwiki.views.CircleProgressView;

public class uplpad extends Activity {

    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;

    private static ProgressDialog dialog;

    private int[] status = {-1, -1, -1};
    private int jindu = 0;

    private CircleProgressView mCircleBar;

    private ImageButton photoPhoto;
    private ImageButton photoSelect;
    private ImageButton videoPhoto;
    private ImageButton videoSelect;
    private ImageButton check;

    private Button compete;

    Handler handler;

    private String photoPath;
    private String photouri;
    private Uri photoUri;
    private Bitmap bitmap;
    private String videoPath;
    private String videouri;
    private Uri videoUri;
    private String things;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uplpad);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    initBack();
                    if (jindu == 0) {
                        jindu = 25;
                        mCircleBar.setProgress(jindu);
                    }
                    Log.e("debug", status.toString());
                    Log.e("debug", photoPath);
                }
                if (msg.what == 0x234) {
                    initBack();
                    if (jindu == 25) {
                        jindu = 65;
                        mCircleBar.setProgress(jindu);
                    }
                    Log.e("debug", status.toString());
                    Log.e("debug", videoPath);
                }
                if (msg.what == 0x666) {
                    Toast.makeText(uplpad.this, "上传成功！", Toast.LENGTH_LONG).show();
                    //上传成功，取消进度框
                    if (dialog != null) dialog.dismiss();
                    Log.e("Debug", "finish_upload!");


                    finish();
                }
            }
        };

        initView();

    }

    private void initView() {

        mCircleBar = (CircleProgressView) findViewById(R.id.circleProgressbar);
        mCircleBar.setProgress(jindu);

        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);

        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);

        linearLayout3 = (LinearLayout) findViewById(R.id.linearLayout3);

        photoPhoto = (ImageButton) findViewById(R.id.photoPhoto);
        photoPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }
        });

        photoSelect = (ImageButton) findViewById(R.id.photoSelect);
        photoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用系统图库
                startActivityForResult(intent, 2);
            }
        });

        videoPhoto = (ImageButton) findViewById(R.id.videoPhoto);
        videoPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
                startActivityForResult(intent, 3);
            }
        });

        videoSelect = (ImageButton) findViewById(R.id.videoSelect);
        videoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, 4);
            }
        });

        check = (ImageButton) findViewById(R.id.check);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status[0] == -1 || status[1] == -1) {
                    Toast.makeText(uplpad.this, "请选择需要上传的信息！", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(uplpad.this, "文件选择正确，请点击上传！", Toast.LENGTH_SHORT);
                    status[2] = 0;
                    initBack();
                }

            }
        });

        //点击按钮之后上传视频
        compete = (Button) findViewById(R.id.compete);
        compete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传时的处理
                if (dialog == null) dialog = new ProgressDialog(uplpad.this);
                dialog.setTitle("请稍候");
                dialog.setMessage("上传中......");
                dialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //通过控制台获取AppId,SecretId,SecretKey
                        final int APP_ID = 10023565;
                        final String SECRET_ID = "AKIDtwIIJ2ehYIfVZvuPUrlRQQiHXjcI5v7g";
                        final String SECRET_KEY = "v4wQ5DdXyIjS11UDombgKSTjOPqO6kwd";
                        String photo_bucketName = "photo01";
                        String video_bucketName = "video01";
                        VideoCloud photoUpload = new VideoCloud(APP_ID, SECRET_ID, SECRET_KEY);
                        VideoCloud videoUpload = new VideoCloud(APP_ID, SECRET_ID, SECRET_KEY);

                        File photo = new File(photoPath);
                        File video = new File(videoPath);
                        try {
                            String photos = photoUpload.uploadFile(photo_bucketName, photo.getName(), photoPath);
                            String videos = videoUpload.uploadFile(video_bucketName, video.getName(), videoPath);
                            photouri = getUri(photos);
                            videouri = getUri(videos);

                            //imageuri、videouri已经上传成功并获取地址，
                            Log.i("test", photouri + " " + videouri);
                            // 1.首先需要以imageuri作为参数，调用百度识图获取识别结果
                            RecogPic recogPic = new RecogPic(photouri);
                            //创建一个线程池
                            ExecutorService pool1 = Executors.newFixedThreadPool(1);
                            Future fu = pool1.submit(recogPic);
                            try {
                                things = fu.get().toString();//结果字符串
                                if (things == "") {
                                    Log.d("test", "识别无结果");
                                } else {
                                    Log.i("test", "recogPic: " + things);
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }


                            // 2.需要从myapplication中读取userid，然后将该条记录插入上传表中
                            MyApplication myApplication = (MyApplication) getApplication();
                            HashMap map = new HashMap();
                            map.put("uid", myApplication.getId());
                            map.put("img", photouri);
                            map.put("video", videouri);
                            map.put("keyword", things);
                            C.asyncPost(C.saveUp, map);

                            //上传成功获得了图片和视频的网络地址，给主线程发送0x666消息，在主线程中处理下一个步骤

                            handler.sendEmptyMessage(0x666);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        });

        initBack();
    }

    private String getUri(String uri) {
        try {
            JSONObject JS = new JSONObject(uri);
            JSONObject data = JS.getJSONObject("data");

            return data.getString("access_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SdCardPath")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("Debug", "start onActivityResult");

        Log.e("Debug", "requestCode:" + requestCode + " + resultCode:" + resultCode + " + RESULT_OK:" + RESULT_OK);


        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                        Log.i("TestFile",
                                "SD card is not avaiable/writeable right now.");
                        return;
                    }
                    new DateFormat();

                    //Toast.makeText(this, name, Toast.LENGTH_LONG).show();
                    Bundle bundle = data.getExtras();
                    bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

                    String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";

                    photoPath = "/sdcard/Arsystem/" + name;

                    storePNG storepng = new storePNG(bitmap, photoPath);
                    storepng.start();

                    status[0] = 0;
                    handler.sendEmptyMessage(0x123);
                    break;

                case 2:
                    photoUri = data.getData();
                    Cursor cursor = this.getContentResolver().query(photoUri, null, null, null, null);
                    cursor.moveToFirst();
                    photoPath = cursor.getString(1); //图片文件路径
                    cursor.close();

                    status[0] = 1;
                    handler.sendEmptyMessage(0x123);
                    break;
                case 3:
                    videoUri = data.getData();
                    Cursor cursor1 = this.getContentResolver().query(videoUri, null, null, null, null);
                    cursor1.moveToFirst();
                    videoPath = cursor1.getString(1);
                    cursor1.close();

                    status[1] = 0;
                    handler.sendEmptyMessage(0x234);

                    break;
                case 4:
                    videoUri = data.getData();
                    Log.e("path1", videoUri.getPath());
                    Log.e("path2,", videoUri.getEncodedPath());
//                    Cursor cursor1 = this.getContentResolver().query(uri2, null, null, null, null);
//                    cursor1.moveToFirst();
                    videoPath = videoUri.getEncodedPath(); //图片文件路径
//                    cursor1.close();

                    status[1] = 1;
                    handler.sendEmptyMessage(0x234);
                    break;
            }
        }
    }

    class storePNG extends Thread {
        private Bitmap bitmap;

        private String fileName;

        public storePNG(Bitmap bitmap, String fileName) {
            this.bitmap = bitmap;
            this.fileName = fileName;
        }

        @Override
        public void run() {

            FileOutputStream b = null;
            File file = new File("/sdcard/Arsystem/");
            file.mkdirs();// 创建文件夹

            try {
                b = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检查存储卡是否插入
     *
     * @return
     */
    public static boolean isHasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }

    //设置背景色
    private void initBack() {
        setLinearLayoutBack(linearLayout1, status[0]);
        setLinearLayoutBack(linearLayout2, status[1]);
        setLinearLayoutBack(linearLayout3, status[2]);
    }

    private void setLinearLayoutBack(LinearLayout linearLayout, int i) {
        if (i == -1) {
            linearLayout.setBackgroundResource(R.drawable.stepfinishgray);
        } else {
            linearLayout.setBackgroundResource(R.drawable.stepfinishwhite);
        }
    }
}
