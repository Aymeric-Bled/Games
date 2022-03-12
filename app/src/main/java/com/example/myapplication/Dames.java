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
    private ArrayList<Pair<Integer, Integer>> possibleMoves = new ArrayList<>();
    private boolean color = false;
    private ArrayList<Pair<Integer, Integer>> currentMove = new ArrayList<>();
    private ArrayList<DamesMove> moves = new ArrayList<>();
    private boolean canMove = true;
    private int maxDepthAlphaBeta = 8;
    private int nbPlayer = 1;
    private AnimatorSet s = new AnimatorSet();
    private MCTS root;

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

        private void addeatMoves(ArrayList<DamesMove> moves, DamesMove currentMove, int i, int j, int direction){
            boolean end = true;
            if (!Dame) {
                if (direction == 1) {
                    if (i < taille - 2) {
                        if (j < taille - 2) {
                            if (value[i + 2][j + 2] == null && value[i + 1][j + 1] != null && value[i + 1][j + 1].getColor() != this.color && !contains(currentMove.eatenPieces, value[i + 1][j + 1])) {
                                currentMove.positions.add(new Pair(i + 2, j + 2));
                                currentMove.eatenPieces.add(value[i + 1][j + 1]);
                                addeatMoves(moves, currentMove.copy(), i + 2, j + 2, direction);
                                currentMove.positions.remove(currentMove.positions.size() - 1);
                                currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                                end = false;

                            }
                        }
                        if (j > 1) {
                            if (value[i + 2][j - 2] == null && value[i + 1][j - 1] != null && value[i + 1][j - 1].getColor() != this.color && !contains(currentMove.eatenPieces, value[i + 1][j - 1])) {
                                currentMove.positions.add(new Pair(i + 2, j - 2));
                                currentMove.eatenPieces.add(value[i + 1][j - 1]);
                                addeatMoves(moves, currentMove.copy(), i + 2, j - 2, direction);
                                currentMove.positions.remove(currentMove.positions.size() - 1);
                                currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                                end = false;
                            }
                        }
                    }
                    if (i > 1) {
                        if (j < taille - 2) {
                            if (value[i - 2][j + 2] == null && value[i - 1][j + 1] != null && value[i - 1][j + 1].getColor() != this.color && !contains(currentMove.eatenPieces, value[i - 1][j + 1])) {
                                currentMove.positions.add(new Pair(i - 2, j + 2));
                                currentMove.eatenPieces.add(value[i - 1][j + 1]);
                                addeatMoves(moves, currentMove.copy(), i - 2, j + 2, direction);
                                currentMove.positions.remove(currentMove.positions.size() - 1);
                                currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                                end = false;

                            }
                        }
                        if (j > 1) {
                            if (value[i - 2][j - 2] == null && value[i - 1][j - 1] != null && value[i - 1][j - 1].getColor() != this.color && !contains(currentMove.eatenPieces, value[i - 1][j - 1])) {
                                currentMove.positions.add(new Pair(i - 2, j - 2));
                                currentMove.eatenPieces.add(value[i - 1][j - 1]);
                                addeatMoves(moves, currentMove.copy(), i - 2, j - 2, direction);
                                currentMove.positions.remove(currentMove.positions.size() - 1);
                                currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
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
                                    if (i + x * (k + 1) >= 0 && i + x * (k + 1) < taille && j + y * (k + 1) >= 0 && j + y * (k + 1) < taille && value[i + x * (k + 1)][j + y * (k + 1)] == null && !contains(currentMove.eatenPieces, value[i + x * k][j + y * k])) {
                                        currentMove.eatenPieces.add(value[i + x * k][j + y * k]);
                                        eaten++;
                                    } else {
                                        break;
                                    }
                                } else if (value[i + x * k][j + y * k] == null) {
                                    currentMove.positions.add(new Pair(i + x * k, j + y * k));
                                    if (eaten > 0) {
                                        addeatMoves(moves, currentMove.copy(), i + x * k, j + y * k, -x * y);
                                        end = false;
                                    }
                                    currentMove.positions.remove(currentMove.positions.size() - 1);
                                }
                                else{
                                    break;
                                }
                                k++;
                            }
                            while (eaten > 0) {
                                currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                                eaten--;
                            }
                        }
                    }
                }

            }
            if (end && currentMove.positions.size() > 1) {
                if (!Dame) {
                    moves.add(currentMove.copy());
                } else {
                    boolean add = true;
                    Pair<Integer, Integer> coordinate = currentMove.positions.get(currentMove.positions.size() - 1);
                    for (int x = -1; add && x < 2; x = x + 2) {
                        for (int y = -1; add && y < 2; y = y + 2) {
                            if (coordinate.first + 2 * x >= 0 && coordinate.first + 2 * x < taille && coordinate.second + 2 * y >= 0 && coordinate.second+ 2 * y < taille){
                                if (value[coordinate.first + x][coordinate.second + y] != null && value[coordinate.first + x][coordinate.second + y].getColor() != color && value[coordinate.first + 2 * x][coordinate.second + 2 * y] == null && !contains(currentMove.eatenPieces, value[coordinate.first + x][coordinate.second + y])){
                                    add = false;
                                }
                            }
                        }
                    }
                    if (add){
                        moves.add(currentMove.copy());
                    }
                }
            }
        }

        ArrayList<DamesMove> getMoves(){
            ArrayList<DamesMove> moves = new ArrayList<>();
            DamesMove currentMove = new DamesMove();
            currentMove.positions.add(new Pair(i,j));
            addeatMoves(moves, currentMove, i, j, 1);
            addeatMoves(moves, currentMove, i, j, -1);
            if (!Dame) {
                if (this.color) {
                    if (i < taille - 1) {
                        if (j < taille - 1) {
                            if (value[i + 1][j + 1] == null) {
                                currentMove.positions.add(new Pair(i + 1, j + 1));
                                moves.add(currentMove.copy());
                                currentMove.positions.remove(currentMove.positions.size() - 1);
                            }
                        }
                        if (j > 0) {
                            if (value[i + 1][j - 1] == null) {
                                currentMove.positions.add(new Pair(i + 1, j - 1));
                                moves.add(currentMove.copy());
                                currentMove.positions.remove(currentMove.positions.size() - 1);
                            }
                        }
                    }
                } else {
                    if (i > 0) {
                        if (j < taille - 1) {
                            if (value[i - 1][j + 1] == null) {
                                currentMove.positions.add(new Pair(i - 1, j + 1));
                                moves.add(currentMove.copy());
                                currentMove.positions.remove(currentMove.positions.size() - 1);
                            }
                        }
                        if (j > 0) {
                            if (value[i - 1][j - 1] == null) {
                                currentMove.positions.add(new Pair(i - 1, j - 1));
                                moves.add(currentMove.copy());
                                currentMove.positions.remove(currentMove.positions.size() - 1);
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
                            currentMove.positions.add(new Pair(i + x * k, j + y * k ));
                            moves.add(currentMove.copy());
                            currentMove.positions.remove(currentMove.positions.size() - 1);
                            k++;
                        }
                    }
                }
            }
            return moves;
        }

        ArrayList<DamesMove> getLegalMoves(){
            ArrayList<DamesMove> moves = this.getMoves();
            if (mustEat()){
                while(!moves.isEmpty() && moves.get(moves.size() - 1).eatenPieces.isEmpty()){
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

        int getValue(){
            if (this.color){
                if (Dame){
                    return -5;
                }
                else{
                    return -1;
                }
            }
            else{
                if (Dame){
                    return 5;
                }
                else{
                    return 1;
                }
            }
        }
    }

    private class DamesMove extends Move{
        ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
        ArrayList<Piece> eatenPieces = new ArrayList<>();
        boolean newDame = false;

        DamesMove copy(){
            DamesMove move = new DamesMove();
            move.positions = (ArrayList<Pair<Integer, Integer>>) this.positions.clone();
            move.eatenPieces = (ArrayList<Piece>) this.eatenPieces.clone();
            return move;
        }

        boolean isEqual(DamesMove move){
            if (this.positions.size() != move.positions.size()){
                return false;
            }
            for (int i = 0; i < this.positions.size(); i++){
                if (!AreEqual(this.positions.get(i), move.positions.get(i))){
                    return false;
                }

            }
            return true;
        }

    }

    boolean AreEqual(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2){
        return p1.first == p2.first && p1.second == p2.second;
    }

    private class MCTS_Dames extends MCTS{
        Dames dames;
        public MCTS_Dames(int color, Move move, MCTS parent, Dames dames) {
            super(color, move, parent);
            this.dames = dames;
        }

        @Override
        boolean isGameOver() {
            return dames.getLegalMoves(mycolor == 1).isEmpty();
        }

        @Override
        int getWinner() {
            if (value() > 0){
                return 0;
            }
            else if (value() < 1){
                return 1;
            }
            return -1;
        }

        @Override
        ArrayList<Move> getLegalMoves() {
            return dames.getLegalMoves(mycolor == 1);
        }

        @Override
        void doMove(int color, Move move) {
            simulateMove((DamesMove) move, color == 1);
        }

        @Override
        void undoMove(int color, Move move) {
            simulateUndoMove((DamesMove) move);
        }

        @Override
        MCTS newMCTS(int color, Move move, MCTS parent) {
            return new MCTS_Dames(color, move, parent, this.dames);
        }

        @Override
        Move chooseRandomMove(int color) {
            ArrayList<Move> moves = getLegalMoves();
            return moves.get((int) (Math.random() * moves.size()));
        }

        @Override
        Context getContext() {
            return getApplicationContext();
        }

        @Override
        int[] weight(int color) {
            return new int[0];
        }
    }

    MCTS newMCTS(int color){
        return new Dames.MCTS_Dames(color, null, null, this);
    }

    void updateTree(DamesMove move){
        for (MCTS child : this.root.children){
            DamesMove mv = (DamesMove) child.move;
            if (move.isEqual(mv)){
                this.root = child;
                this.root.parent = null;
                return;
            }
        }
        this.root = newMCTS(1-this.root.mycolor);
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

    boolean contains(ArrayList<Piece> pieces, Piece piece){
        for (Piece p : pieces){
            if (p == piece){
                return true;
            }
        }
        return false;
    }

    boolean contains(ArrayList<Pair<Integer,Integer>> path, Pair<Integer,Integer> coordinate){
        for (Pair<Integer,Integer> c : path){
            if (c.first == coordinate.first && c.second == coordinate.second){
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
                for (DamesMove move : moves) {
                    Pair<Integer, Integer> coordinate = move.positions.get(1);
                    tab_board.getButton(coordinate.first, coordinate.second).setBackgroundResource(R.drawable.possible_gris);
                    possibleMoves.add(new Pair(coordinate.first, coordinate.second));
                }
            }
        }
        else{
            if (contains(possibleMoves, new Pair(i, j))) {
                currentMove.add(new Pair(i, j));
                ArrayList<DamesMove> finalMoves = new ArrayList<>();
                for (DamesMove move : moves) {
                    if (contains(currentMove, move.positions)){
                        finalMoves.add(move);
                    }
                }
                moves = finalMoves;
                for (Pair<Integer, Integer> coordinate : possibleMoves){
                    tab_board.getButton(coordinate.first, coordinate.second).setBackgroundColor(getColor(R.color.grey));
                }
                possibleMoves.clear();
                if (moves.get(0).positions.size() == currentMove.size()){
                    canMove = false;
                    doMove(moves.get(0), 0);
                    currentMove.clear();
                    moves.clear();
                }
                else{
                    tab_board.getButton(i, j).setBackgroundResource(R.drawable.path);
                    int ind = currentMove.size();
                    for (DamesMove move : moves) {
                        Pair<Integer, Integer> coordinate = move.positions.get(ind);
                        tab_board.getButton(coordinate.first, coordinate.second).setBackgroundResource(R.drawable.possible_gris);
                        possibleMoves.add(new Pair(coordinate.first, coordinate.second));
                    }
                }
            }
            else {
                Pair<Integer,Integer> start = currentMove.get(0);
                for (Pair<Integer, Integer> coordinate : possibleMoves){
                    tab_board.getButton(coordinate.first, coordinate.second).setBackgroundColor(getColor(R.color.grey));
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

    void doMove(final DamesMove move, final int ind){
        if (ind >= move.positions.size() - 1){
            updateTree(move);
            //Toast.makeText(this, "" + root.n, Toast.LENGTH_SHORT).show();
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
        Pair<Integer, Integer> start = move.positions.get(ind);
        Pair<Integer, Integer> end = move.positions.get(ind + 1);
        value[end.first][end.second] = value[start.first][start.second];
        value[end.first][end.second].setCoordinate(end.first, end.second);
        tab_board.getButton(end.first, end.second).setBackgroundColor(getColor(R.color.grey));
        ObjectAnimator objectAnimator1;
        if (ind == move.positions.size() - 2 && !value[end.first][end.second].getColor() && end.first == 0) {
            value[end.first][end.second].Dame = true;
            objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.beigedame, R.drawable.beigedame);
        }
        else if(ind == move.positions.size() - 2 && value[end.first][end.second].getColor() && end.first == taille - 1){
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
                    ArrayList<DamesMove> moves = value[i][j].getMoves();
                    if (!moves.isEmpty() && !moves.get(0).eatenPieces.isEmpty()){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private void addeatMoves(ArrayList<DamesMove> moves, DamesMove currentMove, boolean color, int i, int j, int direction, boolean Dame){
        boolean end = true;
        if (!Dame) {
            if (direction == 1) {
                if (i < taille - 2) {
                    if (j < taille - 2) {
                        if (value[i + 2][j + 2] == null && value[i + 1][j + 1] != null && ((value[i + 1][j + 1].getValue() > 0 && color) || (value[i + 1][j + 1].getValue() < 0 && !color)) && !contains(currentMove.eatenPieces, value[i + 1][j + 1])) {
                            currentMove.positions.add(new Pair(i + 2, j + 2));
                            currentMove.eatenPieces.add(value[i + 1][j + 1]);
                            addeatMoves(moves, currentMove, color, i + 2, j + 2, direction, Dame);
                            currentMove.positions.remove(currentMove.positions.size() - 1);
                            currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                            end = false;

                        }
                    }
                    if (j > 1) {
                        if (value[i + 2][j - 2] == null && value[i + 1][j - 1] != null && ((value[i + 1][j - 1].getValue() > 0 && color) || (value[i + 1][j - 1].getValue() < 0 && !color)) && !contains(currentMove.eatenPieces, value[i + 1][j - 1])) {
                            currentMove.positions.add(new Pair(i + 2, j - 2));
                            currentMove.eatenPieces.add(value[i + 1][j - 1]);
                            addeatMoves(moves, currentMove, color, i + 2, j - 2, direction, Dame);
                            currentMove.positions.remove(currentMove.positions.size() - 1);
                            currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                            end = false;
                        }
                    }
                }
                if (i > 1) {
                    if (j < taille - 2) {
                        if (value[i - 2][j + 2] == null && value[i - 1][j + 1] != null && ((value[i - 1][j + 1].getValue() > 0 && color) || (value[i - 1][j + 1].getValue() < 0 && !color)) && !contains(currentMove.eatenPieces, value[i - 1][j + 1])) {
                            currentMove.positions.add(new Pair(i - 2, j + 2));
                            currentMove.eatenPieces.add(value[i - 1][j + 1]);
                            addeatMoves(moves, currentMove, color, i - 2, j + 2, direction, Dame);
                            currentMove.positions.remove(currentMove.positions.size() - 1);
                            currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                            end = false;

                        }
                    }
                    if (j > 1) {
                        if (value[i - 2][j - 2] == null && value[i - 1][j - 1] != null && ((value[i - 1][j - 1].getValue() > 0 && color) || (value[i - 1][j - 1].getValue() < 0 && !color)) && !contains(currentMove.eatenPieces, value[i - 1][j - 1])) {
                            currentMove.positions.add(new Pair(i - 2, j - 2));
                            currentMove.eatenPieces.add(value[i - 1][j - 1]);
                            addeatMoves(moves, currentMove, color, i - 2, j - 2, direction, Dame);
                            currentMove.positions.remove(currentMove.positions.size() - 1);
                            currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
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
                                if (value[i + x * k][j + y * k] != null && ((value[i + x * k][j + y * k].getValue() > 0 && color) || (value[i + x * k][j + y * k].getValue() < 0 && !color))) {
                                    if (i + x * (k + 1) >= 0 && i + x * (k + 1) < taille && j + y * (k + 1) >= 0 && j + y * (k + 1) < taille && value[i + x * (k + 1)][j + y * (k + 1)] == null && !contains(currentMove.eatenPieces, value[i + x * k][j + y * k])) {
                                        currentMove.eatenPieces.add(value[i + x * k][j + y * k]);
                                        eaten++;
                                    } else {
                                        break;
                                    }
                                } else if (value[i + x * k][j + y * k] == null) {
                                    currentMove.positions.add(new Pair(i + x * k, j + y * k));
                                    if (eaten > 0) {
                                        addeatMoves(moves, currentMove, color, i + x * k, j + y * k, -direction, Dame);
                                        end = false;
                                    }
                                    currentMove.positions.remove(currentMove.positions.size() - 1);
                                }
                                else{
                                    break;
                                }
                                k++;

                            }
                            while (eaten > 0) {
                                currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                                eaten--;
                            }
                        }
                    }
                }
        }
        if (end && currentMove.positions.size() > 1){
            if (!Dame) {
                moves.add(currentMove.copy());
            } else {
                boolean add = true;
                Pair<Integer, Integer> coordinate = currentMove.positions.get(currentMove.positions.size() - 1);
                for (int x = -1; add && x < 2; x = x + 2) {
                    for (int y = -1; add && y < 2; y = y + 2) {
                        if (coordinate.first + 2 * x >= 0 && coordinate.first + 2 * x < taille && coordinate.second + 2 * y >= 0 && coordinate.second+ 2 * y < taille){
                            if (value[coordinate.first + x][coordinate.second + y] != null && ((value[coordinate.first + x][coordinate.second + y].getValue() > 0 && color) || (value[coordinate.first + x][coordinate.second + y].getValue() < 0 && !color)) && value[coordinate.first + 2 * x][coordinate.second + 2 * y] == null && !contains(currentMove.eatenPieces, value[coordinate.first + x][coordinate.second + y])){
                                add = false;
                            }
                        }
                    }
                }
                if (add){
                    moves.add(currentMove.copy());
                }
            }
        }
    }

    ArrayList<DamesMove> getMoves(boolean color, int i, int j){
        ArrayList<DamesMove> moves = new ArrayList<>();
        DamesMove currentMove = new DamesMove();
        currentMove.positions.add(new Pair(i,j));
        addeatMoves(moves, currentMove, color, i, j, 1, value[i][j].Dame);
        addeatMoves(moves, currentMove, color, i, j, -1, value[i][j].Dame);
        if (!value[i][j].Dame) {
            if (color) {
                if (i < taille - 1) {
                    if (j < taille - 1) {
                        if (value[i + 1][j + 1] == null) {
                            currentMove.positions.add(new Pair(i + 1, j + 1));
                            moves.add(currentMove.copy());
                            currentMove.positions.remove(currentMove.positions.size() - 1);
                        }
                    }
                    if (j > 0) {
                        if (value[i + 1][j - 1] == null) {
                            currentMove.positions.add(new Pair(i + 1, j - 1));
                            moves.add(currentMove.copy());
                            currentMove.positions.remove(currentMove.positions.size() - 1);
                        }
                    }
                }
            } else {
                if (i > 0) {
                    if (j < taille - 1) {
                        if (value[i - 1][j + 1] == null) {
                            currentMove.positions.add(new Pair(i - 1, j + 1));
                            moves.add(currentMove.copy());
                            currentMove.positions.remove(currentMove.positions.size() - 1);
                        }
                    }
                    if (j > 0) {
                        if (value[i - 1][j - 1] == null) {
                            currentMove.positions.add(new Pair(i - 1, j - 1));
                            moves.add(currentMove.copy());
                            currentMove.positions.remove(currentMove.positions.size() - 1);
                        }
                    }
                }
            }
        }
        else{
            for (int x = -1; x < 2; x = x + 2){
                for (int y = -1; y < 2; y = y + 2){
                    int k = 1;
                    while (i + x * k >= 0 && i + x * k < taille && j + y * k >= 0 && j + y * k < taille && value[i + x * k][j + y * k] == null){
                        currentMove.positions.add(new Pair(i + x * k, j + y * k ));
                        moves.add(currentMove.copy());
                        currentMove.positions.remove(currentMove.positions.size() - 1);
                        k++;
                    }
                }
            }
        }
        return moves;
    }
    ArrayList<Move> getLegalMoves(boolean color) {
        ArrayList<Move> moves = new ArrayList<>();
        boolean mustEat = false;
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                if (value[i][j] != null && ((value[i][j].getValue() > 0 && !color) || (value[i][j].getValue() < 0 && color))){
                    ArrayList<DamesMove> pieceMoves = getMoves(color, i, j);
                    if (!pieceMoves.isEmpty() && !pieceMoves.get(0).eatenPieces.isEmpty()){
                        mustEat = true;
                        moves.clear();
                    }
                    if (!mustEat){
                        moves.addAll(pieceMoves);
                    }
                    else{
                        int ind = 0;
                        while (ind < pieceMoves.size() && !pieceMoves.get(ind).eatenPieces.isEmpty()){
                            moves.add(pieceMoves.get(ind));
                            ind++;
                        }
                    }
                }
            }
        }
        return moves;
    }

    void simulateMove(DamesMove move, boolean color){
        Pair<Integer, Integer> start = move.positions.get(0);
        Pair<Integer, Integer> end = move.positions.get(move.positions.size() - 1);
        value[end.first][end.second] = value[start.first][start.second];
        value[end.first][end.second].i = end.first;
        value[end.first][end.second].j = end.second;
        if ((!color && end.first == 0 || color && end.first == taille - 1) && !value[end.first][end.second].Dame){
            move.newDame = true;
        }
        if (move.newDame){
            value[end.first][end.second].Dame = true;
        }
        value[start.first][start.second] = null;
        for (Piece piece : move.eatenPieces){
            value[piece.i][piece.j] = null;
        }

    }

    void simulateUndoMove(DamesMove move){
        Pair<Integer, Integer> start = move.positions.get(0);
        Pair<Integer, Integer> end = move.positions.get(move.positions.size() - 1);
        value[start.first][start.second] = value[end.first][end.second];
        value[start.first][start.second].i = start.first;
        value[start.first][start.second].j = start.second;
        if (move.newDame){
            value[start.first][start.second].Dame = false;
        }
        value[end.first][end.second] = null;
        for (Piece piece : move.eatenPieces){
            value[piece.i][piece.j] = piece;
        }
    }
/*
    int[][] createTabValue(){
        int[][] value = new int[taille][taille];
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                if (value[i][j] == null){
                    value[i][j] = 0;
                }
                else{
                    if (value[i][j].getColor()){
                        if (value[i][j].Dame)
                            value[i][j] = -5;
                        else
                            value[i][j] = -1;
                    }
                    else{
                        if (value[i][j].Dame)
                            value[i][j] = 5;
                        else
                            value[i][j] = 1;
                    }
                }
            }
        }
        return value;
    }

 */

    int value(){
        int v = 0;
        for (int i = 0; i < taille; i++){
            for (int j = 0; j < taille; j++){
                if (value[i][j] != null)
                    v += value[i][j].getValue();
            }
        }
        return v;
    }

    Pair<ArrayList<DamesMove>, Integer> alphaBeta(boolean color, int depth, int alpha, int beta){
        if (depth == 0){
            return new Pair(new ArrayList<>(), value());
        }
        ArrayList<DamesMove> bestMoves = new ArrayList<>();
        ArrayList<Move> legalMoves = getLegalMoves(color);
        if (legalMoves.isEmpty()){
            return new Pair(new ArrayList<>(), value());
        }
        if (!color){
            int max = -10000;
            for (Move move : legalMoves){
                simulateMove((DamesMove) move, color);
                Pair<ArrayList<DamesMove>, Integer> m = alphaBeta(!color, depth - 1, alpha, beta);
                simulateUndoMove((DamesMove) move);
                if (m.second == max){
                    bestMoves.add((DamesMove) move);
                }
                else if (m.second > max) {
                    max = m.second;
                    bestMoves.clear();
                    bestMoves.add((DamesMove) move);
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
            for (Move move : legalMoves){
                simulateMove((DamesMove) move, color);
                Pair<ArrayList<DamesMove>, Integer> m = alphaBeta(!color, depth - 1, alpha, beta);
                simulateUndoMove((DamesMove) move);
                if (m.second == min){
                    bestMoves.add((DamesMove) move);
                }
                else if (m.second < min) {
                    min = m.second;
                    bestMoves.clear();
                    bestMoves.add((DamesMove) move);
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
        //Pair<ArrayList<DamesMove>, Integer> alphaBeta = alphaBeta(color, maxDepthAlphaBeta, -10000, 10000);
        //doMove(alphaBeta.first.get((int) (Math.random() * alphaBeta.first.size())), 0);
        doMove((DamesMove) root.getBestMove(2000, true, 20), 0);
    }


    boolean end(){
        if (getLegalMoves(color).isEmpty()){
            AlertDialog.Builder fin_ = new AlertDialog.Builder(this);
            if (value() > 0){
                fin_.setTitle("Victoire des blancs");
            }
            else if (value() < 0){
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
        this.root = newMCTS(0);
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