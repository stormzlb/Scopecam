package utils;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by Admin on 2017/4/20.
 */

public class ImageFileFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        if (pathname == null) return false;
        return pathname.getName().toLowerCase().endsWith(".jpg");
    }
}
