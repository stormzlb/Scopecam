package crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CshHandler implements UncaughtExceptionHandler {
    public static final String DEVICE_MODEL = "Scopecam";

    public static final String TAG = "CshHandler";
    private static final String SAVE_PTAH = Environment.getExternalStorageDirectory() + "/" + DEVICE_MODEL + "/crash/";
    private UncaughtExceptionHandler mDefaultHandler;
    private static CshHandler INSTANCE = new CshHandler();
    private Context mContext;
    private Map<String, String> infos = new HashMap<>();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private CshHandler() {
    }

    public static CshHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Looper.loop();
            }
        }.start();
        collectDeviceInfo(mContext);
        saveCrashInfo2File(ex);
        return true;
    }

    private void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
            }
        }
    }

    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = getFile(SAVE_PTAH);
                if (!dir.exists()) {
                    makeRootDirectory(SAVE_PTAH);
                }
                FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath() + "/" + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
        }
        return null;
    }

    public static File getFile(String filePath) {
        String pathTmp = "/";
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        }
        String[] files = filePath.split("/");
        for (String strFile : files) {
            String str = getValidFilePath(pathTmp, strFile);
            if (str == null) {
                return file;
            }
            pathTmp = str;
        }
        file = new File(pathTmp);
        return file;
    }

    private static String getValidFilePath(String filePath, String fileName) {
        String validPath = null;
        if (filePath == null || filePath.length() == 0 || fileName == null || fileName.length() == 0) {
            return filePath;
        }
        File file = new File(filePath);
        if (!file.isDirectory()) {
            return null;
        }
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.getName().equalsIgnoreCase(fileName)) {
                if (filePath.equals("/")) {
                    validPath = filePath + fileName;
                } else {
                    validPath = filePath + "/" + fileName;
                }
                break;
            }
        }
        return validPath;
    }

    public static int makeRootDirectory(String filePath) {
        int ret;

        File file;
        try {
            file = getFile(filePath);
            if (!file.exists()) {
                if (creatDirectory(filePath)) {
                    ret = 0;
                } else {
                    ret = -1;
                }
            } else {
                ret = 1;
            }
        } catch (Exception e) {
            ret = -1;
        }
        return ret;
    }

    private static boolean creatDirectory(String filePath) {
        String pathTmp = "/";
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        String[] files = filePath.split("/");
        for (int i = 0; i < files.length; i++) {
            String str = getValidFilePath(pathTmp, files[i]);
            if (str == null) {
                if (!new File(pathTmp + "/" + files[i]).mkdir()) {
                    return false;
                }
                str = pathTmp + "/" + files[i];
            }
            pathTmp = str;
        }
        return true;
    }
}
