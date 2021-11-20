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
import android.os.Bundle;
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

public class Hex extends AppCompatActivity {
    private Button main;
    private Button new_;
    private LinearLayout background;
    private Spinner player;
    private String players[] = {"1 joueur","2 joueurs","0 joueur"};
    private int nb_players = 1;
    private int m=13;
    private int n;
    private double w;
    private double h;
    private int dx;
    private int dy;
    private int tab_button[][]= new int [m][m];
    private boolean tab_color[][][] = new boolean[2][m][m];
    private int color = 0;
    private int queue[][]=new int [m*m][m*m + 1];
    private int begin[] = new int [m*m];
    private int end[] = new int [m*m];
    private boolean free[] = new boolean[m*m];
    private boolean fin = false;
    private boolean play = true;
    private AnimatorSet s;

    int new_queue(){
        int i=0;
        while (!free[i]){
            i++;
        }
        begin[i] = 0;
        end [i] = 0;
        free[i] = false;
        return i;
    }

    boolean is_empty(int q) {
        return end[q] == begin[q];
    }

    void enqueue (int q, int n){
        queue[q][end[q]] = n;
        end[q] = (end[q] + 1) % (m * m + 1);
    }

    int dequeue (int q){
        if (is_empty(q)){
            return -1;
        }
        n = queue[q][begin[q]];
        begin[q] = (begin[q] + 1) % (m * m + 1);
        return n;
    }

