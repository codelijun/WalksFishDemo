package com.example.lijun.walksfishdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.example.lijun.walksfishdemo.utils.ScreenUtil;
import com.example.lijun.walksfishdemo.widget.WalksFishView;

public class MainActivity extends AppCompatActivity {

    private static final boolean DEBUG = true;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_SET_WALLPAPER = 1001;
    private WalksFishView mWalksFishView;
    private ValueAnimator mValueAnimator;
    private Handler mHandler = new Handler();
    private PointF mTargetPosition;
    private PointF mCurrentPosition;
    private int mFishWalksDistance;
    private int mWindowsWidth;
    private int mWindowsHeight;
    private int mFishViewWidth;
    private int mFishViewHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mWalksFishView = findViewById(R.id.walks_fish_view);

        mTargetPosition = new PointF();
        mFishWalksDistance = ScreenUtil.dip2px(this, 300);
        mWindowsWidth = ScreenUtil.getScreenWidth(this);
        mWindowsHeight = ScreenUtil.getScreenHeight(this);
        mFishViewWidth = mWalksFishView.getFishViewWidth();
        mFishViewHeight = mWalksFishView.getFishViewHeight();

        initFishPosition();
        initAnimation();
        mHandler.post(mTranslationRunnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_WALLPAPER) {
            if (resultCode == RESULT_OK) {
                // TODO: 2017/3/13 设置动态壁纸成功
                finish();
            } else {
                // TODO: 2017/3/13 取消设置动态壁纸
                finish();
            }
        }
    }

    private void initAnimation() {
        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setDuration(3000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float positionX = ((float) animation.getAnimatedValue() * (mTargetPosition.x - mCurrentPosition.x) + mCurrentPosition.x);
                float positionY = ((float) animation.getAnimatedValue() * (mTargetPosition.y - mCurrentPosition.y) + mCurrentPosition.y);
                if (DEBUG) {
                    Log.d(TAG, " onAnimationUpdate() positionX== " + positionX + " positionY== " + positionY);
                }
                setLayout(positionX, positionY);
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mCurrentPosition.x = mTargetPosition.x;
                mCurrentPosition.y = mTargetPosition.y;
                if (DEBUG) {
                    Log.d(TAG, " onAnimationEnd() " + mCurrentPosition.x);
                }
            }
        });
    }

    private Runnable mTranslationRunnable = new Runnable() {
        @Override
        public void run() {
            float currentAngle = mWalksFishView.getFishBodyAngle();
            float targetAngle = (float) (Math.random() * 90) - 45;
            if (DEBUG) {
                Log.d(TAG, " run() targetAngle== " + targetAngle + " mCurrentPosition y== " + mCurrentPosition.y);
            }
            if (mCurrentPosition.x < mFishViewWidth || mCurrentPosition.x > mWindowsWidth - mFishViewWidth) {
                targetAngle = (float) (Math.random() + ((currentAngle > 0 || currentAngle > 180) ? 45 : -45));
            }
            if (mCurrentPosition.y < mFishViewHeight || mCurrentPosition.y > mWindowsHeight - mFishViewHeight) {
                targetAngle = (float) (Math.random() + ((currentAngle > 90 || currentAngle > 270) ? 45 : -45));
            }

            mWalksFishView.turnToPosition(targetAngle);
            mTargetPosition = ScreenUtil.calculatPoint(mCurrentPosition, mFishWalksDistance, targetAngle + currentAngle);
            if (DEBUG) {
                Log.d(TAG, " run()mCurrentPosition== " + mCurrentPosition.x + " mTargetPosition.x== " + mTargetPosition.x + " mTargetPosition.y== " + mTargetPosition.y);
            }
            mHandler.postDelayed(mTranslationRunnable, 3300);
            mValueAnimator.start();
        }
    };

    private void initFishPosition() {
        int positionX = (int) ((Math.random() * (mWindowsWidth - mFishViewWidth)));
        int positionY = (int) ((Math.random() * (mWindowsHeight - mFishViewHeight)));
        if (DEBUG) {
            Log.d(TAG, " initFishPosition() positionX== " + positionX + " positionY== " + positionY);
        }
        setLayout(positionX, positionY);
        mCurrentPosition = new PointF(positionX, positionY);
    }

    private void setLayout(float x, float y) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mWalksFishView.getLayoutParams();
        layoutParams.leftMargin = (int) (x + 0.5f);
        layoutParams.topMargin = (int) (y + 0.5f);
        layoutParams.width = mWalksFishView.getFishViewWidth();
        layoutParams.height = mWalksFishView.getFishViewHeight();
        mWalksFishView.setLayoutParams(layoutParams);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWalksFishView.onDestroyView();
    }
}
