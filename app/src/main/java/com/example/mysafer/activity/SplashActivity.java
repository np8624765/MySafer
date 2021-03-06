package com.example.mysafer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysafer.R;
import com.example.mysafer.db.AntivirusDao;
import com.example.mysafer.service.AddressService;
import com.example.mysafer.service.CallSafeService;
import com.example.mysafer.service.WatchDogService;
import com.example.mysafer.untils.SteamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;

//闪屏界面
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

    private SharedPreferences sp;

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
                    Toast.makeText(SplashActivity.this, "无法检查更新", Toast.LENGTH_SHORT).show();
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

        sp = getSharedPreferences("config", MODE_PRIVATE);

        //拷贝归属地查询数据库和病毒库
        copyDB("address.db");
        copyDB("antivirus.db");

        //更新病毒库
        updateVirus();

        //创建快捷方式
        if(!hasShortcut(this)) {
            createShortcut();
        }
        //判断是否开启归属地服务,黑名单，手机锁
        openAddressService();
        openCallSafeService();
        openAppLockService();

        RelativeLayout rl = (RelativeLayout)findViewById(R.id.rl_root);

        if(sp.getBoolean("auto_update", true)) {
            //检查升级
            CheckVersion();
        }else {
            handler.sendEmptyMessageDelayed(CODE_UPDATE_NOT, 2000);
        }

        //渐变的动画
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
        anim.setDuration(2000);
        rl.startAnimation(anim);
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
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3000);   //设置连接超时
                    conn.setReadTimeout(3000);      //设置读超时
                    conn.connect();
                    //返回值正常
                    if (conn.getResponseCode() == 200) {
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
                        if (getCurrentVersion() < versionCode) {
                            msg.what = CODE_UPDATE_DIALOG;
                        } else {
                            msg.what = CODE_UPDATE_NOT;
                        }
                    }
                } catch (MalformedInputException e) {
                    e.printStackTrace();
                    msg.what = CODE_URL_ERR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = CODE_IO_ERR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = CODE_JSON_ERR;
                } finally {
                    //强制让闪屏界面显示2秒
                    long endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < 2000) {
                        try {
                            sleep(2000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //发生消息给Handler
                    handler.sendMessage(msg);
                    //关闭网络连接
                    if (conn != null) {
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
        builder.setTitle("版本" + versionName + "升级");
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
        //返回键监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }

    //进入主界面
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //结束掉当前界面，用户点击返回键时不会再回到这个Activity
        finish();
    }

    //使用xUtils工具包，下载新版本
    private void download() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //显示出下载进度
            tvProgress = (TextView) findViewById(R.id.tv_progress);
            tvProgress.setVisibility(View.VISIBLE);
            String target = Environment.getExternalStorageDirectory() + "/mysafer.apk";
            HttpUtils utils = new HttpUtils();
            utils.download(downloadUrl, target, new RequestCallBack<File>() {
                //正在下载
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    tvProgress.setText("下载进度：" + (current * 100 / total) + "%");
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
                    //startActivity(intent);
                    startActivityForResult(intent, 0);
                }

                //下载失败
                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    enterHome();
                }
            });
        } else {
            Toast.makeText(SplashActivity.this, "未找到外部存储！", Toast.LENGTH_SHORT).show();
            enterHome();
        }
    }

    //接收调用的Intent所返回的信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterHome();
    }

    //拷贝数据库至files文件夹下,用于归属地查询和病毒库
    private void copyDB(String dbName) {
        File desFile = new File(getFilesDir(), dbName);
        //数据库已经存在，就不拷贝了
        if (desFile.exists()) {
            return;
        }
        try {
            InputStream in = getAssets().open(dbName);
            FileOutputStream out = new FileOutputStream(desFile);
            int len = 0;
            byte[] buf = new byte[1024];
            while((len=in.read(buf))!=-1) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //根据用户设置，开启去电来电归属地服务
    private void openAddressService() {
        if(sp.getBoolean("show_address", false)) {
            //开启归属地服务
            startService(new Intent(SplashActivity.this, AddressService.class));
        }
    }

    //根据用户设置，开启黑名单服务
    private void openCallSafeService() {
        if(sp.getBoolean("call_safe", false)) {
            //开启归属地服务
            startService(new Intent(SplashActivity.this, CallSafeService.class));
        }
    }

    //根据用户设置，开启手机程序锁
    private void openAppLockService() {
        if(sp.getBoolean("app_lock", false)) {
            //开启归属地服务
            startService(new Intent(SplashActivity.this, WatchDogService.class));
        }
    }

    //创建快捷方式
    private void createShortcut() {
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //这个方法无效...
        intent.putExtra("duplicate", false);
        intent.putExtra(
                Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),
                        R.mipmap.logo));
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "我的卫士");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(this, SplashActivity.class));
        sendBroadcast(intent);
    }

    //判断快捷方式是否存在
    public boolean hasShortcut(Context cx) {
        boolean result = false;
        // 获取当前应用名称
        String title = null;
        try {
            final PackageManager pm = cx.getPackageManager();
            title = pm.getApplicationLabel(
                    pm.getApplicationInfo(cx.getPackageName(),
                            PackageManager.GET_META_DATA)).toString();
        } catch (Exception e) {
        }

        final String uriStr;
        if (android.os.Build.VERSION.SDK_INT < 8) {
            uriStr = "content://com.android.launcher.settings/favorites?notify=true";
        } else {
            uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
        }
        final Uri CONTENT_URI = Uri.parse(uriStr);
        final Cursor c = cx.getContentResolver().query(CONTENT_URI, null,
                "title=?", new String[] { title }, null);
        if (c != null && c.getCount() > 0) {
            result = true;
        }
        return result;
    }

    //更新病毒数据库
    private void updateVirus() {

        //使用XUtils访问数据
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, "http://192.168.1.134/mysafer/virus.json",
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        //System.out.println(responseInfo.result);
                        try {
                            JSONObject json = new JSONObject(responseInfo.result);
                            String md5 = json.getString("md5");
                            String desc = json.getString("desc");
                            AntivirusDao.addVirus(md5, desc);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(SplashActivity.this, "更新病毒库失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
