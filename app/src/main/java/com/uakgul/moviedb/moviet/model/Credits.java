package com.uakgul.moviedb.moviet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Credits {


    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("cast")
    @Expose
    private List<Cast> cast = null;

    @SerializedName("crew")
    @Expose
    private List<Crew> crew = null;

    @Override
    public String toString() {
        return "Credits{" +
                "id=" + id +
                ", cast=" + cast +
                ", crew=" + crew +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Cast> getCast() {
        return cast;
    }

    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }

    public List<Crew> getCrew() {
        return crew;
    }

    public void setCrew(List<Crew> crew) {
        this.crew = crew;
    }


}
