package view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.obdstar.usbcamera.R;

import java.util.ArrayList;
import java.util.List;

import adapter.PhotoPagerAdapter;
import app.CommonActivity;
import utils.ClickEffectUtil;

/**
 * Created by zlb on 2017/10/30.
 */

public class BrowsePhoto extends CommonActivity implements View.OnClickListener {

    private int mPosition;

    private List<String> allPicturePaths;
    private ViewPager mViewPager;
    private TextView mTvPosition;
    private ImageView mIvBack, mIvImg;

    private PhotoPagerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_pictures);

        Bundle bundle = this.getIntent().getExtras();
        mPosition = bundle.getInt("position");
        allPicturePaths = (List<String>) bundle.getSerializable("mData");
        initUI();
        initData();
    }

    private void initUI() {

        mIvBack = findView(R.id.iv_back);
        mIvImg = findView(R.id.iv_img);

        ClickEffectUtil.set(mIvBack);
        ClickEffectUtil.set(mIvImg);

        mViewPager = findView(R.id.vp_photo);
        mTvPosition = findView(R.id.tv_position);

        mTvPosition.setText(mPosition + 1 + "/" + allPicturePaths.size());

    }


    private void initData() {

        mAdapter = new PhotoPagerAdapter(getApplicationContext(), allPicturePaths);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mPosition);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTvPosition.setText(position + 1 + "/" + allPicturePaths.size());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

            case R.id.iv_img:
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

            default:
                break;
        }
    }
}
