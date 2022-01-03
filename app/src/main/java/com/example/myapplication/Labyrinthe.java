package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Labyrinthe extends AppCompatActivity {
    private Button main;
    private TextView selection;
    private Spinner t;
    private Button g;
    private Button profondeur;
    private Button largeur;
    private Button a_etoile;
    private Spinner grilleSelection;
    private int value[][];
    private int copy[][];
    private int taille = 21;
    private Object Taille[] = {21, 31, 41, 51, 61, 71, 81, 91, 101};

    private enum Grille {Labyrinthe, Labyrinthe_boucle, Aléatoire, Aléatoire_sortie}

    private Grille currentGrille = Grille.Labyrinthe;
    private Table tab;
    AnimatorSet s = null;

    void create_layout() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w = metrics.widthPixels / taille;
        tab = new Table((GridLayout) findViewById(R.id.grille), taille, taille, this, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT), w);
        value = new int[taille][taille];
        copy = new int[taille][taille];
        Button b;
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                b = tab.getButton(i, j);
                b.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    int[] cases_accessibles(int ind) {
        int CA[] = new int[5];
        CA[0] = -1;
        CA[1] = -1;
        CA[2] = -1;
        CA[3] = -1;
        CA[4] = -1;
        int i = 0;
        if (ind / taille >= 2 && value[ind / taille - 2][ind % taille] == 1) {
            CA[++i] = ind - 2 * taille;
        }
        if (ind % taille >= 2 && value[ind / taille][ind % taille - 2] == 1) {
            CA[++i] = ind - 2;
        }
        if (ind / taille < taille - 2 && value[ind / taille + 2][ind % taille] == 1) {
            CA[++i] = ind + 2 * taille;
        }
        if (ind % taille < taille - 2 && value[ind / taille][ind % taille + 2] == 1) {
            CA[++i] = ind + 2;
        }
        CA[0] = i;
        return CA;
    }

    void update() {
        Button b;
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                b = tab.getButton(i, j);
                if (value[i][j] == 1)
                    b.setBackgroundColor(Color.BLACK);
                else
                    b.setBackgroundColor(Color.WHITE);

            }
        }
    }


    void labyrinthe() {
        if (s != null) {
            s.pause();
        }
        int pile[] = new int[taille * taille];
        boolean fin;
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++)
                value[i][j] = 1;
        }
        int t = (taille - 1) / 2;
        int ind = (int) (Math.random() * (t) * (t));
        int ligne = 2 * (ind / (t)) + 1;
        int colonne = 2 * (ind % (t)) + 1;
        ind = ligne * taille + colonne;
        int CA[];
        int i = 0;
        value[ind / taille][ind % taille] = 0;
        pile[i++] = ind;
        int k;
        while (i != 0) {
            fin = false;
            while (!fin) {
                ind = pile[i - 1];
                CA = cases_accessibles(ind);
                if (CA[0] == 0) {
                    fin = true;
                } else {
                    k = CA[1 + (int) (Math.random() * CA[0])];
                    pile[i++] = k;
                    ind = (k + ind) / 2;
                    value[ind / taille][ind % taille] = 0;
                    ind = k;
                    value[ind / taille][ind % taille] = 0;
                }
            }
            i--;
        }
        int n = (int) (Math.random() * 4 * t);
        if (n < t) {
            value[0][2 * n % t + 1] = 0;
        } else {
            if (n < 2 * t) {
                value[taille - 1][2 * n % t + 1] = 0;
            } else {
                if (n < 3 * t) {
                    value[2 * n % t + 1][0] = 0;
                } else {
                    value[2 * n % t + 1][taille - 1] = 0;
                }
            }
        }
        int m = (int) (Math.random() * 3 * t);
        if (n / t <= m / t)
            m += t;
        n = m;
        if (n < t) {
            value[0][2 * n % t + 1] = 0;
        } else {
            if (n < 2 * t) {
                value[taille - 1][2 * n % t + 1] = 0;
            } else {
                if (n < 3 * t) {
                    value[2 * n % t + 1][0] = 0;
                } else {
                    value[2 * n % t + 1][taille - 1] = 0;
                }
            }
        }
        update();
    }

    void grille_aleatoire(double p) {
        if (s != null) {
            s.pause();
        }
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if (i == 0 || j == 0 || i == taille - 1 || j == taille - 1) {
                    value[i][j] = 1;
                } else {
                    if (Math.random() < p) {
                        value[i][j] = 1;
                    } else {
                        value[i][j] = 0;
                    }
                }
            }
        }
        int n = (int) (Math.random() * 4 * (taille - 2));
        if (n < taille - 2) {
            value[0][1 + n % (taille - 2)] = 0;
        } else {
            if (n < 2 * (taille - 2)) {
                value[taille - 1][1 + n % (taille - 2)] = 0;
            } else {
                if (n < 3 * (taille - 2)) {
                    value[n % (taille - 2) + 1][0] = 0;
                } else {
                    value[n % (taille - 2) + 1][taille - 1] = 0;
                }
            }
        }
        int m = (int) (Math.random() * 3 * (taille - 2));
        if (n / (taille - 2) <= m / (taille - 2))
            m += taille - 2;
        n = m;
        if (n < taille - 2) {
            value[0][1 + n % (taille - 2)] = 0;
        } else {
            if (n < 2 * (taille - 2)) {
                value[taille - 1][1 + n % (taille - 2)] = 0;
            } else {
                if (n < 3 * (taille - 2)) {
                    value[n % (taille - 2) + 1][0] = 0;
                } else {
                    value[n % (taille - 2) + 1][taille - 1] = 0;
                }
            }
        }
        update();
    }

    int[] autour(int ind, int a) {
        int l[] = new int[5];
        int i = 0;
        if (ind / taille != taille - 1 && ((a == 2 && value[ind / taille + 1][ind % taille] == a) || (a == 0 && value[ind / taille + 1][ind % taille] == a && autour(ind + taille, 2)[0] == 0)))
            l[++i] = ind + taille;
        if (ind / taille != 0 && ((a == 2 && value[ind / taille - 1][ind % taille] == a) || (a == 0 && value[ind / taille - 1][ind % taille] == a && autour(ind - taille, 2)[0] == 0)))
            l[++i] = ind - taille;
        if (ind % taille != taille - 1 && ((a == 2 && value[ind / taille][ind % taille + 1] == a) || (a == 0 && value[ind / taille][ind % taille + 1] == a && autour(ind + 1, 2)[0] == 0)))
            l[++i] = ind + 1;
        if (ind % taille != 0 && ((a == 2 && value[ind / taille][ind % taille - 1] == a) || (a == 0 && value[ind / taille][ind % taille - 1] == a && autour(ind - 1, 2)[0] == 0)))
            l[++i] = ind - 1;
        l[0] = i;
        return l;
    }


    void profondeur() {
        if (s != null) {
            s.pause();
        }
        del_trace();
        int pile[] = new int[taille * taille];
        int ind = 0;
        for (int i = 1; i < taille - 1; i++) {
            if (value[i][0] == 0)
                pile[ind++] = i * taille;
            if (value[i][taille - 1] == 0)
                pile[ind++] = i * taille + taille - 1;
        }
        for (int j = 1; j < taille - 1; j++) {
            if (value[0][j] == 0)
                pile[ind++] = j;
            if (value[taille - 1][j] == 0)
                pile[ind++] = (taille - 1) * taille + j;
        }
        int n = (int) (Math.random() * 2);
        ind = pile[n];
        int fin = pile[1 - n];
        int a[];
        int b[];
        Button button;
        s = new AnimatorSet();
        Animator anim[] = new Animator[4 * taille * taille];
        int k = 0;
        while (ind != fin) {
            a = autour(ind, 0);
            if (a[0] == 0) {
                while (a[0] == 0) {
                    b = autour(ind, 2);
                    value[ind / taille][ind % taille] = 4;
                    button = tab.getButton(ind / taille, ind % taille);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), Color.BLUE, Color.BLUE);
                    objectAnimator.setDuration(40);
                    if (k != 0) {
                        s.play(objectAnimator).after(anim[k - 1]);
                    } else {
                        s.play(objectAnimator);
                    }
                    if (b[0] == 0) {
                        s.start();
                        return;
                    }
                    ind = b[1 + (int) (Math.random() * b[0])];
                    value[ind / taille][ind % taille] = 3;
                    button = tab.getButton(ind / taille, ind % taille);
                    ObjectAnimator objectAnimator2 = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), Color.GREEN, Color.GREEN);
                    objectAnimator2.setDuration(40);
                    if (k != 0) {
                        s.play(objectAnimator2).after(anim[k - 1]);
                    } else {
                        s.play(objectAnimator2);
                    }
                    a = autour(ind, 0);
                    anim[k++] = objectAnimator;
                    anim[k++] = objectAnimator2;

                }
            } else {
                value[ind / taille][ind % taille] = 2;
                button = tab.getButton(ind / taille, ind % taille);
                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), Color.RED, Color.RED);
                objectAnimator.setDuration(40);
                if (k != 0) {
                    s.play(objectAnimator).after(anim[k - 1]);
                } else {
                    s.play(objectAnimator);
                }
                ind = a[1 + (int) (Math.random() * a[0])];
                value[ind / taille][ind % taille] = 3;
                button = tab.getButton(ind / taille, ind % taille);
                ObjectAnimator objectAnimator2 = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), Color.GREEN, Color.GREEN);
                objectAnimator2.setDuration(40);
                if (k != 0) {
                    s.play(objectAnimator2).after(anim[k - 1]);
                } else {
                    s.play(objectAnimator2);
                }
                a = autour(ind, 0);
                anim[k++] = objectAnimator;
                anim[k++] = objectAnimator2;
            }
        }
        s.start();

    }

    int anim(Animator anim[], int k, ArrayList l, ArrayList chemin) {
        int ind = k;
        Set liste = new HashSet();

        liste.addAll(l);
        liste.addAll(chemin);


        for (Object o : liste) {
            int p = (int) o;
            Button button = tab.getButton(p / taille, p % taille);
            if (copy[p / taille][p % taille] == 0 && value[p / taille][p % taille] != 0) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), Color.WHITE, Color.WHITE);
                objectAnimator.setDuration(150);
                if (k > 0) {
                    s.play(objectAnimator).after(anim[k - 1]);
                } else {
                    if (ind > 0)
                        s.play(objectAnimator).with(anim[0]);
                    else
                        s.play(objectAnimator);
                }
                anim[ind++] = objectAnimator;
            }

            if (copy[p / taille][p % taille] == 2 && value[p / taille][p % taille] != 2) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), Color.RED, Color.RED);
                objectAnimator.setDuration(150);
                if (k > 0) {
                    s.play(objectAnimator).after(anim[k - 1]);
                } else {
                    if (ind > 0)
                        s.play(objectAnimator).with(anim[0]);
                    else
                        s.play(objectAnimator);
                }
                anim[ind++] = objectAnimator;
            }

            if (copy[p / taille][p % taille] == 3 && value[p / taille][p % taille] != 3) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), Color.GREEN, Color.GREEN);
                objectAnimator.setDuration(150);
                if (k > 0) {
                    s.play(objectAnimator).after(anim[k - 1]);
                } else {
                    if (ind > 0)
                        s.play(objectAnimator).with(anim[0]);
                    else
                        s.play(objectAnimator);
                }
                anim[ind++] = objectAnimator;
            }

            if (copy[p / taille][p % taille] == 4 && value[p / taille][p % taille] != 4) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), Color.BLUE, Color.BLUE);
                objectAnimator.setDuration(150);
                if (k > 0) {
                    s.play(objectAnimator).after(anim[k - 1]);
                } else {
                    if (ind > 0)
                        s.play(objectAnimator).with(anim[0]);
                    else
                        s.play(objectAnimator);
                }
                anim[ind++] = objectAnimator;
            }
            value[p / taille][p % taille] = copy[p / taille][p % taille];
        }
        return ind;
    }

    void largeur() {
        if (s != null) {
            s.pause();
        }
        del_trace();
        int pile[] = new int[taille * taille];
        int ind = 0;
        for (int i = 1; i < taille - 1; i++) {
            if (value[i][0] == 0)
                pile[ind++] = i * taille;
            if (value[i][taille - 1] == 0)
                pile[ind++] = i * taille + taille - 1;
        }
        for (int j = 1; j < taille - 1; j++) {
            if (value[0][j] == 0)
                pile[ind++] = j;
            if (value[taille - 1][j] == 0)
                pile[ind++] = (taille - 1) * taille + j;
        }
        int n = (int) (Math.random() * 2);
        ind = pile[n];
        int fin = pile[1 - n];
        ArrayList<ArrayList> L = new ArrayList<>();
        ArrayList array = new ArrayList();
        array.add(ind);
        L.add(array);
        for (int i = 0; i < taille; i++)
            for (int j = 0; j < taille; j++)
                copy[i][j] = value[i][j];
        copy[ind/taille][ind%taille] = 3;
        largeur_recursif(L, fin, 1, (ArrayList) array.clone());
    }


    void largeur_recursif(final ArrayList L, final int fin, final int size, ArrayList chemin) {
        s = new AnimatorSet();
        Animator anim[] = new Animator[taille * taille * taille * taille];
        int k = 0;
        if (!L.isEmpty()) {
            ArrayList liste = (ArrayList<ArrayList>) L.clone();
            L.removeAll(liste);
            for (Object o : liste) {
                ArrayList l = (ArrayList) o;
                int p = (int) l.get(size - 1);
                int autour[] = autour(p, 0);
                if (autour[0] == 1){
                    int a = autour[1];
                    l.add(a);
                    L.add(l);
                    for (Object c : l) {
                        int m = (int) c;
                        copy[m / taille][m % taille] = 2;
                    }
                    copy[a / taille][a % taille] = 3;
                    k = anim(anim, k, l, chemin);
                    for (Object c : l) {
                        int m = (int) c;
                        copy[m / taille][m % taille] = 4;
                    }
                    if (a == fin) {
                        s.start();
                        return;
                    }
                    chemin = l;

                }
                else {
                    for (int i = 1; i <= autour[0]; i++) {
                        int a = autour[i];
                        ArrayList clone = (ArrayList) l.clone();
                        clone.add(a);
                        L.add(clone);
                        for (Object c : l) {
                            int m = (int) c;
                            copy[m / taille][m % taille] = 2;
                        }
                        copy[a / taille][a % taille] = 3;
                        k = anim(anim, k, clone, chemin);
                        for (Object c : clone) {
                            int m = (int) c;
                            copy[m / taille][m % taille] = 4;
                        }
                        if (a == fin) {
                            s.start();
                            return;
                        }
                        chemin = clone;
                    }
                }
            }
            if (k > 0) {
                final Animator objectAnimator = anim[k - 1];
                final ArrayList finalChemin1 = chemin;
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(objectAnimator);
                        largeur_recursif(L, fin, size + 1, finalChemin1);
                    }
                });
            } else {
                anim(anim, k, new ArrayList(), chemin);
            }
        }
        s.start();
    }


    int heuristic(Pair<Integer,ArrayList<Pair<Integer,Integer>>> start, Pair<Integer,Integer> end){
        return start.first + Math.abs(start.second.get(start.first - 1).first - end.first) + Math.abs(start.second.get(start.first - 1).second - end.second);
    }

    void addTodo(ArrayList<Pair<Integer, Pair<Integer,ArrayList<Pair<Integer,Integer>>>>> todo, Pair<Integer,ArrayList<Pair<Integer,Integer>>> newPath, Pair<Integer,Integer> end){
        int h = heuristic(newPath, end);
        if (todo.isEmpty() || h >= todo.get(todo.size() - 1).first) {
            todo.add(new Pair(h,newPath));
        }
        int a = 0;
        int b = todo.size() - 1;
        int ind = (a + b) / 2;
        while (b - a > 1){
            if (h == todo.get(ind).first){
                break;
            }
            else if (h > todo.get(ind).first){
                a = ind + 1;
            }
            else{
                b = ind;
            }
            ind = (a + b) / 2;
        }
        todo.add(ind, new Pair(h,newPath));
    }

    void a_etoile(){
        if (s != null) {
            s.pause();
        }
        del_trace();
        ArrayList<Pair<Integer,Integer>> empty = new ArrayList();
        boolean already_seen[][] = new boolean[taille][taille];
        for (int i = 1; i < taille - 1; i++) {
            if (value[i][0] == 0)
                empty.add(new Pair(i, 0));
            if (value[i][taille - 1] == 0)
                empty.add(new Pair(i, taille - 1));
        }
        for (int j = 1; j < taille - 1; j++) {
            if (value[0][j] == 0)
                empty.add(new Pair(0, j));
            if (value[taille - 1][j] == 0)
                empty.add(new Pair(taille - 1, j));
        }
        int n = (int) (Math.random() * 2);
        Pair<Integer,Integer> start = empty.get(n);
        Pair<Integer,Integer> end = empty.get(1 - n);
        ArrayList<ArrayList<Pair<Integer,Integer>>> paths = new ArrayList<>();
        ArrayList<Pair<Integer, Pair<Integer,ArrayList<Pair<Integer,Integer>>>>> todo = new ArrayList<>();
        ArrayList<Pair<Integer,Integer>> list = new ArrayList();
        list.add(start);
        addTodo(todo, new Pair(1, list.clone()), end);
        while (!todo.isEmpty()){
            Pair<Integer, Pair<Integer,ArrayList<Pair<Integer,Integer>>>> element = todo.remove(0);
            ArrayList<Pair<Integer,Integer>> path = element.second.second;
            Pair<Integer, Integer> last = path.get(path.size() - 1);
            if (already_seen[last.first][last.second]){
                continue;
            }
            already_seen[last.first][last.second] = true;
            paths.add((ArrayList<Pair<Integer,Integer>>) path.clone());
            if (last.first == end.first && last.second == end.second){
                animation(paths, new ArrayList<Pair<Integer, Integer>>());
                return;
            }
            int size = element.second.first;
            for (int x = -1; x < 2; x++){
                for (int y = -1; y < 2; y++){
                    if (Math.abs(x) + Math.abs(y) == 1){
                        Pair<Integer, Integer> coordinate = new Pair(path.get(size - 1).first + x, path.get(size - 1).second + y);
                        if (coordinate.first >= 0 && coordinate.first < taille && coordinate.second >= 0 && coordinate.second < taille && value[coordinate.first][coordinate.second] != 1 && !already_seen[coordinate.first][coordinate.second]) {
                            ArrayList<Pair<Integer,Integer>> clone = (ArrayList<Pair<Integer,Integer>>) path.clone();
                            clone.add(coordinate);
                            addTodo(todo, new Pair(size + 1, clone), end);
                        }
                    }
                }
            }
        }
        paths.add(new ArrayList<Pair<Integer, Integer>>());
        animation(paths, new ArrayList<Pair<Integer, Integer>>());
        return;
    }

    boolean contains(ArrayList<Pair<Integer,Integer>> path, Pair<Integer,Integer> coordinate){
        for (Pair<Integer,Integer> point : path){
            if (point.first == coordinate.first && point.second == coordinate.second){
                return true;
            }
        }
        return false;
    }

    void animation(final ArrayList<ArrayList<Pair<Integer,Integer>>> paths, ArrayList<Pair<Integer,Integer>> formerPath){
        if (paths.isEmpty()){
            return;
        }
        s = new AnimatorSet();
        final ArrayList<Pair<Integer,Integer>> path = paths.remove(0);
        Button button;
        ArrayList<ObjectAnimator> anim = new ArrayList<>();
        for (Pair<Integer,Integer> coordinate : formerPath){
            if (!contains(path, coordinate)) {
                button = tab.getButton(coordinate.first, coordinate.second);
                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), Color.BLUE, Color.BLUE);
                objectAnimator.setDuration(150);
                anim.add(objectAnimator);
            }
        }
        for (Pair<Integer,Integer> coordinate : path){
            button = tab.getButton(coordinate.first, coordinate.second);
            ObjectAnimator objectAnimator = ObjectAnimator.ofObject(button, "backgroundColor", new ArgbEvaluator(), Color.RED, Color.RED);
            objectAnimator.setDuration(150);
            anim.add(objectAnimator);
        }
        s.play(anim.get(0));
        for (int i = 1; i < anim.size(); i++) {
            s.play(anim.get(i)).with(anim.get(0));
        }
        anim.get(anim.size() - 1).addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animation(paths, path);
            }
        });
        s.start();
    }

    void del_trace() {
        Button b;
        for (int i = 0; i < taille; i++)
            for (int j = 0; j < taille; j++)
                if (value[i][j] > 1) {
                    value[i][j] = 0;
                    b = tab.getButton(i, j);
                    b.setBackgroundColor(Color.WHITE);
                }
    }

    void nouvelle_grille() {
        switch (currentGrille) {
            case Labyrinthe:
                labyrinthe();
                break;
            case Aléatoire:
                grille_aleatoire(0.3);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labyrinthe);
        this.main = findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();
            }
        });
        this.t = findViewById(R.id.taille);
        ArrayAdapter tailles = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Taille);
        t.setAdapter(tailles);
        t.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                taille = (int) Taille[position];
                create_layout();
                nouvelle_grille();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        this.g = findViewById(R.id.g);
        g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nouvelle_grille();
            }
        });

        this.grilleSelection = findViewById(R.id.grilleSelection);
        final ArrayAdapter grilles = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Grille.values());
        grilleSelection.setAdapter(grilles);

        grilleSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                currentGrille = (Grille) grilles.getItem(position);
                nouvelle_grille();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        this.profondeur = findViewById(R.id.profondeur);
        profondeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profondeur();
            }
        });
        this.largeur = findViewById(R.id.largeur);
        largeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                largeur();
            }
        });
        this.a_etoile = findViewById((R.id.a_etoile));
        a_etoile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a_etoile();
            }
        });
        create_layout();
    }
}
