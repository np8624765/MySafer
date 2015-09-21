package com.example.mysafer.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysafer.R;
import com.example.mysafer.bean.ProcessInfo;
import com.example.mysafer.untils.ProcessUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：陈志辉
 * 日期：2015/9/16 15:55
 * 描述：
 */
public class ProcessManagerActivity extends Activity {

    private TextView tvProcess;
    private TextView tvRam;
    private LinearLayout llLoading;
    private ListView lvProcess;

    private ActivityManager activityManager;

    private List<ProcessInfo> processInfos;
    private List<ProcessInfo> userProcess;
    private List<ProcessInfo> systemProcess;

    private ProcessAdapter adapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            llLoading.setVisibility(View.GONE);
            if(msg.what==0) {
                tvProcess.setText("进程数：" + processInfos.size());
                adapter = new ProcessAdapter();
                lvProcess.setAdapter(adapter);
            }else if(msg.what==1) {
                adapter.notifyDataSetChanged();
            }else if(msg.what==2) {
                initUI();
                initData();
            }

        }
    };

    class ProcessAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return processInfos.size() + 2;
        }
        @Override
        public Object getItem(int position) {
            if(position==0) {
                return null;
            }else if(position==userProcess.size()+1) {
                return null;
            }else if(position<userProcess.size()+1) {
                return userProcess.get(position-1);
            }else{
                return systemProcess.get(position-userProcess.size()-2);
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
                TextView textView = new TextView(ProcessManagerActivity.this);
                textView.setText("用户进程(" + userProcess.size() + ")");
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
                textView.setPadding(3,3,3,3);
                return textView;
            }else if(position==userProcess.size()+1) {
                TextView textView = new TextView(ProcessManagerActivity.this);
                textView.setText("系统进程("+systemProcess.size()+")");
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
                textView.setPadding(3,3,3,3);
                return textView;
            }

            final ProcessInfo process;
            if(position < userProcess.size()+1) {
                process = userProcess.get(position-1);
            }else {
                process = systemProcess.get(position - userProcess.size() - 2);
            }
            View view = null;
            if(convertView!=null&&convertView instanceof LinearLayout) {
                view = convertView;
            }else {
                view = View.inflate(
                        ProcessManagerActivity.this, R.layout.process_manager_list_item , null);
            }
            ImageView ivAppLogo = (ImageView)view.findViewById(R.id.iv_app_logo);
            TextView tvApkName = (TextView)view.findViewById(R.id.tv_apkName);
            TextView tvUseRam = (TextView)view.findViewById(R.id.tv_use_ram);
            CheckBox cb = (CheckBox)view.findViewById(R.id.cb_process);
            //设置图标
            ivAppLogo.setImageDrawable(process.getIcon());
            tvApkName.setText(process.getAppName());
            tvUseRam.setText("占用内存："
                    + Formatter.formatFileSize(ProcessManagerActivity.this, process.getMemorySize()));
            cb.setChecked(process.isCheck());
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
        setContentView(R.layout.activity_process_manager);
        tvProcess = (TextView)findViewById(R.id.tv_process);
        tvRam = (TextView)findViewById(R.id.tv_ram);
        llLoading = (LinearLayout)findViewById(R.id.ll_loading);
        lvProcess = (ListView)findViewById(R.id.lv_process);

        lvProcess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Object object = lvProcess.getItemAtPosition(position);
                if (object != null && object instanceof ProcessInfo) {
                    CheckBox cb = (CheckBox)view.findViewById(R.id.cb_process);
                    boolean current = cb.isChecked();
                    cb.setChecked(!current);
                    ((ProcessInfo) object).setIsCheck(!current);
                }
            }
        });

        //获得进程管理器
        activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        //获取当前手机中运行的所有进程
        tvProcess.setText("进程数：" + getCurrentProcessCount());
        //得到总内存，建议使用API 16以上
        //long totalMem = memoryInfo.totalMem;
        tvRam.setText("剩余/总内存：" + Formatter.formatFileSize(this, getAvailMen())
                + "/" + Formatter.formatFileSize(this, getTotalMem()));

    }

    //初始化数据
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                userProcess = new ArrayList<>();
                systemProcess = new ArrayList<>();
                processInfos = ProcessUtils.getProcessInfos(ProcessManagerActivity.this);
                for (ProcessInfo info : processInfos) {
                    if(info.isUserApk()){
                        userProcess.add(info);
                    }else {
                        systemProcess.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    //获取当前手机中运行的所有进程
    private int getCurrentProcessCount() {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                activityManager.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    private long getAvailMen() {
        //获取内存的基本信息
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        //得到剩余内存
        return memoryInfo.availMem;
    }

    //直接去读系统文件（/proc/meminfo），得到总内存
    private long getTotalMem() {
        long totalMem = 0;
        try {
            FileInputStream in = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String info = reader.readLine();
            StringBuilder sb = new StringBuilder();
            for (char c :info.toCharArray()) {
                if(c>='0' && c<='9') {
                    sb.append(c);
                }
            }
            in.close();
            reader.close();
            totalMem = Long.parseLong(sb.toString())*1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalMem;
    }

    //全选
    public void checkAll(View view) {
        for (ProcessInfo item : processInfos) {
            item.setIsCheck(true);
            handler.sendEmptyMessage(1);
        }
    }

    //反选
    public void checkRev(View view) {
        for (ProcessInfo item : processInfos) {
            item.setIsCheck(!item.isCheck());
            handler.sendEmptyMessage(1);
        }
    }

    //清理进程
    public void clear(View view) {
        int count = 0;
        int memory = 0;
        for (ProcessInfo item : processInfos) {
           if(item.isCheck()) {
               ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
               activityManager.killBackgroundProcesses(item.getPackageName());
               if(item.isUserApk()) {
                   userProcess.remove(item);
               }else {
                   systemProcess.remove(item);
               }
               count++;
               memory += item.getMemorySize();
           }
        }
        handler.sendEmptyMessage(2);
        Toast.makeText(
                this, "共清理了"+ count + "个进程，释放了"
                        + Formatter.formatFileSize(this, memory)+ "内存空间", Toast.LENGTH_SHORT).show();
    }


    



}
