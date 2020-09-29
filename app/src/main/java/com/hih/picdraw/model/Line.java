package com.hih.picdraw.model;


import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by likeye on 2020/7/28 16:26.
 **/
//直线
public class Line {
    private PointF p1;
    private PointF p2;
    private PointF pmid;
    Paint paint;


    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
    public PointF getP1() {
        return p1;
    }

    public void setP1(PointF p1) {
        this.p1 = p1;
    }

    public PointF getP2() {
        return p2;
    }

    public void setP2(PointF p2) {
        this.p2 = p2;
    }

    public PointF getPmid() {
        pmid=new PointF();
        pmid.x=p1.x+(p2.x-p1.x)/2;
        pmid.y=p1.y+(p2.y-p1.y)/2;
        return pmid;
    }

    public double getAngle(){
        double x=p1.x-p2.x;
        double y=p1.y-p2.y;
        return y/x;
    }
    public double getD(){
        double lineLength = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
                * (p1.y - p2.y));
        return lineLength;
    }

}
