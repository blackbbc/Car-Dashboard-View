package me.sweetll.drawerdemo.UI;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.util.zip.Inflater;

import hugo.weaving.DebugLog;
import me.sweetll.drawerdemo.R;

/**
 * Created by sweet on 15-8-13.
 */
public class CarDashBoardView extends View {

    private static final int DEFAULT_LONG_POINTER_SIZE = 1;

    private Paint mPaint;
    private float mStrokeWidth;
    private int mStrokeColor;
    private RectF mRect;
    private String mStrokeCap;
    private int mStartAngel;
    private int mSweepAngel;
    private int mStartValue;
    private int mEndValue;
    private int mValue;
    private double mPointAngel;
    private float mRectLeft;
    private float mRectTop;
    private float mRectRight;
    private float mRectBottom;
    private int mPoint;
    private int mPointColor;
    private int mPointSize;
    private int mPointStartColor;
    private int mPointEndColor;

    private ValueAnimator boardAnimator;

    private int numId;
    private int labelId;
    private TextView numView;
    private TextView labelView;

    public CarDashBoardView(Context context) {
        super(context);
        init();
    }

    @DebugLog
    public CarDashBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CarDashBoardView, 0, 0);

        // stroke style
        mStrokeWidth = a.getDimension(R.styleable.CarDashBoardView_strokeWidth, 10);
        mStrokeColor = a.getColor(R.styleable.CarDashBoardView_strokeColor, getResources().getColor(android.R.color.darker_gray));
        mStrokeCap = a.getString(R.styleable.CarDashBoardView_strokeCap);

        // angel start and sweep (opposite direction 0, 270, 180, 90)
        mStartAngel = a.getInt(R.styleable.CarDashBoardView_startAngel, 0);
        mSweepAngel = a.getInt(R.styleable.CarDashBoardView_sweepAngel, 360);

        // scale (from mStartValue to mEndValue)
        mStartValue = a.getInt(R.styleable.CarDashBoardView_startValue, 0);
        mEndValue = a.getInt(R.styleable.CarDashBoardView_endValue, 1000);

        // pointer size and color
        mPointSize = a.getColor(R.styleable.CarDashBoardView_pointSize, 0);
        mPointStartColor = a.getColor(R.styleable.CarDashBoardView_pointStartColor, getResources().getColor(android.R.color.white));
        mPointEndColor = a.getColor(R.styleable.CarDashBoardView_pointEndColor, getResources().getColor(android.R.color.white));

        // calculating one point sweep
        mPointAngel = ((double) Math.abs(mSweepAngel) / (mEndValue - mStartValue));

        numId = a.getResourceId(R.styleable.CarDashBoardView_numView, 0);
        labelId = a.getResourceId(R.styleable.CarDashBoardView_labelView, 0);

//        if (labelId!= 0) {
//            labelView = (TextView) ((Activity) context).findViewById(labelId);
//        } else {
//            labelView = null;
//        }

        a.recycle();
        init();
    }

    private void init() {
        //main Paint
        mPaint = new Paint();
        mPaint.setColor(mStrokeColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
        if (!TextUtils.isEmpty(mStrokeCap)) {
            if (mStrokeCap.equals("BUTT"))
                mPaint.setStrokeCap(Paint.Cap.BUTT);
            else if (mStrokeCap.equals("ROUND"))
                mPaint.setStrokeCap(Paint.Cap.ROUND);
        } else
            mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setStyle(Paint.Style.STROKE);
        mRect = new RectF();

        mValue = mStartValue;
        mPoint = mStartAngel;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float paddingLeft = getPaddingLeft();
        float paddingRight = getPaddingRight();
        float paddingTop = getPaddingTop();
        float paddingBottom = getPaddingBottom();
        float width = getWidth() - (paddingLeft + paddingRight);
        float height = getHeight() - (paddingTop + paddingBottom);
        float radius = (width > height ? width / 2 : height / 2);

        mRectLeft = width / 2 - radius + paddingLeft;
        mRectTop = height / 2 - radius + paddingTop;
        mRectRight = width / 2 - radius + paddingLeft + width;
        mRectBottom = height / 2 - radius + paddingTop + height;

        mRect.set(mRectLeft, mRectTop, mRectRight, mRectBottom);

        mPaint.setColor(mStrokeColor);
        mPaint.setShader(null);
        canvas.drawArc(mRect, mStartAngel, mSweepAngel, false, mPaint);
        mPaint.setColor(mPointStartColor);
        mPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), mPointEndColor, mPointStartColor, android.graphics.Shader.TileMode.MIRROR));
        if (mPointSize > 0) {//if size of pointer is defined
            if (mPoint > mStartAngel + mPointSize / 2) {
                canvas.drawArc(mRect, mPoint - mPointSize / 2, mPointSize, false, mPaint);
            } else { //to avoid excedding start/zero point
                canvas.drawArc(mRect, mPoint, mPointSize, false, mPaint);
            }
        } else { //draw from start point to value point (long pointer)
            if (mValue == mStartValue) //use non-zero default value for start point (to avoid lack of pointer for start/zero value)
                canvas.drawArc(mRect, mStartAngel, DEFAULT_LONG_POINTER_SIZE, false, mPaint);
            else
                canvas.drawArc(mRect, mStartAngel, mPoint - mStartAngel, false, mPaint);
        }

    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        setValue(value, true);
    }

    public void setValue(final int value, final boolean animate) {
        if (animate) {
            boardAnimator = ValueAnimator.ofInt(getValue(), value);
            boardAnimator.setDuration(700);

            setValue(0, false);

            boardAnimator.setInterpolator(new DecelerateInterpolator());

            boardAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int interpolation = (int) animation.getAnimatedValue();
                    if (numId != 0) {
                        numView = (TextView) getRootView().findViewById(numId);
                        numView.setText("" + interpolation + " rpm");
                    } else {
                        numView = null;
                    }
                    setValue(interpolation, false);
                }
            });

            if (!boardAnimator.isStarted()) {
                boardAnimator.start();
            }

        } else {
            mValue = value;
            mPoint = (int) (mStartAngel + (mValue - mStartValue) * mPointAngel);
            postInvalidate();
        }
    }

}
