package app;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;

import com.obdstar.usbcamera.BuildConfig;

import java.io.File;
import java.util.List;

import crash.CshHandler;
import log.Logger;
import log.NLog;
import utils.ToastUtil;

import static com.tencent.bugly.crashreport.CrashReport.initCrashReport;

/**
 * Created by zlb on 2017/10/17.
 */

public class CameraApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        CshHandler cshHandler = CshHandler.getInstance();
        cshHandler.init(getApplicationContext());
        initCrashReport(getApplicationContext(), "6216a5977a", false);
        initNlog();
    }

    private void initNlog() {

        NLog.setDebug(BuildConfig.DEBUG, Logger.VERBOSE);

        if (BuildConfig.LOG_DEBUG) {
            String offlineLogDirPath;
            if (true) { //hasSdWritePermission(this)
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CameraLog" + File.separator + "log";

                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }
                offlineLogDirPath = path;
            } else {
                File dir = getExternalFilesDir("Xycd");
                String path = dir.getAbsolutePath() + File.separator + "log";
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }
                offlineLogDirPath = path;
            }
            //ToastUtil.showShort(getApplicationContext(), offlineLogDirPath);
            NLog.trace(Logger.TRACE_ALL, offlineLogDirPath);
        }
    }


}
