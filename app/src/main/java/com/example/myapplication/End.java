package com.example.myapplication;

import android.animation.AnimatorSet;
import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;

public abstract class End{
    private ArrayList<Carte> carreau = new ArrayList<>();
    private ArrayList<Carte> coeur = new ArrayList<>();
    private ArrayList<Carte> pique = new ArrayList<>();
    private ArrayList<Carte> trefle = new ArrayList<>();
    private LinearLayout endCarreau;
    private LinearLayout endCoeur;
    private LinearLayout endPique;
    private LinearLayout endTrefle;
    private int x_carreau;
    private int y_carreau;
    private int x_coeur;
    private int y_coeur;
    private int x_pique;
    private int y_pique;
    private int x_trefle;
    private int y_trefle;
    private int width;
    private int height;
    private Context context;

    End(int x_carreau, int y_carreau, int x_coeur, int y_coeur, int x_pique, int y_pique, int x_trefle, int y_trefle, int width, int height, Context context){
        this.x_carreau = x_carreau;
        this.y_carreau = y_carreau;
        this.x_coeur = x_coeur;
        this.y_coeur = y_coeur;
        this.x_pique = x_pique;
        this.y_pique = y_pique;
        this.x_trefle = x_trefle;
        this.y_trefle = y_trefle;
        this.width = width;
        this.height = height;
        this.context = context;
        this.endCarreau = new LinearLayout(context);
        endCarreau.setX(x_carreau);
        endCarreau.setY(y_carreau);
        endCarreau.setBackgroundResource(R.drawable.carte_vide);
        endCarreau.setAlpha((float) 0.2);
        addContentView(endCarreau, new LinearLayout.LayoutParams(width, height));
        this.endCoeur = new LinearLayout(context);
        endCoeur.setX(x_coeur);
        endCoeur.setY(y_coeur);
        endCoeur.setBackgroundResource(R.drawable.carte_vide);
        endCoeur.setAlpha((float) 0.2);
        addContentView(endCoeur, new FrameLayout.LayoutParams(width, height));
        this.endPique = new LinearLayout(context);
        endPique.setX(x_pique);
        endPique.setY(y_pique);
        endPique.setBackgroundResource(R.drawable.carte_vide);
        endPique.setAlpha((float) 0.2);
        addContentView(endPique, new LinearLayout.LayoutParams(width, height));
        this.endTrefle = new LinearLayout(context);
        endTrefle.setX(x_trefle);
        endTrefle.setY(y_trefle);
        endTrefle.setBackgroundResource(R.drawable.carte_vide);
        endTrefle.setAlpha((float) 0.2);
        addContentView(endTrefle, new LinearLayout.LayoutParams(width, height));

    }

    abstract void addContentView(View v, ViewGroup.LayoutParams params);

    boolean addCarte(AnimatorSet s, Carte carte) {
        Carte.Number number[] = Carte.Number.values();
        int i = 0;
        switch (carte.getColor()) {
            case Carreau:
                while (i < number.length && number[i] != carte.getNumber())
                    i++;
                if (i >= number.length)
                    return false;
                if (i == carreau.size()) {
                    carreau.add(carte);
                    carte.bringToFront();
                    //carte.setWidth(width);
                    //carte.setHeight(height);
                    carte.move(s, x_carreau, y_carreau, width, height);
                    return true;
                }
                break;
            case Coeur:
                while (i < number.length && number[i] != carte.getNumber())
                    i++;
                if (i >= number.length)
                    return false;
                if (i == coeur.size()) {
                    coeur.add(carte);
                    carte.bringToFront();
                    //carte.setWidth(width);
                    //carte.setHeight(height);
                    carte.move(s, x_coeur, y_coeur, width, height);
                    return true;
                }
                break;
            case Pique:
                while (i < number.length && number[i] != carte.getNumber())
                    i++;
                if (i >= number.length)
                    return false;
                if (i == pique.size()) {
                    pique.add(carte);
                    carte.bringToFront();
                    //carte.setWidth(width);
                    //carte.setHeight(height);
                    carte.move(s, x_pique, y_pique, width, height);
                    return true;
                }
                break;
            case Trefle:
                while (i < number.length && number[i] != carte.getNumber())
                    i++;
                if (i >= number.length)
                    return false;
                if (i == trefle.size()) {
                    trefle.add(carte);
                    carte.bringToFront();
                    //carte.setWidth(width);
                    //carte.setHeight(height);
                    carte.move(s, x_trefle, y_trefle, width, height);
                    return true;
                }
                break;
            default:
                break;

        }
        return false;
    }


    void remove(Carte carte){
        switch (carte.getColor()){
            case Carreau:
                carreau.remove(carte);
                break;
            case Coeur:
                coeur.remove(carte);
                break;
            case Pique:
                pique.remove(carte);
                break;
            case Trefle:
                trefle.remove(carte);
                break;
        }
    }

    boolean end(){
        int n = Carte.Number.values().length;
        return carreau.size() == n && coeur.size() == n && pique.size() == n && trefle.size() == n;
    }

    void _new(){
        delete(carreau);
        delete(coeur);
        delete(pique);
        delete(trefle);
    }

    void delete(ArrayList<Carte> cartes){
        while (!cartes.isEmpty())
            cartes.remove(0).delete();
    }


    Pair<Carte.Color, Carte.Number> doEnd(){
        ArrayList<Carte> min = carreau;
        int ca = carreau.size();
        int co = coeur.size();
        int pi = pique.size();
        int tr = trefle.size();
        int n = ca;
        Carte.Color color = Carte.Color.Carreau;
        if (co < n){
            min = coeur;
            color = Carte.Color.Coeur;
            n = co;
        }
        if (pi < n){
            min = pique;
            color = Carte.Color.Pique;
            n = pi;
        }
        if (tr < n){
            min = trefle;
            color = Carte.Color.Trefle;
            n = tr;
        }
        if (n > 0) {
            Carte carte = min.get(n - 1);
            return new Pair<>(carte.getColor(), Carte.Number.values()[Carte.getNumber(carte.getNumber()) + 1]);
        }
        return new Pair<>(color, Carte.Number.As);
    }



}
