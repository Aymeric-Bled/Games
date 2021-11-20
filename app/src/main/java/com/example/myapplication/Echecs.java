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

public class Echecs extends AppCompatActivity {
    private Button main;
    private Button new_;
    private Spinner player;
    private Button unDo;
    private String players[] = {"1 joueur","2 joueurs","0 joueur"};
    private int nb_players = 1;
    private GridLayout grille;
    private int taille = 8;
    private int tab_button[][] = new int[taille][taille];
    private int piece[][]=new int[taille][taille];
    private int position = -1;
    private int color = 0;
    private boolean fin = false;
    private int maxDepthMinimax = 4;
    private int maxDepthAlphaBeta = 5;
    private boolean isMoving = false;
    private boolean movedBlackKing = false;
    private boolean movedWhiteKing = false;
    private ArrayList<int[][]> gameMoves;


    ArrayList<Integer> accessible(int p, int tab_piece[][]){
        ArrayList<Integer> list = new ArrayList<>();
        if (p == -1)
            return list;
        switch (tab_piece[p / taille][p % taille]){
            case 0:
                int j;
                if (p / taille == 6){
                    j = 3;
                }
                else{
                    j = 2;
                }
                if (p/taille > 0) {
                    for (int i = 1; i < j && tab_piece[p / taille - i][p % taille] == -1; i++)
                        list.add(p - i * taille);
                    if (p % taille > 0 && tab_piece[p / taille - 1][p % taille - 1] != -1 && tab_piece[p / taille - 1][p % taille - 1] % 2 != tab_piece[p / taille][p % taille] % 2)
                        list.add(p - taille - 1);
                    if (p % taille < taille - 1 && tab_piece[p / taille - 1][p % taille + 1] != -1 && tab_piece[p / taille - 1][p % taille + 1] % 2 != tab_piece[p / taille][p % taille] % 2)
                        list.add(p - taille + 1);
                }
                break;
            case 1:
                if (p / taille == 1){
                    j = 3;
                }
                else{
                    j = 2;
                }
                if (p/taille < taille - 1) {
                    for (int i = 1; i < j && tab_piece[p / taille + i][p % taille] == -1; i++)
                        list.add(p + i * taille);
                    if (p % taille > 0 && tab_piece[p / taille + 1][p % taille - 1] != -1 && tab_piece[p / taille + 1][p % taille - 1] % 2 != tab_piece[p / taille][p % taille] % 2)
                        list.add(p + taille - 1);
                    if (p % taille < taille - 1 && tab_piece[p / taille + 1][p % taille + 1] != -1 && tab_piece[p / taille + 1][p % taille + 1] % 2 != tab_piece[p / taille][p % taille] % 2)
                        list.add(p + taille + 1);
                }
                break;
            case 2:
            case 3:
                int x;
                for(x = 1;p / taille + x < taille && tab_piece[p / taille + x][p % taille] == -1; x++)
                    list.add(p + x * taille);
                if (p / taille + x < taille && tab_piece[p / taille + x][p % taille] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p + x * taille);
                for(x = 1;p / taille - x >= 0 && tab_piece[p / taille - x][p % taille] == -1; x++)
                    list.add(p - x * taille);
                if (p / taille - x >= 0 && tab_piece[p / taille - x][p % taille] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p - x * taille);
                for(x = 1;p % taille + x < taille && tab_piece[p / taille][p % taille + x] == -1; x++)
                    list.add(p + x);
                if (p % taille + x < taille && tab_piece[p / taille][p % taille + x] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p + x);
                for(x = 1;p % taille - x >= 0 && tab_piece[p / taille][p % taille - x] == -1; x++)
                    list.add(p - x);
                if (p % taille - x >= 0 && tab_piece[p / taille][p % taille - x] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p - x);
                break;
            case 4:
            case 5:
                if(p / taille >= 2 && p % taille >= 1 && (tab_piece[p / taille - 2][p % taille - 1] == -1 || tab_piece[p / taille - 2][p % taille - 1] %2 != tab_piece[p / taille][p % taille] %2))
                    list.add(p - 2 * taille - 1);
                if(p / taille >= 2 && p % taille < taille - 1 && (tab_piece[p / taille - 2][p % taille + 1] == -1 || tab_piece[p / taille - 2][p % taille + 1] %2 != tab_piece[p / taille][p % taille] %2))
                    list.add(p - 2 * taille + 1);
                if(p / taille >= 1 && p % taille >= 2 && (tab_piece[p / taille - 1][p % taille - 2] == -1 || tab_piece[p / taille - 1][p % taille - 2] %2 != tab_piece[p / taille][p % taille] %2))
                    list.add(p - 1 * taille - 2);
                if(p / taille >= 1 && p % taille < taille - 2 && (tab_piece[p / taille - 1][p % taille + 2] == -1 || tab_piece[p / taille - 1][p % taille + 2] %2 != tab_piece[p / taille][p % taille] %2))
                    list.add(p - 1 * taille + 2);
                if(p / taille < taille - 2 && p % taille >= 1 && (tab_piece[p / taille + 2][p % taille - 1] == -1 || tab_piece[p / taille + 2][p % taille - 1] %2 != tab_piece[p / taille][p % taille] %2))
                    list.add(p + 2 * taille - 1);
                if(p / taille < taille - 2 && p % taille < taille - 1 && (tab_piece[p / taille + 2][p % taille + 1] == -1 || tab_piece[p / taille + 2][p % taille + 1] %2 != tab_piece[p / taille][p % taille] %2))
                    list.add(p + 2 * taille + 1);
                if(p / taille < taille - 1 && p % taille >= 2 && (tab_piece[p / taille + 1][p % taille - 2] == -1 || tab_piece[p / taille + 1][p % taille - 2] %2 != tab_piece[p / taille][p % taille] %2))
                    list.add(p + 1 * taille - 2);
                if(p / taille < taille - 1 && p % taille < taille - 2 && (tab_piece[p / taille + 1][p % taille + 2] == -1 || tab_piece[p / taille + 1][p % taille + 2] %2 != tab_piece[p / taille][p % taille] %2))
                    list.add(p + 1 * taille + 2);
                break;
            case 6:
            case 7:
                for(x = 1;p / taille + x < taille && p % taille + x < taille && tab_piece[p / taille + x][p % taille + x] == -1; x++)
                    list.add(p + x * taille + x);
                if (p / taille + x < taille && p % taille + x < taille && tab_piece[p / taille + x][p % taille + x] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p + x * taille + x);
                for(x = 1;p / taille + x < taille && p % taille - x >= 0 && tab_piece[p / taille + x][p % taille - x] == -1; x++)
                    list.add(p + x * taille - x);
                if (p / taille + x < taille && p % taille - x >= 0 && tab_piece[p / taille + x][p % taille - x] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p + x * taille - x);
                for(x = 1;p / taille - x >= 0 && p % taille + x < taille && tab_piece[p / taille - x][p % taille + x] == -1; x++)
                    list.add(p - x * taille + x);
                if (p / taille - x >= 0 && p % taille + x < taille && tab_piece[p / taille - x][p % taille + x] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p - x * taille + x);
                for(x = 1;p / taille - x >= 0 && p % taille - x >= 0 && tab_piece[p / taille - x][p % taille - x] == -1; x++)
                    list.add(p - x * taille - x);
                if (p / taille - x >= 0 && p % taille - x >= 0 && tab_piece[p / taille - x][p % taille - x] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p - x * taille - x);
                break;
            case 8:
            case 9:
                for(x = 1;p / taille + x < taille && tab_piece[p / taille + x][p % taille] == -1; x++)
                    list.add(p + x * taille);
                if (p / taille + x < taille && tab_piece[p / taille + x][p % taille] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p + x * taille);
                for(x = 1;p / taille - x >= 0 && tab_piece[p / taille - x][p % taille] == -1; x++)
                    list.add(p - x * taille);
                if (p / taille - x >= 0 && tab_piece[p / taille - x][p % taille] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p - x * taille);
                for(x = 1;p % taille + x < taille && tab_piece[p / taille][p % taille + x] == -1; x++)
                    list.add(p + x);
                if (p % taille + x < taille && tab_piece[p / taille][p % taille + x] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p + x);
                for(x = 1;p % taille - x >= 0 && tab_piece[p / taille][p % taille - x] == -1; x++)
                    list.add(p - x);
                if (p % taille - x >= 0 && tab_piece[p / taille][p % taille - x] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p - x);
                for(x = 1;p / taille + x < taille && p % taille + x < taille && tab_piece[p / taille + x][p % taille + x] == -1; x++)
                    list.add(p + x * taille + x);
                if (p / taille + x < taille && p % taille + x < taille && tab_piece[p / taille + x][p % taille + x] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p + x * taille + x);
                for(x = 1;p / taille + x < taille && p % taille - x >= 0 && tab_piece[p / taille + x][p % taille - x] == -1; x++)
                    list.add(p + x * taille - x);
                if (p / taille + x < taille && p % taille - x >= 0 && tab_piece[p / taille + x][p % taille - x] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p + x * taille - x);
                for(x = 1;p / taille - x >= 0 && p % taille + x < taille && tab_piece[p / taille - x][p % taille + x] == -1; x++)
                    list.add(p - x * taille + x);
                if (p / taille - x >= 0 && p % taille + x < taille && tab_piece[p / taille - x][p % taille + x] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p - x * taille + x);
                for(x = 1;p / taille - x >= 0 && p % taille - x >= 0 && tab_piece[p / taille - x][p % taille - x] == -1; x++)
                    list.add(p - x * taille - x);
                if (p / taille - x >= 0 && p % taille - x >= 0 && tab_piece[p / taille - x][p % taille - x] % 2 != tab_piece[p / taille][p % taille] % 2)
                    list.add(p - x * taille - x);
                break;
            case 10:
                for (x = -1; x< 2; x++) {
                    for (int y = -1; y < 2; y++)
                        if ((x != 0 || y != 0) && p / taille + x >= 0 && p / taille + x < taille && p % taille + y >= 0 && p % taille + y < taille && (tab_piece[p / taille + x][p % taille + y] == -1 || tab_piece[p / taille + x][p % taille + y] % 2 != tab_piece[p / taille][p % taille] % 2))
                            list.add(p + x * taille + y);
                }
                if (!movedWhiteKing && tab_piece[taille - 1][5] == -1 && tab_piece[taille - 1][6] == -1 && tab_piece[taille - 1][7] == 2){
                    list.add(p + 2);
                }
                if (!movedWhiteKing && tab_piece[taille - 1][3] == -1 && tab_piece[taille - 1][2] == -1 && tab_piece[taille - 1][1] == -1 && tab_piece[taille - 1][0] == 2) {
                    list.add(p - 2);
                }
                break;
            case 11:
                for (x = -1; x< 2; x++) {
                    for (int y = -1; y < 2; y++)
                        if ((x != 0 || y != 0) && p / taille + x >= 0 && p / taille + x < taille && p % taille + y >= 0 && p % taille + y < taille && (tab_piece[p / taille + x][p % taille + y] == -1 || tab_piece[p / taille + x][p % taille + y] % 2 != tab_piece[p / taille][p % taille] % 2))
                            list.add(p + x * taille + y);
                }
                if (!movedBlackKing && tab_piece[0][5] == -1 && tab_piece[0][6] == -1 && tab_piece[0][7] == 3){
                    list.add(p + 2);
                }
                if (!movedBlackKing && tab_piece[0][3] == -1 && tab_piece[0][2] == -1 && tab_piece[0][1] == -1 && tab_piece[0][0] == 3) {
                    list.add(p - 2);
                }
                break;
            default:
                break;
        }
        return list;
    }

