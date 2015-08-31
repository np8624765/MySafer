package com.example.mysafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mysafer.R;

public class HomeActivity extends Activity {

    private GridView gvHome;
    //功能名称数组
    private String[] items = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理", "流量监控",
            "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    //对应功能的图片
    private int[] icons  = new int[]{R.drawable.home_sjfd, R.drawable.home_txws, R.drawable.home_rjgl,
            R.drawable.home_jcgl, R.drawable.home_lljk, R.drawable.home_sjsd, R.drawable.home_hcql,
            R.drawable.home_gjgj, R.drawable.home_szzx};

    //GridView的适配器，类似于lIstView的Adapter
    class HomeAdapter extends BaseAdapter{
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this, R.layout.home_list_item, null);
            ImageView iv = (ImageView)view.findViewById(R.id.iv_item);
            TextView tv = (TextView)view.findViewById(R.id.tv_item);

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

        gvHome = (GridView)findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());

        //监听主界面的点击事件
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //设置中心
                    case 8:
                        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                        break;
                }
            }
        });
    }

}
