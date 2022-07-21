package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class Solitaire extends AppCompatActivity {
    private ImageView main;
    private ImageView _new;
    private Button _end;
    private SolitaireDraw draw;
    private SolitaireEnd end;
    private SolitaireColumn column;
    private int columnCount = 7;
    private boolean firstStart = true;


    private class SolitaireCarte extends Carte{

        SolitaireCarte(Color color, Number number, Context context, int width, int height) {
            super(color,number,context, width, height);
            addContentView(getLayout(), getLayout().getLayoutParams());
        }

        @Override
        void removeView(View v) {
            v.setVisibility(View.GONE);
        }

        void onClickListener(View v){
            final SolitaireCarte carte = this;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Carte.getIsDoingEnd()) {
                        AnimatorSet s = new AnimatorSet();
                        switch (carte.getPosition()) {
                            case Draw:
                                if (draw.isInTopDraw(carte)) {
                                    draw.drawCard();
                                    break;
                                }
                                if (draw.isCurrentCard(carte)) {
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
                                        draw.remove(carte);
                                        carte.setPosition(Position.End);
                                        break;
                                    }
                                    if (column.add(s, carte)) {
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
                                        draw.remove(carte);
                                        carte.setPosition(Position.Column);
                                        break;
                                    }
                                }
                                break;
                            case Column:
                                if (carte.isRecto()) {
                                    if (column.isLast(carte) && end.addCarte(s, carte)) {
                                        final ObjectAnimator objectAnimator = (ObjectAnimator) s.getChildAnimations().get(0);
                                        objectAnimator.addListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                column.returnCard();
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
                                    if (column.moveColumn(s, carte)) {
                                        final ObjectAnimator objectAnimator = (ObjectAnimator) s.getChildAnimations().get(0);
                                        objectAnimator.addListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                column.returnCard();
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
                                }
                                break;
                            case End:
                                if (column.add(s, carte)) {
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
                                    end.remove(carte);
                                    carte.setPosition(Position.Column);
                                    break;
                                }
                                break;
                        }
                    }
                }
            });
        }

        @Override
        void onLongClickListener(View v) {

        }

    }

    private class SolitaireColumn extends Column{
        SolitaireColumn(int[] x, int[] y, int width, int height, int deltaHeight, Context context) {
            super(columnCount, x, y, width, height, deltaHeight, context);
        }

        @Override
        void addContentView(View v, ViewGroup.LayoutParams params) {
            Solitaire.this.addContentView(v,params);
        }

        @Override
        Carte newCarte(Carte.Color color, Carte.Number number) {
            return new SolitaireCarte(color, number, getContext(), getWidth(), getHeight());
        }
        @Override
        int searchColumn(Carte carte, ArrayList<Carte> column[], int length){
            for (int i = 0; i < length; i++){
                if (column[i].isEmpty()){
                    if (carte.getNumber() == Carte.Number.Roi)
                        return i;
                }
                else {
                    Carte c = column[i].get(column[i].size() - 1);
                    if (c.getColor(c.getColor()) != carte.getColor(carte.getColor()) && Carte.getNumber(carte.getNumber()) + 1 == Carte.getNumber(c.getNumber()))
                        return i;
                }
            }
            return -1;
        }

        boolean end(){
            for (int i = 0; i < getLength(); i++) {
                if (getColumn()[i].size() != getRectoLength()[i])
                    return false;
            }
            return true;
        }
    }

    private class SolitaireDraw extends Draw{

        SolitaireDraw(int x_draw, int y_draw, int x_card, int y_card, int width, int height, Context context) {
            super(x_draw, y_draw, x_card, y_card, width, height, context);
        }

        @Override
        void addContentView(View v, ViewGroup.LayoutParams params) {
            Solitaire.this.addContentView(v,params);
        }

        @Override
        Carte newCarte(Carte.Color color, Carte.Number number) {
            return new SolitaireCarte(color, number, getContext(), getWidth(), getHeight());
        }
    }

    private class SolitaireEnd extends End{

        SolitaireEnd(int x_carreau, int y_carreau, int x_coeur, int y_coeur, int x_pique, int y_pique, int x_trefle, int y_trefle, int width, int height, Context context) {
            super(x_carreau, y_carreau, x_coeur, y_coeur, x_pique, y_pique, x_trefle, y_trefle, width, height, context);
        }

        @Override
        void addContentView(View v, ViewGroup.LayoutParams params) {
            Solitaire.this.addContentView(v,params);
        }
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
            if (column.doEnd(s, pair, end) || draw.doEnd(s, pair, end)) {
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

    void _new(){
        draw._new();
        end._new();
        column._new();
        ArrayList<Pair<Carte.Color, Carte.Number>> cartes = Carte.random();
        for (int i = 0; i < columnCount; i++){
            for (int j = 0; j < i; j++){
                Pair<Carte.Color, Carte.Number> pair = cartes.remove(0);
                Carte carte = column.newCarte(pair.first, pair.second);
                carte.setPosition(Carte.Position.Column);
                column.add(carte,i,false);
            }
            Pair<Carte.Color, Carte.Number> pair = cartes.remove(0);
            Carte carte = column.newCarte(pair.first, pair.second);
            ((SolitaireCarte) carte).setPosition(Carte.Position.Column);
            column.add(carte,i,true);
        }
        while(!cartes.isEmpty()) {
            Pair<Carte.Color, Carte.Number> pair = cartes.remove(0);
            Carte carte = draw.newCarte(pair.first, pair.second);
            ((SolitaireCarte) carte).setPosition(Carte.Position.Draw);
            draw.add(carte);
        }

        _end.setAlpha(0);
        _end.setClickable(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solitaire);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                _end.setAlpha(0);
                _end.setClickable(false);
                Carte.setIsDoingEnd(true);
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
    }

    public synchronized boolean isFirstStart() {
        if (firstStart){
            firstStart = false;
            return true;
        }
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirstStart()) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = main.getHeight();
            this.draw = new SolitaireDraw(6*width / 7,height,5 * width / 7,height,width / 7, 3 * width / 14, this);
            this.end = new SolitaireEnd(0, height,width / 7, height,2*width / 7, height,3*width / 7, height,width / 7, 3 * width / 14, this);
            height += width / 21;
            int x[] = {0, width / 7, 2 * width / 7, 3 * width / 7, 4 * width / 7, 5 * width / 7, 6 * width / 7};
            int y[] = {height + 3 * width / 14, height + 3 * width / 14, height + 3 * width / 14, height + 3 * width / 14, height + 3 * width / 14, height + 3 * width / 14, height + 3 * width / 14};
            this.column = new SolitaireColumn(x, y, width / 7, 3 * width / 14, width / 21, this);
            _new();

        }
    }
}