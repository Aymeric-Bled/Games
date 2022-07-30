package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button alea;
    private Button pow_2;
    private Button cinq;
    private Button chomp;
    private Button demineur;
    private Button echecs;
    private Button hex;
    private Button jeudelavie;
    private Button jeudutaquin;
    private Button puissance4;
    private Button sudoku;
    private Button labyrinthe;
    private Button snake;
    private Button solitaire;
    private Button freecell;
    private Button dames;
    private Button mastermind;

    private ArrayList<Button> buttons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.pow_2 = findViewById(R.id.pow_2);
        pow_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pow = new Intent(getApplicationContext(), Pow_2.class);
                startActivity(pow);
                finish();
            }
        });
        buttons.add(pow_2);

        this.cinq = findViewById(R.id.cinq);
        cinq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cinq = new Intent(getApplicationContext(), Cinq.class);
                startActivity(cinq);
                finish();
            }
        });
        buttons.add(cinq);

        this.chomp = findViewById(R.id.chomp);
        chomp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chomp = new Intent(getApplicationContext(), Chomp.class);
                startActivity(chomp);
                finish();
            }
        });
        buttons.add(chomp);

        this.demineur = findViewById(R.id.demineur);
        demineur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dem = new Intent(getApplicationContext(),Demineur.class);
                startActivity(dem);
                finish();
            }
        });
        buttons.add(demineur);

        this.echecs = findViewById(R.id.echecs);
        echecs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ech = new Intent(getApplicationContext(),Echecs.class);
                startActivity(ech);
                finish();
            }
        });
        buttons.add(echecs);


        this.hex = findViewById(R.id.hex);
        hex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hex = new Intent(getApplicationContext(),Hex.class);
                startActivity(hex);
                finish();
            }
        });
        buttons.add(hex);

        this.jeudelavie = findViewById(R.id.jeu_de_la_vie);
        jeudelavie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jdv = new Intent(getApplicationContext(),Jeu_de_la_vie.class);
                startActivity(jdv);
                finish();
            }
        });
        buttons.add(jeudelavie);

        this.jeudutaquin = findViewById(R.id.jeu_du_taquin);
        jeudutaquin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jdt = new Intent(getApplicationContext(),Taquin.class);
                startActivity(jdt);
                finish();
            }
        });
        buttons.add(jeudutaquin);

        this.puissance4 = findViewById(R.id.puissance_4);
        puissance4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent p4 = new Intent(getApplicationContext(),Puissance_4.class);
                startActivity(p4);
                finish();
            }
        });
        buttons.add(puissance4);

        this.sudoku = findViewById(R.id.sudoku);
        sudoku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sdk = new Intent(getApplicationContext(),Sudoku.class);
                startActivity(sdk);
                finish();
            }
        });
        buttons.add(sudoku);

        this.labyrinthe = findViewById(R.id.labyrinthe);
        labyrinthe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lab = new Intent(getApplicationContext(),Labyrinthe.class);
                startActivity(lab);
                finish();
            }
        });
        buttons.add(labyrinthe);

        this.snake = findViewById(R.id.snake);
        snake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent snk= new Intent(getApplicationContext(),Snake.class);
                startActivity(snk);
                finish();
            }
        });
        buttons.add(snake);

        this.solitaire = findViewById(R.id.solitaire);
        solitaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sol= new Intent(getApplicationContext(),Solitaire.class);
                startActivity(sol);
                finish();
            }
        });
        buttons.add(solitaire);

        this.freecell = findViewById(R.id.freecell);
        freecell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fre= new Intent(getApplicationContext(),Freecell.class);
                startActivity(fre);
                finish();
            }
        });
        buttons.add(freecell);

        this.dames = findViewById(R.id.dames);
        dames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dam= new Intent(getApplicationContext(),Dames.class);
                startActivity(dam);
                finish();
            }
        });
        buttons.add(dames);

        this.mastermind = findViewById(R.id.mastermind);
        mastermind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mas= new Intent(getApplicationContext(),Mastermind.class);
                startActivity(mas);
                finish();
            }
        });
        buttons.add(mastermind);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels / 3;
        int height = metrics.heightPixels / 3;

        for (Button b: buttons){
            ViewGroup.LayoutParams params = b.getLayoutParams();
            params.height = height;
            params.width = width;
            b.setMinimumWidth(0);
            b.setMinimumHeight(0);
        }
    }
}
