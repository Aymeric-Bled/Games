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
    int move = -1;
    int depth = 0;

    public MCTS(int color, int move, MCTS parent){
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

    void expand(int[][] tab, int taille){
        if (this.isLeaf){
            if (isGameOver(tab)){
                MCTS node = this;
                int c = getWinner(tab);
                while (node != null){
                    if (c != node.mycolor){
                        node.w += 1;
                    }
                    node.n += 1;
                    node = node.parent;
                }
                return;
            }
            ArrayList<Integer> legalMoves = getLegalMoves(tab, this.mycolor);
            if (!legalMoves.isEmpty()){
                this.isLeaf = false;
                //int[] weight = weight(this.mycolor);
                for (int move : legalMoves){
                    MCTS node = newMCTS(1 - this.mycolor, move, this);
                    //node.p *= (weight[move] + 1);
                    this.children.add(node);
                    //Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT).show();
                    //int[][] copy = copy(tab, taille);
                    node.doMove(tab, this.mycolor, move);
                    int c = node.rollout(tab, 1 - this.mycolor, taille);
                    node.undoMove(tab, this.mycolor, move);
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
            //int[][] copy = copy(tab, taille);
            child.doMove(tab, this.mycolor, child.move);
            child.expand(tab, taille);
            child.undoMove(tab, this.mycolor, child.move);
        }


    }

    int rollout(int[][] tab, int mycolor, int taille){
        ArrayList<Integer> moves = new ArrayList<>();
        int color = mycolor;
        //int[][] copy = copy(tab, taille);
        while (!isGameOver(tab)){
            int move = chooseRandomMove(tab, color);
            moves.add(move);
            doMove(tab,color,move);
            color = 1 - color;
        }
        int c = getWinner(tab);
        while (!moves.isEmpty()){
            color = 1 - color;
            int move = moves.remove(moves.size() - 1);
            undoMove(tab,color,move);
        }
        return c;
    }
/*
    int [][] copy(int[][] tab, int taille){
        int copy[][] = new int[taille][taille];
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                copy[i][j] = tab[i][j];
            }
        }
        return copy;
    }
 */

    int getBestMove(int[][] tab, int taille, int time){
        final long timeout = System.currentTimeMillis() + time;
        int i = 0;
        while (System.currentTimeMillis() < timeout){
            i += 1;
            expand(tab, taille);
        }
        //Toast.makeText(getContext(), "" + i, Toast.LENGTH_SHORT).show();
        float max = -1000;
        ArrayList<Integer> bestMoves = new ArrayList<>();
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
            return 0;
        }
        return bestMoves.get((int) (Math.random() * bestMoves.size()));
    }

    abstract boolean isGameOver(int[][] tab);
    abstract int getWinner(int[][] tab);
    abstract ArrayList<Integer> getLegalMoves(int[][] tab, int color);
    abstract void doMove(int tab[][], int color, int move);
    abstract void undoMove(int tab[][], int color, int move);
    abstract MCTS newMCTS(int color, int move, MCTS parent);
    abstract int chooseRandomMove(int[][] tab, int color);
    abstract Context getContext();
    abstract int[] weight(int color);
}
