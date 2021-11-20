package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

public class Cinq extends AppCompatActivity {
    private Button main;
    private Button new_;
    private Button what;
    private GridLayout grille;
    private int tab_button[][] = new int[5][5];
    private int tab_color[][] = new int[5][5];
    String gameState;


    int place (int b){
        for (int i =0; i < 5; i++)
            for (int j =0; j < 5; j++)
                if (tab_button[i][j] == b)
                    return 5*i+j;
                return -1;
    }

    void swap(int p){
        Button b =findViewById(tab_button[p/5][p%5]);
        if (tab_color[p/5][p%5] == 0){
            tab_color[p/5][p%5] = 1;
            b.setBackgroundColor(Color.BLACK);
        }
        else{
            tab_color[p/5][p%5] = 0;
            b.setBackgroundColor(Color.WHITE);
        }
    }

    void new_(){
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (tab_color[i][j] == 1)
                    swap(5*i+j);
    }

    void end(){
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (tab_color[i][j] == 0)
                    return;
        AlertDialog.Builder fin = new AlertDialog.Builder(this);
        fin.setTitle("Gagné !!!");
        fin.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();

            }
        });
        fin.setPositiveButton("Recommencer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new_();
            }
        });
        fin.setNeutralButton("Fermer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        fin.show();
    }

    void move(final int b){
        Button button = findViewById(b);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = place(b);
                swap(p);
                if (p % 5 > 0)
                    swap(p - 1);
                if (p % 5 < 4)
                    swap(p + 1);
                if (p / 5 > 0)
                    swap(p - 5);
                if (p / 5 < 4)
                    swap (p + 5);
                end();

            }
        });
    }


    void create_cinq(){
        Button b;
        int id;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w = (metrics.widthPixels - 6)/ 5;
        grille= findViewById(R.id.grille);
        grille.removeAllViews();
        grille.setColumnCount(5);
        grille.setRowCount(5);
        for (int i=0; i < 5; i++)
            for (int j=0; j<5; j++) {
                b = new Button(this);
                id = Button.generateViewId();
                b.setId(id);
                tab_button[i][j] = id;
                tab_color[i][j] = 0;
                b.setMinimumHeight(0);
                b.setMinimumWidth(0);
                b.setBackgroundColor(Color.WHITE);
                b.setHeight(w);
                b.setWidth(w);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.setMargins(1,1,1,1);
                b.setLayoutParams(params);
                grille.addView(b, params);
            }
    }

    void what(){
        AlertDialog.Builder what = new AlertDialog.Builder(this);
        what.setTitle("Règles");
        what.setMessage("L'objectif de ce jeu est de noircir entièrement le plateau. \nDifficulté : Lorsque l'on modifie la couleur d'une case on modifie également la couleur de ses voisins en formant des plus (+).");
        what.setNeutralButton("Fermer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        what.show();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinq);
        create_cinq();
        //Toast.makeText(this, "create", Toast.LENGTH_SHORT).show();

        this.main = findViewById(R.id.main);
        this.new_ = findViewById(R.id.new_);
        this.what = findViewById(R.id.what);



        if (savedInstanceState != null) {
            //gameState = savedInstanceState.getString("cinq");
            //main.setText(gameState);
        }
        else{
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

            what.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    what();
                }
            });
            for (int i = 0; i < 5; i++)
                for (int j = 0; j < 5; j++)
                    move(tab_button[i][j]);
        }



    }
    @Override
    public void onRestoreInstanceState ( Bundle savedInstanceState ) {
        super.onRestoreInstanceState(savedInstanceState);
        gameState = savedInstanceState.getString("cinq");
        //Toast.makeText(this, "restore", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSaveInstanceState ( Bundle outState ) {
        super.onSaveInstanceState(outState);
        gameState = "saveText";
        outState.putString("cinq", gameState);
        //Toast.makeText(this, outState.getString("cinq"), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "destroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Toast.makeText(this, "restart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
    }
}
