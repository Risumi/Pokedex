package com.example.risumi.pokedex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    ArrayList<Pokemon> listPokemon;
    ArrayList<Pokedex> listPokedex;
    String listLimit [];
    JSONObject pokedex;
    JSONObject pokedexs;
    Button button;
    int selectedLayout;
    int selectedLimit;
    int selectedPokedex;
    String [] pokedexes;
    int len;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponent();
        checkInternetConnection(0,"https://pokeapi.co/api/v2/pokedex/1/",listLimit[0]);
        showRecyclerGrid();
    }

    public void initializeComponent(){
        listPokemon = new ArrayList<>();
        listPokedex = new ArrayList<>();
        listLimit = new String[]{"10","25","50"};

        mRecyclerView = findViewById(R.id.Recycler);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getAdapter() instanceof PokemonAdapterGrid){
                    int position = parent.getChildAdapterPosition(view);
                    int spanCount = 2;
                    int spacing = 10;
                    if (position >= 0) {
                        int column = position % spanCount;
                        outRect.left = spacing - column * spacing / spanCount;
                        outRect.right = (column + 1) * spacing / spanCount;
                        if (position < spanCount) {
                            outRect.top = spacing;
                        }
                        outRect.bottom = spacing;
                    }
                    else {
                        outRect.left = 0;
                        outRect.right = 0;
                        outRect.top = 0;
                        outRect.bottom = 0;
                    }
                }
            }
        });
        button = findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        ProgressDialog dialog;
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        int lastVisibleItemPosition ;
        if (layoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }else {
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
        try {
            setDataPokemon(lastVisibleItemPosition,selectedLimit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter.notifyDataSetChanged();
        dialog.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_display:
                showAlertLayout();
                break;
            case R.id.action_limit:
                showAlertLimit();
                break;
            case R.id.action_pokedex:
                showAlertPokedex();
                break;
            case R.id.profile:
                Intent intent = new Intent(this,ProfileActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void showAlertError(final int lastVisibleItem, final String url){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Connection Failed")
                .setMessage("Please check your internet connection")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkInternetConnection(lastVisibleItem,url,listLimit[selectedLimit]);
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    public void showAlertLayout(){
        new MaterialAlertDialogBuilder(this)
                .setSingleChoiceItems(R.array.display, selectedLayout, null)
                .setTitle("Display options")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        selectedLayout = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        if (selectedLayout == 0){
                            showRecyclerGrid();
                        }else {
                            showRecyclerList();
                        }
                    }
                })
                .show();
    }

    public void showAlertPokedex(){
        new MaterialAlertDialogBuilder(this)
                .setSingleChoiceItems(pokedexes, selectedPokedex, null)
                .setTitle("Set Pokédex")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        listPokemon.clear();
                        selectedPokedex= ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        checkInternetConnection(0,listPokedex.get(selectedPokedex).getUrl(),listLimit[selectedLimit]);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void showAlertLimit(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Set view limit")
                .setSingleChoiceItems(listLimit, selectedLimit, null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        listPokemon.clear();
                        selectedLimit= ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        checkInternetConnection(0,listPokedex.get(selectedPokedex).getUrl(),listLimit[selectedLimit]);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void showRecyclerList(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter= new PokemonAdapterList(listPokemon);
        ((PokemonAdapterList)mAdapter).setOnItemClickCallback(new PokemonAdapterList.OnItemClickCallback() {
              @Override
              public void onItemClicked(Pokemon pokemon) {
                  Intent intent = new Intent(MainActivity.this, PokemonInfoActivity.class);
                  intent.putExtra("pokemon",pokemon);
                  startActivity(intent);
              }
          });
        mRecyclerView.setAdapter(mAdapter);
    }

    public void showRecyclerGrid(){
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mAdapter = new PokemonAdapterGrid(listPokemon);
        ((PokemonAdapterGrid)mAdapter).setOnItemClickCallback(new PokemonAdapterGrid.OnItemClickCallback() {
            @Override
            public void onItemClicked(Pokemon pokemon) {
                Intent intent = new Intent(MainActivity.this, PokemonInfoActivity.class);
                intent.putExtra("pokemon",pokemon);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    public class StartAsyncTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;
        int lastVisibleItem;
        int selectedLimit;
        String url;
        StartAsyncTask(int lastVisibleItem, String url, int selectedLimit) {
            this.lastVisibleItem = lastVisibleItem;
            this.url = url;
            this.selectedLimit = selectedLimit;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading...");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                GetData getData = new GetData();
                pokedexs = getData.readJsonFromUrl("https://pokeapi.co/api/v2/pokedex/");
                pokedex = getData.readJsonFromUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                setDataPokedex();
                setDataPokemon(lastVisibleItem,selectedLimit);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.cancel();
            mAdapter.notifyDataSetChanged();
        }
    }

    void setDataPokedex(){
        try {
            pokedexes = new String[pokedexs.getJSONArray("results").length()] ;
            for (int i = 0;i<pokedexs.getJSONArray("results").length();i++){
                JSONObject pokedex = pokedexs.getJSONArray("results").getJSONObject(i);
                listPokedex.add(new Pokedex(
                        pokedex.getString("name"),
                        pokedex.getString("url")));
                pokedexes[i]=StringUtils.capitalize(pokedex.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void setDataPokemon(int lastVisible,int selectedLimit) throws JSONException {
        JSONArray jsonArray= pokedex.getJSONArray("pokemon_entries");
        len = jsonArray.length();
        int loop;
        if (lastVisible!=0){
            lastVisible+=1;
        }
        if (lastVisible+Integer.parseInt(listLimit[selectedLimit])>len){
            loop = len;
            button.setVisibility(View.GONE);
        }else {
            loop=lastVisible+Integer.parseInt(listLimit[selectedLimit]);
            button.setVisibility(View.VISIBLE);
        }
        try {
            for (int i = lastVisible; i < loop; i++) {
                int entryNum = jsonArray.getJSONObject(i)
                        .getInt("entry_number");
                JSONObject pokemonSpecies= jsonArray.getJSONObject(i)
                        .getJSONObject("pokemon_species");
                String pokemonName = StringUtils.capitalize(pokemonSpecies
                        .getString("name"));
                String urlPokemon = pokemonSpecies
                        .getString("url");
                String [] urlExplode = urlPokemon.split("/");
                String imageURL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + urlExplode[6]+ ".png";
                listPokemon.add(new Pokemon(pokemonName,Integer.parseInt(urlExplode[6]),entryNum, urlPokemon, imageURL));
                Log.d("Pokemon", pokemonName);
            }
        }catch (JSONException e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        getSupportActionBar().setTitle(StringUtils.capitalize(listPokedex.get(selectedPokedex).getName())+" Pokédex");
    }

    private void checkInternetConnection(int lastVisibleItem,String url,String limit) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Boolean connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (connected){
            new StartAsyncTask(lastVisibleItem,url,selectedLimit).execute();
        }else {
            showAlertError(lastVisibleItem,url);
        }
    }


}