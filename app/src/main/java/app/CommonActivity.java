package app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.obdstar.usbcamera.R;

/**
 * Created by zlb on 2017/9/29.
 */

public class CommonActivity extends AppCompatActivity {

    private Dialog myDialog;
    private TextView tv_loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 将activity设置为全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public <T extends View> T findView(int id) {
        T view = (T) findViewById(id);
        return view;
    }

    public <T extends View> T findView(int id, View root_view) {
        T view = (T) root_view.findViewById(id);
        return view;
    }

    /**
     * 现实进度条
     *
     * @param str
     */
    public void showDialogLoading(String str) {
        if (myDialog == null) {
            View view_loading = getLayoutInflater().inflate(R.layout.dialog_loading, null);
            tv_loading = findView(R.id.tv_loading, view_loading);
            myDialog = new AlertDialog.Builder(this, R.style.dialog_notitle).setView(view_loading).create();
        }
        tv_loading.setText(str);
        myDialog.show();
    }

    /**
     * 转圈圈消失
     */
    public void dismissDialog() {
        if (null != myDialog && myDialog.isShowing()) {
            myDialog.dismiss();
        }
    }
}
