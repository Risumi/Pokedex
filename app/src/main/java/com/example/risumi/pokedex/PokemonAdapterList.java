package com.example.risumi.pokedex;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class PokemonAdapterList extends RecyclerView.Adapter<PokemonAdapterList.PokemonViewHolder> {

    ArrayList<Pokemon> pokemonArrayList;
    Pokemon current;
    private OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }


    public PokemonAdapterList(ArrayList<Pokemon> pokemonArrayList ) {
        this.pokemonArrayList = pokemonArrayList;
    }

    @Override
    public PokemonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list,parent,false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PokemonViewHolder holder, final int position) {

        current = pokemonArrayList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(current.getImageURL())
                .apply(new RequestOptions().override(115, 300))
                .into(holder.PokemonImage);
        holder.PokemonIndex.setText(current.getIndexPokedex());
        holder.PokemonName.setText(current.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onItemClicked(pokemonArrayList.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return pokemonArrayList.size();
    }

    class PokemonViewHolder extends RecyclerView.ViewHolder {
        TextView PokemonName, PokemonIndex;
        ImageView PokemonImage;
        MaterialCardView cardView;

        public PokemonViewHolder(View itemView) {
            super(itemView);
            PokemonName = itemView.findViewById(R.id.txtName);
            PokemonIndex = itemView.findViewById(R.id.txtIndex);
            PokemonImage = itemView.findViewById(R.id.imgPokemon);
            cardView = itemView.findViewById(R.id.cardPokemon);
        }
    }

    public interface OnItemClickCallback{
        void onItemClicked(Pokemon pokemon);
    }
}
