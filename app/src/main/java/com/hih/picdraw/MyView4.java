package com.hih.picdraw;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.hih.picdraw.model.BrokenLine;
import com.hih.picdraw.model.Circle;
import com.hih.picdraw.model.Line;
import com.hih.picdraw.model.Operation;
import com.hih.picdraw.model.Oval;
import com.hih.picdraw.model.Polygon;
import com.hih.picdraw.model.Rect;
import com.shoulashou.piantdemo.doublemoveview3.test.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.random;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Created by likeye on 2020/7/28 17:12.
 **/
public class MyView4 extends View {
    //圆集合
    private List<Circle> circleList = new ArrayList<>();
    //直线集合
    public List<Line> lineList = new ArrayList<>();
    //矩形集合
    private List<Rect> rectList = new ArrayList<>();
    //椭圆集合
    private List<Oval> ovalList = new ArrayList<>();
    //多边形集合
    private List<Polygon> polygonList = new ArrayList<>();
    //折线集合
    private List<BrokenLine> brokenLineList = new ArrayList<>();
    //操作集合
    private List<Operation> operationList = new ArrayList<>();
    //矩形删除集合
    private List<Rect> rectListDel = new ArrayList<>();
    //椭圆删除集合
    private List<Oval> ovalListDel = new ArrayList<>();
    private Context mContext;

    //移动
    public boolean move = false;

    private boolean clip1 = false;
    public boolean clip2 = false;
    public boolean finsh = false;
    private boolean clip3 = false;
    private boolean clip4 = false;
    private boolean sCirle = false;
    private boolean isClip1 = false;
    private boolean isClip2 = false;
    private boolean isClip3 = false;
    private boolean isClip4 = false;
    //第一次缩放倍数
    public float firstS;
    float l;
    //进行的操作标志
    public int flag;
    //第一次选中标识
    boolean ff = true;
    //选中的第几步
    private int chooseOperation = 0;
    //选中的图形
    private int chooseLine = 0;
    private int chooseCircle = 0;
    private int chooseRect = 0;
    private int chooseOval = 0;
    private int choosePolygon = 0;
    private int chooseBrokenLine=0;
    //当前操作的图形
    private Line lineTemp;
    private Circle circleTemp;
    private Oval ovalTemp;
    private Rect rectTemp;
    private BrokenLine brokenLineTemp;
    private Polygon polygonTemp;
    //颜色
    public int colPicker=Color.BLACK;
    public int sizePicker=5;
    public Paint.Style penStyle=Paint.Style.STROKE;
    //图形旋转后的中心相对点
    private PointF pxd;
    public boolean delChoose=false;
    //手指抬起
    private boolean isUp = false;
    private int upflag = 0;
    private boolean touchMove = false;
    //旋转操作标志
    private boolean rotate = false;
    //按下和移动以及抬起点
    public PointF downPoint, movePoint, upPoint;
    //图中坐标
    public PointF downPointB, movePointB, upPointB;
    //圆半径
    public float lineD;
    private float circleR;
    //封闭图形选中的点
    private int choosePoint=-1;
    //矩形与x轴的角度
    private double rectAngle = 0;
    private double ovalAngle = 0;
    //多边形和折线的点集合
    public List<PointF> pointFS = new ArrayList<>();
    private PointF pp1, pp2, pp3;
    private RectF rectF;
    private Bitmap mSrcBitmap;
    private Bitmap mMultiplyBitmap = null;//混合之后的图片,双指缩放移动的时候,单独移动这张混合后图片,提高用户体验
    public boolean mIsMove;//是否双指拖动图片中ing
    private int mBitmapWidth, mBitmapHeight;//图片的长度和高度
    private float mCenterLeft, mCenterTop;//图片居中时左上角的坐标
    private int mCenterHeight, mCenterWidth; // 图片适应屏幕时的大小
    public float mCenterScale;//画布居中时的比例
    private int mViewWidth, mViewHeight;//当前View的长度和宽度
    public float mTransX = 0, mTransY = 0; // 偏移量，图片真实偏移量为　mCentreTranX + mTransX
    private float mScale = 1.0f; // 缩放倍数, 图片真实的缩放倍数为 mPrivateScale * mScale
    private boolean mIsSaveArg = false;//保存参数使用
    private Bitmap mGraffitiBitmap; // 用绘制涂鸦的图片
    private Canvas mBitmapCanvas; // 用于绘制涂鸦的画布
    private Bitmap mCurrentBitmap; // 绘制当前线时用到的图片
    private Canvas mCurrentCanvas; // 当前绘制线的画布
    private int[] mWhiteBuffer;//保存白色图片内存，刷新时重新刷新图片
    private int mTouchMode; // 触摸模式，触点数量
    private PorterDuffXfermode mPdXfermode; // 定义PorterDuffXfermode变量
    private Paint mPdXfPaint;// 绘图的混合模式
    private Paint mCurrentPaint;
    private Paint  paint, paintT,paintPen;
    private Paint mEraserPaint;
    private ChooseListener listener;


    public void setChooseListenr(ChooseListener listener) {
        this.listener = listener;
    }

    public MyView4(Context context, Bitmap bitmap) {
        super(context);
        mContext = context;
        init(bitmap);
    }

    public MyView4(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //初始化，定义画笔，画布
    private void init(Bitmap bitmap) {
        mSrcBitmap = bitmap;
        mBitmapWidth = mSrcBitmap.getWidth();
        mBitmapHeight = mSrcBitmap.getHeight();
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        TypedValue value=new TypedValue();
//        options.inTargetDensity = value.density;
//        options.inScaled = false;
//        mSrcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.demobg,options);
        mMultiplyBitmap = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
        mGraffitiBitmap = getTransparentBitmap(mSrcBitmap);
        //画图中的画笔
        mCurrentPaint = new Paint();
        mCurrentPaint.setStyle(Paint.Style.STROKE);
        mCurrentPaint.setStrokeWidth(5);
        mCurrentPaint.setColor(Color.BLACK);
        // mCurrentPaint.setAlpha(100);
        mCurrentPaint.setAntiAlias(true);
        //  mCurrentPaint.setStrokeJoin(Paint.Join.ROUND);
        // mCurrentPaint.setStrokeCap(Paint.Cap.ROUND);
        //mCurrentPaint.setXfermode(null);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);
        paintT = new Paint();
        paintT.setColor(Color.BLACK);
        paintT.setStrokeWidth(5);
        paintT.setTextSize(30);
        mEraserPaint=new Paint();
        mEraserPaint.setColor(Color.WHITE);
        mCurrentBitmap = getTransparentBitmap(mSrcBitmap);
        //设置混合模式   （正片叠底）
        mPdXfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        mPdXfPaint = new Paint();
        mPdXfPaint.setAntiAlias(true);
        mPdXfPaint.setFilterBitmap(true);
        mIsMove = false;
    }

    //初始化，获取图片与view的大小和缩放倍数
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        float nw = mBitmapWidth * 1f / mViewWidth;
        float nh = mBitmapHeight * 1f / mViewHeight;
        if (nw > nh) {
            mCenterScale = 1 / nw;
            mCenterWidth = mViewWidth;
            mCenterHeight = (int) (mBitmapHeight * mCenterScale);
        } else {
            mCenterScale = 1 / nh;
            mCenterWidth = (int) (mBitmapWidth * mCenterScale);
            mCenterHeight = mViewHeight;
        }
        firstS = mCenterScale;
        // 使图片居中
        mCenterLeft = (mViewWidth - mCenterWidth) / 2f;
        mCenterTop = (mViewHeight - mCenterHeight) / 2f;
        initCanvas();
        initCurrentCanvas();
        mIsMove = false;
    }
    Path path=new Path();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scale = mCenterScale * mScale;
        float x = (mCenterLeft + mTransX) / scale;
        float y = (mCenterTop + mTransY) / scale;
        //复原原来比例
       if (flag == 7) {
           // mCenterScale = 1 / (mBitmapWidth * 1f / mViewWidth);
           // mCenterScale = 1 / (mBitmapHeight * 1f / mViewHeight);
            x = (mViewWidth - mCenterWidth) / 2f / scale;
            y = (mViewHeight - mCenterHeight) / 2f / scale;
            flag = 0;
            mCenterLeft = (mViewWidth - mCenterWidth) / 2f;
            mCenterTop = (mViewHeight - mCenterHeight) / 2f;
            mTransX = 0;
            mTransY = 0;
            mScale = 1;
            mCenterScale=firstS;
        }
       if(flag==100){
           flag = 0;
           x=0;
           y=0;
           mScale = 1;
           mCenterLeft=0;
           mCenterTop=0;
           mCenterScale=1;
           scale=1;
       }
       // Log.d("ss","scale="+scale+"mCenterScale="+mCenterScale+",mScale="+mScale);
        canvas.scale(scale, scale);

