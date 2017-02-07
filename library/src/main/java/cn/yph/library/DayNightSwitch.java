package cn.yph.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.BounceInterpolator;

/**
 * Created by yuanpenghao on 16/9/6.
 */
public class DayNightSwitch extends View {

    private static int DEFAULT_WIDTH = 120;
    private static int DEFAULT_HEIGHT = 80;

    private Paint lightPaint;

    private Paint sunPaint;

    private Paint cloudPaint;

    private Paint starPaint;

    private Paint bgPaint;

    private boolean isOpen = false;

    private float bgRadius = DEFAULT_HEIGHT / 2;

    private float lightRadius = bgRadius - 14;

    private float sunRadius = lightRadius - 10;

    private float halfLength;

    private int bgStrokeWidth = 2;

    private RectF bgRect;

    private int nightBgColor = Color.parseColor("#7A8B8B");

    private int nightBgStrokeColor = Color.parseColor("#4A4A4A");

    private int dayBgColor = Color.parseColor("#87CEFF");

    private int dayBgStrokeColor = Color.parseColor("#63B8FF");

    private int nightLightColor = Color.parseColor("#EAEAEA");

    private int nightMoonColor = Color.WHITE;

    private int dayLightColor = Color.parseColor("#FFC125");

    private int daySunColor = Color.parseColor("#FFEC8B");

    private int starColor = Color.parseColor("#FFFFFF");

    private float thumbOffset;

    private float negativePos;
    private float positivePos;

    private float mSlop;

    private ValueAnimator moveAnimator;
    private float centerY;

    private int mDefaultPadding = 10;

    private float startPos;

    private float cloudRatio;
    private int cloudBaseLine;
    private int cloudCenterX;
    private int mDefaultSunPadding = 3;

    public DayNightSwitch(Context context) {
        super(context);

        init();
    }

    public DayNightSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public DayNightSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {

        halfLength = Utils.dp2px(getContext(), DEFAULT_WIDTH) / 2;

        mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        lightPaint = new Paint();
        lightPaint.setColor(dayLightColor);
        lightPaint.setAntiAlias(true);

        sunPaint = new Paint();
        sunPaint.setColor(daySunColor);
        sunPaint.setAntiAlias(true);

        cloudPaint = new Paint();
        cloudPaint.setColor(Color.WHITE);
        cloudPaint.setAntiAlias(true);

        starPaint = new Paint();
        starPaint.setColor(Color.WHITE);
        starPaint.setAntiAlias(true);

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(nightBgColor);
        bgPaint.setStrokeWidth(Utils.dp2px(getContext(), bgStrokeWidth));
        bgPaint.setStyle(Paint.Style.FILL);

        bgRect = new RectF(Utils.dp2px(getContext(), mDefaultPadding), Utils.dp2px(getContext(), mDefaultPadding), Utils.dp2px(getContext(), DEFAULT_WIDTH - mDefaultPadding), Utils.dp2px(getContext(), DEFAULT_HEIGHT - mDefaultPadding));

        negativePos = Utils.dp2px(getContext(), bgRadius + mDefaultSunPadding);
        positivePos = Utils.dp2px(getContext(), DEFAULT_WIDTH - bgRadius - mDefaultSunPadding);
        thumbOffset = negativePos;
        startPos = thumbOffset;
        centerY = Utils.dp2px(getContext(), DEFAULT_HEIGHT / 2);

        cloudBaseLine = Utils.dp2px(getContext(), DEFAULT_HEIGHT - bgStrokeWidth - mDefaultPadding - 10);
        cloudCenterX = Utils.dp2px(getContext(), (int) (DEFAULT_WIDTH - bgRadius - mDefaultPadding - lightRadius * 0.6));

        bgRadius = centerY - Utils.dp2px(getContext(), mDefaultPadding);

        lightRadius = bgRadius - 10;

        sunRadius = lightRadius - 10;

    }

