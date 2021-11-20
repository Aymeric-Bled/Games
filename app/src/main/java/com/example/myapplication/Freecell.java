package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class Freecell extends AppCompatActivity {
    private Button main;
    private Button _new;
    private Button _end;
    private FreecellColumn column;
    private FreecellEnd end;
    private FreecellDepot depot;
    private int columnCount = 8;

    private class FreecellCarte extends Carte{

        FreecellCarte(Color color, Number number, Context context, int width, int height) {
            super(color,number,context, width, height);
            addContentView(getLayout(), getLayout().getLayoutParams());
        }

        @Override
        void removeView(View v) {
            v.setVisibility(View.GONE);
        }

        void onClickListener(View v){
            final FreecellCarte carte = this;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimatorSet s = new AnimatorSet();
                    switch (carte.getPosition()) {
                        case Column:
                            if (column.isLast(carte) && end.addCarte(s, carte)) {
                                final ObjectAnimator objectAnimator = (ObjectAnimator) s.getChildAnimations().get(0);
                                objectAnimator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        if (column.end()) {
                                            _end.setAlpha(1);
                                            _end.setClickable(true);
                                        }
                                        end();
                                    }
                                });
                                s.start();
                                column.remove(carte);
                                carte.setPosition(Position.End);
                                break;
                            }
                            if (column.isOrdered(carte) && column.moveColumn(s, carte)) {
                                final ObjectAnimator objectAnimator = (ObjectAnimator) s.getChildAnimations().get(0);
                                objectAnimator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        if (column.end()) {
                                            _end.setAlpha(1);
                                            _end.setClickable(true);
                                        }
                                        end();
                                    }
                                });
                                s.start();
                                break;
                            }
                            break;
                        case Depot:
                            if (end.addCarte(s, carte)) {
                                final ObjectAnimator objectAnimator = (ObjectAnimator) s.getChildAnimations().get(0);
                                objectAnimator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        if (column.end()) {
                                            _end.setAlpha(1);
                                            _end.setClickable(true);
                                        }
                                        end();
                                    }
                                });
                                s.start();
                                depot.remove(carte);
                                carte.setPosition(Position.End);
                                break;
                            }
                        case End:
                            if (column.add(s,carte)){
                                final ObjectAnimator objectAnimator = (ObjectAnimator) s.getChildAnimations().get(0);
                                objectAnimator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        if (column.end()) {
                                            _end.setAlpha(1);
                                            _end.setClickable(true);
                                        }
                                        end();
                                    }
                                });
                                s.start();
                                if (carte.getPosition() == Position.End)
                                    end.remove(carte);
                                else
                                    depot.remove(carte);
                                carte.setPosition(Position.Column);
                                break;
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        @Override
        void onLongClickListener(View v) {
            final FreecellCarte carte = this;
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AnimatorSet s = new AnimatorSet();
                    switch (carte.getPosition()) {
                        case Column:
                            if (column.isLast(carte) && depot.addCarte(s, carte)) {
                                final ObjectAnimator objectAnimator = (ObjectAnimator) s.getChildAnimations().get(0);
                                objectAnimator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        end();
                                    }
                                });
                                s.start();
                                column.remove(carte);
                                carte.setPosition(Position.Depot);
                                break;
                            }
                            break;
                        default:
                            break;
                    }

                    return true;
                }
            });
        }
    }

    private class FreecellColumn extends Column{
        FreecellColumn(int[] x, int[] y, int width, int height, int deltaHeight, Context context) {
            super(columnCount, x, y, width, height, deltaHeight, context);
        }

        @Override
        void addContentView(View v, ViewGroup.LayoutParams params) {
            Freecell.this.addContentView(v,params);
        }

        @Override
        Carte newCarte(Carte.Color color, Carte.Number number) {
            return new FreecellCarte(color, number, getContext(), getWidth(), getHeight());
        }

        @Override
        int searchColumn(Carte carte, ArrayList<Carte>[] column, int length) {
            int co = -1;
            for (int i = 0; i < length; i++){
                if (column[i].isEmpty()){
                    if (co == -1)
                        co = i;
                }
                else {
                    Carte c = column[i].get(column[i].size() - 1);
                    if (c.getColor(c.getColor()) != carte.getColor(carte.getColor()) && Carte.getNumber(carte.getNumber()) + 1 == Carte.getNumber(c.getNumber()))
                        return i;
                }
            }
            return co;
        }

        boolean end(){
            for (int i = 0; i < getLength(); i++) {
                if (getColumn()[i].size() > 0 && !isOrdered(getColumn()[i].get(0)))
                    return false;
            }
            return true;
        }

    }



    private class FreecellEnd extends End{

        FreecellEnd(int x_carreau, int y_carreau, int x_coeur, int y_coeur, int x_pique, int y_pique, int x_trefle, int y_trefle, int width, int height, Context context) {
            super(x_carreau, y_carreau, x_coeur, y_coeur, x_pique, y_pique, x_trefle, y_trefle, width, height, context);
        }

        @Override
        void addContentView(View v, ViewGroup.LayoutParams params) {
            Freecell.this.addContentView(v,params);
        }
    }

    private class FreecellDepot extends Depot{

        FreecellDepot(int[] x, int[] y, int width, int height, Context context) {
            super(x, y, width, height, context);
        }

        @Override
        void addContentView(View v, ViewGroup.LayoutParams params) {
            Freecell.this.addContentView(v,params);
        }
    }


    void _new(){
        column._new();
        end._new();
        depot._new();
        ArrayList<Pair<Carte.Color, Carte.Number>> cartes = Carte.random();

        while (!cartes.isEmpty()) {
            for (int i = 0; i < columnCount && !cartes.isEmpty(); i++) {
                Pair<Carte.Color, Carte.Number> pair = cartes.remove(0);
                Carte carte = column.newCarte(pair.first, pair.second);
                ((FreecellCarte) carte).setPosition(Carte.Position.Column);
                column.add(carte, i, true);
            }
        }

        _end.setAlpha(0);
        _end.setClickable(false);
    }


    boolean end(){
        if (end.end()){
            _end.setAlpha(0);
            _end.setClickable(false);
            AlertDialog.Builder fin = new AlertDialog.Builder(this);
            fin.setTitle("Félicitations !!!");
            fin.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);
                    finish();

                }
            });
            fin.setPositiveButton("Nouvelle partie", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _new();
                }
            });
            fin.setNeutralButton("Fermer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            fin.show();
            return true;
        }
        return false;
    }

    void doEnd(){
        if (!end()) {
            Pair<Carte.Color, Carte.Number> pair = end.doEnd();
            AnimatorSet s = new AnimatorSet();
            if (column.doEnd(s, pair, end) || depot.doEnd(s, pair, end)) {
                final ObjectAnimator objectAnimator = (ObjectAnimator) s.getChildAnimations().get(0);
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        doEnd();
                    }
                });
                s.start();
            }
        }
        else{
            Carte.setIsDoingEnd(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freecell);
        this.main = findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(main);
                finish();
            }
        });
        this._end = findViewById(R.id.end);
        _end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doEnd();
            }
        });
        this._new = findViewById(R.id.new_);
        _new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _new();
            }
        });
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = main.getMinHeight();
        int x_depot[] = {5 * width / 9, 6 * width / 9, 7 * width / 9, 8 * width / 9};
        int y_depot[] = {height, height, height, height};
        this.depot = new FreecellDepot(x_depot,y_depot,width / 9, 3 * width / 18, this);
        this.end = new FreecellEnd(0, height,width / 9, height,2*width / 9, height,3*width / 9, height,width / 9, 3 * width / 18, this);
        height += width / 24;
        int x_column[] = {0, width / 8, 2 * width / 8, 3 * width / 8, 4 * width / 8, 5 * width / 8, 6 * width / 8, 7 * width / 8};
        int y_column[] = {height + 3 * width / 16, height + 3 * width / 16, height + 3 * width / 16, height + 3 * width / 16, height + 3 * width / 16, height + 3 * width / 16, height + 3 * width / 16, height + 3 * width / 16};
        this.column = new FreecellColumn(x_column, y_column, width / 8, 3 * width / 16, width / 15, this);
        _new();

    }
}