        if (!mIsMove) {
            //正片叠底混合模式
            initCurrentCanvas();
            if (movePoint != null) {
                /*int x1=200,y1=200;
                int x3=400,y3=200;
                int x2=(x1+x3)/2,y2=y1+30;
                path.moveTo(x1,y1);//起点
                path.quadTo(x2,y2,x3,y3);//3点画弧
                int xa1=150,ya1=400;
                int xa3=450,ya3=400;
                int xa2=(xa1+xa3)/2,ya2=460;
                path.moveTo(xa1,ya1);
                path.quadTo(xa2,ya2,xa3,ya3);
                path.moveTo(x1,y1);
                path.lineTo(xa1,ya1);
                path.moveTo(x3,y3);
                path.lineTo(xa3,ya3);
                mBitmapCanvas.drawPath(path,paint);*/
                paintP();
                lineChange();
                circleChange();
                rectChange();
                ovalChange();
                polygonChange();
                brokenLineChange();
            }
            //Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.demobg).copy(Bitmap.Config.ARGB_8888, true);
            canvas.drawBitmap(mSrcBitmap, x, y, mPdXfPaint);
            mPdXfPaint.setXfermode(mPdXfermode);
            canvas.drawBitmap(mCurrentBitmap, x, y, mPdXfPaint);
            // 绘制涂鸦图片
            canvas.drawBitmap(mGraffitiBitmap, x, y, mPdXfPaint);
            mPdXfPaint.setXfermode(null);
        } else {
            //只显示原始图片
            canvas.drawBitmap(mMultiplyBitmap, x, y, null);
        }
    }

    //基本图形的绘画
    private void paintP() {
        mCurrentPaint.setColor(colPicker);
        mCurrentPaint.setStrokeWidth(sizePicker);
        mCurrentPaint.setStyle(penStyle);
        if (flag == 1) {
            mCurrentCanvas.drawLine(downPointB.x, downPointB.y, movePointB.x, movePointB.y, mCurrentPaint);
        }
        float xl = movePointB.x - downPointB.x;
        float yl = movePointB.y - downPointB.y;
        if (flag == 2) {
            l = (float) sqrt(xl * xl + yl * yl);
            mCurrentCanvas.drawCircle(downPointB.x, downPointB.y, l, mCurrentPaint);
        }
        if (flag == 3) {
            mCurrentCanvas.drawRect(downPointB.x, downPointB.y, movePointB.x, movePointB.y, mCurrentPaint);
        }
        if (flag == 4) {
            rectF = new RectF();
            rectF.set(downPointB.x, downPointB.y, movePointB.x, movePointB.y);
            mCurrentCanvas.drawOval(rectF, mCurrentPaint);
        }
        if (flag == 5) {
            mCurrentCanvas.drawCircle(pointFS.get(0).x, pointFS.get(0).y, 10, paint);
            for (int i = 1; i < pointFS.size(); i++) {

                mCurrentCanvas.drawLine(pointFS.get(i - 1).x, pointFS.get(i - 1).y, pointFS.get(i).x, pointFS.get(i).y, mCurrentPaint);
                mCurrentCanvas.drawCircle(pointFS.get(i).x, pointFS.get(i).y, 10, paint);
            }
        }
        if (flag == 6) {
            mBitmapCanvas.drawCircle(downPointB.x,downPointB.y,10,paint);
            for (int i = 1; i < pointFS.size(); i++) {
                mCurrentCanvas.drawLine(pointFS.get(i - 1).x, pointFS.get(i - 1).y, pointFS.get(i).x, pointFS.get(i).y, mCurrentPaint);
            }
            if (pointFS.size() > 2) {
                mCurrentCanvas.drawLine(pointFS.get(0).x, pointFS.get(0).y, pointFS.get(pointFS.size() - 1).x, pointFS.get(pointFS.size() - 1).y, mCurrentPaint);
            }
        }
        if(flag==10){
            PointF pf1,pf2,pf3,pf4;
            PointF mb1=new PointF();
            mb1.x=movePointB.x-30;
            mb1.y=movePointB.y-30;
            PointF mb2=new PointF();
            mb2.x=movePointB.x-30;
            mb2.y=movePointB.y+30;
            PointF mb3=new PointF();
            mb3.x=movePointB.x+30;
            mb3.y=movePointB.y-30;
            PointF mb4=new PointF();
            mb4.x=movePointB.x+30;
            mb4.y=movePointB.y+30;
            mBitmapCanvas.drawRect(movePointB.x-30,movePointB.y-30,movePointB.x+30,movePointB.y+30,mEraserPaint);
            for(Line line:lineList){
                boolean t=isLineIntersectRectangle(line.getP1().x,line.getP1().y,line.getP2().x,line.getP2().y,
                        movePointB.x-30,movePointB.y-30,movePointB.x+30,movePointB.y+30);
                if(t){
                    pf1=getLineCross(line.getP1(),line.getP2(),movePointB.x-30,false);
                    pf2=getLineCross(line.getP1(),line.getP2(),movePointB.x+30,false);
                    pf3=getLineCross(line.getP1(),line.getP2(),movePointB.y-30,true);
                    pf4=getLineCross(line.getP1(),line.getP2(),movePointB.y+30,true);
                    if(pf1!=null){
                        if(movePointB.x>line.getP1().x) {
                            Line line1 = new Line();
                            line1.setP1(line.getP1());
                            line1.setP2(pf1);
                            lineList.add(line1);
                        }else{
                            Line line1 = new Line();
                            line1.setP1(line.getP2());
                            line1.setP2(pf1);
                            lineList.add(line1);
                        }
                    }
                    if(pf2!=null){
                        if(movePointB.x>line.getP1().x) {
                            Line line1 = new Line();
                            line1.setP1(pf2);
                            line1.setP2(line.getP2());
                            lineList.add(line1);
                        }else{
                            Line line1 = new Line();
                            line1.setP1(pf2);
                            line1.setP2(line.getP1());
                            lineList.add(line1);
                        }
                    }
                    if(pf3!=null){
                        if(movePointB.y>line.getP1().y) {
                            Line line1 = new Line();
                            line1.setP1(line.getP1());
                            line1.setP2(pf3);
                            lineList.add(line1);
                        }else{
                            Line line1 = new Line();
                            line1.setP1(line.getP2());
                            line1.setP2(pf3);
                            lineList.add(line1);
                        }
                    }
                    if(pf4!=null){
                        if(movePointB.y>line.getP1().y) {
                            Line line1 = new Line();
                            line1.setP1(line.getP1());
                            line1.setP2(pf4);
                            lineList.add(line1);
                        }else{
                            Line line1 = new Line();
                            line1.setP1(line.getP2());
                            line1.setP2(pf4);
                            lineList.add(line1);
                        }

                    }
                    reMoveR(lineList,line);
                    break;
                }
            }

        }
    }
    //直线的移动改变
    private void lineChange() {
        if (flag == 0 && chooseLine > 0) {
            float xx = movePointB.x - downPointB.x;
            float yy = movePointB.y - downPointB.y;
            pp1 = new PointF();
            pp1.x = lineTemp.getP1().x + xx;
            pp1.y = lineTemp.getP1().y + yy;
            pp2 = new PointF();
            pp2.x = lineTemp.getP2().x + xx;
            pp2.y = lineTemp.getP2().y + yy;
            pp3 = new PointF();
            pp3.x = (pp1.x + (pp2.x - pp1.x) / 2);
            pp3.y = (pp1.y + (pp2.y - pp1.y) / 2);
            lineD = (float) lineSpace(pp1.x, pp1.y, pp2.x, pp2.y);
            //移动
            if (move) {

                reMoveR(lineList, lineTemp);
                mCurrentCanvas.drawLine(pp1.x, pp1.y, pp2.x, pp2.y, mCurrentPaint);
                mCurrentCanvas.drawCircle(pp1.x, pp1.y, 10, paint);
                mCurrentCanvas.drawCircle(pp2.x, pp2.y, 10, paint);
                mCurrentCanvas.drawCircle(pp3.x, pp3.y, 10, paint);
            }
            //端点拖动
            if (clip2) {
                reMoveR(lineList, lineTemp);
                pp1 = new PointF();
                pp1.x = lineTemp.getP1().x;
                pp1.y = lineTemp.getP1().y;
                double currentX = pp1.x - movePointB.x;
                double currentY = pp1.y - movePointB.y;
                mCurrentCanvas.drawLine(pp1.x, pp1.y, movePointB.x, movePointB.y, mCurrentPaint);
                for (int i = 0; i < lineList.size(); i++) {
                    if (abs(currentY / currentX - lineList.get(i).getAngle()) < 0.01) {
                        Log.d("xxx", "i=" + currentY / currentX + "j=" + lineList.get(i).getAngle());
                        mCurrentCanvas.drawLine(lineList.get(i).getP1().x, lineList.get(i).getP1().y,
                                lineList.get(i).getP2().x, lineList.get(i).getP2().y, paint);
                    }
                }
            }
            //端点拖动
            if (clip1) {
                reMoveR(lineList, lineTemp);
                pp2 = new PointF();
                pp2.x = lineTemp.getP2().x;
                pp2.y = lineTemp.getP2().y;
                mCurrentCanvas.drawLine(movePointB.x, movePointB.y, pp2.x, pp2.y, mCurrentPaint);
            }
            if (!touchMove) {
                //Random r=new Random(1);
               // for(int i=0;i<1000000;i++){
                //    mBitmapCanvas.drawPoint(r.nextInt(5472),r.nextInt(3648),paint);
               // }
//                mBitmapCanvas.drawText("" + lineD, pp3.x + 20, pp3.y - 20, paintT);
                mBitmapCanvas.drawCircle(pp1.x, pp1.y, 10, paint);
                mBitmapCanvas.drawCircle(pp2.x, pp2.y, 10, paint);
                mBitmapCanvas.drawCircle(pp3.x, pp3.y, 10, paint);
            }
        }
    }
    //圆的移动改变
    private void circleChange() {
        if (flag == 0&& chooseCircle > 0) {
            float xx = movePointB.x - downPointB.x;
            float yy = movePointB.y - downPointB.y;
            pp1 = new PointF();
            pp1.x = circleTemp.getPoint().x + xx;
            pp1.y = circleTemp.getPoint().y + yy;
            if (move) {
                reMoveR(circleList, circleTemp);
                mCurrentCanvas.drawCircle(pp1.x, pp1.y, circleTemp.getR(), mCurrentPaint);
                mCurrentCanvas.drawCircle(pp1.x, pp1.y, 10, paint);
            }
            if (sCirle) {
                reMoveR(circleList, circleTemp);
                circleR = (float) (lineSpace(movePointB.x, movePointB.y, circleTemp.getPoint().x, circleTemp.getPoint().y));
                mCurrentCanvas.drawCircle(circleTemp.getPoint().x, circleTemp.getPoint().y, circleR, mCurrentPaint);
               /* if (lineSpace(movePointB.x, movePointB.y, circleTemp.getPoint().x - xx, circleTemp.getPoint().y - yy) > lineSpace(downPointB.x, downPointB.y, circleTemp.getPoint().x - xx, circleTemp.getPoint().y - yy)) {
                    circleR = (float) (circleTemp.getR() + lineSpace(movePointB.x, movePointB.y, downPointB.x, downPointB.y));
                    mCurrentCanvas.drawCircle(circleTemp.getPoint().x, circleTemp.getPoint().y, circleR, mCurrentPaint);
                } else {
                    circleR = (float) (circleTemp.getR() - lineSpace(movePointB.x, movePointB.y, downPointB.x, downPointB.y));
                    mCurrentCanvas.drawCircle(circleTemp.getPoint().x, circleTemp.getPoint().y, circleR, mCurrentPaint);
                }*/
                mCurrentCanvas.drawCircle(circleTemp.getPoint().x, circleTemp.getPoint().y, 10, paint);
            }
            if (!touchMove) {
                mBitmapCanvas.drawCircle(pp1.x, pp1.y, 10, paint);
                mBitmapCanvas.drawCircle(pp1.x + circleTemp.getR(), pp1.y, 10, paint);
            }
        }
    }
    //矩形的移动改变
    private void rectChange() {
        if (flag == 0 && chooseRect > 0) {
            float xx = movePointB.x - downPointB.x;
            float yy = movePointB.y - downPointB.y;
            pp1 = new PointF();
            pp1.x = rectTemp.getP1().x + xx;
            pp1.y = rectTemp.getP1().y + yy;
            pp2 = new PointF();
            pp2.x = rectTemp.getP2().x + xx;
            pp2.y = rectTemp.getP2().y + yy;
            pp3 = new PointF();
            pp3.x = (pp1.x + (pp2.x - pp1.x) / 2);
            pp3.y = (pp1.y + (pp2.y - pp1.y) / 2);
            double abc = Math.toRadians(360 - rectTemp.getAngle());
            pxd = new PointF();
            pxd.x = (float) ((movePointB.x - rectTemp.getPmid().x) * Math.cos(abc) - (movePointB.y - rectTemp.getPmid().y) * Math.sin(abc) + rectTemp.getPmid().x);
            pxd.y = (float) ((movePointB.x - rectTemp.getPmid().x) * Math.sin(abc) + (movePointB.y - rectTemp.getPmid().y) * Math.cos(abc) + rectTemp.getPmid().y);
            if (move) {
                reMoveR(rectList, rectTemp);
                mCurrentCanvas.rotate((float) rectTemp.getAngle(), pp3.x, pp3.y);
                mCurrentCanvas.drawRect(pp1.x, pp1.y, pp2.x, pp2.y, mCurrentPaint);
                mCurrentCanvas.drawCircle(pp3.x, pp3.y, 10, paint);
            }
            /*if (clip1) {
                if (ff) {
                    ff = false;
                    rectList.remove(rectTemp);
                    initCanvas();//添上这句防止重复绘制
                    draw1(mBitmapCanvas); // 保存到图片中
                    invalidate();
                }
                pp2 = new PointF();
                pp2.x = rectTemp.getP2().x;
                pp2.y = rectTemp.getP2().y;
                mCurrentCanvas.drawRect(movePointB.x, movePointB.y, pp2.x, pp2.y, mCurrentPaint);
            }
            if (clip2) {
                if (ff) {
                    ff = false;
                    rectList.remove(rectTemp);
                    initCanvas();//添上这句防止重复绘制
                    draw1(mBitmapCanvas); // 保存到图片中
                    invalidate();
                }
                pp1 = new PointF();
                pp1.x = rectTemp.getP1().x;
                pp1.y = rectTemp.getP1().y;
                mCurrentCanvas.drawRect(pp1.x, pp1.y, movePointB.x, movePointB.y, mCurrentPaint);

            }*/
            if (isClip1) {
                reMoveR(rectList, rectTemp);
                mCurrentCanvas.rotate((float) rectTemp.getAngle(), rectTemp.getPmid().x, rectTemp.getPmid().y);
                mCurrentCanvas.drawRect(rectTemp.getP1().x, pxd.y, rectTemp.getP2().x, rectTemp.getP2().y, mCurrentPaint);
            }
            if (isClip2) {
                reMoveR(rectList, rectTemp);
                mCurrentCanvas.rotate((float) rectTemp.getAngle(), rectTemp.getPmid().x, rectTemp.getPmid().y);
                mCurrentCanvas.drawRect(rectTemp.getP1().x, rectTemp.getP1().y, rectTemp.getP2().x, pxd.y, mCurrentPaint);
            }
            if (isClip3) {
                reMoveR(rectList, rectTemp);
                mCurrentCanvas.rotate((float) rectTemp.getAngle(), rectTemp.getPmid().x, rectTemp.getPmid().y);
                mCurrentCanvas.drawRect(pxd.x, rectTemp.getP1().y, rectTemp.getP2().x, rectTemp.getP2().y, mCurrentPaint);
            }
            if (isClip4) {
                reMoveR(rectList, rectTemp);
                mCurrentCanvas.rotate((float) rectTemp.getAngle(), rectTemp.getPmid().x, rectTemp.getPmid().y);
                mCurrentCanvas.drawRect(rectTemp.getP1().x, rectTemp.getP1().y, pxd.x, rectTemp.getP2().y, mCurrentPaint);
            }
            if (rotate) {
                reMoveR(rectList, rectTemp);
                double angle = calcAngle(rectTemp.getPmid(), rectTemp.getLp1(), movePointB);
                rectAngle = angle;
                Log.d("SSS", "angle=" + angle);
                double left = rectTemp.getP1().x;
                double right = rectTemp.getP2().x;
                double top = rectTemp.getP1().y;
                double bottom = rectTemp.getP2().y;
                mCurrentCanvas.rotate((float) angle, (float) (left + (right - left) / 2), (float) (top + (bottom - top) / 2));
                mCurrentCanvas.drawRect(rectTemp.getP1().x, rectTemp.getP1().y, rectTemp.getP2().x, rectTemp.getP2().y, mCurrentPaint);
                mCurrentCanvas.rotate((float) -angle, (float) (left + (right - left) / 2), (float) (top + (bottom - top) / 2));
            }
           /* if(clip3){
                pp5 = new PointF();
                pp5.x=rectList.get(chooseRect - 1).getP4().x;
                pp5.y=rectList.get(chooseRect - 1).getP4().y;
                mCurrentCanvas.drawRect(movePointB.x, movePointB.y,pp5.x, pp5.y, mCurrentPaint);
            }
           /* if(clip4){
                pp4 = new PointF();
                pp4.x=rectList.get(chooseRect - 1).getP3().x;
                pp4.y=rectList.get(chooseRect - 1).getP3().y;
                mCurrentCanvas.drawRect(pp4.x, pp4.y,movePointB.x, movePointB.y, mCurrentPaint);
            }*/
            if (!touchMove) {
                mBitmapCanvas.drawCircle(rectTemp.getPmid().x, rectTemp.getPmid().y, 10, paint);
                double ang = rectTemp.getAngle();
                ang = Math.toRadians(ang);
                float mx = rectTemp.getPmid().x;
                float my = rectTemp.getPmid().y;
                double l = rectTemp.getL() / 2;
                double w = rectTemp.getW() / 2;
                //mBitmapCanvas.drawCircle((float)(mx-l*Math.cos(ang)),(float)(my-l*Math.sin(ang)),10,paint);
                // mBitmapCanvas.drawCircle((float)(mx+l*Math.cos(ang)),(float)(my+l*Math.sin(ang)),10,paint);
                mBitmapCanvas.drawCircle(rectTemp.getZwp1().x, rectTemp.getZwp1().y, 10, paint);
                mBitmapCanvas.drawCircle(rectTemp.getZwp2().x, rectTemp.getZwp2().y, 10, paint);
                mBitmapCanvas.drawCircle(rectTemp.getZlp1().x, rectTemp.getZlp1().y, 10, paint);
                mBitmapCanvas.drawCircle(rectTemp.getZlp2().x, rectTemp.getZlp2().y, 10, paint);
                // mBitmapCanvas.drawCircle((float)(mx+w*Math.sin(ang)),(float)(my-w*Math.cos(ang)),10,paint);
                //  mBitmapCanvas.drawCircle((float)(mx-w*Math.sin(ang)),(float)(my+w*Math.cos(ang)),10,paint);
                //  mBitmapCanvas.drawCircle(rectTemp.getLp1().x, rectTemp.getLp1().y, 10, paint);
                // mBitmapCanvas.drawCircle(rectTemp.getLp2().x, rectTemp.getLp2().y, 10, paint);
                // mBitmapCanvas.drawCircle(rectTemp.getWp1().x, rectTemp.getWp1().y, 10, paint);
                //mBitmapCanvas.drawCircle(rectTemp.getWp2().x, rectTemp.getWp2().y, 10, paint);
                PointF pointF = new PointF();
                pointF.x = (float) (mx + (w + 150) * Math.sin(ang));
                pointF.y = (float) (my - (w + 150) * Math.cos(ang));
                drawAL(rectTemp.getPmid(), pointF);
            }
        }
    }
    //椭圆的移动改变
    private void ovalChange() {
        if (flag == 0&& chooseOval > 0) {
            float xx = movePointB.x - downPointB.x;
            float yy = movePointB.y - downPointB.y;
            float left = ovalTemp.getRectF().left + xx;
            float top = ovalTemp.getRectF().top + yy;
            float right = ovalTemp.getRectF().right + xx;
            float bottom = ovalTemp.getRectF().bottom + yy;
            rectF = new RectF(left, top, right, bottom);
            PointF pm = new PointF();
            pm.x = left + (right - left) / 2;
            pm.y = top + (bottom - top) / 2;
            double abc = Math.toRadians(360 - ovalTemp.getAngle());
            pxd = new PointF();
            pxd.x = (float) ((movePointB.x - ovalTemp.getPmid().x) * Math.cos(abc) - (movePointB.y - ovalTemp.getPmid().y) * Math.sin(abc) + ovalTemp.getPmid().x);
            pxd.y = (float) ((movePointB.x - ovalTemp.getPmid().x) * Math.sin(abc) + (movePointB.y - ovalTemp.getPmid().y) * Math.cos(abc) + ovalTemp.getPmid().y);

            if (move) {
                reMoveR(ovalList, ovalTemp);
                mCurrentCanvas.rotate((float) ovalTemp.getAngle(), pm.x, pm.y);
                mCurrentCanvas.drawOval(rectF, mCurrentPaint);
            }
            if (isClip1) {
                reMoveR(ovalList, ovalTemp);
                rectF = new RectF(ovalTemp.getLmp().x, pxd.y, ovalTemp.getRmp().x, ovalTemp.getBmp().y);
                mCurrentCanvas.rotate((float) ovalTemp.getAngle(), ovalTemp.getPmid().x, ovalTemp.getPmid().y);
                mCurrentCanvas.drawOval(rectF, mCurrentPaint);
            }
            if (isClip2) {
                reMoveR(ovalList, ovalTemp);
                rectF = new RectF(ovalTemp.getLmp().x, ovalTemp.getTmp().y, ovalTemp.getRmp().x, pxd.y);
                mCurrentCanvas.rotate((float) ovalTemp.getAngle(), ovalTemp.getPmid().x, ovalTemp.getPmid().y);
                mCurrentCanvas.drawOval(rectF, mCurrentPaint);
            }
            if (isClip3) {
                reMoveR(ovalList, ovalTemp);
                rectF = new RectF(pxd.x, ovalTemp.getTmp().y, ovalTemp.getRmp().x, ovalTemp.getBmp().y);
                mCurrentCanvas.rotate((float) ovalTemp.getAngle(), ovalTemp.getPmid().x, ovalTemp.getPmid().y);
                mCurrentCanvas.drawOval(rectF, mCurrentPaint);
            }
            if (isClip4) {
                reMoveR(ovalList, ovalTemp);
                rectF = new RectF(ovalTemp.getLmp().x, ovalTemp.getTmp().y, pxd.x, ovalTemp.getBmp().y);
                mCurrentCanvas.rotate((float) ovalTemp.getAngle(), ovalTemp.getPmid().x, ovalTemp.getPmid().y);
                mCurrentCanvas.drawOval(rectF, mCurrentPaint);
            }
            if (rotate) {
                reMoveR(ovalList, ovalTemp);
                double angle = calcAngle(ovalTemp.getPmid(), ovalTemp.getTmp(), movePointB);
                ovalAngle = angle;
                mCurrentCanvas.rotate((float) angle, ovalTemp.getPmid().x, ovalTemp.getPmid().y);
                //以椭圆中心点旋转图形
                mCurrentCanvas.drawOval(ovalTemp.getRectF(), mCurrentPaint);
                //mCurrentCanvas.rotate((float) angle, ovalTemp.getPmid().x, ovalTemp.getPmid().y);
            }
            if (!touchMove) {
                mBitmapCanvas.drawCircle(ovalTemp.getPmid().x, ovalTemp.getPmid().y, 10, paint);
                mBitmapCanvas.drawCircle(ovalTemp.getZlmp().x, ovalTemp.getZlmp().y, 10, paint);
                mBitmapCanvas.drawCircle(ovalTemp.getZrmp().x, ovalTemp.getZrmp().y, 10, paint);
                mBitmapCanvas.drawCircle(ovalTemp.getZtmp().x, ovalTemp.getZtmp().y, 10, paint);
                mBitmapCanvas.drawCircle(ovalTemp.getZbmp().x, ovalTemp.getZbmp().y, 10, paint);
                float mx = ovalTemp.getPmid().x;
                float my = ovalTemp.getPmid().y;
                double l = ovalTemp.getL() / 2;
                double w = ovalTemp.getW() / 2;
                double ang = ovalTemp.getAngle();
                ang = Math.toRadians(ang);
                PointF pointF = new PointF();
                pointF.x = (float) (mx + (w + 150) * Math.sin(ang));
                pointF.y = (float) (my - (w + 150) * Math.cos(ang));
                drawAL(ovalTemp.getPmid(), pointF);
            }

        }
    }
    private void brokenLineChange(){
        if(flag==0&&chooseBrokenLine>0){
            if(!touchMove){
                for(PointF pointF:brokenLineTemp.getBList()){
                    mBitmapCanvas.drawCircle(pointF.x,pointF.y,10, paint);
                }
            }
            for(int i=0;i<brokenLineTemp.getBList().size();i++){
                if(i==choosePoint){
                    reMoveR(brokenLineList,brokenLineTemp);
                    brokenLineTemp.getBList().set(i,movePointB);
                    for(int j=1;j<brokenLineTemp.getBList().size();j++){
                        mCurrentCanvas.drawLine(brokenLineTemp.getBList().get(j-1).x,brokenLineTemp.getBList().get(j-1).y,brokenLineTemp.getBList().get(j).x,brokenLineTemp.getBList().get(j).y,mCurrentPaint);
                    }
                    break;
                }
            }
        }
    }
    float xmd,ymd;
    //封闭图形的移动改变
    private void polygonChange(){
        if (flag == 0  && choosePolygon > 0) {
            if (!touchMove) {
                for (int i = 0; i < polygonTemp.getPointFList().size(); i++) {
                    mBitmapCanvas.drawCircle(polygonTemp.getPointFList().get(i).x, polygonTemp.getPointFList().get(i).y, 10, paint);
                }
            }
            if(move){
                xmd=movePointB.x-polygonTemp.getPm().x;
                ymd=movePointB.y-polygonTemp.getPm().y;
                reMoveR(polygonList,polygonTemp);
                for (int j = 1; j< polygonTemp.getPointFList().size(); j++) {
                    mCurrentCanvas.drawLine(polygonTemp.getPointFList().get(j- 1).x+xmd,polygonTemp.getPointFList().get(j - 1).y+ymd, polygonTemp.getPointFList().get(j).x+xmd, polygonTemp.getPointFList().get(j).y+ymd, mCurrentPaint);
                }
                if (polygonTemp.getPointFList().size() > 2) {
                    mCurrentCanvas.drawLine(polygonTemp.getPointFList().get(0).x+xmd, polygonTemp.getPointFList().get(0).y+ymd, polygonTemp.getPointFList().get(polygonTemp.getPointFList().size() - 1).x+xmd, polygonTemp.getPointFList().get(polygonTemp.getPointFList().size() - 1).y+ymd, mCurrentPaint);
                }
            }
            for(int i=0;i<polygonTemp.getPointFList().size();i++){
                if(i==choosePoint) {
                    reMoveR(polygonList,polygonTemp);
                    polygonTemp.getPointFList().set(i,movePointB);
                    for (int j = 1; j< polygonTemp.getPointFList().size(); j++) {
                        mCurrentCanvas.drawLine(polygonTemp.getPointFList().get(j- 1).x,polygonTemp.getPointFList().get(j - 1).y, polygonTemp.getPointFList().get(j).x, polygonTemp.getPointFList().get(j).y, mCurrentPaint);
                    }
                    if (polygonTemp.getPointFList().size() > 2) {
                        mCurrentCanvas.drawLine(polygonTemp.getPointFList().get(0).x, polygonTemp.getPointFList().get(0).y, polygonTemp.getPointFList().get(polygonTemp.getPointFList().size() - 1).x, polygonTemp.getPointFList().get(polygonTemp.getPointFList().size() - 1).y, mCurrentPaint);
                    }
                    break;
                }
            }
        }
    }
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchMove = false;
                downPoint = new PointF();
                downPoint.x = event.getX();
                downPoint.y = event.getY();
                downPointB = new PointF();
                downPointB.x = screenToBitmapX(downPoint.x);
                downPointB.y = screenToBitmapY(downPoint.y);
                listener.onPoint(downPointB);
                if (flag == 6 || flag == 5) {
                    pointFS.add(downPointB);
                }
                if (flag == 0) {
                    isUp = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isUp) {
                                if (!move && !clip2 && !clip1 && !sCirle && !isClip1 && !isClip2 && !isClip3 && !isClip4 && !rotate&&choosePoint==-1) {
                                    longChoose(downPointB);
                                }
                                return;
                            }
                        }
                    }, 500);
                    if (!mIsMove && lineList.size() > 0 && chooseLine > 0) {

                        double dp1 = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), lineTemp.getP1().x, lineTemp.getP1().y);
                        double dp2 = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), lineTemp.getP2().x, lineTemp.getP2().y);
                        double d = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), lineTemp.getPmid().x, lineTemp.getPmid().y);
                        if (d < 100) {
                            move = true;
                        } else if (dp2 < 100) {
                            clip2 = true;
                        } else if (dp1 < 100) {
                            clip1 = true;
                        }else{
                            chooseLine=0;
                        }
                    }
                    if (!mIsMove && circleList.size() > 0 && chooseCircle > 0) {
                        double d = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), circleTemp.getPoint().x, circleTemp.getPoint().y);
                        double d1 = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), circleTemp.getPoint().x + circleTemp.getR(), circleTemp.getPoint().y);
                        if (d < 100) {
                            move = true;
                        } else if (d1 < 100) {
                            sCirle = true;
                        }else{
                            chooseCircle=0;
                        }
                    }
                    if (!mIsMove && rectList.size() > 0 && chooseRect > 0) {
                        double ang = rectTemp.getAngle();
                        ang = Math.toRadians(ang);
                        float mx = rectTemp.getPmid().x;
                        float my = rectTemp.getPmid().y;
                        double l = rectTemp.getL() / 2;
                        double w = rectTemp.getW() / 2;
                        PointF pointF = new PointF();
                        pointF.x = (float) (mx + (w + 150) * Math.sin(ang));
                        pointF.y = (float) (my - (w + 150) * Math.cos(ang));
                        double d = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), rectTemp.getPmid().x, rectTemp.getPmid().y);
                        double dr = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), pointF.x, pointF.y);
                        double d5 = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), rectTemp.getZlp1().x, rectTemp.getZlp1().y);
                        double d6 = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), rectTemp.getZlp2().x, rectTemp.getZlp2().y);
                        double d7 = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), rectTemp.getZwp1().x, rectTemp.getZwp1().y);
                        double d8 = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), rectTemp.getZwp2().x, rectTemp.getZwp2().y);
                        if (d < 100) {
                            move = true;
                        } else if (dr < 100) {
                            rotate = true;
                        } else if (d5 < 100) {
                            isClip1 = true;
                        } else if (d6 < 100) {
                            isClip2 = true;
                        } else if (d7 < 100) {
                            isClip3 = true;
                        } else if (d8 < 100) {
                            isClip4 = true;
                        }else{chooseRect=0;}
                    }
                    if (!mIsMove && ovalList.size() > 0 && chooseOval > 0) {
                        float mx = ovalTemp.getPmid().x;
                        float my = ovalTemp.getPmid().y;
                        double l = ovalTemp.getL() / 2;
                        double w = ovalTemp.getW() / 2;
                        double ang = ovalTemp.getAngle();
                        ang = Math.toRadians(ang);
                        PointF pointF = new PointF();
                        pointF.x = (float) (mx + (w + 150) * Math.sin(ang));
                        pointF.y = (float) (my - (w + 150) * Math.cos(ang));
                        double d = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), ovalTemp.getPmid().x, ovalTemp.getPmid().y);
                        double dr = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), pointF.x, pointF.y);
                        double d1 = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), ovalTemp.getZtmp().x, ovalTemp.getZtmp().y);
                        double d2 = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), ovalTemp.getZbmp().x, ovalTemp.getZbmp().y);
                        double d3 = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), ovalTemp.getZlmp().x, ovalTemp.getZlmp().y);
                        double d4 = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), ovalTemp.getZrmp().x, ovalTemp.getZrmp().y);
                        if (d < 100) {
                            move = true;
                        } else if (dr < 100) {
                            rotate = true;
                        } else if (d1 < 100) {
                            isClip1 = true;
                        } else if (d2 < 100) {
                            isClip2 = true;
                        } else if (d3 < 100) {
                            isClip3 = true;
                        } else if (d4 < 100) {
                            isClip4 = true;
                        }else{chooseOval=0;}
                    }
                    if (!mIsMove && polygonList.size() > 0 && choosePolygon > 0) {
                        double d1=lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()),polygonTemp.getPm().x,polygonTemp.getPm().y);
                        if(d1<100) move=true;
                        for (int i = 0; i < polygonTemp.getPointFList().size(); i++) {
                            double d = lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()), polygonTemp.getPointFList().get(i).x, polygonTemp.getPointFList().get(i).y);
                            if (d < 100) {
                                choosePoint = i;
                                move=false;
                                break;
                            }
                        }

                    }
                    if(!mIsMove&&brokenLineList.size()>0&&chooseBrokenLine>0){
                        int i=0;
                        for(PointF pointF:brokenLineTemp.getBList()){
                            double d=lineSpace(screenToBitmapX(event.getX()), screenToBitmapY(event.getY()),pointF.x,pointF.y);
                            if(d<100){
                                choosePoint=i;
                                break;
                            }
                            i++;
                        }

                    }
                }
                upflag = 0;
                mTouchMode = 1;
                penTouchDown(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_UP:
                if (mTouchMode == 1 && !mIsMove) {
                    upPoint = new PointF();
                    upPoint.x = event.getX();
                    upPoint.y = event.getY();
                    upPointB = new PointF();
                    upPointB.x = screenToBitmapX(upPoint.x);
                    upPointB.y = screenToBitmapY(upPoint.y);
                    listener.onPoint(upPointB);
                    isUp = true;
                    upflag = 1;
                    if (upflag == 1) {
                        saveToArr();
                        if (flag == 0  && touchMove) {
                            if (chooseLine > 0) lineUp();
                            if (chooseCircle > 0) circleUp();
                            if (chooseOval > 0) ovalUp();
                            if (chooseRect > 0) rectUp();
                            if(choosePolygon>0)polygonUp();
                            if(chooseBrokenLine>0)brokenLineUp();
                        }
                    }
                    move = false;
                    touchMove = false;
                    clip2 = false;
                    clip1 = false;
                    sCirle = false;
                    isClip1 = false;
                    isClip2 = false;
                    isClip3 = false;
                    isClip4 = false;
                    rotate = false;
                    rectF = new RectF();
                    mTouchMode = 0;
                    ff = true;
                    if (flag != 6 && flag != 5) {
                        downPointB = new PointF();
                        movePointB = new PointF();
                       // pointFS = new ArrayList<>();
                        flag = 0;
                        initCanvas();//添上这句防止重复绘制
                        draw1(mBitmapCanvas); // 保存到图片中
                        invalidate();
                    }else {
                        invalidate();
                    }
                    rectAngle = 0;
                    ovalAngle = 0;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mTouchMode == 1 && !mIsMove) {
                    touchMove = true;
                    movePoint = new PointF();
                    movePoint.x = event.getX();
                    movePoint.y = event.getY();
                    movePointB = new PointF();
                    movePointB.x = screenToBitmapX(movePoint.x);
                    movePointB.y = screenToBitmapY(movePoint.y);
                    listener.onPoint(movePointB);
                    invalidate();
                }
                return true;
        }
        return true;
    }
    //直线操作抬手保存数据
    private void lineUp() {
        if (move) {
            reMoveR(lineList, lineTemp);
            Line line = new Line();
            line.setP1(pp1);
            line.setP2(pp2);
            line.setPaint(lineTemp.getPaint());
            lineList.add(line);
            lineTemp = line;
        }
        if (clip2) {
            reMoveR(lineList, lineTemp);
            pp3 = new PointF();
            pp3.x = (pp1.x + (movePointB.x - pp1.x) / 2);
            pp3.y = (pp1.y + (movePointB.y - pp1.y) / 2);
            Line line = new Line();
            line.setP1(lineTemp.getP1());
            line.setP2(movePointB);
            line.setPaint(lineTemp.getPaint());
            lineList.add(line);
            lineTemp = line;
        }
        if (clip1) {
            reMoveR(lineList, lineTemp);
            pp3 = new PointF();
            pp3.x = (movePointB.x + (pp2.x - movePointB.x) / 2);
            pp3.y = (movePointB.y + (pp2.y - movePointB.y) / 2);
            Line line = new Line();
            line.setP1(movePointB);
            line.setP2(lineTemp.getP2());
            line.setPaint(lineTemp.getPaint());
            lineList.add(line);
            lineTemp = line;
        }
    }
    //圆操作抬手保存数据
    private void circleUp() {
        if (move) {
            reMoveR(circleList, circleTemp);
            Circle circle = new Circle();
            circle.setR(circleTemp.getR());
            circle.setPoint(pp1);
            circle.setPaint(circleTemp.getPaint());
            circleList.add(circle);
            circleTemp = circle;
        }
        if (sCirle) {
            reMoveR(circleList, circleTemp);
            Circle circle = new Circle();
            circle.setR(circleR);
            circle.setPoint(circleTemp.getPoint());
            circle.setPaint(circleTemp.getPaint());
            circleList.add(circle);
            circleTemp = circle;
        }
    }
    //椭圆操作抬手保存数据
    private void ovalUp() {
        if (move) {
            reMoveR(ovalList, ovalTemp);
            Oval oval = new Oval();
            oval.setRectF(rectF);
            oval.setAngle(ovalTemp.getAngle());
            oval.setPaint(ovalTemp.getPaint());
            ovalList.add(oval);
            ovalTemp = oval;
        }
        if (isClip1) {
            reMoveR(ovalList, ovalTemp);
            PointF pmid = new PointF();
            pmid.x = ovalTemp.getPmid().x;
            pmid.y = pxd.y + (ovalTemp.getRectF().bottom - pxd.y) / 2;
            double d = Math.sqrt((pmid.x - ovalTemp.getPmid().x) * (pmid.x - ovalTemp.getPmid().x) + (pmid.y - ovalTemp.getPmid().y) * (pmid.y - ovalTemp.getPmid().y));
            PointF pdc = new PointF();
            if (pmid.y > ovalTemp.getPmid().y) {
                pdc.x = (float) (ovalTemp.getPmid().x - d * Math.sin(Math.toRadians(ovalTemp.getAngle())));
                pdc.y = (float) (ovalTemp.getPmid().y + d * Math.cos(Math.toRadians(ovalTemp.getAngle())));
            } else {
                pdc.x = (float) (ovalTemp.getPmid().x + d * Math.sin(Math.toRadians(ovalTemp.getAngle())));
                pdc.y = (float) (ovalTemp.getPmid().y - d * Math.cos(Math.toRadians(ovalTemp.getAngle())));
            }
            float xxx = pdc.x - pmid.x;
            float yyy = pdc.y - pmid.y;
            RectF rect = new RectF();
            if((ovalTemp.getBmp().y-ovalTemp.getPmid().y)*(ovalTemp.getBmp().y-pmid.y)>0)
                rect.set(ovalTemp.getLmp().x + xxx, pxd.y + yyy, ovalTemp.getRmp().x + xxx, ovalTemp.getBmp().y + yyy);
            else
                rect.set(ovalTemp.getLmp().x + xxx,ovalTemp.getBmp().y + yyy , ovalTemp.getRmp().x + xxx, pxd.y + yyy);
            Oval oval = new Oval();
            oval.setRectF(rect);
            oval.setAngle(ovalTemp.getAngle());
            oval.setPaint(ovalTemp.getPaint());
            ovalList.add(oval);
            ovalTemp = oval;
        }
        if (isClip2) {
            reMoveR(ovalList, ovalTemp);
            PointF pmid = new PointF();
            pmid.x = ovalTemp.getPmid().x;
            pmid.y = pxd.y + (ovalTemp.getRectF().top - pxd.y) / 2;
            double d = Math.sqrt((pmid.x - ovalTemp.getPmid().x) * (pmid.x - ovalTemp.getPmid().x) + (pmid.y - ovalTemp.getPmid().y) * (pmid.y - ovalTemp.getPmid().y));
            PointF pdc = new PointF();
            if (pmid.y > ovalTemp.getPmid().y) {
                pdc.x = (float) (ovalTemp.getPmid().x - d * Math.sin(Math.toRadians(ovalTemp.getAngle())));
                pdc.y = (float) (ovalTemp.getPmid().y + d * Math.cos(Math.toRadians(ovalTemp.getAngle())));
            } else {
                pdc.x = (float) (ovalTemp.getPmid().x + d * Math.sin(Math.toRadians(ovalTemp.getAngle())));
                pdc.y = (float) (ovalTemp.getPmid().y - d * Math.cos(Math.toRadians(ovalTemp.getAngle())));
            }

            float xxx = pdc.x - pmid.x;
            float yyy = pdc.y - pmid.y;
            RectF rect = new RectF();
            if((ovalTemp.getTmp().y-ovalTemp.getPmid().y)*(ovalTemp.getTmp().y-pmid.y)>0)
                rect.set(ovalTemp.getLmp().x + xxx, ovalTemp.getTmp().y + yyy, ovalTemp.getRmp().x + xxx, pxd.y + yyy);
            else
                rect.set(ovalTemp.getLmp().x + xxx, pxd.y + yyy, ovalTemp.getRmp().x + xxx, ovalTemp.getTmp().y + yyy);

            Oval oval = new Oval();
            oval.setRectF(rect);
            oval.setAngle(ovalTemp.getAngle());
            oval.setPaint(ovalTemp.getPaint());
            ovalList.add(oval);
            ovalTemp = oval;
        }
        if (isClip3) {
            reMoveR(ovalList, ovalTemp);
            PointF pmid = new PointF();
            pmid.x = pxd.x + (ovalTemp.getRectF().right - pxd.x) / 2;
            pmid.y = ovalTemp.getPmid().y;
            double d = Math.sqrt((pmid.x - ovalTemp.getPmid().x) * (pmid.x - ovalTemp.getPmid().x) + (pmid.y - ovalTemp.getPmid().y) * (pmid.y - ovalTemp.getPmid().y));
            PointF pdc = new PointF();
            if (pmid.x < ovalTemp.getPmid().x) {
                pdc.x = (float) (ovalTemp.getPmid().x - d * Math.cos(Math.toRadians(ovalTemp.getAngle())));
                pdc.y = (float) (ovalTemp.getPmid().y - d * Math.sin(Math.toRadians(ovalTemp.getAngle())));
            } else {
                pdc.x = (float) (ovalTemp.getPmid().x + d * Math.cos(Math.toRadians(ovalTemp.getAngle())));
                pdc.y = (float) (ovalTemp.getPmid().y + d * Math.sin(Math.toRadians(ovalTemp.getAngle())));
            }
            float xxx = pdc.x - pmid.x;
            float yyy = pdc.y - pmid.y;
            RectF rect = new RectF();
            if((ovalTemp.getRmp().x-ovalTemp.getPmid().x)*(ovalTemp.getRmp().x-pmid.x)>0)
                rect.set(pxd.x + xxx, ovalTemp.getTmp().y + yyy, ovalTemp.getRmp().x + xxx, ovalTemp.getBmp().y + yyy);
            else{
                rect.set(ovalTemp.getRmp().x + xxx, ovalTemp.getTmp().y + yyy, pxd.x + xxx, ovalTemp.getBmp().y + yyy);
            }
            Oval oval = new Oval();
            oval.setRectF(rect);
            oval.setAngle(ovalTemp.getAngle());
            oval.setPaint(ovalTemp.getPaint());
            ovalList.add(oval);
            ovalTemp = oval;
        }
        if (isClip4) {
            reMoveR(ovalList, ovalTemp);
            PointF pmid = new PointF();
            pmid.x = pxd.x + (ovalTemp.getRectF().left - pxd.x) / 2;
            pmid.y = ovalTemp.getPmid().y;
            double d = Math.sqrt((pmid.x - ovalTemp.getPmid().x) * (pmid.x - ovalTemp.getPmid().x) + (pmid.y - ovalTemp.getPmid().y) * (pmid.y - ovalTemp.getPmid().y));
            PointF pdc = new PointF();
            if (pmid.x < ovalTemp.getPmid().x) {
                pdc.x = (float) (ovalTemp.getPmid().x - d * Math.cos(Math.toRadians(ovalTemp.getAngle())));
                pdc.y = (float) (ovalTemp.getPmid().y - d * Math.sin(Math.toRadians(ovalTemp.getAngle())));
            } else {
                pdc.x = (float) (ovalTemp.getPmid().x + d * Math.cos(Math.toRadians(ovalTemp.getAngle())));
                pdc.y = (float) (ovalTemp.getPmid().y + d * Math.sin(Math.toRadians(ovalTemp.getAngle())));
            }
            float xxx = pdc.x - pmid.x;
            float yyy = pdc.y - pmid.y;
            RectF rect = new RectF();
            if((ovalTemp.getLmp().x-ovalTemp.getPmid().x)*(ovalTemp.getLmp().x-pmid.x)>0)
                rect.set(ovalTemp.getLmp().x + xxx, ovalTemp.getTmp().y + yyy, pxd.x + xxx, ovalTemp.getBmp().y + yyy);
            else
                rect.set(pxd.x + xxx, ovalTemp.getTmp().y + yyy, ovalTemp.getLmp().x + xxx, ovalTemp.getBmp().y + yyy);
            Oval oval = new Oval();
            oval.setAngle(ovalTemp.getAngle());
            oval.setRectF(rect);
            oval.setPaint(ovalTemp.getPaint());
            ovalList.add(oval);
            ovalTemp = oval;
        }
        if (rotate) {
            reMoveR(ovalList, ovalTemp);
            Oval oval = new Oval();
            oval.setRectF(ovalTemp.getRectF());
            oval.setAngle(ovalAngle);
            oval.setPaint(ovalTemp.getPaint());
            ovalList.add(oval);
            ovalTemp = oval;
        }
    }
    //矩形操作抬手保存数据
    private void rectUp() {
        if (move) {
            reMoveR(rectList, rectTemp);
            Rect rect = new Rect();
            rect.setP1(pp1);
            rect.setP2(pp2);
            rect.setAngle(rectTemp.getAngle());
            rect.setPaint(rectTemp.getPaint());
            rectList.add(rect);
            rectTemp = rect;
        }
        if (clip2) {
            reMoveR(rectList, rectTemp);
            pp3 = new PointF();
            pp3.x = (pp1.x + (movePointB.x - pp1.x) / 2);
            pp3.y = (pp1.y + (movePointB.y - pp1.y) / 2);
            PointF p = new PointF();
            p.x = movePointB.x;
            p.y = rectList.get(chooseRect - 1).getP3().y;
            rectList.get(chooseRect - 1).setP3(p);
            p = new PointF();
            p.x = rectList.get(chooseRect - 1).getP4().x;
            p.y = movePointB.y;
            rectList.get(chooseRect - 1).setP4(p);
            rectList.get(chooseRect - 1).setP2(movePointB);
            rectList.get(chooseRect - 1).setPmid(pp3);
        }
        if (clip1) {
            reMoveR(rectList, rectTemp);
            pp3 = new PointF();
            pp3.x = (movePointB.x + (pp2.x - movePointB.x) / 2);
            pp3.y = (movePointB.y + (pp2.y - movePointB.y) / 2);
            PointF p = new PointF();
            p.x = movePointB.x;
            p.y = rectList.get(chooseRect - 1).getP4().y;
            rectList.get(chooseRect - 1).setP4(p);
            p = new PointF();
            p.x = rectList.get(chooseRect - 1).getP3().x;
            p.y = movePointB.y;
            rectList.get(chooseRect - 1).setP3(p);
            rectList.get(chooseRect - 1).setP1(movePointB);
            rectList.get(chooseRect - 1).setPmid(pp3);
        }
        if (isClip1) {
            reMoveR(rectList, rectTemp);
            PointF pmid = new PointF();
            pmid.x = rectTemp.getP1().x + (rectTemp.getP2().x - rectTemp.getP1().x) / 2;
            pmid.y = pxd.y + (rectTemp.getP2().y - pxd.y) / 2;
            double d = Math.sqrt((pmid.x - rectTemp.getPmid().x) * (pmid.x - rectTemp.getPmid().x) + (pmid.y - rectTemp.getPmid().y) * (pmid.y - rectTemp.getPmid().y));
            PointF pdc = new PointF();
            if (pmid.y > rectTemp.getPmid().y) {
                pdc.x = (float) (rectTemp.getPmid().x - d * Math.sin(Math.toRadians(rectTemp.getAngle())));
                pdc.y = (float) (rectTemp.getPmid().y + d * Math.cos(Math.toRadians(rectTemp.getAngle())));
            } else {
                pdc.x = (float) (rectTemp.getPmid().x + d * Math.sin(Math.toRadians(rectTemp.getAngle())));
                pdc.y = (float) (rectTemp.getPmid().y - d * Math.cos(Math.toRadians(rectTemp.getAngle())));
            }
            float xxx = pdc.x - pmid.x;
            float yyy = pdc.y - pmid.y;
            Rect rect = new Rect();
            PointF pointF = new PointF();
            pointF.x = rectTemp.getP1().x + xxx;
            pointF.y = pxd.y + yyy;
            PointF p2 = new PointF();
            p2.x = rectTemp.getP2().x + xxx;
            p2.y = rectTemp.getP2().y + yyy;
            if((rectTemp.getP2().y-rectTemp.getPmid().y)*(rectTemp.getP2().y-pmid.y)<0) {
                float y=p2.y;
                p2.y=pointF.y;
                pointF.y=y;
            }
            rect.setP1(pointF);
            rect.setP2(p2);
            rect.setAngle(rectTemp.getAngle());
            rect.setPaint(rectTemp.getPaint());
            rectList.add(rect);
            rectTemp = rect;
        }
        if (isClip2) {
            reMoveR(rectList, rectTemp);
            PointF pmid = new PointF();
            pmid.x = rectTemp.getP1().x + (rectTemp.getP2().x - rectTemp.getP1().x) / 2;
            pmid.y = pxd.y + (rectTemp.getP1().y - pxd.y) / 2;
            double d = Math.sqrt((pmid.x - rectTemp.getPmid().x) * (pmid.x - rectTemp.getPmid().x) + (pmid.y - rectTemp.getPmid().y) * (pmid.y - rectTemp.getPmid().y));
            PointF pdc = new PointF();
            if (pmid.y > rectTemp.getPmid().y) {
                pdc.x = (float) (rectTemp.getPmid().x - d * Math.sin(Math.toRadians(rectTemp.getAngle())));
                pdc.y = (float) (rectTemp.getPmid().y + d * Math.cos(Math.toRadians(rectTemp.getAngle())));
            } else {
                pdc.x = (float) (rectTemp.getPmid().x + d * Math.sin(Math.toRadians(rectTemp.getAngle())));
                pdc.y = (float) (rectTemp.getPmid().y - d * Math.cos(Math.toRadians(rectTemp.getAngle())));
            }
            float xxx = pdc.x - pmid.x;
            float yyy = pdc.y - pmid.y;
            Rect rect = new Rect();
            PointF pointF = new PointF();
            pointF.x = rectTemp.getP2().x + xxx;
            pointF.y = pxd.y + yyy;
            PointF p1 = new PointF();
            p1.x = rectTemp.getP1().x + xxx;
            p1.y = rectTemp.getP1().y + yyy;
            if((rectTemp.getP1().y-rectTemp.getPmid().y)*(rectTemp.getP1().y-pmid.y)<0) {
                float y = p1.y;
                p1.y = pointF.y;
                pointF.y = y;
            }
            rect.setP1(p1);
            rect.setP2(pointF);
            rect.setAngle(rectTemp.getAngle());
            rect.setPaint(rectTemp.getPaint());
            rectList.add(rect);
            rectTemp = rect;
        }
        if (isClip3) {
            reMoveR(rectList, rectTemp);
            PointF pmid = new PointF();
            pmid.x = pxd.x + (rectTemp.getP2().x - pxd.x) / 2;
            pmid.y = rectTemp.getP1().y + (rectTemp.getP2().y - rectTemp.getP1().y) / 2;
            double d = Math.sqrt((pmid.x - rectTemp.getPmid().x) * (pmid.x - rectTemp.getPmid().x) + (pmid.y - rectTemp.getPmid().y) * (pmid.y - rectTemp.getPmid().y));
            PointF pdc = new PointF();
            if (pmid.x < rectTemp.getPmid().x) {
                pdc.y = (float) (rectTemp.getPmid().y - d * Math.sin(Math.toRadians(rectTemp.getAngle())));
                pdc.x = (float) (rectTemp.getPmid().x - d * Math.cos(Math.toRadians(rectTemp.getAngle())));
            } else {
                pdc.x = (float) (rectTemp.getPmid().x + d * Math.cos(Math.toRadians(rectTemp.getAngle())));
                pdc.y = (float) (rectTemp.getPmid().y + d * Math.sin(Math.toRadians(rectTemp.getAngle())));
            }
            float xxx = pdc.x - pmid.x;
            float yyy = pdc.y - pmid.y;
            Rect rect = new Rect();
            PointF pointF = new PointF();
            pointF.x = pxd.x + xxx;
            pointF.y = rectTemp.getP1().y + yyy;
            PointF p2 = new PointF();
            p2.x = rectTemp.getP2().x + xxx;
            p2.y = rectTemp.getP2().y + yyy;
            if((rectTemp.getP2().x-rectTemp.getPmid().x)*(rectTemp.getP2().x-pmid.x)<0) {
                float x=p2.x;
                p2.x=pointF.x;
                pointF.x=x;
            }
            rect.setP1(pointF);
            rect.setP2(p2);
            rect.setAngle(rectTemp.getAngle());
            rect.setPaint(rectTemp.getPaint());
            rectList.add(rect);
            rectTemp = rect;
        }
        if (isClip4) {
            reMoveR(rectList, rectTemp);
            PointF pmid = new PointF();
            pmid.x = pxd.x + (rectTemp.getP1().x - pxd.x) / 2;
            pmid.y = rectTemp.getP1().y + (rectTemp.getP2().y - rectTemp.getP1().y) / 2;
            double d = Math.sqrt((pmid.x - rectTemp.getPmid().x) * (pmid.x - rectTemp.getPmid().x) + (pmid.y - rectTemp.getPmid().y) * (pmid.y - rectTemp.getPmid().y));
            PointF pdc = new PointF();
            if (pmid.x < rectTemp.getPmid().x) {
                pdc.x = (float) (rectTemp.getPmid().x - d * Math.cos(Math.toRadians(rectTemp.getAngle())));
                pdc.y = (float) (rectTemp.getPmid().y - d * Math.sin(Math.toRadians(rectTemp.getAngle())));
            } else {
                pdc.x = (float) (rectTemp.getPmid().x + d * Math.cos(Math.toRadians(rectTemp.getAngle())));
                pdc.y = (float) (rectTemp.getPmid().y + d * Math.sin(Math.toRadians(rectTemp.getAngle())));
            }
            float xxx = pdc.x - pmid.x;
            float yyy = pdc.y - pmid.y;
            Rect rect = new Rect();
            PointF pointF = new PointF();
            pointF.x = pxd.x + xxx;
            pointF.y = rectTemp.getP2().y + yyy;
            PointF p1 = new PointF();
            p1.x = rectTemp.getP1().x + xxx;
            p1.y = rectTemp.getP1().y + yyy;
            if((rectTemp.getP1().x-rectTemp.getPmid().x)*(rectTemp.getP1().x-pmid.x)<0) {
                float x=p1.x;
                p1.x=pointF.x;
                pointF.x=x;
            }
            rect.setP1(p1);
            rect.setP2(pointF);
            rect.setAngle(rectTemp.getAngle());
            rect.setPaint(rectTemp.getPaint());
            rectList.add(rect);
            rectTemp = rect;
        }
        if (rotate) {
            reMoveR(rectList, rectTemp);
            Rect rect = new Rect();
            rect.setP1(rectTemp.getP1());
            rect.setP2(rectTemp.getP2());
            rect.setPmid(pp3);
            rect.setAngle(rectAngle);
            rect.setPaint(rectTemp.getPaint());
            rectList.add(rect);
            rectTemp = rect;
        }
        /*if(clip3){
                            pp3 = new PointF();
                            pp3.x=(pp5.x + (movePointB.x - pp5.x) / 2);
                            pp3.y=(pp5.y + (movePointB.y - pp5.y) / 2);
                            PointF p=new PointF();
                            p.x=movePointB.x;
                            p.y=rectList.get(chooseRect - 1).getP2().y;
                            rectList.get(chooseRect - 1).setP2(p);
                            p=new PointF();
                            p.x=rectList.get(chooseRect - 1).getP1().x;
                            p.y=movePointB.y;
                            rectList.get(chooseRect - 1).setP1(p);
                            rectList.get(chooseRect - 1).setP3(movePointB);
                            rectList.get(chooseRect - 1).setPmid(pp3);
                        }
                       /* if(clip4){
                            pp3 = new PointF();
                            pp3.x=(movePointB.x + (pp4.x - movePointB.x) / 2);
                            pp3.y=(movePointB.y + (pp4.y - movePointB.y) / 2);
                            PointF p=new PointF();
                            p.x=movePointB.x;
                            p.y=rectList.get(chooseRect - 1).getP2().y;
                            rectList.get(chooseRect - 1).setP2(p);
                            p=new PointF();
                            p.x=rectList.get(chooseRect - 1).getP1().x;
                            p.y=movePointB.y;
                            rectList.get(chooseRect - 1).setP1(p);
                            rectList.get(chooseRect - 1).setP4(movePointB);
                            rectList.get(chooseRect - 1).setPmid(pp3);
                        }*/
    }
    //封闭图形操作抬手保存数据
    private void polygonUp(){
        if(choosePoint>=0){
            polygonList.add(polygonTemp);
            choosePoint=-1;
        }
        if(move){
            Polygon polygon=new Polygon();
            List<PointF> pointFList=new ArrayList<>();
            for(int i=0;i<polygonTemp.getPointFList().size();i++){
                PointF pointF=new PointF();
                pointF.x=polygonTemp.getPointFList().get(i).x+xmd;
                pointF.y=polygonTemp.getPointFList().get(i).y+ymd;
                pointFList.add(pointF);
            }
            polygon.setPointFList(pointFList);
            polygon.setPaint(polygonTemp.getPaint());
            polygonList.add(polygon);
            polygonTemp=polygon;
        }
    }
    //折线操作抬手保存数据
    private void brokenLineUp(){
        if(choosePoint>=0){
           brokenLineList.add(brokenLineTemp);
           choosePoint=-1;

        }
    }
    //将数据保存到集合
    public void saveToArr() {
        if (flag == 1) {
            setPen();
            Line line = new Line();
            line.setP1(downPointB);
            line.setP2(upPointB);
            line.setPaint(paintPen);
            lineList.add(line);
            Operation operation = new Operation();
            operation.setType(flag);
            operationList.add(operation);
        }
        if (flag == 2) {
            setPen();
            Circle circle = new Circle();
            circle.setR(l);
            circle.setPoint(downPointB);
            circle.setPaint(paintPen);
            circleList.add(circle);
            Operation operation = new Operation();
            operation.setType(flag);
            operationList.add(operation);
        }
        if (flag == 3) {
            setPen();
            Rect rect = new Rect();
            PointF p1 = new PointF();
            PointF p2 = new PointF();
            if (downPointB.x < upPointB.x && downPointB.y < upPointB.y) {
                p1 = downPointB;
                p2 = upPointB;
            }
            if (downPointB.x < upPointB.x && downPointB.y > upPointB.y) {
                p1.x = downPointB.x;
                p1.y = upPointB.y;
                p2.x = upPointB.x;
                p2.y = downPointB.y;
            }
            if (downPointB.x > upPointB.x && downPointB.y < upPointB.y) {
                p1.x = upPointB.x;
                p1.y = downPointB.y;
                p2.x = downPointB.x;
                p2.y = upPointB.y;
            }
            if (downPointB.x > upPointB.x && downPointB.y > upPointB.y) {
                p1 = upPointB;
                p2 = downPointB;
            }
            rect.setP1(p1);
            rect.setP2(p2);
            rect.setPaint(paintPen);
//            PointF p = new PointF();
//            p.x = upPointB.x;
//            p.y = downPointB.y;
//            rect.setP3(p);
//            p = new PointF();
//            p.x = downPointB.x;
//            p.y = upPointB.y;
//            rect.setP4(p);
//            PointF pp3 = new PointF();
//            pp3.x = downPointB.x + (upPointB.x - downPointB.x) / 2;
//            pp3.y = downPointB.y + (upPointB.y - downPointB.y) / 2;
//            rect.setPmid(pp3);
//            rect.setL(Math.abs(downPointB.x - upPointB.x));
//            rect.setW(Math.abs(downPointB.y - upPointB.y));
            rect.setAngle(rectAngle);
            if(delChoose)rectListDel.add(rect);
            else {
                rectList.add(rect);
                Log.d("SD","p1="+rect.getP1()+"p2="+rect.getP2()+"p3="+rect.getP3()+"p4="+rect.getP4());
                Operation operation = new Operation();
                operation.setType(flag);
                operationList.add(operation);
            }
        }
        if (flag == 4) {
            setPen();
            Oval oval = new Oval();
            oval.setRectF(rectF);
            oval.setAngle(0);
            oval.setPaint(paintPen);
            ovalList.add(oval);
            Operation operation = new Operation();
            operation.setType(flag);
            operationList.add(operation);
        }
        if (flag == 5&&finsh) {
            setPen();
            BrokenLine brokenLine = new BrokenLine();
            brokenLine.setBList(pointFS);
            brokenLine.setPaint(paintPen);
            brokenLineList.add(brokenLine);
            pointFS=new ArrayList<>();
            initCanvas();//添上这句防止重复绘制
            draw1(mBitmapCanvas); // 保存到图片中
            invalidate();
        }
        if (flag == 6&&finsh) {
            setPen();
            Polygon polygon = new Polygon();
            polygon.setPointFList(pointFS);
            polygon.setPaint(paintPen);
            polygonList.add(polygon);
            pointFS=new ArrayList<>();
            initCanvas();//添上这句防止重复绘制
            draw1(mBitmapCanvas); // 保存到图片中
            invalidate();
        }
    }
    //缩放
    public void setTransScale(float scale, float dx, float dy) {
        mScale = scale;
        mTransX = dx;
        mTransY = dy;
        if (!mIsSaveArg) {
            invalidate();
        }
        mIsSaveArg = false;
    }
    /**
     * 画箭头
     */
    public void drawAL(PointF pf1, PointF pf2) {
        float sx = pf1.x;
        float sy = pf1.y;
        float ex = pf2.x;
        float ey = pf2.y;
        double H = 30; // 箭头高度
        double L = 10; // 底边的一半
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = ey - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        Paint pp = new Paint();
        pp.setColor(Color.RED);
        pp.setStrokeWidth(3);
        pp.setAntiAlias(true);
        // 画线
        mCurrentCanvas.drawLine(sx, sy, ex, ey, pp);
        Path triangle = new Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        mCurrentCanvas.drawPath(triangle, paint);

    }
    // 计算
    public double[] rotateVec(float px, float py, double ang, boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }
    //长按选中
    public void longChoose(PointF p) {
        chooseLine = 0;
        chooseCircle = 0;
        chooseRect = 0;
        chooseOval = 0;
        choosePolygon=0;
        chooseBrokenLine=0;
        float x0 = p.x;
        float y0 = p.y;
        double min = 1000;
        int k = 0, l = 0;
        for (Line line : lineList) {
            k++;
            float x1 = line.getP1().x;
            float y1 = line.getP1().y;
            float x2 = line.getP2().x;
            float y2 = line.getP2().y;
            double d = pointToLine(x1, y1, x2, y2, x0, y0);
            if (d < min && d < 100) {
                l++;
                min = d;
                chooseLine = k;
                chooseOperation = l;
                lineTemp = line;
            }
        }
        k = 0;
        for (Circle circle : circleList) {
            k++;
            double d = lineSpace(x0, y0, circle.getPoint().x, circle.getPoint().y);
            d = d - circle.getR();
            if (d < 0) d = -d;
            if (d < min && d < 100) {
                l++;
                min = d;
                chooseCircle = k;
                chooseOperation = l;
                circleTemp = circle;
            }
        }
        k = 0;
        for (Rect rect : rectList) {
            k++;
            double d = pointToRect(rect, p);
            if (d < min && d < 100) {
                l++;
                min = d;
                chooseRect = k;

                rectTemp = rect;
            }
        }
        k = 0;
        for (Oval oval : ovalList) {
            k++;
            double d = lineSpace(x0, y0, oval.getPmid().x, oval.getPmid().y);
            if (d < min && d < 100) {
                l++;
                min = d;
                chooseOval = k;
                ovalTemp = oval;
            }
        }
        k = 0;
        for (Polygon polygon : polygonList) {
            k++;
            double d = lineSpace(x0, y0, polygon.getPm().x, polygon.getPm().y);
            if (d < min && d < 100) {
                l++;
                min = d;
                choosePolygon = k;
                polygonTemp = polygon;
            }
        }
        k = 0;
        for(BrokenLine brokenLine:brokenLineList){
            k++;
            double d;
            for(int i=1;i<brokenLine.getBList().size();i++){
                d=pointToLine(x0,y0,brokenLine.getBList().get(i-1).x,brokenLine.getBList().get(i-1).y,
                        brokenLine.getBList().get(i).x,brokenLine.getBList().get(i).y);
                if(d<min&&d<100){
                    l++;
                    min=d;
                    chooseBrokenLine=k;
                    brokenLineTemp=brokenLine;
                }
            }
        }
        if (chooseCircle > 0) {
            chooseLine = 0;
        }
        if (chooseRect > 0) {
            chooseCircle = 0;
            chooseLine=0;
        }
        if (chooseOval > 0) {
            chooseRect = 0;
            chooseLine=0;
            chooseCircle=0;
        }
        if(choosePolygon>0){
            chooseRect = 0;
            chooseLine=0;
            chooseCircle=0;
            chooseOval=0;
        }
    }
    //设置画笔
    private void setPen(){
        paintPen=new Paint();
        paintPen.setColor(colPicker);
        paintPen.setStyle(penStyle);
        paintPen.setStrokeWidth(sizePicker);
        paintPen.setAntiAlias(true);
    }
    // 点到直线的最短距离的判断 点（x0,y0） 到由两点组成的线段（x1,y1） ,( x2,y2 )
    private double pointToLine(float x1, float y1, float x2, float y2, float x0, float y0) {
        double space = 0;
        double a, b, c;
        a = lineSpace(x1, y1, x2, y2);// 线段的长度
        b = lineSpace(x1, y1, x0, y0);// (x1,y1)到点的距离
        c = lineSpace(x2, y2, x0, y0);// (x2,y2)到点的距离
        if (c <= 0.000001 || b <= 0.000001) {
            space = 0;
            return space;
        }
        if (a <= 0.000001) {
            space = b;
            return space;
        }
        if (c * c >= a * a + b * b) {
            space = b;
            return space;
        }
        if (b * b >= a * a + c * c) {
            space = c;
            return space;
        }
        double p = (a + b + c) / 2;// 半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
        space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
        return space;
    }

    // 计算两点之间的距离
    private double lineSpace(float x1, float y1, float x2, float y2) {
        double lineLength;
        lineLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
                * (y1 - y2));
        return lineLength;
    }

    //保存当前缩放的倍数
    public void saveCurrentScale() {
        mCenterScale = mCenterScale * mScale;
        mCenterLeft = (mCenterLeft + mTransX) / mCenterScale;
        mCenterTop = (mCenterTop + mTransY) / mCenterScale;
        mIsSaveArg = true;
        saveMultiplyBitmap();
    }

    //撤销
    public void cx() {
        if (operationList.size() > 0) {
            if (operationList.get(operationList.size() - 1).getType() == 1) {
                lineList.remove(lineList.size() - 1);
            }
            if (operationList.get(operationList.size() - 1).getType() == 2) {
                circleList.remove(circleList.size() - 1);
            }
            if (operationList.get(operationList.size() - 1).getType() == 3) {
                rectList.remove(rectList.size() - 1);
            }
            if (operationList.get(operationList.size() - 1).getType() == 4) {
                ovalList.remove(ovalList.size() - 1);
            }
            if (operationList.get(operationList.size() - 1).getType() == 6) {
                polygonList.remove(polygonList.size() - 1);
            }
            chooseLine = 0;
            chooseCircle = 0;
            chooseOval = 0;
            chooseRect = 0;
            choosePolygon = 0;
            chooseBrokenLine=0;
            operationList.remove(operationList.size() - 1);
            initCanvas();//添上这句防止重复绘制
            draw1(mBitmapCanvas); // 保存到图片中
            invalidate();
        }
    }

    //双指移动的时候,生成混合之后的图片
    private void saveMultiplyBitmap() {
        mIsMove = true;
        Canvas canvas = new Canvas(mMultiplyBitmap);
        canvas.drawBitmap(mSrcBitmap, 0, 0, mPdXfPaint);
        mPdXfPaint.setXfermode(mPdXfermode);
        // 绘制涂鸦图片
        canvas.drawBitmap(mGraffitiBitmap, 0, 0, mPdXfPaint);
        mPdXfPaint.setXfermode(null);
    }

    /**
     * 在画笔的状态下第一个触点按下的情况
     */
    private void penTouchDown(float x, float y) {
        mIsMove = false;
        // 为了仅点击时也能出现绘图，模拟滑动一个像素点
    }

    /**
     * 初始化当前画线的绘图
     */
    private void initCurrentCanvas() {
        mCurrentBitmap.setPixels(mWhiteBuffer, 0, mSrcBitmap.getWidth(), 0, 0, mSrcBitmap.getWidth(), mSrcBitmap.getHeight());
        mCurrentCanvas = new Canvas(mCurrentBitmap);
    }

    /**
     * 初始化涂鸦的绘图
     */
    private void initCanvas() {
        mGraffitiBitmap.setPixels(mWhiteBuffer, 0, mSrcBitmap.getWidth(), 0, 0, mSrcBitmap.getWidth(), mSrcBitmap.getHeight());
        mBitmapCanvas = new Canvas(mGraffitiBitmap);
    }
    /**
     * 创建一个图片,透明度为255(不透明), 底色为白色 ,目的是为了使用正片叠底
     *
     * @param sourceImg
     * @return
     */
    public Bitmap getTransparentBitmap(Bitmap sourceImg) {
        mWhiteBuffer = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        Arrays.fill(mWhiteBuffer, 0xFFFFFFFF);
        sourceImg = Bitmap.createBitmap(mWhiteBuffer, sourceImg.getWidth(), sourceImg.getHeight(), Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
        return sourceImg;
    }

    // 还原堆栈中的记录的操作
    private void draw1(Canvas canvas) {
        Random r=new Random(1);
        boolean flagP;
        float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        float x3 = 0, x4 = 0, y3 = 0, y4 = 0;
        for(int i=0;i<rectList.size();i++){
            x1 = rectList.get(i).getP1().x;
            y1 = rectList.get(i).getP1().y;
            x2 = rectList.get(i).getP2().x;
            y2 = rectList.get(i).getP2().y;
            for(int j=0;j<100000;j++){
                flagP=true;
                int x = r.nextInt(5472);
                int y = r.nextInt(3648);
                if(x>x1&&x<x2&&y>y1&&y<y2){
                    if(rectListDel.size()>0) {
                        for (int k = 0; k < rectListDel.size(); k++) {
                            x3 = rectListDel.get(k).getP1().x;
                            y3 = rectListDel.get(k).getP1().y;
                            x4 = rectListDel.get(k).getP2().x;
                            y4 = rectListDel.get(k).getP2().y;
                            if (x > x3 && x < x4 && y > y3 && y < y4) {
                                flagP=false;
                                break;
                            }
                        }
                        if (flagP) {
                            canvas.rotate((float)rectList.get(i).getAngle(), rectList.get(i).getPmid().x, rectList.get(i).getPmid().y);
                            canvas.drawPoint(x, y, paint);
                            canvas.rotate((float)-rectList.get(i).getAngle(), rectList.get(i).getPmid().x, rectList.get(i).getPmid().y);
                        }
                    }else{
                        canvas.rotate((float)rectList.get(i).getAngle(), rectList.get(i).getPmid().x, rectList.get(i).getPmid().y);
                        canvas.drawPoint(x, y, paint);
                        canvas.rotate((float)-rectList.get(i).getAngle(), rectList.get(i).getPmid().x, rectList.get(i).getPmid().y);

                    }
                      }
            }
        }

//        Random r=new Random(1);
//        boolean flagP;
//        float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
//        for(int i=0;i<100000;i++){
//            flagP=true;
//            int x = r.nextInt(5472);
//            int y = r.nextInt(3648);
//            if(rectListDel.size()>0) {
//                for (int j = 0; j < rectListDel.size(); j++) {
//                    x1 = rectListDel.get(j).getP1().x;
//                    y1 = rectListDel.get(j).getP1().y;
//                    x2 = rectListDel.get(j).getP2().x;
//                    y2 = rectListDel.get(j).getP2().y;
//                    if (x > x1 && x < x2 && y > y1 && y < y2) {
//                        flagP=false;
//                        break;
//                    }
//                }
//                if (flagP) {
//                    canvas.drawPoint(x, y, paint);
//                }
//            }else{
//                canvas.drawPoint(x, y, paint);
//            }
//
//        }
        for (Line line : lineList) {
            canvas.drawLine(line.getP1().x, line.getP1().y, line.getP2().x, line.getP2().y, line.getPaint());
        }
        for (Circle circle : circleList) {
            canvas.drawCircle(circle.getPoint().x, circle.getPoint().y, circle.getR(), circle.getPaint());
        }
        for (Rect rect : rectList) {
            canvas.rotate((float) rect.getAngle(), rect.getPmid().x, rect.getPmid().y);
            canvas.drawRect(rect.getP1().x, rect.getP1().y, rect.getP2().x, rect.getP2().y, rect.getPaint());
            canvas.rotate((float) -rect.getAngle(), rect.getPmid().x, rect.getPmid().y);
        }
        for (Oval oval : ovalList) {
            canvas.rotate((float) oval.getAngle(), oval.getPmid().x, oval.getPmid().y);
            canvas.drawOval(oval.getRectF(), oval.getPaint());
            canvas.rotate((float) -oval.getAngle(), oval.getPmid().x, oval.getPmid().y);
        }
        for (BrokenLine brokenLine : brokenLineList) {
            for (int i = 1; i < brokenLine.getBList().size(); i++) {
                canvas.drawLine(brokenLine.getBList().get(i - 1).x, brokenLine.getBList().get(i - 1).y, brokenLine.getBList().get(i).x, brokenLine.getBList().get(i).y, brokenLine.getPaint());
            }
        }
        for (Polygon polygon : polygonList) {
            if (polygon.getPointFList().size() > 2) {
                canvas.drawCircle(polygon.getPm().x, polygon.getPm().y, 10, paint);
            }
            for (int i = 1; i < polygon.getPointFList().size(); i++) {
                canvas.drawLine(polygon.getPointFList().get(i - 1).x, polygon.getPointFList().get(i - 1).y, polygon.getPointFList().get(i).x, polygon.getPointFList().get(i).y, mCurrentPaint);
            }
            if (polygon.getPointFList().size() > 2) {
                canvas.drawLine(polygon.getPointFList().get(0).x, polygon.getPointFList().get(0).y, polygon.getPointFList().get(polygon.getPointFList().size() - 1).x, polygon.getPointFList().get(polygon.getPointFList().size() - 1).y,polygon.getPaint());
            }
        }
    }

    /**
     * 将触摸的屏幕坐标转换成实际图片中的坐标
     */
    public float screenToBitmapX(float touchX) {
        return (touchX - mCenterLeft - mTransX) / (mCenterScale * mScale);
    }
    public float screenToBitmapY(float touchY) {
        return (touchY - mCenterTop - mTransY) / (mCenterScale * mScale);
    }
    //通过触点的坐标和实际图片中的坐标,得到当前图片的起始点坐标
    public final float toTransX(float touchX, float graffitiX) {
        return -graffitiX * (mCenterScale * mScale) + touchX - mCenterLeft;
    }
    public final float toTransY(float touchY, float graffitiY) {
        return -graffitiY * (mCenterScale * mScale) + touchY - mCenterTop;
    }
    //点到矩形的距离
    public double pointToRect(Rect rect, PointF p) {
        double dmin;
        double d1 = pointToLine(rect.getP1().x, rect.getP1().y, rect.getP3().x, rect.getP3().y, p.x, p.y);
        double d2 = pointToLine(rect.getP1().x, rect.getP1().y, rect.getP4().x, rect.getP4().y, p.x, p.y);
        double d3 = pointToLine(rect.getP2().x, rect.getP2().y, rect.getP3().x, rect.getP3().y, p.x, p.y);
        double d4 = pointToLine(rect.getP2().x, rect.getP2().y, rect.getP4().x, rect.getP4().y, p.x, p.y);
        dmin = d1;
        if (d2 < dmin) {
            dmin = d2;
        }
        if (d3 < dmin) {
            dmin = d3;
        }
        if (d4 < dmin) {
            dmin = d4;
        }
        return dmin;
    }
    private PointF getYP(PointF p1, PointF p2, float d) {
        d = (float) lineSpace(p1.x, p1.y, p2.x, p2.y) + d;
        float k = angle(p1, p2);
        k = new Float(Math.toRadians(k));
        float x2 = (float) (d * Math.cos(k)) + p1.x;
        float y2 = (float) (d * Math.sin(k) + p1.y);
        PointF p3 = new PointF();
        p3.x = x2;
        p3.y = y2;
        return p3;
    }
    // 两点与X轴的夹角(坐标系为X轴右为正，Y轴向下为正)
    private float angle(PointF a, PointF b) {
        float dx = b.x - a.x;
        float dy = b.y - a.y;
        float dis = (float) Math.sqrt(dx * dx + dy * dy);
        float rota = dis > 0 ? Math.round(Math.asin(dy / dis) / Math.PI * 180) : 0;
        // rota范围（-90，90）当b点在a点左边时，必要另处理
        if (b.x < a.x) {
            rota = 180 - rota;
        }
        return rota;
    }
    public interface ChooseListener {
        void onChoose(Line line);
        void onPoint(PointF pointF);
    }
    //判断矩形与线段是否相交
    private static boolean isLineIntersectRectangle(float linePointX1, float linePointY1, float linePointX2, float linePointY2,
                                                    float rectangleLeftTopX, float rectangleLeftTopY, float rectangleRightBottomX, float rectangleRightBottomY) {
        double lineHeight = linePointY1 - linePointY2;
        double lineWidth = linePointX2 - linePointX1;
        double t1 = lineHeight * rectangleLeftTopX + lineWidth * rectangleLeftTopY;
        double t2 = lineHeight * rectangleRightBottomX + lineWidth * rectangleRightBottomY;
        double t3 = lineHeight * rectangleLeftTopX + lineWidth * rectangleRightBottomY;
        double t4 = lineHeight * rectangleRightBottomX + lineWidth * rectangleLeftTopY;
        double c = linePointX1 * linePointY2 - linePointX2 * linePointY1;
        if ((t1 + c >= 0 && t2 + c <= 0)
                || (t1 + c <= 0 && t2 + c >= 0)
                || (t3 + c >= 0 && t4 + c <= 0)
                || (t3 + c <= 0 && t4 + c >= 0)) {
            if (rectangleLeftTopX > rectangleRightBottomX) {
                double temp = rectangleLeftTopX;
                rectangleLeftTopX = rectangleRightBottomX;
                rectangleRightBottomX = (float) temp;
            }

            if (rectangleLeftTopY < rectangleRightBottomY) {
                double temp1 = rectangleLeftTopY;
                rectangleLeftTopY = rectangleRightBottomY;
                rectangleRightBottomY = (float) temp1;
            }

            if ((linePointX1 < rectangleLeftTopX && linePointX2 < rectangleLeftTopX)
                    || (linePointX1 > rectangleRightBottomX && linePointX2 > rectangleRightBottomX)
                    || (linePointY1 > rectangleLeftTopY && linePointY2 > rectangleLeftTopY)
                    || (linePointY1 < rectangleRightBottomY && linePointY2 < rectangleRightBottomY)) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    public void reDraw() {
        lineList.clear();
        circleList.clear();
        ovalList.clear();
        rectList.clear();
        polygonList.clear();
        brokenLineList.clear();
        chooseCircle=0;
        chooseLine=0;
        chooseOval=0;
        chooseRect=0;
        choosePolygon=0;
        initCanvas();//添上这句防止重复绘制
        invalidate();
    }
    //移除选中操作图形重画
    public void reMoveR(List<?> list, Object ob) {
        if (ff) {
            ff = false;
            list.remove(ob);
            initCanvas();//添上这句防止重复绘制
            draw1(mBitmapCanvas); // 保存到图片中
            invalidate();
        }
    }
    //三点的夹角
    public static double calcAngle(PointF cen, PointF first, PointF second) {
        final double M_PI = 3.1415926535897;

        double ma_x = first.x - cen.x;
        double ma_y = first.y - cen.y;
        double mb_x = second.x - cen.x;
        double mb_y = second.y - cen.y;
        double v1 = (ma_x * mb_x) + (ma_y * mb_y);
        double ma_val = Math.sqrt(ma_x * ma_x + ma_y * ma_y);
        double mb_val = Math.sqrt(mb_x * mb_x + mb_y * mb_y);
        double cosM = v1 / (ma_val * mb_val);
        double angleAMB = Math.acos(cosM) * 180 / M_PI;


        float v = (cen.x - first.x) * (second.y - cen.y) - (cen.y - first.y) * (second.x - cen.x);
        if (v > 0) {
//          System.out.println("逆时针");
            return -angleAMB;
        } else if (v < 0) {
//          System.out.println("顺时针");
        } else {
//          System.out.println("不变");
        }
        return angleAMB;
    }
    //删除操作
    public void delShape(){
        if(chooseLine>0){
            chooseLine=0;
            reMoveR(lineList,lineTemp);
            lineTemp=new Line();
        }
        if(chooseCircle>0){
            chooseCircle=0;
            reMoveR(circleList,circleTemp);
            circleTemp=new Circle();
        }
        if(chooseOval>0){
            chooseOval=0;
            reMoveR(ovalList,ovalTemp);
            ovalTemp=new Oval();
        }
        if(chooseRect>0){
            chooseRect=0;
            reMoveR(rectList,rectTemp);
            rectTemp=new Rect();
        }
        if(choosePolygon>0){
            choosePolygon=0;
            reMoveR(polygonList,polygonTemp);
            polygonTemp=new Polygon();
        }
    }
    public void saveToFile(){
        StringBuilder sb=new StringBuilder();
        for (Line line:lineList) {
            sb.append("line:"+line.getP1()+","+line.getP2()+";");
        }
        for(Circle circle:circleList){
            sb.append("circle:"+circle.getPoint()+","+circle.getR()+";");
        }
        for(Oval oval:ovalList){
            sb.append("oval:"+oval.getTmp()+","+oval.getBmp()+","+oval.getLmp()+","+oval.getRmp()+","+oval.getAngle()+";");
        }
        for(Rect rect:rectList){
            sb.append("rect:"+rect.getLp1()+","+rect.getLp2()+","+rect.getWp1()+","+rect.getWp2()+","+rect.getAngle()+";");
        }
        for(BrokenLine brokenLine:brokenLineList){
            for(PointF pointF:brokenLine.getBList()){
                sb.append(pointF+",");
            }
        }
        for(BrokenLine brokenLine:brokenLineList) {
            for (PointF pointF : brokenLine.getBList()) {
                sb.append(pointF + ",");
            }
        }
        FileUtil fileUtil=new FileUtil();
        fileUtil.saveFile(getContext(),sb.toString(),"PointData.txt");
    }
    //线段与x=a或y=a的交点
    public PointF getLineCross(PointF p1,PointF p2,float a,boolean yp){
        float A=p2.y-p1.y;
        float B=p1.x-p2.x;
        float C=p2.x*p1.y-p1.x*p2.y;
        PointF pointF=new PointF();
        if(yp){
            pointF.x=(-C-B*a)/A;
            pointF.y=a;
            if(pointF.x<p2.x&&pointF.x>p1.x){
                return pointF;
            }else{
                return null;
            }
        }else{
            pointF.x=a;
            pointF.y=(-C-A*a)/B;
            if(pointF.y<p2.y&&pointF.y>p1.y){
                return pointF;
            }else{
                return null;
            }

        }

    }
    //两线段的交点
    public  static PointF getCrossPoint(PointF p1,PointF p2,PointF p3,PointF p4){
        float x;
        float y;
        float x1=p1.x;
        float y1=p1.y;
        float x2=p2.x;
        float y2=p2.y;
        float x3=p3.x;
        float y3=p3.y;
        float x4=p4.x;
        float y4=p4.y;
        float k1=Float.MAX_VALUE;
        float k2=Float.MAX_VALUE;
        boolean flag1=false;
        boolean flag2=false;

        if((x1-x2)==0)
            flag1=true;
        if((x3-x4)==0)
            flag2=true;

        if(!flag1)
            k1=(y1-y2)/(x1-x2);
        if(!flag2)
            k2=(y3-y4)/(x3-x4);

        if(k1==k2)
            return null;

        if(flag1){
            if(flag2)
                return null;
            x=x1;
            if(k2==0){
                y=y3;
            }else{
                y=k2*(x-x4)+y4;
            }
        }else if(flag2){
            x=x3;
            if(k1==0){
                y=y1;
            }else{
                y=k1*(x-x2)+y2;
            }
        }else{
            if(k1==0){
                y=y1;
                x=(y-y4)/k2+x4;
            }else if(k2==0){
                y=y3;
                x=(y-y2)/k1+x2;
            }else{
                x=(k1*x2-k2*x4+y4-y2)/(k1-k2);
                y=k1*(x-x2)+y2;
            }
        }
        if(between(x1,x2,x)&&between(y1,y2,y)&&between(x3,x4,x)&&between(y3,y4,y)){
            PointF point=new PointF();
            point.x=x;
            point.y=y;
            if(point.equals(p1)||point.equals(p2))
                return null;
            return point;
        }else{
            return null;
        }
    }

    public static boolean between(float a,float b,float target){
        if(target>=a-0.01&&target<=b+0.01||target<=a+0.01&&target>=b-0.01)
            return true;
        else
            return false;
    }
}