    private float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float moveLen = Math.abs(event.getX() - x);
                if (event.getX() > x) {
                    if (!isOpen) {
                        thumbOffset = negativePos + moveLen;
                        if (thumbOffset > positivePos) {
                            thumbOffset = positivePos;
                        } else if (thumbOffset < negativePos) {
                            thumbOffset = negativePos;
                        }
                        invalidate();
                        return true;
                    }
                } else {
                    if (isOpen) {
                        thumbOffset = positivePos - moveLen;
                        if (thumbOffset > positivePos) {
                            thumbOffset = positivePos;
                        } else if (thumbOffset < negativePos) {
                            thumbOffset = negativePos;
                        }
                        invalidate();
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startPos = thumbOffset;
                float moveActionOffset = event.getX() - x;
                if (moveActionOffset < mSlop) {
                    startMoveAnimator(isOpen);
                } else {
                    if (Math.abs(moveActionOffset) >= (halfLength - negativePos)) {
                        startMoveAnimator(isOpen);
                    } else {
                        startMoveAnimator(!isOpen);
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean isDay = thumbOffset >= halfLength;
        drawBg(canvas, isDay);

        drawSun(canvas);

        if (isDay) {
            drawCloud(canvas);
        } else {
            drawStars(canvas);
        }

    }

    private void drawBg(Canvas canvas, boolean isDay) {
        if (isDay) {
            bgPaint.setColor(dayBgColor);
            bgPaint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(bgRect, bgRadius, bgRadius, bgPaint);
            bgPaint.setColor(dayBgStrokeColor);
            bgPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(bgRect, bgRadius, bgRadius, bgPaint);
        } else {
            bgPaint.setColor(nightBgColor);
            bgPaint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(bgRect, bgRadius, bgRadius, bgPaint);
            bgPaint.setColor(nightBgStrokeColor);
            bgPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(bgRect, bgRadius, bgRadius, bgPaint);
        }
    }

    private void drawStars(Canvas canvas) {

        int sw = Utils.dp2px(getContext(), DEFAULT_WIDTH);
        int sh = Utils.dp2px(getContext(), DEFAULT_HEIGHT);

        float stars[][] = new float[7][2];
        stars[0][0] = (float) (sw / 2.0);
        stars[0][1] = (float) (sh / 5.0);

        stars[1][0] = (float) (sw * 3 / 4.0);
        stars[1][1] = (float) (sh / 5.0);

        stars[2][0] = (float) (sw * 5 / 8.0);
        stars[2][1] = (float) (sh * 2 / 5.0);

        stars[3][0] = (float) (sw * 27 / 40.0);
        stars[3][1] = (float) (sh * 3 / 5.0);

        stars[4][0] = (float) (sw * 5 / 6.0);
        stars[4][1] = (float) (sh * 9 / 20.0);

        stars[5][0] = (float) (sw * 4 / 5.0);
        stars[5][1] = (float) (sh * 7 / 10.0);

        stars[6][0] = (float) (sw * 11 / 20.0);
        stars[6][1] = (float) (sh * 3 / 4.0);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(starColor);

        float pos = thumbOffset / sw;
        float t = 4 - 10 * pos;
        if (pos > 0.8) {
            t = 10 * pos;
        }

        canvas.drawCircle(stars[0][0], stars[0][1], 6 + 2 * t, paint);
        canvas.drawCircle(stars[1][0], stars[1][1], 5 + 2 * t, paint);
        canvas.drawCircle(stars[2][0], stars[2][1], 5 + 2 * t, paint);
        canvas.drawCircle(stars[3][0], stars[3][1], 4 + 2 * t, paint);
        canvas.drawCircle(stars[4][0], stars[4][1], 8 - 2 * t, paint);
        canvas.drawCircle(stars[5][0], stars[5][1], 7 - 2 * t, paint);
        canvas.drawCircle(stars[6][0], stars[6][1], 7 - 2 * t, paint);
    }

    private void drawCloud(Canvas canvas) {

        float baseCircleRadius = Utils.dp2px(getContext(), DEFAULT_HEIGHT / 10);
        float secondCircleRadius = baseCircleRadius * 1.2f;
        float thirdCircleRadius = baseCircleRadius * 1.1f;

        cloudPaint.setColor(Color.GRAY);
        canvas.drawCircle(cloudCenterX - baseCircleRadius * cloudRatio, cloudBaseLine - baseCircleRadius, baseCircleRadius, cloudPaint);
        canvas.drawCircle((float) (cloudCenterX - secondCircleRadius * 0.5 * cloudRatio), (float) (cloudBaseLine - secondCircleRadius * 1.5 * cloudRatio), secondCircleRadius, cloudPaint);
        canvas.drawCircle((float) (cloudCenterX + thirdCircleRadius * 0.5 * cloudRatio), (float) (cloudBaseLine - thirdCircleRadius * 1.5 * cloudRatio), thirdCircleRadius, cloudPaint);
        canvas.drawCircle(cloudCenterX + baseCircleRadius * cloudRatio, cloudBaseLine - baseCircleRadius, baseCircleRadius, cloudPaint);
        canvas.drawRect(cloudCenterX - baseCircleRadius * cloudRatio, cloudBaseLine - baseCircleRadius * 2, cloudCenterX + baseCircleRadius * cloudRatio, cloudBaseLine, cloudPaint);

        int whiteCloudBaseLine = cloudBaseLine - 4;
        float whiteBaseCircleRadius = baseCircleRadius - 4;
        float whiteSecondCircleRadius = secondCircleRadius - 4;
        float whiteThirdCircleRadius = thirdCircleRadius - 4;
        cloudPaint.setColor(Color.WHITE);
        canvas.drawCircle(cloudCenterX - baseCircleRadius * cloudRatio, cloudBaseLine - baseCircleRadius, whiteBaseCircleRadius, cloudPaint);
        canvas.drawCircle((float) (cloudCenterX - secondCircleRadius * 0.5 * cloudRatio), (float) (cloudBaseLine - secondCircleRadius * 1.5 * cloudRatio), whiteSecondCircleRadius, cloudPaint);
        canvas.drawCircle((float) (cloudCenterX + thirdCircleRadius * 0.5 * cloudRatio), (float) (cloudBaseLine - thirdCircleRadius * 1.5 * cloudRatio), whiteThirdCircleRadius, cloudPaint);
        canvas.drawCircle(cloudCenterX + baseCircleRadius * cloudRatio, cloudBaseLine - baseCircleRadius, whiteBaseCircleRadius, cloudPaint);
        canvas.drawRect(cloudCenterX - baseCircleRadius * cloudRatio, whiteCloudBaseLine - whiteBaseCircleRadius * 2, cloudCenterX + whiteBaseCircleRadius * cloudRatio, whiteCloudBaseLine, cloudPaint);

    }

    private void drawSun(Canvas canvas) {
        boolean isDay = thumbOffset >= halfLength;
        if (isDay) {
            lightPaint.setColor(dayLightColor);
            sunPaint.setColor(daySunColor);
        } else {
            lightPaint.setColor(nightLightColor);
            sunPaint.setColor(nightMoonColor);
        }

        canvas.drawCircle(thumbOffset, centerY, lightRadius, lightPaint);

        canvas.drawCircle(thumbOffset, centerY, sunRadius, sunPaint);
    }

    private void startMoveAnimator(final boolean toStart) {

        moveAnimator = ValueAnimator.ofFloat(0, 1);
        moveAnimator.setDuration(300);
        moveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (toStart) {
                    thumbOffset = startPos - (startPos - negativePos) * value;
                } else {
                    thumbOffset = (positivePos - startPos) * value + startPos;
                }
                postInvalidate();
            }
        });
        moveAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isOpen = !toStart;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                thumbOffset = isOpen ? positivePos : negativePos;
                startPos = thumbOffset;
                postInvalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                thumbOffset = isOpen ? positivePos : negativePos;
                startPos = thumbOffset;
                postInvalidate();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        moveAnimator.start();
        if (!toStart && positivePos - thumbOffset > 20) {
            startCloudAnimation();
        }
    }

    private void startCloudAnimation() {
        final ValueAnimator cloudAnimator = ValueAnimator.ofFloat(0, 1);
        cloudAnimator.setDuration(300);
        cloudAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cloudRatio = (float) animation.getAnimatedValue();
                Utils.log("anim", cloudRatio);
            }
        });
        cloudAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cloudRatio = 1;
                postInvalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                cloudRatio = 1;
                postInvalidate();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        cloudAnimator.setInterpolator(new BounceInterpolator());
        postDelayed(new Runnable() {
            @Override
            public void run() {
                cloudAnimator.start();
            }
        }, 100);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Utils.dp2px(getContext(), DEFAULT_WIDTH) + getPaddingLeft() + getPaddingRight();
        int height = Utils.dp2px(getContext(), DEFAULT_HEIGHT) + getPaddingTop() + getPaddingBottom();

        if (widthSpecMode != MeasureSpec.AT_MOST) {
            width = Math.max(width, widthSpecSize);
        }

        if (heightSpecMode != MeasureSpec.AT_MOST) {
            height = Math.max(height, heightSpecSize);
        }

        setMeasuredDimension(width, height);
    }

    public interface onStateChangeListener {
        void onStateChange(boolean isOpen);
    }

}
