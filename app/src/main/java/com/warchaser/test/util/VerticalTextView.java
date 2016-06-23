package com.warchaser.test.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class VerticalTextView extends TextView {

    public static final int LAYOUT_CHANGED = 1;
    private Paint paint;
    private int mTextPosx = 0;// x坐标
    private int mTextPosy = 0;// y坐标
    private int mTextWidth = 0;// 绘制宽度
    private int mTextHeight = 0;// 绘制高度
    private int mFontHeight = 0;// 绘制字体高度
    private float mFontSize = 24;// 字体大小
    private int mRealLine = 0;// 字符串真实的行数
    private int mLineWidth = 0;//列宽度
    private int TextLength = 0;//字符串长度
    private int oldwidth = 0;//存储久的width

    private int mTextColor = 0;
    private int mDefaultTextColor = Color.BLACK;

    private int mMaxLength = 20;
    private int mDefaultMaxLength = 100;

    private String mText = "";//待显示的文字
    private Handler mHandler = null;
    private Matrix matrix;
    private Paint.Align textStartAlign = Paint.Align.RIGHT;//draw start left or right.//default right
    BitmapDrawable drawable = (BitmapDrawable) getBackground();

    public VerticalTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public VerticalTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        String nameSpace = "http://schemas.android.com/apk/res/android";

        mTextColor = attrs.getAttributeIntValue(nameSpace, "textColor", mDefaultTextColor);
//        mFontSize = attrs.getAttributeIntValue(nameSpace, "textSize", 24);
//        mMaxHeight = attrs.getAttributeValue(nameSpace, "maxHeight");

        mMaxLength =  attrs.getAttributeIntValue(nameSpace, "maxLength", mDefaultMaxLength);

        mFontSize = getTextSize();

        matrix = new Matrix();
        paint = new Paint();//新建画笔
        paint.setTextAlign(Paint.Align.CENTER);//文字居中
        paint.setAntiAlias(true);//平滑处理
        paint.setColor(mTextColor);//默认文字颜色

        this.TextLength = mText.length();

    }

    /*
    //获取整数值
    private final int getAttributeIntValue(AttributeSet attrs,String field) {
    	int intVal = 0;
    	//TODO
    	//应该可以直接用attrs.getAttributeIntValue()获取对应的数值的，
    	//但不知道为什么一直无法获得只好临时写个函数凑合着用,没有写完整，暂时只支持px作为单位，其它单位的转换有空再写
    	String tempText=attrs.getAttributeValue(androidns, field);
    	intVal = (int)Math.ceil(Float.parseFloat(tempText.replaceAll("px","")));
		return intVal;
    }*/
    //设置文字
    public final void setText(String text)
    {
        this.mText = text;
        this.TextLength = text.length();
//        if (mTextHeight > 0) GetTextInfo();
    }

    public void setMaxLength(int maxLength)
    {
        this.mMaxLength = maxLength;
        invalidate();
    }

    //设置字体大小
    public final void setTextSize(float size)
    {
        if (size != paint.getTextSize())
        {
            mFontSize = size;
//            if (mTextHeight > 0) GetTextInfo();
        }
    }

    //设置字体颜色
    public final void setTextColor(int color) {
        paint.setColor(color);
    }

    //设置字体颜色
    public final void setTextARGB(int a, int r, int g, int b) {
        paint.setARGB(a, r, g, b);
    }

