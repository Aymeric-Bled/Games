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

public class Dames extends AppCompatActivity {
    private Button new_;
    private Spinner player;
    private String players[] = {"1 joueur","2 joueurs","0 joueur"};
    private Button main;
    private GridLayout grille_board;
    private GridLayout grille_piece;
    private int taille = 10;
    private Table tab_board;
    private Table tab_piece;
    private Piece[][] value = new Piece[taille][taille];
    private int width;
    private ArrayList<Pair<Pair<Integer, Integer>,Boolean>> possibleMoves = new ArrayList<>();
    private boolean color = false;
    private ArrayList<Pair<Integer, Integer>> currentMove = new ArrayList<>();
    private ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> moves = new ArrayList<>();
    private boolean canMove = true;
    private int maxDepthAlphaBeta = 8;
    private int nbPlayer = 1;
    private AnimatorSet s = new AnimatorSet();

    public class Piece{
        private boolean color;
        private int i;
        private int j;
        private Button piece;
        private boolean Dame;

        Piece(boolean color, int i, int j){
            this.color = color;
            this.i = i;
            this.j = j;
            this.piece = tab_piece.getButton(i,j);
            if (this.color){
                this.piece.setBackgroundResource(R.drawable.blackbutton);
            }
            else{
                this.piece.setBackgroundResource(R.drawable.beigebutton);
            }
            value[i][j] = this;
            this.Dame = false;
        }

