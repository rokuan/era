package rokuan.com.eranote.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;

/**
 * Created by Christophe on 19/01/2015.
 */
public class Utils {
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

    public static Bitmap getScaledBitmap(Bitmap bmp){
        return getScaledBitmap(bmp, 256, 256);
    }

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
