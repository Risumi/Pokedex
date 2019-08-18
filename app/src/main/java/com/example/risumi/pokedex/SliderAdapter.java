package com.example.risumi.pokedex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private ArrayList<String> sprite;

    public SliderAdapter(ArrayList<String> sprite) {
        this.sprite = sprite;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        switch (position) {
            case 0:
                Glide.with(viewHolder.itemView)
                        .load(sprite.get(0))
//                        .apply(new RequestOptions().override(300, 500))
                        .into(viewHolder.imgPokemon);
                break;
            case 1:
                Glide.with(viewHolder.itemView)
                        .load(sprite.get(1))
//                        .apply(new RequestOptions().override(300, 500))
                        .into(viewHolder.imgPokemon);
                break;
            case 2:
                Glide.with(viewHolder.itemView)
                        .load(sprite.get(2))
//                        .apply(new RequestOptions().override(300, 500))
                        .into(viewHolder.imgPokemon);
                break;
            case 3:
                Glide.with(viewHolder.itemView)
                        .load(sprite.get(3))
//                        .apply(new RequestOptions().override(300, 500))
                        .into(viewHolder.imgPokemon);
                break;

        }

    }

    @Override
    public int getCount() {
        return sprite.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imgPokemon;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imgPokemon = itemView.findViewById(R.id.imgPokemon);
            this.itemView = itemView;
        }
    }
}