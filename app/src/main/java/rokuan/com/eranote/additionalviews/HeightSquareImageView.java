package rokuan.com.eranote.additionalviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Christophe on 19/01/2015.
 */
public class HeightSquareImageView extends ImageView {
    public HeightSquareImageView(Context context) {
        super(context);
    }

    public HeightSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeightSquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec){
        super.onMeasure(widthSpec, heightSpec);

        int height = this.getMeasuredHeight();
        this.setMeasuredDimension(height, height);
    }
}