    ArrayList<Integer> possible(int p, int c, int tab_piece[][]){
        ArrayList<Integer> list = new ArrayList<>();
        if (p == -1 || tab_piece[p / taille][p % taille] == -1 || tab_piece[p / taille][p % taille] % 2 != c)
            return list;
        ArrayList<Integer> accessible = accessible(p, tab_piece);
        ArrayList<Integer> acc;
        int x, y;
        boolean possible;
        for (Integer n : accessible) {
            possible = true;
            int copy[][] = copy((tab_piece));
            doMove(new Pair<Integer, Integer>(p, n), copy);
            for (int j = 0; possible && j < taille * taille; j++) {
                if (copy[j / taille][j % taille] != -1 && copy[j / taille][j % taille] % 2 == 1 - c) {
                    acc = accessible(j, copy);
                    for (Integer k : acc) {
                        if (k != -1 && copy[k / taille][k % taille] >= 10 && copy[k / taille][k % taille] % 2 == c)
                            possible = false;
                    }
                }
            }
            if (possible)
                list.add(n);
        }
        return list;
    }

    boolean isCheckMate(int c, int[][] tab_piece){
        if (!getLegalMoves(c, tab_piece).isEmpty())
            return false;
        ArrayList<Pair<Integer, Integer>> moves= getLegalMoves(1 - c, tab_piece);
        for (Pair<Integer, Integer>move : moves){
            if (tab_piece[move.second/taille][move.second%taille] == 10 + c)
                return true;
        }
        return false;
    }


