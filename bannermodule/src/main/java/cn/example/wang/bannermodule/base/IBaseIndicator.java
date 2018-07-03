package cn.example.wang.bannermodule.base;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by WANG on 2018/5/25.
 */

public interface IBaseIndicator {

    void attachBannerView(Context context, ViewGroup parent);

    void ViewCount(int viewCount);

    void preSelectedPage(int preIndex);

    void currentSelectedPage(int currentIndex);
}
