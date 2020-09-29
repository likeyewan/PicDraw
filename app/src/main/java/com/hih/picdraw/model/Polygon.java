package com.hih.picdraw.model;

import android.graphics.Paint;
import android.graphics.PointF;

import java.util.List;

/**
 * Created by likeye on 2020/8/1 11:16.
 **/
public class Polygon {
    private PointF pm;
    private List<PointF> pointFList;
    Paint paint;


    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
    public List<PointF> getPointFList() {
        return pointFList;
    }
    public void setPointFList(List<PointF> pointFList) {
        this.pointFList = pointFList;
    }
    public PointF getPm() {
        pm=getCenterOfGravityPoint(pointFList);
        return pm;
    }
    /**
     * 获取不规则多边形重心点
     *
     * @param mPoints
     * @return
     */
    public  PointF getCenterOfGravityPoint(List<PointF> mPoints) {
        float area = (float) 0.0;//多边形面积
        float Gx = (float) 0.0, Gy = (float) 0.0;// 重心的x、y
        for (int i = 1; i <= mPoints.size(); i++) {
            double iLat = mPoints.get(i % mPoints.size()).x;
            double iLng = mPoints.get(i % mPoints.size()).y;
            double nextLat = mPoints.get(i - 1).x;
            double nextLng = mPoints.get(i - 1).y;
            double temp = (iLat * nextLng - iLng * nextLat) / 2.0;
            area += temp;
            Gx += temp * (iLat + nextLat) / 3.0;
            Gy += temp * (iLng + nextLng) / 3.0;
        }
        Gx = Gx / area;
        Gy = Gy / area;

        return new PointF(Gx, Gy);
    }

}
