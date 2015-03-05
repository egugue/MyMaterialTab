package htoyama.material_tab;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MaterialTabHost extends FrameLayout implements ViewPager.OnPageChangeListener{
    private static final String TAG = MaterialTabHost.class.getSimpleName();

    private final int DISABLE_COLOR = getResources().getColor(R.color.mth_text_disable);
    private final int ABLE_COLOR = getResources().getColor(R.color.mth_text);

    private int mScrollState;
    private ImageView mIndicatorImageView;
    private LinearLayout mTabContainerLayout;
    private OnTabClickListener mOnTabClickListener;

    public static interface OnTabClickListener {
        public void onTabClick(int position);
    }

    public MaterialTabHost(Context context) {
        this(context, null);
    }

    public MaterialTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public void addTab(CharSequence title) {
        final TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.tab_text, null);
        tv.setText(title);

        int height = getContext().getResources().getDimensionPixelSize(R.dimen.mth_height);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, height, 1);
        tv.setLayoutParams(lp);

        int color = mTabContainerLayout.getChildCount() == 0 ? ABLE_COLOR : DISABLE_COLOR;
        tv.setTextColor(color);

        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getChildPosition(v);
                if (position < 0) return;

                updateTab(position);
                mIndicatorImageView.animate()
                        .setDuration(200)
                        .translationX(v.getX());

                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onTabClick(position);
                }
            }
        });

        mTabContainerLayout.addView(tv);
    }

    public void setOnTabClickListener(OnTabClickListener listener) {
        mOnTabClickListener = listener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        View view = mTabContainerLayout.getChildAt(position);
        if (view == null) {
            return;
        }

        int mark = (int) (view.getLeft() + positionOffset * view.getWidth());
        mIndicatorImageView.setTranslationX(mark);
        //mIndicatorImageView.setX(mark);
    }

    @Override
    public void onPageSelected(int position) {
        if (mScrollState == ViewPager.SCROLL_STATE_SETTLING) {
            updateTab(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mScrollState = state;
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

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Log.d(TAG, "dispathDraw");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.merge_tab_host_content, this, true);
        mTabContainerLayout = (LinearLayout) findViewById(R.id.mtb_tab_conteiner);
        mIndicatorImageView = (ImageView) findViewById(R.id.mtb_tab_indicator);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialTabHost);
        TypedValue outValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();

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

        a.recycle();
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

}