package com.zpw.views.exercise18;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;

import com.zpw.views.R;
import com.zpw.views.exercise18.model.Planet;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zpw on 2018/7/6.
 */

public class ActivityA extends AppCompatActivity implements PlanetAdapter.OnPlanetClickedListener {
    private List<Planet> planets = Arrays.asList(
            new Planet("Mercury", "https://upload.wikimedia.org/wikipedia/commons/e/ee/Mercury_transit_1.jpg"),
            new Planet("Venus", "https://upload.wikimedia.org/wikipedia/commons/8/85/Venus_globe.jpg"),
            new Planet("Earth", "https://upload.wikimedia.org/wikipedia/commons/9/97/The_Earth_seen_from_Apollo_17.jpg"),
            new Planet("Mars", "https://upload.wikimedia.org/wikipedia/commons/5/58/Mars_23_aug_2003_hubble.jpg"),
            new Planet("Jupiter", "https://upload.wikimedia.org/wikipedia/commons/e/e2/Jupiter.jpg"),
            new Planet("Saturn", "https://upload.wikimedia.org/wikipedia/commons/b/b4/Saturn_(planet)_large.jpg"),
            new Planet("Uranus", "https://upload.wikimedia.org/wikipedia/commons/3/3d/Uranus2.jpg"),
            new Planet("Neptune", "https://upload.wikimedia.org/wikipedia/commons/5/56/Neptune_Full.jpg"));

    RecyclerView planetsView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_a);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupTransition();

        planetsView = (RecyclerView) findViewById(R.id.planets_view);

        PlanetAdapter planetAdapter = new PlanetAdapter(this);
        planetAdapter.setPlanets(planets);

        planetsView.setHasFixedSize(true);
        planetsView.setLayoutManager(new LinearLayoutManager(this));
        planetsView.setAdapter(planetAdapter);

    }

    private void setupTransition() {
        Slide slide = new Slide(Gravity.LEFT);
        slide.setDuration(1000);
        slide.setInterpolator(new FastOutSlowInInterpolator());
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        slide.excludeTarget(R.id.appbar, true);
        getWindow().setExitTransition(slide);
    }

    @Override
    public void onPlanetClicked(View sharedImage, Planet planet) {
        Intent intent = new Intent(ActivityA.this, ActivityB.class);
        intent.putExtra("url", planet.url);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, sharedImage, getString(R.string.planet_transition_item));
        startActivity(intent, options.toBundle());
    }
}
