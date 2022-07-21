package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;

public class Cinq extends AppCompatActivity {
    private ImageView main;
    private ImageView new_;
    private Button what;
    private GridLayout grille;
    private int tab_button[][] = new int[5][5];
    private int tab_color[][] = new int[5][5];


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
                b.setMinimumHeight(0);
                b.setMinimumWidth(0);
                if (tab_color[i][j] == 0)
                    b.setBackgroundColor(Color.WHITE);
                else
                    b.setBackgroundColor(Color.BLACK);
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
        CouchbaseLite.init(this);
        Database database;
        Document document = null;
        boolean hasSave = false;
        try {
            database = new Database("games");
            document = database.getDocument("5x5");
            if (document != null){
                document.getArray("tab_color");
                hasSave = true;
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        if (hasSave) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    tab_color[i][j] = document.getArray("tab_color").getArray(i).getInt(j);
                }
            }
        }
        else{
            for (int i=0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    tab_color[i][j] = 0;
                }
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // using toolbar as ActionBar
        setSupportActionBar(toolbar);
        create_cinq();
        //Toast.makeText(this, "create", Toast.LENGTH_SHORT).show();

        this.main = findViewById(R.id.main);
        this.new_ = findViewById(R.id.new_);
        this.what = findViewById(R.id.what);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Database database;
        try {
            database = new Database("games");
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return;
        }
        MutableDocument document = new MutableDocument("5x5");
        MutableArray array = new MutableArray();
        for (int i = 0 ; i < 5; i++){
            MutableArray arr = new MutableArray();
            for (int j = 0; j < 5; j++){
                arr.addInt(tab_color[i][j]);
            }
            array.addArray(arr);
        }
        document.setArray("tab_color", array);
        try {
            database.save(document);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        int height = findViewById(android.R.id.content).getHeight();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int w = (Math.min(height, width) - 6) / 5;
        for (int i =0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Button b = findViewById(tab_button[i][j]);
                b.setWidth(w);
                b.setHeight(w);
            }
        }

    }
}
