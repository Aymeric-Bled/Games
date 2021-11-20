package com.example.myapplication;

import android.animation.AnimatorSet;
import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

public abstract class Draw{
    private ArrayList<Carte> draw = new ArrayList<Carte>();
    private int x_draw;
    private int y_draw;
    private ArrayList<Carte> card = new ArrayList<Carte>();
    private int x_card;
    private int y_card;
    private LinearLayout endDraw;
    private LinearLayout endCard;
    private int width;
    private int height;
    private Context context;

    Draw(int x_draw, int y_draw, int x_card, int y_card, int width, int height, Context context){
        this.x_draw = x_draw;
        this.y_draw = y_draw;
        this.x_card = x_card;
        this.y_card = y_card;
        this.width = width;
        this.height = height;
        this.context = context;
        this.endDraw = new LinearLayout(context);
        endDraw.setX(x_draw);
        endDraw.setY(y_draw);
        endDraw.setBackgroundResource(R.drawable.carte_vide);
        endDraw.setAlpha((float) 0.2);
        addContentView(endDraw, new LinearLayout.LayoutParams(width, height));
        endDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        this.endCard = new LinearLayout(context);
        endCard.setX(x_card);
        endCard.setY(y_card);
        endCard.setBackgroundResource(R.drawable.carte_vide);
        endCard.setAlpha((float) 0.2);
        addContentView(endCard, new LinearLayout.LayoutParams(width, height));
    }

    abstract void addContentView(View v, ViewGroup.LayoutParams params);

    abstract Carte newCarte(Carte.Color color, Carte.Number number);

    void add(Carte carte){
        draw.add(carte);
        carte.setX(x_draw);
        carte.setY(y_draw);
        carte.setVersoLayout();
        carte.bringToFront();
    }

    void remove(Carte carte){
        card.remove(carte);
    }
    void reset(){
        while(!card.isEmpty()){
            Carte carte = card.remove(card.size() - 1);
            draw.add(carte);
            carte.setX(x_draw);
            carte.setY(y_draw);
            carte.setVersoLayout();
            carte.bringToFront();
        }
    }
    void drawCard(){
        if (isEmpty())
            return;
        Carte carte = draw.remove(draw.size() - 1);
        card.add(carte);
        carte.setX(x_card);
        carte.setY(y_card);
        carte.setRectoLayout();
    }
    boolean isInTopDraw(Carte carte){
        if (isEmpty())
            return  false;
        return carte == draw.get(draw.size() - 1);
    }
    boolean isCurrentCard(Carte carte){
        if (card.isEmpty())
            return false;
        return carte == card.get(card.size() - 1);
    }
    boolean isEmpty(){
        return draw.isEmpty();
    }

    Context getContext(){
        return context;
    }

    int getWidth(){
        return width;
    }

    int getHeight(){
        return height;
    }

    void _new(){
        delete(draw);
        delete(card);
    }

    void delete(ArrayList<Carte> cartes){
        while (!cartes.isEmpty())
            cartes.remove(0).delete();
    }

    boolean doEnd(AnimatorSet s, Pair<Carte.Color, Carte.Number> pair, End end){
        reset();
        if (draw.size() == 0)
            return false;
        drawCard();
        if (card.size() > 0) {
            Carte carte = card.get(card.size() - 1);
            while (!(carte.getColor() == pair.first && carte.getNumber() == pair.second) && draw.size() > 0) {
                drawCard();
                carte = card.get(card.size() - 1);
            }
            if (carte.getColor() == pair.first && carte.getNumber() == pair.second) {
                card.remove(carte);
                end.addCarte(s,carte);
                carte.setPosition(Carte.Position.End);
                return true;
            }
        }
        return false;
    }

}
