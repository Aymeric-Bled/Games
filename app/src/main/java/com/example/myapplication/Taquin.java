package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;


public class Taquin extends AppCompatActivity {
    private Button main;
    private Button alea;
    private Button solveur;
    private int taille = 4;
    private int tab[][];
    private Table table;
    private boolean solver = false;
    private int minVal = -1;
    private ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moveHistory = new ArrayList<>();
    private Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> lastMove = null;
    private boolean pause = false;
    private int range = 1;
    private Spinner t;
    private Object Taille[] = {3, 4, 5, 6, 7, 8, 9, 10};
    private AnimatorSet s = new AnimatorSet();;

    void create_table(){
        tab=new int[taille][taille];
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w=(metrics.widthPixels - taille - 1)/taille;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,w);
        params.setMargins(1,1,1,1);
        table = new Table((GridLayout) findViewById(R.id.grille), taille, taille, this, params, w);
        for (int i = 0; i < taille; i++)
            for (int j = 0; j < taille; j++){
                tab[i][j] = (taille * i + j + 1) % (taille * taille);
                Button b = table.getButton(i,j);
                b.setTextSize(w/6);
                if (i*taille + j != taille * taille - 1) {
                    b.setBackgroundResource(R.drawable.taquinbutton);
                    b.setText(""+ (taille*i+j+1));
                    b.setTextColor(Color.WHITE);
                }
                else
                    b.setBackgroundColor(Color.TRANSPARENT);
            }

