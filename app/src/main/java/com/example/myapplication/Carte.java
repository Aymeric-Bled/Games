package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Random;


abstract public class Carte{

    enum Color{Carreau, Coeur, Pique, Trefle}
    enum Number{As, Deux, Trois, Quatre, Cinq, Six, Sept, Huit, Neuf, Dix, Valet, Dame, Roi}
    enum Position {End,Draw,Column,Depot}
    private Color color;
    private Number number;
    private LinearLayout layout;
    private boolean is_empty_layout;
    private boolean is_recto;
    private LinearLayout layout_top;
    private Button left_top;
    private Button top;
    private Button right_top;
    private Button middle;
    private LinearLayout layout_bottom;
    private Button left_bottom;
    private Button bottom;
    private Button right_bottom;
    private int width;
    private int height;
    private Context context;
    private boolean isMoving = false;
    private Position position;
    static private boolean isDoingEnd = false;

    Carte(Color color, Number number, Context context, int width, int height){
        this.color = color;
        this.number = number;
        this.layout = new LinearLayout(context);;
        onClickListener(layout);
        onLongClickListener(layout);
        this.is_empty_layout = true;
        this.is_recto = true;
        this.setVersoLayout();
        this.width = width;
        this.height = height;
        setWidth(width);
        setHeight(height);
        this.context = context;
        setVersoLayout();
    }

    LinearLayout getLayout(){
        return layout;
    }

    int getColor(Color color){
        switch (color){
            case Carreau:
            case Coeur:
                return android.graphics.Color.RED;
            default:
                return android.graphics.Color.BLACK;
        }
    }

    int getDrawable(Color color){
        switch (color){
            case Carreau:
                return R.drawable.carreau;
            case Coeur:
                return R.drawable.coeur;
            case Pique:
                return R.drawable.pique;
            case Trefle:
                return R.drawable.trefle;
            default:
                return -1;
        }
    }

    int getDrawable(Number number, Color color){
        switch (number){
            case As:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.as_noir;
                else
                    return R.drawable.as_rouge;
            case Deux:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.deux_noir;
                else
                    return R.drawable.deux_rouge;
            case Trois:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.trois_noir;
                else
                    return R.drawable.trois_rouge;
            case Quatre:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.quatre_noir;
                else
                    return R.drawable.quatre_rouge;
            case Cinq:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.cinq_noir;
                else
                    return R.drawable.cinq_rouge;
            case Six:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.six_noir;
                else
                    return R.drawable.six_rouge;
            case Sept:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.sept_noir;
                else
                    return R.drawable.sept_rouge;
            case Huit:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.huit_noir;
                else
                    return R.drawable.huit_rouge;
            case Neuf:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.neuf_noir;
                else
                    return R.drawable.neuf_rouge;
            case Dix:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.dix_noir;
                else
                    return R.drawable.dix_rouge;
            case Valet:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.valet_noir;
                else
                    return R.drawable.valet_rouge;
            case Dame:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.dame_noir;
                else
                    return R.drawable.dame_rouge;
            case Roi:
                if (getColor(color) == android.graphics.Color.BLACK)
                    return R.drawable.roi_noir;
                else
                    return R.drawable.roi_rouge;
            default:
                return -1;
        }
    }

