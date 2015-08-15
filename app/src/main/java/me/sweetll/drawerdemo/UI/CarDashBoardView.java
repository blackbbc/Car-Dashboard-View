package me.sweetll.drawerdemo.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sweet on 15-8-13.
 */
public class CarDashBoardView extends View {
    private Paint paint;
    RectF oval = new RectF();

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3.0f);
        paint.setStyle(Paint.Style.STROKE);
    }

    public CarDashBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        oval.left = 100;
        oval.top = 100;
        oval.right = 400;
        oval.bottom = 300;
        canvas.drawArc(oval, 200, 135, true, paint);

        oval.left = 100;
        oval.top = 400;
        oval.right = 400;
        oval.bottom = 700;
        canvas.drawArc(oval, 200, 135, true, paint);
    }
}
