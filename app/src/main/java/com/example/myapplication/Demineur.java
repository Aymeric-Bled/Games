package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;

import java.util.ArrayList;

public class Demineur extends AppCompatActivity {
    private ImageView main;
    private ImageView new_;
    private Button déminer;
    private Button flag;
    private int taille = 20;
    private int mines = 60;
    private boolean fini = false;
    private Table tab;
    private boolean tab_flag[][] = new boolean[taille][taille];
    private boolean tab_mines[][]=new boolean[taille][taille];
    private int tab_values[][]=new int[taille][taille];
    private boolean tab_seen[][]=new boolean[taille][taille];
    private Button currentButton = null;
    private int x;
    private CharSequence ch[]={"0","1","2","3","4","5","6","7","8","9"};
    private int height;

    void create_demineur(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        x= 120;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(x-1, x-1);
        params.setMargins(1,1,1,1);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
        ConstraintLayout.LayoutParams p1 = (ConstraintLayout.LayoutParams) scrollView.getLayoutParams();
        ConstraintLayout.LayoutParams p = new ConstraintLayout.LayoutParams(metrics.widthPixels, height);
        p.topToBottom = p1.topToBottom;
        p.bottomToTop = p1.bottomToTop;
        scrollView.setLayoutParams(p);
        tab = new Table((GridLayout) findViewById(R.id.grille), taille, taille, this, params, x-1);
        Button b;
        for (int i=0; i<taille;i++) {
            for (int j = 0; j < taille; j++) {
                b = tab.getButton(i,j);
                b.setText(ch[tab_values[i][j]]);
                if (tab_mines[i][j] && fini){
                    b.setBackgroundResource(R.drawable.mine);
                    b.setTextColor(Color.TRANSPARENT);
                }
                else if (tab_flag[i][j]){
                    b.setBackgroundResource(R.drawable.flag);
                    b.setTextColor(Color.TRANSPARENT);
                }
                else if (tab_seen[i][j]){
                    b.setBackgroundColor(Color.WHITE);
                    b.setTextColor(Color.BLACK);
                }
                else{
                    b.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                    b.setTextColor(Color.TRANSPARENT);

                }
            }
        }
        déminer.setWidth(metrics.widthPixels/2);
        flag.setWidth(metrics.widthPixels/2);
    }
    int place(Button b){
        for (int i=0; i<taille;i++){
            for (int j = 0; j < taille; j++) {
                if (tab.getButton(i,j) == b)
                    return i*taille+ j;
            }
        }
        return -1;
    }
    void move(final Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = place(currentButton);
                if (position!=-1 && !tab_seen[position/taille][position%taille]){
                    if (!tab_flag[position/taille][position % taille])
                        currentButton.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                    else
                        currentButton.setBackgroundResource(R.drawable.flag);
                }
                currentButton = button;
                position = place(button);
                if (!tab_flag[position/taille][position%taille] && !tab_seen[position/taille][position%taille])
                    button.setBackgroundColor(getColor(R.color.colorPrimary));
                if (tab_flag[position/taille][position%taille] && !tab_seen[position/taille][position%taille])
                    button.setBackgroundResource(R.drawable.flag_light);
            }
        });
    }

    void flag(){
        if (fini){
            return;
        }
        int position = place(currentButton);
        if (position != -1 && !tab_seen[position/taille][position%taille]) {
            if (tab_flag[position / taille][position % taille]) {
                currentButton.setBackgroundColor(getColor(R.color.colorPrimary));
                tab_flag[position / taille][position % taille] = false;
            } else {
                currentButton.setBackgroundResource(R.drawable.flag_light);
                tab_flag[position / taille][position % taille] = true;
            }
        }
    }
    int[]  alea(int length){
        int alea[] = new int[length];
        int n;
        int j;
        boolean already_seen[] = new boolean[length];
        for (int i = 0; i<length; i++) {
            already_seen[i] = false;
        }
        for (int i = 0; i < length; i++){
            n = (int) (Math.random()* (length - i));
            for (j = 0; n > 0; j++){
                if (!already_seen[j])
                    n--;
            }
            while (already_seen[j])
                j++;
            alea[i] = j;
            already_seen[j] = true;
        }
        return alea;
    }

    void initialise_mines(){
        int alea[]=alea(taille*taille);
        for (int i =0; i< taille*taille; i++) {
            tab_values[i / taille][i % taille] = 0;
            tab_seen[i / taille][i % taille] = false;
            tab_flag[i / taille][i % taille] = false;
        }

        for (int i=0; i< taille*taille; i++){
            if (i<mines) {
                tab_mines[alea[i] / taille][alea[i] % taille] = true;
                if (alea[i] / taille > 0) {
                    tab_values[alea[i] / taille - 1][alea[i] % taille]++;
                    if (alea[i] % taille > 0) {
                        tab_values[alea[i] / taille - 1][alea[i] % taille - 1]++;
                    }
                    if (alea[i] % taille < taille - 1) {
                        tab_values[alea[i] / taille - 1][alea[i] % taille + 1]++;
                    }
                }
                if (alea[i] / taille < taille - 1) {
                    tab_values[alea[i] / taille + 1][alea[i] % taille]++;
                    if (alea[i] % taille > 0) {
                        tab_values[alea[i] / taille + 1][alea[i] % taille - 1]++;
                    }
                    if (alea[i] % taille < taille - 1) {
                        tab_values[alea[i] / taille + 1][alea[i] % taille + 1]++;
                    }
                }
                if (alea[i] % taille > 0) {
                    tab_values[alea[i] / taille][alea[i] % taille - 1]++;
                }
                if (alea[i] % taille < taille - 1) {
                    tab_values[alea[i] / taille][alea[i] % taille + 1]++;
                }
            }
            else{
                tab_mines[alea[i]/taille][alea[i]%taille] = false;
            }
        }
    }

    void new_(){
        fini = false;
        Button b;
        initialise_mines();
        for (int i=0; i< taille; i++){
            for (int j =0; j < taille; j++){
                b=tab.getButton(i,j);
                b.setText(ch[tab_values[i][j]]);
                b.setTextColor(Color.TRANSPARENT);
                b.setBackgroundColor(getColor(R.color.colorPrimaryDark));
            }
        }
        currentButton = null;
    }

    void end(boolean b){
        fini = true;
        Button animation = findViewById(R.id.anim);
        animation.setBackgroundColor(Color.RED);
        AnimatorSet s = new AnimatorSet();
        boolean already_seen[]=new boolean[taille*taille];
        for (int i=0; i< taille*taille; i++)
            already_seen[i] = false;
        ArrayList<Integer> q1 = new ArrayList<>();
        ArrayList<Integer> q2;
        int position = place(currentButton);
        int p = position;
        q1.add(p);
        already_seen[p]=true;
        int n;
        int ind=-1;
        final ArrayList<Animator> anim = new ArrayList<>();
        Button button;
        ObjectAnimator objectAnimator;
        while(!q1.isEmpty()){
            q2 = new ArrayList<>();
            while(!q1.isEmpty()) {
                n = q1.remove(0);
                already_seen[n] = true;
                if (tab_mines[n / taille][n % taille]) {
                    button = tab.getButton(n / taille, n % taille);
                    if (n == position)
                        objectAnimator = ObjectAnimator.ofObject(button, "backgroundResource", new ArgbEvaluator(), R.drawable.mine_light, R.drawable.mine_light);
                    else
                        objectAnimator = ObjectAnimator.ofObject(button, "backgroundResource", new ArgbEvaluator(), R.drawable.mine, R.drawable.mine);
                    objectAnimator.setDuration(100);
                    if (!anim.isEmpty()) {
                        s.play(objectAnimator).after(anim.get(ind));
                    } else {
                        s.play(objectAnimator);
                    }
                    anim.add(objectAnimator);
                }
                if (n / taille > 0 && !already_seen[n - taille]) {
                    q2.add(n - taille);
                    already_seen[n - taille] = true;
                }
                if (n / taille > 0 && n % taille > 0 && !already_seen[n - taille - 1]) {
                    q2.add(n - taille - 1);
                    already_seen[n - taille - 1] = true;
                }
                if (n / taille > 0 && n % taille < taille - 1 && !already_seen[n - taille + 1]) {
                    q2.add(n - taille + 1);
                    already_seen[n - taille + 1] = true;
                }
                if (n % taille > 0 && !already_seen[n - 1]) {
                    q2.add(n - 1);
                    already_seen[n - 1] = true;
                }
                if (n / taille < taille - 1 && !already_seen[n + taille]) {
                    q2.add(n + taille);
                    already_seen[n + taille] = true;
                }
                if (n % taille < taille - 1 && !already_seen[n + 1]) {
                    q2.add(n + 1);
                    already_seen[n + 1] = true;
                }
                if (n / taille < taille - 1 && n % taille > 0 && !already_seen[n + taille - 1]) {
                    q2.add(n + taille - 1);
                    already_seen[n + taille - 1] = true;
                }
                if (n / taille < taille - 1 && n % taille < taille - 1 && !already_seen[n + taille + 1]) {
                    q2.add(n + taille + 1);
                    already_seen[n + taille + 1] = true;
                }

            }
            ind = anim.size() - 1 ;
            q1 = q2;
        }
        objectAnimator = ObjectAnimator.ofObject(animation, "backgroundColor", new ArgbEvaluator(), Color.RED, Color.WHITE);
        if (ind >= 0) {
            s.play(objectAnimator).after(anim.get(ind));
        } else {
            s.play(objectAnimator);
        }
        anim.add(objectAnimator);
        final AlertDialog.Builder fin = new AlertDialog.Builder(this);
        if (b) {
            fin.setTitle("Gagné!!!");
        } else {

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

        anim.get(anim.size() - 1).addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(anim.get(anim.size() - 1));
                fin.show();
            }
        });
        s.start();

    }

    void zero(int p , AnimatorSet s){
        boolean already_seen[]=new boolean[taille*taille];
        for (int i=0; i< taille*taille; i++)
            already_seen[i] = false;
        ArrayList<Integer> q1 = new ArrayList<>();
        ArrayList<Integer> q2;
        q1.add(p);
        already_seen[p]=true;
        int n;
        int ind=-1;
        int k = 0;
        Animator anim[] = new Animator[2*taille*taille];
        Button b;
        ObjectAnimator objectAnimator;
        ObjectAnimator objectAnimator1;
        while(!q1.isEmpty()){
            q2 = new ArrayList<>();
            while(!q1.isEmpty()) {
                n = q1.remove(0);
                tab_seen[n/taille][n%taille] = true;
                b = tab.getButton(n/taille,n%taille);
                objectAnimator = ObjectAnimator.ofObject(b, "backgroundColor", new ArgbEvaluator(), ((ColorDrawable)b.getBackground()).getColor(),Color.WHITE );
                objectAnimator.setDuration(100);
                objectAnimator1 = ObjectAnimator.ofObject(b, "textColor", new ArgbEvaluator(), getColor(R.color.trans),Color.BLACK);
                objectAnimator1.setDuration(100);
                if (ind>=0) {
                    s.play(objectAnimator).after(anim[ind]);
                    s.play(objectAnimator1).after(anim[ind]);
                }
                else{
                    s.play(objectAnimator);
                    s.play(objectAnimator1);
                }
                anim[k++] = objectAnimator;
                anim[k++] = objectAnimator1;
                if (tab_values[n/taille][n%taille] == 0 ){
                    if (n/taille >0 && !already_seen[n-taille] && !tab_seen[n/taille - 1][n%taille]){
                        q2.add(n-taille);
                        already_seen[n-taille] = true;
                    }
                    if (n/taille >0 && n%taille >0 && !already_seen[n-taille - 1] && !tab_seen[n/taille - 1][n%taille - 1]){
                        q2.add(n-taille - 1);
                        already_seen[n-taille - 1] = true;
                    }
                    if (n/taille >0 && n%taille <taille - 1  && !already_seen[n-taille + 1] && !tab_seen[n/taille - 1][n%taille + 1]){
                        q2.add(n-taille + 1);
                        already_seen[n-taille + 1] = true;
                    }
                    if (n%taille > 0 && !already_seen[n-1] && !tab_seen[n/taille][n%taille - 1]){
                        q2.add(n-1);
                        already_seen[n-1] = true;
                    }
                    if (n/taille < taille - 1 && !already_seen[n+taille] && !tab_seen[n/taille + 1][n%taille]){
                        q2.add(n+taille);
                        already_seen[n+taille] = true;
                    }
                    if (n%taille < taille - 1 && !already_seen[n+1] && !tab_seen[n/taille][n%taille + 1]){
                        q2.add(n+1);
                        already_seen[n+1] = true;
                    }
                    if (n/taille < taille - 1 && n%taille >0 && !already_seen[n+taille - 1] && !tab_seen[n/taille + 1][n%taille - 1]){
                        q2.add(n+taille - 1);
                        already_seen[n+taille - 1] = true;
                    }
                    if (n/taille < taille - 1 && n%taille <taille - 1 && !already_seen[n+taille + 1] && !tab_seen[n/taille + 1][n%taille + 1]) {
                        q2.add( n + taille + 1);
                        already_seen[n + taille + 1] = true;
                    }
                }


            }
            ind = k - 1 ;
            q1 = q2;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demineur);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.déminer=findViewById(R.id.déminer);
        this.main = findViewById(R.id.main);
        this.new_= findViewById(R.id.new_);
        this.flag=findViewById(R.id.flag);

        déminer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fini){
                    return;
                }
                int position = place(currentButton);
                int p = position;

                if (p != -1 && !tab_flag[p/taille][p%taille] && !tab_seen[p/taille][p%taille]){
                    if (tab_mines[p/taille][p%taille]){
                        end(false);
                    }
                    else{
                        if (tab_values[p/taille][p%taille]==0){
                            AnimatorSet s = new AnimatorSet();
                            zero(p,s);
                            s.start();

                        }
                        else {
                            currentButton.setBackgroundColor(Color.WHITE);
                            currentButton.setTextColor(Color.BLACK);
                            tab_seen[p / taille][p % taille] = true;
                        }
                    }
                    boolean fin =true;
                    for (int i=0; i<taille*taille; i++){
                        if (tab_seen[i/taille][i%taille] == tab_mines[i/taille][i%taille])
                            fin = false;
                    }
                    if (fin){
                        end(true);
                    }
                }
            }
        });
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(main);
                finish();
            }
        });
        flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag();
            }
        });
        new_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_();
            }
        });
        CouchbaseLite.init(this);
        Database database;
        Document document = null;
        boolean hasSave = false;
        try {
            database = new Database("games");
            document = database.getDocument("démineur");
            if (document != null) {
                document.getInt("taille");
                document.getInt("mines");
                document.getBoolean("fini");
                document.getArray("tab_flag");
                document.getArray("tab_mines");
                document.getArray("tab_values");
                document.getArray("tab_seen");
                hasSave = true;
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        if (hasSave) {
            taille = document.getInt("taille");
            mines = document.getInt("mines");
            fini = document.getBoolean("fini");
            for (int i = 0; i < taille; i++) {
                for (int j = 0; j < taille; j++) {
                    tab_flag[i][j] = document.getArray("tab_flag").getArray(i).getBoolean(j);
                    tab_mines[i][j] = document.getArray("tab_mines").getArray(i).getBoolean(j);
                    tab_values[i][j] = document.getArray("tab_values").getArray(i).getInt(j);
                    tab_seen[i][j] = document.getArray("tab_seen").getArray(i).getBoolean(j);
                }
            }
        } else {
            initialise_mines();
        }
        create_demineur();
        for (int i = 0; i < taille; i++)
            for (int j = 0; j < taille; j++)
                move(tab.getButton(i, j));
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
        MutableDocument document = new MutableDocument("démineur");

        document.setInt("taille", taille);
        document.setInt("mines", mines);
        document.setBoolean("fini", fini);
        MutableArray array_flag = new MutableArray();
        MutableArray array_mines = new MutableArray();
        MutableArray array_values = new MutableArray();
        MutableArray array_seen = new MutableArray();
        for (int i = 0 ; i < taille; i++){
            MutableArray arr_flag = new MutableArray();
            MutableArray arr_mines = new MutableArray();
            MutableArray arr_values = new MutableArray();
            MutableArray arr_seen = new MutableArray();
            for (int j = 0; j < taille; j++){
                arr_flag.addBoolean(tab_flag[i][j]);
                arr_mines.addBoolean(tab_mines[i][j]);
                arr_values.addInt(tab_values[i][j]);
                arr_seen.addBoolean(tab_seen[i][j]);
            }
            array_flag.addArray(arr_flag);
            array_mines.addArray(arr_mines);
            array_values.addArray(arr_values);
            array_seen.addArray(arr_seen);
        }
        document.setArray("tab_flag", array_flag);
        document.setArray("tab_mines", array_mines);
        document.setArray("tab_values", array_values);
        document.setArray("tab_seen", array_seen);
        try {
            database.save(document);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        height = findViewById(android.R.id.content).getHeight() - main.getHeight() - flag.getHeight();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
        ConstraintLayout.LayoutParams p1 = (ConstraintLayout.LayoutParams) scrollView.getLayoutParams();
        ConstraintLayout.LayoutParams p = new ConstraintLayout.LayoutParams(metrics.widthPixels, height);
        p.topToBottom = p1.topToBottom;
        p.bottomToTop = p1.bottomToTop;
        scrollView.setLayoutParams(p);
    }
}
