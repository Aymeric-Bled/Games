package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Hex extends AppCompatActivity {
    private ImageView main;
    private ImageView new_;
    private Spinner player;
    private String players[] = {"1 joueur", "2 joueurs", "0 joueur"};
    private int nb_players = 1;
    private int n;
    private int m = 13;
    private double w;
    private double h;
    private int dx;
    private int dy;
    private int tab_button[][] = new int[m][m];
    private HexPlateau plateau;
    private int color = 0;
    private boolean fin = false;
    private boolean play = false;
    private AnimatorSet s = new AnimatorSet();
    private MCTS root;
    private Thread thread = null;

    class HexMove extends Move {
        int x;
        int y;

        HexMove(int color, int x, int y) {
            super(color);
            this.x = x;
            this.y = y;
        }
    }


    class HexPlateau extends Plateau {
        private boolean[][][] tab_color;
        private int[] up;
        private ArrayList<int[]> histUp;
        private ArrayList<Integer> empties;
        /*
        private ArrayList<Integer> pivotsRed;
        private ArrayList<ArrayList<Integer>> histPivotsRed;
        private ArrayList<Integer> pivotsBlue;
        private ArrayList<ArrayList<Integer>> histPivotsBlue;
        private HashMap<Integer,Integer> superUpRed;
        private ArrayList<HashMap<Integer,Integer>> histSuperUpRed;
        private HashMap<Integer,Integer> superUpBlue;
        private ArrayList<HashMap<Integer,Integer>> histSuperUpBlue;

         */



        HexPlateau() {
            tab_color = new boolean[2][m][m];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < m; j++) {
                    tab_color[0][i][j] = false;
                    tab_color[1][i][j] = false;
                    if ((i != 0 || j != 0) && (i != 0 || j != m - 1) && (i != m - 1 || j != 0) && (i != m - 1 || j != m - 1)) {
                        if (i == 0 || i == m - 1) {
                            tab_color[0][i][j] = true;
                        } else if (j == 0 || j == m - 1) {
                            tab_color[1][i][j] = true;
                        }
                    }
                }
            }
            empties = new ArrayList<>();
            for (int i = 1; i < m - 1; i++) {
                for (int j = 1; j < m - 1; j++) {
                    empties.add(i * m + j);
                }
            }
            //pivotsRed = new ArrayList<>();
            //pivotsBlue = new ArrayList<>();
            up = new int[m * m];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < m; j++) {
                    up[i * m + j] = i * m + j;
                }
            }


            for (int i = 0; i < m; i++) {
                for (int j = 0; j < m; j++) {
                    int mov = i * m + j;
                    for (int x = -1; x < 2; x++) {
                        for (int y = -1; y < 2; y++) {
                            if (mov / m + x >= 0 && mov / m + x < m && mov % m + y >= 0 && mov % m + y < m && is_neighbor(mov, mov + m * x + y) && ((tab_color[0][mov / m + x][mov % m + y] && tab_color[0][mov / m][mov % m]) || (tab_color[1][mov / m + x][mov % m + y] && tab_color[1][mov / m][mov % m]))) {
                                int root = getRoot(mov / m + x, mov % m + y);
                                int root2 = getRoot(mov / m, mov % m);
                                if (root != root2)
                                    up[root] = root2;
                            }
                        }
                    }
                }
            }
            /*
            superUpRed = new HashMap<>();
            ArraySet<Integer> rootsRed = new ArraySet<>();
            for (int i = 0; i < m; i++){
                for (int j = 0; j < m; j++) {
                    if (tab_color[0][i][j]) {
                        rootsRed.add(getRoot(i, j));
                    }
                }
            }
            for (int root : rootsRed){
                superUpRed.put(root, root);
            }
            superUpBlue = new HashMap<>();
            ArraySet<Integer> rootsBlue = new ArraySet<>();
            for (int i = 0; i < m; i++){
                for (int j = 0; j < m; j++) {
                    if (tab_color[1][i][j]) {
                        rootsBlue.add(getRoot(i, j));
                    }
                }
            }
            for (int root : rootsBlue){
                superUpBlue.put(root, root);
            }
             */
            histUp = new ArrayList<>();
            /*
            histPivotsRed = new ArrayList<>();
            histPivotsBlue = new ArrayList<>();
            histSuperUpRed = new ArrayList<>();
            histSuperUpBlue = new ArrayList<>();

             */
        }

        HexPlateau(HexPlateau plateau){
            this.tab_color = new boolean[2][m][m];
            for (int c = 0; c < 2; c++){
                for (int i = 0; i < m; i++){
                    for (int j = 0; j < m; j++){
                        tab_color[c][i][j] = plateau.tab_color[c][i][j];
                    }
                }
            }
            this.up = plateau.up.clone();
            /*
            this.pivotsRed = (ArrayList<Integer>) plateau.pivotsRed.clone();
            this.pivotsBlue = (ArrayList<Integer>) plateau.pivotsBlue.clone();
            this.superUpRed = (HashMap<Integer, Integer>) plateau.superUpRed.clone();
            this.superUpBlue = (HashMap<Integer, Integer>) plateau.superUpRed.clone();

             */
            this.empties = (ArrayList<Integer>) plateau.empties.clone();
            this.histUp = new ArrayList<>();
            for (int[] tab : plateau.histUp){
                this.histUp.add(tab.clone());
            }
            /*
            this.histPivotsRed = new ArrayList<>();
            for (ArrayList<Integer> tab : plateau.histPivotsRed){
                this.histPivotsRed.add((ArrayList<Integer>) tab.clone());
            }
            this.histPivotsBlue = new ArrayList<>();
            for (ArrayList<Integer> tab : plateau.histPivotsBlue){
                this.histPivotsBlue.add((ArrayList<Integer>) tab.clone());
            }
            this.histSuperUpRed = new ArrayList<>();
            for (HashMap<Integer,Integer> tab : plateau.histSuperUpRed){
                this.histSuperUpRed.add((HashMap<Integer,Integer>) tab.clone());
            }
            this.histSuperUpBlue = new ArrayList<>();
            for (HashMap<Integer,Integer> tab : plateau.histSuperUpBlue){
                this.histSuperUpBlue.add((HashMap<Integer,Integer>) tab.clone());
            }

             */
        }

        int getRoot(int i, int j) {
            int n = i * m + j;
            while (up[n] != n) {
                n = up[n];
            }
            return n;
        }
