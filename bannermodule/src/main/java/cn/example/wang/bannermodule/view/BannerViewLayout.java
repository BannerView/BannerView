package cn.example.wang.bannermodule.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.example.wang.bannermodule.handler.WeakHandler;
import cn.example.wang.bannermodule.base.IBaseImageLoad;
import cn.example.wang.bannermodule.base.IBaseIndicator;
import cn.example.wang.bannermodule.listener.BannerOnPagerChangeListener;
import cn.example.wang.bannermodule.listener.BannerPagerClickListener;
import cn.example.wang.bannermodule.transformer.ABaseTransformer;

/**
 * Created by WANG on 2018/5/18.
 * 1.实现类似画廊特效的时候最好设置 setOffscreenPageLimit 为显示的图片的数量.
 * 2.reset 方法跟create方法不能同时调用.reset的时候会把你设置的mOffscreenPageLimit重置
 * 3.setIndicatorManager 方法只有在create之前有效
 */

public class BannerViewLayout extends FrameLayout {

    /**
     * 默认将图片集合手动扩容四张图片,配合轮播
     */
    private final int EXPAND_SOURCE_ALL = 4;

    /**
     * 图片集合单侧扩容的数量
     */
    private final int EXPAND_SOURCE_ONE_SIDE = 2;

    private String mIndicatorTAG = "indicator_container";

    public BannerViewLayout(Context context) {
        super(context);
        init(context, null);
    }

    public BannerViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BannerViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private BannerViewPager mViewPager;
    private List<Object> mImagUrls;
    private List<View> mViews;
    private IBaseImageLoad mImageLoad;
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.CENTER_CROP;
    private ImageAdapter mImageAdapter;
    private int mCount;
    private volatile int mCurrentPosition = 1;
    private int mStartPosition = 2;
    private boolean isAutoPlay = true;
    private volatile long mDelayTime = 2000;
    private boolean isResetData = false;
    private WeakHandler mHandler = new WeakHandler();
    private BannerPagerClickListener mBannerPagerClickListener;
    private BannerOnPagerChangeListener meOnPagerChangeListener;
    private int mPrePosition = -1;
    private int mOffscreenPageLimit;
    private IBaseIndicator mBaseIndicator;
    private boolean mUserIndicator = false;


    public void setBannerPagerClickListener(BannerPagerClickListener bannerPagerClickListener) {
        this.mBannerPagerClickListener = bannerPagerClickListener;
    }

    public void setBannerOnPagerChangeListener(BannerOnPagerChangeListener bannerPagerClickListener) {
        this.meOnPagerChangeListener = bannerPagerClickListener;
    }

    public BannerViewLayout setViewpagerMargin(int margin) {
        mViewPager.setPageMargin(margin);
        return this;
    }

