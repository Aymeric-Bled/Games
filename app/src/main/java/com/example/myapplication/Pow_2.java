package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

public class Pow_2 extends AppCompatActivity {
    private Button main;
    private Button score;
    private Button new_;
    private GridLayout grille;
    private Table tab;
    private int tab_number[][] = new int[4][4];
    private int tab_number2[][] = new int[4][4];
    private int s = 0;
    private int background_color[] = {Color.rgb(202, 192, 181), Color.rgb(240, 226, 220), Color.rgb(237, 224, 200), Color.rgb(243, 174, 124), Color.rgb(247, 146, 102), Color.rgb(242,118, 95), Color.rgb(248, 91, 61), Color.rgb(238, 203, 116), Color.rgb(238, 201, 99), Color.rgb(239, 197, 85), Color.rgb(238, 193, 66), Color.rgb(238, 191, 47), Color.rgb(240, 101, 110), Color.rgb(230, 73, 88), Color.rgb(226, 65, 60), Color.rgb(114, 177, 214), Color.rgb(95, 158, 226), Color.rgb(3, 122, 192)};
    private int text_color[] = {Color.rgb(119, 110, 101), Color.rgb(119, 110, 101), Color.rgb(119, 110, 101), Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};




    void create_2048(){
        Button b;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w = (metrics.widthPixels - 5)/ 4;
        grille= findViewById(R.id.grille);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,w);
        params.setMargins(1,1,1,1);
        tab = new Table(grille, 4, 4, this, params, w);
        for (int i=0; i < 4; i++)
            for (int j=0; j<4; j++) {
                b = tab.getButton(i,j);
                b.setBackgroundColor(background_color[0]);
                b.setTextColor(text_color[0]);
                b.setTextSize(w/8);
            }
    }

    void spawn(){
        int free[] = new int[16];
        int length = 0;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (tab_number[i][j] == 0)
                    free[length++] = 4*i+j;

                if (length != 0){
                    int p = free[(int) (Math.random() * length)];
                    Button b = tab.getButton(p/4, p%4);
                    b.setText("2");
                    tab_number[p/4][p%4] = 1;
                    b.setBackgroundColor(background_color[1]);
                }
    }



    void copy(int t1[][], int t2[][]){
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++){
                t1[i][j] = t2[i][j];
            }
    }

   void move(final Button button){
        button.setOnTouchListener(new OnSwipeTouchListener(Pow_2.this) {
           public void onSwipeTop() {
               swipe_top();
               if (update()) {
                   spawn();
                   copy(tab_number2, tab_number);
               }
           }
           public void onSwipeRight() {
               swipe_right();
               if (update()) {
                   spawn();
                   copy(tab_number2, tab_number);
               }
           }
           public void onSwipeLeft() {
               swipe_left();
               if (update()) {
                   spawn();
                   copy(tab_number2, tab_number);
               }
           }
           public void onSwipeBottom() {
               swipe_bottom();
               if (update()) {
                   spawn();
                   copy(tab_number2, tab_number);
               }
           }

       });
    }
    int [] simplification(int t[]){
        int i = 0;
        int j = 0;
        int result[] = {0,0,0,0};
        while (i<4) {
            if (t[i] == 0){
                i++;
            }
            else {
                int k = i + 1;
                while (k < 4 && t[k] == 0)
                    k++;
                if (k < 4 && t[i] == t[k]) {
                    result[j++] = t[i] + 1;
                    s += (int) Math.pow(2, t[i]);
                    i = k + 1;
                }
                else {
                    result[j++] = t[i];
                    i = k;
                }
            }
        }
        return result;
    }

    boolean update(){
        boolean move = false;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w = (metrics.widthPixels - 5)/ 4;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++){
                Button button = tab.getButton(i, j);
                if (tab_number[i][j] != tab_number2[i][j]) {
                    move = true;
                }
                    if (tab_number[i][j] == 0) {
                        button.setText("");
                        button.setTextSize(w/8);
                    } else {
                        button.setText((int) Math.pow(2, tab_number[i][j]) + "");
                        button.setTextSize(w/8);
                        if (tab_number[i][j] >= 10){
                            button.setTextSize((3*w) / 32);
                        }
                        if (tab_number[i][j] >= 14){
                            button.setTextSize((3*w) / 40);
                        }
                    }
                    button.setBackgroundColor(background_color[tab_number[i][j]]);
                    button.setTextColor(text_color[tab_number[i][j]]);
            }
        score.setText("Score: " + s);
        return move;
    }
    void swipe_left(){
        for (int i = 0; i < 4; i++){
            int t[] = simplification(tab_number[i]);
            for (int j = 0; j < 4; j++){
                tab_number[i][j] = t[j];
            }
        }
    }
    void reverse(int t[], int n){
        for (int i = 0; i < n / 2; i++){
            int x = t[i];
            t[i] = t[n-1-i];
            t[n-1-i] = x;
        }
    }
    void swipe_right(){
        for (int i = 0; i < 4; i++){
            int t[] = tab_number[i];
            reverse(t, 4);
            t = simplification(t);
            reverse(t, 4);
            for (int j = 0; j < 4; j++){
                tab_number[i][j] = t[j];
            }
        }
    }
    void swipe_top(){
        for (int i = 0; i < 4; i++){
            int t[] = new int[4];
            for (int j = 0; j < 4; j++){
                t[j] = tab_number[j][i];
            }
            t = simplification(t);
            for (int j = 0; j < 4; j++){
                tab_number[j][i] = t[j];
            }
        }
    }

    void swipe_bottom(){
        for (int i = 0; i < 4; i++){
            int t[] = new int[4];
            for (int j = 0; j < 4; j++){
                t[j] = tab_number[j][i];
            }
            reverse(t, 4);
            t = simplification(t);
            reverse(t, 4);
            for (int j = 0; j < 4; j++){
                tab_number[j][i] = t[j];
            }
        }
    }
    void _new(){
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++){
                Button b = tab.getButton(i, j);
                b.setText("");
                tab_number[i][j] = 0;
                tab_number2[i][j] = 0;
                b.setTextColor(text_color[0]);
                b.setBackgroundColor(background_color[0]);
            }
        spawn();
        spawn();
        s = 0;
        score.setText("Score: 0");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pow_2);
        this.main = findViewById(R.id.main);
        this.grille = findViewById(R.id.grille);
        this.score = findViewById(R.id.score);
        this.new_ = findViewById(R.id.new_);
        create_2048();
        spawn();
        spawn();

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();
            }
        });
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                move(tab.getButton(i, j));
        new_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _new();
            }
        });
    }

}