        for (int i = 0; i < taille; i++)
            for (int j = 0; j < taille; j++)
                move(table.getButton(i,j));
    }

    int place(int n, int t[][]){
        for (int i=0;i<taille;i++) {
            for (int j = 0; j < taille; j++) {
                if (t[i][j] == n) {
                    return i * taille + j;
                }
            }
        }
        return -1;
    }
    int num(Button b){
        for (int i=0; i<taille; i++) {
            for (int j = 0; j < taille; j++) {
                if (b.getText().equals(""+(taille*i+j+1))) {
                    return taille*i + j + 1;
                }
            }
        }
        return -1;
    }


    void alea(){
        int z=place(0, tab);
        int randomInt;
        for (int i=0; i<10000 + (int) (Math.random() * 1000); i++){
            randomInt =(int)(Math.random() * 4);
            if (randomInt==0 && z/taille!=0){
                tab[z/taille][z%taille]=tab[z/taille-1][z%taille];
                tab[z/taille-1][z%taille]=0;
                z-=taille;
            }
            if (randomInt==1 && z/taille!=taille - 1){
                tab[z/taille][z%taille]=tab[z/taille+1][z%taille];
                tab[z/taille+1][z%taille]=0;
                z+=taille;
            }
            if (randomInt==2 && z%taille!=0){
                tab[z/taille][z%taille]=tab[z/taille][z%taille-1];
                tab[z/taille][z%taille-1]=0;
                z-=1;
            }
            if (randomInt==3 && z%taille!=taille - 1){
                tab[z/taille][z%taille]=tab[z/taille][z%taille+1];
                tab[z/taille][z%taille+1]=0;
                z+=1;
            }
        }

        for (int i = 0; i < taille; i++)
            for (int j = 0; j < taille; j++){
                Button b = table.getButton(i,j);
                if (tab[i][j] == 0){
                    b.setBackgroundResource(R.color.trans);
                    b.setTextColor(Color.TRANSPARENT);
                }
                else{
                    b.setBackgroundResource(R.drawable.taquinbutton);
                    b.setTextColor(Color.WHITE);
                    b.setText("" + tab[i][j]);
                }
            }

    }

    void move(final Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (solver)
                    return;
                int n = place(num(button), tab);
                int m = place(0, tab);
                if (n == m)
                    return;
                Button b = table.getButton(m/taille, m%taille);
                if (n % taille == m % taille) {
                    if (n / taille == m / taille + 1) {
                        doMove(new Pair(new Pair(m/taille, m%taille), new Pair(n/taille, n%taille)));

                    } else {
                        if (n / taille == m / taille - 1) {
                            doMove(new Pair(new Pair(m/taille, m%taille), new Pair(n/taille, n%taille)));
                        }
                    }
                } else {
                    if (n / taille == m / taille) {
                        if (n % taille == m % taille + 1) {
                            doMove(new Pair(new Pair(m/taille, m%taille), new Pair(n/taille, n%taille)));

                        } else {
                            if (n % taille == m % taille - 1) {
                                doMove(new Pair(new Pair(m/taille, m%taille), new Pair(n/taille, n%taille)));
                            }

                        }
                    }

                }

            }
        });
    }

    boolean end(){
        for (int i=0; i<taille * taille - 1; i++){
            if (tab[i/taille][i%taille]!= i+1) return false;
        }
        return true;
    }
    void fin(){
        if (end()) {
            AlertDialog.Builder fin = new AlertDialog.Builder(this);
            fin.setTitle("Félicitations !!!");
            fin.setMessage("Le taquin est terminé");
            fin.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);
                    finish();

                }
            });
            fin.setPositiveButton("Mélanger", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alea();
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

    int value(int t[][]){
        int v = 0;
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                int n = t[i][j];
                if (n != 0) {
                    int x = (n - 1) / taille;
                    int y = (n - 1) % taille;
                    if ((x < y ? x : y) < range) {
                        v += Math.abs(i - x) + Math.abs(j - y);
                    }
                }
            }
        }
        return v;
    }

    int[][] copy(int t[][]){
        int [][] copy = new int[taille][taille];
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                copy[i][j] = t[i][j];
            }
        }
        return copy;
    }

    ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> legalMoves(int t[][]){
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> legalMoves = new ArrayList<>();
        int m = place(0, t);
        int x = m / taille;
        int y = m % taille;
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> lastMove = moveHistory.isEmpty() ? this.lastMove : moveHistory.get(moveHistory.size() - 1);
        if (x > range - 1){
            if (lastMove == null || lastMove.first.first != x - 1 || lastMove.first.second != y)
                legalMoves.add(new Pair(new Pair(x, y), new Pair(x - 1, y)));
        }
        if (x < taille - 1){
            if (lastMove == null || lastMove.first.first != x + 1 || lastMove.first.second != y)
                legalMoves.add(new Pair(new Pair(x, y), new Pair(x + 1, y)));
        }
        if (y > range - 1){
            if (lastMove == null || lastMove.first.first != x || lastMove.first.second != y - 1)
                legalMoves.add(new Pair(new Pair(x, y), new Pair(x, y - 1)));
        }
        if (y < taille - 1){
            if (lastMove == null || lastMove.first.first != x || lastMove.first.second != y + 1)
                legalMoves.add(new Pair(new Pair(x, y), new Pair(x, y + 1)));
        }
        assert !legalMoves.isEmpty() && legalMoves.size() <= 4;
        return legalMoves;
    }

    String key(int t[][], int depth){
        String key = "" + depth;
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                key += t[i][j] / 10;
                key += t[i][j] % 10;
            }
        }
        //Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
        return key;
    }

    Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Integer> bestMove(int t[][], int depth, long timeout, Map<String, Pair<ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>, Integer>> dict, Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Integer> bmove) throws TimeoutException {
        if (System.currentTimeMillis() > timeout && bmove != null) {
            throw new TimeoutException();
        }
        /*
        String key = key(t, depth);
        if (dict.containsKey(key)){
            Pair<ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>, Integer> val = dict.get(key);
            return new Pair(val.first.get((int) (Math.random() * val.first.size())), val.second);
        }
         */
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> legalMoves = legalMoves(t);
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> bestMoves = new ArrayList<>();
        int min = 1000;
        for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move : legalMoves){
            Pair<Integer, Integer> p1 = move.first;
            Pair<Integer, Integer> p2 = move.second;
            t[p1.first][p1.second] = t[p2.first][p2.second];
            t[p2.first][p2.second] = 0;
            moveHistory.add(move);
            Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Integer> result;
            if (depth > 1)
                result = bestMove(t, depth - 1, timeout, dict, bmove);
            else
                result = new Pair(move, value(t));
            if (result != null && result.second < min){
                min = result.second;
                bestMoves.clear();
                bestMoves.add(move);
            }
            else if (result != null && result.second == min){
                bestMoves.add(move);
            }
            t[p2.first][p2.second] = t[p1.first][p1.second];
            t[p1.first][p1.second] = 0;
            moveHistory.remove(moveHistory.size() - 1);
        }
        if (!bestMoves.isEmpty()) {
            //dict.put(key, new Pair(bestMoves, min));
            return new Pair(bestMoves.get((int) (Math.random() * bestMoves.size())), min);
        }
        return null;
    }

    Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Integer> bestMove(int t[][], Map<String, Pair<ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>, Integer>> dict) {
        int depth = 1;
        final long timeout = System.currentTimeMillis() + 500;
        Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Integer> move = null;
        try {
            while (true) {
                moveHistory = new ArrayList<>();
                Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Integer> m = bestMove(t, depth, timeout, dict, move);
                if (m != null && (minVal == -1 || m.second < minVal)) {
                    move = m;
                    minVal = move.second;
                }
                depth++;
            }
        } catch (TimeoutException e) {
            //Toast.makeText(this, "" + depth, Toast.LENGTH_SHORT).show();
            return move;
        }
    }

    synchronized void doMove(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move){
        Pair<Integer, Integer> p1 = move.first;
        Pair<Integer, Integer> p2 = move.second;
        tab[p1.first][p1.second] = tab[p2.first][p2.second];
        tab[p2.first][p2.second] = 0;
        Button b;
        b = table.getButton(p1.first, p1.second);
        b.setBackgroundResource(R.drawable.taquinbutton);
        b.setTextColor(Color.WHITE);
        b.setText("" + tab[p1.first][p1.second]);
        b.setClickable(true);
        b = table.getButton(p2.first, p2.second);
        b.setBackgroundResource(R.color.trans);
        b.setTextColor(Color.TRANSPARENT);
        b.setClickable(false);
        fin();

    }

    void solve() {
        int v = value(tab);
        if (v == 0 && range < taille - 1)
            range ++;
        v = value(tab);
        if (v == 0) {
            solver = false;
            return;
        }
        Map<String, Pair<ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>, Integer>> dict = new HashMap();
        minVal = value(tab);
        Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, Integer> result = bestMove(copy(tab), dict);
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move = result.first;
        doMove(move);
        s = new AnimatorSet();
        ObjectAnimator animator =  ObjectAnimator.ofObject(main, "TextColor", new ArgbEvaluator(), Color.BLACK, Color.BLACK);
        animator.setDuration(0);
        s.play(animator);
        s.addListener(new AnimatorListenerAdapter() {
                          @Override
                          public void onAnimationEnd(Animator animation) {
                              solve();
                          }
                      });
        moveHistory.add(move);
        lastMove = move;
        s.start();
    }

    synchronized boolean canSolve(){
        if (!solver){
            solver = true;
            return true;
        }
        return false;
    }
    void doSolve() {
        if (canSolve()){
            range = 1;
            s = new AnimatorSet();
            ObjectAnimator animator =  ObjectAnimator.ofObject(main, "TextColor", new ArgbEvaluator(), Color.BLACK, Color.BLACK);
            animator.setDuration(500);
            s.play(animator);
            s.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    solve();
                }
            });
            s.start();
        }
        else{
            s.pause();
            s = new AnimatorSet();
            solver = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taquin);
        create_table();
        alea();
        this.main = findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.pause();
                solver = false;
                Intent main = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(main);
                finish();
            }
        });
        this.t = findViewById(R.id.taille);
        ArrayAdapter tailles = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Taille);
        t.setAdapter(tailles);
        t.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                solver = false;
                s.pause();
                if (taille != position + 3) {
                    taille = position + 3;
                    create_table();
                    alea();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        t.setSelection(1);
        this.solveur = findViewById(R.id.solver);
        solveur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSolve();
            }
        });
        this.alea = findViewById(R.id.alea);
        alea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solver = false;
                s.pause();
                alea();
            }
        });

    }
}
