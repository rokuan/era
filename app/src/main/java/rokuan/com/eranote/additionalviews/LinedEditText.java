package rokuan.com.eranote.additionalviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

import rokuan.com.eranote.R;

/**
 * An EditText with painted lines
 */
public class LinedEditText extends EditText {
    private Rect mRect;
    private Paint mPaint;

    public LinedEditText(Context context) {
        super(context);
        this.initView();
    }

    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public LinedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView();
    }

    @SuppressWarnings("deprecation")
    private void initView(){
        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //mPaint.setColor(getResources().getColor(R.color.dark_indigo));
        mPaint.setColor(Color.BLACK);

        if(Build.VERSION.SDK_INT >= 16) {
            this.setBackground(null);
        } else {
            this.setBackgroundDrawable(null);
        }

        this.setGravity(Gravity.TOP | Gravity.LEFT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = getHeight();
        int lineHeight = getLineHeight();
        int count = height / lineHeight;

        if (getLineCount() > count)
            count = getLineCount(); //for long text with scrolling

        Rect r = mRect;
        Paint paint = mPaint;
        int baseline = getLineBounds(0, r); //first line

        for (int i=0; i<count; i++) {
            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
            baseline += getLineHeight();    //next line
        }

        super.onDraw(canvas);
    }
}
