package com.haha.pkprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.math.BigInteger;


/**
 * @author zhe.chen
 * @date 2020/7/9 14:30
 * Des:PK进度条,可设置最小进度,中间进度icon
 */
public class PKProgressView extends View {

    private Paint mPaintLeft = new Paint(Paint.ANTI_ALIAS_FLAG);//画左侧矩形
    private Paint mPaintRight = new Paint(Paint.ANTI_ALIAS_FLAG);//画右侧矩形
    private Paint mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);//画字体
    private Paint drawablePaint = new Paint();

    private int mTextSize = 10;
    private int mLeftColor = Color.parseColor("#E83D4D");//左侧矩形颜色
    private int mRightColor = Color.parseColor("#7859FF");//右侧矩形颜色

    private float mLeftWidthRatio = 0f;//左侧矩形的宽度
    private float mMinWidthRatio = 0.02f;//左边最小宽度比值

    private float width = dpTpPx(300), height = dpTpPx(15);//给控件一个默认宽度
    private float mTextHeight, mTextWidth;
    private BigInteger mLeftNum = BigInteger.valueOf(0), mRightNum = BigInteger.valueOf(0);//左右两侧数量
    private String mLeftText = "我方0", mRightText = "0对方";
    private Bitmap mIndicatorBitmap;
    private int mIndicatorResId;
    private String TAG = "PKProgressView";


    public PKProgressView(Context context) {
        super(context);
        init(null);//初始化
    }

    public PKProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PKProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PKProgressView);
            if (typedArray.hasValue(R.styleable.PKProgressView_indicatorIcon)) {
                mIndicatorResId = typedArray.getResourceId(R.styleable.PKProgressView_indicatorIcon, 0);
            }
            if (typedArray.hasValue(R.styleable.PKProgressView_minWidthRatio)) {
                mMinWidthRatio = typedArray.getFloat(R.styleable.PKProgressView_minWidthRatio, 0f);
            }
            if (typedArray.hasValue(R.styleable.PKProgressView_showTextSize)) {
                mTextSize = typedArray.getInteger(R.styleable.PKProgressView_showTextSize, 10);
            }
        }
        mPaintLeft.setColor(mLeftColor);
        mPaintRight.setColor(mRightColor);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(sp2px(mTextSize));
        drawablePaint.setDither(true);
        drawablePaint.setFilterBitmap(true);
    }

    public void addLeft(int leftNum) {
        addLeft(BigInteger.valueOf(leftNum));
    }

    /**
     * 左边添加
     *
     * @param leftNum
     */
    public void addLeft(BigInteger leftNum) {
        BigInteger mTempLeftNum = mLeftNum.add(leftNum);
        setLeftNum(mTempLeftNum);
    }

    public void addRight(int rightNum) {
        addRight(BigInteger.valueOf(rightNum));
    }

    /**
     * 右边添加
     *
     * @param rightNum
     */
    public void addRight(BigInteger rightNum) {
        BigInteger mTempRightNum = mRightNum.add(rightNum);
        setRightNum(mTempRightNum);
    }

    public void subtractLeft(int leftNum) {
        subtractLeft(BigInteger.valueOf(leftNum));
    }

    /**
     * 左边减去
     */
    public void subtractLeft(BigInteger leftNum) {
        if (leftNum.compareTo(mLeftNum) == 1) return;//减去的值大于当前值则return
        BigInteger mTempLeft = mLeftNum.subtract(leftNum);
        setLeftNum(mTempLeft);
    }

    public void subtractRight(int rightNum) {
        subtractRight(BigInteger.valueOf(rightNum));
    }

    /**
     * 右边减去
     */
    public void subtractRight(BigInteger rightNum) {
        if (rightNum.compareTo(mRightNum) == 1) return;
        BigInteger mTempRight = mRightNum.subtract(rightNum);
        setRightNum(mTempRight);
    }


    /**
     * 设置左边进度
     *
     * @param leftNum
     */
    public void setLeftNum(BigInteger leftNum) {
        mLeftNum = leftNum;
        Log.i(TAG, "我方--->" + mLeftNum);
        mLeftText = "我方" + mLeftNum;
        invalidate();
    }

    /**
     * 设置右边边进度
     *
     * @param rightNum
     */
    public void setRightNum(BigInteger rightNum) {
        mRightNum = rightNum;
        Log.i(TAG, "对方--->" + mRightNum);
        mRightText = mRightNum + "对方";
        invalidate();
    }

    /**
     * 设置左边最小宽度值比值
     *
     * @param minWithPercentage
     */
    public void setMinWithRatio(float minWithPercentage) {
        mMinWidthRatio = minWithPercentage;
    }

    /**
     * 设置文字大小
     *
     * @param size
     */
    public void setTextSize(int size) {
        mPaintText.setTextSize(sp2px(size));
    }

    /**
     * 设置PK指示器（中间进度Icon）
     *
     * @param resId
     */
    public void setIndicatorIcon(int resId) {
        mIndicatorResId = resId;
    }

    public BigInteger getLeftNum() {
        return mLeftNum;
    }

    public BigInteger getRightNum() {
        return mRightNum;
    }

    public BigInteger getTotalNum() {
        return mLeftNum.add(mRightNum);
    }

    /**
     * 重置状态
     */
    public void reset() {
        mLeftNum = BigInteger.valueOf(0);
        mRightNum = BigInteger.valueOf(0);
        mLeftText = "我方" + mLeftNum;
        mRightText = mRightNum + "对方";
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect rect = new Rect();
        mPaintText.getTextBounds(mRightText, 0, mRightText.length(), rect);//用一个矩形去"套"字符串,获得能完全套住字符串的最小矩形
        mTextWidth = rect.width();//字符串的宽度
        mTextHeight = rect.height();//字符串的高度

        //获取文字偏移量
        Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
        mPaintText.getFontMetrics(fontMetrics);
        float textOffsetY = (fontMetrics.descent + fontMetrics.ascent) / 2;

        if (mLeftNum.equals(BigInteger.valueOf(0)) && mRightNum.equals(BigInteger.valueOf(0))) {
            mLeftWidthRatio = 0.5f;
            canvas.drawRect(mLeftWidthRatio * width, 0, width, height, mPaintRight);
            canvas.drawRect(0, 0, mLeftWidthRatio * width, height, mPaintLeft);
        } else if (mLeftNum.equals(BigInteger.valueOf(0))) {
            mLeftWidthRatio = mMinWidthRatio;
            canvas.drawRect(mLeftWidthRatio * width, 0, width, height, mPaintRight);
            canvas.drawRect(0, 0, mLeftWidthRatio * width, height, mPaintLeft);
        } else if (mRightNum.equals(BigInteger.valueOf(0))) {
            mLeftWidthRatio = 1 - mMinWidthRatio;
            canvas.drawRect(mLeftWidthRatio * width, 0, width, height, mPaintRight);
            canvas.drawRect(0, 0, mLeftWidthRatio * width, height, mPaintLeft);
        } else {
            mLeftWidthRatio = mLeftNum.floatValue() / (mRightNum.floatValue() + mLeftNum.floatValue());
            checkMinWidth();
            if (mRightNum.compareTo(mLeftNum) == 1) {
                //如果 mRightNum 大于 mLeftNum
                canvas.drawRect(0, 0, mLeftWidthRatio * width, height, mPaintLeft);
                canvas.drawRect(mLeftWidthRatio * width, 0, width, height, mPaintRight);
            } else {
                canvas.drawRect(mLeftWidthRatio * width, 0, width, height, mPaintRight);
                canvas.drawRect(0, 0, mLeftWidthRatio * width, height, mPaintLeft);
            }
        }
        //绘制中间的指示器
        if (getIndicationBitmap() != null) {
            if (getIndicationBitmap().getHeight() < height) {
                float scaleY = height / getIndicationBitmap().getHeight();
                Matrix matrix = new Matrix();
                //放大图片并平移
                matrix.setScale(1f, scaleY);
                matrix.postTranslate((mLeftWidthRatio * width) - (getIndicationBitmap().getWidth() / 2), 0);

                canvas.drawBitmap(getIndicationBitmap(), matrix, drawablePaint);
            } else {
                canvas.drawBitmap(getIndicationBitmap(), (mLeftWidthRatio * width) - (getIndicationBitmap().getWidth() / 2), (height / 2) / 2, drawablePaint);
            }
        }
        //绘制左侧文字
        canvas.drawText(mLeftText, dpTpPx(5), height / 2 - textOffsetY, mPaintText);
        //绘制右侧文字
        canvas.drawText(mRightText, width - mTextWidth - dpTpPx(5), height / 2 - textOffsetY, mPaintText);
    }

    /**
     * 检查宽度,如果低于最小值则让其等于最小值
     */
    private void checkMinWidth() {
        if (mLeftWidthRatio < mMinWidthRatio) {
            mLeftWidthRatio = mMinWidthRatio;
        }
    }

    private Bitmap getIndicationBitmap() {
        if (mIndicatorBitmap != null) return mIndicatorBitmap;
        if (mIndicatorResId == 0) return null;
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), mIndicatorResId);
        if (bitmap == null) {
            Log.e(TAG, "获取到的图片为空");
            return null;
        }
        Bitmap bitmapCopy = Bitmap.createBitmap(bitmap);
        if (bitmapCopy != null) {
            mIndicatorBitmap = bitmapCopy;
        }
        return mIndicatorBitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    /**
     * 测量高
     *
     * @param measureSpec
     * @return
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        height = result;
        return result;
    }

    /**
     * 测量宽度
     *
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        width = result;
        return result;
    }

    //将dp转换为px
    private float dpTpPx(float value) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (float) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
    }

    // 将sp值转换为px值
    private int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


}