/*
        int getSuperRoot(int i, int j, int color){
            assert tab_color[color][i][j];
            int n = getRoot(i,j);
            HashMap<Integer, Integer> superUp;
            if (color == 0){
                superUp = superUpRed;
            }
            else{
                superUp = superUpBlue;
            }
            if (!superUp.containsKey(n)){
                superUp.put(n,n);
                return n;
            }
            while (superUp.get(n) != n){
                n = superUp.get(n);
            }
            return n;
        }

 */

        @Override
        public void doMove(Move move) {
            HexMove mov = (HexMove) move;
            int color = mov.getColor();
            histUp.add(up.clone());
            /*
            histPivotsRed.add((ArrayList<Integer>) pivotsRed.clone());
            histPivotsBlue.add((ArrayList<Integer>) pivotsBlue.clone());
            histSuperUpRed.add((HashMap<Integer, Integer>) superUpRed.clone());
            histSuperUpBlue.add((HashMap<Integer, Integer>) superUpBlue.clone());

             */
            assert !tab_color[1 - color][mov.x][mov.y];
            tab_color[color][mov.x][mov.y] = true;
            int i = empties.indexOf(mov.x * m + mov.y);
            empties.remove(i);
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    if (mov.x + x >= 0 && mov.x + x < m && mov.y + y >= 0 && mov.y + y < m && is_neighbor(mov.x * m + mov.y, mov.x * m + mov.y + m * x + y) && tab_color[color][mov.x + x][mov.y + y]) {
                        int root = getRoot(mov.x + x, mov.y + y);
                        int root2 = getRoot(mov.x, mov.y);
                        if (root != root2)
                            up[root] = root2;
                    }
                }
            }
            /*
            if (color == 0){
                if (!superUpRed.containsKey(getRoot(mov.x,mov.y))) {
                    superUpRed.put(getRoot(mov.x,mov.y), getRoot(mov.x,mov.y));
                    System.out.println("test");
                }
            }
            else{
                if (!superUpBlue.containsKey(getRoot(mov.x,mov.y))) {
                    superUpBlue.put(getRoot(mov.x,mov.y), getRoot(mov.x,mov.y));
                    System.out.println("test");
                }
            }
            boolean done = false;
            if (pivotsRed.contains(mov.x * m + mov.y)){
                System.out.println("testRed");
                resetSuperUp(pivotsRed, superUpRed, 0);
                if (color == 0) {
                    done = true;
                }
            }
            if (pivotsBlue.contains(mov.x * m + mov.y)){
                System.out.println("testBlue");
                resetSuperUp(pivotsBlue, superUpBlue, 1);
                if (color == 1) {
                    done = true;
                }
            }
            if (!done){
                System.out.println("testNotDone");
                if (color == 0) {
                    lookForLinks(pivotsRed, superUpRed, color);
                }
                else{
                    lookForLinks(pivotsBlue, superUpBlue, color);
                }
            }
             */
        }

        /*

        public void resetSuperUp(ArrayList<Integer> pivots, HashMap<Integer,Integer> superUp, int color){
            pivots.clear();
            superUp.clear();
            ArraySet<Integer> roots = new ArraySet<>();
            for (int i = 0; i < m; i++){
                for (int j = 0; j < m; j++) {
                    if (tab_color[color][i][j]) {
                        roots.add(getRoot(i, j));
                    }
                }
            }
            for (int root : roots){
                superUp.put(root, root);
            }
            lookForLinks(pivots, superUp, color);
        }

        public void lookForLinks(ArrayList<Integer> pivots, HashMap<Integer,Integer> superUp, int color) {
            boolean change = true;
            while (change){
                change = false;
                HashMap<String,ArrayList<Integer>> map = new HashMap<>();
                for (int empty : empties){
                    ArraySet<Integer> aroundRoots = getAroundRoots(empty, color);
                    for (int aroundRoot1 : aroundRoots){
                        for (int aroundRoot2 : aroundRoots){
                            if (aroundRoot1 != aroundRoot2) {
                                String key;
                                if (aroundRoot1 < aroundRoot2) {
                                    key = aroundRoot1 + "-" + aroundRoot2;
                                } else {
                                    key = aroundRoot2 + "-" + aroundRoot1;
                                }
                                if (map.containsKey(key)){
                                    map.get(key).add(empty);
                                }
                                else{
                                    ArrayList<Integer> list = new ArrayList<>();
                                    list.add(empty);
                                    map.put(key, list);
                                }
                            }
                        }
                    }
                }
                for (String key : map.keySet()){
                    int i = 0;
                    while (i < map.get(key).size()){
                        if (pivots.contains(map.get(key).get(i))){
                            map.get(key).remove((Object) map.get(key).get(i));
                        }
                        else{
                            i++;
                        }
                    }
                    if (map.get(key).size() >= 2){
                        int root1 = Integer.parseInt(key.split("-")[0]);
                        int root2 = Integer.parseInt(key.split("-")[1]);
                        superUp.put(root1,root2);
                        pivots.add(map.get(key).get(0));
                        pivots.add(map.get(key).get(1));
                        change = true;
                    }
                }
            }
        }

        ArraySet<Integer> getAroundRoots(int i, int color) {
            ArraySet<Integer> roots = new ArraySet<>();
            if (i / m > 0 && in(i - m) && tab_color[color][(i - m) / m][(i - m) % m]) {
                roots.add(getSuperRoot((i - m) / m, (i - m) % m, color));
            }
            if (i % m > 0 && in(i - 1) && tab_color[color][(i - 1) / m][(i - 1) % m]) {
                roots.add(getSuperRoot((i - 1) / m, (i - 1) % m, color));
            }
            if (i / m < m - 1 && in(i + m) && tab_color[color][(i + m)/m][(i + m)%m]) {
                roots.add(getSuperRoot((i + m)/m, (i + m)%m, color));
            }
            if (i % m < m - 1 && in(i + 1) && tab_color[color][(i + 1)/m][(i + 1)%m]) {
                roots.add(getSuperRoot((i + 1)/m, (i + 1)%m, color));
            }
            if (i / m > 0 && i % m > 0 && in(i - m - 1) && tab_color[color][(i - m - 1)/m][(i - m - 1)%m]) {
                roots.add(getSuperRoot( (i - m - 1)/m, (i - m - 1)%m, color));
            }
            if (i / m < m - 1 && i % m < m - 1 && in(i + m + 1) && tab_color[color][(i + m + 1)/m][(i + m + 1)%m]) {
                roots.add(getSuperRoot( (i + m + 1)/m, (i + m + 1)%m, color));
            }
            return roots;
        }
        */

        @Override
        public void undoMove(Move move) {
            HexMove mov = (HexMove) move;
            int color = mov.getColor();
            tab_color[color][mov.x][mov.y] = false;
            tab_color[1 - color][mov.x][mov.y] = false;
            empties.add(mov.x * m + mov.y);
            up = histUp.remove(histUp.size() - 1);
            /*
            pivotsRed = histPivotsRed.remove(histPivotsRed.size() - 1);
            pivotsBlue = histPivotsBlue.remove(histPivotsBlue.size() - 1);
            superUpRed = histSuperUpRed.remove(histSuperUpRed.size() - 1);
            superUpBlue = histSuperUpBlue.remove(histSuperUpBlue.size() - 1);

             */
        }

        @Override
        public Plateau copy() {
            return new HexPlateau(this);
        }
