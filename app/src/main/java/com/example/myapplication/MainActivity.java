package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.alea=findViewById(R.id.alea);
        alea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=(int)(Math.random()*14);
                if (i == 0) {
                    Intent pow = new Intent(getApplicationContext(), Pow_2.class);
                    startActivity(pow);
                    finish();
                }
                if (i == 1) {
                    Intent cinq = new Intent(getApplicationContext(), Cinq.class);
                    startActivity(cinq);
                    finish();
                }
                if (i == 2) {
                    Intent dem = new Intent(getApplicationContext(), Demineur.class);
                    startActivity(dem);
                    finish();
                }
                if (i == 3) {
                    Intent ech = new Intent(getApplicationContext(), Echecs.class);
                    startActivity(ech);
                    finish();
                }
                if (i == 4) {
                    Intent hex = new Intent(getApplicationContext(), Hex.class);
                    startActivity(hex);
                    finish();
                }
                if (i == 5) {
                    Intent jdv = new Intent(getApplicationContext(), Jeu_de_la_vie.class);
                    startActivity(jdv);
                    finish();
                }
                if (i == 6) {
                    Intent jdt = new Intent(getApplicationContext(), Taquin.class);
                    startActivity(jdt);
                    finish();
                }
                if (i == 7) {
                    Intent p4 = new Intent(getApplicationContext(), Puissance_4.class);
                    startActivity(p4);
                    finish();
                }
                if (i == 8) {
                    Intent sdk = new Intent(getApplicationContext(), Sudoku.class);
                    startActivity(sdk);
                    finish();
                }
                if (i == 9) {
                    Intent lab = new Intent(getApplicationContext(), Labyrinthe.class);
                    startActivity(lab);
                    finish();
                }
                if (i == 10){
                    Intent snk= new Intent(getApplicationContext(),Snake.class);
                    startActivity(snk);
                    finish();
                }
                if (i == 11){
                    Intent chm= new Intent(getApplicationContext(),Chomp.class);
                    startActivity(chm);
                    finish();
                }
                if (i == 12){
                    Intent sol= new Intent(getApplicationContext(),Solitaire.class);
                    startActivity(sol);
                    finish();
                }
                if (i == 13){
                    Intent fre= new Intent(getApplicationContext(),Freecell.class);
                    startActivity(fre);
                    finish();
                }
                if (i == 14){
                    Intent dam= new Intent(getApplicationContext(),Dames.class);
                    startActivity(dam);
                    finish();

                }
            }
        });

        this.pow_2 = findViewById(R.id.pow_2);
        pow_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pow = new Intent(getApplicationContext(), Pow_2.class);
                startActivity(pow);
                finish();
            }
        });

        this.cinq = findViewById(R.id.cinq);
        cinq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cinq = new Intent(getApplicationContext(), Cinq.class);
                startActivity(cinq);
                finish();
            }
        });

        this.chomp = findViewById(R.id.chomp);
        chomp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chomp = new Intent(getApplicationContext(), Chomp.class);
                startActivity(chomp);
                finish();
            }
        });

        this.demineur = findViewById(R.id.demineur);
        demineur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dem = new Intent(getApplicationContext(),Demineur.class);
                startActivity(dem);
                finish();
            }
        });

        this.echecs = findViewById(R.id.echecs);
        echecs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ech = new Intent(getApplicationContext(),Echecs.class);
                startActivity(ech);
                finish();
            }
        });


        this.hex = findViewById(R.id.hex);
        hex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hex = new Intent(getApplicationContext(),Hex.class);
                startActivity(hex);
                finish();
            }
        });

        this.jeudelavie = findViewById(R.id.jeu_de_la_vie);
        jeudelavie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jdv = new Intent(getApplicationContext(),Jeu_de_la_vie.class);
                startActivity(jdv);
                finish();
            }
        });
        this.jeudutaquin = findViewById(R.id.jeu_du_taquin);
        jeudutaquin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jdt = new Intent(getApplicationContext(),Taquin.class);
                startActivity(jdt);
                finish();
            }
        });
        this.puissance4 = findViewById(R.id.puissance_4);
        puissance4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent p4 = new Intent(getApplicationContext(),Puissance_4.class);
                startActivity(p4);
                finish();
            }
        });
        this.sudoku = findViewById(R.id.sudoku);
        sudoku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sdk = new Intent(getApplicationContext(),Sudoku.class);
                startActivity(sdk);
                finish();
            }
        });
        this.labyrinthe = findViewById(R.id.labyrinthe);
        labyrinthe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lab = new Intent(getApplicationContext(),Labyrinthe.class);
                startActivity(lab);
                finish();
            }
        });
        this.snake = findViewById(R.id.snake);
        snake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent snk= new Intent(getApplicationContext(),Snake.class);
                startActivity(snk);
                finish();
            }
        });
        this.solitaire = findViewById(R.id.solitaire);
        solitaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sol= new Intent(getApplicationContext(),Solitaire.class);
                startActivity(sol);
                finish();
            }
        });
        this.freecell = findViewById(R.id.freecell);
        freecell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fre= new Intent(getApplicationContext(),Freecell.class);
                startActivity(fre);
                finish();
            }
        });
        this.dames = findViewById(R.id.dames);
        dames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dam= new Intent(getApplicationContext(),Dames.class);
                startActivity(dam);
                finish();
            }
        });
    }
}
