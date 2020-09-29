package com.hih.picdraw.model;

import android.graphics.Paint;
import android.graphics.PointF;

import java.util.List;

/**
 * Created by likeye on 2020/9/17 14:12.
 **/
public class BrokenLine {
    List<PointF> BList;
    Paint paint;


    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public List<PointF> getBList() {
        return BList;
    }

    public void setBList(List<PointF> BList) {
        this.BList = BList;
    }
}
