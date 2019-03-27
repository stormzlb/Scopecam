package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

/**
 * Created by zlb on 2017/10/30.
 */

public class PhotoPagerAdapter extends PagerAdapter {

    private List<String> mList;
    private Context mContext;

    public PhotoPagerAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * 必须要实现的方法
     * 每次滑动的时实例化一个页面,ViewPager同时加载3个页面,假如此时你正在第二个页面，向左滑动，
     * 将实例化第4个页面
     **/
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mContext);
        photoView.enable();
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        photoView.setLayoutParams(layoutParams);
        photoView.setBackgroundColor(Color.BLACK);
        ((ViewPager) container).addView(photoView);
        Glide.with(mContext)
                .load(new File(mList.get(position)))
                .centerCrop()
                .crossFade()
                .into(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageView imageView = (PhotoView) object;
        if (imageView == null) {
            return;
        }
        Glide.clear(imageView);     //核心，解决OOM
        ((ViewPager) container).removeView(imageView);
    }
}
