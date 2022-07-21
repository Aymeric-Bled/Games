package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class Sudoku extends AppCompatActivity {
    private ImageView main;
    private ImageView new_;
    private int tab_id[]={R.id.s1,R.id.s2,R.id.s3,R.id.s4,R.id.s5,R.id.s6,R.id.s7,R.id.s8,R.id.s9,R.id.s10,R.id.s11,R.id.s12,R.id.s13,R.id.s14,R.id.s15,R.id.s16,R.id.s17,R.id.s18,R.id.s19,R.id.s20,R.id.s21,R.id.s22,R.id.s23,R.id.s24,R.id.s25,R.id.s26,R.id.s27,R.id.s28,R.id.s29,R.id.s30,R.id.s31,R.id.s32,R.id.s33,R.id.s34,R.id.s35,R.id.s36,R.id.s37,R.id.s38,R.id.s39,R.id.s40,R.id.s41,R.id.s42,R.id.s43,R.id.s44,R.id.s45,R.id.s46,R.id.s47,R.id.s48,R.id.s49,R.id.s50,R.id.s51,R.id.s52,R.id.s53,R.id.s54,R.id.s55,R.id.s56,R.id.s57,R.id.s58,R.id.s59,R.id.s60,R.id.s61,R.id.s62,R.id.s63,R.id.s64,R.id.s65,R.id.s66,R.id.s67,R.id.s68,R.id.s69,R.id.s70,R.id.s71,R.id.s72,R.id.s73,R.id.s74,R.id.s75,R.id.s76,R.id.s77,R.id.s78,R.id.s79,R.id.s80,R.id.s81};
    private int num[]={R.id.un,R.id.deux,R.id.trois,R.id.quatre,R.id.cinq,R.id.six,R.id.sept,R.id.huit,R.id.neuf,R.id.del,0};
    private int t[][]={{0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0}};
    private CharSequence ch[]={"","1","2","3","4","5","6","7","8","9"};
    private int position=-1;
    private int[][]fin=null;
    private boolean cross = false;
    private Spinner grilleSelection;
    private enum Grille {Normal, Croix, Box}
    private int[][] blocs;
    private int[] isInBloc;
    private boolean bloc = false;

    void fin() {
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++) {
                Button button = findViewById(tab_id[i * 9 + j]);
                CharSequence text = button.getText();
                if (text == "" || !(button.getCurrentTextColor() == Color.BLACK || button.getCurrentTextColor() == Color.BLUE))
                    return;
            }
        }
        AlertDialog.Builder fin = new AlertDialog.Builder(this);
        fin.setTitle("Félicitations !!!");
        fin.setMessage("Le sudoku est terminé");
        fin.setPositiveButton("Nouvelle grille", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new_grille();

            }
        });
        fin.setNeutralButton("Fermer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        fin.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();

            }
        });
        fin.show();
    }

    boolean [] ligne(int [][] t,int i){
        boolean [] ligne={false,false,false,false,false,false,false,false,false};
        for (int j=0; j<9;j++){
            if (t[i][j]!=0)
                ligne[t[i][j]-1]=true;
        }
        return ligne;
    }


    boolean [] colonne(int[][] t, int j){
        boolean [] colonne={false,false,false,false,false,false,false,false,false};
        for(int i=0; i<9;i++){
            if (t[i][j]!=0)
                colonne[t[i][j]-1]=true;
        }
        return colonne;
    }

    void initialise_blocs(){
        blocs = new int[9][9];
        isInBloc = new int[81];
        for (int n = 0; n < 9; n++){
            int i = n / 3;
            int j = n % 3;
            int ind = 0;
            for (int x = 0; x < 3; x++){
                for (int y = 0; y < 3; y++){
                    int p = (3 * i + x) * 9 + 3 * j + y;
                    blocs[n][ind++] = p;
                    isInBloc[p] = n;
                }
            }
        }
        //print_tab(isInBloc, 81);
    }
    void print_tab(int [] tab, int length){
        String s = "[";
        boolean coma = false;
        for (int i = 0; i < length; i++){
            if (coma)
                s += ",";
            s += tab[i];
            coma = true;
        }
        s += "]";
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    boolean [] bloc(int[][] t, int i, int j){
        boolean [] bloc={false,false,false,false,false,false,false,false,false};
        int b = isInBloc[9 * i + j];
        for (int n = 0; n < 9; n++){
            int p = blocs[b][n];
            int x = p / 9;
            int y = p % 9;
            if (t[x][y] != 0)
                bloc[t[x][y]-1]=true;
        }
        return bloc;
    }

    void random_blocs(){
        for (int i = 0; i < 100; i++){
            Pair<Integer, Integer> ind = getlegalMove();
            for (int j = 0; j < 9; j++) {
                if (blocs[isInBloc[ind.first]][j] == ind.first) {
                    blocs[isInBloc[ind.first]][j] = ind.second;
                    break;
                }
            }
            for (int j = 0; j < 9; j++) {
                if (blocs[isInBloc[ind.second]][j] == ind.second) {
                    blocs[isInBloc[ind.second]][j] = ind.first;
                    break;
                }
            }
            int bn = isInBloc[ind.first];
            isInBloc[ind.first] = isInBloc[ind.second];
            isInBloc[ind.second] = bn;
        }
    }

    void doColors(){
        Button button;
        if (bloc) {
            ArrayList colors = new ArrayList();
            colors.add(0xFFADD8E6);
            colors.add(0xFFFFCCCB);
            colors.add(0xFFFFFF88);
            colors.add(0xFFFED8B1);
            colors.add(0xFF90EE90);
            colors.add(0xFFCF9FFF);
            colors.add(0xFFFFFFFF);
            colors.add(0xFFC4A484);
            colors.add(0xFFFFB6C1);
            Collections.shuffle(colors);
            for (int i = 0; i < 81; i++) {
                button = findViewById(tab_id[i]);
                button.setBackgroundColor((int) colors.get(isInBloc[i]));
            }
        }
        else {
            for (int i = 0; i < 81; i++) {
                button = findViewById(tab_id[i]);
                button.setBackgroundColor(Color.WHITE);
            }
        }
        for (int i = 0; i < 81; i++) {
            button = findViewById(tab_id[i]);
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) button.getLayoutParams();
            //lp.setMarginEnd(250);
            int dp = (int) (0.8 * getApplicationContext().getResources().getDisplayMetrics().density) * 2;
            int three_dp = dp * 2;
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int w =(int) (metrics.widthPixels - dp) / 9 ;
            float left = dp, top = dp, right = dp, bottom = dp;
            if (i % 9 == 0 || isInBloc[i - 1] != isInBloc[i])
                left = three_dp;
            if (i / 9 <= 0 || isInBloc[i - 9] != isInBloc[i])
                top = three_dp;
            if (i % 9 >= 8|| isInBloc[i + 1] != isInBloc[i])
                right = three_dp;
            if (i / 9 >= 8 || isInBloc[i + 9] != isInBloc[i])
                bottom = three_dp;
            lp.setMargins((int)left,(int)top,(int)right,(int)bottom);
            button.setWidth((int)(w - left / 2 - right / 2));
            button.setHeight((int)(w - top / 2 - bottom / 2));
        }
    }

    boolean percolation(){
        ArrayList percol = new ArrayList();
        ArrayList seen = new ArrayList();
        for (int n = 0; n < 9 ; n++){
            int p = blocs[n][0];
            percol.clear();
            seen.clear();
            percol.add(p);
            seen.add(p);
            while (!percol.isEmpty()) {
                p = (int) percol.remove(0);
                int i = p / 9;
                int j = p % 9;
                for (int x = -1; x < 2; x++) {
                    for (int y = -1; y < 2; y++) {
                        if (Math.abs(x) + Math.abs(y) == 1) {
                            int ind = (i + x) * 9 + (j + y);
                            if (i + x >= 0 && i + x < 9 && j + y >= 0 && j + y < 9 && !seen.contains(ind) && isInBloc[ind] == n) {
                                percol.add(ind);
                                seen.add(ind);
                            }
                        }
                    }
                }
            }
            if (seen.size() != 9){
                return false;
            }
        }
        return true;
    }

    Pair<Integer, Integer> getlegalMove() {
        ArrayList<Pair<Integer, Integer>> index = new ArrayList<>();
        for (int n = 0; n < 81; n++) {
            if (n == 4 * 9 + 4){
                continue;
            }
            int i = n / 9;
            int j = n % 9;
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    if (Math.abs(x) + Math.abs(y) == 1) {
                        int ind = (i + x) * 9 + (j + y);
                        if (i + x >= 0 && i + x < 9 && j + y >= 0 && j + y < 9 && isInBloc[n] != isInBloc[ind] ) {
                            int c = isInBloc[ind];
                            for (int m = 0 ; m < 9; m++)
                                if (blocs[c][m] != 4 * 9 + 4)
                                    index.add(new Pair(n, blocs[c][m]));
                        }
                    }
                }
            }
        }

        while (!index.isEmpty()) {
            int i = (int) (Math.random() * index.size());
            Pair<Integer, Integer> ind = index.remove(i);
            for (int j = 0; j < 9; j++) {
                if (blocs[isInBloc[ind.first]][j] == ind.first) {
                    blocs[isInBloc[ind.first]][j] = ind.second;
                    break;
                }
            }
            for (int j = 0; j < 9; j++) {
                if (blocs[isInBloc[ind.second]][j] == ind.second) {
                    blocs[isInBloc[ind.second]][j] = ind.first;
                    break;
                }
            }
            int bn = isInBloc[ind.first];
            isInBloc[ind.first] = isInBloc[ind.second];
            isInBloc[ind.second] = bn;
            //print_tab(isInBloc, 81);
            if (percolation()) {
                return new Pair(81 - 1 - ind.first, 81 - 1 - ind.second);
            }
            for (int j = 0; j < 9; j++) {
                if (blocs[isInBloc[ind.first]][j] == ind.first) {
                    blocs[isInBloc[ind.first]][j] = ind.second;
                    break;
                }
            }
            for (int j = 0; j < 9; j++) {
                if (blocs[isInBloc[ind.second]][j] == ind.second) {
                    blocs[isInBloc[ind.second]][j] = ind.first;
                    break;
                }
            }
            bn = isInBloc[ind.first];
            isInBloc[ind.first] = isInBloc[ind.second];
            isInBloc[ind.second] = bn;
        }
        return null;
    }
