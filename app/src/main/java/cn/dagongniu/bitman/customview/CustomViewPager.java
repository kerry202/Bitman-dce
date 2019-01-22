package cn.dagongniu.bitman.customview;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * viewpager高度自适应
 */
public class CustomViewPager extends ViewPager {
    private boolean isCanScroll = true;
    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int indent = getCurrentItem();//当前显示fragment
        int weights = 0;
        Fragment fragment = ((FragmentPagerAdapter) getAdapter()).getItem(indent);
        if (fragment.getView() != null) {
            View view = fragment.getView();
            view.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            weights = view.getMeasuredHeight();
        }
        weights = MeasureSpec.makeMeasureSpec(weights, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, weights);
    }
    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isCanScroll && super.onTouchEvent(ev);
    }
}
