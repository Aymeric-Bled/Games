package com.example.myapplication;


import android.animation.AnimatorSet;
import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


abstract class Depot {


    private final Carte[] depot = new Carte[4];
    private LinearLayout[] layout = new LinearLayout[4];
    private int[] x;
    private int[] y;
    private int width;
    private int height;
    private Context context;

    Depot(int[] x, int[] y, int width, int height, Context context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.context = context;
        for (int i = 0; i < 4; i++) {
            this.layout[i] = new LinearLayout(context);
            layout[i].setX(x[i]);
            layout[i].setY(y[i]);
            layout[i].setBackgroundResource(R.drawable.carte_vide);
            layout[i].setAlpha((float) 0.2);
            addContentView(layout[i], new LinearLayout.LayoutParams(width, height));
            depot[i] = null;
        }

    }

    abstract void addContentView(View v, ViewGroup.LayoutParams params);

    boolean addCarte(AnimatorSet s, Carte carte) {
        int i = 0;
        while(i < 4 && depot[i] != null)
            i++;
        if (i >= 4)
            return false;
        depot[i] = carte;
        carte.bringToFront();
        carte.move(s, x[i], y[i], width, height);
        return true;
    }


    void remove(Carte carte) {
        for (int i = 0; i < 4; i++){
            if (depot[i] == carte) {
                depot[i] = null;
                return;
            }
        }
    }



    void _new() {
        for (int i = 0; i < 4; i++) {
            if (depot[i] != null){
                depot[i].delete();
            }
        }
    }

    boolean doEnd(AnimatorSet s, Pair<Carte.Color, Carte.Number> pair, End end) {
        for (int i = 0; i < 4; i++){
            Carte carte = depot[i];
            if (carte != null && carte.getColor() == pair.first && carte.getNumber() == pair.second){
                depot[i] = null;
                end.addCarte(s,carte);
                carte.setPosition(Carte.Position.End);
                return true;
            }
        }
        return false;
    }


}