    void initialise_piece(){
        for (int i=0; i<taille;i++) {
            for (int j = 0; j < taille; j++) {
                switch (i * taille + j) {
                    case 48:
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                        piece[i][j] = 0;
                        break;
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                        piece[i][j] = 1;
                        break;
                    case 56:
                    case 63:
                        piece[i][j] = 2;
                        break;
                    case 0:
                    case 7:
                        piece[i][j] = 3;
                        break;
                    case 57:
                    case 62:
                        piece[i][j] = 4;
                        break;
                    case 1:
                    case 6:
                        piece[i][j] = 5;
                        break;
                    case 58:
                    case 61:
                        piece[i][j] = 6;
                        break;
                    case 2:
                    case 5:
                        piece[i][j] = 7;
                        break;
                    case 59:
                        piece[i][j] = 8;
                        break;
                    case 3:
                        piece[i][j] = 9;
                        break;
                    case 60:
                        piece[i][j] = 10;
                        break;
                    case 4:
                        piece[i][j] = 11;
                        break;
                    default:
                        piece[i][j] = -1;
                        break;
                }
            }
        }
    }




    void create_layout(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int x=metrics.widthPixels/taille;
        grille= findViewById(R.id.grille);
        grille.removeAllViews();
        grille.setColumnCount(taille);
        grille.setRowCount(taille);
        tab_button=new int [taille][taille];
        Button b;
        int id;
        for (int i=0; i<taille;i++) {
            for (int j = 0; j < taille; j++) {
                b = new Button(this);
                id = Button.generateViewId();
                b.setId(id);
                tab_button[i][j]=id;
                b.setLayoutParams(new LinearLayout.LayoutParams(x,x));
                b.setMinimumHeight(0);
                b.setMinimumWidth(0);
                b.setHeight(x);
                b.setWidth(x);
                if((i+j)%2==0){
                    switch (piece[i][j]) {
                        case 0:
                            b.setBackgroundResource(R.drawable.pion_blanc_sur_blanc);
                            break;
                        case 1:
                            b.setBackgroundResource(R.drawable.pion_noir_sur_blanc);
                            break;
                        case 2:
                            b.setBackgroundResource(R.drawable.tour_blanc_sur_blanc);
                            break;
                        case 3:
                            b.setBackgroundResource(R.drawable.tour_noir_sur_blanc);
                            break;
                        case 4:
                            b.setBackgroundResource(R.drawable.cavalier_blanc_sur_blanc);
                            break;
                        case 5:
                            b.setBackgroundResource(R.drawable.cavalier_noir_sur_blanc);
                            break;
                        case 6:
                            b.setBackgroundResource(R.drawable.fou_blanc_sur_blanc);
                            break;
                        case 7:
                            b.setBackgroundResource(R.drawable.fou_noir_sur_blanc);
                            break;
                        case 8:
                            b.setBackgroundResource(R.drawable.reine_blanc_sur_blanc);
                            break;
                        case 9:
                            b.setBackgroundResource(R.drawable.reine_noir_sur_blanc);
                            break;
                        case 10:
                            b.setBackgroundResource(R.drawable.roi_blanc_sur_blanc);
                            break;
                        case 11:
                            b.setBackgroundResource(R.drawable.roi_noir_sur_blanc);
                            break;
                        default:
                            b.setBackgroundColor(getColor(R.color.white));
                    }
                }
                else{
                    switch (piece[i][j]) {
                        case 0:
                            b.setBackgroundResource(R.drawable.pion_blanc_sur_gris);
                            break;
                        case 1:
                            b.setBackgroundResource(R.drawable.pion_noir_sur_gris);
                            break;
                        case 2:
                            b.setBackgroundResource(R.drawable.tour_blanc_sur_gris);
                            break;
                        case 3:
                            b.setBackgroundResource(R.drawable.tour_noir_sur_gris);
                            break;
                        case 4:
                            b.setBackgroundResource(R.drawable.cavalier_blanc_sur_gris);
                            break;
                        case 5:
                            b.setBackgroundResource(R.drawable.cavalier_noir_sur_gris);
                            break;
                        case 6:
                            b.setBackgroundResource(R.drawable.fou_blanc_sur_gris);
                            break;
                        case 7:
                            b.setBackgroundResource(R.drawable.fou_noir_sur_gris);
                            break;
                        case 8:
                            b.setBackgroundResource(R.drawable.reine_blanc_sur_gris);
                            break;
                        case 9:
                            b.setBackgroundResource(R.drawable.reine_noir_sur_gris);
                            break;
                        case 10:
                            b.setBackgroundResource(R.drawable.roi_blanc_sur_gris);
                            break;
                        case 11:
                            b.setBackgroundResource(R.drawable.roi_noir_sur_gris);
                            break;
                        default:
                            b.setBackgroundColor(getColor(R.color.grey));
                    }

                }
                grille.addView(b,new LinearLayout.LayoutParams(x,x));
            }
        }
    }

