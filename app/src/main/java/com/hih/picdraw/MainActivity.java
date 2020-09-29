package com.hih.picdraw;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hih.picdraw.model.Line;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements MyView4.ChooseListener {

    @BindView(R.id.typePick)
    ImageView typePick;
    @BindView(R.id.delPick)
    ImageView delPick;
    @BindView(R.id.bili)
    Button bili;
    @BindView(R.id.redraw)
    Button redraw;
    @BindView(R.id.xzb)
    TextView xzb;
    @BindView(R.id.yzb)
    TextView yzb;
    @BindView(R.id.color)
    TextView colorT;
    @BindView(R.id.recover)
    Button recover;
    @BindView(R.id.act_main_mainlayout)
    FrameLayout actMainMainlayout;
    @BindView(R.id.drawer_layout)
    RelativeLayout drawerLayout;
    @BindView(R.id.pen_size)
    TextView penSize;
    @BindView(R.id.decrease)
    TextView decrease;
    @BindView(R.id.add)
    TextView add;
    @BindView(R.id.pen_type)
    Spinner penType;
    @BindView(R.id.pic_type)
    Spinner picType;
    @BindView(R.id.cx)
    Button cx;
    private int select_paint_type_index = 0;
    private Bitmap mSrcBitmap;
    private FrameLayout mFrameLayout;
    private MyView4 mDoubleMoveView;
    private int mTouchMode;
    private PointF mTouchCenterPt;//两指中点坐标
    private float mOldDist, mNewDist;
    private float mToucheCentreXOnGraffiti, mToucheCentreYOnGraffiti;
    private boolean flag = false;
    private int size = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
            initView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
    }


    /**
     * 选择颜色的dialog
     */
    public void showPaintColorDialog() {
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.show(getSupportFragmentManager(), "dialog_color");
        colorPickerDialog.setOnColorChangeListenter(new ColorPickerDialog.OnColorListener() {
            @Override
            public void onEnsure(int color) {
                if (color == 0) {
                    mDoubleMoveView.colPicker = Color.BLACK;
                } else {
                    mDoubleMoveView.colPicker = color;
                    colorT.setBackgroundColor(color);
                }
            }

            @Override
            public void onBack() {
            }
        });
        colorPickerDialog.setCancelable(false);
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initView() throws FileNotFoundException {
        String[] strings = {"轮廓", "填充"};
        spinnerModel(strings, penType);
        String[] strings1 = {"选中区域", "剔除区域"};
        spinnerModel(strings1, picType);
        mFrameLayout = findViewById(R.id.act_main_mainlayout);
        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        options.inTargetDensity = value.density;
        options.inScaled = false;
        penSize.setText(size + "");
        mSrcBitmap = getLoacalBitmap("/sdcard/test.png"); //从本地取图片(在cdcard中获取)  //
        Log.d("ss", "mw=" + mSrcBitmap.getWidth() + "," + mSrcBitmap.getHeight());
        mDoubleMoveView = new MyView4(MainActivity.this, mSrcBitmap);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mFrameLayout.addView(mDoubleMoveView, params);
        mDoubleMoveView.setChooseListenr(this);
        mDoubleMoveView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchMode = 1;
                        if (mDoubleMoveView.flag == -1) {
                            mToucheCentreXOnGraffiti = mDoubleMoveView.screenToBitmapX(event.getX());
                            mToucheCentreYOnGraffiti = mDoubleMoveView.screenToBitmapY(event.getY());
                            mDoubleMoveView.saveCurrentScale();
                        }
                        return false;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mTouchMode = 0;
                        flag = false;
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        if (mTouchMode == 1 && !flag) {
                            if (mDoubleMoveView.lineList.size() > 0)
                                if (mDoubleMoveView.flag == -1) {
                                    float transX = mDoubleMoveView.toTransX(event.getX(), mToucheCentreXOnGraffiti);
                                    float transY = mDoubleMoveView.toTransY(event.getY(), mToucheCentreYOnGraffiti);
                                    mDoubleMoveView.setTransScale(1, transX, transY);
                                }
                            return false;
                        }
                        if (mTouchMode == 2) {
                            PointF ptf = getMid(event);
                            mNewDist = spacing(event);// 两点按下时的距离
                            float sc = mNewDist / mOldDist;
                            float transX = mDoubleMoveView.toTransX(ptf.x, mToucheCentreXOnGraffiti);
                            float transY = mDoubleMoveView.toTransY(ptf.y, mToucheCentreYOnGraffiti);
                            mDoubleMoveView.setTransScale(sc, transX, transY);
                            // mDoubleMoveView.setScale(sc,ptf);
                        }
                        return true;
                    case MotionEvent.ACTION_POINTER_UP:
                        mTouchMode -= 1;
                        if (mTouchMode == 1) {
                            //mDoubleMoveView.PointertUp();
                        }
                        return true;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mTouchMode += 1;
                        if (mTouchMode == 2) {
                            flag = true;
                            mTouchCenterPt = getMid(event);
                            mOldDist = spacing(event);// 两点按下时的距离
                            mToucheCentreXOnGraffiti = mDoubleMoveView.screenToBitmapX(mTouchCenterPt.x);
                            mToucheCentreYOnGraffiti = mDoubleMoveView.screenToBitmapY(mTouchCenterPt.y);
                            mDoubleMoveView.saveCurrentScale();
                        }

                        return true;
                }
                return false;
            }
        });
    }

    public void receive() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket;
                try {
                    //这里进行连接服务器，
                    socket = new Socket("192.168.3.240", 7000);
                    //获取输入流
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    // DataOutputStream outputStream=new DataOutputStream(socket.getOutputStream());
                    // sendImgMsg(outputStream);
                    while (true) {
                        getImgMsg(input);
                        //读取长度，也即是消息头，
                        // long len = input.readLong();
                        //创建这个长度的字节数组
                        //byte[] bytes = new byte[(int)len];
                        //再读取这个长度的字节数，也就是真正的消息体
                        // input.read(bytes);
                        //将字节数组转为String
                        //  String s = new String(bytes);
                        //  Log.i("accept", "len: "+len);
                        //  Log.i("accept", "msg: "+s);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    break;
                default:
                    break;
            }
        }
    };
    Bitmap bmp;

    public void getImgMsg(DataInputStream input) throws IOException {
        int size = input.readInt();
        Log.d("SS", "len=" + size);
        byte[] data = new byte[1024];
        int len = 0;

        // while (len < size) {
        //    len += input.read(data, len, size - len);
        //  }
        while (input.read(data) != -1) {
            //  ByteArrayOutputStream outPut = new ByteArrayOutputStream();
            //   bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            //  bmp.compress(Bitmap.CompressFormat.PNG, 100, outPut);
            // Message message=new Message();
            //  message.what=1;
            //  handler.sendMessage(message);
        }
        Log.d("SS", "len1=" + data.length);

//        img.setImageBitmap(bmp);
        //然后在读这个长度的字节到字节数组
        // input.readFully(bytes);
        //将独到的内容保存为文件到本地
        // File file = new File(len + ".png");
        // FileOutputStream fileOutputStream = new FileOutputStream(file);
        // fileOutputStream.write(bytes);
        // getPicFromBytes(bytes)
        // System.out.println("ok");
    }

    public void sendImgMsg(DataOutputStream out) throws IOException {
//发送的图片为图标，将bitmap转为字节数组
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.demobg);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bout);
//写入字节的长度，再写入图片的字节
        long len = bout.size();
