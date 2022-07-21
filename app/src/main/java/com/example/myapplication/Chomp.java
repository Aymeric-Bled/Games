package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;

public class Chomp extends AppCompatActivity {
    private ImageView main;
    private Spinner players;
    private String nb_players[] = {"1 joueur","2 joueurs","0 joueur"};
    private ImageView new_;
    private int width = 9;
    private int height = 10;
    private Table tab;
    private int length[] = new int[height];
    private boolean winning[][];
    private boolean alreadySeen[][];
    private int size;
    boolean player = true;
    private int nbPlayers = 1;
    boolean end = false;
    private int coef[][] = new int [height + 1][width + 1];


    void create_chomp(){
        compute_coef();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w = (metrics.widthPixels - width - 1)/ width;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(1,1,1,1);
        tab = new Table((GridLayout) findViewById(R.id.grille), height, width, this, params, w);
        for (int i=0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Button b = tab.getButton(i, j);
                b.setBackgroundColor(getColor(R.color.brown));
            }
            length[i] = width;
        }
        Button b = tab.getButton(0,0);
        b.setBackgroundColor(Color.RED);
        size = (int) Math.sqrt(number(length)) + 1;
        winning = new boolean[size][size];
        alreadySeen = new boolean[size][size];
        alreadySeen[0][0] = true;
    }

    void compute_coef(){
        for (int j = 0; j < width + 1; j++) {
            coef[1][j] = j;
        }
        for (int i = 2; i < height + 1; i++){
            coef[i][1] = coef[i - 1] [width] + 1;
            for (int j = 2; j < width + 1; j++){
                coef[i][j] = coef[i][j - 1] + coef[i - 1] [width + 2 - j];
            }
        }
    }

    int number(int length[]){
        int s = 0;
        for (int i = 1; i < height; i++){
            s += coef[i][length[i - 1] - length[i]];
        }
        s += coef[height][length[height - 1]];
        return s;
    }

    void simulMove(int copy[], int i, int j){
        for (int x = i; x < height; x++) {
            if (copy[x] > j)
                copy[x] = j;
        }
    }

    boolean winningMove(int length[]){
        int n = number(length);
        if (n == 0)
            return false;
        if (alreadySeen[n / size][n % size])
            return winning[n / size][n % size];
        for(int i = 0; i < height; i++){
            for (int j = 0; j < length[i]; j++){
                int copy[] = new int[height];
                System.arraycopy(length, 0, copy, 0, height);
                simulMove(copy, i, j);
                if (winningMove(copy)){
                    //winning[n] = -1;
                    alreadySeen[n / size][n % size] = true;
                    return false;
                }
            }
        }
        alreadySeen[n / size][n % size] = true;
        winning[n / size][n % size] = true;
        return true;
    }

    boolean canEat(int i,int j){
        return length[i] > j;
    }

    void new_(){
        end = false;
        player = true;
        for (int i=0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Button b = tab.getButton(i, j);
                b.setAlpha(1);
            }
            length[i] = width;
        }
        if (nbPlayers == 0)
            playComputer();
    }

    void end(){
        if (number(length) == 1){
            end = true;
            AlertDialog.Builder fin = new AlertDialog.Builder(this);
            if (player) {
                if (nbPlayers == 1)
                    fin.setTitle("Gagné !!!");
                else if (nbPlayers == 2)
                    fin.setTitle("Joueur 1 gagne !!!");
                else
                    fin.setTitle("Ordi 1 gagne !!!");

            }
            else{
                if (nbPlayers == 1)
                    fin.setTitle("Perdu !!!");
                else if (nbPlayers == 2)
                    fin.setTitle("Joueur 2 gagne !!!");
                else
                    fin.setTitle("Ordi 2 gagne !!!");

            }
            fin.setMessage("Il ne reste que le carré empoissonné");
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
                    new_();
                }
            });
            fin.setNeutralButton("Fermer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            fin.show();
        }
    }
    void eat(final int i, final int j, final int sum){
        if (sum <= height - 1 + width - 1 - i - j) {
            AnimatorSet s = new AnimatorSet();
            for (int x = 0; x <= sum; x++) {
                if (i + x < height && j + sum - x < width) {
                    Button button = tab.getButton(i + x, j + sum - x);
                    if (button.getAlpha() == 1) {
                        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(button, "alpha", 0);
                        //objectAnimator.setDuration(30);
                        s.play(objectAnimator);
                    }
                }
            }
            if (s.getChildAnimations().size() > 0) {
                final ObjectAnimator objectAnimator = (ObjectAnimator) s.getChildAnimations().get(0);
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        eat(i, j, sum + 1);
                    }
                });
                s.setDuration(100);
                s.start();
            }
            else{
                end();
                player = !player;
                if ((nbPlayers == 1 && !player || nbPlayers == 0) && !end)
                    playComputer();
            }
        }
        else{
            end();
            player = !player;
            if ((nbPlayers == 1 && !player || nbPlayers == 0)  && !end)
                playComputer();
        }
    }


    Pair<Integer, Integer> moveComputer(){
        ArrayList<Pair<Integer, Integer>> bestMoves= new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> moves= new ArrayList<>();
        for (int i = 0; i < height; i++){
            for (int j = 0; j < length[i]; j ++){
                int copy[] = new int[height];
                System.arraycopy(length, 0, copy, 0, height);
                simulMove(copy, i, j);
                if (winningMove(copy)){
                    bestMoves.add(new Pair<Integer, Integer>(i,j));
                }
                if (i != 0 || j != 0)
                    moves.add(new Pair<Integer, Integer>(i,j));
            }
        }
        int size = bestMoves.size();
        if (size == 0)
            return moves.get((int) (Math.random() * moves.size()));
        return bestMoves.get((int) (Math.random() * size));
    }

    void playComputer(){
        Pair<Integer, Integer> move = moveComputer();
        move(move.first, move.second);
    }

    void move(int i, int j){
        if (canEat(i,j)){
            for (int x = i; x < height; x++) {
                if (length[x] > j)
                    length[x] = j;
            }
            eat(i, j, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chomp);

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
        this.new_ = findViewById(R.id.new_);
        new_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_();
            }
        });
        this.players = findViewById(R.id.player);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nb_players);
        players.setAdapter(spinnerArrayAdapter);
        players.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nbPlayers = (position + 1) % 3;
                new_();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        create_chomp();

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                if (i != 0 || j != 0) {
                    Button button = tab.getButton(i, j);
                    final int finalI = i;
                    final int finalJ = j;
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (nbPlayers == 2 || nbPlayers == 1 && player)
                                move(finalI, finalJ);
                        }
                    });
                }
            }
        }


    }
}