    void setRectoLayout(){
        if (!is_recto) {
            is_recto = true;
            layout.setBackgroundResource(R.drawable.carte);
            layout.bringToFront();
            if (is_empty_layout) {

                is_empty_layout = false;
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(new FrameLayout.LayoutParams(width, height));
                layout_top = new LinearLayout(context);
                layout_top.setOrientation(LinearLayout.HORIZONTAL);
                layout_top.setLayoutParams(new LinearLayout.LayoutParams(this.width, this.width / 3));

                left_top = new Button(context);
                onClickListener(left_top);
                onLongClickListener(left_top);
                left_top.setMinimumWidth(0);
                left_top.setMinimumHeight(0);
                left_top.setWidth(this.width / 3);
                left_top.setHeight(this.width / 3);
                left_top.setBackgroundResource(getDrawable(number, color));
                left_top.setLayoutParams(new LinearLayout.LayoutParams(this.width / 3, ViewGroup.LayoutParams.MATCH_PARENT));
                left_top.setScaleX((float) 0.7);
                left_top.setScaleY((float) 0.7);
                layout_top.addView(left_top);

                top = new Button(context);
                onClickListener(top);
                onLongClickListener(top);
                top.setMinimumWidth(0);
                top.setMinimumHeight(0);
                top.setWidth(this.width / 3);
                top.setHeight(this.width / 3);
                top.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                layout_top.addView(top);

                right_top = new Button(context);
                onClickListener(right_top);
                onLongClickListener(right_top);
                right_top.setMinimumWidth(0);
                right_top.setMinimumHeight(0);
                right_top.setWidth(this.width / 3);
                right_top.setHeight(this.width / 3);
                right_top.setBackgroundResource(getDrawable(this.color));
                right_top.setScaleX((float) 0.7);
                right_top.setScaleY((float) 0.7);
                right_top.setLayoutParams(new LinearLayout.LayoutParams(this.width / 3, ViewGroup.LayoutParams.MATCH_PARENT));
                layout_top.addView(right_top);


                layout.addView(layout_top);

                middle = new Button(context);
                onClickListener(middle);
                onLongClickListener(middle);
                middle.setMinimumWidth(0);
                middle.setMinimumHeight(0);
                middle.setWidth(this.height - 2 * this.width / 3);
                middle.setHeight(this.height - 2 * this.width / 3);
                middle.setBackgroundResource(getDrawable(this.color));
                middle.setScaleX((float) 0.8);
                middle.setScaleY((float) 0.8);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(this.height - 2 * this.width / 3, this.height - 2 * this.width / 3);
                params.gravity = Gravity.CENTER;
                middle.setLayoutParams(params);
                layout.addView(middle);

                layout_bottom = new LinearLayout(context);
                layout_bottom.setOrientation(LinearLayout.HORIZONTAL);
                layout_bottom.setLayoutParams(new LinearLayout.LayoutParams(this.width, this.width / 3));

                left_bottom = new Button(context);
                onClickListener(left_bottom);
                onLongClickListener(left_bottom);
                left_bottom.setMinimumWidth(0);
                left_bottom.setMinimumHeight(0);
                left_bottom.setWidth(this.width / 3);
                left_bottom.setHeight(this.width / 3);
                left_bottom.setBackgroundResource(getDrawable(this.color));
                left_bottom.setScaleX((float) 0.7);
                left_bottom.setScaleY((float) 0.7);
                left_bottom.setLayoutParams(new LinearLayout.LayoutParams(this.width / 3, ViewGroup.LayoutParams.MATCH_PARENT));
                layout_bottom.addView(left_bottom);

                bottom = new Button(context);
                onClickListener(bottom);
                onLongClickListener(bottom);
                bottom.setMinimumWidth(0);
                bottom.setMinimumHeight(0);
                bottom.setWidth(this.width / 3);
                bottom.setHeight(this.width / 3);
                bottom.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                layout_bottom.addView(bottom);

                right_bottom = new Button(context);
                onClickListener(right_bottom);
                onLongClickListener(right_bottom);
                right_bottom.setMinimumWidth(0);
                right_bottom.setMinimumHeight(0);
                right_bottom.setWidth(this.width / 3);
                right_bottom.setHeight(this.width / 3);
                right_bottom.setBackgroundResource(getDrawable(number, color));
                right_bottom.setLayoutParams(new LinearLayout.LayoutParams(this.width / 3, ViewGroup.LayoutParams.MATCH_PARENT));
                right_bottom.setScaleX((float) 0.7);
                right_bottom.setScaleY((float) 0.7);
                layout_bottom.addView(right_bottom);

                layout.addView(layout_bottom);


            } else {
                right_top.setBackgroundResource(getDrawable(color));
                left_bottom.setBackgroundResource(getDrawable(color));
                middle.setBackgroundResource(getDrawable(color));
                left_top.setBackgroundResource(getDrawable(number, color));
                right_bottom.setBackgroundResource(getDrawable(number, color));
            }
        }

    }

    void setVersoLayout(){
        if (is_recto) {
            is_recto = false;
            if (!is_empty_layout) {
                right_top.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                left_bottom.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                middle.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                left_top.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                right_bottom.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            }
            layout.setBackgroundResource(R.drawable.carte_verso);
        }
    }