    void set(Button b, int i, int j){
        if (piece[i][j] != -1)
            b.setBackgroundResource(getDrawable(b,i,j));
        else
            b.setBackgroundColor(getColor(getDrawable(b,i,j)));
    }

    int getDrawable(Button b, int i, int j){
        if ((i + j) % 2 == 0) {
            switch (piece[i][j]) {
                case 0:
                    return R.drawable.pion_blanc_sur_blanc;
                case 1:
                    return R.drawable.pion_noir_sur_blanc;
                case 2:
                    return R.drawable.tour_blanc_sur_blanc;
                case 3:
                    return R.drawable.tour_noir_sur_blanc;
                case 4:
                    return R.drawable.cavalier_blanc_sur_blanc;
                case 5:
                    return R.drawable.cavalier_noir_sur_blanc;
                case 6:
                    return R.drawable.fou_blanc_sur_blanc;
                case 7:
                    return R.drawable.fou_noir_sur_blanc;
                case 8:
                    return R.drawable.reine_blanc_sur_blanc;
                case 9:
                    return R.drawable.reine_noir_sur_blanc;
                case 10:
                    return R.drawable.roi_blanc_sur_blanc;
                case 11:
                    return R.drawable.roi_noir_sur_blanc;
                default:
                    return R.color.white;
            }
        } else {
            switch (piece[i][j]) {
                case 0:
                    return R.drawable.pion_blanc_sur_gris;
                case 1:
                    return R.drawable.pion_noir_sur_gris;
                case 2:
                    return R.drawable.tour_blanc_sur_gris;
                case 3:
                    return R.drawable.tour_noir_sur_gris;
                case 4:
                    return R.drawable.cavalier_blanc_sur_gris;
                case 5:
                    return R.drawable.cavalier_noir_sur_gris;
                case 6:
                    return R.drawable.fou_blanc_sur_gris;
                case 7:
                    return R.drawable.fou_noir_sur_gris;
                case 8:
                    return R.drawable.reine_blanc_sur_gris;
                case 9:
                    return R.drawable.reine_noir_sur_gris;
                case 10:
                    return R.drawable.roi_blanc_sur_gris;
                case 11:
                    return R.drawable.roi_noir_sur_gris;
                default:
                    return R.color.grey;
            }
        }
    }

    void set_possible(Button b, int i, int j) {
        switch (piece[i][j]) {
            case 0:
                b.setBackgroundResource(R.drawable.pion_blanc_sur_vert);
                break;
            case 1:
                b.setBackgroundResource(R.drawable.pion_noir_sur_vert);
                break;
            case 2:
                b.setBackgroundResource(R.drawable.tour_blanc_sur_vert);
                break;
            case 3:
                b.setBackgroundResource(R.drawable.tour_noir_sur_vert);
                break;
            case 4:
                b.setBackgroundResource(R.drawable.cavalier_blanc_sur_vert);
                break;
            case 5:
                b.setBackgroundResource(R.drawable.cavalier_noir_sur_vert);
                break;
            case 6:
                b.setBackgroundResource(R.drawable.fou_blanc_sur_vert);
                break;
            case 7:
                b.setBackgroundResource(R.drawable.fou_noir_sur_vert);
                break;
            case 8:
                b.setBackgroundResource(R.drawable.reine_blanc_sur_vert);
                break;
            case 9:
                b.setBackgroundResource(R.drawable.reine_noir_sur_vert);
                break;
            case 10:
                b.setBackgroundResource(R.drawable.roi_blanc_sur_vert);
                break;
            case 11:
                b.setBackgroundResource(R.drawable.roi_noir_sur_vert);
                break;
            default:
                if ((i + j) % 2 == 0)
                    b.setBackgroundResource(R.drawable.possible_blanc);
                else
                    b.setBackgroundResource(R.drawable.possible_gris);
        }


    }
    void new_(){
        initialise_piece();
        Button b;
        for (int i=0; i<taille;i++) {
            for (int j = 0; j < taille; j++) {
                b = findViewById(tab_button[i][j]);
                set(b,i,j);
            }
        }
        color = 0;
        fin = false;
        gameMoves = new ArrayList<>();
        gameMoves.add(copy(piece));
        if (nb_players == 0)
            play_computer();
    }

