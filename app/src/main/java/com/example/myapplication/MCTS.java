package com.example.myapplication;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

abstract public class MCTS {
    MCTS parent = null;
    boolean isLeaf = true;
    ArrayList<MCTS> children = new ArrayList();
    float p = (float)1;
    float n = 0;
    float w = 0;
    int mycolor = -1;
    Move move = null;
    int depth = 0;

    public MCTS(int color, Move move, MCTS parent){
        this.mycolor = color;
        this.move = move;
        this.parent = parent;
        if (parent != null){
            this.depth = parent.depth + 1;
        }
    }

    float q(){
        if (this.n == 0){
            return 0;
        }
        return this.w / this.n;
    }

    float u(){
        return (float) (this.p * Math.sqrt(this.n) / (1 + this.n));
    }

    MCTS chooseChildren(){
        float max = -1;
        ArrayList<MCTS> bestChildren = new ArrayList<>();
        //Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT).show();
        for (MCTS child : this.children){
            float value = child.q() + child.u();
            if (value > max){
                max = value;
                bestChildren.clear();
                bestChildren.add(child);
            }
            else if (value == max){
                bestChildren.add(child);
            }
        }
        return bestChildren.get((int) (Math.random() * bestChildren.size()));
    }

    void expand(boolean useDepth, int maxDepth){
        if (this.isLeaf){
            if (isGameOver()){
                MCTS node = this;
                int c = getWinner();
                while (node != null){
                    if (c != node.mycolor){
                        node.w += 1;
                    }
                    node.n += 1;
                    node = node.parent;
                }
                return;
            }
            ArrayList<Move> legalMoves = getLegalMoves();
            if (!legalMoves.isEmpty()){
                this.isLeaf = false;
                //int[] weight = weight(this.mycolor);
                for (Move move : legalMoves){
                    MCTS node = newMCTS(1 - this.mycolor, move, this);
                    //node.p *= (weight[move] + 1);
                    this.children.add(node);
                    //Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT).show();
                    //int[][] copy = copy(taille);
                    node.doMove(this.mycolor, move);
                    int c = node.rollout(1 - this.mycolor, useDepth, maxDepth);
                    node.undoMove(this.mycolor, move);
                    while (node != null){
                        if (c != node.mycolor){
                            node.w += 1;
                        }
                        node.n += 1;
                        node = node.parent;
                    }

                }
            }

        }
        else{
            MCTS child = this.chooseChildren();
            child.doMove(this.mycolor, child.move);
            child.expand(useDepth, maxDepth);
            child.undoMove(this.mycolor, child.move);
        }


    }

    int rollout(int mycolor, boolean useDepth, int maxDepth){
        ArrayList<Move> moves = new ArrayList<>();
        int color = mycolor;
        int depth = 0;
        while (!isGameOver() && (!useDepth || depth < maxDepth)){
            Move move = chooseRandomMove(color);
            moves.add(move);
            doMove(color,move);
            color = 1 - color;
            depth ++;
        }
        int c = getWinner();
        while (!moves.isEmpty()){
            color = 1 - color;
            Move move = moves.remove(moves.size() - 1);
            undoMove(color,move);
        }
        return c;
    }
/*
    int [][] copy(int[][] int taille){
        int copy[][] = new int[taille][taille];
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                copy[i][j] = tab[i][j];
            }
        }
        return copy;
    }
 */

    Move getBestMove(int time, boolean useDepth, int maxDepth){
        final long timeout = System.currentTimeMillis() + time;
        int i = 0;
        while (System.currentTimeMillis() < timeout){
            i += 1;
            expand(useDepth, maxDepth);
        }
        //Toast.makeText(getContext(), "" + i, Toast.LENGTH_SHORT).show();
        float max = -1000;
        ArrayList<Move> bestMoves = new ArrayList<>();
        for (MCTS child : children){
            float value = child.q() - child.u();
            //Toast.makeText(getContext(), "" + value, Toast.LENGTH_SHORT).show();
            if (value > max){
                max = value;
                bestMoves.clear();
                bestMoves.add(child.move);
            }
            else if (value == max){
                bestMoves.add(child.move);
            }
        }
        if (bestMoves.isEmpty()){
            return null;
        }
        return bestMoves.get((int) (Math.random() * bestMoves.size()));
    }

    abstract boolean isGameOver();
    abstract int getWinner();
    abstract ArrayList<Move> getLegalMoves();
    abstract void doMove(int color, Move move);
    abstract void undoMove(int color, Move move);
    abstract MCTS newMCTS(int color, Move move, MCTS parent);
    abstract Move chooseRandomMove(int color);
    abstract Context getContext();
    abstract int[] weight(int color);
}

;
