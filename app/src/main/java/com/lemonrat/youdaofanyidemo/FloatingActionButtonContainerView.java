package com.lemonrat.youdaofanyidemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

public class FloatingActionButtonContainerView extends FrameLayout {

    private static final int INIT_SIZE = 3;//默认容器中的FloatingActionButton数量
    private static final int DO_ROTATE = 1;//旋转动画
    private static final int RECOVER_ROTATE = -1;//恢复旋转之前的状态
    private static final int UNFOLDING = 2;//菜单展开状态
    private static final int FOLDING = 3;//菜单折叠状态
    private static final int MARGIN = 50;
    private int mWidth = 500;//ViewGroup的宽
    private int mHeight = 500;//ViewGroup的高
    private int length = 300;//子View展开的距离
    private int flag = FOLDING;//菜单展开与折叠的状态
    private float mScale = 0.8f;//展开之后的缩放比例
    private int mDuration = 400;//动画时长
    private FloatingActionButton ctrlButton;//在Activity中显示的Button


    public FloatingActionButtonContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initContainerView();
    }


    public FloatingActionButtonContainerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initContainerView();
    }


    private void initContainerView() {
        this.removeAllViews();
        setUpCtrlButton();
        setContainerSize(INIT_SIZE);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutCtrlButton();
        layoutExpandChildButton();
    }

    private void setContainerSize(int size) {
        boolean reqLayout = false;
        final int childCount = this.getChildCount();
        final int expandChild = childCount - 1;
        if (size > expandChild) {
            for (int i = 0; i < (size - expandChild); i++) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
                FloatingActionButton btnFloatingAction = new FloatingActionButton(this.getContext());
                setChildButtonListener(btnFloatingAction);
                btnFloatingAction.setVisibility(INVISIBLE);
                switch (i) {
                    case 0:  btnFloatingAction.setImageResource(R.drawable.ic_switch);
                        break;
                    case 1:  btnFloatingAction.setImageResource(R.drawable.ic_clear);
                        break;
                    case 2:  btnFloatingAction.setImageResource(R.drawable.ic_setting);
                        break;
                }


                this.addView(btnFloatingAction, lp);
                reqLayout = true;
            }
        } else if (size < expandChild) {
            if (size < 0) {
                size = 0;
            }
            for (int i = 0; i < (expandChild - size); i++) {
                this.removeViewAt(this.getChildCount() - 1);
                reqLayout = true;
            }
        }

        if (reqLayout) {
            this.requestLayout();
        }
    }

    private void layoutCtrlButton() {

        int width = ctrlButton.getMeasuredWidth();
        int height = ctrlButton.getMeasuredHeight();
        //1:相对于父控件布局的left
        //2:控件的top
        //3:右边缘的left
        //4:底部的top
        //所以后两个直接用left加上宽，以及top加上height就好
        ctrlButton.layout(mWidth - width-MARGIN, mHeight-height-MARGIN, mWidth-MARGIN, mHeight-MARGIN);

    }

    private void layoutExpandChildButton() {
        final int cCount = getChildCount();
        final int width = ctrlButton.getMeasuredWidth();
        final int height = ctrlButton.getMeasuredHeight();

        //设置子View的初始化位置与mainButton重合并且设置不可见
        for (int i = 1; i < cCount; i++) {
            final View view = getChildAt(i);
            view.layout(mWidth - width-MARGIN, mHeight-height-MARGIN, mWidth-MARGIN, mHeight-MARGIN);
            view.setVisibility(INVISIBLE);
            view.setClickable(false);
            view.setFocusable(false);
        }
    }

    private void setUpCtrlButton() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        ctrlButton = new FloatingActionButton(this.getContext());
        ctrlButton.setImageResource(R.drawable.ic_menu);
        setCtrlButtonListener(ctrlButton);
        this.addView(ctrlButton, lp);

    }


    private void setCtrlButtonListener(final View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == FOLDING) {
                    final int cCount = FloatingActionButtonContainerView.this.getChildCount();
                    ctrlButtonRotateAnimation(getChildAt(0), DO_ROTATE);
                    for (int i = 1; i < cCount; i++) {
                        view = getChildAt(i);
                        view.setVisibility(VISIBLE);
                        setTranslation(view, 90 / (cCount - 2) * (i - 1));
                        flag = UNFOLDING;
                        setRotateAnimation(view, DO_ROTATE);
                    }
                } else {
                    ctrlButtonRotateAnimation(getChildAt(0), RECOVER_ROTATE);
                    setBackTranslation();
                    flag = FOLDING;
                    setRotateAnimation(view, RECOVER_ROTATE);

                }
            }
        });
    }

    private void setTranslation(final View view, int angle) {
        int x = (int) (length * Math.sin(Math.toRadians(angle)));
        int y = (int) (length * Math.cos(Math.toRadians(angle)));
        ObjectAnimator tX = ObjectAnimator.ofFloat(view, "translationX", -x);
        ObjectAnimator tY = ObjectAnimator.ofFloat(view, "translationY", -y);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", mScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", mScale);
        AnimatorSet set = new AnimatorSet();
        set.play(tX).with(tY).with(alpha);
        set.play(scaleX).with(scaleY).with(tX);
        set.setDuration(mDuration);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setClickable(true);
                view.setFocusable(true);
            }
        });
    }

    private void setBackTranslation() {
        int cCount = getChildCount();
        for (int i = 1; i < cCount; i++) {
            final View view = getChildAt(i);
            ObjectAnimator tX = ObjectAnimator.ofFloat(view, "translationX", 0);
            ObjectAnimator tY = ObjectAnimator.ofFloat(view, "translationY", 0);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0);
            AnimatorSet set = new AnimatorSet();
            set.play(tX).with(tY).with(alpha);
            set.setDuration(mDuration);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();

            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(INVISIBLE);
                    view.setClickable(false);
                    view.setFocusable(false);
                }
            });

        }
    }

    private void setRotateAnimation(View view, int flag) {
        ObjectAnimator rotate = null;
        if (flag == DO_ROTATE) {
            rotate = ObjectAnimator.ofFloat(view, "rotation", 135);
        } else {
            rotate = ObjectAnimator.ofFloat(view, "rotation", 0);
        }
        rotate.setDuration(mDuration);
        rotate.start();
    }

    private void ctrlButtonRotateAnimation(View view, int flag) {
        ObjectAnimator rotate = null;
        if (flag == DO_ROTATE) {
            rotate = ObjectAnimator.ofFloat(view, "rotation", 135);
        } else {
            rotate = ObjectAnimator.ofFloat(view, "rotation", 0);
        }
        rotate.setDuration(mDuration);
        rotate.start();
    }




    private void setChildButtonListener(final View view) {
        view.setOnTouchListener(new OnTouchListener() {
            int x, y;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) motionEvent.getX();
                        y = (int) motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if((int)motionEvent.getX() == x && (int)motionEvent.getY() == y) {
                            setBackTranslation();
                            setRotateAnimation(ctrlButton, RECOVER_ROTATE);
                            flag = UNFOLDING;
                            view.callOnClick();
                        }
                        break;
                }
                return true;
            }
        });
    }


}
