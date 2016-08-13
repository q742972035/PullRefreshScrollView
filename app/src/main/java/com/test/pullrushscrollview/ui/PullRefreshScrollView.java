package com.test.pullrushscrollview.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by 13798 on 2016/8/13.
 */
public abstract class PullRefreshScrollView extends FrameLayout implements MyScrollView.ScrollChangedListener {
    protected Context mContext;
    /**
     * 头部
     */
    private View headerView;
    private MyScrollView scrollView;

    private int headerHeight;

    /**
     * 下拉刷新状态
     */
    public static final int PULL_REFRESH = 0x001;

    /**
     * 松开刷新状态
     */
    public static final int LOOSEN_REFRESH = 0x002;

    /**
     * 正在刷新状态
     */
    public static final int REFRESHING = 0x003;

    /**
     * 初始化状态
     */
    private int mState = PULL_REFRESH;


    public PullRefreshScrollView(Context context) {
        this(context, null);
    }

    public PullRefreshScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initHeader();
        initScrollView();
        initView();
    }

    private void initHeader() {
        headerView = getHeaderView();
        if (headerView != null) {
            headerView.measure(0, 0);
            headerView.setPadding(0, -headerView.getMeasuredHeight(), 0, 0);
            headerHeight = headerView.getPaddingTop();
        }
    }

    public abstract View getHeaderView();

    private void initScrollView() {
        scrollView = getScrollView();
        if (scrollView == null)
            throw new RuntimeException("这是下拉刷新ScrollView控件，请勿返回空！");
        scrollView.setScrollChangedListener(this);
        scrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return touchEvent(v, event);
            }
        });
    }

    public abstract MyScrollView getScrollView();

    private void initView() {
        // 最外层layout
        LinearLayout root = new LinearLayout(mContext);
        root.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams rootLayout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.setLayoutParams(rootLayout);
        // 仅仅可以添加一次
        addView(root);
        if (headerView != null)
            root.addView(headerView);
        root.addView(scrollView);
    }


    /**
     * 通过偏移量移动头部
     *
     * @param dY
     */
    private void scrollHeaderBy(int dY) {
        int paddingTop = headerView.getPaddingTop();
        int end = paddingTop + dY;
        headerView.setPadding(0, end <= headerHeight ? headerHeight : end, 0, 0);
    }

    /**
     * 关闭下拉刷新
     */
    private void closeRefreshing() {
        scrollTo(0, 0);
        if (mState != PULL_REFRESH)
            setState(PULL_REFRESH);
        headerView.setPadding(0, headerHeight, 0, 0);
        if (mCallback != null)
            mCallback.stopRefresh();
    }

    /**
     * 顶部高度，默认为0
     */
    private int currentTop;

    @Override
    public void scrollChanged(int l, int t, int oldl, int oldt) {
        currentTop = t;
    }

    /**
     * 设置头部返回动画执行时间
     *
     * @param duration
     */
    private void setDuration(int duration) {
        mDuration = duration;
    }

    private void setDamp(float damp) {
        mDamp = damp;
    }

    /**
     * 阻尼系数
     */
    private float mDamp = 1;
    private boolean isUp = true;
    private int lastY;
    private boolean isPulling;
    private int endHeight;
    private ValueAnimator valueAnimator;
    private int mDuration = 500;

    private boolean touchEvent(View v, MotionEvent event) {
        if (headerView == null)
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (isUp) {
                    isUp = false;
                    lastY = (int) event.getRawY();
                    // 清除动画状态
                    if (valueAnimator != null && valueAnimator.isRunning())
                        valueAnimator.cancel();
                }
                // 是否正在下拉
                int currentY = (int) event.getRawY();
                float dY = (currentY - lastY) * mDamp;
                isPulling = dY > 0;

                lastY = currentY;

                if (headerView != null) {
                    // 头部将要移动到的地方
                    int paddintTop = (int) (headerView.getPaddingTop() + dY);
                    if (paddintTop > headerHeight && currentTop == 0) {
                        if (isPulling && mState == REFRESHING && paddintTop > 0)
                            return false;
                        scrollHeaderBy((int) dY);
                        // 露出头的百分比，超过1转松开刷新
                        float percent = (headerView.getPaddingTop() + Math.abs(headerHeight)) * 1.0f / Math.abs(headerHeight);
                        if (mCallback != null) {
                            mCallback.dragToLoosen(percent <= 1 ? percent : 1, percent <= 1 ? (int) dY : 0);
                        }
                        if (percent <= 1) {
                            if (mState == LOOSEN_REFRESH)
                                setState(PULL_REFRESH);
                        } else {
                            if (mState != LOOSEN_REFRESH)
                                setState(LOOSEN_REFRESH);
                        }
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isUp = true;
                // 最终位置
                endHeight = -1;
                if (mState == PULL_REFRESH)
                    endHeight = headerHeight;
                else if (mState == LOOSEN_REFRESH)
                    endHeight = 0;
                if (endHeight != -1) {
                    valueAnimator = ValueAnimator.ofInt(headerView.getPaddingTop(), endHeight);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int padding = (int) animation.getAnimatedValue();
                            int lastPadding = padding;
                            float percent = (padding + Math.abs(headerHeight)) * 1.0f / Math.abs(headerHeight);
                            if (padding < 0) {
                                if (mCallback != null)
                                    mCallback.dragToLoosen(percent, padding - lastPadding);
                            }
                            headerView.setPadding(0, padding, 0, 0);
                        }
                    });
                    valueAnimator.addListener(new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (endHeight == 0) {
                                setState(REFRESHING);
                            } else if (endHeight == headerHeight)
                                setState(PULL_REFRESH);
                        }
                    });

                    valueAnimator.setDuration(mDuration);
                    valueAnimator.start();
                }

                break;
        }

        return false;
    }

    private void setState(int state) {
        switch (state) {
            // 下拉刷新状态
            case PULL_REFRESH:
                if (mState != PULL_REFRESH && headerView != null) {
                    mState = PULL_REFRESH;
                    if (mCallback != null)
                        mCallback.toRullRefresh();
                }
                break;
            // 松开刷新状态
            case LOOSEN_REFRESH:
                if (mState != LOOSEN_REFRESH && headerView != null) {
                    mState = LOOSEN_REFRESH;
                    if (mCallback != null)
                        mCallback.toLoosenRefresh();
                }
                break;
            // 正在刷新状态
            case REFRESHING:
                if (mState != REFRESHING && headerView != null) {
                    mState = REFRESHING;
                    if (mCallback != null)
                        mCallback.toRefreshing();
                }
                break;
            default:
                break;
        }
    }


    /**
     * 避免繁琐的判断
     */
    private StateCallBack mCallback;

    public void setStateCallBace(StateCallBack callback) {
        mCallback = callback;
    }

    public interface StateCallBack {
        /**
         * 从下拉刷新到松开刷新的瞬间
         */
        void toLoosenRefresh();

        /**
         * 从松开刷新到下拉刷新的瞬间
         */
        void toRullRefresh();

        /**
         * toLoading
         * 状态为正在刷新
         */
        void toRefreshing();


        /**
         * 从下拉刷新拖拽到松开刷新的移动百分比
         *
         * @param percent 0-1
         * @param dY      调用间隔的偏移量
         */
        void dragToLoosen(float percent, int dY);


        /**
         * 停止正在刷新（被动执行）
         */
        void stopRefresh();

    }

    class SimpleAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    public class Builder {
        private PullRefreshScrollView mLv;

        public Builder(PullRefreshScrollView view) {
            mLv = view;
        }

        /**
         * 设置动画时间
         */
        public Builder setDuration(int duration) {
            mLv.setDuration(duration);
            return this;
        }

        /**
         * 设置滑动时阻尼系数
         */
        public Builder setDamp(float damp) {
            mLv.setDamp(damp);
            return this;
        }


        /**
         * 关闭刷新
         */
        public Builder closeRefreshing() {
            mLv.closeRefreshing();
            return this;
        }

    }
}
