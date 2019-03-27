package view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.obdstar.usbcamera.R;

import app.CommonActivity;

/**
 * Created by zlb on 2017/10/18.
 */

public class SettingActivity extends CommonActivity implements View.OnClickListener {

    private ImageView mIvBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();


    }

    private void initView() {

        mIvBack = findView(R.id.iv_back);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            default:

                break;


        }
    }
}
