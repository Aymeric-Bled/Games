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
    private int t[][] = {{0,0,0,0,0,0,0},{0,0,0,0,0,0,0},{0,0,0,0,0,0,0},{0,0,0,0,0,0,0},{0,0,0,0,0,0,0},{0,0,0,0,0,0,0}};
    private boolean couleur = true;
    private boolean fin = false;
    private boolean debut = true;
    private boolean play = true;
    private AnimatorSet s;


    void create_puissance_4(){
        Button b;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w = (metrics.widthPixels - 7*20)/ 7;
        GridLayout grille= findViewById(R.id.grille);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,w);
        params.setMargins(10,10,10,10);
        tab = new Table(grille, 6, 7, this, params, w);
        for (int i=0; i < 6; i++)
            for (int j=0; j<7; j++) {
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
        for (int i=0; i<6;i++)
            for(int j=0; j<7; j++){
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
        for (int i=0; i<42; i++){
            if (tab.getButton(i/7, i%7)==b) return i;
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
            while (j<7 && present(t[n][j],s,len)) j++;
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
        while (j<7 && present(t[n][j],s,len)) {
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
            while (i<6 && present(t[i][p],s,len)) i++;
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
        while (i<6 && present(t[i][p],s,len)) {
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
            while (n+a<6 && p+a<7 && present(t[n+a][p+a],s,len)) a++;
            while (n+b>=0 && p+b>=0 && present(t[n+b][p+b],s,len)) b--;
            return a-b-1;
        }
        int a=1;
        int b=-1;
        int d1=1;
        int d2=-1;
        while (n+a<6 && p+a<7 && present(t[n+a][p+a],s,len)) {
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
            while (n+a<6 && p-a>=0 && present(t[n+a][p-a],s,len)) a++;
            while (n-b>=0 && p+b<7 && present(t[n-b][p+b],s,len)) b++;
            return a+b-1;
        }
        int a=1;
        int b=1;
        int d1=1;
        int d2=1;
        while (n+a<6 && p-a>=0 && present(t[n+a][p-a],s,len)) {
            if (a <= 3 && t[n + a][p - a] == s[0]) d1++;
            a++;
        }
        while (n-b>=0 && p+b<7 && present(t[n-b][p+b],s,len)) {
            if (b <= 3 && t[n - b][p + b] == s[0]) d2++;
            b++;
        }
        if (a+b-1>3) return d1+d2-1;
        return 0;
    }

    int[][] copy(int B[][]){
        int C[][]=new int[6][7];
        for (int i=0;i<6; i++)
            for (int j=0; j<7; j++)
                C[i][j]=B[i][j];
        return C;
    }
    boolean coup_victoire(int c,boolean couleur){
        return coup_victoire(t,c,couleur);
    }
    boolean coup_victoire(int t[][],int c,boolean couleur){
        int i=0;
        while (i < 6 && t[i][c] == 0) i++;
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
            while (i < 6 && B[i][c] == 0) i++;
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
            while (b && c<7){
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
            while (i < 6 && B[i][c] == 0) i++;
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
            while (!b && c<7){
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
        while (i < 6 && t[i][c] == 0) i++;
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
        while (i < 6 && t[i][c] == 0) i++;
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
        while (i < 6 && t[i][c] == 0) i++;
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
        int n=i/7;
        int p=i%7;
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
        while (c < 7 && t[0][c] != 0)
            c++;
        if (c >= 7){
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
    int [] list(int f,int p[], boolean couleur){
        int l[]={-1,-1,-1,-1,-1,-1,-1};
        int j=0;
        if (f==0)
            for (int i=0; i<7 && p[i]>=0 ;i++){
                if (t[0][p[i]]==0){
                    l[j++]=p[i];
                }
            }
        if (f==1)
            for (int i=0; i<7 && p[i]>=0 ;i++){
                if (coup_victoire(p[i],couleur)){
                    l[j++]=p[i];
                }
            }
        if (f==2)
            for (int i=0; i<7 && p[i]>=0 ;i++){
                if (coup_gagnant(p[i],couleur)){
                    l[j++]=p[i];
                }
            }
        if (f==3)
            for (int i=0; i<7 && p[i]>=0 ;i++){
                if (!coup_perdant(p[i],couleur)){
                    l[j++]=p[i];
                }
            }
        if (f==4)
            for (int i=0; i<7 && p[i]>=0 ;i++){
                if (coup_secour(p[i],couleur)){
                    l[j++]=p[i];
                }
            }
        if (f==5)
            for (int i=0; i<7 && p[i]>=0 ;i++){
                if (!coup_donne(p[i],couleur)){
                    l[j++]=p[i];
                }
            }
        if (f==6)
            for (int i=0; i<7 && p[i]>=0 ;i++){
                if (!coup_gache(p[i],couleur)){
                    l[j++]=p[i];
                }
            }
        return l;
    }
    int longueur(int l[]){
        int i;
        for (i=0; i<7; i++)
            if (l[i]==-1) return i;
        return i;
    }

    int [] alea(int l[]){
        int a[]={-1,-1,-1,-1,-1,-1,-1};
        int i=0;
        int n=longueur(l);
        while (n!=0){
            int j=(int) (Math.random() * n);
            a[i++]=l[j];
            for (int k=j; k<n-1;k++){
                l[k]=l[k+1];
            }
            n--;
        }
        return a;

    }
    int max(int a,int b,int c, int d){
        if (a>=b && a>=c && a>=d) return a;
        if (b>=c && b>=d) return b;
        if (c>=d) return c;
        return d;
    }
    int coup_smart(int l[]){
        int c=l[0];
        int i=0;
        while (i < 6 && t[i][c] == 0) i++;
        int s[]=new int [2];
        if (couleur) {
            s[0]=1;
            s[1]=0;
        }
        else{
            s[0]=2;
            s[1]=0;
        }
        int m= max(ligne(t,i-1,c,s,2),ligne(t,i-1,c,s,2),diagonale_1(t,i-1,c,s,2),diagonale_2(t,i-1,c,s,2));
        for (int j=1; j<longueur(l);j++){
            i=0;
            while (i < 6 && t[i][l[j]] == 0) i++;
            int n=max(ligne(t,i-1,l[j],s,2),ligne(t,i-1,l[j],s,2),diagonale_1(t,i-1,l[j],s,2),diagonale_2(t,i-1,l[j],s,2));
            if (n>m){
                m=n;
                c=l[j];
            }

        }
        return c;

    }

    int move_computer(){
        int l[]={0,1,2,3,4,5,6};
        l=list(0,l,couleur);
        int v[]=list(1,l,couleur);
        if (v[0]!=-1) return v[(int)(Math.random() * (longueur(v)))];
        int s[]=list(4,l,couleur);
        if (s[0]!=-1) return s[(int)(Math.random() * (longueur(s)))];
        int g[]=list(2,l,couleur);
        if (g[0]!=-1) return g[(int)(Math.random() * (longueur(g)))];
        int d[]=list(5,l,couleur);
        if (d[0]==-1) return l[(int)(Math.random() * (longueur(l)))];
        int p[]=list(3,d,couleur);
        if (p[0]!=-1){
            int ga_p[]=list(6,p,couleur);
            if (ga_p[0]!=-1){
                return coup_smart(alea(ga_p));
            }
            return coup_smart(alea(p));
        }
        int ga_d[]=list(6,d,couleur);
        if (ga_d[0]!=-1){
            return coup_smart(alea(ga_d));
        }
        return coup_smart(alea(d));
    }

    void play_computer() {
        int p = move_computer();
        int i = 0;
        int ind = 0;
        if (t[i][p % 7] == 0) {
            s = new AnimatorSet();
            final Animator anim[] = new Animator[13];
            while (i < 6 && t[i][p % 7] == 0) i++;
            if (couleur) {
                t[i - 1][p % 7] = 1;
            } else {
                t[i - 1][p % 7] = 2;
            }

            for (int l = 0; l < i; l++) {
                if (couleur) {
                    ObjectAnimator objectAnimator = ObjectAnimator.ofObject(tab.getButton(p / 7, p % 7), "backgroundResource", new ArgbEvaluator(), R.drawable.redbutton, R.drawable.redbutton);
                    objectAnimator.setDuration(100);
                    if (l > 0) {
                        ObjectAnimator objectAnimator2 = ObjectAnimator.ofObject(tab.getButton((p - 7) / 7, (p - 7) % 7), "backgroundResource", new ArgbEvaluator(), R.drawable.whitebutton, R.drawable.whitebutton);
                        objectAnimator2.setDuration(100);
                        s.play(anim[ind - 1]).before(objectAnimator).before(objectAnimator2);
                        anim[ind++] = objectAnimator2;
                    } else {
                        s.play(objectAnimator);
                    }
                    anim[ind++] = objectAnimator;
                } else {
                    ObjectAnimator objectAnimator = ObjectAnimator.ofObject(tab.getButton(p / 7, p % 7), "backgroundResource", new ArgbEvaluator(), R.drawable.yellowbutton, R.drawable.yellowbutton);
                    objectAnimator.setDuration(100);
                    if (l > 0) {
                        ObjectAnimator objectAnimator2 = ObjectAnimator.ofObject(tab.getButton((p - 7) / 7, (p - 7) % 7), "backgroundResource", new ArgbEvaluator(), R.drawable.whitebutton, R.drawable.whitebutton);
                        objectAnimator2.setDuration(100);
                        s.play(anim[ind - 1]).before(objectAnimator).before(objectAnimator2);
                        anim[ind++] = objectAnimator2;
                    } else {
                        s.play(objectAnimator);
                    }
                    anim[ind++] = objectAnimator;

                }
                p += 7;
            }
            final int k = ind;
            final int position = p - 7;
            anim[ind - 1].addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(anim[k - 1]);
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
                    if (t[i][p % 7] == 0) {
                        while (i < 6 && t[i][p % 7] == 0) i++;
                        if (couleur) {
                            t[i - 1][p % 7] = 1;
                        } else {
                            t[i - 1][p % 7] = 2;
                        }
                        s = new AnimatorSet();
                        final Animator anim[] = new Animator[13];
                        int ind = 0;
                        p = p % 7;
                        for (int l = 0; l < i; l++) {
                            if (couleur) {
                                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(tab.getButton(p / 7, p % 7), "backgroundResource", new ArgbEvaluator(), R.drawable.redbutton, R.drawable.redbutton);
                                objectAnimator.setDuration(100);
                                if (l > 0) {
                                    ObjectAnimator objectAnimator2 = ObjectAnimator.ofObject(tab.getButton((p - 7) / 7, (p - 7) % 7), "backgroundResource", new ArgbEvaluator(), R.drawable.whitebutton, R.drawable.whitebutton);
                                    objectAnimator2.setDuration(100);
                                    s.play(anim[ind - 1]).before(objectAnimator).before(objectAnimator2);
                                    anim[ind++] = objectAnimator2;

                                } else {
                                    s.play(objectAnimator);
                                }
                                anim[ind++] = objectAnimator;
                            } else {
                                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(tab.getButton(p / 7, p % 7), "backgroundResource", new ArgbEvaluator(), R.drawable.yellowbutton, R.drawable.yellowbutton);
                                objectAnimator.setDuration(100);
                                if (l > 0) {
                                    ObjectAnimator objectAnimator2 = ObjectAnimator.ofObject(tab.getButton((p - 7) / 7, (p - 7) % 7), "backgroundResource", new ArgbEvaluator(), R.drawable.whitebutton, R.drawable.whitebutton);
                                    objectAnimator2.setDuration(100);
                                    s.play(anim[ind - 1]).before(objectAnimator).before(objectAnimator2);
                                    anim[ind++] = objectAnimator2;
                                } else {
                                    s.play(objectAnimator);
                                }
                                anim[ind++] = objectAnimator;

                            }
                            p += 7;
                        }

                        final int k = ind;
                        final int position = p - 7;
                        anim[ind - 1].addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(anim[k - 1]);
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

        for (int i=0; i<6; i++)
            for (int j=0; j < 7; j++)
                play(tab.getButton(i, j));
        new_();
    }
}
