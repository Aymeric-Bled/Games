package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class Snake extends AppCompatActivity {
    private Button main;
    private Button new_;
    private Button scoreButton;
    private Table tab;
    private int taille = 20;
    private int value[][] = new int[taille][taille];
    private enum Direction{RIGHT, LEFT, TOP, BOTTOM}
    private Direction direction;
    private Direction directionExpected;
    private ArrayList snake;
    private int ind = 0;
    private int score;
    private int length;
    private boolean fin = true;

    void create_layout(){
        Button b;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w = metrics.widthPixels/ taille;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,w);
        tab = new Table((GridLayout) findViewById(R.id.grille), taille, taille, this, params, w);
        for (int i=0; i < taille; i++)
            for (int j=0; j<taille; j++) {
                b = tab.getButton(i,j);
                b.setBackgroundColor(getColor(R.color.transgrey));
            }
    }


    void new_(){
        direction = Direction.RIGHT;
        directionExpected = Direction.RIGHT;
        snake = new ArrayList();
        for (int i = 0; i < taille * taille; i++){
            snake.add(0);
        }
        int n = taille/2;
        Button b;
        for (int i = 0; i < taille; i++)
            for (int j = 0; j < taille; j++){
                value[i][j] = 0;
                b=tab.getButton(i,j);
                b.setBackgroundColor(getColor(R.color.transgrey));
            }
        for (int i = 4; i >= 0; i--) {
            int k =taille * n + n - i;
            snake.set(ind, k);
            ind = (ind+1) % (taille * taille);
            value[k/taille][k%taille] = 1;
            b=tab.getButton(k/taille,k%taille);
            b.setBackgroundColor(Color.RED);
        }
        length = 5;
        score = 0;
        scoreButton.setText("Score : 0");
    }
    void fin(){
        fin = true;
        AlertDialog.Builder fin = new AlertDialog.Builder(this);
        fin.setTitle("Game over !!!");
        fin.setMessage("Score : " + score);
        fin.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();

            }
        });
        fin.setPositiveButton("Rejouer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new_();
                snake();
            }
        });
        fin.setNeutralButton("Fermer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        fin.show();
    }

    void snake(){
        if (!fin) {
            AnimatorSet s = new AnimatorSet();
            int lastPosition = (int) snake.get((ind - 1 + taille * taille) % (taille * taille));
            int firstPosition = (int) snake.get((ind - length + taille * taille) % (taille * taille));
            switch (direction) {
                case TOP:
                    if (lastPosition / taille == 0 || value[lastPosition / taille - 1][lastPosition % taille] == 1) {
                        fin();
                        return;
                    } else {
                        int p = lastPosition - taille;
                        snake.set(ind, p);
                        ind = (ind + 1) % (taille * taille);
                        Button button = tab.getButton(p / taille, p % taille);
                        final ObjectAnimator objectAnimator = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.red), ContextCompat.getColor(this, R.color.red));
                        objectAnimator.setDuration(150);
                        objectAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(objectAnimator);
                                direction = directionExpected;
                                snake();
                            }
                        });
                        s.play(objectAnimator);
                        if (value[p / taille][p % taille] != 2) {
                            button = tab.getButton(firstPosition / taille, firstPosition % taille);
                            ObjectAnimator objectAnimator2;
                            if ((int) (Math.random() * 6) == 0) {
                                value[firstPosition / taille][firstPosition % taille] = 2;
                                objectAnimator2 = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.yellow), ContextCompat.getColor(this, R.color.yellow));
                            } else {
                                value[firstPosition / taille][firstPosition % taille] = 0;
                                objectAnimator2 = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.transgrey), ContextCompat.getColor(this, R.color.transgrey));
                            }
                            objectAnimator2.setDuration(150);
                            s.play(objectAnimator2);
                        } else {
                            length++;
                            score++;
                            scoreButton.setText("Score : " + score);
                        }
                        value[p / taille][p % taille] = 1;
                    }
                    break;
                case BOTTOM:
                    if (lastPosition / taille == taille - 1 || value[lastPosition / taille + 1][lastPosition % taille] == 1) {
                        fin();
                        return;
                    } else {
                        int p = lastPosition + taille;
                        snake.set(ind, p);
                        ind = (ind + 1) % (taille * taille);
                        Button button = tab.getButton(p / taille, p % taille);
                        final ObjectAnimator objectAnimator = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.red), ContextCompat.getColor(this, R.color.red));
                        objectAnimator.setDuration(150);
                        objectAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(objectAnimator);
                                direction = directionExpected;
                                snake();
                            }
                        });
                        s.play(objectAnimator);
                        if (value[p / taille][p % taille] != 2) {
                            button = tab.getButton(firstPosition / taille, firstPosition % taille);
                            ObjectAnimator objectAnimator2;
                            if ((int) (Math.random() * 6) == 0) {
                                value[firstPosition / taille][firstPosition % taille] = 2;
                                objectAnimator2 = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.yellow), ContextCompat.getColor(this, R.color.yellow));
                            } else {
                                value[firstPosition / taille][firstPosition % taille] = 0;
                                objectAnimator2 = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.transgrey), ContextCompat.getColor(this, R.color.transgrey));
                            }
                            objectAnimator2.setDuration(150);
                            s.play(objectAnimator2);
                        } else {
                            length++;
                            score++;
                            scoreButton.setText("Score : " + score);
                        }
                        value[p / taille][p % taille] = 1;
                    }
                    break;
                case LEFT:
                    if (lastPosition % taille == 0 || value[lastPosition / taille][lastPosition % taille - 1] == 1) {
                        fin();
                        return;
                    } else {
                        int p = lastPosition - 1;
                        snake.set(ind, p);
                        ind = (ind + 1) % (taille * taille);
                        Button button = tab.getButton(p / taille, p % taille);
                        final ObjectAnimator objectAnimator = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.red), ContextCompat.getColor(this, R.color.red));
                        objectAnimator.setDuration(200);
                        objectAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(objectAnimator);
                                direction = directionExpected;
                                snake();
                            }
                        });
                        s.play(objectAnimator);
                        if (value[p / taille][p % taille] != 2) {
                            button = tab.getButton(firstPosition / taille, firstPosition % taille);
                            ObjectAnimator objectAnimator2;
                            if ((int) (Math.random() * 6) == 0) {
                                value[firstPosition / taille][firstPosition % taille] = 2;
                                objectAnimator2 = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.yellow), ContextCompat.getColor(this, R.color.yellow));
                            } else {
                                value[firstPosition / taille][firstPosition % taille] = 0;
                                objectAnimator2 = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.transgrey), ContextCompat.getColor(this, R.color.transgrey));
                            }
                            objectAnimator2.setDuration(200);
                            s.play(objectAnimator2);
                        } else {
                            length++;
                            score++;
                            scoreButton.setText("Score : " + score);
                        }
                        value[p / taille][p % taille] = 1;
                    }
                    break;
                case RIGHT:
                    if (lastPosition % taille == taille - 1 || value[lastPosition / taille][lastPosition % taille + 1] == 1) {
                        fin();
                        return;
                    } else {
                        int p = lastPosition + 1;
                        snake.set(ind, p);
                        ind = (ind + 1) % (taille * taille);
                        Button button = tab.getButton(p / taille, p % taille);
                        final ObjectAnimator objectAnimator = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.red), ContextCompat.getColor(this, R.color.red));
                        objectAnimator.setDuration(200);
                        objectAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(objectAnimator);
                                direction = directionExpected;
                                snake();
                            }
                        });
                        s.play(objectAnimator);
                        if (value[p / taille][p % taille] != 2) {
                            button = tab.getButton(firstPosition / taille, firstPosition % taille);
                            ObjectAnimator objectAnimator2;
                            if ((int) (Math.random() * 6) == 0) {
                                value[firstPosition / taille][firstPosition % taille] = 2;
                                objectAnimator2 = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.yellow), ContextCompat.getColor(this, R.color.yellow));
                            } else {
                                value[firstPosition / taille][firstPosition % taille] = 0;
                                objectAnimator2 = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), ContextCompat.getColor(this, R.color.transgrey), ContextCompat.getColor(this, R.color.transgrey));
                            }
                            objectAnimator2.setDuration(200);
                            s.play(objectAnimator2);
                        } else {
                            length++;
                            score++;
                            scoreButton.setText("Score : " + score);
                        }
                        value[p / taille][p % taille] = 1;
                    }
                    break;
            }
            s.start();
        }
    }


    void move(final Button button){
        button.setOnTouchListener(new OnSwipeTouchListener(Snake.this){
            public void onSwipeTop() {
                if (direction != Direction.TOP && direction != Direction.BOTTOM)
                    directionExpected = Direction.TOP;
            }
            public void onSwipeRight() {
                if (direction != Direction.RIGHT && direction != Direction.LEFT)
                    directionExpected = Direction.RIGHT;
            }
            public void onSwipeLeft() {
                if (direction != Direction.RIGHT && direction != Direction.LEFT)
                    directionExpected = Direction.LEFT;
            }
            public void onSwipeBottom() {
                if (direction != Direction.TOP && direction != Direction.BOTTOM)
                    directionExpected = Direction.BOTTOM;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake);

        this.main = findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fin = true;
                Intent main = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(main);
                finish();
            }
        });

        this.scoreButton = findViewById(R.id.score);

        create_layout();

        this.new_ = findViewById(R.id.new_);
        new_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fin) {
                    new_();
                    fin = false;
                    snake();
                }
            }
        });

        for (int i = 0; i < taille; i++)
            for (int j = 0; j < taille; j++)
                move(tab.getButton(i,j));
    }
}
