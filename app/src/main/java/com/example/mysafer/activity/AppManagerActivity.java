package com.example.mysafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mysafer.R;
import com.example.mysafer.bean.AppInfo;
import com.example.mysafer.untils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity {

    List<AppInfo> appsInfos;
    List<AppInfo> appsUser;
    List<AppInfo> appsSystem;

    private ListView lvApps;
    private TextView tvROM;
    private TextView tvSdcard;
    private LinearLayout lloading;

    private PopupWindow pop;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AppManagerAdapter appManagerAdapter = new AppManagerAdapter();
            lvApps.setAdapter(appManagerAdapter);
            lloading.setVisibility(View.GONE);
            //监听滚动事件
            lvApps.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    closePopup();
                }
            });

            //监听点击事件
            lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //关闭原先的PopupWindow
                    closePopup();
                    final Object object = lvApps.getItemAtPosition(position);
                    if (object != null && object instanceof AppInfo) {
                        View contentView = View.inflate(AppManagerActivity.this, R.layout.item_popup, null);
                        //初始化popupwindow
                        pop = new PopupWindow(contentView,
                                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        //使用popupwindow进行动画效果需要设置透明背景
                        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        //设置点击事件
                        LinearLayout llUninstall = (LinearLayout)contentView.findViewById(R.id.ll_uninstall);
                        LinearLayout llStart = (LinearLayout)contentView.findViewById(R.id.ll_start);

                        llUninstall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uninstallApp(((AppInfo)object).getApkPackageName());
                                closePopup();
                            }
                        });
                        llStart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startApp(((AppInfo) object).getApkPackageName());
                                closePopup();
                            }
                        });

                        //获取view在界面中的位置
                        int[] location = new int[2];
                        view.getLocationInWindow(location);
                        //显示出popupwindow
                        pop.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 210, location[1]);
                        //添加动画
                        ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                        sa.setDuration(200);
                        contentView.startAnimation(sa);
                    }
                }
            });
        }
    };

    class AppManagerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return appsUser.size()+appsSystem.size()+2;
        }
        @Override
        public Object getItem(int position) {
            if(position==0) {
                return null;
            }else if(position==appsUser.size()+1) {
                return null;
            }else if(position<appsUser.size()+1) {
                return appsUser.get(position-1);
            }else{
                return appsSystem.get(position-appsUser.size()-2);
            }
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //第一个位置为应用程序title
            if(position==0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("用户程序(" + appsUser.size() + ")");
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
                textView.setPadding(3,3,3,3);
                return textView;
            }else if(position==appsUser.size()+1) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("系统程序("+appsSystem.size()+")");
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
                textView.setPadding(3,3,3,3);
                return textView;
            }

            final AppInfo app;
            if(position < appsUser.size()+1) {
                app = appsUser.get(position-1);
            }else {
                app = appsSystem.get(position - appsUser.size() - 2);
            }
            View view = null;
            if(convertView!=null&&convertView instanceof LinearLayout) {
                view = convertView;
            }else {
                view = View.inflate(AppManagerActivity.this, R.layout.app_manager_list_item , null);
            }
            ImageView ivAppLogo = (ImageView)view.findViewById(R.id.iv_app_logo);
            TextView tvApkName = (TextView)view.findViewById(R.id.tv_apkName);
            TextView tvApkInstalled = (TextView)view.findViewById(R.id.tv_apkInstalled);
            //设置图标
            ivAppLogo.setImageDrawable(app.getIcon());
            tvApkName.setText(app.getApkName());
            tvApkInstalled.setText((app.isRom() ? "手机存储" : "SD卡存储")
                    + "   占用大小："
                    + Formatter.formatFileSize(AppManagerActivity.this, app.getApkSize()));
            return view;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    //初始化UI
    private void initUI() {
        setContentView(R.layout.activity_app_manager);
        lvApps = (ListView)findViewById(R.id.lv_apps);
        tvROM = (TextView)findViewById(R.id.tv_rom);
        tvSdcard = (TextView)findViewById(R.id.tv_sdcard);
        lloading = (LinearLayout)findViewById(R.id.ll_loading);
        //获取rom的剩余空间
        long romFreeSpace = Environment.getDataDirectory().getFreeSpace();
        //获取sd卡的剩余空间
        long sdCardFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();

        //格式化大小并赋值
        tvROM.setText("内存可用：" + Formatter.formatFileSize(this, romFreeSpace));
        tvSdcard.setText("SD卡可用：" + Formatter.formatFileSize(this, sdCardFreeSpace));
    }

    private void initData() {
        appsUser = new ArrayList<AppInfo>();
        appsSystem = new ArrayList<AppInfo>();
        new Thread(){
            @Override
            public void run() {
                //获取所有应用程序
                appsInfos =  AppUtils.getAppInfos(AppManagerActivity.this);
                //分为用户程序和系统程序
                for(AppInfo info : appsInfos) {
                    if(info.isUserApp()) {
                        appsUser.add(info);
                    }else {
                        appsSystem.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if(pop!=null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
        super.onDestroy();
    }

    //关闭PopupWindow界面
    private void closePopup() {
        if(pop!=null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }

    //卸载APP
    private void uninstallApp(String apkPackageName) {
        Intent intent = new Intent(
                "android.intent.action.DELETE", Uri.parse("package:"+apkPackageName));
        startActivityForResult(intent, 0);
    }

    //启动APP
    private void startApp(String apkPackageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(apkPackageName);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initUI();
        initData();
    }
}
