package com.hih.picdraw.model;

import android.graphics.Paint;

/**
 * Created by likeye on 2020/7/30 13:56.
 **/
public class Operation {
    private int type;
    Paint paint;


    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
