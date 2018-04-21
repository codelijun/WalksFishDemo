package com.example.lijun.walksfishdemo.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class WalksFishView extends ImageView {
    private static final boolean DEBUG = true;
    private static final String TAG = "WalksFishView";

    private ValueAnimator mValueAnimator;
    private ValueAnimator mAngleAnimator;
    //鱼当前的位置
    private int mPositionX;
    private int mPositionY;
    private float mCurrentAnigle;
    private float mtargetAnigle;
    private Handler mHandler;
    private WalksFishDrawable mWalksFishDrawable;

    public WalksFishView(Context context) {
        this(context, null);
    }

    public WalksFishView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WalksFishView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHandler = new Handler();
        mWalksFishDrawable = new WalksFishDrawable(context);
        setImageDrawable(mWalksFishDrawable);
        initAnimator();
        mValueAnimator.start();
    }

    public WalksFishDrawable getWalksFishDrawable() {
        return mWalksFishDrawable;
    }

    private void initAnimator() {
        mAngleAnimator = ValueAnimator.ofFloat(0, 1);
        mAngleAnimator.setDuration(500);
        mAngleAnimator.setInterpolator(new LinearInterpolator());
        mAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float temp = (float) animation.getAnimatedValue();
                mWalksFishDrawable.setMainAngle(temp * mtargetAnigle + mCurrentAnigle);
            }
        });

        mValueAnimator = ValueAnimator.ofInt(0, 360);
        mValueAnimator.setDuration(1000);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mWalksFishDrawable != null) {
                    if (DEBUG) {
                        Log.d(TAG, " onAnimationUpdate() ");
                    }
                    mWalksFishDrawable.setCurrentValue((Integer) animation.getAnimatedValue());
                    mWalksFishDrawable.invalidateSelf();
                }
            }
        });
    }

    public void setFishPosition(int positionX, int positionY) {
        this.mPositionX = positionX;
        this.mPositionY = positionY;
    }

    public int getFishViewWidth(){
        return mWalksFishDrawable.getIntrinsicWidth();
    }

    public int getFishViewHeight(){
        return mWalksFishDrawable.getIntrinsicHeight();
    }

    public int getPositionX() {
        return mPositionX;
    }

    public int getPositionY() {
        return mPositionY;
    }

    public float getFishBodyAngle(){
        return mWalksFishDrawable.getMainAngle();
    }

    public void turnToPosition(float targetAngle) {
        if (mWalksFishDrawable != null) {
            mtargetAnigle = targetAngle;
            mCurrentAnigle = mWalksFishDrawable.getMainAngle();
            mAngleAnimator.start();
        }
    }

    public void onDestroyView() {
        mValueAnimator.cancel();
    }
}