        private void addeatMoves(ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> moves, Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>> currentMove, int i, int j, int direction){
            boolean end = true;
            if (!Dame) {
                if (direction == 1) {
                    if (i < taille - 2) {
                        if (j < taille - 2) {
                            if (value[i + 2][j + 2] == null && value[i + 1][j + 1] != null && value[i + 1][j + 1].getColor() != this.color && !contains(currentMove.second, new Pair(i + 1, j + 1))) {
                                currentMove.first.add(new Pair(i + 2, j + 2));
                                currentMove.second.add(new Pair(new Pair(i + 1, j + 1), value[i + 1][j + 1].Dame));
                                addeatMoves(moves, currentMove, i + 2, j + 2, direction);
                                currentMove.first.remove(currentMove.first.size() - 1);
                                currentMove.second.remove(currentMove.second.size() - 1);
                                end = false;

                            }
                        }
                        if (j > 1) {
                            if (value[i + 2][j - 2] == null && value[i + 1][j - 1] != null && value[i + 1][j - 1].getColor() != this.color && !contains(currentMove.second, new Pair(i + 1, j - 1))) {
                                currentMove.first.add(new Pair(i + 2, j - 2));
                                currentMove.second.add(new Pair(new Pair(i + 1, j - 1), value[i + 1][j - 1].Dame));
                                addeatMoves(moves, currentMove, i + 2, j - 2, direction);
                                currentMove.first.remove(currentMove.first.size() - 1);
                                currentMove.second.remove(currentMove.second.size() - 1);
                                end = false;
                            }
                        }
                    }
                    if (i > 1) {
                        if (j < taille - 2) {
                            if (value[i - 2][j + 2] == null && value[i - 1][j + 1] != null && value[i - 1][j + 1].getColor() != this.color && !contains(currentMove.second, new Pair(i - 1, j + 1))) {
                                currentMove.first.add(new Pair(i - 2, j + 2));
                                currentMove.second.add(new Pair(new Pair(i - 1, j + 1), value[i - 1][j + 1].Dame));
                                addeatMoves(moves, currentMove, i - 2, j + 2, direction);
                                currentMove.first.remove(currentMove.first.size() - 1);
                                currentMove.second.remove(currentMove.second.size() - 1);
                                end = false;

                            }
                        }
                        if (j > 1) {
                            if (value[i - 2][j - 2] == null && value[i - 1][j - 1] != null && value[i - 1][j - 1].getColor() != this.color && !contains(currentMove.second, new Pair(i - 1, j - 1))) {
                                currentMove.first.add(new Pair(i - 2, j - 2));
                                currentMove.second.add(new Pair(new Pair(i - 1, j - 1), value[i - 1][j - 1].Dame));
                                addeatMoves(moves, currentMove, i - 2, j - 2, direction);
                                currentMove.first.remove(currentMove.first.size() - 1);
                                currentMove.second.remove(currentMove.second.size() - 1);
                                end = false;
                            }
                        }

                    }
                }
            }
            else{
                for (int x = -1; x < 2;  x = x + 2){
                    for (int y = -1; y < 2; y = y + 2){
                        if (x * y == direction) {
                            int k = 1;
                            int eaten = 0;
                            while (i + x * k >= 0 && i + x * k < taille && j + y * k >= 0 && j + y * k < taille) {
                                if (value[i + x * k][j + y * k] != null && value[i + x * k][j + y * k].getColor() != color) {
                                    if (i + x * (k + 1) >= 0 && i + x * (k + 1) < taille && j + y * (k + 1) >= 0 && j + y * (k + 1) < taille && value[i + x * (k + 1)][j + y * (k + 1)] == null && !contains(currentMove.second, new Pair(i + x * k, j + y * k))) {
                                        currentMove.second.add(new Pair(new Pair(i + x * k, j + y * k), value[i + x * k][j + y * k].Dame));
                                        eaten++;
                                    } else {
                                        break;
                                    }
                                } else if (value[i + x * k][j + y * k] == null) {
                                    currentMove.first.add(new Pair(i + x * k, j + y * k));
                                    if (eaten > 0) {
                                        addeatMoves(moves, currentMove, i + x * k, j + y * k, -x * y);
                                        end = false;
                                    }
                                    currentMove.first.remove(currentMove.first.size() - 1);
                                }
                                else{
                                    break;
                                }
                                k++;
                            }
                            while (eaten > 0) {
                                currentMove.second.remove(currentMove.second.size() - 1);
                                eaten--;
                            }
                        }
                    }
                }

            }
            if (end && currentMove.first.size() > 1) {
                if (!Dame) {
                    moves.add(new Pair(currentMove.first.clone(), currentMove.second.clone()));
                } else {
                    boolean add = true;
                    Pair<Integer, Integer> coordinate = currentMove.first.get(currentMove.first.size() - 1);
                    for (int x = -1; add && x < 2; x = x + 2) {
                        for (int y = -1; add && y < 2; y = y + 2) {
                            if (coordinate.first + 2 * x >= 0 && coordinate.first + 2 * x < taille && coordinate.second + 2 * y >= 0 && coordinate.second+ 2 * y < taille){
                                if (value[coordinate.first + x][coordinate.second + y] != null && value[coordinate.first + x][coordinate.second + y].getColor() != color && value[coordinate.first + 2 * x][coordinate.second + 2 * y] == null && !contains(currentMove.second, new Pair(coordinate.first + x,coordinate.second + y))){
                                    add = false;
                                }
                            }
                        }
                    }
                    if (add){
                        moves.add(new Pair(currentMove.first.clone(), currentMove.second.clone()));
                    }
                }
            }
        }

        ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> getMoves(){
            ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> moves = new ArrayList<>();
            ArrayList<Pair<Integer, Integer>> path = new ArrayList<>();
            ArrayList<Pair<Integer, Integer>> eatenPieces = new ArrayList<>();
            path.add(new Pair(i,j));
            addeatMoves(moves, new Pair(path, eatenPieces), i, j, 1);
            addeatMoves(moves, new Pair(path, eatenPieces), i, j, -1);
            if (!Dame) {
                if (this.color) {
                    if (i < taille - 1) {
                        if (j < taille - 1) {
                            if (value[i + 1][j + 1] == null) {
                                path.add(new Pair(i + 1, j + 1));
                                moves.add(new Pair(path.clone(), eatenPieces.clone()));
                                path.remove(path.size() - 1);
                            }
                        }
                        if (j > 0) {
                            if (value[i + 1][j - 1] == null) {
                                path.add(new Pair(i + 1, j - 1));
                                moves.add(new Pair(path.clone(), eatenPieces.clone()));
                                path.remove(path.size() - 1);
                            }
                        }
                    }
                } else {
                    if (i > 0) {
                        if (j < taille - 1) {
                            if (value[i - 1][j + 1] == null) {
                                path.add(new Pair(i - 1, j + 1));
                                moves.add(new Pair(path.clone(), eatenPieces.clone()));
                                path.remove(path.size() - 1);
                            }
                        }
                        if (j > 0) {
                            if (value[i - 1][j - 1] == null) {
                                path.add(new Pair(i - 1, j - 1));
                                moves.add(new Pair(path.clone(), eatenPieces.clone()));
                                path.remove(path.size() - 1);
                            }
                        }
                    }
                }
            }
            else{
                for (int x = -1; x < 2;  x = x + 2){
                    for (int y = -1; y < 2; y = y + 2){
                        int k = 1;
                        while (i + x * k >= 0 && i + x * k < taille && j + y * k >= 0 && j + y * k < taille && value[i + x * k][j + y * k] == null){
                            path.add(new Pair(i + x * k, j + y * k ));
                            moves.add(new Pair(path.clone(), eatenPieces.clone()));
                            path.remove(path.size() - 1);
                            k++;
                        }
                    }
                }
            }
            return moves;
        }

        ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> getLegalMoves(){
            ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> moves = this.getMoves();
            if (mustEat()){
                while(!moves.isEmpty() && moves.get(moves.size() - 1).second.isEmpty()){
                    moves.remove(moves.size() - 1);
                }
            }
            return moves;
        }

