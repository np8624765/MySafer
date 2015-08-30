package com.example.mysafer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysafer.R;
import com.example.mysafer.untils.SteamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;


public class SplashActivity extends Activity {
    private static final int CODE_UPDATE_DIALOG = 0;
    private static final int CODE_URL_ERR = 1;
    private static final int CODE_IO_ERR = 2;
    private static final int CODE_JSON_ERR = 3;
    private static final int CODE_UPDATE_NOT = 4;

    private String versionName;
    private int versionCode;
    private String description;
    private String downloadUrl;

    private TextView tvProgress;

    //使用handler，可以让子线程中刷新UI
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_URL_ERR:
                    Toast.makeText(SplashActivity.this, "Url格式错误", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_IO_ERR:
                    Toast.makeText(SplashActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_JSON_ERR:
                    Toast.makeText(SplashActivity.this, "Json数据解析错误", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_UPDATE_NOT:
                    enterHome();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //检查升级
        CheckVersion();
    }

    //获取当前版本信息
    private int getCurrentVersion() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //获取服务器上的最新版本信息
    private void CheckVersion() {
        //获得开始时间
        final long startTime = System.currentTimeMillis();
        //在子线程中访问网络
        new Thread() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                HttpURLConnection conn = null;
                try {
                    URL url = new URL("http://10.1.15.143/mysafer/update.json");
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3000);   //设置连接超时
                    conn.setReadTimeout(3000);      //设置读超时
                    conn.connect();
                    //返回值正常
                    if(conn.getResponseCode()==200) {
                        InputStream in = conn.getInputStream();
                        String result = SteamUtils.readFromStream(in);
                        //System.out.println("网络返回的结果：" + result);
                        //解析json数据
                        JSONObject jsonObject = new JSONObject(result);
                        versionName = jsonObject.getString("versionName");
                        versionCode = jsonObject.getInt("versionCode");
                        description = jsonObject.getString("description");
                        downloadUrl = jsonObject.getString("downloadUrl");
                        //System.out.println(getCurrentVersion()+"：" + versionCode);
                        if(getCurrentVersion()<versionCode) {
                            msg.what = CODE_UPDATE_DIALOG;
                        }else{
                            msg.what = CODE_UPDATE_NOT;
                        }
                    }
                }catch (MalformedInputException e) {
                    e.printStackTrace();
                    msg.what = CODE_URL_ERR;
                }catch (IOException e) {
                    e.printStackTrace();
                    msg.what = CODE_IO_ERR;
                }catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = CODE_JSON_ERR;
                }finally {
                    //强制让闪屏界面显示2秒
                    long endTime = System.currentTimeMillis();
                    if((endTime-startTime)<2000) {
                        try {
                            sleep(2000-(endTime-startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //发生消息给Handler
                    handler.sendMessage(msg);
                    //关闭网络连接
                    if(conn!=null) {
                        conn.disconnect();
                    }
                }
            }
        }.start();
    }

    //弹出对话框
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //提示框标题
        builder.setTitle("版本" + versionName +"升级");
        //提示框内容
        builder.setMessage(description);
        //提示框按钮
        builder.setPositiveButton("马上升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                download();
            }
        });
        //提示框按钮
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        builder.show();
    }

    //进入主界面
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //结束掉当前界面
        finish();
    }

    //使用xUtils工具包，下载新版本
    private void download() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //显示出下载进度
            tvProgress = (TextView)findViewById(R.id.tv_progress);
            tvProgress.setVisibility(View.VISIBLE);
            String target = Environment.getExternalStorageDirectory()+"/mysafer.apk";
            HttpUtils utils = new HttpUtils();
            utils.download(downloadUrl, target, new RequestCallBack<File>() {
                //正在下载
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    tvProgress.setText("下载进度："+(current*100/total)+"%");
                    //System.out.println("下载进度："+ current + "/" +total);
                }
                //下载成功
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //System.out.println("下载成功");
                    //调用系统PackageInstaller安装新apk
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(responseInfo.result),
                            "application/vnd.android.package-archive");
                    startActivity(intent);
                }
                //下载失败
                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(SplashActivity.this, "未找到外部存储！", Toast.LENGTH_SHORT).show();
        }
    }


}
