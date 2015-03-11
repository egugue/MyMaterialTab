package htoyama.material_tab;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MaterialTabHost extends FrameLayout {
    private final int DISABLE_COLOR = getResources().getColor(R.color.mth_text_disable);
    private final int ABLE_COLOR = getResources().getColor(R.color.mth_text);
    private final int TAB_HEIGHT = getResources().getDimensionPixelSize(R.dimen.mth_height);

    private ImageView mIndicatorImageView;
    private LinearLayout mTabContainerLayout;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    public MaterialTabHost(Context context) {
        this(context, null);
    }

    public MaterialTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(new InternalViewPagerListener());
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public void addTab(CharSequence title) {
        final TextView tv = (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.tab_text, null);
        int textColor = mTabContainerLayout.getChildCount() == 0 ? ABLE_COLOR : DISABLE_COLOR;

        tv.setTextColor(textColor);
        tv.setText(title);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, TAB_HEIGHT, 1));
        tv.setOnClickListener(mInternalTabClickListener);

        mTabContainerLayout.addView(tv);
    }

    private void updateTab(int position) {
        TextView tv;

        for (int i = 0; i < mTabContainerLayout.getChildCount(); i++) {
            if (i == position) continue;

            tv = (TextView) mTabContainerLayout.getChildAt(i);
            tv.setTextColor(DISABLE_COLOR);
        }

        tv = (TextView) mTabContainerLayout.getChildAt(position);
        if (tv != null) {
            tv.setTextColor(ABLE_COLOR);
        }

    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.merge_tab_host_content, this, true);
        mTabContainerLayout = (LinearLayout) findViewById(R.id.mtb_tab_conteiner);
        mIndicatorImageView = (ImageView) findViewById(R.id.mtb_tab_indicator);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialTabHost);
        TypedValue outValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        a.recycle();

        //setup Background color
        theme.resolveAttribute(R.attr.colorPrimary, outValue, true);
        setBackgroundColor(outValue.data);

        //setup Indicator
        //theme.resolveAttribute(R.attr.colorControlActivated, outValue, true);
        theme.resolveAttribute(R.attr.colorAccent, outValue, true);
        int indicatorColor = a.getColor(R.styleable.MaterialTabHost_indicatorColor, outValue.data);
        ColorDrawable indicator = new ColorDrawable(indicatorColor);
        mIndicatorImageView.setImageDrawable(indicator);

        setupIndicatorHeight();
    }

    private void setupIndicatorHeight() {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                if (mTabContainerLayout.getChildCount() == 0) {
                    mIndicatorImageView.setVisibility(View.GONE);
                    return true;
                }

                ViewGroup.LayoutParams lp = mIndicatorImageView.getLayoutParams();
                lp.width = mTabContainerLayout.getChildAt(0).getWidth();
                mIndicatorImageView.setLayoutParams(lp);
                return true;
            }
        });
    }

    private int getChildPosition(View view) {
        for (int i = 0; i <= getChildCount(); i++) {
            if (view.equals(mTabContainerLayout.getChildAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private OnClickListener mInternalTabClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = getChildPosition(v);
            if (position >= 0) {
                mViewPager.setCurrentItem(position);
            }
        }
    };

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            View view = mTabContainerLayout.getChildAt(position);
            if (view == null) {
                return;
            }

            int mark = (int) (view.getLeft() + positionOffset * view.getWidth());
            mIndicatorImageView.setTranslationX(mark);

            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_SETTLING) {
                updateTab(position);
            }

            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    }

}