package urbetter.opengles;

import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

//leon zlm20151230
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
//import com.jovision.account.util.utils.Utils;

public class GLFrameSurface extends GLSurfaceView {

    //leon zlm
    private final String TAG = "GLFrameSurface";

    //leon zlm add in 20151231
    private boolean isFrameSurfaceBuilt = false;

    public GLFrameSurface(Context context) {
        super(context);
    }

    public GLFrameSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        //leon zlm
        isFrameSurfaceBuilt = false;
    }

    @Override
    protected void onAttachedToWindow() {
        Log.i(TAG, "surface onAttachedToWindow()");
        super.onAttachedToWindow();
        // setRenderMode() only takes effectd after SurfaceView attached to window!
        // note that on this mode, surface will not render util GLSurfaceView.requestRender() is
        // called, it's good and efficient -v-
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        Log.i(TAG, "surface setRenderMode RENDERMODE_WHEN_DIRTY");

        //leon zlm add in 20151231
        isFrameSurfaceBuilt = true;
    }


    //leon zlm add in 20151231
    public boolean isSurfaceBuilt() {
        return isFrameSurfaceBuilt;
    }

}
