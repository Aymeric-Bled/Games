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
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class Dames extends AppCompatActivity {
    private ImageView new_;
    private Spinner player;
    private String players[] = {"1 joueur","2 joueurs","0 joueur"};
    private ImageView main;
    private GridLayout grille_board;
    private GridLayout grille_piece;
    private int taille = 10;
    private Table tab_board;
    private Table tab_piece;
    private PlateauDames plateau;
    private int width;
    private ArrayList<Pair<Integer, Integer>> possibleMoves = new ArrayList<>();
    private int color = 0;
    private ArrayList<Pair<Integer, Integer>> currentMove = new ArrayList<>();
    private ArrayList<PlateauDames.DamesMove> moves = new ArrayList<>();
    private boolean canMove = true;
    private int maxDepthAlphaBeta = 8;
    private int nbPlayer = 1;
    private AnimatorSet s = new AnimatorSet();
    private MCTS root;





    boolean AreEqual(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2){
        return p1.first == p2.first && p1.second == p2.second;
    }

    private class PlateauDames extends Plateau{

        Piece[][] value = new Piece[taille][taille];

        PlateauDames(){
            for (int i=0; i<taille;i++) {
                for (int j = 0; j < taille; j++) {
                    value[i][j] = null;
                    if((i+j)%2==1){
                        if (i < taille / 2 - 1) {
                            value[i][j] = new Piece(1);
                            tab_piece.getButton(i,j).setBackgroundResource(R.drawable.blackbutton);
                        }
                        else if (i > taille / 2){
                            value[i][j] = new Piece(0);
                            tab_piece.getButton(i,j).setBackgroundResource(R.drawable.beigebutton);
                        }
                    }
                }
            }
        }

        PlateauDames(PlateauDames plateauDames){
            this.value = new Piece[taille][taille];
            for (int i = 0; i < taille; i++){
                for (int j = 0; j < taille; j++){
                    if (plateauDames.value[i][j] == null) {
                        this.value[i][j] = null;
                    }
                    else{
                        this.value[i][j] = new Piece(plateauDames.value[i][j]);
                    }
                }
            }
        }

        public class Piece{
            private int color;
            private boolean Dame;



            Piece(int color){
                this.color = color;
                this.Dame = false;
            }

            Piece(Piece piece){
                this.color = piece.color;
                this.Dame = piece.Dame;
            }



            private void addeatMoves(ArrayList<DamesMove> moves, DamesMove currentMove, int i, int j, int direction){
                boolean end = true;
                if (!Dame) {
                    if (direction == 1) {
                        if (i < taille - 2) {
                            if (j < taille - 2) {
                                if (value[i + 2][j + 2] == null && value[i + 1][j + 1] != null && value[i + 1][j + 1].getColor() != this.color && !contains(currentMove.eatenPieces, new Pair(i + 1,j + 1))) {
                                    currentMove.positions.add(new Pair(i + 2, j + 2));
                                    currentMove.eatenPieces.add(new Pair(i + 1,j + 1));
                                    addeatMoves(moves, currentMove.copy(), i + 2, j + 2, direction);
                                    currentMove.positions.remove(currentMove.positions.size() - 1);
                                    currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                                    end = false;

                                }
                            }
                            if (j > 1) {
                                if (value[i + 2][j - 2] == null && value[i + 1][j - 1] != null && value[i + 1][j - 1].getColor() != this.color && !contains(currentMove.eatenPieces, new Pair(i + 1,j - 1))) {
                                    currentMove.positions.add(new Pair(i + 2, j - 2));
                                    currentMove.eatenPieces.add(new Pair(i + 1,j - 1));
                                    addeatMoves(moves, currentMove.copy(), i + 2, j - 2, direction);
                                    currentMove.positions.remove(currentMove.positions.size() - 1);
                                    currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                                    end = false;
                                }
                            }
                        }
                        if (i > 1) {
                            if (j < taille - 2) {
                                if (value[i - 2][j + 2] == null && value[i - 1][j + 1] != null && value[i - 1][j + 1].getColor() != this.color && !contains(currentMove.eatenPieces, new Pair(i - 1,j + 1))) {
                                    currentMove.positions.add(new Pair(i - 2, j + 2));
                                    currentMove.eatenPieces.add(new Pair(i - 1,j + 1));
                                    addeatMoves(moves, currentMove.copy(), i - 2, j + 2, direction);
                                    currentMove.positions.remove(currentMove.positions.size() - 1);
                                    currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                                    end = false;

                                }
                            }
                            if (j > 1) {
                                if (value[i - 2][j - 2] == null && value[i - 1][j - 1] != null && value[i - 1][j - 1].getColor() != this.color && !contains(currentMove.eatenPieces, new Pair(i - 1,j - 1))) {
                                    currentMove.positions.add(new Pair(i - 2, j - 2));
                                    currentMove.eatenPieces.add(new Pair(i - 1,j - 1));
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
                                        if (i + x * (k + 1) >= 0 && i + x * (k + 1) < taille && j + y * (k + 1) >= 0 && j + y * (k + 1) < taille && value[i + x * (k + 1)][j + y * (k + 1)] == null && !contains(currentMove.eatenPieces, new Pair(i + x * k,j + y * k))) {
                                            currentMove.eatenPieces.add(new Pair(i + x * k,j + y * k));
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
                                    if (value[coordinate.first + x][coordinate.second + y] != null && value[coordinate.first + x][coordinate.second + y].getColor() != color && value[coordinate.first + 2 * x][coordinate.second + 2 * y] == null && !contains(currentMove.eatenPieces, new Pair(coordinate.first + x,coordinate.second + y))){
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

            ArrayList<DamesMove> getMoves(int i, int j){
                ArrayList<DamesMove> moves = new ArrayList<>();
                DamesMove currentMove = new DamesMove(this.color);
                currentMove.positions.add(new Pair(i,j));
                addeatMoves(moves, currentMove, i, j, 1);
                addeatMoves(moves, currentMove, i, j, -1);
                if (!Dame) {
                    if (this.color == 1) {
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

            ArrayList<DamesMove> getLegalMoves(int i, int j){
                ArrayList<DamesMove> moves = this.getMoves(i,j);
                if (mustEat(this.color)){
                    while(!moves.isEmpty() && moves.get(moves.size() - 1).eatenPieces.isEmpty()){
                        moves.remove(moves.size() - 1);
                    }
                }
                return moves;
            }

            int getColor(){
                return this.color;
            }

            int getValue(){
                if (this.color == 1){
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

        public class DamesMove extends Move{
            ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
            ArrayList<Pair<Integer, Integer>> eatenPieces = new ArrayList<>();
            boolean newDame = false;

            public DamesMove(int color) {
                super(color);
            }

            DamesMove copy(){
                DamesMove move = new DamesMove(this.color);
                move.positions = new ArrayList<>();
                for (Pair<Integer, Integer> pair : this.positions){
                    move.positions.add(new Pair<>(pair.first, pair.second));
                }
                move.eatenPieces = new ArrayList<>();
                for (Pair<Integer, Integer> pair : this.eatenPieces){
                    move.eatenPieces.add(new Pair<>(pair.first, pair.second));
                }
                move.newDame = this.newDame;
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

        @Override
        void doMove(Move move) {
            DamesMove damesMove = (DamesMove) move;
            int color = damesMove.getColor();
            for (Pair<Integer, Integer> pair : damesMove.eatenPieces){
                value[pair.first][pair.second] = null;
            }
            Pair<Integer, Integer> start = damesMove.positions.get(0);
            Pair<Integer, Integer> end = damesMove.positions.get(damesMove.positions.size() - 1);
            if (start.first != end.first || start.second != end.second) {
                value[end.first][end.second] = value[start.first][start.second];
                value[start.first][start.second] = null;
            }
            if ((color == 0 && end.first == 0 || color == 1 && end.first == taille - 1) && !value[end.first][end.second].Dame){
                damesMove.newDame = true;
            }
            if (damesMove.newDame){
                value[end.first][end.second].Dame = true;
            }
        }

        @Override
        void undoMove(Move move) {
            DamesMove damesMove = (DamesMove) move;
            int color = damesMove.getColor();
            Pair<Integer, Integer> start = damesMove.positions.get(0);
            Pair<Integer, Integer> end = damesMove.positions.get(damesMove.positions.size() - 1);
            if (start.first != end.first || start.second != end.second) {
                value[start.first][start.second] = value[end.first][end.second];
                value[end.first][end.second] = null;
            }
            if (damesMove.newDame){
                value[start.first][start.second].Dame = false;
            }
            for (Pair<Integer, Integer> pair : damesMove.eatenPieces){
                value[pair.first][pair.second] = new Piece(1 - color);
            }
        }

        @Override
        Plateau copy() {
            return new PlateauDames(this);
        }
        @Override
        int getWinner(int color) {
            if (value() > 0){
                return 0;
            }
            else if (value() < 1){
                return 1;
            }
            return -1;
        }

        @Override
        boolean isGameOver(int color) {
            return getLegalMoves(color).isEmpty();
        }

        @Override
        ArrayList<Move> getLegalMoves(int color) {
            ArrayList<Move> moves = new ArrayList<>();
            boolean mustEat = false;
            for (int i = 0; i < taille; i++){
                for (int j = 0; j < taille; j++){
                    if (value[i][j] != null && ((value[i][j].getValue() > 0 && color == 0) || (value[i][j].getValue() < 0 && color == 1))){
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

        boolean mustEat(int color){
            for (int i = 0; i < taille; i++){
                for (int j = 0; j < taille; j++){
                    if (value[i][j] != null && value[i][j].getColor() == color){
                        ArrayList<DamesMove> moves = value[i][j].getMoves(i,j);
                        if (!moves.isEmpty() && !moves.get(0).eatenPieces.isEmpty()){
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        private void addeatMoves(ArrayList<DamesMove> moves, DamesMove currentMove, int color, int i, int j, int direction, boolean Dame){
            boolean end = true;
            if (!Dame) {
                if (direction == 1) {
                    if (i < taille - 2) {
                        if (j < taille - 2) {
                            if (value[i + 2][j + 2] == null && value[i + 1][j + 1] != null && ((value[i + 1][j + 1].getValue() > 0 && color == 1) || (value[i + 1][j + 1].getValue() < 0 && color == 0)) && !contains(currentMove.eatenPieces, new Pair(i + 1,j + 1))) {
                                currentMove.positions.add(new Pair(i + 2, j + 2));
                                currentMove.eatenPieces.add(new Pair(i + 1,j + 1));
                                addeatMoves(moves, currentMove, color, i + 2, j + 2, direction, Dame);
                                currentMove.positions.remove(currentMove.positions.size() - 1);
                                currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                                end = false;

                            }
                        }
                        if (j > 1) {
                            if (value[i + 2][j - 2] == null && value[i + 1][j - 1] != null && ((value[i + 1][j - 1].getValue() > 0 && color == 1) || (value[i + 1][j - 1].getValue() < 0 && color == 0)) && !contains(currentMove.eatenPieces, new Pair(i + 1,j - 1))) {
                                currentMove.positions.add(new Pair(i + 2, j - 2));
                                currentMove.eatenPieces.add(new Pair(i + 1,j - 1));
                                addeatMoves(moves, currentMove, color, i + 2, j - 2, direction, Dame);
                                currentMove.positions.remove(currentMove.positions.size() - 1);
                                currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                                end = false;
                            }
                        }
                    }
                    if (i > 1) {
                        if (j < taille - 2) {
                            if (value[i - 2][j + 2] == null && value[i - 1][j + 1] != null && ((value[i - 1][j + 1].getValue() > 0 && color == 1) || (value[i - 1][j + 1].getValue() < 0 && color == 0)) && !contains(currentMove.eatenPieces, new Pair(i - 1,j + 1))) {
                                currentMove.positions.add(new Pair(i - 2, j + 2));
                                currentMove.eatenPieces.add(new Pair(i - 1,j + 1));
                                addeatMoves(moves, currentMove, color, i - 2, j + 2, direction, Dame);
                                currentMove.positions.remove(currentMove.positions.size() - 1);
                                currentMove.eatenPieces.remove(currentMove.eatenPieces.size() - 1);
                                end = false;

                            }
                        }
                        if (j > 1) {
                            if (value[i - 2][j - 2] == null && value[i - 1][j - 1] != null && ((value[i - 1][j - 1].getValue() > 0 && color == 1) || (value[i - 1][j - 1].getValue() < 0 && color == 0)) && !contains(currentMove.eatenPieces, new Pair(i - 1,j - 1))) {
                                currentMove.positions.add(new Pair(i - 2, j - 2));
                                currentMove.eatenPieces.add(new Pair(i - 1,j - 1));
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
                                if (value[i + x * k][j + y * k] != null && ((value[i + x * k][j + y * k].getValue() > 0 && color == 1) || (value[i + x * k][j + y * k].getValue() < 0 && color == 0))) {
                                    if (i + x * (k + 1) >= 0 && i + x * (k + 1) < taille && j + y * (k + 1) >= 0 && j + y * (k + 1) < taille && value[i + x * (k + 1)][j + y * (k + 1)] == null && !contains(currentMove.eatenPieces, new Pair(i + x * k,j + y * k))) {
                                        currentMove.eatenPieces.add(new Pair(i + x * k,j + y * k));
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
                                if (value[coordinate.first + x][coordinate.second + y] != null && ((value[coordinate.first + x][coordinate.second + y].getValue() > 0 && color == 1) || (value[coordinate.first + x][coordinate.second + y].getValue() < 0 && color == 0)) && value[coordinate.first + 2 * x][coordinate.second + 2 * y] == null && !contains(currentMove.eatenPieces, new Pair(coordinate.first + x,coordinate.second + y))){
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

        ArrayList<DamesMove> getMoves(int  color, int i, int j){
            ArrayList<DamesMove> moves = new ArrayList<>();
            DamesMove currentMove = new DamesMove(color);
            currentMove.positions.add(new Pair(i,j));
            addeatMoves(moves, currentMove, color, i, j, 1, value[i][j].Dame);
            addeatMoves(moves, currentMove, color, i, j, -1, value[i][j].Dame);
            if (!value[i][j].Dame) {
                if (color == 1) {
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
    }

    private class MCTS_Dames extends MCTS{
        public MCTS_Dames(int color, Move move, MCTS parent) {
            super(color, move, parent);
        }

        @Override
        MCTS newMCTS(int color, Move move, MCTS parent) {
            return new MCTS_Dames(color, move, parent);
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
        return new Dames.MCTS_Dames(color, null, null);
    }

    void updateTree(PlateauDames.DamesMove move){
        for (MCTS child : this.root.children){
            PlateauDames.DamesMove mv = (PlateauDames.DamesMove) child.move;
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
            if (plateau.value[i][j] == null || plateau.value[i][j].getColor() != color) {
                return;
            }
            moves = plateau.value[i][j].getLegalMoves(i,j);
            if (!moves.isEmpty()) {
                currentMove.add(new Pair(i, j));
                for (PlateauDames.DamesMove move : moves) {
                    Pair<Integer, Integer> coordinate = move.positions.get(1);
                    tab_board.getButton(coordinate.first, coordinate.second).setBackgroundResource(R.drawable.possible_gris);
                    possibleMoves.add(new Pair(coordinate.first, coordinate.second));
                }
            }
        }
        else{
            if (contains(possibleMoves, new Pair(i, j))) {
                currentMove.add(new Pair(i, j));
                ArrayList<PlateauDames.DamesMove> finalMoves = new ArrayList<>();
                for (PlateauDames.DamesMove move : moves) {
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
                    for (PlateauDames.DamesMove move : moves) {
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

    void doMove(final PlateauDames.DamesMove move, final int ind){
        if (ind == move.positions.size() - 1){
            updateTree(move);
            color = 1 - color;
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
            else if (nbPlayer == 1){
                if (color == 1) {
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
        plateau.value[end.first][end.second] = plateau.value[start.first][start.second];
        tab_board.getButton(end.first, end.second).setBackgroundColor(getColor(R.color.grey));
        ObjectAnimator objectAnimator1;
        if (ind == move.positions.size() - 2 && plateau.value[end.first][end.second].getColor() == 0 && end.first == 0) {
            plateau.value[end.first][end.second].Dame = true;
            objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.beigedame, R.drawable.beigedame);
        }
        else if(ind == move.positions.size() - 2 && plateau.value[end.first][end.second].getColor() == 1 && end.first == taille - 1){
            plateau.value[end.first][end.second].Dame = true;
            objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.blackdame, R.drawable.blackdame);
        }
        else {
            if (plateau.value[end.first][end.second].getColor() == 1) {
                if (plateau.value[end.first][end.second].Dame) {
                    objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.blackdame, R.drawable.blackdame);
                } else {
                    objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.blackbutton, R.drawable.blackbutton);
                }
            } else {
                if (plateau.value[end.first][end.second].Dame) {
                    objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.beigedame, R.drawable.beigedame);
                } else {
                    objectAnimator1 = ObjectAnimator.ofObject(tab_piece.getButton(end.first, end.second), "backgroundResource", new ArgbEvaluator(), R.drawable.beigebutton, R.drawable.beigebutton);
                }
            }
        }
        objectAnimator1.setDuration(300);
        s.play(objectAnimator1);
        plateau.value[start.first][start.second] = null;
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(tab_piece.getButton(start.first, start.second), "backgroundColor", new ArgbEvaluator(), getColor(R.color.trans), getColor(R.color.trans));
        objectAnimator.setDuration(300);
        s.play(objectAnimator).with(objectAnimator1);

        int abs = Math.abs(end.first - start.first);
        for (int k = 1; k < abs; k++){
            int i = start.first + (k * (end.first - start.first)) / abs;
            int j = start.second + (k * (end.second - start.second)) / abs;
            plateau.value[i][j] = null;
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

    Pair<ArrayList<PlateauDames.DamesMove>, Integer> alphaBeta(int color, int depth, int alpha, int beta){
        if (depth == 0){
            return new Pair(new ArrayList<>(), plateau.value());
        }
        ArrayList<PlateauDames.DamesMove> bestMoves = new ArrayList<>();
        ArrayList<Move> legalMoves = plateau.getLegalMoves(color);
        if (legalMoves.isEmpty()){
            return new Pair(new ArrayList<>(), plateau.value());
        }
        if (color == 0){
            int max = -10000;
            for (Move move : legalMoves){
                plateau.doMove((PlateauDames.DamesMove) move);
                Pair<ArrayList<PlateauDames.DamesMove>, Integer> m = alphaBeta(1 - color, depth - 1, alpha, beta);
                plateau.undoMove((PlateauDames.DamesMove) move);
                if (m.second == max){
                    bestMoves.add((PlateauDames.DamesMove) move);
                }
                else if (m.second > max) {
                    max = m.second;
                    bestMoves.clear();
                    bestMoves.add((PlateauDames.DamesMove) move);
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
                plateau.doMove((PlateauDames.DamesMove) move);
                Pair<ArrayList<PlateauDames.DamesMove>, Integer> m = alphaBeta(1 - color, depth - 1, alpha, beta);
                plateau.undoMove((PlateauDames.DamesMove) move);
                if (m.second == min){
                    bestMoves.add((PlateauDames.DamesMove) move);
                }
                else if (m.second < min) {
                    min = m.second;
                    bestMoves.clear();
                    bestMoves.add((PlateauDames.DamesMove) move);
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
        doMove((PlateauDames.DamesMove) root.getBestMove(plateau, 2000, 100, true, 20), 0);
    }




    boolean end(){
        if (plateau.getLegalMoves(color).isEmpty()){
            AlertDialog.Builder fin_ = new AlertDialog.Builder(this);
            if (plateau.value() > 0){
                fin_.setTitle("Victoire des blancs");
            }
            else if (plateau.value() < 0){
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
        color = 0;
        Button b_board;
        Button b_piece;
        for (int i=0; i<taille;i++) {
            for (int j = 0; j < taille; j++) {
                b_board = tab_board.getButton(i,j);
                b_piece = tab_piece.getButton(i,j);
                b_piece.setBackgroundColor(getColor(R.color.trans));
                if((i+j)%2==0){
                    b_board.setBackgroundColor(getColor(R.color.white));
                }
                else{
                    b_board.setBackgroundColor(getColor(R.color.grey));
                }
            }
        }
        plateau = new PlateauDames();
        this.root = newMCTS(0);
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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