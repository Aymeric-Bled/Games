package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ToggleButton;

import java.util.ArrayList;


public class Jeu_de_la_vie extends AppCompatActivity {
    private ImageView main;
    private ImageView lecture;
    private ImageView del;
    private ImageView alea;
    private Table tab;
    private int taille = 50;
    private int color[][]= new int[taille][taille];
    private int copy[][]=new int[taille][taille];
    private int height;
    private boolean play = false;


    private AnimatorSet s = new AnimatorSet();


    void create_layout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
        params.setMargins(1,1,1,1);
        tab = new Table((GridLayout) findViewById(R.id.grille) , taille, taille, this, params, 100);
        Button b;
        for (int i=0; i<taille;i++) {
            for (int j = 0; j < taille; j++) {
                b = tab.getButton(i,j);
                b.setBackgroundColor(Color.WHITE);
                color[i][j] = 0;
            }
        }
    }

    int num_button(Button b){
        for (int i=0; i<taille; i++) {
            for (int j=0; j<taille;j++){
                if (tab.getButton(i,j)==b){
                    return taille*i+j;
                }
            }
        }
        return -1;
    }


    void modify_color(final Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!play) {
                    if (((ColorDrawable) button.getBackground()).getColor() == Color.BLACK) {
                        button.setBackgroundColor(Color.WHITE);
                        int n= num_button(button);
                        color[n/taille][n%taille]=0;
                    } else {
                        button.setBackgroundColor(Color.BLACK);
                        int n= num_button(button);
                        color[n/taille][n%taille]=0;
                    }
                }
            }
        });
    }
    void initialise(){
        Button b;
        for(int i=0; i< taille; i++){
            for(int j=0; j<taille; j++){
                b=tab.getButton(i,j);
                if (((ColorDrawable)b.getBackground()).getColor()== Color.WHITE) {
                    color[i][j] = 0;
                }
                else{
                    color[i][j]=1;
                }
            }
        }
    }
    int nb_voisins(int i,int j){
        int voisin=0;
        for (int x=-1;x<2;x++){
            for(int y=-1;y<2;y++){
                if(i+x>=0 && i+x<taille&& j+y>=0 && j+y<taille && (x!=0||y!=0) &&color[i+x][j+y]==1) voisin++;
            }
        }
        return voisin;
    }

    void jeu_de_la_vie() {
        s = new AnimatorSet();
        Button b;
        final ArrayList<ObjectAnimator> anim = new ArrayList<>();
        int voisin = 0;
        initialise();
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                voisin = nb_voisins(i, j);
                if (voisin == 2) {
                    copy[i][j] = color[i][j];
                } else {
                    if (voisin == 3) {
                        copy[i][j] = 1;
                    } else {
                        copy[i][j] = 0;
                    }
                }

            }

        }
        for (int i = 0; i < taille; i++)
            for (int j = 0; j < taille; j++) {
                if (color[i][j] != copy[i][j]) {
                    color[i][j] = copy[i][j];
                    b = tab.getButton(i, j);
                    if (copy[i][j] == 1) {
                        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(b, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.black), ContextCompat.getColor(this, R.color.black));
                        objectAnimator.setDuration(200);
                        if (anim.isEmpty())
                            s.play(objectAnimator);
                        else
                            s.play(objectAnimator).with(anim.get(0));
                        anim.add(objectAnimator);
                    } else {
                        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(b, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.white), ContextCompat.getColor(this, R.color.white));
                        objectAnimator.setDuration(200);
                        if (anim.isEmpty())
                            s.play(objectAnimator);
                        else
                            s.play(objectAnimator).with(anim.get(0));
                        anim.add(objectAnimator);
                    }
                }
            }

        if (!anim.isEmpty()) {
            anim.get(0).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (play){
                        jeu_de_la_vie();
                    }
                }
            });
        }
        else{
            lecture.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            play = false;
        }
        s.start();
    }

    void del_color(Button button){
        button.setBackgroundColor(Color.WHITE);
    }

    void alea(){
        Button b;
        for (int i = 0; i < taille; i++)
            for (int j = 0; j < taille; j++){
                b=tab.getButton(i,j);
                if (Math.random() > 0.5){
                    color[i][j] = 1;
                    b.setBackgroundColor(Color.BLACK);
                }
                else{
                    color[i][j] = 0;
                    b.setBackgroundColor(Color.WHITE);
                }
            }
    }

    public synchronized boolean canPlay(){
        if (play){
            return false;
        }
        play = true;
        lecture.setImageResource(R.drawable.ic_baseline_pause_24);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu_de_la_vie);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        create_layout();
        this.main = findViewById(R.id.main);
        this.lecture= findViewById(R.id.lecture);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(main);
                finish();
            }
        });
        for (int i=0; i<taille; i++)
            for (int j=0; j<taille; j++)
                modify_color(tab.getButton(i,j));


        this.del = findViewById(R.id.del);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!play) {
                    for (int i=0; i<taille; i++)
                        for (int j=0; j<taille; j++)
                            del_color(tab.getButton(i,j));
                }
            }
        });

        lecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canPlay()){
                    jeu_de_la_vie();
                }
                else{
                    s.pause();
                    lecture.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    play = false;
                }
            }
        });

        this.alea = findViewById(R.id.alea);
        alea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!play)
                    alea();
            }
        });


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        height = findViewById(android.R.id.content).getHeight() - main.getHeight();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ScrollView scrollView = findViewById(R.id.scroll);
        ConstraintLayout.LayoutParams p1 = (ConstraintLayout.LayoutParams) scrollView.getLayoutParams();
        ConstraintLayout.LayoutParams p = new ConstraintLayout.LayoutParams(metrics.widthPixels, height);
        p.topToBottom = p1.topToBottom;
        p.bottomToTop = p1.bottomToTop;
        scrollView.setLayoutParams(p);
    }

}