/*
    boolean [] bloc(int[][] t, int i, int j){
        boolean [] bloc={false,false,false,false,false,false,false,false,false};
        for (int x=i-i%3;x<i-i%3+3;x++)
            for (int y=j-j%3; y<j-j%3+3;y++)
                if(t[x][y]!=0)
                    bloc[t[x][y]-1]=true;
        return bloc;
    }

 */

    boolean [] diagonale1(int[][] t, boolean test){
        boolean [] diagonale1={false,false,false,false,false,false,false,false,false};
        if (!test){
            return diagonale1;
        }
        for(int i = 0; i < 9; i++)
                if(t[i][i]!=0)
                    diagonale1[t[i][i]-1]=true;
        return diagonale1;
    }

    boolean [] diagonale2(int[][] t, boolean test){
        boolean [] diagonale2={false,false,false,false,false,false,false,false,false};
        if (!test){
            return diagonale2;
        }
        for(int i = 0; i < 9; i++)
            if(t[i][9 - 1 - i]!=0)
                diagonale2[t[i][9 - 1 - i]-1]=true;
        return diagonale2;
    }

    boolean [] possible(int[][]t, int i , int j){
        boolean [] possible={true,true,true,true,true,true,true,true,true};
        boolean [] ligne=ligne(t,i);
        boolean [] colonne=colonne(t,j);
        boolean [] bloc=bloc(t,i,j);
        boolean [] diagonale1=diagonale1(t,i == j);
        boolean [] diagonale2=diagonale2(t,i == 9 - 1 - j);
        for (int k=0; k<9; k++){
            if (ligne[k] || colonne[k] || bloc[k] || (cross && (diagonale1[k] || diagonale2[k])))
                possible[k]=false;
        }
        return possible;
    }

    int[][] copy(int B[][]){
        int C[][]=new int[9][9];
        for (int i=0;i<9; i++)
            for (int j=0; j<9; j++)
                C[i][j]=B[i][j];
        return C;
    }

    boolean equals(int t1[][], int t2[][]){
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                if (t1[i][j] != t2[i][j]){
                    return false;
                }
            }
        }
        return true;
    }

    ArrayList<int[][]> grilles(int t[][],ArrayList<Pair<Integer,Integer>> empties, ArrayList<int[][]>g){
        if (empties.isEmpty()){
            if (g.isEmpty() || !equals(g.get(0), t))
                g.add(t);
            return g;
        }
        if (g.size() > 1)
            return g;
        int min = 10;
        ArrayList<Pair<Integer,Integer>> bestPositions = new ArrayList<>();
        boolean[] possible;
        for (Pair<Integer,Integer> position : empties){
            int nb = 0;
            possible = possible(t, position.first, position.second);
            for (int i = 0; i < 9; i++){
                if (possible[i]){
                    nb++;
                }
            }
            if (nb < min){
                min = nb;
                bestPositions.clear();
                bestPositions.add(position);
            }
            else if (nb == min){
                bestPositions.add(position);
            }
        }
        if (min == 0)
            return g;

        Pair<Integer,Integer> bestPosition = bestPositions.get((int) (Math.random() * bestPositions.size()));
        possible = possible(t, bestPosition.first, bestPosition.second);
        empties.remove(bestPosition);
        int[][]c=copy(t);
        for (int n=0; n<9;n++){
            if (g.size() > 1)
                return g;
            if (possible[n]) {
                c[bestPosition.first][bestPosition.second] = n+1;
                g = grilles(c, empties, g);
                c[bestPosition.first][bestPosition.second] = 0;
            }
        }
        empties.add(bestPosition);
        return g;
    }
    void _new(){
        if (bloc){
            initialise_blocs();
            random_blocs();
        }
        for (int i=0;i<9;i++)
            for(int j=0;j<9;j++)
                t[i][j]=0;
        int n=0;
        ArrayList<int[][]> g = new ArrayList<>();
        while (true){
            g.clear();
            if (n<18){
                int []l={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
                int k=0;
                int max = -1;
                for (int x=0; x<9;x++) {
                    for (int y = 0; y < 9; y++) {
                        boolean[] possible = possible(t,x,y);
                        if (possible[n%9] == false)
                            continue;
                        int p = 0;
                        for (int i = 0; i <9; i++){
                            if (possible[i])
                                p++;
                        }
                        if (p > max && t[x][y] == 0) {
                            max = p;
                            k = 0;
                            l[k++] = 9 * x + y;
                        }
                        else if (p == max && t[x][y] == 0)
                            l[k++] = 9 * x + y;
                    }
                }
                int m=l[(int) (Math.random() * k)];
                int i=m/9;
                int j=m%9;
                t[i][j]=n%9+1;
                n++;
            }
            else{
                ArrayList<Pair<Integer,Integer>> empties = new ArrayList<>();
                for (int i = 0; i < 9; i++){
                    for (int j = 0; j < 9; j++){
                        if (t[i][j] == 0){
                            empties.add(new Pair(i,j));
                        }
                    }
                }
                Collections.shuffle(empties);
                g = grilles(t,empties,g);
                if (g.size() == 1) {
                    fin=g.get(0);
                    break;
                }
                if (g.size() == 0){
                    //Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                    _new();
                    return;
                    /*
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                    break;
                     */
                }
                else{
                    int []l={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
                    int k=0;
                    int max = -1;
                    for (int x=0; x<9;x++) {
                        for (int y = 0; y < 9; y++) {
                            boolean[] possible = possible(t,x,y);
                            int p = 0;
                            for (int i = 0; i <9; i++){
                                if (possible[i])
                                    p++;
                            }
                            if (p > max && t[x][y] == 0 && g.get(0)[x][y] != g.get(1)[x][y]) {
                                max = p;
                                k = 0;
                                l[k++] = 9 * x + y;
                            }
                            else if (p == max && t[x][y] == 0 && g.get(0)[x][y] != g.get(1)[x][y])
                                l[k++] = 9 * x + y;
                        }
                    }
                    int m=l[(int) (Math.random() * k)];
                    int i=m/9;
                    int j=m%9;
                    t[i][j]=g.get((int) (Math.random() * 2))[i][j];
                    n++;
                }
            }
        }
        while (n < 25){
            int i=(int) (Math.random() * 9);
            int j=(int) (Math.random() * 9);
            while(t[i][j]!=0){
                i=(int) (Math.random() * 9);
                j=(int) (Math.random() * 9);
            }
            t[i][j]=fin[i][j];
            n++;
        }
        Button b;
        for (int i=0; i<9; i++)
            for (int j=0; j<9; j++){
                b=findViewById(tab_id[9*i+j]);
                if (t[i][j]==0){
                    b.setTextColor(Color.BLUE);
                    b.setText("");
                }
                else{
                    b.setTextColor(Color.BLACK);
                    b.setText(ch[t[i][j]]);
                }
            }
        if (bloc)
            doColors();
    }

    void new_grille(){
        AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setTitle("Créer une nouvelle grille?");
        d.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        d.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _new();
            }
        });
        d.show();

    }

    int place(int b){
        for (int i=0; i<81;i++){
            if (tab_id[i]==b)
                return i;
        }
        return -1;
    }
    void move(final int b){
        final Button button=findViewById(b);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position!=-1){
                    Button button=findViewById(tab_id[position]);
                    button.setAlpha(1);
                }
                button.setAlpha((float)0.8);
                position=place(b);
            }
        });
    }

    void play(final int n){
        int b=num[n];
        final Button button=findViewById(b);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position!=-1) {
                    Button button2 = findViewById(tab_id[position]);
                    if (button2.getCurrentTextColor()!=Color.BLACK && button2.getCurrentTextColor()!=Color.RED){
                        button2.setText(button.getText());
                        int i=position/9;
                        int j=position%9;
                        Button button3;
                        boolean c=true;
                        for (int y=0; y<9;y++){
                            if (t[i][y]!=0 && t[i][y]==n+1){
                                button2.setTextColor(0xFFFF4400);
                                button3=findViewById(tab_id[i*9+y]);
                                c=false;
                                if (button3.getCurrentTextColor()==Color.BLACK)
                                    button3.setTextColor(Color.RED);
                                else
                                    if (button3.getCurrentTextColor()==Color.BLUE)
                                        button3.setTextColor(0xFFFF4400);
                            }
                        }
                        for(int x=0; x<9;x++){
                            if (t[x][j]!=0 && t[x][j]==n+1) {
                                button2.setTextColor(0xFFFF4400);
                                button3 = findViewById(tab_id[x * 9 + j]);
                                c=false;
                                if (button3.getCurrentTextColor()==Color.BLACK)
                                    button3.setTextColor(Color.RED);
                                else
                                    if (button3.getCurrentTextColor()==Color.BLUE)
                                        button3.setTextColor(0xFFFF4400);
                            }
                        }
                        for (int m = 0; m < 9; m++){
                            int x = blocs[isInBloc[i * 9 + j]][m] / 9;
                            int y = blocs[isInBloc[i * 9 + j]][m] % 9;
                            if (t[x][y] != 0 && t[x][y] == n+1){
                                button2.setTextColor(0xFFFF4400);
                                button3 = findViewById(tab_id[x * 9 + y]);
                                c=false;
                                if (button3.getCurrentTextColor()==Color.BLACK)
                                    button3.setTextColor(Color.RED);
                                else
                                if (button3.getCurrentTextColor()==Color.BLUE)
                                    button3.setTextColor(0xFFFF4400);

                            }
                        }
                        if (cross && i == j) {
                            for (int x = 0; x < 9; x++) {
                                if (t[x][x] != 0 && t[x][x] == n + 1) {
                                    button2.setTextColor(0xFFFF4400);
                                    button3 = findViewById(tab_id[x * 9 + x]);
                                    c = false;
                                    if (button3.getCurrentTextColor() == Color.BLACK)
                                        button3.setTextColor(Color.RED);
                                    else if (button3.getCurrentTextColor() == Color.BLUE)
                                        button3.setTextColor(0xFFFF4400);
                                }
                            }
                        }
                        if (cross && i == 9 - 1 - j) {
                            for (int x = 0; x < 9; x++) {
                                if (t[x][9 - 1 - x] != 0 && t[x][9 - 1 - x] == n + 1) {
                                    button2.setTextColor(0xFFFF4400);
                                    button3 = findViewById(tab_id[x * 9 + 9 - 1 - x]);
                                    c = false;
                                    if (button3.getCurrentTextColor() == Color.BLACK)
                                        button3.setTextColor(Color.RED);
                                    else if (button3.getCurrentTextColor() == Color.BLUE)
                                        button3.setTextColor(0xFFFF4400);
                                }
                            }
                        }
                        if (c){
                            button2.setTextColor(Color.BLUE);
                        }
                        t[i][j]=n+1;
                        for (int y=0; y<9;y++){
                            if (t[i][y]!=0){
                                int m=t[i][y];
                                t[i][y]=0;
                                if(possible(t,i,y)[m-1]){
                                    button3=findViewById(tab_id[9*i+y]);
                                    if(button3.getCurrentTextColor()==0xFFFF4400)
                                        button3.setTextColor(Color.BLUE);
                                    if(button3.getCurrentTextColor()==Color.RED)
                                        button3.setTextColor(Color.BLACK);
                                }
                                t[i][y]=m;
                            }
                        }
                        for(int x=0; x<9;x++){
                            if (t[x][j]!=0) {
                                int m=t[x][j];
                                t[x][j]=0;
                                if(possible(t,x,j)[m-1]){
                                    button3=findViewById(tab_id[9*x+j]);
                                    if(button3.getCurrentTextColor()==0xFFFF4400)
                                        button3.setTextColor(Color.BLUE);
                                    if(button3.getCurrentTextColor()==Color.RED)
                                        button3.setTextColor(Color.BLACK);
                                }
                                t[x][j]=m;
                            }
                        }

                        for (int m = 0; m < 9; m++){
                            int x = blocs[isInBloc[i * 9 + j]][m] / 9;
                            int y = blocs[isInBloc[i * 9 + j]][m] % 9;
                            if (t[x][y] != 0){
                                int l=t[x][y];
                                t[x][y]=0;
                                if(possible(t,x,y)[l-1]){
                                    button3=findViewById(tab_id[9*x+y]);
                                    if(button3.getCurrentTextColor()==0xFFFF4400)
                                        button3.setTextColor(Color.BLUE);
                                    if(button3.getCurrentTextColor()==Color.RED)
                                        button3.setTextColor(Color.BLACK);
                                }
                                t[x][y]=l;

                            }
                        }
                        if (cross && i == j) {
                            for (int x = 0; x < 9; x++) {
                                if(t[x][x]!=0) {
                                    int m=t[x][x];
                                    t[x][x]=0;
                                    if (possible(t, x, x)[m - 1]) {
                                        button3 = findViewById(tab_id[9 * x + x]);
                                        if (button3.getCurrentTextColor() == 0xFFFF4400)
                                            button3.setTextColor(Color.BLUE);
                                        if (button3.getCurrentTextColor() == Color.RED)
                                            button3.setTextColor(Color.BLACK);
                                    }
                                    t[x][x]=m;
                                }
                            }
                        }
                        if (cross && i == 9 - 1 - j) {
                            for (int x = 0; x < 9; x++) {
                                if(t[x][9 - 1 - x]!=0) {
                                    int m=t[x][9 - 1 - x];
                                    t[x][9 - 1 - x]=0;
                                    if (possible(t, x, 9 - 1 - x)[m - 1]) {
                                        button3 = findViewById(tab_id[9 * x + 9 - 1 - x]);
                                        if (button3.getCurrentTextColor() == 0xFFFF4400)
                                            button3.setTextColor(Color.BLUE);
                                        if (button3.getCurrentTextColor() == Color.RED)
                                            button3.setTextColor(Color.BLACK);
                                    }
                                    t[x][9 - 1 - x]=m;
                                }
                            }
                        }

                    }
                    fin();
                }

            }
        });

    }

    void del(){
        final Button button=findViewById(num[9]);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position!=-1){
                    Button button2 = findViewById(tab_id[position]);
                    if (button2.getCurrentTextColor() != Color.BLACK && button2.getCurrentTextColor() != Color.RED) {
                        t[position / 9][position % 9] = 0;
                        button2.setText("");

                        Button button3;
                        int i = position / 9;
                        int j = position % 9;
                        for (int y = 0; y < 9; y++) {
                            if (t[i][y] != 0) {
                                int m = t[i][y];
                                t[i][y] = 0;
                                if (possible(t, i, y)[m - 1]) {
                                    button3 = findViewById(tab_id[9 * i + y]);
                                    if (button3.getCurrentTextColor() == 0xFFFF4400)
                                        button3.setTextColor(Color.BLUE);
                                    if (button3.getCurrentTextColor() == Color.RED)
                                        button3.setTextColor(Color.BLACK);
                                }
                                t[i][y] = m;
                            }
                        }
                        for (int x = 0; x < 9; x++) {
                            if (t[x][j] != 0) {
                                int m = t[x][j];
                                t[x][j] = 0;
                                if (possible(t, x, j)[m - 1]) {
                                    button3 = findViewById(tab_id[9 * x + j]);
                                    if (button3.getCurrentTextColor() == 0xFFFF4400)
                                        button3.setTextColor(Color.BLUE);
                                    if (button3.getCurrentTextColor() == Color.RED)
                                        button3.setTextColor(Color.BLACK);
                                }
                                t[x][j] = m;
                            }
                        }
                        for (int m = 0; m < 9; m++){
                            int x = blocs[isInBloc[i * 9 + j]][m] / 9;
                            int y = blocs[isInBloc[i * 9 + j]][m] % 9;
                            if (t[x][y] != 0){
                                int l=t[x][y];
                                t[x][y]=0;
                                if(possible(t,x,y)[l-1]){
                                    button3=findViewById(tab_id[9*x+y]);
                                    if(button3.getCurrentTextColor()==0xFFFF4400)
                                        button3.setTextColor(Color.BLUE);
                                    if(button3.getCurrentTextColor()==Color.RED)
                                        button3.setTextColor(Color.BLACK);
                                }
                                t[x][y]=l;

                            }
                        }
                        if (cross && i == j) {
                           for (int x = 0; x < 9; x++) {
                                if(t[x][x]!=0) {
                                    int m=t[x][x];
                                    t[x][x]=0;
                                    if (possible(t, x, x)[m - 1]) {
                                        button3 = findViewById(tab_id[9 * x + x]);
                                        if (button3.getCurrentTextColor() == 0xFFFF4400)
                                            button3.setTextColor(Color.BLUE);
                                        if (button3.getCurrentTextColor() == Color.RED)
                                            button3.setTextColor(Color.BLACK);
                                    }
                                    t[x][x]=m;
                                }
                            }
                        }
                        if (cross && i == 9 - 1 - j) {
                            for (int x = 0; x < 9; x++) {
                                if(t[x][9 - 1 - x]!=0) {
                                    int m=t[x][9 - 1 - x];
                                    t[x][9 - 1 - x]=0;
                                    if (possible(t, x, 9 - 1 - x)[m - 1]) {
                                        button3 = findViewById(tab_id[9 * x + 9 - 1 - x]);
                                        if (button3.getCurrentTextColor() == 0xFFFF4400)
                                            button3.setTextColor(Color.BLUE);
                                        if (button3.getCurrentTextColor() == Color.RED)
                                            button3.setTextColor(Color.BLACK);
                                    }
                                    t[x][9 - 1 - x]=m;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    void activateCross(){
        cross = true;
        Button button;
        for (int i = 0; i < 9; i++){
            button = findViewById(tab_id[i + 9 * i]);
            button.setBackgroundColor(getColor(R.color.grey));
            button = findViewById(tab_id[i + 9 * (9 - 1 - i)]);
            button.setBackgroundColor(getColor(R.color.grey));
        }
    }

    void deactivateCross(){
        cross = false;
        Button button;
        for (int i = 0; i < 9; i++){
            button = findViewById(tab_id[i + 9 * i]);
            button.setBackgroundColor(getColor(R.color.white));
            button = findViewById(tab_id[i + 9 * (9 - 1 - i)]);
            button.setBackgroundColor(getColor(R.color.white));
        }
    }

    void check_errors(){
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                if (isInBloc[blocs[i][j]] != i){
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        }
        Toast.makeText(this, "fin", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float x=metrics.widthPixels/(350);
        float w=(metrics.widthPixels-18*x)/9;
        Button b;
        for (int i=0; i<81; i++){
            b=findViewById(tab_id[i]);
            b.setWidth((int)w);
            b.setHeight((int)w);

        }
        b=findViewById(R.id.button);
        b.setHeight(metrics.widthPixels);
        for (int i=0; i<10; i++){
            b=findViewById(num[i]);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.height = metrics.widthPixels/8;
            params.width = metrics.widthPixels/5;
            b.setLayoutParams(params);
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
                new_grille();
            }
        });
        this.grilleSelection = findViewById(R.id.grilleSelection);
        final ArrayAdapter grilles = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Grille.values());
        grilleSelection.setAdapter(grilles);

        grilleSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    bloc = false;
                    initialise_blocs();
                    doColors();
                    deactivateCross();
                    _new();
                }
                else if (position == 1){
                    bloc = false;
                    initialise_blocs();
                    doColors();
                    activateCross();
                    _new();
                }
                else{
                    bloc = true;
                    deactivateCross();
                    _new();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        for (int i=0;i<81;i++)
            move(tab_id[i]);
        for (int i=0;i<9;i++)
            play(i);
        del();
    }
}