    public BannerViewLayout autoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
        return this;
    }

    public BannerViewLayout setIndicator(IBaseIndicator baseIndicatorImpl) {
        if (null == baseIndicatorImpl) {
            return this;
        }
        this.mUserIndicator = true;
        this.mBaseIndicator = baseIndicatorImpl;
        return this;
    }

    public BannerViewLayout setDelayTimeForMillis(long delayTime) {
        this.mDelayTime = delayTime;
        return this;
    }

    public BannerViewLayout setDelayTimeForSecond(int second) {
        this.mDelayTime = second * 1000;
        return this;
    }

    public BannerViewLayout setImageLoad(IBaseImageLoad mImageLoad) {
        this.mImageLoad = mImageLoad;
        return this;
    }

    public BannerViewLayout setStartPosition(int startPosition) {
        this.mStartPosition = startPosition + 2;
        return this;
    }

    public BannerViewLayout setScaleType(ImageView.ScaleType scaleType) {
        this.mScaleType = scaleType;
        return this;
    }

    public BannerViewLayout setImagRecs(List<Integer> data) {
        clearData();
        this.mImagUrls.addAll(data);
        return this;
    }

    public BannerViewLayout setImagUrls(List<?> data) {
        clearData();
        this.mImagUrls.addAll(data);
        return this;
    }

    public BannerViewLayout setViewPagerLayoutParams(LayoutParams layoutParams) {
        layoutParams.gravity = Gravity.CENTER;
        mViewPager.setLayoutParams(layoutParams);
        return this;
    }

    public BannerViewLayout addPagerTransformer(ABaseTransformer transformer) {
        mViewPager.setPageTransformer(true, transformer);
        return this;
    }

    public BannerViewLayout setOffscreenPageLimit(int limit) {
        mOffscreenPageLimit = limit + 2;
        return this;
    }

    private void init(Context context, AttributeSet attrs) {
        initViews(context);
    }

    private void initViews(Context context) {
        if (null == mViewPager) {
            mViewPager = new BannerViewPager(context);
            LayoutParams ls = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ls.gravity = Gravity.CENTER;
            mViewPager.setLayoutParams(ls);
            mViewPager.setClipChildren(false);
            this.setClipChildren(false);
            this.addView(mViewPager, 0);
            mImagUrls = new ArrayList<>();
            mViews = new ArrayList<>();
        }
    }

    @Deprecated
    public void reset(List<String> imageUrl) {
        if (null == imageUrl) {
            return;
        }
        mOffscreenPageLimit = imageUrl.size() + EXPAND_SOURCE_ALL;
        mStartPosition = EXPAND_SOURCE_ONE_SIDE;
        clearData();
        this.mImagUrls.addAll(imageUrl);
        create();
    }

    public void onResume() {
        startAutoRunning();
    }

    public void onStop() {
        stopAutoRunnging();
    }


    private void clearData() {
        stopAutoRunnging();
        this.mViews.clear();
        this.mImagUrls.clear();
        this.mCount = -1;
    }

    private void startAutoRunning() {
        if (isAutoPlay) {
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, mDelayTime);
        }
    }

    private void stopAutoRunnging() {
        if (isAutoPlay) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    public void create() {
        if (mImagUrls == null || mImagUrls.size() <= 0) {
            return;
        }
        initIndicator();
        setImageData(mImagUrls);
        setViewPagerData();
        if (isResetData) {
            startAutoRunning();
        }
    }

    private void initIndicator() {
        if (!mUserIndicator) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            String tag = (String) child.getTag();
            if (!TextUtils.isEmpty(tag) && mIndicatorTAG.equals(tag) && child instanceof ViewGroup) {
                mUserIndicator = true;
                mBaseIndicator.attachBannerView(getContext(), (ViewGroup) child);
                return;
            } else {
                mUserIndicator = false;
            }
        }
    }


    private void setImageData(List<Object> data) {
        mCount = data.size();
        if (mCount <= 0 || null == mImageLoad) {
            return;
        }
        if (mUserIndicator) {
            mBaseIndicator.viewCount(mCount);
        }
        for (int i = 0; i < mCount + EXPAND_SOURCE_ALL; i++) {
            ViewGroup view = (ViewGroup) mImageLoad.createImageView(getContext(), this, mScaleType);
            View child = view.getChildAt(0);
            if (null != child && !(child instanceof ImageView)) {
                return;
            }
            ImageView imageView = (ImageView) child;
            Object url;
            //在数据源首位都重复添加两张图片,优化连贯自动轮播效果
            if (i == 0) {
                //最后一张图片
                url = data.get(data.size() - EXPAND_SOURCE_ONE_SIDE);
            } else if (i == 1) {
                url = data.get(data.size() - 1);
            } else if (i == mCount + EXPAND_SOURCE_ONE_SIDE) {
                url = data.get(0);
            } else if (i == mCount + 3) {
                url = data.get(1);
            } else {
                url = data.get(i - EXPAND_SOURCE_ONE_SIDE);
            }
            if (null != url) {
                //网络图片
                if (url instanceof String) {
                    mImageLoad.loadImgForNet(getContext(), imageView, (String) url);
                    //本地资源
                } else if (url instanceof Integer) {
                    mImageLoad.loadImgFoeRes(getContext(), imageView, (Integer) url);
                }
            }

            if (!mViews.contains(view)) {
                mViews.add(view);
            }
        }
    }

    private void setViewPagerData() {
        if (null == mImageAdapter) {
            mImageAdapter = new ImageAdapter();
            initImageViewListener();
        }
        mViewPager.setAdapter(mImageAdapter);
        mViewPager.setCurrentItem(mStartPosition);
        mViewPager.setOffscreenPageLimit(mOffscreenPageLimit);
        mViewPager.setFocusable(true);
        if (mUserIndicator) {
            int realPosition = getRealPosition(mStartPosition);
            mBaseIndicator.currentSelectedPage(realPosition);
            mPrePosition = realPosition;
        }
    }

    private void initImageViewListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int realPosition = position - EXPAND_SOURCE_ONE_SIDE;
                //总会先走一次
                if (hasWindowFocus() && meOnPagerChangeListener != null) {
                    meOnPagerChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                }
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                if (!hasWindowFocus()) {
                    return;
                }
                int realPosition = getRealPosition(position);
                if (realPosition == mPrePosition) {
                    return;
                }
                if (mUserIndicator) {
                    if (mPrePosition >= 0) {
                        mBaseIndicator.preSelectedPage(mPrePosition);
                    }
                    mBaseIndicator.currentSelectedPage(realPosition);
                    mPrePosition = realPosition;
                }
                if (meOnPagerChangeListener != null) {
                    meOnPagerChangeListener.onPageSelected(realPosition);
                }
            }

            /**
             *  <p>
             *     B C A B C A B
             *     0 1 2 3 4 5 6
             * @param state
             */
            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    //滑动停止
                    case ViewPager.SCROLL_STATE_IDLE:
                    //开始拖拽
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        if (mCurrentPosition == (mCount + EXPAND_SOURCE_ONE_SIDE)) {
                            mViewPager.setCurrentItem(mStartPosition, false);
                        } else if (mCurrentPosition == 1) {
                            mViewPager.setCurrentItem(mCount + 1, false);
                        }
                        break;
                    //SCROLL_STATE_SETTLING
                    case ViewPager.SCROLL_STATE_SETTLING:
                        break;
                    default:
                        break;
                }

                if (meOnPagerChangeListener != null) {
                    meOnPagerChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    private int getRealPosition(int position) {
        int realPosition = (position - EXPAND_SOURCE_ONE_SIDE) % mCount;
        if (realPosition < 0) {
            realPosition += mCount;
        }
        return realPosition;
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCount > 1 && isAutoPlay) {
                mCurrentPosition = mCurrentPosition % (mCount + EXPAND_SOURCE_ONE_SIDE) + 1;
                if (mCurrentPosition == 1) {
                    mViewPager.setCurrentItem(EXPAND_SOURCE_ONE_SIDE, false);
                    mHandler.post(mRunnable);
                } else {
                    mViewPager.setCurrentItem(mCurrentPosition);
                    mHandler.postDelayed(mRunnable, mDelayTime);
                }
            }
        }
    };

    class ImageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mViews.get(position);
            container.addView(view);
            int realPosition = (position - EXPAND_SOURCE_ONE_SIDE) % mCount;
            mImageLoad.clickListener(view, realPosition, mBannerPagerClickListener);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isAutoPlay) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_OUTSIDE) {
                startAutoRunning();
            } else if (action == MotionEvent.ACTION_DOWN) {
                stopAutoRunnging();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
