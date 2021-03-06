package cn.example.wang.bannerviewdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.example.wang.bannerviewdemo.banner.BannerImageLoadImpl;
import cn.example.wang.bannermodule.handler.WeakHandler;
import cn.example.wang.bannermodule.base.IBaseImageLoad;
import cn.example.wang.bannermodule.base.IBaseIndicator;
import cn.example.wang.bannermodule.listener.BannerOnPagerChangeListener;
import cn.example.wang.bannermodule.listener.BannerPagerClickListener;
import cn.example.wang.bannermodule.transformer.ABaseTransformer;
import cn.example.wang.bannermodule.view.BannerViewPager;

/**
 * Created by WANG on 2018/5/18.
 * 1.实现类似画廊特效的时候最好设置 setOffscreenPageLimit 为显示的图片的数量.
 * 2.reset 方法跟create方法不能同时调用.reset的时候会把你设置的mOffscreenPageLimit重置
 * 3.setIndicatorManager 方法只有在create之前有效
 */

public class Demo extends FrameLayout {

    /**
     * 默认将图片集合手动扩容四张图片,配合轮播
     */
    private final int EXPAND_SOURCE_ALL = 2;


    private String mIndicatorTAG = "indicator_container";

    public Demo(Context context) {
        super(context);
        init(context, null);
    }

    public Demo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Demo(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public Demo setViewpagerMargin(int margin) {
        mViewPager.setPageMargin(margin);
        return this;
    }

    public Demo autoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
        return this;
    }

    public Demo setIndicator(IBaseIndicator baseIndicatorImpl) {
        if (null == baseIndicatorImpl) {
            return this;
        }
        this.mUserIndicator = true;
        this.mBaseIndicator = baseIndicatorImpl;
        return this;
    }

    public Demo setDelayTimeForMillis(long delayTime) {
        this.mDelayTime = delayTime;
        return this;
    }

    public Demo setDelayTimeForSecond(int second) {
        this.mDelayTime = second * 1000;
        return this;
    }

    public Demo setImageLoad(IBaseImageLoad mImageLoad) {
        this.mImageLoad = mImageLoad;
        return this;
    }

    public Demo setStartPosition(int startPosition) {
        this.mStartPosition = startPosition + 2;
        return this;
    }

    public Demo setScaleType(ImageView.ScaleType scaleType) {
        this.mScaleType = scaleType;
        return this;
    }

    public Demo setImagRecs(List<Integer> data) {
        clearData();
        this.mImagUrls.addAll(data);
        return this;
    }

    public Demo setImagUrls(List<?> data) {
        clearData();
        this.mImagUrls.addAll(data);
        return this;
    }

    public Demo setViewPagerLayoutParams(LayoutParams layoutParams) {
        layoutParams.gravity = Gravity.CENTER;
        mViewPager.setLayoutParams(layoutParams);
        return this;
    }

    public Demo addPagerTransformer(ABaseTransformer transformer) {
        mViewPager.setPageTransformer(true, transformer);
        return this;
    }

    public Demo setOffscreenPageLimit(int limit) {
        mViewPager.setOffscreenPageLimit(limit+EXPAND_SOURCE_ALL);
        return this;
    }

    private IBaseImageLoad getImageLoad() {
        return new BannerImageLoadImpl();
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

    public void reset(List<String> imageUrl) {
        if (null == imageUrl) {
            return;
        }
        mOffscreenPageLimit = imageUrl.size() + EXPAND_SOURCE_ALL;
        mViewPager.setOffscreenPageLimit(mOffscreenPageLimit);
        mStartPosition = 1;
        clearData();
        this.mImagUrls.addAll(imageUrl);
        create();
    }

    public void onResume() {
        startAutoRuning();
    }

    public void onStop() {
        stopAutoRunging();
    }


    private void clearData() {
        stopAutoRunging();
        this.mViews.clear();
        this.mImagUrls.clear();
        this.mCount = -1;
    }

    private void startAutoRuning() {
        if (isAutoPlay) {
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, mDelayTime);
        }
    }

    private void stopAutoRunging() {
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
            startAutoRuning();
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
                mBaseIndicator.attachBannerView((ViewGroup) child);
                return;
            } else {
                mUserIndicator = false;
            }
        }
    }


    private void setImageData(List<Object> data) {
        mCount = data.size();
        if (mCount <= 0) {
            return;
        }
        if (mUserIndicator) {
            mBaseIndicator.viewCount(mCount);
        }
        for (int i = 0; i < mCount + EXPAND_SOURCE_ALL; i++) {
            if (null == mImageLoad) {
                mImageLoad = getImageLoad();
            }
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
                url = data.get(data.size() - 1);
            } else if (i == 1 || i == (mCount + 1)) {
                url = data.get(0);
            } else {
                url = data.get(i - 1);
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
                int realPosition = position - 1;
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

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    //滑动停止
                    case ViewPager.SCROLL_STATE_IDLE:
                        if (mCurrentPosition == 0) {
                            mViewPager.setCurrentItem(mCount, false);
                        } else if (mCurrentPosition == mCount + 1) {
                            mViewPager.setCurrentItem(1, false);
                        }
                        break;
                    //开始拖拽
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        if (mCurrentPosition == mCount + 1) {
                            mViewPager.setCurrentItem(1, false);
                        } else if (mCurrentPosition == 0) {
                            mViewPager.setCurrentItem(mCount, false);
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
        int realPosition = (position - 1) % mCount;
        if (realPosition < 0) {
            realPosition += mCount;
        }
        return realPosition;
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCount > 1 && isAutoPlay) {
                mCurrentPosition = mCurrentPosition % (mCount+1) + 1;
                if (mCurrentPosition == 1) {
                    mViewPager.setCurrentItem(mCurrentPosition, false);
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
            int realPosition = (position - 1) % mCount;
            mImageLoad.clickListener(view, realPosition, mBannerPagerClickListener);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.e("cc.wang", "ImageAdapter.destroyItem." + position);
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
                startAutoRuning();
            } else if (action == MotionEvent.ACTION_DOWN) {
                stopAutoRunging();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
