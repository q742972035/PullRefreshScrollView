package com.test.pullrushscrollview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.test.pullrushscrollview.R;

/**
 * Created by 13798 on 2016/8/13.
 */
public class RealizeScrollView extends PullRefreshScrollView{
    public ImageView iv;
    public TextView tv;
    public ProgressBar pb;

    public RealizeScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RealizeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View getHeaderView() {
        View inflate = View.inflate(mContext, R.layout.header_view, null);
        iv = (ImageView) inflate.findViewById(R.id.iv_rotate);
        tv = (TextView) inflate.findViewById(R.id.tv_text);
        pb = (ProgressBar) inflate.findViewById(R.id.pb);
        return inflate;
    }

    @Override
    public MyScrollView getScrollView() {
        return (MyScrollView) View.inflate(mContext, R.layout.scrollview,null);
    }
}
