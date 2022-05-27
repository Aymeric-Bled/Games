package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Mastermind extends AppCompatActivity {
    private Button main;
    private Button new_;
    private Table tab;
    private int width = 6;
    private int height = 11;
    private int colors[] = {R.id.color1, R.id.color2, R.id.color3, R.id.color4, R.id.color5, R.id.color6, R.id.color7, R.id.color8};
    private Button del = null;
    private Button ok = null;
    private int range;
    private int position = -1;
    private int test_code[] = {-1, -1, -1, -1};
    private int code[] = {-1, -1, -1, -1};
    private int w = 0;

    public void create_mastermind(){
        for (int i = 0; i < 8; i++){
            int finalI = i;
            ((Button) findViewById(colors[i])).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    color(finalI);
                }
            });
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        tab = new Table((GridLayout) findViewById(R.id.grille), height, width, this, params);
        new_();
    }

    public void new_(){
        unselect();
        for (int i=0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Button b = tab.getButton(i, j);
                if (j == 0 || j == 5) {
                    b.setBackgroundResource(R.color.trans);
                    b.setText("");
                }
                else{
                    if (i == 0){
                        b.setBackgroundColor(getColor(R.color.grey));
                    }
                    else {
                        b.setBackgroundResource(R.drawable.whitebutton);
                        int finalJ = j;
                        int finalI = i;
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                select(finalI, finalJ);
                            }
                        });
                    }
                }
            }
        }
        range = height - 1;
        putRange();
        for (int i = 0; i < 4; i++){
            code[i] = (int) (Math.random() * 8);
        }
    }

    public void end(boolean end){
        AlertDialog.Builder fin = new AlertDialog.Builder(this);
        if (end) {
            fin.setTitle("Gagné !!!");
        }
        else{
            fin.setTitle("Perdu !!!");
        }
        fin.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();
            }
        });
        fin.setPositiveButton("Rejouer", new DialogInterface.OnClickListener() {
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

    public void select(int i, int j){
        if (i != range){
            return;
        }
        else{
            unselect();
            position = j;
            Button b = tab.getButton(i, j);
            b.setAlpha((float) 0.8);
        }
    }

    public void unselect(){
        if (position == -1){
            return;
        }
        Button b = tab.getButton(range, position);
        b.setAlpha((float) 1);
        position = -1;
    }

    public void color(int i){
        if (position == -1){
            return;
        }
        test_code[position - 1] = i;
       tab.getButton(range, position).setBackground(findViewById(colors[i]).getBackground());
    }

    public void ok(){
        for (int i = 0; i < 4; i++){
            if (test_code[i] == -1){
                return;
            }
        }
        Pair<Integer, Integer> result = test();
        tab.getButton(range, 0).setTextColor(Color.RED);
        tab.getButton(range, 0).setText("" + result.first);
        tab.getButton(range, 0).setBackgroundResource(R.color.trans);
        tab.getButton(range, 5).setTextColor(Color.YELLOW);
        tab.getButton(range, 5).setText("" + result.second);
        if (result.first == 4){
            end(true);
            return;
        }
        unselect();
        if (range > 1) {
            range--;
            putRange();
        }
        else{
            end(false);
        }
    }

    public void putRange(){
        this.del = tab.getButton(range, 0);
        this.ok = tab.getButton(range, 5);
        this.del.setBackgroundResource(R.drawable.gomme);
        this.ok.setTextColor(Color.WHITE);
        this.ok.setText("ok");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ok();
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                del();
            }
        });
        del();
    }

    public void del(){
        for (int i = 0; i < 4; i++){
            test_code[i] = -1;
            tab.getButton(range, i + 1).setBackgroundResource(R.drawable.whitebutton);

        }
    }

    public Pair<Integer,Integer> test(){
        int red = 0;
        int yellow = 0;
        boolean alreadySeen[] = {false, false, false, false};
        boolean alreadySeenTest[] = {false, false, false, false};
        for (int i = 0; i < 4; i++){
            if (test_code[i] == code[i]){
                red++;
                alreadySeen[i] = true;
                alreadySeenTest[i] = true;
            }
        }
        for (int i = 0; i < 4; i++){
            if (!alreadySeen[i]){
                for (int j = 0; j < 4; j++){
                    if (!alreadySeenTest[j]){
                        if (test_code[j] == code[i]){
                            yellow++;
                            alreadySeen[i] = true;
                            alreadySeenTest[j] = true;
                            break;
                        }
                    }
                }
            }
        }
        return new Pair(red,yellow);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mastermind);
        this.main = findViewById(R.id.main);
        this.new_ = findViewById(R.id.new_);
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
            public void onClick(View view) {
                new_();
            }
        });
        create_mastermind();
        putRange();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int wi = (metrics.widthPixels - 20 * 8 - 10) / 8;
        int w1 = (metrics.widthPixels - 20 * (width) - 10) / width;
        int w2 = (findViewById(android.R.id.content).getHeight() - main.getHeight() - 20 * (height + 1) - 10) / (height + 1);
        w = Math.min(w1, w2);
        if (w > wi){
            w = wi;
        }
        for (int i = 0; i < 8; i++){
            ((Button) findViewById(colors[i])).setWidth(w);
            ((Button) findViewById(colors[i])).setHeight(w);
        }
        tab.setButtonsDimension(w,w);
    }
}