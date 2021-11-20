package com.example.myapplication;

import android.animation.AnimatorSet;
import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

public abstract class Column{
    private ArrayList<Carte>[] column;
    private int rectoLength[];
    private int x[];
    private int y[];
    private int width;
    private int height;
    private int deltaHeight;
    private int length;
    private LinearLayout end[];
    private Context context;

    Column(int length, int x[], int y[], int width, int height, int deltaHeight, Context context){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.deltaHeight = deltaHeight;
        this.context = context;
        this.length = length;
        this.column = new ArrayList[length];
        this.rectoLength = new int[length];
        this.end = new LinearLayout[length];
        for (int i = 0; i < length; i++){
            rectoLength[i] = 0;
            column[i] = new ArrayList<>();
            this.end[i] = new LinearLayout(context);
            end[i].setX(x[i]);
            end[i].setY(y[i]);
            end[i].setBackgroundResource(R.drawable.carte_vide);
            end[i].setAlpha((float) 0.2);
            addContentView(end[i], new LinearLayout.LayoutParams(width, height));
        }
    }

    abstract void addContentView(View v, ViewGroup.LayoutParams params);
    abstract Carte newCarte(Carte.Color color, Carte.Number number);

    synchronized boolean canMoveColumn(ArrayList<Carte> column, int i, int length){
        for (int k = i; k < length; k++){
            if (!column.get(k).canMove()){
                for (int j = i; j < k; j++){
                    column.get(j).setIsMoving(false);
                }
                return false;
            }
        }
        return true;
    }

    abstract int searchColumn(Carte carte, ArrayList<Carte> column[], int length);

    boolean add(AnimatorSet s,Carte carte){
        int i = searchColumn(carte, column, length);
        if (i == -1)
            return  false;
        carte.bringToFront();
        carte.move(s, x[i], column[i].size() * deltaHeight + y[i], width, height);
        column[i].add(carte);
        rectoLength[i]++;
        return true;
    }

    void add(Carte carte, int i, boolean is_recto){
        if (is_recto) {
            carte.setRectoLayout();
            rectoLength[i]++;
        }
        else
            carte.setVersoLayout();
        carte.bringToFront();
        carte.setX(x[i]);
        carte.setY(column[i].size() * deltaHeight + y[i]);
        column[i].add(carte);
    }

    Pair<Integer, Integer> position(Carte carte){
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < column[i].size(); j++)
                if (column[i].get(j) == carte)
                    return new Pair<>(i, j);
        }
        return null;
    }

    boolean moveColumn(AnimatorSet s, Carte carte){
        int c = searchColumn(carte, column, length);
        if (c == -1)
            return false;
        Pair<Integer, Integer> pair = position(carte);
        if (pair == null)
            return false;
        final int i = pair.first;
        int j = pair.second;
        int length = column[i].size();
        if (canMoveColumn(column[i], j, length)) {
            for (int k = j; k < length; k++) {
                Carte card = column[i].get(j);
                card.bringToFront();
                rectoLength[i]--;
                rectoLength[c]++;
                card.move_(s, x[c], column[c].size() * deltaHeight + y[c], width, height);
                column[i].remove(card);
                column[c].add(card);
            }
            return true;
        }
        return false;
    }

    void remove(Carte carte){
        int i = position(carte).first;
        column[i].remove(carte);
        rectoLength[i]--;
    }

    void returnCard(){
        for (int i = 0; i < length; i++) {
            if (!column[i].isEmpty() && rectoLength[i] == 0) {
                column[i].get(column[i].size() - 1).setRectoLayout();
                rectoLength[i]++;
            }
        }
    }

    boolean isLast(Carte carte){
        Pair<Integer, Integer> pair = position(carte);
        if (pair == null)
            return false;
        return pair.second == column[pair.first].size() - 1;
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
        for (int i = 0; i < length; i++) {
            delete(column[i]);
            rectoLength[i] = 0;
        }
    }

    void delete(ArrayList<Carte> cartes){
        while (!cartes.isEmpty())
            cartes.remove(0).delete();
    }


    ArrayList<Carte>[] getColumn(){
        return column;
    }

    int getLength(){
        return length;
    }

    int[] getRectoLength(){
        return rectoLength;
    }

    boolean doEnd(AnimatorSet s, Pair<Carte.Color, Carte.Number> pair, End end){
        for (int i = 0; i < length; i++){
            if (column[i].size() > 0) {
                Carte carte = column[i].get(column[i].size() - 1);
                if (carte.getColor() == pair.first && carte.getNumber() == pair.second) {
                    column[i].remove(carte);
                    rectoLength[i]--;
                    end.addCarte(s,carte);
                    carte.setPosition(Carte.Position.End);
                    return true;
                }
            }
        }
        return false;
    }

    boolean isOrdered(Carte carte){
        Pair<Integer, Integer> pair = position(carte);
        if (pair == null)
            return false;
        for (int i = pair.second; i < column[pair.first].size() - 1; i++){
            Carte c1 = column[pair.first].get(i);
            Carte c2 = column[pair.first].get(i+1);
            if (c1.getColor() == c2.getColor() || Carte.getNumber(c1.getNumber()) != Carte.getNumber(c2.getNumber()) + 1)
                return false;
        }
        return true;
    }


}
