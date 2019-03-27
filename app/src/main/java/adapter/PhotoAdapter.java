package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.obdstar.usbcamera.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mode.GridItem;

/**
 * Created by zlb on 2017/10/25.
 */

public class PhotoAdapter extends BaseAdapter {

    private List<String> mList;
    private Context mContext;

    public PhotoAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void remove(int index) {
        mList.remove(index);
    }

    public List<String> getData() {
        return mList;
    }

    public void removeFileImg(int position) {

        String selectImagePath = mList.get(position);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Scopecam");
        File[] files = file.listFiles();
        int fileCount = files.length;
        for (int i = 0; i < fileCount; i++) {
            if (files[i].getAbsolutePath().equals(selectImagePath)) {
                File f = files[i];
                f.delete();
            }
        }
        remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.grid_item_photo, null);
            holder = new ViewHolder();
            holder.iv = convertView.findViewById(R.id.iv_photos);
            holder.tv = convertView.findViewById(R.id.tv_text);
            holder.cardView = convertView.findViewById(R.id.cardView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.cardView.setRadius(8);//设置图片圆角的半径大小
        holder.cardView.setCardElevation(8);//设置阴影部分大小
        holder.cardView.setContentPadding(5, 5, 5, 5);//设置图片距离阴影大小
        Glide.with(mContext).load(mList.get(position)).into(holder.iv);
        holder.tv.setText(mList.get(position).replace("/storage/emulated/0/Pictures/Scopecam/", ""));
        return convertView;
    }

    static class ViewHolder {
        private ImageView iv;
        private TextView tv;
        private CardView cardView;
    }
}
