package com.sp.benjago.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sp.benjago.helper.FontCache;


/**
 * Created by apple on 28/12/15.
 */

public class RobotoMediam extends TextView {

    public RobotoMediam(Context context) {
        super(context);
        init(context);
    }

    public RobotoMediam(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public RobotoMediam(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        super.setTypeface(FontCache.get("Roboto-Medium.ttf", context));
    }
}
