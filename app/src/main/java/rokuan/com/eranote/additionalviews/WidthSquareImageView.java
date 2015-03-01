package rokuan.com.eranote.additionalviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An square ImageView whose height equals its width
 * @author Lebeau Christophe
 */
public class WidthSquareImageView extends ImageView {
    public WidthSquareImageView(Context context) {
        super(context);
    }

    public WidthSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WidthSquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec){
        super.onMeasure(widthSpec, heightSpec);

        int width = this.getMeasuredWidth();
        this.setMeasuredDimension(width, width);
    }
}
