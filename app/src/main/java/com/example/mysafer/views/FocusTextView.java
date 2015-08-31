package com.example.mysafer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Along on 2015/8/30.
 * 获取焦点的TextView
 */
public class FocusTextView extends TextView {
    //有style样式
    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //有属性时
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //使用代码new对象时
    public FocusTextView(Context context) {
        super(context);
    }

    //强制返回true
    @Override
    public boolean isFocused() {
        return true;
    }
}
