package com.example.mysafer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mysafer.R;

/**
 * 设置中心item的View
 * Created by Along on 2015/8/31.
 */
public class SettingItemView extends RelativeLayout {

    private TextView tv_title;
    private TextView tv_desc;
    private CheckBox cb_status;


    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SettingItemView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        //将布局文件给到SettingItemView中
        View view = View.inflate(getContext(), R.layout.setting_item, this);
        tv_title = (TextView)findViewById(R.id.tv_auto_update_title);
        tv_desc = (TextView)findViewById(R.id.tv_auto_update_desc);
        cb_status = (CheckBox)findViewById(R.id.tv_auto_update_status);
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setDesc(String title) {
        tv_desc.setText(title);
    }

    public boolean isChecked() {
        return cb_status.isChecked();
    }

    public void setChecked(boolean checked) {
        cb_status.setChecked(checked);
    }
}
