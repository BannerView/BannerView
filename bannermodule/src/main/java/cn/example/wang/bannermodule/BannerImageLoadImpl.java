package cn.example.wang.bannermodule;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.example.wang.bannermodule.base.IBaseImageLoad;
import cn.example.wang.bannermodule.listener.BannerPagerClickListener;

/**
 * Created by WANG on 2018/5/18.
 * 如果用户需要 可以自己更改里面的逻辑 不能修改返回值
 */

public class BannerImageLoadImpl implements IBaseImageLoad {


    @Override
    public View createImageView(Context context, ViewGroup parent, ImageView.ScaleType scaleType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_imageview, parent, false);
        return view;
    }

    @Override
    public void  loadImgForNet(Context context, ImageView imageView, String url) {
        if(!TextUtils.isEmpty(url)) {
            Glide.with(context.getApplicationContext()).load(url).dontAnimate().into(imageView);
        }
    }

    @Override
    public void  loadImgFoeRes(Context context, ImageView imageView, int res) {
          imageView.setImageResource(res);
    }

    @Override
    public void clickListener(final View view, final int index , final BannerPagerClickListener bannerPagerClickListener) {
        if(null == view || null == bannerPagerClickListener || index < 0){
            return;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerPagerClickListener.pagerClickListener(view,index);
            }
        });
    }
}
