package cn.example.wang.bannerviewdemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;

import cn.example.wang.bannermodule.BannerIndicatorManagerImpl;
import cn.example.wang.bannermodule.listener.BannerOnPagerChangeListener;
import cn.example.wang.bannermodule.listener.BannerPagerClickListener;
import cn.example.wang.bannermodule.view.BannerViewLayout;


public class MainActivity extends AppCompatActivity {

    //添加注释
    BannerViewLayout bannerView;
    List<String> imageurls;
    List<Integer> recLists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bannerView = findViewById(R.id.bannerView);

        imageurls = new ArrayList<>();
        imageurls.add("http://img.zcool.cn/community/038c0ee5744f9a500000025ae5acd2a.jpg");
        imageurls.add("http://imgstore.cdn.sogou.com/app/a/100540002/402468.jpg");
        imageurls.add("http://pic21.photophoto.cn/20111106/0020032891433708_b.jpg");
        imageurls.add("http://pic1.win4000.com/wallpaper/d/57a9a2955d281.jpg");
        imageurls.add("http://imgsrc.baidu.com/imgad/pic/item/0bd162d9f2d3572c25e340088013632763d0c3e5.jpg");

        recLists = new ArrayList<>();

        recLists.add(R.mipmap.timg);
        recLists.add(R.mipmap.b);
        recLists.add(R.mipmap.c);
        recLists.add(R.mipmap.d);
        recLists.add(R.mipmap.f);


        initBannerView(recLists);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> banners = new ArrayList<>();
                banners.add("http://img05.tooopen.com/images/20140728/sy_67611752335.jpg");
                banners.add("http://pic21.photophoto.cn/20111106/0020032891433708_b.jpg");
                banners.add("http://pic2.ooopic.com/12/42/25/02bOOOPIC95_1024.jpg");
                bannerView.reset(banners);
            }
        });
    }

    private void initBannerView(List<?> imageurls) {


        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.width = dip2px(this,300);
        bannerView
                .setViewPagerLayoutParams(layoutParams)
                .setViewpagerMargin(dip2px(this,10))
                .setStartPosition(0)
                .setIndicator(new BannerIndicatorManagerImpl())
                .setImagUrls(imageurls)
                .autoPlay(false)
                .setDelayTimeForMillis(2000)
                .create();

        bannerView.setBannerPagerClickListener(new BannerPagerClickListener() {
            @Override
            public void pagerClickListener(View view, int position) {
                Intent intent = new Intent(MainActivity.this, CeshiActivity.class);
                startActivity(intent);
                //Toast.makeText(MainActivity.this, ""+position, Toast.LENGTH_SHORT).show();
            }
        });

        bannerView.setBannerOnPagerChangeListener(new BannerOnPagerChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bannerView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bannerView.onStop();
    }

}
