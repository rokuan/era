package rokuan.com.eranote.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;

/**
 * Created by Christophe on 19/01/2015.
 */
public class Utils {
    /**
     * Returns the bitmap pixel data
     * @param bmp the bitmap object
     * @return a byte array or null if {@code bmp} is null
     */
    public static byte[] getBitmapData(Bitmap bmp){
        if(bmp == null){
            return null;
        }

        Bitmap scaled = getScaledBitmap(bmp);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        scaled.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        scaled.recycle();

        return outputStream.toByteArray();
    }

    /**
     * Returns a 256x256-scaled bitmap of {@code bmp}
     * @param bmp
     * @return a scaled bitmap
     */
    public static Bitmap getScaledBitmap(Bitmap bmp){
        return getScaledBitmap(bmp, 256, 256);
    }

    /**
     * Returns a {@code newWidth}x{@code newHeight} copy of {@code bmp}
     * @param bmp
     * @param newWidth
     * @param newHeight
     * @return a scaled bitmap
     */
    public static Bitmap getScaledBitmap(Bitmap bmp, int newWidth, int newHeight){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, false);
    }
}
