package com.example.myapplication;

import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import java.util.ArrayList;

abstract public class MCTS {
    MCTS parent;
    boolean isLeaf = true;
    ArrayList<MCTS> children = new ArrayList();
    float p = (float)100;
    float n = 0;
    float w = 0;
    int mycolor;
    Move move;
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
        for (int i = 0; i < this.children.size(); i++){
            MCTS child = this.children.get(i);
            if (child == null){
                break;
            }
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

    void expand(Plateau plateau, boolean useDepth, int maxDepth){
        if (this.isLeaf){
            if (plateau.isGameOver(this.mycolor)){
                MCTS node = this;
                int c = plateau.getWinner(1 - this.mycolor);
                while (node != null){
                    if (c == -1){
                        node.w += 0.5;
                    }
                    else if (c != node.mycolor){
                        node.w += 1;
                    }
                    node.n += 1;
                    node = node.parent;
                }
                return;
            }
            ArrayList<Move> legalMoves = plateau.getLegalMoves(this.mycolor);
            if (!legalMoves.isEmpty()){
                //int[] weight = weight(this.mycolor);
                ArrayList<Pair<Move,MCTS>> pairs = new ArrayList<>();

                synchronized (this.children) {
                    for (Move move : legalMoves) {
                        MCTS node = newMCTS(1 - this.mycolor, move, this);
                        //node.p *= (weight[move] + 1);
                        this.children.add(node);
                        pairs.add(new Pair(move, node));
                    }
                    this.isLeaf = false;
                }
                for (Pair<Move,MCTS> pair : pairs){
                    Move move = pair.first;
                    MCTS node = pair.second;
                    //Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT).show();
                    //int[][] copy = copy(taille);
                    plateau.doMove(move);
                    int c = plateau.rollout(1 - this.mycolor, useDepth, maxDepth);
                    plateau.undoMove(move);
                    while (node != null){
                        if (c == -1){
                            node.w += 0.5;
                        }
                        else if (c != node.mycolor){
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
            plateau.doMove(child.move);
            child.expand(plateau, useDepth, maxDepth);
            plateau.undoMove(child.move);
        }


    }

    Move getBestMove(Plateau plateau, int time, int nbThread, boolean useDepth, int maxDepth){
        final long timeout = System.currentTimeMillis() + time;
        ArrayList<Plateau> plateaux = new ArrayList<>();
        for (int i = 0; i < nbThread; i++){
            plateaux.add(plateau.copy());
        }
        if (nbThread == 1){
            Plateau p = plateaux.get(0);
            while (System.currentTimeMillis() < timeout) {
                expand(p, useDepth, maxDepth);
            }
        }
        else {
            ArrayList<Thread> threads = new ArrayList<>();
            for (int i = 0; i < nbThread; i++) {
                int finalI = i;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Plateau p = plateaux.get(finalI);
                        while (System.currentTimeMillis() < timeout) {
                            expand(p, useDepth, maxDepth);
                        }
                    }
                };
                thread.start();
                threads.add(thread);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        float max = -1000;
        ArrayList<Move> bestMoves = new ArrayList<>();
        for (MCTS child : children){
            float value = child.q();
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

    abstract MCTS newMCTS(int color, Move move, MCTS parent);
    abstract Context getContext();
    abstract int[] weight(int color);
}

;