    int place (int b){
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                if (tab_button[i][j] == b)
                    return taille*i+j;
            }
        }
        return -1;
    }


    void play_computer(){
        isMoving = true;
        AnimatorSet s = new AnimatorSet();
        ObjectAnimator animator =  ObjectAnimator.ofObject(main, "TextColor", new ArgbEvaluator(), Color.BLACK, Color.BLACK);
        animator.setDuration(200);
        s.play(animator);
        s.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int m = 0;
                try {
                    m = computer_move();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                int x = m / (taille * taille);
                int y = m % (taille * taille);
                int Castling = doMove(new Pair<Integer, Integer>(x,y), piece);
                color = 1 - color;
                if (Castling == 0) {
                    Button b = findViewById(tab_button[x / taille][x % taille]);
                    set(b, x / taille, x % taille);
                    b = findViewById(tab_button[y / taille][y % taille]);
                    set(b, y / taille, y % taille);
                }
                else{
                    for (int i = 0; i < taille; i++){
                        Button b = findViewById(tab_button[x / taille][i]);
                        set(b, x / taille, i);
                    }
                }
                end();
                gameMoves.add(copy(piece));
                if (!fin && nb_players == 0){
                    play_computer();
                }
                else {
                    isMoving = false;
                }
            }
        });
        s.start();
    }


    void play_player(int move, ArrayList<Integer> possible) {
        int Castling = doMove(new Pair<Integer, Integer>(position, move), piece);
        if (Castling == 0) {
            Button b = findViewById(tab_button[position / taille][position % taille]);
            set(b, position / taille, position % taille);
            b = findViewById(tab_button[move / taille][move % taille]);
            set(b, move / taille, move % taille);
        }
        else{
            for (int i = 0; i < taille; i++){
                Button b = findViewById(tab_button[move / taille][i]);
                set(b, move / taille, i);
            }
        }
        for (int m : possible) {
            Button b2 = findViewById(tab_button[m / taille][m % taille]);
            set(b2, m / taille, m % taille);
        }
        position = -1;
        color = 1 - color;
        end();
        gameMoves.add(copy(piece));
        if (!fin && nb_players != 2) {
            play_computer();
        }
        else{
            isMoving = false;
        }
    }

    synchronized boolean canMove(){
        if (!isMoving){
            isMoving = true;
            return true;
        }
        return false;
    }

    void move(final int b){
        final Button button = findViewById(b);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMoving) {
                    int p = place(b);
                    ArrayList<Integer> possible;
                    if (p == position) {
                        possible = possible(position, color, piece);
                        for (int n : possible) {
                            Button b = findViewById(tab_button[n / taille][n % taille]);
                            set(b, n / taille, n % taille);
                        }
                        position = -1;
                    } else {
                        if (piece[p / taille][p % taille] != -1 && piece[p / taille][p % taille] % 2 == color) {
                            if (position != -1) {
                                possible = possible(position, color, piece);
                                for (int n : possible) {
                                    Button b = findViewById(tab_button[n / taille][n % taille]);
                                    set(b, n / taille, n % taille);
                                }
                            }
                            position = p;
                            possible = possible(position, color, piece);
                            for (int n : possible) {
                                Button b = findViewById(tab_button[n / taille][n % taille]);
                                set_possible(b, n / taille, n % taille);
                            }
                        } else {
                            possible = possible(position, color, piece);
                            for (int n : possible) {
                                if (n == p) {
                                    if (canMove()) {
                                        play_player(n, possible);
                                    }
                                    return;
                                }
                            }
                        }

                    }
                }

            }
        });
    }

    int value(int piece){
        switch (piece){
            case 0:
                return 1;
            case 1:
                return -1;
            case 2:
                return 5;
            case 3:
                return -5;
            case 4:
            case 6:
                return 3;
            case 5:
            case 7:
                return -3;
            case 8:
                return 9;
            case 9:
                return -9;
            case 10:
                return 100;
            case 11:
                return -100;
            default:
                return 0;
        }
    }

    int tab_value(int tab_piece[][]){
        int value = 0;
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if(tab_piece[i][j] != -1){
                    value += value(tab_piece[i][j]);
                }
            }
        }
        if (isCheckMate(0, tab_piece))
            value -= 100;
        if (isCheckMate(1, tab_piece))
            value += 100;
        return value;
    }

    int [][] copy(int tab_piece[][]){
        int copy[][] = new int[taille][taille];
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                copy[i][j] = tab_piece[i][j];
            }
        }
        return copy;
    }

    ArrayList<Pair<Integer, Integer>> getLegalMoves(int c, int tab_piece[][]){
        ArrayList<Pair<Integer, Integer>> legalMoves = new ArrayList<>();
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if (tab_piece[i][j]!= -1 && tab_piece[i][j] % 2 == c) {
                    ArrayList<Integer> possible = possible(i * taille + j, c, tab_piece);
                    for (int p : possible) {
                        legalMoves.add(new Pair<>(taille * i + j, p));
                    }
                }
            }
        }
        return legalMoves;
    }
