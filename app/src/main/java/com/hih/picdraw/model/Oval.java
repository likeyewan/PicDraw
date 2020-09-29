package com.hih.picdraw.model;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;


/**
 * Created by likeye on 2020/7/29 17:33.
 **/
//椭圆
public class Oval  {
    private RectF rectF;
    private PointF tmp;
    private PointF bmp;
    private PointF lmp;
    private PointF rmp;
    private PointF ztmp;
    private PointF zbmp;
    private PointF zlmp;
    private PointF zrmp;
    private PointF pmid;
    private double l;
    private double w;
    private double angle;
    Paint paint;


    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
    public RectF getRectF() {
        return rectF;
    }

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
    }

    public PointF getTmp() {
        tmp=new PointF();
        tmp.x=rectF.left+(rectF.right-rectF.left)/2;
        tmp.y=rectF.top;
        return tmp;
    }

    public PointF getBmp() {
        bmp=new PointF();
        bmp.x=rectF.left+(rectF.right-rectF.left)/2;
        bmp.y=rectF.bottom;
        return bmp;
    }
    public PointF getLmp() {
        lmp=new PointF();
        lmp.x=rectF.left;
        lmp.y=rectF.bottom+(rectF.top-rectF.bottom)/2;
        return lmp;
    }

    public PointF getRmp() {
        rmp=new PointF();
        rmp.x=rectF.right;
        rmp.y=rectF.bottom+(rectF.top-rectF.bottom)/2;
        return rmp;
    }
    public PointF getPmid(){
        pmid=new PointF();
        pmid.x=rectF.left+(rectF.right-rectF.left)/2;
        pmid.y=rectF.bottom+(rectF.top-rectF.bottom)/2;
        return pmid;
    }

    public PointF getZtmp() {
        double ang=Math.toRadians(getAngle());
        ztmp=new PointF();
        ztmp.x= (float) (getPmid().x+getW()/2*Math.sin(ang));
        ztmp.y=(float)(getPmid().y-getW()/2*Math.cos(ang));
        return ztmp;
    }

    public PointF getZbmp() {
        double ang=Math.toRadians(getAngle());
        zbmp=new PointF();
        zbmp.x= (float) (getPmid().x-getW()/2*Math.sin(ang));
        zbmp.y=(float)(getPmid().y+getW()/2*Math.cos(ang));
        return zbmp;
    }

    public PointF getZlmp() {
        double ang=Math.toRadians(getAngle());
        zlmp=new PointF();
        zlmp.x=(float) (getPmid().x-getL()/2*Math.cos(ang));
        zlmp.y=(float)(getPmid().y-getL()/2*Math.sin(ang));
        return zlmp;
    }

    public PointF getZrmp() {
        double ang=Math.toRadians(getAngle());
        zrmp=new PointF();
        zrmp.x=(float) (getPmid().x+getL()/2*Math.cos(ang));
        zrmp.y=(float)(getPmid().y+getL()/2*Math.sin(ang));
        return zrmp;
    }

    public double getL() {
        l=Math.abs(rectF.left-rectF.right);
        return l;
    }

    public double getW() {
        w=Math.abs(rectF.bottom-rectF.top);
        return w;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
