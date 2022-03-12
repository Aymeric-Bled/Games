package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;

public class Hex extends AppCompatActivity {
    private Button main;
    private Button new_;
    private LinearLayout background;
    private Spinner player;
    private String players[] = {"1 joueur","2 joueurs","0 joueur"};
    private int nb_players = 1;
    private int m=11;
    private int n;
    private double w;
    private double h;
    private int dx;
    private int dy;
    private int tab_button[][]= new int [m][m];
    private boolean tab_color[][][] = new boolean[2][m][m];
    private ArraySet<Integer> empties = new ArraySet<>();
    private int color = 0;
    private boolean fin = false;
    private boolean play = false;
    private AnimatorSet s;
    private MCTS root;

    class HexMove extends Move{
        int x;
        int y;
        HexMove(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    class MCTS_Hex extends MCTS{

        boolean[][][] t;
        int[] up;
        ArrayList<Integer> empties;
        Hex h;
        ArrayList<int[]> hist;

        MCTS_Hex(int color, HexMove mov, MCTS parent, boolean[][][] t, int[] up, ArrayList empties, Hex h){
            super(color, mov, parent);
            this.t = t;
            this.up = up.clone();
            this.empties = empties;
            this.hist = new ArrayList<>();
            this.h = h;
        }


        @Override
        boolean isGameOver() {
            return h.getRoot(up,0, 1) == h.getRoot(up,m - 1, 1) || h.getRoot(up,1, 0) == h.getRoot(up,1, m - 1);
        }

        @Override
        int getWinner() {
            if (h.getRoot(up,0, 1) == h.getRoot(up,m - 1, 1))
                return 0;
            if (getRoot(up,1, 0) == getRoot(up,1, m - 1))
                return 1;
            //assert false;
            return -1;
        }

        @Override
        ArrayList<Move> getLegalMoves() {
            ArrayList<Move> moves = new ArrayList<>();
            for (int pos : empties){
                moves.add(new HexMove(pos / m, pos % m));
            }
            return moves;
        }

        @Override
        void doMove(int color, Move move) {
            HexMove mov = (HexMove) move;
            hist.add(up.clone());
            assert !t[1 - color][mov.x][mov.y];
            t[color][mov.x][mov.y] = true;
            int i = empties.indexOf(mov.x * m + mov.y);
            empties.remove(i);
            //empties.remove((Object) move);
            for (int x = -1; x < 2; x++){
                for (int y = -1; y < 2; y++){
                    if (mov.x + x >= 0 && mov.x + x < m && mov.y+ y >= 0 && mov.y + y < m && is_neighbor(mov.x * m + mov.y, mov.x * m + mov.y + m * x + y) && t[color][mov.x + x][mov.y + y]){
                        int root = h.getRoot(up, mov.x + x, mov.y + y);
                        int root2 = h.getRoot(up, mov.x, mov.y);
                        if (root != root2)
                            up[root] = root2;
                    }
                }
            }
        }

        @Override
        void undoMove(int color, Move move) {
            HexMove mov = (HexMove) move;
            t[color][mov.x][mov.y] = false;
            t[1 - color][mov.x][mov.y] = false;
            empties.add(mov.x * m + mov.y);
            up = hist.remove(hist.size() - 1);
        }

        @Override
        MCTS newMCTS(int color, Move move, MCTS parent) {
            return new MCTS_Hex(color, (HexMove) move, parent , this.t, this.up, ((MCTS_Hex) parent).empties, this.h);
        }

        @Override
        Move chooseRandomMove(int color) {
            //ArrayList legalMoves = this.getLegalMoves(tab, color);
            int move = (int) empties.get((int) (Math.random() * empties.size()));
            return new HexMove(move / m, move % m);
            //return move_computer(t);
            //return (int) (Math.random() * m*m);
        }

        @Override
        Context getContext() {
            return getApplicationContext();
        }

        int rollout(int mycolor, boolean useDepth, int maxDepth){
            int[] copy = up.clone();
            int c = super.rollout(mycolor, useDepth, maxDepth);
            up = copy;
            return c;
        }



        int[] weight(int color){
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

    MCTS newMCTS(){
        int up[] = new int[m*m];
        for (int i = 0; i < m; i++){
            for (int j = 0; j < m; j++){
                up[i * m + j] = i * m + j;
            }
        }
        for (int i = 0; i < m; i++){
            for (int j = 0; j < m; j++) {
                int mov = i * m + j;
                for (int x = -1; x < 2; x++){
                    for (int y = -1; y < 2; y++){
                        if (mov / m + x >= 0 && mov / m + x < m && mov % m + y >= 0 && mov % m + y < m && is_neighbor(mov, mov + m * x + y) && ((tab_color[0][mov / m + x][mov % m + y] && tab_color[0][mov / m][mov % m]) || (tab_color[1][mov / m + x][mov % m + y] && tab_color[1][mov / m][mov % m]))){
                            int root = getRoot(up, mov / m + x, mov % m + y);
                            int root2 = getRoot(up, mov / m, mov % m);
                            if (root != root2)
                                up[root] = root2;
                        }
                    }
                }
            }
        }
        ArrayList empties = new ArrayList();
        for (int i = 1; i < m - 1; i ++){
            for (int j = 1; j < m - 1; j++){
                if (is_free(tab_color, i * m + j)){
                    empties.add(i * m + j);
                }
            }
        }
        return new MCTS_Hex(this.color, null, null, tab_color, up, (ArrayList) empties, this);
    }

    void updateTree(HexMove move){
        for (MCTS child : this.root.children){
            HexMove mv = (HexMove) child.move;
            if (mv.x == move.x && mv.y == move.y){
                this.root = child;
                this.root.parent = null;
                this.root.doMove(color,move);
                return;
            }
        }
        this.root = newMCTS();
    }

    int getRoot(int[] up, int i, int j){
        int n = i * m + j;
        while (up[n] != n){
            n = up[n];
        }
        return n;
    }

    boolean is_neighbor(int i, int j){
        if (!in(i) || !in(j)){
            return false;
        }
        if (i/m > 0 && j == i - m){
            return true;
        }
        if (i%m > 0 && j == i - 1){
            return true;
        }
        if (i/m < m - 1 && j == i + m){
            return true;
        }
        if (i%m < m - 1 && j == i + 1){
            return true;
        }
        if (i/m > 0  && i%m > 0&& j == i - m - 1){
            return true;
        }
        if (i/m < m - 1 && i%m < m - 1 && j == i + m + 1){
            return true;
        }
        return false;
    }

    boolean is_semi_linked(boolean[][][] t, int i, int j){
        if (!in(i) || !in(j)){
            return false;
        }
        if (i/m > 1 && i% m >0 && j == n-2*m-1 && is_free(t, n-m-1) && is_free(t, n - m)){
            return true;
        }
        if (i/m > 0 && i% m < m - 1 && j == n-m+1 && is_free(t, n-m) && is_free(t, n +1)){
            return true;
        }
        if (i/m < m - 1 && i% m < m - 2 && j == n+m+2 && is_free(t, n+1) && is_free(t, n+m+1)){
            return true;
        }
        if (i/m < m - 2 && i% m < m - 1 && j == n+2*m+1 && is_free(t, n+m+1) && is_free(t, n + m)){
            return true;
        }
        if (i/m < m - 1 && i% m >0 && j == n+m-1 && is_free(t, n+m) && is_free(t, n - 1)){
            return true;
        }
        if (i/m > 0 && i% m >1 && j == n-2*m-1 && is_free(t, n-1) && is_free(t, n - m - 1)){
            return true;
        }
        return false;
    }

    boolean is_under_semi_linked(boolean t[][][], int i, int c){
        if (!is_free(t, i))
            return false;
        if (is_free(t, i-m) && is_owner(t, 1-c, i-m-1) && is_owner(t, 1-c, i+1))
            return true;

        if (is_free(t, i+1) && is_owner(t, 1-c,i-m) && is_owner(t, 1-c,i+m+1))
            return true;
        if (is_free(t, i+m+1) && is_owner(t, 1-c,i+1) && is_owner(t, 1-c,i-m))
            return true;
        if (is_free(t, i+m) && is_owner(t, 1-c,i+m+1) && is_owner(t, 1-c,i-1))
            return true;
        if (is_free(t, i-1) && is_owner(t, 1-c,i+m) && is_owner(t, 1-c,i-m-1))
            return true;
        if (is_free(t, i-m-1) && is_owner(t, 1-c,i-1) && is_owner(t, 1-c,i+m))
            return true;

        return false;
    }

    boolean is_owner(boolean t[][][], int c, int i){

        return in(i) && t[c][i/m][i%m];

    }

    boolean is_free(boolean t[][][], int n){
        return !is_owner(t,0,n) && !is_owner(t,1,n);
    }



    boolean is_connected(boolean t[][][], int n, int p){

        return (in(p) && in(n) && (is_neighbor(n,p) || is_semi_linked(t, n,p)));

    }

    boolean in (int n){
        return !(n/m == 0 && n%m == 0 || n/m == m - 1 && n%m == 0 ||n/m == 0 && n%m == m - 1 ||n/m == m - 1 && n%m == m - 1);
    }

    boolean is_winning(boolean[][][] t, int color){
        boolean already_seen[]= new boolean[m * m];
        int n;
        int k;
        for (int i = 0; i < m*m; i++)
            already_seen[i] = false;
        ArrayList q = new ArrayList();
        if (color == 0)
            for (int i = 1; i < m - 1; i++) {
                q.add(i);
                already_seen[i] = true;
            }
        else{
            for (int i = 1; i < m - 1; i++){
                q.add(m * i);
                already_seen[m * i] = true;
            }
        }
        while (!q.isEmpty()){
            n = (int) q.remove(0);
            for (int i = 1; i< m - 1; i++){
                for (int j = 1; j < m - 1; j++){
                    k = m * i + j;
                    if (!already_seen[k] && is_owner(t, color, k) && is_neighbor(n,k)) {
                        if (i == m - 2 && color == 0 || j == m - 2 && color == 1)
                            return true;
                        q.add(k);
                        already_seen[k] = true;
                    }

                }
            }

        }
        return false;
    }

    void end(){

        if (is_winning(tab_color, color)) {
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

    void create_hex(){
        Button b;
        int id;
        empties = new ArraySet<>();
        for (int i=0; i < m; i++)
            for (int j=0; j<m; j++){
                tab_color[0][i][j] = false;
                tab_color[1][i][j] = false;
                if (i > 0 && j > 0 && i < m - 1 && j < m - 1)
                    empties.add(i * m + j);
                if ((i!=0 || j!=0) && (i!=0 || j!=m-1) && (i!=m-1 || j!=0) && (i!=m-1 || j!=m-1)) {
                    if(i==0 || i==m-1) {
                        b = new Button(this);
                        id = Button.generateViewId();
                        b.setId(id);
                        tab_button[i][j] = id;
                        b.setMinimumHeight(0);
                        b.setMinimumWidth(0);
                        b.setBackgroundResource(R.drawable.red_hexagon);
                        b.setHeight((int) h);
                        b.setWidth((int) w);
                        b.setX((int) (dx + (3 * j * w) / 4));
                        b.setY((int) (dy + h * i + (h * (m - 2 - j)) / 2));
                        addContentView(b,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                        tab_color[0][i][j] = true;
                    }
                    else{
                        if(j==0 || j==m-1) {
                            b = new Button(this);
                            id = Button.generateViewId();
                            b.setId(id);
                            tab_button[i][j] = id;
                            b.setMinimumHeight(0);
                            b.setMinimumWidth(0);
                            b.setBackgroundResource(R.drawable.blue_hexagon);
                            b.setHeight((int) h);
                            b.setWidth((int) w);
                            b.setX((int) (dx + (3 * j * w) / 4));
                            b.setY((int) (dy + h * i + (h * (m - 2 - j)) / 2));
                            addContentView(b,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                            tab_color[1][i][j] = true;
                        }
                        else{
                            b = new Button(this);
                            id = Button.generateViewId();
                            b.setId(id);
                            tab_button[i][j] = id;
                            b.setMinimumHeight(0);
                            b.setMinimumWidth(0);
                            b.setBackgroundResource(R.drawable.white_hexagon);
                            b.setHeight((int) h);
                            b.setWidth((int) w);
                            b.setX((int) (dx + (3 * j * w) / 4));
                            b.setY((int) (dy + h * i + (h * (m - 2 - j)) / 2));
                            addContentView(b,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

                        }
                    }
                }
            }
    }


    void new_(){
        fin = false;
        Button b;
        empties = new ArraySet<>();
        for (int i = 1; i< m-1; i++)
            for (int j= 1; j < m-1; j++){
                b=findViewById(tab_button[i][j]);
                b.setBackgroundResource(R.drawable.white_hexagon);
                tab_color[0][i][j] = false;
                tab_color[1][i][j] = false;
                empties.add(i * m + j);
            }
        this.root = newMCTS();
        if (nb_players != 2)
            play_computer();
    }



    int compute_weight(int n, int c, boolean semi_linked) {

        int w = 0;
        boolean already_seen[]= new boolean[m*m];
        boolean connected[]= new boolean[m*m];
        int k;

        for (int i = 0; i < m*m; i++) {
            if (in(i)) {
                connected[i] = ((semi_linked && is_semi_linked(tab_color,n, i) || !semi_linked && is_connected(tab_color,n, i)) && is_owner(tab_color,c, i));
                already_seen[i] = false;
            }

        }
        ArrayList q1;
        ArrayList q2;

        for (int p = 0; p < m*m; p++) {

            if (in(p) && connected[p]) {

                w++;

                q1 = new ArrayList<>();
                q1.add(p);

                while (!q1.isEmpty()) {

                    q2 = new ArrayList();

                    while (!q1.isEmpty()) {

                        k = (int) q1.remove(0);

                        for (int i = 0; i < m*m ; i++) {

                            if (in(i) && is_connected(tab_color,k, i) && is_owner(tab_color,c, i) && !already_seen[i]) {

                                already_seen[i] = true;
                                q2.add(i);

                            }
                        }
                    }
                    q1 = q2;

                }

                for (k = 0; k < m*m; k++) {
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


    boolean []component(boolean[][][] t, int n, int c){
        boolean connected[]= new boolean[m*m];
        for (int i=0; i<m*m; i++) {
            connected[i] = false;
        }
        ArrayList q =new ArrayList<>();
        q.add(n);
        connected[n] = true;
        int k;
        while (!q.isEmpty()) {
            k = (int) q.remove(0);
            for (int i = 0; i < m * m; i++) {
                if (is_connected(t,i, k) && is_owner(t,c, i) && !connected[i]) {
                    q.add(i);
                    connected[i] = true;
                }
            }
        }
        return connected;
    }

    boolean []next(boolean[][][] t, int n, int c, boolean already_seen[], boolean end_component[], boolean semi_linked, boolean under_semi_linked){
        boolean connected[]= new boolean[m*m];
        boolean next[]= new boolean[m*m];
        for (int i=0; i<m*m; i++) {
            next[i] = false;
            connected[i] = false;
        }
        ArrayList q = new ArrayList<>();
        q.add(n);
        connected[n] = true;
        int k;
        while (!q.isEmpty()){
            k = (int) q.remove(0);
            for (int i = 0; i < m*m; i++){
                if (is_owner(t,c,i) && ((!semi_linked && is_neighbor(i,k)) || (semi_linked && is_connected(t,i,k)))  && !connected[i]){
                    q.add(i);
                    connected[i] = true;
                }
                if (((!semi_linked && is_neighbor(i,k)) || (semi_linked && is_connected(t,i,k))) && (is_free(t,i) && (!under_semi_linked || !is_under_semi_linked(t,i,c)) && !already_seen[i] && !connected[i] || end_component[i])){
                    next[i] = true;
                }
            }
        }
        return next;
    }

    boolean [] path_intersection(boolean[][][] t, boolean under_semi_linked){
        boolean previous[][][]= new boolean[2][m*m][m*m];
        boolean already_seen[] = new boolean[m*m];
        boolean already_seen2[] = new boolean[m*m];
        boolean already_seen3[] = new boolean[m*m];
        boolean path[][] = new boolean[2][m*m];
        boolean path_intersection[] = new boolean[m*m];
        boolean path_final[] = new boolean[m*m];
        boolean begin_component[];
        boolean end_component[];
        boolean next[];
        for (int i = 0; i< m*m; i++) {
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
        for (int c = 0; c<2; c++){
            for (int i = 0; i< m*m; i++) {
                already_seen[i] = false;
                already_seen2[i] = false;
                already_seen3[i] = false;
            }
            if (c == 0){
                begin_component = component(t, 1,c);
                end_component = component(t, m*m - 2, c);
            }
            else{
                begin_component = component(t, m,c);
                end_component = component(t, m*m - m - 1, c);
            }
            q1 = new ArrayList<>();
            q3 = new ArrayList<>();
            fin = false;
            for (int i= 0; i< m*m; i++) {
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
            while (!fin){
                q2 =new ArrayList<>();
                while (!q1.isEmpty()){
                    k = (int) q1.remove(0);
                    next= next(t, k,c, already_seen,end_component,false, under_semi_linked /*&& c == 1 - color*/);
                    for (int i = 0; i<m*m; i++){
                        if (next[i]){
                            if (end_component[i]&& !already_seen3[i]){
                                fin =true;
                                q3.add(i);
                                already_seen3[i] = true;
                            }
                            if (!already_seen2[i]){
                                q2.add(i);
                                already_seen2[i] = true;
                            }
                            previous[c][i][k] = true;
                        }
                    }
                }
                for (int i = 0; i< m*m; i++)
                    already_seen[i] = already_seen2[i];
                q1 = q2;
                if (q1.isEmpty()){
                    return path_intersection(t, false);
                }
            }
            while (!q3.isEmpty()){
                k = (int) q3.remove(0);
                path[c][k] = true;
                for (int i =0; i<m*m; i++){
                    if (previous[c][k][i] && !already_seen3[i]){
                        q3.add(i);
                        already_seen3[i] = true;
                    }
                }
            }
        }
        for (int i = 0; i< m*m; i++){
            path_intersection[i] = path[0][i] && path[1][i];
            path_final[i] = path_intersection[i];
        }
        for (int i = 0; i< m*m; i++){
            if (path_intersection[i])
            for (int j = 0; j < m*m; j++){
                if (!path_intersection[j] && is_neighbor(i,j))
                    path_final[j] = true;
            }
        }
        return path_intersection;

    }


    int path_length(int color, int c, int n, boolean under_semi_linked){
        if (!is_free(tab_color,n))
            return m*m;
        tab_color[color][n/m][n%m] = true;
        empties.remove(n);
        boolean already_seen[] = new boolean[m*m];
        boolean next[];
        boolean begin_component[];
        boolean end_component[];
        for (int i = 0; i<m*m; i++){
            already_seen[i] = false;
        }
        ArrayList q1 = new ArrayList<>();
        ArrayList q2;
        if (c == 0){
            begin_component = component(tab_color, 1,c);
            end_component = component(tab_color, m*m - 2, c);
        }
        else{
            begin_component = component(tab_color, m,c);
            end_component = component(tab_color, m*m - m - 1, c);
        }
        for (int i = 0; i < m*m; i++){
            if (begin_component[i]){
                q1.add(i);
                already_seen[i] = true;
                if (end_component[i]){
                    tab_color[color][n/m][n%m] = false;
                    empties.add(n);
                    return 0;
                }
            }
        }
        int l;
        int k;
        for (l = 0; true ; l++) {
            q2 = new ArrayList<>();
            while (!q1.isEmpty()) {
                k = (int) q1.remove(0);
                next = next(tab_color, k, c, already_seen, end_component, true, under_semi_linked);
                for (int i = 0; i < m * m; i++) {
                    if (next[i] && !already_seen[i]) {
                        q2.add(i);
                        already_seen[i] = true;
                        if (end_component[i]) {
                            tab_color[color][n/m][n%m] = false;
                            empties.add(n);
                            return l;
                        }
                    }
                }

            }
            q1 = q2;
            if (q1.isEmpty()){
                tab_color[color][n/m][n%m] = false;
                empties.add(n);
                return m*m;
            }
        }
    }

    int weight_path(int n){
        return  path_length(color,1-color, n, true) - path_length(1 - color,1-color, n, true) - path_length(color, color,n,true);
    }
    int weight_connected(int n){
        return compute_weight(n,color, true) + compute_weight(n, 1 - color, true);
    }

    int [] weight_path(){
        int [] weight = new int[m * m];
        for (int i = 0; i < m * m; i++){
            if (is_free(tab_color,i))
                weight[i] = weight_path(i);
            else
                weight[i] = - m * m;
        }
        return  weight;
    }

    HexMove MCTS_move(){
        //print_tab(tab, m);
        //return 1;
        return (HexMove) this.root.getBestMove(4000, false, 0);
    }

    int move_computer(boolean[][][] t){
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
            if (is_free(t,i) && path_intersection[i]) {
                best_move[length++] = i;
            }
        }
        int move = best_move[(int) (Math.random() * length)];
        return move;
    }


    synchronized boolean can_play(int p){
        if (play && !fin && is_free(tab_color,p) ){
            play = false;
            return true;
        }
        return false;
    }

    void play_computer(){
        //int move= move_computer();
        HexMove move = MCTS_move();
        updateTree(move);
        //Toast.makeText(getApplicationContext(), "" + move, Toast.LENGTH_SHORT).show();
        Button b=findViewById(tab_button[move.x][move.y]);
        final ObjectAnimator objectAnimator;
        s = new AnimatorSet();
        if (color == 0)
        {
            objectAnimator = ObjectAnimator.ofObject(b, "backgroundResource", new ArgbEvaluator(), R.drawable.red_hexagon, R.drawable.red_hexagon);
        }
        else{
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
        tab_color[color][move.x][move.y] = true;
        empties.remove(move);
        s.play(objectAnimator);
        s.start();
    }

    void play(final int i, final int j){
        final Button button = findViewById(tab_button[i][j]);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (can_play(i*m+j)){
                    updateTree(new HexMove(i,j));
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
                    tab_color[color][i][j] = true ;
                    empties.remove(i * m + j);
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
        this.background = findViewById(R.id.background);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int navigationBarHeight = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        int statusBarHeight = 0;
        resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        int titleBarHeight = 0;
        resourceId = getResources().getIdentifier("title_bar_height", "dimen", "android");
        if (resourceId > 0) {
            titleBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        double width = metrics.widthPixels;
        double height = metrics.heightPixels - titleBarHeight - navigationBarHeight - statusBarHeight;
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
        dy = (int) (2 * height - ((3 * m - 3) * h)) / 4;
        create_hex();
        this.main = findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}