//    //设置字体
//    public void setTypeface(Typeface tf)
//    {
//        if (this.paint.getTypeface() != tf)
//        {
//            this.paint.setTypeface(tf);
//        }
//    }

    //设置行宽
    public void setLineWidth(int LineWidth) {
        mLineWidth = LineWidth;
    }

    //获取实际宽度
    public int getTextWidth() {
        return mTextWidth;
    }

    //设置Handler，用以发送事件
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (drawable != null)
        {
            //画背景
            Bitmap b = Bitmap.createBitmap(drawable.getBitmap(), 0, 0, mTextWidth, mTextHeight);
            canvas.drawBitmap(b, matrix, paint);
        }
        //画字
        draw(canvas, this.mText);
    }

    private void draw(Canvas canvas, String theText)
    {
        char ch;
        mTextPosy = 0;//初始化y坐标

        GLFont mGLFont = new GLFont();

        if(TextLength >= mMaxLength)
        {
            mText = mText.substring(0, mMaxLength);
            TextLength = mText.length();
            mText = mText.replace(mText.substring(TextLength - 3, TextLength), "...");
        }

        theText = mText;

        mTextPosx = textStartAlign == Paint.Align.LEFT ? mLineWidth : mTextWidth - mLineWidth;//初始化x坐标
        mTextPosx -= mFontHeight * (0.5 * mRealLine);
        for (int i = 0; i < this.TextLength; i++)
        {
            ch = theText.charAt(i);
            if (ch == '\n')
            {
                if (textStartAlign == Paint.Align.LEFT)
                {
                    mTextPosx += mLineWidth;// 换列
                }
                else
                {
                    mTextPosx -= mLineWidth;// 换列
                }
                mTextPosy = 0;
            }
            else
            {
                if (mTextPosy + mLineWidth >= this.mTextHeight && !"》".equals(String.valueOf(ch)))
                {
                    if (textStartAlign == Paint.Align.LEFT)
                    {
                        mTextPosx += mLineWidth;// 换列
                    }
                    else
                    {
                        mTextPosx -= mLineWidth;// 换列
                    }
                    i--;
                    mTextPosy = 0;
                }
                else
                {
                    Bitmap bitmap;

                    if(!isChinese(ch))
                    {
                        bitmap = mGLFont.getImage(mFontHeight, mTextWidth, String.valueOf(ch), (int)mFontSize, mTextColor);

                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        bitmap = Bitmap.createBitmap(bitmap,0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    }
                    else
                    {
                        bitmap = mGLFont.getImage(mTextWidth, mFontHeight, String.valueOf(ch), (int)mFontSize, mTextColor);
                    }

                    canvas.drawBitmap(bitmap, mTextPosx, mTextPosy, paint);
                    bitmap.recycle();
                    mTextPosy += mLineWidth;
                }
            }
        }

        //调用接口方法
        //activity.getHandler().sendEmptyMessage(TestFontActivity.UPDATE);
    }

    private boolean isChinese(char c)
    {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (!"《".equals(String.valueOf(c)) && !"》".equals(String.valueOf(c))
                && (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS))
        {
            return true;
        }
        return false;
    }

    //计算文字行数和总宽
    private void GetTextInfo()
    {
        Log.v("TextViewVertical", "GetTextInfo");
        char ch;
        int h = 0;
        paint.setTextSize(mFontSize);
        //获得字宽
        if (mLineWidth == 0)
        {
            mLineWidth = (int)mFontSize;
        }

        Paint.FontMetrics fm = paint.getFontMetrics();
        mFontHeight = (int) (Math.ceil(fm.descent - fm.top) * 0.9);// 获得字体高度

        //计算文字行数
        mRealLine = 0;
        for (int i = 0; i < TextLength; i++)
        {
            ch = mText.charAt(i);
            if (ch == '\n')
            {
                mRealLine++;// 真实的行数加一
                h = 0;
            }
            else
            {
                h += mFontHeight;
                if (h > mTextHeight)
                {
                    mRealLine++;// 真实的行数加一
                    i--;
                    h = 0;
                }
                else
                {
                    if (i == TextLength - 1)
                    {
                        mRealLine++;// 真实的行数加一
                    }
                }
            }
        }
        mRealLine++;

        mTextWidth = mLineWidth * mRealLine;//计算文字总宽度

        if(mTextHeight >= getHeight())
        {
            mTextHeight = getHeight();
        }

        measure(mTextWidth, getHeight());//重新调整大小
        layout(getLeft(), getTop(), getLeft() + mTextWidth, getBottom());//重新绘制容器
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        System.out.println("onMeasure");
        measureHeight(heightMeasureSpec);

        //int measuredWidth = measureWidth(widthMeasureSpec);
        if (mTextWidth == 0) GetTextInfo();
        setMeasuredDimension(mTextWidth, mTextHeight);
        if (oldwidth != getWidth())
        {
            oldwidth = getWidth();
            if (mHandler != null) mHandler.sendEmptyMessage(LAYOUT_CHANGED);
        }
    }

    private int measureHeight(int measureSpec)
    {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 500;
        switch (specMode)
        {
            case MeasureSpec.AT_MOST:
                result = specSize;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                result = 500;
                break;
        }

        mTextHeight = result;//设置文本高度
        return result;
    }
}