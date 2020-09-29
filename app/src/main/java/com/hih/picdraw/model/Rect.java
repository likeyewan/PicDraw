package com.hih.picdraw.model;

import android.graphics.Paint;
import android.graphics.PointF;


/**
 * Created by likeye on 2020/7/29 17:02.
 **/
//矩形
public class Rect {
    private PointF p1;
    private PointF p2;
    private PointF p3;
    private PointF p4;
    private PointF lp1;
    private PointF lp2;
    private PointF wp1;
    private PointF wp2;
    private PointF zlp1;
    private PointF zlp2;
    private PointF zwp1;
    private PointF zwp2;
    private double l;
    private double w;
    private double angle;
    private PointF pmid;
    private PointF cPmid;
    Paint paint;


    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
    public PointF getcPmid() {
        return cPmid;
    }

    public void setcPmid(PointF cPmid) {
        this.cPmid = cPmid;
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

    public PointF getP3() {
       // double a = Math.toRadians(angle);//把数字转换成 度
        p3=new PointF();
       // p3.x= (float) (p2.x+w*Math.cos(a));
       // p3.y= (float) (p2.y-w*Math.sin(a));
        p3.x=p2.x;
        p3.y=p1.y;
        return p3;
    }

    public void setP3(PointF p3) {
        this.p3 = p3;
    }

    public PointF getP4() {
       // double a = Math.toRadians(angle);//把数字转换成 度
        p4=new PointF();
      //  p4.x=(float) (p1.x-w*Math.cos(a));
      //  p4.y=(float) (p1.y+w*Math.sin(a));
        p4.x=p1.x;
        p4.y=p2.y;
        return p4;
    }

    public void setP4(PointF p4) {
        this.p4 = p4;
    }

    public PointF getPmid() {
        pmid=new PointF();
        pmid.x=p1.x+(p2.x-p1.x)/2;
        pmid.y=p1.y+(p2.y-p1.y)/2;
        return pmid;
    }

    public void setPmid(PointF pmid) {
        this.pmid = pmid;
    }
    public PointF getLp1() {
        lp1=new PointF();
        lp1.x=p1.x+(getP3().x-p1.x)/2;
        lp1.y=p1.y+(getP3().y-p1.y)/2;
        return lp1;
    }

    public void setLp1(PointF lp1) {
        this.lp1=lp1;
    }

    public PointF getLp2() {
        lp2=new PointF();
        lp2.x=p2.x+(getP4().x-p2.x)/2;
        lp2.y=p2.y+(getP4().y-p2.y)/2;
        return lp2;
    }

    public PointF getZlp1() {
        double ang=Math.toRadians(getAngle());
        zlp1=new PointF();
        zlp1.x= (float) (getPmid().x+getW()/2*Math.sin(ang));
        zlp1.y=(float)(getPmid().y-getW()/2*Math.cos(ang));
        return zlp1;
    }

    public PointF getZlp2() {
        double ang=Math.toRadians(getAngle());
        zlp2=new PointF();
        zlp2.x= (float) (getPmid().x-getW()/2*Math.sin(ang));
        zlp2.y=(float)(getPmid().y+getW()/2*Math.cos(ang));
        return zlp2;
    }

    public PointF getZwp1() {
        double ang=Math.toRadians(getAngle());
        zwp1=new PointF();
        zwp1.x=(float) (getPmid().x-getL()/2*Math.cos(ang));
        zwp1.y=(float)(getPmid().y-getL()/2*Math.sin(ang));
        return zwp1;
    }

    public PointF getZwp2() {
        double ang=Math.toRadians(getAngle());
        zwp2=new PointF();
        zwp2.x=(float) (getPmid().x+getL()/2*Math.cos(ang));
        zwp2.y=(float)(getPmid().y+getL()/2*Math.sin(ang));
        return zwp2;
    }

    public void setLp2(PointF lp2) {
        this.lp2 = lp2;
    }

    public PointF getWp1() {
        wp1=new PointF();
        wp1.x=p1.x+(getP4().x-p1.x)/2;
        wp1.y=p1.y+(getP4().y-p1.y)/2;
        return wp1;
    }



    public PointF getWp2() {
        wp2=new PointF();
        wp2.x=p2.x+(getP3().x-p2.x)/2;
        wp2.y=p2.y+(getP3().y-p2.y)/2;
        return wp2;
    }



    public double getL() {
        l=Math.abs(p1.x-p2.x);
        return l;
    }

    public void setL(double l) {
        this.l = l;
    }

    public double getW() {

        w=Math.abs(p1.y-p2.y);
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
