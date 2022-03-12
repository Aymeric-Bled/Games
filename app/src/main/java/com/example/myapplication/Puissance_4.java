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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class Puissance_4 extends AppCompatActivity {
    private Button main;
    private Spinner player;
    private String players[] = {"1 joueur","2 joueurs","0 joueur"};
    private int nb_players = 1;
    private Button new_;
    private Spinner first;
    private String first_play[] = {"Le joueur commence","L'ordinateur commence"};
    private boolean player_begin = true;
    private Table tab;
    private int width = 7;
    private int height = 6;
    private int t[][] = new int[height][width];
    private boolean couleur = true;
    private boolean fin = false;
    private boolean debut = true;
    private boolean play = true;
    private AnimatorSet s;


    void create_puissance_4(){
        Button b;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w = (metrics.widthPixels - width*20)/ width;
        GridLayout grille= findViewById(R.id.grille);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,w);
        params.setMargins(10,10,10,10);
        tab = new Table(grille, height, width, this, params, w);
        for (int i=0; i < height; i++)
            for (int j=0; j<width; j++) {
                b = tab.getButton(i,j);
                b.setBackgroundResource(R.drawable.whitebutton);
            }
    }

    void new_(){
        if (s != null){
            s.pause();
        }
        fin=false;
        debut=true;
        Button b;
        for (int i=0; i<height;i++)
            for(int j=0; j<width; j++){
                t[i][j]=0;
                b=tab.getButton(i,j);
                b.setBackgroundResource(R.drawable.whitebutton);
            }

        if (nb_players == 2) {
            first.setAlpha(0);
            first.setClickable(false);
            play = true;
        }
        else if (nb_players == 1) {
            first.setAlpha(1);
            first.setClickable(true);
            if (player_begin) {
                play = true;
            } else {
                play = false;
                play_computer();
            }
        }
        else{
            first.setAlpha(0);
            first.setClickable(false);
            play = false;
            play_computer();
        }
    }

    int place(Button b){
        for (int i=0; i<width*height; i++){
            if (tab.getButton(i/width, i%width)==b) return i;
        }
        return -1;
    }

    boolean present(int x, int s[], int len){
        for (int i=0; i<len; i++){
            if (s[i]==x) return true;
        }
        return false;
    }

    int ligne(int t[][],int n, int p, int s[],int len){
        if (len==1){
            int y=p-1;
            int j=p+1;
            while (y>=0 && present(t[n][y],s,len)) y--;
            while (j<width && present(t[n][j],s,len)) j++;
            return j-y-1;
        }
        int y=p-1;
        int j=p+1;
        int l1=-1;
        int l2=1;
        while (y>=0 && present(t[n][y],s,len)) {
            if (p - y <= 3 && t[n][y] == s[0]) l1--;
            y--;
        }
        while (j<width && present(t[n][j],s,len)) {
            if (j - p <= 3 && t[n][j] == s[0]) l2++;
            j++;
        }
        if (j-y-1>3) return l2-l1-1;
        return 0;
    }
    int colonne(int t[][],int n, int p, int s[],int len){
        if (len==1){
            int x=n-1;
            int i=n+1;
            while (x>=0 && present(t[x][p],s,len)) x--;
            while (i<height && present(t[i][p],s,len)) i++;
            return i-x-1;
        }
        int x=n-1;
        int i=n+1;
        int c1=-1;
        int c2=1;
        while (x>=0 && present(t[x][p],s,len)) {
            if (n - x <= 3 && t[x][p] == s[0]) c1--;
            x--;
        }
        while (i<height && present(t[i][p],s,len)) {
            if (i - n <= 3 && t[i][p] == s[0]) c2++;
            i++;
        }
        if (i-x-1>3) return c2-c1-1;
        return 0;
    }



    int diagonale_1(int t[][],int n, int p, int s[],int len){
        if (len==1){
            int a=1;
            int b=-1;
            while (n+a<height && p+a<width && present(t[n+a][p+a],s,len)) a++;
            while (n+b>=0 && p+b>=0 && present(t[n+b][p+b],s,len)) b--;
            return a-b-1;
        }
        int a=1;
        int b=-1;
        int d1=1;
        int d2=-1;
        while (n+a<height && p+a<width && present(t[n+a][p+a],s,len)) {
            if (a <= 3 && t[n + a][p + a] == s[0]) d1++;
            a++;
        }
        while (n+b>=0 && p+b>=0 && present(t[n+b][p+b],s,len)) {
            if (-b <= 3 && t[n + b][p + b] == s[0]) d2--;
            b--;
        }
        if (a-b-1>3) return d1-d2-1;
        return 0;
    }

    int diagonale_2(int t[][],int n, int p, int s[],int len){
        if (len==1){
            int a=1;
            int b=1;
            while (n+a<height && p-a>=0 && present(t[n+a][p-a],s,len)) a++;
            while (n-b>=0 && p+b<width && present(t[n-b][p+b],s,len)) b++;
            return a+b-1;
        }
        int a=1;
        int b=1;
        int d1=1;
        int d2=1;
        while (n+a<height && p-a>=0 && present(t[n+a][p-a],s,len)) {
            if (a <= 3 && t[n + a][p - a] == s[0]) d1++;
            a++;
        }
        while (n-b>=0 && p+b<width && present(t[n-b][p+b],s,len)) {
            if (b <= 3 && t[n - b][p + b] == s[0]) d2++;
            b++;
        }
        if (a+b-1>3) return d1+d2-1;
        return 0;
    }

    int[][] copy(int B[][]){
        int C[][]=new int[height][width];
        for (int i=0;i<height; i++)
            for (int j=0; j<width; j++)
                C[i][j]=B[i][j];
        return C;
    }
    boolean coup_victoire(int c,boolean couleur){
        return coup_victoire(t,c,couleur);
    }
    boolean coup_victoire(int t[][],int c,boolean couleur){
        int i=0;
        while (i < height && t[i][c] == 0) i++;
        int s[]=new int [1];
        if (couleur) {
            s[0]=1;
        }
        else{
            s[0]=2;
        }
        return (ligne(t,i-1,c,s,1)>=4 || colonne(t,i-1,c,s,1)>=4 ||diagonale_1(t,i-1,c,s,1)>=4 ||diagonale_2(t,i-1,c,s,1)>=4);
    }
    boolean coup_gagnant_r(int C[][],int c,boolean couleur,int nb){
        if (nb<=8){
            int B[][]=copy(C);
            int i=0;
            while (i < height && B[i][c] == 0) i++;
            if (couleur) {
                B[i - 1][c] = 1;
            } else {
                B[i - 1][c] = 2;
            }
            int s[]=new int [1];
            if (couleur) {
                s[0]=1;
            }
            else{
                s[0]=2;
            }
            if(ligne(B,i-1,c,s,1)>=4 || colonne(B,i-1,c,s,1)>=4 ||diagonale_1(B,i-1,c,s,1)>=4 ||diagonale_2(B,i-1,c,s,1)>=4){
                return true;
            }
            c=0;
            boolean b=true;
            while (b && c<width){
                if (B[0][c]==0) b=coup_perdant_r(B,c,!couleur,nb+1);
                c++;
            }
            return b;
        }
        return false;
    }
    boolean coup_gagnant(int c,boolean couleur){
        return coup_gagnant_r(t,c,couleur,0);
    }
    boolean coup_perdant_r(int C[][],int c,boolean couleur,int nb){
        if (coup_victoire(C,c,couleur)) return false;
        if (nb<=8){
            int B[][]=copy(C);
            int i=0;
            while (i < height && B[i][c] == 0) i++;
            if (couleur) {
                B[i - 1][c] = 1;
            } else {
                B[i - 1][c] = 2;
            }
            int s[]=new int [1];
            if (couleur) {
                s[0]=1;
            }
            else{
                s[0]=2;
            }
            c=0;
            boolean b=false;
            while (!b && c<width){
                if (B[0][c]==0) b=coup_gagnant_r(B,c,!couleur,nb+1);
                c++;
            }
            return b;
        }
        return false;
    }

    boolean coup_perdant(int c,boolean couleur){
        return coup_perdant_r(t,c,couleur,0);
    }
    boolean coup_secour(int c, boolean couleur){
        int i=0;
        while (i < height && t[i][c] == 0) i++;
        int s[]=new int [1];
        if (couleur) {
            s[0]=2;
        }
        else{
            s[0]=1;
        }
        return (ligne(t,i-1,c,s,1)>=4 || colonne(t,i-1,c,s,1)>=4 ||diagonale_1(t,i-1,c,s,1)>=4 ||diagonale_2(t,i-1,c,s,1)>=4);
    }

    boolean coup_donne(int c,boolean couleur){
        int i=0;
        while (i < height && t[i][c] == 0) i++;
        int s[]=new int [1];
        if (couleur) {
            s[0]=2;
        }
        else{
            s[0]=1;
        }
        if (t[1][c]==0){
            return (ligne(t,i-2,c,s,1)>=4 || colonne(t,i-2,c,s,1)>=4 ||diagonale_1(t,i-2,c,s,1)>=4 ||diagonale_2(t,i-2,c,s,1)>=4);
        }
        return false;
    }
    boolean coup_gache(int c,boolean couleur){
        int i=0;
        while (i < height && t[i][c] == 0) i++;
        int s[]=new int [1];
        if (couleur) {
            s[0]=1;
        }
        else{
            s[0]=2;
        }
        if (t[1][c]==0){
            return (ligne(t,i-2,c,s,1)>=4 || colonne(t,i-2,c,s,1)>=4 ||diagonale_1(t,i-2,c,s,1)>=4 ||diagonale_2(t,i-2,c,s,1)>=4);
        }
        return false;
    }

    void fin(int i){
        int n=i/width;
        int p=i%width;
        int s[]=new int[1];
        if (couleur) {
            s[0]=1;
        }
        else{
            s[0]=2;
        }
        if(ligne(t,n,p,s,1)>=4 || colonne(t,n,p,s,1)>=4 ||diagonale_1(t,n,p,s,1)>=4 ||diagonale_2(t,n,p,s,1)>=4){
            fin=true;
            AlertDialog.Builder fin = new AlertDialog.Builder(this);
            fin.setTitle("Puissance 4 !!!");
            if (couleur) {
                fin.setMessage("L'équipe rouge a gagné");
            }
            else{
                fin.setMessage("L'équipe jaune a gagné");
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
        }

        int c = 0;
        while (c < width && t[0][c] != 0)
            c++;
        if (c >= width){
            fin=true;
            AlertDialog.Builder fin = new AlertDialog.Builder(this);
            fin.setTitle("Macth nul");
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
        }
    }
    ArrayList<Integer> list(int f, ArrayList<Integer> p, boolean couleur){
        ArrayList<Integer> l = new ArrayList<>();
        if (f==0)
            for (int i=0; i<p.size() && p.get(i)>=0 ;i++){
                if (t[0][ p.get(i)]==0){
                    l.add(p.get(i));
                }
            }
        if (f==1)
            for (int i=0; i<p.size() &&  p.get(i)>=0 ;i++){
                if (coup_victoire(p.get(i),couleur)){
                    l.add(p.get(i));
                }
            }
        if (f==2)
            for (int i=0; i<p.size() && p.get(i)>=0 ;i++){
                if (coup_gagnant(p.get(i),couleur)){
                    l.add(p.get(i));
                }
            }
        if (f==3)
            for (int i=0; i<p.size() && p.get(i)>=0 ;i++){
                if (!coup_perdant(p.get(i),couleur)){
                    l.add(p.get(i));
                }
            }
        if (f==4)
            for (int i=0; i<p.size() && p.get(i)>=0 ;i++){
                if (coup_secour(p.get(i),couleur)){
                    l.add(p.get(i));
                }
            }
        if (f==5)
            for (int i=0; i<p.size() && p.get(i)>=0 ;i++){
                if (!coup_donne(p.get(i),couleur)){
                    l.add(p.get(i));
                }
            }
        if (f==height)
            for (int i=0; i<p.size() && p.get(i)>=0 ;i++){
                if (!coup_gache(p.get(i),couleur)){
                    l.add(p.get(i));
                }
            }
        return l;
    }

    int max(int a,int b,int c, int d){
        if (a>=b && a>=c && a>=d) return a;
        if (b>=c && b>=d) return b;
        if (c>=d) return c;
        return d;
    }
    int coup_smart(ArrayList<Integer> l){
        ArrayList<Integer> bestMoves = new ArrayList<>();
        bestMoves.add(l.get(0));
        int i=0;
        while (i < height && t[i][0] == 0) i++;
        int s[]=new int [2];
        if (couleur) {
            s[0]=1;
            s[1]=0;
        }
        else{
            s[0]=2;
            s[1]=0;
        }
        int m= max(ligne(t,i-1,l.get(0),s,2),ligne(t,i-1,l.get(0),s,2),diagonale_1(t,i-1,l.get(0),s,2),diagonale_2(t,i-1,l.get(0),s,2));
        for (int j=1; j<l.size();j++){
            i=0;
            while (i < height && t[i][l.get(j)] == 0) i++;
            int n=max(ligne(t,i-1,l.get(j),s,2),ligne(t,i-1,l.get(j),s,2),diagonale_1(t,i-1,l.get(j),s,2),diagonale_2(t,i-1,l.get(j),s,2));
            if (n>m){
                m=n;
                bestMoves.clear();
                bestMoves.add(l.get(j));
            }
            else if (n == m){
                bestMoves.add(l.get(j));
            }

        }
        return bestMoves.get((int)(Math.random() * bestMoves.size()));

    }

    int move_computer(){
        ArrayList<Integer> l = new ArrayList<>();
        for (int j = 0; j < width; j++){
            l.add(j);
        }
        l=list(0,l,couleur);
        ArrayList<Integer> v=list(1,l,couleur);
        if (!v.isEmpty()) return v.get((int)(Math.random() * v.size()));
        ArrayList<Integer> s=list(4,l,couleur);
        if (!s.isEmpty()) return s.get((int)(Math.random() * s.size()));
        ArrayList<Integer> g=list(2,l,couleur);
        if (!g.isEmpty()) return g.get((int)(Math.random() * g.size()));
        ArrayList<Integer> d=list(5,l,couleur);
        if (!d.isEmpty()) return l.get((int)(Math.random() * l.size()));
        ArrayList<Integer> p=list(3,d,couleur);
        if (p.isEmpty()){
            ArrayList<Integer> ga_p=list(height,p,couleur);
            if (!ga_p.isEmpty()){
                return coup_smart(ga_p);
            }
            return coup_smart(p);
        }
        ArrayList<Integer> ga_d=list(height,d,couleur);
        if (!ga_d.isEmpty()){
            return coup_smart(ga_d);
        }
        return coup_smart(d);
    }

    void play_computer() {
        int p = move_computer();
        int i = 0;
        if (t[i][p % width] == 0) {
            s = new AnimatorSet();
            final ArrayList<Animator> anim = new ArrayList<>();
            while (i < height && t[i][p % width] == 0) i++;
            if (couleur) {
                t[i - 1][p % width] = 1;
            } else {
                t[i - 1][p % width] = 2;
            }

            for (int l = 0; l < i; l++) {
                if (couleur) {
                    ObjectAnimator objectAnimator = ObjectAnimator.ofObject(tab.getButton(p / width, p % width), "backgroundResource", new ArgbEvaluator(), R.drawable.redbutton, R.drawable.redbutton);
                    objectAnimator.setDuration(100);
                    if (l > 0) {
                        ObjectAnimator objectAnimator2 = ObjectAnimator.ofObject(tab.getButton((p - width) / width, (p - width) % width), "backgroundResource", new ArgbEvaluator(), R.drawable.whitebutton, R.drawable.whitebutton);
                        objectAnimator2.setDuration(100);
                        s.play(anim.get(anim.size() - 1)).before(objectAnimator).before(objectAnimator2);
                        anim.add(objectAnimator2);
                    } else {
                        s.play(objectAnimator);
                    }
                    anim.add(objectAnimator);
                } else {
                    ObjectAnimator objectAnimator = ObjectAnimator.ofObject(tab.getButton(p / width, p % width), "backgroundResource", new ArgbEvaluator(), R.drawable.yellowbutton, R.drawable.yellowbutton);
                    objectAnimator.setDuration(100);
                    if (l > 0) {
                        ObjectAnimator objectAnimator2 = ObjectAnimator.ofObject(tab.getButton((p - width) / width, (p - width) % width), "backgroundResource", new ArgbEvaluator(), R.drawable.whitebutton, R.drawable.whitebutton);
                        objectAnimator2.setDuration(100);
                        s.play(anim.get(anim.size() - 1)).before(objectAnimator).before(objectAnimator2);
                        anim.add(objectAnimator2);
                    } else {
                        s.play(objectAnimator);
                    }
                    anim.add(objectAnimator);

                }
                p += width;
            }
            final int position = p - width;
            anim.get(anim.size() - 1).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(anim.get(anim.size() - 1));
                    fin(position);
                    couleur = !couleur;
                    if (!fin && nb_players == 0)
                        play_computer();
                    else
                        play = true;
                }
            });
            s.start();
        } else {
            Toast toast = Toast.makeText(this, "test", Toast.LENGTH_SHORT);
            toast.show();
            play = true;
        }

    }


    synchronized boolean can_play(){
        if (!fin && play){
            play = false;
            return true;
        }
        return false;
    }

    void play(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (can_play()) {
                    int p = place(button);
                    int i = 0;
                    if (t[i][p % width] == 0) {
                        while (i < height && t[i][p % width] == 0) i++;
                        if (couleur) {
                            t[i - 1][p % width] = 1;
                        } else {
                            t[i - 1][p % width] = 2;
                        }
                        s = new AnimatorSet();
                        final ArrayList<Animator> anim = new ArrayList<>();
                        p = p % width;
                        for (int l = 0; l < i; l++) {
                            if (couleur) {
                                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(tab.getButton(p / width, p % width), "backgroundResource", new ArgbEvaluator(), R.drawable.redbutton, R.drawable.redbutton);
                                objectAnimator.setDuration(100);
                                if (l > 0) {
                                    ObjectAnimator objectAnimator2 = ObjectAnimator.ofObject(tab.getButton((p - width) / width, (p - width) % width), "backgroundResource", new ArgbEvaluator(), R.drawable.whitebutton, R.drawable.whitebutton);
                                    objectAnimator2.setDuration(100);
                                    s.play(anim.get(anim.size() - 1)).before(objectAnimator).before(objectAnimator2);
                                    anim.add(objectAnimator2);

                                } else {
                                    s.play(objectAnimator);
                                }
                                anim.add(objectAnimator);
                            } else {
                                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(tab.getButton(p / width, p % width), "backgroundResource", new ArgbEvaluator(), R.drawable.yellowbutton, R.drawable.yellowbutton);
                                objectAnimator.setDuration(100);
                                if (l > 0) {
                                    ObjectAnimator objectAnimator2 = ObjectAnimator.ofObject(tab.getButton((p - width) / width, (p - width) % width), "backgroundResource", new ArgbEvaluator(), R.drawable.whitebutton, R.drawable.whitebutton);
                                    objectAnimator2.setDuration(100);
                                    s.play(anim.get(anim.size() - 1)).before(objectAnimator).before(objectAnimator2);
                                    anim.add(objectAnimator2);
                                } else {
                                    s.play(objectAnimator);
                                }
                                anim.add(objectAnimator);

                            }
                            p += width;
                        }

                        final int position = p - width;
                        anim.get(anim.size() - 1).addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(anim.get(anim.size() - 1));
                                fin(position);
                                couleur = !couleur;
                                if (!fin && nb_players < 2) {
                                    play_computer();
                                } else {
                                    play = true;
                                }
                            }
                        });
                        s.start();
                    }
                    else{
                        play = true;
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puissance_4);
        create_puissance_4();
        this.main = findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (s != null){
                    s.pause();
                }
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

        this.first = (Spinner) findViewById(R.id.first);

        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, first_play);
        first.setAdapter(spinnerArrayAdapter);
        first.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                player_begin = position == 0;
                new_();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (int i=0; i<height; i++)
            for (int j=0; j < width; j++)
                play(tab.getButton(i, j));
        new_();
    }
}
