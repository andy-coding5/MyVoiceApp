package com.e2excel.myvoice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.TextView;


@SuppressLint("AppCompatCustomView")
public class CustomTextView extends TextView {


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    public CustomTextView(Context context) {
        super(context);
        if (!isInEditMode()) {
            Typeface face = ResourcesCompat.getFont(context, R.font.quicksand_regular);

            this.setTypeface(face);
        }
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Typeface face = ResourcesCompat.getFont(context, R.font.quicksand_regular);
            this.setTypeface(face);
        }
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            Typeface face = ResourcesCompat.getFont(context, R.font.quicksand_light);
            this.setTypeface(face);
        }
    }
}