package com.example.mysafer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mysafer.R;

/**
 * 设置中心click的View
 * Created by Along on 2015/8/31.
 */
public class SettingClickView extends RelativeLayout {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.example.mysafer";

    private TextView tv_title;
    private TextView tv_desc;

    private String title;
    private String desc;


    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //根据命名空间和属性名称获取属性值
        title = attrs.getAttributeValue(NAMESPACE, "click_title");
        desc = attrs.getAttributeValue(NAMESPACE, "click_desc");
        initView();
    }

    public SettingClickView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        //将布局文件给到SettingClickView中
        View view = View.inflate(getContext(), R.layout.setting_click, this);
        tv_title = (TextView)findViewById(R.id.tv_auto_update_title);
        tv_desc = (TextView)findViewById(R.id.tv_auto_update_desc);
        setTitle(title);
        setDesc(desc);
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setDesc(String desc) {
        tv_desc.setText(desc);
    }
}
