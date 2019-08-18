package com.example.risumi.pokedex;

import android.os.Parcel;
import android.os.Parcelable;

public class Pokemon implements Parcelable {
    private String name;
    private int entryNumber;
    private int index;
    private String speciesURL;
    private String imageURL;

    public Pokemon(String name, int index,int entryNumber, String speciesURL, String imageURL) {
        this.name = name;
        this.index = index;
        this.entryNumber = entryNumber;
        this.speciesURL = speciesURL;
        this.imageURL = imageURL;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public int getEntryNumber() {
        return entryNumber;
    }

    public String getSpeciesURL() {
        return speciesURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    protected Pokemon(Parcel in) {
        name = in.readString();
        index = in.readInt();
        entryNumber = in.readInt();
        speciesURL = in.readString();
        imageURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(index);
        dest.writeInt(entryNumber);
        dest.writeString(speciesURL);
        dest.writeString(imageURL);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Pokemon> CREATOR = new Parcelable.Creator<Pokemon>() {
        @Override
        public Pokemon createFromParcel(Parcel in) {
            return new Pokemon(in);
        }

        @Override
        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };

    public String getIndexPokedex(){
        String index;
        if (getEntryNumber() < 10){
            index = "#00"+((Integer) getEntryNumber()).toString();
        }else if (getEntryNumber()<100){
            index = "#0"+((Integer) getEntryNumber()).toString();
        }else {
            index = "#"+((Integer) getEntryNumber()).toString();
        }
        return index;
    }
}