//这里打印一下发送的长度
        Log.i("sendImgMsg", "len: " + len);
        out.writeLong(len);
        out.write(bout.toByteArray());
    }


    /**
     * 计算两指间的距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /*取两指的中心点坐标*/
    private PointF getMid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    //下拉选择框
    private void spinnerModel(String[] strings, Spinner spinner) {
        //将可选内容与ArrayAdapter连接起来
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, strings);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long id) {
                if (strings[position].equals("轮廓"))
                    mDoubleMoveView.penStyle = Paint.Style.STROKE;
                else if (strings[position].equals("填充"))
                    mDoubleMoveView.penStyle = Paint.Style.FILL;
                else if (strings[position].equals("选中区域")) {
                    mDoubleMoveView.delChoose = false;
                } else if (strings[position].equals("剔除区域")) {
                    mDoubleMoveView.delChoose = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        //设置默认值
        spinner.setVisibility(View.VISIBLE);
    }

    //弹出画笔类型选项对话框
    private void showPaintTypeDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.custom_dialog);
        alertDialogBuilder.setTitle("选择类型：");
        alertDialogBuilder.setSingleChoiceItems(R.array.painttype, select_paint_type_index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_paint_type_index = which;
                mDoubleMoveView.flag = which + 1;
                if (which == 5 || which == 4) {
                    mDoubleMoveView.finsh = false;
                }
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.create().show();
    }
    //设置直线的
