package com.example.mysafer.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.mysafer.R;
import com.example.mysafer.bean.ScanInfo;
import com.example.mysafer.db.AntivirusDao;
import com.example.mysafer.untils.Md5Utils;

import java.util.List;

public class AntivirusActivity extends Activity {

    private static final int BEGIN = 1;
    private static final int SCANING = 2;
    private static final int FINISH = 3;

    private ImageView ivScanning;
    private TextView tvInit;
    private ProgressBar pbAntivirus;
    private LinearLayout llContent;
    private ScrollView slContent;
    private TextView tvProgress;

    private List<PackageInfo> packageInfos;

    private Message msg;

    private int progress;
    private int virus;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BEGIN:
                    tvInit.setText("初始化双核查杀引擎");
                    break;
                case SCANING:
                    tvInit.setText("正在进行杀毒，请稍等...");
                    ScanInfo scanInfo = (ScanInfo)msg.obj;
                    TextView tv = new TextView(AntivirusActivity.this);
                    if(scanInfo.getDesc()) {
                        tv.setText(scanInfo.getApkName()+" 是病毒程序！");
                        tv.setTextColor(Color.RED);
                        tv.setTextSize(16);
                    }else {
                        tv.setText(scanInfo.getApkName() + " 扫描安全...");
                        tv.setTextColor(Color.BLUE);
                        tv.setTextSize(16);
                    }
                    llContent.addView(tv);
                    //自动滑动至底部
                    slContent.fullScroll(ScrollView.FOCUS_DOWN);
                    tvProgress.setText("已扫描"+progress+"个应用  发现危险应用"+virus+"个");
                    break;
                case FINISH:
                    ivScanning.clearAnimation();
                    TextView end = new TextView(AntivirusActivity.this);
                    end.setText("扫描应用完毕!");
                    end.setTextSize(16);
                    llContent.addView(end);
                    slContent.fullScroll(ScrollView.FOCUS_DOWN);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private void initUI() {
        setContentView(R.layout.activity_antivirus);
        ivScanning = (ImageView)findViewById(R.id.iv_scanning);
        tvInit = (TextView)findViewById(R.id.tv_init);
        pbAntivirus = (ProgressBar)findViewById(R.id.pb_antivirus);
        llContent = (LinearLayout)findViewById(R.id.ll_content);
        slContent = (ScrollView)findViewById(R.id.sl_content);
        tvProgress = (TextView)findViewById(R.id.tv_progress_info);
        //初始化旋转动画
        RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(4000);
        animation.setRepeatCount(Animation.INFINITE);
        ivScanning.startAnimation(animation);
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                virus = 0;
                msg = Message.obtain();
                msg.what = BEGIN;
                handler.sendMessage(msg);
                SystemClock.sleep(2000);
                //获取手机上的所有应用包
                PackageManager packageManager = getPackageManager();
                packageInfos = packageManager.getInstalledPackages(0);

                //总共有多少应用
                int sum = packageInfos.size();
                pbAntivirus.setMax(sum);
                progress = 0;

                for(PackageInfo info : packageInfos) {
                    //获取应用名称和包名
                    String appName = info.applicationInfo.loadLabel(packageManager).toString();
                    String packageName = info.applicationInfo.packageName;
                    //获取应用文件的目录
                    String sourceDir = info.applicationInfo.sourceDir;
                    String md5 = Md5Utils.getFileMd5(sourceDir);
                    //将md5与数据库进行查询
                    String desc = AntivirusDao.checkFileVirus(md5);
                    Boolean isVirus = false;
                    //判断是否为病毒
                    if(!TextUtils.isEmpty(desc)) {
                        isVirus = true;
                        virus++;
                    }
                    ScanInfo scanInfo = new ScanInfo(appName, packageName, isVirus);

//                    System.out.println("------------------------------------------");
//                    System.out.println(appName);
//                    System.out.println(Md5Utils.getFileMd5(sourceDir));
//                    System.out.println("------------------------------------------");

                    progress++;
                    pbAntivirus.setProgress(progress);
                    msg = Message.obtain();
                    msg.what = SCANING;
                    msg.obj = scanInfo;
                    handler.sendMessage(msg);
                    SystemClock.sleep(100);
                }
                msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }
}
