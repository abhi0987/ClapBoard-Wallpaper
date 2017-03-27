package io.wings.gabriel.tapestry.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by abhis on 3/20/2016.
 */
public class myTextcustomView extends TextView {

    public static Typeface FONT_NAME;

    public myTextcustomView(Context context) {
        super(context);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "custom_font.TTF");

        this.setTypeface(FONT_NAME);
    }

    public myTextcustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "custom_font.TTF");

        this.setTypeface(FONT_NAME);
    }

    public myTextcustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "custom_font.TTF");

        this.setTypeface(FONT_NAME);
    }
}