//    private void setDialog(final Line line) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_set, null, false);
//        builder.setView(view);
//        final Dialog dialog = builder.create();
//        final Button qd=view.findViewById(R.id.qd);
//        final Button qx=view.findViewById(R.id.qx);
//        final EditText x1=view.findViewById(R.id.x1);
//        final EditText x2=view.findViewById(R.id.x2);
//        final EditText y1=view.findViewById(R.id.y1);
//        final EditText y2=view.findViewById(R.id.y2);
//        final EditText d=view.findViewById(R.id.d);
//        DecimalFormat df   = new DecimalFormat("######0.00");
//        x1.setText(""+df.format(line.getP1().x));
//        x2.setText(""+df.format(line.getP2().x));
//        y1.setText(""+df.format(line.getP1().y));
//        y2.setText(""+df.format(line.getP2().y));
//        d.setText(""+df.format(line.getD()));
//        qd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                double l=Double.valueOf(d.getText().toString());
//                Log.d("dd","l="+l);
//                double x=line.getP1().x+(l/Math.sqrt(1+line.getAngle()*line.getAngle()));
//                double y=line.getP1().y-line.getAngle()*(line.getP1().x-x);
//                PointF pointF=new PointF();
//                pointF.x= (float) x;
//                pointF.y= (float) y;
//                line.setP2(pointF);
////                mDoubleMoveView.line=line;
//                Log.d("dd","d="+line.getD());
//                hideKeyboard(view);
//                dialog.dismiss();
//            }
//        });
//        qx.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//    }

    /**
     * 隐藏键盘
     */
    protected void hideKeyboard(View v) {
        ((InputMethodManager) MainActivity.this.getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onChoose(Line line) {

    }

    @Override
    public void onPoint(PointF pointF) {
        xzb.setText(pointF.x + "");
        yzb.setText(pointF.y + "");
    }

    @OnClick({R.id.typePick, R.id.delPick, R.id.bili, R.id.redraw, R.id.color, R.id.decrease, R.id.add, R.id.recover,R.id.cx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.typePick:
                if (select_paint_type_index == 5 || select_paint_type_index == 4) {
                    mDoubleMoveView.finsh = true;
                    mDoubleMoveView.saveToArr();
                    mDoubleMoveView.flag = 0;
                    select_paint_type_index = 0;
                    mDoubleMoveView.pointFS = new ArrayList<>();
                } else {
                    showPaintTypeDialog();
                }
                break;
            case R.id.delPick:
                mDoubleMoveView.delShape();
                break;
            case R.id.bili:
                mDoubleMoveView.flag = 100;
                mDoubleMoveView.setTransScale(1, 0, 0);
                break;
            case R.id.redraw:
                mDoubleMoveView.reDraw();
                break;
            case R.id.color:
                showPaintColorDialog();
                break;
            case R.id.decrease:
                size--;
                penSize.setText(size + "");
                mDoubleMoveView.sizePicker = size;
                break;
            case R.id.add:
                size++;
                penSize.setText(size + "");
                mDoubleMoveView.sizePicker = size;
                break;
            case R.id.recover:
                mDoubleMoveView.flag = 7;
                mDoubleMoveView.setTransScale((mDoubleMoveView.firstS / mDoubleMoveView.mCenterScale), -mDoubleMoveView.mTransX, -mDoubleMoveView.mTransY);
                break;
            case R.id.cx:
                mDoubleMoveView.cx();
                break;
        }
    }

}
