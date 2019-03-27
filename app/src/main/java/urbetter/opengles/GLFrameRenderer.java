package urbetter.opengles;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.DisplayMetrics;
import android.util.Log;

import utils.BitmapUtil;

public class GLFrameRenderer implements Renderer {

    private final String TAG = "GLFrameRenderer";

    public GLSurfaceView mTargetSurface;
    private GLProgram prog = new GLProgram(0);
    private int mScreenWidth, mScreenHeight;
    private int mVideoWidth, mVideoHeight;
    public ByteBuffer y;
    public ByteBuffer u;
    public ByteBuffer v;

    private boolean isFrameRendererBuilt = false;

    public static boolean isTakePicture = false;

    public GLFrameRenderer(GLSurfaceView surface, DisplayMetrics dm) {
        mTargetSurface = surface;
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

    }

//    public GLFrameRenderer(boolean isTakePicture) {
//        this.isTakePicture = isTakePicture;
//    }

    public boolean isRenderedBuilt() {
        return isFrameRendererBuilt;
    }

    //打开Camera
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");
        isFrameRendererBuilt = true;
        if (!prog.isProgramBuilt()) {
            prog.buildProgram();
            Log.i(TAG, "buildProgram done");
        }
    }

    //开启预览
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            if (y != null) {
                // reset position, have to be done
                y.position(0);
                u.position(0);
                v.position(0);
                prog.buildTextures(y, u, v, mVideoWidth, mVideoHeight);

                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                prog.drawFrame();
                if (isTakePicture) {
                    Bitmap bmp = BitmapUtil.comp(createBitmapFromGLSurface(0, 0, mScreenWidth, mScreenHeight, gl));
                    BitmapUtil.saveImage(bmp);
                    isTakePicture = false;
                }
            }
        }
    }

    public static Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl) {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
                    intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_4444);
    }

    /**
     * this method will be called from native code, it happens when the video is about to play or
     * the video size changes.
     */
    public void update(int w, int h) {
        Log.i(TAG, "update(w,h) E");
        if (w > 0 && h > 0) {
            if (mScreenWidth > 0 && mScreenHeight > 0) {
                float f1 = 1f * mScreenHeight / mScreenWidth;
                float f2 = 1f * h / w;
                if (f1 == f2) {
                    prog.createBuffers(GLProgram.squareVertices);
                } else if (f1 < f2) {
                    float widScale = f1 / f2;
                    prog.createBuffers(new float[]{-widScale, -1.0f, widScale, -1.0f, -widScale, 1.0f, widScale,
                            1.0f,});
                } else {
                    float heightScale = f2 / f1;
                    prog.createBuffers(new float[]{-1.0f, -heightScale, 1.0f, -heightScale, -1.0f, heightScale, 1.0f,
                            heightScale,});
                }
            }
            if ((w != mVideoWidth) || (h != mVideoHeight)) {
                this.mVideoWidth = w;
                this.mVideoHeight = h;
                int yarraySize = w * h;
                int uvarraySize = yarraySize / 2;
                synchronized (this) {
                    y = ByteBuffer.allocate(yarraySize);
                    u = ByteBuffer.allocate(uvarraySize);
                    v = ByteBuffer.allocate(uvarraySize);
                }
            }
        }
        Log.i(TAG, "update(w,h) X");
    }

    /**
     * this method will be called from native code, it's used for passing yuv data to me.
     */
    public void update(byte[] ydata, int ysize, byte[] udata, int usize, byte[] vdata, int vsize) {
        synchronized (this) {
            // Log.i(TAG, "update(y,u,v) E");

            y.clear();
            u.clear();
            v.clear();
            //y.put(ydata, 0, ydata.length);
            //u.put(udata, 0, udata.length);
            //v.put(vdata, 0, vdata.length);

            y.put(ydata, 0, ysize);
            u.put(udata, 0, usize);
            v.put(vdata, 0, vsize);
        }

        // request to render
        mTargetSurface.requestRender();//需要刷新时，调用requestRender()函数即可。
        // Log.i(TAG, "update(y,u,v) E");
    }

    /**
     * this method will be called from native code, it's used for passing play state to activity.
     */
    public void updateState(int state) {
        Log.i(TAG, "updateState E = " + state);
    }
}
