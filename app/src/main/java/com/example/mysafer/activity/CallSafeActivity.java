package com.example.mysafer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysafer.R;
import com.example.mysafer.adapter.MyBaseAdapter;
import com.example.mysafer.bean.BlankNumberInfo;
import com.example.mysafer.db.AddressDao;
import com.example.mysafer.db.BlankNumberDao;

import java.util.List;

public class CallSafeActivity extends Activity {

    private ListView lv;
    private LinearLayout ll;
    private List<BlankNumberInfo> infos;
    private AddressDao addressDao;
    private BlankNumberDao dao;
    private MyAdapter adapter;

    private int start;
    private int size;

    private Handler handler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ll.setVisibility(View.GONE);
            if(msg.what==0) {
                adapter = new MyAdapter(infos);
                lv.setAdapter(adapter);
            }else {
                adapter.notifyDataSetChanged();
            }
        }
    };

    class MyAdapter extends MyBaseAdapter<BlankNumberInfo> {
        public MyAdapter(List lists) {
            super(lists);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String[] modes = new String[]{"电话拦截+短信拦截", "电话拦截", "短信拦截"};
            View view = null;
            if(convertView==null) {
                view = View.inflate(CallSafeActivity.this, R.layout.call_safe_list_item, null);
            }else {
                view = convertView;
            }

            TextView tvNumber = (TextView)view.findViewById(R.id.tv_number);
            TextView tvMode = (TextView)view.findViewById(R.id.tv_mode);
            ImageView ivDelete = (ImageView)view.findViewById(R.id.iv_delete);

            final BlankNumberInfo info = lists.get(position);

            tvNumber.setText(info.getNumber()
                    +"("+ addressDao.getAddress(info.getNumber()) +")");
            tvMode.setText(modes[lists.get(position).getMode()]);

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean result = dao.delete(info.getNumber());
                    if (result) {
                        Toast.makeText(CallSafeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        lists.remove(info);
                        //刷新界面
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CallSafeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        start = 0;
        size = 10;
        addressDao = new AddressDao();
        initUI();
        initData();
    }

    //初始化UI
    private void initUI() {
        lv = (ListView)findViewById(R.id.lv_call_safe);
        ll = (LinearLayout)findViewById(R.id.ll_loading);
        //设置滑动监听
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        int pos = lv.getLastVisiblePosition();
                        //System.out.println(pos);
                        if (pos>=dao.getCount()-1) {
                            //Toast.makeText(CallSafeActivity.this, "已经没有更多数据了", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (pos >= (start + size) - 1) {
                            start += size;
                            initData();
                        }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    //初始化数据
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                dao = new BlankNumberDao(CallSafeActivity.this);
                if(infos==null) {
                    infos = dao.findSome(start, size);
                    handler.sendEmptyMessage(0);
                }else{
                    //System.out.println("从"+start+"开始加载...");
                    infos.addAll(dao.findSome(start, size));
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    public void addBlankNumber(View view) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        final View dialog_view = View.inflate(this, R.layout.dialog_add_blank_number, null);
        final AlertDialog dialog = builder.create();
        dialog.setView(dialog_view);
        Button btn_ok = (Button)dialog_view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button)dialog_view.findViewById(R.id.btn_cancel);
        //点击OK
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = 0;
                String number = ((EditText)dialog_view.findViewById(R.id.et_blank_number))
                        .getText().toString().trim();
                Boolean phone = ((CheckBox)dialog_view.findViewById(R.id.cb_phone)).isChecked();
                Boolean sms = ((CheckBox)dialog_view.findViewById(R.id.cb_sms)).isChecked();
                if(TextUtils.isEmpty(number)) {
                    Toast.makeText(CallSafeActivity.this, "请输入黑名单号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!(phone||sms)) {
                    Toast.makeText(CallSafeActivity.this, "请至少选择一种拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断拦截模式
                if(phone && sms) {
                    mode = 0;
                }else if(phone) {
                    mode = 1;
                }else if(sms) {
                    mode = 2;
                }
                boolean result = dao.add(number, mode);
                if(result) {
                    Toast.makeText(CallSafeActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    infos = null;
                    start = 0;
                    initData();
                }else {
                    Toast.makeText(CallSafeActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //点击Cancel
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
