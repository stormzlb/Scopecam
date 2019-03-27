package view.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import log.NLog;
import urbetter.opengles.GLFrameRenderer;
import urbetter.opengles.GLFrameSurface;
import urbetter.usbcam.UsbCamControl;

/**
 * Created by zlb on 2017/10/18.
 */

public class UsbCrameaFragment extends Fragment {

    private CamAsyncTask mCamAsyncTask;
    private static final String TAG = UsbCrameaFragment.class.getSimpleName();
    int mw = 4000;
    int mh = 4000;


    public GLFrameSurface mGLSurface;
    public GLFrameRenderer mGLFRenderer;
    public DisplayMetrics mDisplaysMetrics;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        NLog.d(TAG, "UsbCrameaFragment:onAttach: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NLog.d(TAG, "UsbCrameaFragment:onCreate: ");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NLog.d(TAG, "UsbCrameaFragment:ActivityCreated: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NLog.d(TAG, "UsbCrameaFragment:onDestroy: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        NLog.d(TAG, "UsbCrameaFragment:onDetach: ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "UsbCrameaFragment:onCreateView: ");
        mDisplaysMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDisplaysMetrics);
        mGLSurface = new GLFrameSurface(getActivity());
        mGLSurface.setEGLContextClientVersion(2);
        mGLFRenderer = new GLFrameRenderer(mGLSurface, mDisplaysMetrics);
        mGLSurface.setRenderer(mGLFRenderer);
        return mGLSurface;

    }

    public class CamAsyncTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... strings) {
            NLog.d(TAG, "UsbCrameaFragment:doInBackground:");
            int index = 0;
            int fd = 0;
            while (true) {

                if (index == 1) {
                    UsbCamControl.CamClose(fd);
                }
                if (isCancelled()) {
                    NLog.d(TAG, "UsbCrameaFragment:doInBackground cancel：");
                    return 0;
                }
                fd = UsbCamControl.CamOpen(strings[0], mw, mh);
                if (fd == 0) {
                    continue;
                } else {
                    index = 1;
                }
                mw = UsbCamControl.CamGetWidth(fd);
                mh = UsbCamControl.CamGetHeight(fd);
                mGLFRenderer.update(mw, mh);
                while (isCancelled() == false) {
                    try {
                        if (index == 1) {
                            byte[] buffer = UsbCamControl.CamGetData(fd);
                            if (buffer != null) {
                                byte[] y = new byte[mw * mh];
                                byte[] u = new byte[mw * mh / 2];
                                byte[] v = new byte[mw * mh / 2];
                                int a = 0;
                                int b = 0;
                                for (int i = 0; i < mw * mh * 2; i = i + 4) {
                                    y[a++] = buffer[i];
                                    u[b] = buffer[i + 1];
                                    y[a++] = buffer[i + 2];
                                    v[b++] = buffer[i + 3];
                                }
                                mGLFRenderer.update(y, mw * mh, u, mw * mh / 2, v, mw * mh / 2);
                            }
                        }
                    } catch (Exception e) {
                        if (index == 1) {
                            UsbCamControl.CamClose(fd);
                        }
                        index = 0;
                        e.printStackTrace();
                        break;
                    }
                }
                if (index == 1) {
                    UsbCamControl.CamClose(fd);
                }
                index = 0;
            }

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NLog.d(TAG, "UsbCrameaFragment:onDestroyView: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        NLog.d(TAG, "UsbCrameaFragment:onStop: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        NLog.d(TAG, "UsbCrameaFragment:onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamAsyncTask = new CamAsyncTask();
        mCamAsyncTask.execute("/dev/video7");
        NLog.d(TAG, "UsbCrameaFragment:onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamAsyncTask != null) {
            mCamAsyncTask.cancel(true);
            mCamAsyncTask = null;
        }
        NLog.d(TAG, "UsbCrameaFragment:onPause: ");
    }

}