        boolean getColor(){
            return this.color;
        }

        void setCoordinate(int i, int j){
            this.i = i;
            this.j = j;
        }
    }

    void create_layout(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width=(metrics.widthPixels)/taille;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (0.8 * width) + 2 * (int) (0.1 * width), (int) (0.8 * width) + 2 * (int) (0.1 * width));
        tab_board = new Table(grille_board, taille, taille, this, params, (int) (0.8 * width) + 2 * (int) (0.1 * width));
        params = new LinearLayout.LayoutParams((int) (0.8 * width), (int) (0.8 * width));
        params.setMargins((int) (0.1 * width), (int) (0.1 * width),(int) (0.1 * width), (int) (0.1 * width));
        tab_piece = new Table(grille_piece, taille, taille, this, params, (int) (0.8 * width));
    }

    boolean contains(ArrayList<Pair<Pair<Integer, Integer>,Boolean>> path, Pair<Integer,Integer> coordinate){
        for (Pair<Pair<Integer, Integer>,Boolean> point : path){
            if (point.first.first == coordinate.first && point.first.second == coordinate.second){
                return true;
            }
        }
        return false;
    }
    boolean contains(ArrayList<Pair<Integer,Integer>> shortPath, ArrayList<Pair<Integer,Integer>> longPath){
        if (shortPath.size() > longPath.size())
            return false;
        for (int i = 0; i < shortPath.size(); i++){
            if (shortPath.get(i).first != longPath.get(i).first || shortPath.get(i).second != longPath.get(i).second)
                return false;
        }
        return true;
    }
    void move(int i, int j){
        if (!canMove){
            return;
        }
        if (possibleMoves.isEmpty()) {
            if (value[i][j] == null || value[i][j].getColor() != color) {
                return;
            }
            moves = value[i][j].getLegalMoves();
            if (!moves.isEmpty()) {
                currentMove.add(new Pair(i, j));
                for (Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>> move : moves) {
                    Pair<Integer, Integer> coordinate = move.first.get(1);
                    tab_board.getButton(coordinate.first, coordinate.second).setBackgroundResource(R.drawable.possible_gris);
                    possibleMoves.add(new Pair(new Pair(coordinate.first, coordinate.second), false));
                }
            }
        }
        else{
            if (contains(possibleMoves, new Pair(i, j))) {
                currentMove.add(new Pair(i, j));
                ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> finalMoves = new ArrayList<>();
                for (Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>> move : moves) {
                    if (contains(currentMove, move.first)){
                        finalMoves.add(move);
                    }
                }
                moves = finalMoves;
                for (Pair<Pair<Integer,Integer>,Boolean> coordinate : possibleMoves){
                    tab_board.getButton(coordinate.first.first, coordinate.first.second).setBackgroundColor(getColor(R.color.grey));
                }
                possibleMoves.clear();
                if (moves.get(0).first.size() == currentMove.size()){
                    canMove = false;
                    doMove(moves.get(0), 0);
                    currentMove.clear();
                    moves.clear();
                }
                else{
                    tab_board.getButton(i, j).setBackgroundResource(R.drawable.path);
                    int ind = currentMove.size();
                    for (Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>> move : moves) {
                        Pair<Integer, Integer> coordinate = move.first.get(ind);
                        tab_board.getButton(coordinate.first, coordinate.second).setBackgroundResource(R.drawable.possible_gris);
                        possibleMoves.add(new Pair(new Pair(coordinate.first, coordinate.second), false));
                    }
                }
            }
            else {
                Pair<Integer,Integer> start = currentMove.get(0);
                for (Pair<Pair<Integer,Integer>,Boolean> coordinate : possibleMoves){
                    tab_board.getButton(coordinate.first.first, coordinate.first.second).setBackgroundColor(getColor(R.color.grey));
                }
                for (Pair<Integer,Integer> coordinate : currentMove){
                    tab_board.getButton(coordinate.first, coordinate.second).setBackgroundColor(getColor(R.color.grey));
                }
                currentMove.clear();
                possibleMoves.clear();
                if (i != start.first || j != start.second){
                    move(i,j);
                }
            }
        }
    }

    void doMove(final Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>> move, final int ind){
        if (ind >= move.first.size() - 1){
            color = !color;
            if (end()){
                return;
            }
            if (nbPlayer == 0){
                s = new AnimatorSet();
                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(main, "TextColor", new ArgbEvaluator(), Color.BLACK, Color.BLACK);
                objectAnimator.setDuration(200);
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
            if (nbPlayer == 1){
                if (color) {
                    s = new AnimatorSet();
                    ObjectAnimator objectAnimator = ObjectAnimator.ofObject(main, "TextColor", new ArgbEvaluator(), Color.BLACK, Color.BLACK);
                    objectAnimator.setDuration(200);
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
                else {
                    canMove = true;
                }
            }
            else {
                canMove = true;
            }
            return;
        }
        s = new AnimatorSet();
        Pair<Integer, Integer> start = move.first.get(ind);
        Pair<Integer, Integer> end = move.first.get(ind + 1);
        value[end.first][end.second] = value[start.first][start.second];
        value[end.first][end.second].setCoordinate(end.first, end.second);
        tab_board.getButton(end.first, end.second).setBackgroundColor(getColor(R.color.grey));
        ObjectAnimator objectAnimator1;
        if (ind == move.first.size() - 2 && !value[end.first][end.second].getColor() && end.first == 0) {
            value[end.first][end.second].Dame = true;
            objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.beigedame, R.drawable.beigedame);
        }
        else if(ind == move.first.size() - 2 && value[end.first][end.second].getColor() && end.first == taille - 1){
            value[end.first][end.second].Dame = true;
            objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.blackdame, R.drawable.blackdame);
        }
        else {
            if (value[end.first][end.second].getColor()) {
                if (value[end.first][end.second].Dame) {
                    objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.blackdame, R.drawable.blackdame);
                } else {
                    objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.blackbutton, R.drawable.blackbutton);
                }
            } else {
                if (value[end.first][end.second].Dame) {
                    objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.beigedame, R.drawable.beigedame);
                } else {
                    objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.beigebutton, R.drawable.beigebutton);
                }
            }
        }
        objectAnimator1.setDuration(300);
        s.play(objectAnimator1);
        value[start.first][start.second] = null;
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(tab_piece.getButton(start.first, start.second), "backgroundColor", new ArgbEvaluator(), getColor(R.color.trans), getColor(R.color.trans));
        objectAnimator.setDuration(300);
        s.play(objectAnimator).with(objectAnimator1);

        int abs = Math.abs(end.first - start.first);
        for (int k = 1; k < abs; k++){
            int i = start.first + (k * (end.first - start.first)) / abs;
            int j = start.second + (k * (end.second - start.second)) / abs;
            value[i][j] = null;
            objectAnimator = ObjectAnimator.ofObject(tab_piece.getButton(i,j), "backgroundColor", new ArgbEvaluator(), getColor(R.color.trans), getColor(R.color.trans));
            objectAnimator.setDuration(300);
            s.play(objectAnimator).with(objectAnimator1);
        }
        objectAnimator1.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        doMove(move, ind + 1);
                    }
                }
        );
        s.start();
    }

    boolean mustEat(){
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                if (value[i][j] != null && value[i][j].getColor() == color){
                    ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> moves = value[i][j].getMoves();
                    if (!moves.isEmpty() && !moves.get(0).second.isEmpty()){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private void addeatMoves(ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> moves, Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>> currentMove, int tab_value[][], boolean color, int i, int j, int direction, boolean Dame){
        boolean end = true;
        if (!Dame) {
            if (direction == 1) {
                if (i < taille - 2) {
                    if (j < taille - 2) {
                        if (tab_value[i + 2][j + 2] == 0 && tab_value[i + 1][j + 1] != 0 && ((tab_value[i + 1][j + 1] > 0 && color) || (tab_value[i + 1][j + 1] < 0 && !color)) && !contains(currentMove.second, new Pair(i + 1, j + 1))) {
                            currentMove.first.add(new Pair(i + 2, j + 2));
                            currentMove.second.add(new Pair(new Pair(i + 1, j + 1), Math.abs(tab_value[i + 1][j + 1]) == 5));
                            addeatMoves(moves, currentMove, tab_value, color, i + 2, j + 2, direction, Dame);
                            currentMove.first.remove(currentMove.first.size() - 1);
                            currentMove.second.remove(currentMove.second.size() - 1);
                            end = false;

                        }
                    }
                    if (j > 1) {
                        if (tab_value[i + 2][j - 2] == 0 && tab_value[i + 1][j - 1] != 0 && ((tab_value[i + 1][j - 1] > 0 && color) || (tab_value[i + 1][j - 1] < 0 && !color)) && !contains(currentMove.second, new Pair(i + 1, j - 1))) {
                            currentMove.first.add(new Pair(i + 2, j - 2));
                            currentMove.second.add(new Pair(new Pair(i + 1, j - 1), Math.abs(tab_value[i + 1][j - 1]) == 5));
                            addeatMoves(moves, currentMove, tab_value, color, i + 2, j - 2, direction, Dame);
                            currentMove.first.remove(currentMove.first.size() - 1);
                            currentMove.second.remove(currentMove.second.size() - 1);
                            end = false;
                        }
                    }
                }
                if (i > 1) {
                    if (j < taille - 2) {
                        if (tab_value[i - 2][j + 2] == 0 && tab_value[i - 1][j + 1] != 0 && ((tab_value[i - 1][j + 1] > 0 && color) || (tab_value[i - 1][j + 1] < 0 && !color)) && !contains(currentMove.second, new Pair(i - 1, j + 1))) {
                            currentMove.first.add(new Pair(i - 2, j + 2));
                            currentMove.second.add(new Pair(new Pair(i - 1, j + 1), Math.abs(tab_value[i - 1][j + 1]) == 5));
                            addeatMoves(moves, currentMove, tab_value, color, i - 2, j + 2, direction, Dame);
                            currentMove.first.remove(currentMove.first.size() - 1);
                            currentMove.second.remove(currentMove.second.size() - 1);
                            end = false;

                        }
                    }
                    if (j > 1) {
                        if (tab_value[i - 2][j - 2] == 0 && tab_value[i - 1][j - 1] != 0 && ((tab_value[i - 1][j - 1] > 0 && color) || (tab_value[i - 1][j - 1] < 0 && !color)) && !contains(currentMove.second, new Pair(i - 1, j - 1))) {
                            currentMove.first.add(new Pair(i - 2, j - 2));
                            currentMove.second.add(new Pair(new Pair(i - 1, j - 1), Math.abs(tab_value[i - 1][j - 1]) == 5));
                            addeatMoves(moves, currentMove, tab_value, color, i - 2, j - 2, direction, Dame);
                            currentMove.first.remove(currentMove.first.size() - 1);
                            currentMove.second.remove(currentMove.second.size() - 1);
                            end = false;
                        }
                    }

                }
            }
        }
        else{
            for (int x = -1; x < 2;  x = x + 2){
                    for (int y = -1; y < 2; y = y + 2){
                        if (x * y == direction) {
                            int k = 1;
                            int eaten = 0;
                            while (i + x * k >= 0 && i + x * k < taille && j + y * k >= 0 && j + y * k < taille) {
                                if (tab_value[i + x * k][j + y * k] != 0 && ((tab_value[i + x * k][j + y * k] > 0 && color) || (tab_value[i + x * k][j + y * k] < 0 && !color))) {
                                    if (i + x * (k + 1) >= 0 && i + x * (k + 1) < taille && j + y * (k + 1) >= 0 && j + y * (k + 1) < taille && tab_value[i + x * (k + 1)][j + y * (k + 1)] == 0 && !contains(currentMove.second, new Pair(i + x * k, j + y * k))) {
                                        currentMove.second.add(new Pair(new Pair(i + x * k, j + y * k), Math.abs(tab_value[i + x * k][j + y * k]) == 5));
                                        eaten++;
                                    } else {
                                        break;
                                    }
                                } else if (tab_value[i + x * k][j + y * k] == 0) {
                                    currentMove.first.add(new Pair(i + x * k, j + y * k));
                                    if (eaten > 0) {
                                        addeatMoves(moves, currentMove, tab_value, color, i + x * k, j + y * k, -direction, Dame);
                                        end = false;
                                    }
                                    currentMove.first.remove(currentMove.first.size() - 1);
                                }
                                else{
                                    break;
                                }
                                k++;

                            }
                            while (eaten > 0) {
                                currentMove.second.remove(currentMove.second.size() - 1);
                                eaten--;
                            }
                        }
                    }
                }
        }
        if (end && currentMove.first.size() > 1){
            if (!Dame) {
                moves.add(new Pair(currentMove.first.clone(), currentMove.second.clone()));
            } else {
                boolean add = true;
                Pair<Integer, Integer> coordinate = currentMove.first.get(currentMove.first.size() - 1);
                for (int x = -1; add && x < 2; x = x + 2) {
                    for (int y = -1; add && y < 2; y = y + 2) {
                        if (coordinate.first + 2 * x >= 0 && coordinate.first + 2 * x < taille && coordinate.second + 2 * y >= 0 && coordinate.second+ 2 * y < taille){
                            if (((tab_value[coordinate.first + x][coordinate.second + y] > 0 && color) || (tab_value[coordinate.first + x][coordinate.second + y] < 0 && !color)) && tab_value[coordinate.first + 2 * x][coordinate.second + 2 * y] == 0 && !contains(currentMove.second, new Pair(coordinate.first + x,coordinate.second + y))){
                                add = false;
                            }
                        }
                    }
                }
                if (add){
                    moves.add(new Pair(currentMove.first.clone(), currentMove.second.clone()));
                }
            }
        }
    }

    ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> getMoves(int tab_value[][], boolean color, int i, int j){
        ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> moves = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> path = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> eatenPieces = new ArrayList<>();
        path.add(new Pair(i,j));
        addeatMoves(moves, new Pair(path, eatenPieces), tab_value, color, i, j, 1, Math.abs(tab_value[i][j]) == 5);
        addeatMoves(moves, new Pair(path, eatenPieces), tab_value, color, i, j, -1, Math.abs(tab_value[i][j]) == 5);
        if (Math.abs(tab_value[i][j]) == 1) {
            if (color) {
                if (i < taille - 1) {
                    if (j < taille - 1) {
                        if (tab_value[i + 1][j + 1] == 0) {
                            path.add(new Pair(i + 1, j + 1));
                            moves.add(new Pair(path.clone(), eatenPieces.clone()));
                            path.remove(path.size() - 1);
                        }
                    }
                    if (j > 0) {
                        if (tab_value[i + 1][j - 1] == 0) {
                            path.add(new Pair(i + 1, j - 1));
                            moves.add(new Pair(path.clone(), eatenPieces.clone()));
                            path.remove(path.size() - 1);
                        }
                    }
                }
            } else {
                if (i > 0) {
                    if (j < taille - 1) {
                        if (tab_value[i - 1][j + 1] == 0) {
                            path.add(new Pair(i - 1, j + 1));
                            moves.add(new Pair(path.clone(), eatenPieces.clone()));
                            path.remove(path.size() - 1);
                        }
                    }
                    if (j > 0) {
                        if (tab_value[i - 1][j - 1] == 0) {
                            path.add(new Pair(i - 1, j - 1));
                            moves.add(new Pair(path.clone(), eatenPieces.clone()));
                            path.remove(path.size() - 1);
                        }
                    }
                }
            }
        }
        else{
            for (int x = -1; x < 2; x = x + 2){
                for (int y = -1; y < 2; y = y + 2){
                    int k = 1;
                    while (i + x * k >= 0 && i + x * k < taille && j + y * k >= 0 && j + y * k < taille && tab_value[i + x * k][j + y * k] == 0){
                        path.add(new Pair(i + x * k, j + y * k ));
                        moves.add(new Pair(path.clone(), eatenPieces.clone()));
                        path.remove(path.size() - 1);
                        k++;
                    }
                }
            }
        }
        return moves;
    }
    ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> getLegalMoves(int tab_value[][], boolean color) {
        ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> moves = new ArrayList<>();
        boolean mustEat = false;
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                if ((tab_value[i][j] > 0 && !color) || (tab_value[i][j] < 0 && color)){
                    ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> pieceMoves = getMoves(tab_value, color, i, j);
                    if (!pieceMoves.isEmpty() && !pieceMoves.get(0).second.isEmpty()){
                        mustEat = true;
                        moves.clear();
                    }
                    if (!mustEat){
                        moves.addAll(pieceMoves);
                    }
                    else{
                        int ind = 0;
                        while (ind < pieceMoves.size() && !pieceMoves.get(ind).second.isEmpty()){
                            moves.add(pieceMoves.get(ind));
                            ind++;
                        }
                    }
                }
            }
        }
        return moves;
    }

    void simulateMove(Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>> move, int tab_value[][], boolean color){
        Pair<Integer, Integer> start = move.first.get(0);
        Pair<Integer, Integer> end = move.first.get(move.first.size() - 1);
        tab_value[end.first][end.second] = tab_value[start.first][start.second];
        if ((!color && end.first == 0 || color && end.first == taille - 1) && Math.abs(tab_value[end.first][end.second]) == 1){
            tab_value[end.first][end.second] *= 5;
        }
        tab_value[start.first][start.second] = 0;
        for (Pair<Pair<Integer, Integer>,Boolean> coordinate : move.second){
            tab_value[coordinate.first.first][coordinate.first.second] = 0;
        }

    }

    void simulateUndoMove(Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>  move, int tab_value[][], boolean color){
        Pair<Integer, Integer> start = move.first.get(0);
        Pair<Integer, Integer> end = move.first.get(move.first.size() - 1);
        tab_value[start.first][start.second] = tab_value[end.first][end.second];
        if ((!color && end.first == 0 || color && end.first == taille - 1) && Math.abs(tab_value[start.first][start.second]) == 5){
            tab_value[start.first][start.second] /= 5;
        }
        tab_value[end.first][end.second] = 0;
        for (Pair<Pair<Integer, Integer>,Boolean> coordinate : move.second){
            if (color){
                if (coordinate.second){
                    tab_value[coordinate.first.first][coordinate.first.second] = 5;
                }
                else {
                    tab_value[coordinate.first.first][coordinate.first.second] = 1;
                }
            }
            else{
                if (coordinate.second){
                    tab_value[coordinate.first.first][coordinate.first.second] = -5;
                }
                else{
                    tab_value[coordinate.first.first][coordinate.first.second] = -1;
                }
            }
        }
    }

    int[][] createTabValue(){
        int[][] tab_value = new int[taille][taille];
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                if (value[i][j] == null){
                    tab_value[i][j] = 0;
                }
                else{
                    if (value[i][j].getColor()){
                        if (value[i][j].Dame)
                            tab_value[i][j] = -5;
                        else
                            tab_value[i][j] = -1;
                    }
                    else{
                        if (value[i][j].Dame)
                            tab_value[i][j] = 5;
                        else
                            tab_value[i][j] = 1;
                    }
                }
            }
        }
        return tab_value;
    }

    int value(int[][] tab_value){
        int v = 0;
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                v += tab_value[i][j];
            }
        }
        return v;
    }

    Pair<ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>>, Integer> alphaBeta(int tab_value[][], boolean color, int depth, int alpha, int beta){
        if (depth == 0){
            return new Pair(new ArrayList<>(), value(tab_value));
        }
        ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> bestMoves = new ArrayList<>();
        ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>> legalMoves = getLegalMoves(tab_value, color);
        if (legalMoves.isEmpty()){
            return new Pair(new ArrayList<>(), value(tab_value));
        }
        if (!color){
            int max = -10000;
            for (Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>> move : legalMoves){
                simulateMove(move, tab_value, color);
                Pair<ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>>, Integer> m = alphaBeta(tab_value, !color, depth - 1, alpha, beta);
                simulateUndoMove(move, tab_value, color);
                if (m.second == max){
                    bestMoves.add(move);
                }
                else if (m.second > max) {
                    max = m.second;
                    bestMoves.clear();
                    bestMoves.add(move);
                }
                if (max >= beta){
                    return new Pair(bestMoves, max);
                }
                if (max > alpha){
                    alpha = max;
                }
            }
            return new Pair(bestMoves, max);
        }
        else{
            int min = 10000;
            for (Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>> move : legalMoves){
                simulateMove(move, tab_value, color);
                Pair<ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>>, Integer> m = alphaBeta(tab_value, !color, depth - 1, alpha, beta);
                simulateUndoMove(move, tab_value, color);
                if (m.second == min){
                    bestMoves.add(move);
                }
                else if (m.second < min) {
                    min = m.second;
                    bestMoves.clear();
                    bestMoves.add(move);
                }
                if (min <= alpha){
                    return new Pair(bestMoves, min);
                }
                if (min < beta){
                    beta = min;
                }
            }
            return new Pair(bestMoves, min);
        }
    }

    void play_computer(){
        canMove = false;
        Pair<ArrayList<Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Pair<Integer, Integer>,Boolean>>>>, Integer> alphaBeta = alphaBeta(createTabValue(), color, maxDepthAlphaBeta, -10000, 10000);
        doMove(alphaBeta.first.get((int) (Math.random() * alphaBeta.first.size())), 0);
    }


    boolean end(){
        int [][] tab_value = createTabValue();
        if (getLegalMoves(tab_value, color).isEmpty()){
            AlertDialog.Builder fin_ = new AlertDialog.Builder(this);
            if (value(tab_value) > 0){
                fin_.setTitle("Victoire des blancs");
            }
            else if (value(tab_value) < 0){
                fin_.setTitle("Victoire des noirs");
            }
            else{
                fin_.setTitle("Match nul");
            }
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
            return true;
        }
        else{
            return false;
        }
    }

    void new_(){
        s.pause();
        canMove = true;
        color = false;
        Button b_board;
        Button b_piece;
        for (int i=0; i<taille;i++) {
            for (int j = 0; j < taille; j++) {
                value[i][j] = null;
                b_board = tab_board.getButton(i,j);
                b_piece = tab_piece.getButton(i,j);
                b_piece.setBackgroundColor(getColor(R.color.trans));
                if((i+j)%2==0){
                    b_board.setBackgroundColor(getColor(R.color.white));
                }
                else{
                    b_board.setBackgroundColor(getColor(R.color.grey));
                    if (i < taille / 2 - 1) {
                        new Piece(true, i, j);
                    }
                    else if (i > taille / 2){
                        new Piece(false, i, j);
                    }
                }
            }
        }
        if (nbPlayer == 0) {
            s = new AnimatorSet();
            ObjectAnimator objectAnimator = ObjectAnimator.ofObject(main, "TextColor", new ArgbEvaluator(), Color.BLACK, Color.BLACK);
            objectAnimator.setDuration(200);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dames);
        this.main = findViewById(R.id.main);
        this.grille_board = findViewById(R.id.grille_board);
        this.grille_piece = findViewById(R.id.grille_piece);
        this.player = (Spinner) findViewById(R.id.player);
        this.new_=findViewById(R.id.new_);
        new_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_();
            }
        });

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, players);
        player.setAdapter(spinnerArrayAdapter);
        player.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nbPlayer = (position + 1) % 3;
                new_();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(main);
                finish();
            }
        });
        create_layout();
        new_();
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                final int finalI = i;
                final int finalJ = j;
                tab_piece.getButton(i,j).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        move(finalI, finalJ);
                    }
                });
            }
        }
    }
}