package com.test.pullrushscrollview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by 13798 on 2016/8/13.
 */
public class MyScrollView extends ScrollView{


    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener!=null)
            listener.scrollChanged(l, t, oldl, oldt);
    }


    private ScrollChangedListener listener;
    public void setScrollChangedListener(ScrollChangedListener listener){
        this.listener = listener;
    }


    public interface ScrollChangedListener{
        void scrollChanged(int l, int t, int oldl, int oldt);
    }
}
