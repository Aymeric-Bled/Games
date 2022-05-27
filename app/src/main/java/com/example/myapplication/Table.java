package com.example.myapplication;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

public class Table{
    private Button button[][];
    private int x;
    private int y;
    private GridLayout grid;

    public Table(GridLayout grid, int x, int y, Context context, LinearLayout.LayoutParams params, int w){
        button = new Button[x][y];
        this.x = x;
        this.y = y;
        this.grid = grid;
        grid.removeAllViewsInLayout();
        grid.setRowCount(x);
        grid.setColumnCount(y);
        for (int i=0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Button b = new Button(context);
                button[i][j]=b;
                b.setLayoutParams(params);
                b.setMinimumHeight(0);
                b.setMinimumWidth(0);
                b.setHeight(w);
                b.setWidth(w);
                grid.addView(b,i*y + j);
            }
        }
    }
    public Table(GridLayout grid, int x, int y, Context context, LinearLayout.LayoutParams params){
        button = new Button[x][y];
        this.x = x;
        this.y = y;
        this.grid = grid;
        grid.removeAllViewsInLayout();
        grid.setRowCount(x);
        grid.setColumnCount(y);
        for (int i=0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Button b = new Button(context);
                button[i][j]=b;
                b.setLayoutParams(params);
                b.setMinimumWidth(0);
                b.setMinimumHeight(0);
                grid.addView(b,i * y + j);
            }
        }
    }

    public void setButtonsDimension(int width, int height){
        for (int i=0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Button b = button[i][j];
                ViewGroup.LayoutParams params = b.getLayoutParams();
                params.height = height;
                params.width = width;
                b.setMinimumWidth(0);
                b.setMinimumHeight(0);
                //b.setWidth(width);
                //b.setHeight(height);
            }
        }
    }

    public Button getButton(int x, int y){
        return button[x][y];
    }
}
