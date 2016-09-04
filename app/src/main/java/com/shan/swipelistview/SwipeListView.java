package com.shan.swipelistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * 自定义listView,实现侧滑功能
 * Created by 陈俊山 on 2016/9/4.
 */
public class SwipeListView extends ListView {
    public static int MODE_FORBID = 0;//禁止侧滑模式

    public static int MODE_RIGHT = 1;//从右向左滑出菜单模式

    private int mode = MODE_FORBID;//当前的模式

    private int rightLength = 0;//右侧菜单的长度

    private int slidePosition;//当前滑动的ListView　position

    private int downY;//手指按下X的坐标

    private int downX;//手指按下Y的坐标

    private View itemView;//ListView的item

    private Scroller scroller;//滑动类

    private int mTouchSlop;//认为是用户滑动的最小距离

    private boolean canMove = false;//判断是否可以侧向滑动

    private boolean isSlided = false;//标示是否完成侧滑

    public SwipeListView(Context context) {
        this(context, null);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideMode);
        mode = a.getInt(R.styleable.SlideMode_mode, 0);
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideMode);
        mode = a.getInt(R.styleable.SlideMode_mode, 0);
        scroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 处理我们拖动ListView item的逻辑
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        int lastX = (int) ev.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //默认不处理当前View的事件，即没有侧滑菜单
                if (this.mode == MODE_FORBID) {
                    return super.onTouchEvent(ev);
                }

                // 侧滑状态判断
                if (isSlided) {
                    scrollBack();
                    return false;
                }

                // 滚动是否结束
                if (!scroller.isFinished()) {
                    return false;
                }

                downX = (int) ev.getX();
                downY = (int) ev.getY();
                slidePosition = pointToPosition(downX, downY);

                // 无效的position
                if (slidePosition == AdapterView.INVALID_POSITION) {
                    return super.onTouchEvent(ev);
                }

                itemView = getChildAt(slidePosition - getFirstVisiblePosition());

                //右侧菜单的长度
                if (this.mode == MODE_RIGHT) {
                    this.rightLength = -itemView.getPaddingRight();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!canMove && slidePosition != AdapterView.INVALID_POSITION
                        && (Math.abs(ev.getX() - downX) > mTouchSlop
                        && Math.abs(ev.getY() - downY) < mTouchSlop)) {
                    int offsetX = downX - lastX;
                    if (offsetX > 0 && this.mode == MODE_RIGHT) {
                        canMove = true;//从右向左滑
                    } else {
                        canMove = false;
                    }
                    MotionEvent cancelEvent = MotionEvent.obtain(ev);//侧滑时ListView的OnItemClickListener事件的屏蔽
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    onTouchEvent(cancelEvent);
                }

                if (canMove) {
                    requestDisallowInterceptTouchEvent(true);//侧滑动时，ListView不上下滚动
                    // 根据X坐标的差可以得到手指滑动方向，本例子可以根据自己的需要去灵活修改（左边划出菜单，右边划出菜单，或者左右均可）
                    int deltaX = downX - lastX;
                    if (deltaX > 0 && this.mode == MODE_RIGHT) {
                        itemView.scrollTo(deltaX, 0);//X坐标差大于0手指向右滑动
                    } else {
                        itemView.scrollTo(0, 0);
                    }
                    return true;
                }
            case MotionEvent.ACTION_UP:
                if (canMove) {
                    canMove = false;
                    scrollByDistanceX();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
     */
    private void scrollByDistanceX() {
        //当前模式不允许滑动，则直接返回
        if (this.mode == MODE_FORBID) {
            return;
        }

        if (itemView.getScrollX() > 0 && this.mode == MODE_RIGHT) {
            if (itemView.getScrollX() >= rightLength / 2) {
                scrollLeft();//从右向左滑
            } else {
                scrollBack();//滚回原始位置
            }
        } else {
            scrollBack();//滚回原始位置
        }

    }

    /**
     * 向左滑动
     */
    private void scrollLeft() {
        isSlided = true;
        final int delta = (rightLength - itemView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        scroller.startScroll(itemView.getScrollX(), 0, delta, 0, Math.abs(delta));
        postInvalidate(); // 刷新itemView
    }

    /**
     * 侧滑菜单复原
     */
    private void scrollBack() {
        isSlided = false;
        scroller.startScroll(itemView.getScrollX(), 0, -itemView.getScrollX(), 0, Math.abs(itemView.getScrollX()));
        postInvalidate(); // 刷新itemView
    }

    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true，
        if (scroller.computeScrollOffset()) {
            // 让ListView item根据当前的滚动偏移量进行滚动
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 复原
     */
    public void slideBack() {
        this.scrollBack();
    }

}
