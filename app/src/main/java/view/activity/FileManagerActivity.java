package view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.obdstar.usbcamera.R;

import app.CommonActivity;
import log.NLog;
import utils.ClickEffectUtil;

/**
 * Created by zlb on 2017/10/18.
 */

public class FileManagerActivity extends CommonActivity implements View.OnClickListener {
    private static final String TAG = FileManagerActivity.class.getSimpleName();
    private ImageView mIvPhoto;
    private ImageView mIvBack;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        initView();
    }

    private void initView() {

        mIvPhoto = findView(R.id.iv_photo);
        //mIvVideo = findView(R.id.iv_video);
        mIvBack = findView(R.id.iv_back);

        ClickEffectUtil.set(mIvPhoto);
        //  ClickEffectUtil.set(mIvVideo);
        ClickEffectUtil.set(mIvBack);

        mIvPhoto.setOnClickListener(this);
        // mIvVideo.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_photo:
                Intent intent = new Intent(this, PhotoActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

            case R.id.iv_back:
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "FileManagerActivity:onResume: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "FileManagerActivity:onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "FileManagerActivity:onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NLog.d(TAG, "FileManagerActivity:onDestroy: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "FileManagerActivity:onRestart: ");
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
