package com.test.pullrushscrollview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.test.pullrushscrollview.ui.PullRefreshScrollView;
import com.test.pullrushscrollview.ui.RealizeScrollView;

public class MainActivity extends AppCompatActivity implements PullRefreshScrollView.StateCallBack {
    private RealizeScrollView scrollView;
    private PullRefreshScrollView.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = (RealizeScrollView) findViewById(R.id.rel);
        builder = scrollView.new Builder(scrollView);
        scrollView.setStateCallBace(this);
    }

    @Override
    public void toLoosenRefresh() {
        scrollView.tv.setText("松开刷新");
    }

    @Override
    public void toRullRefresh() {
        scrollView.tv.setText("下拉刷新");
        scrollView.iv.setVisibility(View.VISIBLE);
        scrollView.pb.setVisibility(View.GONE);
    }

    @Override
    public void toRefreshing() {
        scrollView.tv.setText("正在刷新");
        scrollView.iv.setVisibility(View.GONE);
        scrollView.pb.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                builder.closeRefreshing();
            }
        },3000);
    }

    @Override
    public void dragToLoosen(float percent, int dY) {
        scrollView.iv.setRotation(percent*180);
    }

    @Override
    public void stopRefresh() {
        Toast.makeText(this, "刷新完成", Toast.LENGTH_SHORT).show();
    }
}