    void setWidth(int width){
        this.width = width;
        layout.setLayoutParams(new FrameLayout.LayoutParams(width, height));

        if (!is_empty_layout) {
            layout_top = (LinearLayout) layout.getChildAt(0);
            layout_top.setLayoutParams(new LinearLayout.LayoutParams(width, width / 3));


            left_top.setWidth(width / 3);
            left_top.setHeight(width / 3);
            left_top.setLayoutParams(new LinearLayout.LayoutParams(width / 3, ViewGroup.LayoutParams.MATCH_PARENT));

            top.setWidth(width / 3);
            top.setHeight(width / 3);

            right_top.setWidth(width / 3);
            right_top.setHeight(width / 3);
            right_top.setLayoutParams(new LinearLayout.LayoutParams(width / 3, ViewGroup.LayoutParams.MATCH_PARENT));


            middle.setWidth(height - 2 * width / 3);
            middle.setHeight(height - 2 * width / 3);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height - 2 * width / 3, height - 2 * width / 3);
            params.gravity = Gravity.CENTER;
            middle.setLayoutParams(params);

            layout_bottom = (LinearLayout) layout.getChildAt(2);
            layout_bottom.setLayoutParams(new LinearLayout.LayoutParams(width, width / 3));

            left_bottom.setWidth(width / 3);
            left_bottom.setHeight(width / 3);
            left_bottom.setLayoutParams(new LinearLayout.LayoutParams(width / 3, ViewGroup.LayoutParams.MATCH_PARENT));

            bottom.setWidth(width / 3);
            bottom.setHeight(width / 3);

            right_bottom.setWidth(width / 3);
            right_bottom.setHeight(width / 3);
            right_bottom.setLayoutParams(new LinearLayout.LayoutParams(width / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    void setHeight(int height){
        this.height = height;
        layout.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        if (!is_empty_layout) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height - 2 * width / 3, height - 2 * width / 3);
            params.gravity = Gravity.CENTER;
            middle.setLayoutParams(params);
        }
    }

    void setX(float x){
        layout.setX(x);
    }

    void setY(float y){
        layout.setY(y);
    }


    synchronized boolean canMove(){
        if (!isMoving){
            isMoving = true;
            return true;
        }
        return false;
    }


    void move_(AnimatorSet s, int x, int y, int width, int height){
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(layout, "translationX", layout.getX(), x);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(layout, "translationY", layout.getY(), y);
        ValueAnimator animWidth = ValueAnimator.ofInt(layout.getMeasuredWidth(), width);
        animWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                layoutParams.width = val;
                layout.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animHeight = ValueAnimator.ofInt(layout.getMeasuredHeight(), height);
        animHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                layoutParams.height= val;
                layout.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animWidthLayoutTop = ValueAnimator.ofInt(layout_top.getMeasuredWidth(), width);
        animWidthLayoutTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layout_top.getLayoutParams();
                layoutParams.width = val;
                layout_top.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animHeightLayoutTop = ValueAnimator.ofInt(layout_top.getMeasuredHeight(), width / 3);
        animHeightLayoutTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layout_top.getLayoutParams();
                layoutParams.height= val;
                layout_top.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animWidthLeftTop = ValueAnimator.ofInt(left_top.getMeasuredWidth(), width / 3);
        animWidthLeftTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = left_top.getLayoutParams();
                layoutParams.width = val;
                left_top.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animHeightLeftTop = ValueAnimator.ofInt(left_top.getMeasuredHeight(), width / 3);
        animHeightLeftTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = left_top.getLayoutParams();
                layoutParams.height= val;
                left_top.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animWidthTop = ValueAnimator.ofInt(top.getMeasuredWidth(), width / 3);
        animWidthTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = top.getLayoutParams();
                layoutParams.width = val;
                top.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animHeightTop = ValueAnimator.ofInt(top.getMeasuredHeight(), width / 3);
        animHeightTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = top.getLayoutParams();
                layoutParams.height= val;
                top.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animWidthRightTop = ValueAnimator.ofInt(right_top.getMeasuredWidth(), width / 3);
        animWidthRightTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = right_top.getLayoutParams();
                layoutParams.width = val;
                right_top.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animHeightRightTop = ValueAnimator.ofInt(right_top.getMeasuredHeight(), width / 3);
        animHeightRightTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = right_top.getLayoutParams();
                layoutParams.height= val;
                right_top.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animWidthMiddle= ValueAnimator.ofInt(middle.getMeasuredWidth(), height - 2 * width / 3);
        animWidthMiddle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = middle.getLayoutParams();
                layoutParams.width = val;
                middle.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animHeightMiddle = ValueAnimator.ofInt(middle.getMeasuredHeight(), height - 2 * width / 3);
        animHeightMiddle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = middle.getLayoutParams();
                layoutParams.height= val;
                middle.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animWidthLayoutBottom = ValueAnimator.ofInt(layout_bottom.getMeasuredWidth(), width);
        animWidthLayoutBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layout_bottom.getLayoutParams();
                layoutParams.width = val;
                layout_bottom.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animHeightLayoutBottom = ValueAnimator.ofInt(layout_bottom.getMeasuredHeight(), width / 3);
        animHeightLayoutBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layout_bottom.getLayoutParams();
                layoutParams.height= val;
                layout_bottom.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animWidthLeftBottom = ValueAnimator.ofInt(left_bottom.getMeasuredWidth(), width / 3);
        animWidthLeftBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = left_bottom.getLayoutParams();
                layoutParams.width = val;
                left_bottom.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animHeightLeftBottom = ValueAnimator.ofInt(left_bottom.getMeasuredHeight(), width / 3);
        animHeightLeftBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = left_bottom.getLayoutParams();
                layoutParams.height= val;
                left_bottom.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animWidthBottom = ValueAnimator.ofInt(bottom.getMeasuredWidth(), width / 3);
        animWidthBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = bottom.getLayoutParams();
                layoutParams.width = val;
                bottom.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animHeightBottom = ValueAnimator.ofInt(bottom.getMeasuredHeight(), width / 3);
        animHeightBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = bottom.getLayoutParams();
                layoutParams.height= val;
                bottom.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animWidthRightBottom = ValueAnimator.ofInt(right_bottom.getMeasuredWidth(), width / 3);
        animWidthRightBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = right_bottom.getLayoutParams();
                layoutParams.width = val;
                right_bottom.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator animHeightRightBottom = ValueAnimator.ofInt(right_bottom.getMeasuredHeight(), width / 3);
        animHeightRightBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = right_bottom.getLayoutParams();
                layoutParams.height= val;
                right_bottom.setLayoutParams(layoutParams);
            }
        });
        s.playTogether(objectAnimatorX, objectAnimatorY,
                        animWidth, animHeight,
                        animWidthLayoutTop, animHeightLayoutTop,
                        animWidthLeftTop, animHeightLeftTop,
                        animWidthTop, animHeightTop,
                        animWidthRightTop, animHeightRightTop,
                        animWidthMiddle, animHeightMiddle,
                        animWidthLayoutBottom, animHeightLayoutBottom,
                        animWidthLeftBottom, animHeightLeftBottom,
                        animWidthBottom, animHeightBottom,
                        animWidthRightBottom, animHeightRightBottom);
        s.setDuration(300);
        final ObjectAnimator objectAnimator = (ObjectAnimator) s.getChildAnimations().get(0);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isMoving = false;
            }
        });
    }


    boolean move(AnimatorSet s, int x, int y, int width, int height) {
        if (canMove()) {
            move_(s,x,y, width, height);
            return true;
        }
        return false;
    }

    void bringToFront(){
        layout.bringToFront();
    }

    public Color getColor(){
        return color;
    }

    public Number getNumber(){
        return number;
    }


    static int getNumber(Carte.Number number){
        Carte.Number numbers[] = Carte.Number.values();
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] == number)
                return i;
        }
        return -2;
    }

    void delete(){
        layout.removeAllViews();
        removeView(layout);
    }

    boolean isRecto(){
        return is_recto;
    }

    void setIsMoving(boolean isMoving){
        this.isMoving = isMoving;
    }

    static ArrayList<Pair<Carte.Color, Carte.Number>> random(){
        ArrayList<Pair<Carte.Color, Carte.Number>> ordered = new ArrayList<>();
        ArrayList<Pair<Carte.Color, Carte.Number>> unordered = new ArrayList<>();
        for (Carte.Color color : Carte.Color.values()) {
            for (Carte.Number number : Carte.Number.values())
                ordered.add(new Pair(color, number));
        }
        while (!ordered.isEmpty()){
            unordered.add(ordered.remove((int) (Math.random() * ordered.size())));
        }
        return unordered;

    }

    void setPosition(Position position){
        this.position = position;
    }
    Position getPosition(){
        return position;
    }

    static void setIsDoingEnd(boolean isDoingEnd){
        Carte.isDoingEnd = isDoingEnd;
    }

    static boolean getIsDoingEnd(){
        return isDoingEnd;
    }
    abstract void removeView(View v);
    abstract void onClickListener(View v);
    abstract void onLongClickListener(View v);
}