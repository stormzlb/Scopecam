package view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.obdstar.usbcamera.R;

import app.CommonActivity;
import log.NLog;
import urbetter.opengles.GLFrameRenderer;
import utils.ClickEffectUtil;
import view.fragment.UsbCrameaFragment;

public class MainActivity extends CommonActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnManager, btnPictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Fragment usbCameraFragment = new UsbCrameaFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, usbCameraFragment).addToBackStack(null).commit();
        }
        initUI();
        NLog.d(TAG, "MainActivity:onCreate: ");
    }

    public void initUI() {
        btnManager = findView(R.id.btn_manager);
        btnPictures = findView(R.id.btn_pictures);

        ClickEffectUtil.set(btnManager);
        ClickEffectUtil.set(btnPictures);

    }

    @Override
    protected void onStart() {
        super.onStart();
        NLog.d(TAG, "MainActivity:onStart: ");
    }

    @Override
    public void onClick(View view) {

        Intent intent;
        switch (view.getId()) {
            case R.id.btn_manager:
                intent = new Intent(this, FileManagerActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case R.id.btn_pictures:
                GLFrameRenderer.isTakePicture = true;
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NLog.d(TAG, "MainActivity:onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        NLog.d(TAG, "MainActivity:onRestart: i=%s ", "nnnnnnnnn");
    }

    @Override
    protected void onPause() {
        super.onPause();
        NLog.d(TAG, "MainActivity:onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        NLog.d(TAG, "MainActivity:onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NLog.d(TAG, "MainActivity:onDestroy: ");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            System.exit(0);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
