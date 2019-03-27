package mode;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zlb on 2017/10/26.
 */

public class GridItem implements Serializable {

    private String title;

    private Bitmap bitmap;

    public GridItem(String title, Bitmap bitmap) {
        this.title = title;
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
