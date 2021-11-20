package com.example.myapplication;

import android.content.Context;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

public class Table{
    private Button button[][];

    public Table(GridLayout grid, int x, int y, Context context, LinearLayout.LayoutParams params, int w){
        button = new Button[x][y];
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

    public Button getButton(int x, int y){
        return button[x][y];
    }
}