/*
    int random_move(){
        int move[] = new int[taille*taille*taille*taille];
        int length = 0;
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if (piece[i][j]!= -1 && piece[i][j] % 2 == color) {
                    int possible[] = possible(i * taille + j, color, piece);
                    for (int k = 0; k < taille * taille && possible[k] != -1; k++) {
                        int p = possible[k];
                        move[length++] = taille*taille*(taille * i + j) + p;
                    }
                }
            }
        }
        return move[(int) (Math.random() * length)];
    }
*/

    int doMove(Pair<Integer, Integer> move, int tab_piece[][]){
        int Castling = 0;
        if (tab_piece[move.first / taille][move.first % taille] >= 10 && move.second % taille - move.first % taille > 1){
            tab_piece[move.first / taille][5] = tab_piece[move.first / taille][taille - 1];
            tab_piece[move.first / taille][taille - 1] = -1;
            Castling = 1;
        }
        if (tab_piece[move.first / taille][move.first % taille] >= 10 && move.second % taille - move.first % taille < -1){
            tab_piece[move.first / taille][3] = tab_piece[move.first / taille][0];
            tab_piece[move.first / taille][0] = -1;
            Castling = -1;
        }
        if (tab_piece[move.first / taille][move.first % taille] == 0 && move.second / taille == 0 || tab_piece[move.first / taille][move.first % taille] == 1 && move.second / taille == taille - 1)
            tab_piece[move.first / taille][move.first % taille] += 8;
        tab_piece[move.second / taille][move.second % taille] = tab_piece[move.first / taille][move.first % taille];
        tab_piece[move.first / taille][move.first % taille] = -1;
        return Castling;
    }

    int minimax(int c, int depth, int tab_piece[][]) {
        if (depth >= maxDepthMinimax) {
            return tab_value(tab_piece);
        }
        if (c == 0) {
            int max = -1000;
            ArrayList<Integer> bestMoves = new ArrayList<>();
            ArrayList<Pair<Integer, Integer>> moves = getLegalMoves(c, tab_piece);
            if (moves.isEmpty()) {
                return tab_value(tab_piece);
            }
            for (Pair<Integer, Integer> move : moves) {
                int copy[][] = copy(tab_piece);
                doMove(move, copy);
                int m = minimax(1 - c, depth + 1, copy);
                if (m > max) {
                    max = m;
                    bestMoves = new ArrayList<>();
                    bestMoves.add(taille * taille * move.first + move.second);
                }
                else if (m == max){
                    /*
                    int firstPiece = bestMoves.get(0) / (taille * taille);
                    if (tab_piece[firstPiece/taille][firstPiece % taille] != 0 && tab_piece[move.first/taille][move.first % taille] == 0){
                        bestMoves = new ArrayList<>();
                        bestMoves.add(taille * taille * move.first + move.second);
                    }
                    else if (tab_piece[firstPiece/taille][firstPiece % taille] != 0 || tab_piece[move.first/taille][move.first % taille] == 0) {
                        bestMoves.add(taille * taille * move.first + move.second);
                    }

                     */
                    bestMoves.add(taille * taille * move.first + move.second);
                }
            }
            if (depth != 0)
                return max;
            return bestMoves.get((int) (Math.random() * bestMoves.size()));
        } else {
            int min = 1000;
            ArrayList<Integer> bestMoves = new ArrayList<>();
            ArrayList<Pair<Integer, Integer>> moves = getLegalMoves(c, tab_piece);
            if (moves.isEmpty()) {
                return tab_value(tab_piece);
            }
            for (Pair<Integer, Integer> move : moves) {
                int copy[][] = copy(tab_piece);
                doMove(move, copy);
                int m = minimax(1 - c, depth + 1, copy);
                if (m < min) {
                    min = m;
                    bestMoves = new ArrayList<>();
                    bestMoves.add(taille * taille * move.first + move.second);
                }
                else if (m == min){
                    /*
                    int firstPiece = bestMoves.get(0) / (taille * taille);
                    if (tab_piece[firstPiece/taille][firstPiece % taille] != 1 && tab_piece[move.first/taille][move.first % taille] == 1){
                        bestMoves = new ArrayList<>();
                        bestMoves.add(taille * taille * move.first + move.second);
                    }
                    else if (tab_piece[firstPiece/taille][firstPiece % taille] != 1 || tab_piece[move.first/taille][move.first % taille] == 1) {
                        bestMoves.add(taille * taille * move.first + move.second);
                    }

                     */
                    bestMoves.add(taille * taille * move.first + move.second);
                }
            }
            if (depth != 0)
                return min;
            return bestMoves.get((int) (Math.random() * bestMoves.size()));
        }
    }

    Pair<Integer, Integer> alphaBeta(int c, int depth, int tab_piece[][], int alpha, int beta, long timeout, Map<String, Pair<ArrayList<Integer>, Integer>> dict) throws TimeoutException{
        if (System.currentTimeMillis() > timeout) {
            throw new TimeoutException();
        }
        String key = key(tab_piece, depth, c == 0 ? "max" :"min");
        if (dict.containsKey(key)){
            Pair<ArrayList<Integer>, Integer> value = dict.get(key);
            if (depth != 0)
                return new Pair(-1,value.second);
            else {
                return new Pair(value.first.get((int) (Math.random() * value.first.size())), value.second);
            }
        }
        if (depth >= maxDepthAlphaBeta) {
            int value = tab_value(tab_piece);
            dict.put(key, new Pair(null, value));
            return new Pair(-1, value);
        }
        if (c == 0) {
            int max = -1000;
            ArrayList<Integer> bestMoves = new ArrayList<>();
            ArrayList<Pair<Integer, Integer>> moves = getLegalMoves(c, tab_piece);
            if (moves.isEmpty()) {
                int value = tab_value(tab_piece);
                dict.put(key, new Pair(null, value));
                return new Pair(-1, value);
            }
            ArrayList<Pair<Integer,Pair<Integer, Integer>>> orderedMoves = new ArrayList<>();
            for (Pair<Integer,Integer> move : moves){
                int copy[][] = copy(tab_piece);
                doMove(move, copy);
                int m = alphaBeta(1 - c, maxDepthAlphaBeta, copy, alpha, beta, timeout, dict).second;
                int a = 0;
                int b = orderedMoves.size() - 1;
                int n = (a + b) / 2;
                while (a < b){
                    if (orderedMoves.get(n).first == m){
                        break;
                    }
                    else if (orderedMoves.get(n).first > m){
                        a = n + 1;
                    }
                    else {
                        b = n;
                    }
                    n = (a + b) / 2;
                }
                orderedMoves.add(n,new Pair(m, move));
            }
            moves = new ArrayList<>();
            for (Pair<Integer,Pair<Integer, Integer>> move : orderedMoves){
                moves.add(move.second);
            }
            if (depth != 0) {
                for (Pair<Integer, Integer> move : moves) {
                    int copy[][] = copy(tab_piece);
                    doMove(move, copy);
                    int m = alphaBeta(1 - c, depth + 1, copy, alpha, beta, timeout, dict).second;
                    if (m > max) {
                        max = m;
                        bestMoves = new ArrayList<>();
                        bestMoves.add(taille * taille * move.first + move.second);
                    }
                    if (max >= beta){
                        dict.put(key, new Pair(null, max));
                        return new Pair(-1, max);
                    }
                    if (max > alpha){
                        alpha = max;
                    }
                }
                dict.put(key, new Pair(null, max));
                return new Pair(-1, max);
            }
            else {
                for (Pair<Integer, Integer> move : moves) {
                    int copy[][] = copy(tab_piece);
                    doMove(move, copy);
                    int m = alphaBeta(1 - c, depth + 1, copy, alpha, beta, timeout, dict).second;
                    if (m > max) {
                        max = m;
                        bestMoves = new ArrayList<>();
                        bestMoves.add(taille * taille * move.first + move.second);
                    } else if (m == max) {
                    int firstPiece = bestMoves.get(0) / (taille * taille);
                    if (tab_piece[firstPiece/taille][firstPiece % taille] != 0 && tab_piece[move.first/taille][move.first % taille] == 0){
                        bestMoves = new ArrayList<>();
                        bestMoves.add(taille * taille * move.first + move.second);
                    }
                    else if (tab_piece[firstPiece/taille][firstPiece % taille] != 0 || tab_piece[move.first/taille][move.first % taille] == 0) {
                        bestMoves.add(taille * taille * move.first + move.second);
                    }
                        bestMoves.add(taille * taille * move.first + move.second);
                    }
                }
                int move = bestMoves.get((int) (Math.random() * bestMoves.size()));
                dict.put(key, new Pair(bestMoves, max));
                return new Pair(move, max);
            }

        } else {
            int min = 1000;
            ArrayList<Integer> bestMoves = new ArrayList<>();
            ArrayList<Pair<Integer, Integer>> moves = getLegalMoves(c, tab_piece);
            if (moves.isEmpty()) {
                int value = tab_value(tab_piece);
                dict.put(key, new Pair(null, value));
                return new Pair(-1, value);
            }

            ArrayList<Pair<Integer,Pair<Integer, Integer>>> orderedMoves = new ArrayList<>();
            for (Pair<Integer,Integer> move : moves){
                int copy[][] = copy(tab_piece);
                doMove(move, copy);
                int m = alphaBeta(1 - c, maxDepthAlphaBeta, copy, alpha, beta, timeout, dict).second;
                int a = 0;
                int b = orderedMoves.size() - 1;
                int n = (a + b) / 2;
                while (a < b){
                    if (orderedMoves.get(n).first == m){
                        break;
                    }
                    else if (orderedMoves.get(n).first < m){
                        a = n + 1;
                    }
                    else {
                        b = n;
                    }
                    n = (a + b) / 2;
                }
                orderedMoves.add(n,new Pair(m, move));
            }
            moves = new ArrayList<>();
            for (Pair<Integer,Pair<Integer, Integer>> move : orderedMoves){
                moves.add(move.second);
            }
            if (depth != 0) {
                for (Pair<Integer, Integer> move : moves) {
                    int copy[][] = copy(tab_piece);
                    doMove(move, copy);
                    int m = alphaBeta(1 - c, depth + 1, copy, alpha, beta, timeout, dict).second;
                    if (m < min) {
                        min = m;
                        bestMoves = new ArrayList<>();
                        bestMoves.add(taille * taille * move.first + move.second);
                    }
                    if (min <= alpha){
                        dict.put(key, new Pair(null, min));
                        return new Pair(-1, min);
                    }
                    if (min < beta){
                        beta = min;
                    }
                }
                dict.put(key, new Pair(null, min));
                return new Pair(-1, min);
            }
            else {
                for (Pair<Integer, Integer> move : moves) {
                    int copy[][] = copy(tab_piece);
                    doMove(move, copy);
                    int m = alphaBeta(1 - c, depth + 1, copy, alpha, beta, timeout, dict).second;
                    if (m < min) {
                        min = m;
                        bestMoves = new ArrayList<>();
                        bestMoves.add(taille * taille * move.first + move.second);
                    } else if (m == min) {
                    /*
                    int firstPiece = bestMoves.get(0) / (taille * taille);
                    if (tab_piece[firstPiece/taille][firstPiece % taille] != 0 && tab_piece[move.first/taille][move.first % taille] == 0){
                        bestMoves = new ArrayList<>();
                        bestMoves.add(taille * taille * move.first + move.second);
                    }
                    else if (tab_piece[firstPiece/taille][firstPiece % taille] != 0 || tab_piece[move.first/taille][move.first % taille] == 0) {
                        bestMoves.add(taille * taille * move.first + move.second);
                    }

                     */
                        bestMoves.add(taille * taille * move.first + move.second);
                    }
                }
                int move = bestMoves.get((int) (Math.random() * bestMoves.size()));
                dict.put(key, new Pair(bestMoves, min));
                return new Pair(move, min);
            }
        }
    }

    Pair<Integer, Integer> alphaBetaDichotomie(int c, int depth, int tab_piece[][], long timeout, Map<String, Pair<ArrayList<Integer>, Integer>> dict) throws TimeoutException{
        int alpha = -1000;
        int beta = 1000;
        int n = (beta + alpha) / 2;
        Pair<Integer,Integer> test = alphaBeta(c, depth, tab_piece, n - 1, n + 1, timeout, dict);
        while (test.second != n && (beta - alpha) > 0){
            if (test.second > n){
                alpha = n + 1;
            }
            else {
                beta = n - 1;
            }
            n = (beta + alpha) / 2;
            test = alphaBeta(c, depth, tab_piece, n - 1, n + 1, timeout, dict);
        }

        return test;


    }



    String key(int t[][], int depth, String max_min){
        String key = max_min + depth;
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                key += (t[i][j] + 1) / 10;
                key += (t[i][j] + 1) % 10;
            }
        }
        //Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
        return key;
    }

    int computer_move() throws TimeoutException{
        maxDepthAlphaBeta = 2;
        final long timeout = System.currentTimeMillis() + 2000;
        Map<String, Pair<ArrayList<Integer>, Integer>> dict = new HashMap();
        Pair<Integer, Integer> result = alphaBeta(color, 0, piece, -1000, 1000, System.currentTimeMillis() + 1000000, dict);
        int move = result.first;
        int value = result.second;
        maxDepthAlphaBeta++;
        assert move != -1 : "Error";
        try {
            while (true) {
                result = alphaBetaDichotomie(color, 0, piece, timeout, dict);
                int m = result.first;
                assert m != -1 : "Error";
                int v = result.second;
                //if (!(value > 50 && color == 0 || value < -50 && color == 1)) {
                move = m;
                value = v;
                maxDepthAlphaBeta++;
                //}
                //else{
                //    Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
                //}
            }
        } catch (TimeoutException e) {
            //Toast.makeText(this, "" + maxDepthAlphaBeta, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "" + dict.size(), Toast.LENGTH_SHORT).show();
            return move;
        }
    }

    void end(){
        boolean end = true;
        for (int i = 0; end && i < taille; i++){
            for (int j = 0; end && j < taille; j++){
                if (piece[i][j] != -1 && piece[i][j] % 2 == color){
                    ArrayList<Integer> possible = possible(i * taille + j, color, piece);
                    if (!possible.isEmpty())
                        end = false;
                }
            }
        }
        if (end){
            AlertDialog.Builder fin_ = new AlertDialog.Builder(this);
            fin_.setTitle("Echec et mat !!!");
            if (color == 0)
                fin_.setMessage("Victoire des noirs");
            else
                fin_.setMessage("Victoire des blancs");
            fin_.setPositiveButton("Rejouer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new_();
                }
            });
            fin_.setNeutralButton("Fermer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            fin_.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);
                    finish();

                }
            });
            fin_.show();
            fin = true;
        }
    }

    void unDo(){
        if (nb_players == 0 || isMoving)
            return;
        if (gameMoves.size() > 3 - nb_players) {
            for (int i = 0; i < 3 - nb_players; i++) {
                gameMoves.remove(gameMoves.size() - 1);
            }
            piece = copy(gameMoves.get(gameMoves.size() - 1));
            for (int i = 0; i < taille; i++) {
                for (int j = 0; j < taille; j++) {
                    Button b = (Button) findViewById(tab_button[i][j]);
                    set(b, i, j);
                }
            }
            if (nb_players == 2) {
                color = 1 - color;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echecs);
        initialise_piece();
        create_layout();
        this.grille = findViewById(R.id.grille);
        this.main = findViewById(R.id.main);
        this.new_=findViewById(R.id.new_);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(main);
                finish();
            }
        });
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
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                move(tab_button[i][j]);
            }
        }
        this.unDo = (Button) findViewById(R.id.undo);
        unDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unDo();
            }
        });
    }
}