/*
        @Override
        public int getWinner(int color) {
            if (color == 0 && getSuperRoot( 0, 1, 0) == getSuperRoot( m - 1, 1, 0)) {
                return 0;
            }
            if (color == 1 && getSuperRoot( 1, 0, 1) == getSuperRoot( 1, m - 1, 1)) {
                return 1;
            }
            return -1;
        }

 */
        @Override
        public int getWinner(int color) {
            if (getRoot( 0, 1) == getRoot( m - 1, 1))
                return 0;
            if (getRoot( 1, 0) == getRoot( 1, m - 1))
                return 1;
            assert false;
            return -1;
        }

        /*
        @Override
        public boolean isGameOver(int color) {
            return getSuperRoot( 0, 1, 0) == getSuperRoot( m - 1, 1, 0) || getSuperRoot( 1, 0, 1) == getSuperRoot( 1, m - 1, 1);
        }
         */
        @Override
        public boolean isGameOver(int color) {
            return getRoot( 0, 1) == getRoot( m - 1, 1) || getRoot( 1, 0) == getRoot( 1, m - 1);
        }

        @Override
        public ArrayList<Move> getLegalMoves(int color) {
            ArrayList<Move> moves = new ArrayList<>();
            for (int pos : empties) {
                moves.add(new HexMove(color, pos / m, pos % m));
            }
            return moves;
        }

        @Override
        int rollout(int mycolor, boolean useDepth, int maxDepth) {
            int[] copy = up.clone();
            int c = super.rollout(mycolor, useDepth, maxDepth);
            up = copy;
            return c;
        }
        @Override
        Move chooseRandomMove(int color){
            int n = empties.get((int) (Math.random() * empties.size()));
            return new HexMove(color, n/m, n%m);
        }
    }

    class MCTS_Hex extends MCTS {

        MCTS_Hex(int color, Move mov, MCTS parent) {
            super(color, mov, parent);
        }

        @Override
        MCTS newMCTS(int color, Move move, MCTS parent) {
            return new MCTS_Hex(color, move, parent);
        }

        @Override
        Context getContext() {
            return getApplicationContext();
        }


        int[] weight(int color) {
            int[] weight = new int[m * m];
/*
            boolean[] path_intersection = path_intersection(t,false);
            for (int i = 0; i < m * m; i++){
                if (path_intersection[i]){
                    weight[i] = 5;
                }
                else{
                    weight[i] = 0;
                }
            }

            for (int empty : empties){
                int i = empty / m;
                int j = empty % m;
                ArrayList<Integer> myComponents = new ArrayList<>();
                ArrayList<Integer> opponentComponents = new ArrayList<>();
                for (int di = -2; di < 3; di++){
                    for (int dj = -2; dj < 3; dj++){
                        if (i + di >= 1 && i + di < m - 1 && j + dj >= 1 && j + dj < m - 1){
                            if (is_owner(t, color, (i + di) * m + (j + dj))) {
                                if (is_neighbor(empty, (i + di) * m + (j + dj)) || is_semi_linked(t, empty, (i + di) * m + (j + dj))) {
                                    int root = h.getRoot(up, i + di, j + dj);
                                    if (!myComponents.contains(root)) {
                                        myComponents.add(root);
                                    }
                                }
                            }
                            if (is_owner(t, 1 - color, (i + di) * m + (j + dj))) {
                                if (is_neighbor(empty, (i + di) * m + (j + dj)) || is_semi_linked(t, empty, (i + di) * m + (j + dj))) {
                                    int root = h.getRoot(up, i + di, j + dj);
                                    if (!opponentComponents.contains(root)) {
                                        opponentComponents.add(root);
                                    }
                                }
                            }
                        }
                    }
                }
                weight[empty] += ((myComponents.size() > 1) ? myComponents.size() - 1 : 0) + 2 * ((opponentComponents.size() > 1) ? opponentComponents.size() - 1 : 0);
            }


 */
            return weight;
        }


    }

    MCTS newMCTS() {
        return new MCTS_Hex(this.color, null, null);
    }

    void updateTree(HexMove move) {
        for (MCTS child : this.root.children) {
            HexMove mv = (HexMove) child.move;
            if (mv.x == move.x && mv.y == move.y) {
                this.root = child;
                this.root.parent = null;
                return;
            }
        }
        this.root = newMCTS();
    }


    boolean is_neighbor(int i, int j) {
        if (!in(i) || !in(j)) {
            return false;
        }
        if (i / m > 0 && j == i - m) {
            return true;
        }
        if (i % m > 0 && j == i - 1) {
            return true;
        }
        if (i / m < m - 1 && j == i + m) {
            return true;
        }
        if (i % m < m - 1 && j == i + 1) {
            return true;
        }
        if (i / m > 0 && i % m > 0 && j == i - m - 1) {
            return true;
        }
        if (i / m < m - 1 && i % m < m - 1 && j == i + m + 1) {
            return true;
        }
        return false;
    }

    boolean is_semi_linked(boolean[][][] t, int i, int j) {
        if (!in(i) || !in(j)) {
            return false;
        }
        if (i / m > 1 && i % m > 0 && j == n - 2 * m - 1 && is_free(t, n - m - 1) && is_free(t, n - m)) {
            return true;
        }
        if (i / m > 0 && i % m < m - 1 && j == n - m + 1 && is_free(t, n - m) && is_free(t, n + 1)) {
            return true;
        }
        if (i / m < m - 1 && i % m < m - 2 && j == n + m + 2 && is_free(t, n + 1) && is_free(t, n + m + 1)) {
            return true;
        }
        if (i / m < m - 2 && i % m < m - 1 && j == n + 2 * m + 1 && is_free(t, n + m + 1) && is_free(t, n + m)) {
            return true;
        }
        if (i / m < m - 1 && i % m > 0 && j == n + m - 1 && is_free(t, n + m) && is_free(t, n - 1)) {
            return true;
        }
        if (i / m > 0 && i % m > 1 && j == n - 2 * m - 1 && is_free(t, n - 1) && is_free(t, n - m - 1)) {
            return true;
        }
        return false;
    }

    boolean is_under_semi_linked(boolean t[][][], int i, int c) {
        if (!is_free(t, i))
            return false;
        if (is_free(t, i - m) && is_owner(t, 1 - c, i - m - 1) && is_owner(t, 1 - c, i + 1))
            return true;

        if (is_free(t, i + 1) && is_owner(t, 1 - c, i - m) && is_owner(t, 1 - c, i + m + 1))
            return true;
        if (is_free(t, i + m + 1) && is_owner(t, 1 - c, i + 1) && is_owner(t, 1 - c, i - m))
            return true;
        if (is_free(t, i + m) && is_owner(t, 1 - c, i + m + 1) && is_owner(t, 1 - c, i - 1))
            return true;
        if (is_free(t, i - 1) && is_owner(t, 1 - c, i + m) && is_owner(t, 1 - c, i - m - 1))
            return true;
        if (is_free(t, i - m - 1) && is_owner(t, 1 - c, i - 1) && is_owner(t, 1 - c, i + m))
            return true;

        return false;
    }

    boolean is_owner(boolean t[][][], int c, int i) {

        return in(i) && t[c][i / m][i % m];

    }

    boolean is_free(boolean t[][][], int n) {
        return !is_owner(t, 0, n) && !is_owner(t, 1, n);
    }


    boolean is_connected(boolean t[][][], int n, int p) {

        return (in(p) && in(n) && (is_neighbor(n, p) || is_semi_linked(t, n, p)));

    }

    boolean in(int n) {
        return !(n / m == 0 && n % m == 0 || n / m == m - 1 && n % m == 0 || n / m == 0 && n % m == m - 1 || n / m == m - 1 && n % m == m - 1);
    }

    void end() {
        if (plateau.isGameOver(color)) {
            fin = true;
            AlertDialog.Builder fin = new AlertDialog.Builder(this);
            fin.setTitle("Fin !!!");
            if (color == 0) {
                fin.setMessage("L'équipe rouge a gagné");
            } else {
                fin.setMessage("L'équipe bleue a gagné");
            }
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
                }
            });
            fin.setNeutralButton("Fermer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            fin.show();
            play = true;
        }

    }

    void create_hex() {
        Button b;
        int id;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < m; j++) {
                if ((i != 0 || j != 0) && (i != 0 || j != m - 1) && (i != m - 1 || j != 0) && (i != m - 1 || j != m - 1)) {
                    if (i == 0 || i == m - 1) {
                        b = new Button(this);
                        id = Button.generateViewId();
                        b.setId(id);
                        tab_button[i][j] = id;
                        b.setBackgroundResource(R.drawable.red_hexagon);
                        addContentView(b, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    } else if (j == 0 || j == m - 1) {
                        b = new Button(this);
                        id = Button.generateViewId();
                        b.setId(id);
                        tab_button[i][j] = id;
                        b.setBackgroundResource(R.drawable.blue_hexagon);
                        addContentView(b, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    } else {
                        b = new Button(this);
                        id = Button.generateViewId();
                        b.setId(id);
                        tab_button[i][j] = id;
                        b.setBackgroundResource(R.drawable.white_hexagon);
                        addContentView(b, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                }
            }

    }


    void new_() {
        s.pause();
        if (thread != null) {
            thread.interrupt();
        }
        fin = false;
        plateau = new HexPlateau();
        Button b;
        for (int i = 1; i < m - 1; i++)
            for (int j = 1; j < m - 1; j++) {
                b = findViewById(tab_button[i][j]);
                b.setBackgroundResource(R.drawable.white_hexagon);
            }
        this.root = newMCTS();
        if (nb_players != 2) {

            s = new AnimatorSet();
            ObjectAnimator objectAnimator = ObjectAnimator.ofObject(main, "TextColor", new ArgbEvaluator(), Color.BLACK, Color.BLACK);
            objectAnimator.setDuration(100);
            s.play(objectAnimator);
            objectAnimator.addListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            play_computer();
                        }
                    }
            );
            s.start();
        }
    }


    int compute_weight(int n, int c, boolean semi_linked) {

        int w = 0;
        boolean already_seen[] = new boolean[m * m];
        boolean connected[] = new boolean[m * m];
        int k;

        for (int i = 0; i < m * m; i++) {
            if (in(i)) {
                connected[i] = ((semi_linked && is_semi_linked(plateau.tab_color, n, i) || !semi_linked && is_connected(plateau.tab_color, n, i)) && is_owner(plateau.tab_color, c, i));
                already_seen[i] = false;
            }

        }
        ArrayList q1;
        ArrayList q2;

        for (int p = 0; p < m * m; p++) {

            if (in(p) && connected[p]) {

                w++;

                q1 = new ArrayList<>();
                q1.add(p);

                while (!q1.isEmpty()) {

                    q2 = new ArrayList();

                    while (!q1.isEmpty()) {

                        k = (int) q1.remove(0);

                        for (int i = 0; i < m * m; i++) {

                            if (in(i) && is_connected(plateau.tab_color, k, i) && is_owner(plateau.tab_color, c, i) && !already_seen[i]) {

                                already_seen[i] = true;
                                q2.add(i);

                            }
                        }
                    }
                    q1 = q2;

                }

                for (k = 0; k < m * m; k++) {
                    if (in(k) && already_seen[k] && connected[k]) {
                        connected[k] = false;

                    }
                }
            }
        }
        if (w == 1)
            return 0;
        return w;
    }


    boolean[] component(boolean[][][] t, int n, int c) {
        boolean connected[] = new boolean[m * m];
        for (int i = 0; i < m * m; i++) {
            connected[i] = false;
        }
        ArrayList q = new ArrayList<>();
        q.add(n);
        connected[n] = true;
        int k;
        while (!q.isEmpty()) {
            k = (int) q.remove(0);
            for (int i = 0; i < m * m; i++) {
                if (is_connected(t, i, k) && is_owner(t, c, i) && !connected[i]) {
                    q.add(i);
                    connected[i] = true;
                }
            }
        }
        return connected;
    }

    boolean[] next(boolean[][][] t, int n, int c, boolean already_seen[], boolean end_component[], boolean semi_linked, boolean under_semi_linked) {
        boolean connected[] = new boolean[m * m];
        boolean next[] = new boolean[m * m];
        for (int i = 0; i < m * m; i++) {
            next[i] = false;
            connected[i] = false;
        }
        ArrayList q = new ArrayList<>();
        q.add(n);
        connected[n] = true;
        int k;
        while (!q.isEmpty()) {
            k = (int) q.remove(0);
            for (int i = 0; i < m * m; i++) {
                if (is_owner(t, c, i) && ((!semi_linked && is_neighbor(i, k)) || (semi_linked && is_connected(t, i, k))) && !connected[i]) {
                    q.add(i);
                    connected[i] = true;
                }
                if (((!semi_linked && is_neighbor(i, k)) || (semi_linked && is_connected(t, i, k))) && (is_free(t, i) && (!under_semi_linked || !is_under_semi_linked(t, i, c)) && !already_seen[i] && !connected[i] || end_component[i])) {
                    next[i] = true;
                }
            }
        }
        return next;
    }

    boolean[] path_intersection(boolean[][][] t, boolean under_semi_linked) {
        boolean previous[][][] = new boolean[2][m * m][m * m];
        boolean already_seen[] = new boolean[m * m];
        boolean already_seen2[] = new boolean[m * m];
        boolean already_seen3[] = new boolean[m * m];
        boolean path[][] = new boolean[2][m * m];
        boolean path_intersection[] = new boolean[m * m];
        boolean path_final[] = new boolean[m * m];
        boolean begin_component[];
        boolean end_component[];
        boolean next[];
        for (int i = 0; i < m * m; i++) {
            for (int j = 0; j < m * m; j++) {
                previous[0][i][j] = false;
                previous[1][i][j] = false;
            }
            path_intersection[i] = false;
            path[0][i] = false;
            path[1][i] = false;
        }
        boolean fin;
        int k;
        ArrayList q1;
        ArrayList q2;
        ArrayList q3;
        for (int c = 0; c < 2; c++) {
            for (int i = 0; i < m * m; i++) {
                already_seen[i] = false;
                already_seen2[i] = false;
                already_seen3[i] = false;
            }
            if (c == 0) {
                begin_component = component(t, 1, c);
                end_component = component(t, m * m - 2, c);
            } else {
                begin_component = component(t, m, c);
                end_component = component(t, m * m - m - 1, c);
            }
            q1 = new ArrayList<>();
            q3 = new ArrayList<>();
            fin = false;
            for (int i = 0; i < m * m; i++) {
                if (begin_component[i]) {
                    q1.add(i);
                    already_seen[i] = true;
                    if (end_component[i]) {
                        fin = true;
                        q3.add(i);
                        already_seen3[i] = true;
                    }
                }
            }
            while (!fin) {
                q2 = new ArrayList<>();
                while (!q1.isEmpty()) {
                    k = (int) q1.remove(0);
                    next = next(t, k, c, already_seen, end_component, false, under_semi_linked /*&& c == 1 - color*/);
                    for (int i = 0; i < m * m; i++) {
                        if (next[i]) {
                            if (end_component[i] && !already_seen3[i]) {
                                fin = true;
                                q3.add(i);
                                already_seen3[i] = true;
                            }
                            if (!already_seen2[i]) {
                                q2.add(i);
                                already_seen2[i] = true;
                            }
                            previous[c][i][k] = true;
                        }
                    }
                }
                for (int i = 0; i < m * m; i++)
                    already_seen[i] = already_seen2[i];
                q1 = q2;
                if (q1.isEmpty()) {
                    return path_intersection(t, false);
                }
            }
            while (!q3.isEmpty()) {
                k = (int) q3.remove(0);
                path[c][k] = true;
                for (int i = 0; i < m * m; i++) {
                    if (previous[c][k][i] && !already_seen3[i]) {
                        q3.add(i);
                        already_seen3[i] = true;
                    }
                }
            }
        }
        for (int i = 0; i < m * m; i++) {
            path_intersection[i] = path[0][i] && path[1][i];
            path_final[i] = path_intersection[i];
        }
        for (int i = 0; i < m * m; i++) {
            if (path_intersection[i])
                for (int j = 0; j < m * m; j++) {
                    if (!path_intersection[j] && is_neighbor(i, j))
                        path_final[j] = true;
                }
        }
        return path_intersection;

    }


    int path_length(int color, int c, int n, boolean under_semi_linked) {
        if (!is_free(plateau.tab_color, n))
            return m * m;
        plateau.doMove(new HexMove(color, n / m, n % m));
        boolean already_seen[] = new boolean[m * m];
        boolean next[];
        boolean begin_component[];
        boolean end_component[];
        for (int i = 0; i < m * m; i++) {
            already_seen[i] = false;
        }
        ArrayList q1 = new ArrayList<>();
        ArrayList q2;
        if (c == 0) {
            begin_component = component(plateau.tab_color, 1, c);
            end_component = component(plateau.tab_color, m * m - 2, c);
        } else {
            begin_component = component(plateau.tab_color, m, c);
            end_component = component(plateau.tab_color, m * m - m - 1, c);
        }
        for (int i = 0; i < m * m; i++) {
            if (begin_component[i]) {
                q1.add(i);
                already_seen[i] = true;
                if (end_component[i]) {
                    plateau.undoMove(new HexMove(color, n / m, n % m));
                    return 0;
                }
            }
        }
        int l;
        int k;
        for (l = 0; true; l++) {
            q2 = new ArrayList<>();
            while (!q1.isEmpty()) {
                k = (int) q1.remove(0);
                next = next(plateau.tab_color, k, c, already_seen, end_component, true, under_semi_linked);
                for (int i = 0; i < m * m; i++) {
                    if (next[i] && !already_seen[i]) {
                        q2.add(i);
                        already_seen[i] = true;
                        if (end_component[i]) {
                            plateau.undoMove(new HexMove(color, n / m, n % m));
                            return l;
                        }
                    }
                }

            }
            q1 = q2;
            if (q1.isEmpty()) {
                plateau.undoMove(new HexMove(color, n / m, n % m));
                return m * m;
            }
        }
    }

    int weight_path(int n) {
        return path_length(color, 1 - color, n, true) - path_length(1 - color, 1 - color, n, true) - path_length(color, color, n, true);
    }

    int weight_connected(int n) {
        return compute_weight(n, color, true) + compute_weight(n, 1 - color, true);
    }

    int[] weight_path() {
        int[] weight = new int[m * m];
        for (int i = 0; i < m * m; i++) {
            if (is_free(plateau.tab_color, i))
                weight[i] = weight_path(i);
            else
                weight[i] = -m * m;
        }
        return weight;
    }

    HexMove MCTS_move() {
        HexMove move = (HexMove) this.root.getBestMove(plateau, 4000, 10, false, 0);
        /*
        final MCTS root = this.root;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "" + root.n, Toast.LENGTH_SHORT).show();
            }
        });

         */
        return move;
    }

    int move_computer(boolean[][][] t) {
        boolean path_intersection[] = path_intersection(t, true);
        int best_move[] = new int[m * m];
        int length = 0;
        /*
        for (int i = 0; i < m * m; i++) {
            if (is_free(t,i) && path_intersection[i]) {
                if (length == 0 || weigth_connected(i) > weigth_connected(best_move[0])) {
                    best_move[0] = i;
                    length = 1;;
                }
                if (weigth_connected(best_move[0]) == weigth_connected(i) ) {
                        best_move[length++] = i;
                }
            }

        }
        int move = best_move[(int) (Math.random() * length)];

         */

        for (int i = 0; i < m * m; i++) {
            if (is_free(t, i) && path_intersection[i]) {
                best_move[length++] = i;
            }
        }
        int move = best_move[(int) (Math.random() * length)];
        return move;
    }


    synchronized boolean can_play(int p) {
        if (play && !fin && is_free(plateau.tab_color, p)) {
            play = false;
            return true;
        }
        return false;
    }

    void play_computer() {
        final HexMove[] move = new HexMove[1];
        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                move[0] = MCTS_move();
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        updateTree(move[0]);
        plateau.doMove(move[0]);
        Button b = findViewById(tab_button[move[0].x][move[0].y]);
        final ObjectAnimator objectAnimator;
        s = new AnimatorSet();
        if (color == 0) {
            objectAnimator = ObjectAnimator.ofObject(b, "backgroundResource", new ArgbEvaluator(), R.drawable.red_hexagon, R.drawable.red_hexagon);
        } else {
            objectAnimator = ObjectAnimator.ofObject(b, "backgroundResource", new ArgbEvaluator(), R.drawable.blue_hexagon, R.drawable.blue_hexagon);
        }
        objectAnimator.setDuration(100);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                end();
                color = 1 - color;
                if (nb_players > 0)
                    play = true;
                else if (!fin)
                    play_computer();
            }
        });
        //plateau.tab_color[color][move.x][move.y] = true;
        //empties.remove(move);
        s.play(objectAnimator);
        s.start();
    }

    void play(final int i, final int j){
        final Button button = findViewById(tab_button[i][j]);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (can_play(i*m+j)){
                    HexMove move = new HexMove(color, i, j);
                    updateTree(move);
                    plateau.doMove(move);
                    final ObjectAnimator objectAnimator;
                    s = new AnimatorSet();
                    if (color == 0)
                    {
                        objectAnimator = ObjectAnimator.ofObject(button, "backgroundResource", new ArgbEvaluator(), R.drawable.red_hexagon, R.drawable.red_hexagon);
                    }
                    else{
                        objectAnimator = ObjectAnimator.ofObject(button, "backgroundResource", new ArgbEvaluator(), R.drawable.blue_hexagon, R.drawable.blue_hexagon);
                    }
                    objectAnimator.setDuration(100);
                    objectAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            end();
                            color = 1 - color;
                            if (!fin && nb_players < 2){
                                play_computer();
                            }
                            else
                                play = true;
                        }
                    });
                    //plateau.tab_color[color][i][j] = true ;
                    //empties.remove(i * m + j);
                    s.play(objectAnimator);
                    s.start();
                }
            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hex);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.main = findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.pause();
                if (thread != null) {
                    thread.interrupt();
                }
                Intent main = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(main);
                finish();
            }
        });
        this.new_=findViewById(R.id.new_);
        new_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_();
            }
        });
        this.player = (Spinner) findViewById(R.id.player);

        create_hex();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, players);
        player.setAdapter(spinnerArrayAdapter);
        player.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nb_players = (position + 1) % 3;
                new_();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        for (int i=1; i<m-1; i++)
            for (int j=1; j<m-1; j++)
                play(i,j);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        double height = findViewById(android.R.id.content).getHeight() - main.getHeight();
        double width = metrics.widthPixels;
        w = ((3 * m + 1) / 2);
        h = ((3 * m - 3) * Math.sqrt(3)) / 2;
        if (h * width < w * height){
            w = ((4 * width) / (3 * m + 1));
            h = (int) ((w * Math.sqrt(3)) / 2);
            w = (int) w;
        }
        else{
            h = ((2 * height) / (3 * m - 3));
            w = (int) ((2 * h) / Math.sqrt(3));
            h = (int) h;
        }
        dx = (int) (4 * width - ((3 * m + 1) * w)) / 8;
        dy = (int) (2 * height - ((3 * m - 3) * h)) / 4 + main.getHeight();
        Button b;
        for (int i=0; i < m; i++)
            for (int j=0; j<m; j++){
                if ((i!=0 || j!=0) && (i!=0 || j!=m-1) && (i!=m-1 || j!=0) && (i!=m-1 || j!=m-1)) {
                    b = findViewById(tab_button[i][j]);
                    b.setMinimumHeight(0);
                    b.setMinimumWidth(0);
                    b.setHeight((int) h);
                    b.setWidth((int) w);
                    b.setX((int) (dx + (3 * j * w) / 4));
                    b.setY((int) (dy + h * i + (h * (m - 2 - j)) / 2));
                }
            }

    }
}