package com.moka.carsharing.utils.WaveView;

/**
 * Created by blablabla on 2017/7/25.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.moka.carsharing.ui.HomePageActivity;

public class WaveView extends View{
    /**
     * +------------------------+
     * |<--wave length->        |______
     * |   /\          |   /\   |  |
     * |  /  \         |  /  \  | amplitude
     * | /    \        | /    \ |  |
     * |/      \       |/      \|__|____
     * |        \      /        |  |
     * |         \    /         |  |
     * |          \  /          |  |
     * |           \/           | water level
     * |                        |  |
     * |                        |  |
     * +------------------------+__|____
     */
    private static final float DEFAULT_AMPLITUDE_RATIO = 0.05f;
    private static final float DEFAULT_WATER_LEVEL_RATIO = 0.5f;
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f;
    private static final float DEFAULT_WAVE_SHIFT_RATIO = 0.0f;
    private static final int LEVEL_NUMBER = 0;

    public static final int DEFAULT_BEHIND_WAVE_COLOR = Color.parseColor("#00000000");
    public static final int DEFAULT_FRONT_WAVE_COLOR = Color.parseColor("#3CFFFFFF");
    public static final ShapeType DEFAULT_WAVE_SHAPE = ShapeType.CIRCLE;

    public enum ShapeType {
        CIRCLE,
        SQUARE
    }

    // if true, the shader will display the wave
    private boolean mShowWave;

    // shader containing repeated waves
    private BitmapShader mWaveShader;
    // shader matrix
    private Matrix mShaderMatrix;
    // paint to draw wave
    private Paint mViewPaint;
    // paint to draw border
    private Paint mBorderPaint;

    private float mDefaultAmplitude;
    private float mDefaultWaterLevel;
    private float mDefaultWaveLength;
    private double mDefaultAngularFrequency;

    private float mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO;
    private float mWaveLengthRatio = DEFAULT_WAVE_LENGTH_RATIO;
    private float mWaterLevelRatio = DEFAULT_WATER_LEVEL_RATIO;
    private float mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO;
    private int mLevelNumber = LEVEL_NUMBER;
    private int mTextColor ;

    private int mBehindWaveColor = DEFAULT_BEHIND_WAVE_COLOR;
    private int mFrontWaveColor = DEFAULT_FRONT_WAVE_COLOR;
    private ShapeType mShapeType = DEFAULT_WAVE_SHAPE;
    private Context activityContext;
    public WaveView(Context context) {
        super(context);
        init();
    }

    public void setActivity(Context activity) {
        activityContext = activity;
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mShaderMatrix = new Matrix();
        mViewPaint = new Paint();
        mViewPaint.setAntiAlias(true);
    }

    public float getWaveShiftRatio() {
        return mWaveShiftRatio;
    }

    /**
     * Shift the wave horizontally according to <code>waveShiftRatio</code>.
     *
     * @param waveShiftRatio Should be 0 ~ 1. Default to be 0.
     *                       Result of waveShiftRatio multiples width of WaveView is the length to shift.
     */
    public void setWaveShiftRatio(float waveShiftRatio) {
        if (mWaveShiftRatio != waveShiftRatio) {
            mWaveShiftRatio = waveShiftRatio;
            invalidate();
        }
    }

    public float getWaterLevelRatio() {
        return mWaterLevelRatio;
    }

    public void setLevelNumber(int levelNumber) {
        if (mLevelNumber != levelNumber) {
            HomePageActivity activity=(HomePageActivity) activityContext;
            mLevelNumber = levelNumber;
            if(mLevelNumber<30){
                mTextColor=Color.parseColor("#ee592c");
                activity.setThemeColor(1);
            }
            else if(mLevelNumber<=60){
                mTextColor=Color.parseColor("#f78f10");
                activity.setThemeColor(2);
            }else{
                mTextColor=Color.parseColor("#7ec30e");
                activity.setThemeColor(3);
            }

            invalidate();
        }
    }

    /**
     * Set water level according to <code>waterLevelRatio</code>.
     *
     * @param waterLevelRatio Should be 0 ~ 1. Default to be 0.5.
     *                        Ratio of water level to WaveView height.
     */
    public void setWaterLevelRatio(float waterLevelRatio) {
        if (mWaterLevelRatio != waterLevelRatio) {
            mWaterLevelRatio = waterLevelRatio;
            invalidate();
        }
    }

    public float getAmplitudeRatio() {
        return mAmplitudeRatio;
    }

    /**
     * Set vertical size of wave according to <code>amplitudeRatio</code>
     *
     * @param amplitudeRatio Default to be 0.05. Result of amplitudeRatio + waterLevelRatio should be less than 1.
     *                       Ratio of amplitude to height of WaveView.
     */
    public void setAmplitudeRatio(float amplitudeRatio) {
        if (mAmplitudeRatio != amplitudeRatio) {
            mAmplitudeRatio = amplitudeRatio;
            invalidate();
        }
    }

    public float getWaveLengthRatio() {
        return mWaveLengthRatio;
    }

    /**
     * Set horizontal size of wave according to <code>waveLengthRatio</code>
     *
     * @param waveLengthRatio Default to be 1.
     *                        Ratio of wave length to width of WaveView.
     */
    public void setWaveLengthRatio(float waveLengthRatio) {
        mWaveLengthRatio = waveLengthRatio;
    }

    public boolean isShowWave() {
        return mShowWave;
    }

    public void setShowWave(boolean showWave) {
        mShowWave = showWave;
    }

    public void setBorder(int width, int color) {
        if (mBorderPaint == null) {
            mBorderPaint = new Paint();
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setStyle(Style.STROKE);
        }
        mBorderPaint.setColor(color);
        mBorderPaint.setStrokeWidth(width);

        invalidate();
    }

    public void setWaveColor(int behindWaveColor, int frontWaveColor) {
        mBehindWaveColor = behindWaveColor;
        mFrontWaveColor = frontWaveColor;

        if (getWidth() > 0 && getHeight() > 0) {
            // need to recreate shader when color changed
            mWaveShader = null;
            createShader();
            invalidate();
        }
    }

    public void setShapeType(ShapeType shapeType) {
        mShapeType = shapeType;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        createShader();
    }

    /**
     * Create the shader with default waves which repeat horizontally, and clamp vertically
     */
    private void createShader() {
        mDefaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / getWidth();
        mDefaultAmplitude = getHeight() * DEFAULT_AMPLITUDE_RATIO;
        mDefaultWaterLevel = getHeight() * DEFAULT_WATER_LEVEL_RATIO;
        mDefaultWaveLength = getWidth();

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint wavePaint = new Paint();
        wavePaint.setStrokeWidth(2);
        wavePaint.setAntiAlias(true);

        // Draw default waves into the bitmap
        // y=Asin(ωx+φ)+h
        final int endX = getWidth() + 1;
        final int endY = getHeight() + 1;

        float[] waveY = new float[endX];

        wavePaint.setColor(mBehindWaveColor);
        for (int beginX = 0; beginX < endX; beginX++) {
            double wx = beginX * mDefaultAngularFrequency;
            float beginY = (float) (mDefaultWaterLevel + mDefaultAmplitude * Math.sin(wx));
            canvas.drawLine(beginX, beginY, beginX, endY, wavePaint);

            waveY[beginX] = beginY;
        }

        wavePaint.setColor(mFrontWaveColor);
        final int wave2Shift = (int) (mDefaultWaveLength / 4);
        for (int beginX = 0; beginX < endX; beginX++) {
            canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX], beginX, endY, wavePaint);
        }

        // use the bitamp to create the shader
        mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        mViewPaint.setShader(mWaveShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWay(canvas,1);
        drawWay(canvas,3);
        drawWay(canvas,2);
    }
    public void drawWay(Canvas canvas,int time){
        // modify paint shader according to mShowWave state
        if (mShowWave && mWaveShader != null) {
            // first call after mShowWave, assign it to our paint
            if (mViewPaint.getShader() == null) {
                mViewPaint.setShader(mWaveShader);
            }

            // sacle shader according to mWaveLengthRatio and mAmplitudeRatio
            // this decides the size(mWaveLengthRatio for width, mAmplitudeRatio for height) of waves
            mShaderMatrix.setScale(
                    mWaveLengthRatio / DEFAULT_WAVE_LENGTH_RATIO,
                    mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO,
                    0,
                    mDefaultWaterLevel);
            // translate shader according to mWaveShiftRatio and mWaterLevelRatio
            // this decides the start position(mWaveShiftRatio for x, mWaterLevelRatio for y) of waves
            mShaderMatrix.postTranslate(
                    mWaveShiftRatio * getWidth(),
                    (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio) * getHeight());

            // assign matrix to invalidate the shader
            mWaveShader.setLocalMatrix(mShaderMatrix);
            float borderWidth = mBorderPaint == null ? 0f : mBorderPaint.getStrokeWidth();
            //遮罩效果
            int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
            mViewPaint.setShader(null);
            mViewPaint.setStrokeWidth(3);
            mViewPaint.setTextSize(100);
            Typeface font = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
            mViewPaint.setTypeface(font);
            if(time==1){
                mViewPaint.setColor(Color.parseColor("#ffffff"));
                mViewPaint.setTextAlign(Paint.Align.LEFT);
                Rect bounds = new Rect();
                mViewPaint.getTextBounds(getLevelString(mLevelNumber), 0, (getLevelString(mLevelNumber)).length(), bounds);
                Paint.FontMetricsInt fontMetrics = mViewPaint.getFontMetricsInt();
                int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
                canvas.drawText(getLevelString(mLevelNumber),getMeasuredWidth() / 2 - bounds.width() / 2, baseline-30, mViewPaint);
                //下方剩余电量小字
                mViewPaint.setTextSize(40);
                font = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
                mViewPaint.setTypeface(font);
                bounds = new Rect();
                mViewPaint.getTextBounds("剩余电量", 0, ("剩余电量").length(), bounds);
                canvas.drawText("剩余电量",getMeasuredWidth() / 2 - bounds.width() / 2, baseline+25, mViewPaint);
            }else if(time==2){
                mViewPaint.setColor(mTextColor);
                mViewPaint.setTextAlign(Paint.Align.LEFT);
                Rect bounds = new Rect();
                mViewPaint.getTextBounds(getLevelString(mLevelNumber), 0, (getLevelString(mLevelNumber)).length(), bounds);
                Paint.FontMetricsInt fontMetrics = mViewPaint.getFontMetricsInt();
                int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
                canvas.drawText(getLevelString(mLevelNumber),getMeasuredWidth() / 2 - bounds.width() / 2, baseline-30, mViewPaint);
                //下方剩余电量小字
                mViewPaint.setTextSize(40);
                font = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
                mViewPaint.setTypeface(font);
                bounds = new Rect();
                mViewPaint.getTextBounds("剩余电量", 0, ("剩余电量").length(), bounds);
                canvas.drawText("剩余电量",getMeasuredWidth() / 2 - bounds.width() / 2, baseline+25, mViewPaint);
            }
            if(time==1){
                mViewPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            }else if(time==2){
                mViewPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            }
            if (mViewPaint.getShader() == null) {
                mViewPaint.setShader(mWaveShader);
            }
            switch (mShapeType) {
                case CIRCLE:
                    if (borderWidth > 0) {
                        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f,
                                (getWidth() - borderWidth) / 2f - 1f, mBorderPaint);
                    }
                    float radius = getWidth() / 2f - borderWidth;
                    canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, mViewPaint);
                    break;
                case SQUARE:
                    if (borderWidth > 0) {
                        canvas.drawRect(
                                borderWidth / 2f,
                                borderWidth / 2f,
                                getWidth() - borderWidth / 2f - 0.5f,
                                getHeight() - borderWidth / 2f - 0.5f,
                                mBorderPaint);
                    }
                    canvas.drawRect(borderWidth, borderWidth, getWidth() - borderWidth,
                            getHeight() - borderWidth, mViewPaint);
                    break;
            }
            //遮罩效果
            mViewPaint.setXfermode(null);
            canvas.restoreToCount(layerId);
        } else {
            mViewPaint.setShader(null);
        }
    }

    public String getLevelString(int level){
            return level+"%";
    }
}