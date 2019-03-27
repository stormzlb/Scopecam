package view.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.obdstar.usbcamera.R;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapter.PhotoAdapter;
import app.CommonActivity;
import log.NLog;
import mode.GridItem;
import app.CameraApplication;
import utils.ClickEffectUtil;
import utils.ImageFileFilter;


/**
 * Created by zlb on 2017/10/18.
 */

public class PhotoActivity extends CommonActivity implements View.OnClickListener {

    private static final String TAG = PhotoActivity.class.getSimpleName();

    private GridView mGridView;
    private ImageView mIvBack;
    private TextView isEmpty;
    private ImageLoaderAsyncTask mLoaderAsyncTask;

    private List<String> mUrl;

    private PhotoAdapter mPhotoAdapter;

    private File mPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Scopecam");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initUI();
        initData();

    }

    public void initUI() {
        isEmpty = findView(R.id.tv_query_none);
        mGridView = findView(R.id.gv_photo);
        mIvBack = findView(R.id.iv_left_back);
        ClickEffectUtil.set(mIvBack);
        mIvBack.setOnClickListener(this);

    }

    public void initData() {

        mUrl = new ArrayList<>();
        mLoaderAsyncTask = new ImageLoaderAsyncTask();
        mLoaderAsyncTask.execute(mPath);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(PhotoActivity.this, BrowsePhoto.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putSerializable("mData", (Serializable) mUrl);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                new AlertDialog.Builder(PhotoActivity.this)
                        .setTitle("警告")
                        .setMessage("确定要删除吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mPhotoAdapter.removeFileImg(position);
                                isEmpty.setVisibility(mPhotoAdapter.getData().size() == 0 ? View.VISIBLE : View.GONE);
                            }
                        }).show();

                return true;
            }
        });

    }

    public class ImageLoaderAsyncTask extends AsyncTask<File, Object, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialogLoading("正在获取数据中...");
        }

        @Override
        protected List<String> doInBackground(File... files) {
            if (!files[0].exists()) {
                files[0].mkdirs();
            }
            ImageFileFilter imageFileFilter = new ImageFileFilter();
            File[] file = files[0].listFiles();
            int fileCount = file.length;
            for (int i = 0; i < fileCount; i++) {
                if (imageFileFilter.accept(file[i])) {
                    mUrl.add(mPath + "/" + file[i].getName().trim());
                }
            }
            return mUrl;
        }

        @Override
        protected void onPostExecute(List<String> data) {
            mPhotoAdapter = new PhotoAdapter(getApplicationContext(), data);
            mGridView.setAdapter(mPhotoAdapter);
            dismissDialog();
            isEmpty.setVisibility(data.size() == 0 ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_back:
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

            default:
                break;
        }

    }

    //返回键返回事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
