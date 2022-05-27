package com.example.myapplication;

import java.util.ArrayList;

public abstract class Plateau {
    abstract void doMove(Move move);
    abstract void undoMove(Move move);
    abstract Plateau copy();
    abstract int getWinner(int color);
    abstract boolean isGameOver(int color);
    abstract ArrayList<Move> getLegalMoves(int color);
    Move chooseRandomMove(int color){
        ArrayList<Move> moves = getLegalMoves(color);
        assert !moves.isEmpty();
        return moves.get((int)(Math.random() * moves.size()));
    }

    int rollout(int mycolor, boolean useDepth, int maxDepth){
        ArrayList<Move> moves = new ArrayList<>();
        int color = mycolor;
        int depth = 0;
        while (!isGameOver(color) && (!useDepth || depth < maxDepth)){
            Move move = chooseRandomMove(color);
            moves.add(move);
            doMove(move);
            color = 1 - color;
            depth ++;
        }
        int c = getWinner(1 - color);
        while (!moves.isEmpty()){
            color = 1 - color;
            Move move = moves.remove(moves.size() - 1);
            undoMove(move);
        }
        return c;
    }
}
