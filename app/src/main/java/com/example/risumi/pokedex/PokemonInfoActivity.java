package com.example.risumi.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.TypefaceCompatUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.smarteist.autoimageslider.SliderView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class PokemonInfoActivity extends AppCompatActivity {
    SliderView sliderView;
    TextView txtName, txtIndex, txtType1, txtType2, txtHeight, txtWeight, txtDescription, txtSpecies
            ,txtAbilities, txtExp, txtCapture, txtHappiness, txtGrowth;
    Pokemon pokemon;
    ArrayList <String> sprite ;
    BarChart mChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_info);

        sprite = new ArrayList<>();

        Intent intent = getIntent();
        pokemon = intent.getParcelableExtra("pokemon");

        txtName = findViewById(R.id.txtName);
        txtName.setText(pokemon.getName());

        txtIndex = findViewById(R.id.txtGenus);
        txtSpecies = findViewById(R.id.txtSpecies);
        txtType1 = findViewById(R.id.txtType1);
        txtType2 = findViewById(R.id.txtType2);
        txtDescription = findViewById(R.id.txtDescription);
        txtAbilities = findViewById(R.id.txtAbilities);
        txtExp = findViewById(R.id.txtExp);
        txtCapture = findViewById(R.id.txtCapture);
        txtHappiness = findViewById(R.id.txtHappiness);
        txtGrowth = findViewById(R.id.txtGrowth);
        txtHeight = findViewById(R.id.txtHeight);
        txtWeight = findViewById(R.id.txtWeight);
        mChart = findViewById(R.id.BarChart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        checkInternetConnection();
    }



    public class StartAsyncTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(PokemonInfoActivity.this);
            dialog.setMessage("Loading...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                GetData getData = new GetData();
                pokemonSpecies = getData.readJsonFromUrl(pokemon.getSpeciesURL());
                pokemonVarieties = getData.readJsonFromUrl("https://pokeapi.co/api/v2/pokemon/"+pokemon.getIndex()+"/");
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
            setData();
            dialog.cancel();
        }
    }

    JSONObject pokemonSpecies;
    JSONObject pokemonVarieties;

    void setData(){
        String genus = null;
        String type1 = null;
        String type2 = null;
        String weight = null;
        String height = null;
        String description = null;
        String ability = "";
        String exp = null;
        String capture = null ;
        String growth = null;
        String happinesss = null;
        try {
            genus = pokemonSpecies.getJSONArray("genera")
                    .getJSONObject(2)
                    .getString("genus");
            int typeLength = pokemonVarieties.getJSONArray("types").length();
            type1 = StringUtils.capitalize(
                    pokemonVarieties.getJSONArray("types")
                    .getJSONObject(0)
                    .getJSONObject("type")
                    .getString("name"));
            if (typeLength>1){
                type2 = StringUtils.capitalize(
                        pokemonVarieties.getJSONArray("types")
                        .getJSONObject(1)
                        .getJSONObject("type")
                        .getString("name"));
            }
            weight = pokemonVarieties.getString("weight");
            height = pokemonVarieties.getString("height");

            JSONArray flavorText = pokemonSpecies.getJSONArray("flavor_text_entries");
            for (int i = 0;i<flavorText.length();i++){
                if (flavorText.getJSONObject(i).getJSONObject("language").getString("name").equalsIgnoreCase("en")){
                    description = flavorText.getJSONObject(i)
                            .getString("flavor_text");
                    break;
                }
            }
            JSONArray abilities =pokemonVarieties.getJSONArray("abilities");
            for (int i = 0;i<abilities.length();i++){
                if (abilities.getJSONObject(i).getBoolean("is_hidden")){
                    ability += StringUtils.capitalize(abilities.getJSONObject(i)
                            .getJSONObject("ability")
                            .getString("name"))+" (Hidden)";
                }else {
                    ability += StringUtils.capitalize(abilities.getJSONObject(i)
                            .getJSONObject("ability")
                            .getString("name"));
                }
                if (i!=(abilities.length()-1)){
                    ability+="\n";
                }
            }

            JSONObject sprites= pokemonVarieties.getJSONObject("sprites");
            sprite.add(sprites.getString("front_default"));
            sprite.add(sprites.getString("back_default"));
            sprite.add(sprites.getString("front_shiny"));
            sprite.add(sprites.getString("back_shiny"));

            exp = pokemonVarieties.getString("base_experience");
            capture = pokemonSpecies.getString("capture_rate");
            happinesss = pokemonSpecies.getString("base_happiness");
            growth = StringUtils.capitalize(pokemonSpecies.getJSONObject("growth_rate")
                    .getString("name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtIndex.setText(pokemon.getIndexPokedex());
        txtDescription.setText(description);
        txtSpecies.setText(genus);
        txtType1.setText(type2);
        txtType2.setText(type1);
        txtWeight.setText(weight);
        txtHeight.setText(height);
        txtAbilities.setText(ability);
        txtExp.setText(exp);
        txtCapture.setText(capture);
        txtHappiness.setText(happinesss);
        txtGrowth.setText(growth);
        int [] baseStat = new int [6];
        try {
            JSONArray stats = pokemonVarieties.getJSONArray("stats");
            for(int i = 0;i<stats.length();i++ ){
                JSONObject jsonObject = stats.getJSONObject(i)
                        .getJSONObject("stat");
                if (jsonObject.getString("name")
                        .equalsIgnoreCase("speed")){
                    baseStat[5] = stats.getJSONObject(i).getInt("base_stat");
                }else if (jsonObject.getString("name")
                        .equalsIgnoreCase("attack")){
                    baseStat[4] = stats.getJSONObject(i).getInt("base_stat");
                }else if (jsonObject.getString("name")
                        .equalsIgnoreCase("defense")){
                    baseStat[3] = stats.getJSONObject(i).getInt("base_stat");
                }else if (jsonObject.getString("name")
                        .equalsIgnoreCase("special-attack")){
                    baseStat[2] = stats.getJSONObject(i).getInt("base_stat");
                }else if (jsonObject.getString("name")
                        .equalsIgnoreCase("special-defense")){
                    baseStat[1] = stats.getJSONObject(i).getInt("base_stat");
                }else if (jsonObject.getString("name")
                        .equalsIgnoreCase("hp")){
                    baseStat[0] = stats.getJSONObject(i).getInt("base_stat");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sliderView = findViewById(R.id.imageSlider);
        SliderAdapter sliderAdapter = new SliderAdapter(sprite);
        sliderView.setSliderAdapter(sliderAdapter);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setScaleEnabled(false);
        mChart.getAxisLeft().setDrawLabels(false);
        mChart.getAxisRight().setDrawLabels(false);
        mChart.setDrawBorders(false);
        mChart.animateXY(2000, 2000);
        mChart.setScaleMinima(.5f, .5f );
        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value ==0){
                    return "HP";
                }else if (value ==1){
                    return  "Attack";
                }else if (value ==2){
                    return  "Defense";
                }else if (value ==3){
                    return  "Sp.Attack";
                }else if (value ==4){
                    return  "Sp.Defense";
                }else {
                    return  "Speed";
                }
            }
        };
        Typeface light = ResourcesCompat.getFont(this, R.font.roboto);

        xl.setTypeface(light);
        xl.setTextSize(10f);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setValueFormatter(valueFormatter);
        xl.setGranularity(1);

        YAxis yl = mChart.getAxisLeft();
        yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yl.setDrawGridLines(false);
        yl.setEnabled(false);
        yl.setAxisMinimum(0f);

        YAxis yr = mChart.getAxisRight();
        yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        for (int i = 0; i < baseStat.length; i++) {
            yVals1.add(new BarEntry(i, baseStat[i]));
        }

        BarDataSet set1;
        set1 = new BarDataSet(yVals1, "DataSet 1");
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);


        BarData data = new BarData(dataSets);

        data.setValueTextSize(14f);
        data.setValueFormatter(new ValueFormatter() {
            /**
             * Called when drawing any label, used to change numbers into formatted strings.
             *
             * @param value float to be formatted
             * @return formatted string label
             */
            @Override
            public String getFormattedValue(float value) {
                return ((Integer) Math.round(value)).toString();
            }
        });
        data.setBarWidth(.5f);
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setData(data);
        mChart.getLegend().setEnabled(false);
        mChart.invalidate(); // refresh

    }


    private void checkInternetConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Boolean connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (connected){
            new PokemonInfoActivity.StartAsyncTask().execute();
        }else {
            showAlertError();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void showAlertError(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Connection Failed")
                .setMessage("Please check your internet connection")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkInternetConnection();
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

}