    void free(int q){
        free[q] = true;
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

    boolean is_semi_linked(int i, int j){
        if (!in(i) || !in(j)){
            return false;
        }
        if (i/m > 1 && i% m >0 && j == n-2*m-1 && is_free(n-m-1) && is_free(n - m)){
            return true;
        }
        if (i/m > 0 && i% m < m - 1 && j == n-m+1 && is_free(n-m) && is_free(n +1)){
            return true;
        }
        if (i/m < m - 1 && i% m < m - 2 && j == n+m+2 && is_free(n+1) && is_free(n+m+1)){
            return true;
        }
        if (i/m < m - 2 && i% m < m - 1 && j == n+2*m+1 && is_free(n+m+1) && is_free(n + m)){
            return true;
        }
        if (i/m < m - 1 && i% m >0 && j == n+m-1 && is_free(n+m) && is_free(n - 1)){
            return true;
        }
        if (i/m > 0 && i% m >1 && j == n-2*m-1 && is_free(n-1) && is_free(n - m - 1)){
            return true;
        }
        return false;
    }

    boolean is_under_semi_linked(int i, int c){
        if (!is_free(i))
            return false;
        if (is_free(i-m) && is_owner(1-c, i-m-1) && is_owner(1-c, i+1))
            return true;

        if (is_free(i+1) && is_owner(1-c,i-m) && is_owner(1-c,i+m+1))
            return true;
        if (is_free(i+m+1) && is_owner(1-c,i+1) && is_owner(1-c,i-m))
            return true;
        if (is_free(i+m) && is_owner(1-c,i+m+1) && is_owner(1-c,i-1))
            return true;
        if (is_free(i-1) && is_owner(1-c,i+m) && is_owner(1-c,i-m-1))
            return true;
        if (is_free(i-m-1) && is_owner(1-c,i-1) && is_owner(1-c,i+m))
            return true;

        return false;
    }

    boolean is_owner(int c, int i){

        return in(i) && tab_color[c][i/m][i%m];

    }

    boolean is_free(int n){
        return !is_owner(0,n) && !is_owner(1,n);
    }



    boolean is_connected( int n, int p){

        return (in(p) && in(n) && (is_neighbor(n,p) || is_semi_linked(n,p)));

    }

    boolean in (int n){
        return !(n/m == 0 && n%m == 0 || n/m == m - 1 && n%m == 0 ||n/m == 0 && n%m == m - 1 ||n/m == m - 1 && n%m == m - 1);
    }

    boolean is_winning(){
        boolean already_seen[]= new boolean[m * m];
        int n;
        int k;
        for (int i = 0; i < m*m; i++)
            already_seen[i] = false;
        int q =new_queue();
        if (color == 0)
            for (int i = 1; i < m - 1; i++) {
                enqueue(q, i);
                already_seen[i] = true;
            }
        else{
            for (int i = 1; i < m - 1; i++){
                enqueue(q, m * i);
                already_seen[m * i] = true;
            }
        }
        while (!is_empty(q)){
            n = dequeue(q);
            for (int i = 1; i< m - 1; i++){
                for (int j = 1; j < m - 1; j++){
                    k = m * i + j;
                    if (!already_seen[k] && is_owner(color, k) && is_neighbor(n,k)) {
                        if (i == m - 2 && color == 0 || j == m - 2 && color == 1)
                            return true;
                        enqueue(q, k);
                        already_seen[k] = true;
                    }

                }
            }

        }
        free(q);
        return false;
    }

    void end(){

        if (is_winning()) {
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
        for (int i=0; i < m; i++)
            for (int j=0; j<m; j++){
                tab_color[0][i][j] = false;
                tab_color[1][i][j] = false;
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
        for (int i = 1; i< m-1; i++)
            for (int j= 1; j < m-1; j++){
                b=findViewById(tab_button[i][j]);
                b.setBackgroundResource(R.drawable.white_hexagon);
                tab_color[0][i][j] = false;
                tab_color[1][i][j] = false;
            }
        if (nb_players == 0)
            play_computer();
    }



    int compute_weight(int n, int c, boolean semi_linked) {

        int w = 0;
        boolean already_seen[]= new boolean[m*m];
        boolean connected[]= new boolean[m*m];
        int k;

        for (int i = 0; i < m*m; i++) {
            if (in(i)) {
                connected[i] = ((semi_linked && is_semi_linked(n, i) || !semi_linked && is_connected(n, i)) && is_owner(c, i));
                already_seen[i] = false;
            }

        }
        int q1;
        int q2;

        for (int p = 0; p < m*m; p++) {

            if (in(p) && connected[p]) {

                w++;

                q1 = new_queue();
                enqueue(q1, p);

                while (!is_empty(q1)) {

                    q2 = new_queue();

                    while (!is_empty(q1)) {

                        k = dequeue(q1);

                        for (int i = 0; i < m*m ; i++) {

                            if (in(i) && is_connected(k, i) && is_owner(c, i) && !already_seen[i]) {

                                already_seen[i] = true;
                                enqueue(q2, i);

                            }
                        }
                    }
                    free (q1);
                    q1 = q2;

                }

                free (q1);

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


    boolean []component(int n, int c){
        boolean connected[]= new boolean[m*m];
        for (int i=0; i<m*m; i++) {
            connected[i] = false;
        }
        int q =new_queue();
        enqueue(q, n);
        connected[n] = true;
        int k;
        while (!is_empty(q)) {
            k = dequeue(q);
            for (int i = 0; i < m * m; i++) {
                if (is_connected(i, k) && is_owner(c, i) && !connected[i]) {
                    enqueue(q, i);
                    connected[i] = true;
                }
            }
        }
        free(q);
        return connected;
    }

    boolean []next(int n, int c, boolean already_seen[], boolean end_component[], boolean semi_linked, boolean under_semi_linked){
        boolean connected[]= new boolean[m*m];
        boolean next[]= new boolean[m*m];
        for (int i=0; i<m*m; i++) {
            next[i] = false;
            connected[i] = false;
        }
        int q =new_queue();
        enqueue(q, n);
        connected[n] = true;
        int k;
        while (!is_empty(q)){
            k = dequeue(q);
            for (int i = 0; i < m*m; i++){
                if (is_owner(c,i) && ((!semi_linked && is_neighbor(i,k)) || (semi_linked && is_connected(i,k)))  && !connected[i]){
                    enqueue(q,i);
                    connected[i] = true;
                }
                if (((!semi_linked && is_neighbor(i,k)) || (semi_linked && is_connected(i,k))) && (is_free(i) && (!under_semi_linked || !is_under_semi_linked(i,c)) && !already_seen[i] && !connected[i] || end_component[i])){
                    next[i] = true;
                }
            }
        }
        free(q);
        return next;
    }

    boolean [] path_intersection(boolean under_semi_linked){
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
        int q1;
        int q2;
        int q3;
        for (int c = 0; c<2; c++){
            for (int i = 0; i< m*m; i++) {
                already_seen[i] = false;
                already_seen2[i] = false;
                already_seen3[i] = false;
            }
            if (c == 0){
                begin_component = component(1,c);
                end_component = component(m*m - 2, c);
            }
            else{
                begin_component = component(m,c);
                end_component = component(m*m - m - 1, c);
            }
            q1 = new_queue();
            q3 = new_queue();
            fin = false;
            for (int i= 0; i< m*m; i++) {
                if (begin_component[i]) {
                    enqueue(q1, i);
                    already_seen[i] = true;
                    if (end_component[i]) {
                        fin = true;
                        enqueue(q3,i);
                        already_seen3[i] = true;
                    }
                }
            }
            while (!fin){
                q2 =new_queue();
                while (!is_empty(q1)){
                    k=dequeue(q1);
                    next= next(k,c, already_seen,end_component,false, under_semi_linked && c == 1 - color);
                    for (int i = 0; i<m*m; i++){
                        if (next[i]){
                            if (end_component[i]&& !already_seen3[i]){
                                fin =true;
                                enqueue(q3,i);
                                already_seen3[i] = true;
                            }
                            if (!already_seen2[i]){
                                enqueue(q2, i);
                                already_seen2[i] = true;
                            }
                            previous[c][i][k] = true;
                        }
                    }
                }
                for (int i = 0; i< m*m; i++)
                    already_seen[i] = already_seen2[i];
                free(q1);
                q1 = q2;
                if (is_empty(q1)){
                    free(q1);
                    return path_intersection(false);
                }
            }
            free(q1);
            while (!is_empty(q3)){
                k=dequeue(q3);
                path[c][k] = true;
                for (int i =0; i<m*m; i++){
                    if (previous[c][k][i] && !already_seen3[i]){
                        enqueue(q3, i);
                        already_seen3[i] = true;
                    }
                }
            }
            free(q3);
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
        if (!is_free(n))
            return m*m;
        tab_color[color][n/m][n%m] = true;
        boolean already_seen[] = new boolean[m*m];
        boolean next[];
        boolean begin_component[];
        boolean end_component[];
        for (int i = 0; i<m*m; i++){
            already_seen[i] = false;
        }
        int q1 = new_queue();
        int q2;
        if (c == 0){
            begin_component = component(1,c);
            end_component = component(m*m - 2, c);
        }
        else{
            begin_component = component(m,c);
            end_component = component(m*m - m - 1, c);
        }
        for (int i = 0; i < m*m; i++){
            if (begin_component[i]){
                enqueue(q1, i);
                already_seen[i] = true;
                if (end_component[i]){
                    free(q1);
                    tab_color[color][n/m][n%m] = false;
                    return 0;
                }
            }
        }
        int l;
        int k;
        for (l = 0; true ; l++) {
            q2 = new_queue();
            while (!is_empty(q1)) {
                k = dequeue(q1);
                next = next(k, c, already_seen, end_component, true, under_semi_linked);
                for (int i = 0; i < m * m; i++) {
                    if (next[i] && !already_seen[i]) {
                        enqueue(q2, i);
                        already_seen[i] = true;
                        if (end_component[i]) {
                            free(q1);
                            free(q2);
                            tab_color[color][n/m][n%m] = false;
                            return l;
                        }
                    }
                }

            }
            free(q1);
            q1 = q2;
            if (is_empty(q1)){
                free(q1);
                tab_color[color][n/m][n%m] = false;
                return m*m;
            }
        }
    }

    int weight_path(int n){
        return  path_length(color,1-color, n, true) - path_length(1 - color,1-color, n, true) - path_length(color, color,n,true);
    }
    int weigth_connected(int n){
        return compute_weight(n,color, true) + compute_weight(n, 1 - color, true);
    }

    int [] weight_path(){
        int [] weight = new int[m * m];
        for (int i = 0; i < m * m; i++){
            if (is_free(i))
                weight[i] = weight_path(i);
            else
                weight[i] = - m * m;
        }
        return  weight;
    }

    int move_computer(){
        boolean path_intersection[] = path_intersection(true);
        int best_move[] = new int[m * m];
        int length = 0;
        for (int i = 0; i < m * m; i++) {
            if (is_free(i) && path_intersection[i]) {
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
        return move;
    }


    synchronized boolean can_play(int p){
        if (play && !fin && is_free(p) ){
            play = false;
            return true;
        }
        return false;
    }

    void play_computer(){
        int move= move_computer();
        Button b=findViewById(tab_button[move/m][move%m]);
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
        tab_color[color][move/m][move%m] = true;
        s.play(objectAnimator);
        s.start();
    }

    void play(final int i, final int j){
        final Button button = findViewById(tab_button[i][j]);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (can_play(i*m+j)){
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
        for (int i = 0; i< m*m; i++){
            free[i] = true;
        }
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