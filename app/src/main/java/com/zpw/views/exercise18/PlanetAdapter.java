package com.zpw.views.exercise18;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zpw.views.R;
import com.zpw.views.exercise18.model.Planet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zpw on 2018/7/6.
 */

public class PlanetAdapter extends RecyclerView.Adapter<PlanetAdapter.PlanetViewHolder>{
    private List<Planet> planets = new ArrayList<>();

    private OnPlanetClickedListener listener;

    public PlanetAdapter() {
        listener = null;
    }

    public PlanetAdapter(OnPlanetClickedListener listener) {
        this.listener = listener;
    }

    @Override
    public PlanetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlanetViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_planet, parent, false));
    }

    @Override
    public void onBindViewHolder(final PlanetViewHolder holder, final int position) {
        final Planet planet = planets.get(position);
        holder.name.setText(planet.name);

        Glide.with(holder.image.getContext())
                .load(planet.url)
                .centerCrop()
                .into(holder.image);

//        holder.image.setImageBitmap(BitmapFactory.decodeResource(holder.image.getContext().getResources(), R.mipmap.pos0));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPlanetClicked(holder.image, planets.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return planets.size();
    }

    public void setPlanets(List<Planet> planets) {
        this.planets = planets;
    }

    public class PlanetViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView image;

        public PlanetViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }
    }

    public interface OnPlanetClickedListener {
        void onPlanetClicked(View sharedImage, Planet planet);
    }
}
