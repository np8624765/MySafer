package com.example.mysafer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysafer.R;
import com.example.mysafer.untils.Md5Utils;

public class HomeActivity extends Activity {

    private GridView gvHome;
    //功能名称数组
    private String[] items = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理", "流量监控",
            "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    //对应功能的图片
    private int[] icons = new int[]{R.drawable.home_sjfd, R.drawable.home_txws, R.drawable.home_rjgl,
            R.drawable.home_jcgl, R.drawable.home_lljk, R.drawable.home_sjsd, R.drawable.home_hcql,
            R.drawable.home_gjgj, R.drawable.home_szzx};

    private SharedPreferences sp;

    //GridView的适配器，类似于lIstView的Adapter
    class HomeAdapter extends BaseAdapter {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this, R.layout.home_list_item, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_item);
            TextView tv = (TextView) view.findViewById(R.id.tv_item);

            iv.setImageResource(icons[position]);
            tv.setText(items[position]);

            return view;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());

        //监听主界面的点击事件
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //手机防盗
                    case 0:
                        showPwdDailog();
                        break;
                    //设置中心
                    case 8:
                        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                        break;
                }
            }
        });
    }

    //弹出密码窗口
    private void showPwdDailog() {
        if(TextUtils.isEmpty(sp.getString("password", null))) {
            //弹出设置密码窗口
            showSettingPwd();
        }else {
            //弹出输入密码窗口
            showInputPwd();
        }

    }

    //弹出设置密码窗口
    private void showSettingPwd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dailog_setting_password, null);
        dialog.setView(view);
        dialog.show();
        Button ok = (Button) view.findViewById(R.id.bt_ok);
        Button cancel = (Button) view.findViewById(R.id.bt_cancel);
        //点击确定
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd1 = ((EditText) view.findViewById(R.id.et_pwd1)).getText().toString();
                String pwd2 = ((EditText) view.findViewById(R.id.et_pwd2)).getText().toString();
                if (TextUtils.isEmpty(pwd1) || TextUtils.isEmpty(pwd2)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (pwd1.equals(pwd2)) {
                        //Toast.makeText(HomeActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                        sp.edit().putString("password", Md5Utils.encode(pwd1)).commit();
                        dialog.dismiss();
                        //跳转进入手机防盗
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));

                    } else {
                        Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        //点击取消
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //弹出输入密码窗口
    private void showInputPwd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dailog_input_password, null);
        dialog.setView(view);
        dialog.show();
        Button ok = (Button) view.findViewById(R.id.bt_ok);
        Button cancel = (Button) view.findViewById(R.id.bt_cancel);
        //点击确定
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = Md5Utils.encode
                        (((EditText) view.findViewById(R.id.et_pwd)).getText().toString());
                if (!TextUtils.isEmpty(pwd)) {
                    if (pwd.equals(sp.getString("password", null))) {
                        //Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        //跳转进入手机防盗
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));

                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //点击取消
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
